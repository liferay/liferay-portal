/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.request.filter;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.AnnotationLocator;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;

import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptorChain;

/**
 * @author Preston Crary
 */
@Provider
public class CTContainerRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		Message message = PhaseInterceptorChain.getCurrentMessage();

		InterceptorChain interceptorChain = message.getInterceptorChain();

		interceptorChain.add(_CT_PRE_INVOKE_PHASE_INTERCEPTOR);
		interceptorChain.add(_CT_POST_INVOKE_PHASE_INTERCEPTOR);
	}

	private static final String _CT_COLLECTION_SAFE_CLOSABLE =
		CTPreInvokePhaseInterceptor.class.getName() +
			"#CT_COLLECTION_SAFE_CLOSABLE";

	private static final CTPostInvokePhaseInterceptor
		_CT_POST_INVOKE_PHASE_INTERCEPTOR = new CTPostInvokePhaseInterceptor();

	private static final CTPreInvokePhaseInterceptor
		_CT_PRE_INVOKE_PHASE_INTERCEPTOR = new CTPreInvokePhaseInterceptor();

	private static class CTPostInvokePhaseInterceptor
		extends AbstractPhaseInterceptor<Message> {

		@Override
		public void handleMessage(Message message) {
			SafeCloseable safeCloseable = (SafeCloseable)message.get(
				_CT_COLLECTION_SAFE_CLOSABLE);

			if (safeCloseable != null) {
				safeCloseable.close();
			}
		}

		private CTPostInvokePhaseInterceptor() {
			super(Phase.POST_INVOKE);
		}

	}

	private static class CTPreInvokePhaseInterceptor
		extends AbstractPhaseInterceptor<Message> {

		@Override
		public void handleMessage(Message message) {
			Method method = (Method)message.get(
				"org.apache.cxf.resource.method");

			if ((method != null) &&
				!CTCollectionThreadLocal.isProductionMode()) {

				CTAware ctAware = AnnotationLocator.locate(
					method, method.getDeclaringClass(), CTAware.class);

				if ((ctAware == null) || ctAware.onProduction()) {
					message.put(
						_CT_COLLECTION_SAFE_CLOSABLE,
						CTCollectionThreadLocal.
							setProductionModeWithSafeCloseable());
				}
			}
		}

		private CTPreInvokePhaseInterceptor() {
			super(Phase.PRE_INVOKE);
		}

	}

}