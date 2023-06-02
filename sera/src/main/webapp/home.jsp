<%@ page import="sera.sera.User" %>
<%@ page import="sera.sera.Stock" %>
<%@ page import="java.util.ArrayList" %><%--
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
    <link rel="stylesheet" href="css/tables.css">
    <div class="navoverall">
        <h2 class="sera">SERA</h2>
        <div class = "nav">
            <a href="home" class="bold">Home</a>
            <a href="portfolio">Portfolio</a>
            <a href="income" >Income</a>
            <a href="profile">Profile</a>
        </div>
        <a href="login.jsp" class="logout">LOGOUT</a>
    </div>

</head>
<body>
<div>
    <div>
        <img>
    </div>
    <div>
        <h3>welcome, <%=((User)request.getSession().getAttribute("user")).getFirstName()%>.</h3>
        <p>You top stocks today: </p>
        <table>
            <%ArrayList<Stock> topFive = (ArrayList<Stock>) request.getAttribute("topfive");
            for (int i=0; i<topFive.size();i++){
            %>
            <tr>
                <td><%=topFive.get(i).getStockCode()%></td>
                <td><%=String.format("%.2f", topFive.get(i).getMarketPrice())%></td>
<%--                calculate absolute price change--%>
                <td>+<%=String.format("%.2f", topFive.get(i).getPriceChange()*(topFive.get(i).getMarketPrice()-(topFive.get(i).getPnl()/topFive.get(i).getHoldings())))%><%=topFive.get(i).getStockCur()%></td>
                <td><%=String.format("%.2f", topFive.get(i).getPriceChange())%>%</td>
                <td>img of stock prices going up</td>
            </tr>
            <%
                }
            %>

        </table>
    </div>
</div>

<h3> div freq for stock 1: <%=((User)request.getSession().getAttribute("user")).getStocks().get(0).getDivfreq()%></h3>

</body>
</html>
