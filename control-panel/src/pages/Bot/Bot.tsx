import './Bot.css'
import { useState, useEffect, useRef} from 'react'
import { ModelClient } from '../../utils/ModelClient.tsx';
// import { MemoryClient } from '../../utils/MemoryClient.tsx'

interface Message {
  text: string;
  isUser: boolean;
}

const apiClient = new ModelClient();

function Bot() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const conversationRef = useRef<HTMLDivElement>(null);

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

    try {
      const response = await apiClient.ask(userMessage, 'aDL1n_');
      
      const botMessage: Message = {
        text: response,
        isUser: false
      };
      setMessages(prev => [...prev, botMessage]);
    } catch (error) {

      const errorMessage: Message = {
        text: `Error: ${error instanceof Error ? error.message : 'Failed to get response'}`,
        isUser: false
      };
      
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
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
            <img className="send-icon" src="" alt="Send" />
          </button>
        </form>
      </div>
    </>
  )
}

export default Bot;