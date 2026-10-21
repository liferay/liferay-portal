/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class IndividualResourceTest extends BaseIndividualResourceTestCase {

	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPage() throws Exception {
		_testGetWorkspaceGroupChannelIndividualsPageWithProfileTypeAndSort();
		_testGetWorkspaceGroupChannelIndividualsPageWithoutProfileType();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithPagination()
		throws Exception {

		super.testGetWorkspaceGroupChannelIndividualsPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortDateTime()
		throws Exception {

		super.testGetWorkspaceGroupChannelIndividualsPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortDouble()
		throws Exception {

		super.testGetWorkspaceGroupChannelIndividualsPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortInteger()
		throws Exception {

		super.testGetWorkspaceGroupChannelIndividualsPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortString()
		throws Exception {

		super.testGetWorkspaceGroupChannelIndividualsPageWithSortString();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupIndividual() throws Exception {
		super.testGetWorkspaceGroupIndividual();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetWorkspaceGroupIndividual() throws Exception {
		super.testGraphQLGetWorkspaceGroupIndividual();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetWorkspaceGroupIndividualNotFound()
		throws Exception {

		super.testGraphQLGetWorkspaceGroupIndividualNotFound();
	}

	private Object[] _getWorkspaceGroupChannelIndividualsPage(
			String profileType, Sort[] sorts)
		throws Exception {

		AtomicReference<Object[]> argumentsAtomicReference =
			new AtomicReference<>();

		IndividualResource individualResource =
			ReflectionTestUtil.getFieldValue(this, "_individualResource");

		ContactsEngineClient contactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				individualResource, "_contactsEngineClient",
				(ContactsEngineClient)ProxyUtil.newProxyInstance(
					ContactsEngineClient.class.getClassLoader(),
					new Class<?>[] {ContactsEngineClient.class},
					(proxy, method, arguments) -> {
						argumentsAtomicReference.set(arguments);

						return new Results<>();
					}));
		FaroProjectLocalService faroProjectLocalService =
			ReflectionTestUtil.getAndSetFieldValue(
				individualResource, "_faroProjectLocalService",
				(FaroProjectLocalService)ProxyUtil.newProxyInstance(
					FaroProjectLocalService.class.getClassLoader(),
					new Class<?>[] {FaroProjectLocalService.class},
					(proxy, method, arguments) -> null));

		try {
			individualResource.getWorkspaceGroupChannelIndividualsPage(
				testGroup.getGroupId(), RandomTestUtil.randomString(), null,
				null, null, null, profileType, null, null, null, null,
				Pagination.of(1, 20), sorts);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				individualResource, "_contactsEngineClient",
				contactsEngineClient);
			ReflectionTestUtil.setFieldValue(
				individualResource, "_faroProjectLocalService",
				faroProjectLocalService);
		}

		return argumentsAtomicReference.get();
	}

	private void _testGetWorkspaceGroupChannelIndividualsPageWithProfileTypeAndSort()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelIndividualsPage(
			"KNOWN",
			new Sort[] {new Sort("activitiesCount", Sort.LONG_TYPE, true)});

		Assert.assertEquals(Collections.singletonList("KNOWN"), objects[10]);

		List<OrderByField> orderByFields = (List<OrderByField>)objects[19];

		Assert.assertEquals(orderByFields.toString(), 1, orderByFields.size());

		OrderByField orderByField = orderByFields.get(0);

		Assert.assertEquals("activitiesCount", orderByField.getFieldName());
		Assert.assertEquals(
			OrderByField.OrderBy.desc, orderByField.getOrderBy());
		Assert.assertTrue(orderByField.isSystem());
	}

	private void _testGetWorkspaceGroupChannelIndividualsPageWithoutProfileType()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelIndividualsPage(null, null);

		Assert.assertNull(objects[10]);
		Assert.assertNull(objects[19]);
	}

}