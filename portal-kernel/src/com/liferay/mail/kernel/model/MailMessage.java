/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.kernel.model;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.InternetHeaders;

import java.io.File;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 * @author Raymond Augé
 * @author Thiago Moreira
 */
public class MailMessage implements Serializable {

	public MailMessage() {
	}

	public MailMessage(
		InternetAddress from, InternetAddress to, String subject, String body,
		boolean htmlFormat) {

		_from = from;

		if (to != null) {
			_to = new InternetAddress[] {to};
		}
		else {
			_to = new InternetAddress[0];
		}

		_subject = subject;
		_body = body;
		_htmlFormat = htmlFormat;
	}

	public MailMessage(
		InternetAddress from, String subject, String body, boolean htmlFormat) {

		this(from, null, subject, body, htmlFormat);
	}

	public void addFileAttachment(File file) {
		addFileAttachment(file, null);
	}

	public void addFileAttachment(File file, String fileName) {
		if (file == null) {
			return;
		}

		FileAttachment fileAttachment = new FileAttachment(file, fileName);

		_fileAttachments.add(fileAttachment);
	}

	public InternetAddress[] getBCC() {
		return _bcc;
	}

	public String getBody() {
		return _body;
	}

	public InternetAddress[] getBulkAddresses() {
		return _bulkAddresses;
	}

	public InternetAddress[] getCC() {
		return _cc;
	}

	public List<FileAttachment> getFileAttachments() {
		return _fileAttachments;
	}

	public InternetAddress getFrom() {
		return _from;
	}

	public boolean getHTMLFormat() {
		return _htmlFormat;
	}

	public String getInReplyTo() {
		return _inReplyTo;
	}

	public InternetHeaders getInternetHeaders() {
		return _internetHeaders;
	}

	public String getMessageId() {
		return _messageId;
	}

	public InternetAddress[] getReplyTo() {
		return _replyTo;
	}

	public SMTPAccount getSMTPAccount() {
		return _smtpAccount;
	}

	public String getSubject() {
		return _subject;
	}

	public InternetAddress[] getTo() {
		return _to;
	}

	public boolean isHTMLFormat() {
		return _htmlFormat;
	}

	public void setBCC(InternetAddress bcc) {
		_bcc = new InternetAddress[] {bcc};
	}

	public void setBCC(InternetAddress[] bcc) {
		_bcc = bcc;
	}

	public void setBody(String body) {
		_body = body;
	}

	public void setBulkAddresses(InternetAddress[] bulkAddresses) {
		_bulkAddresses = bulkAddresses;
	}

	public void setCC(InternetAddress cc) {
		_cc = new InternetAddress[] {cc};
	}

	public void setCC(InternetAddress[] cc) {
		_cc = cc;
	}

	public void setFrom(InternetAddress from) {
		_from = from;
	}

	public void setHTMLFormat(boolean htmlFormat) {
		_htmlFormat = htmlFormat;
	}

	public void setInReplyTo(String inReplyTo) {
		_inReplyTo = inReplyTo;
	}

	public void setInternetHeaders(InternetHeaders internetHeaders) {
		_internetHeaders = internetHeaders;
	}

	public void setMessageId(String messageId) {
		_messageId = messageId;
	}

	public void setReplyTo(InternetAddress[] replyTo) {
		_replyTo = replyTo;
	}

	public void setSMTPAccount(SMTPAccount account) {
		_smtpAccount = account;
	}

	public void setSubject(String subject) {
		_subject = subject;
	}

	public void setTo(InternetAddress to) {
		_to = new InternetAddress[] {to};
	}

	public void setTo(InternetAddress[] to) {
		_to = to;
	}

	private InternetAddress[] _bcc;
	private String _body;
	private InternetAddress[] _bulkAddresses;
	private InternetAddress[] _cc;
	private final List<FileAttachment> _fileAttachments = new ArrayList<>();
	private InternetAddress _from;
	private boolean _htmlFormat;
	private String _inReplyTo;
	private InternetHeaders _internetHeaders;
	private String _messageId;
	private InternetAddress[] _replyTo;
	private SMTPAccount _smtpAccount;
	private String _subject;
	private InternetAddress[] _to;

}