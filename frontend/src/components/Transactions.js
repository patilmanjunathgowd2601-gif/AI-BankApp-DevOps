import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import client from '../api/client';
import { formatINR } from '../utils/currency';

export default function Transactions() {
  const { accountNumber } = useParams();
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    client.get(`/api/transactions/history/${accountNumber}`)
      .then((res) => setTransactions(res.data))
      .catch((err) => setError(err.response?.data?.message || 'Failed to load transactions'))
      .finally(() => setLoading(false));
  }, [accountNumber]);

  return (
    <div>
      <div className="page-header">
        <h2>Transaction history</h2>
        <p>Account <span className="account-number">{accountNumber}</span></p>
      </div>
      {error && <div className="error">{error}</div>}
      <div className="card">
        {loading && !error && <p>Loading…</p>}
        {!loading && !error && transactions.length === 0 && (
          <div className="empty-state">No transactions yet.</div>
        )}
        {!loading && !error && transactions.length > 0 && (
          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>Date</th><th>Type</th><th>Amount</th><th>Status</th><th>Fraud score</th></tr>
              </thead>
              <tbody>
                {transactions.map((t) => (
                  <tr key={t.id}>
                    <td>{new Date(t.createdAt).toLocaleString()}</td>
                    <td>{t.type}</td>
                    <td>{formatINR(t.amount)}</td>
                    <td>
                      <span className={'status-pill ' + (t.status === 'FLAGGED' ? 'flagged' : 'completed')}>
                        {t.status}
                      </span>
                    </td>
                    <td>{t.fraudScore ?? '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
