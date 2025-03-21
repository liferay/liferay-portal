/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.upgrade.v1_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
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
 * @author Attila Bakay
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

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		_portletId = LayoutTestUtil.addPortletToLayout(
			_layout, DLPortletKeys.DOCUMENT_LIBRARY);
	}

	@Test
	public void testUpgradeHomeFolderSelected() throws Exception {
		_testUpgrade(
			_getMap(
				StringPool.BLANK, _group.getExternalReferenceCode(),
				StringPool.BLANK),
			_getMap(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				_group.getGroupId()));
	}

	@Test
	public void testUpgradeInvalidRepository() throws Exception {
		_testUpgrade(
			Collections.emptyMap(),
			_getMap(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomLong()));
	}

	@Test
	public void testUpgradeInvalidRootFolder() throws Exception {
		_testUpgrade(
			Collections.emptyMap(),
			_getMap(RandomTestUtil.randomLong(), _group.getGroupId()));
	}

	@Test
	public void testUpgradeMissingRepository() throws Exception {
		_testUpgrade(
			Collections.emptyMap(),
			HashMapBuilder.put(
				"rootFolderId",
				String.valueOf(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)
			).build());
	}

	@Test
	public void testUpgradeMissingRootFolder() throws Exception {
		_testUpgrade(
			Collections.emptyMap(),
			HashMapBuilder.put(
				"selectedRepositoryId", String.valueOf(_group.getGroupId())
			).build());
	}

	@Test
	public void testUpgradeRepositoryHomeFolderSelected() throws Exception {
		Repository repository = DLAppTestUtil.addRepository(
			_group.getGroupId());

		_testUpgrade(
			_getMap(
				StringPool.BLANK, _group.getExternalReferenceCode(),
				repository.getExternalReferenceCode()),
			_getMap(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				repository.getRepositoryId()));
	}

	@Test
	public void testUpgradeRepositorySubfolderSelected() throws Exception {
		Repository repository = DLAppTestUtil.addRepository(
			_group.getGroupId());

		Folder folder = DLAppTestUtil.addFolder(repository);

		_testUpgrade(
			_getMap(
				folder.getExternalReferenceCode(),
				_group.getExternalReferenceCode(),
				repository.getExternalReferenceCode()),
			_getMap(folder.getFolderId(), repository.getRepositoryId()));
	}

	@Test
	public void testUpgradeSubfolderSelected() throws Exception {
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		_testUpgrade(
			_getMap(
				folder.getExternalReferenceCode(),
				_group.getExternalReferenceCode(), StringPool.BLANK),
			_getMap(folder.getFolderId(), _group.getGroupId()));
	}

	@Test
	public void testUpgradeWithoutSelectedFolder() throws Exception {
		_testUpgrade(Collections.emptyMap(), Collections.emptyMap());
	}

	private void _assertPortletPreferences(Map<String, String> expectedMap)
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, _portletId);

		Map<String, String[]> map = portletPreferences.getMap();

		Assert.assertEquals(
			MapUtil.toString(map), expectedMap.size(), map.size());

		for (Map.Entry<String, String> entry : expectedMap.entrySet()) {
			Assert.assertTrue(entry.getKey(), map.containsKey(entry.getKey()));

			Assert.assertArrayEquals(
				new String[] {entry.getValue()}, map.get(entry.getKey()));
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

	private Map<String, String> _getMap(
		String rootFolderExternalReferenceCode,
		String selectedGroupExternalReferenceCode,
		String selectedRepositoryExternalReferenceCode) {

		return HashMapBuilder.put(
			"rootFolderExternalReferenceCode", rootFolderExternalReferenceCode
		).put(
			"selectedGroupExternalReferenceCode",
			selectedGroupExternalReferenceCode
		).put(
			"selectedRepositoryExternalReferenceCode",
			selectedRepositoryExternalReferenceCode
		).build();
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_entityCache.clearCache();
			_multiVMPool.clear();
		}
	}

	private void _testUpgrade(
			Map<String, String> expectedMap, Map<String, String> map)
		throws Exception {

		LayoutTestUtil.updateLayoutPortletPreferences(_layout, _portletId, map);

		_assertPortletPreferences(map);

		_runUpgrade();

		_assertPortletPreferences(expectedMap);
	}

	private static final String _CLASS_NAME =
		"com.liferay.document.library.web.internal.upgrade.v1_1_0." +
			"UpgradePortletPreferences";

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

	private String _portletId;

	@Inject(
		filter = "(&(component.name=com.liferay.document.library.web.internal.upgrade.registry.DLWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}