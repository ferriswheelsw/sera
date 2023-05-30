<%@ page import="sera.sera.User" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="sera.sera.Stock" %><%--
  Created by IntelliJ IDEA.
  User: sonja
  Date: 23/5/2023
  Time: 8:04 am
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Income</title>
    <a href="home">HOME</a>
    <a href="portfolio">PORTFOLIO</a>
    <a href="income">INCOME</a>
    <link rel="stylesheet" href="css/tables.css">
<%--    <style>--%>
<%--        /* Style the tab */--%>
<%--        .tabb {--%>
<%--            overflow: hidden;--%>
<%--            border: 1px solid #ccc;--%>
<%--            background-color: #f1f1f1;--%>
<%--            max-width:300px;--%>
<%--        }--%>

<%--        /* Style the buttons inside the tab */--%>
<%--        .tabb button {--%>
<%--            background-color: inherit;--%>
<%--            float: left;--%>
<%--            border: none;--%>
<%--            outline: none;--%>
<%--            cursor: pointer;--%>
<%--            padding: 14px 16px;--%>
<%--            transition: 0.3s;--%>
<%--            font-size: 17px;--%>
<%--        }--%>

<%--        /* Change background color of buttons on hover */--%>
<%--        .tabb button:hover {--%>
<%--            background-color: #ddd;--%>
<%--        }--%>

<%--        /* Create an active/current tablink class */--%>
<%--        .tabb button.active {--%>
<%--            background-color: #ccc;--%>
<%--        }--%>

<%--    </style>--%>
</head>
<body>
<div class="tabb">
    <button id="m1" onclick="document.getElementById('selectMarket').submit();" value = "m1" >plus
    </button>
    <button id="m2" onclick="document.getElementById('selectMarket').submit();" value = "m2">minus
    </button>
    <button id="m3" onclick="document.getElementById('selectMarket').submit();" value = "m3">star
    </button>
</div>
<form id="selectMarket" action="income" method="GET" margin="20px" class="nav">
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

<table>
    <tr>
        <td>Stock code</td>
        <td>Jan</td>
        <td>Feb</td>
        <td>Mar</td>
        <td>Apr</td>
        <td>May</td>
        <td>Jun</td>
        <td>Jul</td>
        <td>Aug</td>
        <td>Sep</td>
        <td>Oct</td>
        <td>Nov</td>
        <td>Dec</td>
    </tr>
    <%
        double[][] iT = (double[][])request.getAttribute("incomeTable");
        ArrayList<Stock> sL = (ArrayList<Stock>)request.getAttribute("stockList");
        for (int i=0;i<sL.size();i++) {
            double[] row = iT[i];
    %>
    <tr>
        <td><%=sL.get(i).getStockCode()%></td>
        <%
            for(int j=0;j<12;j++){
                %>  <td><%=row[j]%></td>
        <%
            }
            %>
    </tr>
    <%
        }
    %>
</table>

<table class="pag">
    <tr>
<%--For displaying Previous link except for the 1st page --%>
<%if ((int)request.getAttribute("currentPage")!=1){%>
    <td class="pag"><a href="income?page=${currentPage - 1}">Previous</a></td>
<%}
for(int i=1;i<((int)request.getAttribute("noOfPages")+1);i++){
    %>
    <td class="pag"><a href="income?page=<%=i%>"><%=i%></a></td>
    <%
}%>

<%--For displaying Next link --%>

    <%if ((int)request.getAttribute("currentPage")< (int)request.getAttribute("noOfPages")){%>
    <td class="pag"><a href="income?page=${currentPage + 1}">Next</a></td>
    <%}%>

    </tr>
</table>

</body>
</html>
