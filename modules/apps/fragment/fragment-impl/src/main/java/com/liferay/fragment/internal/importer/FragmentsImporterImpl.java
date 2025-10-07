/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.importer;

import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentExportImportConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.exception.DuplicateFragmentCollectionKeyException;
import com.liferay.fragment.exception.DuplicateFragmentCompositionKeyException;
import com.liferay.fragment.exception.DuplicateFragmentEntryKeyException;
import com.liferay.fragment.exception.FragmentCollectionNameException;
import com.liferay.fragment.importer.FragmentsImportStrategy;
import com.liferay.fragment.importer.FragmentsImporter;
import com.liferay.fragment.importer.FragmentsImporterResultEntry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentCompositionLocalService;
import com.liferay.fragment.service.FragmentCompositionService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.fragment.validator.FragmentEntryValidator;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentsImporter.class)
public class FragmentsImporterImpl implements FragmentsImporter {

	@Override
	public List<FragmentsImporterResultEntry> importFragmentEntries(
			long userId, long groupId, long fragmentCollectionId, File file,
			FragmentsImportStrategy fragmentsImportStrategy,
			boolean marketplace)
		throws Exception {

		_fragmentsImporterResultEntries = new ArrayList<>();

		try (ZipFile zipFile = new ZipFile(file)) {
			Map<String, String> orphanFragmentCompositions = new HashMap<>();
			Map<String, String> orphanFragmentEntries = new HashMap<>();
			Map<String, String> resourceReferences = new HashMap<>();

			Map<String, FragmentCollectionFolder> fragmentCollectionFolderMap =
				_getFragmentCollectionFolderMap(
					zipFile, groupId, orphanFragmentCompositions,
					orphanFragmentEntries);

			for (Map.Entry<String, FragmentCollectionFolder> entry :
					fragmentCollectionFolderMap.entrySet()) {

				FragmentCollectionFolder fragmentCollectionFolder =
					entry.getValue();

				String name = entry.getKey();
				String description = StringPool.BLANK;

				String collectionJSON = _getContent(
					zipFile, fragmentCollectionFolder.getFileName());

				if (Validator.isNotNull(collectionJSON)) {
					JSONObject jsonObject = _jsonFactory.createJSONObject(
						collectionJSON);

					name = jsonObject.getString("name");
					description = jsonObject.getString("description");
				}

				if (Validator.isNull(name)) {
					throw new FragmentCollectionNameException();
				}

				FragmentCollection fragmentCollection = _addFragmentCollection(
					groupId, entry.getKey(), name, description,
					fragmentsImportStrategy, marketplace);

				_importResources(
					userId, groupId, fragmentCollection, entry.getKey(),
					zipFile, resourceReferences);

				_importFragmentCompositions(
					userId, groupId, zipFile,
					fragmentCollection.getFragmentCollectionId(),
					fragmentCollectionFolder.getFragmentCompositions(),
					fragmentsImportStrategy);

				_importFragmentEntries(
					userId, groupId, zipFile,
					fragmentCollection.getFragmentCollectionId(),
					fragmentCollectionFolder.getFragmentEntries(),
					resourceReferences, fragmentsImportStrategy, marketplace);
			}

			if (MapUtil.isNotEmpty(orphanFragmentCompositions) ||
				MapUtil.isNotEmpty(orphanFragmentEntries)) {

				FragmentCollection fragmentCollection =
					_fragmentCollectionService.fetchFragmentCollection(
						fragmentCollectionId);

				if (fragmentCollection == null) {
					fragmentCollection = _getDefaultFragmentCollection(groupId);
				}

				_importFragmentCompositions(
					userId, groupId, zipFile,
					fragmentCollection.getFragmentCollectionId(),
					orphanFragmentCompositions, fragmentsImportStrategy);

				_importFragmentEntries(
					userId, groupId, zipFile,
					fragmentCollection.getFragmentCollectionId(),
					orphanFragmentEntries, resourceReferences,
					fragmentsImportStrategy, marketplace);
			}
		}

		return _fragmentsImporterResultEntries;
	}

