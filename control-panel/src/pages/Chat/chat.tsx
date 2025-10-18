import React, { useState, useEffect, useRef} from 'react'
import { ModelClient } from "../../utils/modelClient.tsx";
import {MemoryClient, type MemoryData} from '../../utils/memoryClient.tsx'
import {Container, Flex, Group, IconButton, Input, ScrollArea} from "@chakra-ui/react";
import {FaAngleDoubleRight} from "react-icons/fa";
import { Message } from  "@/components/app/message.tsx"
import { useColorModeValue } from '@/components/ui/color-mode.tsx';

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
  const inputRef = useRef<HTMLInputElement | null>(null);

  const inputBg = useColorModeValue("gray.600", "#2A2438");

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
    inputRef.current?.focus();

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
    setInputValue(e.currentTarget.value);
  };


  return (
      <>
        <Flex direction="column" height="100%">
          <Container minHeight="0" minWidth="0" paddingTop="1rem">
            <ScrollArea.Root h="100%">
              <ScrollArea.Viewport borderRadius="15px">
                <ScrollArea.Content display="flex" flexDirection="column" gap="6px">
                  {messages.map((message, index) => (
                    <Message isUser={message.isUser} key={index}>
                      <p style={{
                        wordBreak: "break-word",
                      }}>{message.text}</p>
                    </Message>
                  ))}
                  {isLoading && (
                    <Message isUser={false}>
                      Думает...
                    </Message>
                  )}
                </ScrollArea.Content>
              </ScrollArea.Viewport>
              <ScrollArea.Scrollbar orientation="vertical">
                <ScrollArea.Thumb />
              </ScrollArea.Scrollbar>
              <ScrollArea.Corner />
            </ScrollArea.Root>
          </Container>
            

          <Group padding="1rem" width="80%" alignSelf="center">
            <Input boxShadow="2px 3px 6px rgba(0, 0, 0, 0.5)" disabled={isLoading} value={inputValue} variant="subtle" backgroundColor={inputBg} borderLeftRadius="40px" onChange={handleInputChange} />
            <IconButton boxShadow="2px 2px 8px rgba(0, 0, 0, 0.43)" borderRightRadius="40px" width="50px" onClick={handleSubmit}>
              <FaAngleDoubleRight />
            </IconButton>
          </Group>
        </Flex>
      </>
  )

}

export default Chat;