import './memory.css'
import { useState } from "react";
import { MemoryClient } from '../../utils/memoryClient';

const memoryClient: MemoryClient = new MemoryClient()

interface Memory {
    text: string,
    type: string
}

function Memory() {
    const [memories, setMemories] = useState<Memory[]>([]);

    window.addEventListener("load", () => {
        memoryClient.getMemories().then(memory => {
            setMemories(memory.map(data => {
                return {
                    text: data.text,
                    type: data.messageType
                }
            }))
        })
    });

    return (
        <>
            <div className="card">
                <div className="list">
                    {memories.map((memory) => (
                        <div>
                            <h2>{memory.type}</h2>
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
