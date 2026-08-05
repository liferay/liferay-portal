/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.internal.upgrade.v1_3_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.order.rule.constants.COREntryConstants;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.service.COREntryLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class COREntryTypeSettingsUpgradeProcessTest {

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
	}

	@Test
	public void testUpgrade() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_group.getGroupId());

		CProduct cProduct = cpDefinition.getCProduct();

		COREntry corEntry = _addCOREntry();

		_updateTypeSettings(
			corEntry.getCOREntryId(),
			UnicodePropertiesBuilder.create(
				true
			).setProperty(
				_TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_IDS,
				String.valueOf(cProduct.getCProductId())
			).setProperty(
				COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_QUANTITY,
				_PRODUCT_QUANTITY
			).buildString());

		_runUpgrade();

		EntityCacheUtil.clearCache();

		corEntry = _corEntryLocalService.getCOREntry(corEntry.getCOREntryId());

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				corEntry.getTypeSettings()
			).build();

		Assert.assertEquals(
			cProduct.getExternalReferenceCode(),
			typeSettingsUnicodeProperties.getProperty(
				COREntryConstants.
					TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_EXTERNAL_REFERENCE_CODES));
		Assert.assertNull(
			typeSettingsUnicodeProperties.getProperty(
				_TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_IDS));
		Assert.assertEquals(
			_PRODUCT_QUANTITY,
			typeSettingsUnicodeProperties.getProperty(
				COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_QUANTITY));
	}

	private COREntry _addCOREntry() throws Exception {
		Calendar calendar = Calendar.getInstance();

		return _corEntryLocalService.addCOREntry(
			RandomTestUtil.randomString(), _user.getUserId(), true,
			RandomTestUtil.randomString(), calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
			calendar.get(Calendar.MINUTE), true, RandomTestUtil.randomString(),
			100, COREntryConstants.TYPE_PRODUCTS_LIMIT, StringPool.BLANK,
			_serviceContext);
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private void _updateTypeSettings(long corEntryId, String typeSettings)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update COREntry set typeSettings = ? where COREntryId = ?")) {

			preparedStatement.setString(1, typeSettings);
			preparedStatement.setLong(2, corEntryId);

			preparedStatement.executeUpdate();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.order.rule.internal.upgrade.v1_3_0." +
			"COREntryTypeSettingsUpgradeProcess";

	private static final String _PRODUCT_QUANTITY = "10";

	private static final String _TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_IDS =
		"products-limit-field-product-ids";

	@Inject
	private COREntryLocalService _corEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.order.rule.internal.upgrade.registry.CommerceOrderRuleServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	@DeleteAfterTestRun
	private User _user;

}