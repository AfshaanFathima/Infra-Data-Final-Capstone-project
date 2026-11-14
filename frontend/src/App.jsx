import React, { useCallback, useEffect, useState } from "react";
import { NavLink, Route, Routes, useLocation } from "react-router-dom";
import { api, get, post, put, putAction, del, queryString } from "./api";
import { DataTable, ErrorState, Field, FormActions, Loading, Notice, PageHeader, Panel, StatCard, Status } from "./components/UI";

const navGroups = [
  { title: "OVERVIEW", items: [{ to: "/", label: "Dashboard", icon: "bi-grid-1x2" }] },
  { title: "OPERATIONS", items: [
    { to: "/medicines", label: "Medicines", icon: "bi-capsule" },
    { to: "/warehouses", label: "Warehouses", icon: "bi-building" },
    { to: "/inventory", label: "Inventory", icon: "bi-box-seam" },
    { to: "/substitutes", label: "Substitutions", icon: "bi-arrow-left-right" },
    { to: "/reservations", label: "Reservations", icon: "bi-clipboard-check" },
    { to: "/transfers", label: "Stock transfers", icon: "bi-truck" }
  ]},
  { title: "INSIGHTS", items: [{ to: "/analytics", label: "Analytics", icon: "bi-graph-up-arrow" }] }
];

function Shell({ children }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const location = useLocation();
  useEffect(() => setMobileOpen(false), [location.pathname]);
  return <div className="app-shell">
    <aside className={`sidebar ${mobileOpen ? "sidebar-open" : ""}`}>
      <div className="brand">
        <div className="brand-mark"><i className="bi bi-heart-pulse-fill" /></div>
        <div><strong>PharmaTrack</strong><small>INVENTORY CONTROL</small></div>
      </div>
      <div className="workspace-label">WORKSPACE</div>
      {navGroups.map(group => <div className="nav-group" key={group.title}>
        <div className="nav-caption">{group.title}</div>
        {group.items.map(item => <NavLink end={item.to === "/"} to={item.to} key={item.to} className={({ isActive }) => `side-link ${isActive ? "active" : ""}`}>
          <i className={`bi ${item.icon}`} /><span>{item.label}</span>
        </NavLink>)}
      </div>)}
      <div className="sidebar-bottom">
        <div className="online-dot" /> Backend connection uses localhost:8080
      </div>
    </aside>
    {mobileOpen && <div className="mobile-shade" onClick={() => setMobileOpen(false)} />}
    <main className="main-area">
      <header className="topbar">
        <button className="mobile-menu btn btn-light" onClick={() => setMobileOpen(!mobileOpen)}><i className="bi bi-list" /></button>
        <div className="topbar-title">Pharmaceutical operations platform</div>
        <div className="topbar-right"><span className="connection"><span className="online-dot" /> API workspace</span><div className="user-avatar">PT</div></div>
      </header>
      <div className="content-area">{children}</div>
      <footer className="app-footer">PharmaTrack <span>•</span> Pharmaceutical Inventory Expiry &amp; Substitution Planner</footer>
    </main>
  </div>;
}

function usePageData(loader, deps = []) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const refresh = useCallback(async () => {
    setLoading(true); setError("");
    try { setData(await loader()); }
    catch (e) { setError(e.message || "Unable to load data."); }
    finally { setLoading(false); }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);
  useEffect(() => { refresh(); }, [refresh]);
  return { data, setData, loading, error, refresh };
}

function useNotice() {
  const [notice, setNotice] = useState(null);
  const show = (message, type = "success") => {
    setNotice({ message, type });
    window.scrollTo({ top: 0, behavior: "smooth" });
  };
  return { notice, show, clear: () => setNotice(null) };
}

function ActionButton({ children, onClick, tone = "primary", disabled = false, title }) {
  return <button type="button" title={title} disabled={disabled} className={`btn btn-sm btn-${tone}`} onClick={onClick}>{children}</button>;
}

