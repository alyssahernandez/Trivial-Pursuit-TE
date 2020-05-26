<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>




<div>

	<h2><c:out value="Create a Game!" /></h2>



	<c:url var="createGameURL" value="/create" />

	<form action="${createGameURL }" method="POST">
		<!--  
		<label for="nickname">Nickname:</label>
		<input type="text" name="nickname" />
		
		<label for="gameCode">Game Code:</label> 
		<input type="text" name="gameCode" />
		-->
		<span>Categories:</span>
		<c:forEach var="category" items="${categories }">
			<input type="checkbox" name="categorySelection" id="${category.categoryName }" value="${category.categoryId }" />
			<label for="${category.categoryName }">${category.categoryName }</label> 
		</c:forEach>
		
		<input type="submit" class="button" value="Start Game!" />

	</form>

</div>

<style>
#invite {
	border: 1px solid white;
}
</style>


