/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import com.liferay.mule.internal.error.LiferayError;

import java.io.IOException;

import java.util.Objects;

import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

/**
 * @author Matija Petanjek
 */
public class JsonNodeReader {

	public JsonNode fetchDescendantJsonNode(JsonNode jsonNode, String path) {
		JsonNode descendantJsonNode = jsonNode;

		String[] pathParts = path.split(">");

		for (String pathPart : pathParts) {
			descendantJsonNode = descendantJsonNode.get(pathPart);

			if (descendantJsonNode == null) {
				return NullNode.getInstance();
			}
		}

		return descendantJsonNode;
	}

	public JsonNode fromHttpResponse(HttpResponse httpResponse)
		throws ModuleException {

		HttpEntity httpEntity = httpResponse.getEntity();

		try {
			return objectMapper.readTree(httpEntity.getContent());
		}
		catch (IOException ioException) {
			throw new ModuleException(
				ioException.getMessage(), LiferayError.EXECUTION, ioException);
		}
	}

	public JsonNode getDescendantJsonNode(JsonNode jsonNode, String path) {
		JsonNode descendantJsonNode = jsonNode;

		String[] pathParts = path.split(">");

		for (String pathPart : pathParts) {
			descendantJsonNode = descendantJsonNode.get(pathPart);

			Objects.requireNonNull(descendantJsonNode);
		}

		return descendantJsonNode;
	}

	public boolean hasPath(JsonNode jsonNode, String path) {
		JsonNode descendantJsonNode = jsonNode;

		String[] pathParts = path.split(">");

		for (String pathPart : pathParts) {
			descendantJsonNode = descendantJsonNode.get(pathPart);

			if (descendantJsonNode == null) {
				return false;
			}
		}

		return true;
	}

	private final ObjectMapper objectMapper = new ObjectMapper();

}