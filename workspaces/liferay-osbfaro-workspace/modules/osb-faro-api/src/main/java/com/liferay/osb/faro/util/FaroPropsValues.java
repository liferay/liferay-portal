/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Time;

/**
 * @author Leilany Ulisses
 * @author Marcos Martins
 */
public class FaroPropsValues {

	public static final String FARO_DEFAULT_WE_DEPLOY_KEY =
		GetterUtil.getString(
			PropsUtil.get("faro.default.we.deploy.key"),
			System.getenv("FARO_DEFAULT_WE_DEPLOY_KEY"));

	public static final String FARO_DEMO_CREATOR_METHOD = GetterUtil.getString(
		PropsUtil.get("faro.demo.creator.method"),
		System.getenv("FARO_DEMO_CREATOR_METHOD"));

	public static final boolean FARO_DEMO_DATA_PLATFORM_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get("faro.demo.data.platform.enabled"),
			GetterUtil.getBoolean(
				System.getenv("FARO_DEMO_DATA_PLATFORM_ENABLED")));

	public static final String FARO_MOCK_OSB_ACCOUNT_ENTRY =
		GetterUtil.getString(
			PropsUtil.get("faro.mock.osb.account.entry"),
			System.getenv("FARO_MOCK_OSB_ACCOUNT_ENTRY"));

	public static final String FARO_PROJECT_ID = GetterUtil.getString(
		PropsUtil.get("faro.project.id"), System.getenv("FARO_PROJECT_ID"));

	public static final String FARO_PROJECT_ID_PREFIX = GetterUtil.getString(
		PropsUtil.get("faro.project.id.prefix"),
		System.getenv("FARO_PROJECT_ID_PREFIX"));

	public static final String FARO_URL = GetterUtil.getString(
		PropsUtil.get("faro.url"), System.getenv("FARO_URL"));

	public static final boolean GRAPHQL_API_ENABLED = GetterUtil.getBoolean(
		PropsUtil.get("graphql.api.enabled"),
		GetterUtil.getBoolean(System.getenv("GRAPHQL_API_ENABLED")));

	public static final String ISSUES_EMAIL_ADDRESS = GetterUtil.getString(
		PropsUtil.get("issues.email.address"),
		System.getenv("ISSUES_EMAIL_ADDRESS"));

	public static final String MOCK_PROJECT_ID = GetterUtil.getString(
		PropsUtil.get("mock.project.id"), System.getenv("MOCK_PROJECT_ID"));

	public static final Integer OSB_API_PORT = GetterUtil.getInteger(
		PropsUtil.get("osb.api.port"),
		GetterUtil.getInteger(System.getenv("OSB_API_PORT")));

	public static final String OSB_API_PROTOCOL = GetterUtil.getString(
		PropsUtil.get("osb.api.protocol"), System.getenv("OSB_API_PROTOCOL"));

	public static final String OSB_API_TOKEN = GetterUtil.getString(
		PropsUtil.get("osb.api.token"), System.getenv("OSB_API_TOKEN"));

	public static final String OSB_API_URL = GetterUtil.getString(
		PropsUtil.get("osb.api.url"), System.getenv("OSB_API_URL"));

	public static final String OSB_ASAH_BACKEND_URL = GetterUtil.getString(
		PropsUtil.get("osb.asah.backend.url"),
		System.getenv("OSB_ASAH_BACKEND_URL"));

	public static final String OSB_ASAH_LOCAL_CLUSTER_URL =
		GetterUtil.getString(
			PropsUtil.get("osb.asah.local.cluster.url"),
			System.getenv("OSB_ASAH_LOCAL_CLUSTER_URL"));

	public static final String OSB_ASAH_PUBLISHER_URL = GetterUtil.getString(
		PropsUtil.get("osb.asah.publisher.url"),
		System.getenv("OSB_ASAH_PUBLISHER_URL"));

	public static final String OSB_ASAH_SECURITY_TOKEN = GetterUtil.getString(
		PropsUtil.get("osb.asah.security.token"),
		System.getenv("OSB_ASAH_SECURITY_TOKEN"));

	public static final String OSB_ASAH_TOKEN = GetterUtil.getString(
		PropsUtil.get("osb.asah.token"), System.getenv("OSB_ASAH_TOKEN"));

	public static final boolean OSB_FARO_ANTIVIRUS_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get("osb.faro.antivirus.enabled"),
			GetterUtil.getBoolean(System.getenv("OSB_FARO_ANTIVIRUS_ENABLED")));

	public static final String OSB_FARO_CLAMAV_HOSTNAME = GetterUtil.getString(
		PropsUtil.get("osb.faro.clamav.hostname"),
		System.getenv("OSB_FARO_CLAMAV_HOSTNAME"));

	public static final Integer OSB_FARO_CLAMAV_PORT = GetterUtil.getInteger(
		PropsUtil.get("osb.faro.clamav.port"),
		GetterUtil.getInteger(System.getenv("OSB_FARO_CLAMAV_PORT")));

	public static final Integer OSB_FARO_CLAMAV_TIMEOUT = GetterUtil.getInteger(
		PropsUtil.get("osb.faro.clamav.timeout"),
		GetterUtil.getInteger(System.getenv("OSB_FARO_CLAMAV_TIMEOUT")));

	public static final long OSB_FARO_PROJECT_PROVISION_BACKOFF_WINDOW_MILLIS =
		GetterUtil.getLong(
			PropsUtil.get("osb.faro.project.provision.backoff.window.millis"),
			GetterUtil.getLong(
				System.getenv(
					"OSB_FARO_PROJECT_PROVISION_BACKOFF_WINDOW_MILLIS"),
				Time.MINUTE * 5));

	public static final boolean OSB_FARO_SUBSCRIPTION_PUSH_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get("osb.faro.subscription.push.enabled"),
			GetterUtil.getBoolean(
				System.getenv("OSB_FARO_SUBSCRIPTION_PUSH_ENABLED")));

}