/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.Portlet;

import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionLocalServiceDBPartitionTest {

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
	public void testDeleteCompanyRemovesResourceActions() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		ObjectDefinition objectDefinition = _publishObjectDefinition(company);

		String portletId = objectDefinition.getPortletId();

		boolean deleted = false;

		try {
			Assert.assertEquals(
				objectDefinition.getResourceName(),
				ResourceActionsUtil.getPortletRootModelResource(portletId));

			_companyLocalService.deleteCompany(company);

			deleted = true;

			Assert.assertNull(
				ResourceActionsUtil.getPortletRootModelResource(portletId));
		}
		finally {
			if (!deleted) {
				_companyLocalService.deleteCompany(company);
			}
		}
	}

	@Test
	public void testGetClassName() throws Exception {
		ObjectDefinition objectDefinition1 = null;
		ObjectDefinition objectDefinition2 = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			objectDefinition1 =
				ObjectDefinitionTestUtil.publishObjectDefinition();
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			User user = UserTestUtil.getAdminUser(
				PortalInstancePool.getDefaultCompanyId());

			objectDefinition2 =
				_objectDefinitionLocalService.addCustomObjectDefinition(
					null, user.getUserId(), 0, objectDefinition1.getClassName(),
					null, true, false, true, false, true, false, true, true,
					true, null, RandomTestUtil.randomLocaleStringMap(),
					objectDefinition1.getShortName(), null, null,
					RandomTestUtil.randomLocaleStringMap(), true,
					ObjectDefinitionConstants.SCOPE_COMPANY,
					ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
					Collections.emptyList(),
					Collections.singletonList(
						new TextObjectFieldBuilder(
						).labelMap(
							RandomTestUtil.randomLocaleStringMap()
						).name(
							StringUtil.randomId()
						).build()),
					Collections.emptyList(), new ServiceContext());
		}

		Assert.assertNotEquals(
			objectDefinition1.getClassName(), objectDefinition2.getClassName());
	}

	@Test
	public void testPublishObjectDefinition() throws Exception {
		_objectDefinition = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			_objectDefinition =
				ObjectDefinitionTestUtil.publishObjectDefinition(
					Collections.singletonList(
						new TextObjectFieldBuilder(
						).labelMap(
							RandomTestUtil.randomLocaleStringMap()
						).name(
							"a" + RandomTestUtil.randomString()
						).build()),
					ObjectDefinitionConstants.SCOPE_COMPANY,
					TestPropsValues.getUserId());
		}

		_assertResourceActionsCount(
			TestPropsValues.getCompanyId(), _objectDefinition, 4);
		_assertResourceActionsCount(
			PortalInstancePool.getDefaultCompanyId(), _objectDefinition, 0);
	}

	@Test
	public void testUndeployObjectDefinition() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		ObjectDefinition objectDefinition = _publishObjectDefinition(company);

		_assertServiceReferencesCount(1, objectDefinition);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			_objectDefinitionLocalService.undeployObjectDefinition(
				objectDefinition);
		}

		_assertServiceReferencesCount(0, objectDefinition);

		_companyLocalService.deleteCompany(company);
	}

	@Test
	public void testUpdateClassNameWhenCompanyIsNotInPortalInstancePool()
		throws Exception {

		Company company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			User user = UserTestUtil.getAdminUser(company.getCompanyId());

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.addCustomObjectDefinition(
					null, user.getUserId(), 0, null, null, true, false, true,
					false, true, false, true, true, true, null,
					RandomTestUtil.randomLocaleStringMap(),
					"A" + RandomTestUtil.randomString(), null, null,
					RandomTestUtil.randomLocaleStringMap(), true,
					ObjectDefinitionConstants.SCOPE_COMPANY,
					ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
					Collections.emptyList(),
					Collections.singletonList(
						new TextObjectFieldBuilder(
						).labelMap(
							RandomTestUtil.randomLocaleStringMap()
						).name(
							StringUtil.randomId()
						).build()),
					Collections.emptyList(), new ServiceContext());

			PortalInstancePool.remove(company.getCompanyId());

			ObjectDefinition updatedObjectDefinition =
				_objectDefinitionLocalService.updateClassName(
					objectDefinition.getObjectDefinitionId());

			Assert.assertNotEquals(
				objectDefinition.getClassName(),
				updatedObjectDefinition.getClassName());
		}
		finally {
			PortalInstancePool.add(company);

			_companyLocalService.deleteCompany(company);
		}
	}

	private void _assertResourceActionsCount(
		long companyId, ObjectDefinition objectDefinition,
		int resourceActionsCount) {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			Assert.assertEquals(
				resourceActionsCount,
				_resourceActionLocalService.getResourceActionsCount(
					objectDefinition.getClassName()));
		}
	}

	private void _assertServiceReferencesCount(
			int expectedCount, ObjectDefinition objectDefinition)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectDefinitionLocalServiceDBPartitionTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Collection<ServiceReference<Portlet>> serviceReferences =
			bundleContext.getServiceReferences(
				Portlet.class,
				"(jakarta.portlet.name=" + objectDefinition.getPortletId() +
					")");

		Assert.assertEquals(
			serviceReferences.toString(), expectedCount,
			serviceReferences.size());
	}

	private ObjectDefinition _publishObjectDefinition(Company company)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			User user = UserTestUtil.getAdminUser(company.getCompanyId());

			return ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"a" + RandomTestUtil.randomString()
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY, user.getUserId());
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

}