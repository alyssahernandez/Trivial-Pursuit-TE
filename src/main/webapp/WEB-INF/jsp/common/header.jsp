<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
	<head>
	    <meta charset="UTF-8">
	    
		<title>Programmers' Pursuit</title>
		
		<c:url var="stylesheetHref" value="/css/main.css" />
		<link rel="stylesheet" href="${stylesheetHref}">
		
		<c:url var="faviconHref" value="/img/favicon.png" />
		<link rel="icon" type="image/png" href="${ faviconHref }" sizes="16x16">
		
		<c:url var="jsURL" value="/js/script.js" />
		<script src="${ jsURL }"></script>
	</head>
	<body>

		<header class="header">

            <c:url var="homepageURL" value="/"/>
            <h1 class="header-main"><a href="${ homepageURL }">Programmers' Pursuit</a></h1>
            
			<ul class="header__right-nav">
				<c:url var="profilePicURL" value="${currentUser.picture }"/>
				<img class="header__right-nav--pic" src="${ profilePicURL }">
			
                <c:url var="profileURL" value="/profile"/>
                <li><a class="header__right-nav--link link header-link" id="profile" href="${profileURL}">Your Games</a></li>
            </ul>
            
		</header>
		<main class="main-content">