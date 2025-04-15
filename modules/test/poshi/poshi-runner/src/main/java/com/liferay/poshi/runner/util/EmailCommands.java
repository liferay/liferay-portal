/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.util;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

import org.eclipse.angus.mail.imap.IMAPFolder;

/**
 * @author Kwang Lee
 * @author Michael Hashimoto
 */
public class EmailCommands {

	public static void connectToEmailAccount(
			String emailAddress, String emailPassword)
		throws Exception {

		Properties imapProperties = System.getProperties();

		imapProperties.setProperty("mail.store.protocol", "imaps");

		_imapSession = Session.getInstance(imapProperties);

		Store store = _imapSession.getStore("imaps");

		store.connect("imap.gmail.com", emailAddress, emailPassword);

		_imapFolder = (IMAPFolder)store.getFolder("Inbox");

		_imapFolder.open(Folder.READ_WRITE);

		Properties smtpProperties = System.getProperties();

		smtpProperties.put("mail.smtp.auth", "true");
		smtpProperties.put("mail.smtp.host", "smtp.gmail.com");
		smtpProperties.put("mail.smtp.password", emailPassword);
		smtpProperties.put("mail.smtp.port", "587");
		smtpProperties.put("mail.smtp.starttls.enable", "true");
		smtpProperties.put("mail.smtp.user", emailAddress);

		_smtpSession = Session.getDefaultInstance(smtpProperties);

		_transport = _smtpSession.getTransport("smtp");

		_transport.connect("smtp.gmail.com", emailAddress, emailPassword);
	}

	public static void deleteAllEmails() throws Exception {
		Message[] messages = _imapFolder.getMessages();

		for (Message message : messages) {
			message.setFlag(Flags.Flag.DELETED, true);
		}

		_imapFolder.close(true);
	}

	public static String getEmailBody(int index) throws Exception {
		Message message = _imapFolder.getMessage(index);

		String body = (String)message.getContent();

		return body.trim();
	}

	public static String getEmailSubject(int index) throws Exception {
		Message message = _imapFolder.getMessage(index);

		return message.getSubject();
	}

	public static void replyToEmail(String to, String body) throws Exception {
		Message message = _imapFolder.getMessage(1);

		Message replyMessage = message.reply(false);

		replyMessage.setRecipient(
			MimeMessage.RecipientType.TO, new InternetAddress(to));
		replyMessage.setText(body);

		_transport.sendMessage(
			replyMessage,
			replyMessage.getRecipients(MimeMessage.RecipientType.TO));

		_transport.close();
	}

	public static void sendEmail(String to, String subject, String body)
		throws Exception {

		Message message = new MimeMessage(_smtpSession);

		message.addRecipient(
			MimeMessage.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		message.setText(body);

		message.saveChanges();

		_transport.sendMessage(message, message.getAllRecipients());

		_transport.close();
	}

	private static IMAPFolder _imapFolder;
	private static Session _imapSession;
	private static Session _smtpSession;
	private static Transport _transport;

}