	@Override
	public boolean validateFragmentEntries(
			long userId, long groupId, long fragmentCollectionId, File file)
		throws Exception {

		try (ZipFile zipFile = new ZipFile(file)) {
			Map<String, String> orphanFragmentCompositions = new HashMap<>();
			Map<String, String> orphanFragmentEntries = new HashMap<>();

			Map<String, FragmentCollectionFolder> fragmentCollectionFolderMap =
				_getFragmentCollectionFolderMap(
					zipFile, groupId, orphanFragmentCompositions,
					orphanFragmentEntries);

			for (Map.Entry<String, FragmentCollectionFolder>
					fragmentCollectionFolder :
						fragmentCollectionFolderMap.entrySet()) {

				FragmentCollection fragmentCollection =
					_fragmentCollectionLocalService.fetchFragmentCollection(
						groupId, fragmentCollectionFolder.getKey());

				if (fragmentCollection != null) {
					return false;
				}

				boolean valid = _validateFragmentCompositions(
					groupId, orphanFragmentCompositions);

				if (!valid) {
					return false;
				}

				valid = _validateFragmentEntries(
					groupId, orphanFragmentEntries);

				if (!valid) {
					return false;
				}
			}

			if (MapUtil.isNotEmpty(orphanFragmentCompositions) ||
				MapUtil.isNotEmpty(orphanFragmentEntries)) {

				boolean valid = _validateFragmentCompositions(
					groupId, orphanFragmentCompositions);

				if (!valid) {
					return false;
				}

				valid = _validateFragmentEntries(
					groupId, orphanFragmentEntries);

				if (!valid) {
					return false;
				}
			}
		}

		return true;
	}

	private FragmentCollection _addFragmentCollection(
			long groupId, String fragmentCollectionKey, String name,
			String description, FragmentsImportStrategy fragmentsImportStrategy,
			boolean marketplace)
		throws Exception {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.fetchFragmentCollection(
				groupId, fragmentCollectionKey);

		if (fragmentCollection == null) {
			fragmentCollection =
				_fragmentCollectionService.addFragmentCollection(
					null, groupId, fragmentCollectionKey, name, description,
					marketplace, ServiceContextThreadLocal.getServiceContext());
		}
		else if (Objects.equals(
					FragmentsImportStrategy.DO_NOT_IMPORT,
					fragmentsImportStrategy)) {

			return fragmentCollection;
		}
		else if (Objects.equals(
					FragmentsImportStrategy.KEEP_BOTH,
					fragmentsImportStrategy)) {

			fragmentCollection =
				_fragmentCollectionService.addFragmentCollection(
					null, groupId,
					_fragmentCollectionLocalService.
						generateFragmentCollectionKey(
							groupId, fragmentCollectionKey),
					_fragmentCollectionLocalService.
						getUniqueFragmentCollectionName(groupId, name),
					description, marketplace,
					ServiceContextThreadLocal.getServiceContext());
		}
		else if (Objects.equals(
					FragmentsImportStrategy.OVERWRITE,
					fragmentsImportStrategy)) {

			fragmentCollection =
				_fragmentCollectionService.updateFragmentCollection(
					fragmentCollection.getFragmentCollectionId(), name,
					description);
		}
		else {
			throw new DuplicateFragmentCollectionKeyException(
				fragmentCollectionKey);
		}

		return fragmentCollection;
	}

