/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type;

import com.liferay.client.extension.type.annotation.CETProperty;
import com.liferay.client.extension.type.annotation.CETType;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Antonio Ortega
 */
@CETType(description = "This is a description.", name = "fdsVisualizationMode")
@ProviderType
public interface FDSVisualizationModeCET extends CET {

	@CETProperty(
		defaultValue = "", name = "icon", type = CETProperty.Type.String
	)
	public String getIcon();

	@CETProperty(defaultValue = "", name = "url", type = CETProperty.Type.URL)
	public String getURL();

}