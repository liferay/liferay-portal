/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.metadata.output;

import com.liferay.mule.internal.metadata.MetadataTypeBuilder;
import com.liferay.mule.internal.oas.constants.OASConstants;

import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

/**
 * @author Matija Petanjek
 */
public class PostEndpointOutputTypeResolver
	implements OutputTypeResolver<String> {

	@Override
	public String getCategoryName() {
		return "liferay-post";
	}

	@Override
	public MetadataType getOutputType(
			MetadataContext metadataContext, String endpoint)
		throws ConnectionException, MetadataResolvingException {

		return metadataTypeBuilder.buildMetadataType(
			metadataContext, endpoint, OASConstants.OPERATION_POST,
			OASConstants.
				PATH_RESPONSES_DEFAULT_CONTENT_APPLICATION_JSON_SCHEMA_PATTERN);
	}

	private final MetadataTypeBuilder metadataTypeBuilder =
		new MetadataTypeBuilder();

}