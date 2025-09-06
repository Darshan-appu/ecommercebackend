// js/orders.js

const BASE_URL = 'https://ecommercebackend-4zll.onrender.com'; // Your backend base URL
const ordersTableBody = document.querySelector('#ordersTable tbody');
const statusFilter = document.getElementById('statusFilter');
const alertContainer = document.getElementById('alertContainer');

// Modals
const orderDetailsModal = document.getElementById('orderDetailsModal');
const orderDetailsContent = document.getElementById('orderDetailsContent');
const updateStatusModal = document.getElementById('updateStatusModal');
const currentOrderIdInput = document.getElementById('currentOrderId');
const orderStatusSelect = document.getElementById('orderStatus');
const confirmStatusUpdateBtn = document.getElementById('confirmStatusUpdateBtn');

let allOrders = []; // To store all fetched orders for filtering

// Utility function to show alerts
function showAlert(message, type = 'danger') {
    alertContainer.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
    setTimeout(() => {
        alertContainer.innerHTML = '';
    }, 5000);
}

// Function to fetch and display orders
async function fetchAndRenderOrders() {
    const token = localStorage.getItem('adminToken'); // Assuming admin token is stored here
    if (!token) {
        showAlert('Admin not authenticated. Please log in.', 'warning');
        window.location.href = 'login.html'; // Redirect to admin login
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/api/orders/admin/orders`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                showAlert('Authentication failed. Please log in again.', 'danger');
                localStorage.removeItem('adminToken');
                window.location.href = 'login.html';
                return;
            }
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        allOrders = await response.json();
        renderOrders(allOrders); // Render all orders initially

    } catch (error) {
        console.error('Error fetching orders:', error);
        showAlert(`Failed to load orders: ${error.message}`);
    }
}

// Function to render orders into the table
function renderOrders(ordersToRender) {
    ordersTableBody.innerHTML = ''; // Clear existing rows

    if (ordersToRender.length === 0) {
        ordersTableBody.innerHTML = '<tr><td colspan="7" class="text-center">No orders found.</td></tr>';
        return;
    }

    ordersToRender.forEach(order => {
        const row = ordersTableBody.insertRow();
        const orderDate = new Date(order.orderDate).toLocaleString();
        const customerName = (order.firstName && order.lastName) ? `${order.firstName} ${order.lastName}` : (order.email || 'N/A');

        row.innerHTML = `
            <td>${order.id}</td>
            <td>${customerName}</td>
            <td>${orderDate}</td>
            <td>₹${order.totalAmount.toFixed(2)}</td>
            <td><span class="status-badge status-${order.status.toLowerCase()}">${order.status}</span></td>
            <td>
                <button class="btn btn-sm btn-info view-details-btn" data-order-id="${order.id}">View Details</button>
                <button class="btn btn-sm btn-primary update-status-btn" data-order-id="${order.id}" data-current-status="${order.status}">Update Status</button>
                <button class="btn btn-sm btn-danger delete-order-btn" data-order-id="${order.id}">Delete</button>
            </td>
        `;
    });

    addEventListenersToButtons();
}

// Function to add event listeners to dynamically created buttons
function addEventListenersToButtons() {
    document.querySelectorAll('.view-details-btn').forEach(button => {
        button.onclick = (e) => {
            const orderId = parseInt(e.target.dataset.orderId);
            showOrderDetails(orderId);
        };
    });

    document.querySelectorAll('.update-status-btn').forEach(button => {
        button.onclick = (e) => {
            const orderId = parseInt(e.target.dataset.orderId);
            const currentStatus = e.target.dataset.currentStatus;
            openUpdateStatusModal(orderId, currentStatus);
        };
    });

    document.querySelectorAll('.delete-order-btn').forEach(button => {
        button.onclick = (e) => {
            const orderId = parseInt(e.target.dataset.orderId);
            if (confirm(`Are you sure you want to delete order ID ${orderId}? This cannot be undone.`)) {
                deleteOrder(orderId);
            }
        };
    });

    // Close buttons for modals
    document.querySelectorAll('.modal .close').forEach(closeBtn => {
        closeBtn.onclick = () => {
            orderDetailsModal.style.display = 'none';
            updateStatusModal.style.display = 'none';
        };
    });

    // Close modal when clicking outside
    window.onclick = (event) => {
        if (event.target == orderDetailsModal) {
            orderDetailsModal.style.display = 'none';
        }
        if (event.target == updateStatusModal) {
            updateStatusModal.style.display = 'none';
        }
    };
}


// Function to show order details in modal
function showOrderDetails(orderId) {
    const order = allOrders.find(o => o.id === orderId);
    if (!order) {
        showAlert('Order details not found.', 'warning');
        return;
    }

    const orderDate = new Date(order.orderDate).toLocaleString();
    const customerInfo = (order.firstName || order.lastName || order.email || order.phone) ? `
        <p><strong>Customer Name:</strong> ${order.firstName || 'N/A'} ${order.lastName || ''}</p>
        <p><strong>Email:</strong> ${order.email || 'N/A'}</p>
        <p><strong>Phone:</strong> ${order.phone || 'N/A'}</p>
        <p><strong>City:</strong> ${order.city || 'N/A'}</p>
        <p><strong>Country:</strong> ${order.country || 'N/A'}</p>
        <p><strong>Zip Code:</strong> ${order.zipCode || 'N/A'}</p>
    ` : '<p><strong>Customer:</strong> N/A</p>';

    let orderItemsHtml = '<h5>Order Items:</h5><ul>';
    order.orderItems.forEach(item => {
                let specs = '';
                try {
                    // Your backend sends specifications as a JSON string, so parse it
                    const parsedSpecs = item.specifications ? JSON.parse(item.specifications) : {};
                    specs = Object.entries(parsedSpecs).map(([key, value]) => `${key}: ${value}`).join(', ');
                } catch (e) {
                    specs = item.specifications || ''; // Fallback if not valid JSON
                }
                orderItemsHtml += `
            <li>
                <strong>${item.productName}</strong> (x${item.quantity}) - ₹${item.price ? item.price.toFixed(2) : 'N/A'}
                ${specs ? `<br><small>Specs: ${specs}</small>` : ''}
                ${item.notes ? `<br><small>Notes: ${item.notes}</small>` : ''}
            </li>
        `;
    });
    orderItemsHtml += '</ul>';

    orderDetailsContent.innerHTML = `
        <p><strong>Order ID:</strong> ${order.id}</p>
        <p><strong>Status:</strong> <span class="status-badge status-${order.status.toLowerCase()}">${order.status}</span></p>
        <p><strong>Total Amount:</strong> ₹${order.totalAmount.toFixed(2)}</p>
        <p><strong>Order Date:</strong> ${orderDate}</p>
        ${customerInfo}
        <p><strong>Billing Address:</strong> ${order.billingAddress || 'N/A'}</p>
        <p><strong>Shipping Address:</strong> ${order.shippingAddress || 'N/A'}</p>
        <p><strong>Payment Method:</strong> ${order.paymentMethod ? order.paymentMethod.replace(/_/g, ' ').toUpperCase() : 'N/A'}</p>
        <p><strong>Notes:</strong> ${order.notes || 'No notes'}</p>
        ${orderItemsHtml}
    `;
    orderDetailsModal.style.display = 'block';

    // Set the current order ID for potential status update from details modal
    document.getElementById('updateOrderStatusBtn').onclick = () => openUpdateStatusModal(orderId, order.status);
}

// Function to open the update status modal
function openUpdateStatusModal(orderId, currentStatus) {
    currentOrderIdInput.value = orderId;
    orderStatusSelect.value = currentStatus.toUpperCase(); // Set current status, ensure it matches option values
    updateStatusModal.style.display = 'block';
}

// Event listener for confirming status update
confirmStatusUpdateBtn.onclick = async () => {
    const orderId = currentOrderIdInput.value;
    const newStatus = orderStatusSelect.value.toUpperCase(); // Ensure status is uppercase for backend

    if (!orderId || !newStatus) {
        showAlert('Invalid order ID or status selected.', 'danger');
        return;
    }

    const token = localStorage.getItem('adminToken');
    if (!token) {
        showAlert('Admin not authenticated. Please log in.', 'warning');
        window.location.href = 'login.html';
        return;
    }

    try {
        // Updated to use @RequestParam for status (query parameter)
        const response = await fetch(`${BASE_URL}/api/orders/admin/${orderId}/status?status=${newStatus}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`
                // No 'Content-Type': 'application/json' needed for query params
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to update status: ${response.status} - ${errorText}`);
        }

        showAlert('Order status updated successfully!', 'success');
        updateStatusModal.style.display = 'none';
        fetchAndRenderOrders(); // Refresh the table

    } catch (error) {
        console.error('Error updating order status:', error);
        showAlert(`Failed to update status: ${error.message}`);
    }
};

