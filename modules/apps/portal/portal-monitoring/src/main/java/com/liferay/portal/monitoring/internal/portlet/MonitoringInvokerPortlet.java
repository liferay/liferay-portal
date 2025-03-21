/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.portlet;

import com.liferay.portal.kernel.monitoring.DataSample;
import com.liferay.portal.kernel.monitoring.DataSampleFactory;
import com.liferay.portal.kernel.monitoring.DataSampleThreadLocal;
import com.liferay.portal.kernel.monitoring.PortletRequestType;
import com.liferay.portal.kernel.monitoring.RequestStatus;
import com.liferay.portal.kernel.portlet.InvokerFilterContainer;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.filter.ActionFilter;
import jakarta.portlet.filter.EventFilter;
import jakarta.portlet.filter.HeaderFilter;
import jakarta.portlet.filter.RenderFilter;
import jakarta.portlet.filter.ResourceFilter;

import java.io.IOException;

import java.util.List;

/**
 * @author Michael C. Han
 * @author Karthik Sudarshan
 * @author Raymond Augé
 * @author Philip Jones
 * @author Neil Griffin
 */
public class MonitoringInvokerPortlet
	implements InvokerFilterContainer, InvokerPortlet {

	public MonitoringInvokerPortlet(
		DataSampleFactory dataSampleFactory, InvokerPortlet invokerPortlet,
		MonitoringConfiguration monitoringConfiguration) {

		_dataSampleFactory = dataSampleFactory;
		_invokerPortlet = invokerPortlet;
		_monitoringConfiguration = monitoringConfiguration;
	}

	@Override
	public void destroy() {
		_invokerPortlet.destroy();
	}

	@Override
	public List<ActionFilter> getActionFilters() {
		InvokerFilterContainer invokerFilterContainer =
			(InvokerFilterContainer)_invokerPortlet;

		return invokerFilterContainer.getActionFilters();
	}

	@Override
	public List<EventFilter> getEventFilters() {
		InvokerFilterContainer invokerFilterContainer =
			(InvokerFilterContainer)_invokerPortlet;

		return invokerFilterContainer.getEventFilters();
	}

	@Override
	public Integer getExpCache() {
		return _invokerPortlet.getExpCache();
	}

	@Override
	public List<HeaderFilter> getHeaderFilters() {
		InvokerFilterContainer invokerFilterContainer =
			(InvokerFilterContainer)_invokerPortlet;

		return invokerFilterContainer.getHeaderFilters();
	}

	@Override
	public Portlet getPortlet() {
		return _invokerPortlet.getPortlet();
	}

	@Override
	public ClassLoader getPortletClassLoader() {
		return _invokerPortlet.getPortletClassLoader();
	}

	@Override
	public PortletConfig getPortletConfig() {
		return _invokerPortlet.getPortletConfig();
	}

	@Override
	public PortletContext getPortletContext() {
		return _invokerPortlet.getPortletContext();
	}

	@Override
	public Portlet getPortletInstance() {
		return _invokerPortlet.getPortletInstance();
	}

	@Override
	public List<RenderFilter> getRenderFilters() {
		InvokerFilterContainer invokerFilterContainer =
			(InvokerFilterContainer)_invokerPortlet;

		return invokerFilterContainer.getRenderFilters();
	}

	@Override
	public List<ResourceFilter> getResourceFilters() {
		InvokerFilterContainer invokerFilterContainer =
			(InvokerFilterContainer)_invokerPortlet;

		return invokerFilterContainer.getResourceFilters();
	}

	@Override
	public void init(PortletConfig portletConfig) throws PortletException {
		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)portletConfig;

		_invokerPortlet.init(liferayPortletConfig);

		com.liferay.portal.kernel.model.Portlet portletModel =
			liferayPortletConfig.getPortlet();

		_actionTimeout = portletModel.getActionTimeout();
		_headerTimeout = portletModel.getHeaderTimeout();
		_renderTimeout = portletModel.getRenderTimeout();
	}

	@Override
	public boolean isCheckAuthToken() {
		return _invokerPortlet.isCheckAuthToken();
	}

	@Override
	public boolean isFacesPortlet() {
		return _invokerPortlet.isFacesPortlet();
	}

	@Override
	public boolean isHeaderPortlet() {
		return _invokerPortlet.isHeaderPortlet();
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		DataSample dataSample = null;

		try {
			if (_monitoringConfiguration.monitorPortletActionRequest()) {
				dataSample = _dataSampleFactory.createPortletRequestDataSample(
					PortletRequestType.ACTION, actionRequest, actionResponse);

				dataSample.setTimeout(_actionTimeout);

				dataSample.prepare();

				DataSampleThreadLocal.initialize();
			}

			_invokerPortlet.processAction(actionRequest, actionResponse);

			if (_monitoringConfiguration.monitorPortletActionRequest() &&
				(dataSample != null)) {

				dataSample.capture(RequestStatus.SUCCESS);
			}
		}
		catch (Exception exception) {
			_processException(
				_monitoringConfiguration.monitorPortletActionRequest(),
				dataSample, exception);
		}
		finally {
			if (dataSample != null) {
				DataSampleThreadLocal.addDataSample(dataSample);
			}
		}
	}

	@Override
	public void processEvent(
			EventRequest eventRequest, EventResponse eventResponse)
		throws IOException, PortletException {

		DataSample dataSample = null;

		try {
			if (_monitoringConfiguration.monitorPortletEventRequest()) {
				dataSample = _dataSampleFactory.createPortletRequestDataSample(
					PortletRequestType.EVENT, eventRequest, eventResponse);

				dataSample.prepare();

				DataSampleThreadLocal.initialize();
			}

			_invokerPortlet.processEvent(eventRequest, eventResponse);

			if (_monitoringConfiguration.monitorPortletEventRequest() &&
				(dataSample != null)) {

				dataSample.capture(RequestStatus.SUCCESS);
			}
		}
		catch (Exception exception) {
			_processException(
				_monitoringConfiguration.monitorPortletEventRequest(),
				dataSample, exception);
		}
		finally {
			if (dataSample != null) {
				DataSampleThreadLocal.addDataSample(dataSample);
			}
		}
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		_render(
			renderRequest, renderResponse,
			() -> _invokerPortlet.render(renderRequest, renderResponse),
			PortletRequestType.RENDER, _renderTimeout);
	}

	@Override
	public void renderHeaders(
			HeaderRequest headerRequest, HeaderResponse headerResponse)
		throws IOException, PortletException {

		_render(
			headerRequest, headerResponse,
			() -> _invokerPortlet.renderHeaders(headerRequest, headerResponse),
			PortletRequestType.HEADER, _headerTimeout);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		DataSample dataSample = null;

		try {
			if (_monitoringConfiguration.monitorPortletResourceRequest()) {
				dataSample = _dataSampleFactory.createPortletRequestDataSample(
					PortletRequestType.RESOURCE, resourceRequest,
					resourceResponse);

				dataSample.prepare();

				DataSampleThreadLocal.initialize();
			}

			_invokerPortlet.serveResource(resourceRequest, resourceResponse);

			if (_monitoringConfiguration.monitorPortletResourceRequest() &&
				(dataSample != null)) {

				dataSample.capture(RequestStatus.SUCCESS);
			}
		}
		catch (Exception exception) {
			_processException(
				_monitoringConfiguration.monitorPortletResourceRequest(),
				dataSample, exception);
		}
		finally {
			if (dataSample != null) {
				DataSampleThreadLocal.addDataSample(dataSample);
			}
		}
	}

	public void setInvokerPortlet(InvokerPortlet invokerPortlet) {
		_invokerPortlet = invokerPortlet;
	}

	@Override
	public void setPortletFilters() throws PortletException {
		_invokerPortlet.setPortletFilters();
	}

	private void _processException(
			boolean monitorPortletRequest, DataSample dataSample,
			Exception exception)
		throws IOException, PortletException {

		if (monitorPortletRequest && (dataSample != null)) {
			dataSample.capture(RequestStatus.ERROR);
		}

		if (exception instanceof IOException) {
			throw (IOException)exception;
		}
		else if (exception instanceof PortletException) {
			throw (PortletException)exception;
		}

		throw new PortletException("Unable to process portlet", exception);
	}

	private void _render(
			RenderRequest renderRequest, MimeResponse mimeResponse,
			Renderable renderable, PortletRequestType portletRequestType,
			long timeout)
		throws IOException, PortletException {

		DataSample dataSample = null;

		try {
			if (_monitoringConfiguration.monitorPortletHeaderRequest() ||
				_monitoringConfiguration.monitorPortletRenderRequest()) {

				dataSample = _dataSampleFactory.createPortletRequestDataSample(
					portletRequestType, renderRequest, mimeResponse);

				dataSample.setTimeout(timeout);

				dataSample.prepare();

				DataSampleThreadLocal.initialize();
			}

			renderable.render();

			if ((_monitoringConfiguration.monitorPortletHeaderRequest() ||
				 _monitoringConfiguration.monitorPortletRenderRequest()) &&
				(dataSample != null)) {

				dataSample.capture(RequestStatus.SUCCESS);
			}
		}
		catch (Exception exception) {
			_processException(
				_monitoringConfiguration.monitorPortletHeaderRequest() ||
				_monitoringConfiguration.monitorPortletRenderRequest(),
				dataSample, exception);
		}
		finally {
			if (dataSample != null) {
				DataSampleThreadLocal.addDataSample(dataSample);
			}
		}
	}

	private long _actionTimeout;
	private final DataSampleFactory _dataSampleFactory;
	private long _headerTimeout;
	private InvokerPortlet _invokerPortlet;
	private final MonitoringConfiguration _monitoringConfiguration;
	private long _renderTimeout;

	@FunctionalInterface
	private interface Renderable {

		public void render() throws IOException, PortletException;

	}

}