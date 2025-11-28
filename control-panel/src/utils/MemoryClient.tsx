interface Metadata {
    name: string,
    data: any
}

export interface MemoryData {
    text: string,
    metadata: Metadata,
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
        this.url = new URL('/api/memory', baseUrl);
        this.onMemoryUpdate = onMemoryUpdate;
    }

    public init(): void {

        
        this.run();
        setInterval(this.run, 2500);
    }

    private run = () => {
        fetch(this.url.toString() + "/all")
                .then(async (response) => {
                    if (response.ok)
                        this.onMemoryUpdate(JSON.parse(await response.text()) as MemoryData[])

                })
                .catch(_ => { })
    }

    public delete(messageIndex: Number): void {
        fetch(
            this.url.toString() + "/delete",
            {
                method: "DELETE",
                headers: {
                    'Content-Type': 'application/json' // ОЧЕНЬ ВАЖНО указать тип содержимого
                },
                body: JSON.stringify(messageIndex)
            }).then(() => {
                this.run();
            }
        );
    }
}