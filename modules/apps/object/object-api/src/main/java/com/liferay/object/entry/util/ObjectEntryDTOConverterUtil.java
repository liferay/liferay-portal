/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.entry.util;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.ws.rs.InternalServerErrorException;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryDTOConverterUtil {

	public static DTOConverter<BaseModel<?>, ?> getDTOConverter(
			DTOConverterRegistry dtoConverterRegistry,
			SystemObjectDefinitionManager systemObjectDefinitionManager)
		throws Exception {

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			systemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		DTOConverter<BaseModel<?>, ?> dtoConverter =
			(DTOConverter<BaseModel<?>, ?>)dtoConverterRegistry.getDTOConverter(
				jaxRsApplicationDescriptor.getApplicationName(),
				systemObjectDefinitionManager.getModelClassName(),
				jaxRsApplicationDescriptor.getVersion());

		if (dtoConverter == null) {
			throw new InternalServerErrorException(
				"No DTO converter found for " +
					systemObjectDefinitionManager.getModelClassName());
		}

		return dtoConverter;
	}

	public static Object toDTO(
			BaseModel<?> baseModel, DTOConverterRegistry dtoConverterRegistry,
			SystemObjectDefinitionManager systemObjectDefinitionManager,
			User user)
		throws Exception {

		DTOConverter<BaseModel<?>, ?> dtoConverter = getDTOConverter(
			dtoConverterRegistry, systemObjectDefinitionManager);

		Locale locale = null;

		if (user != null) {
			locale = user.getLocale();
		}

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				false, Collections.emptyMap(), dtoConverterRegistry,
				baseModel.getPrimaryKeyObj(), locale, null, user);

		return dtoConverter.toDTO(defaultDTOConverterContext);
	}

	public static String toDTO(
			DTOConverterRegistry dtoConverterRegistry, JSONFactory jsonFactory,
			ObjectEntry objectEntry, User user)
		throws Exception {

		DTOConverter<ObjectEntry, ?> dtoConverter =
			(DTOConverter<ObjectEntry, ?>)dtoConverterRegistry.getDTOConverter(
				ObjectEntry.class.getName());

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				false, null, dtoConverterRegistry, null, user.getLocale(), null,
				user);

		Object dto = dtoConverter.toDTO(
			defaultDTOConverterContext, objectEntry);

		JSONObject dtoJSONObject = jsonFactory.createJSONObject(dto.toString());

		dtoJSONObject.remove("actions");
		dtoJSONObject.remove("creator");
		dtoJSONObject.remove("dateCreated");
		dtoJSONObject.remove("dateModified");
		dtoJSONObject.remove("id");
		dtoJSONObject.remove("status");
		dtoJSONObject.remove("version");

		return dtoJSONObject.toString();
	}

	public static Map<String, Object> toValues(
		BaseModel<?> baseModel, DTOConverterRegistry dtoConverterRegistry,
		String objectDefinitionName,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry,
		User user) {

		try {
			Object dto = toDTO(
				baseModel, dtoConverterRegistry,
				systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(objectDefinitionName),
				user);

			if (dto == null) {
				return Collections.emptyMap();
			}

			return ObjectMapperUtil.readValue(Map.class, dto.toString());
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Collections.emptyMap();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryDTOConverterUtil.class);

}