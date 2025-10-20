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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

		Map<Long, User> usersMap = new LinkedHashMap<>();

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

		for (String screenName : screenNames) {
			User user = _userLocalService.getUserByScreenName(
				notificationContext.getCompanyId(), screenName);

			if (usersMap.containsKey(user.getUserId())) {
				continue;
			}

			if (_hasViewPermission(user, notificationContext)) {
				usersMap.put(user.getUserId(), user);
			}
		}

		for (NotificationTermEvaluator notificationTermEvaluator :
				_notificationTermEvaluatorTracker.getNotificationTermEvaluators(
					notificationContext.getClassName())) {

			for (String termName : termNames) {
				String termValue = notificationTermEvaluator.evaluate(
					NotificationTermEvaluator.Context.RECIPIENT,
					notificationContext.getTermValues(), termName);

				if (Objects.equals(termName, termValue)) {
					continue;
				}

				Matcher matcher = _numberPattern.matcher(termValue);

				while (matcher.find()) {
					long id = GetterUtil.getLong(Long.valueOf(matcher.group()));

					Role role = _roleLocalService.fetchRole(id);

					if (role != null) {
						for (User user :
								_roleUsersProvider.provide(
									notificationContext,
									Collections.singletonList(
										role.getName()))) {

							usersMap.put(user.getUserId(), user);
						}

						continue;
					}

					if (usersMap.containsKey(id)) {
						continue;
					}

					User user = _userLocalService.getUser(id);

					if (_hasViewPermission(user, notificationContext)) {
						usersMap.put(id, user);
					}
				}
			}
		}

		return new ArrayList<>(usersMap.values());
	}

	private boolean _hasViewPermission(
		User user, NotificationContext notificationContext) {

		return ModelResourcePermissionUtil.contains(
			_permissionCheckerFactory.create(user),
			notificationContext.getGroupId(),
			notificationContext.getClassName(),
			notificationContext.getClassPK(), ActionKeys.VIEW);
	}

	private static final Pattern _numberPattern = Pattern.compile("\\d+");

	private final NotificationTermEvaluatorTracker
		_notificationTermEvaluatorTracker;
	private final PermissionCheckerFactory _permissionCheckerFactory;
	private final RoleLocalService _roleLocalService;
	private final RoleUsersProvider _roleUsersProvider;
	private final UserLocalService _userLocalService;

}