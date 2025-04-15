/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.helper;

import com.liferay.batch.planner.batch.engine.task.TaskItemUtil;
import com.liferay.batch.planner.constants.BatchPlannerPolicyConstants;
import com.liferay.batch.planner.model.BatchPlannerMapping;
import com.liferay.batch.planner.model.BatchPlannerPlan;
import com.liferay.batch.planner.model.BatchPlannerPolicy;
import com.liferay.batch.planner.service.BatchPlannerMappingLocalService;
import com.liferay.batch.planner.service.BatchPlannerMappingService;
import com.liferay.batch.planner.service.BatchPlannerPlanService;
import com.liferay.batch.planner.service.BatchPlannerPolicyLocalService;
import com.liferay.batch.planner.service.BatchPlannerPolicyService;
import com.liferay.batch.planner.service.persistence.BatchPlannerMappingUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.io.File;

import java.net.URI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
@Component(service = BatchPlannerPlanHelper.class)
public class BatchPlannerPlanHelper {

	public BatchPlannerPlan addExportBatchPlannerPlan(
			PortletRequest portletRequest, String name)
		throws Exception {

		String externalType = ParamUtil.getString(
			portletRequest, "externalType");
		boolean template = ParamUtil.getBoolean(portletRequest, "template");

		BatchPlannerPlan batchPlannerPlan =
			_batchPlannerPlanService.addBatchPlannerPlan(
				true, externalType, StringPool.SLASH,
				TaskItemUtil.getInternalClassName(
					ParamUtil.getString(
						portletRequest, "internalClassNameKey")),
				name, 0,
				TaskItemUtil.getTaskItemDelegateName(
					ParamUtil.getString(
						portletRequest, "internalClassNameKey")),
				template);

		_addBatchPlannerPolicies(
			batchPlannerPlan.getBatchPlannerPlanId(),
			BatchPlannerPolicyConstants.exportPlanPolicyNameTypes,
			portletRequest);

		List<BatchPlannerMapping> batchPlannerMappings =
			_getExportBatchPlannerMappings(portletRequest);

		for (BatchPlannerMapping batchPlannerMapping : batchPlannerMappings) {
			_batchPlannerMappingService.addBatchPlannerMapping(
				batchPlannerPlan.getBatchPlannerPlanId(),
				batchPlannerMapping.getExternalFieldName(), "String",
				batchPlannerMapping.getInternalFieldName(), "String",
				StringPool.BLANK);
		}

		return batchPlannerPlan;
	}

	public BatchPlannerPlan addImportBatchPlannerPlan(
			PortletRequest portletRequest, String name, String importFileURI)
		throws Exception {

		String externalType = ParamUtil.getString(
			portletRequest, "externalType", "CSV");
		String internalClassName = TaskItemUtil.getInternalClassName(
			ParamUtil.getString(portletRequest, "internalClassNameKey"));
		String taskItemDelegateName = TaskItemUtil.getTaskItemDelegateName(
			ParamUtil.getString(portletRequest, "internalClassNameKey"));
		boolean template = ParamUtil.getBoolean(portletRequest, "template");

		int size = 0;

		if (!template) {
			File file = new File(new URI(importFileURI));

			size = (int)file.length();
		}

		BatchPlannerPlan batchPlannerPlan =
			_batchPlannerPlanService.addBatchPlannerPlan(
				false, externalType, importFileURI, internalClassName, name,
				size, taskItemDelegateName, template);

		_addBatchPlannerPolicies(
			batchPlannerPlan.getBatchPlannerPlanId(),
			BatchPlannerPolicyConstants.importPlanPolicyNameTypes,
			portletRequest);

		List<BatchPlannerMapping> batchPlannerMappings =
			_getImportBatchPlannerMappings(portletRequest);

		for (BatchPlannerMapping batchPlannerMapping : batchPlannerMappings) {
			_batchPlannerMappingService.addBatchPlannerMapping(
				batchPlannerPlan.getBatchPlannerPlanId(),
				batchPlannerMapping.getExternalFieldName(), "String",
				batchPlannerMapping.getInternalFieldName(), "String",
				StringPool.BLANK);
		}

		return batchPlannerPlan;
	}

