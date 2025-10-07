/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.resource.v1_0;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.dto.v1_0.DataSourceLiferayAnalyticsURL;
import com.liferay.analytics.settings.rest.dto.v1_0.DataSourceToken;
import com.liferay.analytics.settings.rest.internal.client.AnalyticsCloudClient;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.resource.v1_0.DataSourceResource;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.Http;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Riccardo Ferrari
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/data-source.properties",
	scope = ServiceScope.PROTOTYPE, service = DataSourceResource.class
)
public class DataSourceResourceImpl extends BaseDataSourceResourceImpl {

	@Override
	public void deleteDataSource() throws Exception {
		try {
			_analyticsCloudClient.disconnectAnalyticsDataSource(
				_configurationProvider.getCompanyConfiguration(
					AnalyticsConfiguration.class,
					contextCompany.getCompanyId()),
				_companyLocalService.getCompany(contextUser.getCompanyId()));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		_analyticsSettingsManager.deleteCompanyConfiguration(
			contextUser.getCompanyId());
	}

	@Override
	public DataSourceLiferayAnalyticsURL postDataSource(
			DataSourceToken dataSourceToken)
		throws Exception {

		Map<String, Object> properties =
			_analyticsCloudClient.connectAnalyticsDataSource(
				_companyLocalService.getCompany(contextUser.getCompanyId()),
				dataSourceToken.getToken());

		properties.put("token", dataSourceToken.getToken());

		_analyticsSettingsManager.updateCompanyConfiguration(
			contextUser.getCompanyId(), properties);

		DataSourceLiferayAnalyticsURL dataSourceLiferayAnalyticsURL =
			new DataSourceLiferayAnalyticsURL();

		dataSourceLiferayAnalyticsURL.setLiferayAnalyticsURL(
			() -> String.valueOf(properties.get("liferayAnalyticsURL")));

		return dataSourceLiferayAnalyticsURL;
	}

	@Activate
	protected void activate() {
		_analyticsCloudClient = new AnalyticsCloudClient(_http);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataSourceResourceImpl.class);

	private AnalyticsCloudClient _analyticsCloudClient;

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Http _http;

}