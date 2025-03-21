/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.File;
import java.io.IOException;

import java.util.Properties;

import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class NotificationUtil {

	public static void sendEmail(
		String body, String senderName, String subject,
		String recipientEmailAddress) {

		sendEmail(
			JenkinsResultsParserUtil.combine(
				senderName, "@", JenkinsResultsParserUtil.getHostName(null)),
			senderName, recipientEmailAddress, subject, body);
	}

	public static void sendEmail(
		String senderEmailAddress, String senderName,
		String recipientEmailAddress, String subject, String body) {

		sendEmail(
			senderEmailAddress, senderName, recipientEmailAddress, subject,
			body, null, null);
	}

	public static void sendEmail(
		String senderEmailAddress, String senderName,
		String recipientEmailAddress, String subject, String body,
		String attachmentFileName) {

		sendEmail(
			senderEmailAddress, senderName, recipientEmailAddress, subject,
			body, attachmentFileName, null);
	}

	public static synchronized void sendEmail(
		String senderEmailAddress, String senderName,
		String recipientEmailAddress, String subject, String body,
		String attachmentFileName, String mimeType) {

		Thread thread = Thread.currentThread();

		if (thread.getContextClassLoader() == null) {
			thread.setContextClassLoader(
				NotificationUtil.class.getClassLoader());
		}

		body = JenkinsResultsParserUtil.redact(body);
		subject = JenkinsResultsParserUtil.redact(subject);

		Properties sessionProperties = System.getProperties();

		sessionProperties.put("mail.smtp.auth", "true");
		sessionProperties.put("mail.smtp.port", 587);
		sessionProperties.put("mail.smtp.starttls.enable", "true");
		sessionProperties.put("mail.transport.protocol", "smtp");

		Session session = Session.getDefaultInstance(sessionProperties);

		MimeMessage mimeMessage = new MimeMessage(session);

		if (mimeType == null) {
			mimeType = "text/plain";
		}

		try {
			if (!senderEmailAddress.endsWith(".liferay.com")) {
				senderEmailAddress = senderEmailAddress + ".lax.liferay.com";
			}

			mimeMessage.setFrom(
				new InternetAddress(senderEmailAddress, senderName));
			mimeMessage.setRecipients(
				Message.RecipientType.TO, recipientEmailAddress);
			mimeMessage.setSubject(subject);

			Multipart multipart = new MimeMultipart();

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setContent(body, mimeType);

			multipart.addBodyPart(messageBodyPart);

			if ((attachmentFileName != null) &&
				!attachmentFileName.equals("")) {

				BodyPart attachmentBodyPart = new MimeBodyPart();

				DataSource source = new FileDataSource(attachmentFileName);

				attachmentBodyPart.setDataHandler(new DataHandler(source));

				File attachmentFile = new File(attachmentFileName);

				long attachmentFileSize = attachmentFile.length();

				if (attachmentFileSize < _MAX_ATTACHMENT_FILE_SIZE) {
					attachmentBodyPart.setFileName(attachmentFile.getName());

					multipart.addBodyPart(attachmentBodyPart);
				}
				else {
					System.out.println(
						"Attachment file size for " + attachmentFile +
							" exceeds 10MB cannot be attached to email");
				}
			}

			mimeMessage.setContent(multipart);

			mimeMessage.saveChanges();

			Transport transport = session.getTransport();

			transport.connect(
				JenkinsResultsParserUtil.getBuildProperty("email.smtp.server"),
				JenkinsResultsParserUtil.getBuildProperty(
					"email.smtp.username"),
				JenkinsResultsParserUtil.getBuildProperty(
					"email.smtp.password"));

			transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

			System.out.println("Email sent to: " + recipientEmailAddress);

			transport.close();
		}
		catch (IOException | MessagingException exception) {
			System.out.println("Unable to send email.");

			String message = exception.getMessage();

			System.out.println(message);

			exception.printStackTrace();

			Throwable throwable = exception.getCause();

			if (throwable != null) {
				String throwableMessage = throwable.getMessage();

				if (throwableMessage.contains("no object DCH for MIME type")) {
					return;
				}
			}

			StringBuilder sb = new StringBuilder();

			sb.append("Sender: ");
			sb.append(senderEmailAddress);
			sb.append("\nRecipient: ");
			sb.append(recipientEmailAddress);
			sb.append("\nSubject: ");
			sb.append(subject);
			sb.append("\nBody: ");
			sb.append(body);
			sb.append("\nError: ");
			sb.append(message);
			sb.append("\n\n<@U04GTH03Q>");

			sendSlackNotification(
				sb.toString(), "ci-notifications", "Unable to send email");
		}
	}

	public static void sendSlackNotification(
		String body, String channelName, String subject) {

		sendSlackNotification(
			body, channelName, ":liferay-ci:", subject, "Liferay CI");
	}

	public static void sendSlackNotification(
		String body, String channelName, String iconEmoji, String subject,
		String username) {

		body = JenkinsResultsParserUtil.redact(body);
		subject = JenkinsResultsParserUtil.redact(subject);

		String text = body;

		if (subject == null) {
			subject = "";
		}
		else {
			subject = subject.trim();

			if (!subject.isEmpty()) {
				subject = JenkinsResultsParserUtil.combine(
					"*", subject, "*\n\n");

				text = JenkinsResultsParserUtil.combine(
					subject, "> ", body.replaceAll("\n", "\n> "));
			}
		}

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"channel", channelName
		).put(
			"icon_emoji", iconEmoji
		).put(
			"text", text
		).put(
			"username", username
		);

		try {
			Properties properties = JenkinsResultsParserUtil.getBuildProperties(
				true);

			JenkinsResultsParserUtil.toString(
				properties.getProperty("slack.webhook.url"),
				jsonObject.toString());
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private static final long _MAX_ATTACHMENT_FILE_SIZE = 1024 * 1024 * 10;

	static {
		Thread thread = Thread.currentThread();

		thread.setContextClassLoader(Message.class.getClassLoader());
	}

}