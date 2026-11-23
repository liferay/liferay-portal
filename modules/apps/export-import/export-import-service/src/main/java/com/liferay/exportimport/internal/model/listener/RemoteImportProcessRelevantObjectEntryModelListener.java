/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.model.listener;

import com.liferay.exportimport.internal.background.task.RemoteImportBackgroundTaskExecutor;
import com.liferay.exportimport.internal.constants.RemoteInstanceConstants;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.util.ScopeUtil;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.listener.RelevantObjectEntryModelListener;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = RelevantObjectEntryModelListener.class)
public class RemoteImportProcessRelevantObjectEntryModelListener
	extends BaseModelListener<ObjectEntry>
	implements RelevantObjectEntryModelListener {

	@Override
	public String getObjectDefinitionExternalReferenceCode() {
		return RemoteInstanceConstants.
			OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE_REMOTE_IMPORT_PROCESS;
	}

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		Map<String, Serializable> values = objectEntry.getValues();

		try {
			Group group = _groupLocalService.getCompanyGroup(
				objectEntry.getCompanyId());

			_backgroundTaskManager.addBackgroundTask(
				objectEntry.getUserId(), group.getGroupId(),
				RemoteImportBackgroundTaskExecutor.class.getName(),
				RemoteImportBackgroundTaskExecutor.class.getName(),
				HashMapBuilder.<String, Serializable>put(
					"exportProcessId",
					MapUtil.getLong(values, "exportProcessId")
				).put(
					"objectEntryId", objectEntry.getObjectEntryId()
				).put(
					"remoteInstanceId",
					MapUtil.getLong(
						values,
						RemoteInstanceConstants.
							OBJECT_FIELD_NAME_REMOTE_INSTANCE_ID)
				).build(),
				new ServiceContext());
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onBeforeCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		Map<String, Serializable> values = objectEntry.getValues();

		try {
			BackgroundTask exportBackgroundTask =
				_backgroundTaskManager.fetchBackgroundTask(
					MapUtil.getLong(values, "exportProcessId"));

			if ((exportBackgroundTask == null) ||
				(exportBackgroundTask.getCompanyId() !=
					objectEntry.getCompanyId())) {

				throw new ObjectEntryValuesException.InvalidObjectField(
					null, "The export process does not exist",
					"the-export-process-does-not-exist");
			}

			if (exportBackgroundTask.getStatus() !=
					BackgroundTaskConstants.STATUS_SUCCESSFUL) {

				throw new ObjectEntryValuesException.InvalidObjectField(
					null, "Only a successful export process can be sent",
					"only-a-successful-export-process-can-be-sent");
			}

			Group group = _groupLocalService.getGroup(
				exportBackgroundTask.getGroupId());

			if (!ScopeUtil.isCompanyScoped(group) ||
				!Objects.equals(
					exportBackgroundTask.getTaskExecutorClassName(),
					BackgroundTaskExecutorNames.
						LAYOUT_EXPORT_BACKGROUND_TASK_EXECUTOR)) {

				throw new ObjectEntryValuesException.InvalidObjectField(
					null, "The export process is not an instance export",
					"the-export-process-is-not-an-instance-export");
			}

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (!permissionChecker.isGroupAdmin(group.getGroupId())) {
				PortletPermissionUtil.check(
					permissionChecker, group.getGroupId(),
					PortletKeys.COMPANY_EXPORT,
					ActionKeys.ACCESS_IN_CONTROL_PANEL);
			}

			_objectEntryService.getObjectEntry(
				MapUtil.getLong(
					values,
					RemoteInstanceConstants.
						OBJECT_FIELD_NAME_REMOTE_INSTANCE_ID));
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		Map<String, Serializable> originalValues =
			originalObjectEntry.getValues();
		Map<String, Serializable> values = objectEntry.getValues();

		for (String key : _IMMUTABLE_OBJECT_FIELD_NAMES) {
			if (!values.containsKey(key) ||
				Objects.equals(originalValues.get(key), values.get(key))) {

				continue;
			}

			throw new ModelListenerException(
				new ObjectEntryValuesException.InvalidObjectField(
					null,
					"The export process and remote instance cannot change",
					"the-export-process-and-remote-instance-cannot-change"));
		}
	}

	private static final String[] _IMMUTABLE_OBJECT_FIELD_NAMES = {
		"exportProcessId",
		RemoteInstanceConstants.OBJECT_FIELD_NAME_REMOTE_INSTANCE_ID
	};

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}