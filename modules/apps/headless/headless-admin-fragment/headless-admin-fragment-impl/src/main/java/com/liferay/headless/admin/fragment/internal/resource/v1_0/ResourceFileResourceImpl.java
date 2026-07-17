/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.resource.v1_0;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.headless.admin.fragment.dto.v1_0.FileURLReference;
import com.liferay.headless.admin.fragment.dto.v1_0.ResourceFile;
import com.liferay.headless.admin.fragment.internal.odata.entity.v1_0.ResourceFileEntityModel;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.FragmentSetUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.ResourceFolderUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.fragment.internal.util.EnabledUtil;
import com.liferay.headless.admin.fragment.resource.v1_0.ResourceFileResource;
import com.liferay.headless.admin.site.dto.v1_0.util.URLUtil;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.IOException;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/resource-file.properties",
	scope = ServiceScope.PROTOTYPE, service = ResourceFileResource.class
)
public class ResourceFileResourceImpl extends BaseResourceFileResourceImpl {

	@Override
	public void deleteSiteResourceFile(
			String siteExternalReferenceCode,
			String resourceFileExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		FileEntry fileEntry = _dlAppService.getFileEntryByExternalReferenceCode(
			resourceFileExternalReferenceCode,
			GroupUtil.getStagingAwareGroupId(
				true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		_checkResourceFile(fileEntry);

		_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<ResourceFile> getSiteFragmentSetResourceFilesPage(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode, Pagination pagination)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		if (!_portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), groupId,
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES)) {

			return Page.of(Collections.emptyList());
		}

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.
				getFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode, groupId);

		long resourcesFolderId = fragmentCollection.getResourcesFolderId(false);

		if (resourcesFolderId <= 0) {
			return Page.of(Collections.emptyList());
		}

