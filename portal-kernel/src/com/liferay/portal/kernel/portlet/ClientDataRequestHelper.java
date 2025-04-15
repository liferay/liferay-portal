/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.PortletException;

import jakarta.servlet.http.Part;

import java.io.IOException;

import java.util.Collection;

/**
 * @author Jiefeng Wu
 */
public interface ClientDataRequestHelper {

	public Part getPart(String name, Object request, Portlet portlet)
		throws IOException, PortletException;

	public Collection<Part> getParts(Object request, Portlet portlet)
		throws IOException, PortletException;

}