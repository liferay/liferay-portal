/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.extension;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Neil Griffin
 */
@FunctionalInterface
@ProviderType
public interface BeanFilterMethodInvoker {

	public void invokeWithActiveScopes(
			BeanFilterMethod beanFilterMethod, Object filterChain,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortletException;

}