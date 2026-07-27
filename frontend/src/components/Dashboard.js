import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import client from '../api/client';
import { formatINR } from '../utils/currency';

export default function Dashboard() {
  const [accounts, setAccounts] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    client.get('/api/accounts')
      .then((res) => setAccounts(res.data))
      .catch((err) => setError(err.response?.data?.message || 'Failed to load accounts'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      <div className="page-header">
        <h2>Your accounts</h2>
        <p>An overview of your balances and quick access to transaction history.</p>
      </div>
      {error && <div className="error">{error}</div>}
      {loading && !error && <p>Loading…</p>}
      {!loading && !error && accounts.length === 0 && (
        <div className="card empty-state">No accounts found.</div>
      )}
      <div className="accounts-grid">
        {accounts.map((a) => (
          <div className="account-card" key={a.accountNumber}>
            <span className="account-type-badge">{a.accountType}</span>
            <div className="account-number">{a.accountNumber}</div>
            <div className="account-balance">{formatINR(a.balance)}</div>
            <Link to={`/transactions/${a.accountNumber}`}>View transactions →</Link>
          </div>
        ))}
      </div>
    </div>
  );
}
