<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>WebLogic OpenAI Analyzer</title>
</head>
<body>
<h1>WebLogic + OpenAI Analyzer</h1>
<textarea id="logs" rows="12" cols="100" placeholder="Cole aqui logs de exemplo...">Sample WebLogic log: [paste your log here]</textarea><br>
<button type="button" onclick="send()">Enviar para análise</button>
<pre id="result"></pre>

<script>
function send(){
  const logs = document.getElementById('logs').value;
  fetch('analyze', {method: 'POST', body: logs})
    .then(async r => {
      const text = await r.text();
      try {
        const j = JSON.parse(text);
        document.getElementById('result').textContent = JSON.stringify(j, null, 2);
      } catch(e){
        document.getElementById('result').textContent = text;
      }
    })
    .catch(e => document.getElementById('result').textContent = 'Erro: '+e);
}
</script>
</body>
</html>
