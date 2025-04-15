/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.frontend.data.set.provider;

import com.liferay.commerce.frontend.model.AuthorField;
import com.liferay.commerce.frontend.model.LabelField;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.notification.model.CommerceNotificationQueueEntry;
import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.service.CommerceNotificationQueueEntryLocalService;
import com.liferay.commerce.notification.service.CommerceNotificationTemplateService;
import com.liferay.commerce.order.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.web.internal.model.Notification;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.NOTIFICATIONS,
	service = FDSDataProvider.class
)
public class CommerceNotificationFDSDataProvider
	implements FDSDataProvider<Notification> {

	@Override
	public List<Notification> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		return TransformUtil.transform(
			_commerceNotificationQueueEntryLocalService.
				getCommerceNotificationQueueEntries(
					commerceOrder.getGroupId(), CommerceOrder.class.getName(),
					commerceOrder.getCommerceOrderId(), true,
					fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition(), null),
			commerceNotificationQueueEntry -> {
				User user = _userLocalService.fetchUser(
					commerceNotificationQueueEntry.getUserId());

				return new Notification(
					commerceNotificationQueueEntry.
						getCommerceNotificationQueueEntryId(),
					new AuthorField(
						_getUserPortraitSrc(user, httpServletRequest),
						user.getEmailAddress(), user.getFullName()),
					_getSentDate(
						commerceNotificationQueueEntry, httpServletRequest),
					new LabelField(
						"success",
						_getCommerceNotificationTemplateType(
							commerceNotificationQueueEntry)),
					commerceNotificationQueueEntry.getSubject(),
					_htmlParser.extractText(
						commerceNotificationQueueEntry.getBody()),
					_getNotificationPanelURL(
						commerceNotificationQueueEntry.
							getCommerceNotificationQueueEntryId(),
						httpServletRequest));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		return _commerceNotificationQueueEntryLocalService.
			getCommerceNotificationQueueEntriesCount(
				commerceOrder.getGroupId(), CommerceOrder.class.getName(),
				commerceOrder.getCommerceOrderId(), true);
	}

	private String _getCommerceNotificationTemplateType(
			CommerceNotificationQueueEntry commerceNotificationQueueEntry)
		throws PortalException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			_commerceNotificationTemplateService.
				getCommerceNotificationTemplate(
					commerceNotificationQueueEntry.
						getCommerceNotificationTemplateId());

		return commerceNotificationTemplate.getType();
	}

	private String _getNotificationPanelURL(
			long commerceNotificationQueueEntryId,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CommerceOrder.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/commerce_order/view_commerce_notification_queue_entry"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).buildPortletURL();

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		portletURL.setParameter(
			"commerceOrderId", String.valueOf(commerceOrderId));

		portletURL.setParameter(
			"commerceNotificationQueueEntryId",
			String.valueOf(commerceNotificationQueueEntryId));

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL.toString();
	}

	private String _getSentDate(
		CommerceNotificationQueueEntry commerceNotificationQueueEntry,
		HttpServletRequest httpServletRequest) {

		Date sentDate = commerceNotificationQueueEntry.getSentDate();

		String sentDateDescription = StringPool.BLANK;

		if (sentDate != null) {
			sentDateDescription = _language.getTimeDescription(
				httpServletRequest,
				System.currentTimeMillis() - sentDate.getTime(), true);
		}

		return sentDateDescription;
	}

	private String _getUserPortraitSrc(
		User user, HttpServletRequest httpServletRequest) {

		StringBundler sb = new StringBundler(5);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		sb.append(themeDisplay.getPathImage());

		sb.append("/user_portrait?screenName=");
		sb.append(user.getScreenName());
		sb.append("&amp;companyId=");
		sb.append(user.getCompanyId());

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceNotificationFDSDataProvider.class);

	@Reference
	private CommerceNotificationQueueEntryLocalService
		_commerceNotificationQueueEntryLocalService;

	@Reference
	private CommerceNotificationTemplateService
		_commerceNotificationTemplateService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}