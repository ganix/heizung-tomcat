<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=320">
<link rel="stylesheet" type="text/css" href="heizung.css">
<title>Login zur Heizung Webapp</title>
</head>
<body>
	<table class="reftable" cellpadding="5" cellspacing="0" border="1">
		<tr>
			<th>
			Benutzer <%= request.getUserPrincipal().getName() %> ist abgemeldet!
			<%session.invalidate(); application.log("session logoff: " + request.getUserPrincipal().getName());%>	
			</th>
		</tr>
		<tr>
			<td class="tabxpl" align="right">
			Sie können sich erneut anmelden: <a href="./"><b>Login</b></a>
			</td>
		</tr>
	</table>
</body>
</html>

