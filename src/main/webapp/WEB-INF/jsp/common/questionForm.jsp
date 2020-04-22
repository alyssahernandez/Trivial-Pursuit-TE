<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url var="questionFormURL" value="/question/${ currentGame.gameCode }"/>

<div class="question">
	<c:choose>
		<c:when test="${currentPlayerSpace.center }">
		
			<c:choose>		
				<c:when test="${currentGame.hasActivePlayerSelectedCategory eq false }">
				
					<c:choose>
						<c:when test="${currentPlayerTurn.allPies eq true }">
						
							<c:out value="Congrats, ${currentPlayerTurn.name }. All other players: Choose a category for ${currentPlayerTurn.name }'s chance to win." />
							
						</c:when>
						<c:otherwise>
						
							<c:out value="${currentPlayerTurn.name }, choose a category." />
							
						</c:otherwise>
					</c:choose>
				
					<form action="${questionFormURL }" method="POST" class="question-form">				
						<c:forEach var="cat" items="${gameCategories }">
							<label class="question-form_text" for="categoryChoiceId">
								<input type="radio" name="categoryChoiceId" value="${cat.categoryId }" />${cat.categoryName }
							</label>
						</c:forEach>
						<input type="hidden" name="chosenCenterSpaceCategory" value="true" />
						<input type="submit" value="Submit"/>					
					</form>	
							
				</c:when>		
				<c:otherwise>	
				
					<form action="${ questionFormURL }" method="POST" class="question-form">
						<label class="question-form__text" for="answer">${question.question}
							<c:forEach var="answer" items="${possibleAnswers}">
								<input class="question-form__answer" name="answer" type="radio" value="${answer}"/>${answer}
							</c:forEach>
						</label>
						<input type="submit" value="Submit"/>	
					</form>
						
					<!-- TODO: @Jeff: styling for the answers - left this commented out for reference.
					the other question form below is the form for answers - just in two places for
					conditional logic purposes - lmk if you have questions - ALYSSA
					
					<form action="${ questionFormURL }" method="POST" class="question-form">
						<label class="question-form__text" for="answer">${question.question }</label>
						<input class="question-form__answer" name="answer" type="text" placeholder="Enter your answer here..."/>						
						<input class="question-form__button" type="submit" value="Submit Answer"/>
					</form>
						
					 -->
					 
				</c:otherwise>		
			</c:choose>	
			
		</c:when>	
		<c:otherwise>	
			<form action="${ questionFormURL }" method="POST" class="question-form">
				<label class="question-form__text" for="answer">${question.question}
					<c:forEach var="answer" items="${possibleAnswers}">
					<input class="question-form__answer" name="answer" type="radio" value="${answer }"/>${answer}
					</c:forEach>
				</label>
				<input type="submit" value="Submit"/>
			</form>
		</c:otherwise>	
	</c:choose>
</div>
