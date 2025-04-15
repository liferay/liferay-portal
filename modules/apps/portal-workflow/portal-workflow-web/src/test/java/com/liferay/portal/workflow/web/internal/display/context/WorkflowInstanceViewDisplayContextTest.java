/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.DefaultWorkflowInstance;
import com.liferay.portal.kernel.workflow.DefaultWorkflowNode;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.manager.WorkflowLogManager;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Feliphe Marinho
 */
public class WorkflowInstanceViewDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_setUpPortal();

		_setUpFastDateFormatFactory();
		_setUpLiferayPortletRequest();
		_setUpLiferayPortletResponse();
		_setUpPortletPreferences();
		_setUpThemeDisplay();
	}

	@Before
	public void setUp() throws PortalException {
		_workflowInstanceViewDisplayContext = Mockito.spy(
			new WorkflowInstanceViewDisplayContext(
				_liferayPortletRequest, _liferayPortletResponse,
				Mockito.mock(WorkflowComparatorFactory.class),
				Mockito.mock(WorkflowLogManager.class)));

		_language = Mockito.mock(Language.class);

		ReflectionTestUtil.setFieldValue(
			LanguageUtil.class, "_language", _language);
	}

	@Test
	public void testGetStatus() {
		DefaultWorkflowInstance defaultWorkflowInstance =
			new DefaultWorkflowInstance();

		DefaultWorkflowNode defaultWorkflowNode1 = new DefaultWorkflowNode();

		defaultWorkflowNode1.setLabelMap(
			HashMapBuilder.put(
				LocaleUtil.BRAZIL, "Revisar Regra de Negócio"
			).put(
				LocaleUtil.US, "Business Rule Review"
			).build());
		defaultWorkflowNode1.setName("businessRuleReview");

		DefaultWorkflowNode defaultWorkflowNode2 = new DefaultWorkflowNode();

		defaultWorkflowNode2.setLabelMap(
			HashMapBuilder.put(
				LocaleUtil.BRAZIL, "Revisar Sintaxe"
			).put(
				LocaleUtil.US, "Syntax Review"
			).build());
		defaultWorkflowNode2.setName("syntaxReview");

		defaultWorkflowInstance.setCurrentWorkflowNodes(
			Arrays.asList(defaultWorkflowNode1, defaultWorkflowNode2));

		Assert.assertEquals(
			StringUtil.merge(
				Arrays.asList("Revisar Regra de Negócio", "Revisar Sintaxe"),
				StringPool.COMMA_AND_SPACE),
			_workflowInstanceViewDisplayContext.getStatus(
				defaultWorkflowInstance));

		defaultWorkflowNode1.setLabelMap(
			HashMapBuilder.put(
				LocaleUtil.US, "Business Rule Review"
			).build());

		defaultWorkflowNode2.setLabelMap(
			HashMapBuilder.put(
				LocaleUtil.US, "Syntax Review"
			).build());

		Assert.assertEquals(
			StringUtil.merge(
				Arrays.asList("Business Rule Review", "Syntax Review"),
				StringPool.COMMA_AND_SPACE),
			_workflowInstanceViewDisplayContext.getStatus(
				defaultWorkflowInstance));

		defaultWorkflowInstance.setCurrentWorkflowNodes(
			Arrays.asList(defaultWorkflowNode1));

		Assert.assertEquals(
			"Business Rule Review",
			_workflowInstanceViewDisplayContext.getStatus(
				defaultWorkflowInstance));

		Mockito.when(
			_language.get(
				Mockito.any(Locale.class), Mockito.eq("businessRuleReview"))
		).thenReturn(
			"businessRuleReview"
		);

		defaultWorkflowNode1.setLabelMap(Collections.emptyMap());

		Assert.assertEquals(
			"businessRuleReview",
			_workflowInstanceViewDisplayContext.getStatus(
				defaultWorkflowInstance));
	}

	private static void _setUpFastDateFormatFactory() {
		ReflectionTestUtil.setFieldValue(
			FastDateFormatFactoryUtil.class, "_fastDateFormatFactory",
			new FastDateFormatFactoryImpl());
	}

	private static void _setUpLiferayPortletRequest() {
		_liferayPortletRequest = Mockito.mock(LiferayPortletRequest.class);
		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		Mockito.when(
			_portal.getHttpServletRequest(_liferayPortletRequest)
		).thenReturn(
			_httpServletRequest
		);
	}

	private static void _setUpLiferayPortletResponse() {
		_liferayPortletResponse = Mockito.mock(LiferayPortletResponse.class);

		Mockito.when(
			_portal.getHttpServletResponse(_liferayPortletResponse)
		).thenReturn(
			new MockHttpServletResponse()
		);
	}

	private static void _setUpPortal() {
		_portal = Mockito.mock(Portal.class);

		Mockito.when(
			_portal.getPortalURL((PortletRequest)Mockito.any())
		).thenReturn(
			RandomTestUtil.randomString()
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	private static void _setUpPortletPreferences() {
		PortletPreferencesFactoryUtil portletPreferencesFactoryUtil =
			new PortletPreferencesFactoryUtil();

		PortletPreferencesFactory portletPreferencesFactory = Mockito.mock(
			PortletPreferencesFactory.class);

		portletPreferencesFactoryUtil.setPortletPreferencesFactory(
			portletPreferencesFactory);

		Mockito.when(
			portletPreferencesFactory.getPortalPreferences(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			Mockito.mock(PortalPreferences.class)
		);
	}

	private static void _setUpThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			_liferayPortletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.BRAZIL
		);

		Mockito.when(
			themeDisplay.getTimeZone()
		).thenReturn(
			TimeZone.getTimeZone("America/Recife")
		);
	}

	private static HttpServletRequest _httpServletRequest;
	private static LiferayPortletRequest _liferayPortletRequest;
	private static LiferayPortletResponse _liferayPortletResponse;
	private static Portal _portal;

	private Language _language;
	private WorkflowInstanceViewDisplayContext
		_workflowInstanceViewDisplayContext;

}