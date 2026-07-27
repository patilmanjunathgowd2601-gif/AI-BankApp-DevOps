import { render, screen } from '@testing-library/react';
import App from './App';

test('renders the app shell and shows the login link when logged out', () => {
  render(<App />);
  expect(screen.getByText(/AI-BankApp-DevOps/i)).toBeInTheDocument();
  expect(screen.getByText(/Login/i)).toBeInTheDocument();
});
