/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model.provider;

import com.liferay.osb.faro.engine.client.model.ChannelsConfiguration;
import com.liferay.osb.faro.engine.client.model.Provider;

/**
 * @author Rachael Koestartyo
 */
public class MarketoCampaignProvider implements Provider {

	public static final String TYPE = "MARKETO_CAMPAIGN";

	public ChannelsConfiguration getChannelsConfiguration() {
		return _channelsConfiguration;
	}

	public ContactsConfiguration getContactsConfiguration() {
		return _contactsConfiguration;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	public void setChannelsConfiguration(
		ChannelsConfiguration channelsConfiguration) {

		_channelsConfiguration = channelsConfiguration;
	}

	public void setContactsConfiguration(
		ContactsConfiguration contactsConfiguration) {

		_contactsConfiguration = contactsConfiguration;
	}

	public static class ContactsConfiguration {

		public boolean isEnableAllLeads() {
			return _enableAllLeads;
		}

		public void setEnableAllLeads(boolean enableAllLeads) {
			_enableAllLeads = enableAllLeads;
		}

		private boolean _enableAllLeads;

	}

	private ChannelsConfiguration _channelsConfiguration;
	private ContactsConfiguration _contactsConfiguration;

}