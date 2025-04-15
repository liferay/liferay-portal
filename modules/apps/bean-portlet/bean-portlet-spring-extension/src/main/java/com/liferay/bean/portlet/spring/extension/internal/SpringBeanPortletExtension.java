/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal;

import com.liferay.bean.portlet.extension.BeanFilterMethod;
import com.liferay.bean.portlet.extension.BeanFilterMethodInvoker;
import com.liferay.bean.portlet.extension.BeanPortletMethod;
import com.liferay.bean.portlet.extension.BeanPortletMethodDecorator;
import com.liferay.bean.portlet.extension.BeanPortletMethodInvoker;
import com.liferay.bean.portlet.extension.BeanPortletMethodType;
import com.liferay.bean.portlet.extension.ScopedBean;
import com.liferay.bean.portlet.extension.ViewRenderer;
import com.liferay.bean.portlet.registration.util.BeanPortletRegistrarUtil;
import com.liferay.bean.portlet.spring.extension.internal.scope.SpringPortletRequestScope;
import com.liferay.bean.portlet.spring.extension.internal.scope.SpringPortletSessionScope;
import com.liferay.bean.portlet.spring.extension.internal.scope.SpringRedirectScope;
import com.liferay.bean.portlet.spring.extension.internal.scope.SpringRenderStateScope;
import com.liferay.bean.portlet.spring.extension.internal.scope.SpringScopedBeanManager;
import com.liferay.bean.portlet.spring.extension.internal.scope.SpringScopedBeanManagerThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.async.PortletAsyncListenerFactory;
import com.liferay.portal.kernel.portlet.async.PortletAsyncScopeManagerFactory;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionParameters;
import jakarta.portlet.ActionRequest;
import jakarta.portlet.EventPortlet;
import jakarta.portlet.HeaderPortlet;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletAsyncListener;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.ResourceServingPortlet;
import jakarta.portlet.annotations.ActionMethod;
import jakarta.portlet.annotations.DestroyMethod;
import jakarta.portlet.annotations.EventMethod;
import jakarta.portlet.annotations.RenderMethod;
import jakarta.portlet.annotations.ServeResourceMethod;
import jakarta.portlet.filter.ActionFilter;
import jakarta.portlet.filter.EventFilter;
import jakarta.portlet.filter.HeaderFilter;
import jakarta.portlet.filter.RenderFilter;
import jakarta.portlet.filter.ResourceFilter;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

import java.io.PrintWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author Neil Griffin
 */
public class SpringBeanPortletExtension {

	public SpringBeanPortletExtension(ApplicationContext applicationContext) {
		_applicationContext = applicationContext;
	}

	public void step1RegisterScopes(
		ConfigurableBeanFactory configurableBeanFactory) {

		configurableBeanFactory.registerScope(
			"portletAppSession",
			new SpringPortletSessionScope(PortletSession.APPLICATION_SCOPE));
		configurableBeanFactory.registerScope(
			"portletRedirect", new SpringRedirectScope());
		configurableBeanFactory.registerScope(
			"portletRenderState", new SpringRenderStateScope());
		configurableBeanFactory.registerScope(
			"portletRequest", new SpringPortletRequestScope());
		configurableBeanFactory.registerScope(
			"portletSession",
			new SpringPortletSessionScope(PortletSession.PORTLET_SCOPE));

		_configurableBeanFactory = configurableBeanFactory;
	}

	public void step2ProcessAnnotatedType(String className) {
		try {
			Class<?> clazz = Class.forName(className);

			if (_isAnnotatedType(clazz)) {
				_annotatedClasses.add(clazz);
			}
		}
		catch (ClassNotFoundException classNotFoundException) {
			_log.error(classNotFoundException);
		}
	}

	public void step3ApplicationScopeInitialized(
		ServletContext servletContext) {

		BundleContext bundleContext =
			(BundleContext)servletContext.getAttribute("osgi-bundlecontext");

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"servlet.context.name", servletContext.getServletContextName()
			).build();

		_serviceRegistrations.add(
			bundleContext.registerService(
				PortletAsyncScopeManagerFactory.class,
				SpringPortletAsyncScopeManager::new, properties));

		_serviceRegistrations.add(
			bundleContext.registerService(
				PortletAsyncListenerFactory.class,
				new PortletAsyncListenerFactory() {

					@Override
					public <T extends PortletAsyncListener> T
							getPortletAsyncListener(Class<T> clazz)
						throws PortletException {

						T bean = _applicationContext.getBean(clazz);

						if (bean == null) {
							throw new PortletException(
								"Unable to create an instance of " +
									clazz.getName());
						}

						try {
							return clazz.cast(bean);
						}
						catch (Exception exception) {
							throw new PortletException(
								"Unable to create an instance of " +
									clazz.getName(),
								exception);
						}
					}

				},
				properties));

