/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryLocalServiceUtil;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.headless.admin.site.client.dto.v1_0.ClientExtension;
import com.liferay.headless.admin.site.client.dto.v1_0.FavIcon;
import com.liferay.headless.admin.site.client.dto.v1_0.FavIconClientExtension;
import com.liferay.headless.admin.site.client.dto.v1_0.FavIconItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.Scope;
import com.liferay.headless.admin.site.client.dto.v1_0.Settings;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalServiceUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;

/**
 * @author Lourdes Fernández Besada
 */
public class SettingsTestUtil {

	public static void assertPageSpecificationSetting(
			Layout layout, Settings settings)
		throws Exception {

		if (Validator.isNull(layout.getColorSchemeId())) {
			Assert.assertTrue(Validator.isNull(settings.getColorSchemeName()));
		}
		else {
			Assert.assertEquals(
				layout.getColorSchemeId(), settings.getColorSchemeName());
		}

		if (Validator.isNull(layout.getCss())) {
			Assert.assertTrue(Validator.isNull(settings.getCss()));
		}
		else {
			Assert.assertEquals(layout.getCss(), settings.getCss());
		}

		FavIconClientExtension favIconClientExtension = null;
		FavIconItemExternalReference favIconItemExternalReference = null;

		FavIcon favIcon = settings.getFavIcon();

		if (favIcon == null) {
			Assert.assertEquals(0, layout.getFaviconFileEntryId());
		}
		else if (favIcon instanceof FavIconClientExtension) {
			favIconClientExtension = (FavIconClientExtension)favIcon;
		}
		else if (favIcon instanceof FavIconItemExternalReference) {
			favIconItemExternalReference =
				(FavIconItemExternalReference)favIcon;
		}
		else {
			Assert.fail("Unexpected class: " + favIcon.getClass());
		}

		if (layout.getFaviconFileEntryId() == 0) {
			Assert.assertNull(favIconItemExternalReference);
		}
		else {
			DLFileEntry dlFileEntry =
				DLFileEntryLocalServiceUtil.fetchDLFileEntry(
					layout.getFaviconFileEntryId());

			Assert.assertEquals(
				dlFileEntry.getExternalReferenceCode(),
				favIconItemExternalReference.getExternalReferenceCode());

			Scope scope = favIconItemExternalReference.getScope();

			if (scope == null) {
				Assert.assertEquals(
					dlFileEntry.getGroupId(), layout.getGroupId());
			}
			else {
				Group group =
					GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
						scope.getExternalReferenceCode(),
						layout.getCompanyId());

				Assert.assertEquals(
					dlFileEntry.getGroupId(), group.getGroupId());
			}
		}

		_assertClientExtensions(
			settings.getGlobalCSSClientExtensions(), layout,
			ClientExtensionEntryConstants.TYPE_GLOBAL_CSS);
		_assertClientExtensions(
			settings.getGlobalJSClientExtensions(), layout,
			ClientExtensionEntryConstants.TYPE_GLOBAL_JS);
		_assertFavIconClientExtension(favIconClientExtension, layout);

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		Assert.assertEquals(
			unicodeProperties.getProperty("javascript", null),
			settings.getJavascript());

		ItemExternalReference masterPageItemExternalReference =
			settings.getMasterPageItemExternalReference();

		if (layout.getMasterLayoutPlid() == 0) {
			Assert.assertNull(masterPageItemExternalReference);
		}
		else {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchLayoutPageTemplateEntryByPlid(
						layout.getMasterLayoutPlid());

			Assert.assertEquals(
				layoutPageTemplateEntry.getExternalReferenceCode(),
				masterPageItemExternalReference.getExternalReferenceCode());
		}

		ItemExternalReference styleBookItemExternalReference =
			settings.getStyleBookItemExternalReference();

		if (layout.getStyleBookEntryId() == 0) {
			Assert.assertNull(styleBookItemExternalReference);
		}
		else {
			StyleBookEntry styleBookEntry =
				StyleBookEntryLocalServiceUtil.getStyleBookEntry(
					layout.getStyleBookEntryId());

			Assert.assertEquals(
				styleBookEntry.getExternalReferenceCode(),
				styleBookItemExternalReference.getExternalReferenceCode());
		}

