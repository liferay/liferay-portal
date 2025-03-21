/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.web.internal.security.permission.resource.DLFolderPermission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.portal.workflow.util.WorkflowDefinitionManagerUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class DLEditFolderDisplayContext {

	public DLEditFolderDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getCmd() {
		if (isRootFolder()) {
			return "updateWorkflowDefinitions";
		}

		Folder folder = getFolder();

		if (folder == null) {
			return Constants.ADD;
		}

		return Constants.UPDATE;
	}

	public List<DLFileEntryType> getDLFileEntryTypes() throws PortalException {
		if (_dlFileEntryTypes != null) {
			return _dlFileEntryTypes;
		}

		_dlFileEntryTypes =
			DLFileEntryTypeLocalServiceUtil.getFolderFileEntryTypes(
				PortalUtil.getCurrentAndAncestorSiteGroupIds(
					_themeDisplay.getScopeGroupId()),
				getFolderId(), false);

		return _dlFileEntryTypes;
	}

	public int getDLFileEntryTypesCount() throws PortalException {
		List<DLFileEntryType> dlFileEntryTypes = getDLFileEntryTypes();

		return dlFileEntryTypes.size();
	}

	public String getFileEntryTypeRestrictionsHelpMessage() {
		if (isRootFolder()) {
			return StringPool.BLANK;
		}

		return "document-type-restrictions-help";
	}

	public String getFileEntryTypeRestrictionsLabel() throws PortalException {
		if (isRootFolder()) {
			return StringPool.BLANK;
		}

		if (isWorkflowEnabled()) {
			return "document-type-restrictions-and-workflow";
		}

		return "document-type-restrictions";
	}

	public Folder getFolder() {
		if (_folder != null) {
			return _folder;
		}

		_folder = (Folder)_httpServletRequest.getAttribute(
			WebKeys.DOCUMENT_LIBRARY_FOLDER);

		return _folder;
	}

	public long getFolderId() {
		if (_folderId != null) {
			return _folderId;
		}

		_folderId = BeanParamUtil.getLong(
			getFolder(), _httpServletRequest, "folderId");

		return _folderId;
	}

	public String getHeaderNames() throws PortalException {
		if (isWorkflowEnabled()) {
			return "name,workflow,null";
		}

		return "name,null";
	}

	public String getHeaderTitle() {
		Folder folder = getFolder();

		if (folder != null) {
			return folder.getName();
		}

		if (isRootFolder()) {
			return LanguageUtil.get(_httpServletRequest, "home");
		}

		return LanguageUtil.get(_httpServletRequest, "new-folder");
	}

	public String getLanguageId() {
		return LanguageUtil.getLanguageId(_httpServletRequest);
	}

	public long getParentFolderId() {
		if (_parentFolderId != null) {
			return _parentFolderId;
		}

		_parentFolderId = BeanParamUtil.getLong(
			getFolder(), _httpServletRequest, "parentFolderId",
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return _parentFolderId;
	}

	public String getParentFolderName() {
		Folder folder = _getParentFolder();

		if (folder == null) {
			return LanguageUtil.get(_httpServletRequest, "home");
		}

		return folder.getName();
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public long getRepositoryId() {
		if (_repositoryId != null) {
			return _repositoryId;
		}

		_repositoryId = BeanParamUtil.getLong(
			getFolder(), _httpServletRequest, "repositoryId");

		return _repositoryId;
	}

	public List<WorkflowDefinition> getWorkflowDefinitions()
		throws PortalException {

		if (_workflowDefinitions != null) {
			return _workflowDefinitions;
		}

		if (!isWorkflowEnabled()) {
			return null;
		}

		_workflowDefinitions =
			WorkflowDefinitionManagerUtil.liberalGetActiveWorkflowDefinitions(
				_themeDisplay.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		return _workflowDefinitions;
	}

	public boolean hasAdvancedUpdateDLFolderPermission()
		throws PortalException {

		if (getFolder() == null) {
			return true;
		}

		if (_advancedUpdateDLFolderPermission != null) {
			return _advancedUpdateDLFolderPermission;
		}

		_advancedUpdateDLFolderPermission = DLFolderPermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(), getFolderId(),
			ActionKeys.ADVANCED_UPDATE);

		return _advancedUpdateDLFolderPermission;
	}

	public boolean hasUpdateDLFolderPermission() throws PortalException {
		if (getFolder() == null) {
			return true;
		}

		if (_updateDLFolderPermission != null) {
			return _updateDLFolderPermission;
		}

		_updateDLFolderPermission = DLFolderPermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(), getFolderId(), ActionKeys.UPDATE);

		return _updateDLFolderPermission;
	}

	public boolean isFileEntryTypeSelected(DLFileEntryType dlFileEntryType) {
		DLFolder dlFolder = _getDLFolder();

		if (dlFolder == null) {
			return false;
		}

		if (dlFileEntryType.getFileEntryTypeId() ==
				dlFolder.getDefaultFileEntryTypeId()) {

			return true;
		}

		return false;
	}

	public boolean isFileEntryTypeSupported() {
		Folder folder = getFolder();

		if (isRootFolder() ||
			((folder != null) && (folder.getModel() instanceof DLFolder))) {

			return true;
		}

		return false;
	}

	public boolean isRestrictionTypeFileEntryTypesAndWorkflow() {
		DLFolder dlFolder = _getDLFolder();

		if (dlFolder == null) {
			return false;
		}

		if (dlFolder.getRestrictionType() ==
				DLFolderConstants.
					RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW) {

			return true;
		}

		return false;
	}

	public boolean isRestrictionTypeInherit() {
		DLFolder dlFolder = _getDLFolder();

		if (dlFolder == null) {
			return false;
		}

		if (dlFolder.getRestrictionType() ==
				DLFolderConstants.RESTRICTION_TYPE_INHERIT) {

			return true;
		}

		return false;
	}

	public boolean isRestrictionTypeWorkflow() {
		DLFolder dlFolder = _getDLFolder();

		if (dlFolder == null) {
			return false;
		}

		if (dlFolder.getRestrictionType() ==
				DLFolderConstants.RESTRICTION_TYPE_WORKFLOW) {

			return true;
		}

		return false;
	}

	public boolean isRootFolder() {
		return ParamUtil.getBoolean(_httpServletRequest, "rootFolder");
	}

	public boolean isShowDescription() {
		Folder parentFolder = _getParentFolder();

		if ((parentFolder == null) || parentFolder.isSupportsMetadata()) {
			return true;
		}

		return false;
	}

	public boolean isSupportsMetadata() {
		Folder parentFolder = _getParentFolder();

		if ((parentFolder == null) || parentFolder.isSupportsMetadata()) {
			return true;
		}

		return false;
	}

	public boolean isSupportsPermissions() {
		Folder folder = getFolder();

		if ((folder == null) &&
			!RepositoryUtil.isExternalRepository(getRepositoryId())) {

			return true;
		}

		return false;
	}

	public boolean isWorkflowDefinitionSelected(
		WorkflowDefinition workflowDefinition, long fileEntryTypeId) {

		WorkflowDefinitionLink workflowDefinitionLink =
			WorkflowDefinitionLinkLocalServiceUtil.fetchWorkflowDefinitionLink(
				_themeDisplay.getCompanyId(), getRepositoryId(),
				DLFolderConstants.getClassName(), getFolderId(),
				fileEntryTypeId, true);

		if ((workflowDefinitionLink != null) &&
			Objects.equals(
				workflowDefinitionLink.getWorkflowDefinitionName(),
				workflowDefinition.getName()) &&
			(workflowDefinitionLink.getWorkflowDefinitionVersion() ==
				workflowDefinition.getVersion())) {

			return true;
		}

		return false;
	}

	public boolean isWorkflowEnabled() throws PortalException {
		if (_workflowEnabled != null) {
			return _workflowEnabled;
		}

		Group scopeGroup = _themeDisplay.getScopeGroup();

		WorkflowHandler<DLFileEntry> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				DLFileEntry.class.getName());

		if ((workflowHandler != null) &&
			(DLFolderPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), getFolderId(),
				ActionKeys.ADVANCED_UPDATE) ||
			 DLFolderPermission.contains(
				 _themeDisplay.getPermissionChecker(),
				 _themeDisplay.getScopeGroupId(), getFolderId(),
				 ActionKeys.UPDATE)) &&
			!scopeGroup.isLayoutSetPrototype()) {

			_workflowEnabled = true;
		}
		else {
			_workflowEnabled = false;
		}

		return _workflowEnabled;
	}

	private DLFolder _getDLFolder() {
		Folder folder = getFolder();

		if (folder.getModel() instanceof DLFolder) {
			return (DLFolder)folder.getModel();
		}

		return null;
	}

	private Folder _getParentFolder() {
		try {
			if (_parentFolder != null) {
				return _parentFolder;
			}

			if (getParentFolderId() ==
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

				return null;
			}

			_parentFolder = DLAppServiceUtil.getFolder(getParentFolderId());

			return _parentFolder;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLEditFolderDisplayContext.class);

	private Boolean _advancedUpdateDLFolderPermission;
	private List<DLFileEntryType> _dlFileEntryTypes;
	private Folder _folder;
	private Long _folderId;
	private final HttpServletRequest _httpServletRequest;
	private Folder _parentFolder;
	private Long _parentFolderId;
	private String _redirect;
	private Long _repositoryId;
	private final ThemeDisplay _themeDisplay;
	private Boolean _updateDLFolderPermission;
	private List<WorkflowDefinition> _workflowDefinitions;
	private Boolean _workflowEnabled;

}