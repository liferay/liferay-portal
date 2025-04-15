/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.web.internal.util;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.media.CommerceMediaProvider;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramSettingLocalService;
import com.liferay.commerce.shop.by.diagram.type.CSDiagramType;
import com.liferay.commerce.shop.by.diagram.type.CSDiagramTypeRegistry;
import com.liferay.commerce.shop.by.diagram.util.CSDiagramCPTypeHelper;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 * @author Crescenzo Rega
 */
@Component(service = CSDiagramCPTypeHelper.class)
public class CSDiagramCPTypeHelperImpl implements CSDiagramCPTypeHelper {

	@Override
	public FileVersion getCPDiagramImageFileVersion(
			long cpDefinitionId, CSDiagramSetting csDiagramSetting,
			HttpServletRequest httpServletRequest)
		throws Exception {

		FileVersion fileVersion = CSDiagramSettingUtil.getFileVersion(
			csDiagramSetting);

		if (fileVersion != null) {
			return fileVersion;
		}

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		FileEntry fileEntry = _commerceMediaProvider.getDefaultImageFileEntry(
			_portal.getCompanyId(httpServletRequest),
			cpDefinition.getGroupId());

		return fileEntry.getFileVersion();
	}

	@Override
	public CSDiagramSetting getCSDiagramSetting(
			AccountEntry accountEntry, long cpDefinitionId,
			PermissionChecker permissionChecker)
		throws PortalException {

		long accountEntryId = 0;

		if (accountEntry != null) {
			accountEntryId = accountEntry.getAccountEntryId();
		}

		_commerceProductViewPermission.check(
			permissionChecker, accountEntryId, cpDefinitionId);

		return _csDiagramSettingLocalService.
			fetchCSDiagramSettingByCPDefinitionId(cpDefinitionId);
	}

	@Override
	public CSDiagramType getCSDiagramType(String type) {
		return _csDiagramTypeRegistry.getCSDiagramType(type);
	}

	@Override
	public String getImageURL(CSDiagramSetting csDiagramSetting)
		throws Exception {

		return CSDiagramSettingUtil.getImageURL(csDiagramSetting, _dlURLHelper);
	}

	@Reference
	private CommerceMediaProvider _commerceMediaProvider;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CSDiagramSettingLocalService _csDiagramSettingLocalService;

	@Reference
	private CSDiagramTypeRegistry _csDiagramTypeRegistry;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private Portal _portal;

}