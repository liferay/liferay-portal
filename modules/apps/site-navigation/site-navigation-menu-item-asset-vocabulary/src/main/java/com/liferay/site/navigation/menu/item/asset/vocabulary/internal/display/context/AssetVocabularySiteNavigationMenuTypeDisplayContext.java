/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.item.asset.vocabulary.internal.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.vocabulary.item.selector.AssetVocabularyItemSelectorCriterion;
import com.liferay.asset.vocabulary.item.selector.AssetVocabularyItemSelectorReturnType;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class AssetVocabularySiteNavigationMenuTypeDisplayContext {

	public AssetVocabularySiteNavigationMenuTypeDisplayContext(
		HttpServletRequest httpServletRequest, ItemSelector itemSelector,
		SiteNavigationMenuItem siteNavigationMenuItem) {

		_httpServletRequest = httpServletRequest;
		_itemSelector = itemSelector;
		_siteNavigationMenuItem = siteNavigationMenuItem;

		_typeSettingsUnicodeProperties = UnicodePropertiesBuilder.fastLoad(
			siteNavigationMenuItem.getTypeSettings()
		).build();

		_assetVocabulary = AssetVocabularyLocalServiceUtil.fetchAssetVocabulary(
			GetterUtil.getLong(_typeSettingsUnicodeProperties.get("classPK")));

		_liferayPortletResponse = PortalUtil.getLiferayPortletResponse(
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE));
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getAssetVocabularyContextualSidebarContext()
		throws Exception {

		return HashMapBuilder.<String, Object>put(
			"assetVocabulary",
			() -> HashMapBuilder.<String, Object>put(
				"classPK",
				GetterUtil.getLong(
					_typeSettingsUnicodeProperties.get("classPK"))
			).put(
				"groupId",
				GetterUtil.getLong(
					_typeSettingsUnicodeProperties.get("groupId"))
			).put(
				"title",
				() -> {
					if (_assetVocabulary != null) {
						return _assetVocabulary.getTitle(
							_themeDisplay.getLocale());
					}

					return _typeSettingsUnicodeProperties.get("title");
				}
			).put(
				"type", "asset-vocabulary"
			).put(
				"uuid", _typeSettingsUnicodeProperties.get("uuid")
			).build()
		).put(
			"chooseAssetVocabularyProps",
			_getChooseAssetVocabularyButtonContext()
		).put(
			"defaultLanguageId",
			LocaleUtil.toLanguageId(LocaleUtil.getMostRelevantLocale())
		).put(
			"locales",
			JSONUtil.toJSONArray(
				LanguageUtil.getAvailableLocales(
					_themeDisplay.getSiteGroupId()),
				locale -> {
					String w3cLanguageId = LocaleUtil.toW3cLanguageId(locale);

					return JSONUtil.put(
						"id", LocaleUtil.toLanguageId(locale)
					).put(
						"label", w3cLanguageId
					).put(
						"symbol", StringUtil.toLowerCase(w3cLanguageId)
					);
				})
		).put(
			"localizedNames",
			() -> JSONFactoryUtil.createJSONObject(
				_typeSettingsUnicodeProperties.getProperty(
					"localizedNames", "{}"))
		).put(
			"namespace", _liferayPortletResponse.getNamespace()
		).put(
			"numberOfCategories",
			() -> {
				if (_assetVocabulary == null) {
					return 0;
				}

				return _assetVocabulary.getCategoriesCount();
			}
		).put(
			"showAssetVocabularyLevel",
			() -> GetterUtil.getBoolean(
				_typeSettingsUnicodeProperties.get("showAssetVocabularyLevel"))
		).put(
			"siteName",
			() -> {
				long groupId = GetterUtil.getLong(
					_typeSettingsUnicodeProperties.get("groupId"));

				if (groupId == _themeDisplay.getCompanyGroupId()) {
					return LanguageUtil.get(_httpServletRequest, "global");
				}

				Group group = GroupLocalServiceUtil.getGroup(groupId);

				return group.getDescriptiveName(_themeDisplay.getLocale());
			}
		).put(
			"useCustomName",
			() -> GetterUtil.getBoolean(
				_typeSettingsUnicodeProperties.get("useCustomName"))
		).build();
	}

	private Map<String, Object> _getChooseAssetVocabularyButtonContext() {
		return HashMapBuilder.<String, Object>put(
			"assetVocabularySelectorURL",
			() -> {
				AssetVocabularyItemSelectorCriterion
					assetVocabularyItemSelectorCriterion =
						new AssetVocabularyItemSelectorCriterion();

				assetVocabularyItemSelectorCriterion.
					setDesiredItemSelectorReturnTypes(
						new AssetVocabularyItemSelectorReturnType());
				assetVocabularyItemSelectorCriterion.
					setIncludeAncestorSiteAndDepotGroupIds(true);
				assetVocabularyItemSelectorCriterion.
					setIncludeInternalVocabularies(false);

				RequestBackedPortletURLFactory requestBackedPortletURLFactory =
					RequestBackedPortletURLFactoryUtil.create(
						_httpServletRequest);

				return PortletURLBuilder.create(
					_itemSelector.getItemSelectorURL(
						requestBackedPortletURLFactory,
						_liferayPortletResponse.getNamespace() +
							"selectAssetVocabulary",
						assetVocabularyItemSelectorCriterion)
				).buildString();
			}
		).put(
			"eventName",
			_liferayPortletResponse.getNamespace() + "selectAssetVocabulary"
		).put(
			"getAssetVocabularyDetailsURL",
			() -> {
				LiferayPortletURL itemDetailsURL =
					(LiferayPortletURL)ResourceURLBuilder.createResourceURL(
						_liferayPortletResponse
					).setResourceID(
						"/navigation_menu/get_asset_vocabulary_details"
					).buildResourceURL();

				itemDetailsURL.setCopyCurrentRenderParameters(false);

				return itemDetailsURL.toString();
			}
		).build();
	}

	private final AssetVocabulary _assetVocabulary;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final SiteNavigationMenuItem _siteNavigationMenuItem;
	private final ThemeDisplay _themeDisplay;
	private final UnicodeProperties _typeSettingsUnicodeProperties;

}