// Function to delete an order
async function deleteOrder(orderId) {
    const token = localStorage.getItem('adminToken');
    if (!token) {
        showAlert('Admin not authenticated. Please log in.', 'warning');
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/api/orders/admin/${orderId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to delete order: ${response.status} - ${errorText}`);
        }

        showAlert(`Order ID ${orderId} deleted successfully!`, 'success');
        fetchAndRenderOrders(); // Refresh the table

    } catch (error) {
        console.error('Error deleting order:', error);
        showAlert(`Failed to delete order: ${error.message}`);
    }
}

// Event listener for status filter dropdown
statusFilter.onchange = (e) => {
    const selectedStatus = e.target.value;
    let filteredOrders = [];
    if (selectedStatus === '') {
        filteredOrders = allOrders; // Show all orders
    } else {
        filteredOrders = allOrders.filter(order => order.status.toLowerCase() === selectedStatus.toLowerCase());
    }
    renderOrders(filteredOrders);
};

// Initial fetch when the page loads
document.addEventListener('DOMContentLoaded', fetchAndRenderOrders);

// --- Basic Modal Close Logic (re-added here for completeness if not in main.js) ---
document.querySelectorAll('.modal .close').forEach(closeButton => {
    closeButton.addEventListener('click', function() {
        this.closest('.modal').style.display = 'none';
    });
});

window.addEventListener('click', function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
    }
});