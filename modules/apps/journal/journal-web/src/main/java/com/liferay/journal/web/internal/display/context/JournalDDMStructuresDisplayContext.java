/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureServiceUtil;
import com.liferay.dynamic.data.mapping.util.comparator.StructureIdComparator;
import com.liferay.dynamic.data.mapping.util.comparator.StructureModifiedDateComparator;
import com.liferay.dynamic.data.mapping.util.comparator.StructureNameComparator;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class JournalDDMStructuresDisplayContext {

	public JournalDDMStructuresDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_journalWebConfiguration =
			(JournalWebConfiguration)_httpServletRequest.getAttribute(
				JournalWebConfiguration.class.getName());
		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SearchContainer<DDMStructure> getDDMStructureSearchContainer()
		throws Exception {

		if (_ddmStructureSearchContainer != null) {
			return _ddmStructureSearchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String emptyResultsMessage = "there-are-no-structures";

		if (Validator.isNotNull(_getKeywords())) {
			emptyResultsMessage = "no-structures-were-found";
		}

		SearchContainer<DDMStructure> ddmStructureSearchContainer =
			new SearchContainer(
				_renderRequest, _getPortletURL(), null, emptyResultsMessage);

		ddmStructureSearchContainer.setOrderByCol(getOrderByCol());
		ddmStructureSearchContainer.setOrderByComparator(
			_getOrderByComparator());
		ddmStructureSearchContainer.setOrderByType(getOrderByType());

		long[] groupIds = {_themeDisplay.getScopeGroupId()};

		if (_journalWebConfiguration.showAncestorScopesByDefault()) {
			groupIds =
				SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						_themeDisplay.getScopeGroupId(), false, true);
		}

		long[] structureGroupIds = groupIds;

		if (Validator.isNotNull(_getKeywords())) {
			ddmStructureSearchContainer.setResultsAndTotal(
				() -> {
					List<DDMStructure> results = DDMStructureServiceUtil.search(
						themeDisplay.getCompanyId(), structureGroupIds,
						PortalUtil.getClassNameId(
							JournalArticle.class.getName()),
						_getKeywords(), WorkflowConstants.STATUS_ANY,
						ddmStructureSearchContainer.getStart(),
						ddmStructureSearchContainer.getEnd(),
						ddmStructureSearchContainer.getOrderByComparator());

					List<DDMStructure> sortedResults = new ArrayList<>(results);

					Collections.sort(
						sortedResults,
						ddmStructureSearchContainer.getOrderByComparator());

					return sortedResults;
				},
				DDMStructureServiceUtil.searchCount(
					themeDisplay.getCompanyId(), structureGroupIds,
					PortalUtil.getClassNameId(JournalArticle.class.getName()),
					_getKeywords(), WorkflowConstants.STATUS_ANY));
		}
		else {
			ddmStructureSearchContainer.setResultsAndTotal(
				() -> {
					List<DDMStructure> results =
						DDMStructureServiceUtil.getStructures(
							themeDisplay.getCompanyId(), structureGroupIds,
							PortalUtil.getClassNameId(
								JournalArticle.class.getName()),
							ddmStructureSearchContainer.getStart(),
							ddmStructureSearchContainer.getEnd(),
							ddmStructureSearchContainer.getOrderByComparator());

					List<DDMStructure> sortedResults = new ArrayList<>(results);

					Collections.sort(
						sortedResults,
						ddmStructureSearchContainer.getOrderByComparator());

					return sortedResults;
				},
				DDMStructureServiceUtil.getStructuresCount(
					themeDisplay.getCompanyId(), structureGroupIds,
					PortalUtil.getClassNameId(JournalArticle.class.getName())));
		}

		ddmStructureSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_ddmStructureSearchContainer = ddmStructureSearchContainer;

		return ddmStructureSearchContainer;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, JournalPortletKeys.JOURNAL,
			"ddm-structure-order-by-col", "modified-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, JournalPortletKeys.JOURNAL,
			"ddm-structure-order-by-type", "desc");

		return _orderByType;
	}

	public boolean isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_renderRequest, "keywords");

		return _keywords;
	}

	private OrderByComparator<DDMStructure> _getOrderByComparator() {
		OrderByComparator<DDMStructure> orderByComparator = null;

		boolean orderByAsc = false;

		String orderByType = getOrderByType();

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		String orderByCol = getOrderByCol();

		if (orderByCol.equals("id")) {
			orderByComparator = StructureIdComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("modified-date")) {
			orderByComparator = new StructureModifiedDateComparator(orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator = new StructureNameComparator(
				orderByAsc, _themeDisplay.getLocale());
		}

		return orderByComparator;
	}

	private PortletURL _getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view_ddm_structures.jsp"
		).setKeywords(
			() -> {
				String keywords = _getKeywords();

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).setParameter(
			"orderByCol",
			() -> {
				String orderByCol = getOrderByCol();

				if (Validator.isNotNull(orderByCol)) {
					return orderByCol;
				}

				return null;
			}
		).setParameter(
			"orderByType",
			() -> {
				String orderByType = getOrderByType();

				if (Validator.isNotNull(orderByType)) {
					return orderByType;
				}

				return null;
			}
		).buildPortletURL();
	}

	private SearchContainer<DDMStructure> _ddmStructureSearchContainer;
	private final HttpServletRequest _httpServletRequest;
	private final JournalWebConfiguration _journalWebConfiguration;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}