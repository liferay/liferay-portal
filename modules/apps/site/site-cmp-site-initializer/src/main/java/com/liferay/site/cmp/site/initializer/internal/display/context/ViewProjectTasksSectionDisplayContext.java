/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Albuquerque
 */
public class ViewProjectTasksSectionDisplayContext
	extends BaseTasksSectionDisplayContext {

	public ViewProjectTasksSectionDisplayContext(
		AssetTagLocalService assetTagLocalService,
		ClassNameLocalService classNameLocalService,
		ObjectDefinition cmpProjectObjectDefinition,
		ObjectDefinition cmpTaskObjectDefinition,
		DepotEntryLocalService depotEntryLocalService,
		HttpServletRequest httpServletRequest,
		ListTypeEntryLocalService listTypeEntryLocalService,
		ObjectEntryService objectEntryService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectStateFlowLocalService objectStateFlowLocalService,
		ObjectStateLocalService objectStateLocalService,
		RoleService roleService) {

		super(
			assetTagLocalService, classNameLocalService,
			cmpProjectObjectDefinition, cmpTaskObjectDefinition,
			depotEntryLocalService, httpServletRequest,
			listTypeEntryLocalService, objectEntryService,
			objectFieldLocalService, objectStateFlowLocalService,
			objectStateLocalService, roleService);
	}

	@Override
	public String getAPIURL() {
		StringBundler sb = new StringBundler(6);

		sb.append("/o/search/v1.0/search?emptySearch=true");
		sb.append("&filter=(objectDefinitionId eq ");
		sb.append(objectDefinition.getObjectDefinitionId());

		if (assetEntry != null) {
			sb.append(" and scopeGroupId eq ");
			sb.append(assetEntry.getGroupId());
		}

		sb.append(")&nestedFields=cmpProjectToCMPTasks,embedded");

		return sb.toString();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return getProjectTasksFDSActionDropdownItems(
			objectDefinition.getClassName());
	}

	public Map<String, Object> getTasksQuickFiltersProperties() {
		return HashMapBuilder.<String, Object>put(
			"cmpProjectObjectEntryId",
			() -> {
				if (assetEntry == null) {
					return null;
				}

				return assetEntry.getClassPK();
			}
		).build();
	}

}