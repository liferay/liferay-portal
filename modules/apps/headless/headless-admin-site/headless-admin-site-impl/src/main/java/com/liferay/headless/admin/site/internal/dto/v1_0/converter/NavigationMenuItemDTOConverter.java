/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageNavigationMenuItemSettings;
import com.liferay.headless.admin.site.dto.v1_0.NavigationMenuItem;
import com.liferay.headless.admin.site.dto.v1_0.PageNavigationMenuItemSettings;
import com.liferay.headless.admin.site.dto.v1_0.URLNavigationMenuItemSettings;
import com.liferay.headless.admin.site.dto.v1_0.VocabularyNavigationMenuItemSettings;
import com.liferay.headless.admin.site.internal.constants.DisplayPageTypeSiteNavigationMenuItemTypeConstants;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = DTOConverter.class)
public class NavigationMenuItemDTOConverter
	implements DTOConverter<SiteNavigationMenuItem, NavigationMenuItem> {

	@Override
	public String getContentType() {
		return NavigationMenuItem.class.getSimpleName();
	}

	@Override
	public NavigationMenuItem toDTO(
			DTOConverterContext dtoConverterContext,
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws Exception {

		UnicodeProperties unicodeProperties = _getUnicodeProperties(
			siteNavigationMenuItem);

		Layout layout = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			unicodeProperties.getProperty("externalReferenceCode"),
			siteNavigationMenuItem.getGroupId());

		String navigationMenuItemType = _toType(
			siteNavigationMenuItem.getType());

		return new NavigationMenuItem() {
			{
				setAvailableLanguages(
					() -> {
						Map<Locale, String> localizedMap =
							_getLocalizedNamesFromProperties(unicodeProperties);

						Set<Locale> locales = localizedMap.keySet();

						return LocaleUtil.toW3cLanguageIds(
							locales.toArray(new Locale[localizedMap.size()]));
					});
				setCreator(
					() -> CreatorUtil.toCreator(
						siteNavigationMenuItem.getUserId(),
						siteNavigationMenuItem.getUserName()));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						SiteNavigationMenuItem.class.getName(),
						siteNavigationMenuItem.getSiteNavigationMenuItemId(),
						siteNavigationMenuItem.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(siteNavigationMenuItem::getCreateDate);
				setDateModified(siteNavigationMenuItem::getModifiedDate);
				setDefaultLanguageId(
					() -> {
						String defaultLanguageId =
							unicodeProperties.getProperty("defaultLanguageId");

						if (defaultLanguageId != null) {
							return defaultLanguageId;
						}

						return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
					});
				setDisplayIcon(
					() -> unicodeProperties.getProperty("displayIcon"));
				setExternalReferenceCode(
					siteNavigationMenuItem::getExternalReferenceCode);
				setId(siteNavigationMenuItem::getSiteNavigationMenuItemId);
				setName(
					() -> _getName(
						layout, dtoConverterContext.getLocale(),
						navigationMenuItemType, unicodeProperties,
						getUseCustomName()));
				setName_i18n(
					() -> {
						Map<Locale, String> localizedNames =
							_getLocalizedNamesFromProperties(unicodeProperties);

						if ((!useCustomName || localizedNames.isEmpty()) &&
							(layout != null)) {

							localizedNames = layout.getNameMap();
						}

						return LocalizedMapUtil.getI18nMap(localizedNames);
					});
				setNavigationMenuItems(
					() -> {
						Map<Long, List<SiteNavigationMenuItem>>
							siteNavigationMenuItemsMap =
								(Map<Long, List<SiteNavigationMenuItem>>)
									dtoConverterContext.getAttribute(
										"siteNavigationMenuItemsMap");

						if (siteNavigationMenuItemsMap == null) {
							return null;
						}

						return TransformUtil.transformToArray(
							siteNavigationMenuItemsMap.getOrDefault(
								siteNavigationMenuItem.
									getSiteNavigationMenuItemId(),
								new ArrayList<>()),
							item -> {
								DefaultDTOConverterContext
									defaultDTOConverterContext =
										new DefaultDTOConverterContext(
											dtoConverterContext.
												isAcceptAllLanguages(),
											null, _dtoConverterRegistry,
											siteNavigationMenuItem.
												getSiteNavigationMenuItemId(),
											dtoConverterContext.getLocale(),
											dtoConverterContext.getUriInfo(),
											dtoConverterContext.getUser());

								defaultDTOConverterContext.setAttribute(
									"siteNavigationMenuItemsMap",
									siteNavigationMenuItemsMap);

								return NavigationMenuItemDTOConverter.this.
									toDTO(defaultDTOConverterContext, item);
							},
							NavigationMenuItem.class);
					});
				setNavigationMenuItemSettings(
					() -> _getNavigationMenuItemSettings(
						siteNavigationMenuItem.getType(), unicodeProperties));
				setType(siteNavigationMenuItem::getType);
				setUseCustomName(
					() -> Boolean.valueOf(
						unicodeProperties.getProperty("useCustomName")));
			}
		};
	}

	private Locale _getLocaleFromProperty(Map.Entry<String, String> property) {
		return LocaleUtil.fromLanguageId(
			StringUtil.removeSubstring(property.getKey(), "name_"));
	}

	private Map<Locale, String> _getLocalizedNamesFromProperties(
			UnicodeProperties unicodeProperties)
		throws JSONException {

		if (unicodeProperties == null) {
			return new HashMap<>();
		}

		String localizedNames = unicodeProperties.getProperty("localizedNames");

		Map<Locale, String> properties = new HashMap<>();

		if (localizedNames != null) {
			JSONObject localizedNamesJSONObject = _jsonFactory.createJSONObject(
				localizedNames);

			for (String key : localizedNamesJSONObject.keySet()) {
				properties.put(
					LocaleUtil.fromLanguageId(key),
					localizedNamesJSONObject.getString(key));
			}

			return properties;
		}

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			if (!_isNameProperty(entry)) {
				continue;
			}

			properties.put(_getLocaleFromProperty(entry), entry.getValue());
		}

		return properties;
	}

	private String _getName(
			Layout layout, Locale preferredLocale, String type,
			UnicodeProperties unicodeProperties, boolean useCustomName)
		throws JSONException {

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getDefault());
		String preferredLanguageId = LocaleUtil.toLanguageId(preferredLocale);

		if (StringUtil.equals(type, "page")) {
			if (useCustomName) {
				return unicodeProperties.getProperty(
					"name_" + preferredLanguageId,
					unicodeProperties.getProperty("name_" + defaultLanguageId));
			}

			if (layout != null) {
				return layout.getName(preferredLocale);
			}
		}

		if (useCustomName) {
			JSONObject customNameJSONObject = _jsonFactory.createJSONObject(
				unicodeProperties.getProperty("localizedNames"));

			return customNameJSONObject.getString(
				preferredLanguageId,
				customNameJSONObject.getString(defaultLanguageId));
		}

		if (StringUtil.equals(type, "navigationMenu") ||
			StringUtil.equals(type, "url")) {

			return unicodeProperties.getProperty(
				"name_" + preferredLanguageId,
				unicodeProperties.getProperty("name_" + defaultLanguageId));
		}

		return unicodeProperties.getProperty("title");
	}

	private Object _getNavigationMenuItemSettings(
		String type, UnicodeProperties unicodeProperties) {

		if (Objects.equals(
				type, SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY)) {

			return new VocabularyNavigationMenuItemSettings() {
				{
					setClassName(
						() -> unicodeProperties.getProperty("className"));
					setExternalReferenceCode(
						() -> unicodeProperties.getProperty(
							"externalReferenceCode"));
					setScopeExternalReferenceCode(
						() -> unicodeProperties.getProperty(
							"scopeExternalReferenceCode"));
					setShowAssetVocabularyLevel(
						() -> Boolean.valueOf(
							unicodeProperties.getProperty(
								"showAssetVocabularyLevel")));
					setTitle(() -> unicodeProperties.getProperty("title"));
					setType(() -> unicodeProperties.getProperty("type"));
				}
			};
		}
		else if (Objects.equals(
					type, SiteNavigationMenuItemTypeConstants.LAYOUT)) {

			return new PageNavigationMenuItemSettings() {
				{
					setExternalReferenceCode(
						() -> unicodeProperties.getProperty(
							"externalReferenceCode"));
					setPrivatePage(
						() -> Boolean.valueOf(
							unicodeProperties.getProperty("privateLayout")));
					setTitle(() -> unicodeProperties.getProperty("title"));
				}
			};
		}
		else if (Objects.equals(
					type, SiteNavigationMenuItemTypeConstants.URL)) {

			return new URLNavigationMenuItemSettings() {
				{
					setUrl(() -> unicodeProperties.getProperty("url"));
					setUseNewTab(
						() -> Boolean.valueOf(
							unicodeProperties.getProperty("useNewTab")));
				}
			};
		}

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				type);

		Class<?> clazz = siteNavigationMenuItemType.getClass();

		if (Objects.equals(
				clazz.getName(),
				DisplayPageTypeSiteNavigationMenuItemTypeConstants.
					CLASS_NAME)) {

			return new DisplayPageNavigationMenuItemSettings() {
				{
					setClassName(
						() -> unicodeProperties.getProperty("className"));
					setExternalReferenceCode(
						() -> unicodeProperties.getProperty(
							"externalReferenceCode"));
					setScopeExternalReferenceCode(
						() -> unicodeProperties.getProperty(
							"scopeExternalReferenceCode"));
					setTitle(() -> unicodeProperties.getProperty("title"));
					setType(() -> unicodeProperties.getProperty("type"));
				}
			};
		}

		return null;
	}

	private UnicodeProperties _getUnicodeProperties(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		if (siteNavigationMenuItem == null) {
			return null;
		}

		return UnicodePropertiesBuilder.fastLoad(
			siteNavigationMenuItem.getTypeSettings()
		).build();
	}

	private boolean _isNameProperty(Map.Entry<String, String> entry) {
		String key = entry.getKey();

		return key.startsWith("name_");
	}

	private String _toType(String type) {
		if (type.equals("layout")) {
			return "page";
		}
		else if (type.equals("node")) {
			return "navigationMenu";
		}
		else if (type.equals(FileEntry.class.getName())) {
			return DLFileEntry.class.getName();
		}

		return type;
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

}