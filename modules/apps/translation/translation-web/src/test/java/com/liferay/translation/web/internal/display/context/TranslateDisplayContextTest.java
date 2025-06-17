/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.display.context;

import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.portal.bean.BeanPropertiesImpl;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.redirect.RedirectURLSettingsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;
import com.liferay.translation.info.field.TranslationInfoFieldChecker;
import com.liferay.translation.service.TranslationEntryLocalServiceUtil;

import java.util.ArrayList;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class TranslateDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_classNameLocalServiceUtilMockedStatic.close();
		_redirectURLSettingsUtilMockedStatic.close();
		_translationEntryLocalServiceUtilMockedStatic.close();
		_workflowDefinitionLinkLocalServiceUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_setUpBeanPropertiesUtil();
		_setUpLanguageUtil();
		_setUpPortalUtil();
		_setUpRedirectURLSettingsUtil();
	}

	@Test
	public void testRedirectURL() {
		Assert.assertNull(_getRedirectURL("javascript:alert(document.domain)"));

		String url = "/myexample1.png?param1=a&param2=b";

		Assert.assertEquals(url, String.valueOf(_getRedirectURL(url)));
	}

	private MockHttpServletRequest _getMockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			new MockLiferayPortletRenderRequest());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));

		return mockHttpServletRequest;
	}

	private Object _getRedirectURL(String value) {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		mockHttpServletRequest.setParameter("redirect", value);

		mockLiferayPortletActionRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		InfoForm infoForm = Mockito.mock(InfoForm.class);

		Mockito.when(
			infoForm.getInfoFieldSetEntries()
		).thenReturn(
			new ArrayList<>()
		);

		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService =
			Mockito.mock(WorkflowDefinitionLinkLocalService.class);

		Mockito.when(
			workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			true
		);

		TranslateDisplayContext translateDisplayContext =
			new TranslateDisplayContext(
				new ArrayList<>(), new ArrayList<>(), () -> true,
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				infoForm, mockLiferayPortletActionRequest,
				new MockLiferayPortletRenderResponse(), null,
				RandomTestUtil.randomLong(),
				Mockito.mock(InfoItemFieldValues.class),
				LanguageUtil.getLanguageId(LocaleUtil.ENGLISH),
				Mockito.mock(InfoItemFieldValues.class),
				LanguageUtil.getLanguageId(LocaleUtil.SPAIN),
				Mockito.mock(TranslationInfoFieldChecker.class));

		Assert.assertNotNull(translateDisplayContext);

		Map<String, Object> infoFieldSetEntriesData = ReflectionTestUtil.invoke(
			translateDisplayContext, "getInfoFieldSetEntriesData",
			new Class<?>[0]);

		return infoFieldSetEntriesData.get("redirectURL");
	}

	private void _setUpBeanPropertiesUtil() {
		BeanPropertiesUtil beanPropertiesUtil = new BeanPropertiesUtil();

		beanPropertiesUtil.setBeanProperties(new BeanPropertiesImpl());
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(new LanguageImpl());
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	private void _setUpRedirectURLSettingsUtil() {
		_redirectURLSettingsUtilMockedStatic.when(
			() -> RedirectURLSettingsUtil.getAllowedProtocols(Mockito.anyLong())
		).thenReturn(
			new String[] {"http", "https"}
		);
	}

	private static final MockedStatic<ClassNameLocalServiceUtil>
		_classNameLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ClassNameLocalServiceUtil.class);
	private static final MockedStatic<RedirectURLSettingsUtil>
		_redirectURLSettingsUtilMockedStatic = Mockito.mockStatic(
			RedirectURLSettingsUtil.class);
	private static final MockedStatic<TranslationEntryLocalServiceUtil>
		_translationEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			TranslationEntryLocalServiceUtil.class);
	private static final MockedStatic<WorkflowDefinitionLinkLocalServiceUtil>
		_workflowDefinitionLinkLocalServiceUtilMockedStatic =
			Mockito.mockStatic(WorkflowDefinitionLinkLocalServiceUtil.class);

}