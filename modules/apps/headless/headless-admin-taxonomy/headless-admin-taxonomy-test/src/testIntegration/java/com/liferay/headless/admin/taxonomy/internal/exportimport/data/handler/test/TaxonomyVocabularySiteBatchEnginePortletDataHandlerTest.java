/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseSiteBatchEnginePortletDataHandlerTestCase;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.internal.exportimport.data.handler.test.util.TaxonomyVocabularyTestUtil;
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
public class TaxonomyVocabularySiteBatchEnginePortletDataHandlerTest
	extends BaseSiteBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEmptyEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_assetVocabularyLocalService.getOrAddEmptyVocabulary(
			externalReferenceCode, TestPropsValues.getUserId(), groupId);
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		AssetTestUtil.addVocabulary(externalReferenceCode, groupId);
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_assetVocabularyLocalService.deleteVocabulary(
			_getAssetVocabulary(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		AssetVocabulary assetVocabulary = _getAssetVocabulary(
			externalReferenceCode, groupId);

		return assetVocabulary.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return TaxonomyVocabulary.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		AssetVocabulary assetVocabulary = _getAssetVocabulary(
			externalReferenceCode, groupId);

		return assetVocabulary.getVocabularyId();
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		AssetVocabulary assetVocabulary = _getAssetVocabulary(
			externalReferenceCode, groupId);

		return assetVocabulary.getStatus();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (assetVocabulary == null) {
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

		TaxonomyVocabularyTestUtil.updateAssetVocabularyCreatorUserId(
			_getAssetVocabulary(externalReferenceCode, groupId), userId);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		TaxonomyVocabularyTestUtil.updateAssetVocabulary(
			_getAssetVocabulary(externalReferenceCode, groupId));
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		TaxonomyVocabularyTestUtil.updateAssetVocabularyModifiedDate(
			_getAssetVocabulary(externalReferenceCode, groupId), modifiedDate);
	}

	private AssetVocabulary _getAssetVocabulary(
			String externalReferenceCode, long groupId)
		throws Exception {

		return _assetVocabularyLocalService.
			getAssetVocabularyByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

}