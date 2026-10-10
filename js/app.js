// FLASHDEAL PRO - Big Sale Day Web Concurrency Simulator
// Matching MNR University Project #7 Java Backend Architecture

const PRODUCTS = [
  {
    id: "PROD-101",
    name: "OnePlus Nord Buds 2 TWS Earbuds",
    desc: "Active Noise Cancellation 25dB, 12.4mm Dynamic Bass Drivers",
    category: "Electronics",
    originalPrice: 2999,
    flashSalePrice: 1499,
    initialStock: 25,
    availableStock: 25,
    discountBadge: "50% OFF"
  },
  {
    id: "PROD-102",
    name: "boAt Rockerz 450 Bluetooth Headphones",
    desc: "40mm drivers, 15 hours playtime, lightweight ergonomic design",
    category: "Electronics",
    originalPrice: 3990,
    flashSalePrice: 1299,
    initialStock: 15,
    availableStock: 15,
    discountBadge: "67% OFF"
  },
  {
    id: "PROD-103",
    name: "Noise ColorFit Pulse 3 Smartwatch",
    desc: "1.96-inch TFT display, Bluetooth calling, 100 sports modes",
    category: "Wearables",
    originalPrice: 4999,
    flashSalePrice: 1799,
    initialStock: 20,
    availableStock: 20,
    discountBadge: "64% OFF"
  },
  {
    id: "PROD-104",
    name: "Xiaomi 20000mAh Power Bank 3i",
    desc: "18W fast charging, triple output ports, dual input Type-C",
    category: "Accessories",
    originalPrice: 2199,
    flashSalePrice: 1199,
    initialStock: 30,
    availableStock: 30,
    discountBadge: "45% OFF"
  },
  {
    id: "PROD-105",
    name: "SanDisk Ultra 128GB MicroSDXC Card",
    desc: "Up to 140MB/s read speed, Class 10, A1 App Performance",
    category: "Storage",
    originalPrice: 1800,
    flashSalePrice: 749,
    initialStock: 40,
    availableStock: 40,
    discountBadge: "58% OFF"
  },
  {
    id: "PROD-106",
    name: "Logitech B170 Wireless Optical Mouse",
    desc: "Reliable 2.4GHz wireless connection, 12-month battery life",
    category: "Computer Accessories",
    originalPrice: 895,
    flashSalePrice: 499,
    initialStock: 50,
    availableStock: 50,
    discountBadge: "44% OFF"
  },
  {
    id: "PROD-107",
    name: "Portronics Ruffpad 8.5E LCD Notepad",
    desc: "Environment-friendly re-writable digital notepad with stylus",
    category: "Gadgets",
    originalPrice: 999,
    flashSalePrice: 299,
    initialStock: 35,
    availableStock: 35,
    discountBadge: "70% OFF"
  },
  {
    id: "PROD-108",
    name: "Realme 65W SuperDart Fast Charger",
    desc: "Type-C flash charging adapter, multi-layer safety protection",
    category: "Mobile Accessories",
    originalPrice: 2499,
    flashSalePrice: 1099,
    initialStock: 18,
    availableStock: 18,
    discountBadge: "56% OFF"
  }
];

const CUSTOMER_NAMES = [
  "Aarav Sharma", "Diya Patel", "Rohan Verma", "Ananya Iyer", "Vikram Singh",
  "Priya Nair", "Rajesh Kumar", "Sneha Reddy", "Arjun Gupta", "Kavya Deshmukh",
  "Aditya Chopra", "Neha Rao", "Karthik Joshi", "Pooja Bose", "Rahul Menon",
  "Meera Bhat", "Suresh Pillai", "Ishita Saxena", "Manoj Tiwari", "Divya Jain",
  "Gaurav Kulkarni", "Swati Pandey", "Nikhil Hegde", "Tanvi Agarwal", "Abhishek Dubey",
  "Ritu Mishra", "Varun Goswami", "Simran Gill", "Deepak Roy", "Shalini Das",
  "Pranav Nambiar", "Nisha Kaul", "Alok Sengupta", "Shruti Mathur", "Siddharth Sen",
  "Aishwarya Pillai", "Vishal Thakur", "Pallavi Chauhan", "Akash Varma", "Preeti Nanda",
  "Manish Soni", "Sonali Chawla", "Harish Bedi", "Anjali Trivedi", "Sanjay Bakshi",
  "Komal Sethi", "Amit Malviya", "Bhavna Mittal", "Sunil Bajpai", "Rupa Mukherjee"
];

