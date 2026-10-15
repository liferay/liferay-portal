/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.item.selector.taglib.internal.servlet.item.selector.ItemSelectorUtil;
import com.liferay.item.selector.taglib.internal.util.EntryURLUtil;
import com.liferay.item.selector.taglib.internal.util.GroupItemSelectorProviderRegistryUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletURL;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Cristina González
 */
public class GroupSelectorDisplayContext {

	public GroupSelectorDisplayContext(
		LiferayPortletRequest liferayPortletRequest) {

		_liferayPortletRequest = liferayPortletRequest;
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries() {
		String groupItemSelectorLabel = getGroupItemSelectorLabel(
			getGroupType());

		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(
						_liferayPortletRequest.getLocale(),
						"sites-and-libraries"));
				breadcrumbEntry.setURL(
					String.valueOf(getGroupItemSelectorURL(StringPool.BLANK)));
			}
		).add(
			() -> Validator.isNotNull(groupItemSelectorLabel),
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(groupItemSelectorLabel);
				breadcrumbEntry.setURL(
					String.valueOf(getGroupItemSelectorURL(getGroupType())));
			}
		).build();
	}

	public String getGroupItemSelectorIcon(String groupType) {
		GroupItemSelectorProvider groupItemSelectorProvider =
			GroupItemSelectorProviderRegistryUtil.getGroupItemSelectorProvider(
				groupType);

		if (groupItemSelectorProvider == null) {
			return "folder";
		}

		String icon = groupItemSelectorProvider.getIcon();

		if (icon == null) {
			return "folder";
		}

		return icon;
	}

	public String getGroupItemSelectorLabel(String groupType) {
		GroupItemSelectorProvider groupItemSelectorProvider =
			GroupItemSelectorProviderRegistryUtil.getGroupItemSelectorProvider(
				groupType);

		if (groupItemSelectorProvider == null) {
			return StringPool.BLANK;
		}

		String label = groupItemSelectorProvider.getLabel(
			_liferayPortletRequest.getLocale());

		if (label == null) {
			return StringPool.BLANK;
		}

		return label;
	}

	public PortletURL getGroupItemSelectorURL(String groupType) {
		return PortletURLBuilder.create(
			_getItemSelectorURL()
		).setParameter(
			"groupType", groupType
		).setParameter(
			"scopeGroupType", _isScopeGroupType()
		).setParameter(
			"selectedTab", _getSelectedTab()
		).setParameter(
			"showGroupSelector", true
		).buildPortletURL();
	}

	public String getGroupType() {
		if (_groupType != null) {
			return _groupType;
		}

		_groupType = ParamUtil.getString(_liferayPortletRequest, "groupType");

		return _groupType;
	}

	public Set<String> getGroupTypes() {
		if (_groupTypes != null) {
			return _groupTypes;
		}

		_groupTypes =
			GroupItemSelectorProviderRegistryUtil.
				getGroupItemSelectorProviderTypes();

		for (String criterion :
				ParamUtil.getStringValues(_liferayPortletRequest, "criteria")) {

			List<String> parts = StringUtil.split(criterion);

			if (parts.contains("file") || parts.contains("folder") ||
				parts.contains("image") ||
				(parts.contains("infoitem") &&
				 _isLegacyAssetItemSelectorCriterion())) {

				_groupTypes.remove("space-depot");

				break;
			}
		}

		return _groupTypes;
	}

	public SearchContainer<String> getGroupTypesSearchContainer() {
		SearchContainer<String> searchContainer = new SearchContainer<>(
			_liferayPortletRequest, getGroupItemSelectorURL(StringPool.BLANK),
			null, null);

		searchContainer.setResultsAndTotal(
			ListUtil.fromCollection(getGroupTypes()));

		return searchContainer;
	}

	public SearchContainer<Group> getSearchContainer() {
		SearchContainer<Group> searchContainer = new GroupSearch(
			_liferayPortletRequest, getGroupItemSelectorURL(getGroupType()));

		searchContainer.setEmptyResultsMessage(_getEmptyResultsMessage());
		searchContainer.setResultsAndTotal(
			() -> (List<Group>)_liferayPortletRequest.getAttribute(
				"liferay-item-selector:group-selector:groups"),
			GetterUtil.getInteger(
				_liferayPortletRequest.getAttribute(
					"liferay-item-selector:group-selector:groupsCount")));

		return searchContainer;
	}

	public String getViewGroupURL(Group group) {
		PortletURL portletURL = EntryURLUtil.getGroupPortletURL(
			group, _liferayPortletRequest);

		return PortletURLBuilder.create(
			portletURL
		).setParameter(
			"groupType", getGroupType()
		).setParameter(
			"scopeGroupType", _isScopeGroupType()
		).buildString();
	}

	public boolean isGroupTypeActive(String groupType) {
		return groupType.equals(getGroupType());
	}

	public boolean isGroupTypeNavigationEnabled() {
		if (_groupTypeNavigationEnabled != null) {
			return _groupTypeNavigationEnabled;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_groupTypeNavigationEnabled = FeatureFlagManagerUtil.isEnabled(
			themeDisplay.getCompanyId(), "LPD-81022");

		return _groupTypeNavigationEnabled;
	}

	public boolean isShowGroupTypes() {
		if (isGroupTypeNavigationEnabled() &&
			Validator.isNull(getGroupType())) {

			return true;
		}

		return false;
	}

	protected GroupSelectorDisplayContext(
		String groupType, LiferayPortletRequest liferayPortletRequest) {

		_groupType = groupType;
		_liferayPortletRequest = liferayPortletRequest;
	}

	private String _getEmptyResultsMessage() {
		GroupItemSelectorProvider groupItemSelectorProvider =
			GroupItemSelectorProviderRegistryUtil.getGroupItemSelectorProvider(
				getGroupType());

		if (groupItemSelectorProvider == null) {
			return GroupSearch.EMPTY_RESULTS_MESSAGE;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String emptyResultsMessage =
			groupItemSelectorProvider.getEmptyResultsMessage(
				themeDisplay.getLocale());

		if (emptyResultsMessage == null) {
			return GroupSearch.EMPTY_RESULTS_MESSAGE;
		}

		return emptyResultsMessage;
	}

	private ItemSelector _getItemSelector() {
		return ItemSelectorUtil.getItemSelector();
	}

	private List<ItemSelectorCriterion> _getItemSelectorCriteria() {
		if (_itemSelectorCriteria != null) {
			return _itemSelectorCriteria;
		}

		ItemSelector itemSelector = _getItemSelector();

		_itemSelectorCriteria = itemSelector.getItemSelectorCriteria(
			_liferayPortletRequest.getParameterMap());

		return _itemSelectorCriteria;
	}

	private PortletURL _getItemSelectorURL() {
		ItemSelector itemSelector = _getItemSelector();
		List<ItemSelectorCriterion> itemSelectorCriteria =
			_getItemSelectorCriteria();

		return itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(_liferayPortletRequest),
			ParamUtil.getString(
				_liferayPortletRequest, "itemSelectedEventName"),
			itemSelectorCriteria.toArray(new ItemSelectorCriterion[0]));
	}

	private String _getSelectedTab() {
		if (_selectedTab != null) {
			return _selectedTab;
		}

		_selectedTab = ParamUtil.getString(
			_liferayPortletRequest, "selectedTab");

		if (Validator.isNull(_selectedTab)) {
			_selectedTab = GetterUtil.getString(
				_liferayPortletRequest.getAttribute(
					"liferay-item-selector:group-selector:selectedTab"));
		}

		return _selectedTab;
	}

	private boolean _isLegacyAssetItemSelectorCriterion() {
		for (ItemSelectorCriterion itemSelectorCriterion :
				_getItemSelectorCriteria()) {

			if (!(itemSelectorCriterion instanceof
					InfoItemItemSelectorCriterion)) {

				continue;
			}

			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion =
				(InfoItemItemSelectorCriterion)itemSelectorCriterion;

			if (Objects.equals(
					infoItemItemSelectorCriterion.getItemType(),
					JournalArticle.class.getName())) {

				return true;
			}
		}

		String selectedTab = _getSelectedTab();

		if (Validator.isNotNull(selectedTab) &&
			!selectedTab.startsWith(
				_CLASS_NAME_OBJECT_ENTRY_ITEM_SELECTOR_VIEW +
					StringPool.UNDERLINE)) {

			return true;
		}

		return false;
	}

	private boolean _isScopeGroupType() {
		if (_scopeGroupType != null) {
			return _scopeGroupType;
		}

		_scopeGroupType = ParamUtil.getBoolean(
			_liferayPortletRequest, "scopeGroupType");

		return _scopeGroupType;
	}

	private static final String _CLASS_NAME_OBJECT_ENTRY_ITEM_SELECTOR_VIEW =
		"com.liferay.object.web.internal.item.selector." +
			"ObjectEntryItemSelectorView";

	private String _groupType;
	private Boolean _groupTypeNavigationEnabled;
	private Set<String> _groupTypes;
	private List<ItemSelectorCriterion> _itemSelectorCriteria;
	private final LiferayPortletRequest _liferayPortletRequest;
	private Boolean _scopeGroupType;
	private String _selectedTab;

}