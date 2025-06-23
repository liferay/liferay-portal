/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.internal.resource.v1_0;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskContentType;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.ItemClassRegistry;
import com.liferay.batch.engine.configuration.BatchEngineTaskCompanyConfiguration;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.constants.CreateStrategy;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.model.BatchEngineImportTaskError;
import com.liferay.batch.engine.service.BatchEngineImportTaskService;
import com.liferay.headless.batch.engine.dto.v1_0.FailedItem;
import com.liferay.headless.batch.engine.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.internal.resource.v1_0.util.ParametersUtil;
import com.liferay.headless.batch.engine.resource.v1_0.ImportTaskResource;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Ivica Cardic
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/import-task.properties",
	property = "batch.engine=true", scope = ServiceScope.PROTOTYPE,
	service = ImportTaskResource.class
)
public class ImportTaskResourceImpl extends BaseImportTaskResourceImpl {

	@Override
	public ImportTask deleteImportTask(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			MultipartBody multipartBody)
		throws Exception {

		return _importFile(
			BatchEngineTaskOperation.DELETE, null,
			multipartBody.getBinaryFile("file"), callbackURL, className, null,
			externalReferenceCode, null, importStrategy, taskItemDelegateName,
			null);
	}

	@Override
	public ImportTask deleteImportTaskObject(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName, Object object)
		throws Exception {

		String contentType = contextHttpServletRequest.getHeader(
			HttpHeaders.CONTENT_TYPE);

		return _importFile(
			BatchEngineTaskOperation.DELETE, null,
			_getBytes(object, contentType), callbackURL, className, null,
			_getBatchEngineTaskContentType(contentType), externalReferenceCode,
			null, importStrategy, taskItemDelegateName, null);
	}

	@Override
	public ImportTask getImportTask(Long importTaskId) throws Exception {
		return _toImportTask(
			_batchEngineImportTaskService.getBatchEngineImportTask(
				importTaskId));
	}

