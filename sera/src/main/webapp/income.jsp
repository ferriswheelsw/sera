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
</head>
<body>
<form id="selectMarket" action="income" method="GET">
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

<%--For displaying Previous link except for the 1st page --%>
<%if ((int)request.getAttribute("currentPage")!=1){%>
    <td><a href="income?page=${currentPage - 1}">Previous</a></td>
<%}%>
<%--For displaying Page numbers. The when condition does not display
            a link for the current page--%>

<table border="1" cellpadding="5" cellspacing="5">
    <tr>
        <c:forEach begin="1" end="${noOfPages}" var="i">
            <c:choose>
                <c:when test="${currentPage eq i}">
                    <td>${i}</td>
                </c:when>
                <c:otherwise>
                    <td><a href="income?page=${i}">${i}</a></td>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </tr>
</table>

<%--For displaying Next link --%>


    <%if ((int)request.getAttribute("currentPage")< (int)request.getAttribute("noOfPages")){%>
    <td><a href="income?page=${currentPage + 1}">Next</a></td>
    <%}%>


</body>
</html>
