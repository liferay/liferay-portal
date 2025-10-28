/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.display.context;

import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalServiceUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Anderson Luiz
 */
public class EditStyleBookEntryDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_jsonFactoryUtilMockedStatic.when(
			JSONFactoryUtil::createJSONObject
		).thenReturn(
			Mockito.mock(JSONObject.class)
		);

		StyleBookEntry styleBookEntry = Mockito.mock(StyleBookEntry.class);

		Mockito.when(
			styleBookEntry.isHead()
		).thenReturn(
			true
		);

		_styleBookEntryLocalServiceUtilMockedStatic.when(
			() -> StyleBookEntryLocalServiceUtil.fetchStyleBookEntry(
				Mockito.anyLong())
		).thenReturn(
			styleBookEntry
		);
	}

	@After
	public void tearDown() {
		_jsonFactoryUtilMockedStatic.close();
		_layoutSetLocalServiceUtilMockedStatic.close();
		_styleBookEntryLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testGetFrontendTokenDefinitionJSONObject()
		throws PortalException {

		Group group = Mockito.mock(Group.class);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		LayoutSet layoutSet = Mockito.mock(LayoutSet.class);

		Mockito.when(
			layoutSet.getGroup()
		).thenReturn(
			group
		);

		_layoutSetLocalServiceUtilMockedStatic.when(
			() -> LayoutSetLocalServiceUtil.fetchLayoutSet(
				Mockito.eq(group.getGroupId()), Mockito.anyBoolean())
		).thenReturn(
			layoutSet
		);

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			Mockito.mock(FrontendTokenDefinitionRegistry.class);

		_invokeGetFrontendTokenDefinitionJSONObject(
			frontendTokenDefinitionRegistry,
			_getThemeDisplay(group, layoutSet));

		_layoutSetLocalServiceUtilMockedStatic.verify(
			() -> LayoutSetLocalServiceUtil.fetchLayoutSet(
				Mockito.eq(groupId), Mockito.eq(false)));

		Mockito.verify(
			frontendTokenDefinitionRegistry
		).getFrontendTokenDefinition(
			layoutSet
		);
	}

	private ThemeDisplay _getThemeDisplay(Group group, LayoutSet layoutSet) {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLayoutSet()
		).thenReturn(
			layoutSet
		);

		PortletDisplay portletDisplay = Mockito.mock(PortletDisplay.class);

		Mockito.doNothing(
		).when(
			portletDisplay
		).setURLBack(
			StringPool.BLANK
		);

		Mockito.when(
			themeDisplay.getPortletDisplay()
		).thenReturn(
			portletDisplay
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			group
		);

		return themeDisplay;
	}

	private void _invokeGetFrontendTokenDefinitionJSONObject(
		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry,
		ThemeDisplay themeDisplay) {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		EditStyleBookEntryDisplayContext editStyleBookEntryDisplayContext =
			new EditStyleBookEntryDisplayContext(
				null, frontendTokenDefinitionRegistry, httpServletRequest, null,
				new MockRenderResponse());

		ReflectionTestUtil.invoke(
			editStyleBookEntryDisplayContext,
			"_getFrontendTokenDefinitionJSONObject", new Class<?>[0]);
	}

	private static final MockedStatic<JSONFactoryUtil>
		_jsonFactoryUtilMockedStatic = Mockito.mockStatic(
			JSONFactoryUtil.class);
	private static final MockedStatic<LayoutSetLocalServiceUtil>
		_layoutSetLocalServiceUtilMockedStatic = Mockito.mockStatic(
			LayoutSetLocalServiceUtil.class);
	private static final MockedStatic<StyleBookEntryLocalServiceUtil>
		_styleBookEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			StyleBookEntryLocalServiceUtil.class);

}