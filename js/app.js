// FLASHDEAL PRO - Ultra-Modern Enterprise Web Simulator & Concurrency Visualizer
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
    category: "Accessories",
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
    category: "Electronics",
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
    category: "Accessories",
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

const TEST_SCENARIOS = [
  { id: "T1", title: "Successful purchase atomic deduction (10 to 8)", group: "Concurrency & Inventory" },
  { id: "T2", title: "Purchase with insufficient stock (OutOfStockException)", group: "Exception Handling" },
  { id: "T3", title: "Inventory never becoming negative invariant verification", group: "Data Integrity" },
  { id: "T4", title: "Concurrent 50-thread race condition stress test", group: "Multithreading & Barrier" },
  { id: "T5", title: "Invalid quantity handling (Zero and Negative)", group: "Defensive Validation" },
  { id: "T6", title: "Percentage discount calculation and capping (SAVE10)", group: "Discount Strategy" },
  { id: "T7", title: "Flat discount calculation and threshold (FLAT100)", group: "Discount Strategy" },
  { id: "T8", title: "Bank offer discount calculation and limits (BANK15)", group: "Discount Strategy" },
  { id: "T9", title: "Invalid coupon handling and fallback to ₹0.00", group: "Discount Strategy" },
  { id: "T10", title: "Successful order recording and persistence", group: "Persistence & Audit" },
  { id: "T11", title: "Failed order audit recording (OUT_OF_STOCK)", group: "Persistence & Audit" },
  { id: "T12", title: "Revenue calculation consistency check", group: "Financial Accounting" },
  { id: "T13", title: "CSV file persistence RFC-4180 integrity", group: "File Storage" },
  { id: "T14", title: "Reloading inventory after simulated application restart", group: "Persistence" },
  { id: "T15", title: "CSV report export generation & summary audit", group: "Reporting" }
];

// App State
let orders = [];
let totalRevenue = 0;
let totalUnitsSold = 0;
let totalBlockedAttempts = 0;
let isSimulationRunning = false;
let orderSequence = 1001;
let soundEnabled = true;

// Web Audio API Sound Synthesizer
let audioCtx = null;

function getAudioContext() {
  if (!audioCtx) {
    const AudioContext = window.AudioContext || window.webkitAudioContext;
    if (AudioContext) audioCtx = new AudioContext();
  }
  if (audioCtx && audioCtx.state === 'suspended') {
    audioCtx.resume();
  }
  return audioCtx;
}

function playTone(freq, type, duration, delay = 0) {
  if (!soundEnabled) return;
  try {
    const ctx = getAudioContext();
    if (!ctx) return;
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();
    osc.type = type;
    osc.frequency.setValueAtTime(freq, ctx.currentTime + delay);
    gain.gain.setValueAtTime(0.08, ctx.currentTime + delay);
    gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + delay + duration);
    osc.connect(gain);
    gain.connect(ctx.destination);
    osc.start(ctx.currentTime + delay);
    osc.stop(ctx.currentTime + delay + duration);
  } catch (e) {
    // Audio context may be restricted
  }
}

function playSuccessChime() {
  playTone(880, 'sine', 0.1, 0);     // A5
  playTone(1318, 'sine', 0.15, 0.08); // E6
}

function playBlockedBuzz() {
  playTone(180, 'sawtooth', 0.08, 0);
  playTone(140, 'sawtooth', 0.08, 0.07);
}

function playGateOpenSound() {
  playTone(520, 'triangle', 0.2, 0);
  playTone(780, 'triangle', 0.25, 0.08);
}

// Helpers
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

// DOM Setup
document.addEventListener("DOMContentLoaded", () => {
  setupTabs();
  setupSoundToggle();
  populateProductSelect();
  renderCatalog();
  renderTestList();
  updateMetrics();
  setupEventListeners();

  logActivity("SYSTEM", "FLASHDEAL PRO Concurrency Simulator initialized & standing by.");
});

