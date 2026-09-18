/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.parameter.contributor;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.search.experiences.blueprint.parameter.SXPParameter;
import com.liferay.search.experiences.blueprint.parameter.contributor.SXPParameterContributor;
import com.liferay.search.experiences.blueprint.parameter.contributor.SXPParameterContributorDefinition;
import com.liferay.search.experiences.internal.blueprint.parameter.DoubleSXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.StringSXPParameter;
import com.liferay.search.experiences.internal.configuration.IpstackConfiguration;
import com.liferay.search.experiences.internal.web.cache.IpstackWebCacheItem;

import java.beans.ExceptionListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Petteri Karttunen
 */
public class IpstackSXPParameterContributor implements SXPParameterContributor {

	public IpstackSXPParameterContributor(
		ConfigurationProvider configurationProvider) {

		_configurationProvider = configurationProvider;
	}

	@Override
	public void contribute(
		ExceptionListener exceptionListener, SearchContext searchContext,
		Set<SXPParameter> sxpParameters) {

		IpstackConfiguration ipstackConfiguration = _getIpstackConfiguration(
			searchContext.getCompanyId());

		if (!ipstackConfiguration.enabled()) {
			return;
		}

		String ipAddress = (String)searchContext.getAttribute(
			"search.experiences.ip.address");

		if (Validator.isNull(ipAddress)) {
			return;
		}

		JSONObject jsonObject = IpstackWebCacheItem.get(
			searchContext.getCompanyId(), exceptionListener, ipAddress,
			ipstackConfiguration);

		if (jsonObject.length() == 0) {
			return;
		}

		sxpParameters.add(
			new StringSXPParameter(
				"ipstack.city", true, jsonObject.getString("city")));
		sxpParameters.add(
			new StringSXPParameter(
				"ipstack.continent_code", true,
				jsonObject.getString("continent_code")));
		sxpParameters.add(
			new StringSXPParameter(
				"ipstack.continent_name", true,
				jsonObject.getString("continent_name")));
		sxpParameters.add(
			new StringSXPParameter(
				"ipstack.country_code", true,
				jsonObject.getString("country_code")));
		sxpParameters.add(
			new StringSXPParameter(
				"ipstack.country_name", true,
				jsonObject.getString("country_name")));
		sxpParameters.add(
			new DoubleSXPParameter(
				"ipstack.latitude", true, jsonObject.getDouble("latitude")));
		sxpParameters.add(
			new DoubleSXPParameter(
				"ipstack.longitude", true, jsonObject.getDouble("longitude")));
		sxpParameters.add(
			new StringSXPParameter(
				"ipstack.region_code", true,
				jsonObject.getString("region_code")));
		sxpParameters.add(
			new StringSXPParameter(
				"ipstack.region_name", true,
				jsonObject.getString("region_name")));
		sxpParameters.add(
			new StringSXPParameter(
				"ipstack.zip", true, jsonObject.getString("zip")));
	}

	@Override
	public String getSXPParameterCategoryNameKey() {
		return "ip";
	}

	@Override
	public List<SXPParameterContributorDefinition>
		getSXPParameterContributorDefinitions(long companyId, Locale locale) {

		IpstackConfiguration ipstackConfiguration = _getIpstackConfiguration(
			companyId);

		if (!ipstackConfiguration.enabled()) {
			return Collections.<SXPParameterContributorDefinition>emptyList();
		}

		return Arrays.asList(
			new SXPParameterContributorDefinition(
				StringSXPParameter.class, "city", "ipstack.city"),
			new SXPParameterContributorDefinition(
				StringSXPParameter.class, "continent-code",
				"ipstack.continent_code"),
			new SXPParameterContributorDefinition(
				StringSXPParameter.class, "continent-name",
				"ipstack.continent_name"),
			new SXPParameterContributorDefinition(
				StringSXPParameter.class, "country-code",
				"ipstack.country_code"),
			new SXPParameterContributorDefinition(
				StringSXPParameter.class, "country-name",
				"ipstack.country_name"),
			new SXPParameterContributorDefinition(
				DoubleSXPParameter.class, "latitude", "ipstack.latitude"),
			new SXPParameterContributorDefinition(
				DoubleSXPParameter.class, "longitude", "ipstack.longitude"),
			new SXPParameterContributorDefinition(
				StringSXPParameter.class, "region-code", "ipstack.region_code"),
			new SXPParameterContributorDefinition(
				StringSXPParameter.class, "region-name", "ipstack.region_name"),
			new SXPParameterContributorDefinition(
				StringSXPParameter.class, "zip", "ipstack.zip"));
	}

	private IpstackConfiguration _getIpstackConfiguration(long companyId) {
		try {
			return _configurationProvider.getCompanyConfiguration(
				IpstackConfiguration.class, companyId);
		}
		catch (ConfigurationException configurationException) {
			return ReflectionUtil.throwException(configurationException);
		}
	}

	private final ConfigurationProvider _configurationProvider;

}