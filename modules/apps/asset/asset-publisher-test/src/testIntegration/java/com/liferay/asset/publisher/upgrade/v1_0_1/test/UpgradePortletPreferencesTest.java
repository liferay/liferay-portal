/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.upgrade.v1_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
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
	}

	@Test
	public void testUpgrade() throws Exception {
		Map<String, String> map = _getRandomStringMap(
			ListUtil.fromMapKeys(_classNamesMap));

		_assertUpgrade(_getReplacedClassNamesMap(map), map);
	}

	@Test
	public void testUpgradeWithoutUpdating() throws Exception {
		Map<String, String> map = _getRandomStringMap(
			ListUtil.fromMapValues(_classNamesMap));

		_assertUpgrade(map, map);
	}

	private String _addManualSelectionAssetPublisherPortletToLayout(
			Map<String, String> map)
		throws Exception {

		String portletId = LayoutTestUtil.addPortletToLayout(
			_layout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			_getPreferenceMap(map));

		_assertPortletPreferences(portletId, map);

		return portletId;
	}

	private void _assertPortletPreferences(
			String portletId, Map<String, String> expectedMap)
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, portletId);

		Assert.assertEquals(
			"manual", portletPreferences.getValue("selectionStyle", null));

		String[] assetEntryXmls = portletPreferences.getValues(
			"assetEntryXml", null);

		Assert.assertNotNull(assetEntryXmls);

		Assert.assertEquals(
			assetEntryXmls.toString(), expectedMap.size(),
			assetEntryXmls.length);

		for (String assetEntryXml : assetEntryXmls) {
			Document document = _saxReader.read(assetEntryXml);

			Element rootElement = document.getRootElement();

			Element assetEntryTypeElement = rootElement.element(
				"asset-entry-type");

			Assert.assertNotNull(assetEntryTypeElement);

			String assetEntryType = assetEntryTypeElement.getText();

			Assert.assertTrue(expectedMap.containsKey(assetEntryType));

			Element assetEntryUuidElement = rootElement.element(
				"asset-entry-uuid");

			Assert.assertNotNull(assetEntryUuidElement);

			Assert.assertEquals(
				expectedMap.get(assetEntryType),
				assetEntryUuidElement.getText());
		}
	}

	private void _assertUpgrade(
			Map<String, String> expectedMap, Map<String, String> map)
		throws Exception {

		String portletId = _addManualSelectionAssetPublisherPortletToLayout(
			map);

		_runUpgrade();

		_assertPortletPreferences(portletId, expectedMap);
	}

	private String _getAssetEntryXml(
			String assetEntryType, String assetEntryUuid)
		throws Exception {

		Document document = _saxReader.createDocument(StringPool.UTF8);

		Element assetEntryElement = document.addElement("asset-entry");

		Element assetEntryTypeElement = assetEntryElement.addElement(
			"asset-entry-type");

		assetEntryTypeElement.addText(assetEntryType);

		Element assetEntryUuidElement = assetEntryElement.addElement(
			"asset-entry-uuid");

		assetEntryUuidElement.addText(assetEntryUuid);

		return document.formattedString(StringPool.BLANK);
	}

	private String[] _getAssetEntryXMLs(Map<String, String> map)
		throws Exception {

		List<String> assetEntryXMLs = new ArrayList<>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			assetEntryXMLs.add(
				_getAssetEntryXml(entry.getKey(), entry.getValue()));
		}

		return assetEntryXMLs.toArray(new String[0]);
	}

	private HashMap<String, String[]> _getPreferenceMap(Map<String, String> map)
		throws Exception {

		return HashMapBuilder.put(
			"assetEntryXml", _getAssetEntryXMLs(map)
		).put(
			"selectionStyle", new String[] {"manual"}
		).build();
	}

	private Map<String, String> _getRandomStringMap(List<String> keys) {
		Map<String, String> map = new HashMap<>();

		for (String key : keys) {
			map.put(key, RandomTestUtil.randomString());
		}

		return map;
	}

	private Map<String, String> _getReplacedClassNamesMap(
		Map<String, String> map) {

		Map<String, String> resultMap = new HashMap<>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String newKey = _classNamesMap.get(entry.getKey());

			Assert.assertNotNull(newKey);

			resultMap.put(newKey, entry.getValue());
		}

		return resultMap;
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

	private static final String _CLASS_NAME =
		"com.liferay.asset.publisher.web.internal.upgrade.v1_0_1." +
			"UpgradePortletPreferences";

	private static final Map<String, String> _classNamesMap =
		HashMapBuilder.put(
			"com.liferay.portlet.blogs.model.BlogsEntry",
			"com.liferay.blogs.model.BlogsEntry"
		).put(
			"com.liferay.portlet.bookmarks.model.BookmarksEntry",
			"com.liferay.bookmarks.model.BookmarksEntry"
		).put(
			"com.liferay.portlet.bookmarks.model.BookmarksFolder",
			"com.liferay.bookmarks.model.BookmarksFolder"
		).put(
			"com.liferay.portlet.documentlibrary.model.DLFileEntry",
			"com.liferay.document.library.kernel.model.DLFileEntry"
		).put(
			"com.liferay.portlet.documentlibrary.model.DLFolder",
			"com.liferay.document.library.kernel.model.DLFolder"
		).put(
			"com.liferay.portlet.journal.model.JournalArticle",
			"com.liferay.journal.model.JournalArticle"
		).put(
			"com.liferay.portlet.journal.model.JournalFolder",
			"com.liferay.journal.model.JournalFolder"
		).put(
			"com.liferay.portlet.messageboards.model.MBMessage",
			"com.liferay.message.boards.model.MBMessage"
		).put(
			"com.liferay.portlet.wiki.model.WikiPage",
			"com.liferay.wiki.model.WikiPage"
		).build();

	@Inject(
		filter = "(&(component.name=com.liferay.asset.publisher.web.internal.upgrade.registry.AssetPublisherWebUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SAXReader _saxReader;

}