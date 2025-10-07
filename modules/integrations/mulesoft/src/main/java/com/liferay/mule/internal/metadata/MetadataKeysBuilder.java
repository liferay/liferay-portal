/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.metadata;

import com.fasterxml.jackson.databind.JsonNode;

import com.liferay.mule.internal.connection.LiferayConnection;
import com.liferay.mule.internal.oas.constants.OASConstants;
import com.liferay.mule.internal.util.JsonNodeReader;

import java.io.IOException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataKey;
import org.mule.runtime.api.metadata.MetadataKeyBuilder;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.FailureCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matija Petanjek
 */
public class MetadataKeysBuilder {

	public Set<MetadataKey> buildClassNameMetadataKeys(
			MetadataContext metadataContext)
		throws ConnectionException, MetadataResolvingException {

		Set<MetadataKey> metadataKeys = new HashSet<>();

		JsonNode schemasJsonNode = jsonNodeReader.getDescendantJsonNode(
			getOASJsonNode(metadataContext),
			OASConstants.PATH_COMPONENTS_SCHEMAS);

		Iterator<Map.Entry<String, JsonNode>> iterator =
			schemasJsonNode.fields();

		while (iterator.hasNext()) {
			Map.Entry<String, JsonNode> entry = iterator.next();

			JsonNode schemaJsonNode = entry.getValue();

			JsonNode classNameJsonNode = jsonNodeReader.fetchDescendantJsonNode(
				schemaJsonNode,
				OASConstants.PATH_PROPERTIES_X_CLASS_NAME_DEFAULT);

			if (!classNameJsonNode.isNull()) {
				MetadataKeyBuilder metadataKeyBuilder =
					MetadataKeyBuilder.newKey(classNameJsonNode.asText());

				metadataKeys.add(metadataKeyBuilder.build());
			}
		}

		return metadataKeys;
	}

	public Set<MetadataKey> buildEndpointMetadataKeys(
			MetadataContext metadataContext, String operation)
		throws ConnectionException, MetadataResolvingException {

		Set<MetadataKey> metadataKeys = new HashSet<>();

		JsonNode oasJsonNode = getOASJsonNode(metadataContext);

		JsonNode pathsJsonNode = oasJsonNode.get(OASConstants.PATHS);

		Iterator<Map.Entry<String, JsonNode>> pathsIterator =
			pathsJsonNode.fields();

		while (pathsIterator.hasNext()) {
			Map.Entry<String, JsonNode> entry = pathsIterator.next();

			JsonNode pathJsonNode = entry.getValue();

			if (pathJsonNode.has(operation)) {
				String path = entry.getKey();

				MetadataKeyBuilder metadataKeyBuilder =
					MetadataKeyBuilder.newKey(path);

				metadataKeys.add(metadataKeyBuilder.build());
			}
		}

		return metadataKeys;
	}

	protected JsonNode getOASJsonNode(MetadataContext metadataContext)
		throws ConnectionException, MetadataResolvingException {

		Optional<LiferayConnection> liferayConnectionOptional =
			metadataContext.getConnection();

		try {
			LiferayConnection liferayConnection =
				liferayConnectionOptional.get();

			return jsonNodeReader.fromHttpResponse(
				liferayConnection.getOpenAPISpecHttpResponse());
		}
		catch (IOException ioException) {
			logger.error(
				"Unable to read OpenAPI document from Liferay Portal instance",
				ioException);

			throw new MetadataResolvingException(
				ioException.getMessage(), FailureCode.NO_DYNAMIC_KEY_AVAILABLE,
				ioException);
		}
		catch (TimeoutException timeoutException) {
			logger.error(
				"Unable to establish connection to Liferay Portal instance",
				timeoutException);

			throw new MetadataResolvingException(
				timeoutException.getMessage(), FailureCode.CONNECTION_FAILURE,
				timeoutException);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(
		MetadataKeysBuilder.class);

	private final JsonNodeReader jsonNodeReader = new JsonNodeReader();

}