/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.liferay.osb.faro.engine.client.model.provider.CSVProvider;
import com.liferay.osb.faro.engine.client.model.provider.DemandbaseProvider;
import com.liferay.osb.faro.engine.client.model.provider.HubSpotProvider;
import com.liferay.osb.faro.engine.client.model.provider.LiferayProvider;
import com.liferay.osb.faro.engine.client.model.provider.MarketoCampaignProvider;
import com.liferay.osb.faro.engine.client.model.provider.MarketoProvider;
import com.liferay.osb.faro.engine.client.model.provider.SalesforceProvider;

/**
 * @author Matthew Kong
 */
@JsonSubTypes(
	{
		@JsonSubTypes.Type(name = CSVProvider.TYPE, value = CSVProvider.class),
		@JsonSubTypes.Type(
			name = DemandbaseProvider.TYPE, value = DemandbaseProvider.class
		),
		@JsonSubTypes.Type(
			name = HubSpotProvider.TYPE, value = HubSpotProvider.class
		),
		@JsonSubTypes.Type(
			name = LiferayProvider.TYPE, value = LiferayProvider.class
		),
		@JsonSubTypes.Type(
			name = MarketoCampaignProvider.TYPE,
			value = MarketoCampaignProvider.class
		),
		@JsonSubTypes.Type(
			name = MarketoProvider.TYPE, value = MarketoProvider.class
		),
		@JsonSubTypes.Type(
			name = SalesforceProvider.TYPE, value = SalesforceProvider.class
		)
	}
)
@JsonTypeInfo(
	include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type",
	use = JsonTypeInfo.Id.NAME
)
public interface Provider {

	public String getType();

}