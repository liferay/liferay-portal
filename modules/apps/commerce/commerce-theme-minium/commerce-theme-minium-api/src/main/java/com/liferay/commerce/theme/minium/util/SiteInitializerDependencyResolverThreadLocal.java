/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.theme.minium.util;

import com.liferay.commerce.theme.minium.SiteInitializerDependencyResolver;
import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Marco Leo
 */
public class SiteInitializerDependencyResolverThreadLocal {

	public static SiteInitializerDependencyResolver
		getSiteInitializerDependencyResolver() {

		return _siteInitializerDependencyResolver.get();
	}

	public static void removeSiteInitializerDependencyResolver() {
		_siteInitializerDependencyResolver.remove();
	}

	public static void setSiteInitializerDependencyResolver(
		SiteInitializerDependencyResolver siteInitializerDependencyResolver) {

		_siteInitializerDependencyResolver.set(
			siteInitializerDependencyResolver);
	}

	private static final ThreadLocal<SiteInitializerDependencyResolver>
		_siteInitializerDependencyResolver = new CentralizedThreadLocal<>(
			SiteInitializerDependencyResolverThreadLocal.class.getName() +
				"._siteInitializerDependencyResolver");

}