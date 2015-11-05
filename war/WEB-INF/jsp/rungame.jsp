<%@page import="org.grape.galaxy.game.GalaxyGame"%>
<%@page import="org.grape.galaxy.game.GalaxyGames"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xml:lang="ru" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="content-language" content="ru" />
<%
	GalaxyGame game = GalaxyGames.getGalaxyGame(request
			.getParameter("id"));
	if (game == null) {
		throw new Exception("Game " + request.getParameter("id")
				+ " not found");
	}
	String quality = request.getParameter("quality");
	String qualityDesc;
	if ("low".equals(quality)) {
		qualityDesc = "низкое";
	} else if ("mid".equals(request.getParameter("quality"))) {
		qualityDesc = "высокое";
	} else {
		qualityDesc = "среднее";
	}
%>
<meta name="description"
	content="Запуск игры <%=game.getName()%> (<%=qualityDesc%> качество)" />
<meta name="keywords" content="<%=game.getKeywords()%>" />
<title><%=game.getName()%></title>
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
<script type="text/javascript" src="http://www.java.com/js/deployJava.js"></script>
</head>
<body>
	<script type="text/javascript">
		var attributes = {
			width : '720',
			height : '540'
		};
		var parameters = {
			jnlp_href : '/games/<%=game.getId()%>.jnlp',
			quality : '<%=quality%>',
			java_arguments : '-Dsun.java2d.noddraw=true',
			separate_jvm : 'true',
			browser_name : navigator.appName,
			browser_version : navigator.appVersion,
			user_agent : navigator.userAgent
		};
		deployJava.runApplet(attributes, parameters, '1.6');
	</script>
	<noscript>
		<p style="font-size: larger; color: red">
			Для запуска игры <%=game.getName()%> необходимо включить поддержку JavaScript в браузере
		</p>
	</noscript>
</body>
</html>