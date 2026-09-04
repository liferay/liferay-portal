/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;
import com.liferay.site.cms.site.initializer.internal.util.CommentUtil;
import com.liferay.site.cms.site.initializer.internal.util.PermissionUtil;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Adriano Interaminense
 */
public class ViewDashboardDisplayContext {

	public ViewDashboardDisplayContext(
		AnalyticsSettingsManager analyticsSettingsManager,
		DepotEntryLocalService depotEntryLocalService,
		ModelResourcePermission<DepotEntry> depotEntryModelResourcePermission,
		DepotEntryService depotEntryService, DLConfiguration dlConfiguration,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest,
		ObjectDefinitionService objectDefinitionService,
		RoleLocalService roleLocalService, ThemeDisplay themeDisplay,
		TranslationInfoItemFieldValuesExporterRegistry
			translationInfoItemFieldValuesExporterRegistry) {

		_analyticsSettingsManager = analyticsSettingsManager;
		_depotEntryLocalService = depotEntryLocalService;
		_depotEntryModelResourcePermission = depotEntryModelResourcePermission;
		_depotEntryService = depotEntryService;
		_dlConfiguration = dlConfiguration;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_objectDefinitionService = objectDefinitionService;
		_roleLocalService = roleLocalService;
		_themeDisplay = themeDisplay;
		_translationInfoItemFieldValuesExporterRegistry =
			translationInfoItemFieldValuesExporterRegistry;
	}

	public Map<String, Object> getConstants() {
		return HashMapBuilder.<String, Object>put(
			"cmsGroupId", () -> _getCMSGroupId()
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
			"admin", () -> _hasUserRole(RoleConstants.ADMINISTRATOR)
		).put(
			"administeredSpaceIds", () -> _getAvailableDepotEntryIds()
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
			"allSectionFDSName", CMSSiteInitializerFDSNames.ALL_SECTION
		).put(
			"assetLibraries",
			SectionDisplayContextUtil.getDepotEntriesJSONArray(
				_httpServletRequest)
		).put(
			"autocompleteURL", SectionDisplayContextUtil.getAutocompleteURL()
		).put(
			"availableExportFileFormats",
			() -> TransformUtil.transform(
				_translationInfoItemFieldValuesExporterRegistry.
					getTranslationInfoItemFieldValuesExporters(),
				translationInfoItemFieldValuesExporter ->
					SectionDisplayContextUtil.getExportFileFormatJSONObject(
						_themeDisplay, translationInfoItemFieldValuesExporter))
		).put(
			"availableLocales",
			SectionDisplayContextUtil.getLocalesJSONArray(
				_themeDisplay.getLocale(),
				LanguageUtil.getAvailableLocales(
					_themeDisplay.getSiteGroupId()))
		).put(
			"breadcrumbProps",
			HashMapBuilder.<String, Object>put(
				"breadcrumbItems",
				JSONUtil.putAll(
					JSONUtil.put(
						"active", false
					).put(
						"label",
						SectionDisplayContextUtil.getLayoutName(_themeDisplay)
					))
			).put(
				"hideSpace", true
			).build()
		).put(
			"brokenLinksCheckerEnabled",
			GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.CMS_BROKEN_LINKS_CHECKER_ENABLED))
		).put(
			"candidateAssetLibraries",
			SectionDisplayContextUtil.getDepotEntriesJSONArray(
				_httpServletRequest)
		).put(
			"cmpEnabled", LicenseManagerUtil.isAppEnabled(App.CMP)
		).put(
			"cmsGroupId", () -> _getCMSGroupId()
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
			"contentProgressFilter",
			SectionDisplayContextUtil.getContentProgressFilterString(
				_httpServletRequest)
		).put(
			"contentViewURL",
			SectionDisplayContextUtil.getContentViewURL(_themeDisplay)
		).put(
			"defaultPermissionAdditionalProps",
			PermissionUtil.getDefaultPermissionAdditionalProps(
				_httpServletRequest, _themeDisplay)
		).put(
			"expiringSoonFDSName",
			CMSSiteInitializerFDSNames.EXPIRING_SOON_SECTION
		).put(
			"expiringSoonFilterString",
			SectionDisplayContextUtil.getExpiringSoonFilterString(
				_httpServletRequest)
		).put(
			"fdsActionDropdownItems",
			() ->
				SectionDisplayContextUtil.getNeedsReviewFDSActionDropdownItems(
					_httpServletRequest)
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
			"parentObjectEntryFolderExternalReferenceCode",
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS
		).put(
			"redirect", _themeDisplay.getURLCurrent()
		).put(
			"upcomingReviewsFDSName",
			CMSSiteInitializerFDSNames.UPCOMING_REVIEWS_SECTION
		).put(
			"upcomingReviewsFilterString",
			SectionDisplayContextUtil.getUpcomingReviewsFilterString(
				_httpServletRequest)
		).build();
	}

	private List<String> _getAvailableDepotEntryIds() throws PortalException {
		return TransformUtil.transform(
			_depotEntryService.getDepotEntryGroupIds(
				_themeDisplay.getCompanyId(), _themeDisplay.getUserId(),
				DepotConstants.TYPE_SPACE),
			groupId -> {
				DepotEntry depotEntry =
					_depotEntryLocalService.fetchGroupDepotEntry(groupId);

				if ((depotEntry != null) &&
					_depotEntryModelResourcePermission.contains(
						_themeDisplay.getPermissionChecker(), depotEntry,
						ActionKeys.VIEW_SITE_ADMINISTRATION)) {

					return String.valueOf(depotEntry.getDepotEntryId());
				}

				return null;
			});
	}

	private Long _getCMSGroupId() {
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

	private boolean _hasUserRole(String roleName) throws PortalException {
		return _roleLocalService.hasUserRole(
			_themeDisplay.getUserId(), _themeDisplay.getCompanyId(), roleName,
			true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewDashboardDisplayContext.class);

	private final AnalyticsSettingsManager _analyticsSettingsManager;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final ModelResourcePermission<DepotEntry>
		_depotEntryModelResourcePermission;
	private final DepotEntryService _depotEntryService;
	private final DLConfiguration _dlConfiguration;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinitionService _objectDefinitionService;
	private final RoleLocalService _roleLocalService;
	private final ThemeDisplay _themeDisplay;
	private final TranslationInfoItemFieldValuesExporterRegistry
		_translationInfoItemFieldValuesExporterRegistry;

}