		return _getResourceFilesPage(resourcesFolderId, groupId, pagination);
	}

	@Override
	public ResourceFile getSiteResourceFile(
			String siteExternalReferenceCode,
			String resourceFileExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		FileEntry fileEntry = _dlAppService.getFileEntryByExternalReferenceCode(
			resourceFileExternalReferenceCode,
			GroupUtil.getGroupId(
				true, true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		_checkResourceFile(fileEntry);

		return _toResourceFile(fileEntry);
	}

	@Override
	public Page<ResourceFile> getSiteResourceFilesPage(
			String siteExternalReferenceCode, Filter filter,
			Pagination pagination)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		if (!_portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), groupId,
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES)) {

			return Page.of(Collections.emptyList());
		}

		long[] resourcesFolderIds = transformToLongArray(
			_fragmentCollectionLocalService.getFragmentCollections(groupId),
			fragmentCollection -> {
				long resourcesFolderId =
					fragmentCollection.getResourcesFolderId(false);

				if (resourcesFolderId <= 0) {
					return null;
				}

				return resourcesFolderId;
			});

		if (ArrayUtil.isEmpty(resourcesFolderIds)) {
			return Page.of(Collections.emptyList());
		}

		return _getResourceFilesPage(
			booleanQuery -> {
			},
			filter, resourcesFolderIds, groupId, pagination);
	}

	@Override
	public Page<ResourceFile> getSiteResourceFolderResourceFilesPage(
			String siteExternalReferenceCode,
			String resourceFolderExternalReferenceCode, Pagination pagination)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		Folder folder = _dlAppService.getFolderByExternalReferenceCode(
			resourceFolderExternalReferenceCode, groupId);

		ResourceFolderUtil.checkResourceFolder(
			_dlFolderLocalService.getDLFolder(folder.getFolderId()));

		return _getResourceFilesPage(folder.getFolderId(), groupId, pagination);
	}

	@Override
	public ResourceFile postSiteFragmentSetResourceFile(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode, ResourceFile resourceFile)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		_portletResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return _addResourceFile(
			_fragmentCollectionService.
				getFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode, groupId),
			groupId, resourceFile);
	}

	@Override
	public ResourceFile postSiteResourceFile(
			String siteExternalReferenceCode, ResourceFile resourceFile)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		_portletResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return _addResourceFile(
			_getOrAddFragmentCollection(groupId, resourceFile), groupId,
			resourceFile);
	}

	@Override
	public ResourceFile putSiteResourceFile(
			String siteExternalReferenceCode,
			String resourceFileExternalReferenceCode, ResourceFile resourceFile)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		DLFileEntry dlFileEntry =
			_dlFileEntryLocalService.fetchFileEntryByExternalReferenceCode(
				groupId, resourceFileExternalReferenceCode);

		if (dlFileEntry == null) {
			_portletResourcePermission.check(
				PermissionThreadLocal.getPermissionChecker(), groupId,
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

			resourceFile.setExternalReferenceCode(
				() -> resourceFileExternalReferenceCode);

			return _addResourceFile(
				_getOrAddFragmentCollection(groupId, resourceFile), groupId,
				resourceFile);
		}

		_checkResourceFile(
			_dlAppService.getFileEntry(dlFileEntry.getFileEntryId()));

		_checkName(resourceFile.getName());

		byte[] bytes = _toByteArray(resourceFile.getFileURLReference());

		_checkBytes(bytes);

		return _toResourceFile(
			_dlAppLocalService.updateFileEntry(
				contextUser.getUserId(), dlFileEntry.getFileEntryId(),
				resourceFile.getName(),
				_mimeTypes.getContentType(resourceFile.getName()),
				resourceFile.getName(), null, null, null,
				DLVersionNumberIncrease.NONE, bytes, null, null, null,
				_getServiceContext(groupId, resourceFile)));
	}

	private ResourceFile _addResourceFile(
			FragmentCollection fragmentCollection, long groupId,
			ResourceFile resourceFile)
		throws Exception {

		_checkName(resourceFile.getName());

		byte[] bytes = _toByteArray(resourceFile.getFileURLReference());

		_checkBytes(bytes);

		DLFolder dlFolder = ResourceFolderUtil.getOrAddParentDLFolder(
			contextCompany.getCompanyId(), fragmentCollection, groupId,
			contextHttpServletRequest,
			contextAcceptLanguage.getPreferredLocale(),
			resourceFile.getResourceFolder(),
			resourceFile.getResourceFolderExternalReferenceCode(),
			contextUser.getUserId());

		return _toResourceFile(
			_dlAppLocalService.addFileEntry(
				resourceFile.getExternalReferenceCode(),
				contextUser.getUserId(), dlFolder.getRepositoryId(),
				dlFolder.getFolderId(), resourceFile.getName(),
				_mimeTypes.getContentType(resourceFile.getName()),
				resourceFile.getName(), null, null, null, bytes, null, null,
				null, _getServiceContext(groupId, resourceFile)));
	}

	private void _checkBytes(byte[] bytes) {
		if (ArrayUtil.isEmpty(bytes)) {
			throw new IllegalArgumentException(
				_language.get(
					contextAcceptLanguage.getPreferredLocale(),
					"a-file-url-reference-with-content-is-required"));
		}
	}

	private void _checkName(String name) {
		if (Validator.isNull(name)) {
			throw new IllegalArgumentException(
				_language.get(
					contextAcceptLanguage.getPreferredLocale(),
					"name-is-required"));
		}
	}

	private void _checkResourceFile(FileEntry fileEntry) throws Exception {
		DLFolder dlFolder = _dlFolderLocalService.fetchDLFolder(
			fileEntry.getFolderId());

		FragmentCollection fragmentCollection =
			FragmentSetUtil.getFragmentCollection(dlFolder);

		if (fragmentCollection == null) {
			throw new NoSuchFileEntryException(
				"No resource file exists with external reference code " +
					fileEntry.getExternalReferenceCode());
		}

		_fragmentCollectionService.getFragmentCollection(
			fragmentCollection.getFragmentCollectionId());
	}

	private FragmentCollection _getOrAddFragmentCollection(
			long groupId, ResourceFile resourceFile)
		throws Exception {

		return FragmentSetUtil.getOrAddFragmentCollection(
			contextCompany.getCompanyId(), resourceFile.getFragmentSet(),
			resourceFile.getFragmentSetExternalReferenceCode(), groupId,
			contextHttpServletRequest,
			"a-fragment-set-external-reference-code-is-required-to-create-a-" +
				"new-resource-file",
			contextAcceptLanguage.getPreferredLocale(),
			contextUser.getUserId());
	}

	private Page<ResourceFile> _getResourceFilesPage(
			long folderId, long groupId, Pagination pagination)
		throws Exception {

		return _getResourceFilesPage(
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(Field.FOLDER_ID, String.valueOf(folderId)),
					BooleanClauseOccur.MUST);
			},
			null, new long[] {folderId}, groupId, pagination);
	}

	private Page<ResourceFile> _getResourceFilesPage(
			UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
			Filter filter, long[] folderIds, long groupId,
			Pagination pagination)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(), booleanQueryUnsafeConsumer, filter,
			DLFileEntry.class.getName(), null, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setFolderIds(folderIds);
				searchContext.setGroupIds(new long[] {groupId});
				searchContext.setVulcanCheckPermissions(false);
			},
			null,
			document -> _toResourceFile(
				_dlAppService.getFileEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private ServiceContext _getServiceContext(
		long groupId, ResourceFile resourceFile) {

		ServiceContext serviceContext = ServiceContextUtil.getServiceContext(
			contextCompany.getCompanyId(), resourceFile.getDateCreated(),
			groupId, contextHttpServletRequest, resourceFile.getDateModified(),
			contextUser.getUserId());

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		return serviceContext;
	}

	private byte[] _toByteArray(FileURLReference fileURLReference)
		throws Exception {

		if (fileURLReference == null) {
			return new byte[0];
		}

		String fileBase64 = fileURLReference.getFileBase64();

		if (Validator.isNotNull(fileBase64)) {
			return Base64.decode(fileBase64);
		}

		String url = fileURLReference.getUrl();

		if (Validator.isNotNull(url)) {
			try {
				return URLUtil.getByteArray(url);
			}
			catch (IOException ioException) {
				throw new IllegalArgumentException(
					"Unable to download file from " + url, ioException);
			}
		}

		return new byte[0];
	}

	private ResourceFile _toResourceFile(FileEntry fileEntry) throws Exception {
		return _resourceFileDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				false, null, _dtoConverterRegistry, contextHttpServletRequest,
				fileEntry.getFileEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			fileEntry);
	}

	private static final EntityModel _entityModel =
		new ResourceFileEntityModel();

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference
	private Language _language;

	@Reference
	private MimeTypes _mimeTypes;

	@Reference(
		target = "(resource.name=" + FragmentConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.fragment.internal.dto.v1_0.converter.ResourceFileDTOConverter)"
	)
	private DTOConverter<FileEntry, ResourceFile> _resourceFileDTOConverter;

}