/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.subscription.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.NoSuchTicketException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.subscription.exception.NoSuchSubscriptionException;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.subscription.web.internal.constants.SubscriptionPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SubscriptionPortletKeys.UNSUBSCRIBE,
		"mvc.command.name=/subscription/unsubscribe"
	},
	service = MVCActionCommand.class
)
public class UnsubscribeMVCActionCommand extends BaseMVCActionCommand {

	@Override
	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String key = ParamUtil.getString(actionRequest, "key");
		long userId = ParamUtil.getLong(actionRequest, "userId");

		PortletURL portletURL = PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				actionRequest, SubscriptionPortletKeys.UNSUBSCRIBE,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/unsubscribe/unsubscribed.jsp"
		).setParameter(
			"key", key
		).setParameter(
			"userId", userId
		).setWindowState(
			WindowState.MAXIMIZED
		).buildPortletURL();

		try {
			_checkUser(userId, actionRequest);

			Subscription subscription = _unsubscribe(key, userId);

			portletURL.setParameter(
				"subscriptionTitle",
				_getTitle(actionRequest.getLocale(), subscription));

			actionResponse.sendRedirect(portletURL.toString());
		}
		catch (NoSuchSubscriptionException noSuchSubscriptionException) {
			_log.error(noSuchSubscriptionException);

			actionResponse.sendRedirect(portletURL.toString());
		}
		catch (PortalException portalException) {
			SessionErrors.add(
				actionRequest, portalException.getClass(), portalException);

			actionResponse.setRenderParameter(
				"mvcPath", "/unsubscribe/error.jsp");
		}
	}

	private void _checkUser(long userId, ActionRequest actionRequest)
		throws PortalException {

		User user = _portal.getUser(actionRequest);

		if ((user != null) && (userId != user.getUserId())) {
			throw new PrincipalException();
		}
	}

	private void _checkUser(long userId, Subscription subscription)
		throws PrincipalException {

		if ((subscription != null) && (subscription.getUserId() != userId)) {
			throw new PrincipalException();
		}
	}

	private Ticket _getTicket(String key) throws PortalException {
		Ticket ticket = _ticketLocalService.getTicket(key);

		if (ticket.getType() != TicketConstants.TYPE_SUBSCRIPTION) {
			throw new NoSuchTicketException("Invalid type " + ticket.getType());
		}

		String className = ticket.getClassName();

		if (!className.equals(Subscription.class.getName())) {
			throw new NoSuchTicketException("Invalid className " + className);
		}

		return ticket;
	}

	private String _getTitle(Locale locale, Subscription subscription)
		throws PortalException {

		Group group = _groupLocalService.fetchGroup(subscription.getClassPK());

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.format(
			resourceBundle, "blog-at-x", group.getDescriptiveName(locale));
	}

	private Subscription _unsubscribe(String key, long userId)
		throws PortalException {

		Ticket ticket = _getTicket(key);

		long subscriptionId = ticket.getClassPK();

		if (ticket.isExpired()) {
			_ticketLocalService.deleteTicket(ticket);

			throw new NoSuchTicketException("{ticketKey=" + key + "}");
		}

		Subscription subscription = _subscriptionLocalService.getSubscription(
			subscriptionId);

		_checkUser(userId, subscription);

		_subscriptionLocalService.deleteSubscription(subscription);

		return subscription;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UnsubscribeMVCActionCommand.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private TicketLocalService _ticketLocalService;

}