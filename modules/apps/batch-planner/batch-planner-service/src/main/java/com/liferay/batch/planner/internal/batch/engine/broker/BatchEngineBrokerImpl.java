/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.internal.batch.engine.broker;

import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.constants.CreateStrategy;
import com.liferay.batch.engine.jaxrs.uri.BatchEngineUriInfo;
import com.liferay.batch.planner.batch.engine.broker.BatchEngineBroker;
import com.liferay.batch.planner.constants.BatchPlannerPlanConstants;
import com.liferay.batch.planner.constants.BatchPlannerPolicyConstants;
import com.liferay.batch.planner.model.BatchPlannerMapping;
import com.liferay.batch.planner.model.BatchPlannerMappingModel;
import com.liferay.batch.planner.model.BatchPlannerPlan;
import com.liferay.batch.planner.model.BatchPlannerPolicy;
import com.liferay.batch.planner.service.BatchPlannerMappingLocalService;
import com.liferay.batch.planner.service.BatchPlannerPlanLocalService;
import com.liferay.headless.batch.engine.resource.v1_0.ExportTaskResource;
import com.liferay.headless.batch.engine.resource.v1_0.ImportTaskResource;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;

import jakarta.ws.rs.core.UriInfo;

import java.io.File;
import java.io.FileInputStream;

import java.net.URI;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Beslic
 */
@Component(service = BatchEngineBroker.class)
public class BatchEngineBrokerImpl implements BatchEngineBroker {

	@Override
	public void submit(long batchPlannerPlanId) throws Exception {
		BatchPlannerPlan batchPlannerPlan =
			_batchPlannerPlanLocalService.getBatchPlannerPlan(
				batchPlannerPlanId);

		_batchPlannerPlanLocalService.updateStatus(
			batchPlannerPlan.getBatchPlannerPlanId(),
			BatchPlannerPlanConstants.STATUS_QUEUED);

		try {
			if (batchPlannerPlan.isExport()) {
				_submitExportTask(batchPlannerPlan);
			}
			else {
				_submitImportTask(batchPlannerPlan);
			}
		}
		catch (Exception exception) {
			_batchPlannerPlanLocalService.updateStatus(
				batchPlannerPlan.getBatchPlannerPlanId(),
				BatchPlannerPlanConstants.STATUS_FAILED);

			throw exception;
		}
	}

	private String _getFieldNameMapping(
		List<BatchPlannerMapping> batchPlannerMappings) {

		if (batchPlannerMappings.isEmpty()) {
			throw new IllegalArgumentException(
				"There are no batch planner mappings");
		}

		StringBundler sb = new StringBundler(
			(batchPlannerMappings.size() * 3) - 1);

		Iterator<BatchPlannerMapping> iterator =
			batchPlannerMappings.iterator();

		while (iterator.hasNext()) {
			BatchPlannerMapping batchPlannerMapping = iterator.next();

			sb.append(batchPlannerMapping.getExternalFieldName());

			sb.append(StringPool.EQUAL);
			sb.append(batchPlannerMapping.getInternalFieldName());

			if (iterator.hasNext()) {
				sb.append(StringPool.COMMA);
			}
		}

		return sb.toString();
	}

	private File _getFile(long batchPlannerPlanId) throws Exception {
		BatchPlannerPlan batchPlannerPlan =
			_batchPlannerPlanLocalService.getBatchPlannerPlan(
				batchPlannerPlanId);

		return new File(new URI(batchPlannerPlan.getExternalURL()));
	}

	private String[] _getHeaderNames(
		List<BatchPlannerMapping> batchPlannerMappings,
		UnsafeFunction<BatchPlannerMapping, String, Exception> unsafeFunction) {

		return TransformUtil.transformToArray(
			batchPlannerMappings, unsafeFunction, String.class);
	}

	private String _getImportErrorStrategy(BatchPlannerPlan batchPlannerPlan)
		throws Exception {

		BatchPlannerPolicy batchPlannerPolicy =
			batchPlannerPlan.getBatchPlannerPolicy("onErrorFail");

		boolean onErrorFail = Boolean.valueOf(batchPlannerPolicy.getValue());

		if (onErrorFail) {
			return BatchEngineImportTaskConstants.
				IMPORT_STRATEGY_STRING_ON_ERROR_FAIL;
		}

		return BatchEngineImportTaskConstants.
			IMPORT_STRATEGY_STRING_ON_ERROR_CONTINUE;
	}

