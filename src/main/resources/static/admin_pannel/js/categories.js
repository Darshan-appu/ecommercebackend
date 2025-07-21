// js/categories.js
// -------------------------------------------------------------
//  A L L  I N  O N E  – fully‑updated admin logic
//  – CRUD + image‑upload for categories
//  – JWT‑protected calls to Spring‑Boot backend
// -------------------------------------------------------------

// --- Configuration ---------------------------------------------------------
const API_BASE_URL = 'https://ecommercebackend-i16e.onrender.com/api/categories'; // Spring Boot API

// --- DOM REFS --------------------------------------------------------------
const categoriesTableBody = document.querySelector('#categoriesTable tbody');
const addCategoryModal = document.getElementById('addCategoryModal');
const editCategoryModal = document.getElementById('editCategoryModal');
const saveCategoryBtn = document.getElementById('saveCategoryBtn');
const updateCategoryBtn = document.getElementById('updateCategoryBtn');
const alertContainer = document.getElementById('alertContainer');

// *Add* modal inputs
const categoryNameInput = document.getElementById('categoryName');
const categorySlugInput = document.getElementById('categorySlug');
const categoryDescriptionInput = document.getElementById('categoryDescription');
const categoryStatusSelect = document.getElementById('categoryStatus');
const categoryImageInput = document.getElementById('categoryImage'); //  <-- For new category image upload

// *Edit* modal inputs
const editCategoryIdInput = document.getElementById('editCategoryId');
const editCategoryNameInput = document.getElementById('editCategoryName');
const editCategorySlugInput = document.getElementById('editCategorySlug');
const editCategoryDescriptionInput = document.getElementById('editCategoryDescription');
const editCategoryStatusSelect = document.getElementById('editCategoryStatus');
const editCategoryImageInput = document.getElementById('editCategoryImage'); // <-- For existing category image update

// --- EVENT WIRING ----------------------------------------------------------
document.addEventListener('DOMContentLoaded', () => {

    loadCategories(); // load table on page‑open
    saveCategoryBtn.addEventListener('click', saveCategory);
    updateCategoryBtn.addEventListener('click', updateCategory);

    categoryNameInput.addEventListener('input', () => categorySlugInput.value = generateSlug(categoryNameInput.value));
    editCategoryNameInput.addEventListener('input', () => editCategorySlugInput.value = generateSlug(editCategoryNameInput.value));

    // open modal for *Add*
    document.querySelector('[data-target="#addCategoryModal"]').addEventListener('click', () => {
        addCategoryModal.style.display = 'flex';
        document.getElementById('addCategoryForm').reset();
        categoryStatusSelect.value = 'Active'; // Default to active for new categories
    });

    // close modals
    document.querySelectorAll('.modal .close').forEach(c => c.addEventListener('click', () => {
        addCategoryModal.style.display = 'none';
        editCategoryModal.style.display = 'none';
        // Reset file inputs when closing modals
        if (categoryImageInput) categoryImageInput.value = '';
        if (editCategoryImageInput) editCategoryImageInput.value = '';
    }));
    window.addEventListener('click', e => {
        if (e.target === addCategoryModal) {
            addCategoryModal.style.display = 'none';
            if (categoryImageInput) categoryImageInput.value = '';
        }
        if (e.target === editCategoryModal) {
            editCategoryModal.style.display = 'none';
            if (editCategoryImageInput) editCategoryImageInput.value = '';
        }
    });
});

