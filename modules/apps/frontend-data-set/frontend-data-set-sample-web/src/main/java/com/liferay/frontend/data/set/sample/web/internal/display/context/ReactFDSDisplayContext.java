/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.display.context;

import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.frontend.data.set.sample.web.internal.serializer.FDSSerializerUtil;
import com.liferay.frontend.data.set.serializer.FDSSerializer;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Marko Cikos
 */
public class ReactFDSDisplayContext {

	public ReactFDSDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public String getAPIURL() {
		return "/o/c/fdssamples";
	}

	public Object getViews() {
		FDSSerializer fdsSerializer = FDSSerializerUtil.getFDSSerializer();

		return fdsSerializer.serializeViews(
			FDSSampleFDSNames.REACT, _httpServletRequest);
	}

	private final HttpServletRequest _httpServletRequest;

}