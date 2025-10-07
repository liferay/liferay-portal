/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.messaging.internal;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.mail.settings.configuration.MailSettingSystemConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.security.auth.EmailAddressGenerator;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.security.auth.EmailAddressGeneratorFactory;

import jakarta.mail.internet.InternetAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 * @author Zsolt Balogh
 */
@Component(
	configurationPid = "com.liferay.mail.settings.configuration.MailSettingSystemConfiguration",
	property = "destination.name=" + DestinationNames.MAIL,
	service = MessageListener.class
)
public class MailMessageListener extends BaseMessageListener {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_mailSettingSystemConfiguration = ConfigurableUtil.createConfigurable(
			MailSettingSystemConfiguration.class, properties);
	}

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
			_mailSettingSystemConfiguration.auditTrail());

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

			MailEngine.send(
				_mailService, mailMessage,
				_mailSettingSystemConfiguration.batchSize(),
				_mailSettingSystemConfiguration.throwsExceptionOnFailure());
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

		Set<String> sendBlacklist = new HashSet<>(
			Arrays.asList(_mailSettingSystemConfiguration.sendBlacklist()));

		if (sendBlacklist.contains(emailAddress)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Email ", emailAddress, " will be ignored because it ",
						"is included in blacklist"));
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

	@Reference
	private MailService _mailService;

	private volatile MailSettingSystemConfiguration
		_mailSettingSystemConfiguration;

}