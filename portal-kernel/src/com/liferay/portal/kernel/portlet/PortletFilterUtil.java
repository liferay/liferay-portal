/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.filter.FilterChain;
import jakarta.portlet.filter.HeaderFilterChain;

import java.io.IOException;

/**
 * @author Michael Young
 * @author Neil Griffin
 */
public class PortletFilterUtil {

	public static void doFilter(
			PortletRequest portletRequest, PortletResponse portletResponse,
			String lifecycle, FilterChain filterChain)
		throws IOException, PortletException {

		if (lifecycle.equals(PortletRequest.ACTION_PHASE)) {
			ActionRequest actionRequest = (ActionRequest)portletRequest;
			ActionResponse actionResponse = (ActionResponse)portletResponse;

			filterChain.doFilter(actionRequest, actionResponse);
		}
		else if (lifecycle.equals(PortletRequest.EVENT_PHASE)) {
			EventRequest eventRequest = (EventRequest)portletRequest;
			EventResponse eventResponse = (EventResponse)portletResponse;

			filterChain.doFilter(eventRequest, eventResponse);
		}
		else if (lifecycle.equals(PortletRequest.HEADER_PHASE)) {
			HeaderRequest headerRequest = (HeaderRequest)portletRequest;
			HeaderResponse headerResponse = (HeaderResponse)portletResponse;

			HeaderFilterChain headerFilterChain =
				(HeaderFilterChain)filterChain;

			headerFilterChain.doFilter(headerRequest, headerResponse);
		}
		else if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
			RenderRequest renderRequest = (RenderRequest)portletRequest;
			RenderResponse renderResponse = (RenderResponse)portletResponse;

			filterChain.doFilter(renderRequest, renderResponse);
		}
		else if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			ResourceRequest resourceRequest = (ResourceRequest)portletRequest;
			ResourceResponse resourceResponse =
				(ResourceResponse)portletResponse;

			filterChain.doFilter(resourceRequest, resourceResponse);
		}
	}

}