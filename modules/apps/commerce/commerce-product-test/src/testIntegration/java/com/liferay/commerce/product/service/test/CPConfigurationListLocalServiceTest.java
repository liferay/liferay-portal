/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.CPConfigurationListParentCPConfigurationListGroupIdException;
import com.liferay.commerce.product.exception.RequiredCPConfigurationListException;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CPConfigurationListLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());

		_commerceCatalog = _commerceCatalogService.addCommerceCatalog(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), "USD", "en_US", _serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		_cpConfigurationListLocalService.deleteCPConfigurationLists(
			_serviceContext.getCompanyId());
	}

	@Test
	public void testAddCPConfigurationList() throws Exception {
		frutillaRule.scenario(
			"Add Product Configuration List"
		).given(
			"There is a Commerce Catalog"
		).when(
			"A Configuration List is added"
		).then(
			"The Configuration List is created"
		);

		String externalReferenceCode = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		CPConfigurationList cpConfigurationList = _addCPConfigurationList(
			externalReferenceCode, _commerceCatalog.getGroupId(), 0, false,
			name);

		Assert.assertNotNull(cpConfigurationList);
		Assert.assertEquals(
			externalReferenceCode,
			cpConfigurationList.getExternalReferenceCode());
		Assert.assertEquals(name, cpConfigurationList.getName());
	}

	@Test(expected = RequiredCPConfigurationListException.class)
	public void testCannotDeleteMasterCPConfigurationList() throws Exception {
		frutillaRule.scenario(
			"Master configuration cannot be deleted"
		).given(
			"There is a Commerce Catalog"
		).and(
			"its master configuration"
		).when(
			"the delete operation of the Configuration List is triggered"
		).then(
			"an exception is thrown"
		).and(
			"the configuration list does not get delete"
		);

		List<CPConfigurationList> cpConfigurationLists =
			_cpConfigurationListLocalService.getCPConfigurationLists(
				_commerceCatalog.getGroupId(), _commerceCatalog.getCompanyId());

		CPConfigurationList cpConfigurationList = cpConfigurationLists.get(0);

		Assert.assertTrue(cpConfigurationList.isMaster());

		_cpConfigurationListLocalService.deleteCPConfigurationList(
			cpConfigurationList);
	}

	@Test(
		expected = CPConfigurationListParentCPConfigurationListGroupIdException.class
	)
	public void testChildCPConfigurationListCannotHaveDifferentCatalog()
		throws Exception {

		frutillaRule.scenario(
			"Add child Product Configuration List"
		).given(
			"A parent Product Configuration List"
		).when(
			"A child Configuration List is added"
		).and(
			"A different catalog is used to create it"
		).then(
			"An exception is thrown"
		);

		String externalReferenceCode1 = RandomTestUtil.randomString();
		String name1 = RandomTestUtil.randomString();

		CPConfigurationList cpConfigurationList1 = _addCPConfigurationList(
			externalReferenceCode1, _commerceCatalog.getGroupId(), 0, false,
			name1);

		String externalReferenceCode2 = RandomTestUtil.randomString();
		String name2 = RandomTestUtil.randomString();

		CommerceCatalog commerceCatalog =
			_commerceCatalogService.addCommerceCatalog(
				RandomTestUtil.randomString(),
				AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
				RandomTestUtil.randomString(), "USD", "en_US", _serviceContext);

		_addCPConfigurationList(
			externalReferenceCode2, commerceCatalog.getGroupId(),
			cpConfigurationList1.getCPConfigurationListId(), false, name2);
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private CPConfigurationList _addCPConfigurationList(
			String externalReferenceCode, long groupId,
			long parentCPConfigurationListId, boolean masterCPConfigurationList,
			String name)
		throws Exception {

		Date date = new Date();

		Calendar calendar = CalendarFactoryUtil.getCalendar(date.getTime());

		int displayDateHour = calendar.get(Calendar.HOUR);

		if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
			displayDateHour += 12;
		}

		return _cpConfigurationListLocalService.addCPConfigurationList(
			externalReferenceCode, _user.getUserId(), groupId,
			parentCPConfigurationListId, masterCPConfigurationList, name, 0D,
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.YEAR), displayDateHour,
			calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
			new ServiceContext());
	}

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogService _commerceCatalogService;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	private User _user;

}