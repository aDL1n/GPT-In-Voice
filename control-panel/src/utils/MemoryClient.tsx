export class MemoryClient {
    private readonly baseUrl: string;

    constructor(baseUrl: string = 'http://localhost:8080') {
        this.baseUrl = baseUrl;
    }

    async getMemories(): Promise<any[]> {
        const url = new URL('/memory/all', this.baseUrl);

        try {
            const response = await fetch(url.toString());

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            throw new Error(`Failed to fetch chat memory: ${error instanceof Error ? error.message : String(error)}`);
        }
    }
}