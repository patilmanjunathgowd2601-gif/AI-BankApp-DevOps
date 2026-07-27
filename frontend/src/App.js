import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import Transfer from './components/Transfer';
import Transactions from './components/Transactions';
import NavBar from './components/NavBar';

function RequireAuth({ children }) {
  const token = localStorage.getItem('token');
  return token ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <BrowserRouter>
      <NavBar />
      <div className="container">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/" element={<RequireAuth><Dashboard /></RequireAuth>} />
          <Route path="/transfer" element={<RequireAuth><Transfer /></RequireAuth>} />
          <Route path="/transactions/:accountNumber" element={<RequireAuth><Transactions /></RequireAuth>} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}
