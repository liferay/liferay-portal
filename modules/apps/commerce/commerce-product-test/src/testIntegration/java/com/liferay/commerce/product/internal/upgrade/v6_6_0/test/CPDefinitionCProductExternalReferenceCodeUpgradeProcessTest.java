/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v6_6_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
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
public class CPDefinitionCProductExternalReferenceCodeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			TestPropsValues.getGroupId());

		long cpDefinitionId = cpDefinition.getCPDefinitionId();

		CProduct cProduct = _cProductLocalService.getCProduct(
			cpDefinition.getCProductId());

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update CPDefinition set CProductExternalReferenceCode = " +
					"NULL where CPDefinitionId = ?")) {

			preparedStatement.setLong(1, cpDefinitionId);

			preparedStatement.executeUpdate();
		}

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();

		cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		Assert.assertEquals(
			cProduct.getExternalReferenceCode(),
			cpDefinition.getCProductExternalReferenceCode());
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.product.internal.upgrade.v6_6_0." +
			"CPDefinitionCProductExternalReferenceCodeUpgradeProcess";

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CProductLocalService _cProductLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.product.internal.upgrade.registry.CommerceProductServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}