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


</head>
<body>
<div>
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
                <td>+<%=String.format("%.2f", topFive.get(i).getPriceChange()/100*(topFive.get(i).getMarketPrice()-(topFive.get(i).getPnl()/topFive.get(i).getHoldings())))%><%=topFive.get(i).getStockCur()%></td>
                <td><%=String.format("%.2f", topFive.get(i).getPriceChange())%>%</td>
                <%if ( (topFive.get(i).getPriceChange()/100*(topFive.get(i).getMarketPrice()-(topFive.get(i).getPnl()/topFive.get(i).getHoldings()))) > 0){
                %> <td><img src="img/up.png" width="30"></td>
                <%
                    }else{
                %> <td><img src="img/down.png" width="30"></td>
                <%
                    }%>
            </tr>
            <%
                }
            %>

        </table>
    </div>
</div>

<h3>Total estimate income this year / <%=(String)((User) request.getSession().getAttribute("user")).getDefaultCurrency()%>: <%=(double)request.getSession().getAttribute("totalincome")%></h3>
<h3>Total income this month / <%=(String)((User) request.getSession().getAttribute("user")).getDefaultCurrency()%>: <%=(double)request.getSession().getAttribute("totalPerMonth")%></h3>

</body>
</html>
