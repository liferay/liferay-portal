/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.facet;

/**
 * A URL builder that controls serving by CDN/portal of underlying resource.
 *
 * <p>
 * By default, CDN aware resources are served from CDN if it is configured.
 * </p>
 *
 * @author Iván Zaera Avellón
 */
public interface CDNAwareAbsolutePortalURLBuilder<T> {

	/**
	 * Returns a version of this URL builder that ignores the CDN part. See
	 * {@code
	 * com.liferay.portal.kernel.util.Portal#getCDNHost(
	 * jakarta.servlet.http.HttpServletRequest)} for details.
	 *
	 * @return a version of this URL builder that ignores the CDN part
	 */
	public T ignoreCDNHost();

}