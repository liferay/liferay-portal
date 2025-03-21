/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.display.context;

import com.liferay.dynamic.data.mapping.configuration.DDMGroupServiceConfiguration;
import com.liferay.dynamic.data.mapping.configuration.DDMWebConfiguration;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.constants.DDMWebKeys;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.dynamic.data.mapping.util.DDMDisplay;
import com.liferay.dynamic.data.mapping.util.DDMDisplayRegistryUtil;
import com.liferay.dynamic.data.mapping.util.DDMDisplayTabItem;
import com.liferay.dynamic.data.mapping.util.DDMTemplateHelper;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.dynamic.data.mapping.web.internal.context.helper.DDMWebRequestHelper;
import com.liferay.dynamic.data.mapping.web.internal.search.StructureSearch;
import com.liferay.dynamic.data.mapping.web.internal.search.StructureSearchTerms;
import com.liferay.dynamic.data.mapping.web.internal.search.TemplateSearch;
import com.liferay.dynamic.data.mapping.web.internal.search.TemplateSearchTerms;
import com.liferay.dynamic.data.mapping.web.internal.security.permission.resource.DDMStructurePermission;
import com.liferay.dynamic.data.mapping.web.internal.security.permission.resource.DDMTemplatePermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.template.comparator.TemplateHandlerComparator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author Rafael Praxedes
 */
public class DDMDisplayContext {

