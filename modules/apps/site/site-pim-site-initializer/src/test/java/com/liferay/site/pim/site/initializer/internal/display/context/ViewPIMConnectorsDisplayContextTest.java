/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 */
public class ViewPIMConnectorsDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAPIURL() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getRESTContextPath()
		).thenReturn(
			"/pim/connectors"
		);

		ViewPIMConnectorsDisplayContext viewPIMConnectorsDisplayContext =
			new ViewPIMConnectorsDisplayContext(
				httpServletRequest, objectDefinition);

		Assert.assertEquals(
			"/o/pim/connectors",
			viewPIMConnectorsDisplayContext.getAPIURL());
	}

	@Test
	public void testGetAPIURLWithNullObjectDefinition() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ViewPIMConnectorsDisplayContext viewPIMConnectorsDisplayContext =
			new ViewPIMConnectorsDisplayContext(httpServletRequest, null);

		Assert.assertEquals(
			StringPool.BLANK, viewPIMConnectorsDisplayContext.getAPIURL());
	}

	@Test
	public void testGetEmptyState() {
		LanguageUtil languageUtil = new LanguageUtil();

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				httpServletRequest,
				"create-a-connector-to-link-the-pim-to-a-shopping-experience")
		).thenReturn(
			"Create a connector to link the PIM to a shopping experience."
		);

		Mockito.when(
			language.get(httpServletRequest, "no-connectors-yet")
		).thenReturn(
			"No Connectors Yet"
		);

		languageUtil.setLanguage(language);

		ViewPIMConnectorsDisplayContext viewPIMConnectorsDisplayContext =
			new ViewPIMConnectorsDisplayContext(httpServletRequest, null);

		Map<String, Object> emptyState =
			viewPIMConnectorsDisplayContext.getEmptyState();

		Assert.assertEquals(
			"Create a connector to link the PIM to a shopping experience.",
			emptyState.get("description"));
		Assert.assertEquals("No Connectors Yet", emptyState.get("title"));
	}

	@Test
	public void testGetFDSActionDropdownItems() {
		LanguageUtil languageUtil = new LanguageUtil();

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);
		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				httpServletRequest, "are-you-sure-you-want-to-delete-this")
		).thenReturn(
			"Are you sure?"
		);

		Mockito.when(
			language.get(httpServletRequest, "delete")
		).thenReturn(
			"Delete"
		);

		Mockito.when(
			language.get(httpServletRequest, "edit")
		).thenReturn(
			"Edit"
		);

		languageUtil.setLanguage(language);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getFriendlyURL()
		).thenReturn(
			"/cms"
		);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getPathFriendlyURLPublic()
		).thenReturn(
			"/web"
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			group
		);

		Mockito.when(
			themeDisplay.getURLCurrent()
		).thenReturn(
			"/web/cms/connectors"
		);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		ViewPIMConnectorsDisplayContext viewPIMConnectorsDisplayContext =
			new ViewPIMConnectorsDisplayContext(httpServletRequest, null);

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			viewPIMConnectorsDisplayContext.getFDSActionDropdownItems();

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 2,
			fdsActionDropdownItems.size());

		FDSActionDropdownItem editFDSActionDropdownItem =
			fdsActionDropdownItems.get(0);

		String editHref = String.valueOf(editFDSActionDropdownItem.get("href"));

		Assert.assertTrue(editHref, editHref.contains("/edit-connector"));
		Assert.assertTrue(
			editHref, editHref.contains("&objectEntryId={id}"));
		Assert.assertEquals("pencil", editFDSActionDropdownItem.get("icon"));
		Assert.assertEquals("Edit", editFDSActionDropdownItem.get("label"));

		Map<?, ?> data = (Map<?, ?>)editFDSActionDropdownItem.get("data");

		Assert.assertEquals("edit", data.get("id"));
		Assert.assertEquals("get", data.get("method"));
		Assert.assertEquals("update", data.get("permissionKey"));

		FDSActionDropdownItem deleteFDSActionDropdownItem =
			fdsActionDropdownItems.get(1);

		Assert.assertEquals(
			"{actions.delete.href}",
			deleteFDSActionDropdownItem.get("href"));
		Assert.assertEquals(
			"trash", deleteFDSActionDropdownItem.get("icon"));
		Assert.assertEquals(
			"Delete", deleteFDSActionDropdownItem.get("label"));
		Assert.assertEquals(
			"headless", deleteFDSActionDropdownItem.get("target"));

		data = (Map<?, ?>)deleteFDSActionDropdownItem.get("data");

		Assert.assertEquals("Are you sure?", data.get("confirmationMessage"));
		Assert.assertEquals("delete", data.get("id"));
		Assert.assertEquals("delete", data.get("method"));
		Assert.assertEquals("delete", data.get("permissionKey"));
	}

}
