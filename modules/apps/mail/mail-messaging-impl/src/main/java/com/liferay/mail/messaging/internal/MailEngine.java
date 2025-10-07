/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.messaging.internal;

import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.model.FileAttachment;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.model.SMTPAccount;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.LogUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;

import jakarta.mail.Address;
import jakarta.mail.Header;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.SendFailedException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.InternetHeaders;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.File;
import java.io.InputStream;

import java.net.SocketException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Jorge Ferrer
 * @author Neil Griffin
 * @author Thiago Moreira
 * @author Brett Swaim
 * @author Julius Lee
 */
public class MailEngine {

	public static void send(
			MailService mailService, InternetAddress from, InternetAddress[] to,
			InternetAddress[] cc, InternetAddress[] bcc,
			InternetAddress[] bulkAddresses, String subject, String body,
			boolean htmlFormat, InternetAddress[] replyTo, String messageId,
			String inReplyTo, List<FileAttachment> fileAttachments,
			SMTPAccount smtpAccount, InternetHeaders internetHeaders,
			String batchSize, boolean throwsExceptionOnFailure)
		throws PortalException {

		long startTime = System.currentTimeMillis();

		if (_log.isDebugEnabled()) {
			_log.debug("From: " + from);
			_log.debug("To: " + Arrays.toString(to));
			_log.debug("CC: " + Arrays.toString(cc));
			_log.debug("BCC: " + Arrays.toString(bcc));
			_log.debug("List Addresses: " + Arrays.toString(bulkAddresses));
			_log.debug("Subject: " + subject);
			_log.debug("Body: " + body);
			_log.debug("HTML Format: " + htmlFormat);
			_log.debug("Reply to: " + Arrays.toString(replyTo));
			_log.debug("Message ID: " + messageId);
			_log.debug("In Reply To: " + inReplyTo);

			if ((fileAttachments != null) && _log.isDebugEnabled()) {
				for (int i = 0; i < fileAttachments.size(); i++) {
					FileAttachment fileAttachment = fileAttachments.get(i);

					InputStream inputStream = fileAttachment.getInputStream();

					if (inputStream == null) {
						continue;
					}

					_log.debug(
						StringBundler.concat(
							"Attachment ", i, " and file name ",
							fileAttachment.getFileName()));
				}
			}
		}

		List<File> tempFiles = new ArrayList<>();

		try {
			_validateAddress(from);

			if (ArrayUtil.isNotEmpty(to)) {
				_validateAddresses(to);
			}

			if (ArrayUtil.isNotEmpty(cc)) {
				_validateAddresses(cc);
			}

			if (ArrayUtil.isNotEmpty(bcc)) {
				_validateAddresses(bcc);
			}

			if (ArrayUtil.isNotEmpty(replyTo)) {
				_validateAddresses(replyTo);
			}

			if (ArrayUtil.isNotEmpty(bulkAddresses)) {
				_validateAddresses(bulkAddresses);
			}

			Session session = null;

			if (smtpAccount == null) {
				session = mailService.getSession();
			}
			else {
				session = mailService.getSession(smtpAccount);
			}

			Message message = new LiferayMimeMessage(session);

			message.addHeader(
				"X-Auto-Response-Suppress", "AutoReply, DR, NDR, NRN, OOF, RN");

			message.setFrom(from);

			if (ArrayUtil.isNotEmpty(to)) {
				message.setRecipients(Message.RecipientType.TO, to);
			}

			if (ArrayUtil.isNotEmpty(cc)) {
				message.setRecipients(Message.RecipientType.CC, cc);
			}

			if (ArrayUtil.isNotEmpty(bcc)) {
				message.setRecipients(Message.RecipientType.BCC, bcc);
			}

			subject = GetterUtil.getString(subject);

			message.setSubject(_sanitizeCRLF(subject));

			if (ListUtil.isNotEmpty(fileAttachments)) {
				MimeMultipart rootMultipart = new MimeMultipart(
					_MULTIPART_TYPE_MIXED);

				MimeMultipart messageMultipart = new MimeMultipart(
					_MULTIPART_TYPE_ALTERNATIVE);

				MimeBodyPart messageBodyPart = new MimeBodyPart();

				messageBodyPart.setContent(messageMultipart);

				rootMultipart.addBodyPart(messageBodyPart);

				if (htmlFormat) {
					MimeBodyPart bodyPart = new MimeBodyPart();

					bodyPart.setContent(body, _TEXT_HTML);

					messageMultipart.addBodyPart(bodyPart);
				}
				else {
					MimeBodyPart bodyPart = new MimeBodyPart();

					bodyPart.setText(body);

					messageMultipart.addBodyPart(bodyPart);
				}

				for (FileAttachment fileAttachment : fileAttachments) {
					InputStream inputStream = fileAttachment.getInputStream();

					if (inputStream == null) {
						continue;
					}

					MimeBodyPart mimeBodyPart = new MimeBodyPart();

					File file = FileUtil.createTempFile(inputStream);

					tempFiles.add(file);

					DataSource dataSource = new FileDataSource(file);

					mimeBodyPart.setDataHandler(new DataHandler(dataSource));

					mimeBodyPart.setDisposition(Part.ATTACHMENT);

					if (fileAttachment.getFileName() != null) {
						mimeBodyPart.setFileName(fileAttachment.getFileName());
					}
					else {
						mimeBodyPart.setFileName(file.getName());
					}

					rootMultipart.addBodyPart(mimeBodyPart);
				}

				message.setContent(rootMultipart);

				message.saveChanges();
			}
			else {
				if (htmlFormat) {
					message.setContent(body, _TEXT_HTML);
				}
				else {
					message.setContent(body, _TEXT_PLAIN);
				}
			}

			message.setSentDate(new Date());

			if (ArrayUtil.isNotEmpty(replyTo)) {
				message.setReplyTo(replyTo);
			}

			if (messageId != null) {
				message.setHeader("Message-ID", _sanitizeCRLF(messageId));
			}

			if (inReplyTo != null) {
				message.setHeader("In-Reply-To", _sanitizeCRLF(inReplyTo));
				message.setHeader("References", _sanitizeCRLF(inReplyTo));
			}

			if (internetHeaders != null) {
				Enumeration<Header> enumeration =
					internetHeaders.getAllHeaders();

				while (enumeration.hasMoreElements()) {
					Header header = enumeration.nextElement();

					message.setHeader(header.getName(), header.getValue());
				}
			}

			_send(
				session, message, bulkAddresses,
				GetterUtil.getInteger(batchSize, _BATCH_SIZE),
				throwsExceptionOnFailure);
		}
		catch (SendFailedException sendFailedException) {
			_log.error(sendFailedException);

			if (throwsExceptionOnFailure) {
				throw new PortalException(sendFailedException);
			}
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
		finally {
			for (File file : tempFiles) {
				file.delete();
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Sending mail takes " +
					(System.currentTimeMillis() - startTime) + " ms");
		}
	}

	public static void send(
			MailService mailService, MailMessage mailMessage, String batchSize,
			boolean throwsExceptionOnFailure)
		throws PortalException {

		send(
			mailService, mailMessage.getFrom(), mailMessage.getTo(),
			mailMessage.getCC(), mailMessage.getBCC(),
			mailMessage.getBulkAddresses(), mailMessage.getSubject(),
			mailMessage.getBody(), mailMessage.isHTMLFormat(),
			mailMessage.getReplyTo(), mailMessage.getMessageId(),
			mailMessage.getInReplyTo(), mailMessage.getFileAttachments(),
			mailMessage.getSMTPAccount(), mailMessage.getInternetHeaders(),
			batchSize, throwsExceptionOnFailure);
	}

	private static Address[] _getBatchAddresses(
		Address[] addresses, int index, int batchSize) {

		if ((batchSize == _BATCH_SIZE) && (index == 0)) {
			return addresses;
		}
		else if (batchSize == _BATCH_SIZE) {
			return null;
		}

		int start = index * batchSize;

		if (start > addresses.length) {
			return null;
		}

		int end = (index + 1) * batchSize;

		if (end > addresses.length) {
			end = addresses.length;
		}

		return ArrayUtil.subset(addresses, start, end);
	}

	private static String _getSMTPProperty(Session session, String suffix) {
		String protocol = GetterUtil.getString(
			session.getProperty("mail.transport.protocol"));

		if (protocol.equals(Account.PROTOCOL_SMTPS)) {
			return session.getProperty("mail.smtps." + suffix);
		}

		return session.getProperty("mail.smtp." + suffix);
	}

	private static String _sanitizeCRLF(String text) {
		return StringUtil.replace(
			text, new char[] {CharPool.NEW_LINE, CharPool.RETURN},
			new char[] {CharPool.SPACE, CharPool.SPACE});
	}

	private static void _send(
			Session session, Message message, InternetAddress[] bulkAddresses,
			int batchSize, boolean throwsExceptionOnFailure)
		throws PortalException {

		if ((_DATA_LIMIT_MAIL_MESSAGE_MAX_PERIOD > 0) &&
			(_DATA_LIMIT_MAIL_MESSAGE_MAX_COUNT > 0)) {

			long currentTime = System.currentTimeMillis();

			if (((currentTime - _lastResetTime.get()) / 1000) >
					_DATA_LIMIT_MAIL_MESSAGE_MAX_PERIOD) {

				_mailMessageCounts.clear();

				_lastResetTime.set(currentTime);
			}

			AtomicLong mailMessageCount = _mailMessageCounts.computeIfAbsent(
				CompanyThreadLocal.getCompanyId(), id -> new AtomicLong());

			if (mailMessageCount.incrementAndGet() >
					_DATA_LIMIT_MAIL_MESSAGE_MAX_COUNT) {

				throw new PortalException(
					"Unable to exceed maximum number of allowed mail messages");
			}
		}

		try {
			boolean smtpAuth = GetterUtil.getBoolean(
				_getSMTPProperty(session, "auth"));
			String smtpHost = _getSMTPProperty(session, "host");
			int smtpPort = GetterUtil.getInteger(
				_getSMTPProperty(session, "port"), Account.PORT_SMTP);
			String user = _getSMTPProperty(session, "user");
			String password = _getSMTPProperty(session, "password");

			if (smtpAuth && Validator.isNotNull(user) &&
				Validator.isNotNull(password)) {

				String protocol = GetterUtil.getString(
					session.getProperty("mail.transport.protocol"),
					Account.PROTOCOL_SMTP);

				Transport transport = session.getTransport(protocol);

				transport.connect(smtpHost, smtpPort, user, password);

				Address[] addresses = null;

				if (ArrayUtil.isNotEmpty(bulkAddresses)) {
					addresses = bulkAddresses;
				}
				else {
					addresses = message.getAllRecipients();
				}

				for (int i = 0;; i++) {
					Address[] batchAddresses = _getBatchAddresses(
						addresses, i, batchSize);

					if (ArrayUtil.isEmpty(batchAddresses)) {
						break;
					}

					transport.sendMessage(message, batchAddresses);
				}

				transport.close();
			}
			else {
				if (ArrayUtil.isNotEmpty(bulkAddresses)) {
					int curBatch = 0;

					Address[] portion = _getBatchAddresses(
						bulkAddresses, curBatch, batchSize);

					while (ArrayUtil.isNotEmpty(portion)) {
						Transport.send(message, portion);

						curBatch++;

						portion = _getBatchAddresses(
							bulkAddresses, curBatch, batchSize);
					}
				}
				else {
					Transport.send(message);
				}
			}
		}
		catch (MessagingException messagingException) {
			if (messagingException.getNextException() instanceof
					SocketException) {

				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to connect to a valid mail server. Please " +
							"make sure one is properly configured: " +
								messagingException.getMessage());
				}
			}
			else {
				LogUtil.log(
					_log, messagingException,
					"Unable to send message: " +
						messagingException.getMessage());
			}

			if (throwsExceptionOnFailure) {
				throw new PortalException(messagingException);
			}
		}
	}

	private static void _validateAddress(Address address)
		throws AddressException {

		if (address == null) {
			throw new AddressException("Email address is null");
		}

		String addressString = address.toString();

		for (char c : addressString.toCharArray()) {
			if ((c == CharPool.NEW_LINE) || (c == CharPool.RETURN)) {
				throw new AddressException(
					StringBundler.concat(
						"Email address ", addressString,
						" is invalid because it contains line breaks"));
			}
		}
	}

	private static void _validateAddresses(Address[] addresses)
		throws AddressException {

		if (addresses == null) {
			throw new AddressException();
		}

		for (Address internetAddress : addresses) {
			_validateAddress(internetAddress);
		}
	}

	private static final int _BATCH_SIZE = 0;

	private static final long _DATA_LIMIT_MAIL_MESSAGE_MAX_COUNT =
		GetterUtil.getLong(
			PropsUtil.get(PropsKeys.DATA_LIMIT_MAIL_MESSAGE_MAX_COUNT));

	private static final long _DATA_LIMIT_MAIL_MESSAGE_MAX_PERIOD =
		GetterUtil.getLong(
			PropsUtil.get(PropsKeys.DATA_LIMIT_MAIL_MESSAGE_MAX_PERIOD));

	private static final String _MULTIPART_TYPE_ALTERNATIVE = "alternative";

	private static final String _MULTIPART_TYPE_MIXED = "mixed";

	private static final String _TEXT_HTML = "text/html;charset=\"UTF-8\"";

	private static final String _TEXT_PLAIN = "text/plain;charset=\"UTF-8\"";

	private static final Log _log = LogFactoryUtil.getLog(MailEngine.class);

	private static final AtomicLong _lastResetTime = new AtomicLong();
	private static final Map<Long, AtomicLong> _mailMessageCounts =
		new ConcurrentHashMap<>();

	private static class LiferayMimeMessage extends MimeMessage {

		@Override
		protected void updateMessageID() throws MessagingException {
			String[] messageIds = getHeader("Message-ID");

			if (ArrayUtil.isNotEmpty(messageIds)) {

				// Keep current value

				return;
			}

			super.updateMessageID();
		}

		private LiferayMimeMessage(Session session) {
			super(session);
		}

	}

}