/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.util;

import com.liferay.commerce.configuration.CommerceAccountGroupServiceConfiguration;
import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.configuration.CommerceOrderConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.exception.CommerceOrderGuestCheckoutException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.commerce.service.CommerceOrderLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SettingsException;
import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Gianmarco Brunialti Masera
 */
public class CommerceChannelConfigurationUtil {

	public static String getOpenCommerceOrderVisibilityScope(long groupId) {
		return getOpenCommerceOrderVisibilityScope(groupId, false);
	}

	public static String getOpenCommerceOrderVisibilityScope(
		long groupId, boolean skipAdminPermissionCheck) {

		if (!skipAdminPermissionCheck) {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (permissionChecker.isCompanyAdmin(
					permissionChecker.getCompanyId()) ||
				permissionChecker.isGroupAdmin(groupId)) {

				return StringPool.BLANK;
			}
		}

		try {
			CommerceOrderConfiguration commerceOrderConfiguration =
				ConfigurationProviderUtil.getConfiguration(
					CommerceOrderConfiguration.class,
					new GroupServiceSettingsLocator(
						groupId,
						CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

			return commerceOrderConfiguration.openOrdersVisibilityScope();
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		return StringPool.BLANK;
	}

	public static String getPlacedCommerceOrderVisibilityScope(long groupId) {
		return getPlacedCommerceOrderVisibilityScope(groupId, false);
	}

	public static String getPlacedCommerceOrderVisibilityScope(
		long groupId, boolean skipAdminPermissionCheck) {

		if (!skipAdminPermissionCheck) {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (permissionChecker.isCompanyAdmin(
					permissionChecker.getCompanyId()) ||
				permissionChecker.isGroupAdmin(groupId)) {

				return StringPool.BLANK;
			}
		}

		try {
			CommerceOrderConfiguration commerceOrderConfiguration =
				ConfigurationProviderUtil.getConfiguration(
					CommerceOrderConfiguration.class,
					new GroupServiceSettingsLocator(
						groupId,
						CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

			return commerceOrderConfiguration.placedOrdersVisibilityScope();
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		return StringPool.BLANK;
	}

	public static boolean isGuestBrowsingEnabled(long groupId) {
		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if ((group == null) ||
			!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-109588")) {

			return true;
		}

		return _isGuestBrowsingEnabled(groupId);
	}

	public static boolean isGuestBrowsingEnabled(
			PermissionChecker permissionChecker, long groupId)
		throws PortalException {

		if ((permissionChecker != null) && permissionChecker.isSignedIn()) {
			return true;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if ((group == null) ||
			!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-109588")) {

			return true;
		}

		String className = group.getClassName();

		if (!className.equals(CommerceChannel.class.getName())) {
			groupId =
				CommerceChannelLocalServiceUtil.
					getCommerceChannelGroupIdBySiteGroupId(groupId);

			if (groupId == 0) {
				return true;
			}
		}

		return _isGuestBrowsingEnabled(groupId);
	}

	public static boolean isUserNotificationScopeEnabled(long groupId) {
		try {
			CommerceOrderConfiguration commerceOrderConfiguration =
				ConfigurationProviderUtil.getConfiguration(
					CommerceOrderConfiguration.class,
					new GroupServiceSettingsLocator(
						groupId,
						CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

			return commerceOrderConfiguration.userNotificationScopeEnabled();
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		return false;
	}

	public static void validateGuestCheckout(long commerceOrderId)
		throws PortalException {

		CommerceOrder commerceOrder =
			CommerceOrderLocalServiceUtil.fetchCommerceOrder(commerceOrderId);

		if ((commerceOrder == null) || !commerceOrder.isGuestOrder()) {
			return;
		}

		long groupId = commerceOrder.getGroupId();

		if (!isGuestBrowsingEnabled(groupId)) {
			throw new CommerceOrderGuestCheckoutException();
		}

		try {
			CommerceAccountGroupServiceConfiguration
				commerceAccountGroupServiceConfiguration =
					ConfigurationProviderUtil.getConfiguration(
						CommerceAccountGroupServiceConfiguration.class,
						new GroupServiceSettingsLocator(
							groupId,
							CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

			if (commerceAccountGroupServiceConfiguration.commerceSiteType() !=
					CommerceChannelConstants.SITE_TYPE_B2B) {

				return;
			}

			CommerceOrderCheckoutConfiguration
				commerceOrderCheckoutConfiguration =
					ConfigurationProviderUtil.getConfiguration(
						CommerceOrderCheckoutConfiguration.class,
						new GroupServiceSettingsLocator(
							groupId,
							CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

			if (commerceOrderCheckoutConfiguration.guestCheckoutEnabled()) {
				return;
			}
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		throw new CommerceOrderGuestCheckoutException();
	}

	private static boolean _isGuestBrowsingEnabled(long groupId) {
		try {
			Settings settings = FallbackKeysSettingsUtil.getSettings(
				new GroupServiceSettingsLocator(
					groupId, CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

			return GetterUtil.getBoolean(
				settings.getValue("guestBrowsingEnabled", null), true);
		}
		catch (SettingsException settingsException) {
			if (_log.isDebugEnabled()) {
				_log.debug(settingsException);
			}
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceChannelConfigurationUtil.class);

}