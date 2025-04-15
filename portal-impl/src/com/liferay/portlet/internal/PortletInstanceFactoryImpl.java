/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.module.util.ServiceTrackerFieldUpdaterCustomizer;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.InvokerFilterContainer;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.InvokerPortletFactory;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletInstanceFactory;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portlet.PortletContextFactoryUtil;
import com.liferay.portlet.UndeployedPortlet;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;

import jakarta.servlet.ServletContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Neil Griffin
 */
public class PortletInstanceFactoryImpl implements PortletInstanceFactory {

	public void afterPropertiesSet() throws Exception {
		_serviceTracker = new ServiceTracker<>(
			SystemBundleUtil.getBundleContext(), InvokerPortletFactory.class,
			new ServiceTrackerFieldUpdaterCustomizer
				<InvokerPortletFactory, InvokerPortletFactory>(
					ReflectionUtil.getDeclaredField(
						PortletInstanceFactoryImpl.class,
						"_invokerPortletFactory"),
					this, _defaultInvokerPortletFactory) {

				@Override
				protected void afterServiceUpdate(
					InvokerPortletFactory oldInvokerPortletFactory,
					InvokerPortletFactory newInvokerPortletFactory) {

					_pool.clear();
				}

			});

		_serviceTracker.open();
	}

	@Override
	public void clear(Portlet portlet) {
		clear(portlet, true);
	}

	@Override
	public void clear(Portlet portlet, boolean resetRemotePortletBag) {
		String rootPortletId = portlet.getRootPortletId();

		Map<String, InvokerPortlet> portletInstances = _pool.remove(
			rootPortletId);

		if (portletInstances != null) {
			InvokerPortlet rootInvokerPortletInstance = portletInstances.remove(
				rootPortletId);

			if (rootInvokerPortletInstance != null) {
				rootInvokerPortletInstance.destroy();
			}

			portletInstances.clear();
		}

		PortletApp portletApp = portlet.getPortletApp();

		if (resetRemotePortletBag && portletApp.isWARFile()) {
			PortletBag portletBag = PortletBagPool.remove(rootPortletId);

			if (portletBag != null) {
				portletBag.destroy();
			}
		}
	}

	@Override
	public InvokerPortlet create(Portlet portlet, ServletContext servletContext)
		throws PortletException {

		return create(portlet, servletContext, false);
	}

	@Override
	public InvokerPortlet create(
			Portlet portlet, ServletContext servletContext,
			boolean destroyPrevious)
		throws PortletException {

		if (destroyPrevious) {
			destroyRelated(portlet);
		}

		boolean instanceable = false;

		boolean deployed = !portlet.isUndeployedPortlet();

		if (portlet.isInstanceable() && deployed &&
			PortletIdCodec.hasInstanceId(portlet.getPortletId())) {

			instanceable = true;
		}

		String rootPortletId = portlet.getRootPortletId();

		InvokerPortlet rootInvokerPortletInstance = null;

		Map<String, InvokerPortlet> portletInstances = null;

		if (deployed) {
			portletInstances = _pool.get(rootPortletId);

			if (portletInstances == null) {
				portletInstances = new ConcurrentHashMap<>();

				_pool.put(rootPortletId, portletInstances);
			}
			else {
				if (instanceable) {
					InvokerPortlet instanceInvokerPortletInstance =
						portletInstances.get(portlet.getPortletId());

					if (instanceInvokerPortletInstance != null) {
						return instanceInvokerPortletInstance;
					}
				}

				rootInvokerPortletInstance = portletInstances.get(
					rootPortletId);
			}
		}

		if (rootInvokerPortletInstance == null) {
			PortletConfig portletConfig = PortletConfigFactoryUtil.create(
				portlet, servletContext);

			jakarta.portlet.Portlet portletInstance = null;

			if (deployed) {
				portletInstance = PortletBagUtil.getPortletInstance(
					servletContext, portlet, rootPortletId);
			}
			else {
				portletInstance = UndeployedPortlet.getInstance();
			}

			rootInvokerPortletInstance = init(
				portlet, portletConfig, portletInstance);

			if (deployed) {
				portletInstances.put(rootPortletId, rootInvokerPortletInstance);
			}
		}

		if (!instanceable) {
			return rootInvokerPortletInstance;
		}

		jakarta.portlet.Portlet portletInstance =
			rootInvokerPortletInstance.getPortletInstance();

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, servletContext);

		PortletContext portletContext = portletConfig.getPortletContext();

		boolean checkAuthToken = rootInvokerPortletInstance.isCheckAuthToken();
		boolean facesPortlet = rootInvokerPortletInstance.isFacesPortlet();
		boolean headerPortlet = rootInvokerPortletInstance.isHeaderPortlet();

		InvokerPortlet instanceInvokerPortletInstance =
			_invokerPortletFactory.create(
				portlet, portletInstance, portletConfig, portletContext,
				(InvokerFilterContainer)rootInvokerPortletInstance,
				checkAuthToken, facesPortlet, headerPortlet);

		if (deployed) {
			portletInstances.put(
				portlet.getPortletId(), instanceInvokerPortletInstance);
		}

		return instanceInvokerPortletInstance;
	}

	@Override
	public void delete(Portlet portlet) {
		if (PortletIdCodec.hasInstanceId(portlet.getPortletId())) {
			Map<String, InvokerPortlet> portletInstances = _pool.get(
				portlet.getRootPortletId());

			if (portletInstances != null) {
				portletInstances.remove(portlet.getPortletId());
			}
		}
	}

	public void destroy() {

		// LPS-10473

		_serviceTracker.close();
	}

	@Override
	public void destroy(Portlet portlet) {
		clear(portlet);

		destroyRelated(portlet);

		PortletLocalServiceUtil.destroyPortlet(portlet);
	}

	public void setDefaultInvokerPortletFactory(
		InvokerPortletFactory defaultInvokerPortletFactory) {

		_defaultInvokerPortletFactory = defaultInvokerPortletFactory;

		_invokerPortletFactory = defaultInvokerPortletFactory;
	}

	protected void destroyRelated(Portlet portlet) {
		PortletConfigFactoryUtil.destroy(portlet);
		PortletContextFactoryUtil.destroy(portlet);
	}

	protected InvokerPortlet init(
			Portlet portlet, PortletConfig portletConfig,
			jakarta.portlet.Portlet portletInstance)
		throws PortletException {

		PortletContext portletContext = portletConfig.getPortletContext();

		InvokerFilterContainer invokerFilterContainer =
			InvokerFilterContainerImpl.EMPTY_INVOKER_FILTER_CONTAINER;

		if (!portlet.isUndeployedPortlet()) {
			invokerFilterContainer = new InvokerFilterContainerImpl(
				portlet, portletContext);
		}

		InvokerPortlet invokerPortlet = _invokerPortletFactory.create(
			portlet, portletInstance, portletContext, invokerFilterContainer);

		invokerPortlet.init(portletConfig);

		return invokerPortlet;
	}

	private InvokerPortletFactory _defaultInvokerPortletFactory;
	private volatile InvokerPortletFactory _invokerPortletFactory;
	private final Map<String, Map<String, InvokerPortlet>> _pool =
		new ConcurrentHashMap<>();
	private ServiceTracker<InvokerPortletFactory, InvokerPortletFactory>
		_serviceTracker;

}