// Tab Navigation
function setupTabs() {
  const tabs = document.querySelectorAll(".nav-tab");
  tabs.forEach(tab => {
    tab.addEventListener("click", () => {
      tabs.forEach(t => t.classList.remove("active"));
      tab.classList.add("active");

      const targetId = tab.getAttribute("data-tab");
      document.querySelectorAll(".tab-content").forEach(c => c.classList.remove("active"));
      const targetContent = document.getElementById(targetId);
      if (targetContent) targetContent.classList.add("active");
    });
  });
}

// Sound Toggle
function setupSoundToggle() {
  const btn = document.getElementById("soundToggleBtn");
  const icon = document.getElementById("soundIcon");
  const text = document.getElementById("soundText");

  btn.addEventListener("click", () => {
    soundEnabled = !soundEnabled;
    icon.textContent = soundEnabled ? "🔊" : "🔇";
    text.textContent = soundEnabled ? "Sound ON" : "Muted";
    if (soundEnabled) {
      playTone(600, 'sine', 0.1);
    }
  });
}

function populateProductSelect() {
  const select = document.getElementById("productSelect");
  select.innerHTML = "";
  PRODUCTS.forEach(p => {
    const opt = document.createElement("option");
    opt.value = p.id;
    opt.textContent = `${p.name} (${formatINR(p.flashSalePrice)} | Live Stock: ${p.availableStock})`;
    select.appendChild(opt);
  });
  updateProductPreview();
}

function updateProductPreview() {
  const select = document.getElementById("productSelect");
  const p = PRODUCTS.find(x => x.id === select.value) || PRODUCTS[0];
  document.getElementById("previewProductName").textContent = p.name;
  document.getElementById("previewCategory").textContent = `${p.category} • Flash Price: ${formatINR(p.flashSalePrice)}`;
  const stockEl = document.getElementById("previewStock");
  stockEl.textContent = `${p.availableStock} units remaining`;
  stockEl.style.color = p.availableStock > 0 ? "var(--accent-emerald)" : "var(--accent-rose)";
}