	public BatchPlannerPlan copyBatchPlannerPlan(
			long userId, long batchPlannerPlanId, String externalURL,
			String name)
		throws Exception {

		User user = _userLocalService.fetchUser(userId);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		if (!externalURL.startsWith("file://")) {
			externalURL = "file://" + externalURL;
		}

		try {
			return _copyBatchPlannerPlan(batchPlannerPlanId, externalURL, name);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	public BatchPlannerPlan updateExportBatchPlannerPlan(
			PortletRequest portletRequest)
		throws PortalException {

		return _updateBatchPlannerPlan(
			portletRequest, _getExportBatchPlannerMappings(portletRequest),
			BatchPlannerPolicyConstants.exportPlanPolicyNameTypes);
	}

	public BatchPlannerPlan updateImportBatchPlannerPlan(
			PortletRequest portletRequest)
		throws PortalException {

		return _updateBatchPlannerPlan(
			portletRequest, _getImportBatchPlannerMappings(portletRequest),
			BatchPlannerPolicyConstants.importPlanPolicyNameTypes);
	}

	private void _addBatchPlannerPolicies(
			long batchPlannerPlanId, Map<String, String> planPolicyNameTypes,
			PortletRequest portletRequest)
		throws Exception {

		for (Map.Entry<String, String> entry : planPolicyNameTypes.entrySet()) {
			String name = entry.getKey();

			String value = ParamUtil.getString(portletRequest, name);

			if (Objects.equals(entry.getValue(), "checkbox")) {
				value = _getCheckboxValue(portletRequest, name);
			}

			if (Validator.isNull(value)) {
				continue;
			}

			_batchPlannerPolicyService.addBatchPlannerPolicy(
				batchPlannerPlanId, name, value);
		}
	}

	private BatchPlannerPlan _copyBatchPlannerPlan(
			long batchPlannerPlanId, String externalURL, String name)
		throws Exception {

		BatchPlannerPlan templateBatchPlannerPlan =
			_batchPlannerPlanService.fetchBatchPlannerPlan(batchPlannerPlanId);

		if ((templateBatchPlannerPlan == null) ||
			!templateBatchPlannerPlan.isTemplate()) {

			throw new IllegalArgumentException(
				"Bbatch planner plan " + batchPlannerPlanId +
					" is not a template");
		}

		File file = new File(new URI(externalURL));

		BatchPlannerPlan batchPlannerPlan =
			_batchPlannerPlanService.addBatchPlannerPlan(
				templateBatchPlannerPlan.isExport(),
				templateBatchPlannerPlan.getExternalType(), externalURL,
				templateBatchPlannerPlan.getInternalClassName(), name,
				(int)file.length(),
				templateBatchPlannerPlan.getTaskItemDelegateName(), false);

		List<BatchPlannerMapping> batchPlannerMappings =
			_batchPlannerMappingService.getBatchPlannerMappings(
				templateBatchPlannerPlan.getBatchPlannerPlanId());

		for (BatchPlannerMapping batchPlannerMapping : batchPlannerMappings) {
			_batchPlannerMappingLocalService.addBatchPlannerMapping(
				batchPlannerPlan.getUserId(),
				batchPlannerPlan.getBatchPlannerPlanId(),
				batchPlannerMapping.getExternalFieldName(),
				batchPlannerMapping.getExternalFieldType(),
				batchPlannerMapping.getInternalFieldName(),
				batchPlannerMapping.getInternalFieldType(),
				batchPlannerMapping.getScript());
		}

		List<BatchPlannerPolicy> batchPlannerPolicies =
			_batchPlannerPolicyService.getBatchPlannerPolicies(
				templateBatchPlannerPlan.getBatchPlannerPlanId());

		for (BatchPlannerPolicy batchPlannerPolicy : batchPlannerPolicies) {
			_batchPlannerPolicyLocalService.addBatchPlannerPolicy(
				batchPlannerPlan.getUserId(),
				batchPlannerPlan.getBatchPlannerPlanId(),
				batchPlannerPolicy.getName(), batchPlannerPolicy.getValue());
		}

		return batchPlannerPlan;
	}

	private String _getCheckboxValue(
		PortletRequest portletRequest, String name) {

		String value = portletRequest.getParameter(name);

		if (value == null) {
			return Boolean.FALSE.toString();
		}

		return Boolean.TRUE.toString();
	}

	private List<BatchPlannerMapping> _getExportBatchPlannerMappings(
		PortletRequest portletRequest) {

		String[] fieldNames = ParamUtil.getStringValues(
			portletRequest, "fieldName");

		if (fieldNames == null) {
			return Collections.emptyList();
		}

		List<BatchPlannerMapping> batchPlannerMappings = new ArrayList<>();

		for (String fieldName : fieldNames) {
			BatchPlannerMapping batchPlannerMapping =
				BatchPlannerMappingUtil.create(0);

			batchPlannerMapping.setExternalFieldName(fieldName);
			batchPlannerMapping.setInternalFieldName(fieldName);

			batchPlannerMappings.add(batchPlannerMapping);
		}

		return batchPlannerMappings;
	}

	private List<BatchPlannerMapping> _getImportBatchPlannerMappings(
		PortletRequest portletRequest) {

		List<BatchPlannerMapping> batchPlannerMappings = new ArrayList<>();

		Enumeration<String> enumeration = portletRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String parameterName = enumeration.nextElement();

			if (!parameterName.startsWith("externalFieldName_") ||
				Validator.isNull(
					ParamUtil.getString(portletRequest, parameterName))) {

				continue;
			}

			String suffix = parameterName.substring(
				"externalFieldName_".length());

			String internalFieldName = ParamUtil.getString(
				portletRequest, "internalFieldName_" + suffix);

			if (Validator.isNull(internalFieldName) &&
				parameterName.contains("properties_") &&
				Validator.isNotNull(
					ParamUtil.getString(
						portletRequest,
						"internalFieldName_properties_" + suffix))) {

				internalFieldName = suffix;
			}

			BatchPlannerMapping batchPlannerMapping =
				BatchPlannerMappingUtil.create(0);

			batchPlannerMapping.setExternalFieldName(
				ParamUtil.getString(portletRequest, parameterName));
			batchPlannerMapping.setInternalFieldName(internalFieldName);

			batchPlannerMappings.add(batchPlannerMapping);
		}

		return batchPlannerMappings;
	}

	private BatchPlannerPlan _updateBatchPlannerPlan(
			PortletRequest portletRequest,
			List<BatchPlannerMapping> batchPlannerMappings,
			Map<String, String> planPolicyNameTypes)
		throws PortalException {

		long batchPlannerPlanId = ParamUtil.getLong(
			portletRequest, "batchPlannerPlanId");

		String externalType = ParamUtil.getString(
			portletRequest, "externalType");
		String internalClassName = ParamUtil.getString(
			portletRequest, "internalClassNameKey");
		String name = ParamUtil.getString(portletRequest, "name");

		BatchPlannerPlan batchPlannerPlan =
			_batchPlannerPlanService.updateBatchPlannerPlan(
				batchPlannerPlanId, externalType, internalClassName, name);

		_updateBatchPlannerPolicies(
			batchPlannerPlanId, planPolicyNameTypes, portletRequest);

		_batchPlannerMappingService.deleteBatchPlannerMappings(
			batchPlannerPlanId);

		for (BatchPlannerMapping batchPlannerMapping : batchPlannerMappings) {
			_batchPlannerMappingService.addBatchPlannerMapping(
				batchPlannerPlanId, batchPlannerMapping.getExternalFieldName(),
				"String", batchPlannerMapping.getInternalFieldName(), "String",
				StringPool.BLANK);
		}

		return batchPlannerPlan;
	}

	private void _updateBatchPlannerPolicies(
			long batchPlannerPlanId, Map<String, String> planPolicyNameTypes,
			PortletRequest portletRequest)
		throws PortalException {

		for (Map.Entry<String, String> entry : planPolicyNameTypes.entrySet()) {
			String name = entry.getKey();

			String value = ParamUtil.getString(portletRequest, name);

			if (Objects.equals(entry.getValue(), "checkbox")) {
				value = _getCheckboxValue(portletRequest, name);
			}

			BatchPlannerPolicy batchPlannerPolicy =
				_batchPlannerPolicyLocalService.fetchBatchPlannerPolicy(
					batchPlannerPlanId, name);

			if (Validator.isNull(value) && (batchPlannerPolicy != null)) {
				_batchPlannerPolicyService.deleteBatchPlannerPolicy(
					batchPlannerPlanId, name);

				continue;
			}

			if (batchPlannerPolicy != null) {
				_batchPlannerPolicyService.updateBatchPlannerPolicy(
					batchPlannerPlanId, name, value);

				continue;
			}

			_batchPlannerPolicyService.addBatchPlannerPolicy(
				batchPlannerPlanId, name, value);
		}
	}

	@Reference
	private BatchPlannerMappingLocalService _batchPlannerMappingLocalService;

	@Reference
	private BatchPlannerMappingService _batchPlannerMappingService;

	@Reference
	private BatchPlannerPlanService _batchPlannerPlanService;

	@Reference
	private BatchPlannerPolicyLocalService _batchPlannerPolicyLocalService;

	@Reference
	private BatchPlannerPolicyService _batchPlannerPolicyService;

	@Reference
	private UserLocalService _userLocalService;

}