/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.facet;

/**
 * A URL builder that controls prepending the proxy path ({@link
 * com.liferay.portal.kernel.util.PropsValues#PORTAL_PROXY_PATH}) to the URL.
 *
 * <p>
 * By default, the proxy path is always used. However, there are scenarios where
 * the developer may need to strip it from the generated URL (e.ge. when
 * retrieving the internal URL of a resource).
 * </p>
 *
 * @author Iván Zaera Avellón
 */
public interface PathProxyAwareAbsolutePortalURLBuilder<T> {

	/**
	 * Returns a version of this URL builder that ignores the proxy path part.
	 *
	 * @return a version of this URL builder that ignores the proxy path part
	 */
	public T ignorePathProxy();

}