/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
public abstract class BaseBulkActionTaskComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "bulk-actions";
	}

	protected int getBulkActionTaskItemsCount(
			String executionStatus, HttpServletRequest httpServletRequest)
		throws PortalException {

		ObjectEntry bulkActionTaskObjectEntry =
			(ObjectEntry)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM);

		if (bulkActionTaskObjectEntry == null) {
			return -1;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectDefinition bulkActionTaskObjectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BULK_ACTION_TASK", themeDisplay.getCompanyId());

		List<ObjectEntry> objectEntries = ListUtil.filter(
			objectEntryLocalService.getOneToManyObjectEntries(
				bulkActionTaskObjectEntry.getGroupId(),
				objectRelationshipLocalService.getObjectRelationship(
					bulkActionTaskObjectDefinition.getObjectDefinitionId(),
					"bulkActionTaskToBulkActionTaskItems"
				).getObjectRelationshipId(),
				null, false, bulkActionTaskObjectEntry.getObjectEntryId(), true,
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			objectEntry ->
				Objects.equals(
					GetterUtil.getLong(
						objectEntry.getValues(
						).get(
							"r_bulkActionTaskToBulkActionTaskItems_c_" +
								"bulkActionTaskId"
						)),
					bulkActionTaskObjectEntry.getObjectEntryId()) &&
				Objects.equals(
					GetterUtil.getString(
						objectEntry.getValues(
						).get(
							"executionStatus"
						)),
					executionStatus));

		return objectEntries.size();
	}

	@Reference
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Reference
	protected ObjectEntryLocalService objectEntryLocalService;

	@Reference
	protected ObjectRelationshipLocalService objectRelationshipLocalService;

}