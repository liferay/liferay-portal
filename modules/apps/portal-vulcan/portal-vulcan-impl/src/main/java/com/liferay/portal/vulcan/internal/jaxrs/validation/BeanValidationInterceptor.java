/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.validation;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.ContextProviderUtil;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

import java.lang.reflect.Method;

import java.util.List;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.validation.AbstractValidationInterceptor;

/**
 * @author Javier Gamarra
 */
@Provider
public class BeanValidationInterceptor
	extends AbstractValidationInterceptor implements ContainerRequestFilter {

	public BeanValidationInterceptor() {
		super("pre-invoke");
	}

	public BeanValidationInterceptor(String phase) {
		super(phase);
	}

	@Override
	public void filter(ContainerRequestContext containerRequestContext)
		throws IOException {

		Message message = PhaseInterceptorChain.getCurrentMessage();

		InterceptorChain interceptorChain = message.getInterceptorChain();

		interceptorChain.add(this);
	}

	public void handleMessage(Message message) throws Fault {
		Object serviceObject = getServiceObject(message);

		if (serviceObject == null) {
			return;
		}

		Method method = getServiceMethod(message);

		if (method == null) {
			return;
		}

		List<Object> arguments = MessageContentsList.getContentsList(message);

		handleValidation(message, serviceObject, method, arguments);
	}

	@Override
	protected Object getServiceObject(Message message) {
		return ContextProviderUtil.getMatchedResource(message);
	}

	@Override
	protected void handleValidation(
		Message message, Object resource, Method method,
		List<Object> arguments) {

		if (ListUtil.isEmpty(arguments)) {
			return;
		}

		ValidationUtil.validateArguments(resource, method, arguments.toArray());
	}

}