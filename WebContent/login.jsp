<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
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
			Bitte geben Sie Ihre Zugangsdaten ein:
			</th>
		</tr>
		<tr>
			<td class="tabxpl" align="right">
				<form action='<%=response.encodeURL("j_security_check")%>'
					method="POST">
					Benutzername:<input type="text" name="j_username" /><br>
					Passwort:<input	type="password" name="j_password" /><br>
					<input type="submit" value="Login" />
				</form>
			</td>
		</tr>
	</table>
</body>
</html>