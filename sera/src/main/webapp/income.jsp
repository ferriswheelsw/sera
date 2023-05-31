<%@ page import="sera.sera.User" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="sera.sera.Stock" %>
<%@ page import="sera.sera.Dividend" %><%--
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
    <div class="navoverall">
        <h2 class="sera">SERA</h2>
        <div class = "nav">
            <a href="home">Home</a>
            <a href="portfolio">Portfolio</a>
            <a href="income" class="bold">Income</a>
        </div>
        <a href="login.jsp" class="logout">LOGOUT</a>
    </div>


    <link rel="stylesheet" href="css/tables.css">
</head>
<body>
<br>
<form action="income" method="Get" class="tabb">
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


<%--<table>--%>
<%--    <tr>--%>
<%--        <td>Stock code</td>--%>
<%--        <td>Jan</td>--%>
<%--        <td>Feb</td>--%>
<%--        <td>Mar</td>--%>
<%--        <td>Apr</td>--%>
<%--        <td>May</td>--%>
<%--        <td>Jun</td>--%>
<%--        <td>Jul</td>--%>
<%--        <td>Aug</td>--%>
<%--        <td>Sep</td>--%>
<%--        <td>Oct</td>--%>
<%--        <td>Nov</td>--%>
<%--        <td>Dec</td>--%>
<%--    </tr>--%>
<%--    <%--%>
<%--        double[][] iT = (double[][])request.getAttribute("incomeTable");--%>
<%--        ArrayList<Stock> sL = (ArrayList<Stock>)request.getAttribute("stockList");--%>
<%--        for (int i=0;i<sL.size();i++) {--%>
<%--            double[] row = iT[i];--%>
<%--    %>--%>
<%--    <tr>--%>
<%--        <td><%=sL.get(i).getStockCode()%></td>--%>
<%--        <%--%>
<%--            for(int j=0;j<12;j++){--%>
<%--                %>  <td><%=row[j]%></td>--%>
<%--        <%--%>
<%--            }--%>
<%--            %>--%>
<%--    </tr>--%>
<%--    <%--%>
<%--        }--%>
<%--    %>--%>
<%--</table>--%>

<%-- new new--%>
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
        Dividend[][] divT = (Dividend[][])request.getAttribute("divTable");
        ArrayList<Stock> stockL = (ArrayList<Stock>)request.getAttribute("stockList");
        for (int i=0;i<stockL.size();i++) {
            Dividend[] row = divT[i];
    %>
    <tr>
        <td><%=stockL.get(i).getStockCode()%></td>
        <%
            for(int j=0;j<12;j++){
                if (row[j] == null){
        %> <td>0.00</td>
        <%
                }else{
        %>  <td class = "<%=row[j].getDivType()%>"><%=String.format("%.2f", row[j].getDivPrice()*(stockL.get(i).getHoldings()))%></td>
        <%
            }
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
