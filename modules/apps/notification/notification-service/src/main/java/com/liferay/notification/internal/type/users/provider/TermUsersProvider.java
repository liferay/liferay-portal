/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.users.provider;

import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.term.evaluator.NotificationTermEvaluator;
import com.liferay.notification.term.evaluator.NotificationTermEvaluatorTracker;
import com.liferay.notification.type.util.NotificationTypeUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 */
public class TermUsersProvider implements UsersProvider {

	public TermUsersProvider(
		NotificationTermEvaluatorTracker notificationTermEvaluatorTracker,
		PermissionCheckerFactory permissionCheckerFactory,
		RoleLocalService roleLocalService, RoleUsersProvider roleUsersProvider,
		UserLocalService userLocalService) {

		_notificationTermEvaluatorTracker = notificationTermEvaluatorTracker;
		_permissionCheckerFactory = permissionCheckerFactory;
		_roleLocalService = roleLocalService;
		_roleUsersProvider = roleUsersProvider;
		_userLocalService = userLocalService;
	}

	@Override
	public String getRecipientType() {
		return NotificationRecipientConstants.TYPE_TERM;
	}

	@Override
	public List<User> provide(
			NotificationContext notificationContext, List<String> values)
		throws PortalException {

		List<User> users = new ArrayList<>();

		List<String> screenNames = new ArrayList<>();
		List<String> termNames = new ArrayList<>();

		for (String value : values) {
			if (!NotificationTypeUtil.isTermValue(value)) {
				screenNames.add(value);
			}
			else {
				termNames.add(value);
			}
		}

		users.addAll(
			TransformUtil.unsafeTransform(
				screenNames,
				screenName -> {
					User user = _userLocalService.getUserByScreenName(
						notificationContext.getCompanyId(), screenName);

					if (!ModelResourcePermissionUtil.contains(
							_permissionCheckerFactory.create(user),
							notificationContext.getGroupId(),
							notificationContext.getClassName(),
							notificationContext.getClassPK(),
							ActionKeys.VIEW)) {

						return null;
					}

					return user;
				}));

		for (NotificationTermEvaluator notificationTermEvaluator :
				_notificationTermEvaluatorTracker.getNotificationTermEvaluators(
					notificationContext.getClassName())) {

			users.addAll(
				TransformUtil.unsafeTransform(
					termNames,
					termName -> {
						String termValue = notificationTermEvaluator.evaluate(
							NotificationTermEvaluator.Context.RECIPIENT,
							notificationContext.getTermValues(), termName);

						if (Objects.equals(termName, termValue)) {
							return null;
						}

						Role role = _roleLocalService.fetchRole(
							GetterUtil.getLong(termValue));

						if (role != null) {
							users.addAll(
								_roleUsersProvider.provide(
									notificationContext,
									Collections.singletonList(role.getName())));

							return null;
						}

						User user = _userLocalService.getUser(
							GetterUtil.getLong(termValue));

						if (!ModelResourcePermissionUtil.contains(
								_permissionCheckerFactory.create(user),
								notificationContext.getGroupId(),
								notificationContext.getClassName(),
								notificationContext.getClassPK(),
								ActionKeys.VIEW)) {

							return null;
						}

						return user;
					}));
		}

		return users;
	}

	private final NotificationTermEvaluatorTracker
		_notificationTermEvaluatorTracker;
	private final PermissionCheckerFactory _permissionCheckerFactory;
	private final RoleLocalService _roleLocalService;
	private final RoleUsersProvider _roleUsersProvider;
	private final UserLocalService _userLocalService;

}