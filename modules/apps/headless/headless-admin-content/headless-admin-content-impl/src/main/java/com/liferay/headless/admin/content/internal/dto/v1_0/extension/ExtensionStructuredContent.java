/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.internal.dto.v1_0.extension;

import com.liferay.headless.admin.content.dto.v1_0.Version;
import com.liferay.headless.delivery.dto.v1_0.StructuredContent;

import jakarta.validation.constraints.NotNull;

/**
 * @author Luis Miguel Barcos
 */
public class ExtensionStructuredContent extends StructuredContent {

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	@NotNull
	protected Version version;

}