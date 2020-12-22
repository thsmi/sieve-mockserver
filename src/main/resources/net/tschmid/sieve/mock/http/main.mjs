
let socket = null;

function connect() {
  let socket = new WebSocket("ws://localhost:8080/log");

  socket.onopen = function (e) {
    document.querySelector("#status").classList.add("connected");
    console.log("[open] Connection established");
    document.getElementById("txtOutput").value = "Connected to server\r\n";
  };

  socket.onmessage = function (event) {
    document.getElementById("txtOutput").value = event.data + "\r\n" + document.getElementById("txtOutput").value;
    // alert(`[message] Data received from server: ${event.data}`);
  };

  socket.onclose = function (event) {

    document.querySelector("#status").classList.remove("connected");

    if (event.wasClean) {
      document.getElementById("txtOutput")
        .value = `[close] Connection closed cleanly, code=${event.code} reason=${event.reason}` + "\r\n" + document.getElementById("txtOutput").value;
      return;
    }

    document.getElementById("txtOutput")
      .value = '[close] Connection died' + "\r\n" + document.getElementById("txtOutput").value;

    return;
  };

  socket.onerror = function (error) {
    alert(`[error] ${error.message}`);
  };

  return socket;
}


async function start() {
  if (socket === null || socket.readyState !== socket.OPEN)
    socket = connect();


  const value = document.getElementById("txtTest").value;

  await fetch('test', {
    method: 'PUT',
    cache: 'no-cache',
    headers: {
      'Content-Type': 'text/plain'
    },
    body: value
  });
}


async function main() {

  socket = connect();

  document.getElementById("cbTemplates").addEventListener("change", async () => {

    const url = new URL("template", document.URL);

    url.searchParams.append("template", document.getElementById("cbTemplates").value);

    const response = await fetch(url, {
      cache: "no-cache",
      headers: {
        'Content-Type': 'text/plain'
      },
    });

    document.getElementById("txtTest").value = await (response.text());

    start();
  });

  document.getElementById("btnStartTest").addEventListener("click", async () => {
    start();
  });

  document.getElementById("btnStopTest").addEventListener("click", async () => {
    await fetch('test', {method: 'DELETE', cache: 'no-cache'});
  });

  document.getElementById("btnStopServer").addEventListener("click", async () => {
    await fetch('shutdown', {method: 'DELETE', cache: 'no-cache'});
  });  

  let flags = await ((await fetch('flags', {cache: 'no-cache'})).json());

  document.getElementById("cbxStartTLS").checked = !(flags["SUPPORT_TLS"] === "true");  

  document.getElementById("cbxRespawn").checked = (flags["NO_RESPAWN"] === "true");

  document.getElementById("cbxCyrusBug").checked = (flags["SIMULATE_CYRUS_STARTTLS_BUG"] === "true");
  document.getElementById("cbxFragment").checked = (flags["SIMULATE_FRAGMENTATION"] == "true");
  document.getElementById("cbxSlowResponse").checked = (flags["SIMULATE_DELAY"] == "true");
  
  document.getElementById("cbxSecureSasl").checked = (flags["SECURE_SASL"] === "true");
  
  console.log(flags);
}

if (document.readyState !== 'loading')
  main();
else
  document.addEventListener('DOMContentLoaded', () => { main(); }, { once: true });