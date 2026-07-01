/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model.provider;

import com.liferay.osb.faro.engine.client.model.ChannelsConfiguration;
import com.liferay.osb.faro.engine.client.model.Provider;

/**
 * @author Matthew Kong
 */
public class SalesforceProvider implements Provider {

	public static final String TYPE = "SALESFORCE";

	public AccountsConfiguration getAccountsConfiguration() {
		return _accountsConfiguration;
	}

	public CampaignsConfiguration getCampaignsConfiguration() {
		return _campaignsConfiguration;
	}

	public ChannelsConfiguration getChannelsConfiguration() {
		return _channelsConfiguration;
	}

	public ContactsConfiguration getContactsConfiguration() {
		return _contactsConfiguration;
	}

	public OpportunitiesConfiguration getOpportunitiesConfiguration() {
		return _opportunitiesConfiguration;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	public void setAccountsConfiguration(
		AccountsConfiguration accountsConfiguration) {

		_accountsConfiguration = accountsConfiguration;
	}

	public void setCampaignsConfiguration(
		CampaignsConfiguration campaignsConfiguration) {

		_campaignsConfiguration = campaignsConfiguration;
	}

	public void setChannelsConfiguration(
		ChannelsConfiguration channelsConfiguration) {

		_channelsConfiguration = channelsConfiguration;
	}

	public void setContactsConfiguration(
		ContactsConfiguration contactsConfiguration) {

		_contactsConfiguration = contactsConfiguration;
	}

	public void setOpportunitiesConfiguration(
		OpportunitiesConfiguration opportunitiesConfiguration) {

		_opportunitiesConfiguration = opportunitiesConfiguration;
	}

	public static class AccountsConfiguration {

		public boolean isEnableAllAccounts() {
			return _enableAllAccounts;
		}

		public void setEnableAllAccounts(boolean enableAllAccounts) {
			_enableAllAccounts = enableAllAccounts;
		}

		private boolean _enableAllAccounts;

	}

	public static class CampaignsConfiguration {

		public boolean isEnableAllCampaigns() {
			return _enableAllCampaigns;
		}

		public void setEnableAllCampaigns(boolean enableAllCampaigns) {
			_enableAllCampaigns = enableAllCampaigns;
		}

		private boolean _enableAllCampaigns;

	}

	public static class ContactsConfiguration {

		public boolean isEnableAllContacts() {
			return _enableAllContacts;
		}

		public boolean isEnableAllLeads() {
			return _enableAllLeads;
		}

		public void setEnableAllContacts(boolean enableAllContacts) {
			_enableAllContacts = enableAllContacts;
		}

		public void setEnableAllLeads(boolean enableAllLeads) {
			_enableAllLeads = enableAllLeads;
		}

		private boolean _enableAllContacts;
		private boolean _enableAllLeads;

	}

	public static class OpportunitiesConfiguration {

		public boolean isEnableAllOpportunities() {
			return _enableAllOpportunities;
		}

		public void setEnableAllOpportunities(boolean enableAllOpportunities) {
			_enableAllOpportunities = enableAllOpportunities;
		}

		private boolean _enableAllOpportunities;

	}

	private AccountsConfiguration _accountsConfiguration;
	private CampaignsConfiguration _campaignsConfiguration;
	private ChannelsConfiguration _channelsConfiguration;
	private ContactsConfiguration _contactsConfiguration;
	private OpportunitiesConfiguration _opportunitiesConfiguration;

}