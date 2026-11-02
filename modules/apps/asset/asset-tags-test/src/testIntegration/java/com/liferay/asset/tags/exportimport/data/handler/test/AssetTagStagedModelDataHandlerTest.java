/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Kocsis
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class AssetTagStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testExportImportDuplicateTags() throws Exception {
		List<AssetTag> assetTags = AssetTagLocalServiceUtil.getGroupTags(
			liveGroup.getGroupId());

		int groupTagsCount = assetTags.size();

		String name = RandomTestUtil.randomString();

		_exportImportDeletedTag(name);
		_exportImportDeletedTag(name);
		_exportImportDeletedTag(name);

		assetTags = AssetTagLocalServiceUtil.getGroupTags(
			liveGroup.getGroupId());

		Assert.assertEquals(
			assetTags.toString(), groupTagsCount + 3, assetTags.size());

		AssetTagLocalServiceUtil.getTag(liveGroup.getGroupId(), name);
		AssetTagLocalServiceUtil.getTag(
			liveGroup.getGroupId(), name + " (Duplicate)");
		AssetTagLocalServiceUtil.getTag(
			liveGroup.getGroupId(), name + " (Duplicate 1)");
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return AssetTestUtil.addTag(
			RandomTestUtil.randomString(), group.getGroupId(),
			RandomTestUtil.randomString());
	}

	@Override
	protected StagedModel addStagedModelWithExternalReferenceCode(
			Group group, String externalReferenceCode,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return AssetTestUtil.addTag(
			externalReferenceCode, group.getGroupId(),
			RandomTestUtil.randomString());
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return AssetTagLocalServiceUtil.getAssetTagByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return AssetTag.class;
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		super.validateImportedStagedModel(stagedModel, importedStagedModel);

		AssetTag tag = (AssetTag)stagedModel;
		AssetTag importedTag = (AssetTag)importedStagedModel;

		Assert.assertEquals(
			tag.getExternalReferenceCode(),
			importedTag.getExternalReferenceCode());
		Assert.assertEquals(tag.getName(), importedTag.getName());
	}

	private void _exportImportDeletedTag(String name) throws Exception {
		AssetTag tag = AssetTestUtil.addTag(
			RandomTestUtil.randomString(), stagingGroup.getGroupId(), name);

		initExport();

		StagedModelDataHandlerUtil.exportStagedModel(portletDataContext, tag);

		AssetTagLocalServiceUtil.deleteTag(tag);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportStagedModel = readExportedStagedModel(tag);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportStagedModel);
		}
	}

}