/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.kernel.staging.StagingURLHelperUtil;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryRegistryUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.repository.liferayrepository.model.LiferayFolder;
import com.liferay.portal.service.http.GroupServiceHttp;
import com.liferay.portlet.documentlibrary.constants.DLConstants;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = "jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
	service = ExportImportPortletPreferencesProcessor.class
)
public class DLExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return ListUtil.fromArray(
			_dlCommentsAndRatingsExporterImporterCapability, _exportCapability);
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(
			_dlCommentsAndRatingsExporterImporterCapability, _importCapability);
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.PORTLET_DATA) &&
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			return portletPreferences;
		}

		// Root folder external reference code is set, only export that

		String rootFolderExternalReferenceCode = portletPreferences.getValue(
			_PREFERENCE_KEY_ROOT_FOLDER_EXTERNAL_REFERENCE_CODE, null);

		String selectedGroupExternalReferenceCode = portletPreferences.getValue(
			_PREFERENCE_KEY_SELECTED_GROUP_EXTERNAL_REFERENCE_CODE, null);

		if (!Validator.isBlank(rootFolderExternalReferenceCode)) {
			try {
				Folder folder = _getFolder(
					rootFolderExternalReferenceCode,
					selectedGroupExternalReferenceCode, portletDataContext);

				if (folder != null) {
					String selectedRepositoryExternalReferenceCode =
						_getRepositoryExternalReferenceCode(folder);

					portletPreferences.setValue(
						_PREFERENCE_KEY_SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE,
						selectedRepositoryExternalReferenceCode);

					if ((folder.getGroupId() ==
							portletDataContext.getGroupId()) ||
						!ExportImportThreadLocal.isStagingInProcess()) {

						StagedModelDataHandlerUtil.exportReferenceStagedModel(
							portletDataContext,
							portletDataContext.getPortletId(), folder);
					}
					else {
						_saveStagingPreferencesMapping(
							portletDataContext, rootFolderExternalReferenceCode,
							selectedGroupExternalReferenceCode,
							selectedRepositoryExternalReferenceCode);
					}
				}

				return portletPreferences;
			}
			catch (ReadOnlyException readOnlyException) {
				throw new PortletDataException(
					"Unable to update portlet preferences during import",
					readOnlyException);
			}
		}

		if (!Validator.isBlank(selectedGroupExternalReferenceCode)) {
			Group selectedGroup =
				_groupLocalService.fetchGroupByExternalReferenceCode(
					selectedGroupExternalReferenceCode,
					portletDataContext.getCompanyId());

			if (!_exportImportHelper.isExportPortletData(portletDataContext) ||
				(selectedGroup.getGroupId() !=
					portletDataContext.getGroupId())) {

				if (ExportImportThreadLocal.isStagingInProcess()) {
					_saveStagingPreferencesMapping(
						portletDataContext, null,
						selectedGroupExternalReferenceCode,
						portletPreferences.getValue(
							_PREFERENCE_KEY_SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE,
							null));
				}

				return portletPreferences;
			}
		}

		// Root folder external reference code is not set, we need to export
		// everything

		try {
			portletDataContext.addPortletPermissions(DLConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			PortletDataException portletDataException =
				new PortletDataException(portalException);

			portletDataException.setPortletId(DLPortletKeys.DOCUMENT_LIBRARY);
			portletDataException.setType(
				PortletDataException.EXPORT_PORTLET_PERMISSIONS);

			throw portletDataException;
		}

		try {
			if (portletDataContext.getBooleanParameter(
					_dlPortletDataHandler.getNamespace(), "folders")) {

				StagedModelRepository<?> stagedModelRepository =
					StagedModelRepositoryRegistryUtil.getStagedModelRepository(
						DLFolder.class.getName());

				ActionableDynamicQuery folderActionableDynamicQuery =
					stagedModelRepository.getExportActionableDynamicQuery(
						portletDataContext);

				folderActionableDynamicQuery.setPerformActionMethod(
					(DLFolder dlFolder) -> {
						if (dlFolder.isInTrash()) {
							return;
						}

						StagedModelDataHandlerUtil.exportReferenceStagedModel(
							portletDataContext,
							portletDataContext.getPortletId(),
							_dlAppLocalService.getFolder(
								dlFolder.getFolderId()));
					});

				folderActionableDynamicQuery.performActions();
			}

			if (portletDataContext.getBooleanParameter(
					_dlPortletDataHandler.getNamespace(), "documents")) {

				StagedModelRepository<?> stagedModelRepository =
					StagedModelRepositoryRegistryUtil.getStagedModelRepository(
						DLFileEntry.class.getName());

				ActionableDynamicQuery fileEntryActionableDynamicQuery =
					stagedModelRepository.getExportActionableDynamicQuery(
						portletDataContext);

				fileEntryActionableDynamicQuery.setPerformActionMethod(
					(DLFileEntry dlFileEntry) ->
						StagedModelDataHandlerUtil.exportReferenceStagedModel(
							portletDataContext,
							portletDataContext.getPortletId(),
							_dlAppLocalService.getFileEntry(
								dlFileEntry.getFileEntryId())));

				fileEntryActionableDynamicQuery.performActions();
			}

			if (portletDataContext.getBooleanParameter(
					_dlPortletDataHandler.getNamespace(), "document-types")) {

				StagedModelRepository<?> stagedModelRepository =
					StagedModelRepositoryRegistryUtil.getStagedModelRepository(
						DLFileEntryType.class.getName());

				ActionableDynamicQuery fileEntryTypeActionableDynamicQuery =
					stagedModelRepository.getExportActionableDynamicQuery(
						portletDataContext);

				fileEntryTypeActionableDynamicQuery.setPerformActionMethod(
					(DLFileEntryType dlFileEntryType) -> {
						if (dlFileEntryType.isExportable()) {
							StagedModelDataHandlerUtil.
								exportReferenceStagedModel(
									portletDataContext,
									portletDataContext.getPortletId(),
									dlFileEntryType);
						}
					});

				fileEntryTypeActionableDynamicQuery.performActions();
			}

			if (portletDataContext.getBooleanParameter(
					_dlPortletDataHandler.getNamespace(), "repositories")) {

				StagedModelRepository<?> stagedModelRepository =
					StagedModelRepositoryRegistryUtil.getStagedModelRepository(
						Repository.class.getName());

				ActionableDynamicQuery repositoryActionableDynamicQuery =
					stagedModelRepository.getExportActionableDynamicQuery(
						portletDataContext);

				repositoryActionableDynamicQuery.setPerformActionMethod(
					(Repository repository) ->
						StagedModelDataHandlerUtil.exportReferenceStagedModel(
							portletDataContext,
							portletDataContext.getPortletId(), repository));

				repositoryActionableDynamicQuery.performActions();
			}

			if (portletDataContext.getBooleanParameter(
					_dlPortletDataHandler.getNamespace(), "shortcuts")) {

				StagedModelRepository<?> stagedModelRepository =
					StagedModelRepositoryRegistryUtil.getStagedModelRepository(
						DLFileShortcut.class.getName());

				ActionableDynamicQuery fileShortcutActionableDynamicQuery =
					stagedModelRepository.getExportActionableDynamicQuery(
						portletDataContext);

				fileShortcutActionableDynamicQuery.setPerformActionMethod(
					(DLFileShortcut dlFileShortcut) ->
						StagedModelDataHandlerUtil.exportReferenceStagedModel(
							portletDataContext,
							portletDataContext.getPortletId(),
							_dlAppLocalService.getFileShortcut(
								dlFileShortcut.getFileShortcutId())));

				fileShortcutActionableDynamicQuery.performActions();
			}
		}
		catch (PortalException portalException) {
			PortletDataException portletDataException =
				new PortletDataException(portalException);

			portletDataException.setPortletId(DLPortletKeys.DOCUMENT_LIBRARY);
			portletDataException.setType(
				PortletDataException.EXPORT_PORTLET_DATA);

			throw portletDataException;
		}

		return portletPreferences;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		JSONObject stagingPreferencesMappingJSONObject =
			_fetchStagingPreferencesMappingJSONObject(portletDataContext);

		if (stagingPreferencesMappingJSONObject != null) {
			try {
				portletPreferences.setValue(
					_PREFERENCE_KEY_ROOT_FOLDER_EXTERNAL_REFERENCE_CODE,
					stagingPreferencesMappingJSONObject.getString(
						_PREFERENCE_KEY_ROOT_FOLDER_EXTERNAL_REFERENCE_CODE));
				portletPreferences.setValue(
					_PREFERENCE_KEY_SELECTED_GROUP_EXTERNAL_REFERENCE_CODE,
					stagingPreferencesMappingJSONObject.getString(
						_PREFERENCE_KEY_SELECTED_GROUP_EXTERNAL_REFERENCE_CODE));
				portletPreferences.setValue(
					_PREFERENCE_KEY_SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE,
					stagingPreferencesMappingJSONObject.getString(
						_PREFERENCE_KEY_SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE));

				return portletPreferences;
			}
			catch (ReadOnlyException readOnlyException) {
				throw new PortletDataException(readOnlyException);
			}
		}

		// Root folder external reference code is set, only import that

		String rootFolderExternalReferenceCode = portletPreferences.getValue(
			_PREFERENCE_KEY_ROOT_FOLDER_EXTERNAL_REFERENCE_CODE, null);

		if (!Validator.isBlank(rootFolderExternalReferenceCode)) {
			Element foldersElement =
				portletDataContext.getImportDataGroupElement(DLFolder.class);

			List<Element> folderElements = foldersElement.elements();

			if (!folderElements.isEmpty()) {
				try {
					Element folderElement = folderElements.get(0);

					StagedModelDataHandlerUtil.importStagedModel(
						portletDataContext, folderElement);

					Map<Long, Long> folderIds =
						(Map<Long, Long>)
							portletDataContext.getNewPrimaryKeysMap(
								Folder.class +
									".folderIdsAndRepositoryEntryIds");

					long rootFolderId = _getFolderId(
						folderElement, portletDataContext);

					long importedRootFolderId = MapUtil.getLong(
						folderIds, rootFolderId, rootFolderId);

					Folder folder = _getFolder(
						importedRootFolderId, portletDataContext);

					if (folder != null) {
						portletPreferences.setValue(
							_PREFERENCE_KEY_ROOT_FOLDER_EXTERNAL_REFERENCE_CODE,
							folder.getExternalReferenceCode());

						portletPreferences.setValue(
							_PREFERENCE_KEY_SELECTED_GROUP_EXTERNAL_REFERENCE_CODE,
							_getGroupExternalReferenceCode(folder));

						portletPreferences.setValue(
							_PREFERENCE_KEY_SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE,
							_getRepositoryExternalReferenceCode(folder));
					}

					return portletPreferences;
				}
				catch (ReadOnlyException readOnlyException) {
					throw new PortletDataException(
						"Unable to update portlet preferences during import",
						readOnlyException);
				}
			}
		}

		// Root folder external reference code is not set, need to import
		// everything

		try {
			portletDataContext.importPortletPermissions(
				DLConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			PortletDataException portletDataException =
				new PortletDataException(portalException);

			portletDataException.setPortletId(DLPortletKeys.DOCUMENT_LIBRARY);
			portletDataException.setType(
				PortletDataException.IMPORT_PORTLET_PERMISSIONS);

			throw portletDataException;
		}

		if (portletDataContext.getBooleanParameter(
				_dlPortletDataHandler.getNamespace(), "folders")) {

			Element foldersElement =
				portletDataContext.getImportDataGroupElement(DLFolder.class);

			for (Element folderElement : foldersElement.elements()) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, folderElement);
			}
		}

		if (portletDataContext.getBooleanParameter(
				_dlPortletDataHandler.getNamespace(), "documents")) {

			Element fileEntriesElement =
				portletDataContext.getImportDataGroupElement(DLFileEntry.class);

			for (Element fileEntryElement : fileEntriesElement.elements()) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, fileEntryElement);
			}
		}

		if (portletDataContext.getBooleanParameter(
				_dlPortletDataHandler.getNamespace(), "document-types")) {

			Element fileEntryTypesElement =
				portletDataContext.getImportDataGroupElement(
					DLFileEntryType.class);

			for (Element fileEntryTypeElement :
					fileEntryTypesElement.elements()) {

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, fileEntryTypeElement);
			}
		}

		if (portletDataContext.getBooleanParameter(
				_dlPortletDataHandler.getNamespace(), "repositories")) {

			Element repositoriesElement =
				portletDataContext.getImportDataGroupElement(Repository.class);

			for (Element repositoryElement : repositoriesElement.elements()) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, repositoryElement);
			}
		}

		if (portletDataContext.getBooleanParameter(
				_dlPortletDataHandler.getNamespace(), "shortcuts")) {

			Element fileShortcutsElement =
				portletDataContext.getImportDataGroupElement(
					DLFileShortcut.class);

			for (Element fileShortcutElement :
					fileShortcutsElement.elements()) {

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, fileShortcutElement);
			}
		}

		return portletPreferences;
	}

	private JSONObject _fetchStagingPreferencesMappingJSONObject(
			PortletDataContext portletDataContext)
		throws PortletDataException {

		try {
			String stagingPreferencesMappingJSON =
				portletDataContext.getZipEntryAsString(
					String.format(
						"%s/staging-preferences-mapping.json",
						portletDataContext.getPortletId()));

			if (Validator.isNull(stagingPreferencesMappingJSON)) {
				return null;
			}

			return _jsonFactory.createJSONObject(stagingPreferencesMappingJSON);
		}
		catch (JSONException jsonException) {
			throw new PortletDataException(jsonException);
		}
	}

	private Folder _getFolder(
			long folderId, PortletDataContext portletDataContext)
		throws PortletDataException {

		Folder folder = null;

		try {
			folder = _dlAppLocalService.getFolder(folderId);

			DLFolder dlFolder = _dlFolderLocalService.getDLFolder(folderId);

			if (dlFolder.isInTrash()) {
				return null;
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Portlet ", portletDataContext.getPortletId(),
						" refers to an invalid root folder ID ", folderId),
					portalException);
			}
		}

		return folder;
	}

	private Folder _getFolder(
			String folderExternalReferenceCode,
			String groupExternalReferenceCode,
			PortletDataContext portletDataContext)
		throws PortletDataException {

		try {
			Group group = _groupLocalService.getGroupByExternalReferenceCode(
				groupExternalReferenceCode, portletDataContext.getCompanyId());

			DLFolder dlFolder =
				_dlFolderLocalService.getDLFolderByExternalReferenceCode(
					folderExternalReferenceCode, group.getGroupId());

			if (!dlFolder.isInTrash()) {
				return new LiferayFolder(dlFolder);
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Portlet ", portletDataContext.getPortletId(),
						" refers to an invalid root folder external reference ",
						"code ", folderExternalReferenceCode,
						" with group external reference code ",
						groupExternalReferenceCode),
					portalException);
			}
		}

		return null;
	}

	private long _getFolderId(
		Element folderElement, PortletDataContext portletDataContext) {

		Folder folder = (Folder)portletDataContext.getZipEntryAsObject(
			folderElement, folderElement.attributeValue("path"));

		return folder.getFolderId();
	}

	private String _getGroupExternalReferenceCode(Folder folder) {
		Group group = _groupLocalService.fetchGroup(folder.getGroupId());

		if (group == null) {
			return StringPool.BLANK;
		}

		return group.getExternalReferenceCode();
	}

	private String _getMirrorGroupExternalReferenceCode(
		PortletDataContext portletDataContext,
		String groupExternalReferenceCode) {

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			groupExternalReferenceCode, portletDataContext.getCompanyId());

		if (group == null) {
			return groupExternalReferenceCode;
		}

		Group stagingGroup = group.getStagingGroup();

		if (stagingGroup != null) {
			return stagingGroup.getExternalReferenceCode();
		}

		if (ExportImportThreadLocal.isStagingInProcess() &&
			group.isStagedRemotely()) {

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			String remoteGroupExternalReferenceCode =
				typeSettingsUnicodeProperties.get(
					"remoteGroupExternalReferenceCode");

			if (Validator.isNull(remoteGroupExternalReferenceCode)) {
				remoteGroupExternalReferenceCode =
					_getRemoteGroupExternalReferenceCode(
						typeSettingsUnicodeProperties);
			}

			if (Validator.isNotNull(remoteGroupExternalReferenceCode)) {
				groupExternalReferenceCode = remoteGroupExternalReferenceCode;
			}
		}

		Group liveGroup = _groupLocalService.fetchGroup(group.getLiveGroupId());

		if (liveGroup == null) {
			return groupExternalReferenceCode;
		}

		return liveGroup.getExternalReferenceCode();
	}

	private String _getRemoteGroupExternalReferenceCode(
		UnicodeProperties typeSettingsUnicodeProperties) {

		String remoteAddress = GetterUtil.getString(
			typeSettingsUnicodeProperties.get("remoteAddress"));
		long remoteGroupId = GetterUtil.getLong(
			typeSettingsUnicodeProperties.get("remoteGroupId"));

		if (Validator.isNull(remoteAddress) || (remoteGroupId <= 0)) {
			return null;
		}

		int remotePort = GetterUtil.getInteger(
			typeSettingsUnicodeProperties.get("remotePort"));
		String remotePathContext = GetterUtil.getString(
			typeSettingsUnicodeProperties.get("remotePathContext"));
		boolean secureConnection = GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.get("secureConnection"));

		String remoteURL = StagingURLHelperUtil.buildRemoteURL(
			remoteAddress, remotePort, remotePathContext, secureConnection);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		User user = permissionChecker.getUser();

		try {
			HttpPrincipal httpPrincipal = new HttpPrincipal(
				remoteURL, user.getLogin(), user.getPassword(),
				user.isPasswordEncrypted());

			try (SafeCloseable safeCloseable =
					ThreadContextClassLoaderUtil.swap(
						PortalClassLoaderUtil.getClassLoader())) {

				Group group = GroupServiceHttp.getGroup(
					httpPrincipal, remoteGroupId);

				return group.getExternalReferenceCode();
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private String _getRepositoryExternalReferenceCode(Folder folder) {
		Repository repository = _repositoryLocalService.fetchRepository(
			folder.getRepositoryId());

		if (repository == null) {
			return StringPool.BLANK;
		}

		return repository.getExternalReferenceCode();
	}

	private void _saveStagingPreferencesMapping(
		PortletDataContext portletDataContext,
		String rootFolderExternalReferenceCode,
		String selectedGroupExternalReferenceCode,
		String selectedRepositoryExternalReferenceCode) {

		if (ExportImportThreadLocal.isStagingInProcess()) {
			portletDataContext.addZipEntry(
				String.format(
					"%s/staging-preferences-mapping.json",
					portletDataContext.getPortletId()),
				JSONUtil.put(
					_PREFERENCE_KEY_ROOT_FOLDER_EXTERNAL_REFERENCE_CODE,
					rootFolderExternalReferenceCode
				).put(
					_PREFERENCE_KEY_SELECTED_GROUP_EXTERNAL_REFERENCE_CODE,
					_getMirrorGroupExternalReferenceCode(
						portletDataContext, selectedGroupExternalReferenceCode)
				).put(
					_PREFERENCE_KEY_SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE,
					selectedRepositoryExternalReferenceCode
				).toString());
		}
	}

	private static final String
		_PREFERENCE_KEY_ROOT_FOLDER_EXTERNAL_REFERENCE_CODE =
			"rootFolderExternalReferenceCode";

	private static final String
		_PREFERENCE_KEY_SELECTED_GROUP_EXTERNAL_REFERENCE_CODE =
			"selectedGroupExternalReferenceCode";

	private static final String
		_PREFERENCE_KEY_SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE =
			"selectedRepositoryExternalReferenceCode";

	private static final Log _log = LogFactoryUtil.getLog(
		DLExportImportPortletPreferencesProcessor.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference(
		target = "(component.name=com.liferay.document.library.web.internal.exportimport.portlet.preferences.processor.DLCommentsAndRatingsExporterImporterCapability)"
	)
	private Capability _dlCommentsAndRatingsExporterImporterCapability;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference(
		target = "(jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY + ")"
	)
	private PortletDataHandler _dlPortletDataHandler;

	@Reference(target = "(name=PortletDisplayTemplateExporter)")
	private Capability _exportCapability;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = "(name=PortletDisplayTemplateImporter)")
	private Capability _importCapability;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

}