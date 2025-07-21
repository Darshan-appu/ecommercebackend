document.addEventListener('DOMContentLoaded', function() {
    // Base URL for your API
    const API_BASE_URL = 'https://ecommercebackend-i16e.onrender.com/api/products/admin'; // Make sure this matches your Spring Boot app's port

    // Load categories for dropdowns (assuming categories are still managed locally or via another API)
    loadCategories();

    // Load products table from API
    loadProducts();

    // Search functionality
    document.getElementById('productSearch').addEventListener('input', function() {
        loadProducts(this.value);
    });

    // Category filter (if categories are fetched from the backend, this would need adjustment)
    document.getElementById('categoryFilter').addEventListener('change', function() {
        loadProducts('', this.value);
    });

    // Save product button
    document.getElementById('saveProductBtn').addEventListener('click', function() {
        saveProduct();
    });

    // Update product button
    document.getElementById('updateProductBtn').addEventListener('click', function() {
        updateProduct();
    });

    // Event listener for closing modals
    document.querySelectorAll('.close-button').forEach(button => {
        button.addEventListener('click', function() {
            this.closest('.modal').style.display = 'none';
        });
    });

    // Close modals when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target.classList.contains('modal')) {
            event.target.style.display = 'none';
        }
    });
});

// --- Helper Functions ---

function getToken() {
    const token = localStorage.getItem('adminToken');
    if (!token) {
        showAlert('Please login first to access the page.', 'danger');
        window.location.href = '/admin_pannel/admin-login.html';
        return null;
    }
    return token;
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(amount);
}

function showAlert(message, type) {
    const alertContainer = document.getElementById('alertContainer');
    if (!alertContainer) {
        console.error("Alert container not found!");
        return;
    }
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    alertContainer.appendChild(alert);

    // Remove alert after 3 seconds
    setTimeout(() => {
        alert.remove();
    }, 3000);
}

// --- Category Management (Assuming local for now, but easily adaptable to API) ---

function loadCategories() {
    // In a real application, you'd fetch categories from a /api/categories endpoint.
    // For this example, keeping it simple as per your original code.
    const categories = JSON.parse(localStorage.getItem('ecommerceCategories')) || [];
    const categoryDropdown = document.getElementById('productCategory');
    const editCategoryDropdown = document.getElementById('editProductCategory');
    const categoryFilter = document.getElementById('categoryFilter');

    // Clear existing options
    if (categoryDropdown) categoryDropdown.innerHTML = '<option value="">Select Category</option>';
    if (editCategoryDropdown) editCategoryDropdown.innerHTML = '<option value="">Select Category</option>';
    if (categoryFilter) categoryFilter.innerHTML = '<option value="">All Categories</option>';

    // Add categories to dropdowns
    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.name;
        option.textContent = category.name;
        if (categoryDropdown) categoryDropdown.appendChild(option.cloneNode(true));
        if (editCategoryDropdown) editCategoryDropdown.appendChild(option.cloneNode(true));
        if (categoryFilter) categoryFilter.appendChild(option.cloneNode(true));
    });
}

// --- Product Management ---