function Dashboard() {
  const { data, loading, error, refresh } = usePageData(async () => {
    const [dashboard, expiry, reservations, transfers] = await Promise.all([
      get("/analytics/dashboard"), get("/analytics/expiry"),
      get("/analytics/reservations"), get("/analytics/transfers")
    ]);
    return { dashboard, expiry, reservations, transfers };
  });
  return <>
    <PageHeader title="Dashboard" subtitle="A live overview of medicine stock, expiry risk and operational activity." action={<button className="btn btn-outline-primary" onClick={refresh}><i className="bi bi-arrow-clockwise me-2" />Refresh data</button>} />
    {loading && <Loading />}
    {error && <ErrorState message={error} onRetry={refresh} />}
    {data && <>
      <div className="section-kicker">INVENTORY SNAPSHOT</div>
      <div className="row g-3 mb-4">
        <div className="col-6 col-xl-3"><StatCard label="Total medicines" value={data.dashboard.totalMedicines} icon="bi-capsule" tone="blue" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Warehouses" value={data.dashboard.totalWarehouses} icon="bi-building" tone="purple" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Inventory batches" value={data.dashboard.totalInventoryBatches} icon="bi-box-seam" tone="teal" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Available units" value={data.dashboard.totalAvailableUnits} icon="bi-boxes" tone="green" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Reserved units" value={data.dashboard.totalReservedUnits} icon="bi-lock" tone="purple" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Quarantined units" value={data.dashboard.totalQuarantinedUnits} icon="bi-shield-exclamation" tone="orange" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Expired batches" value={data.dashboard.expiredBatches} icon="bi-calendar-x" tone="red" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Near expiry batches" value={data.dashboard.nearExpiryBatches} icon="bi-hourglass-split" tone="orange" /></div>
      </div>
      <div className="row g-3">
        <div className="col-lg-6"><Panel title="Expiry exposure" subtitle="Stock requiring attention">
          <div className="metric-line"><span>Expired units</span><strong>{data.dashboard.expiredUnits ?? data.expiry.expiredBatchCount ?? 0}</strong></div>
          <div className="metric-line"><span>Estimated expiry loss</span><strong>{money(data.dashboard.estimatedExpiryLoss ?? data.expiry.estimatedExpiryLoss)}</strong></div>
          <div className="metric-line"><span>Expired batches</span><strong>{data.expiry.expiredBatchCount ?? data.dashboard.expiredBatches ?? 0}</strong></div>
          <div className="metric-line"><span>Near-expiry batches</span><strong>{data.expiry.nearExpiryBatchCount ?? data.dashboard.nearExpiryBatches ?? 0}</strong></div>
        </Panel></div>
        <div className="col-lg-6"><Panel title="Pending actions" subtitle="Work items needing review">
          <div className="metric-line"><span>Pending reservations</span><strong>{data.dashboard.pendingReservations ?? data.reservations.pending ?? 0}</strong></div>
          <div className="metric-line"><span>Pending transfers</span><strong>{data.dashboard.pendingTransfers ?? data.transfers.recommended ?? 0}</strong></div>
          <div className="metric-line"><span>Total reservations</span><strong>{data.reservations.totalReservations ?? 0}</strong></div>
          <div className="metric-line"><span>Total transfer recommendations</span><strong>{data.transfers.totalTransfers ?? 0}</strong></div>
        </Panel></div>
      </div>
    </>}
  </>;
}

const medicineFields = [
  ["medicineCode", "Medicine code", "text", true],
  ["genericName", "Generic name", "text", true],
  ["brandName", "Brand name", "text"],
  ["strength", "Strength", "text"],
  ["dosageForm", "Dosage form", "text"],
  ["manufacturer", "Manufacturer", "text"],
  ["unitOfMeasure", "Unit of measure", "text"]
];
const warehouseFields = [
  ["warehouseCode", "Warehouse code", "text", true],
  ["warehouseName", "Warehouse name", "text", true],
  ["address", "Address", "text"], ["city", "City", "text"], ["state", "State", "text"],
  ["postalCode", "Postal code", "text"], ["contactPerson", "Contact person", "text"], ["contactPhone", "Contact phone", "text"]
];

function EntityForm({ title, fields, initial = {}, onSubmit, onCancel, submitLabel }) {
  const [values, setValues] = useState(initial);
  useEffect(() => setValues(initial), [initial]);
  const change = e => setValues(v => ({ ...v, [e.target.name]: e.target.value }));
  return <Panel title={title} className="form-panel">
    <form onSubmit={e => { e.preventDefault(); onSubmit(values); }}>
      <div className="row">
        {fields.map(([name, label, type = "text", required = false]) => <div className="col-md-6" key={name}>
          <Field name={name} label={label} type={type} required={required} value={values[name] ?? ""} onChange={change} />
        </div>)}
      </div>
      <FormActions onCancel={onCancel} submitLabel={submitLabel || "Save"} />
    </form>
  </Panel>;
}

