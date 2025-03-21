/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.dao.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryLocalServiceUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class SegmentsEntrySearchContainerFactory {

	public static SearchContainer<SegmentsEntry> create(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {

		SearchContainer<SegmentsEntry> searchContainer = new SearchContainer(
			renderRequest,
			PortletURLUtil.getCurrent(renderRequest, renderResponse), null,
			"no-segments-were-found");

		searchContainer.setId("segmentsEntries");
		searchContainer.setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				renderRequest, RolesAdminPortletKeys.ROLES_ADMIN, "name"));
		searchContainer.setOrderByType(
			SearchOrderByUtil.getOrderByType(
				renderRequest, RolesAdminPortletKeys.ROLES_ADMIN, "asc"));

		String tabs3 = ParamUtil.getString(renderRequest, "tabs3", "current");

		long roleId = ParamUtil.getLong(renderRequest, "roleId");

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		RowChecker rowChecker = null;

		if (tabs3.equals("current")) {
			params.put("roleIds", new long[] {roleId});

			rowChecker = new EmptyOnClickRowChecker(renderResponse);
		}
		else {
			rowChecker = new SegmentsEntryRoleChecker(renderResponse, roleId);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		searchContainer.setResultsAndTotal(
			SegmentsEntryLocalServiceUtil.searchSegmentsEntries(
				_buildSearchContext(
					themeDisplay.getCompanyId(),
					themeDisplay.getCompanyGroupId(),
					ParamUtil.getString(renderRequest, "keywords"), params,
					searchContainer.getStart(), searchContainer.getEnd(),
					_getSort(
						searchContainer.getOrderByCol(),
						searchContainer.getOrderByType(), themeDisplay))));

		searchContainer.setRowChecker(rowChecker);

		return searchContainer;
	}

	private static SearchContext _buildSearchContext(
		long companyId, long groupId, String keywords,
		LinkedHashMap<String, Object> params, int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		Map<String, Serializable> attributes =
			HashMapBuilder.<String, Serializable>put(
				Field.NAME, keywords
			).build();

		params.put("keywords", keywords);

		attributes.put("params", params);

		searchContext.setAttributes(attributes);

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {groupId});

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		return searchContext;
	}

	private static Sort _getSort(
		String orderByCol, String orderByType, ThemeDisplay themeDisplay) {

		if (Objects.equals(orderByCol, "name")) {
			return new Sort(
				Field.getSortableFieldName(
					"localized_name_".concat(themeDisplay.getLanguageId())),
				Sort.STRING_TYPE, !Objects.equals(orderByType, "asc"));
		}

		return new Sort(
			Field.MODIFIED_DATE, Sort.LONG_TYPE,
			!Objects.equals(orderByType, "asc"));
	}

}