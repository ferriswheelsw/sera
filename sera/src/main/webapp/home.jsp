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
            <a href="home" style="font-weight:bold">Home</a>
            <a href="portfolio">Portfolio</a>
            <a href="income" >Income</a>
            <a href="profile">Profile</a>
        </div>
        <a href="login.jsp" class="logout">LOGOUT</a>
    </div>

    <div style="display:inline;">
        <div style="float:left; position: absolute;
  top: 50%;
  left: 28%;
  transform: translate(-50%, -50%);
margin: 0 auto;
width:250px;
display: flex;
  flex-wrap: wrap;
  /* for horizontal aligning of child divs */
  justify-content: center;" class="animate-bottom">
            <img src="img/seranopadding.png" width="250px" >
            <p style="text-align:center;">Check out your portfolio today!</p>
            <div style="display:inline-block; vertical-align: middle; ">
                <img src="img/seralogo2.png" width="30px" style="display:inline-block">
                <p style="display: inline-block;vertical-align: 70%;">SERA Team</p>
            </div>

        </div>

        <div style="position: absolute; top: 50%; left: 65%; transform: translate(-50%, -50%);">
            <h3>Welcome, <%=((User)request.getSession().getAttribute("user")).getFirstName()%>.</h3>
            <p>Your top stocks today: </p>
            <table id="hometable">
                <%ArrayList<Stock> topFive = (ArrayList<Stock>) request.getAttribute("topfive");
                for (int i=0; i<topFive.size();i++){
                %>
                <tr>
                    <td style="font-weight:bold;"><%=topFive.get(i).getStockCode()%></td>
                    <td><%=String.format("%.2f", topFive.get(i).getMarketPrice())%> <%=topFive.get(i).getStockCur()%></td>

                    <%if ( (topFive.get(i).getPriceChange()/100*(topFive.get(i).getMarketPrice()-(topFive.get(i).getPnl()/topFive.get(i).getHoldings()))) > 0){
                    %>
<%--                    absolute price change--%>
                    <td>+<%=String.format("%.2f", topFive.get(i).getPriceChange()/100*(topFive.get(i).getMarketPrice()-(topFive.get(i).getPnl()/topFive.get(i).getHoldings())))%> <%=topFive.get(i).getStockCur()%></td>
                    <td>+<%=String.format("%.2f", topFive.get(i).getPriceChange())%>%</td>
                    <td><img src="img/up.png" width="30"></td>
                    <%
                        }else{
                    %>
                    <td><%=String.format("%.2f", topFive.get(i).getPriceChange()/100*(topFive.get(i).getMarketPrice()-(topFive.get(i).getPnl()/topFive.get(i).getHoldings())))%> <%=topFive.get(i).getStockCur()%></td>
                    <td><%=String.format("%.2f", topFive.get(i).getPriceChange())%>%</td>
                    <td><img src="img/down.png" width="30"></td>
                    <%
                        }%>
                </tr>
                <%
                    }
                %>

            </table>
            <h3>Total estimate income this year / <%=(String)((User) request.getSession().getAttribute("user")).getDefaultCurrency()%>: <%=String.format("%.2f",(double)request.getSession().getAttribute("totalincome"))%></h3>
            <h3>Total income this month / <%=(String)((User) request.getSession().getAttribute("user")).getDefaultCurrency()%>: <%=String.format("%.2f",(double)request.getSession().getAttribute("totalPerMonth"))%></h3>

        </div>
    </div>

</div>


</body>
</html>
