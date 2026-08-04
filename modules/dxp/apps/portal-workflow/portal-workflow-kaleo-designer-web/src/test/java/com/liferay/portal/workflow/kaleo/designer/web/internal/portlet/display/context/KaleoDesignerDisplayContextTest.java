/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.portlet.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.kaleo.designer.web.internal.constants.KaleoDesignerWebKeys;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.runtime.action.ActionExecutorManager;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Nathaly Gomes
 */
public class KaleoDesignerDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpPortalUtil();

		_setUpKaleoDefinitionVersion();

		_setUpKaleoDesignerDisplayContext();
	}

	@Test
	public void testGetBackURL() {
		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		String redirect = RandomTestUtil.randomString();

		Mockito.when(
			renderRequest.getParameter("redirect")
		).thenReturn(
			redirect
		);

		Assert.assertEquals(
			redirect, _kaleoDesignerDisplayContext.getBackURL(renderRequest));
	}

	@Test
	public void testGetKaleoDefinitionVersionsJSONArray() throws Exception {
		Assert.assertEquals(
			String.valueOf(_getJSONArray(StringPool.BLANK)),
			String.valueOf(
				_kaleoDesignerDisplayContext.
					getKaleoDefinitionVersionsJSONArray(
						_kaleoDefinitionVersion)));

		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getFullName()
		).thenReturn(
			"default-service-account default-service-account"
		);

		Mockito.when(
			_userLocalService.fetchUserByScreenName(
				_kaleoDefinitionVersion.getCompanyId(),
				UserConstants.SCREEN_NAME_DEFAULT_SERVICE_ACCOUNT)
		).thenReturn(
			user
		);

		Assert.assertEquals(
			String.valueOf(
				_getJSONArray(
					"default-service-account default-service-account")),
			String.valueOf(
				_kaleoDesignerDisplayContext.
					getKaleoDefinitionVersionsJSONArray(
						_kaleoDefinitionVersion)));

		Mockito.when(
			user.getFullName()
		).thenReturn(
			"Test Test"
		);

		Mockito.when(
			_userLocalService.fetchUser(_kaleoDefinitionVersion.getUserId())
		).thenReturn(
			user
		);

		Assert.assertEquals(
			String.valueOf(_getJSONArray("Test Test")),
			String.valueOf(
				_kaleoDesignerDisplayContext.
					getKaleoDefinitionVersionsJSONArray(
						_kaleoDefinitionVersion)));
	}

	@Test
	public void testIsReadOnly() throws Exception {
		_isReadOnly(false);
		_isReadOnly(true);
	}

	private JSONArray _getJSONArray(String expectedCreatorName) {
		return JSONFactoryUtil.createJSONArray(
		).put(
			JSONUtil.put(
				"creatorName", expectedCreatorName
			).put(
				"dateCreated",
				() -> {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

					return simpleDateFormat.format(_createDate);
				}
			).put(
				"version", "1.0"
			)
		);
	}

	private void _isReadOnly(boolean system) throws Exception {
		KaleoDefinition kaleoDefinition = Mockito.mock(KaleoDefinition.class);

		Mockito.when(
			kaleoDefinition.isSystem()
		).thenReturn(
			system
		);

		Mockito.when(
			_kaleoDefinitionVersion.getKaleoDefinition()
		).thenReturn(
			kaleoDefinition
		);

		Mockito.when(
			_liferayPortletRequest.getAttribute(
				KaleoDesignerWebKeys.KALEO_DRAFT_DEFINITION)
		).thenReturn(
			_kaleoDefinitionVersion
		);

		Mockito.when(
			_portletResourcePermission.contains(
				Mockito.any(), Mockito.anyLong(), Mockito.any())
		).thenReturn(
			Boolean.TRUE
		);

		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				Mockito.mock(PermissionChecker.class)
			);

			Assert.assertEquals(
				system, _kaleoDesignerDisplayContext.isReadOnly());
		}
	}

	private void _setUpKaleoDefinitionVersion() {
		_createDate = RandomTestUtil.nextDate();

		Mockito.when(
			_kaleoDefinitionVersion.getCreateDate()
		).thenReturn(
			_createDate
		);

		Mockito.when(
			_kaleoDefinitionVersion.getVersion()
		).thenReturn(
			"1.0"
		);
	}

	private void _setUpKaleoDesignerDisplayContext() {
		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.when(
			renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			Mockito.mock(ThemeDisplay.class)
		);

		_kaleoDesignerDisplayContext = new KaleoDesignerDisplayContext(
			Mockito.mock(ActionExecutorManager.class),
			Mockito.mock(GroupLocalService.class),
			Mockito.mock(KaleoDefinitionVersionLocalService.class),
			_portletResourcePermission, renderRequest,
			Mockito.mock(ResourceBundleLoader.class),
			Mockito.mock(ScriptManagementConfigurationHelper.class),
			_userLocalService);

		Mockito.when(
			_kaleoDesignerDisplayContext.getKaleoDefinitionVersions(
				_kaleoDefinitionVersion)
		).thenReturn(
			List.of(_kaleoDefinitionVersion)
		);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getHttpServletRequest(Mockito.any())
		).thenReturn(
			Mockito.mock(HttpServletRequest.class)
		);

		Mockito.when(
			portal.getLiferayPortletRequest(Mockito.any())
		).thenReturn(
			_liferayPortletRequest
		);

		portalUtil.setPortal(portal);
	}

	private Date _createDate;
	private final KaleoDefinitionVersion _kaleoDefinitionVersion = Mockito.mock(
		KaleoDefinitionVersion.class);
	private KaleoDesignerDisplayContext _kaleoDesignerDisplayContext;
	private final LiferayPortletRequest _liferayPortletRequest = Mockito.mock(
		LiferayPortletRequest.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);
	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

}