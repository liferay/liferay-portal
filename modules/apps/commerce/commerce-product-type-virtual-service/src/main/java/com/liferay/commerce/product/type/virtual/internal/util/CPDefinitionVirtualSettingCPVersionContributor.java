/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.internal.util;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.commerce.product.util.CPVersionContributor;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ethan Bustad
 */
@Component(
	property = "commerce.product.content.contributor.name=CPDefinitionVirtualSettingCPVersionContributor",
	service = CPVersionContributor.class
)
public class CPDefinitionVirtualSettingCPVersionContributor
	implements CPVersionContributor {

	@Override
	public void onDelete(long cpDefinitionId) throws PortalException {
		_cpDefinitionVirtualSettingLocalService.
			deleteCPDefinitionVirtualSetting(
				CPDefinition.class.getName(), cpDefinitionId);
	}

	@Override
	public void onUpdate(long oldCPDefinitionId, long newCPDefinitionId) {
		_cpDefinitionVirtualSettingLocalService.cloneCPDefinitionVirtualSetting(
			oldCPDefinitionId, newCPDefinitionId);
	}

	@Reference
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

}