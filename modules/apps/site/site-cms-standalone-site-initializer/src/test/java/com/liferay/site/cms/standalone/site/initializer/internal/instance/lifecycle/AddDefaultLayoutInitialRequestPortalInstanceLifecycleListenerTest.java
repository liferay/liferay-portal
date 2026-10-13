/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.instance.lifecycle;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.initializer.SiteInitializer;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.Dictionary;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Jan Brychta
 */
public class AddDefaultLayoutInitialRequestPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-109177")
	public void testDoPortalInstanceRegisteredDoesNotReinitializeSiteWhenAlreadyInitialized()
		throws Exception {

		Theme theme = Mockito.mock(Theme.class);

		Mockito.when(
			theme.getName()
		).thenReturn(
			"CMS"
		);

		Mocks mocks = _portalInstanceRegistered(theme);

		Mockito.verify(
			mocks._siteInitializer, Mockito.never()
		).initialize(
			Mockito.anyLong()
		);
	}

	@Test
	@TestInfo("LPD-109177")
	public void testDoPortalInstanceRegisteredEnablesPromptForSignInOnCMSGroupWhenAlreadyInitialized()
		throws Exception {

		Theme theme = Mockito.mock(Theme.class);

		Mockito.when(
			theme.getName()
		).thenReturn(
			"CMS"
		);

		Mocks mocks = _portalInstanceRegistered(theme);

		ArgumentCaptor<Dictionary<String, Object>> argumentCaptor =
			ArgumentCaptor.forClass(Dictionary.class);

		Mockito.verify(
			mocks._configurationProvider
		).saveGroupConfiguration(
			Mockito.eq(_COMPANY_ID), Mockito.eq(_CMS_GROUP_ID),
			Mockito.eq(
				"com.liferay.login.web.internal.configuration." +
					"AuthLoginConfiguration"),
			argumentCaptor.capture()
		);

		Mockito.verifyNoMoreInteractions(mocks._configurationProvider);

		Dictionary<String, Object> properties = argumentCaptor.getValue();

		Assert.assertEquals(Boolean.TRUE, properties.get("promptEnabled"));
	}

	@Test
	@TestInfo("LPD-109177")
	public void testDoPortalInstanceRegisteredInitializesSiteWhenNotInitialized()
		throws Exception {

		Mocks mocks = _portalInstanceRegistered(null);

		Mockito.verify(
			mocks._siteInitializer
		).initialize(
			_GUEST_GROUP_ID
		);
	}

	private Mocks _portalInstanceRegistered(Theme theme) throws Exception {
		Mocks mocks = new Mocks();

		AddDefaultLayoutInitialRequestPortalInstanceLifecycleListener
			addDefaultLayoutInitialRequestPortalInstanceLifecycleListener =
				new AddDefaultLayoutInitialRequestPortalInstanceLifecycleListener();

		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		Group cmsGroup = Mockito.mock(Group.class);

		Mockito.when(
			cmsGroup.getGroupId()
		).thenReturn(
			_CMS_GROUP_ID
		);

		Group guestGroup = Mockito.mock(Group.class);

		Mockito.when(
			guestGroup.getGroupId()
		).thenReturn(
			_GUEST_GROUP_ID
		);

		Mockito.when(
			groupLocalService.getGroup(_COMPANY_ID, GroupConstants.CMS)
		).thenReturn(
			cmsGroup
		);

		Mockito.when(
			groupLocalService.getGroup(_COMPANY_ID, GroupConstants.GUEST)
		).thenReturn(
			guestGroup
		);

		ReflectionTestUtil.setFieldValue(
			addDefaultLayoutInitialRequestPortalInstanceLifecycleListener,
			"_groupLocalService", groupLocalService);

		ReflectionTestUtil.setFieldValue(
			addDefaultLayoutInitialRequestPortalInstanceLifecycleListener,
			"_configurationProvider", mocks._configurationProvider);

		LayoutSetLocalService layoutSetLocalService = Mockito.mock(
			LayoutSetLocalService.class);

		LayoutSet layoutSet = Mockito.mock(LayoutSet.class);

		Mockito.when(
			layoutSetLocalService.getLayoutSet(_GUEST_GROUP_ID, false)
		).thenReturn(
			layoutSet
		);

		ReflectionTestUtil.setFieldValue(
			addDefaultLayoutInitialRequestPortalInstanceLifecycleListener,
			"_layoutSetLocalService", layoutSetLocalService);

		ThemeLocalService themeLocalService = Mockito.mock(
			ThemeLocalService.class);

		Mockito.when(
			themeLocalService.fetchTheme(
				Mockito.eq(_COMPANY_ID), Mockito.nullable(String.class))
		).thenReturn(
			theme
		);

		ReflectionTestUtil.setFieldValue(
			addDefaultLayoutInitialRequestPortalInstanceLifecycleListener,
			"_themeLocalService", themeLocalService);

		RoleLocalService roleLocalService = Mockito.mock(
			RoleLocalService.class);

		ReflectionTestUtil.setFieldValue(
			addDefaultLayoutInitialRequestPortalInstanceLifecycleListener,
			"_roleLocalService", roleLocalService);

		UserLocalService userLocalService = Mockito.mock(
			UserLocalService.class);

		Mockito.when(
			userLocalService.getRoleUsers(
				Mockito.anyLong(), Mockito.eq(0), Mockito.eq(1))
		).thenReturn(
			Collections.emptyList()
		);

		User user = Mockito.mock(User.class);

		Mockito.when(
			userLocalService.getGuestUser(_COMPANY_ID)
		).thenReturn(
			user
		);

		ReflectionTestUtil.setFieldValue(
			addDefaultLayoutInitialRequestPortalInstanceLifecycleListener,
			"_userLocalService", userLocalService);

		Snapshot<PermissionCheckerFactory> permissionCheckerFactorySnapshot =
			Mockito.mock(Snapshot.class);

		PermissionCheckerFactory permissionCheckerFactory = Mockito.mock(
			PermissionCheckerFactory.class);

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionCheckerFactory.create(Mockito.any(User.class))
		).thenReturn(
			permissionChecker
		);

		Mockito.when(
			permissionCheckerFactorySnapshot.get()
		).thenReturn(
			permissionCheckerFactory
		);

		ReflectionTestUtil.setFieldValue(
			PermissionCheckerFactoryUtil.class,
			"_permissionCheckerFactorySnapshot",
			permissionCheckerFactorySnapshot);

		ReflectionTestUtil.setFieldValue(
			addDefaultLayoutInitialRequestPortalInstanceLifecycleListener,
			"_siteInitializer", mocks._siteInitializer);

		PrefsProps prefsProps = Mockito.mock(PrefsProps.class);

		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		Mockito.when(
			prefsProps.getPreferences(Mockito.anyLong())
		).thenReturn(
			portletPreferences
		);

		ReflectionTestUtil.setFieldValue(
			PrefsPropsUtil.class, "_prefsProps", prefsProps);

		addDefaultLayoutInitialRequestPortalInstanceLifecycleListener.
			doPortalInstanceRegistered(_COMPANY_ID);

		return mocks;
	}

	private static final long _CMS_GROUP_ID = 20121;

	private static final long _COMPANY_ID = 1;

	private static final long _GUEST_GROUP_ID = 20126;

	private static class Mocks {

		private final ConfigurationProvider _configurationProvider =
			Mockito.mock(ConfigurationProvider.class);
		private final SiteInitializer _siteInitializer = Mockito.mock(
			SiteInitializer.class);

	}

}