/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchPortletPreferencesException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.persistence.PortletPreferencesPersistence;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.Portlet;

import java.util.HashMap;

import org.junit.After;
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
 * @author Nathaly Gomes
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionPortletIdUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_newPortletId =
			ObjectPortletKeys.OBJECT_DEFINITIONS + StringPool.UNDERLINE +
				StringUtil.split(
					_objectDefinition.getClassName(), StringPool.POUND)[1];
		_oldPortletId =
			ObjectPortletKeys.OBJECT_DEFINITIONS + StringPool.UNDERLINE +
				_objectDefinition.getObjectDefinitionId();

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectDefinitionPortletIdUpgradeProcessTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			Portlet.class, new MVCPortlet(),
			HashMapDictionaryBuilder.<String, Object>put(
				"com.liferay.portlet.preferences-company-wide", "false"
			).put(
				"com.liferay.portlet.preferences-owned-by-group", false
			).put(
				"com.liferay.portlet.preferences-unique-per-layout", true
			).put(
				"jakarta.portlet.name", _oldPortletId
			).build());

		_objectDefinitionLocalService.addObjectDefinition(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), 0, true,
			RandomTestUtil.randomString(), false);
	}

	@After
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			GroupTestUtil.addGroup());

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), layout, _oldPortletId, "column-1",
			new HashMap<>());

		Assert.assertNotNull(
			_portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				_oldPortletId));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();
		}

		_portletPreferencesPersistence.clearCache();

		AssertUtils.assertFailure(
			NoSuchPortletPreferencesException.class,
			StringBundler.concat(
				"No PortletPreferences exists with the key {ownerId=",
				PortletKeys.PREFS_OWNER_ID_DEFAULT, ", ownerType=",
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, ", plid=",
				layout.getPlid(), ", portletId=", _oldPortletId, "}"),
			() -> _portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				_oldPortletId));
		Assert.assertNotNull(
			_portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				_newPortletId));
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v10_0_1." +
			"ObjectDefinitionPortletIdUpgradeProcess";

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	private String _newPortletId;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private String _oldPortletId;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletPreferencesPersistence _portletPreferencesPersistence;

	private ServiceRegistration<Portlet> _serviceRegistration;

}