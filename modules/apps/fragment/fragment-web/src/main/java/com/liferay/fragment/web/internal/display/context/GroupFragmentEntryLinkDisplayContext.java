/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLinkTable;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jürgen Kappler
 */
public class GroupFragmentEntryLinkDisplayContext {

	public GroupFragmentEntryLinkDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public long getFragmentCollectionId() {
		if (Validator.isNotNull(_fragmentCollectionId)) {
			return _fragmentCollectionId;
		}

		_fragmentCollectionId = ParamUtil.getLong(
			_renderRequest, "fragmentCollectionId");

		return _fragmentCollectionId;
	}

	public FragmentEntry getFragmentEntry() {
		if (_fragmentEntry != null) {
			return _fragmentEntry;
		}

		try {
			_fragmentEntry = FragmentEntryLocalServiceUtil.getFragmentEntry(
				getFragmentEntryId());
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}

		return _fragmentEntry;
	}

	public long getFragmentEntryId() {
		if (Validator.isNotNull(_fragmentEntryId)) {
			return _fragmentEntryId;
		}

		_fragmentEntryId = ParamUtil.getLong(_renderRequest, "fragmentEntryId");

		return _fragmentEntryId;
	}

	public long getFragmentGroupUsageCount(Group group) throws PortalException {
		Map<Group, Integer> groupFragmentEntryUsages =
			_getGroupFragmentEntryUsages();

		return groupFragmentEntryUsages.get(group);
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_renderRequest, FragmentPortletKeys.FRAGMENT,
			"group-fragment-entry-link-order-by-col", "name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_renderRequest, FragmentPortletKeys.FRAGMENT,
			"group-fragment-entry-link-order-by-type", "asc");

		return _orderByType;
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_renderRequest, "redirect");

		return _redirect;
	}

	public SearchContainer<Group> getSearchContainer() throws PortalException {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		SearchContainer<Group> groupsSearchContainer = new SearchContainer(
			_renderRequest, _renderResponse.createRenderURL(), null,
			"there-are-no-fragment-usages");

		groupsSearchContainer.setId("groups" + getFragmentCollectionId());

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		groupsSearchContainer.setOrderByCol(getOrderByCol());
		groupsSearchContainer.setOrderByComparator(
			new GroupNameComparator(orderByAsc));
		groupsSearchContainer.setOrderByType(getOrderByType());

		Map<Group, Integer> groupFragmentEntryUsages =
			_getGroupFragmentEntryUsages();

		List<Group> groups = new ArrayList<>(groupFragmentEntryUsages.keySet());

		Collections.sort(groups, groupsSearchContainer.getOrderByComparator());

		groupsSearchContainer.setResultsAndTotal(
			() -> groups, groupFragmentEntryUsages.size());

		if (FragmentPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES)) {

			groupsSearchContainer.setRowChecker(
				new EmptyOnClickRowChecker(_renderResponse));
		}

		_searchContainer = groupsSearchContainer;

		return _searchContainer;
	}

	private Predicate _getFragmentEntryScopePredicate(Group group) {
		return Predicate.withParentheses(
			FragmentEntryLinkTable.INSTANCE.fragmentEntryScopeERC.eq(
				group.getExternalReferenceCode()
			).or(
				Predicate.withParentheses(
					FragmentEntryLinkTable.INSTANCE.fragmentEntryScopeERC.
						isNull(
						).and(
							FragmentEntryLinkTable.INSTANCE.groupId.eq(
								group.getGroupId())
						))
			));
	}

	private Map<Group, Integer> _getGroupFragmentEntryUsages()
		throws PortalException {

		if (_groupFragmentEntryUsages != null) {
			return _groupFragmentEntryUsages;
		}

		FragmentEntry fragmentEntry = getFragmentEntry();

		Group group = GroupLocalServiceUtil.getGroup(
			fragmentEntry.getGroupId());

		Map<Group, Integer> groupFragmentEntryUsages = new HashMap<>();

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			FragmentEntryLinkTable.INSTANCE.groupId,
			DSLFunctionFactoryUtil.countDistinct(
				FragmentEntryLinkTable.INSTANCE.classPK
			).as(
				"allUsageCount"
			)
		).from(
			FragmentEntryLinkTable.INSTANCE
		).where(
			FragmentEntryLinkTable.INSTANCE.fragmentEntryERC.eq(
				fragmentEntry.getExternalReferenceCode()
			).and(
				_getFragmentEntryScopePredicate(group)
			).and(
				FragmentEntryLinkTable.INSTANCE.deleted.eq(false)
			)
		).groupBy(
			FragmentEntryLinkTable.INSTANCE.groupId
		);

		List<Object[]> results = FragmentEntryLinkLocalServiceUtil.dslQuery(
			dslQuery);

		for (Object[] result : results) {
			long groupId = (Long)result[0];
			Number count = (Number)result[1];

			groupFragmentEntryUsages.put(
				GroupLocalServiceUtil.getGroup(groupId), count.intValue());
		}

		_groupFragmentEntryUsages = groupFragmentEntryUsages;

		return _groupFragmentEntryUsages;
	}

	private Long _fragmentCollectionId;
	private FragmentEntry _fragmentEntry;
	private Long _fragmentEntryId;
	private Map<Group, Integer> _groupFragmentEntryUsages;
	private String _orderByCol;
	private String _orderByType;
	private String _redirect;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<Group> _searchContainer;

}