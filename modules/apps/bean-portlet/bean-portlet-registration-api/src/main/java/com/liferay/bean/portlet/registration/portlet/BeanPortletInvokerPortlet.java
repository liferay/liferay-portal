/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.registration.portlet;

import com.liferay.bean.portlet.extension.BeanPortletMethod;
import com.liferay.bean.portlet.extension.BeanPortletMethodInvoker;
import com.liferay.bean.portlet.extension.BeanPortletMethodType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Event;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Map;

/**
 * @author Neil Griffin
 */
public class BeanPortletInvokerPortlet implements InvokerPortlet {

	public BeanPortletInvokerPortlet(
		Map<BeanPortletMethodType, List<BeanPortletMethod>> beanMethods,
		BeanPortletMethodInvoker beanPortletMethodInvoker,
		ClassLoader portletClassLoader) {

		_beanMethods = beanMethods;
		_beanPortletMethodInvoker = beanPortletMethodInvoker;
		_portletClassLoader = portletClassLoader;

		boolean facesPortlet = false;

		beanMethods:
		for (Map.Entry<BeanPortletMethodType, List<BeanPortletMethod>> entry :
				beanMethods.entrySet()) {

			for (BeanPortletMethod beanPortletMethod : entry.getValue()) {
				Method method = beanPortletMethod.getMethod();

				if (ClassUtil.isSubclass(
						method.getDeclaringClass(),
						"jakarta.portlet.faces.GenericFacesPortlet")) {

					facesPortlet = true;

					break beanMethods;
				}
			}
		}

		_facesPortlet = facesPortlet;
	}

	@Override
	public void destroy() {
		try {
			_invokeBeanMethods(_beanMethods.get(BeanPortletMethodType.DESTROY));
		}
		catch (PortletException portletException) {
			_log.error(portletException);
		}
	}

	@Override
	public Integer getExpCache() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Portlet getPortlet() {
		return this;
	}

	@Override
	public ClassLoader getPortletClassLoader() {
		return _portletClassLoader;
	}

	@Override
	public PortletConfig getPortletConfig() {
		return _portletConfig;
	}

	@Override
	public PortletContext getPortletContext() {
		return _portletConfig.getPortletContext();
	}

	@Override
	public Portlet getPortletInstance() {
		return this;
	}

	@Override
	public void init(PortletConfig portletConfig) throws PortletException {
		_invokeBeanMethods(
			_beanMethods.get(BeanPortletMethodType.INIT), portletConfig);

		_portletConfig = portletConfig;
	}

	@Override
	public boolean isCheckAuthToken() {
		return GetterUtil.getBoolean(
			_portletConfig.getInitParameter("check-auth-token"));
	}

	@Override
	public boolean isFacesPortlet() {
		return _facesPortlet;
	}

	@Override
	public boolean isHeaderPortlet() {
		return true;
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		_invokeMethodWithActiveScopes(
			_beanMethods.get(BeanPortletMethodType.ACTION), actionRequest,
			actionResponse);
	}

	@Override
	public void processEvent(
			EventRequest eventRequest, EventResponse eventResponse)
		throws PortletException {

		List<BeanPortletMethod> beanPortletMethods = _beanMethods.get(
			BeanPortletMethodType.EVENT);

		if (ListUtil.isEmpty(beanPortletMethods)) {
			return;
		}

		Event event = eventRequest.getEvent();

		beanPortletMethods = ListUtil.filter(
			beanPortletMethods,
			beanPortletMethod -> beanPortletMethod.isEventProcessor(
				event.getQName()));

		_invokeMethodWithActiveScopes(
			beanPortletMethods, eventRequest, eventResponse);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		_invokeMethodWithActiveScopes(
			_beanMethods.get(BeanPortletMethodType.RENDER), renderRequest,
			renderResponse);
	}

	@Override
	public void renderHeaders(
			HeaderRequest headerRequest, HeaderResponse headerResponse)
		throws PortletException {

		_invokeMethodWithActiveScopes(
			_beanMethods.get(BeanPortletMethodType.HEADER), headerRequest,
			headerResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		_invokeMethodWithActiveScopes(
			_beanMethods.get(BeanPortletMethodType.SERVE_RESOURCE),
			resourceRequest, resourceResponse);
	}

	@Override
	public void setPortletFilters() {
		throw new UnsupportedOperationException();
	}

	private void _invokeBeanMethods(
			List<BeanPortletMethod> beanPortletMethods, Object... arguments)
		throws PortletException {

		if (ListUtil.isEmpty(beanPortletMethods)) {
			return;
		}

		for (BeanPortletMethod beanPortletMethod : beanPortletMethods) {
			try {
				beanPortletMethod.invoke(arguments);
			}
			catch (ReflectiveOperationException reflectiveOperationException) {
				Throwable throwable = reflectiveOperationException.getCause();

				if (throwable instanceof PortletException) {
					throw (PortletException)throwable;
				}

				throw new PortletException(throwable);
			}
		}
	}

	private void _invokeMethodWithActiveScopes(
			List<BeanPortletMethod> beanPortletMethods,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortletException {

		if (ListUtil.isEmpty(beanPortletMethods)) {
			return;
		}

		_beanPortletMethodInvoker.invokeWithActiveScopes(
			beanPortletMethods, _portletConfig, portletRequest,
			portletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BeanPortletInvokerPortlet.class);

	private final Map<BeanPortletMethodType, List<BeanPortletMethod>>
		_beanMethods;
	private final BeanPortletMethodInvoker _beanPortletMethodInvoker;
	private final boolean _facesPortlet;
	private final ClassLoader _portletClassLoader;
	private PortletConfig _portletConfig;

}