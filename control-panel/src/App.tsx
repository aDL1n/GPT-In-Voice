import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Header } from '../components/Header';
import Bot from './pages/Bot/Bot';

const App: React.FC = () => {
  const navItems = [
    { label: 'Bot', href: '/' },
  ];

  return (
    <>
      <Header navItems={navItems} />
      <Routes>
        <Route path="/" element={<Bot />} />
      </Routes>
    </>
  );
};

export default App;