async function loadProducts(searchTerm = '', category = '') {
    const token = getToken();
    if (!token) return;

    const tbody = document.querySelector('#productsTable tbody');
    if (!tbody) {
        console.error("Products table body not found!");
        return;
    }

    // Clear existing rows
    tbody.innerHTML = '<tr><td colspan="8">Loading products...</td></tr>';

    try {
        // Construct query parameters
        const queryParams = new URLSearchParams();
        if (searchTerm) queryParams.append('search', searchTerm);
        if (category) queryParams.append('category', category); // Assuming your backend can filter by category name

        const url = `${API_BASE_URL}/all?${queryParams.toString()}`;

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 403) {
                showAlert('Access denied. Please ensure you have admin privileges.', 'danger');
                window.location.href = '/admin_pannel/admin-login.html'; // Redirect to login
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const products = await response.json();
        tbody.innerHTML = ''; // Clear loading message

        if (products.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8">No products found.</td></tr>';
            return;
        }

        // Add products to table
        products.forEach(product => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${product.id}</td>
                <td><img src="${product.imageUrl || 'https://via.placeholder.com/50'}" alt="${product.name}" width="50" class="img-thumbnail"></td>
                <td>${product.name}</td>
                <td>${product.category || 'N/A'}</td>
                <td>${formatCurrency(product.price)}</td>
                <td>${product.stock}</td>
                <td>${product.sales}</td>
                <td>
                    <button class="btn btn-primary btn-sm edit-btn" data-id="${product.id}">Edit</button>
                    <button class="btn btn-danger btn-sm delete-btn" data-id="${product.id}">Delete</button>
                    <button class="btn btn-info btn-sm upload-image-btn" data-id="${product.id}">Upload Image</button>
                    <button class="btn btn-warning btn-sm upload-datasheet-btn" data-id="${product.id}">Upload Datasheet</button>
                </td>
            `;
            tbody.appendChild(row);
        });

        // Add event listeners to buttons
        attachProductButtonListeners();

    } catch (error) {
        console.error('Error loading products:', error);
        tbody.innerHTML = '<tr><td colspan="8" class="text-danger">Failed to load products.</td></tr>';
        showAlert('Failed to load products. Please try again.', 'danger');
    }
}

function attachProductButtonListeners() {
    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function() {
            editProduct(this.getAttribute('data-id'));
        });
    });

    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            deleteProduct(this.getAttribute('data-id'));
        });
    });

    document.querySelectorAll('.upload-image-btn').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-id');
            document.getElementById('uploadImageProductId').value = productId;
            document.getElementById('uploadImageModal').style.display = 'flex';
        });
    });

    document.querySelectorAll('.upload-datasheet-btn').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-id');
            document.getElementById('uploadDatasheetProductId').value = productId;
            document.getElementById('uploadDatasheetModal').style.display = 'flex';
        });
    });
}

async function saveProduct() {
    const token = getToken();
    if (!token) return;

    const name = document.getElementById('productName').value;
    const category = document.getElementById('productCategory').value;
    const price = parseFloat(document.getElementById('productPrice').value);
    const stock = parseInt(document.getElementById('productStock').value);
    const description = document.getElementById('productDescription').value;

    if (!name || !category || isNaN(price) || isNaN(stock)) {
        showAlert('Please fill in all required fields (Name, Category, Price, Stock).', 'danger');
        return;
    }

    const newProductData = {
        name,
        category,
        price,
        stock,
        description,
        sales: 0 // Default sales to 0 for a new product
    };

    try {
        const response = await fetch(`${API_BASE_URL}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(newProductData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const createdProduct = await response.json();
        showAlert('Product added successfully', 'success');

        // Optional: If you want to immediately prompt for image/datasheet upload
        // You could open the image/datasheet upload modal here for the createdProduct.id

    } catch (error) {
        console.error('Error saving product:', error);
        showAlert('Failed to add product. Please try again.', 'danger');
    } finally {
        // Close modal and reset form
        document.getElementById('addProductModal').style.display = 'none';
        document.getElementById('addProductForm').reset();
        loadProducts(); // Reload products to show the new one
    }
}

async function editProduct(productId) {
    const token = getToken();
    if (!token) return;

    try {
        const response = await fetch(`${API_BASE_URL}/${productId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const product = await response.json();

        // Fill the edit form with product data
        document.getElementById('editProductId').value = product.id;
        document.getElementById('editProductName').value = product.name;
        document.getElementById('editProductCategory').value = product.category || ''; // Handle null category
        document.getElementById('editProductPrice').value = product.price;
        document.getElementById('editProductStock').value = product.stock;
        document.getElementById('editProductSales').value = product.sales;
        document.getElementById('editProductDescription').value = product.description || '';

        // Show the edit modal
        document.getElementById('editProductModal').style.display = 'flex';

    } catch (error) {
        console.error('Error fetching product for edit:', error);
        showAlert('Failed to load product for editing. Please try again.', 'danger');
    }
}

async function updateProduct() {
    const token = getToken();
    if (!token) return;

    const id = parseInt(document.getElementById('editProductId').value);
    const name = document.getElementById('editProductName').value;
    const category = document.getElementById('editProductCategory').value;
    const price = parseFloat(document.getElementById('editProductPrice').value);
    const stock = parseInt(document.getElementById('editProductStock').value);
    const sales = parseInt(document.getElementById('editProductSales').value);
    const description = document.getElementById('editProductDescription').value;

    if (!name || !category || isNaN(price) || isNaN(stock) || isNaN(sales)) {
        showAlert('Please fill in all required fields.', 'danger');
        return;
    }

    const updatedProductData = {
        name,
        category,
        price,
        stock,
        sales,
        description
    };

    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedProductData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        showAlert('Product updated successfully', 'success');

    } catch (error) {
        console.error('Error updating product:', error);
        showAlert('Failed to update product. Please try again.', 'danger');
    } finally {
        // Close modal
        document.getElementById('editProductModal').style.display = 'none';
        loadProducts(); // Reload products
    }
}

async function deleteProduct(productId) {
    const token = getToken();
    if (!token) return;

    if (!confirm('Are you sure you want to delete this product? This action cannot be undone.')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${productId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        showAlert('Product deleted successfully', 'success');
        loadProducts(); // Reload products after deletion

    } catch (error) {
        console.error('Error deleting product:', error);
        showAlert('Failed to delete product. Please try again.', 'danger');
    }
}

// --- Image and Datasheet Upload Functions ---

async function uploadProductImage() {
    const token = getToken();
    if (!token) return;

    const productId = document.getElementById('uploadImageProductId').value;
    const imageFile = document.getElementById('productImageFile').files[0];

    if (!productId || !imageFile) {
        showAlert('Please select an image file and ensure product ID is valid.', 'danger');
        return;
    }

    const formData = new FormData();
    formData.append('image', imageFile);

    try {
        const response = await fetch(`${API_BASE_URL}/${productId}/image`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
                    // 'Content-Type': 'multipart/form-data' is NOT set here; browser sets it automatically with boundary
            },
            body: formData
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
        }

        showAlert('Product image uploaded successfully!', 'success');
        document.getElementById('uploadImageModal').style.display = 'none';
        document.getElementById('uploadImageForm').reset();
        loadProducts(); // Reload products to show updated image

    } catch (error) {
        console.error('Error uploading product image:', error);
        showAlert(`Failed to upload image: ${error.message}`, 'danger');
    }
}

async function uploadProductDatasheet() {
    const token = getToken();
    if (!token) return;

    const productId = document.getElementById('uploadDatasheetProductId').value;
    const datasheetFile = document.getElementById('productDatasheetFile').files[0];

    if (!productId || !datasheetFile) {
        showAlert('Please select a datasheet file (PDF) and ensure product ID is valid.', 'danger');
        return;
    }

    const formData = new FormData();
    formData.append('file', datasheetFile);

    try {
        const response = await fetch(`${API_BASE_URL}/${productId}/datasheet`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
        }

        showAlert('Product datasheet uploaded successfully!', 'success');
        document.getElementById('uploadDatasheetModal').style.display = 'none';
        document.getElementById('uploadDatasheetForm').reset();
        loadProducts(); // Reload products to show updated datasheet (if you display it)

    } catch (error) {
        console.error('Error uploading product datasheet:', error);
        showAlert(`Failed to upload datasheet: ${error.message}`, 'danger');
    }
}


// --- Admin JWT Authentication Check ---
// This part remains largely the same, but the getToken() function now centralizes the logic.
// The check happens implicitly at the start of functions that require authentication.
// Initial check on page load:
(function() {
    const token = localStorage.getItem('adminToken');
    if (!token) {
        alert('Please login first to access the page.');
        window.location.href = '/admin_pannel/admin-login.html';
    }
})();