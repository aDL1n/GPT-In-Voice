from fastapi import FastAPI, BackgroundTasks, Request
from fastapi.responses import JSONResponse
import uuid
import os
import wave
import struct
import numpy as np
from scipy.signal import resample
from faster_whisper import WhisperModel
from typing import Dict

app = FastAPI()
os.makedirs("temp", exist_ok=True)

DISCORD_SR = 48000
TARGET_SR = 16000
BYTES_PER_FRAME = int(DISCORD_SR * 0.02 * 2 * 2)  # 20ms, 2 channels, 2 bytes

model = WhisperModel("base", compute_type="float32")

tasks: Dict[str, Dict] = {}  # request_id: {"status": "pending/done", "text": ...}

@app.post("/stream")
async def stream_audio(request: Request, background_tasks: BackgroundTasks):
    audio_bytes = await request.body()
    if not audio_bytes or len(audio_bytes) < BYTES_PER_FRAME:
        return JSONResponse(status_code=400, content={"error": "Not enough audio"})

    request_id = str(uuid.uuid4())
    tasks[request_id] = {"status": "processing", "text": None}
    background_tasks.add_task(process_audio_task, request_id, audio_bytes)
    return {"request_id": request_id}

@app.get("/result/{request_id}")
def get_result(request_id: str):
    if request_id not in tasks:
        return JSONResponse(status_code=404, content={"error": "Unknown request_id"})
    return tasks[request_id]

def process_audio_task(request_id: str, raw_bytes: bytes):
    frames = [raw_bytes[i:i+BYTES_PER_FRAME] for i in range(0, len(raw_bytes), BYTES_PER_FRAME)]
    buffer = bytearray()

    for frame in frames:
        if len(frame) < BYTES_PER_FRAME:
            continue

        try:
            samples = struct.unpack(f">{len(frame)//2}h", frame)
            mono = [(samples[i] + samples[i+1]) // 2 for i in range(0, len(samples), 2)]
            le_frame = struct.pack(f"<{len(mono)}h", *mono)
        except:
            continue

        buffer.extend(le_frame)

    if not buffer:
        tasks[request_id] = {"status": "done", "text": ""}
        return

    pcm_path = f"temp/{request_id}.pcm"
    wav_path = pcm_path.replace(".pcm", ".wav")

    with open(pcm_path, "wb") as f:
        f.write(buffer)

    pcm_to_wav(pcm_path, wav_path)

    segments, _ = model.transcribe(wav_path, temperature=0.5, language="ru")
    text = " ".join(seg.text.strip() for seg in segments)

#     os.remove(wav_path)
    os.remove(pcm_path)
    print("transcript done for: " + request_id)
    tasks[request_id] = {"status": "done", "text": text}

def pcm_to_wav(pcm_path: str, wav_path: str):
    with open(pcm_path, "rb") as f:
        audio = np.frombuffer(f.read(), dtype=np.int16)

    resampled = resample(audio, int(len(audio) * TARGET_SR / DISCORD_SR)).astype(np.int16)

    with wave.open(wav_path, "wb") as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(TARGET_SR)
        wf.writeframes(resampled.tobytes())


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=5000, reload=False)
