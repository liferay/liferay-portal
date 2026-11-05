/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.upgrade.v1_4_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Chaitanya Sammetla
 */
@RunWith(Arquillian.class)
public class UpgradePortletPreferencesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUpgradeDifferentGroupSelected() throws Exception {
		_otherGroup = GroupTestUtil.addGroup();

		_testUpgrade(
			HashMapBuilder.put(
				"rootFolderExternalReferenceCode", StringPool.BLANK
			).put(
				"selectedGroupExternalReferenceCode",
				_otherGroup.getExternalReferenceCode()
			).put(
				"selectedRepositoryExternalReferenceCode", StringPool.BLANK
			).build(),
			_group,
			_getMap(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				_otherGroup.getGroupId()));
	}

	@Test
	public void testUpgradeInvalidRootFolder() throws Exception {
		_testUpgrade(
			Collections.emptyMap(), _group,
			_getMap(RandomTestUtil.randomLong(), _group.getGroupId()));
	}

	@Test
	public void testUpgradeLayoutSameGroupSelected() throws Exception {
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		_testUpgrade(
			HashMapBuilder.put(
				"rootFolderExternalReferenceCode",
				folder.getExternalReferenceCode()
			).put(
				"selectedRepositoryExternalReferenceCode", StringPool.BLANK
			).build(),
			LayoutTestUtil.addTypePortletLayout(_group),
			_getMap(folder.getFolderId(), _group.getGroupId()));
	}

	@Test
	public void testUpgradeSameGroupSelected() throws Exception {
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		_testUpgrade(
			HashMapBuilder.put(
				"rootFolderExternalReferenceCode",
				folder.getExternalReferenceCode()
			).put(
				"selectedRepositoryExternalReferenceCode", StringPool.BLANK
			).build(),
			_group, _getMap(folder.getFolderId(), _group.getGroupId()));
	}

	@Test
	public void testUpgradeStagingGroupLiveGroupSelected() throws Exception {
		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_group = _groupLocalService.getGroup(_group.getGroupId());

		Map<String, String> map = _getMap(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, _group.getGroupId());

		_setPortletPreferences(
			map, _group.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
			PortletKeys.PREFS_PLID_SHARED);

		Map<String, String> expectedMap = HashMapBuilder.put(
			"rootFolderExternalReferenceCode", StringPool.BLANK
		).put(
			"selectedRepositoryExternalReferenceCode", StringPool.BLANK
		).build();

		_testUpgrade(expectedMap, _group.getStagingGroup(), map);

		_assertPortletPreferences(
			expectedMap, _group.getGroupId(),
			PortletKeys.PREFS_OWNER_TYPE_GROUP, PortletKeys.PREFS_PLID_SHARED);
	}

	@Test
	public void testUpgradeStagingLayoutLiveGroupSelected() throws Exception {
		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_group = _groupLocalService.getGroup(_group.getGroupId());

		_testUpgrade(
			HashMapBuilder.put(
				"rootFolderExternalReferenceCode", StringPool.BLANK
			).put(
				"selectedRepositoryExternalReferenceCode", StringPool.BLANK
			).build(),
			LayoutTestUtil.addTypePortletLayout(_group.getStagingGroup()),
			_getMap(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				_group.getGroupId()));
	}

	@Test
	public void testUpgradeSubfolderSelected() throws Exception {
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		_testUpgrade(
			HashMapBuilder.put(
				"rootFolderExternalReferenceCode",
				folder.getExternalReferenceCode()
			).build(),
			_group,
			HashMapBuilder.put(
				"rootFolderId", String.valueOf(folder.getFolderId())
			).build());
	}

	private void _assertPortletPreferences(
			Map<String, String> expectedMap, long ownerId, int ownerType,
			long plid)
		throws Exception {

		for (String portletId : _PORTLET_IDS) {
			PortletPreferences portletPreferences = _getPortletPreferences(
				ownerId, ownerType, plid, portletId);

			Map<String, String[]> map = portletPreferences.getMap();

			Assert.assertEquals(
				MapUtil.toString(map), expectedMap.size(), map.size());

			for (Map.Entry<String, String> entry : expectedMap.entrySet()) {
				Assert.assertArrayEquals(
					new String[] {entry.getValue()}, map.get(entry.getKey()));
			}
		}
	}

	private Map<String, String> _getMap(
		long rootFolderId, long selectedRepositoryId) {

		return HashMapBuilder.put(
			"rootFolderId", String.valueOf(rootFolderId)
		).put(
			"selectedRepositoryId", String.valueOf(selectedRepositoryId)
		).build();
	}

	private PortletPreferences _getPortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws Exception {

		return _portletPreferencesLocalService.getPreferences(
			TestPropsValues.getCompanyId(), ownerId, ownerType, plid,
			portletId);
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.document.library.web.internal.upgrade.v1_1_0." +
					"UpgradePortletPreferences",
				LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator,
				"com.liferay.document.library.web.internal.upgrade.v1_4_0." +
					"UpgradePortletPreferences");

			upgradeProcess.upgrade();

			_entityCache.clearCache();
			_multiVMPool.clear();
		}
	}

	private void _setPortletPreferences(
			Map<String, String> map, long ownerId, int ownerType, long plid)
		throws Exception {

		for (String portletId : _PORTLET_IDS) {
			PortletPreferences portletPreferences = _getPortletPreferences(
				ownerId, ownerType, plid, portletId);

			for (Map.Entry<String, String> entry : map.entrySet()) {
				portletPreferences.setValue(entry.getKey(), entry.getValue());
			}

			portletPreferences.store();
		}
	}

	private void _testUpgrade(
			Map<String, String> expectedMap, Group group,
			Map<String, String> map)
		throws Exception {

		_testUpgrade(
			expectedMap, map, group.getGroupId(),
			PortletKeys.PREFS_OWNER_TYPE_GROUP, PortletKeys.PREFS_PLID_SHARED);
	}

	private void _testUpgrade(
			Map<String, String> expectedMap, Layout layout,
			Map<String, String> map)
		throws Exception {

		_testUpgrade(
			expectedMap, map, PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid());
	}

	private void _testUpgrade(
			Map<String, String> expectedMap, Map<String, String> map,
			long ownerId, int ownerType, long plid)
		throws Exception {

		_setPortletPreferences(map, ownerId, ownerType, plid);

		_runUpgrade();

		_assertPortletPreferences(expectedMap, ownerId, ownerType, plid);
	}

	private static final String[] _PORTLET_IDS = {
		DLPortletKeys.DOCUMENT_LIBRARY, DLPortletKeys.MEDIA_GALLERY_DISPLAY
	};

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@DeleteAfterTestRun
	private Group _otherGroup;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.document.library.web.internal.upgrade.registry.DLWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}