/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.BaseAlloyControllerImpl;
import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.web.internal.permission.resource.PatcherPermission;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import java.lang.reflect.Method;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Zsolt Balogh
 */
public class PatcherAlloyControllerImpl extends BaseAlloyControllerImpl {

	@Override
	public void updateModel(BaseModel<?> baseModel, Object... properties)
		throws Exception {

		setWorkflowedModel(baseModel, properties);

		super.updateModel(baseModel, properties);
	}

	@Override
	public void updateModelIgnoreRequest(
			BaseModel<?> baseModel, Object... properties)
		throws Exception {

		setWorkflowedModel(baseModel, false, properties);

		long classPK = GetterUtil.getLong(baseModel.getPrimaryKeyObj());

		BaseModel<?> oldBaseModel = BaseModelUtil.fetchBaseModel(
			clazz, baseModel.getModelClassName(), classPK);

		super.updateModelIgnoreRequest(baseModel, properties);

		if (baseModel instanceof PatcherBuild) {
			PatcherBuildUtil.reindexRelatedModels(
				this, (PatcherBuild)baseModel);
		}

		if (baseModel instanceof AuditedModel) {
			AuditedModel auditedModel = (AuditedModel)baseModel;

			User user = UserLocalServiceUtil.getUser(auditedModel.getUserId());

			if (changedStatus(oldBaseModel, baseModel)) {
				if (baseModel instanceof PatcherFix) {
					PatcherFix patcherFix = (PatcherFix)baseModel;

					if (PatcherFixUtil.isMainPatcherFix(classPK) ||
						((patcherFix.getType() ==
							PatcherFixConstants.TYPE_REBASE) &&
						 ((patcherFix.getStatus() ==
							 WorkflowConstants.STATUS_FIX_COMPLETE) ||
						  (patcherFix.getStatus() ==
							  WorkflowConstants.STATUS_FIX_FAILED)))) {

						return;
					}
				}

				EmailUtil.sendPatcherStatusEmail(
					baseModel, user.getEmailAddress(), getThemeDisplay());
			}

			if ((baseModel instanceof PatcherBuild) &&
				changedQaStatus(
					(PatcherBuild)oldBaseModel, (PatcherBuild)baseModel)) {

				PatcherBuild patcherBuild = (PatcherBuild)baseModel;

				if ((patcherBuild.getQaStatus() ==
						WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_STARTED) ||
					(patcherBuild.getQaStatus() ==
						WorkflowConstants.
							STATUS_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY) ||
					(patcherBuild.getQaStatus() ==
						WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED) ||
					(patcherBuild.getQaStatus() ==
						WorkflowConstants.
							STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY) ||
					(patcherBuild.getQaStatus() ==
						WorkflowConstants.STATUS_BUILD_QA_PENDING_SMOKE_ONLY) ||
					(patcherBuild.getQaStatus() ==
						WorkflowConstants.STATUS_BUILD_QA_TESTING_SKIPPED) ||
					(patcherBuild.getQaStatus() ==
						WorkflowConstants.
							STATUS_BUILD_QA_TESTING_SKIPPED_SMOKE_ONLY) ||
					(patcherBuild.getQaStatus() ==
						WorkflowConstants.STATUS_PENDING)) {

					return;
				}

				EmailUtil.sendPatcherEmail(
					baseModel, user.getEmailAddress(),
					WorkflowConstants.getStatusLabel(
						patcherBuild.getQaStatus()),
					getThemeDisplay());
			}
		}
	}

	protected boolean changedQaStatus(
			PatcherBuild oldPatcherBuild, PatcherBuild newPatcherBuild)
		throws Exception {

		if (oldPatcherBuild == null) {
			return false;
		}

		if (oldPatcherBuild.getQaStatus() != newPatcherBuild.getQaStatus()) {
			return true;
		}

		return false;
	}

	protected boolean changedStatus(
			BaseModel<?> oldBaseModel, BaseModel<?> newBaseModel)
		throws Exception {

		if (oldBaseModel == null) {
			return false;
		}

		Integer oldBaseModelStatus = BaseModelUtil.fetchBaseModelStatus(
			oldBaseModel);
		Integer newBaseModelStatus = BaseModelUtil.fetchBaseModelStatus(
			newBaseModel);

		if ((oldBaseModelStatus != null) && (newBaseModelStatus != null) &&
			!oldBaseModelStatus.equals(newBaseModelStatus)) {

			return true;
		}

		return false;
	}

