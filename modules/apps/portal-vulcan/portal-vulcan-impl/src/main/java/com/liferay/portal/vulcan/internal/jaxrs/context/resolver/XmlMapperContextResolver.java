/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.resolver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/**
 * @author Ivica Cardic
 */
@Provider
public class XmlMapperContextResolver implements ContextResolver<XmlMapper> {

	@Override
	public XmlMapper getContext(Class<?> clazz) {
		return XmlMapperHolder._XML_MAPPER;
	}

	private static class XmlMapperHolder {

		private static final XmlMapper _XML_MAPPER = new XmlMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				registerModule(new JakartaXmlBindAnnotationModule());
				setDateFormat(new ISO8601DateFormat());
				setDefaultUseWrapper(false);
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			}
		};

	}

}