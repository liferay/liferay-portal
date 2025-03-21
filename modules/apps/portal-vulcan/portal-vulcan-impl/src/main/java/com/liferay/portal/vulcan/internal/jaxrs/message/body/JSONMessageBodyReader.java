/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.message.body;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

/**
 * @author Javier Gamarra
 * @author Ivica Cardic
 */
@Consumes(MediaType.APPLICATION_JSON)
@Provider
public class JSONMessageBodyReader extends BaseMessageBodyReader {

	public JSONMessageBodyReader() {
		super(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);
	}

}