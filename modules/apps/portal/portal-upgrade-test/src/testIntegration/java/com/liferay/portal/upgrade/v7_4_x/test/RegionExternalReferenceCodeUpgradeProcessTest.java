/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.test.util.CountryTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.BaseExternalReferenceCodeUpgradeProcessTestCase;
import com.liferay.portal.upgrade.v7_4_x.RegionExternalReferenceCodeUpgradeProcess;

import org.junit.After;
import org.junit.runner.RunWith;

/**
 * @author Balazs Breier
 */
@RunWith(Arquillian.class)
public class RegionExternalReferenceCodeUpgradeProcessTest
	extends BaseExternalReferenceCodeUpgradeProcessTestCase {

	@After
	public void tearDownCountry() throws PortalException {
		if (_country != null) {
			_countryLocalService.deleteCountry(_country);
		}
	}

	@Override
	protected ExternalReferenceCodeModel[] addExternalReferenceCodeModels(
			String tableName)
		throws PortalException {

		_country = _countryLocalService.addCountry(
			null, CountryTestUtil.randomA2(group.getCompanyId()),
			CountryTestUtil.randomA3(group.getCompanyId()), true, true, null,
			RandomTestUtil.randomString(),
			String.valueOf(RandomTestUtil.nextInt()), 0D, true, false, true,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Region region = _regionLocalService.addRegion(
			null, _country.getCountryId(), true, RandomTestUtil.randomString(),
			0D, "ZZ-1",
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		return new ExternalReferenceCodeModel[] {region};
	}

	@Override
	protected ExternalReferenceCodeModel fetchExternalReferenceCodeModel(
		ExternalReferenceCodeModel externalReferenceCodeModel,
		String tableName) {

		Region region = (Region)externalReferenceCodeModel;

		return _regionLocalService.fetchRegion(region.getRegionId());
	}

	@Override
	protected String getExternalReferenceCode(
		ExternalReferenceCodeModel externalReferenceCodeModel,
		String tableName) {

		Region region = (Region)externalReferenceCodeModel;

		Country country = _countryLocalService.fetchCountry(
			region.getCountryId());

		return country.getA2() + "_" + region.getRegionCode();
	}

	@Override
	protected String[] getTableNames() {
		return new String[] {"Region"};
	}

	@Override
	protected UpgradeProcess getUpgradeProcess() {
		return new RegionExternalReferenceCodeUpgradeProcess();
	}

	@Override
	protected UpgradeStepRegistrator getUpgradeStepRegistrator() {
		return null;
	}

	@Override
	protected Version getVersion() {
		return null;
	}

	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private RegionLocalService _regionLocalService;

}