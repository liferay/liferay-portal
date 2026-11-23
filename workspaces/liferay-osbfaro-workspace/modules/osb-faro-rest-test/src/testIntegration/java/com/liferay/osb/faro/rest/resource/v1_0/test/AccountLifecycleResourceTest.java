/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.rest.resource.v1_0.AccountLifecycleResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
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
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class AccountLifecycleResourceTest
	extends BaseAccountLifecycleResourceTestCase {

	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPage()
		throws Exception {

		_testGetWorkspaceGroupChannelAccountLifecyclesPageWithDateModifiedSort();
		_testGetWorkspaceGroupChannelAccountLifecyclesPageWithSort();
		_testGetWorkspaceGroupChannelAccountLifecyclesPageWithoutSort();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPageWithPagination()
		throws Exception {

		super.testGetWorkspaceGroupChannelAccountLifecyclesPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortDateTime()
		throws Exception {

		super.
			testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortDouble()
		throws Exception {

		super.testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortInteger()
		throws Exception {

		super.
			testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortString()
		throws Exception {

		super.testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortString();
	}

	private Object[] _getWorkspaceGroupChannelAccountLifecyclesPage(
			String channelId, String search, Sort[] sorts)
		throws Exception {

		AtomicReference<Object[]> atomicReference = new AtomicReference<>();

		AccountLifecycleResource accountLifecycleResource =
			ReflectionTestUtil.getFieldValue(this, "_accountLifecycleResource");

		ContactsEngineClient contactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				accountLifecycleResource, "_contactsEngineClient",
				(ContactsEngineClient)ProxyUtil.newProxyInstance(
					ContactsEngineClient.class.getClassLoader(),
					new Class<?>[] {ContactsEngineClient.class},
					(proxy, method, arguments) -> {
						atomicReference.set(arguments);

						return new Results<>();
					}));
		FaroProjectLocalService faroProjectLocalService =
			ReflectionTestUtil.getAndSetFieldValue(
				accountLifecycleResource, "_faroProjectLocalService",
				(FaroProjectLocalService)ProxyUtil.newProxyInstance(
					FaroProjectLocalService.class.getClassLoader(),
					new Class<?>[] {FaroProjectLocalService.class},
					(proxy, method, arguments) -> null));

		try {
			accountLifecycleResource.
				getWorkspaceGroupChannelAccountLifecyclesPage(
					testGroup.getGroupId(), channelId, search,
					Pagination.of(2, 10), sorts);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				accountLifecycleResource, "_contactsEngineClient",
				contactsEngineClient);
			ReflectionTestUtil.setFieldValue(
				accountLifecycleResource, "_faroProjectLocalService",
				faroProjectLocalService);
		}

		return atomicReference.get();
	}

	private void _testGetWorkspaceGroupChannelAccountLifecyclesPageWithDateModifiedSort()
		throws Exception {

		long channelId = RandomTestUtil.randomLong();
		String search = RandomTestUtil.randomString();

		Object[] objects = _getWorkspaceGroupChannelAccountLifecyclesPage(
			String.valueOf(channelId), search,
			new Sort[] {new Sort("dateModified", false)});

		Assert.assertEquals(channelId, objects[1]);
		Assert.assertEquals(search, objects[4]);
		Assert.assertEquals(2, objects[5]);
		Assert.assertEquals(10, objects[6]);
		Assert.assertEquals("modifiedDate:asc", objects[7]);
	}

	private void _testGetWorkspaceGroupChannelAccountLifecyclesPageWithSort()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelAccountLifecyclesPage(
			String.valueOf(RandomTestUtil.randomLong()),
			RandomTestUtil.randomString(), new Sort[] {new Sort("id", true)});

		Assert.assertEquals("id:desc", objects[7]);
	}

	private void _testGetWorkspaceGroupChannelAccountLifecyclesPageWithoutSort()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelAccountLifecyclesPage(
			String.valueOf(RandomTestUtil.randomLong()),
			RandomTestUtil.randomString(), null);

		Assert.assertEquals("name:asc", objects[7]);
	}

}