/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.scheduler;

import com.liferay.exportimport.internal.constants.RemoteInstanceConstants;
import com.liferay.exportimport.internal.notifications.RemoteImportUserNotificationUtil;
import com.liferay.exportimport.internal.util.RemoteImportHttpUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.interval.IntervalActionProcessor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = SchedulerJobConfiguration.class)
public class CheckRemoteImportProcessesSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeConsumer<Long, Exception>
		getCompanyJobExecutorUnsafeConsumer() {

		return companyId -> _checkRemoteImportProcesses(companyId);
	}

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _companyLocalService.forEachCompanyId(
			companyId -> _checkRemoteImportProcesses(companyId));
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			1, TimeUnit.MINUTE);
	}

	private boolean _checkRemoteImportProcess(long objectEntryId)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		Map<String, Serializable> values = objectEntry.getValues();

		ObjectEntry remoteInstanceObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				MapUtil.getLong(
					values,
					RemoteInstanceConstants.
						OBJECT_FIELD_NAME_REMOTE_INSTANCE_ID));

		JSONObject importProcessJSONObject =
			RemoteImportHttpUtil.fetchImportProcessJSONObject(
				objectEntry.getCompanyId(),
				MapUtil.getLong(values, "remoteImportProcessId"),
				remoteInstanceObjectEntry.getValues());

		if (importProcessJSONObject == null) {
			String errorMessage = _language.get(
				_getLocale(objectEntry.getUserId()),
				"the-remote-import-was-deleted-from-the-remote-instance");

			_updateRemoteImportProcess(
				objectEntry,
				HashMapBuilder.<String, Serializable>put(
					"errorMessage", errorMessage
				).put(
					"importStatus", BackgroundTaskConstants.STATUS_FAILED
				).build());

			return true;
		}

		JSONObject statusJSONObject = importProcessJSONObject.getJSONObject(
			"status");

		int status = statusJSONObject.getInt("code");

		if (!ArrayUtil.contains(_FINAL_STATUSES, status)) {
			return false;
		}

		_updateRemoteImportProcess(
			objectEntry,
			HashMapBuilder.<String, Serializable>put(
				"importStatus", status
			).build());

		return true;
	}

	private void _checkRemoteImportProcesses(long companyId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-71352")) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					RemoteInstanceConstants.
						OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE_REMOTE_IMPORT_PROCESS,
					companyId);

		if (objectDefinition == null) {
			return;
		}

		Predicate predicate = _filterFactory.create(
			"importStatus eq " + BackgroundTaskConstants.STATUS_IN_PROGRESS,
			objectDefinition);

		boolean skipObjectEntryResourcePermission =
			ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission();

		ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

		try {
			IntervalActionProcessor<Void> intervalActionProcessor =
				new IntervalActionProcessor<>(
					_objectEntryLocalService.getValuesListCount(
						new Long[] {0L}, companyId,
						objectDefinition.getUserId(),
						objectDefinition.getObjectDefinitionId(), predicate,
						false, null));

			intervalActionProcessor.setPerformIntervalActionMethod(
				(start, end) -> {
					List<Long> objectEntryIds =
						_objectEntryLocalService.getPrimaryKeys(
							new Long[] {0L}, companyId,
							objectDefinition.getUserId(),
							objectDefinition.getObjectDefinitionId(), predicate,
							false, null, start, end, null);

					for (long objectEntryId : objectEntryIds) {
						try {
							if (_checkRemoteImportProcess(objectEntryId)) {
								continue;
							}
						}
						catch (Exception exception) {
							if (_log.isWarnEnabled()) {
								_log.warn(
									"Unable to check remote import process " +
										objectEntryId,
									exception);
							}
						}

						intervalActionProcessor.incrementStart();
					}

					return null;
				});

			intervalActionProcessor.performIntervalActions();
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
				skipObjectEntryResourcePermission);
		}
	}

	private Locale _getLocale(long userId) {
		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return LocaleUtil.getDefault();
		}

		return user.getLocale();
	}

	private void _updateRemoteImportProcess(
			ObjectEntry objectEntry, Map<String, Serializable> values)
		throws Exception {

		RemoteImportUserNotificationUtil.sendUserNotificationEvent(
			_objectEntryLocalService.partialUpdateObjectEntry(
				objectEntry.getUserId(), objectEntry.getObjectEntryId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				values, new ServiceContext()));
	}

	private static final int[] _FINAL_STATUSES = {
		BackgroundTaskConstants.STATUS_CANCELLED,
		BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
		BackgroundTaskConstants.STATUS_FAILED,
		BackgroundTaskConstants.STATUS_SUCCESSFUL
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CheckRemoteImportProcessesSchedulerJobConfiguration.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}