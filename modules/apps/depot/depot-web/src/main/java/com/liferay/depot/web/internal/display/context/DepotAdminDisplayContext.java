/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.display.context;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.search.DepotEntrySearch;
import com.liferay.depot.service.DepotEntryGroupRelServiceUtil;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.depot.service.DepotEntryServiceUtil;
import com.liferay.depot.web.internal.frontend.taglib.clay.servlet.taglib.DepotEntryVerticalCard;
import com.liferay.depot.web.internal.servlet.taglib.util.DepotActionDropdownItemsProvider;
import com.liferay.depot.web.internal.util.DepotEntryAdminSearchUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import java.util.List;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @author Roberto Díaz
 */
public class DepotAdminDisplayContext {

	public DepotAdminDisplayContext(
		DepotEntryService depotEntryService, Language language,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_depotEntryService = depotEntryService;
		_language = language;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public List<DropdownItem> getActionDropdownItems(DepotEntry depotEntry) {
		DepotActionDropdownItemsProvider depotActionDropdownItemsProvider =
			new DepotActionDropdownItemsProvider(
				depotEntry, _liferayPortletRequest, _liferayPortletResponse);

		return depotActionDropdownItemsProvider.getActionDropdownItems();
	}

	public String getDefaultDisplayStyle() {
		return "icon";
	}

	public DepotEntry getDepotEntry(DepotEntry depotEntry)
		throws PortalException {

		Group group = depotEntry.getGroup();

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (stagingGroupHelper.isLocalStagingGroup(group) ||
			stagingGroupHelper.isRemoteStagingGroup(group)) {

			return depotEntry;
		}

		Group localStagingGroup = stagingGroupHelper.fetchLocalStagingGroup(
			group);

		if (localStagingGroup == null) {
			return depotEntry;
		}

		DepotEntry stagingGroupDepotEntry =
			DepotEntryServiceUtil.fetchGroupDepotEntry(
				localStagingGroup.getGroupId());

		if (stagingGroupDepotEntry == null) {
			return depotEntry;
		}

		return stagingGroupDepotEntry;
	}

	public int getDepotEntryConnectedGroupsCount(DepotEntry depotEntry)
		throws PortalException {

		return DepotEntryGroupRelServiceUtil.getDepotEntryGroupRelsCount(
			depotEntry);
	}

	public DepotEntryVerticalCard getDepotEntryVerticalCard(
			DepotEntry depotEntry)
		throws PortalException {

		SearchContainer<DepotEntry> searchContainer = searchContainer();

		return new DepotEntryVerticalCard(
			depotEntry, _liferayPortletRequest, _liferayPortletResponse,
			searchContainer.getRowChecker());
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_liferayPortletRequest, DepotPortletKeys.DEPOT_ADMIN,
			getDefaultDisplayStyle());

		return _displayStyle;
	}

	public PortletURL getIteratorURL() throws PortalException {
		SearchContainer<DepotEntry> searchContainer = searchContainer();

		return searchContainer.getIteratorURL();
	}

	public String getSearchContainerId() {
		return "depotEntries";
	}

	public String getViewDepotURL(DepotEntry depotEntry)
		throws PortalException {

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_liferayPortletRequest, depotEntry.getGroup(),
				DepotPortletKeys.DEPOT_ADMIN, 0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/depot/view_depot_dashboard"
		).setParameter(
			"depotEntryId", depotEntry.getDepotEntryId()
		).buildString();
	}

	public boolean isDisplayStyleDescriptive() {
		return Objects.equals(getDisplayStyle(), "descriptive");
	}

	public boolean isDisplayStyleIcon() {
		return Objects.equals(getDisplayStyle(), "icon");
	}

	public SearchContainer<DepotEntry> searchContainer()
		throws PortalException {

		if (_depotEntrySearch != null) {
			return _depotEntrySearch;
		}

		PortletRequest portletRequest =
			(PortletRequest)_liferayPortletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)_liferayPortletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		_depotEntrySearch = DepotEntryAdminSearchUtil.getDepotEntrySearch(
			_depotEntryService, DepotConstants.TYPE_ASSET_LIBRARY, _language,
			portletRequest, portletResponse, _getPortletURL());

		return _depotEntrySearch;
	}

	private PortletURL _getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setParameter(
			"displayStyle", getDisplayStyle()
		).buildPortletURL();
	}

	private DepotEntrySearch _depotEntrySearch;
	private final DepotEntryService _depotEntryService;
	private String _displayStyle;
	private final Language _language;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}