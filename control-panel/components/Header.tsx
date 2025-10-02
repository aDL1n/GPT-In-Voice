import React, { useState, useEffect } from 'react';
import './Header.css'

interface HeaderProps {
    navItems: { 
        label: string; 
        href: string 
    } [];
}

export const Header: React.FC<HeaderProps> = ({navItems}) => {
    const [isScrolled, setIsScrolled] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
        setIsScrolled(window.scrollY > 50);
        };
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    return (
    <header className={`custom-header ${isScrolled ? 'scrolled' : ''}`}>
      <h1 className="custom-header__title">Control Panel</h1>
      <nav className="custom-header__nav">
        {navItems.map((item, index) => (
          <a key={index} href={item.href} className="custom-header__link">
            {item.label}
          </a>
        ))}
      </nav>
    </header>
  );
}