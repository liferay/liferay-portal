/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Group;

import jakarta.portlet.PortletURL;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
public interface RequestBackedPortletURLFactory {

	public PortletURL createActionURL(String portletId);

	public PortletURL createControlPanelActionURL(
		String portletId, Group group, long refererGroupId, long refererPlid);

	public PortletURL createControlPanelPortletURL(
		String portletId, Group group, long refererGroupId, long refererPlid,
		String lifecycle);

	public PortletURL createControlPanelRenderURL(
		String portletId, Group group, long refererGroupId, long refererPlid);

	public PortletURL createControlPanelResourceURL(
		String portletId, Group group, long refererGroupId, long refererPlid);

	public PortletURL createPortletURL(String portletId, String lifecycle);

	public PortletURL createRenderURL(String portletId);

	public PortletURL createResourceURL(String portletId);

}