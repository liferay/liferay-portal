/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.internal.upgrade.v1_6_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class CommerceCurrencyExternalReferenceCodeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		_testUpgradeWithCustomCurrency();
		_testUpgradeWithDefaultCurrency();
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();
	}

	private void _testUpgradeWithCustomCurrency() throws Exception {
		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				TestPropsValues.getCompanyId());

		String externalReferenceCode = RandomTestUtil.randomString();

		_updateExternalReferenceCode(commerceCurrency, externalReferenceCode);

		_runUpgrade();

		commerceCurrency = _commerceCurrencyLocalService.getCommerceCurrency(
			commerceCurrency.getCommerceCurrencyId());

		Assert.assertEquals(
			externalReferenceCode, commerceCurrency.getExternalReferenceCode());
	}

	private void _testUpgradeWithDefaultCurrency() throws Exception {
		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.fetchCommerceCurrency(
				TestPropsValues.getCompanyId(), _CODE);

		_updateExternalReferenceCode(
			commerceCurrency, RandomTestUtil.randomString());

		_runUpgrade();

		commerceCurrency = _commerceCurrencyLocalService.getCommerceCurrency(
			commerceCurrency.getCommerceCurrencyId());

		Assert.assertEquals(
			_EXTERNAL_REFERENCE_CODE,
			commerceCurrency.getExternalReferenceCode());
	}

	private void _updateExternalReferenceCode(
			CommerceCurrency commerceCurrency, String externalReferenceCode)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update CommerceCurrency set externalReferenceCode = ? where " +
					"commerceCurrencyId = ?")) {

			preparedStatement.setString(1, externalReferenceCode);
			preparedStatement.setLong(
				2, commerceCurrency.getCommerceCurrencyId());

			preparedStatement.executeUpdate();
		}

		EntityCacheUtil.clearCache();
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.currency.internal.upgrade.v1_6_0." +
			"CommerceCurrencyExternalReferenceCodeUpgradeProcess";

	private static final String _CODE = "JPY";

	private static final String _EXTERNAL_REFERENCE_CODE = "JAPANESE_YEN";

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.currency.internal.upgrade.registry.CommerceCurrencyServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}