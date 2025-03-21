/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.document.library.web.internal.search.StructureSearch;
import com.liferay.document.library.web.internal.search.StructureSearchTerms;
import com.liferay.document.library.web.internal.security.permission.resource.DDMStructurePermission;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;

import jakarta.portlet.PortletURL;

/**
 * @author Rafael Praxedes
 */
public class DLViewFileEntryMetadataSetsDisplayContext {

	public DLViewFileEntryMetadataSetsDisplayContext(
		DDMStructureLinkLocalService ddmStructureLinkLocalService,
		DDMStructureService ddmStructureService,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, Portal portal) {

		_ddmStructureLinkLocalService = ddmStructureLinkLocalService;
		_ddmStructureService = ddmStructureService;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_portal = portal;

		_dlRequestHelper = new DLRequestHelper(
			portal.getHttpServletRequest(liferayPortletRequest));
	}

	public PortletURL getCopyDDMStructurePortletURL(DDMStructure ddmStructure) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/document_library/ddm/copy_ddm_structure.jsp"
		).setRedirect(
			PortletURLUtil.getCurrent(
				_liferayPortletRequest, _liferayPortletResponse)
		).setParameter(
			"ddmStructureId", ddmStructure.getStructureId()
		).buildRenderURL();
	}

	public PortletURL getDeleteDDMStructurePortletURL(
		DDMStructure ddmStructure) {

		return _getDeleteDataDefinitionPortletURL(ddmStructure);
	}

	public PortletURL getEditDDMStructurePortletURL(DDMStructure ddmStructure) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/document_library/edit_ddm_structure"
		).setRedirect(
			PortletURLBuilder.create(
				PortletURLUtil.getCurrent(
					_liferayPortletRequest, _liferayPortletResponse)
			).setNavigation(
				"file_entry_metadata_sets"
			).buildString()
		).setParameter(
			"ddmStructureId", ddmStructure.getStructureId()
		).buildRenderURL();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_liferayPortletRequest, DDMPortletKeys.DYNAMIC_DATA_MAPPING,
			"entries-order-by-col", "modified-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_liferayPortletRequest, DDMPortletKeys.DYNAMIC_DATA_MAPPING,
			"entries-order-by-type", "asc");

		return _orderByType;
	}

	public SearchContainer<DDMStructure> getStructureSearch() throws Exception {
		StructureSearch structureSearch = new StructureSearch(
			_liferayPortletRequest, getPortletURL());

		if (structureSearch.isSearch()) {
			structureSearch.setEmptyResultsMessage("no-results-were-found");
		}
		else {
			structureSearch.setEmptyResultsMessage("there-are-no-results");
		}

		structureSearch.setOrderByCol(getOrderByCol());
		structureSearch.setOrderByComparator(
			DDMUtil.getStructureOrderByComparator(
				getOrderByCol(), getOrderByType()));
		structureSearch.setOrderByType(getOrderByType());

		StructureSearchTerms searchTerms =
			(StructureSearchTerms)structureSearch.getSearchTerms();

		if (searchTerms.isSearchRestriction()) {
			structureSearch.setResultsAndTotal(
				() -> _ddmStructureLinkLocalService.getStructureLinkStructures(
					_getSearchRestrictionClassNameId(),
					_getSearchRestrictionClassPK(), structureSearch.getStart(),
					structureSearch.getEnd()),
				_ddmStructureLinkLocalService.getStructureLinksCount(
					_getSearchRestrictionClassNameId(),
					_getSearchRestrictionClassPK()));
		}
		else {
			long[] groupIds = _portal.getCurrentAndAncestorSiteGroupIds(
				_portal.getScopeGroupId(
					_dlRequestHelper.getRequest(),
					DLPortletKeys.DOCUMENT_LIBRARY, true));

			structureSearch.setResultsAndTotal(
				() -> _ddmStructureService.getStructures(
					_dlRequestHelper.getCompanyId(), groupIds,
					getStructureClassNameId(), searchTerms.getKeywords(),
					searchTerms.getStatus(), structureSearch.getStart(),
					structureSearch.getEnd(),
					structureSearch.getOrderByComparator()),
				_ddmStructureService.getStructuresCount(
					_dlRequestHelper.getCompanyId(), groupIds,
					getStructureClassNameId(), searchTerms.getKeywords(),
					searchTerms.getStatus()));
		}

		return structureSearch;
	}

	public boolean isShowAddStructureButton() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if (!group.hasLocalOrRemoteStagingGroup() ||
			(group.isStagingGroup() &&
			 DDMStructurePermission.containsAddDDMStructurePermission(
				 _dlRequestHelper.getPermissionChecker(),
				 _dlRequestHelper.getScopeGroupId(),
				 getStructureClassNameId()))) {

			return true;
		}

		return false;
	}

	protected long getClassNameId() {
		return ParamUtil.getLong(_liferayPortletRequest, "classNameId");
	}

	protected long getClassPK() {
		return ParamUtil.getLong(_liferayPortletRequest, "classPK");
	}

	protected String getKeywords() {
		return ParamUtil.getString(_liferayPortletRequest, "keywords");
	}

	protected PortletURL getPortletURL() {
		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		String mvcPath = ParamUtil.getString(_liferayPortletRequest, "mvcPath");

		if (Validator.isNotNull(mvcPath)) {
			portletURL.setParameter("mvcPath", mvcPath);
		}

		String navigation = ParamUtil.getString(
			_liferayPortletRequest, "navigation");

		if (Validator.isNotNull(navigation)) {
			portletURL.setParameter("navigation", navigation);
		}

		long templateId = ParamUtil.getLong(
			_liferayPortletRequest, "templateId");

		if (templateId != 0) {
			portletURL.setParameter("templateId", String.valueOf(templateId));
		}

		long classNameId = getClassNameId();

		if (classNameId != 0) {
			portletURL.setParameter("classNameId", String.valueOf(classNameId));
		}

		long classPK = getClassPK();

		if (classPK != 0) {
			portletURL.setParameter("classPK", String.valueOf(classPK));
		}

		long resourceClassNameId = _getResourceClassNameId();

		if (resourceClassNameId != 0) {
			portletURL.setParameter(
				"resourceClassNameId", String.valueOf(resourceClassNameId));
		}

		String delta = ParamUtil.getString(_liferayPortletRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String eventName = ParamUtil.getString(
			_liferayPortletRequest, "eventName");

		if (Validator.isNotNull(eventName)) {
			portletURL.setParameter("eventName", eventName);
		}

		String keywords = getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	protected long getStructureClassNameId() {
		return _portal.getClassNameId(DLFileEntryMetadata.class.getName());
	}

	private PortletURL _getDeleteDataDefinitionPortletURL(
		DDMStructure ddmStructure) {

		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/document_library/delete_data_definition"
		).setRedirect(
			_getRedirect()
		).setKeywords(
			_getKeywords()
		).setNavigation(
			"file_entry_metadata_sets"
		).setParameter(
			"dataDefinitionId", ddmStructure.getStructureId()
		).buildActionURL();
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_liferayPortletRequest, "keywords");

		return _keywords;
	}

	private String _getRedirect() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getURLCurrent();
	}

	private long _getResourceClassNameId() {
		long resourceClassNameId = ParamUtil.getLong(
			_liferayPortletRequest, "resourceClassNameId");

		if (resourceClassNameId == 0) {
			resourceClassNameId = _portal.getClassNameId(
				PortletDisplayTemplate.class);
		}

		return resourceClassNameId;
	}

	private long _getSearchRestrictionClassNameId() {
		return ParamUtil.getLong(
			_dlRequestHelper.getRequest(), "searchRestrictionClassNameId");
	}

	private long _getSearchRestrictionClassPK() {
		return ParamUtil.getLong(
			_dlRequestHelper.getRequest(), "searchRestrictionClassPK");
	}

	private final DDMStructureLinkLocalService _ddmStructureLinkLocalService;
	private final DDMStructureService _ddmStructureService;
	private final DLRequestHelper _dlRequestHelper;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByCol;
	private String _orderByType;
	private final Portal _portal;

}