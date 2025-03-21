/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.renderer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Gabriel Albuquerque
 */
@ProviderType
public interface DataLayoutRenderer {

	public String render(
			Long dataLayoutId,
			DataLayoutRendererContext dataLayoutRendererContext)
		throws Exception;

	/**
	 * @deprecated As of Mueller (7.2.x), see {@link
	 *             DataLayoutRenderer#render(Long, DataLayoutRendererContext)}
	 */
	@Deprecated
	public default String render(
			Long dataLayoutId, Map<String, Object> dataRecordValues,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		DataLayoutRendererContext dataLayoutRendererContext =
			new DataLayoutRendererContext();

		dataLayoutRendererContext.setDataRecordValues(dataRecordValues);
		dataLayoutRendererContext.setHttpServletRequest(httpServletRequest);
		dataLayoutRendererContext.setHttpServletResponse(httpServletResponse);

		return render(dataLayoutId, dataLayoutRendererContext);
	}

}