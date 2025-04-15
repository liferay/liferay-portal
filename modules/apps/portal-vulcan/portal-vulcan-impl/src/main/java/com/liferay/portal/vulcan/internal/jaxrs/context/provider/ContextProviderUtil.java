/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider;

import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.internal.jaxrs.lifecycle.SafeReleaseInstanceResourceProvider;
import com.liferay.portal.vulcan.jaxrs.constants.JaxRsConstants;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxrs.impl.ResourceContextImpl;
import org.apache.cxf.jaxrs.impl.UriInfoImpl;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;

/**
 * @author Víctor Galán
 * @author Cristina González
 * @author Brian Wing Shun Chan
 */
public class ContextProviderUtil {

	public static EntityModel getEntityModel(Message message) throws Exception {
		Object matchedResource = getMatchedResource(message);

		if (!(matchedResource instanceof EntityModelResource)) {
			return null;
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)matchedResource;

		return entityModelResource.getEntityModel(_getPathParameters(message));
	}

	public static HttpServletRequest getHttpServletRequest(Message message) {
		return (HttpServletRequest)message.getContextualProperty(
			"HTTP.REQUEST");
	}

	public static Object getMatchedResource(Message message) {
		return _getMatchedResource(true, message);
	}

	public static MultivaluedHashMap<String, String> getMultivaluedHashMap(
		Map<String, String[]> parameterMap) {

		return new MultivaluedHashMap<String, String>() {
			{
				for (Entry<String, String[]> entry : parameterMap.entrySet()) {
					put(entry.getKey(), Arrays.asList(entry.getValue()));
				}
			}
		};
	}

	public static void releaseResourceInstance(Message message) {
		Exchange exchange = message.getExchange();

		Object resource = _getMatchedResource(false, message);

		if (resource == null) {
			return;
		}

		OperationResourceInfo operationResourceInfo = exchange.get(
			OperationResourceInfo.class);

		ClassResourceInfo classResourceInfo =
			operationResourceInfo.getClassResourceInfo();

		ResourceProvider resourceProvider =
			classResourceInfo.getResourceProvider();

		if (resourceProvider != null) {
			resourceProvider.releaseInstance(message, resource);
		}
	}

	private static Object _fetchExistingResource(
		Exchange exchange, String... keys) {

		Object resource = null;

		for (int i = 0; (i < keys.length) && (resource == null); i++) {
			resource = exchange.get(keys[i]);
		}

		return resource;
	}

	private static Object _getMatchedResource(
		boolean initialize, Message message) {

		Exchange exchange = message.getExchange();

		Object resource = _fetchExistingResource(
			exchange, JAXRSUtils.ROOT_INSTANCE,
			JaxRsConstants.LAST_SERVICE_OBJECT);

		if (resource != null) {
			return resource;
		}

		OperationResourceInfo operationResourceInfo = exchange.get(
			OperationResourceInfo.class);

		if (operationResourceInfo == null) {
			return null;
		}

		ResourceContext resourceContext = new ResourceContextImpl(
			message, operationResourceInfo);

		ClassResourceInfo classResourceInfo =
			operationResourceInfo.getClassResourceInfo();

		ResourceProvider resourceProvider =
			classResourceInfo.getResourceProvider();

		if (resourceProvider != null) {
			if (!(resourceProvider instanceof
					SafeReleaseInstanceResourceProvider)) {

				classResourceInfo.setResourceProvider(
					new SafeReleaseInstanceResourceProvider(resourceProvider));
			}

			Object instance = resourceProvider.getInstance(message);

			if (initialize) {
				resourceContext.initResource(instance);
			}

			return instance;
		}

		UriInfo uriInfo = new UriInfoImpl(message);

		List<Object> matchedResources = uriInfo.getMatchedResources();

		Class<?> matchedResourceClass = (Class<?>)matchedResources.get(0);

		return resourceContext.getResource(matchedResourceClass);
	}

	private static MultivaluedMap<String, String> _getPathParameters(
		Message message) {

		UriInfoImpl uriInfoImpl = new UriInfoImpl(message);

		return uriInfoImpl.getPathParameters();
	}

}