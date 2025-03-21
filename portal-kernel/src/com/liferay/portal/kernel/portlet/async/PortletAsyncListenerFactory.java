/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.async;

import jakarta.portlet.PortletAsyncListener;
import jakarta.portlet.PortletException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Shuyang Zhou
 */
@FunctionalInterface
@ProviderType
public interface PortletAsyncListenerFactory {

	public <T extends PortletAsyncListener> T getPortletAsyncListener(
			Class<T> clazz)
		throws PortletException;

}