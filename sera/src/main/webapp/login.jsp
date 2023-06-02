<%--
  Created by IntelliJ IDEA.
  User: sonja
  Date: 11/5/2023
  Time: 7:06 am
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>LOGIN</title>
    <link rel="stylesheet" href="css/tables.css">
</head>
<body>
<h1>Login</h1>
<form action="home" method="post" onsubmit="return load()" class="animate-bottom" id="loginForm">
    <p>Input email <input type = "text" name = "email" required/></p>
    <p>Input password <input type = "text" name = "pw" required/></p>
    <p><input type="submit" value="SUBMIT"/></p>
</form>
<%--https://www.w3schools.com/howto/howto_css_loader.asp--%>
<div id="loginloader" class="loader" style="
/*center of page*/
/*https://blog.hubspot.com/website/center-div-css#center-div-vertically-css*/
position: absolute;
  top: 38%;
  left: 46%;
  transform: translate(-50%, -50%);"></div>

<script>
    function load() {
        document.getElementById("loginloader").style.display = "block";
        document.getElementById("loginForm").style.display = "none";
    }
</script>

</body>
</html>