function Medicines() {
  const { data: rows, loading, error, refresh } = usePageData(() => get("/medicines"), []);
  const { notice, show, clear } = useNotice();
  const [editing, setEditing] = useState(null);
  const [creating, setCreating] = useState(false);
  const save = async values => {
    try {
      if (editing) await put(`/medicines/${editing.medicineId}`, values);
      else await post("/medicines", values);
      setEditing(null); setCreating(false); show(editing ? "Medicine updated." : "Medicine created."); refresh();
    } catch (e) { show(e.message, "danger"); }
  };
  const remove = async row => {
    if (!window.confirm(`Delete medicine ${row.genericName}?`)) return;
    try { await del(`/medicines/${row.medicineId}`); show("Medicine deleted."); refresh(); }
    catch (e) { show(e.message, "danger"); }
  };
  return <>
    <PageHeader title="Medicines" subtitle="Maintain the medicine catalogue and product details." action={<button className="btn btn-primary" onClick={() => { setEditing(null); setCreating(!creating); }}><i className="bi bi-plus-lg me-2" />Add medicine</button>} />
    <Notice notice={notice} onClose={clear} />
    {(creating || editing) && <EntityForm title={editing ? "Edit medicine" : "New medicine"} fields={medicineFields} initial={editing || {}} onSubmit={save} onCancel={() => { setCreating(false); setEditing(null); }} />}
    <Panel title="Medicine catalogue" subtitle={`${rows?.length ?? 0} records`}>
      {loading ? <Loading /> : error ? <ErrorState message={error} onRetry={refresh} /> : <DataTable rowKey="medicineId" rows={rows || []} columns={[
        { key: "medicineCode", label: "Code" }, { key: "genericName", label: "Generic name" },
        { key: "brandName", label: "Brand" }, { key: "strength", label: "Strength" },
        { key: "dosageForm", label: "Dosage form" }, { key: "manufacturer", label: "Manufacturer" },
        { key: "isActive", label: "Status", render: r => <Status value={r.isActive ? "ACTIVE" : "INACTIVE"} /> }
      ]} renderActions={r => <div className="d-flex justify-content-end gap-1">
        <ActionButton tone="light border" onClick={() => { setCreating(false); setEditing(r); }}> <i className="bi bi-pencil" /> </ActionButton>
        <ActionButton tone="outline-danger" onClick={() => remove(r)}><i className="bi bi-trash" /></ActionButton>
      </div>} />}
    </Panel>
  </>;
}

function Warehouses() {
  const { data: rows, loading, error, refresh } = usePageData(() => get("/warehouses"), []);
  const { notice, show, clear } = useNotice();
  const [editing, setEditing] = useState(null);
  const [creating, setCreating] = useState(false);
  const save = async values => {
    try {
      if (editing) await put(`/warehouses/${editing.warehouseId}`, values);
      else await post("/warehouses", values);
      setEditing(null); setCreating(false); show(editing ? "Warehouse updated." : "Warehouse created."); refresh();
    } catch (e) { show(e.message, "danger"); }
  };
  const deactivate = async row => {
    if (!window.confirm(`Deactivate ${row.warehouseName}?`)) return;
    try { await del(`/warehouses/${row.warehouseId}`); show("Warehouse deactivated."); refresh(); }
    catch (e) { show(e.message, "danger"); }
  };
  return <>
    <PageHeader title="Warehouses" subtitle="Manage storage locations and warehouse contacts." action={<button className="btn btn-primary" onClick={() => { setEditing(null); setCreating(!creating); }}><i className="bi bi-plus-lg me-2" />Add warehouse</button>} />
    <Notice notice={notice} onClose={clear} />
    {(creating || editing) && <EntityForm title={editing ? "Edit warehouse" : "New warehouse"} fields={warehouseFields} initial={editing || {}} onSubmit={save} onCancel={() => { setCreating(false); setEditing(null); }} />}
    <Panel title="Warehouse directory" subtitle={`${rows?.length ?? 0} records`}>
      {loading ? <Loading /> : error ? <ErrorState message={error} onRetry={refresh} /> : <DataTable rowKey="warehouseId" rows={rows || []} columns={[
        { key: "warehouseCode", label: "Code" }, { key: "warehouseName", label: "Warehouse" },
        { key: "city", label: "City" }, { key: "state", label: "State" },
        { key: "contactPerson", label: "Contact" }, { key: "contactPhone", label: "Phone" },
        { key: "isActive", label: "Status", render: r => <Status value={r.isActive ? "ACTIVE" : "INACTIVE"} /> }
      ]} renderActions={r => <div className="d-flex justify-content-end gap-1">
        <ActionButton tone="light border" onClick={() => { setCreating(false); setEditing(r); }}><i className="bi bi-pencil" /></ActionButton>
        {r.isActive && <ActionButton tone="outline-danger" onClick={() => deactivate(r)}><i className="bi bi-person-x" /></ActionButton>}
      </div>} />}
    </Panel>
  </>;
}

