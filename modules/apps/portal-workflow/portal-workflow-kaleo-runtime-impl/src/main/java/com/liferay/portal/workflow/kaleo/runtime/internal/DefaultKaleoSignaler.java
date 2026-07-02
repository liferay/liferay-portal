/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal;

import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.petra.concurrent.ThreadPoolHandlerAdapter;
import com.liferay.petra.executor.PortalExecutorConfig;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.NamedThreadFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.KaleoSignaler;
import com.liferay.portal.workflow.kaleo.runtime.constants.WorkflowInstanceDestinationNames;
import com.liferay.portal.workflow.kaleo.runtime.graph.GraphWalker;
import com.liferay.portal.workflow.kaleo.runtime.graph.PathElement;
import com.liferay.portal.workflow.kaleo.runtime.internal.node.util.NodeExecutorRegistryUtil;
import com.liferay.portal.workflow.kaleo.runtime.node.NodeExecutor;
import com.liferay.portal.workflow.kaleo.runtime.util.ExecutionContextHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Rafael Praxedes
 */
@Component(service = AopService.class)
@CTAware
@Transactional(
	isolation = Isolation.PORTAL, propagation = Propagation.SUPPORTS,
	rollbackFor = Exception.class
)
public class DefaultKaleoSignaler
	extends BaseKaleoBean implements AopService, KaleoSignaler {

	@Override
	public void signalEntry(
			String transitionName, ExecutionContext executionContext)
		throws PortalException {

		signalEntry(transitionName, executionContext, false);
	}

	@Override
	public void signalEntry(
			String transitionName, ExecutionContext executionContext,
			boolean waitForCompletion)
		throws PortalException {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		executionContext.setTransitionName(transitionName);

		_execute(
			new PathElement(
				null, kaleoInstanceToken.getCurrentKaleoNode(),
				executionContext),
			waitForCompletion);
	}

	@Override
	public void signalExecute(
			KaleoNode currentKaleoNode, ExecutionContext executionContext)
		throws PortalException {

		signalExecute(currentKaleoNode, executionContext, false);
	}

	@Override
	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRED,
		rollbackFor = Exception.class
	)
	public void signalExecute(
			KaleoNode currentKaleoNode, ExecutionContext executionContext,
			boolean waitForCompletion)
		throws PortalException {

		NodeExecutor nodeExecutor = NodeExecutorRegistryUtil.getNodeExecutor(
			currentKaleoNode.getType());

		List<PathElement> remainingPathElements = new ArrayList<>();

		nodeExecutor.execute(
			currentKaleoNode, executionContext, remainingPathElements);

		_executionContextHelper.checkKaleoInstanceComplete(executionContext);

		for (PathElement remainingPathElement : remainingPathElements) {
			_execute(remainingPathElement, waitForCompletion);
		}
	}

	@Override
	public void signalExit(
			String transitionName, ExecutionContext executionContext)
		throws PortalException {

		signalExit(transitionName, executionContext, false);
	}

	@Override
	public void signalExit(
			String transitionName, ExecutionContext executionContext,
			boolean waitForCompletion)
		throws PortalException {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		executionContext.setTransitionName(transitionName);

		_execute(
			new PathElement(
				kaleoInstanceToken.getCurrentKaleoNode(), null,
				executionContext),
			waitForCompletion);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_registerPortalExecutorConfig(bundleContext);

		_noticeableExecutorService = _portalExecutorManager.getPortalExecutor(
			DefaultKaleoSignaler.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_noticeableExecutorService.shutdown();

		_serviceRegistration.unregister();
	}

	private void _execute(PathElement pathElement, boolean waitForCompletion) {
		if (PortalRunMode.isTestMode() || waitForCompletion) {
			_walk(pathElement);

			return;
		}

		long ctCollectionId = CTCollectionThreadLocal.getCTCollectionId();

		_noticeableExecutorService.submit(
			new CompanyInheritableThreadLocalCallable<>(
				() -> {
					try (SafeCloseable safeCloseable =
							CTCollectionThreadLocal.
								setCTCollectionIdWithSafeCloseable(
									ctCollectionId)) {

						_walk(pathElement);
					}

					return null;
				}));
	}

	private void _registerPortalExecutorConfig(BundleContext bundleContext) {
		PortalExecutorConfig portalExecutorConfig = new PortalExecutorConfig(
			DefaultKaleoSignaler.class.getName(), 1, 1, 60, TimeUnit.SECONDS,
			Integer.MAX_VALUE,
			new NamedThreadFactory(
				DefaultKaleoSignaler.class.getName(), Thread.NORM_PRIORITY,
				PortalClassLoaderUtil.getClassLoader()),
			new ThreadPoolExecutor.AbortPolicy(),
			new ThreadPoolHandlerAdapter() {

				@Override
				public void afterExecute(
					Runnable runnable, Throwable throwable) {

					CentralizedThreadLocal.
						clearShortLivedCentralizedThreadLocals();
				}

			});

		_serviceRegistration = bundleContext.registerService(
			PortalExecutorConfig.class, portalExecutorConfig, null);
	}

	private void _walk(PathElement pathElement) {
		ExecutionContext executionContext = pathElement.getExecutionContext();
		String name = PrincipalThreadLocal.getName();
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			ServiceContext serviceContext =
				executionContext.getServiceContext();

			if (PrincipalThreadLocal.getUserId() == 0) {
				PrincipalThreadLocal.setName(serviceContext.getUserId());
			}

			if (permissionChecker == null) {
				PermissionThreadLocal.setPermissionChecker(
					_defaultPermissionCheckerFactory.create(
						_userLocalService.getUser(serviceContext.getUserId())));
			}

			Queue<List<PathElement>> queue = new LinkedList<>();

			queue.add(Collections.singletonList(pathElement));

			List<PathElement> pathElements = null;

			while ((pathElements = queue.poll()) != null) {
				for (PathElement curPathElement : pathElements) {
					List<PathElement> remainingPathElements = new ArrayList<>();

					_graphWalker.follow(
						curPathElement.getStartNode(),
						curPathElement.getTargetNode(), remainingPathElements,
						curPathElement.getExecutionContext());

					if (!remainingPathElements.isEmpty()) {
						queue.add(remainingPathElements);
					}
				}
			}
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);

			try {
				KaleoInstanceToken executionContextKaleoInstanceToken =
					executionContext.getKaleoInstanceToken();

				KaleoInstance kaleoInstance =
					executionContextKaleoInstanceToken.getKaleoInstance();

				for (KaleoInstanceToken kaleoInstanceToken :
						kaleoInstanceTokenLocalService.getKaleoInstanceTokens(
							kaleoInstance.getKaleoInstanceId())) {

					kaleoInstanceTokenLocalService.completeKaleoInstanceToken(
						kaleoInstanceToken.getKaleoInstanceTokenId());
				}

				kaleoInstanceLocalService.completeKaleoInstance(
					kaleoInstance.getKaleoInstanceId());

				kaleoLogLocalService.addInstanceFailKaleoLog(
					executionContextKaleoInstanceToken, throwable.getMessage(),
					executionContext.getServiceContext());

				Message message = new Message();

				message.put("companyId", kaleoInstance.getCompanyId());

				message.put("createDate", new Date());
				message.put("exception", throwable);
				message.put("userId", kaleoInstance.getUserId());
				message.put(
					"workflowInstanceId", kaleoInstance.getKaleoInstanceId());

				_messageBus.sendMessage(
					WorkflowInstanceDestinationNames.WORKFLOW_INSTANCE,
					message);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
		finally {
			PrincipalThreadLocal.setName(name);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultKaleoSignaler.class);

	@Reference
	private PermissionCheckerFactory _defaultPermissionCheckerFactory;

	@Reference
	private ExecutionContextHelper _executionContextHelper;

	@Reference
	private GraphWalker _graphWalker;

	@Reference
	private MessageBus _messageBus;

	private NoticeableExecutorService _noticeableExecutorService;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	private ServiceRegistration<PortalExecutorConfig> _serviceRegistration;

	@Reference
	private UserLocalService _userLocalService;

}