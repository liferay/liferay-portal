/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.internal.filters.util;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.servlet.filters.util.CacheFileNameContributor;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carlos Sierra Andrés
 */
@Component(service = CacheFileNameContributor.class)
public class MinifierTypeCacheFileNameContributor
	implements CacheFileNameContributor {

	@Override
	public String getParameterName() {
		return "minifierType";
	}

	@Override
	public String getParameterValue(HttpServletRequest httpServletRequest) {
		String minifierType = httpServletRequest.getParameter(
			getParameterName());

		if (Validator.isNull(minifierType)) {
			return null;
		}

		if (minifierType.equals("css") || minifierType.equals("js")) {
			return minifierType;
		}

		return null;
	}

}