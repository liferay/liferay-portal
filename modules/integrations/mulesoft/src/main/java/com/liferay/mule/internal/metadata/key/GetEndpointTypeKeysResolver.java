/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.metadata.key;

import com.liferay.mule.internal.metadata.MetadataKeysBuilder;
import com.liferay.mule.internal.oas.constants.OASConstants;

import java.util.Set;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataKey;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.TypeKeysResolver;

/**
 * @author Matija Petanjek
 */
public class GetEndpointTypeKeysResolver implements TypeKeysResolver {

	@Override
	public String getCategoryName() {
		return "liferay-get";
	}

	@Override
	public Set<MetadataKey> getKeys(MetadataContext metadataContext)
		throws ConnectionException, MetadataResolvingException {

		return metadataKeysBuilder.buildEndpointMetadataKeys(
			metadataContext, OASConstants.OPERATION_GET);
	}

	private final MetadataKeysBuilder metadataKeysBuilder =
		new MetadataKeysBuilder();

}