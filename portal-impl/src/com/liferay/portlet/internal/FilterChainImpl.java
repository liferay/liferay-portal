/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventPortlet;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderPortlet;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.ResourceServingPortlet;
import jakarta.portlet.filter.ActionFilter;
import jakarta.portlet.filter.EventFilter;
import jakarta.portlet.filter.FilterChain;
import jakarta.portlet.filter.HeaderFilter;
import jakarta.portlet.filter.HeaderFilterChain;
import jakarta.portlet.filter.PortletFilter;
import jakarta.portlet.filter.RenderFilter;
import jakarta.portlet.filter.ResourceFilter;

import java.io.IOException;

import java.util.Collections;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class FilterChainImpl implements FilterChain, HeaderFilterChain {

	public FilterChainImpl(
		Portlet portlet, List<? extends PortletFilter> portletFilters) {

		_portlet = portlet;

		if (portletFilters != null) {
			_portletFilters = portletFilters;
		}
		else {
			_portletFilters = Collections.emptyList();
		}
	}

	@Override
	public void doFilter(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		if (_portletFilters.size() > _pos) {
			ActionFilter actionFilter = (ActionFilter)_portletFilters.get(
				_pos++);

			actionFilter.doFilter(actionRequest, actionResponse, this);
		}
		else {
			_portlet.processAction(actionRequest, actionResponse);
		}
	}

	@Override
	public void doFilter(EventRequest eventRequest, EventResponse eventResponse)
		throws IOException, PortletException {

		if (_portletFilters.size() > _pos) {
			EventFilter eventFilter = (EventFilter)_portletFilters.get(_pos++);

			eventFilter.doFilter(eventRequest, eventResponse, this);
		}
		else {
			EventPortlet eventPortlet = (EventPortlet)_portlet;

			eventPortlet.processEvent(eventRequest, eventResponse);
		}
	}

	@Override
	public void doFilter(
			HeaderRequest headerRequest, HeaderResponse headerResponse)
		throws IOException, PortletException {

		if (_portletFilters.size() > _pos) {
			HeaderFilter headerFilter = (HeaderFilter)_portletFilters.get(
				_pos++);

			headerFilter.doFilter(headerRequest, headerResponse, this);
		}
		else {
			HeaderPortlet headerPortlet = (HeaderPortlet)_portlet;

			headerPortlet.renderHeaders(headerRequest, headerResponse);
		}
	}

	@Override
	public void doFilter(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (_portletFilters.size() > _pos) {
			RenderFilter renderFilter = (RenderFilter)_portletFilters.get(
				_pos++);

			renderFilter.doFilter(renderRequest, renderResponse, this);
		}
		else {
			_portlet.render(renderRequest, renderResponse);
		}
	}

	@Override
	public void doFilter(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		if (_portletFilters.size() > _pos) {
			ResourceFilter resourceFilter = (ResourceFilter)_portletFilters.get(
				_pos++);

			resourceFilter.doFilter(resourceRequest, resourceResponse, this);
		}
		else {
			ResourceServingPortlet resourceServingPortlet =
				(ResourceServingPortlet)_portlet;

			resourceServingPortlet.serveResource(
				resourceRequest, resourceResponse);
		}
	}

	private final Portlet _portlet;
	private final List<? extends PortletFilter> _portletFilters;
	private int _pos;

}