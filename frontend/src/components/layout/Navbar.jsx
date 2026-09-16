import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function Navbar() {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/');
  }

  return (
    <header className="navbar">
      <Link to="/" className="navbar__brand">
        SafariSmart <span>TZ</span>
      </Link>

      <div className="navbar__auth">
        {isAuthenticated ? (
          <>
            <span className="navbar__user">
              Hi, {user.fullName.split(' ')[0]}
            </span>

            <button
              onClick={handleLogout}
              className="btn btn--ghost"
            >
              Log out
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className="btn btn--ghost">
              Log in
            </Link>

            <Link to="/register" className="btn btn--primary">
              Sign up
            </Link>
          </>
        )}
      </div>
    </header>
  );
}