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
    <a href="home">HOME</a>
    <a href="portfolio">PORTFOLIO</a>
    <a href="income">INCOME</a>
    <link rel="stylesheet" href="css/tables.css">
</head>
<body>
<form id="selectMarket" action="portfolio" method="GET" class="nav">
    <select name="selectM" onchange="document.getElementById('selectMarket').submit();">
        <%
            ArrayList<String> markets = ((User)request.getSession().getAttribute("user")).getMarkets();
            for (String market : markets) {
                if (request.getAttribute("currentMarket").equals(market)){


        %>
        <option value="<%=market%>"  id ='mN' selected><%= market %></option>
        <%
            }
                else{
        %>
        <option value="<%=market%>" id='mN'><%= market %></option>
        <%
                }
            }
        %>
    </select>
</form>

<h3>Total Income / <%=((User)request.getSession().getAttribute("user")).getDefaultCurrency()%>: ${totalIncome}</h3>

<table>
    <%ArrayList<Stock> sL = (ArrayList<Stock>)request.getAttribute("stockList");
    String cur = sL.get(0).getStockCur();%>
    <tr>
        <td>Stock code</td>
        <td>Current market price / <%=cur%></td>
        <td>%1D</td>
        <td>1D PnL / <%=((User)request.getSession().getAttribute("user")).getDefaultCurrency()%></td>
        <td>%div</td>
        <td>1Y div / <%=((User)request.getSession().getAttribute("user")).getDefaultCurrency()%></td>
        <td>No of ex-div dates per year</td>
        <td>Quantity</td>
    </tr>
    <%
        for (Stock s : sL) {
    %>
    <tr>
        <td><%=s.getStockCode()%></td>
        <td><%=s.getMarketPrice()%></td>
        <td><%=s.getPriceChange()%></td>
        <td><%=s.getPnl()%></td>
        <td><%=s.getPercentdiv()%></td>
        <td><%=s.getTotaldiv()%></td>
        <td><%=s.getDivfreq()%></td>
        <td><%=s.getHoldings()%></td>
    </tr>
    <%
        }
    %>
</table>


</body>
</html>
