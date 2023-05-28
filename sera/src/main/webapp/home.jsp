<%@ page import="sera.sera.User" %><%--
  Created by IntelliJ IDEA.
  User: sonja
  Date: 11/5/2023
  Time: 7:16 am
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title> Home </title>
</head>
<body>
<a href="home">HOME</a>
<a href="portfolio">PORTFOLIO</a>
<a href="income">INCOME</a>
<%--<button onclick="location.href ='portfolio'" >Back Home</button>--%>
<h3> Welcome, <%=((User)request.getSession().getAttribute("user")).getFirstName()%>!</h3>
<h3> div freq for stock 1: <%=((User)request.getSession().getAttribute("user")).getStocks().get(0).getDivfreq()%></h3>

</body>
</html>
