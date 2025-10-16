import './chat.css'
import React, { useState, useEffect, useRef} from 'react'
import { ModelClient } from "../../utils/modelClient.tsx";
import { MemoryClient } from '../../utils/memoryClient.tsx'
import {Box, Center, Group, IconButton, Input} from "@chakra-ui/react";
import {FaAngleDoubleRight} from "react-icons/fa";

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
      <Center>
        <Box>
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
          <Group>
            <Input onChange={(e) => handleInputChange(e)} />
            <IconButton onClick={(e) => handleSubmit(e)}>
              <FaAngleDoubleRight />
            </IconButton>
          </Group>
        </Box>
      </Center>
    </>
  )
}

export default Chat;