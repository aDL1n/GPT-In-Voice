import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Header } from '@/components/app/header.tsx';
import { AppBody } from '@/components/app/appBody.tsx';

import Chat from './pages/Chat/chat';
import Memory from './pages/Memory/memory'

const App: React.FC = () => {
  const navItems = [
    { label: 'Chat', href: '/chat' },
    { label: 'Discord', href: '/discord' },
    { label: 'Memory', href: '/memory' },
    { label: 'Models', href: '/models' },
    { label: 'Rag', href: '/rag' },
  ];

  return (
    <>
      <Header navItems={navItems} />
      <AppBody>
        <Routes>
          <Route path="/" element={<Chat />} />
          <Route path="/chat" element={<Chat />} />
          {/*<Route path="/discord" element={<Discord />} />*/}
          <Route path="/memory" element={<Memory />} />
          {/*<Route path="/models" element={<Models />} />*/}
          {/*<Route path="/rag" element={<Rag />} />*/}
        </Routes>
      </AppBody>
    </>
  );
};

export default App;
