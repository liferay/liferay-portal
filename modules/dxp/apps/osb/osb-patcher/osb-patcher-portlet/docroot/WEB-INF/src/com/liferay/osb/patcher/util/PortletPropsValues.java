/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.util.portlet.PortletProps;

/**
 * @author Zsolt Balogh
 */
public class PortletPropsValues {

	public static final String GITHUB_URL = PortletProps.get(
		PortletPropsKeys.GITHUB_URL);

	public static final String HELP_CENTER_API_PASSWORD = PortletProps.get(
		PortletPropsKeys.HELP_CENTER_API_PASSWORD);

	public static final String HELP_CENTER_API_USERNAME = PortletProps.get(
		PortletPropsKeys.HELP_CENTER_API_USERNAME);

	public static final String HELP_CENTER_FILE_REPO_ID = PortletProps.get(
		PortletPropsKeys.HELP_CENTER_FILE_REPO_ID);

	public static final String HELP_CENTER_FILE_REPO_URL = PortletProps.get(
		PortletPropsKeys.HELP_CENTER_FILE_REPO_URL);

	public static final String HELP_CENTER_GET_ACCOUNT_API_ENDPOINT =
		PortletProps.get(PortletPropsKeys.HELP_CENTER_GET_ACCOUNT_API_ENDPOINT);

	public static final String HELP_CENTER_JSONWS_URL = PortletProps.get(
		PortletPropsKeys.HELP_CENTER_JSONWS_URL);

	public static final String HELP_CENTER_TICKET_ATTACHMENT_API_ENDPOINT =
		PortletProps.get(
			PortletPropsKeys.HELP_CENTER_TICKET_ATTACHMENT_API_ENDPOINT);

	public static final String HELP_CENTER_TOKEN_TICKET_DIR = PortletProps.get(
		PortletPropsKeys.HELP_CENTER_TOKEN_TICKET_DIR);

	public static final String HELP_CENTER_URL = PortletProps.get(
		PortletPropsKeys.HELP_CENTER_URL);

	public static final String HOTFIX_MOUNT_PATH = PortletProps.get(
		PortletPropsKeys.HOTFIX_MOUNT_PATH);

	public static final String INFO_MODIFY_TICKETS_LIST_URL = PortletProps.get(
		PortletPropsKeys.INFO_MODIFY_TICKETS_LIST_URL);

	public static final String JENKINS_ADMIN_USER_TOKEN = PortletProps.get(
		PortletPropsKeys.JENKINS_ADMIN_USER_TOKEN);

	public static final String JENKINS_ADMIN_USERNAME = PortletProps.get(
		PortletPropsKeys.JENKINS_ADMIN_USERNAME);

	public static final String JENKINS_BUILD_WITH_PARAMETERS_PATH =
		PortletProps.get(PortletPropsKeys.JENKINS_BUILD_WITH_PARAMETERS_PATH);

	public static final String JENKINS_LOAD_BALANCER_BASE_INVOCATION_URL =
		PortletProps.get(
			PortletPropsKeys.JENKINS_LOAD_BALANCER_BASE_INVOCATION_URL);

	public static final boolean JENKINS_LOAD_BALANCER_ENABLED =
		GetterUtil.getBoolean(
			PortletProps.get(PortletPropsKeys.JENKINS_LOAD_BALANCER_ENABLED));

	public static final String JENKINS_TOKEN = PortletProps.get(
		PortletPropsKeys.JENKINS_TOKEN);

	public static final String JENKINS_URL = PortletProps.get(
		PortletPropsKeys.JENKINS_URL);

	public static final String JIRA_URL = PortletProps.get(
		PortletPropsKeys.JIRA_URL);

	public static final String LESA_URL = PortletProps.get(
		PortletPropsKeys.LESA_URL);

	public static final String LIFERAY_USERS_PROFILE_URL = PortletProps.get(
		PortletPropsKeys.LIFERAY_USERS_PROFILE_URL);

