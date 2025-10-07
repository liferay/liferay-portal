/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.error;

import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

/**
 * @author Matija Petanjek
 */
public class LiferayResponseValidator {

	public void validate(HttpResponse httpResponse) throws ModuleException {
		if (httpResponse == null) {
			throw new ModuleException(
				"Server returned no response", LiferayError.SERVER_ERROR);
		}
		else if (httpResponse.getStatusCode() >= 400) {
			throw new ModuleException(
				getMessage(httpResponse),
				LiferayError.fromStatus(httpResponse.getStatusCode()));
		}
	}

	private String getMessage(HttpResponse httpResponse) {
		HttpEntity httpEntity = httpResponse.getEntity();

		return String.format(
			"Request failed with status: %d, and message: %s",
			httpResponse.getStatusCode(),
			IOUtils.toString(httpEntity.getContent()));
	}

}