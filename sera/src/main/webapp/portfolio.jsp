<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="sera.sera.User" %>
<%@ page import="sera.sera.Stock" %>
<%@ page import="java.lang.reflect.Array" %><%--
  Created by IntelliJ IDEA.
  User: sonja
  Date: 17/5/2023
  Time: 11:13 am
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>portfolio</title>
    <link rel="stylesheet" href="css/tables.css">
</head>
<body>
<div class="navoverall">
    <h2 class="sera">SERA</h2>
    <div class = "nav">
        <a href="home">Home</a>
        <a href="portfolio" style="font-weight:bold">Portfolio</a>
        <a href="income">Income</a>
        <a href="profile">Profile</a>
    </div>
    <a href="login.jsp" class="logout">LOGOUT</a>
</div>
<br>
<form action="portfolio" method="Get" class="tabb" style="margin: 0px">
    <%
        ArrayList<String> markets1 = ((User)request.getSession().getAttribute("user")).getMarkets();
        for (String market : markets1) {
            if(market.equals(request.getAttribute("currentMarket"))){
    %>
    <input class="selected" type="submit" name="action" value=<%=market%>>
    <%
    } else{
    %><input class="normal" type="submit" name="action" value=<%=market%>>
    <%
            }
        }
    %>
</form>

<h3 style="color:#009345">Total Income / <%=((User)request.getSession().getAttribute("user")).getDefaultCurrency()%>: ${totalIncome}</h3>

<table id="ptable">
    <%ArrayList<Stock> sL = (ArrayList<Stock>)request.getAttribute("stockList");
    String cur = sL.get(0).getStockCur();%>
    <tr>
        <td style="font-weight:bold">Stock code</td>
        <td>Market price / <%=cur%></td>
        <td>%1D</td>
        <td>1D PnL / <%=((User)request.getSession().getAttribute("user")).getDefaultCurrency()%></td>
        <td>%div</td>
        <td>1Y div / <%=((User)request.getSession().getAttribute("user")).getDefaultCurrency()%></td>
        <td>Ex-div dates per yr</td>
        <td>Quantity</td>
    </tr>
    <%
        for (Stock s : sL) {
    %>
    <tr>
        <td style="font-weight:bold"><%=s.getStockCode()%></td>
        <td><%=String.format("%.2f", s.getMarketPrice())%></td>
        <td><%=String.format("%.2f", s.getPriceChange())%></td>
        <td><%=String.format("%.2f", s.getPnl())%></td>
        <td><%=String.format("%.2f", s.getPercentdiv())%></td>
        <td><%=String.format("%.2f", s.getTotaldiv())%></td>
        <td><%=s.getDivfreq()%></td>
        <td><%=s.getHoldings()%></td>
    </tr>
    <%
        }
    %>
</table>


</body>
</html>
