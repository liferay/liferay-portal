/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateServiceUtil;
import com.liferay.dynamic.data.mapping.util.comparator.DDMTemplateNameComparator;
import com.liferay.dynamic.data.mapping.util.comparator.TemplateIdComparator;
import com.liferay.dynamic.data.mapping.util.comparator.TemplateModifiedDateComparator;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.servlet.taglib.util.JournalDDMTemplateActionDropdownItemsProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
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
public class JournalDDMTemplateDisplayContext {

	public JournalDDMTemplateDisplayContext(
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

	public long getClassPK() {
		if (_classPK != null) {
			return _classPK;
		}

		_classPK = ParamUtil.getLong(_httpServletRequest, "classPK");

		return _classPK;
	}

	public DDMStructure getDDMStructure() {
		if ((_ddmStructure != null) || (getClassPK() <= 0)) {
			return _ddmStructure;
		}

		_ddmStructure = DDMStructureLocalServiceUtil.fetchStructure(
			getClassPK());

		return _ddmStructure;
	}

	public List<DropdownItem> getDDMTemplateActionDropdownItems(
			DDMTemplate ddmTemplate)
		throws Exception {

		JournalDDMTemplateActionDropdownItemsProvider
			ddmTemplateActionDropdownItems =
				new JournalDDMTemplateActionDropdownItemsProvider(
					ddmTemplate, _renderRequest, _renderResponse);

		return ddmTemplateActionDropdownItems.getActionDropdownItems();
	}

	public SearchContainer<DDMTemplate> getDDMTemplateSearchContainer()
		throws Exception {

		if (_ddmTemplateSearchContainer != null) {
			return _ddmTemplateSearchContainer;
		}

		String emptyResultsMessage = "there-are-no-templates";

		if (Validator.isNotNull(_getKeywords())) {
			emptyResultsMessage = "no-templates-were-found";
		}

		SearchContainer<DDMTemplate> ddmTemplateSearchContainer =
			new SearchContainer(
				_renderRequest, _getPortletURL(), null, emptyResultsMessage);

		ddmTemplateSearchContainer.setOrderByCol(getOrderByCol());
		ddmTemplateSearchContainer.setOrderByComparator(
			_getOrderByComparator());
		ddmTemplateSearchContainer.setOrderByType(getOrderByType());

		long[] groupIds = {_themeDisplay.getScopeGroupId()};

		if (_journalWebConfiguration.showAncestorScopesByDefault()) {
			groupIds =
				SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						_themeDisplay.getScopeGroupId(), false, true);
		}

		long[] templateGroupIds = groupIds;

		if (Validator.isNotNull(_getKeywords())) {
			ddmTemplateSearchContainer.setResultsAndTotal(
				() -> {
					List<DDMTemplate> ddmTemplates =
						DDMTemplateServiceUtil.search(
							_themeDisplay.getCompanyId(), templateGroupIds,
							new long[] {
								PortalUtil.getClassNameId(DDMStructure.class)
							},
							_getDDMTemplateClassPKs(),
							PortalUtil.getClassNameId(JournalArticle.class),
							_getKeywords(), StringPool.BLANK, StringPool.BLANK,
							WorkflowConstants.STATUS_ANY,
							ddmTemplateSearchContainer.getStart(),
							ddmTemplateSearchContainer.getEnd(),
							ddmTemplateSearchContainer.getOrderByComparator());

					List<DDMTemplate> sortedDDMTemplates = new ArrayList<>(
						ddmTemplates);

					Collections.sort(
						sortedDDMTemplates,
						ddmTemplateSearchContainer.getOrderByComparator());

					return sortedDDMTemplates;
				},
				DDMTemplateServiceUtil.searchCount(
					_themeDisplay.getCompanyId(), templateGroupIds,
					new long[] {PortalUtil.getClassNameId(DDMStructure.class)},
					_getDDMTemplateClassPKs(),
					PortalUtil.getClassNameId(JournalArticle.class),
					_getKeywords(), StringPool.BLANK, StringPool.BLANK,
					WorkflowConstants.STATUS_ANY));
		}
		else {
			ddmTemplateSearchContainer.setResultsAndTotal(
				() -> {
					List<DDMTemplate> ddmTemplates =
						DDMTemplateServiceUtil.getTemplates(
							_themeDisplay.getCompanyId(), templateGroupIds,
							new long[] {
								PortalUtil.getClassNameId(DDMStructure.class)
							},
							_getDDMTemplateClassPKs(),
							PortalUtil.getClassNameId(JournalArticle.class),
							ddmTemplateSearchContainer.getStart(),
							ddmTemplateSearchContainer.getEnd(),
							ddmTemplateSearchContainer.getOrderByComparator());

					List<DDMTemplate> sortedDDMTemplates = new ArrayList<>(
						ddmTemplates);

					Collections.sort(
						sortedDDMTemplates,
						ddmTemplateSearchContainer.getOrderByComparator());

					return sortedDDMTemplates;
				},
				DDMTemplateServiceUtil.getTemplatesCount(
					_themeDisplay.getCompanyId(), templateGroupIds,
					new long[] {PortalUtil.getClassNameId(DDMStructure.class)},
					_getDDMTemplateClassPKs(),
					PortalUtil.getClassNameId(JournalArticle.class)));
		}

		ddmTemplateSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_ddmTemplateSearchContainer = ddmTemplateSearchContainer;

		return ddmTemplateSearchContainer;
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				_httpServletRequest);

		_displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle");

		if (Validator.isNull(_displayStyle)) {
			_displayStyle = portalPreferences.getValue(
				JournalPortletKeys.JOURNAL + ".ddmTemplates", "display-style",
				_DISPLAY_VIEWS[0]);
		}

		if (!ArrayUtil.contains(_DISPLAY_VIEWS, _displayStyle)) {
			_displayStyle = _DISPLAY_VIEWS[0];
		}

		portalPreferences.setValue(
			JournalPortletKeys.JOURNAL + ".ddmTemplates", "display-style",
			_displayStyle);

		return _displayStyle;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, JournalPortletKeys.JOURNAL,
			"ddm-template-order-by-col", "modified-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, JournalPortletKeys.JOURNAL,
			"ddm-template-order-by-type", "desc");

		return _orderByType;
	}

	public boolean isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	private long[] _getDDMTemplateClassPKs() {
		if (getClassPK() > 0) {
			return new long[] {getClassPK()};
		}

		return null;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_renderRequest, "keywords");

		return _keywords;
	}

	private OrderByComparator<DDMTemplate> _getOrderByComparator() {
		OrderByComparator<DDMTemplate> orderByComparator = null;

		boolean orderByAsc = false;

		String orderByType = getOrderByType();

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		String orderByCol = getOrderByCol();

		if (orderByCol.equals("id")) {
			orderByComparator = TemplateIdComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("modified-date")) {
			orderByComparator = new TemplateModifiedDateComparator(orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator = new DDMTemplateNameComparator(
				orderByAsc, _themeDisplay.getLocale());
		}

		return orderByComparator;
	}

	private PortletURL _getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view_ddm_templates.jsp"
		).setKeywords(
			() -> {
				String keywords = _getKeywords();

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).setParameter(
			"classPK",
			() -> {
				long classPK = ParamUtil.getLong(_renderRequest, "classPK");

				if (classPK > 0) {
					return classPK;
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

	private static final String[] _DISPLAY_VIEWS = {"icon", "list"};

	private Long _classPK;
	private DDMStructure _ddmStructure;
	private SearchContainer<DDMTemplate> _ddmTemplateSearchContainer;
	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final JournalWebConfiguration _journalWebConfiguration;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}