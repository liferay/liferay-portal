/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.view.cards;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Mikel Lorza
 */
@ProviderType
public interface FDSCardSchemaBuilderFactory {

	public FDSCardSchemaBuilder create();

}