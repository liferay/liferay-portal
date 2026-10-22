/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.provisioning.client.model;

import com.liferay.osb.faro.provisioning.client.constants.KoroneikiConstants;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.Account;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ExternalLink;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ProductPurchase;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rachael Koestartyo
 */
public class OSBAccountEntryTest {

	@Test
	public void testGetSalesforceAccountId() {
		OSBAccountEntry osbAccountEntry = new OSBAccountEntry(
			_getAccount(
				_getExternalLink(
					KoroneikiConstants.DOMAIN_SALESFORCE, "salesforceAccountId",
					KoroneikiConstants.ENTITY_NAME_ACCOUNT)));

		Assert.assertEquals(
			"salesforceAccountId", osbAccountEntry.getSalesforceAccountId());
	}

	@Test
	public void testGetSalesforceAccountIdWithOtherEntityName() {
		OSBAccountEntry osbAccountEntry = new OSBAccountEntry(
			_getAccount(
				_getExternalLink(
					KoroneikiConstants.DOMAIN_SALESFORCE,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())));

		Assert.assertNull(osbAccountEntry.getSalesforceAccountId());
	}

	@Test
	public void testGetSalesforceAccountIdWithoutExternalLinks() {
		OSBAccountEntry osbAccountEntry = new OSBAccountEntry(_getAccount());

		Assert.assertNull(osbAccountEntry.getSalesforceAccountId());
	}

	private Account _getAccount(ExternalLink... externalLinks) {
		Account account = new Account();

		account.setExternalLinks(externalLinks);
		account.setProductPurchases(new ProductPurchase[0]);

		return account;
	}

	private ExternalLink _getExternalLink(
		String domain, String entityId, String entityName) {

		ExternalLink externalLink = new ExternalLink();

		externalLink.setDomain(domain);
		externalLink.setEntityId(entityId);
		externalLink.setEntityName(entityName);

		return externalLink;
	}

}