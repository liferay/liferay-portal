/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.constants.CommerceCurrencyConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Currency;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.math.BigDecimal;

import java.util.Date;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@RunWith(Arquillian.class)
public class CurrencyCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_commerceCurrencyLocalService.addCommerceCurrency(
			externalReferenceCode, TestPropsValues.getUserId(),
			RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(), StringPool.DOLLAR,
			BigDecimal.ONE,
			HashMapBuilder.put(
				LocaleUtil.US, CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN
			).build(),
			2, 2, StringPool.BLANK, false, RandomTestUtil.randomDouble(), true);
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_commerceCurrencyLocalService.deleteCommerceCurrency(
			_getCommerceCurrency(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		CommerceCurrency commerceCurrency = _getCommerceCurrency(
			externalReferenceCode, groupId);

		return commerceCurrency.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return Currency.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		CommerceCurrency commerceCurrency = _getCommerceCurrency(
			externalReferenceCode, groupId);

		return commerceCurrency.getCommerceCurrencyId();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.
				fetchCommerceCurrencyByExternalReferenceCode(
					externalReferenceCode, group.getCompanyId());

		if (commerceCurrency == null) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean supportsComments() {
		return false;
	}

	@Override
	protected boolean supportsEmptyEntries() {
		return false;
	}

	@Override
	protected boolean supportsPermissions() {
		return false;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		CommerceCurrency commerceCurrency = _getCommerceCurrency(
			externalReferenceCode, groupId);

		commerceCurrency.setUserId(userId);

		_commerceCurrencyLocalService.updateCommerceCurrency(commerceCurrency);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		CommerceCurrency commerceCurrency = _getCommerceCurrency(
			externalReferenceCode, groupId);

		commerceCurrency.setSymbol(RandomTestUtil.randomString());

		_commerceCurrencyLocalService.updateCommerceCurrency(commerceCurrency);
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		CommerceCurrency commerceCurrency = _getCommerceCurrency(
			externalReferenceCode, groupId);

		commerceCurrency.setModifiedDate(modifiedDate);

		_commerceCurrencyLocalService.updateCommerceCurrency(commerceCurrency);
	}

	private CommerceCurrency _getCommerceCurrency(
			String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _commerceCurrencyLocalService.
			getCommerceCurrencyByExternalReferenceCode(
				externalReferenceCode, group.getCompanyId());
	}

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

}