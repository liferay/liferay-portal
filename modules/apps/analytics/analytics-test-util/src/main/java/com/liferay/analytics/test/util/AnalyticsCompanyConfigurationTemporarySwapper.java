/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.test.util;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

/**
 * @author Eudaldo Alonso
 */
public class AnalyticsCompanyConfigurationTemporarySwapper
	extends CompanyConfigurationTemporarySwapper {

	public AnalyticsCompanyConfigurationTemporarySwapper(long companyId)
		throws Exception {

		this(companyId, RandomTestUtil.randomString());
	}

	public AnalyticsCompanyConfigurationTemporarySwapper(
			long companyId, String dataSourceId)
		throws Exception {

		this(companyId, dataSourceId, true);
	}

	public AnalyticsCompanyConfigurationTemporarySwapper(
			long companyId, String dataSourceId, boolean enableAllGroupIds)
		throws Exception {

		this(
			companyId, dataSourceId, enableAllGroupIds,
			"http://" + RandomTestUtil.randomString());
	}

	public AnalyticsCompanyConfigurationTemporarySwapper(
			long companyId, String dataSourceId, boolean enableAllGroupIds,
			String faroBackendURL)
		throws Exception {

		super(
			companyId, AnalyticsConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"liferayAnalyticsDataSourceId", dataSourceId
			).put(
				"liferayAnalyticsEnableAllGroupIds", enableAllGroupIds
			).put(
				"liferayAnalyticsFaroBackendSecuritySignature",
				RandomTestUtil.randomString()
			).put(
				"liferayAnalyticsFaroBackendURL", faroBackendURL
			).build());
	}

}