	public DDMDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse,
		DDMStructureLinkLocalService ddmStructureLinkLocalService,
		DDMStructureService ddmStructureService,
		DDMTemplateHelper ddmTemplateHelper,
		DDMTemplateService ddmTemplateService,
		DDMWebConfiguration ddmWebConfiguration) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_ddmStructureLinkLocalService = ddmStructureLinkLocalService;
		_ddmStructureService = ddmStructureService;
		_ddmTemplateHelper = ddmTemplateHelper;
		_ddmTemplateService = ddmTemplateService;
		_ddmWebConfiguration = ddmWebConfiguration;

		_ddmWebRequestHelper = new DDMWebRequestHelper(
			PortalUtil.getHttpServletRequest(renderRequest));
	}

	public boolean autogenerateStructureKey() {
		return _ddmWebConfiguration.autogenerateStructureKey();
	}

	public boolean autogenerateTemplateKey() {
		return _ddmWebConfiguration.autogenerateTemplateKey();
	}

	public boolean changeableDefaultLanguage() {
		return _ddmWebConfiguration.changeableDefaultLanguage();
	}

	public boolean containsAddTemplatePermission(String actualTemplateTypeValue)
		throws PortalException {

		DDMDisplay ddmDisplay = getDDMDisplay();

		String expectedTemplateTypeValue = _getTemplateTypeValue();

		if (DDMTemplatePermission.containsAddTemplatePermission(
				_ddmWebRequestHelper.getPermissionChecker(),
				_ddmWebRequestHelper.getScopeGroupId(), _getClassNameId(),
				PortalUtil.getClassNameId(ddmDisplay.getStructureType())) &&
			(Validator.isNull(expectedTemplateTypeValue) ||
			 expectedTemplateTypeValue.equals(actualTemplateTypeValue))) {

			return true;
		}

		return false;
	}

	public DDMStructure fetchStructure(DDMTemplate template) {
		return _ddmTemplateHelper.fetchStructure(template);
	}

	public List<DropdownItem> getActionItemsDropdownItems(String action) {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", action);
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					LanguageUtil.get(
						_ddmWebRequestHelper.getRequest(), "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getAutocompleteJSON(
			HttpServletRequest httpServletRequest, String language)
		throws Exception {

		return _ddmTemplateHelper.getAutocompleteJSON(
			httpServletRequest, language);
	}

	public String getClearResultsURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(_getPortletURL(), _renderResponse)
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public DDMDisplay getDDMDisplay() {
		return DDMDisplayRegistryUtil.getDDMDisplay(getRefererPortletName());
	}

	public SearchContainer<DDMStructure> getDDMStructureSearchContainer()
		throws Exception {

		StructureSearch structureSearch = new StructureSearch(
			_renderRequest, _getPortletURL());

		if (structureSearch.isSearch()) {
			structureSearch.setEmptyResultsMessage(
				LanguageUtil.format(
					_ddmWebRequestHelper.getRequest(), "no-x-were-found",
					getScopedStructureLabel(), false));
		}
		else {
			structureSearch.setEmptyResultsMessage(
				LanguageUtil.format(
					_ddmWebRequestHelper.getRequest(), "there-are-no-x",
					getScopedStructureLabel(), false));
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
			long[] groupIds = {
				PortalUtil.getScopeGroupId(
					_ddmWebRequestHelper.getRequest(), getRefererPortletName(),
					true)
			};

			if (_showAncestorScopes()) {
				groupIds = PortalUtil.getCurrentAndAncestorSiteGroupIds(
					groupIds);
			}

			Group group = null;

			Layout layout = _ddmWebRequestHelper.getLayout();

			if (layout != null) {
				group = layout.getGroup();
			}

			if ((group != null) && !group.isStagingGroup()) {
				groupIds = ArrayUtil.append(groupIds, group.getGroupId());
			}

			long[] allGroupIds = groupIds;

			structureSearch.setResultsAndTotal(
				() -> _ddmStructureService.getStructures(
					_ddmWebRequestHelper.getCompanyId(), allGroupIds,
					_getStructureClassNameId(), searchTerms.getKeywords(),
					searchTerms.getStatus(), structureSearch.getStart(),
					structureSearch.getEnd(),
					structureSearch.getOrderByComparator()),
				_ddmStructureService.getStructuresCount(
					_ddmWebRequestHelper.getCompanyId(), allGroupIds,
					_getStructureClassNameId(), searchTerms.getKeywords(),
					searchTerms.getStatus()));
		}

		return structureSearch;
	}

	public SearchContainer<DDMTemplate> getDDMTemplateSearchContainer()
		throws Exception {

		TemplateSearch templateSearch = new TemplateSearch(
			_renderRequest, _getPortletURL());

		if (templateSearch.isSearch()) {
			templateSearch.setEmptyResultsMessage("no-templates-were-found");
		}
		else {
			templateSearch.setEmptyResultsMessage("there-are-no-templates");
		}

		templateSearch.setOrderByCol(getOrderByCol());
		templateSearch.setOrderByComparator(
			DDMUtil.getTemplateOrderByComparator(
				getOrderByCol(), getOrderByType()));
		templateSearch.setOrderByType(getOrderByType());

		TemplateSearchTerms searchTerms =
			(TemplateSearchTerms)templateSearch.getSearchTerms();
		DDMDisplay ddmDisplay = getDDMDisplay();

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] groupIds = ddmDisplay.getTemplateGroupIds(
			themeDisplay, _showAncestorScopes());

		templateSearch.setResultsAndTotal(
			() -> _ddmTemplateService.search(
				_ddmWebRequestHelper.getCompanyId(), groupIds,
				_getTemplateClassNameIds(), _getDDMTemplateClassPKs(),
				_getResourceClassNameId(), searchTerms.getKeywords(),
				searchTerms.getType(), _getTemplateMode(),
				searchTerms.getStatus(), templateSearch.getStart(),
				templateSearch.getEnd(), templateSearch.getOrderByComparator()),
			_ddmTemplateService.searchCount(
				_ddmWebRequestHelper.getCompanyId(), groupIds,
				_getTemplateClassNameIds(), _getDDMTemplateClassPKs(),
				_getResourceClassNameId(), searchTerms.getKeywords(),
				searchTerms.getType(), _getTemplateMode(),
				searchTerms.getStatus()));

		return templateSearch;
	}

	public List<NavigationItem> getNavigationItem() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(true);
				navigationItem.setLabel(getScopedStructureLabel());
			}
		).build();
	}

	public List<NavigationItem> getNavigationItems(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return new NavigationItemList() {
			{
				DDMDisplay ddmDisplay = getDDMDisplay();

				for (DDMDisplayTabItem ddmDisplayTabItem :
						ddmDisplay.getTabItems()) {

					if (!ddmDisplayTabItem.isShow(liferayPortletRequest)) {
						continue;
					}

					String ddmDisplayTabItemTitle = GetterUtil.getString(
						ddmDisplayTabItem.getTitle(
							liferayPortletRequest, liferayPortletResponse));

					DDMDisplayTabItem defaultDDMDisplayTabItem =
						ddmDisplay.getDefaultTabItem();

					String defaultDDMDisplayTabItemTitle = GetterUtil.getString(
						defaultDDMDisplayTabItem.getTitle(
							liferayPortletRequest, liferayPortletResponse));

					String ddmDisplayTabItemHREF = GetterUtil.getString(
						ddmDisplayTabItem.getURL(
							liferayPortletRequest, liferayPortletResponse));

					add(
						navigationItem -> {
							navigationItem.setActive(
								Objects.equals(
									ddmDisplayTabItemTitle,
									defaultDDMDisplayTabItemTitle));
							navigationItem.setHref(ddmDisplayTabItemHREF);
							navigationItem.setLabel(ddmDisplayTabItemTitle);
						});
				}
			}
		};
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_renderRequest, DDMPortletKeys.DYNAMIC_DATA_MAPPING,
			"entries-order-by-col", "modified-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_renderRequest, DDMPortletKeys.DYNAMIC_DATA_MAPPING,
			"entries-order-by-type", "asc");

		return _orderByType;
	}

	public List<DropdownItem> getOrderItemsDropdownItems() {
		return DropdownItemListBuilder.add(
			_getOrderByDropdownItem("modified-date")
		).add(
			_getOrderByDropdownItem("id")
		).build();
	}

	public String getRefererPortletName() {
		return ParamUtil.getString(
			_ddmWebRequestHelper.getRequest(), "refererPortletName",
			_ddmWebRequestHelper.getPortletName());
	}

	public String getScopedStructureLabel() {
		DDMDisplay ddmDisplay = getDDMDisplay();

		return ddmDisplay.getTitle(_ddmWebRequestHelper.getLocale());
	}

	public CreationMenu getSelectStructureCreationMenu()
		throws PortalException {

		if (!isShowAddStructureButton()) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				PortletURL redirectURL = PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCPath(
					"/select_structure.jsp"
				).setParameter(
					"classPK", _getClassPK()
				).setParameter(
					"eventName",
					ParamUtil.getString(
						_renderRequest, "eventName", "selectStructure")
				).buildPortletURL();

				dropdownItem.setHref(
					_renderResponse.createRenderURL(), "mvcPath",
					"/edit_structure.jsp", "redirect", redirectURL, "groupId",
					String.valueOf(_ddmWebRequestHelper.getScopeGroupId()));

				dropdownItem.setLabel(
					LanguageUtil.get(_ddmWebRequestHelper.getRequest(), "add"));
			}
		).build();
	}

	public String getSelectStructureSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/select_structure.jsp"
		).setParameter(
			"classPK", ParamUtil.getLong(_renderRequest, "classPK")
		).setParameter(
			"eventName",
			ParamUtil.getString(_renderRequest, "eventName", "selectStructure")
		).buildString();
	}

	public String getSelectTemplateSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/select_template.jsp"
		).setParameter(
			"classNameId", _getClassNameId()
		).setParameter(
			"classPK", ParamUtil.getLong(_renderRequest, "classPK")
		).setParameter(
			"eventName",
			ParamUtil.getString(_renderRequest, "eventName", "selectTemplate")
		).setParameter(
			"resourceClassNameId", _getResourceClassNameId()
		).setParameter(
			"templateId", ParamUtil.getLong(_renderRequest, "templateId")
		).buildString();
	}

	public String getSortingURL() throws Exception {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(_getPortletURL(), _renderResponse)
		).setParameter(
			"orderByType",
			() -> {
				String orderByType = ParamUtil.getString(
					_renderRequest, "orderByType");

				if (orderByType.equals("asc")) {
					return "desc";
				}

				return "asc";
			}
		).buildString();
	}

	public CreationMenu getStructureCreationMenu() throws PortalException {
		if (!isShowAddStructureButton()) {
			return null;
		}

		PortletURL redirectURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view.jsp"
		).setParameter(
			"groupId", _ddmWebRequestHelper.getScopeGroupId()
		).buildPortletURL();

		return CreationMenuBuilder.addPrimaryDropdownItem(
			_getCreationMenuDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCPath(
					"/edit_structure.jsp"
				).setRedirect(
					redirectURL
				).setParameter(
					"groupId", _ddmWebRequestHelper.getScopeGroupId()
				).buildPortletURL(),
				"add")
		).build();
	}

	public String getStructureSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view.jsp"
		).setTabs1(
			ParamUtil.getString(_renderRequest, "tabs1", "structures")
		).setParameter(
			"groupId", _ddmWebRequestHelper.getScopeGroupId()
		).buildString();
	}

	public String getStructureSearchContainerId() {
		return "ddmStructures";
	}

	public CreationMenu getTemplateCreationMenu() throws PortalException {
		if (!isShowAddTemplateButton()) {
			return null;
		}

		return new CreationMenu() {
			{
				if (_getClassNameId() == PortalUtil.getClassNameId(
						DDMStructure.class)) {

					PortletURL addTemplateURL =
						PortletURLBuilder.createRenderURL(
							_renderResponse
						).setMVCPath(
							"/edit_template.jsp"
						).setParameter(
							"classNameId", _getClassNameId()
						).setParameter(
							"classPK", _getClassPK()
						).setParameter(
							"groupId", _ddmWebRequestHelper.getScopeGroupId()
						).setParameter(
							"mode", _getTemplateMode()
						).setParameter(
							"resourceClassNameId", _getResourceClassNameId()
						).buildPortletURL();

					String message = "add";

					if (containsAddTemplatePermission(
							DDMTemplateConstants.TEMPLATE_TYPE_FORM)) {

						addTemplateURL.setParameter(
							"structureAvailableFields",
							_renderResponse.getNamespace() +
								"getAvailableFields");

						if (Validator.isNull(_getTemplateTypeValue())) {
							message = "add-form-template";
						}

						addPrimaryDropdownItem(
							_getCreationMenuDropdownItem(
								addTemplateURL, message));
					}

					if (containsAddTemplatePermission(
							DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY)) {

						addTemplateURL.setParameter(
							"type", DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY);

						if (Validator.isNull(_getTemplateTypeValue())) {
							message = "add-display-template";
						}

						addPrimaryDropdownItem(
							_getCreationMenuDropdownItem(
								addTemplateURL, message));
					}
				}
				else {
					List<TemplateHandler> templateHandlers =
						_getTemplateHandlers();

					if (!templateHandlers.isEmpty()) {
						PortletURL addPortletDisplayTemplateURL =
							PortletURLBuilder.createRenderURL(
								_renderResponse
							).setMVCPath(
								"/edit_template.jsp"
							).setParameter(
								"groupId",
								_ddmWebRequestHelper.getScopeGroupId()
							).setParameter(
								"type",
								DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY
							).buildPortletURL();

						for (TemplateHandler templateHandler :
								templateHandlers) {

							addPortletDisplayTemplateURL.setParameter(
								"classNameId",
								String.valueOf(
									PortalUtil.getClassNameId(
										templateHandler.getClassName())));
							addPortletDisplayTemplateURL.setParameter(
								"classPK", String.valueOf(0));
							addPortletDisplayTemplateURL.setParameter(
								"resourceClassNameId",
								String.valueOf(_getResourceClassNameId()));

							addPrimaryDropdownItem(
								_getCreationMenuDropdownItem(
									addPortletDisplayTemplateURL,
									templateHandler.getName(
										_ddmWebRequestHelper.getLocale())));
						}
					}
				}
			}
		};
	}

	public String getTemplateSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view_template.jsp"
		).setTabs1(
			ParamUtil.getString(_renderRequest, "tabs1", "templates")
		).setParameter(
			"classNameId", _getClassNameId()
		).setParameter(
			"classPK", _getClassPK()
		).setParameter(
			"eventName",
			ParamUtil.getString(_renderRequest, "eventName", "selectTemplate")
		).setParameter(
			"groupId", _ddmWebRequestHelper.getScopeGroupId()
		).setParameter(
			"resourceClassNameId", _getResourceClassNameId()
		).setParameter(
			"templateId", ParamUtil.getLong(_renderRequest, "templateId")
		).buildString();
	}

	public String getTemplateSearchContainerId() {
		return "ddmTemplates";
	}

	public int getTotalItems(String context) throws Exception {
		SearchContainer<?> searchContainer;

		if (Objects.equals(
				context, DDMWebKeys.DYNAMIC_DATA_MAPPING_STRUCTURE)) {

			searchContainer = getDDMStructureSearchContainer();
		}
		else {
			searchContainer = getDDMTemplateSearchContainer();
		}

		return searchContainer.getTotal();
	}

	public boolean isAutocompleteEnabled(String language) {
		return _ddmTemplateHelper.isAutocompleteEnabled(language);
	}

	public boolean isDisabledManagementBar(String context) throws Exception {
		if (_hasResults(context) || isSearch()) {
			return false;
		}

		return true;
	}

	public boolean isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	public boolean isShowAddStructureButton() throws PortalException {
		DDMDisplay ddmDisplay = getDDMDisplay();

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (ddmDisplay.isShowAddButton(themeDisplay.getScopeGroup()) &&
			DDMStructurePermission.containsAddStructurePermission(
				_ddmWebRequestHelper.getPermissionChecker(),
				_ddmWebRequestHelper.getScopeGroupId(),
				_getStructureClassNameId())) {

			return true;
		}

		return false;
	}

	public boolean isShowAddTemplateButton() throws PortalException {
		DDMDisplay ddmDisplay = getDDMDisplay();

		ThemeDisplay themeDisplay = _ddmWebRequestHelper.getThemeDisplay();

		if (_ddmWebConfiguration.enableTemplateCreation() &&
			ddmDisplay.isShowAddButton(themeDisplay.getScopeGroup())) {

			long classNameId = _getClassNameId();
			long resourceClassNameId = PortalUtil.getClassNameId(
				ddmDisplay.getStructureType());

			if ((classNameId != 0) && (resourceClassNameId != 0)) {
				return DDMTemplatePermission.containsAddTemplatePermission(
					_ddmWebRequestHelper.getPermissionChecker(),
					_ddmWebRequestHelper.getScopeGroupId(), classNameId,
					resourceClassNameId);
			}

			return true;
		}

		return false;
	}

	public String[] smallImageExtensions() {
		DDMGroupServiceConfiguration ddmGroupServiceConfiguration =
			_ddmWebRequestHelper.getDDMGroupServiceConfiguration();

		return ddmGroupServiceConfiguration.smallImageExtensions();
	}

	public int smallImageMaxSize() {
		DDMGroupServiceConfiguration ddmGroupServiceConfiguration =
			_ddmWebRequestHelper.getDDMGroupServiceConfiguration();

		return ddmGroupServiceConfiguration.smallImageMaxSize();
	}

	private boolean _containsAddPortletDisplayTemplatePermission(
			String resourceName)
		throws PortalException {

		if (_getClassNameId() > 0) {
			return PortletPermissionUtil.contains(
				_ddmWebRequestHelper.getPermissionChecker(),
				_ddmWebRequestHelper.getLayout(), resourceName,
				ActionKeys.ADD_PORTLET_DISPLAY_TEMPLATE);
		}

		return PortletPermissionUtil.contains(
			_ddmWebRequestHelper.getPermissionChecker(),
			_ddmWebRequestHelper.getScopeGroupId(),
			_ddmWebRequestHelper.getLayout(), resourceName,
			ActionKeys.ADD_PORTLET_DISPLAY_TEMPLATE, false, false);
	}

	private long _getClassNameId() {
		return ParamUtil.getLong(_renderRequest, "classNameId");
	}

	private long _getClassPK() {
		return ParamUtil.getLong(_renderRequest, "classPK");
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getCreationMenuDropdownItem(PortletURL url, String label) {

		return dropdownItem -> {
			dropdownItem.setHref(url);
			dropdownItem.setLabel(
				LanguageUtil.get(_ddmWebRequestHelper.getRequest(), label));
		};
	}

	private long[] _getDDMTemplateClassPKs() {
		if (_getClassPK() > 0) {
			return new long[] {_getClassPK()};
		}

		return null;
	}

	private String _getKeywords() {
		return ParamUtil.getString(_renderRequest, "keywords");
	}

	private UnsafeConsumer<DropdownItem, Exception> _getOrderByDropdownItem(
		String orderByCol) {

		return dropdownItem -> {
			dropdownItem.setActive(orderByCol.equals(getOrderByCol()));
			dropdownItem.setHref(_getPortletURL(), "orderByCol", orderByCol);
			dropdownItem.setLabel(
				LanguageUtil.get(
					_ddmWebRequestHelper.getRequest(), orderByCol));
		};
	}

	private PortletURL _getPortletURL() {
		PortletURL portletURL = _renderResponse.createRenderURL();

		String mvcPath = ParamUtil.getString(_renderRequest, "mvcPath");

		if (Validator.isNotNull(mvcPath)) {
			portletURL.setParameter("mvcPath", mvcPath);
		}

		String tabs1 = ParamUtil.getString(_renderRequest, "tabs1");

		if (Validator.isNotNull(tabs1)) {
			portletURL.setParameter("tabs1", tabs1);
		}

		long templateId = ParamUtil.getLong(_renderRequest, "templateId");

		if (templateId != 0) {
			portletURL.setParameter("templateId", String.valueOf(templateId));
		}

		long classNameId = _getClassNameId();

		if (classNameId != 0) {
			portletURL.setParameter("classNameId", String.valueOf(classNameId));
		}

		long classPK = _getClassPK();

		if (classPK != 0) {
			portletURL.setParameter("classPK", String.valueOf(classPK));
		}

		long resourceClassNameId = _getResourceClassNameId();

		if (resourceClassNameId != 0) {
			portletURL.setParameter(
				"resourceClassNameId", String.valueOf(resourceClassNameId));
		}

		String refererPortletName = getRefererPortletName();

		if (Validator.isNotNull(refererPortletName)) {
			portletURL.setParameter("refererPortletName", refererPortletName);
		}

		String delta = ParamUtil.getString(_renderRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String eventName = ParamUtil.getString(_renderRequest, "eventName");

		if (Validator.isNotNull(eventName)) {
			portletURL.setParameter("eventName", eventName);
		}

		String keywords = _getKeywords();

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

		boolean showAncestorScopes = _showAncestorScopes();

		if (showAncestorScopes) {
			portletURL.setParameter(
				"showAncestorScopes", String.valueOf(showAncestorScopes));
		}

		return portletURL;
	}

	private long _getResourceClassNameId() {
		long resourceClassNameId = ParamUtil.getLong(
			_renderRequest, "resourceClassNameId");

		if (resourceClassNameId == 0) {
			resourceClassNameId = PortalUtil.getClassNameId(
				PortletDisplayTemplate.class);
		}

		return resourceClassNameId;
	}

	private long _getSearchRestrictionClassNameId() {
		return ParamUtil.getLong(
			_ddmWebRequestHelper.getRequest(), "searchRestrictionClassNameId");
	}

	private long _getSearchRestrictionClassPK() {
		return ParamUtil.getLong(
			_ddmWebRequestHelper.getRequest(), "searchRestrictionClassPK");
	}

	private long _getStructureClassNameId() {
		DDMDisplay ddmDisplay = getDDMDisplay();

		return PortalUtil.getClassNameId(ddmDisplay.getStructureType());
	}

	private long[] _getTemplateClassNameIds() {
		DDMDisplay ddmDisplay = getDDMDisplay();

		return ddmDisplay.getTemplateClassNameIds(_getClassNameId());
	}

	private List<TemplateHandler> _getTemplateHandlers()
		throws PortalException {

		List<TemplateHandler> templateHandlers = new ArrayList<>();

		if (_getClassNameId() > 0) {
			TemplateHandler templateHandler =
				TemplateHandlerRegistryUtil.getTemplateHandler(
					_getClassNameId());

			if (_containsAddPortletDisplayTemplatePermission(
					templateHandler.getResourceName())) {

				templateHandlers.add(templateHandler);
			}
		}
		else {
			PortletDisplayTemplate portletDisplayTemplate =
				_portletDisplayTemplateSnapshot.get();

			templateHandlers =
				portletDisplayTemplate.getPortletDisplayTemplateHandlers();

			Iterator<TemplateHandler> iterator = templateHandlers.iterator();

			while (iterator.hasNext()) {
				TemplateHandler templateHandler = iterator.next();

				if (!_containsAddPortletDisplayTemplatePermission(
						templateHandler.getResourceName())) {

					iterator.remove();
				}
			}
		}

		ListUtil.sort(
			templateHandlers,
			new TemplateHandlerComparator(_ddmWebRequestHelper.getLocale()));

		return templateHandlers;
	}

	private String _getTemplateMode() {
		DDMDisplay ddmDisplay = getDDMDisplay();

		return ParamUtil.getString(
			_renderRequest, "mode", ddmDisplay.getTemplateMode());
	}

	private String _getTemplateTypeValue() {
		DDMDisplay ddmDisplay = getDDMDisplay();

		String scopeTemplateType = ddmDisplay.getTemplateType();

		String templateTypeValue = StringPool.BLANK;

		if (scopeTemplateType.equals(
				DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY)) {

			templateTypeValue = DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY;
		}
		else if (scopeTemplateType.equals(
					DDMTemplateConstants.TEMPLATE_TYPE_FORM)) {

			templateTypeValue = DDMTemplateConstants.TEMPLATE_TYPE_FORM;
		}

		return templateTypeValue;
	}

	private boolean _hasResults(String context) throws Exception {
		if (getTotalItems(context) > 0) {
			return true;
		}

		return false;
	}

	private boolean _showAncestorScopes() {
		return ParamUtil.getBoolean(_renderRequest, "showAncestorScopes");
	}

	private static final Snapshot<PortletDisplayTemplate>
		_portletDisplayTemplateSnapshot = new Snapshot<>(
			DDMDisplayContext.class, PortletDisplayTemplate.class);

	private final DDMStructureLinkLocalService _ddmStructureLinkLocalService;
	private final DDMStructureService _ddmStructureService;
	private final DDMTemplateHelper _ddmTemplateHelper;
	private final DDMTemplateService _ddmTemplateService;
	private final DDMWebConfiguration _ddmWebConfiguration;
	private final DDMWebRequestHelper _ddmWebRequestHelper;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}