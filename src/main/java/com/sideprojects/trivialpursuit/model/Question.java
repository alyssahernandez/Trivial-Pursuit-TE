package com.sideprojects.trivialpursuit.model;

//AC: all questions will have these getters to get the ID, the String question, and the String answer wherever you want them
public class Question {
	private Integer questionID;
	private Integer categoryID;
	private String question;
	private String answer;
	
	public Question() {}
	
	public Integer getQuestionID() {return questionID;}
	public void setQuestionID(Integer questionID) {this.questionID = questionID;}
	
	public Integer getCategoryID() {return categoryID;}
	public void setCategoryID(Integer categoryID) {this.categoryID = categoryID;}
	
	public String getQuestion() { return question; }
	public void setQuestion(String question) {this.question = question;}
	
	public String getAnswer() {return answer;}
	public void setAnswer(String answer) {this.answer = answer;}
}
