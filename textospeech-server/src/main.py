import uuid
import wave
import io

import numpy as np

from fastapi import FastAPI, BackgroundTasks, Request
from fastapi.responses import JSONResponse, Response
from piper import PiperVoice, SynthesisConfig

TARGET_RATE = 48000
TARGET_CHANNELS = 2
TARGET_DTYPE = np.int16

voice = PiperVoice.load("models/dmitri/ru_RU-dmitri-medium.onnx", use_cuda=True)
syn_config = SynthesisConfig(
    volume=0.5,
    length_scale=1.1,
    noise_scale=1.0,
    noise_w_scale=1.1,
    normalize_audio=True,
)

app = FastAPI()
tasks = {}

@app.post("/speech")
async def text2speech(request: Request, background_tasks: BackgroundTasks):
    body = await request.body()
    text = body.decode("utf-8").strip()
    if not text:
        return JSONResponse(status_code=400, content={"error": "Not enough text"})

    request_id = str(uuid.uuid4())
    tasks[request_id] = {"status": "processing", "audio": None}
    background_tasks.add_task(process_text_task, request_id, text)
    return {"request_id": request_id}


@app.get("/result/{request_id}")
def get_result(request_id: str):
    if request_id not in tasks:
        return JSONResponse(status_code=404, content={"error": "Unknown request_id"})

    task = tasks[request_id]
    if task["status"] != "done":
        return {"status": task["status"]}

    return Response(content=task["audio"], media_type="audio/wav")


def process_text_task(request_id: str, text: str):
    buf = io.BytesIO()
    with wave.open(buf, "wb") as wav_file:
        voice.synthesize_wav(text, wav_file, syn_config=syn_config)

    wav_bytes = buf.getvalue()
    pcm_be = convert_wav_to_pcm_48k16_stereo_be(wav_bytes)

    tasks[request_id] = {"status": "done", "audio": pcm_be}

def convert_wav_to_pcm_48k16_stereo_be(wav_bytes: bytes) -> bytes:
    with wave.open(io.BytesIO(wav_bytes), "rb") as wav_in:
        src_rate = wav_in.getframerate()
        src_channels = wav_in.getnchannels()
        sampwidth = wav_in.getsampwidth()
        nframes = wav_in.getnframes()
        frames = wav_in.readframes(nframes)

    if sampwidth != 2:
        raise ValueError(f"Ожидается 16-bit PCM (sampwidth=2), а получено sampwidth={sampwidth}")

    audio = np.frombuffer(frames, dtype="<i2")
    if src_channels not in (1, 2):
        raise ValueError(f"Поддерживаются 1 или 2 канала, а получено: {src_channels}")

    audio = audio.reshape(-1, src_channels)

    if src_channels == 1:
        audio = np.repeat(audio, 2, axis=1)

    if src_rate != TARGET_RATE:
        src_len = audio.shape[0]
        dst_len = int(round(src_len * TARGET_RATE / float(src_rate)))
        if dst_len < 1:
            dst_len = 1

        src_t = np.linspace(0.0, 1.0, num=src_len, endpoint=True, dtype=np.float64)
        dst_t = np.linspace(0.0, 1.0, num=dst_len, endpoint=True, dtype=np.float64)

        ch_left = np.interp(dst_t, src_t, audio[:, 0].astype(np.float64))
        ch_right = np.interp(dst_t, src_t, audio[:, 1].astype(np.float64))
        audio = np.stack([ch_left, ch_right], axis=1)
    else:
        audio = audio.astype(np.float64)


    audio = np.clip(np.rint(audio), np.iinfo(TARGET_DTYPE).min, np.iinfo(TARGET_DTYPE).max).astype(TARGET_DTYPE)
    pcm_be = audio.astype(">i2").tobytes()
    return pcm_be

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=5002, reload=False)