/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.bridges.mvc;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

/**
 * Provides an interface to allow the portlet to process a particular action
 * request. This interface can only be used when the portlet is based on {@link
 * MVCPortlet}.
 *
 * <p>
 * The action command to be invoked is determined by two factors:
 * </p>
 *
 * <ul>
 * <li>
 * The portlet name that the action URL refers to.
 * </li>
 * <li>
 * The parameter value <code>ActionRequest.ACTION_NAME</code> of the action URL.
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
 * <code>jakarta.portlet.name</code>: The portlet name associated to this action
 * command.
 * </li>
 * <li>
 * <code>mvc.command.name</code>: the command name to match with the parameter
 * value <code>ActionRequest.ACTION_NAME</code>. This name cannot contain any
 * comma (<code>,</code>).
 * </li>
 * </ul>
 *
 * <p>
 * The method {@link MVCPortlet#processAction(ActionRequest, ActionResponse)}
 * searches the OSGi Registry for the action command that matches both the
 * portlet name with the property <code>jakarta.portlet.name</code> and the
 * parameter value <code>ActionRequest.ACTION_NAME</code> with the property
 * <code>mvc.command.name</code>.
 * </p>
 *
 * <p>
 * In general, only one action command is executed per portlet action URL. If
 * the parameter value <code>ActionRequest.ACTION_NAME</code> is, however, a
 * comma separated list of names, {@link MVCPortlet} finds the matching action
 * commands and invokes them sequentially in the order they're specified in the
 * list.
 * </p>
 *
 * <p>
 * When there are multiple action commands registered for the same portlet name
 * and with the same command name, only the action command with the highest
 * service ranking is invoked.
 * </p>
 *
 * <p>
 * {@link BaseMVCActionCommand} is an abstract class that implements this
 * interface and can be extended to simplify using action commands.
 * </p>
 *
 * @author Michael C. Han
 */
public interface MVCActionCommand extends MVCCommand {

	public static final MVCActionCommand EMPTY = new MVCActionCommand() {

		@Override
		public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse) {

			return false;
		}

	};

	/**
	 * Invoked by {@link MVCPortlet} to allow the portlet to process an action
	 * request.
	 *
	 * @param  actionRequest the action request
	 * @param  actionResponse the action response
	 * @return <code>true</code> if an error occurs in processing the action
	 *         request; <code>false</code> otherwise
	 */
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException;

}