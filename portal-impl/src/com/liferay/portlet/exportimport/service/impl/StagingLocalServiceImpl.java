/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.exportimport.service.impl;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelTitleComparator;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.exception.ExportImportIOException;
import com.liferay.exportimport.kernel.exception.MissingReferenceException;
import com.liferay.exportimport.kernel.exception.RemoteExportException;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.staging.StagingURLHelperUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.kernel.staging.constants.StagingConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutSetBranchConstants;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.IndexStatusManagerThreadLocal;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.RemoteAuthException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.http.GroupServiceHttp;
import com.liferay.portlet.exportimport.service.base.StagingLocalServiceBaseImpl;
import com.liferay.portlet.exportimport.staging.StagingAdvicesThreadLocal;

import jakarta.portlet.PortletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael C. Han
 * @author Máté Thurzó
 * @author Vilmos Papp
 */
public class StagingLocalServiceImpl extends StagingLocalServiceBaseImpl {

	@Override
	public void checkDefaultLayoutSetBranches(
			long userId, Group liveGroup, boolean branchingPublic,
			boolean branchingPrivate, boolean remote,
			ServiceContext serviceContext)
		throws PortalException {

		long targetGroupId = 0;

		if (remote) {
			targetGroupId = liveGroup.getGroupId();
		}
		else {
			Group stagingGroup = liveGroup.getStagingGroup();

			if (stagingGroup == null) {
				return;
			}

			targetGroupId = stagingGroup.getGroupId();
		}

		LayoutSetBranch layoutSetBranch =
			_layoutSetBranchLocalService.fetchLayoutSetBranch(
				targetGroupId, false,
				LayoutSetBranchConstants.MASTER_BRANCH_NAME);

		if (branchingPublic && (layoutSetBranch == null)) {
			addDefaultLayoutSetBranch(
				userId, targetGroupId, liveGroup.getDescriptiveName(), false,
				serviceContext);
		}
		else if (!branchingPublic && (layoutSetBranch != null)) {
			deleteLayoutSetBranches(targetGroupId, false);

			removeLayoutSetBranchIdFromExportImportConfigurations(
				targetGroupId, remote, false);
		}
		else if (layoutSetBranch != null) {
			ExportImportDateUtil.clearLastPublishDate(targetGroupId, false);
		}

		layoutSetBranch = _layoutSetBranchLocalService.fetchLayoutSetBranch(
			targetGroupId, true, LayoutSetBranchConstants.MASTER_BRANCH_NAME);

		if (branchingPrivate && (layoutSetBranch == null)) {
			addDefaultLayoutSetBranch(
				userId, targetGroupId, liveGroup.getDescriptiveName(), true,
				serviceContext);
		}
		else if (!branchingPrivate && (layoutSetBranch != null)) {
			deleteLayoutSetBranches(targetGroupId, true);

			removeLayoutSetBranchIdFromExportImportConfigurations(
				targetGroupId, remote, true);
		}
		else if (layoutSetBranch != null) {
			ExportImportDateUtil.clearLastPublishDate(targetGroupId, true);
		}
	}

