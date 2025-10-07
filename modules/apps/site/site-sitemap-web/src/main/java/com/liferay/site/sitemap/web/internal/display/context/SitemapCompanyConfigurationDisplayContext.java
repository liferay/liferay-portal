/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.sitemap.web.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.object.item.selector.ObjectDefinitionItemSelectorCriterion;
import com.liferay.object.item.selector.ObjectDefinitionItemSelectorReturnType;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lourdes Fernández Besada
 */
public class SitemapCompanyConfigurationDisplayContext {

	public SitemapCompanyConfigurationDisplayContext(
		GroupLocalService groupLocalService, ItemSelector itemSelector,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		SitemapConfigurationManager sitemapConfigurationManager,
		ThemeDisplay themeDisplay) {

		_groupLocalService = groupLocalService;
		_itemSelector = itemSelector;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_sitemapConfigurationManager = sitemapConfigurationManager;
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
					group -> (group != null) && !group.isGuest()),
				new GroupNameComparator(true, _themeDisplay.getLocale())));

		searchContainer.setResultsAndTotal(groups);

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

		Group guestGroup = _getGuestGroup();

		siteItemSelectorCriterion.setExcludedGroupIds(
			new long[] {guestGroup.getGroupId()});

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

		searchContainer.setResultsAndTotal(
			ListUtil.filter(
				TransformUtil.transformToList(
					_sitemapConfigurationManager.
						getCompanySitemapObjectDefinitionIds(
							_themeDisplay.getCompanyId()),
					objectDefinitionId ->
						_objectDefinitionLocalService.fetchObjectDefinition(
							objectDefinitionId)),
				objectDefinition -> objectDefinition != null));

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

		_objectDefinitionSelectorURL = String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					_liferayPortletRequest),
				getSelectObjectDefinitionEventName(),
				objectDefinitionItemSelectorCriterion));

		return _objectDefinitionSelectorURL;
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

	public boolean xmlSitemapIndexEnabled() throws ConfigurationException {
		return _sitemapConfigurationManager.xmlSitemapIndexCompanyEnabled(
			_themeDisplay.getCompanyId());
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
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private SearchContainer<ObjectDefinition> _objectDefinitionSearchContainer;
	private String _objectDefinitionSelectorURL;
	private String _selectGroupEventName;
	private String _selectObjectDefinitionEventName;
	private final SitemapConfigurationManager _sitemapConfigurationManager;
	private final ThemeDisplay _themeDisplay;

}