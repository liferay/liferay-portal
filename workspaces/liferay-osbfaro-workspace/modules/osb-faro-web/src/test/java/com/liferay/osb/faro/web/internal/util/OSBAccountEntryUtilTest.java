/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntryBuilder;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.osb.faro.provisioning.client.model.display.main.FaroSubscriptionDisplay;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class OSBAccountEntryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testBuildFromPayload() {
		OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

		osbOfferingEntry.setProductEntryId(
			ProductConstants.ENTERPRISE_PRODUCT_ENTRY_ID);
		osbOfferingEntry.setQuantity(1);

		OSBAccountEntry osbAccountEntry = OSBAccountEntryBuilder.setName(
			"Corp Project Name"
		).setOfferingEntries(
			Collections.singletonList(osbOfferingEntry)
		).build();

		Assert.assertEquals("Corp Project Name", osbAccountEntry.getName());

		List<OSBOfferingEntry> offeringEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			offeringEntries.toString(), 1, offeringEntries.size());
		Assert.assertEquals(
			ProductConstants.ENTERPRISE_PRODUCT_ENTRY_ID,
			offeringEntries.get(
				0
			).getProductEntryId());
	}

	@Test
	public void testBuildFromStoredSubscription() throws Exception {
		OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

		osbOfferingEntry.setProductEntryId(
			ProductConstants.ENTERPRISE_PRODUCT_ENTRY_ID);
		osbOfferingEntry.setQuantity(1);
		osbOfferingEntry.setStartDate(new Date());
		osbOfferingEntry.setStatus(
			ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE);

		OSBAccountEntry pushedOSBAccountEntry = OSBAccountEntryBuilder.setName(
			"Corp Project Name"
		).setOfferingEntries(
			Collections.singletonList(osbOfferingEntry)
		).build();

		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getSubscription()
		).thenReturn(
			JSONUtil.writeValueAsString(
				new FaroSubscriptionDisplay(pushedOSBAccountEntry))
		);

		OSBAccountEntry osbAccountEntry = OSBAccountEntryUtil.build(
			faroProject);

		List<OSBOfferingEntry> offeringEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			offeringEntries.toString(), 1, offeringEntries.size());

		OSBOfferingEntry rebuiltOSBOfferingEntry = offeringEntries.get(0);

		Assert.assertEquals(
			ProductConstants.ENTERPRISE_PRODUCT_ENTRY_ID,
			rebuiltOSBOfferingEntry.getProductEntryId());
		Assert.assertEquals(1, rebuiltOSBOfferingEntry.getQuantity());
		Assert.assertEquals(
			ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE,
			rebuiltOSBOfferingEntry.getStatus());
		Assert.assertNotNull(rebuiltOSBOfferingEntry.getStartDate());
	}

}