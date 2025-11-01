import {toaster} from "@/components/ui/toaster.tsx";

export class ModelClient {
  private readonly url: URL;

  constructor(baseUrl: string = 'http://localhost:8080/') {
    this.url = new URL(baseUrl);
  }

  public async getModelName(): Promise<string> {
    const response = await fetch(this.url);
    return JSON.parse(await response.text());
  }

  async ask(message: string, username: string): Promise<string> {
    const askUrl = new URL("/api/model/ask", this.url);
    askUrl.searchParams.append('message', message);
    askUrl.searchParams.append('username', username);

    try {
      const response = await fetch(askUrl.toString(), {
        method: 'GET',
        headers: {
          'Accept': 'text/plain; charset=utf-8'
        }
      });

      return await response.text();
    } catch (error) {
      toaster.create({
        description: "Не удалось отправить запрос",
        type: "error",
        closable: true
      });
      throw new Error(`Request failed: ${error instanceof Error ? error.message : String(error)}`);
    }
  }

  public async getSystemPrompt(): Promise<string> {
    const response = await fetch(this.url + "api/model/systemPrompt", {
      headers: {
        'Accept': 'text/plain; charset=utf-8'
      }
    });

    return response.text();
  }
}