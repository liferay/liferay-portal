/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.scheduler;

import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.model.BatchEngineImportTaskError;
import com.liferay.batch.engine.service.BatchEngineImportTaskService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.cms.site.initializer.configuration.BulkActionTaskConfiguration;
import com.liferay.site.cms.site.initializer.constants.BulkActionExecutionStatusConstants;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	configurationPid = "com.liferay.site.cms.site.initializer.configuration.BulkActionTaskConfiguration",
	service = SchedulerJobConfiguration.class
)
public class UpdateBulkActionTaskSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _companyLocalService.forEachCompanyId(
			this::_updateObjectEntries);
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return _triggerConfiguration;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		BulkActionTaskConfiguration bulkActionTaskConfiguration =
			ConfigurableUtil.createConfigurable(
				BulkActionTaskConfiguration.class, properties);

		_triggerConfiguration = TriggerConfiguration.createTriggerConfiguration(
			bulkActionTaskConfiguration.checkInterval(), TimeUnit.MINUTE);
	}

	private BatchEngineImportTaskError _getBatchEngineImportTaskError(
		List<BatchEngineImportTaskError> batchEngineImportTaskErrors,
		long classPK) {

		List<BatchEngineImportTaskError> filteredBatchEngineImportTaskErrors =
			ListUtil.filter(
				batchEngineImportTaskErrors,
				batchEngineImportTaskError -> {
					try {
						JSONObject jsonObject = _jsonFactory.createJSONObject(
							batchEngineImportTaskError.getItem());

						return classPK == jsonObject.getLong("id");
					}
					catch (JSONException jsonException) {
						if (_log.isDebugEnabled()) {
							_log.debug("Unable to parse JSON", jsonException);
						}

						return false;
					}
				});

		if (ListUtil.isNotEmpty(filteredBatchEngineImportTaskErrors)) {
			return filteredBatchEngineImportTaskErrors.get(0);
		}

		return null;
	}

	private Tuple _getTuple(
			ObjectRelationship objectRelationship, Long primaryKey)
		throws Exception {

		Date completionDate = null;
		int numberOfFailedItems = 0;
		int numberOfSuccessfulItems = 0;

		for (ObjectEntry objectEntry :
				_objectEntryLocalService.getOneToManyObjectEntries(
					0, objectRelationship.getObjectRelationshipId(), null,
					false, primaryKey, true, null, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			Map<String, Serializable> values = objectEntry.getValues();

			String executionStatus = GetterUtil.getString(
				values.get("executionStatus"));

			if (executionStatus.equals(
					BulkActionExecutionStatusConstants.COMPLETED) ||
				executionStatus.equals(
					BulkActionExecutionStatusConstants.FAILED)) {

				if (executionStatus.equals(
						BulkActionExecutionStatusConstants.COMPLETED) &&
					Validator.isBlank(
						GetterUtil.getString(values.get("description")))) {

					numberOfSuccessfulItems++;
				}
				else {
					numberOfFailedItems++;
				}

				continue;
			}

			long classPK = GetterUtil.getLong(values.get("classPK"));

			BatchEngineImportTask batchEngineImportTask =
				_batchEngineImportTaskService.getBatchEngineImportTask(
					GetterUtil.getLong(values.get("importTaskId")));

			BatchEngineImportTaskError batchEngineImportTaskError =
				_getBatchEngineImportTaskError(
					batchEngineImportTask.getBatchEngineImportTaskErrors(),
					classPK);

			if (batchEngineImportTaskError != null) {
				values.put(
					"description", batchEngineImportTaskError.getMessage());
			}

			values.put(
				"executionStatus",
				StringUtil.toLowerCase(
					batchEngineImportTask.getExecuteStatus()));

			ObjectEntry updateObjectEntry =
				_objectEntryLocalService.partialUpdateObjectEntry(
					objectEntry.getUserId(), objectEntry.getObjectEntryId(),
					objectEntry.getObjectEntryFolderId(), values,
					new ServiceContext());

			values = updateObjectEntry.getValues();

			executionStatus = GetterUtil.getString(
				values.get("executionStatus"));

			if (executionStatus.equals(
					BulkActionExecutionStatusConstants.COMPLETED) &&
				Validator.isBlank(
					GetterUtil.getString(values.get("description")))) {

				numberOfSuccessfulItems++;
			}
			else if (executionStatus.equals(
						BulkActionExecutionStatusConstants.FAILED) ||
					 !Validator.isBlank(
						 GetterUtil.getString(values.get("description")))) {

				numberOfFailedItems++;
			}

			if ((completionDate == null) ||
				completionDate.before(batchEngineImportTask.getEndTime())) {

				completionDate = batchEngineImportTask.getEndTime();
			}
		}

		return new Tuple(
			completionDate, numberOfFailedItems, numberOfSuccessfulItems);
	}

	private void _partialUpdateObjectEntry(
			ObjectRelationship objectRelationship, Long primaryKey)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			primaryKey);

		Map<String, Serializable> values = objectEntry.getValues();

		try {
			Tuple tuple = _getTuple(objectRelationship, primaryKey);

			values.put("completionDate", (Date)tuple.getObject(0));

			int numberOfFailedItems = (int)tuple.getObject(1);

			values.put("numberOfFailedItems", numberOfFailedItems);

			int numberOfSuccessfulItems = (int)tuple.getObject(2);

			values.put("numberOfSuccessfulItems", numberOfSuccessfulItems);

			long numberOfItems = GetterUtil.getInteger(
				values.get("numberOfItems"));

			if (numberOfItems ==
					(numberOfSuccessfulItems + numberOfFailedItems)) {

				values.put(
					"executionStatus",
					BulkActionExecutionStatusConstants.COMPLETED);
			}
			else {
				values.put(
					"executionStatus",
					BulkActionExecutionStatusConstants.STARTED);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			values.put("completionDate", new Date());
			values.put(
				"executionStatus", BulkActionExecutionStatusConstants.FAILED);
		}

		_objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), values, new ServiceContext());
	}

	private void _updateObjectEntries(long companyId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BULK_ACTION_TASK", companyId);

		List<Long> primaryKeys = _objectEntryLocalService.getPrimaryKeys(
			new Long[0], companyId, 0, objectDefinition.getObjectDefinitionId(),
			_filterFactory.create(
				StringBundler.concat(
					"executionStatus in ('",
					BulkActionExecutionStatusConstants.INITIAL, "','",
					BulkActionExecutionStatusConstants.STARTED, "')"),
				objectDefinition),
			false, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		if (ListUtil.isEmpty(primaryKeys)) {
			return;
		}

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectDefinition.getObjectDefinitionId(),
				"bulkActionTaskToBulkActionTaskItems");

		for (Long primaryKey : primaryKeys) {
			_partialUpdateObjectEntry(objectRelationship, primaryKey);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateBulkActionTaskSchedulerJobConfiguration.class);

	@Reference
	private BatchEngineImportTaskService _batchEngineImportTaskService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private TriggerConfiguration _triggerConfiguration;

}