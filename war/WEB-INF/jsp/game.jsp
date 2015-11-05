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
%>
<meta name="description" content="<%=game.getDescription()%>" />
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
</head>
<body>
	<h1><%=game.getName()%></h1>
	<img src="/images/<%=game.getId()%>.png" width="341" height="256"
		alt="Скриншот <%=game.getName()%>" />
	<h2>Описание игры</h2>
	<p><%=game.getDescription()%></p>
	<h2>Управление</h2>
	<p><%=game.getControls()%></p>
	<h2>Запуск игры</h2>
	<p>Необходимым условием для запуска игры является наличие среды
		исполнения Java (JRE). Аналогично, как для запуска Flash приложений
		требуется Flash плеер. Если JRE нужной версии и плагин ее интеграции в
		браузер не будут найдены, произойдет автоматическое перенаправление на
		сайт разработчиков Java для установки JRE.</p>
	<p>
		<a href="/rungame?id=<%=game.getId()%>&quality=low"><img
			style="border-width: 0" src="/images/startlow.png" width="200"
			height="90" alt="Запуск (низкое качество)" />
		</a><a href="/rungame?id=<%=game.getId()%>&quality=mid"><img
			style="border-width: 0" src="/images/startmid.png" width="200"
			height="90" alt="Запуск (среднее качество)" />
		</a><a href="/rungame?id=<%=game.getId()%>&quality=hi"><img
			style="border-width: 0" src="/images/starthi.png" width="200"
			height="90" alt="Запуск (высокое качество)" />
		</a>
	</p>
</body>
</html>