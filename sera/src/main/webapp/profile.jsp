<%@ page import="sera.sera.User" %><%--
  Created by IntelliJ IDEA.
  User: sonja
  Date: 31/5/2023
  Time: 12:56 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Profile</title>
    <link rel="stylesheet" href="css/tables.css">
    <div class="navoverall">
        <h2 class="sera">SERA</h2>
        <div class = "nav">
            <a href="home">Home</a>
            <a href="portfolio">Portfolio</a>
            <a href="income">Income</a>
            <a href="profile" class="bold">Profile</a>
        </div>
        <a href="login.jsp" class="logout">LOGOUT</a>
    </div>
</head>
<body>
    <div class = "profilediv" >
        <h3 style="display: inline;">Stock portfolio CSV template: </h3>
        <a href="https://docs.google.com/spreadsheets/d/e/2PACX-1vS6nm9PraoIMzKQdCL_XkvKL6impJc_Fmoi6PF-OyPmLWpwvnberTP7dqqroI7RX6iNGQVGV3spHRJl/pub?output=csv" class="csvlink">Download</a>
    </div>
    <div class="profilediv">
        <h3>Hi</h3>
    </div>
    <div class="profilediv">
        <h3>Hi</h3>
        <div>
            <form id="selectC" action="profile" method="GET">
                <select name="selectCur" >
                    <%String cur = ((User)request.getSession().getAttribute("user")).getDefaultCurrency();
                    String[] currencies = new String[]{"HKD", "USD", "EUR", "JPY", "CNY", "GBP", "AUD", "CAD", "CHF", "SGD", "NZD", "KRW"};
                    for (String c:currencies){
                        if (c.equals(cur)){

                        %>
                              <option value="<%=c%>" selected><%=c%></option>
                    <%
                        } else{
                    %>        <option value="<%=c%>"><%=c%></option>
                    <%
                        }
                    }%>
<%--                    <option value="HKD">HKD</option>--%>
<%--                    <option value="USD">USD</option>--%>
<%--                    <option value="EUR">EUR</option>--%>
<%--                    <option value="JPY">JPY</option>--%>
<%--                    <option value="CNY">CNY</option>--%>
<%--                    <option value="GBP">GBP</option>--%>
<%--                    <option value="AUD">AUD</option>--%>
<%--                    <option value="CAD">CAD</option>--%>
<%--                    <option value="CHF">CHF</option>--%>
<%--                    <option value="SGD">SGD</option>--%>
<%--                    <option value="NZD">NZD</option>--%>
<%--                    <option value="KRW">KRW</option>--%>
                </select>
                <button type="submit">Enter</button>
            </form>

        </div>
    </div>
</body>
</html>
