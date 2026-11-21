/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.connection.helper;

import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;

import com.liferay.portal.search.elasticsearch8.internal.settings.SettingsHelper;

/**
 * @author André de Oliveira
 */
public interface IndexCreationHelper {

	public void contribute(CreateIndexRequest.Builder builder);

	public void contributeIndexSettings(SettingsHelper settingsHelper);

	public void whenIndexCreated(String indexName);

}