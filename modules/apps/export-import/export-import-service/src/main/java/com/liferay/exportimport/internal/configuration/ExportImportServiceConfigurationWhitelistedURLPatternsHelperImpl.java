/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.configuration;

import com.liferay.exportimport.configuration.ExportImportServiceConfiguration;
import com.liferay.exportimport.configuration.ExportImportServiceConfigurationWhitelistedURLPatternsHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.url.pattern.mapper.URLPatternMapper;
import com.liferay.petra.url.pattern.mapper.URLPatternMapperFactory;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael Bowerman
 */
@Component(
	configurationPid = "com.liferay.exportimport.configuration.ExportImportServiceConfiguration",
	service = ExportImportServiceConfigurationWhitelistedURLPatternsHelper.class
)
public class ExportImportServiceConfigurationWhitelistedURLPatternsHelperImpl
	implements ExportImportServiceConfigurationWhitelistedURLPatternsHelper {

	@Override
	public boolean isWhitelistedURL(long companyId, String url) {
		if (!_urlPatternMappers.containsKey(companyId)) {
			try {
				rebuildURLPatternMapper(companyId);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Unable to instantiate URL pattern mapper for ",
							"company ", companyId),
						exception);
				}
				else {
					_log.error(
						StringBundler.concat(
							"Unable to instantiate URL pattern mapper for ",
							"company ", companyId, ": ",
							exception.getMessage()));
				}

				return false;
			}
		}

		URLPatternMapper<Boolean> urlPatternMapper = _urlPatternMappers.get(
			companyId);

		if (urlPatternMapper == null) {
			return false;
		}

		Boolean result = urlPatternMapper.getValue(url);

		if (result == null) {
			return false;
		}

		return result;
	}

	@Override
	public void rebuildURLPatternMapper(long companyId) throws Exception {
		rebuildURLPatternMapper(companyId, null);
	}

	@Override
	public void rebuildURLPatternMapper(
			long companyId,
			ExportImportServiceConfiguration exportImportServiceConfiguration)
		throws Exception {

		if (exportImportServiceConfiguration == null) {
			exportImportServiceConfiguration =
				_configurationProvider.getCompanyConfiguration(
					ExportImportServiceConfiguration.class, companyId);
		}

		String[] whitelistedURLPatterns =
			exportImportServiceConfiguration.
				validateLayoutReferencesWhitelistedURLPatterns();

		if (ArrayUtil.isEmpty(whitelistedURLPatterns)) {
			_urlPatternMappers.put(companyId, null);

			return;
		}

		Map<String, Boolean> whitelistedURLPatternsMap = new HashMap<>();

		for (String whitelistedURLPattern : whitelistedURLPatterns) {
			whitelistedURLPatternsMap.put(whitelistedURLPattern, true);
		}

		_urlPatternMappers.put(
			companyId,
			URLPatternMapperFactory.create(whitelistedURLPatternsMap));
	}

	@Override
	public void rebuildURLPatternMappers() {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				try {
					rebuildURLPatternMapper(companyId);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Unable to instantiate URL pattern mapper for ",
								"company ", companyId),
							exception);
					}
					else {
						_log.error(
							StringBundler.concat(
								"Unable to instantiate URL pattern mapper for ",
								"company ", companyId, ": ",
								exception.getMessage()));
					}
				}
			});
	}

	@Override
	public void removeURLPatternMapper(long companyId) {
		_urlPatternMappers.remove(companyId);
	}

	@Override
	public void removeURLPatternMappers() {
		_urlPatternMappers.clear();
	}

	@Activate
	protected void activate() {
		removeURLPatternMappers();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportServiceConfigurationWhitelistedURLPatternsHelperImpl.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private final Map<Long, URLPatternMapper<Boolean>> _urlPatternMappers =
		Collections.synchronizedMap(new HashMap<>());

}