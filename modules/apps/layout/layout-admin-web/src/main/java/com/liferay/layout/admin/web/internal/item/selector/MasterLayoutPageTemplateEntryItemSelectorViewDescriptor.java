/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.item.selector;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.criteria.AssetEntryItemSelectorReturnType;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.layout.page.template.util.LayoutPageTemplateEntryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class MasterLayoutPageTemplateEntryItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<LayoutPageTemplateEntry> {

	public MasterLayoutPageTemplateEntryItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest, PortletURL portletURL) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Group group = _themeDisplay.getRefererGroup();

		if (group == null) {
			group = _themeDisplay.getScopeGroup();
		}

		if (group.isLayoutPrototype()) {
			_designLibraryGroupIds = new long[0];

			LayoutPageTemplateEntry layoutPrototypeLayoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchFirstLayoutPageTemplateEntry(group.getClassPK());

			_scopeGroupId = layoutPrototypeLayoutPageTemplateEntry.getGroupId();
		}
		else {
			_designLibraryGroupIds =
				LayoutPageTemplateEntryUtil.
					fetchMasterLayoutDesignLibraryGroupIds(
						group.getCompanyId(), group.getGroupId());
			_scopeGroupId = group.getGroupId();
		}

		_defaultLayoutPageTemplateEntry =
			LayoutPageTemplateEntryUtil.
				fetchDefaultMasterLayoutPageTemplateEntry(
					_designLibraryGroupIds, group.getGroupId());
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "icon";
	}

	@Override
	public String[] getDisplayViews() {
		return new String[] {"icon"};
	}

	@Override
	public String[] getGroupTypes() {
		return new String[] {"design-library", "site"};
	}

	@Override
	public ItemDescriptor getItemDescriptor(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return new MasterLayoutPageTemplateEntryItemDescriptor(
			_isDefaultTemplate(layoutPageTemplateEntry),
			layoutPageTemplateEntry, _scopeGroupId);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new AssetEntryItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"name", "create-date"};
	}

	@Override
	public SearchContainer<LayoutPageTemplateEntry> getSearchContainer()
		throws PortalException {

		SearchContainer<LayoutPageTemplateEntry>
			masterLayoutPageTemplateEntrySearchContainer =
				new SearchContainer<>(
					(PortletRequest)_httpServletRequest.getAttribute(
						JavaConstants.JAKARTA_PORTLET_REQUEST),
					_portletURL, null, "there-are-no-master-pages");

		long scopeGroupId = _themeDisplay.getScopeGroupId();

		if (ArrayUtil.contains(_designLibraryGroupIds, scopeGroupId)) {
			masterLayoutPageTemplateEntrySearchContainer.setResultsAndTotal(
				LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntries(
					scopeGroupId,
					LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null));

			return masterLayoutPageTemplateEntrySearchContainer;
		}

		long refererGroupId = _themeDisplay.getRefererGroupId();

		if ((refererGroupId > 0) && (refererGroupId != scopeGroupId)) {
			return masterLayoutPageTemplateEntrySearchContainer;
		}

		List<LayoutPageTemplateEntry> masterLayoutPageTemplateEntries =
			new ArrayList<>();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				createLayoutPageTemplateEntry(0);

		layoutPageTemplateEntry.setName(
			LanguageUtil.get(_httpServletRequest, "blank"));

		if (_defaultLayoutPageTemplateEntry == null) {
			layoutPageTemplateEntry.setDefaultTemplate(true);
		}

		layoutPageTemplateEntry.setStatus(WorkflowConstants.STATUS_APPROVED);

		masterLayoutPageTemplateEntries.add(layoutPageTemplateEntry);

		masterLayoutPageTemplateEntries.addAll(
			LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntries(
				_scopeGroupId,
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));

		masterLayoutPageTemplateEntrySearchContainer.setResultsAndTotal(
			masterLayoutPageTemplateEntries);

		return masterLayoutPageTemplateEntrySearchContainer;
	}

	@Override
	public boolean isScopeGroupOnly() {
		return true;
	}

	@Override
	public boolean isShowBreadcrumb() {
		return ArrayUtil.isNotEmpty(_designLibraryGroupIds);
	}

	@Override
	public boolean isShowManagementToolbar() {
		return false;
	}

	private boolean _isDefaultTemplate(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		if ((layoutPageTemplateEntry.getGroupId() == 0) ||
			(layoutPageTemplateEntry.getGroupId() == _scopeGroupId)) {

			return layoutPageTemplateEntry.isDefaultTemplate();
		}

		if ((_defaultLayoutPageTemplateEntry != null) &&
			(_defaultLayoutPageTemplateEntry.getLayoutPageTemplateEntryId() ==
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId())) {

			return true;
		}

		return false;
	}

	private final LayoutPageTemplateEntry _defaultLayoutPageTemplateEntry;
	private final long[] _designLibraryGroupIds;
	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private final long _scopeGroupId;
	private final ThemeDisplay _themeDisplay;

}