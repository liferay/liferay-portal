package com.liferay.production.readiness.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;
import com.liferay.production.readiness.ignore.service.ProductionReadinessIgnoreLocalService;

import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author lily
 */
@RunWith(Arquillian.class)
public class ProductionReadinessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(new LiferayIntegrationTestRule());

	@Test
	public void testSeedRules() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(ProductionReadinessRule.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Collection<ServiceReference<ProductionReadinessRule>> references =
			bundleContext.getServiceReferences(
				ProductionReadinessRule.class, null);

		Assert.assertTrue(
			"Should have at least 3 seed rules, but found " + references.size(),
			references.size() >= 3);

		long companyId = TestPropsValues.getCompanyId();

		for (ServiceReference<ProductionReadinessRule> reference : references) {
			ProductionReadinessRule rule = bundleContext.getService(reference);

			try {
				Collection<Result> results = rule.check(companyId);

				Assert.assertNotNull(results);
				Assert.assertFalse(results.isEmpty());

				for (Result result : results) {
					Assert.assertNotNull(result.getStatus());
					Assert.assertNotNull(result.getSeverity());
					Assert.assertNotNull(result.getMessageKey());
				}
			}
			finally {
				bundleContext.ungetService(reference);
			}
		}
	}

	@Test
	public void testIgnoreService() throws Exception {
		long companyId = TestPropsValues.getCompanyId();
		long userId = TestPropsValues.getUserId();

		String ruleKey = "test-rule-key";

		_productionReadinessIgnoreLocalService.addProductionReadinessIgnore(
			userId, companyId, ruleKey, "testing ignore");

		Assert.assertNotNull(
			_productionReadinessIgnoreLocalService.fetchProductionReadinessIgnore(
				companyId, ruleKey));

		_productionReadinessIgnoreLocalService.deleteProductionReadinessIgnore(
			companyId, ruleKey);

		Assert.assertNull(
			_productionReadinessIgnoreLocalService.fetchProductionReadinessIgnore(
				companyId, ruleKey));
	}

	@Inject
	private ProductionReadinessIgnoreLocalService
		_productionReadinessIgnoreLocalService;

}
