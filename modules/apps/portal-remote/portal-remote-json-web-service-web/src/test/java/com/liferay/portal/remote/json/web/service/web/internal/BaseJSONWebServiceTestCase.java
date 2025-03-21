/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.transformer.SortedHashMapJSONTransformer;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.remote.json.web.service.JSONWebServiceAction;
import com.liferay.portal.remote.json.web.service.JSONWebServiceActionsManager;
import com.liferay.portal.remote.json.web.service.exception.NoSuchJSONWebServiceException;
import com.liferay.portal.remote.json.web.service.web.internal.action.JSONWebServiceInvokerAction;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Igor Spasic
 * @author Raymond Augé
 */
public abstract class BaseJSONWebServiceTestCase {

	@AfterClass
	public static void tearDownClass() {
		_jsonWebServiceActionsManagerServiceRegistration.unregister();
	}

	protected static void initPortalServices() {
		JSONWebServiceActionsManagerImpl jsonWebServiceActionsManagerImpl =
			new JSONWebServiceActionsManagerImpl();

		jsonWebServiceActionsManagerImpl.activate(
			SystemBundleUtil.getBundleContext());

		_jsonWebServiceActionsManagerServiceRegistration =
			_bundleContext.registerService(
				JSONWebServiceActionsManager.class,
				jsonWebServiceActionsManagerImpl, null);

		jsonWebServiceActionsManager = jsonWebServiceActionsManagerImpl;
	}

	protected static void registerAction(Object action) {
		registerAction(action, StringPool.BLANK);
	}

	protected static void registerAction(
		Object action, String servletContextName) {

		registerActionClass(action, action.getClass(), servletContextName);
	}

	protected static void registerActionClass(Class<?> actionClass) {
		registerActionClass(actionClass, StringPool.BLANK);
	}

	protected static void registerActionClass(
		Class<?> actionClass, String servletContextName) {

		registerActionClass(null, actionClass, servletContextName);
	}

	protected static void registerActionClass(
		Object action, Class<?> actionClass, String servletContextName) {

		Method[] methods = actionClass.getMethods();

		for (Method actionMethod : methods) {
			if (actionMethod.getDeclaringClass() != actionClass) {
				continue;
			}

			String path = JSONWebServiceMappingResolverUtil.resolvePath(
				actionClass, actionMethod);
			String method = JSONWebServiceMappingResolverUtil.resolveHttpMethod(
				actionMethod);

			jsonWebServiceActionsManager.registerJSONWebServiceAction(
				servletContextName, StringPool.BLANK, action, actionClass,
				actionMethod, path, method);
		}
	}

	protected MockHttpServletRequest createHttpRequest(String pathInfo) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest30();

		mockHttpServletRequest.setAttribute(
			WebKeys.ORIGINAL_PATH_INFO, pathInfo);
		mockHttpServletRequest.setMethod(HttpMethods.GET);

		return mockHttpServletRequest;
	}

	protected MockHttpServletRequest createHttpRequest(
		String pathInfo, String method) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest30();

		mockHttpServletRequest.setAttribute(
			WebKeys.ORIGINAL_PATH_INFO, pathInfo);
		mockHttpServletRequest.setMethod(method);

		return mockHttpServletRequest;
	}

	protected JSONWebServiceAction lookupJSONWebServiceAction(
			HttpServletRequest httpServletRequest)
		throws NoSuchJSONWebServiceException {

		return jsonWebServiceActionsManager.getJSONWebServiceAction(
			httpServletRequest);
	}

	protected void setServletContext(
		MockHttpServletRequest mockHttpServletRequest, String contextName) {

		MockServletContext mockServletContext =
			(MockServletContext)mockHttpServletRequest.getServletContext();

		mockServletContext.setServletContextName(contextName);

		MockHttpSession mockHttpServletSession = new MockHttpSession(
			mockServletContext);

		mockHttpServletRequest.setSession(mockHttpServletSession);
	}

	protected String toJSON(Object object) {
		if (object instanceof JSONWebServiceInvokerAction.InvokerResult) {
			final JSONWebServiceInvokerAction.InvokerResult invokerResult =
				(JSONWebServiceInvokerAction.InvokerResult)object;

			JSONWebServiceInvokerAction jsonWebServiceInvokerAction =
				invokerResult.getJSONWebServiceInvokerAction();

			JSONWebServiceInvokerAction.InvokerResult newInvokerResult =
				jsonWebServiceInvokerAction.new InvokerResult(
					invokerResult.getResult()) {

					@Override
					protected JSONSerializer createJSONSerializer() {
						JSONSerializer jsonSerializer =
							JSONFactoryUtil.createJSONSerializer();

						jsonSerializer.transform(
							new SortedHashMapJSONTransformer(), HashMap.class);

						return jsonSerializer;
					}

				};

			object = newInvokerResult;
		}

		if (object instanceof JSONSerializable) {
			JSONSerializable jsonSerializable = (JSONSerializable)object;

			return jsonSerializable.toJSONString();
		}

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		return jsonSerializer.serialize(object);
	}

	protected String toJSON(Object object, String... includes) {
		if (object instanceof JSONSerializable) {
			JSONSerializable jsonSerializable = (JSONSerializable)object;

			return jsonSerializable.toJSONString();
		}

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		jsonSerializer.include(includes);

		return jsonSerializer.serialize(object);
	}

	protected List<Object> toList(String json) {
		JSONDeserializer<Map<String, Object>> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		return (List<Object>)jsonDeserializer.deserialize(json);
	}

	protected Map<String, Object> toMap(String json) {
		JSONDeserializer<Map<String, Object>> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		return jsonDeserializer.deserialize(json);
	}

	protected static JSONWebServiceActionsManager jsonWebServiceActionsManager;

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static ServiceRegistration<JSONWebServiceActionsManager>
		_jsonWebServiceActionsManagerServiceRegistration;

	private class MockHttpServletRequest30 extends MockHttpServletRequest {

		public MockHttpServletRequest30() {
			_mockServletContext = new MockServletContext() {
			};

			_mockServletContext.setContextPath(StringPool.BLANK);
			_mockServletContext.setServletContextName(StringPool.BLANK);
		}

		@Override
		public MockServletContext getServletContext() {
			return _mockServletContext;
		}

		private final MockServletContext _mockServletContext;

	}

}