function Inventory() {
  const { data: batches, loading, error, refresh } = usePageData(() => get("/inventory/batches"), []);
  const { data: medicines } = usePageData(() => get("/medicines"), []);
  const { data: warehouses } = usePageData(() => get("/warehouses"), []);
  const { data: summary, refresh: refreshSummary } = usePageData(() => get("/inventory/summary"), []);
  const { notice, show, clear } = useNotice();
  const [showForm, setShowForm] = useState(false);
  const [filterMedicine, setFilterMedicine] = useState("");
  const [fefo, setFefo] = useState(null);
  const [form, setForm] = useState({ medicineId: "", warehouseId: "", batchNumber: "", manufacturingDate: "", expiryDate: "", receivedQuantity: "", quantityOnHand: "", unitCost: "" });
  const refreshAll = () => { refresh(); refreshSummary(); };
  const submit = async e => {
    e.preventDefault();
    const body = { ...form, medicineId: Number(form.medicineId), warehouseId: Number(form.warehouseId), receivedQuantity: Number(form.receivedQuantity), quantityOnHand: Number(form.quantityOnHand), unitCost: form.unitCost === "" ? null : Number(form.unitCost), manufacturingDate: form.manufacturingDate || null };
    try { await post("/inventory/batches", body); show("Inventory batch created."); setShowForm(false); refreshAll(); }
    catch (err) { show(err.message, "danger"); }
  };
  const loadFefo = async () => {
    if (!filterMedicine) { show("Select a medicine first.", "warning"); return; }
    try { setFefo(await get(`/inventory/fefo/${filterMedicine}`)); }
    catch (e) { show(e.message, "danger"); }
  };
  const visible = filterMedicine ? (batches || []).filter(b => String(b.medicineId) === String(filterMedicine)) : (batches || []);
  return <>
    <PageHeader title="Inventory" subtitle="Track batch-level quantities, expiry dates and FEFO stock eligibility." action={<button className="btn btn-primary" onClick={() => setShowForm(!showForm)}><i className="bi bi-plus-lg me-2" />Add batch</button>} />
    <Notice notice={notice} onClose={clear} />
    <div className="row g-3 mb-3">
      <div className="col-6 col-xl-2"><StatCard label="Total batches" value={summary?.totalBatches} icon="bi-box-seam" /></div>
      <div className="col-6 col-xl-2"><StatCard label="Units on hand" value={summary?.totalUnitsOnHand} icon="bi-boxes" tone="teal" /></div>
      <div className="col-6 col-xl-2"><StatCard label="Available units" value={summary?.totalAvailableUnits} icon="bi-check2-circle" tone="green" /></div>
      <div className="col-6 col-xl-2"><StatCard label="Expired batches" value={summary?.expiredBatches} icon="bi-calendar-x" tone="red" /></div>
      <div className="col-6 col-xl-2"><StatCard label="Near expiry" value={summary?.nearExpiryBatches} icon="bi-hourglass-split" tone="orange" /></div>
      <div className="col-6 col-xl-2"><StatCard label="Quarantined" value={summary?.quarantinedBatches} icon="bi-shield-exclamation" tone="purple" /></div>
    </div>
    {showForm && <Panel title="Create inventory batch" className="form-panel">
      <form onSubmit={submit}><div className="row">
        <div className="col-md-6"><Field label="Medicine" name="medicineId" required value={form.medicineId} options={(medicines || []).map(m => ({ value: m.medicineId, label: `${m.medicineCode} — ${m.genericName}` }))} onChange={e => setForm({ ...form, medicineId: e.target.value })} /></div>
        <div className="col-md-6"><Field label="Warehouse" name="warehouseId" required value={form.warehouseId} options={(warehouses || []).map(w => ({ value: w.warehouseId, label: `${w.warehouseCode} — ${w.warehouseName}` }))} onChange={e => setForm({ ...form, warehouseId: e.target.value })} /></div>
        {[
          ["batchNumber", "Batch number", "text", false], ["manufacturingDate", "Manufacturing date", "date", false],
          ["expiryDate", "Expiry date", "date", true], ["receivedQuantity", "Received quantity", "number", true],
          ["quantityOnHand", "Quantity on hand", "number", true], ["unitCost", "Unit cost", "number", false]
        ].map(([name, label, type, required]) => <div className="col-md-6" key={name}><Field label={label} name={name} type={type} required={required} min={type === "number" ? "0" : undefined} step={name === "unitCost" ? "0.01" : "1"} value={form[name]} onChange={e => setForm({ ...form, [name]: e.target.value })} /></div>)}
      </div><FormActions onCancel={() => setShowForm(false)} submitLabel="Create batch" /></form>
    </Panel>}
    <Panel title="Batch inventory" subtitle={`${visible.length} batches displayed`}>
      <div className="filter-row mb-3">
        <select className="form-select filter-select" value={filterMedicine} onChange={e => { setFilterMedicine(e.target.value); setFefo(null); }}>
          <option value="">All medicines</option>{(medicines || []).map(m => <option key={m.medicineId} value={m.medicineId}>{m.genericName}</option>)}
        </select>
        <button className="btn btn-outline-primary" onClick={loadFefo} disabled={!filterMedicine}><i className="bi bi-sort-down me-2" />Show FEFO order</button>
        {fefo && <button className="btn btn-light border" onClick={() => setFefo(null)}>Clear FEFO view</button>}
      </div>
      {fefo && <div className="alert alert-info small">FEFO order: eligible batches sorted by earliest expiry first.</div>}
      {loading ? <Loading /> : error ? <ErrorState message={error} onRetry={refresh} /> : <DataTable rowKey="batchId" rows={fefo || visible} columns={[
        { key: "batchNumber", label: "Batch" }, { key: "medicineName", label: "Medicine" },
        { key: "warehouseName", label: "Warehouse" }, { key: "expiryDate", label: "Expiry" },
        { key: "quantityOnHand", label: "On hand" }, { key: "availableQuantity", label: "Available" },
        { key: "unitCost", label: "Unit cost", render: r => money(r.unitCost) },
        { key: "expiryStatus", label: "Expiry", render: r => <Status value={r.expiryStatus} /> },
        { key: "batchStatus", label: "Batch status", render: r => <Status value={r.batchStatus} /> }
      ]} />}
    </Panel>
  </>;
}

