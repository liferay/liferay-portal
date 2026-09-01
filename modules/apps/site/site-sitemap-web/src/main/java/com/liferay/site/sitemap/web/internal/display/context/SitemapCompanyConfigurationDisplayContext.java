/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.sitemap.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.SelectOption;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.item.selector.ObjectDefinitionItemSelectorCriterion;
import com.liferay.object.item.selector.ObjectDefinitionItemSelectorReturnType;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.storage.helper.SitemapStorageHelper;

import java.text.Format;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lourdes Fernández Besada
 */
public class SitemapCompanyConfigurationDisplayContext {

	public SitemapCompanyConfigurationDisplayContext(
		GroupLocalService groupLocalService, ItemSelector itemSelector,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SitemapConfigurationManager sitemapConfigurationManager,
		SitemapManager sitemapManager,
		SitemapStorageHelper sitemapStorageHelper, ThemeDisplay themeDisplay) {

		_groupLocalService = groupLocalService;
		_itemSelector = itemSelector;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_sitemapConfigurationManager = sitemapConfigurationManager;
		_sitemapManager = sitemapManager;
		_sitemapStorageHelper = sitemapStorageHelper;
		_themeDisplay = themeDisplay;
	}

	public SearchContainer<Group> getGroupSearchContainer() throws Exception {
		if (_groupSearchContainer != null) {
			return _groupSearchContainer;
		}

		SearchContainer<Group> searchContainer = new SearchContainer<>(
			_liferayPortletRequest, _liferayPortletResponse.createRenderURL(),
			null, "no-sites-were-found");

		List<Group> groups = ListUtil.fromArray(_getGuestGroup());

		groups.addAll(
			ListUtil.sort(
				ListUtil.filter(
					TransformUtil.transformToList(
						_sitemapConfigurationManager.getCompanySitemapGroupIds(
							_themeDisplay.getCompanyId()),
						groupId -> _groupLocalService.fetchGroup(groupId)),
					group -> !group.isGuest()),
				new GroupNameComparator(true, _themeDisplay.getLocale())));

		searchContainer.setResultsAndTotal(() -> groups, groups.size());

		_groupSearchContainer = searchContainer;

		return _groupSearchContainer;
	}

	public String getGroupSelectorURL() throws Exception {
		if (_groupSelectorURL != null) {
			return _groupSelectorURL;
		}

		SiteItemSelectorCriterion siteItemSelectorCriterion =
			new SiteItemSelectorCriterion();

		siteItemSelectorCriterion.setAllowNavigation(false);
		siteItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new GroupItemSelectorReturnType());
		siteItemSelectorCriterion.setIncludeCompany(false);
		siteItemSelectorCriterion.setIncludeParentSites(true);
		siteItemSelectorCriterion.setIncludeRecentSites(false);

