package com.liferay.feature.flag.web.internal.service;

import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBag;
import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBagProvider;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagType;
import com.liferay.portal.kernel.model.Company;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

@Component(service = PortalInstanceLifecycleListener.class)
public class CompanyPortalInstanceLifecycleListener extends
	BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) {
		System.out.println("Disabling deprecated for company " + company.getWebId());

		FeatureFlagsBag featureFlagsBag =
			_featureFlagsBagProvider.getOrCreateFeatureFlagsBag(
				company.getCompanyId());

		List<FeatureFlag> deprecationFeatureFlags =
			featureFlagsBag.getFeatureFlags(
				FeatureFlagType.DEPRECATION.getPredicate());

		for (FeatureFlag deprecationFeatureFlag :
			deprecationFeatureFlags) {

			_featureFlagsBagProvider.setEnabled(
				company.getCompanyId(), deprecationFeatureFlag.getKey(),
				false);
		}
	}

	@Reference
	private FeatureFlagsBagProvider _featureFlagsBagProvider;
}
