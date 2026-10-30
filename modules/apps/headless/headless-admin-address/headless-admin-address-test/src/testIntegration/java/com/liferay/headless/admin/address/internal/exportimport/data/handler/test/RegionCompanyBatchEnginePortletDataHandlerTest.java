/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.search.IndexStatusManagerThreadLocal;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class RegionCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_indexReadOnly = IndexStatusManagerThreadLocal.isIndexReadOnly();

		IndexStatusManagerThreadLocal.setIndexReadOnly(true);
	}

	@After
	public void tearDown() {
		IndexStatusManagerThreadLocal.setIndexReadOnly(_indexReadOnly);
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		Country country = _getOrAddCountry(group);

		_regionLocalService.addRegion(
			externalReferenceCode, country.getCountryId(), true,
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
			StringUtil.toUpperCase(RandomTestUtil.randomString(4)),
			ServiceContextTestUtil.getServiceContext(
				group.getCompanyId(), groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_regionLocalService.deleteRegion(
			_getRegion(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		Region region = _getRegion(externalReferenceCode, groupId);

		return region.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return com.liferay.headless.admin.address.dto.v1_0.Region.class.
			getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		Region region = _getRegion(externalReferenceCode, groupId);

		return region.getRegionId();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		Region region = _regionLocalService.fetchRegionByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());

		if (region == null) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean supportsComments() {
		return false;
	}

	@Override
	protected boolean supportsEmptyEntries() {
		return false;
	}

	@Override
	protected boolean supportsPermissions() {
		return false;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		Region region = _getRegion(externalReferenceCode, groupId);

		region.setUserId(userId);

		_regionLocalService.updateRegion(region);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Region region = _getRegion(externalReferenceCode, groupId);

		_regionLocalService.updateRegion(
			region.getExternalReferenceCode(), region.getRegionId(),
			region.isActive(), RandomTestUtil.randomString(),
			region.getPosition(), region.getRegionCode());
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		Region region = _getRegion(externalReferenceCode, groupId);

		region.setModifiedDate(modifiedDate);

		_regionLocalService.updateRegion(region);
	}

	private Country _getOrAddCountry(Group group) throws Exception {
		Country country =
			_countryLocalService.fetchCountryByExternalReferenceCode(
				_COUNTRY_EXTERNAL_REFERENCE_CODE, group.getCompanyId());

		if (country != null) {
			return country;
		}

		return _countryLocalService.addCountry(
			_COUNTRY_EXTERNAL_REFERENCE_CODE,
			_getRandomA2(group.getCompanyId()),
			_getRandomA3(group.getCompanyId()), true, true,
			String.valueOf(RandomTestUtil.randomInt(100, 999)),
			RandomTestUtil.randomString(),
			String.valueOf(RandomTestUtil.nextInt()),
			RandomTestUtil.randomDouble(), true, true, true,
			ServiceContextTestUtil.getServiceContext(
				group.getCompanyId(), group.getGroupId(),
				TestPropsValues.getUserId()));
	}

	private String _getRandomA2(long companyId) {
		return StringUtil.toUpperCase(
			RandomTestUtil.randomString(
				2,
				randomString -> {
					Country country = _countryLocalService.fetchCountryByA2(
						companyId, StringUtil.toUpperCase(randomString));

					return country == null;
				}));
	}

	private String _getRandomA3(long companyId) {
		return StringUtil.toUpperCase(
			RandomTestUtil.randomString(
				3,
				randomString -> {
					Country country = _countryLocalService.fetchCountryByA3(
						companyId, StringUtil.toUpperCase(randomString));

					return country == null;
				}));
	}

	private Region _getRegion(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _regionLocalService.getRegionByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());
	}

	private static final String _COUNTRY_EXTERNAL_REFERENCE_CODE =
		"batch-engine-region-test-country";

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private boolean _indexReadOnly;

	@Inject
	private RegionLocalService _regionLocalService;

}