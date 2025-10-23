import React from 'react';

import { Flex } from '@chakra-ui/react';
import { useColorModeValue } from './components/ui/color-mode';
import { Routes, Route } from 'react-router-dom';

import { Header } from '@/components/app/header.tsx';
import { AppBody } from '@/components/app/appBody.tsx';

import Chat from './pages/Chat/chat';
import Memory from './pages/Memory/memory'
import Models from './pages/Models/models';
import { Toaster } from './components/ui/toaster';

const App: React.FC = () => {
  const backgroundColor = useColorModeValue("gray.900", "#2A2438");

  const navItems = [
    { label: 'Chat', href: '/chat' },
    { label: 'Discord', href: '/discord' },
    { label: 'Memory', href: '/memory' },
    { label: 'Models', href: '/models' },
    { label: 'Rag', href: '/rag' },
  ];

  return (
    <>
      <Flex direction="column" height="100vh" width="100vw" backgroundColor={backgroundColor}>
        <Header navItems={navItems} />
        <AppBody>
          <Routes>
            <Route path="/" element={<Chat />} />
            <Route path="/chat" element={<Chat />} />
            {/*<Route path="/discord" element={<Discord />} />*/}
            <Route path="/memory" element={<Memory />} />
            <Route path="/models" element={<Models />} />
            {/*<Route path="/rag" element={<Rag />} />*/}
          </Routes>
        </AppBody>
        <Toaster />
      </Flex>
    </>
  );
};

export default App;