	private void _addFragmentEntry(
			long groupId, String fileName, FragmentEntry fragmentEntry,
			long fragmentCollectionId, String fragmentEntryKey, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, boolean marketplace,
			boolean readOnly, String thumbnailPath, String typeLabel,
			String typeOptions, FragmentsImportStrategy fragmentsImportStrategy,
			long userId, ZipFile zipFile)
		throws Exception {

		if (fragmentEntry != null) {
			if (Objects.equals(
					FragmentsImportStrategy.DO_NOT_IMPORT,
					fragmentsImportStrategy)) {

				return;
			}

			if (Objects.equals(
					FragmentsImportStrategy.DO_NOT_OVERWRITE,
					fragmentsImportStrategy)) {

				throw new DuplicateFragmentEntryKeyException(fragmentEntryKey);
			}
		}

		JSONObject configurationJSONObject = _jsonFactory.safeCreateJSONObject(
			configuration, true);
		String errorMessage = null;
		int type = FragmentConstants.getTypeFromLabel(
			StringUtil.toLowerCase(StringUtil.trim(typeLabel)));
		int status = WorkflowConstants.STATUS_APPROVED;

		try {
			_fragmentEntryProcessorRegistry.validateFragmentEntryHTML(
				html, configurationJSONObject);

			_fragmentEntryValidator.validateConfiguration(
				configurationJSONObject);
			_fragmentEntryValidator.validateTypeOptions(type, typeOptions);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			if (type == FragmentConstants.TYPE_REACT) {
				_fragmentsImporterResultEntries.add(
					new FragmentsImporterResultEntry(
						name, FragmentsImporterResultEntry.Status.INVALID,
						FragmentsImporterResultEntry.Type.FRAGMENT,
						portalException.getMessage()));

				return;
			}

			errorMessage = portalException.getLocalizedMessage();
			status = WorkflowConstants.STATUS_DRAFT;
		}

		try {
			if (fragmentEntry == null) {
				fragmentEntry = _fragmentEntryService.addFragmentEntry(
					null, groupId, fragmentCollectionId, fragmentEntryKey, name,
					css, html, js, cacheable, configuration, icon, 0,
					marketplace, readOnly, type, typeOptions, status,
					ServiceContextThreadLocal.getServiceContext());

				_fragmentEntryLocalService.updateFragmentEntry(
					fragmentEntry.getFragmentEntryId(),
					_getPreviewFileEntryId(
						userId, groupId, zipFile, FragmentEntry.class.getName(),
						fragmentEntry.getFragmentEntryId(), fileName,
						thumbnailPath));
			}
			else if (Objects.equals(
						FragmentsImportStrategy.KEEP_BOTH,
						fragmentsImportStrategy)) {

				fragmentEntry = _fragmentEntryService.addFragmentEntry(
					null, groupId, fragmentCollectionId,
					_fragmentEntryLocalService.generateFragmentEntryKey(
						groupId, fragmentEntryKey),
					_fragmentEntryLocalService.getUniqueFragmentEntryName(
						groupId, fragmentCollectionId, name),
					css, html, js, cacheable, configuration, icon, 0,
					marketplace, readOnly, type, typeOptions, status,
					ServiceContextThreadLocal.getServiceContext());

				_fragmentEntryLocalService.updateFragmentEntry(
					fragmentEntry.getFragmentEntryId(),
					_getPreviewFileEntryId(
						userId, groupId, zipFile, FragmentEntry.class.getName(),
						fragmentEntry.getFragmentEntryId(), fileName,
						thumbnailPath));
			}
			else {
				if (fragmentEntry.getPreviewFileEntryId() > 0) {
					PortletFileRepositoryUtil.deletePortletFileEntry(
						fragmentEntry.getPreviewFileEntryId());
				}

				fragmentEntry = _fragmentEntryService.updateFragmentEntry(
					fragmentEntry.getFragmentEntryId(), fragmentCollectionId,
					name, css, html, js, cacheable, configuration, icon,
					_getPreviewFileEntryId(
						userId, groupId, zipFile, FragmentEntry.class.getName(),
						fragmentEntry.getFragmentEntryId(), fileName,
						thumbnailPath),
					readOnly, typeOptions, status);
			}

			FragmentsImporterResultEntry.Status
				fragmentsImporterResultEntryStatus =
					FragmentsImporterResultEntry.Status.IMPORTED;

			if (fragmentEntry.getStatus() == WorkflowConstants.STATUS_DRAFT) {
				fragmentsImporterResultEntryStatus =
					FragmentsImporterResultEntry.Status.IMPORTED_DRAFT;
			}

			_fragmentsImporterResultEntries.add(
				new FragmentsImporterResultEntry(
					name, fragmentsImporterResultEntryStatus,
					FragmentsImporterResultEntry.Type.FRAGMENT, errorMessage));
		}
		catch (PortalException portalException) {
			_fragmentsImporterResultEntries.add(
				new FragmentsImporterResultEntry(
					name, FragmentsImporterResultEntry.Status.INVALID,
					FragmentsImporterResultEntry.Type.FRAGMENT,
					portalException.getMessage()));
		}
	}

