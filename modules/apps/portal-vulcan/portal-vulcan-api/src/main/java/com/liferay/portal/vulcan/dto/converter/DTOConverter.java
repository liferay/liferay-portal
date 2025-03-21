/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.dto.converter;

import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Rubén Pulido
 * @author Víctor Galán
 */
public interface DTOConverter<E, D> {

	public String getContentType();

	public default String getDTOClassName() {
		Class<?> clazz = getClass();

		Type[] types = clazz.getGenericInterfaces();

		for (Type type : types) {
			String typeName = type.getTypeName();

			if (!typeName.contains(DTOConverter.class.getSimpleName())) {
				continue;
			}

			ParameterizedType parameterizedType = (ParameterizedType)type;

			Type[] argumentTypes = parameterizedType.getActualTypeArguments();

			return argumentTypes[0].getTypeName();
		}

		return null;
	}

	public default String getExternalDTOClassName() {
		Class<?> clazz = getClass();

		Type[] types = clazz.getGenericInterfaces();

		for (Type type : types) {
			String typeName = type.getTypeName();

			if (!typeName.contains(DTOConverter.class.getSimpleName())) {
				continue;
			}

			ParameterizedType parameterizedType = (ParameterizedType)type;

			Type[] argumentTypes = parameterizedType.getActualTypeArguments();

			return argumentTypes[1].getTypeName();
		}

		return null;
	}

	public default String getJaxRsLink(long classPK, UriInfo uriInfo) {
		return null;
	}

	public default E getObject(String externalReferenceCode) throws Exception {
		return null;
	}

	public default D toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		return toDTO(
			dtoConverterContext,
			getObject(String.valueOf(dtoConverterContext.getId())));
	}

	public default D toDTO(DTOConverterContext dtoConverterContext, E object)
		throws Exception {

		return null;
	}

	public default D toDTO(E object) throws Exception {
		return toDTO(null, object);
	}

}