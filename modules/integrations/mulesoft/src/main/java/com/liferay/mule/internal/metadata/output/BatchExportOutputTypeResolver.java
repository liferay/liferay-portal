/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.metadata.output;

import com.liferay.mule.internal.metadata.MetadataTypeBuilder;

import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

/**
 * @author Matija Petanjek
 */
public class BatchExportOutputTypeResolver
	implements OutputTypeResolver<String> {

	@Override
	public String getCategoryName() {
		return "liferay-batch";
	}

	@Override
	public MetadataType getOutputType(
			MetadataContext metadataContext, String className)
		throws ConnectionException, MetadataResolvingException {

		return metadataTypeBuilder.buildBatchMetadataType(
			metadataContext, className);
	}

	private final MetadataTypeBuilder metadataTypeBuilder =
		new MetadataTypeBuilder();

}