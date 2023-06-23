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
<div style="display:inline;">
    <h2 style="float:left; margin:auto;">SERA</h2>
    <p style="text-align: center; margin: auto;">Welcome to SERA</p>
</div>

<div style="display:inline;">
    <img src="img/serasquare.png" width="500px" style="float:left; position: absolute;
  top: 46%;
  left: 32%;
  transform: translate(-50%, -50%);" class="animate-bottom">
    <div style="position: absolute;
  top: 46%;
  left: 62%;
  transform: translate(-50%, -50%);">
        <form action="home" method="post" onsubmit="return load()" class="animate-bottom" id="loginForm">
            <p style="padding-bottom: 10px">Input email           :   <input type = "text" name = "email" required/></p>
            <p style="padding-bottom: 10px">Input password          :   <input type = "text" name = "pw" required/></p>
            <p><input type="submit" class="btn" value="SUBMIT" /></p>
        </form>
    </div>
    <%--https://www.w3schools.com/howto/howto_css_loader.asp--%>
    <div id="loginloader" class="loader" style="
/*center of page*/
/*https://blog.hubspot.com/website/center-div-css#center-div-vertically-css*/
position: absolute;
  top: 41%;
  left: 60%;
  transform: translate(-50%, -50%);
width:60px;
height:60px;"></div>
</div>



<script>
    function load() {
        document.getElementById("loginloader").style.display = "block";
        document.getElementById("loginForm").style.display = "none";
    }
</script>

</body>
</html>
