/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service.impl;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.base.CPDVirtualSettingFileEntryServiceBaseImpl;
import com.liferay.commerce.product.type.virtual.service.persistence.CPDefinitionVirtualSettingPersistence;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import java.io.InputStream;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPDVirtualSettingFileEntry"
	},
	service = AopService.class
)
public class CPDVirtualSettingFileEntryServiceImpl
	extends CPDVirtualSettingFileEntryServiceBaseImpl {

	@Override
	public CPDVirtualSettingFileEntry addCPDefinitionVirtualSetting(
			long groupId, String className, long classPK,
			long cpDefinitionVirtualSettingId, long fileEntryId, String url,
			String version)
		throws PortalException {

		_checkFileEntry(fileEntryId);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingPersistence.findByPrimaryKey(
				cpDefinitionVirtualSettingId);

		_checkPermission(
			cpDefinitionVirtualSetting.getClassName(),
			cpDefinitionVirtualSetting.getClassPK(), ActionKeys.UPDATE);

		return cpdVirtualSettingFileEntryLocalService.
			addCPDVirtualSettingFileEntry(
				getUserId(), groupId, cpDefinitionVirtualSettingId, fileEntryId,
				url, version);
	}

	@Override
	public FileEntry addFileEntry(
			long groupId, long folderId, InputStream inputStream,
			String fileName, String mimeType, String serviceName)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(groupId);

		if (commerceCatalog == null) {
			throw new PrincipalException();
		}

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalog, ActionKeys.UPDATE);

		return cpdVirtualSettingFileEntryLocalService.addFileEntry(
			getUserId(), groupId, CommerceCatalog.class.getName(),
			commerceCatalog.getCommerceCatalogId(), serviceName, folderId,
			inputStream, fileName, mimeType);
	}

	@Override
	public CPDVirtualSettingFileEntry deleteCPDVirtualSettingFileEntry(
			long cpdVirtualSettingFileEntryId)
		throws PortalException {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			cpdVirtualSettingFileEntryLocalService.
				getCPDVirtualSettingFileEntry(cpdVirtualSettingFileEntryId);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpdVirtualSettingFileEntry.getCPDefinitionVirtualSetting();

		_checkPermission(
			cpDefinitionVirtualSetting.getClassName(),
			cpDefinitionVirtualSetting.getClassPK(), ActionKeys.UPDATE);

		return cpdVirtualSettingFileEntryLocalService.
			deleteCPDVirtualSettingFileEntry(cpdVirtualSettingFileEntryId);
	}

	@Override
	public CPDVirtualSettingFileEntry deleteCPDVirtualSettingFileEntry(
			String className, long classPK, long cpdVirtualSettingFileEntryId)
		throws PortalException {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			cpdVirtualSettingFileEntryLocalService.
				getCPDVirtualSettingFileEntry(cpdVirtualSettingFileEntryId);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpdVirtualSettingFileEntry.getCPDefinitionVirtualSetting();

		_checkPermission(
			cpDefinitionVirtualSetting.getClassName(),
			cpDefinitionVirtualSetting.getClassPK(), ActionKeys.UPDATE);

		return cpdVirtualSettingFileEntryLocalService.
			deleteCPDVirtualSettingFileEntry(cpdVirtualSettingFileEntryId);
	}

	@Override
	public CPDVirtualSettingFileEntry fetchCPDVirtualSettingFileEntry(
			long cpdVirtualSettingFileEntryId)
		throws PortalException {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			cpdVirtualSettingFileEntryLocalService.
				fetchCPDVirtualSettingFileEntry(cpdVirtualSettingFileEntryId);

		if (cpdVirtualSettingFileEntry != null) {
			CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
				cpdVirtualSettingFileEntry.getCPDefinitionVirtualSetting();

			_checkPermission(
				cpDefinitionVirtualSetting.getClassName(),
				cpDefinitionVirtualSetting.getClassPK(), ActionKeys.VIEW);
		}

		return cpdVirtualSettingFileEntry;
	}

	@Override
	public List<CPDVirtualSettingFileEntry> getCPDVirtualSettingFileEntries(
			String className, long classPK, long cpDefinitionVirtualSettingId,
			int start, int end)
		throws PortalException {

		_checkPermission(className, classPK, ActionKeys.VIEW);

		return cpdVirtualSettingFileEntryLocalService.
			getCPDVirtualSettingFileEntries(
				cpDefinitionVirtualSettingId, start, end);
	}

	@Override
	public CPDVirtualSettingFileEntry getCPDVirtualSettingFileEntry(
			long cpdVirtualSettingFileEntryId)
		throws PortalException {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			cpdVirtualSettingFileEntryLocalService.
				getCPDVirtualSettingFileEntry(cpdVirtualSettingFileEntryId);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpdVirtualSettingFileEntry.getCPDefinitionVirtualSetting();

		_checkPermission(
			cpDefinitionVirtualSetting.getClassName(),
			cpDefinitionVirtualSetting.getClassPK(), ActionKeys.VIEW);

		return cpdVirtualSettingFileEntry;
	}

	@Override
	public CPDVirtualSettingFileEntry updateCPDefinitionVirtualSetting(
			long cpdVirtualSettingFileEntryId, long fileEntryId, String url,
			String version)
		throws PortalException {

		_checkFileEntry(fileEntryId);

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			cpdVirtualSettingFileEntryLocalService.
				getCPDVirtualSettingFileEntry(cpdVirtualSettingFileEntryId);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpdVirtualSettingFileEntry.getCPDefinitionVirtualSetting();

		_checkPermission(
			cpDefinitionVirtualSetting.getClassName(),
			cpDefinitionVirtualSetting.getClassPK(), ActionKeys.UPDATE);

		return cpdVirtualSettingFileEntryLocalService.
			updateCPDVirtualSettingFileEntry(
				cpdVirtualSettingFileEntryId, fileEntryId, url, version);
	}

	private void _checkCommerceCatalog(long groupId, String actionId)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(groupId);

		if (commerceCatalog == null) {
			throw new PrincipalException();
		}

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalog, actionId);
	}

	private void _checkFileEntry(long fileEntryId) throws PortalException {
		if (fileEntryId > 0) {
			_dlFileEntryModelResourcePermission.check(
				getPermissionChecker(), fileEntryId, ActionKeys.VIEW);
		}
	}

	private void _checkPermission(String className, long classPK, String action)
		throws PortalException {

		long cpDefinitionId = classPK;

		if (className.equals(CPInstance.class.getName())) {
			CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
				classPK);

			cpDefinitionId = cpInstance.getCPDefinitionId();
		}

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		_checkCommerceCatalog(cpDefinition.getGroupId(), action);
	}

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionVirtualSettingPersistence
		_cpDefinitionVirtualSettingPersistence;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.document.library.kernel.model.DLFileEntry)"
	)
	private ModelResourcePermission<DLFileEntry>
		_dlFileEntryModelResourcePermission;

}