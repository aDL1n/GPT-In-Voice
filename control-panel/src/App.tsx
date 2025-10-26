import React from 'react';

import { Flex } from '@chakra-ui/react';
import { Routes, Route } from 'react-router-dom';

import { Header } from '@/components/app/header.tsx';
import { AppBody } from '@/components/app/appBody.tsx';

import Chat from './pages/Chat/chat';
import Memory from './pages/Memory/memory'
import Settings from './pages/Settings/settings';
import { Toaster } from './components/ui/toaster';

const App: React.FC = () => {

  const navItems = [
    { label: 'Chat', href: '/chat' },
    { label: 'Memory', href: '/memory' },
    { label: 'Settings', href: '/settings' },
  ];

  return (
    <>
      <Flex direction="column" height="100vh" width="100vw" backgroundColor="gray.950">
        <Header navItems={navItems} />
        <AppBody>
          <Routes>
            <Route path="/" element={<Chat />} />
            <Route path="/chat" element={<Chat />} />
            <Route path="/memory" element={<Memory />} />
            <Route path="/settings" element={<Settings />} />
          </Routes>
        </AppBody>
        <Toaster />
      </Flex>
    </>
  );
};

export default App;
