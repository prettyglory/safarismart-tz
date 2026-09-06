import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="placeholder-page">
      <h2>Page not found</h2>
      <Link to="/">Back home</Link>
    </div>
  );
}
