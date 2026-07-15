/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.site.cms.site.initializer.internal.util.CommentUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Adriano Interaminense
 */
public class ViewDashboardDisplayContext {

	public ViewDashboardDisplayContext(
		AnalyticsSettingsManager analyticsSettingsManager,
		DLConfiguration dlConfiguration, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest,
		ObjectDefinitionService objectDefinitionService,
		RoleLocalService roleLocalService, ThemeDisplay themeDisplay) {

		_analyticsSettingsManager = analyticsSettingsManager;
		_dlConfiguration = dlConfiguration;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_objectDefinitionService = objectDefinitionService;
		_roleLocalService = roleLocalService;
		_themeDisplay = themeDisplay;
	}

	public Map<String, Object> getConstants() {
		return HashMapBuilder.<String, Object>put(
			"cmsGroupId",
			() -> {
				try {
					Group group = _groupLocalService.getGroup(
						_themeDisplay.getCompanyId(), GroupConstants.CMS);

					return group.getGroupId();
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}

				return null;
			}
		).put(
			"ercContentStructures",
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES
		).put(
			"ercFileTypes",
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
		).build();
	}

	public Map<String, Object> getReactData() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"additionalProps", _getAdditionalProps()
		).put(
			"admin",
			() -> _roleLocalService.hasUserRole(
				_themeDisplay.getUserId(), _themeDisplay.getCompanyId(),
				RoleConstants.ADMINISTRATOR, true)
		).put(
			"analyticsEnabled",
			() -> {
				try {
					return _analyticsSettingsManager.isAnalyticsEnabled(
						_themeDisplay.getCompanyId());
				}
				catch (Exception exception) {
					_log.error(exception);

					return false;
				}
			}
		).put(
			"constants", getConstants()
		).put(
			"dashboard",
			PortalUtil.getLayoutFullURL(
				LayoutLocalServiceUtil.getLayoutByFriendlyURL(
					_themeDisplay.getScopeGroupId(), false, "/dashboard"),
				_themeDisplay)
		).put(
			"freeTier", LicenseManagerUtil.isFreeTier()
		).put(
			"learnResources",
			LearnMessageUtil.getReactDataJSONObject("site-cms-site-initializer")
		).build();
	}

	private Map<String, Object> _getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"autocompleteURL", SectionDisplayContextUtil.getAutocompleteURL()
		).put(
			"breadcrumbProps",
			HashMapBuilder.<String, Object>put(
				"breadcrumbItems",
				JSONUtil.putAll(
					JSONUtil.put(
						"active", false
					).put(
						"label", _getLayoutName()
					))
			).put(
				"hideSpace", true
			).build()
		).put(
			"candidateAssetLibraries",
			SectionDisplayContextUtil.getDepotEntriesJSONArray(
				_httpServletRequest, null)
		).put(
			"cmsGroupId",
			() -> {
				try {
					Group group = _groupLocalService.getGroup(
						_themeDisplay.getCompanyId(), GroupConstants.CMS);

					return GetterUtil.getLong(group.getGroupId());
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}
				}

				return null;
			}
		).put(
			"collaboratorURLs",
			() -> SectionDisplayContextUtil.getCollaboratorURLs(
				_themeDisplay.getCompanyId(), _objectDefinitionService,
				new String[] {
					ObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
					ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
				})
		).put(
			"commentsProps", CommentUtil.getCommentsProps(_httpServletRequest)
		).put(
			"contentViewURL",
			SectionDisplayContextUtil.getContentViewURL(_themeDisplay)
		).put(
			"fileMimeTypeCssClasses",
			() -> {
				if (_dlConfiguration == null) {
					return null;
				}

				return SectionDisplayContextUtil.getFileMimeTypeCssClasses(
					_dlConfiguration);
			}
		).put(
			"fileMimeTypeIcons",
			() -> {
				if (_dlConfiguration == null) {
					return null;
				}

				return SectionDisplayContextUtil.getFileMimeTypeIcons(
					_dlConfiguration);
			}
		).put(
			"objectDefinitionCssClasses",
			SectionDisplayContextUtil.getObjectDefinitionCssClasses()
		).put(
			"objectDefinitionIcons",
			SectionDisplayContextUtil.getObjectDefinitionIcons()
		).put(
			"redirect", _themeDisplay.getURLCurrent()
		).build();
	}

	private String _getLayoutName() {
		Layout layout = _themeDisplay.getLayout();

		if (layout == null) {
			return null;
		}

		return layout.getName(_themeDisplay.getLocale(), true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewDashboardDisplayContext.class);

	private final AnalyticsSettingsManager _analyticsSettingsManager;
	private final DLConfiguration _dlConfiguration;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinitionService _objectDefinitionService;
	private final RoleLocalService _roleLocalService;
	private final ThemeDisplay _themeDisplay;

}