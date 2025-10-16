export class ModelClient {
  private readonly baseUrl: string;

  constructor(baseUrl: string = 'http://localhost:8080') {
    this.baseUrl = baseUrl;
  }

  async ask(message: string, username: string): Promise<string> {
    const url = new URL('/model/ask', this.baseUrl);
    url.searchParams.append('message', message);
    url.searchParams.append('username', username);

    try {
      const response = await fetch(url.toString(), {
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