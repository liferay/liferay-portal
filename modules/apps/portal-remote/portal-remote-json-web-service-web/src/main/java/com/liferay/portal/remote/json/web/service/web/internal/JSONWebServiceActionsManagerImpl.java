/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.reflect.AnnotationLocator;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.bean.ClassLoaderBeanHandler;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMode;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MethodParameter;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.remote.json.web.service.JSONWebServiceAction;
import com.liferay.portal.remote.json.web.service.JSONWebServiceActionMapping;
import com.liferay.portal.remote.json.web.service.JSONWebServiceActionsManager;
import com.liferay.portal.remote.json.web.service.exception.NoSuchJSONWebServiceException;
import com.liferay.portal.spring.aop.AopInvocationHandler;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Igor Spasic
 * @author Miguel Pastor
 * @author Raymond Augé
 */
@Component(service = JSONWebServiceActionsManager.class)
public class JSONWebServiceActionsManagerImpl
	implements JSONWebServiceActionsManager {

	public static Class<?> getTargetClass(Object service) {
		while (true) {
			if (ProxyUtil.isProxyClass(service.getClass())) {
				InvocationHandler invocationHandler =
					ProxyUtil.getInvocationHandler(service);

				if (invocationHandler instanceof AopInvocationHandler) {
					AopInvocationHandler aopInvocationHandler =
						(AopInvocationHandler)invocationHandler;

					service = aopInvocationHandler.getTarget();
				}
				else if (invocationHandler instanceof ClassLoaderBeanHandler) {
					ClassLoaderBeanHandler classLoaderBeanHandler =
						(ClassLoaderBeanHandler)invocationHandler;

					Object bean = classLoaderBeanHandler.getBean();

					if (bean instanceof ServiceWrapper) {
						ServiceWrapper<?> serviceWrapper =
							(ServiceWrapper<?>)bean;

						service = serviceWrapper.getWrappedService();
					}
					else {
						service = bean;
					}
				}
				else {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to handle proxy of type " +
								invocationHandler);
					}

					return null;
				}
			}
			else if (service instanceof ServiceWrapper) {
				ServiceWrapper<?> serviceWrapper = (ServiceWrapper<?>)service;

				service = serviceWrapper.getWrappedService();
			}
			else {
				break;
			}
		}

		return service.getClass();
	}

	@Override
	public Set<String> getContextNames() {
		_ensureOpen();

		return new TreeSet<>(
			_contextNameIndexedJSONWebServiceActionConfigs.keySet());
	}

	@Override
	public JSONWebServiceAction getJSONWebServiceAction(
			HttpServletRequest httpServletRequest)
		throws NoSuchJSONWebServiceException {

		_ensureOpen();

		String path = GetterUtil.getString(
			httpServletRequest.getAttribute(WebKeys.ORIGINAL_PATH_INFO));

		String method = GetterUtil.getString(httpServletRequest.getMethod());

		String parameterPath = null;

		JSONRPCRequest jsonRPCRequest = null;

		int parameterPathIndex = _getParameterPathIndex(path);

		if (parameterPathIndex != -1) {
			parameterPath = path.substring(parameterPathIndex);

			path = path.substring(0, parameterPathIndex);
		}
		else {
			if (method.equals(HttpMethods.POST) &&
				!_portal.isMultipartRequest(httpServletRequest)) {

				jsonRPCRequest = JSONRPCRequest.detectJSONRPCRequest(
					httpServletRequest);

				if (jsonRPCRequest != null) {
					path += StringPool.SLASH + jsonRPCRequest.getMethod();

					method = null;
				}
			}
		}

		JSONWebServiceActionParameters jsonWebServiceActionParameters =
			new JSONWebServiceActionParameters();

		jsonWebServiceActionParameters.collectAll(
			httpServletRequest, parameterPath, jsonRPCRequest, null);

		if (jsonWebServiceActionParameters.getServiceContext() != null) {
			ServiceContextThreadLocal.pushServiceContext(
				jsonWebServiceActionParameters.getServiceContext());
		}

		JSONWebServiceActionConfig jsonWebServiceActionConfig =
			_findJSONWebServiceAction(
				httpServletRequest, path, method,
				jsonWebServiceActionParameters);

		return new JSONWebServiceActionImpl(
			jsonWebServiceActionConfig, jsonWebServiceActionParameters);
	}

	@Override
	public JSONWebServiceAction getJSONWebServiceAction(
			HttpServletRequest httpServletRequest, String path, String method,
			Map<String, Object> parameterMap)
		throws NoSuchJSONWebServiceException {

		_ensureOpen();

		JSONWebServiceActionParameters jsonWebServiceActionParameters =
			new JSONWebServiceActionParameters();

		jsonWebServiceActionParameters.collectAll(
			httpServletRequest, null, null, parameterMap);

		JSONWebServiceActionConfig jsonWebServiceActionConfig =
			_findJSONWebServiceAction(
				httpServletRequest, path, method,
				jsonWebServiceActionParameters);

		return new JSONWebServiceActionImpl(
			jsonWebServiceActionConfig, jsonWebServiceActionParameters);
	}

	@Override
	public JSONWebServiceActionMapping getJSONWebServiceActionMapping(
		String signature) {

		_ensureOpen();

		return _signatureIndexedJSONWebServiceActionConfigs.get(signature);
	}

	@Override
	public List<JSONWebServiceActionMapping> getJSONWebServiceActionMappings(
		String contextName) {

		_ensureOpen();

		List<JSONWebServiceActionConfig> jsonWebServiceActionConfigs =
			_contextNameIndexedJSONWebServiceActionConfigs.get(contextName);

		if (jsonWebServiceActionConfigs == null) {
			return Collections.emptyList();
		}

		return new ArrayList<>(jsonWebServiceActionConfigs);
	}

	@Override
	public synchronized void registerJSONWebServiceAction(
		String contextName, String contextPath, Object actionObject,
		Class<?> actionClass, Method actionMethod, String path, String method) {

		_ensureOpen();

		_registerJSONWebServiceAction(
			contextName, contextPath, actionObject, actionClass, actionMethod,
			path, method);
	}

	@Override
	public int registerService(
		String contextName, String contextPath, Object service) {

		_ensureOpen();

		return _registerService(contextName, contextPath, service);
	}

	@Override
	public synchronized int unregisterJSONWebServiceActions(
		Object actionObject) {

		_ensureOpen();

		return _unregisterJSONWebServiceActions(actionObject);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.create(
			bundleContext,
			StringBundler.concat(
				"(&(json.web.service.context.name=*)(json.web.service.context.",
				"path=*)(!(objectClass=", AopService.class.getName(), ")))"),
			new JSONWebServiceTrackerCustomizer(bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_openedServiceTrackerDCLSingleton.destroy(ServiceTracker::close);
	}

	private boolean _addJSONWebServiceActionConfig(
		JSONWebServiceActionConfig jsonWebServiceActionConfig) {

		JSONWebServiceActionConfig oldJSONWebServiceActionConfig =
			_signatureIndexedJSONWebServiceActionConfigs.putIfAbsent(
				jsonWebServiceActionConfig.getSignature(),
				jsonWebServiceActionConfig);

		if (oldJSONWebServiceActionConfig != null) {
			return false;
		}

		List<JSONWebServiceActionConfig> jsonWebServiceActionConfigs =
			_contextNameIndexedJSONWebServiceActionConfigs.computeIfAbsent(
				jsonWebServiceActionConfig.getContextName(),
				key -> new CopyOnWriteArrayList<>());

		jsonWebServiceActionConfigs.add(jsonWebServiceActionConfig);

		jsonWebServiceActionConfigs =
			_pathIndexedJSONWebServiceActionConfigs.computeIfAbsent(
				jsonWebServiceActionConfig.getPath(),
				key -> new CopyOnWriteArrayList<>());

		jsonWebServiceActionConfigs.add(jsonWebServiceActionConfig);

		return true;
	}

	private int _countMatchedParameters(
		String[] parameterNames, MethodParameter[] methodParameters) {

		int matched = 0;

		for (MethodParameter methodParameter : methodParameters) {
			String methodParameterName = methodParameter.getName();

			methodParameterName = StringUtil.toLowerCase(methodParameterName);

			for (String parameterName : parameterNames) {
				if (StringUtil.equalsIgnoreCase(
						parameterName, methodParameterName)) {

					matched++;
				}
			}
		}

		return matched;
	}

	private void _ensureOpen() {
		_openedServiceTrackerDCLSingleton.getSingleton(
			() -> {
				_serviceTracker.open();

				return _serviceTracker;
			});
	}

	private JSONWebServiceActionConfig _findJSONWebServiceAction(
			HttpServletRequest httpServletRequest, String path, String method,
			JSONWebServiceActionParameters jsonWebServiceActionParameters)
		throws NoSuchJSONWebServiceException {

		String[] paths = _resolvePaths(httpServletRequest, path);

		String contextName = paths[0];

		path = paths[1];

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Request JSON web service action with path ", path,
					" and method ", method, " for ", contextName));
		}

		JSONWebServiceActionConfig jsonWebServiceActionConfig =
			_getJSONWebServiceActionConfig(
				contextName, path, method,
				jsonWebServiceActionParameters.getParameterNames());

		if ((jsonWebServiceActionConfig == null) &&
			jsonWebServiceActionParameters.includeDefaultParameters()) {

			jsonWebServiceActionConfig = _getJSONWebServiceActionConfig(
				contextName, path, method,
				jsonWebServiceActionParameters.getParameterNames());
		}

		if (jsonWebServiceActionConfig == null) {
			throw new NoSuchJSONWebServiceException(
				StringBundler.concat(
					"No JSON web service action with path ", path,
					" and method ", method, " for ", contextName));
		}

		return jsonWebServiceActionConfig;
	}

	private JSONWebServiceActionConfig _getJSONWebServiceActionConfig(
		String contextName, String path, String method,
		String[] parameterNames) {

		int hint = -1;

		int offset = 0;

		if (Validator.isNotNull(contextName)) {
			String pathPrefix = StringBundler.concat(
				StringPool.SLASH, contextName, StringPool.PERIOD);

			if (path.startsWith(pathPrefix)) {
				offset = pathPrefix.length();
			}
		}

		int dotIndex = path.indexOf(CharPool.PERIOD, offset);

		if (dotIndex != -1) {
			hint = GetterUtil.getInteger(path.substring(dotIndex + 1), -1);

			if (hint != -1) {
				path = path.substring(0, dotIndex);
			}
		}

		List<JSONWebServiceActionConfig> jsonWebServiceActionConfigs =
			_pathIndexedJSONWebServiceActionConfigs.get(path);

		if (ListUtil.isEmpty(jsonWebServiceActionConfigs)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to find JSON web service actions with path ",
						path, " for ", contextName));
			}

			return null;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Found ", jsonWebServiceActionConfigs.size(),
					" JSON web service actions with path ", path, " for ",
					contextName));
		}

		jsonWebServiceActionConfigs = new ArrayList<>(
			jsonWebServiceActionConfigs);

		Collections.sort(jsonWebServiceActionConfigs);

		int max = -1;

		JSONWebServiceActionConfig matchedJSONWebServiceActionConfig = null;

		for (JSONWebServiceActionConfig jsonWebServiceActionConfig :
				jsonWebServiceActionConfigs) {

			if (PropsValues.JSONWS_WEB_SERVICE_STRICT_HTTP_METHOD &&
				(method != null)) {

				String jsonWebServiceActionConfigMethod =
					jsonWebServiceActionConfig.getMethod();

				if ((jsonWebServiceActionConfigMethod != null) &&
					!jsonWebServiceActionConfigMethod.equals(method)) {

					continue;
				}
			}

			MethodParameter[] jsonWebServiceActionConfigMethodParameters =
				jsonWebServiceActionConfig.getMethodParameters();

			int methodParametersCount =
				jsonWebServiceActionConfigMethodParameters.length;

			if ((hint != -1) && (methodParametersCount != hint)) {
				continue;
			}

			int count = _countMatchedParameters(
				parameterNames, jsonWebServiceActionConfigMethodParameters);

			if ((count > max) &&
				((hint != -1) || (count >= methodParametersCount))) {

				max = count;

				matchedJSONWebServiceActionConfig = jsonWebServiceActionConfig;
			}
		}

		if (_log.isDebugEnabled()) {
			if (matchedJSONWebServiceActionConfig == null) {
				_log.debug(
					StringBundler.concat(
						"Unable to match parameters to a JSON web service ",
						"action with path ", path, " for ", contextName));
			}
			else {
				_log.debug(
					StringBundler.concat(
						"Matched parameters to a JSON web service action with ",
						"path ", path, " for ", contextName));
			}
		}

		return matchedJSONWebServiceActionConfig;
	}

	private int _getJSONWebServiceActionsCount(String contextName) {
		List<JSONWebServiceActionConfig> jsonWebServiceActionConfigs =
			_contextNameIndexedJSONWebServiceActionConfigs.get(contextName);

		if (jsonWebServiceActionConfigs == null) {
			return 0;
		}

		return jsonWebServiceActionConfigs.size();
	}

	private int _getParameterPathIndex(String path) {
		int index = path.indexOf(CharPool.SLASH, 1);

		if (index != -1) {
			index = path.indexOf(CharPool.SLASH, index + 1);
		}

		return index;
	}

	private void _processBean(
		String contextName, String contextPath, Object bean) {

		if (!PropsValues.JSON_WEB_SERVICE_ENABLED) {
			return;
		}

		JSONWebService jsonWebService = AnnotationLocator.locate(
			getTargetClass(bean), JSONWebService.class);

		if (jsonWebService == null) {
			return;
		}

		try {
			JSONWebServiceMode jsonWebServiceMode = jsonWebService.mode();

			Method[] serviceMethods = JSONWebServiceScannerUtil.scan(bean);

			for (Method method : serviceMethods) {
				JSONWebService methodJSONWebService = method.getAnnotation(
					JSONWebService.class);

				if (methodJSONWebService == null) {
					if (!jsonWebServiceMode.equals(JSONWebServiceMode.AUTO)) {
						continue;
					}
				}
				else {
					JSONWebServiceMode methodJSONWebServiceMode =
						methodJSONWebService.mode();

					if (methodJSONWebServiceMode.equals(
							JSONWebServiceMode.IGNORE)) {

						continue;
					}
				}

				String httpMethod =
					JSONWebServiceMappingResolverUtil.resolveHttpMethod(method);

				if (!JSONWebServiceNamingUtil.isValidHttpMethod(httpMethod)) {
					continue;
				}

				Class<?> serviceBeanClass = method.getDeclaringClass();

				String path = JSONWebServiceMappingResolverUtil.resolvePath(
					serviceBeanClass, method);

				if (!JSONWebServiceNamingUtil.isIncludedPath(
						contextName, contextPath, path)) {

					continue;
				}

				if (JSONWebServiceNamingUtil.isIncludedMethod(method)) {
					_registerJSONWebServiceAction(
						contextName, contextPath, bean, serviceBeanClass,
						method, path, httpMethod);
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private synchronized void _registerJSONWebServiceAction(
		String contextName, String contextPath, Object actionObject,
		Class<?> actionClass, Method actionMethod, String path, String method) {

		try {
			if (!_addJSONWebServiceActionConfig(
					new JSONWebServiceActionConfig(
						contextName, contextPath, actionObject, actionClass,
						actionMethod, path, method))) {

				if (_log.isWarnEnabled()) {
					_log.warn(
						"A JSON web service action is already registered at " +
							path);
				}
			}
		}
		catch (Exception exception) {
			_log.warn(
				StringBundler.concat(
					"Unable to register service method {actionClass=",
					actionClass, ", actionMethod=", actionMethod,
					", actionObject=", actionObject, ", contextName=",
					contextName, ", contextPath=", contextPath, ", method=",
					method, ", path=", path, "}: ", exception.getMessage()));
		}
	}

	private int _registerService(
		String contextName, String contextPath, Object service) {

		_processBean(contextName, contextPath, service);

		int count = _getJSONWebServiceActionsCount(contextPath);

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Configured ", count, " actions for ", contextPath));
		}

		return count;
	}

	private boolean _removeJSONWebServiceActionConfig(
		JSONWebServiceActionConfig jsonWebServiceActionConfig) {

		if (!_signatureIndexedJSONWebServiceActionConfigs.remove(
				jsonWebServiceActionConfig.getSignature(),
				jsonWebServiceActionConfig)) {

			return false;
		}

		String contextName = jsonWebServiceActionConfig.getContextName();

		List<JSONWebServiceActionConfig> jsonWebServiceActionConfigs =
			_contextNameIndexedJSONWebServiceActionConfigs.get(contextName);

		jsonWebServiceActionConfigs.remove(jsonWebServiceActionConfig);

		if (jsonWebServiceActionConfigs.isEmpty()) {
			_contextNameIndexedJSONWebServiceActionConfigs.remove(contextName);
		}

		jsonWebServiceActionConfigs =
			_pathIndexedJSONWebServiceActionConfigs.get(
				jsonWebServiceActionConfig.getPath());

		jsonWebServiceActionConfigs.remove(jsonWebServiceActionConfig);

		if (jsonWebServiceActionConfigs.isEmpty()) {
			_pathIndexedJSONWebServiceActionConfigs.remove(
				jsonWebServiceActionConfig.getPath());
		}

		return true;
	}

	private String[] _resolvePaths(
		HttpServletRequest httpServletRequest, String path) {

		String contextName = null;

		int index = path.indexOf(CharPool.FORWARD_SLASH, 1);

		if (index != -1) {
			index = path.lastIndexOf(CharPool.PERIOD, index);

			if (index != -1) {
				contextName = path.substring(1, index);
			}
		}

		if (contextName == null) {
			ServletContext servletContext =
				httpServletRequest.getServletContext();

			contextName = servletContext.getServletContextName();

			if (Validator.isNotNull(contextName)) {
				path = StringBundler.concat(
					StringPool.SLASH, contextName, StringPool.PERIOD,
					path.substring(1));
			}
		}

		return new String[] {contextName, path};
	}

	private synchronized int _unregisterJSONWebServiceActions(
		Object actionObject) {

		int count = 0;

		for (JSONWebServiceActionConfig jsonWebServiceActionConfig :
				_signatureIndexedJSONWebServiceActionConfigs.values()) {

			if ((actionObject ==
					jsonWebServiceActionConfig.getActionObject()) &&
				_removeJSONWebServiceActionConfig(jsonWebServiceActionConfig)) {

				count++;
			}
		}

		return count;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSONWebServiceActionsManagerImpl.class);

	private final Map<String, List<JSONWebServiceActionConfig>>
		_contextNameIndexedJSONWebServiceActionConfigs =
			new ConcurrentHashMap<>();
	private final DCLSingleton<ServiceTracker<?, ?>>
		_openedServiceTrackerDCLSingleton = new DCLSingleton<>();
	private final Map<String, List<JSONWebServiceActionConfig>>
		_pathIndexedJSONWebServiceActionConfigs = new ConcurrentHashMap<>();

	@Reference
	private Portal _portal;

	private ServiceTracker<?, ?> _serviceTracker;
	private final ConcurrentMap<String, JSONWebServiceActionConfig>
		_signatureIndexedJSONWebServiceActionConfigs =
			new ConcurrentHashMap<>();

	private class JSONWebServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Object, Object> {

		public JSONWebServiceTrackerCustomizer(BundleContext bundleContext) {
			_bundleContext = bundleContext;
		}

		@Override
		public Object addingService(ServiceReference<Object> serviceReference) {
			String contextName = (String)serviceReference.getProperty(
				"json.web.service.context.name");
			String contextPath = (String)serviceReference.getProperty(
				"json.web.service.context.path");
			Object service = _bundleContext.getService(serviceReference);

			Bundle bundle = serviceReference.getBundle();

			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

			try (SafeCloseable safeCloseable =
					ThreadContextClassLoaderUtil.swap(
						bundleWiring.getClassLoader())) {

				_registerService(contextName, contextPath, service);
			}

			return service;
		}

		@Override
		public void modifiedService(
			ServiceReference<Object> serviceReference, Object service) {

			removedService(serviceReference, service);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<Object> serviceReference, Object service) {

			_unregisterJSONWebServiceActions(service);

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;

	}

}