	private UriInfo _getUriInfo(
		BatchPlannerPlan batchPlannerPlan,
		Map<String, String> planPolicyNameTypes) {

		BatchEngineUriInfo.Builder builder = new BatchEngineUriInfo.Builder();

		for (String name : planPolicyNameTypes.keySet()) {
			builder.queryParameter(
				name,
				_getValue(batchPlannerPlan.fetchBatchPlannerPolicy(name)));
		}

		return builder.taskItemDelegateName(
			batchPlannerPlan.getTaskItemDelegateName()
		).build();
	}

	private String _getValue(BatchPlannerPolicy batchPlannerPolicy) {
		if (batchPlannerPolicy != null) {
			return batchPlannerPolicy.getValue();
		}

		return null;
	}

	private void _submitExportTask(BatchPlannerPlan batchPlannerPlan)
		throws Exception {

		_exportTaskResource.setContextCompany(
			_companyLocalService.getCompany(batchPlannerPlan.getCompanyId()));
		_exportTaskResource.setContextUriInfo(
			_getUriInfo(
				batchPlannerPlan,
				BatchPlannerPolicyConstants.exportPlanPolicyNameTypes));
		_exportTaskResource.setContextUser(
			_userLocalService.getUser(batchPlannerPlan.getUserId()));

		_exportTaskResource.postExportTask(
			batchPlannerPlan.getInternalClassName(),
			batchPlannerPlan.getExternalType(), null, null,
			String.valueOf(batchPlannerPlan.getBatchPlannerPlanId()),
			StringUtil.merge(
				_getHeaderNames(
					_batchPlannerMappingLocalService.getBatchPlannerMappings(
						batchPlannerPlan.getBatchPlannerPlanId()),
					BatchPlannerMappingModel::getInternalFieldName),
				StringPool.COMMA),
			batchPlannerPlan.getTaskItemDelegateName());
	}

	private void _submitImportTask(BatchPlannerPlan batchPlannerPlan)
		throws Exception {

		ImportTaskResource importTaskResource = _factory.create(
		).uriInfo(
			_getUriInfo(
				batchPlannerPlan,
				BatchPlannerPolicyConstants.importPlanPolicyNameTypes)
		).user(
			_userLocalService.getUser(batchPlannerPlan.getUserId())
		).build();

		importTaskResource.setContextCompany(
			_companyLocalService.getCompany(batchPlannerPlan.getCompanyId()));

		File file = _getFile(batchPlannerPlan.getBatchPlannerPlanId());

		CreateStrategy createStrategy =
			CreateStrategy.getDefaultCreateStrategy();

		String value = _getValue(
			batchPlannerPlan.fetchBatchPlannerPolicy("createStrategy"));

		if (value != null) {
			createStrategy = CreateStrategy.valueOf(value);
		}

		try {
			if ((createStrategy == CreateStrategy.INSERT) ||
				(createStrategy == CreateStrategy.UPSERT)) {

				importTaskResource.postImportTask(
					batchPlannerPlan.getInternalClassName(), null, null, null,
					createStrategy.name(),
					String.valueOf(batchPlannerPlan.getBatchPlannerPlanId()),
					_getFieldNameMapping(
						_batchPlannerMappingLocalService.
							getBatchPlannerMappings(
								batchPlannerPlan.getBatchPlannerPlanId())),
					_getImportErrorStrategy(batchPlannerPlan),
					batchPlannerPlan.getTaskItemDelegateName(),
					MultipartBody.of(
						Collections.singletonMap(
							"file",
							new BinaryFile(
								BatchPlannerPlanConstants.getContentType(
									batchPlannerPlan.getExternalType()),
								file.getName(), new FileInputStream(file),
								file.length())),
						null, Collections.emptyMap()));

				return;
			}

			importTaskResource.putImportTask(
				batchPlannerPlan.getInternalClassName(), null,
				String.valueOf(batchPlannerPlan.getBatchPlannerPlanId()),
				_getImportErrorStrategy(batchPlannerPlan),
				batchPlannerPlan.getTaskItemDelegateName(), null,
				MultipartBody.of(
					Collections.singletonMap(
						"file",
						new BinaryFile(
							BatchPlannerPlanConstants.getContentType(
								batchPlannerPlan.getExternalType()),
							file.getName(), new FileInputStream(file),
							file.length())),
					null, Collections.emptyMap()));
		}
		finally {
			_file.delete(file);
		}
	}

	@Reference
	private BatchPlannerMappingLocalService _batchPlannerMappingLocalService;

	@Reference
	private BatchPlannerPlanLocalService _batchPlannerPlanLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ExportTaskResource _exportTaskResource;

	@Reference
	private ImportTaskResource.Factory _factory;

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private UserLocalService _userLocalService;

}