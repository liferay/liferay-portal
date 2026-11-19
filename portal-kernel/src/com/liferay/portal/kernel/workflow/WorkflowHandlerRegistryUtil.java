/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.workflow;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalServiceUtil;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

/**
 * @author Bruno Farache
 * @author Marcellus Tavares
 */
public class WorkflowHandlerRegistryUtil {

	public static List<WorkflowHandler<?>> getScopeableWorkflowHandlers() {
		return ListUtil.filter(
			getWorkflowHandlers(), WorkflowHandler::isScopeable);
	}

	public static <T> WorkflowHandler<T> getWorkflowHandler(String className) {
		return (WorkflowHandler<T>)_workflowHandlerServiceTrackerMap.getService(
			className);
	}

	public static List<WorkflowHandler<?>> getWorkflowHandlers() {
		return TransformUtil.transform(
			_workflowHandlerServiceTrackerMap.keySet(),
			_workflowHandlerServiceTrackerMap::getService);
	}

	public static <T> void startWorkflowInstance(
			long companyId, long groupId, long userId, String className,
			long classPK, T model, ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext =
			(Map<String, Serializable>)serviceContext.removeAttribute(
				"workflowContext");

		if (workflowContext == null) {
			workflowContext = Collections.emptyMap();
		}

		startWorkflowInstance(
			companyId, groupId, userId, className, classPK, model,
			serviceContext, workflowContext);
	}

	public static <T> T startWorkflowInstance(
			long companyId, long groupId, long userId, String className,
			long classPK, T model, ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		if (serviceContext.getWorkflowAction() !=
				WorkflowConstants.ACTION_PUBLISH) {

			return model;
		}

		WorkflowHandler<T> workflowHandler = getWorkflowHandler(className);

		if (workflowHandler == null) {
			if (WorkflowThreadLocal.isEnabled()) {
				throw new WorkflowException(
					"No workflow handler found for " + className);
			}

			return model;
		}

		boolean hasWorkflowInstanceInProgress = _hasWorkflowInstanceInProgress(
			companyId, groupId, className, classPK);

		if (hasWorkflowInstanceInProgress) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Workflow already started for class ", className,
						" with primary key ", classPK, " in group ", groupId));
			}

			return model;
		}

		WorkflowDefinitionLink workflowDefinitionLink = null;

		if (WorkflowThreadLocal.isEnabled()) {
			workflowDefinitionLink = workflowHandler.getWorkflowDefinitionLink(
				companyId, groupId, classPK);
		}

		int status = WorkflowConstants.STATUS_PENDING;

		if (workflowDefinitionLink == null) {
			status = WorkflowConstants.STATUS_APPROVED;
		}

		workflowContext = HashMapBuilder.create(
			workflowContext
		).<String, Serializable>put(
			WorkflowConstants.CONTEXT_COMPANY_ID, String.valueOf(companyId)
		).put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME, className
		).put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_PK, String.valueOf(classPK)
		).put(
			WorkflowConstants.CONTEXT_ENTRY_TYPE,
			workflowHandler.getType(LocaleUtil.getDefault())
		).put(
			WorkflowConstants.CONTEXT_GROUP_ID, String.valueOf(groupId)
		).put(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT, serviceContext
		).put(
			WorkflowConstants.CONTEXT_TASK_COMMENTS,
			GetterUtil.getString(serviceContext.getAttribute("comments"))
		).put(
			WorkflowConstants.CONTEXT_USER_ID, String.valueOf(userId)
		).build();

		workflowHandler.contributeWorkflowContext(workflowContext);

		T updatedModel = workflowHandler.updateStatus(
			model, status, workflowContext);

		if (workflowDefinitionLink != null) {
			Map<String, Serializable> tempWorkflowContext = workflowContext;

			TransactionCallbackUtil.registerCommitCallback(
				() -> {
					if (!_hasWorkflowInstanceInProgress(
							companyId, groupId, className, classPK)) {

						workflowHandler.startWorkflowInstance(
							companyId, groupId, userId, classPK, model,
							tempWorkflowContext);
					}

					return null;
				});
		}

		return updatedModel;
	}

	public static <T> void startWorkflowInstance(
			long companyId, long userId, String className, long classPK,
			T model, ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext =
			(Map<String, Serializable>)serviceContext.removeAttribute(
				"workflowContext");

		if (workflowContext == null) {
			workflowContext = Collections.emptyMap();
		}

		startWorkflowInstance(
			companyId, WorkflowConstants.DEFAULT_GROUP_ID, userId, className,
			classPK, model, serviceContext, workflowContext);
	}

	public static <T> void startWorkflowInstance(
			long companyId, long userId, String className, long classPK,
			T model, ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		startWorkflowInstance(
			companyId, WorkflowConstants.DEFAULT_GROUP_ID, userId, className,
			classPK, model, serviceContext, workflowContext);
	}

	public static <T> T updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		String className = (String)workflowContext.get(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME);

		if (className == null) {
			return null;
		}

		WorkflowHandler<T> workflowHandler = getWorkflowHandler(className);

		if (workflowHandler == null) {
			return null;
		}

		return workflowHandler.updateStatus(status, workflowContext);
	}

	private static boolean _hasWorkflowInstanceInProgress(
			long companyId, long groupId, String className, long classPK)
		throws PortalException {

		WorkflowInstanceLink workflowInstanceLink =
			WorkflowInstanceLinkLocalServiceUtil.fetchWorkflowInstanceLink(
				companyId, groupId, className, classPK);

		if (workflowInstanceLink == null) {
			return false;
		}

		WorkflowInstance workflowInstance =
			WorkflowInstanceManagerUtil.getWorkflowInstance(
				companyId, workflowInstanceLink.getWorkflowInstanceId());

		return !workflowInstance.isComplete();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowHandlerRegistryUtil.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerMap<String, WorkflowHandler<?>>
		_workflowHandlerServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext,
				(Class<WorkflowHandler<?>>)(Class<?>)WorkflowHandler.class,
				null,
				(serviceReference, emitter) -> {
					Object modelClassName = serviceReference.getProperty(
						"model.class.name");

					if (modelClassName != null) {
						emitter.emit((String)modelClassName);

						return;
					}

					WorkflowHandler<?> workflowHandler =
						_bundleContext.getService(serviceReference);

					emitter.emit(workflowHandler.getClassName());

					_bundleContext.ungetService(serviceReference);
				});

}