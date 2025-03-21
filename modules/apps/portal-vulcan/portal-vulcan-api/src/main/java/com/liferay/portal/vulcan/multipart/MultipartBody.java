/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.multipart;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.portal.kernel.util.StreamUtil;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Map;

/**
 * @author Javier Gamarra
 */
public class MultipartBody {

	public static MultipartBody of(
		Map<String, BinaryFile> binaryFiles,
		ObjectMapperProvider objectMapperProvider, Map<String, String> values) {

		return new MultipartBody(binaryFiles, objectMapperProvider, values);
	}

	public BinaryFile getBinaryFile(String key) {
		return _binaryFiles.get(key);
	}

	public byte[] getBinaryFileAsBytes(String key) throws IOException {
		BinaryFile binaryFile = getBinaryFile(key);

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		StreamUtil.transfer(binaryFile.getInputStream(), byteArrayOutputStream);

		return byteArrayOutputStream.toByteArray();
	}

	public <T> T getValueAsInstance(String key, Class<T> clazz)
		throws IOException {

		String valueAsString = getValueAsString(key);

		if (valueAsString == null) {
			throw new BadRequestException(
				"Missing JSON property with the key: " + key);
		}

		return _parseValue(valueAsString, clazz);
	}

	public <T> T getValueAsNullableInstance(String key, Class<T> clazz)
		throws IOException {

		String valueAsString = getValueAsString(key);

		if (valueAsString == null) {
			return null;
		}

		return _parseValue(valueAsString, clazz);
	}

	public String getValueAsString(String key) {
		if ((_values != null) && _values.containsKey(key)) {
			return _values.get(key);
		}

		return null;
	}

	public Map<String, String> getValues() {
		return _values;
	}

	public interface ObjectMapperProvider {

		public ObjectMapper provide(Class<?> clazz);

	}

	private MultipartBody(
		Map<String, BinaryFile> binaryFiles,
		ObjectMapperProvider objectMapperProvider, Map<String, String> values) {

		_binaryFiles = binaryFiles;
		_objectMapperProvider = objectMapperProvider;
		_values = values;
	}

	private <T> T _parseValue(String valueAsString, Class<T> clazz)
		throws IOException {

		ObjectMapper objectMapper = _objectMapperProvider.provide(clazz);

		if (objectMapper == null) {
			throw new InternalServerErrorException(
				"Unable to get object mapper for class " + clazz.getName());
		}

		return objectMapper.readValue(valueAsString, clazz);
	}

	private final Map<String, BinaryFile> _binaryFiles;
	private final ObjectMapperProvider _objectMapperProvider;
	private final Map<String, String> _values;

}