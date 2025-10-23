import { toaster } from "@/components/ui/toaster";

export class ApiStatus {
    private readonly url: URL;

    constructor(apiUrl: string = "http://localhost:8080/") {
        this.url = new URL("/api", apiUrl);
    }

    public async getColor(): Promise<string> {
        try {
            const response = await fetch(this.url.toString());
            if (!response.ok) {
                return "red";
            }

            const responseData = await response.json();

            switch (responseData.apiStatus) {
                case "LOADING":
                    return "orange";
                case "RUNNING":
                    return "green";
                default:
                    return "red";
            }
        } catch (error) {
            toaster.create({
                description: "Не удалось подключиться к GPT-In-Voice",
                type: "error",
                closable: true
            });
            return "red";
        }
    }
}
