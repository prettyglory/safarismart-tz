import { useEffect, useState } from 'react';

// A metadata-driven list + create/edit form for one admin resource.
// `fields` describes the form; `columns` describes the table. Both are
// declared per-resource in AdminDashboardPage -- this component has no
// resource-specific knowledge at all.
export default function CrudPanel({ title, fields, columns, api, referenceData }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState(null);
  const [formData, setFormData] = useState(null); // null = form closed
  const [formError, setFormError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  function load() {
    setLoading(true);
    api.list()
      .then(setItems)
      .catch(() => setLoadError('Could not load this data.'))
      .finally(() => setLoading(false));
  }

  useEffect(load, []);

  function openCreateForm() {
    const blank = {};
    fields.forEach((f) => { blank[f.name] = f.type === 'checkbox' ? false : ''; });
    setFormData(blank);
    setFormError(null);
  }

  function openEditForm(item) {
    const editable = {};
    fields.forEach((f) => {
      editable[f.name] = f.accessor ? f.accessor(item) : item[f.name];
    });
    editable.id = item.id;
    setFormData(editable);
    setFormError(null);
  }

  function closeForm() {
    setFormData(null);
    setFormError(null);
  }

  function handleFieldChange(name, rawValue, type) {
    let value = rawValue;
    if (type === 'number') value = rawValue === '' ? '' : Number(rawValue);
    if (type === 'checkbox') value = rawValue;
    if (type === 'tags') value = rawValue; // kept as raw comma-separated string until submit
    setFormData({ ...formData, [name]: value });
  }

  function buildPayload() {
    const payload = {};
    fields.forEach((f) => {
      let value = formData[f.name];
      if (f.type === 'tags') {
        value = (value || '').split(',').map((s) => s.trim()).filter(Boolean);
      }
      if (f.type === 'select' && f.nullable && value === '') {
        value = null;
      }
      payload[f.name] = value;
    });
    return payload;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    setFormError(null);
    try {
      const payload = buildPayload();
      if (formData.id) {
        await api.update(formData.id, payload);
      } else {
        await api.create(payload);
      }
      closeForm();
      load();
    } catch (err) {
      setFormError(err.response?.data?.message || 'Could not save. Please check the form and try again.');
    } finally {
      setSubmitting(false);
    }
  }

  async function handleToggleActive(item) {
    try {
      await api.setActive(item.id, !item.active);
      load();
    } catch {
      setLoadError('Could not update status.');
    }
  }

  async function handleRemove(item) {
    if (!window.confirm('Delete this item? This cannot be undone.')) return;
    try {
      await api.remove(item.id);
      load();
    } catch (err) {
      setLoadError(err.response?.data?.message || 'Could not delete this item.');
    }
  }

  function renderField(field) {
    const value = formData[field.name] ?? '';

    if (field.type === 'select') {
      const options = referenceData?.[field.optionsKey] || [];
      return (
        <select
          value={value ?? ''}
          onChange={(e) => handleFieldChange(field.name, e.target.value === '' ? '' : e.target.value, 'select')}
          required={!field.nullable}
        >
          {field.nullable && <option value="">(none)</option>}
          {!field.nullable && <option value="" disabled>Select…</option>}
          {field.staticOptions
            ? field.staticOptions.map((opt) => <option key={opt} value={opt}>{opt}</option>)
            : options.map((opt) => <option key={opt.id} value={opt.id}>{opt.name}</option>)}
        </select>
      );
    }

    if (field.type === 'textarea') {
      return (
        <textarea
          value={value}
          onChange={(e) => handleFieldChange(field.name, e.target.value, 'textarea')}
          rows={3}
        />
      );
    }

    if (field.type === 'checkbox') {
      return (
        <input
          type="checkbox"
          checked={Boolean(value)}
          onChange={(e) => handleFieldChange(field.name, e.target.checked, 'checkbox')}
        />
      );
    }

    return (
      <input
        type={field.type === 'number' ? 'number' : 'text'}
        value={value}
        step={field.type === 'number' ? 'any' : undefined}
        onChange={(e) => handleFieldChange(field.name, e.target.value, field.type)}
        required={field.required}
      />
    );
  }

  return (
    <div className="crud-panel">
      <div className="crud-panel__header">
        <h2>{title}</h2>
        {!formData && <button className="btn btn--primary btn--small" onClick={openCreateForm}>+ Add new</button>}
      </div>

      {loading && <p>Loading…</p>}
      {loadError && <p className="form-error">{loadError}</p>}

      {formData && (
        <form className="crud-form" onSubmit={handleSubmit}>
          {fields.map((field) => (
            <label key={field.name} className={field.type === 'checkbox' ? 'crud-form__checkbox-label' : ''}>
              {field.label}
              {renderField(field)}
            </label>
          ))}
          {formError && <p className="form-error">{formError}</p>}
          <div className="crud-form__actions">
            <button type="submit" className="btn btn--primary" disabled={submitting}>
              {submitting ? 'Saving…' : 'Save'}
            </button>
            <button type="button" className="btn btn--ghost" onClick={closeForm}>Cancel</button>
          </div>
        </form>
      )}

      {!loading && !loadError && (
        <table className="crud-table">
          <thead>
            <tr>
              {columns.map((col) => <th key={col.key}>{col.label}</th>)}
              <th></th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                {columns.map((col) => (
                  <td key={col.key}>{col.render ? col.render(item) : item[col.key]}</td>
                ))}
                <td className="crud-table__actions">
                  <button className="btn btn--ghost btn--small" onClick={() => openEditForm(item)}>Edit</button>
                  {api.setActive && (
                    <button className="btn btn--ghost btn--small" onClick={() => handleToggleActive(item)}>
                      {item.active ? 'Deactivate' : 'Activate'}
                    </button>
                  )}
                  {api.remove && (
                    <button className="btn btn--ghost btn--small crud-table__delete" onClick={() => handleRemove(item)}>
                      Delete
                    </button>
                  )}
                </td>
              </tr>
            ))}
            {items.length === 0 && (
              <tr><td colSpan={columns.length + 1} className="crud-table__empty">No records yet.</td></tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}
