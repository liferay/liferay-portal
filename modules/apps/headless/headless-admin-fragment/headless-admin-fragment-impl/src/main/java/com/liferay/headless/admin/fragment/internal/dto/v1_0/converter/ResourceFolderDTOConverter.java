/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.dto.v1_0.converter;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.dto.v1_0.ResourceFolder;
import com.liferay.headless.admin.fragment.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(service = DTOConverter.class)
public class ResourceFolderDTOConverter
	implements DTOConverter<DLFolder, ResourceFolder> {

	@Override
	public String getContentType() {
		return ResourceFolder.class.getSimpleName();
	}

	@Override
	public ResourceFolder toDTO(
			DTOConverterContext dtoConverterContext, DLFolder dlFolder)
		throws Exception {

		UnsafeSupplierValue<DLFolder, Exception> unsafeSupplierValue =
			new UnsafeSupplierValue<>(() -> _getParentDLFolder(dlFolder));

		return new ResourceFolder() {
			{
				setCreator(() -> CreatorUtil.toCreator(dlFolder.getUserId()));
				setDateCreated(dlFolder::getCreateDate);
				setDateModified(dlFolder::getModifiedDate);
				setExternalReferenceCode(dlFolder::getExternalReferenceCode);
				setFragmentSet(() -> _toFragmentSet(dlFolder));
				setName(dlFolder::getName);
				setParentResourceFolder(
					() -> {
						DLFolder parentDLFolder =
							unsafeSupplierValue.getValue();

						if (parentDLFolder == null) {
							return null;
						}

						return ResourceFolderDTOConverter.this.toDTO(
							dtoConverterContext, parentDLFolder);
					});
				setParentResourceFolderExternalReferenceCode(
					() -> {
						DLFolder parentDLFolder =
							unsafeSupplierValue.getValue();

						if (parentDLFolder == null) {
							return null;
						}

						return parentDLFolder.getExternalReferenceCode();
					});
			}
		};
	}

	private DLFolder _getFragmentCollectionDLFolder(DLFolder dlFolder) {
		DLFolder parentDLFolder = _dlFolderLocalService.fetchDLFolder(
			dlFolder.getParentFolderId());

		while ((parentDLFolder != null) && !parentDLFolder.isMountPoint()) {
			dlFolder = parentDLFolder;

			parentDLFolder = _dlFolderLocalService.fetchDLFolder(
				dlFolder.getParentFolderId());
		}

		return dlFolder;
	}

	private DLFolder _getParentDLFolder(DLFolder dlFolder) {
		DLFolder parentDLFolder = _dlFolderLocalService.fetchDLFolder(
			dlFolder.getParentFolderId());

		if ((parentDLFolder == null) || parentDLFolder.isMountPoint()) {
			return null;
		}

		DLFolder grandparentDLFolder = _dlFolderLocalService.fetchDLFolder(
			parentDLFolder.getParentFolderId());

		if ((grandparentDLFolder == null) ||
			grandparentDLFolder.isMountPoint()) {

			return null;
		}

		return parentDLFolder;
	}

	private FragmentSet _toFragmentSet(DLFolder dlFolder) throws Exception {
		DLFolder fragmentCollectionDLFolder = _getFragmentCollectionDLFolder(
			dlFolder);

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.fetchFragmentCollection(
				dlFolder.getGroupId(), fragmentCollectionDLFolder.getName());

		if (fragmentCollection == null) {
			return null;
		}

		return _fragmentSetDTOConverter.toDTO(null, fragmentCollection);
	}

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.fragment.internal.dto.v1_0.converter.FragmentSetDTOConverter)"
	)
	private DTOConverter<FragmentCollection, FragmentSet>
		_fragmentSetDTOConverter;

}