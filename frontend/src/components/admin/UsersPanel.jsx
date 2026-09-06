import { useEffect, useState } from 'react';
import * as adminApi from '../../api/adminApi';

export default function UsersPanel() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  function load() {
    setLoading(true);
    adminApi.listUsers()
      .then(setUsers)
      .catch(() => setError('Could not load users.'))
      .finally(() => setLoading(false));
  }

  useEffect(load, []);

  async function handleToggle(user) {
    try {
      await adminApi.setUserActive(user.id, !user.active);
      load();
    } catch {
      setError('Could not update this user.');
    }
  }

  return (
    <div className="crud-panel">
      <div className="crud-panel__header">
        <h2>Users</h2>
      </div>
      {loading && <p>Loading…</p>}
      {error && <p className="form-error">{error}</p>}
      {!loading && !error && (
        <table className="crud-table">
          <thead>
            <tr><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th></th></tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.fullName}</td>
                <td>{u.email}</td>
                <td>{u.role}</td>
                <td>{u.active ? 'Active' : 'Deactivated'}</td>
                <td>
                  <button className="btn btn--ghost btn--small" onClick={() => handleToggle(u)}>
                    {u.active ? 'Deactivate' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
