/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.ManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.configuration.SharingConfiguration;
import com.liferay.sharing.configuration.SharingConfigurationFactory;
import com.liferay.sharing.display.context.util.SharingDropdownItemFactory;
import com.liferay.sharing.interpreter.SharingEntryInterpreter;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.renderer.SharingEntryEditRenderer;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.security.permission.SharingPermission;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.sharing.servlet.taglib.ui.SharingEntryDropdownItemContributor;
import com.liferay.sharing.util.comparator.SharingEntryModifiedDateComparator;
import com.liferay.sharing.web.internal.constants.SharingPortletKeys;
import com.liferay.sharing.web.internal.filter.SharedAssetsFilterItemRegistry;
import com.liferay.sharing.web.internal.servlet.taglib.ui.SharingEntryDropdownItemContributorRegistry;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Sergio González
 */
public class ViewSharedAssetsDisplayContext {

	public ViewSharedAssetsDisplayContext(
		GroupLocalService groupLocalService, ItemSelector itemSelector,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SharedAssetsFilterItemRegistry sharedAssetsFilterItemRegistry,
		SharingConfigurationFactory sharingConfigurationFactory,
		SharingDropdownItemFactory sharingDropdownItemFactory,
		SharingEntryDropdownItemContributorRegistry
			sharingEntryDropdownItemContributorRegistry,
		Function<SharingEntry, SharingEntryInterpreter>
			sharingEntryInterpreterFunction,
		SharingEntryLocalService sharingEntryLocalService,
		SharingPermission sharingPermission) {

		_groupLocalService = groupLocalService;
		_itemSelector = itemSelector;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_sharedAssetsFilterItemRegistry = sharedAssetsFilterItemRegistry;
		_sharingConfigurationFactory = sharingConfigurationFactory;
		_sharingDropdownItemFactory = sharingDropdownItemFactory;
		_sharingEntryDropdownItemContributorRegistry =
			sharingEntryDropdownItemContributorRegistry;
		_sharingEntryInterpreterFunction = sharingEntryInterpreterFunction;
		_sharingEntryLocalService = sharingEntryLocalService;
		_sharingPermission = sharingPermission;

		_currentURLObj = PortletURLUtil.getCurrent(
			liferayPortletRequest, liferayPortletResponse);

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getClassName() {
		return ParamUtil.getString(_httpServletRequest, "className");
	}

	public ManagementToolbarDisplayContext
		getManagementToolbarDisplayContext() {

		return new ViewSharedAssetsManagementToolbarDisplayContext(
			_httpServletRequest, _itemSelector, _liferayPortletRequest,
			_liferayPortletResponse, getSearchContainer(),
			_sharedAssetsFilterItemRegistry, this);
	}

	public NavigationItemList getNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(_isIncoming());
				navigationItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setParameter(
						"incoming", true
					).buildPortletURL());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "shared-with-me"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(!_isIncoming());
				navigationItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setParameter(
						"incoming", false
					).buildPortletURL());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "shared-by-me"));
			}
		).build();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, SharingPortletKeys.SHARED_ASSETS,
			"modified-date");

		return _orderByCol;
	}

	public SearchContainer<SharingEntry> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<SharingEntry> searchContainer = new SearchContainer<>(
			_liferayPortletRequest,
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).buildPortletURL(),
			null, "no-entries-were-found");

		long classNameId = ClassNameLocalServiceUtil.getClassNameId(
			ParamUtil.getString(_httpServletRequest, "className"));

		if (_isIncoming()) {
			searchContainer.setResultsAndTotal(
				() -> _sharingEntryLocalService.getToUserSharingEntries(
					_themeDisplay.getUserId(), classNameId,
					searchContainer.getStart(), searchContainer.getEnd(),
					SharingEntryModifiedDateComparator.getInstance(
						Objects.equals(getSortingOrder(), "asc"))),
				_sharingEntryLocalService.getToUserSharingEntriesCount(
					_themeDisplay.getUserId(), classNameId));
		}
		else {
			searchContainer.setResultsAndTotal(
				() -> _sharingEntryLocalService.getFromUserSharingEntries(
					_themeDisplay.getUserId(), classNameId,
					searchContainer.getStart(), searchContainer.getEnd(),
					SharingEntryModifiedDateComparator.getInstance(
						Objects.equals(getSortingOrder(), "asc"))),
				_sharingEntryLocalService.getFromUserSharingEntriesCount(
					_themeDisplay.getUserId(), classNameId));
		}

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	public String getSharingEntryAssetTypeTitle(SharingEntry sharingEntry)
		throws PortalException {

		SharingEntryInterpreter sharingEntryInterpreter =
			_sharingEntryInterpreterFunction.apply(sharingEntry);

		if (sharingEntryInterpreter == null) {
			return StringPool.BLANK;
		}

		return sharingEntryInterpreter.getAssetTypeTitle(
			sharingEntry, _themeDisplay.getLocale());
	}

	public List<DropdownItem> getSharingEntryDropdownItems(
			SharingEntry sharingEntry)
		throws PortalException {

		if (!isSharingEntryVisible(sharingEntry)) {
			return null;
		}

		SharingEntryDropdownItemContributor
			sharingEntryDropdownItemContributor =
				_sharingEntryDropdownItemContributorRegistry.
					getSharingEntryMenuItemContributor(
						sharingEntry.getClassNameId());

		return DropdownItemListBuilder.add(
			() -> _hasEditPermission(
				sharingEntry.getClassNameId(), sharingEntry.getClassPK()),
			dropdownItem -> {
				dropdownItem.setHref(
					_getURLEdit(
						sharingEntry, _liferayPortletRequest,
						_liferayPortletResponse));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
			}
		).add(
			sharingEntry::isShareable,
			_sharingDropdownItemFactory.createShareDropdownItem(
				sharingEntry.getClassName(), sharingEntry.getClassPK(),
				_httpServletRequest)
		).add(
			() -> _sharingPermission.containsManageCollaboratorsPermission(
				_themeDisplay.getPermissionChecker(),
				sharingEntry.getClassNameId(), sharingEntry.getClassPK(),
				_themeDisplay.getScopeGroupId()),
			_sharingDropdownItemFactory.createManageCollaboratorsDropdownItem(
				sharingEntry.getClassName(), sharingEntry.getClassPK(),
				_httpServletRequest)
		).addAll(
			sharingEntryDropdownItemContributor.getSharingEntryDropdownItems(
				sharingEntry, _themeDisplay)
		).build();
	}

	public PortletURL getSharingEntryRowPortletURL(SharingEntry sharingEntry)
		throws PortalException {

		if (!isSharingEntryVisible(sharingEntry)) {
			return null;
		}

		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/sharing/view_sharing_entry"
		).setRedirect(
			_currentURLObj
		).setParameter(
			"sharingEntryId", sharingEntry.getSharingEntryId()
		).buildRenderURL();
	}

	public String getSharingEntryTitle(SharingEntry sharingEntry) {
		SharingEntryInterpreter sharingEntryInterpreter =
			_sharingEntryInterpreterFunction.apply(sharingEntry);

		if (sharingEntryInterpreter == null) {
			return StringPool.BLANK;
		}

		return HtmlUtil.escape(sharingEntryInterpreter.getTitle(sharingEntry));
	}

	public String getSortingOrder() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, SharingPortletKeys.SHARED_ASSETS, "asc");

		return _orderByType;
	}

	public boolean isSharingEntryVisible(SharingEntry sharingEntry)
		throws PortalException {

		SharingEntryInterpreter sharingEntryInterpreter =
			_sharingEntryInterpreterFunction.apply(sharingEntry);

		if ((sharingEntryInterpreter == null) ||
			!sharingEntryInterpreter.isVisible(sharingEntry)) {

			return false;
		}

		SharingConfiguration groupSharingConfiguration =
			_sharingConfigurationFactory.getGroupSharingConfiguration(
				_groupLocalService.getGroup(sharingEntry.getGroupId()));

		return groupSharingConfiguration.isEnabled();
	}

	private PortletURL _getURLEdit(
			SharingEntry sharingEntry,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		SharingEntryInterpreter sharingEntryInterpreter =
			_sharingEntryInterpreterFunction.apply(sharingEntry);

		if (sharingEntryInterpreter == null) {
			return null;
		}

		SharingEntryEditRenderer sharingEntryEditRenderer =
			sharingEntryInterpreter.getSharingEntryEditRenderer();

		PortletURL portletURL = sharingEntryEditRenderer.getURLEdit(
			sharingEntry, liferayPortletRequest, liferayPortletResponse);

		if (portletURL == null) {
			return null;
		}

		portletURL.setParameter(
			"redirect", PortalUtil.getCurrentURL(_liferayPortletRequest));

		return portletURL;
	}

	private boolean _hasEditPermission(long classNameId, long classPK) {
		SharingEntry sharingEntry = _sharingEntryLocalService.fetchSharingEntry(
			_themeDisplay.getUserId(), classNameId, classPK);

		if ((sharingEntry != null) &&
			sharingEntry.hasSharingPermission(SharingEntryAction.UPDATE)) {

			return true;
		}

		return false;
	}

	private boolean _isIncoming() {
		return ParamUtil.getBoolean(_httpServletRequest, "incoming", true);
	}

	private final PortletURL _currentURLObj;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByCol;
	private String _orderByType;
	private SearchContainer<SharingEntry> _searchContainer;
	private final SharedAssetsFilterItemRegistry
		_sharedAssetsFilterItemRegistry;
	private final SharingConfigurationFactory _sharingConfigurationFactory;
	private final SharingDropdownItemFactory _sharingDropdownItemFactory;
	private final SharingEntryDropdownItemContributorRegistry
		_sharingEntryDropdownItemContributorRegistry;
	private final Function<SharingEntry, SharingEntryInterpreter>
		_sharingEntryInterpreterFunction;
	private final SharingEntryLocalService _sharingEntryLocalService;
	private final SharingPermission _sharingPermission;
	private final ThemeDisplay _themeDisplay;

}