	private void _addPortletFileEntriesWithFolders(
			long userId, long groupId, FragmentCollection fragmentCollection,
			ZipFile zipFile, Map<String, String> resourceReferences,
			Map<String, String> zipEntryNames, Repository repository)
		throws Exception {

		Map<String, Long> folderIdsMap = HashMapBuilder.put(
			StringPool.BLANK, fragmentCollection.getResourcesFolderId()
		).build();

		if (repository != null) {
			FragmentServiceConfiguration fragmentServiceConfiguration =
				_configurationProvider.getCompanyConfiguration(
					FragmentServiceConfiguration.class,
					fragmentCollection.getCompanyId());

			Map<String, FileEntry> resources =
				fragmentCollection.getResourcesMap();

			for (Map.Entry<String, FileEntry> entry : resources.entrySet()) {
				String fileEntryPath = entry.getKey();

				if (zipEntryNames.containsKey(fileEntryPath)) {
					FileEntry fileEntry = entry.getValue();

					if (fragmentServiceConfiguration.propagateChanges()) {
						PortletFileRepositoryUtil.deletePortletFileEntry(
							fileEntry.getFileEntryId());

						String fileName = entry.getKey();
						String folderPath = StringPool.BLANK;

						int index = fileName.lastIndexOf(StringPool.SLASH);

						if (index != -1) {
							folderPath = fileName.substring(0, index);
							fileName = fileName.substring(index + 1);
						}

						PortletFileRepositoryUtil.addPortletFileEntry(
							null, groupId, userId,
							FragmentCollection.class.getName(),
							fragmentCollection.getFragmentCollectionId(),
							FragmentPortletKeys.FRAGMENT,
							_getOrCreateFolderId(
								folderIdsMap, folderPath,
								repository.getRepositoryId(), userId),
							_getInputStream(
								zipFile, zipEntryNames.get(fileName)),
							fileName, MimeTypesUtil.getContentType(fileName),
							false);

						zipEntryNames.remove(fileEntry.getFileName());
					}
					else {
						String folderPath = StringPool.BLANK;

						int index = fileEntryPath.lastIndexOf(StringPool.SLASH);

						if (index != -1) {
							folderPath = fileEntryPath.substring(0, index);
						}

						String newFileName =
							PortletFileRepositoryUtil.getUniqueFileName(
								fileEntry.getGroupId(), fileEntry.getFolderId(),
								fileEntry.getFileName());

						resourceReferences.put(
							fileEntryPath, folderPath + newFileName);

						zipEntryNames.put(
							folderPath + newFileName,
							zipEntryNames.get(fileEntryPath));

						zipEntryNames.remove(fileEntryPath);
					}
				}
			}
		}
		else {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			repository = PortletFileRepositoryUtil.addPortletRepository(
				groupId, FragmentPortletKeys.FRAGMENT, serviceContext);
		}

		for (Map.Entry<String, String> entry : zipEntryNames.entrySet()) {
			String fileName = entry.getKey();
			String folderPath = StringPool.BLANK;

			int index = fileName.lastIndexOf(StringPool.SLASH);

			if (index != -1) {
				folderPath = fileName.substring(0, index);
				fileName = fileName.substring(index + 1);
			}

			PortletFileRepositoryUtil.addPortletFileEntry(
				null, groupId, userId, FragmentCollection.class.getName(),
				fragmentCollection.getFragmentCollectionId(),
				FragmentPortletKeys.FRAGMENT,
				_getOrCreateFolderId(
					folderIdsMap, folderPath, repository.getRepositoryId(),
					userId),
				_getInputStream(zipFile, entry.getValue()), fileName,
				MimeTypesUtil.getContentType(fileName), false);
		}
	}

	private String _getContent(ZipFile zipFile, String fileName)
		throws Exception {

		ZipEntry zipEntry = zipFile.getEntry(fileName);

		if (zipEntry == null) {
			return StringPool.BLANK;
		}

		return StringUtil.read(zipFile.getInputStream(zipEntry));
	}

	private FragmentCollection _getDefaultFragmentCollection(long groupId)
		throws PortalException {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.fetchFragmentCollection(
				groupId, _FRAGMENT_COLLECTION_KEY_DEFAULT);

		if (fragmentCollection != null) {
			return fragmentCollection;
		}

		return _fragmentCollectionService.addFragmentCollection(
			null, groupId, _FRAGMENT_COLLECTION_KEY_DEFAULT,
			_language.get(
				_portal.getSiteDefaultLocale(groupId),
				_FRAGMENT_COLLECTION_KEY_DEFAULT),
			StringPool.BLANK, false,
			ServiceContextThreadLocal.getServiceContext());
	}

	private String _getFileName(String path) {
		int index = path.lastIndexOf(CharPool.SLASH);

		if (index > 0) {
			return path.substring(index + 1);
		}

		return path;
	}

	private String _getFilePath(String fragmentCollectionKey, String path) {
		path = StringUtil.removeFirst(
			path, fragmentCollectionKey + StringPool.SLASH);

		int index = path.indexOf("resources/");

		path = path.substring(index);

		return StringUtil.removeFirst(path, "resources/");
	}

	private Map<String, FragmentCollectionFolder>
			_getFragmentCollectionFolderMap(
				ZipFile zipFile, long groupId,
				Map<String, String> orphanFragmentCompositions,
				Map<String, String> orphanFragmentEntries)
		throws Exception {

		Map<String, FragmentCollectionFolder> fragmentCollectionFolderMap =
			new HashMap<>();

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if (zipEntry.isDirectory()) {
				continue;
			}

			String fileName = zipEntry.getName();

			if (!_isFragmentCollection(fileName)) {
				continue;
			}

			fragmentCollectionFolderMap.put(
				_getKey(zipFile, groupId, fileName),
				new FragmentCollectionFolder(fileName));
		}

		enumeration = zipFile.entries();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if (zipEntry.isDirectory()) {
				continue;
			}

			String fileName = zipEntry.getName();

			if (!_isFragmentComposition(fileName) &&
				!_isFragmentEntry(fileName)) {

				continue;
			}

			String fragmentCollectionPath = fileName;

			String fragmentCollectionKey = StringPool.BLANK;

