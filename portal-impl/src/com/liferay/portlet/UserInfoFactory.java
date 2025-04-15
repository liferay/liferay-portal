/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.CustomUserAttributes;
import com.liferay.portal.kernel.portlet.UserAttributes;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class UserInfoFactory {

	public static LinkedHashMap<String, String> getUserInfo(
		HttpServletRequest httpServletRequest, Portlet portlet) {

		if (httpServletRequest.getRemoteUser() == null) {
			return null;
		}

		LinkedHashMap<String, String> userInfo = new LinkedHashMap<>();

		try {
			userInfo = getUserInfo(
				PortalUtil.getUser(httpServletRequest), userInfo, portlet);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return userInfo;
	}

	public static LinkedHashMap<String, String> getUserInfo(
		long userId, Portlet portlet) {

		if (userId <= 0) {
			return null;
		}

		LinkedHashMap<String, String> userInfo = new LinkedHashMap<>();

		try {
			User user = UserLocalServiceUtil.getUserById(userId);

			userInfo = getUserInfo(user, userInfo, portlet);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return userInfo;
	}

	public static LinkedHashMap<String, String> getUserInfo(
		User user, LinkedHashMap<String, String> userInfo, Portlet portlet) {

		PortletApp portletApp = portlet.getPortletApp();

		// Liferay user attributes

		try {
			UserAttributes userAttributes = new UserAttributes(user);

			// Mandatory user attributes

			userInfo.put(
				UserAttributes.LIFERAY_COMPANY_ID,
				userAttributes.getValue(UserAttributes.LIFERAY_COMPANY_ID));

			userInfo.put(
				UserAttributes.LIFERAY_USER_ID,
				userAttributes.getValue(UserAttributes.LIFERAY_USER_ID));

			userInfo.put(
				UserAttributes.USER_NAME_FULL,
				userAttributes.getValue(UserAttributes.USER_NAME_FULL));

			// Portlet user attributes

			for (String userAttributeName : portletApp.getUserAttributes()) {
				String userAttributeValue = userAttributes.getValue(
					userAttributeName);

				if (userAttributeValue != null) {
					userInfo.put(userAttributeName, userAttributeValue);
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		Map<String, String> unmodifiableUserInfo = Collections.unmodifiableMap(
			(Map<String, String>)userInfo.clone());

		// Custom user attributes

		Map<String, CustomUserAttributes> customUserAttributesMap =
			new HashMap<>();

		Map<String, String> customUserAttributesClassNames =
			portletApp.getCustomUserAttributes();

		for (Map.Entry<String, String> entry :
				customUserAttributesClassNames.entrySet()) {

			String customUserAttributesClassName = entry.getValue();

			CustomUserAttributes customUserAttributes =
				customUserAttributesMap.get(customUserAttributesClassName);

			if (customUserAttributes == null) {
				if (portletApp.isWARFile()) {
					PortletContextBag portletContextBag =
						PortletContextBagPool.get(
							portletApp.getServletContextName());

					Map<String, CustomUserAttributes>
						portletContextBagCustomUserAttributes =
							portletContextBag.getCustomUserAttributes();

					customUserAttributes =
						portletContextBagCustomUserAttributes.get(
							customUserAttributesClassName);

					if (customUserAttributes != null) {
						customUserAttributes =
							(CustomUserAttributes)customUserAttributes.clone();
					}
				}
				else {
					customUserAttributes = _newInstance(
						customUserAttributesClassName);
				}

				if (customUserAttributes != null) {
					customUserAttributesMap.put(
						customUserAttributesClassName, customUserAttributes);
				}
			}

			if (customUserAttributes != null) {
				String userAttributeName = entry.getKey();

				String attrValue = customUserAttributes.getValue(
					userAttributeName, unmodifiableUserInfo);

				if (attrValue != null) {
					userInfo.put(userAttributeName, attrValue);
				}
			}
		}

		return userInfo;
	}

	private static CustomUserAttributes _newInstance(String className) {
		try {
			return (CustomUserAttributes)InstanceFactory.newInstance(className);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserInfoFactory.class);

}