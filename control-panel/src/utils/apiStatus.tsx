export class ApiStatus {
    private readonly url: URL;

    constructor(
        apiUrl: string = "http://localhost:8080/api"
    ) {
        this.url = new URL("/api", apiUrl);

    }

    public getColor(): string {
        const request = new XMLHttpRequest();
        request.open('GET', this.url.toString(), false); // false = синхронный запрос
        request.send(null);

        if (request.status !== 200) {
            return "red";
        }

        const responseData = JSON.parse(request.responseText);
            switch (responseData.apiStatus) {
                case "SHUTDOWN": 
                    return "red";
                case "LOADING": 
                    return "orange";
                case "RUNNING":
                    return "green";
                default:
                    return "red";
            }
    }
}