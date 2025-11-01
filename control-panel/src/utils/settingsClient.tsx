import {createListCollection, type ListCollection} from "@chakra-ui/react";

export class SettingsClient {
    private readonly url: URL;

    constructor(
        baseUrl: string = "http://localhost:8080/"
    ) {
        this.url = new URL(baseUrl);
    }

    public changeSynthesisModel(modelName: string) {
        fetch(this.url + "api/settings/tts/change", {
            method: "POST",
            cache: "no-cache",
            body: JSON.stringify({ modelName: modelName })
        }).then((response) => {
            if (response.status != 200) console.error("")
        })
    }

    public changeRecognitionModel(modelName: string) {
        fetch(this.url.toString() + "api/settings/stt/change", {
            method: "POST",
            cache: "no-cache",
            body: JSON.stringify({ modelName: modelName })
        }).then((response) => {
            if (response.status != 200) console.error("")
        })
    }

    public async getSynthesisModels(): Promise<ListCollection<{ label: string; value: string }>> {
        const response = await fetch(this.url.toString() + "api/settings/tts/list")

        const data: string[] = JSON.parse(await response.text()) as string[];

        return createListCollection({
            items: data.map(str => ({ label: str, value: str.toLowerCase() }))
        });
    }

    public async getRecognitionModels(): Promise<ListCollection<{ label: string; value: string }>> {
        let response: Response = await fetch(this.url.toString() + "api/settings/stt/list");

        const data: string[] = JSON.parse(await response.text()) as string[];

        return createListCollection({
            items: data.map((str: string) => ({ label: str, value: str.toLowerCase() }))
        });
    }

    public enableSynthesisModel(enable: boolean) {
        fetch(this.url.toString() + "api/settings/tts/enable", {
            method: "POST",
            cache: "no-cache",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(enable)
        }).then((response) => {
            if (response.status != 200) console.error("")
        })
    }

    public enableRecognitionModel(enable: boolean) {
        fetch(this.url.toString() + "api/settings/stt/enable", {
            method: "POST",
            cache: "no-cache",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(enable)
        }).then((response) => {
            if (response.status != 200) console.error("")
        })
    }
}