function Substitutes() {
  const { data: rows, loading, error, refresh } = usePageData(() => get("/substitutes"), []);
  const { data: medicines } = usePageData(() => get("/medicines"), []);
  const { notice, show, clear } = useNotice();
  const [form, setForm] = useState({ medicineId: "", substituteMedicineId: "", clinicalRationale: "", effectiveFrom: "", effectiveUntil: "" });
  const [showForm, setShowForm] = useState(false);
  const [approvedFor, setApprovedFor] = useState("");
  const submit = async e => {
    e.preventDefault();
    try { await post("/substitutes", { ...form, medicineId: Number(form.medicineId), substituteMedicineId: Number(form.substituteMedicineId), effectiveFrom: form.effectiveFrom || null, effectiveUntil: form.effectiveUntil || null }); show("Substitution request submitted for approval."); setShowForm(false); refresh(); }
    catch (err) { show(err.message, "danger"); }
  };
  const review = async (row, status) => {
    const approvedBy = status === "APPROVED" ? (window.prompt("Approved by:", "Admin") || "") : "";
    if (status === "APPROVED" && !approvedBy.trim()) return;
    try { await api(`/substitutes/${row.substitutionId}/approval?${queryString({ status, ...(approvedBy ? { approvedBy } : {}) })}`, { method: "PUT" }); show(`Substitution ${status.toLowerCase()}.`); refresh(); }
    catch (e) { show(e.message, "danger"); }
  };
  const medicineOptions = (medicines || []).map(m => ({ value: m.medicineId, label: `${m.medicineCode} — ${m.genericName}` }));
  return <>
    <PageHeader title="Substitutions" subtitle="Manage clinically justified alternatives and approval status." action={<button className="btn btn-primary" onClick={() => setShowForm(!showForm)}><i className="bi bi-plus-lg me-2" />New request</button>} />
    <Notice notice={notice} onClose={clear} />
    {showForm && <Panel title="Create substitution request" className="form-panel"><form onSubmit={submit}><div className="row">
      <div className="col-md-6"><Field label="Original medicine" name="medicineId" required value={form.medicineId} options={medicineOptions} onChange={e => setForm({ ...form, medicineId: e.target.value })} /></div>
      <div className="col-md-6"><Field label="Substitute medicine" name="substituteMedicineId" required value={form.substituteMedicineId} options={medicineOptions.filter(m => String(m.value) !== String(form.medicineId))} onChange={e => setForm({ ...form, substituteMedicineId: e.target.value })} /></div>
      <div className="col-12"><Field label="Clinical rationale" name="clinicalRationale" type="textarea" value={form.clinicalRationale} onChange={e => setForm({ ...form, clinicalRationale: e.target.value })} /></div>
      <div className="col-md-6"><Field label="Effective from" name="effectiveFrom" type="date" value={form.effectiveFrom} onChange={e => setForm({ ...form, effectiveFrom: e.target.value })} /></div>
      <div className="col-md-6"><Field label="Effective until" name="effectiveUntil" type="date" value={form.effectiveUntil} onChange={e => setForm({ ...form, effectiveUntil: e.target.value })} /></div>
    </div><FormActions onCancel={() => setShowForm(false)} submitLabel="Submit request" /></form></Panel>}
    <Panel title="Substitution register" subtitle={`${rows?.length ?? 0} records`}>
      <div className="filter-row mb-3"><select className="form-select filter-select" value={approvedFor} onChange={e => setApprovedFor(e.target.value)}><option value="">All medicines</option>{medicineOptions.map(m => <option key={m.value} value={m.value}>{m.label}</option>)}</select>
        {approvedFor && <button className="btn btn-outline-primary" onClick={async () => { try { const result = await get(`/substitutes/approved/${approvedFor}`); show(`${result.length} approved substitute(s) found.`); } catch (e) { show(e.message, "danger"); } }}>Check approved alternatives</button>}
      </div>
      {loading ? <Loading /> : error ? <ErrorState message={error} onRetry={refresh} /> : <DataTable rowKey="substitutionId" rows={(rows || []).filter(r => !approvedFor || String(r.medicineId) === approvedFor)} columns={[
        { key: "medicineName", label: "Original medicine" }, { key: "substituteMedicineName", label: "Alternative" },
        { key: "clinicalRationale", label: "Clinical rationale" }, { key: "effectiveFrom", label: "From" },
        { key: "effectiveUntil", label: "Until" }, { key: "approvalStatus", label: "Approval", render: r => <Status value={r.approvalStatus} /> },
        { key: "approvedBy", label: "Reviewed by" }
      ]} renderActions={r => r.approvalStatus === "PENDING" ? <div className="d-flex justify-content-end gap-1">
        <ActionButton tone="success" onClick={() => review(r, "APPROVED")}>Approve</ActionButton>
        <ActionButton tone="outline-danger" onClick={() => review(r, "REJECTED")}>Reject</ActionButton>
      </div> : <span className="text-secondary small">Reviewed</span>} />}
    </Panel>
  </>;
}

