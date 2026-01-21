/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Marcellus Tavares
 */
@ExtendedObjectClassDefinition(
	category = "analytics-cloud", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.analytics.settings.configuration.AnalyticsConfiguration"
)
@ProviderType
public interface AnalyticsConfiguration {

	@Meta.AD(required = false)
	public String[] commerceSyncEnabledAnalyticsChannelIds();

	@Meta.AD(required = false)
	public boolean contentRecommenderMostPopularItemsEnabled();

	@Meta.AD(required = false)
	public boolean contentRecommenderUserPersonalizationEnabled();

	@Meta.AD(deflt = "true", required = false)
	public boolean firstSync();

	@Meta.AD(required = false)
	public String hostsAllowed();

	@Meta.AD(required = false)
	public String liferayAnalyticsConnectionType();

	@Meta.AD(required = false)
	public String liferayAnalyticsDataSourceId();

	@Meta.AD(required = false)
	public boolean liferayAnalyticsEnableAllGroupIds();

	@Meta.AD(required = false)
	public String liferayAnalyticsEndpointURL();

	@Meta.AD(required = false)
	public String liferayAnalyticsFaroBackendSecuritySignature();

	@Meta.AD(required = false)
	public String liferayAnalyticsFaroBackendURL();

	@Meta.AD(required = false)
	public String[] liferayAnalyticsGroupIds();

	@Meta.AD(required = false)
	public String liferayAnalyticsProjectId();

	@Meta.AD(required = false)
	public String liferayAnalyticsURL();

	@Meta.AD(required = false)
	public String[] previousCommerceSyncEnabledAnalyticsChannelIds();

	@Meta.AD(required = false)
	public boolean previousContentRecommenderMostPopularItemsEnabled();

	@Meta.AD(required = false)
	public boolean previousContentRecommenderUserPersonalizationEnabled();

	@Meta.AD(required = false)
	public boolean previousSyncAllAccounts();

	@Meta.AD(required = false)
	public boolean previousSyncAllContacts();

	@Meta.AD(required = false)
	public String[] previousSyncedAccountFieldNames();

	@Meta.AD(required = false)
	public String[] previousSyncedAccountGroupIds();

	@Meta.AD(required = false)
	public String[] previousSyncedCommerceChannelIds();

	@Meta.AD(required = false)
	public String[] previousSyncedContactFieldNames();

	@Meta.AD(required = false)
	public String[] previousSyncedOrderFieldNames();

	@Meta.AD(required = false)
	public String[] previousSyncedOrganizationIds();

	@Meta.AD(required = false)
	public String[] previousSyncedProductFieldNames();

	@Meta.AD(required = false)
	public String[] previousSyncedUserFieldNames();

	@Meta.AD(required = false)
	public String[] previousSyncedUserGroupIds();

	@Meta.AD(required = false)
	public String publicKey();

	@Meta.AD(required = false)
	public String siteReportingGrouping();

	@Meta.AD(required = false)
	public boolean syncAllAccounts();

	@Meta.AD(required = false)
	public boolean syncAllContacts();

	@Meta.AD(required = false)
	public String[] syncedAccountFieldNames();

	@Meta.AD(required = false)
	public String[] syncedAccountGroupIds();

	@Meta.AD(required = false)
	public String[] syncedCategoryFieldNames();

	@Meta.AD(required = false)
	public String[] syncedCommerceChannelIds();

	@Meta.AD(required = false)
	public String[] syncedContactFieldNames();

	@Meta.AD(required = false)
	public String[] syncedGroupIds();

	@Meta.AD(required = false)
	public String[] syncedOrderFieldNames();

	@Meta.AD(required = false)
	public String[] syncedOrderItemFieldNames();

	@Meta.AD(required = false)
	public String[] syncedOrganizationIds();

	@Meta.AD(required = false)
	public String[] syncedProductChannelFieldNames();

	@Meta.AD(required = false)
	public String[] syncedProductFieldNames();

	@Meta.AD(required = false)
	public String[] syncedUserFieldNames();

	@Meta.AD(required = false)
	public String[] syncedUserGroupIds();

	@Meta.AD(required = false)
	public String token();

	@Meta.AD(deflt = "true", required = false)
	public boolean wizardMode();

}