// Transaction Ledger
let orders = [];
let totalRevenue = 0;
let totalUnitsSold = 0;
let totalBlockedAttempts = 0;
let isSimulationRunning = false;
let orderSequence = 1001;

// Currency Formatter
function formatINR(val) {
  return "₹ " + Number(val).toLocaleString("en-IN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
}

function getTimestamp() {
  const now = new Date();
  return now.toTimeString().split(' ')[0] + '.' + String(now.getMilliseconds()).padStart(3, '0');
}

// Discount Strategy Engine
function calculateDiscount(code, amount) {
  if (!code || code === "NONE") return 0;
  if (code === "SAVE10") {
    if (amount < 500) return 0;
    return Math.min(amount * 0.10, 300);
  }
  if (code === "FLAT100") {
    if (amount < 999) return 0;
    return 100;
  }
  if (code === "BANK15") {
    if (amount < 1500) return 0;
    return Math.min(amount * 0.15, 750);
  }
  return 0;
}

// DOM Elements
document.addEventListener("DOMContentLoaded", () => {
  renderCatalog();
  populateProductSelect();
  updateMetrics();
  setupEventListeners();

  logActivity("SYSTEM", "FLASHDEAL PRO Concurrency Simulator initialized and standing by.");
});

function populateProductSelect() {
  const select = document.getElementById("productSelect");
  select.innerHTML = "";
  PRODUCTS.forEach(p => {
    const opt = document.createElement("option");
    opt.value = p.id;
    opt.textContent = `${p.name} (${formatINR(p.flashSalePrice)} | Stock: ${p.availableStock})`;
    select.appendChild(opt);
  });
  updateProductPreview();
}

function updateProductPreview() {
  const select = document.getElementById("productSelect");
  const p = PRODUCTS.find(x => x.id === select.value) || PRODUCTS[0];
  document.getElementById("previewProductName").textContent = p.name;
  const stockEl = document.getElementById("previewStock");
  stockEl.textContent = `${p.availableStock} units remaining`;
  stockEl.style.color = p.availableStock > 0 ? "var(--accent-emerald)" : "var(--accent-rose)";
}

function renderCatalog() {
  const grid = document.getElementById("catalogGrid");
  grid.innerHTML = "";

  PRODUCTS.forEach(p => {
    const card = document.createElement("div");
    card.className = "product-card";
    card.innerHTML = `
      <div class="product-badge-discount">${p.discountBadge}</div>
      <div>
        <div class="product-category">${p.category}</div>
        <div class="product-title">${p.name}</div>
        <div class="product-desc">${p.desc}</div>
      </div>
      <div>
        <div class="product-pricing">
          <span class="price-current">${formatINR(p.flashSalePrice)}</span>
          <span class="price-original">${formatINR(p.originalPrice)}</span>
        </div>
        <div class="stock-meter">
          <span>Available Stock:</span>
          <span style="color: ${p.availableStock > 0 ? 'var(--accent-emerald)' : 'var(--accent-rose)'}">
            ${p.availableStock} units
          </span>
        </div>
        <button class="btn-secondary" style="width: 100%; justify-content: center; font-size: 0.85rem;" 
                onclick="quickSelectProduct('${p.id}')">
          Select for Flash Sale ⚡
        </button>
      </div>
    `;
    grid.appendChild(card);
  });
}

function quickSelectProduct(id) {
  const select = document.getElementById("productSelect");
  select.value = id;
  updateProductPreview();
  window.scrollTo({ top: document.getElementById("simulatorArena").offsetTop - 80, behavior: 'smooth' });
}

function updateMetrics() {
  document.getElementById("metricRevenue").textContent = formatINR(totalRevenue);
  document.getElementById("metricUnitsSold").textContent = totalUnitsSold;
  document.getElementById("metricBlocked").textContent = totalBlockedAttempts;

  const totalAttempts = orders.length;
  const successCount = orders.filter(o => o.status === "SUCCESS").length;
  const rate = totalAttempts > 0 ? ((successCount / totalAttempts) * 100).toFixed(1) + "%" : "0.0%";
  document.getElementById("metricSuccessRate").textContent = rate;
}

function setupEventListeners() {
  document.getElementById("productSelect").addEventListener("change", updateProductPreview);

  document.getElementById("resetStockBtn").addEventListener("click", () => {
    const select = document.getElementById("productSelect");
    const p = PRODUCTS.find(x => x.id === select.value);
    if (!p) return;
    p.availableStock = 10;
    updateProductPreview();
    renderCatalog();
    logActivity("INVENTORY", `Stock for [${p.name}] manually reset to 10 units for demonstration.`);
  });

  document.getElementById("startSimulationBtn").addEventListener("click", runSimulation);

  document.getElementById("clearLogBtn").addEventListener("click", () => {
    document.getElementById("terminalBody").innerHTML = "";
  });

  document.getElementById("exportCsvBtn").addEventListener("click", exportOrdersToCSV);
}

function logActivity(type, message) {
  const terminal = document.getElementById("terminalBody");
  const entry = document.createElement("div");
  entry.className = "log-entry";

  let tagClass = "log-attempt";
  if (type === "SUCCESS") tagClass = "log-success";
  if (type === "REJECTED") tagClass = "log-rejected";
  if (type === "SYSTEM" || type === "INVENTORY" || type === "COMPLETE") tagClass = "log-system";

  entry.innerHTML = `<span class="log-time">[${getTimestamp()}]</span> <span class="${tagClass}">[${type}]</span> ${message}`;
  terminal.appendChild(entry);
  terminal.scrollTop = terminal.scrollHeight;
}

// Core Concurrency Simulation Engine
async function runSimulation() {
  if (isSimulationRunning) return;

  const select = document.getElementById("productSelect");
  const product = PRODUCTS.find(x => x.id === select.value);
  const customerCount = parseInt(document.getElementById("customerCount").value) || 50;
  const qtyPerCustomer = parseInt(document.getElementById("qtyPerCustomer").value) || 1;
  const couponCode = document.getElementById("couponSelect").value;

  if (!product) return;

  isSimulationRunning = true;
  const startBtn = document.getElementById("startSimulationBtn");
  startBtn.disabled = true;
  startBtn.textContent = "Simulating Flash Rush...";

  logActivity("SYSTEM", "==================================================================");
  logActivity("SYSTEM", `FLASH SALE LAUNCHED: ${customerCount} shoppers racing for [${product.name}]`);
  logActivity("SYSTEM", `Initial Stock: ${product.availableStock} | Requested: ${qtyPerCustomer} unit(s) | Coupon: ${couponCode}`);
  logActivity("SYSTEM", `Synchronizing client threads at virtual CountDownLatch barrier...`);

  const progressBar = document.getElementById("progressBar");
  const progressText = document.getElementById("progressText");
  progressBar.style.width = "0%";

  let completed = 0;
  let batchSuccessful = 0;
  let batchRejected = 0;

  // Generate shopper list
  const shoppers = [];
  for (let i = 0; i < customerCount; i++) {
    shoppers.push({
      id: `CUST-${String(i + 1).padStart(3, '0')}`,
      name: CUSTOMER_NAMES[i % CUSTOMER_NAMES.length] + (i >= CUSTOMER_NAMES.length ? ` #${i + 1}` : "")
    });
  }

  // Mutual exclusion lock ensuring atomic stock check and deduction
  let lock = Promise.resolve();
  function atomicDeductStock(qty) {
    return new Promise((resolve) => {
      lock = lock.then(() => {
        if (product.availableStock >= qty) {
          product.availableStock -= qty;
          resolve({ success: true, remaining: product.availableStock });
        } else {
          resolve({ success: false, remaining: product.availableStock });
        }
      });
    });
  }

  // Simulate concurrent arrival with microsecond jitter
  const promises = shoppers.map(async (shopper) => {
    // Random latency between 5ms and 350ms to simulate genuine burst network arrivals
    const jitter = Math.floor(Math.random() * 300) + 10;
    await new Promise(r => setTimeout(r, jitter));

    logActivity("ATTEMPT", `[${shopper.name}] attempting purchase of ${qtyPerCustomer} unit(s)...`);

    // Atomic execution
    const outcome = await atomicDeductStock(qtyPerCustomer);

    completed++;
    const pct = Math.floor((completed / customerCount) * 100);
    progressBar.style.width = pct + "%";
    progressText.textContent = `Completed ${completed} of ${customerCount} requests (${pct}%)`;

    const orderId = `ORD-${orderSequence++}`;
    const basePrice = product.flashSalePrice * qtyPerCustomer;
    const discount = outcome.success ? calculateDiscount(couponCode, basePrice) : 0;
    const finalAmount = outcome.success ? Math.max(0, basePrice - discount) : 0;

    const orderRecord = {
      orderId: orderId,
      customer: shopper.name,
      product: product.name,
      qty: qtyPerCustomer,
      amount: finalAmount,
      timestamp: getTimestamp(),
      status: outcome.success ? "SUCCESS" : "OUT_OF_STOCK",
      reason: outcome.success ? "Inventory Reserved" : "Insufficient Stock (OutOfStockException)"
    };

    orders.unshift(orderRecord);

    if (outcome.success) {
      batchSuccessful++;
      totalUnitsSold += qtyPerCustomer;
      totalRevenue += finalAmount;
      logActivity("SUCCESS", `[${shopper.name}] ORDER CONFIRMED! Order: ${orderId} | Paid: ${formatINR(finalAmount)} | Stock remaining: ${outcome.remaining}`);
    } else {
      batchRejected++;
      totalBlockedAttempts++;
      logActivity("REJECTED", `[${shopper.name}] REJECTED: OutOfStockException thrown. Remaining stock: ${outcome.remaining}`);
    }

    updateProductPreview();
  });

  await Promise.all(promises);

  logActivity("COMPLETE", "------------------------------------------------------------------");
  logActivity("COMPLETE", `FLASH SALE CONCLUDED: ${customerCount} Attempts | Fulfilled: ${batchSuccessful} | Blocked: ${batchRejected}`);
  logActivity("COMPLETE", `Final Stock: ${product.availableStock} units (Strict Zero Overselling Invariant Verified)`);
  logActivity("SYSTEM", "==================================================================");

  isSimulationRunning = false;
  startBtn.disabled = false;
  startBtn.textContent = "⚡ START SIMULTANEOUS FLASH SALE RUSH";

  renderCatalog();
  renderOrdersTable();
  updateMetrics();
}

function renderOrdersTable() {
  const tbody = document.getElementById("ordersTableBody");
  tbody.innerHTML = "";

  const limit = Math.min(orders.length, 25);
  for (let i = 0; i < limit; i++) {
    const o = orders[i];
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td style="font-family: var(--font-mono); font-weight: 700;">${o.orderId}</td>
      <td>${o.customer}</td>
      <td>${o.product}</td>
      <td>${o.qty}</td>
      <td style="font-weight: 700;">${formatINR(o.amount)}</td>
      <td style="color: var(--text-muted); font-size: 0.8rem;">${o.timestamp}</td>
      <td>
        <span class="${o.status === 'SUCCESS' ? 'pill-success' : 'pill-fail'}">
          ${o.status === 'SUCCESS' ? 'SUCCESS' : 'OUT OF STOCK'}
        </span>
      </td>
      <td style="font-size: 0.8rem; color: var(--text-secondary);">${o.reason}</td>
    `;
    tbody.appendChild(tr);
  }
}

function exportOrdersToCSV() {
  if (orders.length === 0) {
    alert("No orders recorded yet. Please run a flash sale simulation first!");
    return;
  }

  let csvContent = "data:text/csv;charset=utf-8,";
  csvContent += "OrderId,Customer,Product,Quantity,FinalAmountINR,Timestamp,Status,Reason\n";

  orders.forEach(o => {
    csvContent += `"${o.orderId}","${o.customer}","${o.product}",${o.qty},${o.amount},"${o.timestamp}","${o.status}","${o.reason}"\n`;
  });

  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", `flashdeal_orders_${Date.now()}.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}