		_assertClientExtension(
			settings.getThemeCSSClientExtension(), layout,
			ClientExtensionEntryConstants.TYPE_THEME_CSS);

		if (Validator.isNull(layout.getThemeId())) {
			Assert.assertTrue(Validator.isNull(settings.getThemeName()));
		}
		else {
			Assert.assertEquals(layout.getThemeId(), settings.getThemeName());
		}

		UnicodeProperties themeSettingsUnicodeProperties =
			_getThemeSettingsUnicodeProperties(unicodeProperties);

		if (themeSettingsUnicodeProperties.isEmpty()) {
			Assert.assertNull(settings.getThemeSettings());
		}
		else {
			Map<String, String> themeSettings = settings.getThemeSettings();

			Assert.assertEquals(
				MapUtil.toString(themeSettings),
				themeSettingsUnicodeProperties.size(), themeSettings.size());

			for (Map.Entry<String, String> entry :
					themeSettingsUnicodeProperties.entrySet()) {

				Assert.assertEquals(
					entry.getValue(), themeSettings.get(entry.getKey()));
			}
		}

		_assertClientExtension(
			settings.getThemeSpritemapClientExtension(), layout,
			ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP);
	}

	public static void assertSettings(
		Settings expectedSettings, Settings actualSettings) {

		if (expectedSettings == null) {
			Assert.assertTrue(
				(actualSettings == null) ||
				Objects.equals(actualSettings.toString(), "{}"));

			return;
		}

		Assert.assertEquals(
			expectedSettings.getColorSchemeName(),
			actualSettings.getColorSchemeName());
		Assert.assertEquals(expectedSettings.getCss(), actualSettings.getCss());
		Assert.assertTrue(
			Objects.deepEquals(
				expectedSettings.getFavIcon(), actualSettings.getFavIcon()));
		Assert.assertTrue(
			Objects.deepEquals(
				expectedSettings.getGlobalCSSClientExtensions(),
				actualSettings.getGlobalCSSClientExtensions()));
		Assert.assertTrue(
			Objects.deepEquals(
				expectedSettings.getGlobalJSClientExtensions(),
				actualSettings.getGlobalJSClientExtensions()));
		Assert.assertEquals(
			expectedSettings.getJavascript(), actualSettings.getJavascript());
		Assert.assertTrue(
			Objects.deepEquals(
				expectedSettings.getMasterPageItemExternalReference(),
				actualSettings.getMasterPageItemExternalReference()));
		Assert.assertTrue(
			Objects.deepEquals(
				expectedSettings.getStyleBookItemExternalReference(),
				actualSettings.getStyleBookItemExternalReference()));
		Assert.assertTrue(
			Objects.deepEquals(
				expectedSettings.getThemeCSSClientExtension(),
				actualSettings.getThemeCSSClientExtension()));
		Assert.assertEquals(
			expectedSettings.getThemeName(), actualSettings.getThemeName());

		Map<String, String> themeSettings = expectedSettings.getThemeSettings();
		Map<String, String> curThemeSettings =
			actualSettings.getThemeSettings();

		if (MapUtil.isEmpty(themeSettings)) {
			Assert.assertTrue(
				MapUtil.toString(curThemeSettings),
				MapUtil.isEmpty(curThemeSettings));

			return;
		}

		Assert.assertEquals(
			MapUtil.toString(curThemeSettings), themeSettings,
			curThemeSettings);

		Assert.assertTrue(
			Objects.deepEquals(
				expectedSettings.getThemeSpritemapClientExtension(),
				actualSettings.getThemeSpritemapClientExtension()));
	}

	public static ItemExternalReference getMasterPageItemExternalReference(
			ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_APPROVED);

		return new ItemExternalReference() {
			{
				setExternalReferenceCode(
					layoutPageTemplateEntry::getExternalReferenceCode);
			}
		};
	}

	public static Settings getSettings(
		FavIcon.FavIconType favIconType, ServiceContext serviceContext) {

		return new Settings() {
			{
				setColorSchemeName(() -> "01");
				setCss(RandomTestUtil::randomString);
				setFavIcon(() -> _getFavIcon(favIconType));
				setGlobalCSSClientExtensions(
					() -> new ClientExtension[] {
						_getClientExtension(
							ClientExtensionEntryConstants.TYPE_GLOBAL_CSS),
						_getClientExtension(
							true, ClientExtensionEntryConstants.TYPE_GLOBAL_CSS)
					});
				setGlobalJSClientExtensions(
					() -> new ClientExtension[] {
						_getClientExtension(
							ClientExtensionEntryConstants.TYPE_GLOBAL_JS),
						_getClientExtension(
							true, ClientExtensionEntryConstants.TYPE_GLOBAL_JS)
					});
				setJavascript(RandomTestUtil::randomString);
				setMasterPageItemExternalReference(
					() -> SettingsTestUtil.getMasterPageItemExternalReference(
						serviceContext));
				setStyleBookItemExternalReference(
					() -> SettingsTestUtil.getStyleBookItemExternalReference(
						serviceContext));
				setThemeCSSClientExtension(
					() -> _getClientExtension(
						ClientExtensionEntryConstants.TYPE_THEME_CSS));
				setThemeName(() -> "classic_WAR_classictheme");
				setThemeSettings(
					() -> TreeMapBuilder.put(
						"lfr-theme:" + RandomTestUtil.randomString(),
						RandomTestUtil.randomString()
					).put(
						"lfr-theme:" + RandomTestUtil.randomString(),
						RandomTestUtil.randomString()
					).build());
				setThemeSpritemapClientExtension(
					() -> _getClientExtension(
						ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP));
			}
		};
	}

	public static ItemExternalReference getStyleBookItemExternalReference(
			ServiceContext serviceContext)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			StyleBookEntryLocalServiceUtil.addStyleBookEntry(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(), false, null,
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), serviceContext);

		return new ItemExternalReference() {
			{
				setExternalReferenceCode(
					styleBookEntry::getExternalReferenceCode);
			}
		};
	}

	public static void modifySettings(
			FavIcon.FavIconType favIconType, ServiceContext serviceContext,
			Settings settings)
		throws Exception {

		if (Validator.isNotNull(settings.getFavIcon())) {
			settings.setFavIcon(() -> null);
		}
		else {
			settings.setFavIcon(() -> _getFavIcon(favIconType));
		}

		if (Validator.isNotNull(settings.getGlobalCSSClientExtensions())) {
			settings.setGlobalCSSClientExtensions(() -> null);
		}
		else {
			settings.setGlobalCSSClientExtensions(
				new ClientExtension[] {
					_getClientExtension(
						ClientExtensionEntryConstants.TYPE_GLOBAL_CSS),
					_getClientExtension(
						ClientExtensionEntryConstants.TYPE_GLOBAL_CSS)
				});
		}

		if (Validator.isNotNull(settings.getGlobalJSClientExtensions())) {
			settings.setGlobalJSClientExtensions(() -> null);
		}
		else {
			settings.setGlobalJSClientExtensions(
				new ClientExtension[] {
					_getClientExtension(
						ClientExtensionEntryConstants.TYPE_GLOBAL_JS),
					_getClientExtension(
						ClientExtensionEntryConstants.TYPE_GLOBAL_JS)
				});
		}

		if (Validator.isNotNull(settings.getJavascript())) {
			settings.setJavascript(() -> null);
		}
		else {
			settings.setJavascript(RandomTestUtil::randomString);
		}

		if (settings.getMasterPageItemExternalReference() != null) {
			settings.setMasterPageItemExternalReference(() -> null);
		}
		else {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				LayoutPageTemplateEntryTestUtil.
					getMasterLayoutPageTemplateEntry(
						serviceContext, WorkflowConstants.STATUS_APPROVED);

			settings.setMasterPageItemExternalReference(
				() -> new ItemExternalReference() {
					{
						setExternalReferenceCode(
							layoutPageTemplateEntry::getExternalReferenceCode);
					}
				});
		}

		if (settings.getStyleBookItemExternalReference() != null) {
			settings.setStyleBookItemExternalReference(() -> null);
		}
		else {
			StyleBookEntry styleBookEntry =
				StyleBookEntryLocalServiceUtil.addStyleBookEntry(
					null, TestPropsValues.getUserId(),
					serviceContext.getScopeGroupId(), false, null,
					RandomTestUtil.randomString(), null,
					RandomTestUtil.randomString(), serviceContext);

			settings.setStyleBookItemExternalReference(
				() -> new ItemExternalReference() {
					{
						setExternalReferenceCode(
							styleBookEntry::getExternalReferenceCode);
					}
				});
		}

		if (Validator.isNotNull(settings.getThemeCSSClientExtension())) {
			settings.setThemeCSSClientExtension(() -> null);
		}
		else {
			settings.setThemeCSSClientExtension(
				_getClientExtension(
					ClientExtensionEntryConstants.TYPE_THEME_CSS));
		}

		if (Validator.isNotNull(settings.getThemeName())) {
			settings.setColorSchemeName(() -> null);
			settings.setThemeName(() -> null);
		}
		else {
			if (RandomTestUtil.randomBoolean()) {
				settings.setColorSchemeName("01");
			}

			settings.setThemeName("classic_WAR_classictheme");
		}

		if (Validator.isNotNull(settings.getThemeSettings())) {
			settings.setThemeSettings(() -> null);
		}
		else {
			settings.setThemeSettings(
				() -> HashMapBuilder.put(
					"lfr-theme:regular:show-maximize-minimize-application-" +
						"links",
					"true"
				).build());
		}

		if (Validator.isNotNull(settings.getThemeSpritemapClientExtension())) {
			settings.setThemeSpritemapClientExtension(() -> null);
		}
		else {
			settings.setThemeSpritemapClientExtension(
				_getClientExtension(
					ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP));
		}
	}

	private static void _assertClientExtension(
		ClientExtension clientExtension, Layout layout, String type) {

		ClientExtensionEntryRel clientExtensionEntryRel =
			ClientExtensionEntryRelLocalServiceUtil.
				fetchClientExtensionEntryRel(
					PortalUtil.getClassNameId(Layout.class), layout.getPlid(),
					type);

		if (clientExtensionEntryRel == null) {
			Assert.assertNull(clientExtension);
		}
		else {
			Assert.assertEquals(
				clientExtensionEntryRel.getCETExternalReferenceCode(),
				clientExtension.getExternalReferenceCode());
			Assert.assertEquals(
				clientExtensionEntryRel.getTypeSettings(),
				UnicodePropertiesBuilder.create(
					clientExtension.getClientExtensionConfig(), true
				).buildString());
		}
	}

	private static void _assertClientExtensions(
		ClientExtension[] clientExtensions, Layout layout, String type) {

		List<ClientExtensionEntryRel> clientExtensionEntryRels =
			ClientExtensionEntryRelLocalServiceUtil.getClientExtensionEntryRels(
				PortalUtil.getClassNameId(Layout.class), layout.getPlid(),
				type);

		if (ArrayUtil.isEmpty(clientExtensions)) {
			Assert.assertEquals(
				clientExtensionEntryRels.toString(), 0,
				clientExtensionEntryRels.size());

			return;
		}

		Map<String, String> map = new HashMap<>();

		for (ClientExtension clientExtension : clientExtensions) {
			map.put(
				clientExtension.getExternalReferenceCode(),
				UnicodePropertiesBuilder.create(
					clientExtension.getClientExtensionConfig(), true
				).buildString());
		}

		for (ClientExtensionEntryRel clientExtensionEntryRel :
				clientExtensionEntryRels) {

			Assert.assertTrue(
				clientExtensionEntryRel.toString(),
				map.containsKey(
					clientExtensionEntryRel.getCETExternalReferenceCode()));
			Assert.assertEquals(
				map.get(clientExtensionEntryRel.getCETExternalReferenceCode()),
				clientExtensionEntryRel.getTypeSettings());
		}
	}

	private static void _assertFavIconClientExtension(
		FavIconClientExtension favIconClientExtension, Layout layout) {

		ClientExtensionEntryRel clientExtensionEntryRel =
			ClientExtensionEntryRelLocalServiceUtil.
				fetchClientExtensionEntryRel(
					PortalUtil.getClassNameId(Layout.class), layout.getPlid(),
					ClientExtensionEntryConstants.TYPE_THEME_FAVICON);

		if (clientExtensionEntryRel == null) {
			Assert.assertNull(favIconClientExtension);
		}
		else {
			Assert.assertEquals(
				clientExtensionEntryRel.getCETExternalReferenceCode(),
				favIconClientExtension.getExternalReferenceCode());
			Assert.assertEquals(
				clientExtensionEntryRel.getTypeSettings(),
				UnicodePropertiesBuilder.create(
					favIconClientExtension.getClientExtensionConfig(), true
				).buildString());
		}
	}

	private static ClientExtension _getClientExtension(
			boolean optionalReference, String type)
		throws Exception {

		ClientExtension clientExtension = new ClientExtension() {
			{
				setClientExtensionConfig(
					() -> HashMapBuilder.put(
						"url", "http://test.com"
					).build());
				setExternalReferenceCode(RandomTestUtil::randomString);
			}
		};

		if (!optionalReference) {
			ClientExtensionEntryLocalServiceUtil.addClientExtensionEntry(
				clientExtension.getExternalReferenceCode(),
				TestPropsValues.getUserId(), StringPool.BLANK,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				StringPool.BLANK, StringPool.BLANK, type,
				UnicodePropertiesBuilder.create(
					clientExtension.getClientExtensionConfig(), true
				).buildString());
		}

		return clientExtension;
	}

	private static ClientExtension _getClientExtension(String type)
		throws Exception {

		return _getClientExtension(false, type);
	}

	private static FavIcon _getFavIcon(FavIcon.FavIconType favIconType)
		throws Exception {

		if (favIconType == FavIcon.FavIconType.CLIENT_EXTENSION) {
			return _getFavIconClientExtension();
		}

		return _getFavIconItemExternalReference();
	}

	private static FavIconClientExtension _getFavIconClientExtension()
		throws Exception {

		FavIconClientExtension favIconClientExtension =
			new FavIconClientExtension() {
				{
					setClientExtensionConfig(
						() -> HashMapBuilder.put(
							"url", "http://test.com"
						).build());
					setExternalReferenceCode(RandomTestUtil::randomString);
					setFavIconType(FavIcon.FavIconType.CLIENT_EXTENSION);
				}
			};

		ClientExtensionEntryLocalServiceUtil.addClientExtensionEntry(
			favIconClientExtension.getExternalReferenceCode(),
			TestPropsValues.getUserId(), StringPool.BLANK,
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			StringPool.BLANK, StringPool.BLANK,
			ClientExtensionEntryConstants.TYPE_THEME_FAVICON,
			UnicodePropertiesBuilder.create(
				favIconClientExtension.getClientExtensionConfig(), true
			).buildString());

		return favIconClientExtension;
	}

	private static FavIconItemExternalReference
			_getFavIconItemExternalReference()
		throws Exception {

		Company company = CompanyLocalServiceUtil.getCompany(
			TestPropsValues.getCompanyId());

		DLFolder dlFolder = DLTestUtil.addDLFolder(company.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		return new FavIconItemExternalReference() {
			{
				setClassName(FileEntry.class::getName);
				setExternalReferenceCode(dlFileEntry::getExternalReferenceCode);
				setFavIconType(FavIconType.ITEM_EXTERNAL_REFERENCE);
				setScope(
					() -> new Scope() {
						{
							setExternalReferenceCode(() -> "L_GLOBAL");
							setType(() -> Type.SITE);
						}
					});
			}
		};
	}

	private static UnicodeProperties _getThemeSettingsUnicodeProperties(
		UnicodeProperties unicodeProperties) {

		UnicodeProperties themeSettingsUnicodeProperties =
			new UnicodeProperties();

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			String key = entry.getKey();

			if (key.startsWith("lfr-theme:")) {
				themeSettingsUnicodeProperties.setProperty(
					key, entry.getValue());
			}
		}

		return themeSettingsUnicodeProperties;
	}

}