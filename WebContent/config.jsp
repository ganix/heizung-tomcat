<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=340">
<jsp:useBean scope="application" id="cfg" class="de.ganix.heizung.Config" />
<jsp:useBean scope="request" id="validation" class="de.ganix.heizung.ConfigValidation">
	<jsp:setProperty name="validation" property="interval" value="${cfg.interval}"/>
	<jsp:setProperty name="validation" property="maxGraphSamples" value="${cfg.maxGraphSamples}"/>
	<jsp:setProperty name="validation" property="setpointTemperature" value="${cfg.setpointTemperature}"/>
</jsp:useBean>
<jsp:setProperty name="validation" property="*" />
<link rel="stylesheet" type="text/css" href="heizung.css">
<title>Willkommen zur Heizung Webapp</title>
</head>
<body>
	<form action="config.jsp" method="post">
		<table class="reftable" cellpadding="5" cellspacing="0" border="1">
			<tr>
				<th colspan="2">Konfiguration</th>
			</tr>
			<tr>
				<td class="tabxpl" align="left">Regelintervall (s):</td>
				<td class="tabxpl" align="center">
					<c:choose>
						<c:when test="${validation.valid and empty param.intervalError}">
	       					<jsp:setProperty name="cfg" property="interval" />
							<input type="text" name="interval" value="${cfg.interval}" />
						</c:when>
						<c:when test="${not validation.valid}">
							<input type="text" name="interval" value="${validation.interval}" /><br>
	     					<font color="red"><c:out value="${validation.intervalError}"/></font>
	     				</c:when>
     				</c:choose>				
				</td>
			</tr>
			<tr>
				<td class="tabxpl" align="left">Werte im Graph:</td>
				<td class="tabxpl" align="center">
					<c:choose>
						<c:when test="${validation.valid and empty param.maxGraphSamplesError}">
	       					<jsp:setProperty name="cfg" property="maxGraphSamples" />
							<input type="text" name="maxGraphSamples" value="${cfg.maxGraphSamples}" />
						</c:when>
						<c:when test="${not validation.valid}">
							<input type="text" name="maxGraphSamples" value="${validation.maxGraphSamples}" /><br>
	     					<font color="red"><c:out value="${validation.maxGraphSamplesError}"/></font>
	     				</c:when>
     				</c:choose>
				</td>
			</tr>			
			<tr>
				<td class="tabxpl" align="left">Solltemperatur (°C):</td>
				<td class="tabxpl" align="center">
					<c:choose>
						<c:when test="${validation.valid and empty param.setpointTemperatureError}">
	       					<jsp:setProperty name="cfg" property="setpointTemperature" />
							<input type="text" name="setpointTemperature" value="${cfg.setpointTemperature}" />
						</c:when>
						<c:when test="${not validation.valid}">
							<input type="text" name="setpointTemperature" value="${validation.setpointTemperature}" /><br>
	     					<font color="red"><c:out value="${validation.setpointTemperatureError}"/></font>
	     				</c:when>
     				</c:choose>
				</td>
			</tr>			
			<tr>
				<td colspan="2" class="tabxpl" align="center">
					<c:if test="${validation.valid and not empty param.action and param.action eq 'save'}">
       					<% cfg.apply(); cfg.persist(); %>
					</c:if>
					<input type="submit" value="save" name="action" />
				</td>
			</tr>
			<tr>
				<td colspan="2" class="tabxpl">Benutzer <%=request.getUserPrincipal().getName()%>
					<a href="logoff.jsp">abmelden</a> | zur <a href="index.jsp">Hauptseite</a></td>
			</tr>
		</table>
	</form>
</body>
</html>