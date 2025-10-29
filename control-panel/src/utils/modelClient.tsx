export class ModelClient {
  private readonly url: URL;

  constructor(baseUrl: string = 'http://localhost:8080/') {
    this.url = new URL("/api/model/", baseUrl);
  }

  async ask(message: string, username: string): Promise<string> {
    const askUrl = new URL("/ask/", this.url);
    askUrl.searchParams.append('message', message);
    askUrl.searchParams.append('username', username);

    try {
      const response = await fetch(askUrl.toString(), {
        method: 'GET',
        headers: {
          'Accept': 'text/plain; charset=utf-8'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.text();
    } catch (error) {
      throw new Error(`Request failed: ${error instanceof Error ? error.message : String(error)}`);
    }
  }

}