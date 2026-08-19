/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.internal.ProvisioningClientImpl;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Marcos Martins
 */
public class ProjectFaroControllerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(
			_projectFaroController, "_provisioningClient",
			new ProvisioningClientImpl());
	}

	@Test
	public void testCreateOSBAccountEntry1() throws Exception {
		OSBAccountEntry osbAccountEntry =
			_projectFaroController.createOSBAccountEntry(false);

		List<OSBOfferingEntry> offeringEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			offeringEntries.toString(), 1, offeringEntries.size());

		OSBOfferingEntry osbOfferingEntry = offeringEntries.get(0);

		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE,
			osbOfferingEntry.getProductEntryId());
		Assert.assertEquals(1, osbOfferingEntry.getQuantity());
		Assert.assertNotNull(osbOfferingEntry.getStartDate());
	}

	@Test
	public void testCreateOSBAccountEntry2() throws Exception {
		_assert("1-BusinessTest", ProductConstants.PRODUCT_ENTRY_ID_BUSINESS);
		_assert(
			"2-BusinessLXCTest",
			ProductConstants.PRODUCT_ENTRY_ID_LXC_BUSINESS);
		_assert(
			"3-EnterpriseTest", ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE);
		_assert(
			"4-EnterpriseLXCTest",
			ProductConstants.PRODUCT_ENTRY_ID_LXC_ENTERPRISE);
		_assert("5-ProLXCTest", ProductConstants.PRODUCT_ENTRY_ID_LXC_PRO);
	}

	private void _assert(String corpProjectUuid, String productEntryId)
		throws Exception {

		OSBAccountEntry osbAccountEntry =
			_projectFaroController.getOSBAccountEntry(corpProjectUuid);

		List<OSBOfferingEntry> offeringEntries =
			osbAccountEntry.getOfferingEntries();

		Assert.assertEquals(
			offeringEntries.toString(), 1, offeringEntries.size());

		OSBOfferingEntry osbOfferingEntry = offeringEntries.get(0);

		Assert.assertEquals(
			productEntryId, osbOfferingEntry.getProductEntryId());
		Assert.assertEquals(1, osbOfferingEntry.getQuantity());
		Assert.assertNull(osbOfferingEntry.getStartDate());
		Assert.assertEquals(
			ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE,
			osbOfferingEntry.getStatus());
	}

	private final ProjectFaroController _projectFaroController =
		new ProjectFaroController();

}