	public static final String[] OSB_PATCHER_ACCOUNT_WHITELIST =
		PortletProps.getArray(PortletPropsKeys.OSB_PATCHER_ACCOUNT_WHITELIST);

	public static final String OSB_PATCHER_AGENT_JENKINS_URL = PortletProps.get(
		PortletPropsKeys.OSB_PATCHER_AGENT_JENKINS_URL);

	public static final String OSB_PATCHER_BUILD_DOWNLOAD_URL =
		PortletProps.get(PortletPropsKeys.OSB_PATCHER_BUILD_DOWNLOAD_URL);

	public static final String OSB_PATCHER_GIT_TAG_PREFIX = PortletProps.get(
		PortletPropsKeys.OSB_PATCHER_GIT_TAG_PREFIX);

	public static final boolean OSB_PATCHER_JENKINS_REQUESTS_ENABLED =
		GetterUtil.getBoolean(
			PortletProps.get(
				PortletPropsKeys.OSB_PATCHER_JENKINS_REQUESTS_ENABLED));

	public static final String OSB_PATCHER_LIFERAY_PORTAL_REPOSITORY =
		PortletProps.get(
			PortletPropsKeys.OSB_PATCHER_LIFERAY_PORTAL_REPOSITORY);

	public static final String OSB_PATCHER_PUBSUB_CREDENTIAL_FILE_PATH =
		PortletProps.get(
			PortletPropsKeys.OSB_PATCHER_PUBSUB_CREDENTIAL_FILE_PATH);

	public static final String OSB_PATCHER_PUBSUB_PROJECT_ID = PortletProps.get(
		PortletPropsKeys.OSB_PATCHER_PUBSUB_PROJECT_ID);

	public static final String OSB_PATCHER_PUBSUB_SUBSCRIPTION_ID =
		PortletProps.get(PortletPropsKeys.OSB_PATCHER_PUBSUB_SUBSCRIPTION_ID);

	public static final boolean OSB_PATCHER_SCANNING_ENABLED =
		GetterUtil.getBoolean(
			PortletProps.get(PortletPropsKeys.OSB_PATCHER_SCANNING_ENABLED));

	public static final String OSB_PATCHER_SHARED_REQUEST_ADD_FIX_PATCHER_TYPE =
		PortletProps.get(
			PortletPropsKeys.OSB_PATCHER_SHARED_REQUEST_ADD_FIX_PATCHER_TYPE);

	public static final String
		OSB_PATCHER_SHARED_REQUEST_BUILD_PATCH_PATCHER_TYPE = PortletProps.get(
			PortletPropsKeys.
				OSB_PATCHER_SHARED_REQUEST_BUILD_PATCH_PATCHER_TYPE);

	public static final String OSB_PATCHER_STATUS_BUILD_JENKINS_PATH =
		PortletProps.get(
			PortletPropsKeys.OSB_PATCHER_STATUS_BUILD_JENKINS_PATH);

	public static final String OSB_PATCHER_STATUS_BUILD_JENKINS_TEST_PATH =
		PortletProps.get(
			PortletPropsKeys.OSB_PATCHER_STATUS_BUILD_JENKINS_TEST_PATH);

	public static final String OSB_PATCHER_STATUS_BUILD_PATH = PortletProps.get(
		PortletPropsKeys.OSB_PATCHER_STATUS_BUILD_PATH);

	public static final String OSB_PATCHER_STATUS_FIX_PATH = PortletProps.get(
		PortletPropsKeys.OSB_PATCHER_STATUS_FIX_PATH);

	public static final String OSB_PATCHER_STATUS_PATH = PortletProps.get(
		PortletPropsKeys.OSB_PATCHER_STATUS_PATH);

	public static final boolean OSB_PATCHER_TESTS_ENABLED =
		GetterUtil.getBoolean(
			PortletProps.get(PortletPropsKeys.OSB_PATCHER_TESTS_ENABLED));

	public static final String TROUBLESHOOTING_URL = PortletProps.get(
		PortletPropsKeys.TROUBLESHOOTING_URL);

}