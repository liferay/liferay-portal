/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import com.liferay.bean.portlet.extension.BeanPortletMethod;
import com.liferay.bean.portlet.extension.BeanPortletMethodType;
import com.liferay.bean.portlet.extension.ViewRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Validator;

import jakarta.mvc.View;

import jakarta.portlet.ActionResponse;
import jakarta.portlet.BaseURL;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.MutableResourceParameters;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderParameters;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.filter.RenderURLWrapper;
import jakarta.portlet.filter.ResourceURLWrapper;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.lang.reflect.Method;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.context.ApplicationEventPublisher;

/**
 * @author Neil Griffin
 */
public class ControllerInterceptor extends BeanPortletMethodInterceptor {

	public ControllerInterceptor(
		ApplicationEventPublisher applicationEventPublisher,
		BeanPortletMethod beanPortletMethod, boolean controller,
		Object eventObject, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		super(beanPortletMethod, controller);

		_applicationEventPublisher = applicationEventPublisher;
		_eventObject = eventObject;
		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
	}

	@Override
	public Object invoke(Object... args) throws ReflectiveOperationException {
		if (!isController()) {
			return super.invoke(args);
		}

		// Set the viewName request attribute before invoking the controller
		// @RenderMethod or @ServeResourceMethod. This makes it possible for
		// the developer to determine the viewName that may have been set by an
		// @ActionMethod in the ACTION_PHASE by calling the
		// ViewEngineContext.getView() method.

		String actionPhaseViewName = null;

		BeanPortletMethodType beanPortletMethodType =
			getBeanPortletMethodType();

		if ((beanPortletMethodType == BeanPortletMethodType.RENDER) ||
			(beanPortletMethodType == BeanPortletMethodType.SERVE_RESOURCE)) {

			RenderParameters renderParameters =
				_portletRequest.getRenderParameters();

			if (renderParameters.getValue(ViewRenderer.REDIRECTED_VIEW) !=
					null) {

				PortletSession portletSession =
					_portletRequest.getPortletSession(true);

				actionPhaseViewName = (String)portletSession.getAttribute(
					ViewRenderer.VIEW_NAME);

				portletSession.removeAttribute(ViewRenderer.VIEW_NAME);

				if (actionPhaseViewName != null) {
					_portletRequest.setAttribute(
						ViewRenderer.VIEW_NAME, actionPhaseViewName);
				}
			}
		}

		BeanPortletMethod beanPortletMethod = getWrapped();

		_applicationEventPublisher.publishEvent(
			new BeforeControllerEventImpl(
				_eventObject,
				new ResourceInfoImpl(
					beanPortletMethod.getBeanClass(),
					beanPortletMethod.getMethod()),
				new UriInfoImpl()));

		Object result = super.invoke(args);

		BaseURL redirectURL = null;

		boolean renderView = true;

		String viewName = null;

		if (Validator.isNull(result)) {
			Method invocationContextMethod = getMethod();

			View view = invocationContextMethod.getAnnotation(View.class);

			if (view != null) {
				viewName = view.value();
			}
		}
		else {
			viewName = result.toString();
		}

		if (Validator.isNotNull(viewName) &&
			((beanPortletMethodType == BeanPortletMethodType.ACTION) ||
			 (beanPortletMethodType == BeanPortletMethodType.SERVE_RESOURCE))) {

			PortletSession portletSession = _portletRequest.getPortletSession(
				true);

			if (viewName.startsWith(ViewRenderer.REDIRECT_PREFIX)) {
				viewName = viewName.substring(
					ViewRenderer.REDIRECT_PREFIX.length());

				if (beanPortletMethodType == BeanPortletMethodType.ACTION) {
					redirectURL = new ActionRedirectURL(
						(ActionResponse)_portletResponse);
				}
				else {
					redirectURL = new ResourceRedirectURL(
						(ResourceResponse)_portletResponse);
				}
			}

			portletSession.setAttribute(ViewRenderer.VIEW_NAME, viewName);

			if (beanPortletMethodType == BeanPortletMethodType.ACTION) {
				ActionResponse actionResponse =
					(ActionResponse)_portletResponse;

				if (redirectURL == null) {
					MutableRenderParameters mutableRenderParameters =
						actionResponse.getRenderParameters();

					mutableRenderParameters.setValue(
						ViewRenderer.REDIRECTED_VIEW, Boolean.TRUE.toString());
				}
				else {
					try {
						actionResponse.sendRedirect(redirectURL.toString());
					}
					catch (IOException ioException) {
						_log.error(ioException);
					}
				}
			}
			else {
				if (redirectURL != null) {
					ResourceResponse resourceResponse =
						(ResourceResponse)_portletResponse;

					resourceResponse.setStatus(
						HttpServletResponse.SC_MOVED_TEMPORARILY);
					resourceResponse.addProperty(
						HttpHeaders.LOCATION, redirectURL.toString());

					renderView = false;
				}
			}
		}

		if (renderView) {
			if (Validator.isNull(viewName)) {
				viewName = actionPhaseViewName;
			}

			if (Validator.isNotNull(viewName)) {
				_portletRequest.setAttribute(ViewRenderer.VIEW_NAME, viewName);
			}
		}

		_applicationEventPublisher.publishEvent(
			new AfterControllerEventImpl(
				_eventObject,
				new ResourceInfoImpl(
					beanPortletMethod.getBeanClass(),
					beanPortletMethod.getMethod()),
				new UriInfoImpl()));

		if (redirectURL != null) {
			try {
				URI uri = new URI(redirectURL.toString());

				_applicationEventPublisher.publishEvent(
					new ControllerRedirectEventImpl(
						_eventObject, uri,
						new ResourceInfoImpl(
							beanPortletMethod.getBeanClass(),
							beanPortletMethod.getMethod()),
						new UriInfoImpl()));
			}
			catch (URISyntaxException uriSyntaxException) {
				_log.error(uriSyntaxException);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ControllerInterceptor.class);

	private final ApplicationEventPublisher _applicationEventPublisher;
	private final Object _eventObject;
	private final PortletRequest _portletRequest;
	private final PortletResponse _portletResponse;

	private static class ActionRedirectURL extends RenderURLWrapper {

		private ActionRedirectURL(ActionResponse actionResponse) {
			super(actionResponse.createRedirectURL(MimeResponse.Copy.ALL));

			MutableRenderParameters mutableRenderParameters =
				getRenderParameters();

			mutableRenderParameters.setValue(
				ViewRenderer.REDIRECTED_VIEW, Boolean.TRUE.toString());
		}

	}

	private static class ResourceRedirectURL extends ResourceURLWrapper {

		private ResourceRedirectURL(ResourceResponse resourceResponse) {
			super(resourceResponse.createResourceURL());

			MutableResourceParameters mutableResourceParameters =
				getResourceParameters();

			mutableResourceParameters.setValue(
				ViewRenderer.REDIRECTED_VIEW, Boolean.TRUE.toString());
		}

	}

}