	@Override
	public ImportTask getImportTaskByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return _toImportTask(
			_batchEngineImportTaskService.
				getBatchEngineImportTaskByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId()));
	}

	@Override
	public Response getImportTaskByExternalReferenceCodeContent(
			String externalReferenceCode)
		throws Exception {

		return _getImportTaskContent(
			_batchEngineImportTaskService.
				getBatchEngineImportTaskByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId()));
	}

	@Override
	public Response getImportTaskByExternalReferenceCodeFailedItemReport(
			String externalReferenceCode)
		throws Exception {

		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskService.
				getBatchEngineImportTaskByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return _getImportTaskFailedItemReport(
			batchEngineImportTask.getBatchEngineImportTaskId());
	}

	@Override
	public Response getImportTaskContent(Long importTaskId) throws Exception {
		return _getImportTaskContent(
			_batchEngineImportTaskService.getBatchEngineImportTask(
				importTaskId));
	}

	@Override
	public Response getImportTaskFailedItemReport(Long importTaskId)
		throws Exception {

		return _getImportTaskFailedItemReport(importTaskId);
	}

	@Override
	public ImportTask postImportTask(
			String className, String batchExternalReferenceCode,
			String batchRestrictFields, String callbackURL,
			String createStrategy, String externalReferenceCode,
			String fieldNameMapping, String importStrategy,
			String taskItemDelegateName, MultipartBody multipartBody)
		throws Exception {

		return _importFile(
			BatchEngineTaskOperation.CREATE, batchExternalReferenceCode,
			multipartBody.getBinaryFile("file"), callbackURL, className,
			createStrategy, externalReferenceCode, fieldNameMapping,
			importStrategy, taskItemDelegateName, null);
	}

	@Override
	public ImportTask postImportTaskObject(
			String className, String batchExternalReferenceCode,
			String batchRestrictFields, String callbackURL,
			String createStrategy, String externalReferenceCode,
			String fieldNameMapping, String importStrategy,
			String taskItemDelegateName, Object object)
		throws Exception {

		String contentType = contextHttpServletRequest.getHeader(
			HttpHeaders.CONTENT_TYPE);

		return _importFile(
			BatchEngineTaskOperation.CREATE, batchExternalReferenceCode,
			_getBytes(object, contentType), callbackURL, className,
			createStrategy, _getBatchEngineTaskContentType(contentType),
			externalReferenceCode, fieldNameMapping, importStrategy,
			taskItemDelegateName, null);
	}

	@Override
	public ImportTask putImportTask(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			String updateStrategy, MultipartBody multipartBody)
		throws Exception {

		return _importFile(
			BatchEngineTaskOperation.UPDATE, null,
			multipartBody.getBinaryFile("file"), callbackURL, className, null,
			externalReferenceCode, null, importStrategy, taskItemDelegateName,
			updateStrategy);
	}

	@Override
	public ImportTask putImportTaskObject(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			String updateStrategy, Object object)
		throws Exception {

		String contentType = contextHttpServletRequest.getHeader(
			HttpHeaders.CONTENT_TYPE);

		return _importFile(
			BatchEngineTaskOperation.UPDATE, null,
			_getBytes(object, contentType), callbackURL, className, null,
			_getBatchEngineTaskContentType(contentType), externalReferenceCode,
			null, importStrategy, taskItemDelegateName, updateStrategy);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		Properties batchSizeProperties = PropsUtil.getProperties(
			"batch.size.", true);

		for (Map.Entry<Object, Object> entry : batchSizeProperties.entrySet()) {
			_itemClassBatchSizeMap.put(
				String.valueOf(entry.getKey()),
				GetterUtil.getInteger(entry.getValue()));
		}
	}

	private String _getBatchEngineTaskContentType(String contentType) {
		if (contentType.equals(MediaType.APPLICATION_JSON)) {
			return String.valueOf(BatchEngineTaskContentType.JSON);
		}
		else if (contentType.equals("application/x-ndjson")) {
			return String.valueOf(BatchEngineTaskContentType.JSONL);
		}
		else if (contentType.equals("text/csv")) {
			return String.valueOf(BatchEngineTaskContentType.CSV);
		}

		return contentType;
	}

	private byte[] _getBytes(Object object, String contentType)
		throws Exception {

		byte[] bytes = null;

		if (contentType.equals(MediaType.APPLICATION_JSON)) {
			ObjectMapper objectMapper = new ObjectMapper();

			bytes = objectMapper.writeValueAsBytes(object);
		}
		else {
			String content = (String)object;

			bytes = content.getBytes();
		}

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			_getUnsyncByteArrayOutputStream(
				"fileName", new ByteArrayInputStream(bytes));

		return unsyncByteArrayOutputStream.toByteArray();
	}

	private Map.Entry<byte[], String> _getContentAndExtensionFromCompressedFile(
			InputStream inputStream)
		throws Exception {

		byte[] content = StreamUtil.toByteArray(inputStream);

		String fileName = null;

		try (ZipInputStream zipInputStream = new ZipInputStream(
				new UnsyncByteArrayInputStream(content))) {

			ZipEntry zipEntry = zipInputStream.getNextEntry();

			fileName = zipEntry.getName();
		}

		return new AbstractMap.SimpleImmutableEntry<>(
			content, _file.getExtension(fileName));
	}

	private Map.Entry<byte[], String>
			_getContentAndExtensionFromUncompressedFile(
				String fileName, InputStream inputStream)
		throws Exception {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			_getUnsyncByteArrayOutputStream(fileName, inputStream);

		return new AbstractMap.SimpleImmutableEntry<>(
			unsyncByteArrayOutputStream.toByteArray(),
			_file.getExtension(fileName));
	}

	private int _getImportBatchSize(long companyId) throws Exception {
		BatchEngineTaskCompanyConfiguration
			batchEngineTaskCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					BatchEngineTaskCompanyConfiguration.class, companyId);

		return batchEngineTaskCompanyConfiguration.importBatchSize();
	}

	private Response _getImportTaskContent(
			BatchEngineImportTask batchEngineImportTask)
		throws Exception {

		BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus =
			BatchEngineTaskExecuteStatus.valueOf(
				batchEngineImportTask.getExecuteStatus());

		if ((batchEngineTaskExecuteStatus !=
				BatchEngineTaskExecuteStatus.COMPLETED) &&
			(batchEngineTaskExecuteStatus !=
				BatchEngineTaskExecuteStatus.FAILED)) {

			return Response.status(
				Response.Status.NOT_FOUND
			).build();
		}

		InputStream contentInputStream =
			_batchEngineImportTaskService.openContentInputStream(
				batchEngineImportTask.getBatchEngineImportTaskId());

		StreamingOutput streamingOutput = outputStream -> StreamUtil.transfer(
			contentInputStream, outputStream);

		return Response.ok(
			streamingOutput
		).header(
			"content-disposition",
			"attachment; filename=" + StringUtil.randomString() + ".zip"
		).build();
	}

	private Response _getImportTaskFailedItemReport(long importTaskId)
		throws Exception {

		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskService.getBatchEngineImportTask(
				importTaskId);

		List<BatchEngineImportTaskError> batchEngineImportTaskErrors =
			batchEngineImportTask.getBatchEngineImportTaskErrors();

		StreamingOutput streamingOutput = outputStream -> {
			try (CSVPrinter csvPrinter = new CSVPrinter(
					new BufferedWriter(new OutputStreamWriter(outputStream)),
					CSVFormat.DEFAULT)) {

				csvPrinter.printRecord("item", "itemIndex", "message");

				for (BatchEngineImportTaskError batchEngineImportTaskError :
						batchEngineImportTaskErrors) {

					csvPrinter.printRecord(
						batchEngineImportTaskError.getItem(),
						batchEngineImportTaskError.getItemIndex(),
						batchEngineImportTaskError.getMessage());
				}
			}
		};

		return Response.ok(
			streamingOutput
		).header(
			"Content-Disposition",
			"attachment; filename=" + StringUtil.randomString() + ".csv"
		).build();
	}

	private UnsyncByteArrayOutputStream _getUnsyncByteArrayOutputStream(
			String fileName, InputStream inputStream)
		throws Exception {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				unsyncByteArrayOutputStream)) {

			ZipEntry zipEntry = new ZipEntry(fileName);

			zipOutputStream.putNextEntry(zipEntry);

			StreamUtil.transfer(inputStream, zipOutputStream, false);
		}

		return unsyncByteArrayOutputStream;
	}

	private boolean _hasUniqueScopeParameters(
		Map<String, Serializable> parameters) {

		Set<String> assetLibraryScopeKeys = SetUtil.fromArray(
			"assetLibraryExternalReferenceCode", "assetLibraryId");
		Set<String> siteScopeKeys = SetUtil.fromArray(
			"siteExternalReferenceCode", "siteId");

		boolean hasAssetLibraryScopeKey = false;
		boolean hasSiteScopeKey = false;

		for (String key : parameters.keySet()) {
			if (assetLibraryScopeKeys.contains(key)) {
				hasAssetLibraryScopeKey = true;
			}
			else if (siteScopeKeys.contains(key)) {
				hasSiteScopeKey = true;
			}

			if (hasAssetLibraryScopeKey && hasSiteScopeKey) {
				return false;
			}
		}

		return true;
	}

	private ImportTask _importFile(
			BatchEngineTaskOperation batchEngineTaskOperation,
			String batchExternalReferenceCode, BinaryFile binaryFile,
			String callbackURL, String className, String createStrategy,
			String externalReferenceCode, String fieldNameMappingString,
			String importStrategy, String taskItemDelegateName,
			String updateStrategy)
		throws Exception {

		Map.Entry<byte[], String> entry = null;

		if (StringUtil.endsWith(binaryFile.getFileName(), "zip")) {
			entry = _getContentAndExtensionFromCompressedFile(
				binaryFile.getInputStream());
		}
		else {
			entry = _getContentAndExtensionFromUncompressedFile(
				binaryFile.getFileName(), binaryFile.getInputStream());
		}

		return _importFile(
			batchEngineTaskOperation, batchExternalReferenceCode,
			entry.getKey(), callbackURL, className, createStrategy,
			entry.getValue(), externalReferenceCode, fieldNameMappingString,
			importStrategy, taskItemDelegateName, updateStrategy);
	}

	private ImportTask _importFile(
			BatchEngineTaskOperation batchEngineTaskOperation,
			String batchExternalReferenceCode, byte[] bytes, String callbackURL,
			String className, String createStrategy,
			String batchEngineTaskContentType, String externalReferenceCode,
			String fieldNameMappingString, String importStrategy,
			String taskItemDelegateName, String updateStrategy)
		throws Exception {

		Class<?> clazz = _itemClassRegistry.getItemClass(className);

		if (clazz == null) {
			throw new IllegalArgumentException(
				"Unknown class name: " + className);
		}

		Map<String, Serializable> parameters = ParametersUtil.toParameters(
			contextUriInfo, _ignoredParameters);

		if (!_hasUniqueScopeParameters(parameters)) {
			throw new IllegalArgumentException(
				"Unsupported combination of scope parameters");
		}

		if (Validator.isNotNull(batchExternalReferenceCode)) {
			parameters.put("externalReferenceCode", batchExternalReferenceCode);
		}

		if (createStrategy != null) {
			CreateStrategy createStrategyEnum = CreateStrategy.valueOf(
				createStrategy);

			parameters.put(
				"createStrategy", createStrategyEnum.getDBOperation());
		}

		if (updateStrategy != null) {
			parameters.put("updateStrategy", updateStrategy);
		}

		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskService.addBatchEngineImportTask(
				externalReferenceCode, contextCompany.getCompanyId(),
				contextUser.getUserId(),
				_itemClassBatchSizeMap.getOrDefault(
					className,
					_getImportBatchSize(contextCompany.getCompanyId())),
				callbackURL, className, bytes,
				StringUtil.upperCase(batchEngineTaskContentType),
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				_toMap(fieldNameMappingString),
				_toImportStrategy(importStrategy),
				batchEngineTaskOperation.name(), parameters,
				taskItemDelegateName);

		ExecutorService executorService =
			_portalExecutorManager.getPortalExecutor(
				ImportTaskResourceImpl.class.getName());

		executorService.submit(
			() -> _batchEngineImportTaskExecutor.execute(
				batchEngineImportTask));

		return _toImportTask(batchEngineImportTask);
	}

	private FailedItem _toFailedItem(
		BatchEngineImportTaskError batchEngineImportTaskError) {

		return new FailedItem() {
			{
				setItem(batchEngineImportTaskError::getItem);
				setItemIndex(batchEngineImportTaskError::getItemIndex);
				setMessage(batchEngineImportTaskError::getMessage);
			}
		};
	}

	private int _toImportStrategy(String importStrategy) {
		if ((importStrategy == null) ||
			importStrategy.equals(
				BatchEngineImportTaskConstants.
					IMPORT_STRATEGY_STRING_ON_ERROR_FAIL)) {

			return BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL;
		}

		return BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_CONTINUE;
	}

	private ImportTask _toImportTask(
		BatchEngineImportTask batchEngineImportTask) {

		return new ImportTask() {
			{
				setClassName(batchEngineImportTask::getClassName);
				setContentType(batchEngineImportTask::getContentType);
				setEndTime(batchEngineImportTask::getEndTime);
				setErrorMessage(batchEngineImportTask::getErrorMessage);
				setExecuteStatus(
					() -> ImportTask.ExecuteStatus.create(
						batchEngineImportTask.getExecuteStatus()));
				setExternalReferenceCode(
					batchEngineImportTask::getExternalReferenceCode);
				setFailedItems(
					() -> transformToArray(
						batchEngineImportTask.getBatchEngineImportTaskErrors(),
						batchEngineImportTaskError -> _toFailedItem(
							batchEngineImportTaskError),
						FailedItem.class));
				setId(batchEngineImportTask::getBatchEngineImportTaskId);
				setImportStrategy(
					() -> ImportTask.ImportStrategy.create(
						BatchEngineImportTaskConstants.getImportStrategyString(
							batchEngineImportTask.getImportStrategy())));
				setOperation(
					() -> ImportTask.Operation.create(
						batchEngineImportTask.getOperation()));
				setProcessedItemsCount(
					batchEngineImportTask::getProcessedItemsCount);
				setStartTime(batchEngineImportTask::getStartTime);
				setTotalItemsCount(batchEngineImportTask::getTotalItemsCount);
			}
		};
	}

	private Map<String, String> _toMap(String fieldNameMappingString) {
		if (Validator.isNull(fieldNameMappingString)) {
			return Collections.emptyMap();
		}

		Map<String, String> fieldNameMappingMap = new HashMap<>();

		String[] fieldNameMappings = StringUtil.split(
			fieldNameMappingString, ',');

		for (String fieldNameMapping : fieldNameMappings) {
			String[] fieldNames = StringUtil.split(fieldNameMapping, '=');

			fieldNameMappingMap.put(fieldNames[0], fieldNames[1]);
		}

		return fieldNameMappingMap;
	}

	private static final Set<String> _ignoredParameters = new HashSet<>(
		Arrays.asList(
			"callbackURL", "fieldNameMapping", "taskItemDelegateName"));

	@Reference
	private BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;

	@Reference
	private BatchEngineImportTaskService _batchEngineImportTaskService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private File _file;

	private final Map<String, Integer> _itemClassBatchSizeMap = new HashMap<>();

	@Reference
	private ItemClassRegistry _itemClassRegistry;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

}