<%-- 
    Document   : exchangerdashboard
    Created on : Dec 27, 2016, 11:35:53 PM
    Author     : Milan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
    </head>
    <body>
        <c:if test="${outcomeMessage!=null}">
            <p><c:out value="${outcomeMessage}" /></p>
        </c:if>

        <c:if test="${loggedAccount==null}">
            <%
                response.sendRedirect("http://localhost:8080/exchangomatic/exchangerlogin");
            %>
        </c:if>

        <a href="http://localhost:8080/exchangomatic/exchangerlogin">Log out</a>

        <h1>Exchanger Dashboard</h1>
        <h3>Transaction Request ID's:</h3>
        <div id="requestsDiv"></div>
        <h2>Send Offer</h2>
        <input type="text" id="requestId" placeholder="Enter Transaction Request ID" /><br>
        <input type="text" id="offerRate" placeholder="Enter Rate" /><br>

        <button onclick="sendTransactionOffer()">Send Offer</button>

        <script>
            function fetchTransactionRequests() {  
                document.getElementById("requestsDiv").innerHTML = "";
                var xhr = new XMLHttpRequest();
                xhr.open("post", "http://localhost:8080/exchangomatic/fetchrequests", false);
                var exchanger = {};
                exchanger.id = "${loggedAccount.id}";
                exchanger.name = "${loggedAccount.name}";
                exchanger.password = "${loggedAccount.password}";
                xhr.setRequestHeader("Content-Type", "application/json");
                xhr.send(JSON.stringify(exchanger));
                if(xhr.responseText === "[]"){ //prevents the function from trying to populate the div if the response is empty (i.e. there are no pending requests)
                    return;
                }
                var transactionRequests = eval("(" + xhr.responseText + ")");
                for (var i = 0; i < transactionRequests.length; i++) { //this loop is here only for the demo, get rid of it afterwards
                    //all transaction requests are available in this array - transactionRequests
                    //Front-enders will need to figure out a way to properly display the info (muhahahah, good luck!)
                    $("#requestsDiv").append("request id: " + transactionRequests[i].id);
                    $("#requestsDiv").append(" - client email: " + transactionRequests[i].client.email);
                    $("#requestsDiv").append(" - amount: " + transactionRequests[i].amount);
                    $("#requestsDiv").append(" - rate: " + transactionRequests[i].rate);
                    $("#requestsDiv").append(" - already offered: " + transactionRequests[i].alreadyOffered);
                    //@MARKO (by milan): the alreadyOffered property (true/false) tells us whether this particular exchanger has already sent an offer to this request
                    //you may want to make such requests "grayed out", or you can just choose to make them invisible
                    //example: every X seconds when Exchanger's dashboard refreshes, it will show all transaction requests that they can send offers to; the array will also contain the requests that the Exchanger has already sent an offer to, so you can decide what you want to do with those (maybe give them another color like Dejan suggested)
                    $("#requestsDiv").append("<br>");  
                }
            }

            function sendTransactionOffer() {
                //fetching specific request first
                var xhr = new XMLHttpRequest(); //TODO: switch to JQ entirely
                xhr.open("post", "http://localhost:8080/exchangomatic/fetchrequest/" + $("#requestId").val(), false);
                exchanger = {};
                exchanger.id = "${loggedAccount.id}";
                exchanger.name = "${loggedAccount.name}";
                exchanger.password = "${loggedAccount.password}";
                xhr.setRequestHeader("Content-Type", "application/json");
                xhr.send(JSON.stringify(exchanger));
                var transactionRequest = eval("(" + xhr.responseText + ")");
                //IMPORTANT: check if transactionRequest exists in the firstplace; make sure to update the endpoint so it returns some kind of notice if the request has expired
                var transactionOffer = {};
                transactionOffer.amount = transactionRequest.amount;
                transactionOffer.rate = document.getElementById("offerRate").value;
                var exchanger = {};
                exchanger.id = ${loggedAccount.id};
                exchanger.name = "${loggedAccount.name}";
                exchanger.password = "${loggedAccount.password}";
                transactionOffer.client = transactionRequest.client;
                transactionOffer.exchanger = exchanger;
                transactionOffer.transactionRequest = transactionRequest;

                var xhr = new XMLHttpRequest();
                xhr.open("post", "http://localhost:8080/exchangomatic/savetransactionoffer", false);
                xhr.setRequestHeader("Content-Type", "application/json");
                xhr.send(JSON.stringify(transactionOffer));

            }
            
            setInterval(fetchTransactionRequests, 5000);
        </script>
    </body>
</html>