		BeanFilterMethodInvoker beanFilterMethodInvoker =
			new BeanFilterMethodInvoker() {

				@Override
				public void invokeWithActiveScopes(
						BeanFilterMethod beanFilterMethod, Object filterChain,
						PortletRequest portletRequest,
						PortletResponse portletResponse)
					throws PortletException {

					SpringScopedBeanManagerThreadLocal.
						invokeWithScopedBeanManager(
							() -> new SpringScopedBeanManager(
								null, portletRequest, portletResponse),
							() -> _invokePortletFilterMethod(
								beanFilterMethod, portletRequest,
								portletResponse, filterChain));
				}

				private void _invokePortletFilterMethod(
						BeanFilterMethod beanFilterMethod,
						PortletRequest portletRequest,
						PortletResponse portletResponse, Object filterChain)
					throws PortletException {

					try {
						beanFilterMethod.invoke(
							portletRequest, portletResponse, filterChain);
					}
					catch (IllegalAccessException illegalAccessException) {
						throw new PortletException(illegalAccessException);
					}
					catch (ReflectiveOperationException
								reflectiveOperationException) {

						Throwable throwable =
							reflectiveOperationException.getCause();

						if (throwable instanceof PortletException) {
							throw (PortletException)throwable;
						}

						throw new PortletException(throwable);
					}
				}

			};

		BeanPortletMethodInvoker beanPortletMethodInvoker =
			new BeanPortletMethodInvoker() {

				@Override
				public void invokeWithActiveScopes(
						List<BeanPortletMethod> beanPortletMethods,
						PortletConfig portletConfig,
						PortletRequest portletRequest,
						PortletResponse portletResponse)
					throws PortletException {

					SpringScopedBeanManagerThreadLocal.
						invokeWithScopedBeanManager(
							() -> new SpringScopedBeanManager(
								portletConfig, portletRequest, portletResponse),
							() -> _invokePortletBeanMethods(
								beanPortletMethods, portletRequest,
								portletResponse, portletConfig));
				}

				private void _invokePortletBeanMethods(
						List<BeanPortletMethod> beanPortletMethods,
						PortletRequest portletRequest,
						PortletResponse portletResponse,
						PortletConfig portletConfig)
					throws PortletException {

					for (BeanPortletMethod beanPortletMethod :
							beanPortletMethods) {

						_invokeBeanPortletMethod(
							beanPortletMethod, portletConfig, portletRequest,
							portletResponse);
					}

					if (portletResponse instanceof RenderResponse ||
						portletResponse instanceof ResourceResponse) {

						ViewRenderer viewRenderer = _applicationContext.getBean(
							"viewRenderer", ViewRenderer.class);

						viewRenderer.render(
							(MimeResponse)portletResponse, portletConfig,
							portletRequest);
					}
				}

			};

