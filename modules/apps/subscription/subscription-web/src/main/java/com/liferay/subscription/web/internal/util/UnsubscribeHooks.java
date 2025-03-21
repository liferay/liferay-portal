/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.subscription.web.internal.util;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.template.MailTemplate;
import com.liferay.mail.kernel.template.MailTemplateContext;
import com.liferay.mail.kernel.template.MailTemplateContextBuilder;
import com.liferay.mail.kernel.template.MailTemplateFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.web.internal.configuration.SubscriptionConfiguration;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.InternetHeaders;

import java.io.IOException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class UnsubscribeHooks {

	public UnsubscribeHooks(
		SubscriptionConfiguration subscriptionConfiguration,
		TicketLocalService ticketLocalService,
		UserLocalService userLocalService,
		SubscriptionSender subscriptionSender) {

		_subscriptionConfiguration = subscriptionConfiguration;
		_ticketLocalService = ticketLocalService;
		_userLocalService = userLocalService;
		_subscriptionSender = subscriptionSender;
	}

	public void addUnsubscriptionLinks(MailMessage mailMessage) {
		if (_subscriptionSender.isBulk()) {
			return;
		}

		InternetAddress[] toAddresses = mailMessage.getTo();

		if (toAddresses.length == 0) {
			return;
		}

		InternetAddress toAddress = toAddresses[0];

		User user = _userLocalService.fetchUserByEmailAddress(
			CompanyThreadLocal.getNonsystemCompanyId(), toAddress.getAddress());

		if (user == null) {
			return;
		}

		Ticket ticket = _userTicketMap.get(user.getUserId());

		if (ticket != null) {
			try {
				String unsubscribeURL = _getUnsubscribeURL(user, ticket);

				_addUnsubscribeHeader(mailMessage, unsubscribeURL);
				_addUnsubscribeLink(mailMessage, unsubscribeURL);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
			finally {
				_userTicketMap.remove(user.getUserId());
			}
		}
	}

	public void createUnsubscriptionTicket(
		com.liferay.portal.kernel.model.Subscription subscription) {

		if (_subscriptionSender.isBulk()) {
			return;
		}

		_userTicketMap.put(subscription.getUserId(), _getTicket(subscription));
	}

	private void _addUnsubscribeHeader(
		MailMessage mailMessage, String unsubscribeURL) {

		InternetHeaders internetHeaders = new InternetHeaders();

		internetHeaders.setHeader(
			"List-Unsubscribe", "<" + unsubscribeURL + ">");

		mailMessage.setInternetHeaders(internetHeaders);
	}

	private void _addUnsubscribeLink(
			MailMessage mailMessage, String unsubscribeURL)
		throws IOException {

		MailTemplateContextBuilder mailTemplateContextBuilder =
			MailTemplateFactoryUtil.createMailTemplateContextBuilder();

		mailTemplateContextBuilder.put("[$UNSUBSCRIBE_URL$]", unsubscribeURL);

		MailTemplateContext mailTemplateContext =
			mailTemplateContextBuilder.build();

		MailTemplate bodyMailTemplate =
			MailTemplateFactoryUtil.createMailTemplate(
				mailMessage.getBody(), true);

		String processedBody = bodyMailTemplate.renderAsString(
			LocaleUtil.US, mailTemplateContext);

		mailMessage.setBody(processedBody);
	}

	private Ticket _getTicket(
		com.liferay.portal.kernel.model.Subscription subscription) {

		Calendar calendar = Calendar.getInstance();

		calendar.add(
			Calendar.DATE,
			_subscriptionConfiguration.unsubscriptionTicketExpirationTime());

		List<Ticket> tickets = _ticketLocalService.getTickets(
			subscription.getCompanyId(), Subscription.class.getName(),
			subscription.getSubscriptionId(),
			TicketConstants.TYPE_SUBSCRIPTION);

		if (ListUtil.isEmpty(tickets)) {
			return _ticketLocalService.addTicket(
				subscription.getCompanyId(), Subscription.class.getName(),
				subscription.getSubscriptionId(),
				TicketConstants.TYPE_SUBSCRIPTION, StringPool.BLANK,
				calendar.getTime(), _subscriptionSender.getServiceContext());
		}

		try {
			Ticket ticket = tickets.get(0);

			return _ticketLocalService.updateTicket(
				ticket.getTicketId(), Subscription.class.getName(),
				subscription.getSubscriptionId(),
				TicketConstants.TYPE_SUBSCRIPTION, StringPool.BLANK,
				calendar.getTime());
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private String _getUnsubscribeURL(User user, Ticket ticket) {
		return StringBundler.concat(
			_subscriptionSender.getContextAttribute("[$PORTAL_URL$]"),
			PortalUtil.getPathMain(), "/portal/unsubscribe?key=",
			ticket.getKey(), "&userId=", user.getUserId());
	}

	private final SubscriptionConfiguration _subscriptionConfiguration;
	private final SubscriptionSender _subscriptionSender;
	private final TicketLocalService _ticketLocalService;
	private final UserLocalService _userLocalService;
	private final Map<Long, Ticket> _userTicketMap = new HashMap<>();

}