	@Override
	protected void executeResource(Method method) throws Exception {
		Class<?> tempClass = clazz;

		clazz = clazz.getSuperclass();

		super.executeResource(method);

		clazz = tempClass;
	}

	@Override
	protected boolean hasPermission() {
		if (!permissioned) {
			return true;
		}

		long id = ParamUtil.getLong(request, "id");

		try {
			if (id > 0) {
				if (alloyServiceInvoker == null) {
					throw new Exception(
						"The alloy service invoker must be set");
				}

				BaseModel<?> baseModel = alloyServiceInvoker.fetchModel(id);

				if (baseModel != null) {
					return PatcherPermission.contains(
						themeDisplay, baseModel, actionPath);
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		return PatcherPermission.contains(
			themeDisplay, controllerPath, actionPath);
	}

	protected void setParameters(Object... attributes) throws Exception {
		if ((attributes.length != 0) && ((attributes.length % 2) != 0)) {
			throw new Exception("Arguments length is not an even number");
		}

		DynamicServletRequest dynamicServletRequest = null;

		if (request instanceof DynamicServletRequest) {
			dynamicServletRequest = (DynamicServletRequest)request;
		}
		else {
			dynamicServletRequest = new DynamicServletRequest(request);
		}

		for (int i = 0; i < attributes.length; i += 2) {
			String name = String.valueOf(attributes[i]);

			String value = String.valueOf(attributes[i + 1]);

			dynamicServletRequest.setParameter(name, value);
		}

		request = dynamicServletRequest;
	}

	protected void setWorkflowedModel(
			BaseModel<?> baseModel, boolean useRequestUser,
			Object... properties)
		throws Exception {

		if (!(baseModel instanceof WorkflowedModel)) {
			return;
		}

		long classPK = GetterUtil.getLong(baseModel.getPrimaryKeyObj());

		BaseModel<?> oldBaseModel = BaseModelUtil.fetchBaseModel(
			clazz, baseModel.getModelClassName(), classPK);

		Map<String, Object> propertiesMap = PatcherUtil.getPropertiesMap(
			properties);

		if (propertiesMap.containsKey("status")) {
			int baseModelStatus = (Integer)propertiesMap.get("status");

			BaseModelUtil.setBaseModelStatus(baseModel, baseModelStatus);
		}

		if (!baseModel.isNew() && !changedStatus(oldBaseModel, baseModel)) {
			return;
		}

		WorkflowedModel workflowedModel = (WorkflowedModel)baseModel;

		User modifiedUser = user;

		if (!useRequestUser) {
			User currentUser = null;

			if (baseModel instanceof PatcherBuild) {
				PatcherBuild patcherBuild = (PatcherBuild)baseModel;

				currentUser = UserLocalServiceUtil.fetchUser(
					patcherBuild.getStatusByUserId());
			}
			else if (baseModel instanceof PatcherFix) {
				PatcherFix patcherFix = (PatcherFix)baseModel;

				currentUser = UserLocalServiceUtil.fetchUser(
					patcherFix.getStatusByUserId());
			}

			if (currentUser != null) {
				modifiedUser = currentUser;
			}
		}

		workflowedModel.setStatusByUserId(modifiedUser.getUserId());
		workflowedModel.setStatusByUserName(modifiedUser.getFullName());
		workflowedModel.setStatusDate(new Date());
	}

	protected void setWorkflowedModel(
			BaseModel<?> baseModel, Object... properties)
		throws Exception {

		setWorkflowedModel(baseModel, true, properties);
	}

	protected void validateRequestGetMethod() throws Exception {
		String requestMethod = request.getMethod();

		if (!_isValidRequestMethod(requestMethod, false, HttpMethods.GET)) {
			throw new Exception(
				translate(
					"the-request-method-x-is-not-supported-by-x", requestMethod,
					actionPath));
		}
	}

	protected void validateRequestPostMethod() throws Exception {
		String requestMethod = request.getMethod();

		if (!_isValidRequestMethod(requestMethod, false, HttpMethods.POST)) {
			throw new Exception(
				translate(
					"the-request-method-x-is-not-supported-by-x", requestMethod,
					actionPath));
		}
	}

	private boolean _isValidRequestMethod(
			String requestMethod, boolean exclude, String... requestMethods)
		throws Exception {

		List<String> methods = ListUtil.fromArray(requestMethods);

		if (exclude) {
			if (!methods.contains(requestMethod)) {
				return true;
			}
		}
		else {
			if (methods.contains(requestMethod)) {
				return true;
			}
		}

		return false;
	}

}