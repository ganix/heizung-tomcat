<%@page import="de.ganix.heizung.Config"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="cfg" scope="application" class="de.ganix.heizung.Config" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=340">
<script src="jquery-1.7.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="heizung.css">
<title>Willkommen zur Heizung Webapp</title>
<script type="text/javascript">
var to = 1000 * <%= ((Config)application.getAttribute("cfg")).getInterval() %>;
function refresh () {
	$.getJSON("jsonpoller", function(json) {
		$("#indoor").text(json.indoor);
		$("#outdoor").text(json.outdoor);
		$("#switch1").text(json.switch1);
		$("#page_date").text(json.date);
	});
	if (document.images) {
	    var image = document.images.chart;
        var new_image = new Image();
        //set up the new image
        new_image.id = "chart";
        new_image.src = image.src;
        new_image.onload = function() {
        	// insert new image and remove old
        	image.parentNode.insertBefore(new_image, image);
        	image.parentNode.removeChild(image);
        };
	}	
	setTimeout("refresh()", to );
}
setTimeout("refresh()", to );
</script>
</head>
<body>
	<jsp:useBean id="temp" class="de.ganix.heizung.Temperature" />
	<table class="reftable" cellpadding="5" cellspacing="0" border="1">
		<tr>
			<th colspan="2">Heizung Webapp - <span id="page_date"><fmt:formatDate type="both" value="<%= new java.util.Date() %>"></fmt:formatDate></span>
			</th>
		</tr>
		<tr>
			<td class="tabxpl" align="left">Raumtemperatur:</td>
			<td class="tabxpl" align="left">
				<div id="indoor">
					<c:choose>
						<c:when test="${not empty temp.innen}" >
							<fmt:formatNumber value="${temp.innen}" maxFractionDigits="2"  minFractionDigits="2" type="number" />°C
						</c:when>
						<c:otherwise>n/a</c:otherwise>
					</c:choose>
				</div>
			</td>
		</tr>
		<tr>
			<td class="tabxpl" align="left">Außentemperatur:</td>
			<td class="tabxpl" align="left">
				<div id="outdoor">
					<c:choose>
						<c:when test="${not empty temp.aussen}" >
							<fmt:formatNumber value="${temp.aussen}" maxFractionDigits="2"  minFractionDigits="2" type="number" />°C
						</c:when>
						<c:otherwise>n/a</c:otherwise>
					</c:choose>
				</div>
			</td>
		</tr>
		<tr>
			<td class="tabxpl" align="left">Ausgang1:</td>
			<td class="tabxpl" align="left">
				<jsp:useBean id="ausgang" class="de.ganix.heizung.Ausgang" />
				<jsp:setProperty name="ausgang" property="*"/>		
				<form action="index.jsp" method="post">
					<span id="switch1">
					<jsp:getProperty name="ausgang" property="ausgang1"/>
					</span>
					<input type="submit" name="ausgang1" value="an"/>
					<input type="submit" name="ausgang1" value="aus"/>
				</form>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="tabxpl" align="center">
			<img alt="chart" name="chart" src="temperaturechart">
			</td>
		</tr>
		<tr>
			<td colspan="2" class="tabxpl">Benutzer <%=request.getUserPrincipal().getName()%>
			<a href="logoff.jsp">abmelden</a> | zu den <a href="config.jsp">Einstellungen</a></td>
		</tr>
	</table>
</body>
</html>