/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.provisioning.client.model.display.main;

import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Marcos Martins
 */
public class FaroSubscriptionDisplayTest {

	@Test
	public void testGetProductEntryId() {
		OSBAccountEntry osbAccountEntry = new OSBAccountEntry();

		OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

		osbOfferingEntry.setProductEntryId(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE);
		osbOfferingEntry.setQuantity(1);
		osbOfferingEntry.setStatus(
			ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE);

		osbAccountEntry.setOfferingEntries(
			Collections.singletonList(osbOfferingEntry));

		FaroSubscriptionDisplay faroSubscriptionDisplay =
			new FaroSubscriptionDisplay(osbAccountEntry);

		Assert.assertEquals(
			ProductConstants.getProductName(
				ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE),
			faroSubscriptionDisplay.getName());
		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE,
			faroSubscriptionDisplay.getProductEntryId());
	}

}