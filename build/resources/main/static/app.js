let ws;
let currentSessionId = null;
let sessions = JSON.parse(localStorage.getItem("sessions")) || {};

// ---------------- WebSocket ----------------
function initWebSocket() {
  ws = new WebSocket("ws://localhost:8080/mcp");

  ws.onopen = () => {
    console.log("Connected to MCP Server");
    if (!currentSessionId) createNewSession();
    addSystemMessage("Connected to server.");
  };

  ws.onmessage = (event) => {
    addMessage("server", event.data);
    saveMessage("server", event.data);
  };

  ws.onclose = () => {
    addSystemMessage("Disconnected from server.");
  };
}

// ---------------- Message Formatting ----------------
function formatText(text) {
  // Try to parse JSON for AI responses
  try {
    const data = JSON.parse(text);
    if (data.response) {
      // Escape HTML special characters for safe display
      const code = data.response
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
      return `<pre><code class="language-java">${code}</code></pre>`;
    }
  } catch (e) {
    // Not JSON, continue formatting as normal
  }

  // Remove redundant "AI says:"
  text = text.replace(/^AI says:\s*/i, "");

  // Replace Markdown bold **text** and italics _text_
  text = text.replace(/\*\*(.*?)\*\*/g, "<b>$1</b>");
  text = text.replace(/_(.*?)_/g, "<i>$1</i>");

  // Convert numbered lists to bullets
  text = text.replace(/^\d+\.\s+/gm, "<br>• ");
  // Convert dash lists to bullets
  text = text.replace(/^-+\s+/gm, "<br>• ");

  // Convert newlines to <br>
  text = text.replace(/\n/g, "<br>");

  return text;
}

// ---------------- Display Messages ----------------
function addMessage(sender, text) {
  const messagesDiv = document.getElementById("messages");
  const msg = document.createElement("div");
  msg.classList.add("message", sender);
  msg.innerHTML = (sender === "server" ? "AI: " : "You: ") + formatText(text);
  messagesDiv.appendChild(msg);
  messagesDiv.scrollTop = messagesDiv.scrollHeight;

  // Re-run Prism syntax highlighting if code exists
  if (sender === "server") {
    Prism.highlightAll();
  }
}

function addSystemMessage(text) {
  const messagesDiv = document.getElementById("messages");
  const msg = document.createElement("div");
  msg.classList.add("message", "server");
  msg.style.fontStyle = "italic";
  msg.innerHTML = "System: " + text;
  messagesDiv.appendChild(msg);
  messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

// ---------------- Session Management ----------------
function saveMessage(sender, text) {
  if (!currentSessionId) return;
  sessions[currentSessionId].messages.push({ sender, text });
  localStorage.setItem("sessions", JSON.stringify(sessions));
}

function loadSession(sessionId) {
  currentSessionId = sessionId;
  document.querySelectorAll("#session-list li").forEach(li =>
    li.classList.remove("active")
  );
  document.querySelector(`#session-${sessionId}`).classList.add("active");

  const messagesDiv = document.getElementById("messages");
  messagesDiv.innerHTML = "";
  sessions[sessionId].messages.forEach(m => addMessage(m.sender, m.text));
}

function createNewSession() {
  const id = Date.now().toString();
  sessions[id] = { id, messages: [] };
  localStorage.setItem("sessions", JSON.stringify(sessions));
  renderSessions();
  loadSession(id);
}

function renderSessions() {
  const list = document.getElementById("session-list");
  list.innerHTML = "";
  Object.values(sessions).forEach(session => {
    const li = document.createElement("li");
    li.id = `session-${session.id}`;
    li.textContent = "Chat " + new Date(parseInt(session.id)).toLocaleString();
    li.onclick = () => loadSession(session.id);
    list.appendChild(li);
  });
}

// ---------------- Input Handling ----------------
document.getElementById("sendButton").onclick = () => {
  const input = document.getElementById("messageInput");
  if (input.value.trim() !== "") {
    ws.send(input.value);
    addMessage("user", input.value);
    saveMessage("user", input.value);
    input.value = "";
  }
};

document.getElementById("new-session").onclick = () => {
  createNewSession();
};

// ---------------- Initialize ----------------
renderSessions();
initWebSocket();
