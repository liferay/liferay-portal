/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.messaging.internal;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.security.auth.EmailAddressGenerator;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.security.auth.EmailAddressGeneratorFactory;
import com.liferay.portal.util.PropsValues;

import jakarta.mail.internet.InternetAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 * @author Zsolt Balogh
 */
@Component(
	property = "destination.name=" + DestinationNames.MAIL,
	service = MessageListener.class
)
public class MailMessageListener extends BaseMessageListener {

	protected void doMailMessage(MailMessage mailMessage) throws Exception {
		InternetAddress from = filterInternetAddress(mailMessage.getFrom());

		if (from == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Skipping email because the sender is not specified");
			}

			return;
		}

		mailMessage.setFrom(from);

		InternetAddress[] to = filterInternetAddresses(mailMessage.getTo());

		mailMessage.setTo(to);

		InternetAddress[] cc = filterInternetAddresses(mailMessage.getCC());

		mailMessage.setCC(cc);

		InternetAddress[] bcc = filterInternetAddresses(mailMessage.getBCC());

		InternetAddress[] auditTrail = InternetAddress.parse(
			PropsValues.MAIL_AUDIT_TRAIL);

		if (auditTrail.length > 0) {
			if (ArrayUtil.isNotEmpty(bcc)) {
				for (InternetAddress internetAddress : auditTrail) {
					bcc = ArrayUtil.append(bcc, internetAddress);
				}
			}
			else {
				bcc = auditTrail;
			}
		}

		mailMessage.setBCC(bcc);

		InternetAddress[] bulkAddresses = filterInternetAddresses(
			mailMessage.getBulkAddresses());

		mailMessage.setBulkAddresses(bulkAddresses);

		InternetAddress[] replyTo = filterInternetAddresses(
			mailMessage.getReplyTo());

		mailMessage.setReplyTo(replyTo);

		if (ArrayUtil.isNotEmpty(to) || ArrayUtil.isNotEmpty(cc) ||
			ArrayUtil.isNotEmpty(bcc) || ArrayUtil.isNotEmpty(bulkAddresses)) {

			MailEngine.send(mailMessage);
		}
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		doMailMessage((MailMessage)message.getPayload());
	}

	protected InternetAddress filterInternetAddress(
		InternetAddress internetAddress) {

		if (PortalRunMode.isTestMode()) {
			return internetAddress;
		}

		EmailAddressGenerator emailAddressGenerator =
			EmailAddressGeneratorFactory.getInstance();

		String emailAddress = internetAddress.getAddress();

		if (emailAddressGenerator.isFake(emailAddress)) {
			return null;
		}

		if (_mailSendBlacklist.contains(emailAddress)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Email ", emailAddress, " will be ignored because it ",
						"is included in ", PropsKeys.MAIL_SEND_BLACKLIST));
			}

			return null;
		}

		return internetAddress;
	}

	protected InternetAddress[] filterInternetAddresses(
		InternetAddress[] internetAddresses) {

		if (internetAddresses == null) {
			return null;
		}

		List<InternetAddress> filteredInternetAddresses = new ArrayList<>(
			internetAddresses.length);

		for (InternetAddress internetAddress : internetAddresses) {
			InternetAddress filteredInternetAddress = filterInternetAddress(
				internetAddress);

			if (filteredInternetAddress != null) {
				filteredInternetAddresses.add(filteredInternetAddress);
			}
		}

		return filteredInternetAddresses.toArray(new InternetAddress[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MailMessageListener.class);

	private static final Set<String> _mailSendBlacklist = new HashSet<>(
		Arrays.asList(PropsValues.MAIL_SEND_BLACKLIST));

}