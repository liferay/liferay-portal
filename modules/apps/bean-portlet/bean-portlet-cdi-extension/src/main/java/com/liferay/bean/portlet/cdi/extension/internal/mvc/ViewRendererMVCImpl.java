/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import com.liferay.bean.portlet.extension.ViewRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.util.TypeLiteral;

import jakarta.mvc.Models;
import jakarta.mvc.binding.ParamError;
import jakarta.mvc.engine.ViewEngine;
import jakarta.mvc.engine.ViewEngineException;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;

import jakarta.ws.rs.core.Configuration;

import java.lang.annotation.Annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Neil Griffin
 */
public class ViewRendererMVCImpl implements ViewRenderer {

	public ViewRendererMVCImpl(
		BeanManager beanManager, boolean importsMvcBindingPackage,
		boolean importsMvcPackage) {

		_beanManager = beanManager;
		_importsMvcBindingPackage = importsMvcBindingPackage;
		_importsMvcPackage = importsMvcPackage;
	}

	@Override
	public void render(
			MimeResponse mimeResponse, PortletConfig portletConfig,
			PortletRequest portletRequest)
		throws PortletException {

		if (!_importsMvcPackage) {
			return;
		}

		Models models = _getModels(_beanManager);

		Map<String, Object> modelMap = models.asMap();

		for (Map.Entry<String, Object> entry : modelMap.entrySet()) {
			portletRequest.setAttribute(entry.getKey(), entry.getValue());
		}

		String viewName = (String)portletRequest.getAttribute(VIEW_NAME);

		if (viewName == null) {
			@SuppressWarnings("deprecation")
			String redirectedView = portletRequest.getParameter(
				REDIRECTED_VIEW);

			if (redirectedView != null) {
				PortletSession portletSession =
					portletRequest.getPortletSession(true);

				viewName = (String)portletSession.getAttribute(VIEW_NAME);

				if (viewName != null) {
					portletSession.removeAttribute(VIEW_NAME);
					portletRequest.setAttribute(VIEW_NAME, viewName);
				}
			}
		}

		if (viewName != null) {
			Configuration configuration = _getConfiguration(_beanManager);

			if (!viewName.contains(".")) {
				String defaultViewExtension = (String)configuration.getProperty(
					ConfigurationImpl.DEFAULT_VIEW_EXTENSION);

				viewName = StringBundler.concat(
					viewName, ".", defaultViewExtension);
			}

			ViewEngine supportingViewEngine = null;

			List<ViewEngine> viewEngines = _getViewEngines(_beanManager);

			for (ViewEngine viewEngine : viewEngines) {
				if (viewEngine.supports(viewName)) {
					supportingViewEngine = viewEngine;

					break;
				}
			}

			if (supportingViewEngine == null) {
				throw new PortletException(
					new ViewEngineException(
						"No ViewEngine found that supports " + viewName));
			}

			try {
				Event<Object> event = _beanManager.getEvent();

				event.fire(
					new BeforeProcessViewEventImpl(
						viewName, supportingViewEngine.getClass()));

				supportingViewEngine.processView(
					new ViewEngineContextImpl(
						configuration, portletRequest.getLocale(), mimeResponse,
						models, portletRequest));

				event.fire(
					new AfterProcessViewEventImpl(
						viewName, supportingViewEngine.getClass()));
			}
			catch (ViewEngineException viewEngineException) {
				throw new PortletException(viewEngineException);
			}
		}

		if (!_importsMvcBindingPackage) {
			return;
		}

		MutableBindingResult mutableBindingResult =
			BeanUtil.getMutableBindingResult(_beanManager);

		if ((mutableBindingResult == null) ||
			mutableBindingResult.isConsulted()) {

			return;
		}

		Set<ParamError> paramErrors = mutableBindingResult.getAllErrors();

		for (ParamError paramError : paramErrors) {
			if (!_log.isWarnEnabled()) {
				continue;
			}

			_log.warn(
				StringBundler.concat(
					"A BindingResult error was not processed for ",
					paramError.getParamName(), ": ", paramError.getMessage()));
		}
	}

	private Configuration _getConfiguration(BeanManager beanManager) {
		Bean<?> bean = beanManager.resolve(
			beanManager.getBeans(Configuration.class));

		return (Configuration)beanManager.getReference(
			bean, Configuration.class,
			beanManager.createCreationalContext(bean));
	}

	private Models _getModels(BeanManager beanManager) {
		Bean<?> bean = beanManager.resolve(beanManager.getBeans(Models.class));

		return (Models)beanManager.getReference(
			bean, Models.class, beanManager.createCreationalContext(bean));
	}

	private List<ViewEngine> _getViewEngines(BeanManager beanManager) {
		List<ViewEngine> viewEngines = new ArrayList<>();

		Set<Bean<?>> beans = beanManager.getBeans(
			_viewEnginesTypeLiteral.getType(), _viewEngines);

		Bean<?> bean = beanManager.resolve(beans);

		CreationalContext<?> creationalContext =
			beanManager.createCreationalContext(bean);

		Object reference = beanManager.getReference(
			bean, _viewEnginesTypeLiteral.getType(), creationalContext);

		if (reference instanceof List) {
			List<?> list = (List)reference;

			for (Object object : list) {
				if (object instanceof ViewEngine) {
					viewEngines.add((ViewEngine)object);
				}
			}
		}

		return viewEngines;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewRendererMVCImpl.class);

	private static final Annotation _viewEngines = new ViewEngines() {

		@Override
		public Class<? extends Annotation> annotationType() {
			return ViewEngines.class;
		}

	};

	private static final TypeLiteral<?> _viewEnginesTypeLiteral =
		new TypeLiteral<List<ViewEngine>>() {
		};

	private final BeanManager _beanManager;
	private final boolean _importsMvcBindingPackage;
	private final boolean _importsMvcPackage;

}