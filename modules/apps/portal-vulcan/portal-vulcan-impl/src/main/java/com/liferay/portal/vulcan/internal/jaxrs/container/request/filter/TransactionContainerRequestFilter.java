/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.request.filter;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.spring.transaction.TransactionAttributeAdapter;
import com.liferay.portal.spring.transaction.TransactionAttributeBuilder;
import com.liferay.portal.spring.transaction.TransactionExecutor;
import com.liferay.portal.spring.transaction.TransactionStatusAdapter;
import com.liferay.portal.vulcan.internal.constants.VulcanConstants;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.MessageObserver;

/**
 * @author Javier Gamarra
 */
@Provider
@Transactional(rollbackFor = Exception.class)
public class TransactionContainerRequestFilter
	implements ContainerRequestFilter, ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext)
		throws IOException {

		if (GetterUtil.getBoolean(
				containerRequestContext.getHeaderString(
					"X-Liferay-Transaction-Disabled"))) {

			if (_log.isDebugEnabled()) {
				_log.debug("Transaction management is disabled");
			}

			return;
		}

		if (_transactionRequiredMethodNames.contains(
				containerRequestContext.getMethod())) {

			Message message = PhaseInterceptorChain.getCurrentMessage();

			InterceptorChain interceptorChain = message.getInterceptorChain();

			TransactionCleanUpMessageObserver
				transactionCleanUpMessageObserver =
					new TransactionCleanUpMessageObserver(
						interceptorChain.getFaultObserver(),
						_transactionExecutor.start(
							_transactionAttributeAdapter));

			containerRequestContext.setProperty(
				VulcanConstants.TRANSACTION_CLEAN_UP_MESSAGE_OBSERVER,
				transactionCleanUpMessageObserver);

			interceptorChain.add(transactionCleanUpMessageObserver);
			interceptorChain.setFaultObserver(
				transactionCleanUpMessageObserver);
		}
	}

	@Override
	public void filter(
			ContainerRequestContext containerRequestContext,
			ContainerResponseContext containerResponseContext)
		throws IOException {

		TransactionCleanUpMessageObserver transactionCleanUpMessageObserver =
			(TransactionCleanUpMessageObserver)
				containerRequestContext.getProperty(
					VulcanConstants.TRANSACTION_CLEAN_UP_MESSAGE_OBSERVER);

		if (transactionCleanUpMessageObserver == null) {
			return;
		}

		Response.Status.Family family = Response.Status.Family.familyOf(
			containerResponseContext.getStatus());

		if (family == Response.Status.Family.SUCCESSFUL) {
			transactionCleanUpMessageObserver.commit();
		}
		else {
			transactionCleanUpMessageObserver.rollback(
				StringBundler.concat(
					"Rollback due to ", family, ": ",
					containerResponseContext.getStatus()));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TransactionContainerRequestFilter.class);

	private static final TransactionAttributeAdapter
		_transactionAttributeAdapter = new TransactionAttributeAdapter(
			TransactionAttributeBuilder.build(
				TransactionContainerRequestFilter.class.getAnnotation(
					Transactional.class)));
	private static final TransactionExecutor _transactionExecutor =
		(TransactionExecutor)PortalBeanLocatorUtil.locate(
			"transactionExecutor");
	private static final Set<String> _transactionRequiredMethodNames =
		new HashSet<>(Arrays.asList("DELETE", "PATCH", "POST", "PUT"));

	private static class TransactionCleanUpMessageObserver
		extends AbstractPhaseInterceptor implements MessageObserver {

		public void commit() {
			try {
				_transactionExecutor.commit(
					_transactionAttributeAdapter, _transactionStatusAdapter);
			}
			finally {
				_complete = true;
			}
		}

		@Override
		public void handleFault(Message message) {
			if (!_complete) {
				rollback("Rollback due to uncaught exception");
			}
		}

		@Override
		public void handleMessage(Message message) {
			if (!_complete) {
				rollback("Rollback due to uncaught exception");
			}
		}

		@Override
		public void onMessage(Message message) {
			if (!_complete) {
				rollback("Rollback due to uncaught exception");
			}

			_messageObserver.onMessage(message);
		}

		public void rollback(String message) {
			Exception exception = new Exception(message);

			try {
				_transactionExecutor.rollback(
					exception, _transactionAttributeAdapter,
					_transactionStatusAdapter);
			}
			catch (Throwable throwable) {
				if (throwable != exception) {
					_log.error(
						"Unable to roll back the transaction", throwable);
				}
			}
			finally {
				_complete = true;
			}
		}

		private TransactionCleanUpMessageObserver(
			MessageObserver messageObserver,
			TransactionStatusAdapter transactionStatusAdapter) {

			super(Phase.POST_INVOKE);

			_messageObserver = messageObserver;
			_transactionStatusAdapter = transactionStatusAdapter;
		}

		private boolean _complete;
		private final MessageObserver _messageObserver;
		private final TransactionStatusAdapter _transactionStatusAdapter;

	}

}