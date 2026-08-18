/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.internal.constants.PIMFDSNames;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

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
public class ProductRelationshipsDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAPIURL() {
		ObjectEntry objectEntry = _mockObjectEntry();

		ProductRelationshipsDisplayContext productRelationshipsDisplayContext =
			_getProductRelationshipsDisplayContext(
				_mockHttpServletRequest(objectEntry, null));

		Assert.assertEquals(
			StringBundler.concat(
				"/o/headless-pim/v1.0/scopes/", objectEntry.getGroupId(),
				"/links?className=",
				URLCodec.encodeURL(objectEntry.getModelClassName()),
				"&externalReferenceCode=",
				URLCodec.encodeURL(objectEntry.getExternalReferenceCode())),
			productRelationshipsDisplayContext.getAPIURL());
	}

	@Test
	public void testGetCreationMenu() throws Exception {
		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				Mockito.any(HttpServletRequest.class),
				Mockito.eq("add-relationship"))
		).thenReturn(
			"Add Relationship"
		);

		_languageUtil.setLanguage(language);

		FDSSerializer fdsSerializer = Mockito.mock(FDSSerializer.class);

		JSONArray filtersJSONArray = Mockito.mock(JSONArray.class);

		Mockito.when(
			filtersJSONArray.toString()
		).thenReturn(
			"[\"filter\"]"
		);

		Mockito.when(
			fdsSerializer.serializeFilters(
				Mockito.eq(PIMFDSNames.PRODUCT_RELATIONSHIP_SELECTOR),
				Mockito.any())
		).thenReturn(
			filtersJSONArray
		);

		ObjectEntry objectEntry = _mockObjectEntry();
		ObjectEntryLocalService objectEntryLocalService = Mockito.mock(
			ObjectEntryLocalService.class);

		Mockito.when(
			objectEntryLocalService.getValues(objectEntry)
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"name", "Product Name"
			).build()
		);

		ThemeDisplay themeDisplay = _mockThemeDisplay();

		ProductRelationshipsDisplayContext productRelationshipsDisplayContext =
			new ProductRelationshipsDisplayContext(
				fdsSerializer,
				_mockHttpServletRequest(objectEntry, themeDisplay),
				objectEntryLocalService);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<ObjectEntryServiceUtil>
				objectEntryServiceUtilMockedStatic = Mockito.mockStatic(
					ObjectEntryServiceUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				false
			);

			List<DropdownItem> dropdownItems = _getPrimaryDropdownItems(
				productRelationshipsDisplayContext.getCreationMenu());

			Assert.assertTrue(dropdownItems.isEmpty());

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				true
			);

			_mockModelResourcePermission(
				false, objectEntry, objectEntryServiceUtilMockedStatic);

			dropdownItems = _getPrimaryDropdownItems(
				productRelationshipsDisplayContext.getCreationMenu());

			Assert.assertTrue(dropdownItems.isEmpty());

			_mockModelResourcePermission(
				true, objectEntry, objectEntryServiceUtilMockedStatic);

			dropdownItems = _getPrimaryDropdownItems(
				productRelationshipsDisplayContext.getCreationMenu());

			Assert.assertEquals(
				dropdownItems.toString(), 1, dropdownItems.size());

			DropdownItem dropdownItem = dropdownItems.get(0);

			Assert.assertEquals("Add Relationship", dropdownItem.get("label"));

			Map<?, ?> data = (Map<?, ?>)dropdownItem.get("data");

			Assert.assertEquals(
				"createProductRelationship", data.get("action"));
			Assert.assertEquals(
				objectEntry.getModelClassName(), data.get("className"));
			Assert.assertEquals(
				objectEntry.getExternalReferenceCode(),
				data.get("externalReferenceCode"));
			Assert.assertEquals("[\"filter\"]", data.get("filters"));
			Assert.assertEquals("Product Name", data.get("name"));
			Assert.assertEquals(
				String.valueOf(objectEntry.getGroupId()), data.get("scopeKey"));

			String searchAPIURL = (String)data.get("searchAPIURL");

			Assert.assertTrue(
				searchAPIURL,
				searchAPIURL.contains(
					URLCodec.encodeURL(
						StringBundler.concat(
							"cmsSection eq 'products' and groupIds/any(g:g eq ",
							objectEntry.getGroupId(),
							") and not (entryClassPK in (",
							objectEntry.getObjectEntryId()))));
			Assert.assertTrue(
				searchAPIURL, searchAPIURL.contains("{relatedObjectEntryIds}"));
		}
	}

	@Test
	public void testGetEmptyState() {
		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				Mockito.any(HttpServletRequest.class),
				Mockito.eq("no-relationships-were-found"))
		).thenReturn(
			"No relationships were found."
		);

		_languageUtil.setLanguage(language);

		ProductRelationshipsDisplayContext productRelationshipsDisplayContext =
			_getProductRelationshipsDisplayContext(
				_mockHttpServletRequest(null, null));

		Map<String, Object> emptyState =
			productRelationshipsDisplayContext.getEmptyState();

		Assert.assertEquals(
			"/states/cms_empty_state_preview.svg", emptyState.get("image"));
		Assert.assertEquals(
			"No relationships were found.", emptyState.get("title"));
	}

	@Test
	public void testGetFDSActionDropdownItems() {
		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				Mockito.any(HttpServletRequest.class),
				Mockito.eq("are-you-sure-you-want-to-delete-this"))
		).thenReturn(
			"Are you sure?"
		);

		Mockito.when(
			language.get(
				Mockito.any(HttpServletRequest.class), Mockito.eq("delete"))
		).thenReturn(
			"Delete"
		);

		_languageUtil.setLanguage(language);

		ObjectEntry objectEntry = _mockObjectEntry();
		ThemeDisplay themeDisplay = _mockThemeDisplay();

		ProductRelationshipsDisplayContext productRelationshipsDisplayContext =
			_getProductRelationshipsDisplayContext(
				_mockHttpServletRequest(objectEntry, themeDisplay));

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
				productRelationshipsDisplayContext.getFDSActionDropdownItems();

			Assert.assertTrue(fdsActionDropdownItems.isEmpty());

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				true
			);

			fdsActionDropdownItems =
				productRelationshipsDisplayContext.getFDSActionDropdownItems();

			Assert.assertEquals(
				fdsActionDropdownItems.toString(), 1,
				fdsActionDropdownItems.size());

			FDSActionDropdownItem fdsActionDropdownItem =
				fdsActionDropdownItems.get(0);

			Assert.assertEquals(
				"{actions.delete.href}", fdsActionDropdownItem.get("href"));
			Assert.assertEquals("trash", fdsActionDropdownItem.get("icon"));
			Assert.assertEquals("Delete", fdsActionDropdownItem.get("label"));
			Assert.assertEquals(
				"headless", fdsActionDropdownItem.get("target"));

			Map<?, ?> data = (Map<?, ?>)fdsActionDropdownItem.get("data");

			Assert.assertEquals(
				"Are you sure?", data.get("confirmationMessage"));
			Assert.assertEquals("delete", data.get("id"));
			Assert.assertEquals("delete", data.get("method"));
			Assert.assertEquals("delete", data.get("permissionKey"));
		}
	}

	private List<DropdownItem> _getPrimaryDropdownItems(
		CreationMenu creationMenu) {

		return ReflectionTestUtil.getFieldValue(
			creationMenu, "_primaryDropdownItems");
	}

	private ProductRelationshipsDisplayContext
		_getProductRelationshipsDisplayContext(
			HttpServletRequest httpServletRequest) {

		return new ProductRelationshipsDisplayContext(
			Mockito.mock(FDSSerializer.class), httpServletRequest,
			Mockito.mock(ObjectEntryLocalService.class));
	}

	private HttpServletRequest _mockHttpServletRequest(
		ObjectEntry objectEntry, ThemeDisplay themeDisplay) {

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(InfoDisplayWebKeys.INFO_ITEM)
		).thenReturn(
			objectEntry
		);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		return httpServletRequest;
	}

	private void _mockModelResourcePermission(
		boolean contains, ObjectEntry objectEntry,
		MockedStatic<ObjectEntryServiceUtil>
			objectEntryServiceUtilMockedStatic) {

		try {
			ModelResourcePermission<ObjectEntry> modelResourcePermission =
				Mockito.mock(ModelResourcePermission.class);

			Mockito.when(
				modelResourcePermission.contains(
					Mockito.any(PermissionChecker.class), Mockito.anyLong(),
					Mockito.eq(ActionKeys.UPDATE))
			).thenReturn(
				contains
			);

			objectEntryServiceUtilMockedStatic.when(
				() -> ObjectEntryServiceUtil.getModelResourcePermission(
					objectEntry.getObjectDefinitionId())
			).thenReturn(
				modelResourcePermission
			);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private ObjectEntry _mockObjectEntry() {
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			objectEntry.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			objectEntry.getModelClassName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			objectEntry.getObjectDefinitionId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			objectEntry.getObjectEntryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		return objectEntry;
	}

	private ThemeDisplay _mockThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			themeDisplay.getPathMain()
		).thenReturn(
			"/c"
		);

		Mockito.when(
			themeDisplay.getPermissionChecker()
		).thenReturn(
			Mockito.mock(PermissionChecker.class)
		);

		Mockito.when(
			themeDisplay.getPortalURL()
		).thenReturn(
			"http://localhost:8080"
		);

		Mockito.when(
			themeDisplay.getURLCurrent()
		).thenReturn(
			"/web/cms/product"
		);

		return themeDisplay;
	}

	private final LanguageUtil _languageUtil = new LanguageUtil();

}