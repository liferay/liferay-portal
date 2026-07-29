/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class ProductsSectionDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAPIURL() {
		ProductsSectionDisplayContext productsSectionDisplayContext =
			new ProductsSectionDisplayContext(null);

		Assert.assertEquals(
			StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=true&filter=",
				URLCodec.encodeURL("cmsSection eq 'products'"),
				"&nestedFields=embedded,systemProperties.",
				"objectDefinitionBrief"),
			productsSectionDisplayContext.getAPIURL());
	}

	@Test
	public void testGetBulkActionDropdownItems() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			language.get(httpServletRequest, "delete")
		).thenReturn(
			"Delete"
		);

		languageUtil.setLanguage(language);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		ProductsSectionDisplayContext productsSectionDisplayContext =
			new ProductsSectionDisplayContext(httpServletRequest);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				false
			);

			List<DropdownItem> bulkActionDropdownItems =
				productsSectionDisplayContext.getBulkActionDropdownItems();

			Assert.assertTrue(bulkActionDropdownItems.isEmpty());

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				true
			);

			bulkActionDropdownItems =
				productsSectionDisplayContext.getBulkActionDropdownItems();

			Assert.assertEquals(
				bulkActionDropdownItems.toString(), 1,
				bulkActionDropdownItems.size());

			DropdownItem deleteDropdownItem = bulkActionDropdownItems.get(0);

			Assert.assertEquals("#", deleteDropdownItem.get("href"));
			Assert.assertEquals("trash", deleteDropdownItem.get("icon"));
			Assert.assertEquals("Delete", deleteDropdownItem.get("label"));

			Map<?, ?> data = (Map<?, ?>)deleteDropdownItem.get("data");

			Assert.assertEquals("delete", data.get("id"));
		}
	}

	@Test
	public void testGetEmptyState() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			language.get(
				httpServletRequest, "click-new-to-create-your-first-product")
		).thenReturn(
			"Click \"New\" to create your first product."
		);

		Mockito.when(
			language.get(httpServletRequest, "no-products-yet")
		).thenReturn(
			"No Products Yet"
		);

		languageUtil.setLanguage(language);

		ProductsSectionDisplayContext productsSectionDisplayContext =
			new ProductsSectionDisplayContext(httpServletRequest);

		Map<String, Object> emptyState =
			productsSectionDisplayContext.getEmptyState();

		Assert.assertEquals(
			"Click \"New\" to create your first product.",
			emptyState.get("description"));
		Assert.assertEquals(
			"/states/cms_empty_state_content.svg", emptyState.get("image"));
		Assert.assertEquals("No Products Yet", emptyState.get("title"));
	}

	@Test
	public void testGetFDSActionDropdownItems() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

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

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			1L
		);

		Mockito.when(
			themeDisplay.getPathMain()
		).thenReturn(
			"/c"
		);

		Mockito.when(
			themeDisplay.getPortalURL()
		).thenReturn(
			"http://localhost:8080"
		);

		Mockito.when(
			themeDisplay.getURLCurrent()
		).thenReturn(
			"/web/cms/products"
		);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		ProductsSectionDisplayContext productsSectionDisplayContext =
			new ProductsSectionDisplayContext(httpServletRequest);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				false
			);

			List<FDSActionDropdownItem> fdsActionDropdownItems =
				productsSectionDisplayContext.getFDSActionDropdownItems();

			Assert.assertTrue(fdsActionDropdownItems.isEmpty());

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				true
			);

			fdsActionDropdownItems =
				productsSectionDisplayContext.getFDSActionDropdownItems();

			Assert.assertEquals(
				fdsActionDropdownItems.toString(), 2,
				fdsActionDropdownItems.size());

			FDSActionDropdownItem editFDSActionDropdownItem =
				fdsActionDropdownItems.get(0);

			Assert.assertTrue(
				String.valueOf(
					editFDSActionDropdownItem.get("href")
				).contains(
					"/edit_content_item?objectEntryId={embedded.id}"
				));
			Assert.assertEquals(
				"pencil", editFDSActionDropdownItem.get("icon"));
			Assert.assertEquals("Edit", editFDSActionDropdownItem.get("label"));

			Map<?, ?> data = (Map<?, ?>)editFDSActionDropdownItem.get("data");

			Assert.assertEquals("edit", data.get("id"));
			Assert.assertEquals("get", data.get("method"));
			Assert.assertEquals("update", data.get("permissionKey"));

			FDSActionDropdownItem deleteFDSActionDropdownItem =
				fdsActionDropdownItems.get(1);

			Assert.assertEquals(
				"{embedded.actions.delete.href}",
				deleteFDSActionDropdownItem.get("href"));
			Assert.assertEquals(
				"trash", deleteFDSActionDropdownItem.get("icon"));
			Assert.assertEquals(
				"Delete", deleteFDSActionDropdownItem.get("label"));
			Assert.assertEquals(
				"headless", deleteFDSActionDropdownItem.get("target"));

			data = (Map<?, ?>)deleteFDSActionDropdownItem.get("data");

			Assert.assertEquals(
				"Are you sure?", data.get("confirmationMessage"));
			Assert.assertEquals("delete", data.get("id"));
			Assert.assertEquals("delete", data.get("method"));
			Assert.assertEquals("delete", data.get("permissionKey"));
		}
	}

}