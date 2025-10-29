export class SettingsClient {
    private readonly url: URL;

    constructor(
        baseUrl: string = "http://localgost:8080/"
    ) {
        this.url = new URL(baseUrl);
    }

    public changeSynthesisModel(modelName: string) {
        fetch(this.url.toString() + "api/settings/tts/change", {
            method: "POST",
            cache: "no-cache",
            body: JSON.stringify(modelName)
        }).then((response) => {
            if (response.status != 200) console.error("")
        })
    }

    public changeRecognitionModel(modelName: string) {
        fetch(this.url.toString() + "api/settings/stt/change", {
            method: "POST",
            cache: "no-cache",
            body: JSON.stringify(modelName)
        }).then((response) => {
            if (response.status != 200) console.error("")
        })
    }

    public async getSynthesisModels(): Promise<string[]> {
        const response = await fetch(this.url.toString() + "api/settings/tts/list")
        return JSON.parse(await response.text()) as string[];
    }

    public async getRecognitionModels(): Promise<string[]> {
        const response = await fetch(this.url.toString() + "api/settings/stt/list")
        return JSON.parse(await response.text()) as string[];
    }

    public enableSynthesisModel(enable: boolean) {
        fetch(this.url.toString() + "api/settings/tts/enable", {
            method: "POST",
            cache: "no-cache",
            body: JSON.stringify(enable)
        }).then((response) => {
            if (response.status != 200) console.error("")
        })
    }

    public enableRecognitionModel(enable: boolean) {
        fetch(this.url.toString() + "api/settings/stt/enable", {
            method: "POST",
            cache: "no-cache",
            body: JSON.stringify(enable)
        }).then((response) => {
            if (response.status != 200) console.error("")
        })
    }
}