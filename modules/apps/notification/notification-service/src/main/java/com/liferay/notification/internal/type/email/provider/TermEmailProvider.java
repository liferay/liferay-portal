/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.email.provider;

import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.term.evaluator.NotificationTermEvaluator;
import com.liferay.notification.term.evaluator.NotificationTermEvaluatorTracker;
import com.liferay.notification.type.util.NotificationTypeUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Carolina Barbosa
 */
public class TermEmailProvider implements EmailProvider {

	public TermEmailProvider(
		NotificationTermEvaluatorTracker notificationTermEvaluatorTracker,
		PermissionCheckerFactory permissionCheckerFactory,
		RoleEmailProvider roleEmailProvider, RoleLocalService roleLocalService,
		UserLocalService userLocalService) {

		_notificationTermEvaluatorTracker = notificationTermEvaluatorTracker;
		_permissionCheckerFactory = permissionCheckerFactory;
		_roleEmailProvider = roleEmailProvider;
		_roleLocalService = roleLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public String provide(NotificationContext notificationContext, Object value)
		throws PortalException {

		if (value == null) {
			return StringPool.BLANK;
		}

		Set<String> emailAddresses = new HashSet<>();

		for (String termName : StringUtil.split(GetterUtil.getString(value))) {
			if (!NotificationTypeUtil.isTermValue(termName)) {
				continue;
			}

			for (NotificationTermEvaluator notificationTermEvaluator :
					_notificationTermEvaluatorTracker.
						getNotificationTermEvaluators(
							notificationContext.getClassName())) {

				String termValue = notificationTermEvaluator.evaluate(
					NotificationTermEvaluator.Context.RECIPIENT,
					notificationContext.getTermValues(), termName);

				if (Objects.equals(termName, termValue)) {
					continue;
				}

				if (NotificationTypeUtil.isEmailAddress(termValue)) {
					emailAddresses.add(termValue);

					continue;
				}

				Role role = _roleLocalService.fetchRole(
					GetterUtil.getLong(termValue));

				if (role != null) {
					return _roleEmailProvider.provide(
						notificationContext,
						Collections.singletonList(
							Collections.singletonMap(
								NotificationRecipientSettingConstants.
									NAME_ROLE_NAME,
								role.getName())));
				}

				User user = _userLocalService.getUser(
					GetterUtil.getLong(termValue));

				if (ModelResourcePermissionUtil.contains(
						_permissionCheckerFactory.create(user),
						notificationContext.getGroupId(),
						notificationContext.getClassName(),
						notificationContext.getClassPK(), ActionKeys.VIEW)) {

					emailAddresses.add(user.getEmailAddress());
				}
			}
		}

		return StringUtil.merge(emailAddresses);
	}

	private final NotificationTermEvaluatorTracker
		_notificationTermEvaluatorTracker;
	private final PermissionCheckerFactory _permissionCheckerFactory;
	private final RoleEmailProvider _roleEmailProvider;
	private final RoleLocalService _roleLocalService;
	private final UserLocalService _userLocalService;

}