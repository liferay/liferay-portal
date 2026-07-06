/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.workflow;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
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
import com.liferay.portal.kernel.util.LocaleUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Bruno Farache
 * @author Marcellus Tavares
 */
public class WorkflowHandlerRegistryUtil {

	public static List<WorkflowHandler<?>> getScopeableWorkflowHandlers() {
		return _getWorkflowHandlers(_scopeableWorkflowHandlerServiceTrackerMap);
	}

	public static <T> WorkflowHandler<T> getWorkflowHandler(String className) {
		return (WorkflowHandler<T>)_workflowHandlerServiceTrackerMap.getService(
			className);
	}

	public static List<WorkflowHandler<?>> getWorkflowHandlers() {
		return _getWorkflowHandlers(_workflowHandlerServiceTrackerMap);
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

	private static List<WorkflowHandler<?>> _getWorkflowHandlers(
		ServiceTrackerMap<String, WorkflowHandler<?>>
			workflowHandlerServiceTrackerMap) {

		List<WorkflowHandler<?>> workflowHandlers = new ArrayList<>();

		for (String modelClassName :
				workflowHandlerServiceTrackerMap.keySet()) {

			workflowHandlers.add(
				workflowHandlerServiceTrackerMap.getService(modelClassName));
		}

		return workflowHandlers;
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
		_scopeableWorkflowHandlerServiceTrackerMap;
	private static final ServiceTrackerMap<String, WorkflowHandler<?>>
		_workflowHandlerServiceTrackerMap;

	private static class WorkflowHandlerServiceReferenceMapper
		implements ServiceReferenceMapper<String, WorkflowHandler<?>> {

		@Override
		public void map(
			ServiceReference<WorkflowHandler<?>> serviceReference,
			Emitter<String> emitter) {

			WorkflowHandler<?> workflowHandler = _bundleContext.getService(
				serviceReference);

			if (_predicate.test(workflowHandler)) {
				emitter.emit(workflowHandler.getClassName());
			}
		}

		private WorkflowHandlerServiceReferenceMapper(
			Predicate<WorkflowHandler<?>> predicate) {

			_predicate = predicate;
		}

		private final Predicate<WorkflowHandler<?>> _predicate;

	}

	static {
		_workflowHandlerServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext,
				(Class<WorkflowHandler<?>>)(Class<?>)WorkflowHandler.class,
				null,
				new WorkflowHandlerServiceReferenceMapper(handler -> true));

		_scopeableWorkflowHandlerServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext,
				(Class<WorkflowHandler<?>>)(Class<?>)WorkflowHandler.class,
				null,
				new WorkflowHandlerServiceReferenceMapper(
					handler -> handler.isScopeable()));
	}

}