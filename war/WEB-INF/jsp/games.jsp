<%@page import="org.grape.galaxy.game.GalaxyGame"%>
<%@page import="org.grape.galaxy.game.GalaxyGames"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xml:lang="ru" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="content-language" content="ru" />
<meta name="description" content="Бесплатные браузерные 3D игры" />
<meta name="keywords" content="бесплатные браузерные 3d игры" />
<title>Бесплатные браузерные 3D игры</title>
<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon" />
<script type="text/javascript">
	var _gaq = _gaq || [];
	_gaq.push([ '_setAccount', 'UA-18556866-2' ]);
	_gaq.push([ '_trackPageview' ]);
	(function() {
		var ga = document.createElement('script');
		ga.type = 'text/javascript';
		ga.async = true;
		ga.src = ('https:' == document.location.protocol ? 'https://ssl'
				: 'http://www')
				+ '.google-analytics.com/ga.js';
		var s = document.getElementsByTagName('script')[0];
		s.parentNode.insertBefore(ga, s);
	})();
</script>
</head>
<body>
	<h1>Бесплатные браузерные 3D игры</h1>
	<%
		for (GalaxyGame game : GalaxyGames.getGalaxyGames()) {
	%>
	<h2><%=game.getName()%></h2>
	<a href="/game?id=<%=game.getId()%>"><img
		src="/images/<%=game.getId()%>.png" width="341" height="256"
		alt="Скриншот <%=game.getName()%>" /> </a>
	<p><%=game.getDescription()%>
		<a href="/game?id=<%=game.getId()%>">Подробнее...</a>
	</p>
	<%
		}
	%>
</body>
</html>