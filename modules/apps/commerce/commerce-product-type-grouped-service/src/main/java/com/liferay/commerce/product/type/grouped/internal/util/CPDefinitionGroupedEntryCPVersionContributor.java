/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.internal.util;

import com.liferay.commerce.product.type.grouped.service.CPDefinitionGroupedEntryLocalService;
import com.liferay.commerce.product.util.CPVersionContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ethan Bustad
 */
@Component(
	property = "commerce.product.content.contributor.name=CPDefinitionGroupedEntryCPVersionContributor",
	service = CPVersionContributor.class
)
public class CPDefinitionGroupedEntryCPVersionContributor
	implements CPVersionContributor {

	@Override
	public void onDelete(long cpDefinitionId) {
		_cpDefinitionGroupedEntryLocalService.deleteCPDefinitionGroupedEntries(
			cpDefinitionId);
	}

	@Override
	public void onUpdate(long oldCPDefinitionId, long newCPDefinitionId) {
		_cpDefinitionGroupedEntryLocalService.cloneCPDefinitionGroupedEntries(
			oldCPDefinitionId, newCPDefinitionId);
	}

	@Reference
	private CPDefinitionGroupedEntryLocalService
		_cpDefinitionGroupedEntryLocalService;

}