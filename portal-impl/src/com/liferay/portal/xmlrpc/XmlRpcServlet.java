/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.xmlrpc;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.xmlrpc.Method;
import com.liferay.portal.kernel.xmlrpc.Response;
import com.liferay.portal.kernel.xmlrpc.XmlRpcConstants;
import com.liferay.portal.kernel.xmlrpc.XmlRpcException;
import com.liferay.portal.util.PortalInstances;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Alexander Chow
 * @author Brian Wing Shun Chan
 */
public class XmlRpcServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		PortalUtil.sendError(
			HttpServletResponse.SC_NOT_FOUND,
			new IllegalArgumentException("The GET method is not supported"),
			httpServletRequest, httpServletResponse);
	}

	@Override
	protected void doPost(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		Response xmlRpcResponse = null;

		try {
			long companyId = PortalInstances.getCompanyId(httpServletRequest);

			String xml = StringUtil.read(httpServletRequest.getInputStream());

			Tuple methodTuple = XmlRpcUtil.parseMethod(xml);

			String methodName = (String)methodTuple.getObject(0);
			Object[] args = (Object[])methodTuple.getObject(1);

			xmlRpcResponse = invokeMethod(
				companyId, getToken(httpServletRequest), methodName, args);
		}
		catch (IOException ioException) {
			xmlRpcResponse = XmlRpcUtil.createFault(
				XmlRpcConstants.NOT_WELL_FORMED, "XML is not well formed");

			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}
		catch (XmlRpcException xmlRpcException) {
			_log.error(xmlRpcException);
		}

		if (xmlRpcResponse == null) {
			xmlRpcResponse = XmlRpcUtil.createFault(
				XmlRpcConstants.SYSTEM_ERROR, "Unknown error occurred");
		}

		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(ContentTypes.TEXT_XML);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		try {
			ServletResponseUtil.write(
				httpServletResponse, xmlRpcResponse.toXml());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			httpServletResponse.setStatus(
				HttpServletResponse.SC_PRECONDITION_FAILED);
		}
	}

	protected String getToken(HttpServletRequest httpServletRequest) {
		String token = httpServletRequest.getPathInfo();

		return HttpComponentsUtil.fixPath(token);
	}

	protected Response invokeMethod(
			long companyId, String token, String methodName, Object[] arguments)
		throws XmlRpcException {

		Method method = XmlRpcMethodUtil.getMethod(token, methodName);

		if (method == null) {
			return XmlRpcUtil.createFault(
				XmlRpcConstants.REQUESTED_METHOD_NOT_FOUND,
				"Requested method not found");
		}

		if (!method.setArguments(arguments)) {
			return XmlRpcUtil.createFault(
				XmlRpcConstants.INVALID_METHOD_PARAMETERS,
				"Method arguments are invalid");
		}

		return method.execute(companyId);
	}

	private static final Log _log = LogFactoryUtil.getLog(XmlRpcServlet.class);

}