/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.service;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceWrapper;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = ServiceWrapper.class)
public class DataEngineDLFileEntryTypeLocalServiceWrapper
	extends DLFileEntryTypeLocalServiceWrapper {

	@Override
	public DLFileEntryType addDLFileEntryType(DLFileEntryType dlFileEntryType) {
		DLFileEntryType fileEntryType = super.addDLFileEntryType(
			dlFileEntryType);

		_updateDDMStructure(fileEntryType.getDataDefinitionId());

		return fileEntryType;
	}

	@Override
	public DLFileEntryType addFileEntryType(
			String externalReferenceCode, long userId, long groupId,
			long dataDefinitionId, String fileEntryTypeKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			int scope, ServiceContext serviceContext)
		throws PortalException {

		DLFileEntryType fileEntryType = super.addFileEntryType(
			externalReferenceCode, userId, groupId, dataDefinitionId,
			fileEntryTypeKey, nameMap, descriptionMap, scope, serviceContext);

		_updateDDMStructure(dataDefinitionId);

		return fileEntryType;
	}

	@Override
	public DLFileEntryType addFileEntryType(
			String externalReferenceCode, long userId, long groupId,
			long dataDefinitionId, String fileEntryTypeKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			ServiceContext serviceContext)
		throws PortalException {

		DLFileEntryType fileEntryType = super.addFileEntryType(
			externalReferenceCode, userId, groupId, dataDefinitionId,
			fileEntryTypeKey, nameMap, descriptionMap, serviceContext);

		_updateDDMStructure(dataDefinitionId);

		return fileEntryType;
	}

	@Override
	public DLFileEntryType deleteFileEntryType(DLFileEntryType dlFileEntryType)
		throws PortalException {

		dlFileEntryType = super.deleteFileEntryType(dlFileEntryType);

		updateDDMStructureLinks(
			dlFileEntryType.getFileEntryTypeId(), Collections.emptySet());

		return dlFileEntryType;
	}

	private void _updateDDMStructure(long structureId) {
		DDMStructure ddmStructure = _ddmStructureLocalService.fetchDDMStructure(
			structureId);

		if (ddmStructure == null) {
			return;
		}

		ddmStructure.setType(DDMStructureConstants.TYPE_AUTO);

		_ddmStructureLocalService.updateDDMStructure(ddmStructure);
	}

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

}