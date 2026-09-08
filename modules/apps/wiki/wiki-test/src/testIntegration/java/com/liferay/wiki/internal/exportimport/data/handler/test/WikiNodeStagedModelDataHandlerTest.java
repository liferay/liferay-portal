/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.test.util.WikiTestUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zsolt Berentey
 */
@RunWith(Arquillian.class)
public class WikiNodeStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	@Test
	public void testCleanStagedModelDataHandler() throws Exception {
		super.testCleanStagedModelDataHandler();

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					"feature.flag.LPD-35013", Boolean.FALSE.toString())) {

			initExport();

			Map<String, List<StagedModel>> dependentStagedModelsMap =
				addDependentStagedModelsMap(stagingGroup);

			StagedModel stagedModel = addStagedModel(
				stagingGroup, dependentStagedModelsMap);

			addComments(stagedModel);

			addRatings(stagedModel);

			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, stagedModel);

			validateExport(
				portletDataContext, stagedModel, dependentStagedModelsMap);

			try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
				deleteStagedModel(
					stagedModel, dependentStagedModelsMap, stagingGroup);

				StagedModel exportedStagedModel = readExportedStagedModel(
					stagedModel);

				Assert.assertNull(exportedStagedModel);
			}
		}
	}

	@Test
	@TestInfo("LPD-85488")
	public void testExportImportPreservesLastPostDate() throws Exception {
		initExport();

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		WikiNode wikiNode = (WikiNode)stagedModel;

		wikiNode.setLastPostDate(new Date());

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				stagedModel);

			ExportImportThreadLocal.setLayoutImportInProcess(true);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedStagedModel);

			StagedModel importedStagedModel = getStagedModel(
				exportedStagedModel.getUuid(), liveGroup);

			WikiNode exportedWikiNode = (WikiNode)exportedStagedModel;
			WikiNode importedWikiNode = (WikiNode)importedStagedModel;

			Assert.assertEquals(
				exportedWikiNode.getLastPostDate(),
				importedWikiNode.getLastPostDate());
		}
		finally {
			ExportImportThreadLocal.setLayoutImportInProcess(false);
		}
	}

	@Override
	@Test
	public void testExportImportWithDefaultData() throws Exception {
		super.testExportImportWithDefaultData();

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					"feature.flag.LPD-35013", Boolean.FALSE.toString())) {

			initExport();

			Map<String, List<StagedModel>> defaultDependentStagedModelsMap =
				addDefaultDependentStagedModelsMap(stagingGroup);

			StagedModel stagedModel = addDefaultStagedModel(
				stagingGroup, defaultDependentStagedModelsMap);

			if (stagedModel == null) {
				return;
			}

			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, stagedModel);

			validateExport(
				portletDataContext, stagedModel,
				defaultDependentStagedModelsMap);

			Map<String, List<StagedModel>> secondDependentStagedModelsMap =
				addDefaultDependentStagedModelsMap(liveGroup);

			addDefaultStagedModel(liveGroup, secondDependentStagedModelsMap);

			try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
				StagedModel exportedStagedModel = readExportedStagedModel(
					stagedModel);

				Assert.assertNull(exportedStagedModel);
			}
		}
	}

	@Test
	@TestInfo("LPS-71134")
	public void testImportNodeWithExistingName() throws Exception {
		WikiNode node = WikiTestUtil.addDefaultNode(stagingGroup.getGroupId());

		WikiPage page = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), node.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));

		WikiNode existingNode = WikiTestUtil.addDefaultNode(
			liveGroup.getGroupId());

		exportImportStagedModel(page);

		WikiNode importedNode =
			WikiNodeLocalServiceUtil.getWikiNodeByUuidAndGroupId(
				node.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(node.getName() + " 2", importedNode.getName());
		Assert.assertEquals(
			1,
			WikiPageLocalServiceUtil.getPagesCount(
				importedNode.getNodeId(), true));

		Assert.assertEquals(
			0,
			WikiPageLocalServiceUtil.getPagesCount(
				existingNode.getNodeId(), true));
	}

	@Override
	@Test
	public void testStagedModelDataHandler() throws Exception {
		super.testStagedModelDataHandler();

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					"feature.flag.LPD-35013", Boolean.FALSE.toString())) {

			initExport();

			Map<String, List<StagedModel>> dependentStagedModelsMap =
				addDependentStagedModelsMap(stagingGroup);

			StagedModel stagedModel = addStagedModel(
				stagingGroup, dependentStagedModelsMap);

			addComments(stagedModel);

			addRatings(stagedModel);

			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, stagedModel);

			validateExport(
				portletDataContext, stagedModel, dependentStagedModelsMap);

			try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
				StagedModel exportedStagedModel = readExportedStagedModel(
					stagedModel);

				Assert.assertNull(exportedStagedModel);
			}
		}
	}

	@Override
	protected StagedModel addDefaultStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		WikiNode wikiNode = WikiNodeLocalServiceUtil.fetchNode(
			group.getGroupId(), "Main");

		if (wikiNode != null) {
			return wikiNode;
		}

		return WikiTestUtil.addDefaultNode(group.getGroupId());
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return WikiTestUtil.addNode(group.getGroupId());
	}

	@Override
	protected StagedModel addStagedModelWithExternalReferenceCode(
			Group group, String externalReferenceCode,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return WikiNodeLocalServiceUtil.addNode(
			externalReferenceCode, TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return WikiNodeLocalServiceUtil.getWikiNodeByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return WikiNode.class;
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		super.validateImportedStagedModel(stagedModel, importedStagedModel);

		WikiNode node = (WikiNode)stagedModel;
		WikiNode importedNode = (WikiNode)importedStagedModel;

		Assert.assertEquals(
			node.getExternalReferenceCode(),
			importedNode.getExternalReferenceCode());
		Assert.assertEquals(node.getName(), importedNode.getName());
		Assert.assertEquals(
			node.getDescription(), importedNode.getDescription());
		Assert.assertEquals(
			node.getLastPostDate(), importedNode.getLastPostDate());
	}

}