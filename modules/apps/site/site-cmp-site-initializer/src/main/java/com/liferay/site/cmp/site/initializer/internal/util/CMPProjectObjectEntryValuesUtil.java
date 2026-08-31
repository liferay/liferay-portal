/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Pedro Leite
 */
public class CMPProjectObjectEntryValuesUtil {

	public static void updateCompletionRate(
			ObjectEntry cmpTaskObjectEntry,
			FilterFactory<Predicate> filterFactory)
		throws PortalException {

		if (cmpTaskObjectEntry == null) {
			return;
		}

		ObjectDefinition cmpTaskObjectDefinition =
			cmpTaskObjectEntry.getObjectDefinition();

		if (!StringUtil.equals(
				cmpTaskObjectDefinition.getExternalReferenceCode(),
				"L_CMP_TASK")) {

			return;
		}

		ObjectEntry cmpProjectObjectEntry =
			ObjectEntryLocalServiceUtil.fetchObjectEntry(
				MapUtil.getLong(
					cmpTaskObjectEntry.getValues(),
					"r_cmpProjectToCMPTasks_c_cmpProjectId"));

		if (cmpProjectObjectEntry == null) {
			return;
		}

		int completionRate = 0;

		int totalCount = _getCount(
			cmpTaskObjectDefinition, cmpTaskObjectEntry, filterFactory,
			StringPool.BLANK);

		if (totalCount != 0) {
			int filteredCount = _getCount(
				cmpTaskObjectDefinition, cmpTaskObjectEntry, filterFactory,
				"state eq 'done'");

			completionRate = (filteredCount * 100) / totalCount;
		}

		if (Objects.equals(
				MapUtil.getInteger(
					cmpProjectObjectEntry.getValues(), "completionRate"),
				completionRate)) {

			return;
		}

		ObjectEntryLocalServiceUtil.partialUpdateObjectEntry(
			cmpProjectObjectEntry.getUserId(),
			cmpProjectObjectEntry.getObjectEntryId(),
			cmpProjectObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"completionRate", completionRate
			).build(),
			new ServiceContext());
	}

	private static int _getCount(
			ObjectDefinition cmpTaskObjectDefinition,
			ObjectEntry cmpTaskObjectEntry,
			FilterFactory<Predicate> filterFactory, String filterString)
		throws PortalException {

		return ObjectEntryLocalServiceUtil.getValuesListCount(
			new Long[] {cmpTaskObjectEntry.getGroupId()}, 0, 0,
			cmpTaskObjectEntry.getObjectDefinitionId(),
			ObjectEntryTable.INSTANCE.status.neq(
				WorkflowConstants.STATUS_DRAFT
			).and(
				filterFactory.create(filterString, cmpTaskObjectDefinition)
			),
			false, null);
	}

}