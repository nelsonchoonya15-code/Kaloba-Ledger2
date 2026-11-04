
async function api(url, method = "GET", body) {
  const res = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: body ? JSON.stringify(body) : undefined
  });
  if (!res.ok) {
    showMessage(await res.text(), true);   // e.g. "Credit limit exceeded: ..."
    return null;
  }
  return res.status === 204 ? true : res.json();
}

function showMessage(text, isError) {
  const m = document.getElementById("message");
  m.textContent = text;
  m.style.color = isError ? "red" : "green";
}

function money(n) {
  return "K" + Number(n).toFixed(2);
}

function makeCell(text) {
  const td = document.createElement("td");
  td.textContent = text;          // textContent (not innerHTML) keeps names safe
  return td;
}

function makeButton(label, onClick) {
  const b = document.createElement("button");
  b.textContent = label;
  b.addEventListener("click", onClick);
  return b;
}

async function loadCustomers() {
  const onlyOwing = document.getElementById("onlyOwing").checked;
  const customers = await api("/api/customers" + (onlyOwing ? "?owing=true" : ""));
  if (!customers) return;

  const list = document.getElementById("list");
  list.innerHTML = "";
  for (const c of customers) {
    const row = document.createElement("tr");
    if (c.balance > 0) row.className = "owes";
    row.appendChild(makeCell(c.name));
    row.appendChild(makeCell(c.type));
    row.appendChild(makeCell(money(c.creditLimit)));
    row.appendChild(makeCell(money(c.balance)));

    const actions = document.createElement("td");
    actions.appendChild(makeButton("Rename", () => renameCustomer(c)));
    actions.appendChild(makeButton("Delete", () => deleteCustomer(c)));
    row.appendChild(actions);
    list.appendChild(row);
  }

  // the dropdowns always list every customer, even when the table is filtered
  const all = onlyOwing ? await api("/api/customers") : customers;
  for (const id of ["saleCustomer", "repayCustomer"]) {
    const select = document.getElementById(id);
    select.innerHTML = "";
    for (const c of all) select.add(new Option(c.name, c.id));
  }
}

async function addCustomer() {
  const name = document.getElementById("cName").value.trim();
  const type = document.getElementById("cType").value;
  if (!name) return showMessage("Enter a name", true);
  if (await api("/api/customers", "POST", { name, type })) {
    document.getElementById("cName").value = "";
    showMessage("Customer added");
    loadCustomers();
  }
}

async function renameCustomer(c) {
  const name = prompt("New name for " + c.name, c.name);
  if (!name) return;
  if (await api("/api/customers/" + c.id, "PUT", { name, type: c.type })) {
    showMessage("Customer updated");
    loadCustomers();
  }
}

async function deleteCustomer(c) {
  if (!confirm("Delete " + c.name + "?")) return;
  if (await api("/api/customers/" + c.id, "DELETE")) {
    showMessage("Customer deleted");
    loadCustomers();
  }
}

async function addSale() {
  const amount = parseFloat(document.getElementById("saleAmount").value);
  if (!(amount > 0)) return showMessage("Enter a valid amount", true);
  const ok = await api("/api/sales", "POST", {
    customerId: document.getElementById("saleCustomer").value,
    description: document.getElementById("saleDesc").value,
    amount
  });
  if (ok) {
    document.getElementById("saleAmount").value = "";
    document.getElementById("saleDesc").value = "";
    showMessage("Sale recorded");
    loadCustomers();
  }
}

async function addRepayment() {
  const amount = parseFloat(document.getElementById("repayAmount").value);
  if (!(amount > 0)) return showMessage("Enter a valid amount", true);
  const ok = await api("/api/repayments", "POST", {
    customerId: document.getElementById("repayCustomer").value,
    amount
  });
  if (ok) {
    document.getElementById("repayAmount").value = "";
    showMessage("Repayment recorded");
    loadCustomers();
  }
}

loadCustomers();