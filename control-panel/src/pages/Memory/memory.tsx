import './memory.css'
import {useEffect, useState} from "react";
import {MemoryClient, type MemoryData} from '../../utils/memoryClient';


function Memory() {
    const [memories, setMemories] = useState<MemoryData[]>([]);

    const handleMemoryUpdate = (data: MemoryData[]) => {
        setMemories(data);
    };

    useEffect(() => {
        const memoryClient: MemoryClient = new MemoryClient(handleMemoryUpdate);
        memoryClient.init();
    }, []);

    return (
        <>
            <div className="card">
                <div className="list">
                    {memories.map((memory) => (
                        <div>
                            <h2>{memory.messageType}</h2>
                            <p>{memory.text}</p>
                        </div>
                    ))}
                </div>
            </div>
            <div className="card">

            </div>

        </>
    )
}

export default Memory;
