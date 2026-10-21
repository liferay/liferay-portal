/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.request.filter;

import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.feature.flag.FeatureFlagChecker;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.ContextProviderUtil;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;

/**
 * @author Carlos Correa
 */
@Provider
public class FeatureFlagContainerRequestFilter
	implements ContainerRequestFilter {

	public FeatureFlagContainerRequestFilter(
		FeatureFlagChecker featureFlagChecker, Portal portal) {

		_featureFlagChecker = featureFlagChecker;
		_portal = portal;
	}

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		Message message = PhaseInterceptorChain.getCurrentMessage();

		Exchange exchange = message.getExchange();

		OperationResourceInfo operationResourceInfo = exchange.get(
			OperationResourceInfo.class);

		if (operationResourceInfo == null) {
			return;
		}

		if (!_featureFlagChecker.isEnabled(
				_portal.getCompanyId(
					ContextProviderUtil.getHttpServletRequest(message)),
				operationResourceInfo.getAnnotatedMethod())) {

			throw new NotFoundException();
		}
	}

	private final FeatureFlagChecker _featureFlagChecker;
	private final Portal _portal;

}