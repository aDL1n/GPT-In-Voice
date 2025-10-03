import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Header } from '../components/Header';
import { AppBody } from '../components/AppBody';

import Bot from './pages/Bot/Bot';

const App: React.FC = () => {
  const navItems = [
    { label: 'Bot', href: '/bot' },
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
          <Route path="/" element={<Bot />} />
          <Route path="/bot" element={<Bot />} />
          <Route path="/discord" element={<Bot />} />
          <Route path="/memory" element={<Bot />} />
          <Route path="/models" element={<Bot />} />
          <Route path="/rag" element={<Bot />} />
        </Routes>
      </AppBody>
    </>
  );
};

export default App;
