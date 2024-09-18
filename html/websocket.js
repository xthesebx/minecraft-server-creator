// small helper function for selecting element by id
let id = id => document.getElementById(id);

//Establish the WebSocket connection and set up event handlers
let ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/ws" + location.pathname);
ws.onmessage = msg => updateChat(msg);
ws.onclose = close => {
    if(close.code === 1008) alert("WebSocket connection closed because ".concat(close.reason));
    history.back();
}

// Add event listeners to button and input field
id("send").addEventListener("click", () => sendAndClear(id("message").value));
id("back").addEventListener("click", () => {
    ws.close();
    history.back();
});
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { // Send message if enter is pressed in input field
        sendAndClear(e.target.value);
    }
});

function sendAndClear(message) {
    if (message !== "") {
        ws.send(message);
        id("message").value = "";
    }
}

function updateChat(msg) { // Update chat-panel and list of connected users
    console.log(msg);
    console.log(msg.data);
    if (msg.data === "ping") {
        return;
    }
    id("chat").insertAdjacentHTML("beforeend", msg.data);
    id("chat").insertAdjacentHTML("beforeend", "<br>");
}