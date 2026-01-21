/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(
	category = "publications",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.change.tracking.web.internal.configuration.CTConfiguration",
	localization = "content/Language",
	name = "publications-portal-configuration-name"
)
public interface CTConfiguration {

	@Meta.AD(
		description = "hidden-applications-help", name = "hidden-applications",
		required = false
	)
	public String[] hiddenApplications();

	@Meta.AD(
		deflt = "com_liferay_dispatch_web_internal_portlet_DispatchPortlet, com_liferay_my_account_web_portlet_MyAccountPortlet, com_liferay_notification_web_internal_portlet_NotificationTemplatesPortlet, com_liferay_oauth_client_admin_web_internal_portlet_OAuthClientAdminPortlet, com_liferay_oauth2_provider_web_internal_portlet_OAuth2AdminPortlet, com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet, com_liferay_object_web_internal_list_type_portlet_portlet_ListTypeDefinitionsPortlet, com_liferay_password_policies_admin_web_portlet_PasswordPoliciesAdminPortlet, com_liferay_portal_language_override_web_internal_portlet_PLOPortlet, com_liferay_portal_search_tuning_rankings_web_internal_portlet_ResultRankingsPortlet, com_liferay_portal_search_tuning_synonyms_web_internal_portlet_SynonymsPortlet, com_liferay_search_experiences_web_internal_blueprint_admin_portlet_SXPBlueprintAdminPortlet, com_liferay_users_admin_web_portlet_UsersAdminPortlet",
		description = "production-only-application-help",
		name = "production-only-application", required = false
	)
	public String[] productionOnlyApplication();

	@Meta.AD(
		description = "show-all-data-when-reviewing-changes-help",
		name = "show-all-data-when-reviewing-changes", required = false
	)
	public boolean showAllData();

	@Meta.AD(
		deflt = "com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet, com_liferay_batch_planner_web_internal_portlet_BatchPlannerPortlet",
		description = "unsupported-application-help",
		name = "unsupported-application", required = false
	)
	public String[] unsupportedApplication();

}