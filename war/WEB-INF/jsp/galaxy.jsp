<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xml:lang="ru" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="content-language" content="ru" />
<meta name="description"
	content="Галактика. Бесплатная многопользовательская 3D игра. Стратегия реального времени. Запускается в браузерах, поддерживающих технологию WebGL (Chrome, Firefox и др.). Не требует плагинов." />
<meta name="keywords"
	content="free browser game, бесплатная браузерная игра, browser 3d game, браузерная 3d игра, MMO strategy, многопользовательская стратегия, MMO RTS browser 3d game, многопользовательская браузерная 3d стратегия, 3d, MMO, RTS, HTML5, WebGL, Google App Engine, GAE, Google Web Toolkit, GWT, strategy, стратегия, galaxy, галактика, grape galaxy, free galaxy 3d, google app engine galaxy, gae galaxy, google web toolkit galaxy, gwt galaxy, gae gwt galaxy, gae gwt grape" />
<title>Галактика</title>
<link type="text/css" rel="stylesheet" href="/css/common.css" />
<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon" />
<%
	UserService userService = UserServiceFactory.getUserService();
	if (userService.isUserLoggedIn()) {
%>
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
<script type="text/javascript" language="javascript"
	src="/galaxy/galaxy.nocache.js"></script>
<script type="text/javascript" src="/o3d/o3d-webgl/base.js"></script>
<script type="text/javascript" src="/o3d/o3djs/base.js"></script>
<script type="text/javascript" id="o3dscript">
	o3djs.base.o3d = o3d;
	o3djs.require('o3djs.webgl');
	o3djs.require('o3djs.math');
	o3djs.require('o3djs.quaternions');
	o3djs.require('o3djs.rendergraph');
	o3djs.require('o3djs.scene');
	o3djs.require('o3djs.pack');
	o3djs.require('o3djs.io');
	o3djs.require('o3djs.picking');
	o3djs.require('o3djs.primitives');
	o3djs.require('o3djs.particles');
	var g_o3d;
	var g_math;
	var g_client;
	var g_pack;
	var g_viewInfo;
	var g_globalParams;
	window.onunload = function() {
		if (g_client) {
			// Removes any callbacks so they don't get called after the page has unloaded
			g_client.cleanup();
		}
	};
</script>
<%
	}
%>
</head>
<body>
	<div class="icons">
		<a href="http://feeds.feedburner.com/grape-galaxy" rel="alternate"
			type="application/rss+xml"><img src="/images/feed-28x28.png"
			alt="Подписаться" title="Подписаться" /> </a> <a
			href="/res/galaxy/galaxy-gadget.gg"><img
			src="/images/gg-28x28.png" alt="Google Desktop Gadget"
			title="Google Desktop Gadget" /> </a> <a href="/help/help.html"><img
			src="/images/help.png" alt="Как играть?" title="Как играть?"
			width="28" height="28" /> </a>
	</div>
	<%
		if (userService.isUserLoggedIn()) {
	%>
	<div class="ladingWrapper">
		<div class="gae">
			<img src="/images/appengine-silver-120x30.gif"
				alt="Технология Google App Engine" />
		</div>
		<jsp:include page="inner/eula.jsp" />
		<jsp:include page="inner/shaders.jsp" />
	</div>
	<%
		} else {
	%>
	<div class="body">
		<div class=topimage>
			<div class="header">
				<h1>Галактика</h1>
			</div>
			<div class="loginPanel">
				<div class="login">
					Пожалуйста, <a href="<%=userService.createLoginURL("/")%>">войдите</a>
					через аккаунт Google.
				</div>
			</div>
		</div>
	</div>
	<%
		}
	%>
</body>
</html>