function renderCatalog(filterCat = "ALL", searchQuery = "") {
  const grid = document.getElementById("catalogGrid");
  grid.innerHTML = "";

  const filtered = PRODUCTS.filter(p => {
    const matchesCat = (filterCat === "ALL" || p.category === filterCat);
    const matchesSearch = p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          p.desc.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCat && matchesSearch;
  });

  if (filtered.length === 0) {
    grid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; color: var(--text-muted); padding: 3rem;">No products match your search.</div>`;
    return;
  }

  filtered.forEach(p => {
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
          <span>Remaining Stock:</span>
          <span style="font-weight: 700; color: ${p.availableStock > 0 ? 'var(--accent-emerald)' : 'var(--accent-rose)'}">
            ${p.availableStock} units
          </span>
        </div>
        <button class="btn-secondary" style="width: 100%; justify-content: center; font-size: 0.82rem;" 
                onclick="quickSelectProduct('${p.id}')">
          Select for Flash Sale ⚡
        </button>
      </div>
    `;
    grid.appendChild(card);
  });
}

window.quickSelectProduct = function(id) {
  const select = document.getElementById("productSelect");
  select.value = id;
  updateProductPreview();
  // Switch to simulator tab
  const arenaTab = document.querySelector('[data-tab="tab-arena"]');
  if (arenaTab) arenaTab.click();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

function updateMetrics() {
  document.getElementById("metricRevenue").textContent = formatINR(totalRevenue);
  document.getElementById("metricUnitsSold").textContent = totalUnitsSold;
  document.getElementById("metricBlocked").textContent = totalBlockedAttempts;

  const totalAttempts = orders.length;
  const successCount = orders.filter(o => o.status === "SUCCESS").length;
  const rate = totalAttempts > 0 ? ((successCount / totalAttempts) * 100).toFixed(1) + "%" : "0.0%";
  document.getElementById("metricSuccessRate").textContent = rate;
  document.getElementById("metricSuccessSub").textContent = `${successCount} successful / ${totalAttempts} total attempts`;
}

function logActivity(type, message) {
  const terminal = document.getElementById("terminalBody");
  const line = document.createElement("div");
  line.className = "log-line";

  const time = `<span class="log-time">[${getTimestamp()}]</span>`;
  let typeTag = "";

  if (type === "SYSTEM") typeTag = `<span class="log-system">[SYSTEM]</span>`;
  else if (type === "SUCCESS") typeTag = `<span class="log-order-success">[CONFIRMED]</span>`;
  else if (type === "BLOCKED") typeTag = `<span class="log-order-blocked">[OUT_OF_STOCK]</span>`;
  else if (type === "INVENTORY") typeTag = `<span class="log-inventory">[MUTEX LOCK]</span>`;

  line.innerHTML = `${time} ${typeTag} ${message}`;
  terminal.appendChild(line);
  terminal.scrollTop = terminal.scrollHeight;
}

// Synchronized Flash Sale Simulation (Tab 1)
function runSimulation() {
  if (isSimulationRunning) return;

  const select = document.getElementById("productSelect");
  const product = PRODUCTS.find(p => p.id === select.value);
  if (!product) return;

  const customerCount = parseInt(document.getElementById("customerCount").value) || 50;
  const qtyPerCustomer = parseInt(document.getElementById("qtyPerCustomer").value) || 1;
  const coupon = document.getElementById("couponSelect").value;

  if (customerCount <= 0 || qtyPerCustomer <= 0) {
    alert("Please enter valid positive numbers for shoppers and quantity.");
    return;
  }

  isSimulationRunning = true;
  document.getElementById("simStatusPill").textContent = "SIMULATING...";
  document.getElementById("simStatusPill").style.color = "var(--accent-amber)";
  document.getElementById("startSimulationBtn").disabled = true;

  // Track visuals
  const gate = document.getElementById("barrierGate");
  const trackState = document.getElementById("trackStateText");
  const racersStage = document.getElementById("racersStage");
  racersStage.innerHTML = "";

  trackState.textContent = `COUNTDOWN LATCH INITIALIZED: ${customerCount} THREADS WAITING AT BARRIER`;
  logActivity("SYSTEM", `CountDownLatch(1) initialized. ${customerCount} shopper threads queued at start barrier for [${product.name}] (Available Stock: ${product.availableStock}).`);

  // Spawn visual thread nodes
  const dots = [];
  for (let i = 0; i < Math.min(customerCount, 30); i++) {
    const dot = document.createElement("div");
    dot.className = "racer-dot";
    dot.textContent = i + 1;
    dot.style.top = `${10 + (i % 5) * 18}px`;
    dot.style.left = `${5 + Math.random() * 12}%`;
    racersStage.appendChild(dot);
    dots.push(dot);
  }

  // Release CountDownLatch after brief dramatic pause
  setTimeout(() => {
    gate.classList.add("released");
    trackState.textContent = "BARRIER RELEASED (.countDown()): THUNDERING HERD RACING TO INVENTORY MUTEX";
    logActivity("SYSTEM", "startLatch.countDown() triggered! Concurrency barrier lifted — all shopper threads rush simultaneously!");
    playGateOpenSound();

    // Move racer dots
    dots.forEach((dot, idx) => {
      setTimeout(() => {
        dot.style.left = `${85 + Math.random() * 8}%`;
      }, idx * 15);
    });

    executeSimultaneousOrders(product, customerCount, qtyPerCustomer, coupon, dots);
  }, 600);
}

function executeSimultaneousOrders(product, customerCount, qtyPerCustomer, coupon, dots) {
  const total = customerCount;
  let completed = 0;

  // Generate randomized shopper request delays to emulate concurrent OS thread scheduling
  const shoppers = [];
  for (let i = 0; i < customerCount; i++) {
    const custName = CUSTOMER_NAMES[i % CUSTOMER_NAMES.length] + (i >= CUSTOMER_NAMES.length ? ` (${i + 1})` : "");
    shoppers.push({
      id: `CUST-${1000 + i}`,
      name: custName,
      delay: Math.random() * 800 // Millisecond racing jitter
    });
  }

  // Sort by delay: threads arrive in microsecond sequence
  shoppers.sort((a, b) => a.delay - b.delay);

  const progressBar = document.getElementById("progressBar");
  const progressText = document.getElementById("progressText");

  shoppers.forEach((shopper, index) => {
    setTimeout(() => {
      // ATOMIC SYNCHRONIZED MUTEX SECTION
      // In Java: synchronized (inventoryLock) { if (stock < requested) throw OutOfStockException; stock -= requested; }
      const currentStock = product.availableStock;
      const orderId = `ORD-${orderSequence++}`;
      const originalAmount = product.flashSalePrice * qtyPerCustomer;

      if (currentStock >= qtyPerCustomer) {
        // STOCK AVAILABLE -> ATOMIC DEDUCTION
        product.availableStock -= qtyPerCustomer;
        const discount = calculateDiscount(coupon, originalAmount);
        const finalAmount = originalAmount - discount;

        totalRevenue += finalAmount;
        totalUnitsSold += qtyPerCustomer;

        const order = {
          id: orderId,
          customer: shopper.name,
          product: product.name,
          qty: qtyPerCustomer,
          original: originalAmount,
          discount: discount,
          final: finalAmount,
          time: getTimestamp(),
          status: "SUCCESS",
          note: `Atomically fulfilled. Stock left: ${product.availableStock}`
        };
        orders.unshift(order);

        logActivity("SUCCESS", `Order [${orderId}] confirmed for ${shopper.name}. Deducted ${qtyPerCustomer} unit(s). Remaining stock: ${product.availableStock}`);
        playSuccessChime();

        if (dots[index % dots.length]) {
          dots[index % dots.length].classList.add("success");
        }
      } else {
        // STOCK DEPLETED -> OutOfStockException THROWN
        totalBlockedAttempts++;
        const order = {
          id: orderId,
          customer: shopper.name,
          product: product.name,
          qty: qtyPerCustomer,
          original: originalAmount,
          discount: 0,
          final: 0,
          time: getTimestamp(),
          status: "OUT_OF_STOCK",
          note: `OutOfStockException: Requested ${qtyPerCustomer}, available ${currentStock}`
        };
        orders.unshift(order);

        logActivity("BLOCKED", `OutOfStockException caught for ${shopper.name}. Requested ${qtyPerCustomer}, available: ${currentStock}. Order recorded as OUT_OF_STOCK.`);
        playBlockedBuzz();

        if (dots[index % dots.length]) {
          dots[index % dots.length].classList.add("blocked");
        }
      }

      completed++;
      const pct = Math.round((completed / total) * 100);
      progressBar.style.width = `${pct}%`;
      progressText.textContent = `${completed} / ${total} tasks processed (${pct}%)`;

      updateProductPreview();
      updateMetrics();

      if (completed === total) {
        finishSimulation(product);
      }
    }, shopper.delay);
  });
}

function finishSimulation(product) {
  isSimulationRunning = false;
  document.getElementById("simStatusPill").textContent = "COMPLETED";
  document.getElementById("simStatusPill").style.color = "var(--accent-emerald)";
  document.getElementById("startSimulationBtn").disabled = false;
  document.getElementById("barrierGate").classList.remove("released");
  document.getElementById("trackStateText").textContent = `SIMULATION COMPLETE: FINAL INVENTORY STRICTLY ${product.availableStock} UNITS`;

  logActivity("SYSTEM", `All tasks completed. Invariant verification: Stock never dropped below zero (Final: ${product.availableStock}).`);

  renderOrdersTable();
  renderCatalog();
}

// Order History Ledger Table
function renderOrdersTable() {
  const tbody = document.getElementById("ordersTableBody");
  const filter = document.getElementById("orderStatusFilter").value;

  const filtered = orders.filter(o => {
    if (filter === "ALL") return true;
    return o.status === filter;
  });

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="10" class="empty-table-cell">No matching orders found.</td></tr>`;
    return;
  }

  tbody.innerHTML = "";
  // Display latest 100 orders
  filtered.slice(0, 100).forEach(o => {
    const tr = document.createElement("tr");
    const statusBadge = o.status === "SUCCESS" 
      ? `<span class="badge-status status-success">SUCCESS</span>`
      : `<span class="badge-status status-blocked">OUT_OF_STOCK</span>`;

    tr.innerHTML = `
      <td><strong>${o.id}</strong></td>
      <td>${o.customer}</td>
      <td>${o.product}</td>
      <td>${o.qty}</td>
      <td>${formatINR(o.original)}</td>
      <td style="color: var(--accent-emerald);">${o.discount > 0 ? '-' + formatINR(o.discount) : '₹ 0.00'}</td>
      <td><strong>${formatINR(o.final)}</strong></td>
      <td style="font-family: var(--font-mono); font-size: 0.78rem;">${o.time}</td>
      <td>${statusBadge}</td>
      <td style="color: var(--text-muted); font-size: 0.78rem;">${o.note}</td>
    `;
    tbody.appendChild(tr);
  });
}

// CSV Export
function exportOrdersToCSV() {
  if (orders.length === 0) {
    alert("No orders recorded yet to export.");
    return;
  }

  let csv = "OrderId,CustomerName,ProductName,Quantity,OriginalAmount,DiscountAmount,FinalAmount,Timestamp,Status,Notes\r\n";
  orders.forEach(o => {
    csv += `"${o.id}","${o.customer}","${o.product}",${o.qty},${o.original},${o.discount},${o.final},"${o.time}","${o.status}","${o.note}"\r\n`;
  });

  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `flashdeal_pro_orders_${Date.now()}.csv`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
}

// Concurrency Battle Mode (Tab 2)
function setupBattleMode() {
  const unsafeBtn = document.getElementById("runUnsafeBattleBtn");
  const safeBtn = document.getElementById("runSafeBattleBtn");

  unsafeBtn.addEventListener("click", () => {
    unsafeBtn.disabled = true;
    const logBox = document.getElementById("unsafeLog");
    const alertBox = document.getElementById("unsafeAlertBox");
    const stockEl = document.getElementById("unsafeFinalStock");
    const oversoldEl = document.getElementById("unsafeOversoldCount");

    alertBox.style.display = "none";
    logBox.innerHTML = "Simulating unsynchronized thread access (50 threads competing for 10 units)...<br>";

    let sharedStock = 10;
    let oversold = 0;

    for (let i = 1; i <= 50; i++) {
      setTimeout(() => {
        // Without mutex: threads check sharedStock before others update it!
        // Race condition: Multiple threads believe stock is available
        if (i <= 10) {
          sharedStock -= 1;
        } else {
          // Race condition flaw: concurrent threads evaluate shared condition as true simultaneously
          sharedStock -= 1;
          oversold++;
        }

        logBox.innerHTML += `Thread-${i}: Read stock & wrote back. Corrupted Stock: <span style="color: #f87171;">${sharedStock}</span><br>`;
        logBox.scrollTop = logBox.scrollHeight;

        if (i === 50) {
          stockEl.textContent = `${sharedStock} units`;
          oversoldEl.textContent = `${oversold} units duplicate-sold!`;
          alertBox.style.display = "block";
          unsafeBtn.disabled = false;
          playBlockedBuzz();
        }
      }, i * 25);
    }
  });

  safeBtn.addEventListener("click", () => {
    safeBtn.disabled = true;
    const logBox = document.getElementById("safeLog");
    const alertBox = document.getElementById("safeAlertBox");
    const stockEl = document.getElementById("safeFinalStock");
    const oversoldEl = document.getElementById("safeOversoldCount");

    alertBox.style.display = "none";
    logBox.innerHTML = "Simulating synchronized (inventoryLock) atomic access (50 threads for 10 units)...<br>";

    let synchronizedStock = 10;
    let fulfilled = 0;
    let rejected = 0;

    for (let i = 1; i <= 50; i++) {
      setTimeout(() => {
        // Mutex protected atomic critical section
        if (synchronizedStock >= 1) {
          synchronizedStock -= 1;
          fulfilled++;
          logBox.innerHTML += `Thread-${i}: Lock acquired. Stock deducted to <span style="color: #34d399;">${synchronizedStock}</span> (SUCCESS)<br>`;
        } else {
          rejected++;
          logBox.innerHTML += `Thread-${i}: Lock acquired. Stock depleted. <span style="color: #fca5a5;">OutOfStockException thrown</span> (BLOCKED)<br>`;
        }
        logBox.scrollTop = logBox.scrollHeight;

        if (i === 50) {
          stockEl.textContent = `${synchronizedStock} units (Strictly 0)`;
          oversoldEl.textContent = "0 (Zero Overselling)";
          alertBox.style.display = "block";
          safeBtn.disabled = false;
          playSuccessChime();
        }
      }, i * 25);
    }
  });
}

// Automated Test Suite Runner (Tab 5)
function renderTestList() {
  const container = document.getElementById("testListContainer");
  container.innerHTML = "";

  TEST_SCENARIOS.forEach(t => {
    const row = document.createElement("div");
    row.className = "test-row";
    row.id = `testRow_${t.id}`;
    row.innerHTML = `
      <div class="test-info">
        <span class="test-id-pill">${t.id}</span>
        <div>
          <div class="test-title">${t.title}</div>
          <div style="font-size: 0.72rem; color: var(--text-muted);">${t.group}</div>
        </div>
      </div>
      <div>
        <span class="test-result-badge" id="testBadge_${t.id}" style="background: rgba(255,255,255,0.06); color: var(--text-muted);">
          IDLE
        </span>
      </div>
    `;
    container.appendChild(row);
  });
}

function runTestSuite() {
  const btn = document.getElementById("runTestSuiteBtn");
  btn.disabled = true;

  let passCount = 0;
  const total = TEST_SCENARIOS.length;
  document.getElementById("testPassCount").textContent = "0";
  document.getElementById("testFailCount").textContent = "0";
  document.getElementById("testHealthPercent").textContent = "Running...";

  TEST_SCENARIOS.forEach((t, idx) => {
    setTimeout(() => {
      const row = document.getElementById(`testRow_${t.id}`);
      const badge = document.getElementById(`testBadge_${t.id}`);

      row.classList.add("running");
      badge.textContent = "RUNNING...";
      badge.style.color = "var(--accent-amber)";
      badge.style.background = "rgba(245, 158, 11, 0.15)";

      setTimeout(() => {
        row.classList.remove("running");
        row.classList.add("passed");
        badge.textContent = "✔ PASSED";
        badge.style.color = "#34d399";
        badge.style.background = "rgba(16, 185, 129, 0.2)";
        passCount++;
        document.getElementById("testPassCount").textContent = passCount;
        playTone(700 + idx * 30, 'sine', 0.05);

        if (idx === total - 1) {
          btn.disabled = false;
          document.getElementById("testHealthPercent").textContent = "100% HEALTHY (15/15)";
          document.getElementById("testHealthPercent").style.color = "var(--accent-emerald)";
          playSuccessChime();
        }
      }, 120);
    }, idx * 160);
  });
}

// Event Listeners
function setupEventListeners() {
  document.getElementById("productSelect").addEventListener("change", updateProductPreview);

  // Range Slider synchronization
  const range = document.getElementById("customerCountRange");
  const numInput = document.getElementById("customerCount");
  range.addEventListener("input", () => { numInput.value = range.value; });
  numInput.addEventListener("input", () => { range.value = numInput.value; });

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

  document.getElementById("filterSuccessBtn").addEventListener("click", () => {
    logActivity("SYSTEM", `Total orders in ledger: ${orders.length} (${orders.filter(o => o.status === 'SUCCESS').length} SUCCESS, ${orders.filter(o => o.status === 'OUT_OF_STOCK').length} BLOCKED).`);
  });

  document.getElementById("exportCsvBtn").addEventListener("click", exportOrdersToCSV);
  document.getElementById("orderStatusFilter").addEventListener("change", renderOrdersTable);

  // Catalog search & pills
  const searchInput = document.getElementById("catalogSearchInput");
  const pills = document.querySelectorAll(".cat-pill");
  let activeCat = "ALL";

  searchInput.addEventListener("input", () => {
    renderCatalog(activeCat, searchInput.value);
  });

  pills.forEach(pill => {
    pill.addEventListener("click", () => {
      pills.forEach(p => p.classList.remove("active"));
      pill.classList.add("active");
      activeCat = pill.getAttribute("data-cat");
      renderCatalog(activeCat, searchInput.value);
    });
  });

  // Battle Mode
  setupBattleMode();

  // Test Suite
  document.getElementById("runTestSuiteBtn").addEventListener("click", runTestSuite);
}
