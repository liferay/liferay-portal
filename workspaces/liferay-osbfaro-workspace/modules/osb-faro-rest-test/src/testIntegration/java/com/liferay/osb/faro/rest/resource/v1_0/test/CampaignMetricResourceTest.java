/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.rest.resource.v1_0.CampaignMetricResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Nilton Vieira
 */
@RunWith(Arquillian.class)
public class CampaignMetricResourceTest
	extends BaseCampaignMetricResourceTestCase {

	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignMetricsPage()
		throws Exception {

		long channelId = RandomTestUtil.randomLong();

		Object[] objects = _getWorkspaceGroupChannelCampaignMetricsPage(
			channelId, "accountsTouched", "closedWonAmount");

		Assert.assertNull(objects[1]);
		Assert.assertEquals(channelId, objects[2]);
		Assert.assertEquals(90, objects[4]);
		Assert.assertEquals("accountsTouched,closedWonAmount", objects[6]);

		Assert.assertNull(
			_getWorkspaceGroupChannelCampaignMetricsPage(
				channelId, (String[])null)[6]);
	}

	private Object[] _getWorkspaceGroupChannelCampaignMetricsPage(
			long channelId, String... selectedMetrics)
		throws Exception {

		AtomicReference<Object[]> atomicReference = new AtomicReference<>();

		CampaignMetricResource campaignMetricResource =
			ReflectionTestUtil.getFieldValue(this, "_campaignMetricResource");

		ContactsEngineClient contactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				campaignMetricResource, "_contactsEngineClient",
				(ContactsEngineClient)ProxyUtil.newProxyInstance(
					ContactsEngineClient.class.getClassLoader(),
					new Class<?>[] {ContactsEngineClient.class},
					(proxy, method, arguments) -> {
						atomicReference.set(arguments);

						return Collections.emptyList();
					}));
		FaroProjectLocalService faroProjectLocalService =
			ReflectionTestUtil.getAndSetFieldValue(
				campaignMetricResource, "_faroProjectLocalService",
				(FaroProjectLocalService)ProxyUtil.newProxyInstance(
					FaroProjectLocalService.class.getClassLoader(),
					new Class<?>[] {FaroProjectLocalService.class},
					(proxy, method, arguments) -> null));

		try {
			campaignMetricResource.getWorkspaceGroupChannelCampaignMetricsPage(
				testGroup.getGroupId(), String.valueOf(channelId), null,
				"LAST_90_DAYS", null, selectedMetrics);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				campaignMetricResource, "_contactsEngineClient",
				contactsEngineClient);
			ReflectionTestUtil.setFieldValue(
				campaignMetricResource, "_faroProjectLocalService",
				faroProjectLocalService);
		}

		return atomicReference.get();
	}

}