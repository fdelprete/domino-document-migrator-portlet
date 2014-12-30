package com.fmdp.domino_migrator.portlet.model;


public class NotesImportBean {

	
	String serverName = "";
	String notesDatabase = "";
	String notesView = "";
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

	@Override
	public String toString() {
		return "UserBean [serverName=" + serverName + ", notesDatabase="
				+ notesDatabase + ", notesView=" + notesView + ", documentsImported=" + documentsImported
				+ ", documentsWithProblem=" + documentsWithProblem
				+ ", totalDocuments=" + totalDocuments
				+ ", totalAttachments=" + totalAttachments + "]";
	}

}
