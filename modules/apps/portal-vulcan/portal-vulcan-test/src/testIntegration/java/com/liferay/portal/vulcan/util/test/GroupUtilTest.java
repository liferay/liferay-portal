/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.GroupUtil;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class GroupUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws PortalException {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testGetAssetLibraryKey() throws Exception {
		Group depotEntryGroup = _depotEntry.getGroup();

		Assert.assertEquals(
			depotEntryGroup.getGroupKey(),
			GroupUtil.getAssetLibraryKey(depotEntryGroup));

		Assert.assertNull(
			GroupUtil.getAssetLibraryKey(
				_groupLocalService.getGroup(TestPropsValues.getGroupId())));
	}

	@Test
	public void testGetDepotGroupId() throws Exception {
		_testGetDepotGroupId();

		Group depotEntryGroup = _depotEntry.getGroup();

		Assert.assertNull(
			GroupUtil.getDepotGroupId(
				String.valueOf(depotEntryGroup.getGroupId()),
				depotEntryGroup.getCompanyId(), _depotEntryLocalService,
				_groupLocalService));
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testGetDepotGroupIdWithFF() throws Exception {
		_testGetDepotGroupId();

		Group depotEntryGroup = _depotEntry.getGroup();

		Assert.assertEquals(
			Long.valueOf(depotEntryGroup.getGroupId()),
			GroupUtil.getDepotGroupId(
				String.valueOf(depotEntryGroup.getGroupId()),
				depotEntryGroup.getCompanyId(), _depotEntryLocalService,
				_groupLocalService));
	}

	@Test
	public void testGetGroupId() throws Exception {
		_testGetGroupId(
			_groupLocalService.getGroup(TestPropsValues.getGroupId()));

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		try {
			_testGetGroupId(userGroup.getGroup());
		}
		finally {
			if (userGroup != null) {
				_userGroupLocalService.deleteUserGroup(userGroup);
			}
		}
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testGetGroupIdWithFF() throws Exception {
		Group group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		Assume.assumeNotNull(group);

		_testGetGroupId(group);
	}

	@Test
	public void testGetSiteExternalReferenceCode() throws Exception {
		Assert.assertNull(
			GroupUtil.getSiteExternalReferenceCode(_depotEntry.getGroup()));

		Group group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		Assert.assertEquals(
			group.getExternalReferenceCode(),
			GroupUtil.getSiteExternalReferenceCode(group));
	}

	@Test
	public void testGetSiteId() throws Exception {
		Assert.assertNull(GroupUtil.getSiteId(_depotEntry.getGroup()));

		Group group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		Assert.assertEquals(
			Long.valueOf(group.getGroupId()), GroupUtil.getSiteId(group));
	}

	private void _testGetDepotGroupId() throws Exception {
		Group depotEntryGroup = _depotEntry.getGroup();

		Assert.assertEquals(
			Long.valueOf(depotEntryGroup.getGroupId()),
			GroupUtil.getDepotGroupId(
				String.valueOf(_depotEntry.getDepotEntryId()),
				depotEntryGroup.getCompanyId(), _depotEntryLocalService,
				_groupLocalService));
		Assert.assertEquals(
			Long.valueOf(depotEntryGroup.getGroupId()),
			GroupUtil.getDepotGroupId(
				depotEntryGroup.getGroupKey(), depotEntryGroup.getCompanyId(),
				_depotEntryLocalService, _groupLocalService));
	}

	private void _testGetGroupId(Group group) {
		Assert.assertEquals(
			Long.valueOf(group.getGroupId()),
			GroupUtil.getGroupId(
				group.getCompanyId(), group.getExternalReferenceCode(),
				_groupLocalService));
		Assert.assertEquals(
			Long.valueOf(group.getGroupId()),
			GroupUtil.getGroupId(
				group.getCompanyId(), String.valueOf(group.getGroupId()),
				_groupLocalService));
		Assert.assertEquals(
			Long.valueOf(group.getGroupId()),
			GroupUtil.getGroupId(
				group.getCompanyId(), group.getGroupKey(), _groupLocalService));
	}

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

}