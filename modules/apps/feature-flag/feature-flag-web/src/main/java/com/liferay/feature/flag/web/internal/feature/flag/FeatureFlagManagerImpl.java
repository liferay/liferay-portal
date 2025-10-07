/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.feature.flag;

import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import java.util.List;
import java.util.function.Predicate;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = FeatureFlagManager.class)
public class FeatureFlagManagerImpl implements FeatureFlagManager {

	@Override
	public List<FeatureFlag> getFeatureFlags(
		long companyId, Predicate<FeatureFlag> predicate) {

		FeatureFlagsBag featureFlagsBag =
			_featureFlagsBagProvider.getOrCreateFeatureFlagsBag(companyId);

		return featureFlagsBag.getFeatureFlags(predicate);
	}

	@Override
	public String getJSON(long companyId) {
		FeatureFlagsBag featureFlagsBag =
			_featureFlagsBagProvider.getOrCreateFeatureFlagsBag(companyId);

		return featureFlagsBag.getJSON();
	}

	@Override
	public boolean isEnabled(long companyId, String key) {
		if (_featureFlagsBagProvider.isSystemKey(key)) {
			companyId = CompanyConstants.SYSTEM;
		}

		FeatureFlagsBag featureFlagsBag =
			_featureFlagsBagProvider.getOrCreateFeatureFlagsBag(companyId);

		return featureFlagsBag.isEnabled(key);
	}

	@Override
	public boolean isEnabled(String key) {
		return isEnabled(CompanyThreadLocal.getCompanyId(), key);
	}

	@Reference(unbind = "-")
	protected void setFeatureFlagsBagProvider(
		FeatureFlagsBagProvider featureFlagsBagProvider) {

		_featureFlagsBagProvider = featureFlagsBagProvider.unwrapProxy();
	}

	private FeatureFlagsBagProvider _featureFlagsBagProvider;

}