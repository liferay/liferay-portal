/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.site.generator.internal.object.action.executor;

import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.object.action.executor.BaseObjectActionExecutor;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.nio.charset.StandardCharsets;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	property = "object.action.executor.key=" + CommitCSGGenerationObjectActionExecutor.KEY,
	service = ObjectActionExecutor.class
)
public class CommitCSGGenerationObjectActionExecutor
	extends BaseObjectActionExecutor implements ObjectDefinitionScoped {

	public static final String KEY = "commit-csg-generation";

	@Override
	public List<String> getAllowedObjectDefinitionNames() {
		return List.of("CSGGeneration");
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	protected void doExecute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception {

		long objectEntryId = payloadJSONObject.getLong("classPK");

		ExecutorService executorService =
			_portalExecutorManager.getPortalExecutor("CSGGenerationCommit");

		executorService.submit(
			() -> _doExecute(companyId, objectEntryId, userId));
	}

	private String _awaitCompletion(
			long batchEngineImportTaskId, String fileName)
		throws InterruptedException {

		long deadline = System.currentTimeMillis() + (Time.MINUTE * 10);

		while (System.currentTimeMillis() < deadline) {
			Thread.sleep(Time.SECOND * 2);

			BatchEngineImportTask batchEngineImportTask =
				_batchEngineImportTaskLocalService.fetchBatchEngineImportTask(
					batchEngineImportTaskId);

			if (batchEngineImportTask == null) {
				continue;
			}

			if (Objects.equals(
					BatchEngineTaskExecuteStatus.COMPLETED.name(),
					batchEngineImportTask.getExecuteStatus())) {

				return null;
			}

			if (Objects.equals(
					BatchEngineTaskExecuteStatus.FAILED.name(),
					batchEngineImportTask.getExecuteStatus())) {

				return batchEngineImportTask.getErrorMessage();
			}
		}

		return StringBundler.concat(
			"Item ", fileName, " timed out after ", Time.MINUTE * 10, "ms");
	}

	private void _doExecute(long companyId, long objectEntryId, long userId) {
		try {
			ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
				objectEntryId);

			String generationStatus = MapUtil.getString(
				objectEntry.getValues(), "generationStatus");

			if (!(Objects.equals(generationStatus, "failed") ||
				  Objects.equals(generationStatus, "ready"))) {

				throw new UnsupportedOperationException(
					StringBundler.concat(
						"Generation ", objectEntry.getExternalReferenceCode(),
						" is not in a committable state: ", generationStatus));
			}

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						"L_CSG_GENERATION_ITEM", companyId);

			List<Map<String, Serializable>> valuesList =
				_objectEntryLocalService.getValuesList(
					0, companyId, userId,
					objectDefinition.getObjectDefinitionId(),
					_filterFactory.create(
						StringBundler.concat(
							"r_items_l_csgGenerationId eq '", objectEntryId,
							"'"),
						objectDefinition),
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					new Sort[] {new Sort("loadOrder", false)});

			if (valuesList.isEmpty()) {
				throw new IllegalArgumentException(
					"There are no items to commit");
			}

			long objectEntryFolderId = objectEntry.getObjectEntryFolderId();

			_objectEntryLocalService.partialUpdateObjectEntry(
				userId, objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"generationStatus", "generating"
				).build(),
				new ServiceContext());

			for (Map<String, Serializable> values : valuesList) {
				String fileName = GetterUtil.getString(values.get("fileName"));

				BatchEngineImportTask batchEngineImportTask =
					_executeBatchEngineImportTask(
						companyId, GetterUtil.getLong(values.get("batchFile")),
						fileName, userId);

				String errorMessage = _awaitCompletion(
					batchEngineImportTask.getBatchEngineImportTaskId(),
					fileName);

				if (errorMessage != null) {
					_objectEntryLocalService.partialUpdateObjectEntry(
						userId, objectEntryId, objectEntryFolderId,
						HashMapBuilder.<String, Serializable>put(
							"failureReason", errorMessage
						).put(
							"generationStatus", "failed"
						).build(),
						new ServiceContext());

					return;
				}
			}

			_objectEntryLocalService.partialUpdateObjectEntry(
				userId, objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"commitDate", new Date()
				).put(
					"generationStatus", "committed"
				).build(),
				new ServiceContext());
		}
		catch (Exception exception1) {
			_log.error(exception1);

			try {
				ObjectEntry objectEntry =
					_objectEntryLocalService.getObjectEntry(objectEntryId);

				_objectEntryLocalService.partialUpdateObjectEntry(
					userId, objectEntryId, objectEntry.getObjectEntryFolderId(),
					HashMapBuilder.<String, Serializable>put(
						"failureReason", exception1.getMessage()
					).put(
						"generationStatus", "failed"
					).build(),
					new ServiceContext());
			}
			catch (Exception exception2) {
				_log.error(exception2);
			}
		}
	}

	private BatchEngineImportTask _executeBatchEngineImportTask(
			long companyId, long fileEntryId, String fileName, long userId)
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		String batchFileJSON = null;

		try (InputStream inputStream = fileEntry.getContentStream()) {
			batchFileJSON = StringUtil.read(inputStream);
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}

		JSONObject batchFileJSONObject = _jsonFactory.createJSONObject(
			batchFileJSON);

		JSONObject configurationJSONObject = batchFileJSONObject.getJSONObject(
			"configuration");

		String className = configurationJSONObject.getString("className");

		JSONArray itemsJSONArray = batchFileJSONObject.getJSONArray("items");

		String taskItemDelegateName = configurationJSONObject.getString(
			"taskItemDelegateName");

		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskLocalService.addBatchEngineImportTask(
				null, companyId, userId, 100, null, className,
				_zipJSON(fileName, itemsJSONArray.toString()), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				_toStringMap(
					configurationJSONObject.getJSONObject(
						"fieldNameMappingMap")),
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				"CREATE",
				_toSerializableMap(
					configurationJSONObject.getJSONObject("parameters")),
				taskItemDelegateName);

		_batchEngineImportTaskExecutor.execute(batchEngineImportTask);

		return batchEngineImportTask;
	}

	private Map<String, Serializable> _toSerializableMap(
		JSONObject jsonObject) {

		if (jsonObject == null) {
			return null;
		}

		Map<String, Serializable> map = new HashMap<>();

		for (String key : jsonObject.keySet()) {
			Object value = jsonObject.get(key);

			if (value instanceof Serializable) {
				map.put(key, (Serializable)value);
			}
			else if (value != null) {
				map.put(key, value.toString());
			}
		}

		return map;
	}

	private Map<String, String> _toStringMap(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}

		Map<String, String> map = new HashMap<>();

		for (String key : jsonObject.keySet()) {
			map.put(key, jsonObject.getString(key));
		}

		return map;
	}

	private byte[] _zipJSON(String fileName, String json) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				byteArrayOutputStream)) {

			zipOutputStream.putNextEntry(new ZipEntry(fileName));

			zipOutputStream.write(json.getBytes(StandardCharsets.UTF_8));

			zipOutputStream.closeEntry();
		}

		return byteArrayOutputStream.toByteArray();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommitCSGGenerationObjectActionExecutor.class);

	@Reference
	private BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;

	@Reference
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

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
	private PortalExecutorManager _portalExecutorManager;

}