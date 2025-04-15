/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.ActionURL;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Neil Griffin
 */
public class ActionURLImpl extends PortletURLImpl implements ActionURL {

	public ActionURLImpl(
		HttpServletRequest httpServletRequest, Portlet portlet, Layout layout,
		String lifecycle, MimeResponse.Copy copy) {

		super(httpServletRequest, portlet, layout, lifecycle, copy);
	}

	public ActionURLImpl(
		PortletRequest portletRequest, Portlet portlet, Layout layout,
		String lifecycle, MimeResponse.Copy copy) {

		super(portletRequest, portlet, layout, lifecycle, copy);
	}

}