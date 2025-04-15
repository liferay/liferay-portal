/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.util;

import aQute.bnd.annotation.ProviderType;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.commerce.shop.by.diagram.type.CSDiagramType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Danny Situ
 * @author Crescenzo Rega
 */
@ProviderType
public interface CSDiagramCPTypeHelper {

	public FileVersion getCPDiagramImageFileVersion(
			long cpDefinitionId, CSDiagramSetting csDiagramSetting,
			HttpServletRequest httpServletRequest)
		throws Exception;

	public CSDiagramSetting getCSDiagramSetting(
			AccountEntry accountEntry, long cpDefinitionId,
			PermissionChecker permissionChecker)
		throws PortalException;

	public CSDiagramType getCSDiagramType(String type);

	public String getImageURL(CSDiagramSetting csDiagramSetting)
		throws Exception;

}