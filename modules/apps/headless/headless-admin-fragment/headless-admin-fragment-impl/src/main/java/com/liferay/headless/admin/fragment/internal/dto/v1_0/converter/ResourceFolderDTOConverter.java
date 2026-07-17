/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.dto.v1_0.converter;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.dto.v1_0.ResourceFolder;
import com.liferay.headless.admin.fragment.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.FragmentSetUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.ResourceFolderUtil;
import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;

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

		UnsafeSupplierValue<FragmentCollection, Exception>
			fragmentCollectionUnsafeSupplierValue = new UnsafeSupplierValue<>(
				() -> FragmentSetUtil.getFragmentCollection(dlFolder));
		UnsafeSupplierValue<DLFolder, Exception>
			parentDLFolderUnsafeSupplierValue = new UnsafeSupplierValue<>(
				() -> ResourceFolderUtil.getResourceDLFolder(
					_dlFolderLocalService.fetchDLFolder(
						dlFolder.getParentFolderId())));

		return new ResourceFolder() {
			{
				setCreator(() -> CreatorUtil.toCreator(dlFolder.getUserId()));
				setDateCreated(dlFolder::getCreateDate);
				setDateModified(dlFolder::getModifiedDate);
				setExternalReferenceCode(dlFolder::getExternalReferenceCode);
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
				setName(dlFolder::getName);
				setParentResourceFolder(
					() -> NestedFieldsSupplier.supply(
						"parentResourceFolder",
						fieldName -> {
							DLFolder parentDLFolder =
								parentDLFolderUnsafeSupplierValue.getValue();

							if (parentDLFolder == null) {
								return null;
							}

							return ResourceFolderDTOConverter.this.toDTO(
								dtoConverterContext, parentDLFolder);
						}));
				setParentResourceFolderExternalReferenceCode(
					() -> {
						DLFolder parentDLFolder =
							parentDLFolderUnsafeSupplierValue.getValue();

						if (parentDLFolder == null) {
							return null;
						}

						return parentDLFolder.getExternalReferenceCode();
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
	private DLFolderLocalService _dlFolderLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.fragment.internal.dto.v1_0.converter.FragmentSetDTOConverter)"
	)
	private DTOConverter<FragmentCollection, FragmentSet>
		_fragmentSetDTOConverter;

}