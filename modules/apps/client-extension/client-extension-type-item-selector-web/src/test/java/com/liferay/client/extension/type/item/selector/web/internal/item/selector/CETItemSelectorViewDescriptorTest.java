/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.item.selector.web.internal.item.selector;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.GlobalCSSCET;
import com.liferay.client.extension.type.GlobalJSCET;
import com.liferay.client.extension.type.ThemeCSSCET;
import com.liferay.client.extension.type.item.selector.CETItemSelectorCriterion;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Thiago Buarque
 */
public class CETItemSelectorViewDescriptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_cetManager = Mockito.mock(CETManager.class);

		_cetItemSelectorCriterion = Mockito.mock(
			CETItemSelectorCriterion.class);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			new MockLiferayResourceRequest());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));

		_cetItemSelectorViewDescriptor = new CETItemSelectorViewDescriptor(
			_cetManager, _cetItemSelectorCriterion, mockHttpServletRequest,
			new MockLiferayPortletURL());
	}

	@Test
	public void testGetSearchContainer() throws PortalException {
		GlobalCSSCET globalCSSCET1 = Mockito.mock(GlobalCSSCET.class);

		Mockito.when(
			globalCSSCET1.getScope()
		).thenReturn(
			"company"
		);

		GlobalCSSCET globalCSSCET2 = Mockito.mock(GlobalCSSCET.class);

		Mockito.when(
			globalCSSCET2.getScope()
		).thenReturn(
			""
		);

		_testGetSearchContainer(
			globalCSSCET1, globalCSSCET2,
			ClientExtensionEntryConstants.TYPE_GLOBAL_CSS);

		GlobalJSCET globalJSCET1 = Mockito.mock(GlobalJSCET.class);

		Mockito.when(
			globalJSCET1.getScope()
		).thenReturn(
			"company"
		);

		GlobalJSCET globalJSCET2 = Mockito.mock(GlobalJSCET.class);

		Mockito.when(
			globalJSCET2.getScope()
		).thenReturn(
			""
		);

		_testGetSearchContainer(
			globalJSCET1, globalJSCET2,
			ClientExtensionEntryConstants.TYPE_GLOBAL_JS);

		ThemeCSSCET themeCSSCET1 = Mockito.mock(ThemeCSSCET.class);

		Mockito.when(
			themeCSSCET1.getScope()
		).thenReturn(
			"controlPanel"
		);

		ThemeCSSCET themeCSSCET2 = Mockito.mock(ThemeCSSCET.class);

		Mockito.when(
			themeCSSCET2.getScope()
		).thenReturn(
			""
		);

		_testGetSearchContainer(
			themeCSSCET1, themeCSSCET2,
			ClientExtensionEntryConstants.TYPE_THEME_CSS);
	}

	private void _testGetSearchContainer(CET cet1, CET cet2, String type)
		throws PortalException {

		Mockito.when(
			_cetItemSelectorCriterion.getType()
		).thenReturn(
			type
		);

		Mockito.when(
			_cetManager.getCETs(
				Mockito.anyLong(), Mockito.any(), Mockito.eq(type),
				Mockito.any(), Mockito.any())
		).thenReturn(
			Arrays.asList(cet1, cet2)
		);

		SearchContainer<CET> searchContainer =
			_cetItemSelectorViewDescriptor.getSearchContainer();

		List<CET> cets = searchContainer.getResults();

		Assert.assertEquals(cets.toString(), 1, cets.size());

		CET cet3 = cets.get(0);

		Assert.assertEquals(
			"", ReflectionTestUtil.invoke(cet3, "getScope", null));
	}

	private static CETItemSelectorCriterion _cetItemSelectorCriterion;
	private static CETItemSelectorViewDescriptor _cetItemSelectorViewDescriptor;
	private static CETManager _cetManager;

}