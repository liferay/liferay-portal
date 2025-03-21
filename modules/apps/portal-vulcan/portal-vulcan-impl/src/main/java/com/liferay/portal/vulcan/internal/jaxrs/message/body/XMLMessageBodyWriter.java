/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.message.body;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

/**
 * @author Ivica Cardic
 */
@Produces({MediaType.APPLICATION_XHTML_XML, MediaType.APPLICATION_XML})
@Provider
public class XMLMessageBodyWriter extends BaseMessageBodyWriter {

	public XMLMessageBodyWriter() {
		super(XmlMapper.class, MediaType.APPLICATION_XML_TYPE);
	}

}