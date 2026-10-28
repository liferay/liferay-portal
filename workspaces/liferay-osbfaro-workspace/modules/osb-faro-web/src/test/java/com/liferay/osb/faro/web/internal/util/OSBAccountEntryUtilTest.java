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
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class OSBAccountEntryUtilTest {

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "OSB_FARO_SUBSCRIPTION_PUSH_ENABLED", false);
	}

	@Test
	public void testBuildFromPayload() {
		OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

		osbOfferingEntry.setProductEntryId(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE);
		osbOfferingEntry.setQuantity(1);

		OSBAccountEntry osbAccountEntry = OSBAccountEntryBuilder.setName(
			"Corp Project Name"
		).setOfferingEntries(
			Collections.singletonList(osbOfferingEntry)
		).build();

		Assert.assertEquals("Corp Project Name", osbAccountEntry.getName());

		List<OSBOfferingEntry> osbOfferingEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			osbOfferingEntries.toString(), 1, osbOfferingEntries.size());

		OSBOfferingEntry rebuiltOSBOfferingEntry = osbOfferingEntries.get(0);

		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE,
			rebuiltOSBOfferingEntry.getProductEntryId());
	}

	@Test
	public void testBuildFromStoredSubscription() throws Exception {
		OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

		osbOfferingEntry.setProductEntryId(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE);
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

		List<OSBOfferingEntry> osbOfferingEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			osbOfferingEntries.toString(), 1, osbOfferingEntries.size());

		OSBOfferingEntry rebuiltOSBOfferingEntry = osbOfferingEntries.get(0);

		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE,
			rebuiltOSBOfferingEntry.getProductEntryId());
		Assert.assertEquals(1, rebuiltOSBOfferingEntry.getQuantity());
		Assert.assertEquals(
			ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE,
			rebuiltOSBOfferingEntry.getStatus());
		Assert.assertNotNull(rebuiltOSBOfferingEntry.getStartDate());
	}

	@Test
	public void testBuildFromStoredSubscriptionWithSubscriptionPushEnabled()
		throws Exception {

		OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

		osbOfferingEntry.setProductEntryId(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE);
		osbOfferingEntry.setQuantity(1);
		osbOfferingEntry.setStartDate(new Date());
		osbOfferingEntry.setStatus(
			ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE);

		OSBOfferingEntry contactsOSBOfferingEntry = new OSBOfferingEntry();

		contactsOSBOfferingEntry.setProductEntryId(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE_CONTACTS);
		contactsOSBOfferingEntry.setQuantity(RandomTestUtil.randomInt());

		OSBAccountEntry koroneikiOSBAccountEntry =
			OSBAccountEntryBuilder.setName(
				RandomTestUtil.randomString()
			).setOfferingEntries(
				Arrays.asList(osbOfferingEntry, contactsOSBOfferingEntry)
			).build();

		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getSubscription()
		).thenReturn(
			JSONUtil.writeValueAsString(
				new FaroSubscriptionDisplay(koroneikiOSBAccountEntry))
		);

		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "OSB_FARO_SUBSCRIPTION_PUSH_ENABLED", true);

		OSBAccountEntry osbAccountEntry = OSBAccountEntryUtil.build(
			faroProject);

		List<OSBOfferingEntry> osbOfferingEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			osbOfferingEntries.toString(), 1, osbOfferingEntries.size());

		OSBOfferingEntry rebuiltOSBOfferingEntry = osbOfferingEntries.get(0);

		Assert.assertEquals(
			ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE,
			rebuiltOSBOfferingEntry.getProductEntryId());
	}

}