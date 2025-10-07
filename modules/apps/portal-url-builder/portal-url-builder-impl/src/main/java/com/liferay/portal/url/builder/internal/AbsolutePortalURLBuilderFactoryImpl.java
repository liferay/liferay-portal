/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.internal.util.CacheHelper;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = AbsolutePortalURLBuilderFactory.class)
public class AbsolutePortalURLBuilderFactoryImpl
	implements AbsolutePortalURLBuilderFactory {

	@Override
	public AbsolutePortalURLBuilder getAbsolutePortalURLBuilder(
		HttpServletRequest httpServletRequest) {

		return new AbsolutePortalURLBuilderImpl(
			_cacheHelper, _hashedFilesRegistry, _portal, httpServletRequest);
	}

	@Reference
	private CacheHelper _cacheHelper;

	@Reference
	private HashedFilesRegistry _hashedFilesRegistry;

	@Reference
	private Portal _portal;

}