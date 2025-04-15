/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.portlet.PortletException;

import jakarta.servlet.http.Part;

import java.io.IOException;

import java.util.Collection;

/**
 * @author Jiefeng Wu
 */
public class ClientDataRequestHelperUtil {

	public static Part getPart(String name, Object request, Portlet portlet)
		throws IOException, PortletException {

		ClientDataRequestHelper clientDataRequestHelper =
			_clientDataRequestHelperSnapshot.get();

		return clientDataRequestHelper.getPart(name, request, portlet);
	}

	public static Collection<Part> getParts(Object request, Portlet portlet)
		throws IOException, PortletException {

		ClientDataRequestHelper clientDataRequestHelper =
			_clientDataRequestHelperSnapshot.get();

		return clientDataRequestHelper.getParts(request, portlet);
	}

	private static final Snapshot<ClientDataRequestHelper>
		_clientDataRequestHelperSnapshot = new Snapshot<>(
			ClientDataRequestHelperUtil.class, ClientDataRequestHelper.class);

}