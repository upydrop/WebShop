<template>
  <div class="app">
    <header class="title">TeaShop🍃</header>
    
    <section class="auth-row">
      <div class="panel panel--compactL">
        <h2>Login</h2>
        <input
          v-model="loginCode"
          class="input"
          placeholder="Authorization token"
        />
        <p class="hint">Введите код перед оформлением заказа.</p>
      </div>

      <div class="panel panel--compactR">
        <h2>Register</h2>
        <div class="form">
          <input v-model="registerForm.name" class="input" placeholder="Name" />
          <input
            v-model="registerForm.email"
            class="input"
            type="email"
            placeholder="E-Mail"
          />
          <div class="form__actions">
            <button class="button" @click="register">Register</button>
            <span class="status">{{ registerStatus }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="panel">
      <h2>Products</h2>
      <div class="products">
        <div class="products__head">
          <span>Product</span>
          <span>Description</span>
          <span class="products__price">Price / g</span>
          <span class="products__qty">Quantity</span>
          <span></span>
        </div>
        <div v-for="product in products" :key="product.name" class="product">
          <span class="product__name">{{ product.name }}</span>
          <span class="product__desc">{{ product.description }}</span>
          <span class="product__price">{{ formatPrice(product.cost) }}</span>
          <div class="stepper">
            <button class="stepper__btn" @click="stepQuantity(product.name, -0.1)">-</button>
            <input
              v-model.number="quantities[product.name]"
              class="input input--qty"
              type="number"
              min="0"
              step="0.1"
            />
            <button class="stepper__btn" @click="stepQuantity(product.name, 0.1)">+</button>
          </div>
          <button class="button" @click="addToCart(product.name)">Add</button>
        </div>
        <div v-if="!products.length" class="empty">Нет товаров</div>
      </div>
    </section>

    <section class="panel">
      <h2>Cart</h2>
      <div class="cart">
        <div class="cart__head">
          <span>Item</span>
          <span class="cart__qty">Quantity</span>
          <span class="cart__price">Price</span>
          <span></span>
        </div>
        <div v-for="row in cartRows" :key="row.name" class="cart__row">
          <span class="cart__name">{{ row.name }}</span>
          <div class="stepper">
            <button class="stepper__btn" @click="stepCart(row.name, -0.1)">-</button>
            <input
              v-model.number="cart[row.name]"
              class="input input--qty"
              type="number"
              min="0"
              step="0.1"
            />
            <button class="stepper__btn" @click="stepCart(row.name, 0.1)">+</button>
          </div>
          
          <span class="cart__price">{{ formatPrice(row.total) }}</span>
          <button class="button button--ghost" @click="removeItem(row.name)">Remove</button>
        </div>
        <div v-if="!cartRows.length" class="empty">Корзина пуста</div>
      </div>
      <div class="checkout">
        <div class="summary">Total: {{ formatPrice(cartSum) }}</div>
        <button class="button button--wide" @click="checkout">Checkout</button>
        <p class="hint">{{ checkoutStatus }}</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";

const apiBase = ref("http://localhost:4567");
const products = ref([]);
const cart = ref({});
const quantities = ref({});
const authToken = ref("");
const loginCode = ref("");
const registerForm = ref({
  name: "",
  email: "",
});
const checkoutStatus = ref("");
const registerStatus = ref("");

const cartRows = computed(() =>
  Object.entries(cart.value).map(([name, quantity]) => {
    const product = products.value.find((item) => item.name === name);
    const price = Number(product?.cost || 0);
    return {
      name,
      quantity,
      total: price * quantity,
    };
  })
);

const cartSum = computed(() =>
  products.value.reduce((sum, product) => {
    const quantity = cart.value[product.name] || 0;
    return sum + quantity * Number(product.cost || 0);
  }, 0)
);

const loadProducts = async () => {
  try {
    const response = await fetch(`${apiBase.value}/products`);
    const payload = await response.json();
    products.value = payload?.data || [];
  } catch (error) {
    console.error(error);
  }
};

const clampQuantity = (value) => {
  const number = Number(value);
  if (Number.isNaN(number)) {
    return 0;
  }
  return Number(number.toFixed(1));
};

const stepQuantity = (name, delta) => {
  const next = clampQuantity((quantities.value[name] || 0) + delta);
  if (next >= 0){
    quantities.value = {
      ...quantities.value,
      [name]: next,
    };
  }
};

const stepCart = (name, delta) => {
  const next = clampQuantity((cart.value[name] || 0) + delta);
  if (next === 0) {
    removeItem(name);
    return;
  }
  cart.value = {
    ...cart.value,
    [name]: next,
  };
};

const addToCart = (name) => {
  const qty = clampQuantity(quantities.value[name] || 0.1);
  if (!qty) {
    return;
  }
  cart.value = {
    ...cart.value,
    [name]: clampQuantity((cart.value[name] || 0) + qty),
  };
  quantities.value = {
    ...quantities.value,
    [name]: qty,
  };
};

const removeItem = (name) => {
  const next = { ...cart.value };
  delete next[name];
  cart.value = next;
};

const formatPrice = (value) => {
  const number = Number(value || 0);
  return new Intl.NumberFormat("ru-RU", {
    style: "currency",
    currency: "EUR",
    maximumFractionDigits: 2,
  }).format(number);
};

const register = async () => {
  registerStatus.value = "";
  if (!registerForm.value.name || !registerForm.value.email) {
    registerStatus.value = "Введите имя и e-mail.";
    return;
  }
  try {
    const response = await fetch(`${apiBase.value}/register`, {
      method: "POST",
      headers: {
        name: registerForm.value.name,
        eMail: registerForm.value.email,
      },
    });
    const raw = await response.text();
    let payload = raw;
    let statusMessage = payload;
    try {
      payload = JSON.parse(raw);
      statusMessage = payload;
    } catch (error) {
      console.error(error);
    }
    if (typeof raw === "string" && raw.trim().startsWith("<")) {
      statusMessage = "Ошибка сервера при регистрации.";
    }
    if (!response.ok || statusMessage === "EMail already in use!") {
      registerStatus.value = statusMessage;
      return;
    }
    authToken.value = statusMessage;
    loginCode.value = statusMessage;
    registerStatus.value = statusMessage;
  } catch (error) {
    console.error(error);
  }
};

const checkout = async () => {
  checkoutStatus.value = "";
  if (!loginCode.value) {
    checkoutStatus.value = "Введите код входа перед оплатой.";
    return;
  }
  if (!cartRows.value.length) {
    checkoutStatus.value = "Корзина пуста.";
    return;
  }

  const cartPayload = {
    products: {},
    cartSum: Number(cartSum.value.toFixed(2)),
  };

  Object.entries(cart.value).forEach(([name, quantity]) => {
    cartPayload.products[name] = [Number(quantity) || 0];
  });

  try {
    const response = await fetch(`${apiBase.value}/checkout`, {
      method: "PUT",
      headers: {
        auth_token: loginCode.value,
        cart: JSON.stringify(cartPayload),
      },
    });
    const payload = await response.json();
    checkoutStatus.value = payload;
    if (payload === "Success!") {
      cart.value = {};
    }
  } catch (error) {
    console.error(error);
    checkoutStatus.value = "Ошибка оформления заказа.";
  }
};

onMounted(() => {
  loadProducts();
});
</script>
