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

</head>
<body>
<div class="navoverall" style="margin-bottom:20px;">
    <h2 class="sera">SERA</h2>
    <div class = "nav">
        <a href="home">Home</a>
        <a href="portfolio">Portfolio</a>
        <a href="income">Income</a>
        <a href="profile" style="font-weight:bold">Profile</a>
    </div>
    <a href="login.jsp" class="logout">LOGOUT</a>
</div>

    <div style="position: absolute;
    /*top: 45%;*/
    /*transform: translateY(-50%); */
    width: 100%;">
        <%
            if (request.getAttribute("message")!=null){
                if(request.getAttribute("message").toString()=="Portfolio update success!")
                {
        %>
        <%--    https://www.w3schools.com/howto/howto_js_alert.asp--%>
        <%--    https://www.w3schools.com/howto/tryit.asp?filename=tryhow_js_alerts--%>
        <div class="alert success" >
            <span class="closebtn">&times;</span>
            <strong>Success!</strong> <%=request.getAttribute("message").toString()%>
        </div>
        <script>
            var close = document.getElementsByClassName("closebtn");
            var i;

            for (i = 0; i < close.length; i++) {
                close[i].onclick = function(){
                    var div = this.parentElement;
                    div.style.opacity = "0";
                    setTimeout(function(){ div.style.display = "none"; }, 600);
                }
            }
        </script>
        <%
            } else{
        %>
        <div class="alert warning" >
            <span class="closebtn">&times;</span>
            <strong>FAIL : </strong> <%=request.getAttribute("message").toString()%>
        </div>
        <script>
            var close = document.getElementsByClassName("closebtn");
            var i;

            for (i = 0; i < close.length; i++) {
                close[i].onclick = function(){
                    var div = this.parentElement;
                    div.style.opacity = "0";
                    setTimeout(function(){ div.style.display = "none"; }, 600);
                }
            }
        </script>

        <%
            }
         }
        %>
        <div style="display:inline-flex">
            <img src="img/settingsgifgreen.gif" style="display:inline;position: absolute; left:30%; transform: translate(-50%, 38%);" width="450px">
            <div style="position:absolute;left:63%; transform: translate(-50%, 100%); text-align:left;" class="animate-bottom" >
                <div class = "profilediv">
<%--                    <img src="img/seranopadding.png" height="35px" style="padding-right:50px; display:inline; vertical-align:middle;">--%>
                    <span>Stock portfolio CSV template: </span>
                    <a href="https://docs.google.com/spreadsheets/d/e/2PACX-1vQ0ag8m4jcwRRM7k48gFWRxTM_tX8XgrzSlk5Jmg-9vyQq1bFh1iZB9gCvceLoPhzcOw7d9xNQNXqrg/pub?output=csv" class="csvlink">Download</a>
                </div>
                <div class="profilediv" >
<%--                    <img src="img/seranopadding.png" height="35px" style="padding-right:50px; display:inline; vertical-align:middle;">--%>
                    <span>Upload CSV (portfolio): &nbsp; </span>
                    <%--form to upload the CSV file as a multipart--%>
                    <form id="uploadcsv" method="post" action="profile" enctype="multipart/form-data" onsubmit="return csvload()" style="display:inline; overflow:hidden;">
                        <input style="height: 25px; font-size:15px; margin-right:-130px;overflow:hidden;" type="file" name="file" />
                        <input class="csvlink" type="submit" value="Upload" />
                    </form>
                    <div class="loader" id="csvloader" style="display:none; width:20px;height:20px;border: 5px solid #f3f3f3;
    border-radius: 50%;
    border-top: 5px solid #caefd0; margin-left:10px;" ></div>
                </div>

                <div class="profilediv">
<%--                    <img src="img/seranopadding.png" height="35px" style="padding-right:50px; display:inline; vertical-align:middle;">--%>
                    <span>Select default currency: &nbsp;</span>
                    <form id="selectC" action="profile" method="GET" onsubmit="return curload()" style="display:inline;">
                        <select name="selectCur" style="height: 25px; font-size:15px;">
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

                        </select>
                        <button type="submit" class="csvlink">Enter</button>
                    </form>
                    <div class="loader" style="display:none; width:30px; height:30px;" id="curloader"></div>
                </div>

            </div>
        </div>
        </div>





    <script>
        function csvload() {
            document.getElementById("csvloader").style.display = "inline-block";
            document.getElementById("uploadcsv").style.display = "none";
        }
        function curload() {
            document.getElementById("curloader").style.display = "inline-block";
            document.getElementById("selectC").style.display = "none";
        }
    </script>
</body>
</html>
