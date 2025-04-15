/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import jakarta.portlet.ActionResponse;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Neil Griffin
 */
@ProviderType
public interface LiferayActionResponse
	extends ActionResponse, LiferayStateAwareResponse {

	public static final String SKIP_ESCAPE_REDIRECT =
		"LIFERAY_SKIP_ESCAPE_REDIRECT";

}