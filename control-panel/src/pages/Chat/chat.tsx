import './chat.css'
import { useState, useEffect, useRef} from 'react'
import { ModelClient } from "../../utils/modelClient.tsx";
import { MemoryClient } from '../../utils/memoryClient.tsx'

interface Message {
  text: string;
  isUser: boolean;
}

const apiClient = new ModelClient();
const memoryClient = new MemoryClient();

function Chat() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const conversationRef = useRef<HTMLDivElement>(null);

  window.addEventListener('load', () => {
    memoryClient.getMemories().then(mem => {
      setMessages(mem.map(data => {
        return {
          text: data.messageType == "SYSTEM" ? "SYSTEM: " + data.text : data.text,
          isUser: data.messageType == "USER"
        }
      }));
    })
  });

  useEffect(() => {
    if (conversationRef.current) {
      conversationRef.current.scrollTop = conversationRef.current.scrollHeight;
    }
  }, [messages]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!inputValue.trim() || isLoading) return;

    const userMessage = inputValue.trim();
    setInputValue('');
    
    const userMessageObj: Message = {
      text: userMessage,
      isUser: true
    };
    
    setMessages(prev => [...prev, userMessageObj]);
    setIsLoading(true);

    let answerMessage: Message;

    try {
      const response = await apiClient.ask(userMessage, 'aDL1n_');
      
      answerMessage = {
        text: response,
        isUser: false
      };
    } catch (error) {
      answerMessage = {
        text: `Error: ${error instanceof Error ? error.message : 'Failed to get response'}`,
        isUser: false
      };
    } finally {
      setIsLoading(false);
    }

    setMessages(prev => [...prev, answerMessage]);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };


  return (
    <>
      <div className='card'>
        <div className='conversation' ref={conversationRef}>
          {messages.map((message, index) => (
            <div 
              key={index} 
              className={`message ${message.isUser ? 'user-message' : 'bot-message'}`}
            >
              <div className="message-content">
                {message.text}
              </div>
            </div>
          ))}
          {isLoading && (
            <div className="message bot-message">
              <div className="message-content loading">
                Думает...
              </div>
            </div>
          )}
        </div>
        <form id='input-form' onSubmit={handleSubmit}>
          <input 
            id="input-field" 
            type="text"
            value={inputValue}
            onChange={handleInputChange}
            disabled={isLoading}
          />
          <button 
            id="submit-button" 
            type="submit"
            disabled={isLoading || !inputValue.trim()}
          >

          <svg width="30" height="30" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16"><g fill="none"><path fill="url(#SVG09UKqbVr)" d="M8.805 8.958L1.994 11l.896-3l-.896-3l6.811 2.042c.95.285.95 1.63 0 1.916"></path><path fill="url(#SVGH7ybqcCB)" d="M1.724 1.053a.5.5 0 0 0-.714.545l1.403 4.85a.5.5 0 0 0 .397.354l5.69.953c.268.053.268.437 0 .49l-5.69.953a.5.5 0 0 0-.397.354l-1.403 4.85a.5.5 0 0 0 .714.545l13-6.5a.5.5 0 0 0 0-.894z"></path><path fill="url(#SVGUgu7sepB)" d="M1.724 1.053a.5.5 0 0 0-.714.545l1.403 4.85a.5.5 0 0 0 .397.354l5.69.953c.268.053.268.437 0 .49l-5.69.953a.5.5 0 0 0-.397.354l-1.403 4.85a.5.5 0 0 0 .714.545l13-6.5a.5.5 0 0 0 0-.894z"></path><defs><linearGradient id="SVGH7ybqcCB" x1="1" x2="12.99" y1="-4.688" y2="11.244" gradientUnits="userSpaceOnUse"><stop stop-color="#3BD5FF"></stop><stop offset="1" stop-color="#0094F0"></stop></linearGradient><linearGradient id="SVGUgu7sepB" x1="8" x2="11.641" y1="4.773" y2="14.624" gradientUnits="userSpaceOnUse"><stop offset=".125" stop-color="#DCF8FF" stop-opacity="0"></stop><stop offset=".769" stop-color="#FF6CE8" stop-opacity=".7"></stop></linearGradient><radialGradient id="SVG09UKqbVr" cx="0" cy="0" r="1" gradientTransform="matrix(7.43807 0 0 1.12359 .5 8)" gradientUnits="userSpaceOnUse"><stop stop-color="#0094F0"></stop><stop offset="1" stop-color="#2052CB"></stop></radialGradient></defs></g></svg>
          </button>
        </form>
      </div>
    </>
  )
}

export default Chat;