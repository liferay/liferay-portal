/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.omni.search;

import java.util.List;

/**
 * @author Thiago Buarque
 */
public interface OmniSearchResultProviderRegistry {

	public List<OmniSearchResultProvider> getOmniSearchResultProviders();

}