package com.fmdp.domino_migrator.portlet.model;

import java.util.Date;

public class NotesImportBean {

	String serverName;
	String notesDatabase;
	String notesView;
	long documentsImported;
	long documentsWithProblem;
	long totalDocuments;
	long totalAttachments;
	Date importDate;
	
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

	public long getDocumentsImported() {
		return documentsImported;
	}

	public void setDocumentsImported(long documentsImported) {
		this.documentsImported = documentsImported;
	}

	public long getDocumentsWithProblem() {
		return documentsWithProblem;
	}

	public void setDocumentsWithProblem(long documentsWithProblem) {
		this.documentsWithProblem = documentsWithProblem;
	}

	public Date getImportDate() {
		return importDate;
	}

	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}

	public long getTotalDocuments() {
		return totalDocuments;
	}

	public void setTotalDocuments(long totalDocuments) {
		this.totalDocuments = totalDocuments;
	}

	public long getTotalAttachments() {
		return totalAttachments;
	}

	public void setTotalAttachments(long totalAttachments) {
		this.totalAttachments = totalAttachments;
	}

	@Override
	public String toString() {
		return "UserBean [serverName=" + serverName + ", notesDatabase="
				+ notesDatabase + ", notesView=" + notesView + ", documentsImported=" + documentsImported
				+ ", documentsWithProblem=" + documentsWithProblem
				+ ", totalDocuments=" + totalDocuments
				+ ", totalAttachments=" + totalAttachments
				+ ", importDate=" + importDate.toString() + "]";
	}

}
