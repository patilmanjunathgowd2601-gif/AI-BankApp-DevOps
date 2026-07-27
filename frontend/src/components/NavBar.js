import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';

export default function NavBar() {
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  const logout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <nav>
      <div className="brand">
        <span className="brand-mark" aria-hidden="true">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M12 2 2 7v2h20V7L12 2Z" fill="#fff" />
            <path d="M4 10v9h3v-9H4Zm6.5 0v9h3v-9h-3ZM17 10v9h3v-9h-3ZM2 21h20v2H2v-2Z" fill="#fff" />
          </svg>
        </span>
        AI-BankApp-DevOps
      </div>
      <div className="nav-links">
        {token ? (
          <>
            <NavLink to="/" end className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}>Dashboard</NavLink>
            <NavLink to="/transfer" className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}>Transfer</NavLink>
          </>
        ) : (
          <>
            <NavLink to="/login" className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}>Login</NavLink>
            <NavLink to="/register" className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}>Register</NavLink>
          </>
        )}
      </div>
      {token && (
        <button style={{ width: 'auto', margin: 0 }} onClick={logout}>Logout</button>
      )}
    </nav>
  );
}
