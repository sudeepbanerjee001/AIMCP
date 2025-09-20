let ws;

document.getElementById('connectBtn').addEventListener('click', function() {
    ws = new WebSocket('ws://localhost:8080/mcp');
    ws.onopen = () => log('Connected to MCP Server');
    ws.onmessage = event => log('Received: ' + event.data);
    ws.onclose = () => log('Connection closed');
});

document.getElementById('subscribeBtn').addEventListener('click', function() {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        log('WebSocket is not connected');
        return;
    }
    const msg = {
        jsonrpc: "2.0",
        id: 1,
        method: "subscribe_resource",
        params: { name: "quotes" }
    };
    ws.send(JSON.stringify(msg));
});

function log(message) {
    const logDiv = document.getElementById('log');
    logDiv.innerHTML += message + '<br>';
}