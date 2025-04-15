/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.web.internal.display.context.helper.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.document.library.web.internal.security.permission.resource.DLFolderPermission;
import com.liferay.document.library.web.internal.util.FolderItemSelectorURLProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.BrowserSnifferUtil;
import com.liferay.portlet.asset.util.comparator.AssetVocabularyGroupLocalizedTitleComparator;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class DLViewDisplayContext {

	public DLViewDisplayContext(
		DLAdminDisplayContext dlAdminDisplayContext,
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_dlAdminDisplayContext = dlAdminDisplayContext;
		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_dlPortletInstanceSettingsHelper = new DLPortletInstanceSettingsHelper(
			new DLRequestHelper(httpServletRequest));
	}

	public String getAddFileEntryURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/document_library/edit_file_entry"
		).setCMD(
			Constants.ADD
		).setRedirect(
			getRedirect()
		).setParameter(
			"folderId", _dlAdminDisplayContext.getFolderId()
		).setParameter(
			"groupId",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return themeDisplay.getScopeGroupId();
			}
		).setParameter(
			"repositoryId", _dlAdminDisplayContext.getRepositoryId()
		).buildString();
	}

	public String getCopyURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/document_library/copy_dl_objects"
		).setRedirect(
			getRedirect()
		).setParameter(
			"sourceFolderId", getFolderId()
		).setParameter(
			"sourceRepositoryId", getRepositoryId()
		).buildString();
	}

	public String getDownloadEntryURL() {
		ResourceURL resourceURL = _renderResponse.createResourceURL();

		resourceURL.setParameter(
			"folderId", String.valueOf(_dlAdminDisplayContext.getFolderId()));
		resourceURL.setResourceID("/document_library/download_entry");

		return resourceURL.toString();
	}

	public String getEditEntryURL() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/document_library/edit_entry"
		).setRedirect(
			getRedirect()
		).buildString();
	}

	public String getEditFileEntryURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/document_library/edit_file_entry"
		).buildString();
	}

	public String[] getEntryColumnNames() {
		return _dlPortletInstanceSettingsHelper.getEntryColumns();
	}

	public Folder getFolder() {
		return _dlAdminDisplayContext.getFolder();
	}

	public long getFolderId() {
		return _dlAdminDisplayContext.getFolderId();
	}

	public String getPermissionURL(String className) throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return StringPool.BLANK;
		}

		return PermissionsURLTag.doTag(
			null, className, themeDisplay.getScopeGroupId(),
			LiferayWindowState.POP_UP.toString(), _httpServletRequest);
	}

	public String getRedirect() {
		PortletURL portletURL = _getCurrentPortletURL();

		return portletURL.toString();
	}

	public long getRepositoryId() {
		return _dlAdminDisplayContext.getRepositoryId();
	}

	public String getRestoreTrashEntriesURL() {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/document_library/edit_entry"
		).setCMD(
			Constants.RESTORE
		).buildString();
	}

	public String getSelectAssetCategoriesURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(_getCurrentPortletURL(), _renderResponse)
		).setParameter(
			"assetCategoryId", (String)null
		).buildString();
	}

	public String getSelectAssetTagsURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(_getCurrentPortletURL(), _renderResponse)
		).setParameter(
			"assetTagId", (String)null
		).buildString();
	}

	public String getSelectCategoriesURL() {
		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		itemSelectorCriterion.setItemType(AssetCategory.class.getName());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortletURLBuilder.create(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_renderRequest),
				themeDisplay.getScopeGroup(), themeDisplay.getScopeGroupId(),
				_renderResponse.getNamespace() + "selectCategories",
				itemSelectorCriterion)
		).buildString();
	}

	public String getSelectExtensionURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(_getCurrentPortletURL(), _renderResponse)
		).setParameter(
			"extension", (String)null
		).buildString();
	}

	public String getSelectFileEntryTypeURL() throws WindowStateException {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/document_library/select_file_entry_type.jsp"
		).setParameter(
			"fileEntryTypeId", _getFileEntryTypeId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getSelectFolderURL() throws PortalException {
		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		FolderItemSelectorURLProvider folderItemSelectorURLProvider =
			new FolderItemSelectorURLProvider(
				_httpServletRequest, itemSelector);

		return folderItemSelectorURLProvider.getSelectMoveToFolderURL(
			_dlAdminDisplayContext.getSelectedRepositoryId(), getFolderId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	public String getSidebarPanelURL() {
		ResourceURL resourceURL = _renderResponse.createResourceURL();

		resourceURL.setParameter(
			"folderId", String.valueOf(_dlAdminDisplayContext.getFolderId()));
		resourceURL.setParameter(
			"repositoryId",
			String.valueOf(_dlAdminDisplayContext.getRepositoryId()));
		resourceURL.setResourceID("/document_library/info_panel");

		return resourceURL.toString();
	}

	public String getUploadURL() throws PortalException {
		if (!isUploadable()) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/document_library/edit_file_entry"
		).setCMD(
			Constants.ADD_DYNAMIC
		).setParameter(
			"folderId", "{folderId}"
		).setParameter(
			"repositoryId", _dlAdminDisplayContext.getRepositoryId()
		).buildString();
	}

	public String getViewFileEntryTypeURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(_getCurrentPortletURL(), _renderResponse)
		).setParameter(
			"browseBy", "file-entry-type"
		).setParameter(
			"fileEntryTypeId", (String)null
		).buildString();
	}

	public String getViewFileEntryURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/document_library/view_file_entry"
		).setRedirect(
			getRedirect()
		).buildString();
	}

	public String getViewMoreFileEntryTypesURL() throws WindowStateException {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/document_library/view_more_menu_items.jsp"
		).setParameter(
			"eventName", _renderResponse.getNamespace() + "selectAddMenuItem"
		).setParameter(
			"folderId", _dlAdminDisplayContext.getFolderId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public boolean isFileEntryMetadataSetsNavigation() {
		return Objects.equals(_getNavigation(), "file_entry_metadata_sets");
	}

	public boolean isFileEntryTypesNavigation() {
		return Objects.equals(_getNavigation(), "file_entry_types");
	}

	public boolean isOpenInMSOfficeEnabled() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		if (portletDisplay.isWebDAVEnabled() &&
			BrowserSnifferUtil.isIeOnWin32(_httpServletRequest)) {

			return true;
		}

		return false;
	}

	public boolean isSearch() {
		return _dlAdminDisplayContext.isSearch();
	}

	public boolean isShowFolderDescription() {
		if (_dlAdminDisplayContext.isDefaultFolderView()) {
			return false;
		}

		Folder folder = _dlAdminDisplayContext.getFolder();

		if (folder == null) {
			return false;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletName = portletDisplay.getPortletName();

		if (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY) ||
			portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {

			return true;
		}

		return false;
	}

	public boolean isUploadable() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!DLFolderPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				_dlAdminDisplayContext.getFolderId(),
				ActionKeys.ADD_DOCUMENT)) {

			return false;
		}

		List<AssetVocabulary> assetVocabularies = new ArrayList<>(
			AssetVocabularyServiceUtil.getGroupVocabularies(
				SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						themeDisplay.getScopeGroupId())));

		assetVocabularies.sort(
			new AssetVocabularyGroupLocalizedTitleComparator(
				themeDisplay.getScopeGroupId(), themeDisplay.getLocale(),
				true));

		long classNameId = ClassNameLocalServiceUtil.getClassNameId(
			DLFileEntryConstants.getClassName());

		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			if (assetVocabulary.isRequired(
					classNameId,
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
					themeDisplay.getScopeGroupId())) {

				return false;
			}
		}

		return true;
	}

	private PortletURL _getCurrentPortletURL() {
		return PortletURLUtil.getCurrent(_renderRequest, _renderResponse);
	}

	private long _getFileEntryTypeId() {
		return _dlAdminDisplayContext.getFileEntryTypeId();
	}

	private String _getNavigation() {
		return _dlAdminDisplayContext.getNavigation();
	}

	private final DLAdminDisplayContext _dlAdminDisplayContext;
	private final DLPortletInstanceSettingsHelper
		_dlPortletInstanceSettingsHelper;
	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}