// --- LOAD ALL CATEGORIES ---------------------------------------------------
async function loadCategories(filter = '') {
    try {
        const token = localStorage.getItem('adminToken');
        if (!token) {
            showAlert('Please log in.', 'danger');
            // Redirect if not authenticated (already handled by the initial check, but good to have here too)
            window.location.href = '/admin_panel/admin-login.html';
            return;
        }

        const res = await fetch(`${API_BASE_URL}/admin/all`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (!res.ok) {
            // Check for 403 Forbidden specifically
            if (res.status === 403) {
                showAlert('Access denied. Please ensure you have admin privileges.', 'danger');
                window.location.href = '/admin_panel/admin-login.html'; // Redirect to login
                return;
            }
            throw new Error(`Failed to fetch categories: ${res.statusText || await res.text()}`);
        }

        const cats = await res.json();
        const visible = cats.filter(c =>
            !filter ||
            c.name.toLowerCase().includes(filter.toLowerCase()) ||
            c.slug.toLowerCase().includes(filter.toLowerCase())
        );
        renderCategoriesTable(visible);

    } catch (err) {
        console.error("Error loading categories:", err);
        showAlert(`Error loading categories: ${err.message}`, 'danger');
        if (categoriesTableBody) {
            categoriesTableBody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:red">Failed to load categories.</td></tr>';
        }
    }
}

// --- TABLE RENDERER --------------------------------------------------------
function renderCategoriesTable(list) {
    if (!categoriesTableBody) return; // Ensure table body exists
    categoriesTableBody.innerHTML = '';

    if (!list.length) {
        categoriesTableBody.innerHTML = '<tr><td colspan="5" style="text-align:center;">No categories found.</td></tr>';
        return;
    }

    list.forEach(c => {
        const tr = document.createElement('tr');
        // Removed the <td> for the image display
        tr.innerHTML = `
           <td>${c.id}</td>
           <td>${c.name}</td>
           <td>${c.slug}</td>
           <td><span class="status ${c.status.toLowerCase()}">${c.status}</span></td>
           <td>
              <button class="btn btn-primary btn-sm edit-btn" data-id="${c.id}"><i class="fas fa-edit"></i> Edit</button>
              <button class="btn btn-danger btn-sm delete-btn" data-id="${c.id}"><i class="fas fa-trash"></i> Delete</button>
           </td>`;
        categoriesTableBody.appendChild(tr);
    });

    // attach dynamic buttons
    document.querySelectorAll('.edit-btn').forEach(b => b.addEventListener('click', () => editCategory(b.dataset.id)));
    document.querySelectorAll('.delete-btn').forEach(b => b.addEventListener('click', () => deleteCategory(b.dataset.id)));
}

// --- SAVE (CREATE) ---------------------------------------------------------
async function saveCategory() {
    const name = categoryNameInput.value.trim();
    if (!name) return showAlert('Category Name is required.', 'danger');

    const data = {
        name,
        slug: categorySlugInput.value.trim() || null,
        description: categoryDescriptionInput.value.trim(),
        status: categoryStatusSelect.value,
        imageUrl: null // Initialize imageUrl to null, it will be updated after upload
    };

    try {
        const token = localStorage.getItem('adminToken');
        if (!token) return; // getToken() handles redirection

        const res = await fetch(`${API_BASE_URL}/admin`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify(data)
        });
        if (!res.ok) {
            throw new Error(`Failed to create category: ${res.statusText || await res.text()}`);
        }
        const createdCategory = await res.json(); // Get the created category object, which includes its ID

        // ---- Image upload (optional) ----
        const imgFile = categoryImageInput.files[0];
        if (imgFile) {
            await uploadImage(createdCategory.id, imgFile, token); // Use the ID of the newly created category
        }

        showAlert(`Category "${createdCategory.name}" added successfully.`, 'success');
        addCategoryModal.style.display = 'none';
        document.getElementById('addCategoryForm').reset();
        loadCategories(); // Reload categories to show the new one
    } catch (err) {
        console.error("Error saving category:", err);
        showAlert(`Error: ${err.message}`, 'danger');
    }
}

// --- EDIT (open modal) -----------------------------------------------------
async function editCategory(id) {
    try {
        const token = localStorage.getItem('adminToken');
        if (!token) return;

        const res = await fetch(`${API_BASE_URL}/admin/${id}`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!res.ok) {
            throw new Error(`Failed to fetch category: ${res.statusText || await res.text()}`);
        }
        const category = await res.json(); // This should be a CategoryDTO, containing imageUrl

        editCategoryIdInput.value = category.id;
        editCategoryNameInput.value = category.name;
        editCategorySlugInput.value = category.slug;
        editCategoryDescriptionInput.value = category.description || '';
        editCategoryStatusSelect.value = category.status;
        // No need to pre-fill the file input for security reasons
        // Current image might be displayed next to the input if desired, using category.imageUrl

        editCategoryModal.style.display = 'flex';
    } catch (err) {
        console.error("Error editing category:", err);
        showAlert(`Error: ${err.message}`, 'danger');
    }
}