	@Override
	public void cleanUpStagingRequest(long stagingRequestId)
		throws PortalException {

		boolean indexReadOnly = IndexStatusManagerThreadLocal.isIndexReadOnly();

		IndexStatusManagerThreadLocal.setIndexReadOnly(true);

		try {
			PortletFileRepositoryUtil.deletePortletFolder(stagingRequestId);
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to clean up staging request " + stagingRequestId,
					noSuchFolderException);
			}
		}
		finally {
			IndexStatusManagerThreadLocal.setIndexReadOnly(indexReadOnly);
		}
	}

	@Override
	public long createStagingRequest(long userId, long groupId, String checksum)
		throws PortalException {

		boolean indexReadOnly = IndexStatusManagerThreadLocal.isIndexReadOnly();

		IndexStatusManagerThreadLocal.setIndexReadOnly(true);

		try {
			ServiceContext serviceContext = new ServiceContext();

			Repository repository =
				PortletFileRepositoryUtil.addPortletRepository(
					groupId, _PORTLET_REPOSITORY_ID, serviceContext);

			Folder folder = PortletFileRepositoryUtil.addPortletFolder(
				userId, repository.getRepositoryId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, checksum,
				serviceContext);

			return folder.getFolderId();
		}
		finally {
			IndexStatusManagerThreadLocal.setIndexReadOnly(indexReadOnly);
		}
	}

	@Override
	public void disableStaging(Group liveGroup, ServiceContext serviceContext)
		throws PortalException {

		disableStaging((PortletRequest)null, liveGroup, serviceContext);
	}

	@Override
	public void disableStaging(
			PortletRequest portletRequest, Group liveGroup,
			ServiceContext serviceContext)
		throws PortalException {

		if (liveGroup.isLayout()) {
			disableStaging(
				portletRequest, liveGroup.getParentGroup(), serviceContext);

			return;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			liveGroup.getTypeSettingsProperties();

		boolean stagedLocally = GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty("staged"));
		boolean stagedRemotely = GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty("stagedRemotely"));

		if (!stagedLocally && !stagedRemotely) {
			return;
		}

		if (stagedRemotely) {
			String remoteURL = StagingURLHelperUtil.buildRemoteURL(
				typeSettingsUnicodeProperties);

			long remoteGroupId = GetterUtil.getLong(
				typeSettingsUnicodeProperties.getProperty("remoteGroupId"));
			boolean forceDisable = GetterUtil.getBoolean(
				serviceContext.getAttribute("forceDisable"));

			disableRemoteStaging(remoteURL, remoteGroupId, forceDisable);
		}

		typeSettingsUnicodeProperties.remove("branchingPrivate");
		typeSettingsUnicodeProperties.remove("branchingPublic");
		typeSettingsUnicodeProperties.remove("remoteAddress");
		typeSettingsUnicodeProperties.remove("remoteGroupId");
		typeSettingsUnicodeProperties.remove("remotePathContext");
		typeSettingsUnicodeProperties.remove("remotePort");
		typeSettingsUnicodeProperties.remove("remoteSiteURL");
		typeSettingsUnicodeProperties.remove("secureConnection");
		typeSettingsUnicodeProperties.remove("overrideRemoteSiteURL");
		typeSettingsUnicodeProperties.remove("staged");
		typeSettingsUnicodeProperties.remove("stagedRemotely");

		Set<String> keys = new HashSet<>();

		for (String key : typeSettingsUnicodeProperties.keySet()) {
			if (key.startsWith(StagingConstants.STAGED_PORTLET)) {
				keys.add(key);
			}
		}

		for (String key : keys) {
			typeSettingsUnicodeProperties.remove(key);
		}

		StagingUtil.deleteLastImportSettings(liveGroup, true);
		StagingUtil.deleteLastImportSettings(liveGroup, false);

		checkDefaultLayoutSetBranches(
			serviceContext.getUserId(), liveGroup, false, false, stagedRemotely,
			serviceContext);

		if (liveGroup.hasStagingGroup()) {
			Group stagingGroup = liveGroup.getStagingGroup();

			_groupLocalService.deleteGroup(stagingGroup.getGroupId());

			liveGroup.clearStagingGroup();
		}

		_groupLocalService.updateGroup(
			liveGroup.getGroupId(), typeSettingsUnicodeProperties.toString());
	}

	@Override
	public void enableLocalStaging(
			long userId, Group liveGroup, boolean branchingPublic,
			boolean branchingPrivate, ServiceContext serviceContext)
		throws PortalException {

		if (liveGroup.isLayout()) {
			enableLocalStaging(
				userId, liveGroup.getParentGroup(), branchingPublic,
				branchingPrivate, serviceContext);

			return;
		}

		if (liveGroup.isStagedRemotely()) {
			disableStaging(liveGroup, serviceContext);
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			liveGroup.getTypeSettingsProperties();

		boolean hasStagingGroup = liveGroup.hasStagingGroup();

		if (!hasStagingGroup) {
			serviceContext.setAttribute("staging", Boolean.TRUE.toString());

			String languageId = typeSettingsUnicodeProperties.getProperty(
				"languageId");

			if (Validator.isNotNull(languageId)) {
				serviceContext.setLanguageId(languageId);
			}

			addStagingGroup(userId, liveGroup, serviceContext);
		}

		checkDefaultLayoutSetBranches(
			userId, liveGroup, branchingPublic, branchingPrivate, false,
			serviceContext);

		typeSettingsUnicodeProperties.setProperty(
			"branchingPrivate", String.valueOf(branchingPrivate));
		typeSettingsUnicodeProperties.setProperty(
			"branchingPublic", String.valueOf(branchingPublic));

		if (!hasStagingGroup) {
			typeSettingsUnicodeProperties.setProperty(
				"staged", Boolean.TRUE.toString());
			typeSettingsUnicodeProperties.setProperty(
				"stagedRemotely", Boolean.FALSE.toString());

			setCommonStagingOptions(
				typeSettingsUnicodeProperties, serviceContext);
		}

		_groupLocalService.updateGroup(
			liveGroup.getGroupId(), typeSettingsUnicodeProperties.toString());

		if (hasStagingGroup) {
			return;
		}

		Group stagingGroup = liveGroup.getStagingGroup();

		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap();

		if (liveGroup.hasPrivateLayouts()) {
			StagingUtil.publishLayouts(
				userId, liveGroup.getGroupId(), stagingGroup.getGroupId(), true,
				parameterMap);

			parameterMap =
				ExportImportConfigurationParameterMapFactoryUtil.
					buildParameterMap();
		}

		if (liveGroup.hasPublicLayouts() || !liveGroup.hasPrivateLayouts()) {
			StagingUtil.publishLayouts(
				userId, liveGroup.getGroupId(), stagingGroup.getGroupId(),
				false, parameterMap);
		}
	}

	@Override
	public void enableRemoteStaging(
			long userId, Group stagingGroup, boolean branchingPublic,
			boolean branchingPrivate, String remoteAddress, int remotePort,
			String remotePathContext, boolean secureConnection,
			long remoteGroupId, ServiceContext serviceContext)
		throws PortalException {

		_groupLocalService.validateRemote(
			stagingGroup.getGroupId(), remoteAddress, remotePort,
			remotePathContext, secureConnection, remoteGroupId);

		if (stagingGroup.hasStagingGroup()) {
			disableStaging(stagingGroup, serviceContext);
		}

		boolean stagedRemotely = stagingGroup.isStagedRemotely();

		boolean oldStagedRemotely = stagedRemotely;

		UnicodeProperties typeSettingsUnicodeProperties =
			stagingGroup.getTypeSettingsProperties();

		String remoteURL = StagingURLHelperUtil.buildRemoteURL(
			remoteAddress, remotePort, remotePathContext, secureConnection);

		if (stagedRemotely) {
			long oldRemoteGroupId = GetterUtil.getLong(
				typeSettingsUnicodeProperties.getProperty("remoteGroupId"));

			String oldRemoteURL = StagingURLHelperUtil.buildRemoteURL(
				typeSettingsUnicodeProperties);

			if (!remoteURL.equals(oldRemoteURL) ||
				(remoteGroupId != oldRemoteGroupId)) {

				disableRemoteStaging(oldRemoteURL, oldRemoteGroupId, false);

				stagedRemotely = false;
			}
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		User user = permissionChecker.getUser();

		HttpPrincipal httpPrincipal = new HttpPrincipal(
			remoteURL, user.getLogin(), user.getPassword(),
			user.isPasswordEncrypted());

		if (!stagedRemotely) {
			enableRemoteStaging(httpPrincipal, remoteGroupId);
		}

		Group remoteGroup = fetchRemoteGroup(httpPrincipal, remoteGroupId);

		checkDefaultLayoutSetBranches(
			userId, stagingGroup, branchingPublic, branchingPrivate, true,
			serviceContext);

		typeSettingsUnicodeProperties.setProperty(
			"branchingPrivate", String.valueOf(branchingPrivate));
		typeSettingsUnicodeProperties.setProperty(
			"branchingPublic", String.valueOf(branchingPublic));
		typeSettingsUnicodeProperties.setProperty(
			"remoteAddress", remoteAddress);
		typeSettingsUnicodeProperties.setProperty(
			"remoteGroupExternalReferenceCode",
			remoteGroup.getExternalReferenceCode());
		typeSettingsUnicodeProperties.setProperty(
			"remoteGroupId", String.valueOf(remoteGroupId));
		typeSettingsUnicodeProperties.setProperty(
			"remoteGroupUUID", remoteGroup.getUuid());
		typeSettingsUnicodeProperties.setProperty(
			"remotePathContext", remotePathContext);
		typeSettingsUnicodeProperties.setProperty(
			"remotePort", String.valueOf(remotePort));
		typeSettingsUnicodeProperties.setProperty(
			"secureConnection", String.valueOf(secureConnection));

		if (!oldStagedRemotely) {
			typeSettingsUnicodeProperties.setProperty(
				"staged", Boolean.TRUE.toString());
			typeSettingsUnicodeProperties.setProperty(
				"stagedRemotely", Boolean.TRUE.toString());

			setCommonStagingOptions(
				typeSettingsUnicodeProperties, serviceContext);
		}

		_groupLocalService.updateGroup(
			stagingGroup.getGroupId(),
			typeSettingsUnicodeProperties.toString());

		updateStagedPortlets(
			remoteURL, remoteGroupId, typeSettingsUnicodeProperties);
	}

	@Override
	public MissingReferences publishStagingRequest(
			long userId, long stagingRequestId,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		File file = null;

		Locale siteDefaultLocale = LocaleThreadLocal.getSiteDefaultLocale();

		try {
			if (exportImportConfiguration.getType() ==
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_PORTLET_REMOTE) {

				ExportImportThreadLocal.setPortletImportInProcess(true);
				ExportImportThreadLocal.setPortletStagingInProcess(true);
			}
			else {
				ExportImportThreadLocal.setLayoutImportInProcess(true);
				ExportImportThreadLocal.setLayoutStagingInProcess(true);
			}

			Folder folder = PortletFileRepositoryUtil.getPortletFolder(
				stagingRequestId);

			FileEntry stagingRequestFileEntry = getStagingRequestFileEntry(
				userId, stagingRequestId, folder);

			file = FileUtil.createTempFile("lar");

			FileUtil.write(file, stagingRequestFileEntry.getContentStream());

			Map<String, Serializable> settingsMap =
				exportImportConfiguration.getSettingsMap();

			settingsMap.put("userId", userId);

			long targetGroupId = MapUtil.getLong(settingsMap, "targetGroupId");

			LocaleThreadLocal.setSiteDefaultLocale(
				PortalUtil.getSiteDefaultLocale(targetGroupId));

			MissingReferences missingReferences = null;

			if (exportImportConfiguration.getType() ==
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_PORTLET_REMOTE) {

				_exportImportLocalService.importPortletDataDeletions(
					exportImportConfiguration, file);

				missingReferences =
					_exportImportLocalService.validateImportPortletInfo(
						exportImportConfiguration, file);

				_exportImportLocalService.importPortletInfo(
					exportImportConfiguration, file);
			}
			else {
				_exportImportLocalService.importLayoutsDataDeletions(
					exportImportConfiguration, file);

				missingReferences =
					_exportImportLocalService.validateImportLayoutsFile(
						exportImportConfiguration, file);

				_exportImportLocalService.importLayouts(
					exportImportConfiguration, file);
			}

			return missingReferences;
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					StagingLocalServiceImpl.class.getName(), ioException);

			exportImportIOException.setStagingRequestId(stagingRequestId);
			exportImportIOException.setType(
				ExportImportIOException.PUBLISH_STAGING_REQUEST);

			throw exportImportIOException;
		}
		catch (MissingReferenceException missingReferenceException) {
			return missingReferenceException.getMissingReferences();
		}
		finally {
			if (exportImportConfiguration.getType() ==
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_PORTLET_REMOTE) {

				ExportImportThreadLocal.setPortletImportInProcess(false);
				ExportImportThreadLocal.setPortletStagingInProcess(false);
			}
			else {
				ExportImportThreadLocal.setLayoutImportInProcess(false);
				ExportImportThreadLocal.setLayoutStagingInProcess(false);
			}

			LocaleThreadLocal.setSiteDefaultLocale(siteDefaultLocale);

			FileUtil.delete(file);
		}
	}

	@Override
	public void updateStagingRequest(
			long userId, long stagingRequestId, String fileName, byte[] bytes)
		throws PortalException {

		Folder folder = PortletFileRepositoryUtil.getPortletFolder(
			stagingRequestId);

		boolean indexReadOnly = IndexStatusManagerThreadLocal.isIndexReadOnly();

		IndexStatusManagerThreadLocal.setIndexReadOnly(true);

		try {
			PortletFileRepositoryUtil.addPortletFileEntry(
				null, folder.getGroupId(), userId, Group.class.getName(),
				folder.getGroupId(), _PORTLET_REPOSITORY_ID,
				folder.getFolderId(), new UnsyncByteArrayInputStream(bytes),
				fileName, ContentTypes.APPLICATION_ZIP, false);
		}
		finally {
			IndexStatusManagerThreadLocal.setIndexReadOnly(indexReadOnly);
		}
	}

	protected void addDefaultLayoutSetBranch(
			long userId, long groupId, String groupName, boolean privateLayout,
			ServiceContext serviceContext)
		throws PortalException {

		String masterBranchDescription =
			LayoutSetBranchConstants.MASTER_BRANCH_DESCRIPTION_PUBLIC;

		if (privateLayout) {
			masterBranchDescription =
				LayoutSetBranchConstants.MASTER_BRANCH_DESCRIPTION_PRIVATE;
		}

		String description = LanguageUtil.format(
			PortalUtil.getSiteDefaultLocale(groupId), masterBranchDescription,
			groupName, false);

		try {
			serviceContext.setWorkflowAction(WorkflowConstants.STATUS_APPROVED);

			LayoutSetBranch layoutSetBranch =
				_layoutSetBranchLocalService.addLayoutSetBranch(
					userId, groupId, privateLayout,
					LayoutSetBranchConstants.MASTER_BRANCH_NAME, description,
					true, LayoutSetBranchConstants.ALL_BRANCHES,
					serviceContext);

			List<LayoutRevision> layoutRevisions =
				_layoutRevisionLocalService.getLayoutRevisions(
					layoutSetBranch.getLayoutSetBranchId(), false);

			for (LayoutRevision layoutRevision : layoutRevisions) {
				_layoutRevisionLocalService.updateStatus(
					userId, layoutRevision.getLayoutRevisionId(),
					WorkflowConstants.STATUS_APPROVED, serviceContext);
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to create master branch for " +
						(privateLayout ? "private" : "public") + " layouts",
					portalException);
			}
		}
	}

	protected Group addStagingGroup(
			long userId, Group liveGroup, ServiceContext serviceContext)
		throws PortalException {

		Group stagingGroup = _groupLocalService.addGroup(
			userId, liveGroup.getParentGroupId(), liveGroup.getClassName(),
			liveGroup.getClassPK(), liveGroup.getGroupId(),
			liveGroup.getNameMap(), liveGroup.getDescriptionMap(),
			liveGroup.getType(), liveGroup.isManualMembership(),
			liveGroup.getMembershipRestriction(), liveGroup.getFriendlyURL(),
			false, true, serviceContext);

		if (LanguageUtil.isInheritLocales(liveGroup.getGroupId())) {
			return stagingGroup;
		}

		UnicodeProperties liveTypeSettingsUnicodeProperties =
			liveGroup.getTypeSettingsProperties();

		UnicodeProperties stagingTypeSettingsUnicodeProperties =
			stagingGroup.getTypeSettingsProperties();

		stagingTypeSettingsUnicodeProperties.setProperty(
			GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES,
			Boolean.FALSE.toString());
		stagingTypeSettingsUnicodeProperties.setProperty(
			PropsKeys.LOCALES,
			liveTypeSettingsUnicodeProperties.getProperty(PropsKeys.LOCALES));
		stagingTypeSettingsUnicodeProperties.setProperty(
			"languageId",
			liveTypeSettingsUnicodeProperties.getProperty(
				"languageId",
				LocaleUtil.toLanguageId(LocaleUtil.getDefault())));

		return _groupLocalService.updateGroup(
			stagingGroup.getGroupId(),
			stagingTypeSettingsUnicodeProperties.toString());
	}

	protected void deleteLayoutSetBranches(long groupId, boolean privateLayout)
		throws PortalException {

		// Find the latest layout revision for all the layouts

		Map<Long, LayoutRevision> layoutRevisions = new HashMap<>();

		List<LayoutSetBranch> layoutSetBranches =
			_layoutSetBranchLocalService.getLayoutSetBranches(
				groupId, privateLayout);

		boolean publishedToLive = false;

		for (LayoutSetBranch layoutSetBranch : layoutSetBranches) {
			String lastPublishDateString = layoutSetBranch.getSettingsProperty(
				"last-publish-date");

			if (Validator.isNotNull(lastPublishDateString)) {
				publishedToLive = true;

				break;
			}
		}

		for (LayoutSetBranch layoutSetBranch : layoutSetBranches) {
			String lastPublishDateString = layoutSetBranch.getSettingsProperty(
				"last-publish-date");

			if (Validator.isNull(lastPublishDateString) && publishedToLive) {
				continue;
			}

			Date lastPublishDate = null;

			if (Validator.isNotNull(lastPublishDateString)) {
				lastPublishDate = new Date(
					GetterUtil.getLong(lastPublishDateString));
			}

			List<LayoutRevision> headLayoutRevisions =
				_layoutRevisionLocalService.getLayoutRevisions(
					layoutSetBranch.getLayoutSetBranchId(), true);

			for (LayoutRevision headLayoutRevision : headLayoutRevisions) {
				LayoutRevision layoutRevision = layoutRevisions.get(
					headLayoutRevision.getPlid());

				if (layoutRevision == null) {
					layoutRevisions.put(
						headLayoutRevision.getPlid(), headLayoutRevision);

					continue;
				}

				Date statusDate = headLayoutRevision.getStatusDate();

				if (statusDate.after(layoutRevision.getStatusDate()) &&
					((lastPublishDate == null) ||
					 lastPublishDate.after(statusDate))) {

					layoutRevisions.put(
						headLayoutRevision.getPlid(), headLayoutRevision);
				}
			}
		}

		// Update all layouts based on their latest revision

		for (LayoutRevision layoutRevision : layoutRevisions.values()) {
			updateLayoutWithLayoutRevision(layoutRevision);
		}

		_layoutSetBranchLocalService.deleteLayoutSetBranches(
			groupId, privateLayout, true);
	}

	protected void disableRemoteStaging(
			String remoteURL, long remoteGroupId, boolean forceDisable)
		throws PortalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		User user = permissionChecker.getUser();

		HttpPrincipal httpPrincipal = new HttpPrincipal(
			remoteURL, user.getLogin(), user.getPassword(),
			user.isPasswordEncrypted());

		try {
			GroupServiceHttp.disableStaging(httpPrincipal, remoteGroupId);
		}
		catch (RemoteAuthException remoteAuthException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(remoteAuthException);
			}

			remoteAuthException.setURL(remoteURL);

			throw remoteAuthException;
		}
		catch (SystemException systemException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(systemException);
			}

			if (!forceDisable) {
				RemoteExportException remoteExportException =
					new RemoteExportException(
						RemoteExportException.BAD_CONNECTION);

				remoteExportException.setURL(remoteURL);

				throw remoteExportException;
			}

			if (_log.isWarnEnabled()) {
				_log.warn("Forcibly disable remote staging");
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			String message = portalException.getMessage();

			if (message.contains(
					NoSuchGroupException.class.getCanonicalName())) {

				if (!forceDisable) {
					RemoteExportException remoteExportException =
						new RemoteExportException(
							RemoteExportException.NO_GROUP);

					remoteExportException.setGroupId(remoteGroupId);

					throw remoteExportException;
				}
			}
			else if (message.contains(
						PrincipalException.class.getCanonicalName())) {

				RemoteExportException remoteExportException =
					new RemoteExportException(
						RemoteExportException.NO_PERMISSIONS);

				remoteExportException.setGroupId(remoteGroupId);

				throw remoteExportException;
			}
			else {
				throw portalException;
			}

			if (_log.isWarnEnabled()) {
				_log.warn("Forcibly disable remote staging");
			}
		}
	}

	protected void enableRemoteStaging(
			HttpPrincipal httpPrincipal, long remoteGroupId)
		throws PortalException {

		try {
			GroupServiceHttp.enableStaging(httpPrincipal, remoteGroupId);
		}
		catch (NoSuchGroupException noSuchGroupException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchGroupException);
			}

			RemoteExportException remoteExportException =
				new RemoteExportException(RemoteExportException.NO_GROUP);

			remoteExportException.setGroupId(remoteGroupId);

			throw remoteExportException;
		}
		catch (PrincipalException principalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			RemoteExportException remoteExportException =
				new RemoteExportException(RemoteExportException.NO_PERMISSIONS);

			remoteExportException.setGroupId(remoteGroupId);

			throw remoteExportException;
		}
		catch (RemoteAuthException remoteAuthException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(remoteAuthException);
			}

			remoteAuthException.setURL(httpPrincipal.getUrl());

			throw remoteAuthException;
		}
		catch (SystemException systemException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(systemException);
			}

			RemoteExportException remoteExportException =
				new RemoteExportException(RemoteExportException.BAD_CONNECTION);

			remoteExportException.setURL(httpPrincipal.getUrl());

			throw remoteExportException;
		}
	}

	protected Group fetchRemoteGroup(HttpPrincipal httpPrincipal, long groupId)
		throws PortalException {

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			return GroupServiceHttp.getGroup(httpPrincipal, groupId);
		}
	}

	protected FileEntry fetchStagingRequestFileEntry(
			long stagingRequestId, Folder folder)
		throws PortalException {

		try {
			return PortletFileRepositoryUtil.getPortletFileEntry(
				folder.getGroupId(), folder.getFolderId(),
				getAssembledFileName(stagingRequestId));
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileEntryException);
			}

			return null;
		}
	}

	protected String getAssembledFileName(long stagingRequestId) {
		return _ASSEMBLED_LAR_PREFIX + String.valueOf(stagingRequestId) +
			".lar";
	}

	protected FileEntry getStagingRequestFileEntry(
			long userId, long stagingRequestId, Folder folder)
		throws PortalException {

		FileEntry stagingRequestFileEntry = fetchStagingRequestFileEntry(
			stagingRequestId, folder);

		if (stagingRequestFileEntry != null) {
			return stagingRequestFileEntry;
		}

		boolean indexReadOnly = IndexStatusManagerThreadLocal.isIndexReadOnly();

		IndexStatusManagerThreadLocal.setIndexReadOnly(true);

		File tempFile = FileUtil.createTempFile("lar");

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				tempFile)) {

			List<FileEntry> fileEntries =
				PortletFileRepositoryUtil.getPortletFileEntries(
					folder.getGroupId(), folder.getFolderId(),
					new RepositoryModelTitleComparator<FileEntry>(true));

			for (FileEntry fileEntry : fileEntries) {
				try {
					StreamUtil.transfer(
						fileEntry.getContentStream(),
						StreamUtil.uncloseable(fileOutputStream));
				}
				finally {
					PortletFileRepositoryUtil.deletePortletFileEntry(
						fileEntry.getFileEntryId());
				}
			}

			String checksum = FileUtil.getMD5Checksum(tempFile);

			if (!checksum.equals(folder.getName())) {
				ExportImportIOException exportImportIOException =
					new ExportImportIOException(
						StagingLocalServiceImpl.class.getName());

				exportImportIOException.setChecksum(checksum);
				exportImportIOException.setType(
					ExportImportIOException.STAGING_REQUEST_CHECKSUM);

				throw exportImportIOException;
			}

			PortletFileRepositoryUtil.addPortletFileEntry(
				null, folder.getGroupId(), userId, Group.class.getName(),
				folder.getGroupId(), _PORTLET_REPOSITORY_ID,
				folder.getFolderId(), tempFile,
				getAssembledFileName(stagingRequestId),
				ContentTypes.APPLICATION_ZIP, false);

			stagingRequestFileEntry = fetchStagingRequestFileEntry(
				stagingRequestId, folder);

			if (stagingRequestFileEntry == null) {
				throw new IOException();
			}

			return stagingRequestFileEntry;
		}
		catch (IOException ioException) {
			ExportImportIOException exportImportIOException =
				new ExportImportIOException(
					StagingLocalServiceImpl.class.getName(), ioException);

			exportImportIOException.setStagingRequestId(stagingRequestId);
			exportImportIOException.setType(
				ExportImportIOException.STAGING_REQUEST_REASSEMBLE_FILE);

			throw exportImportIOException;
		}
		finally {
			IndexStatusManagerThreadLocal.setIndexReadOnly(indexReadOnly);

			FileUtil.delete(tempFile);
		}
	}

	protected void removeLayoutSetBranchIdFromExportImportConfigurations(
			long groupId, boolean remote, boolean privateLayout)
		throws PortalException {

		int configurationType =
			ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_LOCAL;

		if (remote) {
			configurationType =
				ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_REMOTE;
		}

		List<ExportImportConfiguration> exportImportConfigurations =
			_exportImportConfigurationLocalService.
				getExportImportConfigurations(groupId, configurationType);

		for (ExportImportConfiguration exportImportConfiguration :
				exportImportConfigurations) {

			Map<String, Serializable> settingsMap =
				exportImportConfiguration.getSettingsMap();

			Map<String, String[]> parameterMap =
				(Map<String, String[]>)settingsMap.get("parameterMap");

			if (MapUtil.getBoolean(parameterMap, "privateLayout") !=
					privateLayout) {

				continue;
			}

			parameterMap.remove("layoutSetBranchId");

			exportImportConfiguration.setSettings(
				JSONFactoryUtil.serialize(settingsMap));

			_exportImportConfigurationLocalService.
				updateExportImportConfiguration(exportImportConfiguration);
		}
	}

	protected void setCommonStagingOptions(
		UnicodeProperties typeSettingsUnicodeProperties,
		ServiceContext serviceContext) {

		typeSettingsUnicodeProperties.putAll(
			PropertiesParamUtil.getProperties(
				serviceContext, StagingConstants.STAGED_PREFIX));
	}

	protected Layout updateLayoutWithLayoutRevision(
			LayoutRevision layoutRevision)
		throws PortalException {

		// Suppress the usage of the advice to get the latest layout to prevent
		// a StaleObjectStateException

		Layout layout = null;

		boolean stagingAdvicesThreadLocalEnabled =
			StagingAdvicesThreadLocal.isEnabled();

		try {
			StagingAdvicesThreadLocal.setEnabled(false);

			layout = _layoutLocalService.fetchLayout(layoutRevision.getPlid());
		}
		finally {
			StagingAdvicesThreadLocal.setEnabled(
				stagingAdvicesThreadLocalEnabled);
		}

		updatePortletPreferences(layoutRevision, layout);

		layout.setUserId(layoutRevision.getUserId());
		layout.setUserName(layoutRevision.getUserName());
		layout.setCreateDate(layoutRevision.getCreateDate());
		layout.setModifiedDate(layoutRevision.getModifiedDate());
		layout.setPrivateLayout(layoutRevision.isPrivateLayout());
		layout.setName(layoutRevision.getName());
		layout.setTitle(layoutRevision.getTitle());
		layout.setDescription(layoutRevision.getDescription());
		layout.setKeywords(layoutRevision.getKeywords());
		layout.setRobots(layoutRevision.getRobots());
		layout.setTypeSettings(layoutRevision.getTypeSettings());
		layout.setIconImageId(layoutRevision.getIconImageId());
		layout.setThemeId(layoutRevision.getThemeId());
		layout.setColorSchemeId(layoutRevision.getColorSchemeId());
		layout.setCss(layoutRevision.getCss());

		return _layoutLocalService.updateLayout(layout);
	}

	protected void updatePortletPreferences(
		LayoutRevision layoutRevision, Layout layout) {

		_portletPreferencesLocalService.deletePortletPreferencesByPlid(
			layout.getPlid());

		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				layoutRevision.getLayoutRevisionId());

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			jakarta.portlet.PortletPreferences jxPortletPreferences =
				_portletPreferenceValueLocalService.getPreferences(
					portletPreferences);

			_portletPreferencesLocalService.addPortletPreferences(
				layoutRevision.getCompanyId(), portletPreferences.getOwnerId(),
				portletPreferences.getOwnerType(), layout.getPlid(),
				portletPreferences.getPortletId(), null,
				PortletPreferencesFactoryUtil.toXML(jxPortletPreferences));
		}
	}

	protected void updateStagedPortlets(
			String remoteURL, long remoteGroupId,
			UnicodeProperties typeSettingsUnicodeProperties)
		throws PortalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		User user = permissionChecker.getUser();

		HttpPrincipal httpPrincipal = new HttpPrincipal(
			remoteURL, user.getLogin(), user.getPassword(),
			user.isPasswordEncrypted());

		Map<String, String> stagedPortletIds = new HashMap<>();

		for (String key : typeSettingsUnicodeProperties.keySet()) {
			if (key.startsWith(StagingConstants.STAGED_PORTLET)) {
				stagedPortletIds.put(
					key, typeSettingsUnicodeProperties.getProperty(key));
			}
		}

		try {
			GroupServiceHttp.updateStagedPortlets(
				httpPrincipal, remoteGroupId, stagedPortletIds);
		}
		catch (NoSuchGroupException noSuchGroupException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchGroupException);
			}

			RemoteExportException remoteExportException =
				new RemoteExportException(RemoteExportException.NO_GROUP);

			remoteExportException.setGroupId(remoteGroupId);

			throw remoteExportException;
		}
		catch (PrincipalException principalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			RemoteExportException remoteExportException =
				new RemoteExportException(RemoteExportException.NO_PERMISSIONS);

			remoteExportException.setGroupId(remoteGroupId);

			throw remoteExportException;
		}
		catch (RemoteAuthException remoteAuthException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(remoteAuthException);
			}

			remoteAuthException.setURL(remoteURL);

			throw remoteAuthException;
		}
		catch (SystemException systemException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(systemException);
			}

			RemoteExportException remoteExportException =
				new RemoteExportException(RemoteExportException.BAD_CONNECTION);

			remoteExportException.setURL(remoteURL);

			throw remoteExportException;
		}
	}

	private static final String _ASSEMBLED_LAR_PREFIX = "assembled_";

	private static final String _PORTLET_REPOSITORY_ID = "134";

	private static final Log _log = LogFactoryUtil.getLog(
		StagingLocalServiceImpl.class);

	@BeanReference(type = ExportImportConfigurationLocalService.class)
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@BeanReference(type = ExportImportLocalService.class)
	private ExportImportLocalService _exportImportLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = LayoutLocalService.class)
	private LayoutLocalService _layoutLocalService;

	@BeanReference(type = LayoutRevisionLocalService.class)
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@BeanReference(type = LayoutSetBranchLocalService.class)
	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

	@BeanReference(type = PortletPreferencesLocalService.class)
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@BeanReference(type = PortletPreferenceValueLocalService.class)
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

}