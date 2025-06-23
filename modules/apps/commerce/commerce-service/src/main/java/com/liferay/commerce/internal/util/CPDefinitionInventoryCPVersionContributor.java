/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.util;

import com.liferay.commerce.product.util.CPVersionContributor;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ethan Bustad
 */
@Component(
	property = "commerce.product.content.contributor.name=CPDefinitionInventoryCPVersionContributor",
	service = CPVersionContributor.class
)
public class CPDefinitionInventoryCPVersionContributor
	implements CPVersionContributor {

	@Override
	public void onDelete(long cpDefinitionId) {
		_cpDefinitionInventoryLocalService.
			deleteCPDefinitionInventoryByCPDefinitionId(cpDefinitionId);
	}

	@Override
	public void onUpdate(long oldCPDefinitionId, long newCPDefinitionId) {
		_cpDefinitionInventoryLocalService.cloneCPDefinitionInventory(
			oldCPDefinitionId, newCPDefinitionId);
	}

	@Reference
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

}