/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.view.cards;

import com.liferay.frontend.data.set.view.FDSSchemaLabelField;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Mikel Lorza
 */
@ProviderType
public interface FDSCardSchemaBuilder {

	public FDSCardSchemaBuilder add(FDSSchemaLabelField fdsSchemaLabelField);

	public FDSCardSchemaBuilder add(
		String displayTypeKey, Map<String, String> displayTypeValues,
		String value);

	public FDSCardSchemaBuilder add(String displayType, String value);

	public FDSCardSchema build();

}