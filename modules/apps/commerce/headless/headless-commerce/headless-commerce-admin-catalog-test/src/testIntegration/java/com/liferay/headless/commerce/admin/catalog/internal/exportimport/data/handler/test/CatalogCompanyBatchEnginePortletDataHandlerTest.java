/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Catalog;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@RunWith(Arquillian.class)
public class CatalogCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEmptyEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
				group.getCompanyId());

		_commerceCatalogLocalService.getOrAddEmptyCommerceCatalog(
			externalReferenceCode, group.getCompanyId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			commerceCurrency.getCode());
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
				group.getCompanyId());

		_commerceCatalogLocalService.addCommerceCatalog(
			externalReferenceCode, RandomTestUtil.randomString(),
			commerceCurrency.getCode(),
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			ServiceContextTestUtil.getServiceContext(
				group.getCompanyId(), groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_commerceCatalogLocalService.deleteCommerceCatalog(
			_getCommerceCatalog(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		CommerceCatalog commerceCatalog = _getCommerceCatalog(
			externalReferenceCode, groupId);

		return commerceCatalog.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return Catalog.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		CommerceCatalog commerceCatalog = _getCommerceCatalog(
			externalReferenceCode, groupId);

		return commerceCatalog.getCommerceCatalogId();
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		CommerceCatalog commerceCatalog = _getCommerceCatalog(
			externalReferenceCode, groupId);

		return commerceCatalog.getStatus();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.
				fetchCommerceCatalogByExternalReferenceCode(
					externalReferenceCode, group.getCompanyId());

		if (commerceCatalog == null) {
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
		return true;
	}

	@Override
	protected boolean supportsPermissions() {
		return true;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		CommerceCatalog commerceCatalog = _getCommerceCatalog(
			externalReferenceCode, groupId);

		commerceCatalog.setUserId(userId);

		_commerceCatalogLocalService.updateCommerceCatalog(commerceCatalog);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		CommerceCatalog commerceCatalog = _getCommerceCatalog(
			externalReferenceCode, groupId);

		_commerceCatalogLocalService.updateCommerceCatalog(
			commerceCatalog.getCommerceCatalogId(),
			commerceCatalog.getAccountEntryId(), RandomTestUtil.randomString(),
			commerceCatalog.getCommerceCurrencyCode(),
			commerceCatalog.getCatalogDefaultLanguageId());
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		CommerceCatalog commerceCatalog = _getCommerceCatalog(
			externalReferenceCode, groupId);

		commerceCatalog.setModifiedDate(modifiedDate);

		_commerceCatalogLocalService.updateCommerceCatalog(commerceCatalog);
	}

	private CommerceCatalog _getCommerceCatalog(
			String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _commerceCatalogLocalService.
			getCommerceCatalogByExternalReferenceCode(
				externalReferenceCode, group.getCompanyId());
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

}