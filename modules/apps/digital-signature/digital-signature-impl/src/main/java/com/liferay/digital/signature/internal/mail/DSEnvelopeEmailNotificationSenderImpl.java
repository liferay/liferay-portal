/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.mail;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.mail.DSEnvelopeEmailNotificationSender;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.url.SignDSURLProvider;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;

import jakarta.mail.internet.InternetAddress;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(service = DSEnvelopeEmailNotificationSender.class)
public class DSEnvelopeEmailNotificationSenderImpl
	implements DSEnvelopeEmailNotificationSender {

	@Override
	public void sendNotification(
		long companyId, long groupId, String dsEnvelopeId,
		DSRecipient dsRecipient, String emailSubject, String emailMessage) {

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			DigitalSignatureConfigurationUtil.getDigitalSignatureConfiguration(
				companyId, groupId);

		if (!digitalSignatureConfiguration.enabled() ||
			!digitalSignatureConfiguration.enableEmbeddedView()) {

			return;
		}

		String emailAddress = dsRecipient.getEmailAddress();

		if (!Validator.isEmailAddress(emailAddress)) {
			return;
		}

		try {
			String url = _signDSURLProvider.getURL(companyId, dsEnvelopeId);

			if (Validator.isNull(url)) {
				return;
			}

			String fromAddress = PrefsPropsUtil.getString(
				companyId, PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);
			String fromName = PrefsPropsUtil.getString(
				companyId, PropsKeys.ADMIN_EMAIL_FROM_NAME);

			Locale locale = _getLocale(companyId, emailAddress, groupId);

			String subject = emailSubject;

			if (Validator.isNull(subject)) {
				subject = _language.get(locale, "you-have-a-document-to-sign");
			}

			MailMessage mailMessage = new MailMessage(
				new InternetAddress(fromAddress, fromName),
				new InternetAddress(emailAddress), subject,
				_getBody(emailMessage, locale, url), true);

			_mailService.sendEmail(mailMessage);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to send sign email for envelope " + dsEnvelopeId,
				exception);
		}
	}

	private String _getBody(String emailMessage, Locale locale, String url) {
		String message = emailMessage;

		if (Validator.isNull(message)) {
			message = _language.get(locale, "you-have-a-document-to-sign");
		}
		else {
			message = HtmlUtil.escape(message);
		}

		return StringBundler.concat(
			"<p>", message, "</p><p><a href=\"", url, "\">",
			_language.get(locale, "review-and-sign"), "</a></p>");
	}

	private Locale _getLocale(long companyId, String emailAddress, long groupId)
		throws Exception {

		User user = _userLocalService.fetchUserByEmailAddress(
			companyId, emailAddress);

		if (user != null) {
			return user.getLocale();
		}

		return _portal.getSiteDefaultLocale(groupId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DSEnvelopeEmailNotificationSenderImpl.class);

	@Reference
	private Language _language;

	@Reference
	private MailService _mailService;

	@Reference
	private Portal _portal;

	@Reference
	private SignDSURLProvider _signDSURLProvider;

	@Reference
	private UserLocalService _userLocalService;

}