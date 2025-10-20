/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.web.internal.display.context.helper.ObjectRequestHelper;
import com.liferay.object.web.internal.object.entries.frontend.data.set.filter.factory.ObjectFieldFDSFilterFactoryRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Fábio Alves
 */
public class ViewObjectEntriesDisplayContextTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_setUpPortletURLUtil();
	}

	@AfterClass
	public static void tearDownClass() {
		_portletURLUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		_setUpLanguageUtil();
		_setUpMockHttpServletRequest();
		_setUpObjectActionLocalService();
		_setUpObjectDefinition();
		_setUpObjectRequestHelper();
		_setUpPortalUtil();
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		String restContextPath = "/c/" + RandomTestUtil.randomString();

		ViewObjectEntriesDisplayContext viewObjectEntriesDisplayContext =
			Mockito.spy(
				new ViewObjectEntriesDisplayContext(
					_mockHttpServletRequest, _objectActionLocalService,
					Mockito.mock(ObjectFieldFDSFilterFactoryRegistry.class),
					Mockito.mock(ObjectFieldLocalService.class),
					Mockito.mock(ObjectScopeProvider.class),
					Mockito.mock(ObjectViewLocalService.class),
					Mockito.mock(PortletResourcePermission.class),
					restContextPath));

		ReflectionTestUtil.setFieldValue(
			viewObjectEntriesDisplayContext, "_objectRequestHelper",
			_objectRequestHelper);

		Mockito.when(
			viewObjectEntriesDisplayContext.getPortletURL()
		).thenReturn(
			new MockLiferayPortletURL()
		);

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			viewObjectEntriesDisplayContext.getFDSActionDropdownItems();

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 6,
			fdsActionDropdownItems.size());

		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(0), null, "view", "view", "view", "get",
			"item");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(1),
			"/o" + restContextPath + "/{id}/expire", "time", "expire", "expire",
			"post", "item");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(2), null, "trash", "deleteObjectEntry",
			"delete", "delete", "item");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(3), null, "password-policies",
			"permissions", "permissions", "get", "item");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(4),
			StringBundler.concat(
				"/o", restContextPath, "/by-external-reference-code",
				"/{externalReferenceCode}/subscribe"),
			"bell-on", "subscribe", "subscribe", "post", "item");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(5),
			StringBundler.concat(
				"/o", restContextPath, "/by-external-reference-code",
				"/{externalReferenceCode}/unsubscribe"),
			"bell-off", "unsubscribe", "unsubscribe", "post", "item");
	}

	private static void _setUpPortletURLUtil() throws Exception {
		_portletURLUtilMockedStatic = Mockito.mockStatic(PortletURLUtil.class);

		Mockito.when(
			PortletURLUtil.clone(
				Mockito.any(LiferayPortletURL.class),
				Mockito.any(LiferayPortletResponse.class))
		).thenReturn(
			new MockLiferayPortletURL()
		);
	}

	private void _assertFDSActionDropdownItem(
		FDSActionDropdownItem fdsActionDropdownItem, String href, String icon,
		String id, String label, String method, String type) {

		Assert.assertNotNull(fdsActionDropdownItem);

		Map<String, String> data =
			(Map<String, String>)fdsActionDropdownItem.get("data");

		Assert.assertEquals(id, data.get("id"));
		Assert.assertEquals(method, data.get("method"));

		if (Validator.isNotNull(href)) {
			Assert.assertEquals(href, fdsActionDropdownItem.get("href"));
		}

		Assert.assertEquals(icon, fdsActionDropdownItem.get("icon"));
		Assert.assertEquals(label, fdsActionDropdownItem.get("label"));
		Assert.assertEquals(type, fdsActionDropdownItem.get("type"));
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));

		Mockito.when(
			LanguageUtil.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);
	}

	private void _setUpMockHttpServletRequest() {
		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));

		_mockHttpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_DEFINITION, _objectDefinition);
	}

	private void _setUpObjectActionLocalService() {
		Mockito.when(
			_objectActionLocalService.getObjectActions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			Collections.emptyList()
		);
	}

	private void _setUpObjectDefinition() {
		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_objectDefinition.getLabel(Mockito.any(Locale.class))
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private void _setUpObjectRequestHelper() {
		Mockito.when(
			_objectRequestHelper.getRequest()
		).thenReturn(
			_mockHttpServletRequest
		);
	}

	private void _setUpPortalUtil() {
		Portal portal = Mockito.mock(Portal.class);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(portal);

		Mockito.when(
			portal.getHttpServletRequest(Mockito.any(PortletRequest.class))
		).thenReturn(
			_mockHttpServletRequest
		);

		Mockito.when(
			portal.getControlPanelPortletURL(
				_mockHttpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).thenReturn(
			new MockLiferayPortletURL()
		);
	}

	private static MockedStatic<PortletURLUtil> _portletURLUtilMockedStatic;

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final ObjectActionLocalService _objectActionLocalService =
		Mockito.mock(ObjectActionLocalService.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectRequestHelper _objectRequestHelper = Mockito.mock(
		ObjectRequestHelper.class);

}