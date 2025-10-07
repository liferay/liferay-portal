/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.object.deployer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
 * @author Magdalena Jedraszak
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@FeatureFlag("LPS-178642")
@RunWith(Arquillian.class)
public class APIPropertyObjectDefinitionDeployerImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		DBType dbType = DBManagerUtil.getDBType();

		Assume.assumeTrue(
			(dbType == DBType.MYSQL) || (dbType == DBType.POSTGRESQL));

		Assume.assumeTrue(DBPartition.isPartitionEnabled());
	}

	@Test
	@TestInfo("LPD-61326")
	public void testDeploy() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		Company company1 = CompanyTestUtil.addCompany();
		Company company2 = CompanyTestUtil.addCompany();

		Assert.assertEquals(
			_count(
				company1.getCompanyId(),
				_getObjectRelatedModelsProviders(
					bundleContext, company1.getCompanyId())),
			_count(
				company2.getCompanyId(),
				_getObjectRelatedModelsProviders(
					bundleContext, company2.getCompanyId())));
	}

	private int _count(
			long companyId,
			List<ObjectRelatedModelsProvider<?>> objectRelatedModelsProviders)
		throws Exception {

		try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(companyId)) {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_API_PROPERTY", companyId);

			if (objectDefinition == null) {
				return 0;
			}

			int count = 0;

			for (ObjectRelatedModelsProvider<?> objectRelatedModelsProvider :
					objectRelatedModelsProviders) {

				if (Objects.equals(
						objectDefinition.getClassName(),
						objectRelatedModelsProvider.getClassName())) {

					count++;
				}
			}

			return count;
		}
	}

	private List<ObjectRelatedModelsProvider<?>>
			_getObjectRelatedModelsProviders(
				BundleContext bundleContext, long companyId)
		throws Exception {

		List<ObjectRelatedModelsProvider<?>> objectRelatedModelsProviders =
			new ArrayList<>();

		for (ServiceReference<?> serviceReference :
				bundleContext.getServiceReferences(
					ObjectRelatedModelsProvider.class.getName(), null)) {

			ObjectRelatedModelsProvider<?> objectRelatedModelsProvider =
				(ObjectRelatedModelsProvider<?>)bundleContext.getService(
					serviceReference);

			if ((objectRelatedModelsProvider != null) &&
				(objectRelatedModelsProvider.getCompanyId() == companyId)) {

				objectRelatedModelsProviders.add(objectRelatedModelsProvider);
			}
		}

		return objectRelatedModelsProviders;
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}