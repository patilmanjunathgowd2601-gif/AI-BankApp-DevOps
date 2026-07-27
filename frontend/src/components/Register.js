import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import client from '../api/client';

export default function Register() {
  const [form, setForm] = useState({ username: '', email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await client.post('/api/auth/register', form);
      localStorage.setItem('token', res.data.token);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-shell">
      <div className="card auth-card">
        <h2>Create your account</h2>
        <p className="auth-subtitle">Get a SAVINGS account with a ₹50,000 welcome balance.</p>
        <form onSubmit={handleSubmit}>
          <label htmlFor="reg-username">Username</label>
          <input id="reg-username" name="username" placeholder="Username" onChange={handleChange} required />
          <label htmlFor="reg-email">Email</label>
          <input id="reg-email" name="email" type="email" placeholder="Email" onChange={handleChange} required />
          <label htmlFor="reg-password">Password</label>
          <input id="reg-password" name="password" type="password" placeholder="Password (min 8 chars)" onChange={handleChange} required />
          {error && <div className="error">{error}</div>}
          <button type="submit" disabled={loading}>{loading ? 'Creating account…' : 'Register'}</button>
        </form>
        <p>Already have an account? <Link to="/login">Log in</Link></p>
      </div>
    </div>
  );
}
