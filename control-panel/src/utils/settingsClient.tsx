export class SettingsClient {
    private readonly url: URL;

    constructor(
        baseUrl: string = "http://localgost:8080/"
    ) {
        this.url = new URL("/api/settings/", baseUrl);
    }

    public changeSynthesisModel() {

    }

    public changeRecognitionModel() {

    }

    public async getSynthesisModels(): Promise<string[]> {
        const response = await fetch(this.url.toString() + "/stt/list")
        console.log(response);
        return [""];
    }

    public getRecognitionModels(): string[] {
        return [""];
    }

    public enableSynthsisModel() {

    }

    public enableRecognitionModel() {
        
    }
}