import './chat.css'
import React, { useState, useEffect, useRef} from 'react'
import { ModelClient } from "../../utils/modelClient.tsx";
import {MemoryClient, type MemoryData} from '../../utils/memoryClient.tsx'
import {Box, Center, Group, IconButton, Input} from "@chakra-ui/react";
import {FaAngleDoubleRight} from "react-icons/fa";
import { Message } from  "@/components/app/message.tsx"

interface MessageData {
  text: string;
  isUser: boolean;
}

const apiClient = new ModelClient();

function Chat() {
  const [messages, setMessages] = useState<MessageData[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const conversationRef = useRef<HTMLDivElement>(null);

  const handleMessageUpdate = (data: MemoryData[]) => {
    setMessages(data.map((memory) => ({
      text: memory.text,
      isUser: memory.messageType != "ASSISTANT",
    })));
  }

  useEffect(() => {
    const memoryClient: MemoryClient = new MemoryClient(handleMessageUpdate)
    memoryClient.init();

    if (conversationRef.current) {
      conversationRef.current.scrollTop = conversationRef.current.scrollHeight;
    }
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!inputValue.trim() || isLoading) return;

    const userMessage = inputValue.trim();
    setInputValue('');

    const userMessageObj: MessageData = {
      text: userMessage,
      isUser: true
    };

    setMessages(prev => [...prev, userMessageObj]);
    setIsLoading(true);

    let answerMessage: MessageData;

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
        <Box display="flex" flexDirection="column">
          <Center flexDirection="column" h="100%" overflowY="scroll" maxH="calc(100vh - 50px)" w="100%">
            {messages.map((message, index) => (
                <Message isUser={message.isUser} key={index}>
                  <p>{message.text}</p>
                </Message>
            ))}
            {isLoading && (
                <div className="message bot-message">
                  <div className="message-content loading">
                    Думает...
                  </div>
                </div>
            )}
          </Center>
          <Group>
            <Input onChange={(e) => handleInputChange(e)} />
            <IconButton onClick={(e) => handleSubmit(e)}>
              <FaAngleDoubleRight />
            </IconButton>
          </Group>
        </Box>
      </>
  )

}

export default Chat;