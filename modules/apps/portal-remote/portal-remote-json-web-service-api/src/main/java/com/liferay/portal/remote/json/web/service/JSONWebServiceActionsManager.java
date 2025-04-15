/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service;

import com.liferay.portal.remote.json.web.service.exception.NoSuchJSONWebServiceException;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Igor Spasic
 */
public interface JSONWebServiceActionsManager {

	public Set<String> getContextNames();

	public JSONWebServiceAction getJSONWebServiceAction(
			HttpServletRequest httpServletRequest)
		throws NoSuchJSONWebServiceException;

	public JSONWebServiceAction getJSONWebServiceAction(
			HttpServletRequest httpServletRequest, String path, String method,
			Map<String, Object> parameters)
		throws NoSuchJSONWebServiceException;

	public JSONWebServiceActionMapping getJSONWebServiceActionMapping(
		String signature);

	public List<JSONWebServiceActionMapping> getJSONWebServiceActionMappings(
		String contextName);

	public void registerJSONWebServiceAction(
		String contextName, String contextPath, Object actionObject,
		Class<?> actionClass, Method actionMethod, String path, String method);

	public int registerService(
		String contextName, String contextPath, Object service);

	public int unregisterJSONWebServiceActions(Object actionObject);

}