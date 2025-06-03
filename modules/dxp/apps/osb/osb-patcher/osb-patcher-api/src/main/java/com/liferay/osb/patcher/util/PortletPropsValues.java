/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.configuration.PatcherConfigurationUtil;
import com.liferay.osb.patcher.constants.PortletPropsKeys;
import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Zsolt Balogh
 */
public class PortletPropsValues {

	public static final String GITHUB_URL = PatcherConfigurationUtil.get(
		PortletPropsKeys.GITHUB_URL);

	public static final String HELP_CENTER_API_PASSWORD =
		PatcherConfigurationUtil.get(PortletPropsKeys.HELP_CENTER_API_PASSWORD);

	public static final String HELP_CENTER_API_USERNAME =
		PatcherConfigurationUtil.get(PortletPropsKeys.HELP_CENTER_API_USERNAME);

	public static final String HELP_CENTER_FILE_REPO_ID =
		PatcherConfigurationUtil.get(PortletPropsKeys.HELP_CENTER_FILE_REPO_ID);

	public static final String HELP_CENTER_FILE_REPO_URL =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.HELP_CENTER_FILE_REPO_URL);

	public static final String HELP_CENTER_GET_ACCOUNT_API_ENDPOINT =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.HELP_CENTER_GET_ACCOUNT_API_ENDPOINT);

	public static final String HELP_CENTER_JSONWS_URL =
		PatcherConfigurationUtil.get(PortletPropsKeys.HELP_CENTER_JSONWS_URL);

	public static final String HELP_CENTER_TICKET_ATTACHMENT_API_ENDPOINT =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.HELP_CENTER_TICKET_ATTACHMENT_API_ENDPOINT);

	public static final String HELP_CENTER_TOKEN_TICKET_DIR =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.HELP_CENTER_TOKEN_TICKET_DIR);

	public static final String HELP_CENTER_URL = PatcherConfigurationUtil.get(
		PortletPropsKeys.HELP_CENTER_URL);

	public static final String HOTFIX_MOUNT_PATH = PatcherConfigurationUtil.get(
		PortletPropsKeys.HOTFIX_MOUNT_PATH);

	public static final String INFO_MODIFY_TICKETS_LIST_URL =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.INFO_MODIFY_TICKETS_LIST_URL);

	public static final String JENKINS_ADMIN_USER_TOKEN =
		PatcherConfigurationUtil.get(PortletPropsKeys.JENKINS_ADMIN_USER_TOKEN);

	public static final String JENKINS_ADMIN_USERNAME =
		PatcherConfigurationUtil.get(PortletPropsKeys.JENKINS_ADMIN_USERNAME);

	public static final String JENKINS_BUILD_WITH_PARAMETERS_PATH =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.JENKINS_BUILD_WITH_PARAMETERS_PATH);

	public static final String JENKINS_LOAD_BALANCER_BASE_INVOCATION_URL =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.JENKINS_LOAD_BALANCER_BASE_INVOCATION_URL);

	public static final boolean JENKINS_LOAD_BALANCER_ENABLED =
		GetterUtil.getBoolean(
			PatcherConfigurationUtil.get(
				PortletPropsKeys.JENKINS_LOAD_BALANCER_ENABLED));

	public static final String JENKINS_TOKEN = PatcherConfigurationUtil.get(
		PortletPropsKeys.JENKINS_TOKEN);

	public static final String JENKINS_URL = PatcherConfigurationUtil.get(
		PortletPropsKeys.JENKINS_URL);

	public static final String JIRA_URL = PatcherConfigurationUtil.get(
		PortletPropsKeys.JIRA_URL);

	public static final String LESA_URL = PatcherConfigurationUtil.get(
		PortletPropsKeys.LESA_URL);

	public static final String LIFERAY_USERS_PROFILE_URL =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.LIFERAY_USERS_PROFILE_URL);

	public static final String[] OSB_PATCHER_ACCOUNT_WHITELIST =
		PatcherConfigurationUtil.getArray(
			PortletPropsKeys.OSB_PATCHER_ACCOUNT_WHITELIST);

	public static final String OSB_PATCHER_AGENT_JENKINS_URL =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_AGENT_JENKINS_URL);

	public static final String OSB_PATCHER_BUILD_DOWNLOAD_URL =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_BUILD_DOWNLOAD_URL);

	public static final String OSB_PATCHER_GIT_TAG_PREFIX =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_GIT_TAG_PREFIX);

	public static final boolean OSB_PATCHER_JENKINS_REQUESTS_ENABLED =
		GetterUtil.getBoolean(
			PatcherConfigurationUtil.get(
				PortletPropsKeys.OSB_PATCHER_JENKINS_REQUESTS_ENABLED));

	public static final String OSB_PATCHER_LIFERAY_PORTAL_REPOSITORY =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_LIFERAY_PORTAL_REPOSITORY);

	public static final String OSB_PATCHER_PUBSUB_CREDENTIAL_FILE_PATH =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_PUBSUB_CREDENTIAL_FILE_PATH);

	public static final String OSB_PATCHER_PUBSUB_PROJECT_ID =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_PUBSUB_PROJECT_ID);

	public static final String OSB_PATCHER_PUBSUB_SUBSCRIPTION_ID =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_PUBSUB_SUBSCRIPTION_ID);

	public static final boolean OSB_PATCHER_SCANNING_ENABLED =
		GetterUtil.getBoolean(
			PatcherConfigurationUtil.get(
				PortletPropsKeys.OSB_PATCHER_SCANNING_ENABLED));

	public static final String OSB_PATCHER_SHARED_REQUEST_ADD_FIX_PATCHER_TYPE =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_SHARED_REQUEST_ADD_FIX_PATCHER_TYPE);

	public static final String
		OSB_PATCHER_SHARED_REQUEST_BUILD_PATCH_PATCHER_TYPE =
			PatcherConfigurationUtil.get(
				PortletPropsKeys.
					OSB_PATCHER_SHARED_REQUEST_BUILD_PATCH_PATCHER_TYPE);

	public static final String OSB_PATCHER_STATUS_BUILD_JENKINS_PATH =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_STATUS_BUILD_JENKINS_PATH);

	public static final String OSB_PATCHER_STATUS_BUILD_JENKINS_TEST_PATH =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_STATUS_BUILD_JENKINS_TEST_PATH);

	public static final String OSB_PATCHER_STATUS_BUILD_PATH =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_STATUS_BUILD_PATH);

	public static final String OSB_PATCHER_STATUS_FIX_PATH =
		PatcherConfigurationUtil.get(
			PortletPropsKeys.OSB_PATCHER_STATUS_FIX_PATH);

	public static final String OSB_PATCHER_STATUS_PATH =
		PatcherConfigurationUtil.get(PortletPropsKeys.OSB_PATCHER_STATUS_PATH);

	public static final boolean OSB_PATCHER_TESTS_ENABLED =
		GetterUtil.getBoolean(
			PatcherConfigurationUtil.get(
				PortletPropsKeys.OSB_PATCHER_TESTS_ENABLED));

	public static final String TROUBLESHOOTING_URL =
		PatcherConfigurationUtil.get(PortletPropsKeys.TROUBLESHOOTING_URL);

}