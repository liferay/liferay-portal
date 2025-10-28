/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.internal.configuration.persistence.listener;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.configuration.AnalyticsConfigurationRegistry;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(
	property = "model.class.name=com.liferay.analytics.settings.configuration.AnalyticsConfiguration",
	service = ConfigurationModelListener.class
)
public class AnalyticsConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(
		String pid, Dictionary<String, Object> properties) {

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsConfigurationRegistry.getAnalyticsConfiguration(pid);

		properties.put(
			"previousContentRecommenderMostPopularItemsEnabled",
			analyticsConfiguration.contentRecommenderMostPopularItemsEnabled());
		properties.put(
			"previousContentRecommenderUserPersonalizationEnabled",
			analyticsConfiguration.
				contentRecommenderUserPersonalizationEnabled());

		String[] commerceSyncEnabledAnalyticsChannelIds =
			analyticsConfiguration.commerceSyncEnabledAnalyticsChannelIds();

		if (commerceSyncEnabledAnalyticsChannelIds == null) {
			commerceSyncEnabledAnalyticsChannelIds = new String[0];
		}

		properties.put(
			"previousCommerceSyncEnabledAnalyticsChannelIds",
			commerceSyncEnabledAnalyticsChannelIds);

		properties.put(
			"previousSyncAllAccounts",
			analyticsConfiguration.syncAllAccounts());

		properties.put(
			"previousSyncAllContacts",
			analyticsConfiguration.syncAllContacts());

		String[] syncedAccountFieldNames =
			analyticsConfiguration.syncedAccountFieldNames();

		if (ArrayUtil.isNotEmpty(syncedAccountFieldNames)) {
			properties.put(
				"previousSyncedAccountFieldNames", syncedAccountFieldNames);
		}

		String[] syncedAccountGroupIds =
			analyticsConfiguration.syncedAccountGroupIds();

		if (!analyticsConfiguration.syncAllAccounts() &&
			ArrayUtil.isNotEmpty(syncedAccountGroupIds)) {

			properties.put(
				"previousSyncedAccountGroupIds", syncedAccountGroupIds);
		}
		else if (analyticsConfiguration.syncAllAccounts()) {
			properties.put("previousSyncedAccountGroupIds", new String[0]);
		}

		String[] syncedCommerceChannelIds =
			analyticsConfiguration.syncedCommerceChannelIds();

		if (syncedCommerceChannelIds == null) {
			syncedCommerceChannelIds = new String[0];
		}

		properties.put(
			"previousSyncedCommerceChannelIds", syncedCommerceChannelIds);

		String[] syncedContactFieldNames =
			analyticsConfiguration.syncedContactFieldNames();

		if (ArrayUtil.isNotEmpty(syncedContactFieldNames)) {
			properties.put(
				"previousSyncedContactFieldNames", syncedContactFieldNames);
		}

		String[] syncedOrderFieldNames =
			analyticsConfiguration.syncedOrderFieldNames();

		if (ArrayUtil.isNotEmpty(syncedOrderFieldNames)) {
			properties.put(
				"previousSyncedOrderFieldNames", syncedOrderFieldNames);
		}

		String[] syncedOrganizationIds =
			analyticsConfiguration.syncedOrganizationIds();

		if (!analyticsConfiguration.syncAllContacts()) {
			if (syncedOrganizationIds == null) {
				syncedOrganizationIds = new String[0];
			}

			properties.put(
				"previousSyncedOrganizationIds", syncedOrganizationIds);
		}
		else if (analyticsConfiguration.syncAllContacts()) {
			properties.put("previousSyncedOrganizationIds", new String[0]);
		}

		String[] syncedProductFieldNames =
			analyticsConfiguration.syncedProductFieldNames();

		if (ArrayUtil.isNotEmpty(syncedProductFieldNames)) {
			properties.put(
				"previousSyncedProductFieldNames", syncedProductFieldNames);
		}

		String[] syncedUserFieldNames =
			analyticsConfiguration.syncedUserFieldNames();

		if (ArrayUtil.isNotEmpty(syncedUserFieldNames)) {
			properties.put(
				"previousSyncedUserFieldNames", syncedUserFieldNames);
		}

		String[] syncedUserGroupIds =
			analyticsConfiguration.syncedUserGroupIds();

		if (!analyticsConfiguration.syncAllContacts()) {
			if (syncedUserGroupIds == null) {
				syncedUserGroupIds = new String[0];
			}

			properties.put("previousSyncedUserGroupIds", syncedUserGroupIds);
		}
		else if (analyticsConfiguration.syncAllContacts()) {
			properties.put("previousSyncedUserGroupIds", new String[0]);
		}

		String token = analyticsConfiguration.token();

		if (token != null) {
			properties.put("previousToken", token);
		}
	}

	@Reference
	private AnalyticsConfigurationRegistry _analyticsConfigurationRegistry;

}