function Reservations() {
  const { data: rows, loading, error, refresh } = usePageData(() => get("/reservations"), []);
  const { data: medicines } = usePageData(() => get("/medicines"), []);
  const { notice, show, clear } = useNotice();
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ medicineId: "", requestedQuantity: "", customerReference: "", expiresAt: "", notes: "" });
  const submit = async e => {
    e.preventDefault();
    try { await post("/reservations", { ...form, medicineId: Number(form.medicineId), requestedQuantity: Number(form.requestedQuantity), expiresAt: form.expiresAt ? `${form.expiresAt}:00` : null }); show("Reservation created and stock allocated using FEFO."); setShowForm(false); refresh(); }
    catch (err) { show(err.message, "danger"); }
  };
  const action = async (id, type) => {
    try { await putAction(`/reservations/${id}/${type}`); show(`Reservation ${type} processed.`); refresh(); }
    catch (e) { show(e.message, "danger"); }
  };
  const expire = async () => {
    if (!window.confirm("Process all reservations whose expiry time has passed?")) return;
    try { const result = await post("/reservations/expire", {}); show(`Expiry processing complete. Expired: ${result?.expiredReservations ?? 0}`); refresh(); }
    catch (e) { show(e.message, "danger"); }
  };
  return <>
    <PageHeader title="Reservations" subtitle="Allocate stock using FEFO and manage reservation lifecycle." action={<div className="d-flex gap-2"><button className="btn btn-outline-primary" onClick={expire}><i className="bi bi-clock-history me-2" />Process expiry</button><button className="btn btn-primary" onClick={() => setShowForm(!showForm)}><i className="bi bi-plus-lg me-2" />New reservation</button></div>} />
    <Notice notice={notice} onClose={clear} />
    {showForm && <Panel title="Create reservation" className="form-panel"><form onSubmit={submit}><div className="row">
      <div className="col-md-6"><Field label="Medicine" name="medicineId" required value={form.medicineId} options={(medicines || []).map(m => ({ value: m.medicineId, label: `${m.medicineCode} — ${m.genericName}` }))} onChange={e => setForm({ ...form, medicineId: e.target.value })} /></div>
      <div className="col-md-6"><Field label="Requested quantity" name="requestedQuantity" type="number" min="1" required value={form.requestedQuantity} onChange={e => setForm({ ...form, requestedQuantity: e.target.value })} /></div>
      <div className="col-md-6"><Field label="Customer reference" name="customerReference" value={form.customerReference} onChange={e => setForm({ ...form, customerReference: e.target.value })} /></div>
      <div className="col-md-6"><Field label="Expires at" name="expiresAt" type="datetime-local" value={form.expiresAt} onChange={e => setForm({ ...form, expiresAt: e.target.value })} /></div>
      <div className="col-12"><Field label="Notes" name="notes" type="textarea" value={form.notes} onChange={e => setForm({ ...form, notes: e.target.value })} /></div>
    </div><FormActions onCancel={() => setShowForm(false)} submitLabel="Create reservation" /></form></Panel>}
    <Panel title="Reservation register" subtitle={`${rows?.length ?? 0} reservations`}>
      {loading ? <Loading /> : error ? <ErrorState message={error} onRetry={refresh} /> : <DataTable rowKey="reservationId" rows={rows || []} columns={[
        { key: "reservationCode", label: "Reservation" }, { key: "medicineName", label: "Medicine" },
        { key: "requestedQuantity", label: "Requested" }, { key: "allocatedQuantity", label: "Allocated" },
        { key: "customerReference", label: "Customer ref" }, { key: "status", label: "Status", render: r => <Status value={r.status} /> },
        { key: "expiresAt", label: "Expires" },
        { key: "allocations", label: "Allocations", render: r => <span className="small">{r.allocations?.map(a => `${a.batchNumber}: ${a.allocatedQuantity}`).join(", ") || "—"}</span> }
      ]} renderActions={r => <div className="d-flex justify-content-end gap-1">
        {r.status === "PENDING" && <><ActionButton tone="success" onClick={() => action(r.reservationId, "confirm")}>Confirm</ActionButton><ActionButton tone="outline-danger" onClick={() => action(r.reservationId, "cancel")}>Cancel</ActionButton></>}
      </div>} />}
    </Panel>
  </>;
}

