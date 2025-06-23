/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPOptionCategoryLocalService;
import com.liferay.commerce.product.service.CPSpecificationOptionListTypeDefinitionRelLocalService;
import com.liferay.commerce.product.service.CPSpecificationOptionLocalService;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class ListTypeDefinitionResourceTest
	extends BaseListTypeDefinitionResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				_user.getUserId());

		CPOptionCategory cpOptionCategory =
			_cpOptionCategoryLocalService.addCPOptionCategory(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomDouble(), RandomTestUtil.randomString(),
				serviceContext);

		_cpSpecificationOption =
			_cpSpecificationOptionLocalService.addCPSpecificationOption(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				cpOptionCategory.getCPOptionCategoryId(), null,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true,
				RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
				true, serviceContext);
	}

	@Override
	@Test
	public void testPostSpecificationIdListTypeDefinition() throws Exception {
		super.testPostSpecificationIdListTypeDefinition();

		Assert.assertFalse(
			ListUtil.isEmpty(
				_cpSpecificationOptionListTypeDefinitionRelLocalService.
					getCPSpecificationOptionListTypeDefinitionRels(
						_cpSpecificationOption.getCPSpecificationOptionId())));
	}

	@Override
	@Test
	public void testPostSpecificationListTypeDefinition() throws Exception {
		com.liferay.list.type.model.ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				RandomTestUtil.randomString(), _user.getUserId(), false);

		assertHttpResponseStatusCode(
			204,
			listTypeDefinitionResource.
				postSpecificationListTypeDefinitionHttpResponse(
					_cpSpecificationOption.getCPSpecificationOptionId(),
					listTypeDefinition.getListTypeDefinitionId()));

		assertHttpResponseStatusCode(
			404,
			listTypeDefinitionResource.
				postSpecificationListTypeDefinitionHttpResponse(
					_cpSpecificationOption.getCPSpecificationOptionId(), 0L));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name"};
	}

	@Override
	protected ListTypeDefinition
			testDeleteSpecificationListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected Long
			testDeleteSpecificationListTypeDefinition_getSpecificationId()
		throws Exception {

		return _cpSpecificationOption.getCPSpecificationOptionId();
	}

	@Override
	protected ListTypeDefinition
			testGetSpecificationIdListTypeDefinitionsPage_addListTypeDefinition(
				Long id, ListTypeDefinition listTypeDefinition)
		throws Exception {

		return listTypeDefinitionResource.postSpecificationIdListTypeDefinition(
			id, listTypeDefinition);
	}

	@Override
	protected Long testGetSpecificationIdListTypeDefinitionsPage_getId()
		throws Exception {

		return _cpSpecificationOption.getCPSpecificationOptionId();
	}

	@Override
	protected ListTypeDefinition
			testPostSpecificationIdListTypeDefinition_addListTypeDefinition(
				ListTypeDefinition listTypeDefinition)
		throws Exception {

		return _addListTypeDefinition(listTypeDefinition);
	}

	@Override
	protected ListTypeDefinition
			testPostSpecificationListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	private ListTypeDefinition _addListTypeDefinition(
			ListTypeDefinition listTypeDefinition)
		throws Exception {

		return listTypeDefinitionResource.postSpecificationIdListTypeDefinition(
			_cpSpecificationOption.getCPSpecificationOptionId(),
			listTypeDefinition);
	}

	@Inject
	private CPOptionCategoryLocalService _cpOptionCategoryLocalService;

	@DeleteAfterTestRun
	private CPSpecificationOption _cpSpecificationOption;

	@Inject
	private CPSpecificationOptionListTypeDefinitionRelLocalService
		_cpSpecificationOptionListTypeDefinitionRelLocalService;

	@Inject
	private CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@DeleteAfterTestRun
	private User _user;

}