		_groupSelectorURL = String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					_liferayPortletRequest),
				getSelectGroupEventName(), siteItemSelectorCriterion));

		return _groupSelectorURL;
	}

	public String getLastRegenerateSitemapDateString() throws PortalException {
		Date lastRegenerateSitemapDate =
			_sitemapStorageHelper.getLastRegenerateSitemapDate(
				_themeDisplay.getCompanyId());

		if (lastRegenerateSitemapDate == null) {
			return StringPool.DASH;
		}

		return _getDateString(lastRegenerateSitemapDate);
	}

	public String getNextRegenerateSitemapDateString() throws PortalException {
		Date nextRegenerateSitemapDate =
			_sitemapManager.getNextRegenerateSitemapDate(
				_themeDisplay.getCompanyId());

		if (nextRegenerateSitemapDate == null) {
			return StringPool.DASH;
		}

		return _getDateString(nextRegenerateSitemapDate);
	}

	public SearchContainer<ObjectDefinition>
			getObjectDefinitionSearchContainer()
		throws Exception {

		if (_objectDefinitionSearchContainer != null) {
			return _objectDefinitionSearchContainer;
		}

		List<String> headerNames = new ArrayList<>();

		headerNames.add("object-label");
		headerNames.add(null);

		SearchContainer<ObjectDefinition> searchContainer =
			new SearchContainer<>(
				_liferayPortletRequest,
				_liferayPortletResponse.createRenderURL(), headerNames,
				"no-objects-or-cms-structures-were-found");

		List<ObjectDefinition> objectDefinitions =
			_sitemapConfigurationManager.getCompanySitemapObjectDefinitions(
				_themeDisplay.getCompanyId());

		searchContainer.setResultsAndTotal(
			() -> objectDefinitions, objectDefinitions.size());

		_objectDefinitionSearchContainer = searchContainer;

		return _objectDefinitionSearchContainer;
	}

	public String getObjectDefinitionSelectorURL() throws Exception {
		if (_objectDefinitionSelectorURL != null) {
			return _objectDefinitionSelectorURL;
		}

		ObjectDefinitionItemSelectorCriterion
			objectDefinitionItemSelectorCriterion =
				new ObjectDefinitionItemSelectorCriterion();

		objectDefinitionItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new ObjectDefinitionItemSelectorReturnType());
		objectDefinitionItemSelectorCriterion.setObjectDefinitionSettingName(
			ObjectDefinitionSettingConstants.NAME_SITEMAPABLE);

		_objectDefinitionSelectorURL = String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					_liferayPortletRequest),
				getSelectObjectDefinitionEventName(),
				objectDefinitionItemSelectorCriterion));

		return _objectDefinitionSelectorURL;
	}

	public String getRegenerateSitemapInProgressURL() {
		return ResourceURLBuilder.createResourceURL(
			_liferayPortletResponse
		).setResourceID(
			"/site_sitemap/get_regenerate_sitemap_in_progress"
		).buildString();
	}

	public String getSelectGroupEventName() {
		if (_selectGroupEventName != null) {
			return _selectGroupEventName;
		}

		_selectGroupEventName =
			_liferayPortletResponse.getNamespace() + "selectGroup";

		return _selectGroupEventName;
	}

	public String getSelectObjectDefinitionEventName() {
		if (_selectObjectDefinitionEventName != null) {
			return _selectObjectDefinitionEventName;
		}

		_selectObjectDefinitionEventName =
			_liferayPortletResponse.getNamespace() + "selectObjectDefinition";

		return _selectObjectDefinitionEventName;
	}

	public List<SelectOption> getSitemapIndexModeSelectOptions()
		throws ConfigurationException {

		List<SelectOption> selectOptions = new ArrayList<>();

		String[] sitemapIndexModes = {
			SitemapConstants.INDEX_MODE_ASSET_TYPE,
			SitemapConstants.INDEX_MODE_PAGE_LAYOUT
		};

		for (String sitemapIndexMode : sitemapIndexModes) {
			String indexModeName = LanguageUtil.get(
				_themeDisplay.getLocale(), sitemapIndexMode);

			selectOptions.add(
				new SelectOption(
					LanguageUtil.format(
						_themeDisplay.getLocale(), "group-by-x", indexModeName),
					sitemapIndexMode,
					StringUtil.equals(
						sitemapIndexMode, getXMLSitemapIndexMode())));
		}

		return selectOptions;
	}

	public String getXMLSitemapIndexMode() throws ConfigurationException {
		return _sitemapConfigurationManager.getXMLSitemapIndexMode(
			_themeDisplay.getCompanyId());
	}

	public boolean hasVirtualHost(Group group) {
		LayoutSet layoutSet = group.getPublicLayoutSet();

		if ((layoutSet != null) &&
			MapUtil.isNotEmpty(layoutSet.getVirtualHostnames())) {

			return true;
		}

		return false;
	}

	public boolean includeCategories() throws ConfigurationException {
		return _sitemapConfigurationManager.includeCategoriesCompanyEnabled(
			_themeDisplay.getCompanyId());
	}

	public boolean includePages() throws ConfigurationException {
		return _sitemapConfigurationManager.includePagesCompanyEnabled(
			_themeDisplay.getCompanyId());
	}

	public boolean includeWebContent() throws ConfigurationException {
		return _sitemapConfigurationManager.includeWebContentCompanyEnabled(
			_themeDisplay.getCompanyId());
	}

	public boolean isCachedGenerationEnabled() throws ConfigurationException {
		return _sitemapConfigurationManager.isCachedGenerationCompanyEnabled(
			_themeDisplay.getCompanyId());
	}

	public boolean isIndexModeAssetTypeEnabled() throws ConfigurationException {
		return _sitemapConfigurationManager.isIndexModeAssetTypeCompanyEnabled(
			_themeDisplay.getCompanyId());
	}

	public boolean isRegenerateSitemapInProgress() {
		return _sitemapManager.isRegenerateSitemapInProgress(
			_themeDisplay.getCompanyId());
	}

	public boolean isXMLSitemapIndexEnabled() throws ConfigurationException {
		return _sitemapConfigurationManager.isXMLSitemapIndexCompanyEnabled(
			_themeDisplay.getCompanyId());
	}

	private String _getDateString(Date date) {
		Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"MMM d, yyyy HH:mm:ss", _themeDisplay.getLocale(),
			_themeDisplay.getTimeZone());

		return format.format(date);
	}

	private Group _getGuestGroup() throws Exception {
		if (_guestGroup != null) {
			return _guestGroup;
		}

		_guestGroup = _groupLocalService.getGroup(
			_themeDisplay.getCompanyId(), GroupConstants.GUEST);

		return _guestGroup;
	}

	private final GroupLocalService _groupLocalService;
	private SearchContainer<Group> _groupSearchContainer;
	private String _groupSelectorURL;
	private Group _guestGroup;
	private final ItemSelector _itemSelector;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private SearchContainer<ObjectDefinition> _objectDefinitionSearchContainer;
	private String _objectDefinitionSelectorURL;
	private String _selectGroupEventName;
	private String _selectObjectDefinitionEventName;
	private final SitemapConfigurationManager _sitemapConfigurationManager;
	private final SitemapManager _sitemapManager;
	private final SitemapStorageHelper _sitemapStorageHelper;
	private final ThemeDisplay _themeDisplay;

}