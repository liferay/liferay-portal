/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v15_1_4.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.service.CommerceAvailabilityEstimateLocalService;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class
	CommerceAvailabilityEstimateExternalReferenceCodeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			_commerceAvailabilityEstimateLocalService.
				addCommerceAvailabilityEstimate(
					null, RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomDouble(),
					ServiceContextTestUtil.getServiceContext());

		long commerceAvailabilityEstimateId =
			commerceAvailabilityEstimate.getCommerceAvailabilityEstimateId();
		String uuid = commerceAvailabilityEstimate.getUuid();

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update CommerceAvailabilityEstimate set " +
					"externalReferenceCode = NULL, status = ? where " +
						"commerceAvailabilityEstimateId = ?")) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement.setLong(2, commerceAvailabilityEstimateId);

			preparedStatement.executeUpdate();
		}

		_runUpgrade();

		EntityCacheUtil.clearCache();

		commerceAvailabilityEstimate =
			_commerceAvailabilityEstimateLocalService.
				getCommerceAvailabilityEstimate(commerceAvailabilityEstimateId);

		Assert.assertEquals(
			uuid, commerceAvailabilityEstimate.getExternalReferenceCode());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			commerceAvailabilityEstimate.getStatus());
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.internal.upgrade.v15_1_4." +
			"CommerceAvailabilityEstimateExternalReferenceCodeUpgradeProcess";

	@Inject
	private CommerceAvailabilityEstimateLocalService
		_commerceAvailabilityEstimateLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.internal.upgrade.registry.CommerceServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}