function Transfers() {
  const { data: rows, loading, error, refresh } = usePageData(() => get("/transfers"), []);
  const { data: medicines } = usePageData(() => get("/medicines"), []);
  const { notice, show, clear } = useNotice();
  const [medicineId, setMedicineId] = useState("");
  const [status, setStatus] = useState("");
  const [reviewer, setReviewer] = useState("Admin");
  const [filtered, setFiltered] = useState(null);
  const generate = async () => {
    if (!medicineId) { show("Select a medicine to generate recommendations.", "warning"); return; }
    try { const result = await post(`/transfers/recommendations/${medicineId}`); show(`${result?.length ?? 0} recommendation(s) generated.`); setFiltered(null); refresh(); }
    catch (e) { show(e.message, "danger"); }
  };
  const filterStatus = async value => {
    setStatus(value); setFiltered(null);
    if (!value) return;
    try { setFiltered(await get(`/transfers/status/${value}`)); }
    catch (e) { show(e.message, "danger"); }
  };
  const review = async (row, value) => {
    if (!reviewer.trim()) { show("Enter the reviewer name.", "warning"); return; }
    try { await api(`/transfers/${row.transferId}/review?${queryString({ status: value, reviewedBy: reviewer })}`, { method: "PUT" }); show(`Transfer ${value.toLowerCase()}.`); setFiltered(null); refresh(); }
    catch (e) { show(e.message, "danger"); }
  };
  const visible = filtered ?? (rows || []);
  return <>
    <PageHeader title="Stock transfers" subtitle="Generate stock redistribution recommendations and review them." />
    <Notice notice={notice} onClose={clear} />
    <Panel title="Recommendation controls" subtitle="Recommendations are advisory; approval does not automatically execute a physical transfer.">
      <div className="row align-items-end">
        <div className="col-md-5"><Field label="Medicine" name="medicineId" value={medicineId} options={(medicines || []).map(m => ({ value: m.medicineId, label: `${m.medicineCode} — ${m.genericName}` }))} onChange={e => setMedicineId(e.target.value)} /></div>
        <div className="col-md-3 mb-3"><button className="btn btn-primary w-100" onClick={generate}><i className="bi bi-lightning-charge me-2" />Generate recommendations</button></div>
        <div className="col-md-4"><Field label="Filter by status" name="status" value={status} options={[{ value: "RECOMMENDED", label: "Recommended" }, { value: "APPROVED", label: "Approved" }, { value: "REJECTED", label: "Rejected" }, { value: "CANCELLED", label: "Cancelled" }]} onChange={e => filterStatus(e.target.value)} /></div>
        <div className="col-md-4"><Field label="Reviewed by" name="reviewer" value={reviewer} onChange={e => setReviewer(e.target.value)} /></div>
        {status && <div className="col-md-3 mb-3"><button className="btn btn-light border w-100" onClick={() => { setStatus(""); setFiltered(null); }}>Clear filter</button></div>}
      </div>
    </Panel>
    <Panel title="Transfer recommendations" subtitle={`${visible.length} records`}>
      {loading ? <Loading /> : error ? <ErrorState message={error} onRetry={refresh} /> : <DataTable rowKey="transferId" rows={visible} columns={[
        { key: "medicineName", label: "Medicine" }, { key: "sourceWarehouseName", label: "Source" },
        { key: "destinationWarehouseName", label: "Destination" }, { key: "suggestedQuantity", label: "Suggested qty" },
        { key: "transferReason", label: "Reason" }, { key: "recommendationDate", label: "Date" },
        { key: "status", label: "Status", render: r => <Status value={r.status} /> },
        { key: "reviewedBy", label: "Reviewed by" }
      ]} renderActions={r => r.status === "RECOMMENDED" ? <div className="d-flex justify-content-end gap-1">
        <ActionButton tone="success" onClick={() => review(r, "APPROVED")}>Approve</ActionButton>
        <ActionButton tone="outline-danger" onClick={() => review(r, "REJECTED")}>Reject</ActionButton>
      </div> : <span className="small text-secondary">No action</span>} />}
    </Panel>
  </>;
}

