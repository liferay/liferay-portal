/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Sanz
 */
@RunWith(Arquillian.class)
public class ObjectRelationshipLocalServiceDBPartitionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(PropsValues.DATABASE_PARTITION_ENABLED);

		DB db = DBManagerUtil.getDB();

		Assume.assumeTrue(db.isSupportsDBPartition());
	}

	@Test
	public void testAddObjectRelationship() throws Exception {
		Company company1 = CompanyTestUtil.addCompany();
		Company company2 = CompanyTestUtil.addCompany();

		try {
			_resetCounter(company1, _getCurrentId(company2));

			ObjectDefinition objectDefinition1 = _addCustomObjectDefinition(
				company1);
			ObjectDefinition objectDefinition2 = _addCustomObjectDefinition(
				company1);

			_addObjectRelationship(objectDefinition1, objectDefinition2);

			_resetCounter(
				company2, objectDefinition2.getObjectDefinitionId() - 1);

			ObjectDefinition objectDefinition3 = _addCustomObjectDefinition(
				company2);

			Assert.assertEquals(
				objectDefinition2.getObjectDefinitionId(),
				objectDefinition3.getObjectDefinitionId());

			ObjectDefinition objectDefinition4 = _addCustomObjectDefinition(
				company2);

			_addObjectRelationship(objectDefinition4, objectDefinition3);

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						company2.getCompanyId())) {

				objectDefinition3 =
					_objectDefinitionLocalService.getObjectDefinition(
						objectDefinition3.getObjectDefinitionId());

				Assert.assertArrayEquals(
					new long[] {objectDefinition4.getObjectDefinitionId()},
					objectDefinition3.getRootObjectDefinitionIds());
			}
		}
		finally {
			_companyLocalService.deleteCompany(company1);
			_companyLocalService.deleteCompany(company2);
		}
	}

	private ObjectDefinition _addCustomObjectDefinition(Company company)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			User user = UserTestUtil.getAdminUser(company.getCompanyId());

			return ObjectDefinitionTestUtil.addCustomObjectDefinition(
				0, ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						StringUtil.randomId()
					).build()),
				user.getUserId());
		}
	}

	private void _addObjectRelationship(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					objectDefinition1.getCompanyId())) {

			User user = UserTestUtil.getAdminUser(
				objectDefinition1.getCompanyId());

			_objectRelationshipLocalService.addObjectRelationship(
				null, user.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, null, true,
				RandomTestUtil.randomLocaleStringMap(), StringUtil.randomId(),
				false, ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);
		}
	}

	private long _getCurrentId(Company company) {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			return _counterLocalService.getCurrentId(Counter.class.getName());
		}
	}

	private void _resetCounter(Company company, long size) {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			_counterLocalService.reset(Counter.class.getName(), size);
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}