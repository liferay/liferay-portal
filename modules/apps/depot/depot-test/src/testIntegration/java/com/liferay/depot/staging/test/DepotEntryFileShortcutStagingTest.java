/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.test.util.DepotStagingTestUtil;
import com.liferay.depot.test.util.DepotTestUtil;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
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
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class DepotEntryFileShortcutStagingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_liveDepotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_liveGroup = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-112613")
	public void testDepotEntryFileShortcutAfterPublishingConnectedSite()
		throws Exception {

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_liveDepotEntry.getDepotEntryId(), _liveGroup.getGroupId());

		DepotTestUtil.withLocalStagingEnabled(
			_liveDepotEntry,
			stagingDepotEntry -> DepotTestUtil.withLocalStagingEnabled(
				_liveGroup,
				stagingGroup -> {
					FileEntry stagingFileEntry =
						_dlAppLocalService.addFileEntry(
							null, TestPropsValues.getUserId(),
							stagingDepotEntry.getGroupId(),
							DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
							RandomTestUtil.randomString(),
							ContentTypes.IMAGE_JPEG,
							RandomTestUtil.randomBytes(), null, null, null,
							ServiceContextTestUtil.getServiceContext(
								stagingDepotEntry.getGroupId()));

					DepotStagingTestUtil.publishLayouts(
						stagingDepotEntry.getGroup(),
						_liveDepotEntry.getGroup());

					FileShortcut stagingFileShortcut =
						_dlAppLocalService.addFileShortcut(
							null, TestPropsValues.getUserId(),
							stagingGroup.getGroupId(),
							DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
							stagingFileEntry.getFileEntryId(),
							ServiceContextTestUtil.getServiceContext(
								stagingGroup.getGroupId()));

					DepotStagingTestUtil.publishLayouts(
						stagingGroup, _liveGroup);

					DLFileShortcut liveDLFileShortcut =
						_dlFileShortcutLocalService.
							getDLFileShortcutByUuidAndGroupId(
								stagingFileShortcut.getUuid(),
								_liveGroup.getGroupId());
					FileEntry liveFileEntry =
						_dlAppLocalService.getFileEntryByUuidAndGroupId(
							stagingFileEntry.getUuid(),
							_liveDepotEntry.getGroupId());

					Assert.assertEquals(
						liveFileEntry.getFileEntryId(),
						liveDLFileShortcut.getToFileEntryId());
				}));
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileShortcutLocalService _dlFileShortcutLocalService;

	@DeleteAfterTestRun
	private DepotEntry _liveDepotEntry;

	@DeleteAfterTestRun
	private Group _liveGroup;

}