/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.web.internal.frontend.data.set.provider;

import com.liferay.commerce.frontend.model.LabelField;
import com.liferay.commerce.notification.model.CommerceNotificationQueueEntry;
import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.service.CommerceNotificationQueueEntryService;
import com.liferay.commerce.notification.service.CommerceNotificationTemplateService;
import com.liferay.commerce.notification.type.CommerceNotificationType;
import com.liferay.commerce.notification.type.CommerceNotificationTypeRegistry;
import com.liferay.commerce.notification.web.internal.constants.CommerceNotificationFDSNames;
import com.liferay.commerce.notification.web.internal.model.NotificationEntry;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceNotificationFDSNames.NOTIFICATION_ENTRIES,
	service = FDSDataProvider.class
)
public class CommerceNotificationEntryFDSDataProvider
	implements FDSDataProvider<NotificationEntry> {

	@Override
	public List<NotificationEntry> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		return TransformUtil.transform(
			_commerceNotificationQueueEntryService.
				getCommerceNotificationQueueEntries(
					commerceChannel.getGroupId(),
					fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition(), null),
			commerceNotificationQueueEntry -> {
				CommerceNotificationTemplate commerceNotificationTemplate =
					_commerceNotificationTemplateService.
						getCommerceNotificationTemplate(
							commerceNotificationQueueEntry.
								getCommerceNotificationTemplateId());

				CommerceNotificationType commerceNotificationType =
					_commerceNotificationTypeRegistry.
						getCommerceNotificationType(
							commerceNotificationTemplate.getType());

				return new NotificationEntry(
					commerceNotificationQueueEntry.getFromName(),
					commerceNotificationQueueEntry.
						getCommerceNotificationQueueEntryId(),
					commerceNotificationQueueEntry.getPriority(),
					_getSent(
						commerceNotificationQueueEntry, httpServletRequest),
					commerceNotificationQueueEntry.getToName(),
					commerceNotificationType.getLabel(
						themeDisplay.getLocale()));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		return _commerceNotificationQueueEntryService.
			getCommerceNotificationQueueEntriesCount(
				commerceChannel.getGroupId());
	}

	private LabelField _getSent(
		CommerceNotificationQueueEntry commerceNotificationQueueEntry,
		HttpServletRequest httpServletRequest) {

		if (commerceNotificationQueueEntry.isSent()) {
			return new LabelField(
				"success", _language.get(httpServletRequest, "sent"));
		}

		return new LabelField(
			"danger", _language.get(httpServletRequest, "unsent"));
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceNotificationQueueEntryService
		_commerceNotificationQueueEntryService;

	@Reference
	private CommerceNotificationTemplateService
		_commerceNotificationTemplateService;

	@Reference
	private CommerceNotificationTypeRegistry _commerceNotificationTypeRegistry;

	@Reference
	private Language _language;

}