			while (fragmentCollectionPath.length() > 0) {
				fragmentCollectionPath = fragmentCollectionPath.substring(
					0,
					fragmentCollectionPath.lastIndexOf(StringPool.SLASH) + 1);

				String fragmentCollectionFileName =
					fragmentCollectionPath +
						FragmentExportImportConstants.FILE_NAME_COLLECTION;

				ZipEntry fragmentCollectionZipEntry = zipFile.getEntry(
					fragmentCollectionFileName);

				if (fragmentCollectionZipEntry != null) {
					fragmentCollectionKey = _getKey(
						zipFile, groupId, fragmentCollectionFileName);

					break;
				}

				if (Validator.isNull(fragmentCollectionPath)) {
					break;
				}

				fragmentCollectionPath = fragmentCollectionPath.substring(
					0, fragmentCollectionPath.lastIndexOf(StringPool.SLASH));
			}

			if (Validator.isNull(fragmentCollectionKey) &&
				_isFragmentComposition(fileName)) {

				orphanFragmentCompositions.put(
					_getKey(zipFile, groupId, fileName), fileName);

				continue;
			}
			else if (Validator.isNull(fragmentCollectionKey) &&
					 _isFragmentEntry(fileName)) {

				orphanFragmentEntries.put(
					_getKey(zipFile, groupId, fileName), fileName);

				continue;
			}

			FragmentCollectionFolder fragmentCollectionFolder =
				fragmentCollectionFolderMap.get(fragmentCollectionKey);

			if ((fragmentCollectionFolder == null) &&
				_isFragmentComposition(fileName)) {

				orphanFragmentCompositions.put(
					_getKey(zipFile, groupId, fileName), fileName);

				continue;
			}
			else if ((fragmentCollectionFolder == null) &&
					 _isFragmentEntry(fileName)) {

				orphanFragmentEntries.put(
					_getKey(zipFile, groupId, fileName), fileName);

				continue;
			}

