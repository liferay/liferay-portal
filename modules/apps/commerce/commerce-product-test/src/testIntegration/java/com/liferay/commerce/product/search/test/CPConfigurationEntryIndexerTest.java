/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.test.util.HitsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Calendar;
import java.util.Date;

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
public class CPConfigurationEntryIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_indexer = _indexerRegistry.getIndexer(CPConfigurationEntry.class);
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testSearch() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId());

		CPConfigurationList cpConfigurationList1 =
			_cpConfigurationListLocalService.getMasterCPConfigurationList(
				commerceCatalog.getGroupId());

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.getCPConfigurationEntry(
				_portal.getClassNameId(CPDefinition.class),
				cpDefinition.getCPDefinitionId(),
				cpConfigurationList1.getCPConfigurationListId());

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(
			CPField.CP_CONFIGURATION_LIST_ID,
			cpConfigurationList1.getCPConfigurationListId());
		searchContext.setAttribute(
			Field.CLASS_NAME_ID, _portal.getClassNameId(CPDefinition.class));
		searchContext.setCompanyId(_group.getCompanyId());

		Hits hits = _indexer.search(searchContext);

		Document document = HitsAssert.assertOnlyOne(hits);

		Assert.assertEquals(
			String.valueOf(cpConfigurationEntry.getClassPK()),
			document.get(Field.ENTRY_CLASS_PK));

		Date date = new Date();

		Calendar calendar = CalendarFactoryUtil.getCalendar(date.getTime());

		int displayDateHour = calendar.get(Calendar.HOUR);

		if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
			displayDateHour += 12;
		}

		CPConfigurationList cpConfigurationList2 =
			_cpConfigurationListLocalService.addCPConfigurationList(
				null, _user.getUserId(), commerceCatalog.getGroupId(),
				cpConfigurationList1.getCPConfigurationListId(), false,
				"Test List", 0D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());

		searchContext.setAttribute(
			CPField.CP_CONFIGURATION_LIST_ID,
			cpConfigurationList2.getCPConfigurationListId());

		searchContext.setCompanyId(_group.getCompanyId());

		hits = _indexer.search(searchContext);

		document = HitsAssert.assertOnlyOne(hits);

		Assert.assertEquals(
			String.valueOf(cpConfigurationEntry.getClassPK()),
			document.get(Field.ENTRY_CLASS_PK));
	}

	private static Indexer<CPConfigurationEntry> _indexer;

	@Inject
	private static IndexerRegistry _indexerRegistry;

	private static User _user;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

}