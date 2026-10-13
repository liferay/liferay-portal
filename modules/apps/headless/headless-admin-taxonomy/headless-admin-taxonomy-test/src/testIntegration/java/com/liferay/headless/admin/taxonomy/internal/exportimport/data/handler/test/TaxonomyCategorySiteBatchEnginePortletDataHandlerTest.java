/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseSiteBatchEnginePortletDataHandlerTestCase;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.internal.exportimport.data.handler.test.util.TaxonomyCategoryTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
public class TaxonomyCategorySiteBatchEnginePortletDataHandlerTest
	extends BaseSiteBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEmptyEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_assetCategoryLocalService.getOrAddEmptyCategory(
			externalReferenceCode, TestPropsValues.getUserId(), groupId);
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		TaxonomyCategoryTestUtil.addAssetCategory(
			externalReferenceCode, groupId);
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_assetCategoryLocalService.deleteCategory(
			_getAssetCategory(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		AssetCategory assetCategory = _getAssetCategory(
			externalReferenceCode, groupId);

		return assetCategory.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return TaxonomyCategory.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		AssetCategory assetCategory = _getAssetCategory(
			externalReferenceCode, groupId);

		return assetCategory.getCategoryId();
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		AssetCategory assetCategory = _getAssetCategory(
			externalReferenceCode, groupId);

		return assetCategory.getStatus();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		AssetCategory assetCategory =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (assetCategory == null) {
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

		TaxonomyCategoryTestUtil.updateAssetCategoryCreatorUserId(
			_getAssetCategory(externalReferenceCode, groupId), userId);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		TaxonomyCategoryTestUtil.updateAssetCategory(
			_getAssetCategory(externalReferenceCode, groupId));
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		TaxonomyCategoryTestUtil.updateAssetCategoryModifiedDate(
			_getAssetCategory(externalReferenceCode, groupId), modifiedDate);
	}

	private AssetCategory _getAssetCategory(
			String externalReferenceCode, long groupId)
		throws Exception {

		return _assetCategoryLocalService.
			getAssetCategoryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

}