			if (_isFragmentComposition(fileName)) {
				fragmentCollectionFolder.addFragmentComposition(
					_getKey(zipFile, groupId, fileName), fileName);
			}
			else {
				fragmentCollectionFolder.addFragmentEntry(
					_getKey(zipFile, groupId, fileName), fileName);
			}
		}

		return fragmentCollectionFolderMap;
	}

	private String _getFragmentEntryContent(
			ZipFile zipFile, String fileName, String contentPath)
		throws Exception {

		InputStream inputStream = _getFragmentEntryInputStream(
			zipFile, fileName, contentPath);

		if (inputStream == null) {
			return StringPool.BLANK;
		}

		return StringUtil.read(inputStream);
	}

	private InputStream _getFragmentEntryInputStream(
			ZipFile zipFile, String fileName, String contentPath)
		throws Exception {

		if (contentPath.startsWith(StringPool.SLASH)) {
			return _getInputStream(zipFile, contentPath.substring(1));
		}

		if (contentPath.startsWith("./")) {
			contentPath = contentPath.substring(2);
		}

		String path = fileName.substring(
			0, fileName.lastIndexOf(StringPool.SLASH));

		return _getInputStream(zipFile, path + StringPool.SLASH + contentPath);
	}

	private InputStream _getInputStream(ZipFile zipFile, String fileName)
		throws Exception {

		ZipEntry zipEntry = zipFile.getEntry(fileName);

		if (zipEntry == null) {
			return null;
		}

		return zipFile.getInputStream(zipEntry);
	}

	private String _getKey(ZipFile zipFile, long groupId, String fileName)
		throws Exception {

		String key = StringPool.BLANK;

		if (fileName.lastIndexOf(CharPool.SLASH) != -1) {
			String path = fileName.substring(
				0, fileName.lastIndexOf(CharPool.SLASH));

			key = path.substring(path.lastIndexOf(CharPool.SLASH) + 1);
		}
		else if (fileName.equals(
					FragmentExportImportConstants.FILE_NAME_COLLECTION)) {

			JSONObject collectionJSONObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					zipFile.getInputStream(zipFile.getEntry(fileName))));

			key = _fragmentCollectionLocalService.generateFragmentCollectionKey(
				groupId, collectionJSONObject.getString("name"));
		}
		else if (fileName.equals(
					FragmentExportImportConstants.FILE_NAME_FRAGMENT)) {

			JSONObject fragmentJSONObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					zipFile.getInputStream(zipFile.getEntry(fileName))));

			key = _fragmentEntryLocalService.generateFragmentEntryKey(
				groupId, fragmentJSONObject.getString("name"));
		}

		if (Validator.isNotNull(key)) {
			return key;
		}

		throw new IllegalArgumentException("Incorrect file name " + fileName);
	}

	private long _getOrCreateFolderId(
			Map<String, Long> folderIdsMap, String folderPath,
			long repositoryId, long userId)
		throws Exception {

		if (folderIdsMap.containsKey(folderPath)) {
			return folderIdsMap.get(folderPath);
		}

		String folderName = folderPath;

		String parentFolderPath = StringPool.BLANK;

		int index = folderName.lastIndexOf(StringPool.SLASH);

		if (index != -1) {
			folderName = folderName.substring(index + 1);

			parentFolderPath = folderPath.substring(0, index);
		}

		Folder folder = PortletFileRepositoryUtil.addPortletFolder(
			userId, repositoryId,
			_getOrCreateFolderId(
				folderIdsMap, parentFolderPath, repositoryId, userId),
			folderName, ServiceContextThreadLocal.getServiceContext());

		folderIdsMap.put(folderPath, folder.getFolderId());

		return folder.getFolderId();
	}

	private long _getPreviewFileEntryId(
			long userId, long groupId, ZipFile zipFile, String className,
			long classPK, String fileName, String contentPath)
		throws Exception {

		if (Validator.isNull(contentPath)) {
			return 0;
		}

		InputStream inputStream = _getFragmentEntryInputStream(
			zipFile, fileName, contentPath);

		if (inputStream == null) {
			return 0;
		}

		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
				groupId, FragmentPortletKeys.FRAGMENT);

		if (repository == null) {
			if ((groupId == CompanyConstants.SYSTEM) &&
				Objects.equals(className, FragmentEntry.class.getName())) {

				FragmentEntry fragmentEntry =
					_fragmentEntryLocalService.getFragmentEntry(classPK);

				Company company = _companyLocalService.getCompany(
					fragmentEntry.getCompanyId());

				groupId = company.getGroupId();
			}
			else if ((groupId == CompanyConstants.SYSTEM) &&
					 Objects.equals(
						 className, FragmentComposition.class.getName())) {

				FragmentComposition fragmentComposition =
					_fragmentCompositionLocalService.getFragmentComposition(
						classPK);

				Company company = _companyLocalService.getCompany(
					fragmentComposition.getCompanyId());

				groupId = company.getGroupId();
			}

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			repository = PortletFileRepositoryUtil.addPortletRepository(
				groupId, FragmentPortletKeys.FRAGMENT, serviceContext);
		}

		FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
			null, groupId, userId, className, classPK,
			FragmentPortletKeys.FRAGMENT, repository.getDlFolderId(),
			inputStream,
			classPK + "_preview." + FileUtil.getExtension(contentPath),
			MimeTypesUtil.getContentType(contentPath), false);

		return fileEntry.getFileEntryId();
	}

	private void _importFragmentCompositions(
			long userId, long groupId, ZipFile zipFile,
			long fragmentCollectionId, Map<String, String> fragmentCompositions,
			FragmentsImportStrategy fragmentsImportStrategy)
		throws Exception {

		for (Map.Entry<String, String> entry :
				fragmentCompositions.entrySet()) {

			FragmentComposition fragmentComposition =
				_fragmentCompositionService.fetchFragmentComposition(
					groupId, entry.getKey());

			if ((fragmentComposition != null) &&
				Objects.equals(
					FragmentsImportStrategy.DO_NOT_IMPORT,
					fragmentsImportStrategy)) {

				continue;
			}

			String compositionJSON = _getContent(zipFile, entry.getValue());

			if (Validator.isNull(compositionJSON)) {
				continue;
			}

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				compositionJSON);

			String name = jsonObject.getString("name", entry.getKey());
			String description = jsonObject.getString("description");
			String definitionData = _getFragmentEntryContent(
				zipFile, entry.getValue(),
				jsonObject.getString("fragmentCompositionDefinitionPath"));

			try {
				if (fragmentComposition == null) {
					fragmentComposition =
						_fragmentCompositionService.addFragmentComposition(
							null, groupId, fragmentCollectionId, entry.getKey(),
							name, description, definitionData, 0L,
							WorkflowConstants.STATUS_APPROVED,
							ServiceContextThreadLocal.getServiceContext());
				}
				else if (Objects.equals(
							FragmentsImportStrategy.KEEP_BOTH,
							fragmentsImportStrategy)) {

					fragmentComposition =
						_fragmentCompositionService.addFragmentComposition(
							null, groupId, fragmentCollectionId,
							_fragmentCompositionLocalService.
								generateFragmentCompositionKey(
									groupId, entry.getKey()),
							_fragmentCompositionLocalService.
								getUniqueFragmentCompositionName(
									groupId, fragmentCollectionId, name),
							description, definitionData, 0L,
							WorkflowConstants.STATUS_APPROVED,
							ServiceContextThreadLocal.getServiceContext());
				}
				else if (Objects.equals(
							FragmentsImportStrategy.DO_NOT_OVERWRITE,
							fragmentsImportStrategy)) {

					throw new DuplicateFragmentCompositionKeyException();
				}
				else {
					fragmentComposition =
						_fragmentCompositionService.updateFragmentComposition(
							fragmentComposition.getFragmentCompositionId(),
							fragmentCollectionId, name, description,
							definitionData,
							fragmentComposition.getPreviewFileEntryId(),
							fragmentComposition.getStatus());
				}

				if (fragmentComposition.getPreviewFileEntryId() > 0) {
					PortletFileRepositoryUtil.deletePortletFileEntry(
						fragmentComposition.getPreviewFileEntryId());
				}

				_fragmentCompositionService.updateFragmentComposition(
					fragmentComposition.getFragmentCompositionId(),
					_getPreviewFileEntryId(
						userId, groupId, zipFile,
						FragmentComposition.class.getName(),
						fragmentComposition.getFragmentCompositionId(),
						entry.getValue(),
						jsonObject.getString("thumbnailPath")));

				_fragmentsImporterResultEntries.add(
					new FragmentsImporterResultEntry(
						name, FragmentsImporterResultEntry.Status.IMPORTED,
						FragmentsImporterResultEntry.Type.COMPOSITION, null));
			}
			catch (PortalException portalException) {
				_fragmentsImporterResultEntries.add(
					new FragmentsImporterResultEntry(
						name, FragmentsImporterResultEntry.Status.INVALID,
						FragmentsImporterResultEntry.Type.COMPOSITION,
						portalException.getMessage()));
			}
		}
	}

	private void _importFragmentEntries(
			long userId, long groupId, ZipFile zipFile,
			long fragmentCollectionId, Map<String, String> fragmentEntries,
			Map<String, String> resourceReferences,
			FragmentsImportStrategy fragmentsImportStrategy,
			boolean marketplace)
		throws Exception {

		for (Map.Entry<String, String> entry : fragmentEntries.entrySet()) {
			String name = entry.getKey();

			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.fetchFragmentEntry(groupId, name);

			if ((fragmentEntry != null) &&
				Objects.equals(
					FragmentsImportStrategy.DO_NOT_IMPORT,
					fragmentsImportStrategy)) {

				continue;
			}

			String css = StringPool.BLANK;
			String html = StringPool.BLANK;
			String js = StringPool.BLANK;
			boolean cacheable = false;
			String configuration = StringPool.BLANK;
			String icon = StringPool.BLANK;
			boolean readOnly = false;
			String thumbnailPath = StringPool.BLANK;
			String typeLabel = StringPool.BLANK;
			String typeOptions = StringPool.BLANK;

			String fragmentJSON = _getContent(zipFile, entry.getValue());

			if (Validator.isNotNull(fragmentJSON)) {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					fragmentJSON);

				name = jsonObject.getString("name");
				css = _replaceResourceReferences(
					_getFragmentEntryContent(
						zipFile, entry.getValue(),
						jsonObject.getString("cssPath")),
					resourceReferences);
				html = _replaceResourceReferences(
					_getFragmentEntryContent(
						zipFile, entry.getValue(),
						jsonObject.getString("htmlPath")),
					resourceReferences);
				js = _getFragmentEntryContent(
					zipFile, entry.getValue(), jsonObject.getString("jsPath"));
				cacheable = jsonObject.getBoolean("cacheable");
				configuration = _getFragmentEntryContent(
					zipFile, entry.getValue(),
					jsonObject.getString("configurationPath"));
				readOnly = jsonObject.getBoolean("readOnly");
				icon = jsonObject.getString("icon");
				thumbnailPath = jsonObject.getString("thumbnailPath");
				typeLabel = jsonObject.getString("type");
				typeOptions = jsonObject.getString("typeOptions");
			}

			_addFragmentEntry(
				groupId, entry.getValue(), fragmentEntry, fragmentCollectionId,
				entry.getKey(), name, css, html, js, cacheable, configuration,
				icon, marketplace, readOnly, thumbnailPath, typeLabel,
				typeOptions, fragmentsImportStrategy, userId, zipFile);
		}
	}

	private void _importResources(
			long userId, long groupId, FragmentCollection fragmentCollection,
			String fragmentCollectionKey, ZipFile zipFile,
			Map<String, String> resourceReferences)
		throws Exception {

		if (groupId == 0) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Importing resources at the system level is not supported");
			}

			return;
		}

		Set<String> excludePaths = new HashSet<>();

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

		List<? extends ZipEntry> zipEntries = Collections.list(enumeration);

		for (ZipEntry zipEntry : zipEntries) {
			String name = zipEntry.getName();

			if (!(name.endsWith(
					FragmentExportImportConstants.FILE_NAME_COLLECTION) ||
				  name.endsWith(
					  FragmentExportImportConstants.FILE_NAME_FRAGMENT))) {

				continue;
			}

			if (name.endsWith(
					FragmentExportImportConstants.FILE_NAME_COLLECTION)) {

				excludePaths.add(name);

				continue;
			}

			try {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					StringUtil.read(zipFile.getInputStream(zipEntry)));

				String path = name.substring(
					0, name.lastIndexOf(StringPool.SLASH) + 1);

				Collections.addAll(
					excludePaths, path + "fragment.json",
					path + jsonObject.getString("configuration"),
					path + jsonObject.getString("cssPath"),
					path + jsonObject.getString("htmlPath"),
					path + jsonObject.getString("icon"),
					path + jsonObject.getString("jsPath"),
					path + jsonObject.getString("thumbnailPath"));
			}
			catch (Exception exception) {
				_log.error(
					"Unable to read fragments.json file " + name, exception);
			}
		}

		Map<String, String> zipEntryNames = new HashMap<>();

		for (ZipEntry zipEntry : zipEntries) {
			String[] paths = StringUtil.split(
				zipEntry.getName(), StringPool.FORWARD_SLASH);

			String fileName = zipEntry.getName();

			if (!ArrayUtil.contains(paths, "resources") ||
				excludePaths.contains(zipEntry.getName()) ||
				!fileName.contains(fragmentCollectionKey) ||
				fileName.endsWith(StringPool.SLASH)) {

				continue;
			}

			zipEntryNames.put(
				_getFilePath(fragmentCollectionKey, zipEntry.getName()),
				zipEntry.getName());
		}

		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
				groupId, FragmentPortletKeys.FRAGMENT);

		_addPortletFileEntriesWithFolders(
			userId, groupId, fragmentCollection, zipFile, resourceReferences,
			zipEntryNames, repository);
	}

	private boolean _isFragmentCollection(String fileName) {
		return Objects.equals(
			_getFileName(fileName),
			FragmentExportImportConstants.FILE_NAME_COLLECTION);
	}

	private boolean _isFragmentComposition(String fileName) {
		return Objects.equals(
			_getFileName(fileName),
			FragmentExportImportConstants.FILE_NAME_FRAGMENT_COMPOSITION);
	}

	private boolean _isFragmentEntry(String fileName) {
		return Objects.equals(
			_getFileName(fileName),
			FragmentExportImportConstants.FILE_NAME_FRAGMENT);
	}

	private String _replaceResourceReferences(
		String input, Map<String, String> replacedResourcesMap) {

		for (Map.Entry<String, String> replacedResource :
				replacedResourcesMap.entrySet()) {

			String source = "\\[resources:" + replacedResource.getKey() + "\\]";
			String target =
				"\\[resources:" + replacedResource.getValue() + "\\]";

			input = input.replaceAll(source, target);
		}

		return input;
	}

	private boolean _validateFragmentCompositions(
		long groupId, Map<String, String> fragmentCompositions) {

		for (Map.Entry<String, String> orphanFragmentComposition :
				fragmentCompositions.entrySet()) {

			FragmentComposition fragmentComposition =
				_fragmentCompositionService.fetchFragmentComposition(
					groupId, orphanFragmentComposition.getKey());

			if (fragmentComposition != null) {
				return false;
			}
		}

		return true;
	}

	private boolean _validateFragmentEntries(
		long groupId, Map<String, String> fragmentEntries) {

		for (Map.Entry<String, String> orphanFragmentEntry :
				fragmentEntries.entrySet()) {

			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.fetchFragmentEntry(
					groupId, orphanFragmentEntry.getKey());

			if (fragmentEntry != null) {
				return false;
			}
		}

		return true;
	}

	private static final String _FRAGMENT_COLLECTION_KEY_DEFAULT = "imported";

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentsImporterImpl.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference
	private FragmentCompositionLocalService _fragmentCompositionLocalService;

	@Reference
	private FragmentCompositionService _fragmentCompositionService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private FragmentEntryService _fragmentEntryService;

	@Reference
	private FragmentEntryValidator _fragmentEntryValidator;

	private List<FragmentsImporterResultEntry> _fragmentsImporterResultEntries;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private class FragmentCollectionFolder {

		public FragmentCollectionFolder(String fileName) {
			_fileName = fileName;
		}

		public void addFragmentComposition(String key, String fileName) {
			_fragmentCompositions.put(key, fileName);
		}

		public void addFragmentEntry(String key, String fileName) {
			_fragmentEntries.put(key, fileName);
		}

		public String getFileName() {
			return _fileName;
		}

		public Map<String, String> getFragmentCompositions() {
			return _fragmentCompositions;
		}

		public Map<String, String> getFragmentEntries() {
			return _fragmentEntries;
		}

		private final String _fileName;
		private final Map<String, String> _fragmentCompositions =
			new HashMap<>();
		private final Map<String, String> _fragmentEntries = new HashMap<>();

	}

}