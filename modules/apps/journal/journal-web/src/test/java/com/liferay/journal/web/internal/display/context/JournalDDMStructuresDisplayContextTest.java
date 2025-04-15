/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.test.portlet.MockRenderRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.PortalPreferencesImpl;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class JournalDDMStructuresDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpPortalUtil();
		_setUpPortletPreferencesFactoryUtil();
	}

	@Test
	public void testGetOrderByCol() {
		_testGetOrderByColWithParameter();
		_testGetOrderByColWithoutParameter();
	}

	@Test
	public void testGetOrderByType() {
		_testGetOrderByTypeWithParameter();
		_testGetOrderByTypeWithoutParameter();
	}

	private JournalDDMStructuresDisplayContext
		_getJournalDDMStructuresDisplayContext(
			String parameterName, String parameterValue,
			PortalPreferences portalPreferences) {

		Mockito.when(
			PortletPreferencesFactoryUtil.getPortalPreferences(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			portalPreferences
		);

		MockRenderRequest mockRenderRequest = new MockRenderRequest();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		mockHttpServletRequest.setParameter(parameterName, parameterValue);

		Mockito.when(
			PortalUtil.getHttpServletRequest(mockRenderRequest)
		).thenReturn(
			mockHttpServletRequest
		);

		return new JournalDDMStructuresDisplayContext(
			mockRenderRequest, Mockito.mock(RenderResponse.class));
	}

	private MockHttpServletRequest _getMockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JournalWebConfiguration.class.getName(),
			Mockito.mock(JournalWebConfiguration.class));
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));

		return mockHttpServletRequest;
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(Mockito.mock(Portal.class));
	}

	private void _setUpPortletPreferencesFactoryUtil() {
		PortletPreferencesFactoryUtil portletPreferencesFactoryUtil =
			new PortletPreferencesFactoryUtil();

		portletPreferencesFactoryUtil.setPortletPreferencesFactory(
			Mockito.mock(PortletPreferencesFactory.class));
	}

	private void _testGetOrderByColWithoutParameter() {
		String orderByColParamValue = RandomTestUtil.randomString();

		PortalPreferences portalPreferences = new PortalPreferencesImpl();

		portalPreferences.setValue(
			JournalPortletKeys.JOURNAL, "ddm-structure-order-by-col",
			orderByColParamValue);

		JournalDDMStructuresDisplayContext journalDDMStructuresDisplayContext =
			_getJournalDDMStructuresDisplayContext(
				SearchContainer.DEFAULT_ORDER_BY_COL_PARAM, null,
				portalPreferences);

		Assert.assertEquals(
			orderByColParamValue,
			journalDDMStructuresDisplayContext.getOrderByCol());

		Assert.assertEquals(
			orderByColParamValue,
			portalPreferences.getValue(
				JournalPortletKeys.JOURNAL, "ddm-structure-order-by-col"));
	}

	private void _testGetOrderByColWithParameter() {
		String orderByColParamValue = RandomTestUtil.randomString();

		JournalDDMStructuresDisplayContext journalDDMStructuresDisplayContext =
			_getJournalDDMStructuresDisplayContext(
				SearchContainer.DEFAULT_ORDER_BY_COL_PARAM,
				orderByColParamValue, new PortalPreferencesImpl());

		Assert.assertEquals(
			orderByColParamValue,
			journalDDMStructuresDisplayContext.getOrderByCol());
	}

	private void _testGetOrderByTypeWithoutParameter() {
		String orderByTypeParamValue = RandomTestUtil.randomString();

		PortalPreferences portalPreferences = new PortalPreferencesImpl();

		portalPreferences.setValue(
			JournalPortletKeys.JOURNAL, "ddm-structure-order-by-type",
			orderByTypeParamValue);

		JournalDDMStructuresDisplayContext journalDDMStructuresDisplayContext =
			_getJournalDDMStructuresDisplayContext(
				SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM, null,
				portalPreferences);

		Assert.assertEquals(
			orderByTypeParamValue,
			journalDDMStructuresDisplayContext.getOrderByType());

		Assert.assertEquals(
			orderByTypeParamValue,
			portalPreferences.getValue(
				JournalPortletKeys.JOURNAL, "ddm-structure-order-by-type"));
	}

	private void _testGetOrderByTypeWithParameter() {
		String orderByTypeParamValue = RandomTestUtil.randomString();

		JournalDDMStructuresDisplayContext journalDDMStructuresDisplayContext =
			_getJournalDDMStructuresDisplayContext(
				SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM,
				orderByTypeParamValue, new PortalPreferencesImpl());

		Assert.assertEquals(
			orderByTypeParamValue,
			journalDDMStructuresDisplayContext.getOrderByType());
	}

}