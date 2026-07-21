/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.util;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.osb.faro.model.FaroProject;

import jakarta.mail.internet.InternetAddress;

/**
 * @author Eudaldo Alonso
 */
public class FaroEmailSender {

	public static FaroEmailSender create(MailService mailService) {
		return new FaroEmailSender(mailService);
	}

	public void send() throws Exception {
		InternetAddress from = new InternetAddress(
			EmailUtil.getSenderEmailAddress(_faroProject),
			EmailUtil.getSenderName(_faroProject));

		InternetAddress to = new InternetAddress(_toEmailAddress, _toName);

		_mailService.sendEmail(
			new MailMessage(from, to, _subject, _body, true));
	}

	public FaroEmailSender setBody(String body) {
		_body = body;

		return this;
	}

	public FaroEmailSender setFaroProject(FaroProject faroProject) {
		_faroProject = faroProject;

		return this;
	}

	public FaroEmailSender setSubject(String subject) {
		_subject = subject;

		return this;
	}

	public FaroEmailSender setToEmailAddress(String toEmailAddress) {
		_toEmailAddress = toEmailAddress;

		return this;
	}

	public FaroEmailSender setToName(String toName) {
		_toName = toName;

		return this;
	}

	private FaroEmailSender(MailService mailService) {
		_mailService = mailService;
	}

	private String _body;
	private FaroProject _faroProject;
	private final MailService _mailService;
	private String _subject;
	private String _toEmailAddress;
	private String _toName;

}