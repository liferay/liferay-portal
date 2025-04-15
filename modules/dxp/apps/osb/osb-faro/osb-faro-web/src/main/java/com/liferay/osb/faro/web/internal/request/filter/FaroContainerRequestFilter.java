/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.request.filter;

import com.liferay.osb.faro.util.FaroRequestAudit;
import com.liferay.osb.faro.util.FaroThreadLocal;

import jakarta.annotation.Priority;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

/**
 * @author Shinn Lok
 */
@Priority(1)
public class FaroContainerRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		FaroRequestAudit faroRequestAudit = new FaroRequestAudit();

		FaroThreadLocal.setFaroRequestAudit(faroRequestAudit);

		faroRequestAudit.setStartTime(System.currentTimeMillis());
	}

}