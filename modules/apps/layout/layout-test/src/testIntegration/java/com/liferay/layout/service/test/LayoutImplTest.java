/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.LayoutTypeControllerImpl;
import com.liferay.portal.model.impl.ThemeSettingImpl;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class LayoutImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		LayoutTestUtil.addTypePortletLayout(_group);

		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testGetThemeSetting() throws Exception {
		LayoutSet layoutSet = _group.getPublicLayoutSet();

		String key = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		layoutSet = _layoutSetLocalService.updateSettings(
			_group.getGroupId(), false,
			_addThemeSettingProperty(
				key, layoutSet.getSettingsProperties(), value));

		_layout = _layoutLocalService.fetchLayout(_layout.getPlid());

		Assert.assertEquals(value, _layout.getThemeSetting(key, "regular"));

		_layout = _layoutLocalService.updateLookAndFeel(
			_layout.getGroupId(), _layout.isPrivateLayout(),
			_layout.getLayoutId(), layoutSet.getThemeId(),
			layoutSet.getColorSchemeId(), layoutSet.getCss());

		value = RandomTestUtil.randomString();

		_layout = _layoutLocalService.updateLayout(
			_group.getGroupId(), false, _layout.getLayoutId(),
			_addThemeSettingProperty(
				key, _layout.getTypeSettingsProperties(), value));

		Assert.assertEquals(value, _layout.getThemeSetting(key, "regular"));

		Layout masterLayout = _addMasterLayout();

		masterLayout = _layoutLocalService.updateLookAndFeel(
			masterLayout.getGroupId(), masterLayout.isPrivateLayout(),
			masterLayout.getLayoutId(), layoutSet.getThemeId(),
			layoutSet.getColorSchemeId(), layoutSet.getCss());

		value = RandomTestUtil.randomString();

		masterLayout = _layoutLocalService.updateLayout(
			_group.getGroupId(), false, masterLayout.getLayoutId(),
			_addThemeSettingProperty(
				key, masterLayout.getTypeSettingsProperties(), value));

		_layout = _layoutLocalService.updateMasterLayoutPlid(
			_group.getGroupId(), false, _layout.getLayoutId(),
			masterLayout.getPlid());

		Assert.assertEquals(value, _layout.getThemeSetting(key, "regular"));
	}

	@Test
	public void testGetThemeWithMasterLayout() throws Exception {
		_layoutSetLocalService.updateLookAndFeel(
			_group.getGroupId(), true, "admin_WAR_admintheme", "01",
			StringPool.BLANK);

		_layoutSetLocalService.updateLookAndFeel(
			_group.getGroupId(), false, "dialect_WAR_dialecttheme", "01",
			StringPool.BLANK);

		Layout masterLayout = _addMasterLayout();

		masterLayout = _layoutLocalService.updateLookAndFeel(
			masterLayout.getGroupId(), masterLayout.isPrivateLayout(),
			masterLayout.getLayoutId(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK);

		Layout privateLayout = LayoutTestUtil.addTypePortletLayout(
			_group, true);

		privateLayout = _layoutLocalService.updateMasterLayoutPlid(
			privateLayout.getGroupId(), privateLayout.isPrivateLayout(),
			privateLayout.getLayoutId(), masterLayout.getPlid());

		_assertThemeId(privateLayout, "admin_WAR_admintheme");

		Layout publicLayout = LayoutTestUtil.addTypePortletLayout(
			_group, false);

		publicLayout = _layoutLocalService.updateMasterLayoutPlid(
			publicLayout.getGroupId(), publicLayout.isPrivateLayout(),
			publicLayout.getLayoutId(), masterLayout.getPlid());

		_assertThemeId(publicLayout, "dialect_WAR_dialecttheme");
	}

	@Test
	public void testGetTypeReturnsBlank() {
		_layout.setType(null);

		Assert.assertEquals(StringPool.BLANK, _layout.getType());
	}

	@Test
	public void testGetTypeReturnsType() {
		String type = RandomTestUtil.randomString();

		_layout.setType(type);

		Assert.assertEquals(type, _layout.getType());
	}

	@Test
	public void testIsSupportsEmbeddedPortletsWithTypeEmbedded() {
		_layout.setType(LayoutConstants.TYPE_EMBEDDED);

		Assert.assertTrue(_layout.isSupportsEmbeddedPortlets());
	}

	@Test
	public void testIsSupportsEmbeddedPortletsWithTypePanel() {
		_layout.setType(LayoutConstants.TYPE_PANEL);

		Assert.assertTrue(_layout.isSupportsEmbeddedPortlets());
	}

	@Test
	public void testIsSupportsEmbeddedPortletsWithTypePortlet() {
		_layout.setType(LayoutConstants.TYPE_PORTLET);

		Assert.assertTrue(_layout.isSupportsEmbeddedPortlets());
	}

	@Test
	public void testIsTypeEmbeddedReturnsFalse() {
		for (String type : _TYPES) {
			if (type.equals(LayoutConstants.TYPE_EMBEDDED)) {
				continue;
			}

			_layout.setType(type);

			Assert.assertFalse(_layout.isTypeEmbedded());
		}
	}

	@Test
	public void testIsTypeEmbeddedReturnsTrue() {
		_layout.setType(LayoutConstants.TYPE_EMBEDDED);

		Assert.assertTrue(_layout.isTypeEmbedded());
	}

	@Test
	public void testIsTypeEmbeddedWithLayoutTypeController() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(LayoutImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		LayoutTypeController layoutTypeController =
			new LayoutTypeControllerImpl(LayoutConstants.TYPE_EMBEDDED);

		for (String type : _TYPES) {
			if (type.equals(LayoutConstants.TYPE_EMBEDDED)) {
				continue;
			}

			_layout.setType(type);

			ServiceRegistration<LayoutTypeController> serviceRegistration =
				bundleContext.registerService(
					LayoutTypeController.class, layoutTypeController,
					HashMapDictionaryBuilder.<String, Object>put(
						"layout.type", type
					).put(
						"service.ranking", Integer.MAX_VALUE
					).build());

			try {
				Assert.assertTrue(_layout.isTypeEmbedded());
			}
			finally {
				serviceRegistration.unregister();
			}
		}
	}

	@Test
	public void testIsTypePanelReturnsFalse() {
		for (String type : _TYPES) {
			if (type.equals(LayoutConstants.TYPE_PANEL)) {
				continue;
			}

			_layout.setType(type);

			Assert.assertFalse(_layout.isTypePanel());
		}
	}

	@Test
	public void testIsTypePanelReturnsTrue() {
		_layout.setType(LayoutConstants.TYPE_PANEL);

		Assert.assertTrue(_layout.isTypePanel());
	}

	@Test
	public void testIsTypePanelWithLayoutTypeController() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(LayoutImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		LayoutTypeController layoutTypeController =
			new LayoutTypeControllerImpl(LayoutConstants.TYPE_PANEL);

		for (String layoutTypeValue : _TYPES) {
			if (layoutTypeValue.equals(LayoutConstants.TYPE_PANEL)) {
				continue;
			}

			_layout.setType(layoutTypeValue);

			ServiceRegistration<LayoutTypeController> serviceRegistration =
				bundleContext.registerService(
					LayoutTypeController.class, layoutTypeController,
					HashMapDictionaryBuilder.<String, Object>put(
						"layout.type", layoutTypeValue
					).put(
						"service.ranking", Integer.MAX_VALUE
					).build());

			try {
				Assert.assertTrue(_layout.isTypePanel());
			}
			finally {
				serviceRegistration.unregister();
			}
		}
	}

	@Test
	public void testIsTypePortletReturnsFalse() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(LayoutImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		for (String type : _TYPES) {
			if (type.equals(LayoutConstants.TYPE_PORTLET)) {
				continue;
			}

			LayoutTypeController layoutTypeController =
				new LayoutTypeControllerImpl(type);

			ServiceRegistration<LayoutTypeController> serviceRegistration =
				bundleContext.registerService(
					LayoutTypeController.class, layoutTypeController,
					HashMapDictionaryBuilder.<String, Object>put(
						"layout.type", type
					).build());

			try {
				_layout.setType(type);

				Assert.assertFalse(_layout.isTypePortlet());
			}
			finally {
				serviceRegistration.unregister();
			}
		}
	}

	@Test
	public void testIsTypePortletReturnsTrue() {
		_layout.setType(LayoutConstants.TYPE_PORTLET);

		Assert.assertTrue(_layout.isTypePortlet());
	}

	@Test
	public void testIsTypePortletWithLayoutTypeController() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(LayoutImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		LayoutTypeController layoutTypeController =
			new LayoutTypeControllerImpl(LayoutConstants.TYPE_PORTLET);

		for (String type : _TYPES) {
			if (type.equals(LayoutConstants.TYPE_PORTLET)) {
				continue;
			}

			_layout.setType(type);

			ServiceRegistration<LayoutTypeController> serviceRegistration =
				bundleContext.registerService(
					LayoutTypeController.class, layoutTypeController,
					HashMapDictionaryBuilder.<String, Object>put(
						"layout.type", type
					).put(
						"service.ranking", Integer.MAX_VALUE
					).build());

			try {
				Assert.assertTrue(_layout.isTypePortlet());
			}
			finally {
				serviceRegistration.unregister();
			}
		}
	}

	@FeatureFlag("LPD-38869")
	@Test
	public void testPrivateLayoutGetTheme() throws Exception {
		_assertGetTheme(LayoutTestUtil.addTypePortletLayout(_group, true));
	}

	@Test
	public void testPublicLayoutGetTheme() throws Exception {
		_assertGetTheme(LayoutTestUtil.addTypePortletLayout(_group, false));
	}

	private Layout _addMasterLayout() throws Exception {
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		return _layoutLocalService.fetchLayout(
			masterLayoutPageTemplateEntry.getPlid());
	}

	private String _addThemeSettingProperty(
		String key, UnicodeProperties typeSettingsUnicodeProperties,
		String value) {

		typeSettingsUnicodeProperties.put(
			ThemeSettingImpl.namespaceProperty("regular", key), value);

		return typeSettingsUnicodeProperties.toString();
	}

	private void _assertGetTheme(Layout layout) throws Exception {
		_layoutSetLocalService.updateLookAndFeel(
			layout.getGroupId(), layout.isPrivateLayout(),
			"admin_WAR_admintheme", "01", StringPool.BLANK);

		layout = _layoutLocalService.fetchLayout(layout.getPlid());

		_assertThemeId(layout, "admin_WAR_admintheme");

		layout = _layoutLocalService.updateLookAndFeel(
			_group.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			"classic_WAR_classictheme", "01", StringPool.BLANK);

		_assertThemeId(layout, "classic_WAR_classictheme");

		Layout masterLayout = _addMasterLayout();

		masterLayout = _layoutLocalService.updateLookAndFeel(
			masterLayout.getGroupId(), masterLayout.isPrivateLayout(),
			masterLayout.getLayoutId(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK);

		layout = _layoutLocalService.updateMasterLayoutPlid(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			masterLayout.getPlid());

		_assertThemeId(layout, "admin_WAR_admintheme");

		_layoutLocalService.updateLookAndFeel(
			masterLayout.getGroupId(), masterLayout.isPrivateLayout(),
			masterLayout.getLayoutId(), "dialect_WAR_dialecttheme", "01",
			StringPool.BLANK);

		layout = _layoutLocalService.getLayout(layout.getPlid());

		_assertThemeId(layout, "dialect_WAR_dialecttheme");

		layout = _layoutLocalService.updateLookAndFeel(
			_group.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			"admin_WAR_admintheme", "01", StringPool.BLANK);

		_assertThemeId(layout, "dialect_WAR_dialecttheme");
	}

	private void _assertThemeId(Layout layout, String themeId)
		throws Exception {

		Theme theme = layout.getTheme();

		Assert.assertEquals(themeId, theme.getThemeId());
	}

	@SuppressWarnings("deprecation")
	private static final String[] _TYPES = {
		LayoutConstants.TYPE_CONTROL_PANEL, LayoutConstants.TYPE_EMBEDDED,
		LayoutConstants.TYPE_LINK_TO_LAYOUT, LayoutConstants.TYPE_PANEL,
		LayoutConstants.TYPE_PORTLET, LayoutConstants.TYPE_URL
	};

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

}