/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.item.selector.web.internal.display.context;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.item.selector.FragmentEntryItemSelectorCriterion;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.fragment.util.comparator.FragmentEntryCreateDateComparator;
import com.liferay.fragment.util.comparator.FragmentEntryNameComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CollatorUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.text.Collator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Víctor Galán
 */
public class FragmentEntriesDisplayContext {

	public FragmentEntriesDisplayContext(
		FragmentEntryItemSelectorCriterion fragmentEntryItemSelectorCriterion,
		long[] groupIds, HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest, PortletURL portletURL) {

		_fragmentEntryItemSelectorCriterion =
			fragmentEntryItemSelectorCriterion;
		_groupIds = groupIds;
		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_portletURL = portletURL;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries() {
		return BreadcrumbEntryListBuilder.add(
			() -> _groupIds.length > 1,
			breadcrumbEntry -> {
				if (getGroupId() > 0) {
					breadcrumbEntry.setBrowsable(false);
				}

				breadcrumbEntry.setTitle(
					LanguageUtil.get(_httpServletRequest, "design-libraries"));
				breadcrumbEntry.setURL(
					PortletURLBuilder.create(
						_portletURL
					).setParameter(
						"fragmentCollectionId", StringPool.BLANK
					).setParameter(
						"selectedGroupId", StringPool.BLANK
					).buildString());
			}
		).add(
			() -> getGroupId() > 0,
			breadcrumbEntry -> {
				if (getFragmentCollectionId() > 0) {
					breadcrumbEntry.setBrowsable(false);
				}

				breadcrumbEntry.setTitle(getDescriptiveName(_getGroup()));
				breadcrumbEntry.setURL(
					PortletURLBuilder.create(
						_portletURL
					).setParameter(
						"fragmentCollectionId", StringPool.BLANK
					).setParameter(
						"selectedGroupId", getGroupId()
					).buildString());
			}
		).add(
			() -> getFragmentCollectionId() > 0,
			breadcrumbEntry -> {
				FragmentCollection fragmentCollection =
					FragmentCollectionLocalServiceUtil.getFragmentCollection(
						getFragmentCollectionId());

				breadcrumbEntry.setTitle(fragmentCollection.getName());
			}
		).build();
	}

	public String getDescriptiveName(Group group) {
		try {
			return group.getDescriptiveName(_themeDisplay.getLocale());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return group.getName(_themeDisplay.getLocale());
		}
	}

	public long getFragmentCollectionId() {
		if (_fragmentCollectionId != null) {
			return _fragmentCollectionId;
		}

		_fragmentCollectionId = ParamUtil.getLong(
			_httpServletRequest, "fragmentCollectionId");

		return _fragmentCollectionId;
	}

	public SearchContainer<FragmentCollection>
		getFragmentCollectionsSearchContainer() {

		if (_fragmentCollectionSearchContainer != null) {
			return _fragmentCollectionSearchContainer;
		}

		SearchContainer<FragmentCollection> searchContainer =
			new SearchContainer<>(
				_liferayPortletRequest, _portletURL, null,
				"no-fragment-collection-was-found");

		searchContainer.setId("fragmentCollections");

		searchContainer.setResultsAndTotal(
			_getFragmentCollections(getGroupId()));

		_fragmentCollectionSearchContainer = searchContainer;

		return _fragmentCollectionSearchContainer;
	}

	public SearchContainer<FragmentEntry> getFragmentsSearchContainer() {
		if (_fragmentsSearchContainer != null) {
			return _fragmentsSearchContainer;
		}

		SearchContainer<FragmentEntry> searchContainer = new SearchContainer<>(
			_liferayPortletRequest, _portletURL, null, "no-fragment-was-found");

		searchContainer.setId("fragments");
		searchContainer.setOrderByCol(_getOrderByCol());
		searchContainer.setOrderByComparator(
			_getFragmentCollectionOrderByComparator());
		searchContainer.setOrderByType(_getOrderByType());

		List<FragmentEntry> fragmentEntries = null;

		if (isSearch()) {
			fragmentEntries = FragmentEntryLocalServiceUtil.getFragmentEntries(
				getGroupId(), getFragmentCollectionId(), _getKeywords(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, searchContainer.getOrderByComparator());
		}
		else {
			fragmentEntries = FragmentEntryLocalServiceUtil.getFragmentEntries(
				getGroupId(), getFragmentCollectionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, searchContainer.getOrderByComparator());
		}

		searchContainer.setResultsAndTotal(
			ListUtil.filter(
				fragmentEntries,
				fragmentEntry ->
					SetUtil.isEmpty(
						_fragmentEntryItemSelectorCriterion.getInputTypes()) ||
					_filterInputTypes(
						fragmentEntry,
						_fragmentEntryItemSelectorCriterion.getInputTypes())));

		_fragmentsSearchContainer = searchContainer;

		return _fragmentsSearchContainer;
	}

	public long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		if (_groupIds.length == 1) {
			_groupId = _groupIds[0];
		}
		else {
			_groupId = ParamUtil.getLong(
				_httpServletRequest, "selectedGroupId");

			if (!ArrayUtil.contains(_groupIds, _groupId)) {
				_groupId = 0L;
			}
		}

		return _groupId;
	}

	public String getGroupKey() {
		Group group = _getGroup();

		return group.getGroupKey();
	}

	public SearchContainer<Group> getGroupsSearchContainer() {
		if (_groupsSearchContainer != null) {
			return _groupsSearchContainer;
		}

		SearchContainer<Group> searchContainer = new SearchContainer<>(
			_liferayPortletRequest, _portletURL, null,
			"no-design-libraries-were-found");

		searchContainer.setId("groups");

		List<Group> groups = TransformUtil.transformToList(
			_groupIds,
			groupId -> {
				if (ListUtil.isEmpty(_getFragmentCollections(groupId))) {
					return null;
				}

				return GroupLocalServiceUtil.fetchGroup(groupId);
			});

		Collator collator = CollatorUtil.getInstance(_themeDisplay.getLocale());

		searchContainer.setResultsAndTotal(
			ListUtil.sort(
				groups,
				(group1, group2) -> collator.compare(
					getDescriptiveName(group1), getDescriptiveName(group2))));

		_groupsSearchContainer = searchContainer;

		return _groupsSearchContainer;
	}

	public boolean isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	private boolean _filterInputTypes(
		FragmentEntry fragmentEntry, Set<String> inputTypes) {

		if (!Objects.equals(
				fragmentEntry.getType(), FragmentConstants.TYPE_INPUT)) {

			return false;
		}

		try {
			JSONObject typeOptionsJSONObject = JSONFactoryUtil.createJSONObject(
				fragmentEntry.getTypeOptions());

			Set<String> fieldTypes = JSONUtil.toStringSet(
				typeOptionsJSONObject.getJSONArray("fieldTypes"));

			if (SetUtil.isEmpty(
					SetUtil.intersect(fieldTypes, new HashSet<>(inputTypes)))) {

				return false;
			}

			return true;
		}
		catch (JSONException jsonException) {
			_log.error(jsonException);

			return false;
		}
	}

	private OrderByComparator<FragmentEntry>
		_getFragmentCollectionOrderByComparator() {

		boolean orderByAsc = false;

		if (Objects.equals(_getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		OrderByComparator<FragmentEntry> orderByComparator = null;

		if (Objects.equals(_getOrderByCol(), "create-date")) {
			orderByComparator = FragmentEntryCreateDateComparator.getInstance(
				orderByAsc);
		}
		else if (Objects.equals(_getOrderByCol(), "name")) {
			orderByComparator = FragmentEntryNameComparator.getInstance(
				orderByAsc);
		}

		return orderByComparator;
	}

	private List<FragmentCollection> _getFragmentCollections(long groupId) {
		return ListUtil.filter(
			FragmentCollectionLocalServiceUtil.getFragmentCollections(
				groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			fragmentCollection -> {
				if (ListUtil.exists(
						FragmentEntryLocalServiceUtil.getFragmentEntries(
							groupId,
							fragmentCollection.getFragmentCollectionId(),
							WorkflowConstants.STATUS_APPROVED),
						fragmentEntry ->
							SetUtil.isEmpty(
								_fragmentEntryItemSelectorCriterion.
									getInputTypes()) ||
							_filterInputTypes(
								fragmentEntry,
								_fragmentEntryItemSelectorCriterion.
									getInputTypes()))) {

					return true;
				}

				return false;
			});
	}

	private Group _getGroup() {
		return GroupLocalServiceUtil.fetchGroup(getGroupId());
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, FragmentPortletKeys.FRAGMENT,
			"fragment-order-by-col", "modified-date");

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, FragmentPortletKeys.FRAGMENT,
			"fragment-order-by-type", "asc");

		return _orderByType;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntriesDisplayContext.class.getName());

	private Long _fragmentCollectionId;
	private SearchContainer<FragmentCollection>
		_fragmentCollectionSearchContainer;
	private final FragmentEntryItemSelectorCriterion
		_fragmentEntryItemSelectorCriterion;
	private SearchContainer<FragmentEntry> _fragmentsSearchContainer;
	private Long _groupId;
	private final long[] _groupIds;
	private SearchContainer<Group> _groupsSearchContainer;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private String _orderByCol;
	private String _orderByType;
	private final PortletURL _portletURL;
	private final ThemeDisplay _themeDisplay;

}