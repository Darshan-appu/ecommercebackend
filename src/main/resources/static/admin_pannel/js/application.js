const apiBase = "/api/applications";

document.addEventListener("DOMContentLoaded", loadApplications);

function loadApplications() {
  fetch(apiBase)
    .then(res => res.json())
    .then(data => {
      const tbody = document.querySelector("#applicationsTable tbody");
      tbody.innerHTML = "";
      data.forEach(app => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${app.id}</td>
          <td>${app.name}</td>
          <td>${app.products ? app.products.length : 0}</td>
          <td>
            <button class="btn btn-sm btn-warning" onclick="openEditModal(${app.id}, '${app.name}')">
              <i class="fas fa-edit"></i>
            </button>
            <button class="btn btn-sm btn-danger" onclick="deleteApplication(${app.id})">
              <i class="fas fa-trash"></i>
            </button>
          </td>
        `;
        tbody.appendChild(tr);
      });
    });
}

function openAddModal() {
  document.getElementById("addApplicationForm").reset();
  document.getElementById("addApplicationModal").style.display = "block";
}
function closeAddModal() {
  document.getElementById("addApplicationModal").style.display = "none";
}

function saveApplication() {
  const name = document.getElementById("applicationName").value.trim();
  if (!name) {
    document.getElementById("applicationNameError").textContent = "Name is required.";
    document.getElementById("applicationNameError").style.display = "block";
    return;
  }
  fetch(apiBase, {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify({name})
  })
  .then(res => {
    if (res.ok) {
      closeAddModal();
      loadApplications();
    } else {
      alert("Failed to save application.");
    }
  });
}

function openEditModal(id, name) {
  document.getElementById("editApplicationId").value = id;
  document.getElementById("editApplicationName").value = name;
  document.getElementById("editApplicationModal").style.display = "block";
}
function closeEditModal() {
  document.getElementById("editApplicationModal").style.display = "none";
}

function updateApplication() {
  const id = document.getElementById("editApplicationId").value;
  const name = document.getElementById("editApplicationName").value.trim();
  if (!name) {
    document.getElementById("editApplicationNameError").textContent = "Name is required.";
    document.getElementById("editApplicationNameError").style.display = "block";
    return;
  }
  fetch(`${apiBase}/${id}`, {
    method: "PUT",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify({name})
  })
  .then(res => {
    if (res.ok) {
      closeEditModal();
      loadApplications();
    } else {
      alert("Failed to update application.");
    }
  });
}

function deleteApplication(id) {
  if (!confirm("Are you sure you want to delete this application?")) return;
  fetch(`${apiBase}/${id}`, { method: "DELETE" })
  .then(res => {
    if (res.ok) {
      loadApplications();
    } else {
      alert("Failed to delete application.");
    }
  });
}
