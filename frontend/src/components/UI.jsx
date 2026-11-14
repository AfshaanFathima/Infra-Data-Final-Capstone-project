import React from "react";

export function PageHeader({ eyebrow = "PHARMATRACK", title, subtitle, action }) {
  return (
    <div className="page-heading d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
      <div>
        <div className="eyebrow">{eyebrow}</div>
        <h1>{title}</h1>
        {subtitle && <p className="text-secondary mb-0">{subtitle}</p>}
      </div>
      {action}
    </div>
  );
}

export function Panel({ title, subtitle, action, children, className = "" }) {
  return (
    <section className={`panel ${className}`}>
      {(title || action) && (
        <div className="panel-heading d-flex flex-wrap justify-content-between align-items-center gap-2">
          <div>
            {title && <h5 className="mb-1">{title}</h5>}
            {subtitle && <div className="small text-secondary">{subtitle}</div>}
          </div>
          {action}
        </div>
      )}
      {children}
    </section>
  );
}

export function StatCard({ label, value, icon, tone = "blue", hint }) {
  return (
    <div className="stat-card">
      <div className={`stat-icon ${tone}`}><i className={`bi ${icon}`} /></div>
      <div className="stat-label">{label}</div>
      <div className="stat-value">{value ?? "—"}</div>
      {hint && <div className="stat-hint">{hint}</div>}
    </div>
  );
}

export function Status({ value }) {
  const status = String(value ?? "UNKNOWN").toUpperCase();
  const tone = status.includes("APPROVED") || status === "ACTIVE" || status === "CONFIRMED"
    ? "success"
    : status.includes("REJECT") || status.includes("EXPIRED") || status.includes("RECALLED")
      ? "danger"
      : status.includes("PENDING") || status.includes("RECOMMENDED") || status.includes("NEAR")
        ? "warning" : "secondary";
  return <span className={`status-pill ${tone}`}>{status.replaceAll("_", " ")}</span>;
}

export function DataTable({ columns, rows, rowKey, empty = "No records found.", renderActions }) {
  return (
    <div className="table-responsive">
      <table className="table align-middle data-table mb-0">
        <thead><tr>
          {columns.map((col) => <th key={col.key}>{col.label}</th>)}
          {renderActions && <th className="text-end">Actions</th>}
        </tr></thead>
        <tbody>
          {rows?.length ? rows.map((row, index) => (
            <tr key={rowKey ? row[rowKey] : index}>
              {columns.map((col) => (
                <td key={col.key}>{col.render ? col.render(row) : (row[col.key] ?? "—")}</td>
              ))}
              {renderActions && <td className="text-end text-nowrap">{renderActions(row)}</td>}
            </tr>
          )) : (
            <tr><td colSpan={columns.length + (renderActions ? 1 : 0)} className="empty-cell">{empty}</td></tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export function Field({ label, name, value, onChange, type = "text", required = false, options, placeholder, min, step, rows }) {
  return (
    <div className="mb-3">
      <label className="form-label">{label}{required && <span className="text-danger"> *</span>}</label>
      {options ? (
        <select className="form-select" name={name} value={value ?? ""} onChange={onChange} required={required}>
          <option value="">Select {label.toLowerCase()}</option>
          {options.map((option) => (
            <option key={option.value} value={option.value}>{option.label}</option>
          ))}
        </select>
      ) : type === "textarea" ? (
        <textarea className="form-control" name={name} value={value ?? ""} onChange={onChange} required={required} rows={rows || 3} placeholder={placeholder} />
      ) : (
        <input className="form-control" name={name} type={type} value={value ?? ""} onChange={onChange} required={required} placeholder={placeholder} min={min} step={step} />
      )}
    </div>
  );
}

export function Notice({ notice, onClose }) {
  if (!notice) return null;
  return (
    <div className={`alert alert-${notice.type || "info"} alert-dismissible fade show`} role="alert">
      {notice.message}
      <button type="button" className="btn-close" onClick={onClose} aria-label="Close" />
    </div>
  );
}

export function Loading({ label = "Loading data..." }) {
  return <div className="loading-state"><div className="spinner-border spinner-border-sm text-primary" /> <span>{label}</span></div>;
}

export function ErrorState({ message, onRetry }) {
  return <div className="alert alert-danger d-flex justify-content-between align-items-center gap-3">
    <span>{message}</span>{onRetry && <button className="btn btn-sm btn-outline-danger" onClick={onRetry}>Retry</button>}
  </div>;
}

export function FormActions({ onCancel, submitLabel = "Save" }) {
  return <div className="d-flex gap-2 mt-2">
    <button className="btn btn-primary" type="submit">{submitLabel}</button>
    {onCancel && <button className="btn btn-light border" type="button" onClick={onCancel}>Cancel</button>}
  </div>;
}
