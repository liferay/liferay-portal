/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.rest.dto.v1_0.Campaign;
import com.liferay.osb.faro.rest.resource.v1_0.CampaignResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Nilton Vieira
 */
@RunWith(Arquillian.class)
public class CampaignResourceTest extends BaseCampaignResourceTestCase {

	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaign() throws Exception {
		com.liferay.osb.faro.engine.client.model.Campaign engineClientCampaign =
			new com.liferay.osb.faro.engine.client.model.Campaign();

		engineClientCampaign.setId(RandomTestUtil.randomString());

		long channelId = RandomTestUtil.randomLong();

		Object[] objects = _invoke(
			campaignResource -> {
				Campaign campaign =
					campaignResource.getWorkspaceGroupChannelCampaign(
						testGroup.getGroupId(), String.valueOf(channelId),
						engineClientCampaign.getId());

				Assert.assertEquals(
					engineClientCampaign.getId(), campaign.getId());
			},
			engineClientCampaign);

		Assert.assertNull(objects[1]);
		Assert.assertEquals(channelId, objects[2]);
		Assert.assertEquals(engineClientCampaign.getId(), objects[3]);
	}

	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignsPage() throws Exception {
		long channelId = RandomTestUtil.randomLong();
		String search = RandomTestUtil.randomString();

		Object[] objects = _getWorkspaceGroupChannelCampaignsPage(
			channelId, search, new Sort[] {new Sort("name", true)});

		Assert.assertEquals(channelId, objects[1]);
		Assert.assertNull(objects[2]);
		Assert.assertEquals(search, objects[3]);
		Assert.assertEquals(7, objects[5]);
		Assert.assertEquals("campaignName:desc", objects[7]);
		Assert.assertEquals(2, objects[8]);
		Assert.assertEquals(10, objects[9]);

		objects = _getWorkspaceGroupChannelCampaignsPage(
			channelId, search, new Sort[] {new Sort("accountsTouched", false)});

		Assert.assertEquals("accountsTouched:asc", objects[7]);

		objects = _getWorkspaceGroupChannelCampaignsPage(
			channelId, search, null);

		Assert.assertNull(objects[7]);
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignsPageWithPagination()
		throws Exception {

		super.testGetWorkspaceGroupChannelCampaignsPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignsPageWithSortDateTime()
		throws Exception {

		super.testGetWorkspaceGroupChannelCampaignsPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignsPageWithSortDouble()
		throws Exception {

		super.testGetWorkspaceGroupChannelCampaignsPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignsPageWithSortInteger()
		throws Exception {

		super.testGetWorkspaceGroupChannelCampaignsPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignsPageWithSortString()
		throws Exception {

		super.testGetWorkspaceGroupChannelCampaignsPageWithSortString();
	}

	private Object[] _getWorkspaceGroupChannelCampaignsPage(
			long channelId, String search, Sort[] sorts)
		throws Exception {

		return _invoke(
			campaignResource ->
				campaignResource.getWorkspaceGroupChannelCampaignsPage(
					testGroup.getGroupId(), String.valueOf(channelId), null,
					"LAST_7_DAYS", null, search, Pagination.of(2, 10), sorts),
			new Results<>());
	}

	private Object[] _invoke(
			UnsafeConsumer<CampaignResource, Exception> unsafeConsumer,
			Object returnValue)
		throws Exception {

		AtomicReference<Object[]> atomicReference = new AtomicReference<>();

		CampaignResource campaignResource = ReflectionTestUtil.getFieldValue(
			this, "_campaignResource");

		ContactsEngineClient contactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				campaignResource, "_contactsEngineClient",
				(ContactsEngineClient)ProxyUtil.newProxyInstance(
					ContactsEngineClient.class.getClassLoader(),
					new Class<?>[] {ContactsEngineClient.class},
					(proxy, method, arguments) -> {
						atomicReference.set(arguments);

						return returnValue;
					}));
		FaroProjectLocalService faroProjectLocalService =
			ReflectionTestUtil.getAndSetFieldValue(
				campaignResource, "_faroProjectLocalService",
				(FaroProjectLocalService)ProxyUtil.newProxyInstance(
					FaroProjectLocalService.class.getClassLoader(),
					new Class<?>[] {FaroProjectLocalService.class},
					(proxy, method, arguments) -> null));

		try {
			unsafeConsumer.accept(campaignResource);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				campaignResource, "_contactsEngineClient",
				contactsEngineClient);
			ReflectionTestUtil.setFieldValue(
				campaignResource, "_faroProjectLocalService",
				faroProjectLocalService);
		}

		return atomicReference.get();
	}

}