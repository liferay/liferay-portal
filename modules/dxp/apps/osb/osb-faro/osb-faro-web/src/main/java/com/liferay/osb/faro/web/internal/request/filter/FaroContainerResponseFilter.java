/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.request.filter;

import com.liferay.osb.faro.util.FaroRequestAudit;
import com.liferay.osb.faro.util.FaroThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.annotation.Priority;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import java.util.List;
import java.util.Map;

/**
 * @author Shinn Lok
 */
@Priority(1)
public class FaroContainerResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(
		ContainerRequestContext containerRequestContext,
		ContainerResponseContext containerResponseContext) {

		FaroRequestAudit faroRequestAudit =
			FaroThreadLocal.getFaroRequestAudit();

		if ((faroRequestAudit == null) || !faroRequestAudit.isEnabled()) {
			return;
		}

		faroRequestAudit.setEndTime(System.currentTimeMillis());
		faroRequestAudit.setMethod(containerRequestContext.getMethod());
		faroRequestAudit.setStatusCode(containerResponseContext.getStatus());
		faroRequestAudit.setURLPath(
			getURLPath(containerRequestContext.getUriInfo()));

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringUtil.quote(
					faroRequestAudit.toString(), System.lineSeparator()));
		}
	}

	protected String getURLPath(UriInfo uriInfo) {
		StringBundler sb = new StringBundler();

		URI uri = uriInfo.getBaseUri();

		sb.append(uri.getPath());

		sb.append(StringPool.FORWARD_SLASH);
		sb.append(uriInfo.getPath());

		MultivaluedMap<String, String> queryParameters =
			uriInfo.getQueryParameters();

		if (queryParameters.isEmpty()) {
			return sb.toString();
		}

		sb.append(StringPool.QUESTION);

		for (Map.Entry<String, List<String>> entry :
				queryParameters.entrySet()) {

			for (String value : entry.getValue()) {
				sb.append(entry.getKey());
				sb.append(StringPool.EQUAL);
				sb.append(value);
				sb.append(StringPool.AMPERSAND);
			}
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroContainerResponseFilter.class);

}