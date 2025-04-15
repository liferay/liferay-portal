/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.web.internal.display.context;

import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.service.CommerceNotificationTemplateService;
import com.liferay.commerce.notification.type.CommerceNotificationType;
import com.liferay.commerce.notification.type.CommerceNotificationTypeRegistry;
import com.liferay.commerce.notification.web.internal.display.context.helper.CommerceNotificationsRequestHelper;
import com.liferay.commerce.order.CommerceDefinitionTermContributor;
import com.liferay.commerce.order.CommerceDefinitionTermContributorRegistry;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceNotificationTemplatesDisplayContext {

	public CommerceNotificationTemplatesDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		CommerceDefinitionTermContributorRegistry
			commerceDefinitionTermContributorRegistry,
		CommerceNotificationTemplateService commerceNotificationTemplateService,
		CommerceNotificationTypeRegistry commerceNotificationTypeRegistry,
		HttpServletRequest httpServletRequest) {

		_commerceChannelLocalService = commerceChannelLocalService;
		_commerceDefinitionTermContributorRegistry =
			commerceDefinitionTermContributorRegistry;
		_commerceNotificationTemplateService =
			commerceNotificationTemplateService;
		_commerceNotificationTypeRegistry = commerceNotificationTypeRegistry;

		_commerceNotificationsRequestHelper =
			new CommerceNotificationsRequestHelper(httpServletRequest);
	}

	public CommerceChannel getCommerceChannel() throws PortalException {
		CommerceNotificationTemplate commerceNotificationTemplate =
			getCommerceNotificationTemplate();

		if (commerceNotificationTemplate == null) {
			long commerceChannelId = ParamUtil.getLong(
				_commerceNotificationsRequestHelper.getRequest(),
				"commerceChannelId");

			if (commerceChannelId > 0) {
				return _commerceChannelLocalService.getCommerceChannel(
					commerceChannelId);
			}
		}

		return _commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
			commerceNotificationTemplate.getGroupId());
	}

	public long getCommerceChannelId() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		if (commerceChannel == null) {
			return 0;
		}

		return commerceChannel.getCommerceChannelId();
	}

	public CommerceNotificationTemplate getCommerceNotificationTemplate()
		throws PortalException {

		if (_commerceNotificationTemplate != null) {
			return _commerceNotificationTemplate;
		}

		long commerceNotificationTemplateId = ParamUtil.getLong(
			_commerceNotificationsRequestHelper.getRequest(),
			"commerceNotificationTemplateId");

		if (commerceNotificationTemplateId > 0) {
			_commerceNotificationTemplate =
				_commerceNotificationTemplateService.
					getCommerceNotificationTemplate(
						commerceNotificationTemplateId);
		}

		return _commerceNotificationTemplate;
	}

	public CommerceNotificationType getCommerceNotificationType(String key) {
		return _commerceNotificationTypeRegistry.getCommerceNotificationType(
			key);
	}

	public List<CommerceNotificationType> getCommerceNotificationTypes() {
		return _commerceNotificationTypeRegistry.getCommerceNotificationTypes();
	}

	public Map<String, String> getDefinitionTerms(
		String contributorKey, String notificationTypeKey, Locale locale) {

		List<CommerceDefinitionTermContributor>
			definitionTermContributorsByContributorKey =
				_commerceDefinitionTermContributorRegistry.
					getDefinitionTermContributorsByContributorKey(
						contributorKey);
		List<CommerceDefinitionTermContributor>
			definitionTermContributorsByNotificationTypeKey =
				_commerceDefinitionTermContributorRegistry.
					getDefinitionTermContributorsByNotificationTypeKey(
						notificationTypeKey);

		Map<String, String> results = new HashMap<>();

		for (CommerceDefinitionTermContributor
				commerceDefinitionTermContributor :
					definitionTermContributorsByContributorKey) {

			if (definitionTermContributorsByNotificationTypeKey.contains(
					commerceDefinitionTermContributor)) {

				results.putAll(
					commerceDefinitionTermContributor.getDefinitionTerms(
						locale));
			}
		}

		return results;
	}

	private final CommerceChannelLocalService _commerceChannelLocalService;
	private final CommerceDefinitionTermContributorRegistry
		_commerceDefinitionTermContributorRegistry;
	private final CommerceNotificationsRequestHelper
		_commerceNotificationsRequestHelper;
	private CommerceNotificationTemplate _commerceNotificationTemplate;
	private final CommerceNotificationTemplateService
		_commerceNotificationTemplateService;
	private final CommerceNotificationTypeRegistry
		_commerceNotificationTypeRegistry;

}