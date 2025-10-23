interface Metadata {
    name: string,
    data: any
}

export interface MemoryData {
    text: string,
    metadata: Metadata
    messageType: string,
    media: any
}

export class MemoryClient {
    private readonly url: URL;
    private readonly onMemoryUpdate: (data: MemoryData[]) => void;


    constructor(
        onMemoryUpdate: (data: MemoryData[]) => void,
        baseUrl: string = "http://localhost:8080/"
    ) {
        this.url = new URL('/api/memory/all', baseUrl);
        this.onMemoryUpdate = onMemoryUpdate;
    }

    public init(): void {

        let run = () => {
            fetch(this.url.toString())
                .then(async (response) => {
                    if (response.ok) 
                        this.onMemoryUpdate(JSON.parse(await response.text()) as MemoryData[])
                    
                })
                .catch(_ => {})
        }
        run();
        setInterval(run, 2500);
    }
}