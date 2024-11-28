/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.field.expression.handler;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.saml.opensaml.integration.field.expression.handler.UserFieldExpressionHandler;
import com.liferay.saml.opensaml.integration.processor.context.UserProcessorContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"display.index:Integer=200", "prefix=membership",
		"processing.index:Integer=200"
	},
	service = UserFieldExpressionHandler.class
)
public class MembershipsUserFieldExpressionHandler
	implements UserFieldExpressionHandler {

	@Override
	public void bindProcessorContext(
		UserProcessorContext userProcessorContext) {

		List<Long> userGroupIds = new ArrayList<>();

		UserProcessorContext.UserBind<User> userBind =
			userProcessorContext.bind(
				_processingIndex,
				(currentUser, newUser, serviceContext) -> {
					if (userProcessorContext.isDefined(
							String.class, "userGroups")) {

						_userGroupLocalService.setUserUserGroups(
							newUser.getUserId(),
							ArrayUtil.toArray(
								userGroupIds.toArray(new Long[0])));
					}

					return newUser;
				});

		userBind.mapStringArray(
			"userGroups",
			(user, values) -> {
				if (values == null) {
					return;
				}

				for (String value : values) {
					UserGroup userGroup = _userGroupLocalService.fetchUserGroup(
						user.getCompanyId(), value);

					if (userGroup != null) {
						userGroupIds.add(userGroup.getUserGroupId());
					}
					else if (_log.isWarnEnabled()) {
						_log.warn("Ignored unknown user group: " + value);
					}
				}
			});
	}

	@Override
	public User getLdapUser(
			long companyId, String userIdentifier,
			String userIdentifierExpression)
		throws Exception {

		return null;
	}

	@Override
	public String getSectionLabel(Locale locale) {
		return ResourceBundleUtil.getString(
			ResourceBundleUtil.getBundle(
				locale, DefaultUserFieldExpressionHandler.class),
			"user-memberships");
	}

	@Override
	public User getUser(
			long companyId, String userIdentifier,
			String userIdentifierExpression)
		throws PortalException {

		return null;
	}

	@Override
	public List<String> getValidFieldExpressions() {
		return _validFieldExpressions;
	}

	@Override
	public boolean isSupportedForUserMatching(String userIdentifier) {
		return false;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_processingIndex = GetterUtil.getInteger(
			properties.get("processing.index"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MembershipsUserFieldExpressionHandler.class);

	private int _processingIndex;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	private final List<String> _validFieldExpressions =
		Collections.unmodifiableList(Arrays.asList("userGroups"));

}