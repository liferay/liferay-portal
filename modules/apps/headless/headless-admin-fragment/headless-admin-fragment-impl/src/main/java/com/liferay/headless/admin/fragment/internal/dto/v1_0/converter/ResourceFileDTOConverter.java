/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.dto.v1_0.converter;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.exportimport.attachment.ExportImportAttachmentManager;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.headless.admin.fragment.dto.v1_0.FileURLReference;
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.dto.v1_0.ResourceFile;
import com.liferay.headless.admin.fragment.dto.v1_0.ResourceFolder;
import com.liferay.headless.admin.fragment.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.FragmentSetUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.ResourceFolderUtil;
import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(service = DTOConverter.class)
public class ResourceFileDTOConverter
	implements DTOConverter<FileEntry, ResourceFile> {

	@Override
	public String getContentType() {
		return ResourceFile.class.getSimpleName();
	}

	@Override
	public ResourceFile toDTO(
			DTOConverterContext dtoConverterContext, FileEntry fileEntry)
		throws Exception {

		UnsafeSupplierValue<DLFolder, Exception> dlFolderUnsafeSupplierValue =
			new UnsafeSupplierValue<>(
				() -> _dlFolderLocalService.fetchDLFolder(
					fileEntry.getFolderId()));

		UnsafeSupplierValue<FragmentCollection, Exception>
			fragmentCollectionUnsafeSupplierValue = new UnsafeSupplierValue<>(
				() -> FragmentSetUtil.getFragmentCollection(
					dlFolderUnsafeSupplierValue.getValue()));
		UnsafeSupplierValue<DLFolder, Exception>
			resourceDLFolderUnsafeSupplierValue = new UnsafeSupplierValue<>(
				() -> ResourceFolderUtil.getResourceDLFolder(
					dlFolderUnsafeSupplierValue.getValue()));

		return new ResourceFile() {
			{
				setCreator(() -> CreatorUtil.toCreator(fileEntry.getUserId()));
				setDateCreated(fileEntry::getCreateDate);
				setDateModified(fileEntry::getModifiedDate);
				setExternalReferenceCode(fileEntry::getExternalReferenceCode);
				setFileURLReference(() -> _toFileURLReference(fileEntry));
				setFragmentSet(
					() -> NestedFieldsSupplier.supply(
						"fragmentSet",
						fieldName -> _toFragmentSet(
							fragmentCollectionUnsafeSupplierValue.getValue())));
				setFragmentSetExternalReferenceCode(
					() -> {
						FragmentCollection fragmentCollection =
							fragmentCollectionUnsafeSupplierValue.getValue();

						if (fragmentCollection == null) {
							return null;
						}

						return fragmentCollection.getExternalReferenceCode();
					});
				setName(fileEntry::getFileName);
				setResourceFolder(
					() -> NestedFieldsSupplier.supply(
						"resourceFolder",
						fieldName -> {
							DLFolder resourceDLFolder =
								resourceDLFolderUnsafeSupplierValue.getValue();

							if (resourceDLFolder == null) {
								return null;
							}

							return _resourceFolderDTOConverter.toDTO(
								dtoConverterContext, resourceDLFolder);
						}));
				setResourceFolderExternalReferenceCode(
					() -> {
						DLFolder resourceDLFolder =
							resourceDLFolderUnsafeSupplierValue.getValue();

						if (resourceDLFolder == null) {
							return null;
						}

						return resourceDLFolder.getExternalReferenceCode();
					});
			}
		};
	}

	private String _getPortletFileEntryURL(FileEntry fileEntry)
		throws Exception {

		Company company = _companyLocalService.getCompany(
			fileEntry.getCompanyId());

		boolean secure = _isSecure();

		String portalURL = _portal.getPortalURL(
			company.getVirtualHostname(), _portal.getPortalServerPort(secure),
			secure);

		String portletFileEntryURL =
			_portletFileRepository.getPortletFileEntryURL(
				null, fileEntry, StringPool.BLANK);

		return portalURL + portletFileEntryURL;
	}

	private boolean _isSecure() {
		if (Objects.equals(
				Http.HTTPS,
				PropsUtil.get(PropsKeys.PORTAL_INSTANCE_PROTOCOL)) ||
			Objects.equals(
				Http.HTTPS, PropsUtil.get(PropsKeys.WEB_SERVER_PROTOCOL))) {

			return true;
		}

		return false;
	}

	private FileURLReference _toFileURLReference(FileEntry fileEntry) {
		Object model = fileEntry.getModel();

		if (!(model instanceof DLFileEntry dlFileEntry)) {
			return null;
		}

		return new FileURLReference() {
			{
				setFileBase64(
					() -> NestedFieldsSupplier.supply(
						"fileURLReference.fileBase64",
						fieldName -> Base64.encode(
							_file.getBytes(fileEntry.getContentStream()))));
				setUrl(
					() -> {
						if (ExportImportThreadLocal.isExportInProcess()) {
							return _exportImportAttachmentManager.getFileURL(
								dlFileEntry);
						}

						return _getPortletFileEntryURL(fileEntry);
					});
			}
		};
	}

	private FragmentSet _toFragmentSet(FragmentCollection fragmentCollection)
		throws Exception {

		if (fragmentCollection == null) {
			return null;
		}

		return _fragmentSetDTOConverter.toDTO(null, fragmentCollection);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private ExportImportAttachmentManager _exportImportAttachmentManager;

	@Reference
	private File _file;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.fragment.internal.dto.v1_0.converter.FragmentSetDTOConverter)"
	)
	private DTOConverter<FragmentCollection, FragmentSet>
		_fragmentSetDTOConverter;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.fragment.internal.dto.v1_0.converter.ResourceFolderDTOConverter)"
	)
	private DTOConverter<DLFolder, ResourceFolder> _resourceFolderDTOConverter;

}