/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.upgrade.v1_0_5.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balázs Sáfrány-Kovalik
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
		_assertUpgrade(Boolean.FALSE.toString(), "select-more-than-one");
	}

	@Test
	public void testUpgradeWithoutUpdating() throws Exception {
		String string = RandomTestUtil.randomString();

		_assertUpgrade(string, string);
	}

	private void _assertUpgrade(String expected, String value)
		throws Exception {

		String portletId = LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), _layout,
			AssetPublisherPortletKeys.ASSET_PUBLISHER, "column-1",
			HashMapBuilder.put(
				"anyClassType", new String[] {value}
			).put(
				"anyClassTypeDLFileEntryAssetRendererFactory",
				new String[] {value}
			).put(
				"anyClassTypeJournalArticleAssetRendererFactory",
				new String[] {value}
			).build());

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, portletId);

		Assert.assertEquals(
			value, portletPreferences.getValue("anyClassType", null));
		Assert.assertEquals(
			value,
			portletPreferences.getValue(
				"anyClassTypeDLFileEntryAssetRendererFactory", null));
		Assert.assertEquals(
			value,
			portletPreferences.getValue(
				"anyClassTypeJournalArticleAssetRendererFactory", null));

		_runUpgrade();

		portletPreferences = LayoutTestUtil.getPortletPreferences(
			_layout, portletId);

		String anyClassType = portletPreferences.getValue("anyClassType", null);

		Assert.assertEquals(expected, anyClassType);

		String anyClassTypeDLFileEntryAssetRendererFactory =
			portletPreferences.getValue(
				"anyClassTypeDLFileEntryAssetRendererFactory", null);

		Assert.assertEquals(
			expected, anyClassTypeDLFileEntryAssetRendererFactory);

		String anyClassTypeJournalArticleAssetRendererFactory =
			portletPreferences.getValue(
				"anyClassTypeJournalArticleAssetRendererFactory", null);

		Assert.assertEquals(
			expected, anyClassTypeJournalArticleAssetRendererFactory);
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.asset.publisher.web.internal.upgrade.v1_0_5." +
			"UpgradePortletPreferences";

	@Inject(
		filter = "(&(component.name=com.liferay.asset.publisher.web.internal.upgrade.registry.AssetPublisherWebUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

}