		_serviceRegistrations.addAll(
			BeanPortletRegistrarUtil.register(
				new SpringBeanFilterMethodFactory(_configurableBeanFactory),
				beanFilterMethodInvoker,
				new SpringBeanPortletMethodFactory(_configurableBeanFactory),
				beanPortletMethodInvoker, _annotatedClasses, servletContext));
	}

	public void step4SessionScopeBeforeDestroyed(HttpSession httpSession) {
		Enumeration<String> enumeration = httpSession.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			Object value = httpSession.getAttribute(name);

			if (value instanceof ScopedBean) {
				ScopedBean<?> scopedBean = (ScopedBean<?>)value;

				scopedBean.destroy();
			}
		}
	}

	public void step5ApplicationScopeBeforeDestroyed(
		ServletContext servletContext) {

		BeanPortletRegistrarUtil.unregister(
			_serviceRegistrations, servletContext);

		_serviceRegistrations.clear();
	}

	private void _invokeBeanPortletMethod(
			BeanPortletMethod beanPortletMethod, PortletConfig portletConfig,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortletException {

		try {
			@SuppressWarnings("unchecked")
			BeanPortletMethodDecorator beanPortletMethodDecorator =
				_applicationContext.getBean(
					"beanPortletMethodDecorator",
					BeanPortletMethodDecorator.class);

			beanPortletMethod = beanPortletMethodDecorator.getBeanPortletMethod(
				beanPortletMethod, portletConfig, portletRequest,
				portletResponse);

			String include = null;
			Method method = beanPortletMethod.getMethod();

			BeanPortletMethodType beanPortletMethodType =
				beanPortletMethod.getBeanPortletMethodType();

			if (beanPortletMethodType == BeanPortletMethodType.ACTION) {
				ActionRequest actionRequest = (ActionRequest)portletRequest;

				String actionName = null;

				PortletContext portletContext =
					portletConfig.getPortletContext();

				if (portletContext.getEffectiveMajorVersion() >= 3) {
					ActionParameters actionParameters =
						actionRequest.getActionParameters();

					actionName = actionParameters.getValue(
						ActionRequest.ACTION_NAME);
				}
				else {
					actionName = actionRequest.getParameter(
						ActionRequest.ACTION_NAME);
				}

				String beanMethodActionName = beanPortletMethod.getActionName();

				if (Validator.isNull(beanMethodActionName) ||
					beanMethodActionName.equals(actionName)) {

					beanPortletMethod.invoke(portletRequest, portletResponse);
				}
			}
			else if ((beanPortletMethodType == BeanPortletMethodType.HEADER) ||
					 (beanPortletMethodType == BeanPortletMethodType.RENDER)) {

				PortletMode portletMode = portletRequest.getPortletMode();

				PortletMode beanMethodPortletMode =
					beanPortletMethod.getPortletMode();

				if ((beanMethodPortletMode == null) ||
					portletMode.equals(beanMethodPortletMode)) {

					if (method.getParameterCount() == 0) {
						String markup = (String)beanPortletMethod.invoke();

						if (markup != null) {
							MimeResponse mimeResponse =
								(MimeResponse)portletResponse;

							PrintWriter printWriter = mimeResponse.getWriter();

							printWriter.write(markup);
						}
					}
					else {
						beanPortletMethod.invoke(
							portletRequest, portletResponse);
					}

					include = beanPortletMethodType.getInclude(method);
				}
			}
			else if (beanPortletMethodType ==
						BeanPortletMethodType.SERVE_RESOURCE) {

				ResourceRequest resourceRequest =
					(ResourceRequest)portletRequest;

				String resourceID = resourceRequest.getResourceID();

				String beanMethodResourceID = beanPortletMethod.getResourceID();

				if (Validator.isNull(beanMethodResourceID) ||
					beanMethodResourceID.equals(resourceID)) {

					ResourceResponse resourceResponse =
						(ResourceResponse)portletResponse;

					String contentType = beanPortletMethodType.getContentType(
						method);

					if (Validator.isNotNull(contentType) &&
						!Objects.equals(contentType, "*/*")) {

						resourceResponse.setContentType(contentType);
					}

					String characterEncoding =
						beanPortletMethodType.getCharacterEncoding(method);

					if (Validator.isNotNull(characterEncoding)) {
						resourceResponse.setCharacterEncoding(
							characterEncoding);
					}

					if (method.getParameterCount() == 0) {
						String markup = (String)beanPortletMethod.invoke();

						if (Validator.isNotNull(markup)) {
							PrintWriter printWriter =
								resourceResponse.getWriter();

							printWriter.write(markup);
						}
					}
					else {
						beanPortletMethod.invoke(
							resourceRequest, resourceResponse);
					}

					include = beanPortletMethodType.getInclude(method);
				}
			}
			else {
				beanPortletMethod.invoke(portletRequest, portletResponse);
			}

			PortletMode beanMethodPortletMode =
				beanPortletMethod.getPortletMode();

			if (Validator.isNotNull(include) &&
				((beanMethodPortletMode == null) ||
				 beanMethodPortletMode.equals(
					 portletRequest.getPortletMode()))) {

				PortletContext portletContext =
					portletConfig.getPortletContext();

				PortletRequestDispatcher requestDispatcher =
					portletContext.getRequestDispatcher(include);

				if (requestDispatcher == null) {
					_log.error(
						"Unable to acquire dispatcher to include " + include);
				}
				else {
					requestDispatcher.include(portletRequest, portletResponse);
				}
			}
		}
		catch (InvocationTargetException invocationTargetException) {
			Throwable throwable = invocationTargetException.getCause();

			if (throwable instanceof PortletException) {
				throw (PortletException)throwable;
			}

			throw new PortletException(throwable);
		}
		catch (PortletException portletException) {
			throw portletException;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private boolean _isAnnotatedType(Class<?> clazz) {
		if (ActionFilter.class.isAssignableFrom(clazz) ||
			EventFilter.class.isAssignableFrom(clazz) ||
			EventPortlet.class.isAssignableFrom(clazz) ||
			HeaderFilter.class.isAssignableFrom(clazz) ||
			HeaderPortlet.class.isAssignableFrom(clazz) ||
			Portlet.class.isAssignableFrom(clazz) ||
			RenderFilter.class.isAssignableFrom(clazz) ||
			ResourceFilter.class.isAssignableFrom(clazz) ||
			ResourceServingPortlet.class.isAssignableFrom(clazz)) {

			return true;
		}

		Method[] methods = null;

		try {
			methods = clazz.getMethods();
		}
		catch (Throwable throwable) {
			String className = clazz.getName();

			if (!className.startsWith("org.springframework")) {
				_log.error(
					"Unable to discover methods in class " + clazz.getName(),
					throwable);
			}

			return false;
		}

		for (Method method : methods) {
			Annotation[] annotations = method.getAnnotations();

			for (Annotation annotation : annotations) {
				Class<? extends Annotation> annotationClass =
					annotation.getClass();

				if (ActionMethod.class.isAssignableFrom(annotationClass) ||
					DestroyMethod.class.isAssignableFrom(annotationClass) ||
					EventMethod.class.isAssignableFrom(annotationClass) ||
					RenderMethod.class.isAssignableFrom(annotationClass) ||
					ServeResourceMethod.class.isAssignableFrom(
						annotationClass)) {

					return true;
				}
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SpringBeanPortletExtension.class);

	private final Set<Class<?>> _annotatedClasses = new HashSet<>();
	private final ApplicationContext _applicationContext;
	private BeanFactory _configurableBeanFactory;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}