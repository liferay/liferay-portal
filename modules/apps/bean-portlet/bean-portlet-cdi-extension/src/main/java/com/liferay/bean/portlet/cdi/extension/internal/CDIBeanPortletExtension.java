/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal;

import com.liferay.bean.portlet.cdi.extension.internal.annotated.type.ModifiedAnnotatedType;
import com.liferay.bean.portlet.cdi.extension.internal.mvc.MVCExtension;
import com.liferay.bean.portlet.cdi.extension.internal.mvc.ViewRendererMVCImpl;
import com.liferay.bean.portlet.cdi.extension.internal.scope.JSR362CDIBeanProducer;
import com.liferay.bean.portlet.cdi.extension.internal.scope.PortletRequestBeanContext;
import com.liferay.bean.portlet.cdi.extension.internal.scope.PortletSessionBeanContext;
import com.liferay.bean.portlet.cdi.extension.internal.scope.RenderStateBeanContext;
import com.liferay.bean.portlet.cdi.extension.internal.scope.ScopedBeanManager;
import com.liferay.bean.portlet.cdi.extension.internal.scope.ScopedBeanManagerThreadLocal;
import com.liferay.bean.portlet.cdi.extension.internal.scope.ServletContextProducer;
import com.liferay.bean.portlet.extension.BeanFilterMethod;
import com.liferay.bean.portlet.extension.BeanFilterMethodInvoker;
import com.liferay.bean.portlet.extension.BeanPortletMethod;
import com.liferay.bean.portlet.extension.BeanPortletMethodDecorator;
import com.liferay.bean.portlet.extension.BeanPortletMethodInvoker;
import com.liferay.bean.portlet.extension.BeanPortletMethodType;
import com.liferay.bean.portlet.extension.ScopedBean;
import com.liferay.bean.portlet.extension.ViewRenderer;
import com.liferay.bean.portlet.registration.util.BeanPortletRegistrarUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.async.PortletAsyncListenerFactory;
import com.liferay.portal.kernel.portlet.async.PortletAsyncScopeManagerFactory;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;

import jakarta.portlet.ActionParameters;
import jakarta.portlet.ActionRequest;
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
import jakarta.portlet.annotations.ContextPath;
import jakarta.portlet.annotations.Namespace;
import jakarta.portlet.annotations.PortletName;
import jakarta.portlet.annotations.PortletRequestScoped;
import jakarta.portlet.annotations.PortletSerializable;
import jakarta.portlet.annotations.PortletSessionScoped;
import jakarta.portlet.annotations.RenderStateScoped;
import jakarta.portlet.annotations.WindowId;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

import java.io.PrintWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Neil Griffin
 * @author Raymond Augé
 */
public class CDIBeanPortletExtension implements Extension {

	public void step1BeforeBeanDiscovery(
		@Observes BeforeBeanDiscovery beforeBeanDiscovery,
		BeanManager beanManager) {

		if (_log.isDebugEnabled()) {
			_log.debug("Scanning for bean portlets and bean filters");
		}

		beforeBeanDiscovery.addAnnotatedType(
			beanManager.createAnnotatedType(JSR362CDIBeanProducer.class), null);
		beforeBeanDiscovery.addAnnotatedType(
			beanManager.createAnnotatedType(ServletContextProducer.class),
			null);
		beforeBeanDiscovery.addQualifier(ContextPath.class);
		beforeBeanDiscovery.addQualifier(Namespace.class);
		beforeBeanDiscovery.addQualifier(PortletName.class);
		beforeBeanDiscovery.addQualifier(WindowId.class);
		beforeBeanDiscovery.addScope(PortletRequestScoped.class, true, false);
		beforeBeanDiscovery.addScope(PortletSessionScoped.class, true, true);
		beforeBeanDiscovery.addScope(RenderStateScoped.class, true, false);

		MVCExtension.step1BeforeBeanDiscovery(beanManager, beforeBeanDiscovery);
	}

