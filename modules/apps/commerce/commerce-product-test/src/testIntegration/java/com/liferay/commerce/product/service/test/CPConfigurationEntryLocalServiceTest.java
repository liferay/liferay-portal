/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPConfigurationEntrySettingConstants;
import com.liferay.commerce.product.exception.RequiredCPConfigurationEntryException;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationEntrySetting;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationEntrySettingLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
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
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
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
public class CPConfigurationEntryLocalServiceTest {

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

		CPConfigurationList masterCPConfigurationList =
			_cpConfigurationListLocalService.getMasterCPConfigurationList(
				_commerceCatalog.getGroupId());

		_cpConfigurationList =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(),
				masterCPConfigurationList.getCPConfigurationListId(), false,
				RandomTestUtil.randomString(), 2, 1, 1, 2024, 0, 0, 0, 0, 0, 0,
				0, true);

		_cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId());
	}

	@After
	public void tearDown() throws Exception {
		for (CPConfigurationList cpConfigurationList : _cpConfigurationLists) {
			_cpConfigurationListLocalService.deleteCPConfigurationList(
				_cpConfigurationList);
		}
	}

	@Test
	public void testAddCPConfigurationEntry() throws Exception {
		frutillaRule.scenario(
			"Add Product Configuration Entry"
		).given(
			"There is a Commerce Catalog and a configuration"
		).when(
			"A Configuration Entry is added"
		).then(
			"The Configuration Entry is created"
		);

		String externalReferenceCode = RandomTestUtil.randomString();

		CPConfigurationEntry cpConfigurationEntry1 =
			_cpConfigurationEntryLocalService.addCPConfigurationEntry(
				externalReferenceCode, _user.getUserId(),
				_cpConfigurationList.getGroupId(),
				_portal.getClassNameId(CPDefinition.class),
				_cpDefinition.getCPDefinitionId(),
				_cpConfigurationList.getCPConfigurationListId(), 0, "123", true,
				0, "cpde", 1.0, true, true, true, 1.0, "lowstoc",
				BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
				true, true, 1.0, true, true, true, 1.0, 1.0);

		Assert.assertNotNull(cpConfigurationEntry1);
		Assert.assertEquals(
			externalReferenceCode,
			cpConfigurationEntry1.getExternalReferenceCode());

		List<CPConfigurationEntry> cpConfigurationEntries =
			_cpConfigurationEntryLocalService.getCPConfigurationEntries(
				_cpConfigurationList.getCPConfigurationListId());

		CPConfigurationEntry cpConfigurationEntry2 = cpConfigurationEntries.get(
			0);

		Assert.assertEquals(
			cpConfigurationEntry1.getCPConfigurationEntryId(),
			cpConfigurationEntry2.getCPConfigurationEntryId());

		CPConfigurationEntrySetting cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry1.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_CHANGE_LOG);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			cpConfigurationEntrySetting.getValue());

		Assert.assertFalse(
			"CPConfigurationEntryId", jsonObject.has("CPConfigurationEntryId"));
		Assert.assertFalse(
			"CPConfigurationListId", jsonObject.has("CPConfigurationListId"));
		Assert.assertFalse("companyId", jsonObject.has("companyId"));
		Assert.assertFalse("ctCollectionId", jsonObject.has("ctCollectionId"));
		Assert.assertFalse(
			"externalReferenceCode", jsonObject.has("externalReferenceCode"));
		Assert.assertFalse("mvccVersion", jsonObject.has("mvccVersion"));
		Assert.assertFalse("userId", jsonObject.has("userId"));
		Assert.assertFalse("userName", jsonObject.has("userName"));
		Assert.assertFalse("uuid", jsonObject.has("uuid"));
	}

	@Test
	public void testAddCPConfigurationEntryAndDeleteInheritance()
		throws Exception {

		frutillaRule.scenario(
			"Add Product Configuration Entry to child Configuration List"
		).given(
			"There is a Commerce Catalog and its master configuration"
		).when(
			"A Configuration Entry is added"
		).and(
			"The Configuration Entry is created"
		).and(
			"A child Configuration List is added"
		).and(
			"A new Configuration Entry is added"
		).then(
			"The Configuration Entry is substituted by the new one"
		);

		String externalReferenceCode = RandomTestUtil.randomString();

		CPConfigurationEntry cpConfigurationEntry1 =
			_cpConfigurationEntryLocalService.addCPConfigurationEntry(
				externalReferenceCode, _user.getUserId(),
				_cpConfigurationList.getGroupId(),
				_portal.getClassNameId(CPDefinition.class),
				_cpDefinition.getCPDefinitionId(),
				_cpConfigurationList.getCPConfigurationListId(), 0, "123", true,
				0, "cpde", 1.0, true, true, true, 1.0, "lowstoc",
				BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
				true, true, 1.0, true, true, true, 1.0, 1.0);

		Assert.assertNotNull(cpConfigurationEntry1);
		Assert.assertEquals(
			externalReferenceCode,
			cpConfigurationEntry1.getExternalReferenceCode());

		Date date = new Date();

		Calendar calendar = CalendarFactoryUtil.getCalendar(date.getTime());

		int displayDateHour = calendar.get(Calendar.HOUR);

		if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
			displayDateHour += 12;
		}

		CPConfigurationList cpConfigurationList1 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				null, _user.getUserId(), _cpConfigurationList.getGroupId(),
				_cpConfigurationList.getCPConfigurationListId(), false,
				RandomTestUtil.randomString(), 1, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true);

		_cpConfigurationLists.add(cpConfigurationList1);

		Assert.assertTrue(
			ListUtil.isEmpty(
				_cpConfigurationEntryLocalService.getCPConfigurationEntries(
					cpConfigurationList1.getCPConfigurationListId())));

		CPConfigurationEntrySetting cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry1.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

		Assert.assertNotNull(cpConfigurationEntrySetting);
		Assert.assertTrue(
			StringUtil.contains(
				cpConfigurationEntrySetting.getValue(),
				String.valueOf(
					cpConfigurationList1.getCPConfigurationListId())));

		CPConfigurationList cpConfigurationList2 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				null, _user.getUserId(), _cpConfigurationList.getGroupId(),
				cpConfigurationList1.getCPConfigurationListId(), false,
				RandomTestUtil.randomString(), 1, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true);

		_cpConfigurationLists.add(cpConfigurationList2);

		Assert.assertTrue(
			ListUtil.isEmpty(
				_cpConfigurationEntryLocalService.getCPConfigurationEntries(
					cpConfigurationList2.getCPConfigurationListId())));

		cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry1.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

		Assert.assertNotNull(cpConfigurationEntrySetting);
		Assert.assertTrue(
			StringUtil.contains(
				cpConfigurationEntrySetting.getValue(),
				String.valueOf(
					cpConfigurationList2.getCPConfigurationListId())));

		externalReferenceCode = RandomTestUtil.randomString();

		CPConfigurationEntry cpConfigurationEntry2 =
			_cpConfigurationEntryLocalService.addCPConfigurationEntry(
				externalReferenceCode, _user.getUserId(),
				cpConfigurationList2.getGroupId(),
				_portal.getClassNameId(CPDefinition.class),
				_cpDefinition.getCPDefinitionId(),
				cpConfigurationList2.getCPConfigurationListId(), 0, "123", true,
				0, "cpde", 1.0, true, true, true, 1.0, "lowstoc",
				BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
				true, true, 1.0, true, true, true, 1.0, 1.0);

		Assert.assertFalse(
			ListUtil.isEmpty(
				_cpConfigurationEntryLocalService.getCPConfigurationEntries(
					cpConfigurationList2.getCPConfigurationListId())));

		cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry1.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

		Assert.assertNotNull(cpConfigurationEntrySetting);
		Assert.assertFalse(
			StringUtil.contains(
				cpConfigurationEntrySetting.getValue(),
				String.valueOf(
					cpConfigurationList2.getCPConfigurationListId())));

		CPConfigurationList cpConfigurationList3 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				null, _user.getUserId(), _cpConfigurationList.getGroupId(),
				cpConfigurationList2.getCPConfigurationListId(), false,
				RandomTestUtil.randomString(), 1, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true);

		_cpConfigurationLists.add(cpConfigurationList3);

		cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry2.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

		Assert.assertTrue(
			StringUtil.contains(
				cpConfigurationEntrySetting.getValue(),
				String.valueOf(
					cpConfigurationList3.getCPConfigurationListId())));

		cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry1.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

		Assert.assertFalse(
			StringUtil.contains(
				cpConfigurationEntrySetting.getValue(),
				String.valueOf(
					cpConfigurationList3.getCPConfigurationListId())));

		_cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
			cpConfigurationEntry2);

		cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry1.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

		Assert.assertTrue(
			StringUtil.contains(
				cpConfigurationEntrySetting.getValue(),
				String.valueOf(
					cpConfigurationList2.getCPConfigurationListId())));
		Assert.assertTrue(
			StringUtil.contains(
				cpConfigurationEntrySetting.getValue(),
				String.valueOf(
					cpConfigurationList3.getCPConfigurationListId())));
	}

	@Test
	public void testDeleteCPConfigurationEntry() throws Exception {
		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.addCPConfigurationEntry(
				RandomTestUtil.randomString(), _user.getUserId(),
				_cpConfigurationList.getGroupId(),
				_portal.getClassNameId(CPDefinition.class),
				_cpDefinition.getCPDefinitionId(),
				_cpConfigurationList.getCPConfigurationListId(), 0, "123", true,
				0, "cpde", 1.0, true, true, true, 1.0, "lowstoc",
				BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
				true, true, 1.0, true, true, true, 1.0, 1.0);

		_cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
			cpConfigurationEntry.getCPConfigurationEntryId());

		CPConfigurationList masterCPConfigurationList =
			_cpConfigurationListLocalService.getMasterCPConfigurationList(
				_commerceCatalog.getGroupId());

		cpConfigurationEntry =
			_cpConfigurationEntryLocalService.getCPConfigurationEntry(
				_portal.getClassNameId(CPDefinition.class),
				_cpDefinition.getCPDefinitionId(),
				masterCPConfigurationList.getCPConfigurationListId());

		try {
			_cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
				cpConfigurationEntry);

			Assert.fail();
		}
		catch (RequiredCPConfigurationEntryException
					requiredCPConfigurationEntryException) {

			Assert.assertNotNull(requiredCPConfigurationEntryException);
		}

		_cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
			cpConfigurationEntry, true);
	}

	@Test
	public void testInheritCPConfigurationEntry() throws Exception {
		frutillaRule.scenario(
			"Inherit Product Configuration Entry"
		).given(
			"There is a Commerce Catalog and its master configuration"
		).when(
			"A Configuration Entry is added"
		).and(
			"The Configuration Entry is created"
		).and(
			"A child Configuration List is added"
		).then(
			"The Configuration Entry is inherited"
		);

		String externalReferenceCode = RandomTestUtil.randomString();

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.addCPConfigurationEntry(
				externalReferenceCode, _user.getUserId(),
				_cpConfigurationList.getGroupId(),
				_portal.getClassNameId(CPDefinition.class),
				_cpDefinition.getCPDefinitionId(),
				_cpConfigurationList.getCPConfigurationListId(), 0, "123", true,
				0, "cpde", 1.0, true, true, true, 1.0, "lowstoc",
				BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
				true, true, 1.0, true, true, true, 1.0, 1.0);

		Assert.assertNotNull(cpConfigurationEntry);
		Assert.assertEquals(
			externalReferenceCode,
			cpConfigurationEntry.getExternalReferenceCode());

		Date date = new Date();

		Calendar calendar = CalendarFactoryUtil.getCalendar(date.getTime());

		int displayDateHour = calendar.get(Calendar.HOUR);

		if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
			displayDateHour += 12;
		}

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListLocalService.addCPConfigurationList(
				null, _user.getUserId(), _cpConfigurationList.getGroupId(),
				_cpConfigurationList.getCPConfigurationListId(), false,
				RandomTestUtil.randomString(), 1, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true);

		_cpConfigurationLists.add(cpConfigurationList);

		Assert.assertTrue(
			ListUtil.isEmpty(
				_cpConfigurationEntryLocalService.getCPConfigurationEntries(
					cpConfigurationList.getCPConfigurationListId())));

		CPConfigurationEntrySetting cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

		Assert.assertNotNull(cpConfigurationEntrySetting);
		Assert.assertTrue(
			StringUtil.contains(
				cpConfigurationEntrySetting.getValue(),
				String.valueOf(
					cpConfigurationList.getCPConfigurationListId())));
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogService _commerceCatalogService;

	@Inject
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@Inject
	private CPConfigurationEntrySettingLocalService
		_cpConfigurationEntrySettingLocalService;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	private final List<CPConfigurationList> _cpConfigurationLists =
		new ArrayList<>();
	private CPDefinition _cpDefinition;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;
	private User _user;

}