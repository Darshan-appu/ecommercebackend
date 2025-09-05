document.addEventListener("DOMContentLoaded", () => {
    const cartBtn = document.getElementById("cart-icon");
    if (cartBtn) {
        cartBtn.addEventListener("click", () => {
            document.getElementById("cart-modal").style.display = "block";
            loadCartItems();
        });
    }

    const cartDropdownToggle = document.getElementById("cart-icon-modal-trigger");
    if (cartDropdownToggle) {
        cartDropdownToggle.addEventListener("click", (event) => {
            event.preventDefault();
            const cartDropdown = document.getElementById("cart-dropdown");
            if (!$(event.target).closest('#cartModal').length) {
                cartDropdown.classList.toggle("open");
            }
            loadCartItems();
        });
    }

    const cartModal = document.getElementById("cartModal");
    if (cartModal) {
        $(cartModal).on('hidden.bs.modal', function () {
            updateCartCounts();
        });
    }

    // ✅ Call once on load after a short delay
    setTimeout(() => {
        updateCartCounts();
    }, 100);
});

function openCartModal() {
    $('#cartModal').modal('show');
    loadCartItems();
}

function loadCartItems() {
    const token = localStorage.getItem("userToken");
    if (!token) {
        alert("Please login to view cart");
        updateCartCounts();
        return;
    }

    fetch("http://localhost:8080/api/cart", {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    })
        .then((res) => {
            if (!res.ok) {
                if (res.status === 401) {
                    alert("Session expired. Please login again.");
                    localStorage.removeItem("userToken");
                    window.location.href = "login.html";
                }
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(renderCartItems)
        .catch((err) => console.error("Failed to load cart:", err));
}

function renderCartItems(cartItems) {
    const dropdownContainer = document.getElementById("cart-dropdown-list");
    const modalContainer = document.getElementById("modal-cart-items-container");
    dropdownContainer.innerHTML = "";
    modalContainer.innerHTML = "";

    let totalQuantity = 0;
    let subtotal = 0;

    if (!cartItems.length) {
        dropdownContainer.innerHTML = "<p class='text-center p-2'>Your cart is empty.</p>";
        modalContainer.innerHTML = "<p class='text-center p-4'>Your cart is empty.</p>";
    } else {
        cartItems.forEach((item) => {
            const itemPrice = item.product.price * item.quantity;
            totalQuantity += item.quantity;
            subtotal += itemPrice;

            const specs = item.specifications ?
                Object.entries(item.specifications).map(([k, v]) => `<li>${k}: ${v}</li>`).join("") :
                "";

            // Dropdown HTML
            const dropdownHtml = `
                <div class="product-widget">
                    <div class="product-img">
                        <img src="${item.product.imageUrl || 'https://via.placeholder.com/70x70'}" alt="${item.product.name}">
                    </div>
                    <div class="product-body">
                        <h3 class="product-name"><a href="#">${item.product.name}</a></h3>
                        <h4 class="product-price"><span class="qty">${item.quantity}x</span>₹${item.product.price.toFixed(2)}</h4>
                    </div>
                    <button class="cancel-btn" onclick="deleteCartItem(${item.id})"><i class="fa fa-trash"></i></button>
                </div>
            `;
            dropdownContainer.innerHTML += dropdownHtml;

            // Modal HTML
            const modalHtml = `
                <div class="row align-items-center py-2 border-bottom modal-cart-item" data-cart-item-id="${item.id}">
                    <div class="col-md-2">
                        <img src="${item.product.imageUrl || 'https://via.placeholder.com/70x70'}" alt="${item.product.name}" class="img-fluid rounded">
                    </div>
                    <div class="col-md-5">
                        <h5 class="mb-0">${item.product.name}</h5>
                        <small class="text-muted">${item.notes || "No notes"}</small><br>
                        <small class="text-muted"><ul>${specs}</ul></small>
                    </div>
                    <div class="col-md-2">
                        <input type="number" class="form-control quantity-input" value="${item.quantity}" min="1"
                            onchange="updateCartItemQuantity(${item.id}, this.value)">
                    </div>
                    <div class="col-md-2 text-right">
                        ₹${(item.product.price * item.quantity).toFixed(2)}
                    </div>
                    <div class="col-md-1 text-right">
                        <button class="btn btn-danger btn-sm" onclick="deleteCartItem(${item.id})">
                            <i class="fa fa-trash"></i>
                        </button>
                    </div>
                </div>
            `;
            modalContainer.innerHTML += modalHtml;
        });

        modalContainer.innerHTML += `
            <div class="row mt-3">
                <div class="col-12 text-right">
                    <button class="btn btn-warning" onclick="clearCartItems()">
                        <i class="fa fa-trash-o"></i> Clear Cart
                    </button>
                </div>
            </div>
        `;
    }

    updateCartUI(totalQuantity, subtotal);
}

function updateCartCounts() {
    const token = localStorage.getItem("userToken");
    if (!token) {
        updateCartUI(0, 0);
        return;
    }

    fetch("http://localhost:8080/api/cart", {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    })
        .then(res => res.json())
        .then(cartItems => {
            let totalQuantity = 0;
            let subtotal = 0;
            cartItems.forEach(item => {
                totalQuantity += item.quantity;
                subtotal += item.product.price * item.quantity;
            });
            updateCartUI(totalQuantity, subtotal);
        })
        .catch(err => {
            console.error("Failed to update cart counts:", err);
            updateCartUI(0, 0);
        });
}

function updateCartUI(totalQuantity, subtotal) {
    document.getElementById("cart-count").textContent = totalQuantity;
    document.getElementById("cart-item-count").textContent = totalQuantity;
    document.getElementById("cart-subtotal").textContent = subtotal.toFixed(2);
    document.getElementById("modal-subtotal").textContent = `₹${subtotal.toFixed(2)}`;
    const tax = subtotal * 0.10;
    const shipping = 50.00;
    const total = subtotal + tax + shipping;
    document.getElementById("modal-tax").textContent = `₹${tax.toFixed(2)}`;
    document.getElementById("modal-total").textContent = `₹${total.toFixed(2)}`;
}

function deleteCartItem(cartItemId) {
    const token = localStorage.getItem("userToken");
    if (!token) return alert("Please login to remove items from cart");

    if (!confirm("Are you sure you want to remove this item from your cart?")) return;

    fetch(`http://localhost:8080/api/cart/${cartItemId}`, {
        method: 'DELETE',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    })
        .then(res => {
            if (res.ok) {
                alert("Item removed from cart!");
                loadCartItems();
                updateCartCounts();
            } else {
                return res.json().then(err => { throw new Error(err.message || 'Failed to delete item'); });
            }
        })
        .catch(err => console.error("Error deleting item:", err));
}

function clearCartItems() {
    const token = localStorage.getItem("userToken");
    if (!token) return alert("Please login to clear cart");

    if (!confirm("Are you sure you want to clear your entire cart?")) return;

    fetch("http://localhost:8080/api/cart/clear", {
        method: 'DELETE',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    })
        .then(res => {
            if (res.ok) {
                alert("Cart cleared successfully!");
                loadCartItems();
                updateCartCounts();
                $('#cartModal').modal('hide');
            } else {
                return res.json().then(err => { throw new Error(err.message || 'Failed to clear cart'); });
            }
        })
        .catch(err => console.error("Error clearing cart:", err));
}

function updateCartItemQuantity(cartItemId, newQuantity) {
    const token = localStorage.getItem("userToken");
    if (!token) {
        alert("Please login to update cart");
        return;
    }

    newQuantity = parseInt(newQuantity, 10);
    if (isNaN(newQuantity) || newQuantity < 1) {
        alert("Quantity must be at least 1.");
        loadCartItems();
        return;
    }

    fetch(`http://localhost:8080/api/cart`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    })
        .then(res => {
            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
            return res.json();
        })
        .then(cartItems => {
            const existingItem = cartItems.find(item => item.id === cartItemId);
            if (!existingItem) throw new Error("Cart item not found.");

            const transformedSpecifications = existingItem.specifications ?
                Object.entries(existingItem.specifications).map(([key, value]) => ({ key, value })) : [];

            const payload = {
                productId: existingItem.product.id,
                quantity: newQuantity,
                notes: existingItem.notes || null,
                specifications: transformedSpecifications
            };

            return fetch(`http://localhost:8080/api/cart/${cartItemId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(payload)
            });
        })
        .then(res => {
            if (res.ok) {
                loadCartItems();
                updateCartCounts();
            } else {
                return res.json().then(err => {
                    throw new Error(err.message || 'Failed to update item quantity');
                });
            }
        })
        .catch(err => {
            console.error("Error updating item quantity:", err);
            alert("Failed to update quantity. Please try again: " + err.message);
            loadCartItems();
        });
}
