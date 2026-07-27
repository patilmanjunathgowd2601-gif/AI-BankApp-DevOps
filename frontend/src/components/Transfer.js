import React, { useState } from 'react';
import client from '../api/client';

export default function Transfer() {
  const [form, setForm] = useState({ fromAccountNumber: '', toAccountNumber: '', amount: '' });
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setResult(null);
    setLoading(true);
    try {
      const res = await client.post('/api/transactions/transfer', form);
      setResult(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Transfer failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h2>Transfer funds</h2>
        <p>Every transfer is scored by the AI fraud-detection service in real time.</p>
      </div>
      <div className="card" style={{ maxWidth: 480 }}>
        <form onSubmit={handleSubmit}>
          <label htmlFor="from-acc">From account number</label>
          <input id="from-acc" name="fromAccountNumber" placeholder="From account number" onChange={handleChange} required />
          <label htmlFor="to-acc">To account number</label>
          <input id="to-acc" name="toAccountNumber" placeholder="To account number" onChange={handleChange} required />
          <label htmlFor="amount">Amount (₹)</label>
          <input id="amount" name="amount" type="number" step="0.01" placeholder="Amount (₹)" onChange={handleChange} required />
          {error && <div className="error">{error}</div>}
          <button type="submit" disabled={loading}>{loading ? 'Sending…' : 'Send'}</button>
        </form>
        {result && (
          <div className="result-panel">
            <p>
              Status:{' '}
              <span className={'status-pill ' + (result.status === 'FLAGGED' ? 'flagged' : 'completed')}>
                {result.status}
              </span>
            </p>
            {result.status === 'FLAGGED' && (
              <p>This transaction was flagged by the AI fraud-detection service (score: {result.fraudScore}) and held for review.</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