function Analytics() {
  const [active, setActive] = useState("expiry");
  const endpoints = { expiry: "/analytics/expiry", reservations: "/analytics/reservations", transfers: "/analytics/transfers", consumption: "/analytics/consumption" };
  const { data, loading, error, refresh } = usePageData(() => get(endpoints[active]), [active]);
  const title = { expiry: "Expiry analysis", reservations: "Reservation analysis", transfers: "Transfer analysis", consumption: "Consumption analysis" }[active];
  return <>
    <PageHeader title="Analytics" subtitle="Operational reports generated by your Spring Boot analytics services." action={<button className="btn btn-outline-primary" onClick={refresh}><i className="bi bi-arrow-clockwise me-2" />Refresh</button>} />
    <div className="tabs-strip mb-3">{Object.keys(endpoints).map(key => <button key={key} className={`report-tab ${active === key ? "active" : ""}`} onClick={() => setActive(key)}>{key[0].toUpperCase() + key.slice(1)}</button>)}</div>
    <Panel title={title} subtitle="Live response from the backend">
      {loading ? <Loading /> : error ? <ErrorState message={error} onRetry={refresh} /> : <AnalyticsContent data={data} />}
    </Panel>
  </>;
}

function AnalyticsContent({ data }) {
  if (!data || typeof data !== "object") return <div className="empty-cell">No analytics data returned.</div>;
  const entries = Object.entries(data);
  const arrays = entries.filter(([, value]) => Array.isArray(value));
  const scalars = entries.filter(([, value]) => !Array.isArray(value) && (value === null || ["string", "number", "boolean"].includes(typeof value)));
  const objects = entries.filter(([, value]) => value && typeof value === "object" && !Array.isArray(value));
  return <div>
    <div className="row g-3 mb-3">
      {scalars.map(([key, value]) => <div className="col-6 col-md-4 col-xl-3" key={key}><div className="analytics-metric"><div>{pretty(key)}</div><strong>{typeof value === "number" && key.toLowerCase().includes("loss") ? money(value) : String(value ?? "—")}</strong></div></div>)}
    </div>
    {objects.map(([key, value]) => <div className="nested-report mb-3" key={key}><h6>{pretty(key)}</h6><pre>{JSON.stringify(value, null, 2)}</pre></div>)}
    {arrays.map(([key, value]) => <div className="nested-report mb-3" key={key}><h6>{pretty(key)} <span className="text-secondary">({value.length})</span></h6>
      {value.length && typeof value[0] === "object" ? <DataTable columns={Object.keys(value[0]).map(k => ({ key: k, label: pretty(k) }))} rows={value} /> : <pre>{JSON.stringify(value, null, 2)}</pre>}
    </div>)}
  </div>;
}

function NotFound() {
  return <div className="not-found"><h2>Page not found</h2><p className="text-secondary">The page you requested does not exist.</p><a className="btn btn-primary" href="/">Return to dashboard</a></div>;
}

function money(value) {
  if (value === null || value === undefined || value === "") return "—";
  const n = Number(value);
  return Number.isFinite(n) ? new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 2 }).format(n) : String(value);
}
function pretty(value) { return String(value).replace(/([A-Z])/g, " $1").replaceAll("_", " ").replace(/^./, c => c.toUpperCase()); }

export default function App() {
  return <Shell><Routes>
    <Route path="/" element={<Dashboard />} />
    <Route path="/medicines" element={<Medicines />} />
    <Route path="/warehouses" element={<Warehouses />} />
    <Route path="/inventory" element={<Inventory />} />
    <Route path="/substitutes" element={<Substitutes />} />
    <Route path="/reservations" element={<Reservations />} />
    <Route path="/transfers" element={<Transfers />} />
    <Route path="/analytics" element={<Analytics />} />
    <Route path="*" element={<NotFound />} />
  </Routes></Shell>;
}
