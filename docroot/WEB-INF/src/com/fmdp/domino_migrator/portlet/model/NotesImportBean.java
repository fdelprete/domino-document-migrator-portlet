package com.fmdp.domino_migrator.portlet.model;


import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;


public class NotesImportBean {

	
	String serverName = "";
	String notesDatabase = "";
	String notesView = "";
	String notesFieldWithAttachments = "";
	String notesFieldWithTags = "";
	String notesFieldWithCategories = "";
	String notesFieldWithDescr = "";
	String notesFieldWithTitle = "";
	String vocabularyName = "";
	int documentsImported = 0;
	int documentsWithProblem = 0;
	int totalDocuments = 0;
	int totalAttachments = 0;
	
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public String getNotesDatabase() {
		return notesDatabase;
	}

	public void setNotesDatabase(String notesDatabase) {
		this.notesDatabase = notesDatabase;
	}

	public String getNotesView() {
		return notesView;
	}

	public void setNotesView(String notesView) {
		this.notesView = notesView;
	}

	public String getNotesFieldWithAttachments() {
		return notesFieldWithAttachments;
	}

	public void setNotesFieldWithAttachments(String notesFieldWithAttachments) {
		this.notesFieldWithAttachments = notesFieldWithAttachments;
	}

	public String getNotesFieldWithTags() {
		return notesFieldWithTags;
	}

	public void setNotesFieldWithTags(String notesFieldWithTags) {
		this.notesFieldWithTags = notesFieldWithTags;
	}

	public String getNotesFieldWithCategories() {
		return notesFieldWithCategories;
	}

	public void setNotesFieldWithCategories(String notesFieldWithCategories) {
		this.notesFieldWithCategories = notesFieldWithCategories;
	}

	public String getNotesFieldWithDescr() {
		return notesFieldWithDescr;
	}

	public void setNotesFieldWithDescr(String notesFieldWithDescr) {
		this.notesFieldWithDescr = notesFieldWithDescr;
	}

	public String getNotesFieldWithTitle() {
		return notesFieldWithTitle;
	}

	public void setNotesFieldWithTitle(String notesFieldWithDescr) {
		this.notesFieldWithTitle = notesFieldWithTitle;
	}

	public String getVocabularyName() {
		return vocabularyName;
	}

	public void setVocabularyName(String vocabularyName) {
		this.vocabularyName = vocabularyName;
	}

	public int getDocumentsImported() {
		return documentsImported;
	}

	public void setDocumentsImported(int documentsImported) {
		this.documentsImported = documentsImported;
	}

	public int getDocumentsWithProblem() {
		return documentsWithProblem;
	}

	public void setDocumentsWithProblem(int documentsWithProblem) {
		this.documentsWithProblem = documentsWithProblem;
	}


	public int getTotalDocuments() {
		return totalDocuments;
	}

	public void setTotalDocuments(int totalDocuments) {
		this.totalDocuments = totalDocuments;
	}

	public int getTotalAttachments() {
		return totalAttachments;
	}

	public void setTotalAttachments(int totalAttachments) {
		this.totalAttachments = totalAttachments;
	}

	public JSONArray getNotesImportBeanJSONArray() {

			JSONArray notesImportBeanJSONArray = JSONFactoryUtil.createJSONArray();

			JSONObject importPropertyJSONObject =
				JSONFactoryUtil.createJSONObject();

			importPropertyJSONObject.put(
					"documentsImported", this.documentsImported);
			importPropertyJSONObject.put(
					"documentsWithProblem", this.documentsWithProblem);
			importPropertyJSONObject.put(
					"totalAttachments", this.totalAttachments);
			importPropertyJSONObject.put(
					"totalDocuments", this.totalDocuments);
			importPropertyJSONObject.put(
					"serverName", this.serverName);
			importPropertyJSONObject.put(
					"notesDatabase", this.notesDatabase);
			importPropertyJSONObject.put(
					"notesView", this.notesView);
			importPropertyJSONObject.put(
					"notesFieldWithAttachments", this.notesFieldWithAttachments);
			importPropertyJSONObject.put(
					"notesFieldWithTags", this.notesFieldWithTags);
			importPropertyJSONObject.put(
					"notesFieldWithCategories", this.notesFieldWithCategories);
			importPropertyJSONObject.put(
					"notesFieldWithDescr", this.notesFieldWithDescr);
			importPropertyJSONObject.put(
					"notesFieldWithTitle", this.notesFieldWithTitle);
			importPropertyJSONObject.put(
					"vocabularyName", this.vocabularyName);
			
			
			notesImportBeanJSONArray.put(importPropertyJSONObject);

			return notesImportBeanJSONArray;
		}

	@Override
	public String toString() {
		return "UserBean [serverName=" + serverName + ", notesDatabase="
				+ notesDatabase 
				+ ", notesView=" + notesView
				+ ", notesFieldWithAttachments=" + notesFieldWithAttachments
				+ ", notesFieldWithTags=" + notesFieldWithTags
				+ ", notesFieldWithCategories=" + notesFieldWithCategories
				+ ", notesFieldWithDescr=" + notesFieldWithDescr
				+ ", notesFieldWithTitle=" + notesFieldWithTitle
				+ ", vocabularyName=" + vocabularyName
				+ ", documentsImported=" + documentsImported
				+ ", documentsWithProblem=" + documentsWithProblem
				+ ", totalDocuments=" + totalDocuments
				+ ", totalAttachments=" + totalAttachments + "]";
	}

}
