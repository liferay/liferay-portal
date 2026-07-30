/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.internal.resource.v1_0;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntryModel;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.cmp.dto.v1_0.TaskStatistics;
import com.liferay.headless.cmp.resource.v1_0.TaskStatisticsResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carolina Barbosa
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/task-statistics.properties",
	scope = ServiceScope.PROTOTYPE, service = TaskStatisticsResource.class
)
public class TaskStatisticsResourceImpl extends BaseTaskStatisticsResourceImpl {

	@Override
	public TaskStatistics getProjectTaskStatistics(Long projectId)
		throws Exception {

		return _toTaskStatistics(
			_objectEntryService.getObjectEntry(projectId),
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", contextCompany.getCompanyId()));
	}

	@Override
	public TaskStatistics getTaskStatistics() throws Exception {
		return _toTaskStatistics(
			null,
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", contextCompany.getCompanyId()));
	}

	private long _getCount(
			ObjectEntry cmpProjectObjectEntry,
			ObjectDefinition cmpTaskObjectDefinition, String filterString)
		throws Exception {

		List<Long> groupIds = new ArrayList<>();

		if (cmpProjectObjectEntry == null) {
			groupIds = transform(
				_depotEntryLocalService.getDepotEntries(
					contextCompany.getCompanyId(), DepotConstants.TYPE_PROJECT),
				DepotEntryModel::getGroupId);
		}
		else {
			groupIds.add(cmpProjectObjectEntry.getGroupId());
		}

		return _objectEntryLocalService.getValuesListCount(
			groupIds.toArray(new Long[0]), 0, 0,
			cmpTaskObjectDefinition.getObjectDefinitionId(),
			_filterFactory.create(filterString, cmpTaskObjectDefinition), true,
			null);
	}

	private TaskStatistics _toTaskStatistics(
		ObjectEntry cmpProjectObjectEntry,
		ObjectDefinition cmpTaskObjectDefinition) {

		return new TaskStatistics() {
			{
				setBlockedCount(
					() -> _getCount(
						cmpProjectObjectEntry, cmpTaskObjectDefinition,
						"state eq 'blocked'"));
				setInProgressCount(
					() -> _getCount(
						cmpProjectObjectEntry, cmpTaskObjectDefinition,
						"state eq 'inProgress'"));
				setOverdueCount(
					() -> _getCount(
						cmpProjectObjectEntry, cmpTaskObjectDefinition,
						"dueDate lt " + LocalDate.now() +
							" and state ne 'done'"));
				setTotalCount(
					() -> _getCount(
						cmpProjectObjectEntry, cmpTaskObjectDefinition,
						StringPool.BLANK));
			}
		};
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}