// --- UPDATE ---------------------------------------------------------------
async function updateCategory() {
    const id = editCategoryIdInput.value;
    const name = editCategoryNameInput.value.trim();
    if (!name) return showAlert('Category Name is required.', 'danger');

    const data = {
        name,
        slug: editCategorySlugInput.value.trim() || null,
        description: editCategoryDescriptionInput.value.trim(),
        status: editCategoryStatusSelect.value
            // imageUrl will be updated by the separate uploadImage call if a new file is chosen
    };

    try {
        const token = localStorage.getItem('adminToken');
        if (!token) return;

        const res = await fetch(`${API_BASE_URL}/admin/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify(data)
        });
        if (!res.ok) {
            throw new Error(`Failed to update category: ${res.statusText || await res.text()}`);
        }
        const updatedCategory = await res.json();

        const imgFile = editCategoryImageInput.files[0];
        if (imgFile) {
            await uploadImage(id, imgFile, token); // Upload new image if selected
        }

        showAlert(`Category "${updatedCategory.name}" updated successfully.`, 'success');
        editCategoryModal.style.display = 'none';
        loadCategories(); // Reload categories to show updated details
    } catch (err) {
        console.error("Error updating category:", err);
        showAlert(`Error: ${err.message}`, 'danger');
    }
}

// --- DELETE ---------------------------------------------------------------
async function deleteCategory(id) {
    if (!confirm('Are you sure you want to delete this category? This action cannot be undone.')) return;
    try {
        const token = localStorage.getItem('adminToken');
        if (!token) return;

        const res = await fetch(`${API_BASE_URL}/admin/${id}`, {
            method: 'DELETE',
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!res.ok) {
            throw new Error(`Failed to delete category: ${res.statusText || await res.text()}`);
        }
        showAlert('Category deleted successfully.', 'success');
        loadCategories(); // Reload categories after deletion
    } catch (err) {
        console.error("Error deleting category:", err);
        showAlert(`Error: ${err.message}`, 'danger');
    }
}

// --- IMAGE UPLOAD helper ---------------------------------------------------
async function uploadImage(id, file, token) {
    const fd = new FormData();
    fd.append('image', file); // 'image' must match @RequestParam("image") in Spring
    try {
        const res = await fetch(`${API_BASE_URL}/admin/${id}/image`, {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${token}`
                    // Content-Type header is automatically set to multipart/form-data by the browser when using FormData
            },
            body: fd
        });
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Image upload failed: ${res.statusText || errorText}`);
        }
        // Optionally, you can process the response if your backend returns the URL
        // const result = await res.json();
        // console.log("Image upload result:", result);
    } catch (uploadError) {
        console.error("Error in uploadImage helper:", uploadError);
        throw new Error(`Failed to upload image: ${uploadError.message}`); // Re-throw to be caught by save/updateCategory
    }
}

// --- UTILITIES -------------------------------------------------------------
function showAlert(msg, type) {
    if (!alertContainer) {
        console.error("Alert container not found!");
        return;
    }
    alertContainer.innerHTML =
        `<div class="alert alert-${type}">${msg}</div>`;
    setTimeout(() => { alertContainer.innerHTML = ''; }, 5000);
}

function generateSlug(str) {
    return str.toLowerCase()
        .replace(/[^a-z0-9\s-]/g, '')
        .replace(/\s+/g, '-')
        .replace(/^-+|-+$/g, '');
}

// --- BASIC AUTH CHECK (Self-executing function for immediate check) ---
(function() {
    const token = localStorage.getItem('adminToken');
    if (!token) {
        alert('Please login first to access the category management page.');
        window.location.href = '/admin_panel/admin-login.html';
        // Stop further execution of this script if not authorized
        throw new Error('Unauthorized access detected. Redirecting to login.');
    }
})();