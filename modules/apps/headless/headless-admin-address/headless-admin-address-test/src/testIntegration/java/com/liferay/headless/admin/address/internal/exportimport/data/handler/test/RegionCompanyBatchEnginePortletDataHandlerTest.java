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
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CountryTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.index.IndexStatusManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
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

		_country = CountryTestUtil.addCountry(
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				TestPropsValues.getUserId()));

		_originalIndexReadOnly = _indexStatusManager.isIndexReadOnly(
			Region.class.getName());

		_indexStatusManager.setIndexReadOnly(Region.class.getName(), true);
	}

	@After
	public void tearDown() {
		_indexStatusManager.setIndexReadOnly(
			Region.class.getName(), _originalIndexReadOnly);
	}

	@Ignore("LPD-107066")
	@Override
	@Test
	public void testExportImportKeepCreatorData() throws Exception {
	}

	@Ignore("LPD-107066")
	@Override
	@Test
	public void testExportImportWithDateRange() throws Exception {
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		_regionLocalService.addRegion(
			externalReferenceCode, _country.getCountryId(), true,
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
			StringUtil.toUpperCase(RandomTestUtil.randomString(4)),
			ServiceContextTestUtil.getServiceContext(
				group.getCompanyId(), groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_regionLocalService.deleteRegion(
			_getRegionByExternalReferenceCode(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		Region region = _getRegionByExternalReferenceCode(
			externalReferenceCode, groupId);

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

		Region region = _getRegionByExternalReferenceCode(
			externalReferenceCode, groupId);

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

		Region region = _getRegionByExternalReferenceCode(
			externalReferenceCode, groupId);

		region.setUserId(userId);

		_regionLocalService.updateRegion(region);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Region region = _getRegionByExternalReferenceCode(
			externalReferenceCode, groupId);

		_regionLocalService.updateRegion(
			region.getExternalReferenceCode(), region.getRegionId(),
			region.isActive(), RandomTestUtil.randomString(),
			region.getPosition(), region.getRegionCode());
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		Region region = _getRegionByExternalReferenceCode(
			externalReferenceCode, groupId);

		region.setModifiedDate(modifiedDate);

		_regionLocalService.updateRegion(region);
	}

	private Region _getRegionByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _regionLocalService.getRegionByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());
	}

	private Country _country;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private IndexStatusManager _indexStatusManager;

	private boolean _originalIndexReadOnly;

	@Inject
	private RegionLocalService _regionLocalService;

}