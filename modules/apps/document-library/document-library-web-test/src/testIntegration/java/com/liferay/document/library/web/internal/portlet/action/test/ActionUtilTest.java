/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Ankita Malik
 */
@RunWith(Arquillian.class)
public class ActionUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(ActionUtilTest.class);

		bundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.document.library.web");

		Class<?> actionUtilClass = bundle.loadClass(
			"com.liferay.document.library.web.internal.portlet.action." +
				"ActionUtil");

		_getFolderMethod = actionUtilClass.getDeclaredMethod(
			"getFolder", HttpServletRequest.class);

		Class<?> dlPortletInstanceSettingsClass = bundle.loadClass(
			"com.liferay.document.library.web.internal.settings." +
				"DLPortletInstanceSettings");

		_dlPortletInstanceSettingsConstructor =
			dlPortletInstanceSettingsClass.getConstructor(Settings.class);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-92077")
	public void testGetFolderRejectsFolderOutsideConfiguredRootFolder()
		throws Exception {

		Folder rootFolder = DLAppTestUtil.addFolder(_group.getGroupId());

		Folder outsideFolder = DLAppTestUtil.addFolder(_group.getGroupId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"folderId", String.valueOf(outsideFolder.getFolderId()));

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _createThemeDisplay());

		mockHttpServletRequest.setAttribute(
			_DOCUMENT_LIBRARY_PORTLET_INSTANCE_SETTINGS_ATTRIBUTE,
			_dlPortletInstanceSettingsConstructor.newInstance(
				_createSettings(rootFolder.getExternalReferenceCode())));

		try {
			_getFolderMethod.invoke(null, mockHttpServletRequest);

			Assert.fail(
				"ActionUtil.getFolder should reject a folderId outside the " +
					"configured root folder");
		}
		catch (InvocationTargetException invocationTargetException) {
			Throwable throwable = invocationTargetException.getCause();

			Assert.assertTrue(
				"Expected NoSuchFolderException but got " + throwable,
				throwable instanceof NoSuchFolderException);
		}
	}

	private Settings _createSettings(String rootFolderExternalReferenceCode) {
		return new Settings() {

			@Override
			public ModifiableSettings getModifiableSettings() {
				return null;
			}

			@Override
			public Settings getParentSettings() {
				return null;
			}

			@Override
			public String getValue(String key, String defaultValue) {
				if (key.equals("rootFolderExternalReferenceCode")) {
					return rootFolderExternalReferenceCode;
				}

				return defaultValue;
			}

			@Override
			public String[] getValues(String key, String[] defaultValue) {
				return defaultValue;
			}

		};
	}

	private ThemeDisplay _createThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private static final String
		_DOCUMENT_LIBRARY_PORTLET_INSTANCE_SETTINGS_ATTRIBUTE =
			"DOCUMENT_LIBRARY_PORTLET_INSTANCE_SETTINGS";

	private static Constructor<?> _dlPortletInstanceSettingsConstructor;
	private static Method _getFolderMethod;

	@DeleteAfterTestRun
	private Group _group;

}