/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;
import com.liferay.site.cms.site.initializer.internal.util.CommentUtil;
import com.liferay.site.cms.site.initializer.internal.util.PermissionUtil;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseSectionDisplayContext {

	public BaseSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		DLConfiguration dlConfiguration, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService, Portal portal,
		TranslationInfoItemFieldValuesExporterRegistry
			translationInfoItemFieldValuesExporterRegistry) {

		this.depotEntryLocalService = depotEntryLocalService;

		_dlConfiguration = dlConfiguration;

		this.groupLocalService = groupLocalService;
		this.httpServletRequest = httpServletRequest;
		this.language = language;

		_objectDefinitionService = objectDefinitionService;

		this.portal = portal;
		_translationInfoItemFieldValuesExporterRegistry =
			translationInfoItemFieldValuesExporterRegistry;

		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		objectEntryFolder = _getObjectEntryFolder(
			themeDisplay.getCompanyId(),
			httpServletRequest.getAttribute(InfoDisplayWebKeys.INFO_ITEM));
	}

	public String getAdditionalAPIURLParameters() {
		return SectionDisplayContextUtil.getAdditionalAPIURLParameters(
			getCMSSectionFilterString(), httpServletRequest,
			getRootObjectEntryFolderExternalReferenceCode());
	}

	public Map<String, Object> getAdditionalProps() {
		ObjectDefinition cmpProjectObjectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", themeDisplay.getCompanyId());

		return HashMapBuilder.<String, Object>put(
			"additionalAPIURLParameters",
			() -> {
				if (isFolderSearchEnabled()) {
					return getAdditionalAPIURLParameters();
				}

				return null;
			}
		).put(
			"assetLibraries",
			SectionDisplayContextUtil.getDepotEntriesJSONArray(
				httpServletRequest)
		).put(
			"autocompleteURL", SectionDisplayContextUtil.getAutocompleteURL()
		).put(
			"availableExportFileFormats",
			() -> TransformUtil.transform(
				_translationInfoItemFieldValuesExporterRegistry.
					getTranslationInfoItemFieldValuesExporters(),
				translationInfoItemFieldValuesExporter ->
					SectionDisplayContextUtil.getExportFileFormatJSONObject(
						themeDisplay, translationInfoItemFieldValuesExporter))
		).put(
			"availableLocales",
			SectionDisplayContextUtil.getLocalesJSONArray(
				themeDisplay.getLocale(),
				LanguageUtil.getAvailableLocales(themeDisplay.getSiteGroupId()))
		).put(
			"baseAssetLibraryViewURL", ActionUtil.getBaseSpaceURL(themeDisplay)
		).put(
			"baseFolderViewURL", ActionUtil.getBaseViewFolderURL(themeDisplay)
		).put(
			"brokenLinksCheckerEnabled",
			GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.CMS_BROKEN_LINKS_CHECKER_ENABLED))
		).put(
			"candidateAssetLibraries",
			SectionDisplayContextUtil.getDepotEntriesJSONArray(
				httpServletRequest,
				getRootObjectEntryFolderExternalReferenceCode())
		).put(
			"cmpEnabled", LicenseManagerUtil.isAppEnabled(App.CMP)
		).put(
			"cmpProjectLinkObjectDefinitionId",
			() -> {
				ObjectDefinition cmpProjectLinkObjectDefinition =
					ObjectDefinitionLocalServiceUtil.
						fetchObjectDefinitionByExternalReferenceCode(
							"L_CMP_PROJECT_LINK", themeDisplay.getCompanyId());

				if (cmpProjectLinkObjectDefinition == null) {
					return null;
				}

				return cmpProjectLinkObjectDefinition.getObjectDefinitionId();
			}
		).put(
			"cmpProjectObjectDefinitionId",
			() -> {
				if (cmpProjectObjectDefinition == null) {
					return null;
				}

				return cmpProjectObjectDefinition.getObjectDefinitionId();
			}
		).put(
			"cmpProjectViewURL",
			() -> {
				if (cmpProjectObjectDefinition == null) {
					return null;
				}

				return StringBundler.concat(
					themeDisplay.getPortalURL(),
					portal.getPathFriendlyURLPublic(), "/cms/e/project/",
					portal.getClassNameId(
						cmpProjectObjectDefinition.getClassName()));
			}
		).put(
			"cmsGroupId",
			() -> {
				try {
					Group group = groupLocalService.getGroup(
						themeDisplay.getCompanyId(), GroupConstants.CMS);

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
				themeDisplay.getCompanyId(), _objectDefinitionService,
				getObjectFolderExternalReferenceCodes())
		).put(
			"commentsProps", CommentUtil.getCommentsProps(httpServletRequest)
		).put(
			"contentViewURL",
			SectionDisplayContextUtil.getContentViewURL(themeDisplay)
		).put(
			"defaultPermissionAdditionalProps",
			PermissionUtil.getDefaultPermissionAdditionalProps(
				httpServletRequest, themeDisplay)
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
			"objectEntryFolderExternalReferenceCode",
			() -> {
				if (objectEntryFolder == null) {
					return null;
				}

				return objectEntryFolder.getExternalReferenceCode();
			}
		).put(
			"parentObjectEntryFolderExternalReferenceCode",
			_getParentObjectEntryFolderExternalReferenceCode()
		).put(
			"redirect", themeDisplay.getURLCurrent()
		).build();
	}

	public String getAPIURL() {
		if (isFolderSearchEnabled()) {
			return "/o/search/v1.0/search";
		}

		return "/o/search/v1.0/search?" + getAdditionalAPIURLParameters();
	}

	public Map<String, Object> getBreadcrumbProps() throws PortalException {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		addBreadcrumbItem(
			jsonArray, false, null,
			SectionDisplayContextUtil.getLayoutName(themeDisplay));

		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems", jsonArray
		).put(
			"hideSpace", true
		).build();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setHref(
				"#"
			).setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "delete")
			).build(
				"delete"
			));
	}

	public CreationMenu getCreationMenu() {
		return SectionDisplayContextUtil.getCreationMenu(
			getCreationMenuDropdownItems(), httpServletRequest,
			getRootObjectEntryFolderExternalReferenceCode());
	}

	public List<DropdownItem> getCreationMenuDropdownItems() {
		return Collections.emptyList();
	}

	public abstract Map<String, Object> getEmptyState();

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return SectionDisplayContextUtil.getFDSActionDropdownItems(
			httpServletRequest);
	}

	protected void addBreadcrumbItem(
		JSONArray jsonArray, boolean active, String friendlyURL, String label) {

		jsonArray.put(
			JSONUtil.put(
				"active", active
			).put(
				"href", friendlyURL
			).put(
				"label", label
			));
	}

	protected String appendGroupIds(String filterString) {
		return SectionDisplayContextUtil.appendGroupIds(
			filterString, httpServletRequest);
	}

	protected String appendStatus(String filterString) {
		return SectionDisplayContextUtil.appendStatus(filterString);
	}

	protected abstract String getCMSSectionFilterString();

	protected String[] getObjectFolderExternalReferenceCodes() {
		return new String[] {
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
		};
	}

	protected String getRootObjectEntryFolderExternalReferenceCode() {
		return null;
	}

	protected boolean isFolderSearchEnabled() {
		return false;
	}

	protected final DepotEntryLocalService depotEntryLocalService;
	protected final GroupLocalService groupLocalService;
	protected final HttpServletRequest httpServletRequest;
	protected final Language language;
	protected final ObjectEntryFolder objectEntryFolder;
	protected final Portal portal;
	protected final ThemeDisplay themeDisplay;

	private ObjectEntryFolder _getObjectEntryFolder(
		long companyId, Object object) {

		if (object instanceof DepotEntry) {
			DepotEntry depotEntry = (DepotEntry)object;

			return ObjectEntryFolderLocalServiceUtil.
				fetchObjectEntryFolderByExternalReferenceCode(
					getRootObjectEntryFolderExternalReferenceCode(),
					depotEntry.getGroupId(), companyId);
		}
		else if (object instanceof ObjectEntryFolder) {
			return (ObjectEntryFolder)object;
		}

		return null;
	}

	private String _getParentObjectEntryFolderExternalReferenceCode() {
		if (objectEntryFolder == null) {
			return getRootObjectEntryFolderExternalReferenceCode();
		}

		return objectEntryFolder.getExternalReferenceCode();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSectionDisplayContext.class);

	private final DLConfiguration _dlConfiguration;
	private final ObjectDefinitionService _objectDefinitionService;
	private final TranslationInfoItemFieldValuesExporterRegistry
		_translationInfoItemFieldValuesExporterRegistry;

}