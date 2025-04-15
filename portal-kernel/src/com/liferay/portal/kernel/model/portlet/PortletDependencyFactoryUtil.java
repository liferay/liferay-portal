/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model.portlet;

import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.portlet.PortletRequest;

/**
 * @author Neil Griffin
 */
public class PortletDependencyFactoryUtil {

	public static PortletDependency createPortletDependency(
		String name, String scope, String version) {

		PortletDependencyFactory portletDependencyFactory =
			_portletDependencyFactorySnapshot.get();

		return portletDependencyFactory.createPortletDependency(
			name, scope, version);
	}

	public static PortletDependency createPortletDependency(
		String name, String scope, String version, String markup,
		PortletRequest portletRequest) {

		PortletDependencyFactory portletDependencyFactory =
			_portletDependencyFactorySnapshot.get();

		return portletDependencyFactory.createPortletDependency(
			name, scope, version, markup, portletRequest);
	}

	private static final Snapshot<PortletDependencyFactory>
		_portletDependencyFactorySnapshot = new Snapshot<>(
			PortletDependencyFactoryUtil.class, PortletDependencyFactory.class);

}