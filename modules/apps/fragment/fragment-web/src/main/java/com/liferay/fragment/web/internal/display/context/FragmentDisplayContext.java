/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryServiceUtil;
import com.liferay.fragment.util.comparator.FragmentCollectionContributorNameComparator;
import com.liferay.fragment.util.comparator.FragmentCompositionFragmentEntryNameComparator;
import com.liferay.fragment.web.internal.constants.FragmentTypeConstants;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.fragment.web.internal.util.FragmentPortletUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.IconItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.marketplace.constants.MarketplaceActionKeys;
import com.liferay.marketplace.constants.MarketplacePortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jürgen Kappler
 */
public class FragmentDisplayContext {

	public FragmentDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_fragmentCollectionContributorRegistry =
			(FragmentCollectionContributorRegistry)
				httpServletRequest.getAttribute(
					FragmentCollectionContributorRegistry.class.getName());
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		Map<String, Object> fragmentCollectionsViewContext =
			getFragmentCollectionsViewContext();

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref(
					_renderResponse.createRenderURL(), "mvcRenderCommandName",
					"/fragment/edit_fragment_collection", "redirect",
					_themeDisplay.getURLCurrent());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "fragment-set"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "openImportView");
				dropdownItem.putData(
					"importURL",
					(String)fragmentCollectionsViewContext.get("importURL"));
				dropdownItem.putData(
					"viewImportURL",
					(String)fragmentCollectionsViewContext.get(
						"viewImportURL"));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "import"));
			}
		).build();
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"addFragmentCollectionURL",
			() -> {
				LiferayPortletURL addFragmentCollectionURL =
					(LiferayPortletURL)_renderResponse.createResourceURL();

				addFragmentCollectionURL.setCopyCurrentRenderParameters(false);
				addFragmentCollectionURL.setResourceID(
					"/fragment/add_fragment_collection");

				return addFragmentCollectionURL.toString();
			}
		).put(
			"fragmentCollections",
			() -> {
				JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

				for (FragmentCollection fragmentCollection :
						FragmentCollectionLocalServiceUtil.
							getFragmentCollections(
								_themeDisplay.getScopeGroupId(),
								QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

					jsonArray.put(
						JSONUtil.put(
							"fragmentCollectionId",
							fragmentCollection.getFragmentCollectionId()
						).put(
							"name", fragmentCollection.getName()
						));
				}

				return jsonArray;
			}
		).build();
	}

	public String getAvailableActions(Object object) {
		List<String> availableActions = new ArrayList<>();

		boolean marketplace = _isMarketplace(object);

		if (!marketplace) {
			availableActions.add(
				"exportFragmentCompositionsAndFragmentEntries");
		}

		if (FragmentPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES)) {

			if (!marketplace && (object instanceof FragmentEntry)) {
				availableActions.add("copySelectedFragmentEntries");
			}

			availableActions.add(
				"deleteFragmentCompositionsAndFragmentEntries");
			availableActions.add("moveFragmentCompositionsAndFragmentEntries");
		}

		return StringUtil.merge(availableActions, StringPool.COMMA);
	}

	public List<DropdownItem> getCollectionsDropdownItems() {
		boolean hasManageFragmentEntriesPermission =
			FragmentPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData("action", "exportCollections");
							dropdownItem.setIcon("export");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "export"));
						}
					).add(
						() -> hasManageFragmentEntriesPermission,
						dropdownItem -> {
							dropdownItem.setHref(
								PortletURLBuilder.createRenderURL(
									_renderResponse
								).setMVCRenderCommandName(
									"/fragment/view_import"
								).setParameter(
									"fragmentCollectionId", 0
								).buildString());
							dropdownItem.setIcon("import");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "import"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasManageFragmentEntriesPermission,
						dropdownItem -> {
							dropdownItem.putData("action", "deleteCollections");
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "delete"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public SearchContainer<Object> getContributedEntriesSearchContainer() {
		if (_contributedEntriesSearchContainer != null) {
			return _contributedEntriesSearchContainer;
		}

		SearchContainer<Object> contributedEntriesSearchContainer =
			new SearchContainer(
				_renderRequest, _getPortletURL(), null,
				"there-are-no-fragments");

		contributedEntriesSearchContainer.setId(
			"fragmentEntries" + getFragmentCollectionKey());

		FragmentCollectionContributor fragmentCollectionContributor =
			_getFragmentCollectionContributor();

		List<Object> contributedEntries = new ArrayList<>();

		contributedEntries.addAll(
			fragmentCollectionContributor.getFragmentCompositions(
				_themeDisplay.getLocale()));
		contributedEntries.addAll(
			fragmentCollectionContributor.getFragmentEntries(
				_themeDisplay.getLocale()));

		contributedEntries.sort(
			FragmentCompositionFragmentEntryNameComparator.getInstance(true));

		if (isSearch()) {
			contributedEntries = ListUtil.filter(
				contributedEntries,
				contributedEntry -> {
					String lowerCaseName = StringUtil.toLowerCase(
						_getName(contributedEntry));

					return lowerCaseName.contains(
						StringUtil.toLowerCase(_getKeywords()));
				});
		}

		contributedEntriesSearchContainer.setResultsAndTotal(
			contributedEntries);

		contributedEntriesSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_contributedEntriesSearchContainer = contributedEntriesSearchContainer;

		return _contributedEntriesSearchContainer;
	}

	public FragmentCollection getFragmentCollection() {
		if (_fragmentCollection != null) {
			return _fragmentCollection;
		}

		_fragmentCollection =
			FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
				getFragmentCollectionId());

		return _fragmentCollection;
	}

	public FragmentCollectionContributor getFragmentCollectionContributor() {
		return _fragmentCollectionContributorRegistry.
			getFragmentCollectionContributor(getFragmentCollectionKey());
	}

	public List<FragmentCollectionContributor>
		getFragmentCollectionContributors(Locale locale) {

		List<FragmentCollectionContributor> fragmentCollectionContributors =
			_fragmentCollectionContributorRegistry.
				getFragmentCollectionContributors();

		Collections.sort(
			fragmentCollectionContributors,
			new FragmentCollectionContributorNameComparator(locale));

		return fragmentCollectionContributors;
	}

	public long getFragmentCollectionId() {
		if (Validator.isNotNull(_fragmentCollectionId)) {
			return _fragmentCollectionId;
		}

		_fragmentCollectionId = ParamUtil.getLong(
			_httpServletRequest, "fragmentCollectionId",
			_getDefaultFragmentCollectionId());

		return _fragmentCollectionId;
	}

	public String getFragmentCollectionKey() {
		if (_fragmentCollectionKey != null) {
			return _fragmentCollectionKey;
		}

		_fragmentCollectionKey = ParamUtil.getString(
			_httpServletRequest, "fragmentCollectionKey",
			_getDefaultFragmentCollectionKey());

		return _fragmentCollectionKey;
	}

	public String getFragmentCollectionName() throws PortalException {
		if (isSelectedFragmentCollectionContributor()) {
			FragmentCollectionContributor fragmentCollectionContributor =
				_getFragmentCollectionContributor();

			return fragmentCollectionContributor.getName(
				_themeDisplay.getLocale());
		}

		FragmentCollection fragmentCollection = getFragmentCollection();

		if (fragmentCollection == null) {
			return StringPool.BLANK;
		}

		String fragmentCollectionName = fragmentCollection.getName();

		if (!_isScopeGroup()) {
			Group group = GroupLocalServiceUtil.fetchGroup(
				fragmentCollection.getGroupId());

			String groupName = LanguageUtil.get(
				_themeDisplay.getLocale(), "system");

			if (group != null) {
				groupName = getGroupName(group.getGroupId());
			}

			fragmentCollectionName = StringUtil.appendParentheticalSuffix(
				fragmentCollectionName, groupName);
		}

		return HtmlUtil.escape(fragmentCollectionName);
	}

	public Map<String, Object> getFragmentCollectionsViewContext()
		throws Exception {

		return HashMapBuilder.<String, Object>put(
			"deleteFragmentCollectionURL",
			() -> {
				LiferayPortletURL deleteFragmentCollectionURL =
					_renderResponse.createActionURL();

				deleteFragmentCollectionURL.setCopyCurrentRenderParameters(
					false);
				deleteFragmentCollectionURL.setParameter(
					ActionRequest.ACTION_NAME,
					"/fragment/delete_fragment_collection");

				return deleteFragmentCollectionURL.toString();
			}
		).put(
			"exportFragmentCollectionsURL",
			() -> {
				LiferayPortletURL exportFragmentCollectionsURL =
					(LiferayPortletURL)_renderResponse.createResourceURL();

				exportFragmentCollectionsURL.setCopyCurrentRenderParameters(
					false);
				exportFragmentCollectionsURL.setResourceID(
					"/fragment/export_fragment_collections");

				return exportFragmentCollectionsURL.toString();
			}
		).put(
			"importURL",
			() -> PortletURLBuilder.createActionURL(
				_renderResponse
			).setActionName(
				"/fragment/import"
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).setPortletResource(
				() -> {
					PortletDisplay portletDisplay =
						_themeDisplay.getPortletDisplay();

					return portletDisplay.getId();
				}
			).buildString()
		).put(
			"viewDeleteFragmentCollectionsURL",
			() -> PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCRenderCommandName(
				"/fragment/view_fragment_collections"
			).setParameter(
				"includeMarketplaceFragmentCollections", true
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"viewExportFragmentCollectionsURL",
			() -> PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCRenderCommandName(
				"/fragment/view_fragment_collections"
			).setParameter(
				"includeGlobalFragmentCollections", true
			).setParameter(
				"includeMarketplaceFragmentCollections", false
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"viewImportURL",
			() -> PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCRenderCommandName(
				"/fragment/view_import"
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).build();
	}

	public SearchContainer<Object> getFragmentEntriesSearchContainer() {
		if (_fragmentEntriesSearchContainer != null) {
			return _fragmentEntriesSearchContainer;
		}

		SearchContainer<Object> fragmentEntriesSearchContainer =
			new SearchContainer(
				_renderRequest, _getPortletURL(), null,
				"there-are-no-fragments");

		fragmentEntriesSearchContainer.setId(
			"fragmentEntries" + getFragmentCollectionId());
		fragmentEntriesSearchContainer.setOrderByCol(_getOrderByCol());
		fragmentEntriesSearchContainer.setOrderByComparator(
			FragmentPortletUtil.getFragmentCompositionAndEntryOrderByComparator(
				_getOrderByCol(), getOrderByType()));
		fragmentEntriesSearchContainer.setOrderByType(getOrderByType());

		FragmentCollection fragmentCollection = getFragmentCollection();

		int status = WorkflowConstants.STATUS_ANY;

		if (fragmentCollection.getGroupId() !=
				_themeDisplay.getScopeGroupId()) {

			status = WorkflowConstants.STATUS_APPROVED;
		}

		int fragmentEntryStatus = status;

		if (isSearch()) {
			fragmentEntriesSearchContainer.setResultsAndTotal(
				() ->
					FragmentEntryServiceUtil.
						getFragmentCompositionsAndFragmentEntries(
							fragmentCollection.getGroupId(),
							fragmentCollection.getFragmentCollectionId(),
							_getKeywords(), fragmentEntryStatus,
							fragmentEntriesSearchContainer.getStart(),
							fragmentEntriesSearchContainer.getEnd(),
							fragmentEntriesSearchContainer.
								getOrderByComparator()),
				FragmentEntryServiceUtil.
					getFragmentCompositionsAndFragmentEntriesCount(
						fragmentCollection.getGroupId(),
						fragmentCollection.getFragmentCollectionId(),
						_getKeywords(), fragmentEntryStatus));
		}
		else {
			fragmentEntriesSearchContainer.setResultsAndTotal(
				() ->
					FragmentEntryServiceUtil.
						getFragmentCompositionsAndFragmentEntries(
							fragmentCollection.getGroupId(),
							fragmentCollection.getFragmentCollectionId(),
							fragmentEntryStatus,
							fragmentEntriesSearchContainer.getStart(),
							fragmentEntriesSearchContainer.getEnd(),
							fragmentEntriesSearchContainer.
								getOrderByComparator()),
				FragmentEntryServiceUtil.
					getFragmentCompositionsAndFragmentEntriesCount(
						fragmentCollection.getGroupId(),
						fragmentCollection.getFragmentCollectionId(),
						fragmentEntryStatus));
		}

		fragmentEntriesSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_fragmentEntriesSearchContainer = fragmentEntriesSearchContainer;

		return _fragmentEntriesSearchContainer;
	}

	public String getFragmentType() {
		if (_isScopeGroup()) {
			return FragmentTypeConstants.BASIC_FRAGMENT_TYPE;
		}

		return FragmentTypeConstants.INHERITED_FRAGMENT_TYPE;
	}

	public String getGroupName(long groupId) throws PortalException {
		Group group = GroupLocalServiceUtil.getGroup(groupId);

		return group.getDescriptiveName(_themeDisplay.getLocale());
	}

	public Map<String, Object> getMarketplaceProps() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"body",
			LanguageUtil.get(
				_httpServletRequest,
				"we-are-excited-to-share-that-marketplace-is-now-part-of-" +
					"fragments")
		).put(
			"fragmentPortletNamespace", _renderResponse.getNamespace()
		).put(
			"fragmentsImportURL",
			() -> {
				ResourceURL importURL = _renderResponse.createResourceURL();

				importURL.setParameter(
					"fragmentCollectionId",
					ParamUtil.getString(
						_httpServletRequest, "fragmentCollectionId"));
				importURL.setResourceID("/fragment/import");

				return importURL.toString();
			}
		).put(
			"heading",
			LanguageUtil.get(
				_httpServletRequest, "marketplace-is-now-in-fragments")
		).put(
			"permissions",
			HashMapBuilder.<String, Object>put(
				"installFreeApps",
				PortletPermissionUtil.contains(
					_themeDisplay.getPermissionChecker(),
					MarketplacePortletKeys.FRAGMENTS,
					MarketplaceActionKeys.INSTALL_FREE_BUNDLED_APPS)
			).put(
				"manageFragmentsEntries",
				() -> FragmentPermission.contains(
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(),
					FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES)
			).put(
				"purchaseAndInstallPaidApps",
				PortletPermissionUtil.contains(
					_themeDisplay.getPermissionChecker(),
					MarketplacePortletKeys.FRAGMENTS,
					MarketplaceActionKeys.PURCHASE_AND_INSTALL_PAID_APPS)
			).build()
		).build();
	}

	public List<NavigationItem> getNavigationItems() {
		if (!_isShowResourcesTab()) {
			return Collections.emptyList();
		}

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(_getTabs1(), "fragments"));
				navigationItem.setHref(_getPortletURL(), "tabs1", "fragments");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "fragments"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(_getTabs1(), "resources"));
				navigationItem.setHref(_getPortletURL(), "tabs1", "resources");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "resources"));
			}
		).build();
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, FragmentPortletKeys.FRAGMENT,
			"fragment-order-by-type", "asc");

		return _orderByType;
	}

	public String getRedirect() {
		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setParameter(
			"fragmentCollectionId",
			() -> {
				if (getFragmentCollectionId() > 0) {
					return getFragmentCollectionId();
				}

				return null;
			}
		).buildString();
	}

	public VerticalNavItemList getVerticalNavItemList(
		List<FragmentCollection> fragmentCollections) {

		VerticalNavItemList verticalNavItemList = new VerticalNavItemList();

		for (FragmentCollection fragmentCollection : fragmentCollections) {
			verticalNavItemList.add(
				verticalNavItem -> {
					if (isLocked(fragmentCollection)) {
						verticalNavItem.addIcon(
							IconItem.of("lock", StringPool.BLANK));
					}

					verticalNavItem.setActive(
						fragmentCollection.getFragmentCollectionId() ==
							getFragmentCollectionId());

					Long fragmentCollectionId =
						fragmentCollection.getFragmentCollectionId();

					verticalNavItem.setHref(
						PortletURLBuilder.createRenderURL(
							_renderResponse
						).setParameter(
							"fragmentCollectionId", fragmentCollectionId
						).buildString());
					verticalNavItem.setId(String.valueOf(fragmentCollectionId));

					verticalNavItem.setLabel(fragmentCollection.getName());
				});
		}

		return verticalNavItemList;
	}

	public VerticalNavItemList getVerticalNavItemList(
		List<FragmentCollection> fragmentCollections,
		List<FragmentCollectionContributor> fragmentCollectionContributors) {

		VerticalNavItemList verticalNavItemList = new VerticalNavItemList();

		for (FragmentCollectionContributor fragmentCollectionContributor :
				fragmentCollectionContributors) {

			verticalNavItemList.add(
				verticalNavItem -> {
					verticalNavItem.addIcon(
						IconItem.of("lock", StringPool.BLANK));
					verticalNavItem.setActive(
						Objects.equals(
							fragmentCollectionContributor.
								getFragmentCollectionKey(),
							getFragmentCollectionKey()));
					verticalNavItem.setDeprecated(
						fragmentCollectionContributor.isDeprecated());

					String fragmentCollectionKey =
						fragmentCollectionContributor.
							getFragmentCollectionKey();

					verticalNavItem.setHref(
						PortletURLBuilder.createRenderURL(
							_renderResponse
						).setParameter(
							"fragmentCollectionKey", fragmentCollectionKey
						).buildString());
					verticalNavItem.setId(fragmentCollectionKey);

					verticalNavItem.setLabel(
						fragmentCollectionContributor.getName(
							_themeDisplay.getLocale()));
				});
		}

		verticalNavItemList.addAll(getVerticalNavItemList(fragmentCollections));

		return verticalNavItemList;
	}

	public boolean hasDeletePermission() {
		if (hasUpdatePermission() ||
			(FragmentPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES) &&
			 !isLocked(getFragmentCollection()))) {

			return true;
		}

		return false;
	}

	public boolean hasUpdatePermission() {
		if (_updatePermission != null) {
			return _updatePermission;
		}

		_updatePermission = false;

		if (FragmentPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES) &&
			_isScopeGroup()) {

			_updatePermission = true;
		}

		return _updatePermission;
	}

	public boolean isLocked(FragmentCollection fragmentCollection) {
		if ((fragmentCollection.getGroupId() != CompanyConstants.SYSTEM) &&
			(fragmentCollection.getGroupId() !=
				_themeDisplay.getScopeGroupId())) {

			return true;
		}

		Group scopeGroup = _themeDisplay.getScopeGroup();

		if ((fragmentCollection.getGroupId() == CompanyConstants.SYSTEM) &&
			((_themeDisplay.getCompanyId() !=
				PortalInstancePool.getDefaultCompanyId()) ||
			 !scopeGroup.isCompany())) {

			return true;
		}

		return false;
	}

	public boolean isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	public boolean isSelectedFragmentCollectionContributor() {
		return Validator.isNotNull(getFragmentCollectionKey());
	}

	public boolean isShowFragmentCollectionActions() {
		return !isSelectedFragmentCollectionContributor();
	}

	public boolean isShowMarketplace() throws PortalException {
		if (PortletPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				MarketplacePortletKeys.FRAGMENTS,
				MarketplaceActionKeys.INSTALL_FREE_BUNDLED_APPS) ||
			PortletPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				MarketplacePortletKeys.FRAGMENTS,
				MarketplaceActionKeys.PURCHASE_AND_INSTALL_PAID_APPS)) {

			return true;
		}

		return PortletPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(),
			MarketplacePortletKeys.FRAGMENTS, MarketplaceActionKeys.VIEW_APPS);
	}

	public boolean isViewResources() {
		if (Objects.equals(_getTabs1(), "resources") && _isScopeGroup()) {
			return true;
		}

		return false;
	}

	private long _getDefaultFragmentCollectionId() {
		if (Validator.isNotNull(_getSelectedFragmentCollectionKey())) {
			return 0;
		}

		List<FragmentCollection> fragmentCollections =
			FragmentCollectionLocalServiceUtil.getFragmentCollections(
				_themeDisplay.getScopeGroupId(), 0, 1);

		if (ListUtil.isNotEmpty(fragmentCollections)) {
			FragmentCollection fragmentCollection = fragmentCollections.get(0);

			return fragmentCollection.getFragmentCollectionId();
		}

		fragmentCollections =
			FragmentCollectionLocalServiceUtil.getFragmentCollections(
				_themeDisplay.getCompanyGroupId(), 0, 1);

		if (ListUtil.isNotEmpty(fragmentCollections)) {
			FragmentCollection fragmentCollection = fragmentCollections.get(0);

			return fragmentCollection.getFragmentCollectionId();
		}

		return 0;
	}

	private String _getDefaultFragmentCollectionKey() {
		if ((_getSelectedFragmentCollectionId() > 0) ||
			(_getDefaultFragmentCollectionId() > 0)) {

			return StringPool.BLANK;
		}

		List<FragmentCollectionContributor> fragmentCollectionContributors =
			_fragmentCollectionContributorRegistry.
				getFragmentCollectionContributors();

		if (ListUtil.isEmpty(fragmentCollectionContributors)) {
			return StringPool.BLANK;
		}

		fragmentCollectionContributors = ListUtil.sort(
			fragmentCollectionContributors,
			new FragmentCollectionContributorNameComparator(
				_themeDisplay.getLocale()));

		FragmentCollectionContributor fragmentCollectionContributor =
			fragmentCollectionContributors.get(0);

		return fragmentCollectionContributor.getFragmentCollectionKey();
	}

	private FragmentCollectionContributor _getFragmentCollectionContributor() {
		return _fragmentCollectionContributorRegistry.
			getFragmentCollectionContributor(getFragmentCollectionKey());
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private String _getName(Object object) {
		if (object instanceof FragmentComposition) {
			FragmentComposition fragmentComposition =
				(FragmentComposition)object;

			return fragmentComposition.getName();
		}

		FragmentEntry fragmentEntry = (FragmentEntry)object;

		return fragmentEntry.getName();
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

	private PortletURL _getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setKeywords(
			() -> {
				String keywords = _getKeywords();

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).setParameter(
			"fragmentCollectionId",
			() -> {
				long fragmentCollectionId = getFragmentCollectionId();

				if (fragmentCollectionId > 0) {
					return fragmentCollectionId;
				}

				return null;
			}
		).setParameter(
			"fragmentCollectionKey",
			() -> {
				String fragmentCollectionKey = getFragmentCollectionKey();

				if (Validator.isNotNull(fragmentCollectionKey)) {
					return fragmentCollectionKey;
				}

				return null;
			}
		).setParameter(
			"orderByCol",
			() -> {
				String orderByCol = _getOrderByCol();

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

	private long _getSelectedFragmentCollectionId() {
		return ParamUtil.getLong(_httpServletRequest, "fragmentCollectionId");
	}

	private String _getSelectedFragmentCollectionKey() {
		return ParamUtil.getString(
			_httpServletRequest, "fragmentCollectionKey");
	}

	private String _getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(_httpServletRequest, "tabs1", "fragments");

		return _tabs1;
	}

	private boolean _isMarketplace(Object object) {
		if (object instanceof FragmentComposition) {
			FragmentComposition fragmentComposition =
				(FragmentComposition)object;

			return fragmentComposition.isMarketplace();
		}

		FragmentEntry fragmentEntry = (FragmentEntry)object;

		return fragmentEntry.isMarketplace();
	}

	private boolean _isScopeGroup() {
		FragmentCollection fragmentCollection = getFragmentCollection();

		if (fragmentCollection.getGroupId() ==
				_themeDisplay.getScopeGroupId()) {

			return true;
		}

		return false;
	}

	private boolean _isShowResourcesTab() {
		if (isSelectedFragmentCollectionContributor() || !_isScopeGroup()) {
			return false;
		}

		return true;
	}

	private SearchContainer<Object> _contributedEntriesSearchContainer;
	private FragmentCollection _fragmentCollection;
	private final FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;
	private Long _fragmentCollectionId;
	private String _fragmentCollectionKey;
	private SearchContainer<Object> _fragmentEntriesSearchContainer;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private String _tabs1;
	private final ThemeDisplay _themeDisplay;
	private Boolean _updatePermission;

}