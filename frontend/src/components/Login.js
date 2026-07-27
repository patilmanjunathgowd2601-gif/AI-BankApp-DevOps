import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import client from '../api/client';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await client.post('/api/auth/login', { username, password });
      localStorage.setItem('token', res.data.token);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-shell">
      <div className="card auth-card">
        <h2>Welcome back</h2>
        <p className="auth-subtitle">Log in to manage your accounts and transfers.</p>
        <form onSubmit={handleSubmit}>
          <label htmlFor="login-username">Username</label>
          <input id="login-username" placeholder="Username" value={username} onChange={(e) => setUsername(e.target.value)} required />
          <label htmlFor="login-password">Password</label>
          <input id="login-password" type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          {error && <div className="error">{error}</div>}
          <button type="submit" disabled={loading}>{loading ? 'Logging in…' : 'Log in'}</button>
        </form>
        <p>No account? <Link to="/register">Register here</Link></p>
      </div>
    </div>
  );
}
