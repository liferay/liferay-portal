/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.test.util.DepotStagingTestUtil;
import com.liferay.depot.test.util.DepotTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia Garcia
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class DepotEntryGroupRelStagingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_liveDepotEntry = _addDepotEntry();

		_liveGroup = GroupTestUtil.addGroup();
	}

	@Test
	public void testDepotEntryGroupRelAfterConnectingStagedDepotAndStagedSite()
		throws Exception {

		DepotTestUtil.withLocalStagingEnabled(
			_liveDepotEntry,
			stagingDepotEntry -> DepotTestUtil.withLocalStagingEnabled(
				_liveGroup,
				stagingGroup -> {
					DepotEntryGroupRel stagingDepotEntryGroupRel =
						_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
							stagingDepotEntry.getDepotEntryId(),
							stagingGroup.getGroupId());

					Assert.assertEquals(
						stagingDepotEntry.getDepotEntryId(),
						stagingDepotEntryGroupRel.getDepotEntryId());

					Assert.assertNull(
						_depotEntryGroupRelLocalService.
							fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
								stagingDepotEntry.getDepotEntryId(),
								_liveGroup.getGroupId()));
					Assert.assertNull(
						_depotEntryGroupRelLocalService.
							fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
								_liveDepotEntry.getDepotEntryId(),
								stagingGroup.getGroupId()));
				}));
	}

	@Test
	public void testDepotEntryGroupRelAfterPublishingConnectedSite()
		throws Exception {

		DepotTestUtil.withLocalStagingEnabled(
			_liveDepotEntry,
			stagingDepotEntry -> DepotTestUtil.withLocalStagingEnabled(
				_liveGroup,
				stagingGroup -> {
					DepotEntryGroupRel stagingDepotEntryGroupRel =
						_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
							stagingDepotEntry.getDepotEntryId(),
							stagingGroup.getGroupId());

					DepotStagingTestUtil.publishLayouts(
						stagingGroup, _liveGroup);

					DepotEntryGroupRel liveDepotEntryGroupRel =
						_depotEntryGroupRelLocalService.
							getDepotEntryGroupRelByUuidAndGroupId(
								stagingDepotEntryGroupRel.getUuid(),
								_liveGroup.getGroupId());

					stagingDepotEntryGroupRel =
						_depotEntryGroupRelLocalService.
							getDepotEntryGroupRelByUuidAndGroupId(
								stagingDepotEntryGroupRel.getUuid(),
								stagingGroup.getGroupId());

					Assert.assertEquals(
						liveDepotEntryGroupRel.isSearchable(),
						stagingDepotEntryGroupRel.isSearchable());
					Assert.assertEquals(
						liveDepotEntryGroupRel.isDdmStructuresAvailable(),
						stagingDepotEntryGroupRel.isDdmStructuresAvailable());

					Assert.assertEquals(
						_liveDepotEntry.getDepotEntryId(),
						liveDepotEntryGroupRel.getDepotEntryId());

					Assert.assertEquals(
						_liveGroup.getGroupId(),
						liveDepotEntryGroupRel.getToGroupId());

					Assert.assertEquals(
						stagingDepotEntry.getDepotEntryId(),
						stagingDepotEntryGroupRel.getDepotEntryId());

					Assert.assertEquals(
						stagingGroup.getGroupId(),
						stagingDepotEntryGroupRel.getToGroupId());

					Assert.assertNull(
						_depotEntryGroupRelLocalService.
							fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
								stagingDepotEntry.getDepotEntryId(),
								_liveGroup.getGroupId()));
					Assert.assertNull(
						_depotEntryGroupRelLocalService.
							fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
								_liveDepotEntry.getDepotEntryId(),
								stagingGroup.getGroupId()));
				}));
	}

	@Test
	public void testDepotEntryGroupRelAfterPublishingDeletedConnection()
		throws Exception {

		DepotTestUtil.withLocalStagingEnabled(
			_liveDepotEntry,
			stagingDepotEntry -> DepotTestUtil.withLocalStagingEnabled(
				_liveGroup,
				stagingGroup -> {
					DepotEntryGroupRel stagingDepotEntryGroupRel =
						_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
							stagingDepotEntry.getDepotEntryId(),
							stagingGroup.getGroupId());

					DepotStagingTestUtil.publishLayouts(
						stagingGroup, _liveGroup);

					_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
						stagingDepotEntryGroupRel);

					DepotEntryGroupRel liveDepotEntryGroupRel =
						_depotEntryGroupRelLocalService.
							getDepotEntryGroupRelByUuidAndGroupId(
								stagingDepotEntryGroupRel.getUuid(),
								_liveGroup.getGroupId());

					Assert.assertNotNull(liveDepotEntryGroupRel);

					DepotStagingTestUtil.publishLayouts(
						stagingGroup, _liveGroup);

					liveDepotEntryGroupRel =
						_depotEntryGroupRelLocalService.
							fetchDepotEntryGroupRelByUuidAndGroupId(
								stagingDepotEntryGroupRel.getUuid(),
								_liveGroup.getGroupId());

					Assert.assertNull(liveDepotEntryGroupRel);
				}));
	}

	@Test
	public void testDepotEntryGroupRelAfterPublishingModifiedConnection()
		throws Exception {

		DepotTestUtil.withLocalStagingEnabled(
			_liveDepotEntry,
			stagingDepotEntry -> DepotTestUtil.withLocalStagingEnabled(
				_liveGroup,
				stagingGroup -> {
					DepotEntryGroupRel stagingDepotEntryGroupRel =
						_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
							stagingDepotEntry.getDepotEntryId(),
							stagingGroup.getGroupId());

					DepotStagingTestUtil.publishLayouts(
						stagingGroup, _liveGroup);

					_depotEntryGroupRelLocalService.updateSearchable(
						stagingDepotEntryGroupRel.getDepotEntryGroupRelId(),
						false);

					DepotEntryGroupRel liveDepotEntryGroupRel =
						_depotEntryGroupRelLocalService.
							getDepotEntryGroupRelByUuidAndGroupId(
								stagingDepotEntryGroupRel.getUuid(),
								_liveGroup.getGroupId());

					Assert.assertTrue(liveDepotEntryGroupRel.isSearchable());

					DepotStagingTestUtil.publishLayouts(
						stagingGroup, _liveGroup);

					liveDepotEntryGroupRel =
						_depotEntryGroupRelLocalService.
							getDepotEntryGroupRelByUuidAndGroupId(
								stagingDepotEntryGroupRel.getUuid(),
								_liveGroup.getGroupId());

					Assert.assertFalse(liveDepotEntryGroupRel.isSearchable());
				}));
	}

	@Test
	public void testDepotEntryGroupRelAfterStagingDepot() throws Exception {
		DepotEntryGroupRel liveDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_liveDepotEntry.getDepotEntryId(), _liveGroup.getGroupId());

		DepotTestUtil.withLocalStagingEnabled(
			_liveDepotEntry,
			stagingDepotEntry -> {
				Assert.assertEquals(
					liveDepotEntryGroupRel,
					_depotEntryGroupRelLocalService.
						getDepotEntryGroupRelByUuidAndGroupId(
							liveDepotEntryGroupRel.getUuid(),
							_liveGroup.getGroupId()));

				Assert.assertNull(
					_depotEntryGroupRelLocalService.
						fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
							stagingDepotEntry.getDepotEntryId(),
							_liveGroup.getGroupId()));
			});
	}

	@Test
	public void testDepotEntryGroupRelAfterStagingDepotAndSite()
		throws Exception {

		DepotEntryGroupRel liveDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_liveDepotEntry.getDepotEntryId(), _liveGroup.getGroupId(),
				false);

		DepotTestUtil.withLocalStagingEnabled(
			_liveDepotEntry,
			stagingDepotEntry -> DepotTestUtil.withLocalStagingEnabled(
				_liveGroup,
				stagingGroup -> {
					DepotEntryGroupRel stagingDepotEntryGroupRel =
						_depotEntryGroupRelLocalService.
							getDepotEntryGroupRelByUuidAndGroupId(
								liveDepotEntryGroupRel.getUuid(),
								stagingGroup.getGroupId());

					Assert.assertEquals(
						liveDepotEntryGroupRel.isSearchable(),
						stagingDepotEntryGroupRel.isSearchable());
					Assert.assertEquals(
						liveDepotEntryGroupRel.isDdmStructuresAvailable(),
						stagingDepotEntryGroupRel.isDdmStructuresAvailable());

					Assert.assertEquals(
						_liveDepotEntry.getDepotEntryId(),
						liveDepotEntryGroupRel.getDepotEntryId());

					Assert.assertEquals(
						_liveGroup.getGroupId(),
						liveDepotEntryGroupRel.getToGroupId());

					Assert.assertEquals(
						stagingDepotEntry.getDepotEntryId(),
						stagingDepotEntryGroupRel.getDepotEntryId());

					Assert.assertEquals(
						stagingGroup.getGroupId(),
						stagingDepotEntryGroupRel.getToGroupId());

					Assert.assertNull(
						_depotEntryGroupRelLocalService.
							fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
								stagingDepotEntry.getDepotEntryId(),
								_liveGroup.getGroupId()));
					Assert.assertNull(
						_depotEntryGroupRelLocalService.
							fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
								_liveDepotEntry.getDepotEntryId(),
								stagingGroup.getGroupId()));
				}));
	}

	@Test
	public void testDepotEntryGroupRelAfterStagingSiteAndDepot()
		throws Exception {

		DepotEntryGroupRel liveDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_liveDepotEntry.getDepotEntryId(), _liveGroup.getGroupId());

		DepotTestUtil.withLocalStagingEnabled(
			_liveGroup,
			stagingGroup -> DepotTestUtil.withLocalStagingEnabled(
				_liveDepotEntry,
				stagingDepotEntry -> {
					Assert.assertEquals(
						liveDepotEntryGroupRel,
						_depotEntryGroupRelLocalService.
							getDepotEntryGroupRelByUuidAndGroupId(
								liveDepotEntryGroupRel.getUuid(),
								_liveGroup.getGroupId()));

					Assert.assertNotNull(
						_depotEntryGroupRelLocalService.
							fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
								stagingDepotEntry.getDepotEntryId(),
								stagingGroup.getGroupId()));
				}));
	}

	@Test
	public void testDepotEntryGroupRelConnectedToStagedSiteBeforeConnectingStagedDepotAndStagedSite()
		throws Exception {

		DepotEntryGroupRel livingDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_liveDepotEntry.getDepotEntryId(), _liveGroup.getGroupId());

		Assert.assertEquals(
			_liveDepotEntry.getDepotEntryId(),
			livingDepotEntryGroupRel.getDepotEntryId());

		Assert.assertNotNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					_liveDepotEntry.getDepotEntryId(),
					_liveGroup.getGroupId()));

		DepotTestUtil.withLocalStagingEnabled(
			_liveGroup,
			stagingGroup -> {
				Assert.assertNotNull(
					_depotEntryGroupRelLocalService.
						fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
							_liveDepotEntry.getDepotEntryId(),
							stagingGroup.getGroupId()));

				DepotTestUtil.withLocalStagingEnabled(
					_liveDepotEntry,
					stagingDepotEntry -> Assert.assertNotNull(
						_depotEntryGroupRelLocalService.
							fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
								stagingDepotEntry.getDepotEntryId(),
								stagingGroup.getGroupId())));
			});
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private DepotEntry _liveDepotEntry;

	@DeleteAfterTestRun
	private Group _liveGroup;

}