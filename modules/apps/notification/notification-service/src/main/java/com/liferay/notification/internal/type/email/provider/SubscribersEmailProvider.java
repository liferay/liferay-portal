/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.email.provider;

import com.liferay.notification.context.NotificationContext;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Carolina Barbosa
 */
public class SubscribersEmailProvider implements EmailProvider {

	public SubscribersEmailProvider(
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		SubscriptionLocalService subscriptionLocalService,
		UserLocalService userLocalService) {

		_objectEntryFolderLocalService = objectEntryFolderLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_subscriptionLocalService = subscriptionLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public String provide(NotificationContext notificationContext, Object value)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			return null;
		}

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			notificationContext.getClassPK());

		if (objectEntry == null) {
			return StringUtil.merge(
				_getEmailAddresses(
					notificationContext.getCompanyId(),
					notificationContext.getClassName(),
					notificationContext.getClassPK()));
		}

		Set<String> emailAddresses = new HashSet<>();

		emailAddresses.addAll(
			_getEmailAddresses(
				objectEntry.getCompanyId(), ObjectEntryFolder.class.getName(),
				objectEntry.getGroupId()));
		emailAddresses.addAll(
			_getEmailAddresses(
				objectEntry.getCompanyId(), objectEntry.getModelClassName(),
				objectEntry.getObjectEntryId()));

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntry.getObjectEntryFolderId());

		if (objectEntryFolder == null) {
			return StringUtil.merge(emailAddresses);
		}

		emailAddresses.addAll(
			_getEmailAddresses(
				objectEntry.getCompanyId(), ObjectEntryFolder.class.getName(),
				objectEntryFolder.getObjectEntryFolderId()));

		for (long ancestorObjectEntryFolderId :
				objectEntryFolder.getAncestorObjectEntryFolderIds()) {

			emailAddresses.addAll(
				_getEmailAddresses(
					objectEntry.getCompanyId(),
					ObjectEntryFolder.class.getName(),
					ancestorObjectEntryFolderId));
		}

		return StringUtil.merge(emailAddresses);
	}

	private Set<String> _getEmailAddresses(
		long companyId, String className, long classPK) {

		Set<String> emailAddresses = new HashSet<>();

		for (Subscription subscription :
				_subscriptionLocalService.getSubscriptions(
					companyId, className, classPK)) {

			User user = _userLocalService.fetchUser(subscription.getUserId());

			emailAddresses.add(user.getEmailAddress());
		}

		return emailAddresses;
	}

	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final SubscriptionLocalService _subscriptionLocalService;
	private final UserLocalService _userLocalService;

}