/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.bridges.mvc;

import com.liferay.petra.string.StringPool;

import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.PortletException;

/**
 * Provides an interface to handle the header phase of the portlet. This
 * interface can only be used when the portlet is based on {@link MVCPortlet}.
 *
 * <p>
 * The header command that to be invoked is determined by two factors:
 * </p>
 *
 * <ul>
 * <li>
 * The portlet name that the render URL is referring to.
 * </li>
 * <li>
 * The parameter value <code>mvcRenderCommandName</code> of the render URL.
 * </li>
 * </ul>
 *
 * <p>
 * Implementations of this interface must be OSGi components that are registered
 * in the OSGi Registry with the following properties:
 * </p>
 *
 * <ul>
 * <li>
 * <code>jakarta.portlet.name</code>: The portlet name associated to this header
 * command.
 * </li>
 * <li>
 * <code>mvc.command.name</code>: the command name that matches the
 * parameter value <code>mvcRenderCommandName</code>. This name cannot contain
 * any comma (<code>,</code>).
 * </li>
 * </ul>
 *
 * <p>
 * The method {@link MVCPortlet#renderHeaders(HeaderRequest, HeaderResponse)}
 * searches the OSGi Registry for the header command that matches both the
 * portlet name with the property <code>jakarta.portlet.name</code> and the
 * parameter value <code>mvc.command.name</code> with the property
 * <code>mvc.command.name</code>.
 * </p>
 *
 * <p>
 * When there are multiple header commands registered for the same portlet name
 * and with the same command name, only the header command with the highest
 * service ranking is invoked.
 * </p>
 *
 * @author Neil Griffin
 */
public interface MVCHeaderCommand extends MVCCommand {

	public static final MVCHeaderCommand EMPTY = new MVCHeaderCommand() {

		@Override
		public String renderHeaders(
			HeaderRequest headerRequest, HeaderResponse headerResponse) {

			return StringPool.BLANK;
		}

	};

	/**
	 * Invoked by {@link MVCPortlet} to handle the header phase of the portlet.
	 *
	 * @param  headerRequest the header request
	 * @param  headerResponse the header response
	 * @return the path that should be dispatched
	 */
	public String renderHeaders(
			HeaderRequest headerRequest, HeaderResponse headerResponse)
		throws PortletException;

}