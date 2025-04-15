/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.jaxrs.context.provider.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.internal.deployer.ObjectDefinitionDeployerImpl;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.cxf.message.Message;

/**
 * @author Luis Miguel Barcos
 */
public class ObjectContextProviderUtil {

	public static HttpServletRequest getHttpServletRequest(Message message) {
		return (HttpServletRequest)message.getContextualProperty(
			"HTTP.REQUEST");
	}

	public static ObjectDefinition getObjectDefinition(
		Message message,
		ObjectDefinitionDeployerImpl objectDefinitionDeployerImpl,
		Portal portal) {

		long companyId = portal.getCompanyId(getHttpServletRequest(message));

		String restContextPath = (String)message.getContextualProperty(
			"org.apache.cxf.message.Message.BASE_PATH");

		restContextPath = restContextPath.substring(
			restContextPath.indexOf("/o/"));

		restContextPath = StringUtil.removeFirst(restContextPath, "/o");
		restContextPath = StringUtil.replaceLast(restContextPath, '/', "");

		try {
			return objectDefinitionDeployerImpl.getObjectDefinition(
				companyId, restContextPath);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			throw new RuntimeException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectContextProviderUtil.class);

}