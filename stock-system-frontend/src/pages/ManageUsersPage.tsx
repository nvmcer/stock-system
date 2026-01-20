import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

interface User {
  id: number;
  username: string;
  role: string;
}

function ManageUsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const navigate = useNavigate();

  // Fetch all users on component mount
  useEffect(() => {
    const token = localStorage.getItem("token");
    const fetchUsers = async () => {
      try {
        const res = await api.get<User[]>("/api/users", {
          headers: { Authorization: `Bearer ${token}` }
        });
        setUsers(res.data);
      } catch (err) {
        console.error("Failed to fetch users:", err);
      }
    };
    if (token) fetchUsers();
  }, []);

  // Delete user by ID
  const handleDeleteUser = async (username: string, id: number) => {
    // Confirm before deleting - show username for clarity
    if (!window.confirm(`Are you sure you want to delete user "${username}"? This will also delete all their portfolio and trade records.`)) {
      return;
    }

    const token = localStorage.getItem("token");
    try {
      await api.delete(`/api/users/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      // Remove deleted user from state
      setUsers(users.filter(user => user.id !== id));
      alert("User and all related records deleted successfully");
    } catch (err: any) {
      console.error("Delete failed:", err);
      alert("Failed to delete user: " + (err.response?.data?.message || err.message));
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Manage Users</h1>
        <button className="primary" onClick={() => navigate("/admin/dashboard")} style={{ padding: '8px 16px' }}>
          Back to Dashboard
        </button>
      </div>

      <div className="card" style={{ marginTop: '20px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
              <th style={{ padding: '12px', textAlign: 'left' }}>ID</th>
              <th style={{ padding: '12px', textAlign: 'left' }}>Username</th>
              <th style={{ padding: '12px', textAlign: 'left' }}>Role</th>
              <th style={{ padding: '12px', textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                <td style={{ padding: '12px' }}><strong>{user.id}</strong></td>
                <td style={{ padding: '12px' }}>{user.username}</td>
                <td style={{ padding: '12px' }}>
                  <span style={{ 
                    background: user.role === 'ROLE_ADMIN' ? 'rgba(59,130,246,0.2)' : 'rgba(107,114,128,0.2)',
                    color: user.role === 'ROLE_ADMIN' ? '#3b82f6' : '#9ca3af',
                    padding: '4px 8px',
                    borderRadius: '4px',
                    fontSize: '0.85rem'
                  }}>
                    {user.role}
                  </span>
                </td>
                <td style={{ padding: '12px', textAlign: 'right' }}>
                  <button 
                    onClick={() => handleDeleteUser(user.username, user.id)} 
                    style={{ background: 'rgba(239,68,68,0.2)', color: '#ef4444', padding: '6px 12px' }}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ManageUsersPage;