	public <T> void step2ProcessAnnotatedType(
		@Observes ProcessAnnotatedType<T> processAnnotatedType) {

		if (_log.isDebugEnabled()) {
			_log.debug("processAnnotatedType=" + processAnnotatedType);
		}

		AnnotatedType<T> annotatedType =
			processAnnotatedType.getAnnotatedType();

		Class<T> discoveredClass = annotatedType.getJavaClass();

		if (annotatedType.isAnnotationPresent(RenderStateScoped.class) &&
			!PortletSerializable.class.isAssignableFrom(discoveredClass)) {

			if (_log.isWarnEnabled()) {
				_log.warn(
					discoveredClass.getName() + " does not implement " +
						PortletSerializable.class.getName());
			}
		}

		if (_log.isWarnEnabled()) {
			PortletSessionScoped portletSessionScoped =
				annotatedType.getAnnotation(PortletSessionScoped.class);

			if ((portletSessionScoped != null) &&
				(PortletSession.APPLICATION_SCOPE !=
					portletSessionScoped.value()) &&
				(PortletSession.PORTLET_SCOPE !=
					portletSessionScoped.value())) {

				_log.warn(
					"@PortletSessionScoped bean can only be " +
						"PortletSession.APPLICATION_SCOPE or " +
							"PortletSession.PORTLET_SCOPE");
			}
		}

		Set<Annotation> annotations = new HashSet<>(
			annotatedType.getAnnotations());

		if (annotations.remove(
				annotatedType.getAnnotation(RequestScoped.class)) &&
			!annotatedType.isAnnotationPresent(PortletRequestScoped.class)) {

			annotations.add(_portletRequestScoped);
		}

		if (annotations.remove(
				annotatedType.getAnnotation(SessionScoped.class)) &&
			!annotatedType.isAnnotationPresent(PortletSessionScoped.class)) {

			annotations.add(_portletSessionScoped);
		}

		if (Portlet.class.isAssignableFrom(discoveredClass) &&
			!annotatedType.isAnnotationPresent(ApplicationScoped.class)) {

			annotations.remove(
				annotatedType.getAnnotation(ConversationScoped.class));
			annotations.remove(
				annotatedType.getAnnotation(PortletRequestScoped.class));
			annotations.remove(
				annotatedType.getAnnotation(PortletSessionScoped.class));
			annotations.remove(
				annotatedType.getAnnotation(RequestScoped.class));
			annotations.remove(
				annotatedType.getAnnotation(SessionScoped.class));

			annotations.add(_applicationScoped);

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Automatically added @ApplicationScoped to " +
						discoveredClass);
			}
		}

		Set<Type> types = new HashSet<>(annotatedType.getTypeClosure());

		if (types.remove(PortletConfig.class) ||
			!annotations.equals(annotatedType.getAnnotations())) {

			processAnnotatedType.setAnnotatedType(
				new ModifiedAnnotatedType<>(annotatedType, annotations, types));
		}

		_discoveredClasses.add(discoveredClass);

		MVCExtension.step2ProcessAnnotatedType(processAnnotatedType);
	}

	public void step3AfterBeanDiscovery(
		@Observes AfterBeanDiscovery afterBeanDiscovery) {

		afterBeanDiscovery.addContext(new PortletRequestBeanContext());
		afterBeanDiscovery.addContext(new PortletSessionBeanContext());
		afterBeanDiscovery.addContext(new RenderStateBeanContext());

		MVCExtension.step3AfterBeanDiscovery(afterBeanDiscovery);
	}

	@SuppressWarnings({"serial", "unchecked"})
	public void step4ApplicationScopedInitializedAsync(
		@ObservesAsync ServletContext servletContext, BeanManager beanManager,
		BundleContext bundleContext) {

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"servlet.context.name", servletContext.getServletContextName()
			).build();

		_serviceRegistrations.add(
			bundleContext.registerService(
				PortletAsyncScopeManagerFactory.class,
				PortletAsyncScopeManagerImpl::new, properties));

		_serviceRegistrations.add(
			bundleContext.registerService(
				PortletAsyncListenerFactory.class,
				new PortletAsyncListenerFactory() {

					@Override
					public <T extends PortletAsyncListener> T
							getPortletAsyncListener(Class<T> clazz)
						throws PortletException {

						Bean<?> bean = beanManager.resolve(
							beanManager.getBeans(clazz));

						if (bean == null) {
							throw new PortletException(
								"Unable to create an instance of " +
									clazz.getName());
						}

						try {
							return clazz.cast(
								beanManager.getReference(
									bean, bean.getBeanClass(),
									beanManager.createCreationalContext(bean)));
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

					ScopedBeanManagerThreadLocal.invokeWithScopedBeanManager(
						() -> new ScopedBeanManager(
							null, portletRequest, portletResponse),
						() -> _invokePortletFilterMethod(
							beanFilterMethod, portletRequest, portletResponse,
							filterChain));
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

					ScopedBeanManagerThreadLocal.invokeWithScopedBeanManager(
						() -> new ScopedBeanManager(
							portletConfig, portletRequest, portletResponse),
						() -> _invokePortletBeanMethods(
							beanPortletMethods, portletRequest, portletResponse,
							portletConfig));
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
							beanManager, beanPortletMethod, portletConfig,
							portletRequest, portletResponse);
					}

					if (portletResponse instanceof RenderResponse ||
						portletResponse instanceof ResourceResponse) {

						_viewRenderer.render(
							(MimeResponse)portletResponse, portletConfig,
							portletRequest);
					}
				}

			};

		_serviceRegistrations.addAll(
			BeanPortletRegistrarUtil.register(
				new CDIBeanFilterMethodFactory(beanManager),
				beanFilterMethodInvoker,
				new CDIBeanPortletMethodFactory(beanManager),
				beanPortletMethodInvoker, _discoveredClasses, servletContext));

		Bean<?> bean = beanManager.resolve(
			beanManager.getBeans(ViewRenderer.class));

		if (bean == null) {
			Bundle bundle = bundleContext.getBundle();

			Dictionary<String, String> headers = bundle.getHeaders(_ENGLISH_EN);

			String importPackageHeader = headers.get("Import-Package");

			boolean importsMvcPackage = false;

			if (importPackageHeader.contains("jakarta.mvc;")) {
				importsMvcPackage = true;
			}

			boolean importsMvcBindingPackage = false;

			if (importPackageHeader.contains("jakarta.mvc.binding;")) {
				importsMvcBindingPackage = true;
			}

			_viewRenderer = new ViewRendererMVCImpl(
				beanManager, importsMvcBindingPackage, importsMvcPackage);
		}
		else {
			_viewRenderer = (ViewRenderer)beanManager.getReference(
				bean, bean.getBeanClass(),
				beanManager.createCreationalContext(bean));
		}
	}

	public void step4ApplicationScopedInitializedSync(
		@Initialized(ApplicationScoped.class) @Observes ServletContext
			servletContext,
		BeanManager beanManager, Event<ServletContext> servletContextEvent) {

		servletContextEvent.fireAsync(servletContext);
	}

	public void step5SessionScopeBeforeDestroyed(
		@Destroyed(SessionScoped.class) @Observes Object httpSessionObject) {

		HttpSession httpSession = (HttpSession)httpSessionObject;

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

	public void step6ApplicationScopedBeforeDestroyed(
		@Destroyed(ApplicationScoped.class) @Observes Object contextObject) {

		BeanPortletRegistrarUtil.unregister(
			_serviceRegistrations, (ServletContext)contextObject);

		_serviceRegistrations.clear();
	}

	private void _invokeBeanPortletMethod(
			BeanManager beanManager, BeanPortletMethod beanPortletMethod,
			PortletConfig portletConfig, PortletRequest portletRequest,
			PortletResponse portletResponse)
		throws PortletException {

		try {
			Bean<?> bean = beanManager.resolve(
				beanManager.getBeans(BeanPortletMethodDecorator.class));

			if (bean != null) {
				BeanPortletMethodDecorator beanPortletMethodDecorator =
					(BeanPortletMethodDecorator)beanManager.getReference(
						bean, BeanPortletMethodDecorator.class,
						beanManager.createCreationalContext(bean));

				beanPortletMethod =
					beanPortletMethodDecorator.getBeanPortletMethod(
						beanPortletMethod, portletConfig, portletRequest,
						portletResponse);
			}

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

	private static final String _ENGLISH_EN = LocaleUtil.ENGLISH.getLanguage();

	private static final Log _log = LogFactoryUtil.getLog(
		CDIBeanPortletExtension.class);

	private static final Annotation _applicationScoped =
		new ApplicationScoped() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return ApplicationScoped.class;
			}

		};

	private static final Annotation _portletRequestScoped =
		new PortletRequestScoped() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return PortletRequestScoped.class;
			}

		};

	private static final Annotation _portletSessionScoped =
		new PortletSessionScoped() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return PortletSessionScoped.class;
			}

			@Override
			public int value() {
				return PortletSession.PORTLET_SCOPE;
			}

		};

	private final Set<Class<?>> _discoveredClasses = new HashSet<>();
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();
	private ViewRenderer _viewRenderer;

}