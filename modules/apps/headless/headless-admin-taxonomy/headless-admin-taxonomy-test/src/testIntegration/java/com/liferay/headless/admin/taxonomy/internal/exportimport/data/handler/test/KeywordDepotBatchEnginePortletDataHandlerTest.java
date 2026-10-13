/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseDepotBatchEnginePortletDataHandlerTestCase;
import com.liferay.headless.admin.taxonomy.dto.v1_0.Keyword;
import com.liferay.headless.admin.taxonomy.internal.exportimport.data.handler.test.util.KeywordTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
public class KeywordDepotBatchEnginePortletDataHandlerTest
	extends BaseDepotBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		AssetTestUtil.addTag(
			externalReferenceCode, groupId, RandomTestUtil.randomString());
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_assetTagLocalService.deleteTag(
			_getAssetTag(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		AssetTag assetTag = _getAssetTag(externalReferenceCode, groupId);

		return assetTag.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return Keyword.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		AssetTag assetTag = _getAssetTag(externalReferenceCode, groupId);

		return assetTag.getTagId();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		AssetTag assetTag =
			_assetTagLocalService.fetchAssetTagByExternalReferenceCode(
				externalReferenceCode, groupId);

		if (assetTag == null) {
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

		KeywordTestUtil.updateAssetTagCreatorUserId(
			_getAssetTag(externalReferenceCode, groupId), userId);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		KeywordTestUtil.updateAssetTag(
			_getAssetTag(externalReferenceCode, groupId));
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		KeywordTestUtil.updateAssetTagModifiedDate(
			_getAssetTag(externalReferenceCode, groupId), modifiedDate);
	}

	private AssetTag _getAssetTag(String externalReferenceCode, long groupId)
		throws Exception {

		return _assetTagLocalService.getAssetTagByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Inject
	private AssetTagLocalService _assetTagLocalService;

}