/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSpaceConstants;
import com.liferay.site.cms.site.initializer.internal.util.SpaceSummaryHeaderUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Roberto Díaz
 */
public class ViewSpaceSitesSummarySectionDisplayContext {

	public ViewSpaceSitesSummarySectionDisplayContext(
		DepotEntryService depotEntryService,
		DepotEntryGroupRelLocalService depotEntryGroupRelLocalService,
		String externalReferenceCode, long groupId,
		HttpServletRequest httpServletRequest, Language language,
		ModelResourcePermission<User> userModelResourcePermission) {

		_depotEntryService = depotEntryService;
		_depotEntryGroupRelLocalService = depotEntryGroupRelLocalService;
		_externalReferenceCode = externalReferenceCode;
		_groupId = groupId;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_userModelResourcePermission = userModelResourcePermission;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		return StringBundler.concat(
			"/o/headless-asset-library/v1.0/asset-libraries",
			"/by-external-reference-code/", _externalReferenceCode,
			"/connected-sites?page=", CMSSpaceConstants.SPACE_SUMMARY_PAGE,
			"&pageSize=", CMSSpaceConstants.SPACE_SUMMARY_PAGE_SIZE);
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "connectSites");
				dropdownItem.putData(
					"externalReferenceCode", _externalReferenceCode);
				dropdownItem.putData("groupId", _groupId);
				dropdownItem.putData("title", _getSpaceSitesHeaderTitle());
				dropdownItem.setLabel(
					_language.get(_httpServletRequest, "connect-sites"));
			}
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			_language.get(_httpServletRequest, "connect-sites-to-this-space")
		).put(
			"image", "/states/cms_empty_state.svg"
		).put(
			"title",
			_language.get(_httpServletRequest, "no-connected-sites-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			_getSearchableFDSActionDropdownItem(true),
			_getSearchableFDSActionDropdownItem(false),
			new FDSActionDropdownItem(
				StringBundler.concat(
					"/o/headless-asset-library/v1.0/asset-libraries",
					"/by-external-reference-code/", _externalReferenceCode,
					"/connected-sites/by-external-reference-code",
					"/{externalReferenceCode}"),
				null, "delete",
				_language.get(_httpServletRequest, "disconnect"), "delete",
				null, "headless"));
	}

	public Map<String, Object> getHeaderProps() throws Exception {
		return SpaceSummaryHeaderUtil.getSpaceSummaryHeaderProps(
			_httpServletRequest, "view-all-sites",
			HashMapBuilder.<String, Object>put(
				"hasConnectSitesPermission", _hasConnectSitesPermission()
			).build(),
			HashMapBuilder.<String, Object>put(
				"action", "open-sites-modal"
			).put(
				"externalReferenceCode", _externalReferenceCode
			).build(),
			_getSpaceSitesHeaderTitle(), StringPool.BLANK);
	}

	private FDSActionDropdownItem _getSearchableFDSActionDropdownItem(
		boolean searchable) {

		FDSActionDropdownItem fdsActionDropdownItem = new FDSActionDropdownItem(
			StringBundler.concat(
				"/o/headless-asset-library/v1.0/asset-libraries",
				"/by-external-reference-code/", _externalReferenceCode,
				"/connected-sites/by-external-reference-code",
				"/{externalReferenceCode}"),
			null, searchable ? "make-searchable" : "make-unsearchable",
			_language.get(
				_httpServletRequest,
				searchable ? "make-searchable" : "make-unsearchable"),
			"put", null, "headless");

		fdsActionDropdownItem.setRequestBody(
			"{\"searchable\": " + searchable + "}");

		return fdsActionDropdownItem;
	}

	private String _getSpaceSitesHeaderTitle() throws Exception {
		return StringBundler.concat(
			_language.get(_httpServletRequest, "sites"), StringPool.SPACE,
			StringPool.OPEN_PARENTHESIS,
			_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
				_depotEntryService.getGroupDepotEntry(_groupId)),
			StringPool.CLOSE_PARENTHESIS);
	}

	private boolean _hasConnectSitesPermission() throws Exception {
		return _userModelResourcePermission.contains(
			_themeDisplay.getPermissionChecker(), _themeDisplay.getUserId(),
			ActionKeys.UPDATE);
	}

	private final DepotEntryGroupRelLocalService
		_depotEntryGroupRelLocalService;
	private final DepotEntryService _depotEntryService;
	private final String _externalReferenceCode;
	private final long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final ThemeDisplay _themeDisplay;
	private final ModelResourcePermission<User> _userModelResourcePermission;

}