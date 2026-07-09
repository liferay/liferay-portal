/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.cmp.client.dto.v1_0.ContentCoverage;
import com.liferay.headless.cmp.client.dto.v1_0.ContentCoverageEntry;
import com.liferay.headless.cmp.client.dto.v1_0.FunnelStage;
import com.liferay.headless.cmp.client.dto.v1_0.Persona;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.io.Serializable;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Fábio Alves
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-58677")}
)
@RunWith(Arquillian.class)
public class ContentCoverageResourceTest
	extends BaseContentCoverageResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
		_group = CMPTestUtil.getOrAddGroup(ContentCoverageResourceTest.class);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_depotEntryLocalService.deleteDepotEntry(_depotEntry);
	}

	@Override
	@Test
	public void testGetProjectContentCoverage() throws Exception {
		_testGetProjectContentCoverageWithFunnelStages();
		_testGetProjectContentCoverageWithFunnelStagesAndPersonas();
		_testGetProjectContentCoverageWithInvalidProjectId();
		_testGetProjectContentCoverageWithStatuses();
		_testGetProjectContentCoverageWithoutFunnelStagesAndPersonas();
	}

	@Override
	@Test
	public void testGraphQLGetProjectContentCoverage() {
	}

	@Override
	@Test
	public void testGraphQLGetProjectContentCoverageNotFound() {
	}

	private ObjectEntry _addContentObjectEntry(
			long[] assetCategoryIds, String[] assetTagNames)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_depotEntry.getGroupId(), _depotEntry.getCompanyId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), null,
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				(Serializable)HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));

		_partialUpdateObjectEntry(assetCategoryIds, assetTagNames, objectEntry);

		return objectEntry;
	}

	private void _addContentObjectEntry(
			long[] assetCategoryIds, String[] assetTagNames, int status)
		throws Exception {

		ObjectEntry objectEntry = _addContentObjectEntry(
			assetCategoryIds, assetTagNames);

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(), status,
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));
	}

	private ObjectEntry _addProjectObjectEntry(
			long[] assetCategoryIds, String assetTagName)
		throws Exception {

		ObjectEntry objectEntry = CMPTestUtil.addProjectObjectEntry();

		_partialUpdateObjectEntry(assetCategoryIds, new String[0], objectEntry);
		_partialUpdateObjectEntry(
			new long[0], new String[] {assetTagName},
			CMPTestUtil.addTaskObjectEntry(objectEntry));

		return objectEntry;
	}

	private void _assertContentCoverage(
			ContentCoverage expectedContentCoverage, ObjectEntry objectEntry)
		throws Exception {

		ContentCoverage actualContentCoverage =
			contentCoverageResource.getProjectContentCoverage(
				objectEntry.getObjectEntryId());

		JSONAssert.assertEquals(
			expectedContentCoverage.toString(),
			actualContentCoverage.toString(), JSONCompareMode.NON_EXTENSIBLE);
	}

	private AssetCategory _getAssetCategory(String externalReferenceCode) {
		return _assetCategoryLocalService.
			fetchAssetCategoryByExternalReferenceCode(
				externalReferenceCode, _group.getGroupId());
	}

	private void _partialUpdateObjectEntry(
			long[] assetCategoryIds, String[] assetTagNames,
			ObjectEntry objectEntry)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(objectEntry.getGroupId());

		serviceContext.setAssetCategoryIds(assetCategoryIds);
		serviceContext.setAssetTagNames(assetTagNames);

		_objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), new HashMap<>(),
			serviceContext);
	}

	private void _testGetProjectContentCoverageWithFunnelStages()
		throws Exception {

		AssetCategory awarenessAssetCategory = _getAssetCategory(
			"L_CMP_FUNNEL_STAGE_AWARENESS");

		String assetTagName = "L_CMP_TASK_" + RandomTestUtil.randomString(10);

		ObjectEntry objectEntry = _addProjectObjectEntry(
			new long[] {awarenessAssetCategory.getCategoryId()}, assetTagName);

		_addContentObjectEntry(new long[0], new String[] {assetTagName});
		_addContentObjectEntry(
			new long[] {awarenessAssetCategory.getCategoryId()},
			new String[] {assetTagName});

		_addContentObjectEntry(
			new long[] {awarenessAssetCategory.getCategoryId()},
			new String[] {"L_CMP_TASK_" + RandomTestUtil.randomString(10)});

		_assertContentCoverage(
			_toContentCoverage(
				2,
				new ContentCoverageEntry[] {
					_toContentCoverageEntry(1, -1, -1),
					_toContentCoverageEntry(
						1, awarenessAssetCategory.getCategoryId(), -1)
				},
				new AssetCategory[] {awarenessAssetCategory},
				new AssetCategory[0]),
			objectEntry);
	}

	private void _testGetProjectContentCoverageWithFunnelStagesAndPersonas()
		throws Exception {

		AssetCategory awarenessAssetCategory = _getAssetCategory(
			"L_CMP_FUNNEL_STAGE_AWARENESS");
		AssetCategory championAssetCategory = _getAssetCategory(
			"L_CMP_PERSONAS_CHAMPION");
		AssetCategory considerationAssetCategory = _getAssetCategory(
			"L_CMP_FUNNEL_STAGE_CONSIDERATION");
		AssetCategory decisionMakerAssetCategory = _getAssetCategory(
			"L_CMP_PERSONAS_DECISION_MAKER");

		String assetTagName = "L_CMP_TASK_" + RandomTestUtil.randomString(10);

		ObjectEntry projectObjectEntry = _addProjectObjectEntry(
			new long[] {
				awarenessAssetCategory.getCategoryId(),
				championAssetCategory.getCategoryId(),
				considerationAssetCategory.getCategoryId(),
				decisionMakerAssetCategory.getCategoryId()
			},
			assetTagName);

		_addContentObjectEntry(new long[0], new String[] {assetTagName});
		_addContentObjectEntry(
			new long[] {
				awarenessAssetCategory.getCategoryId(),
				championAssetCategory.getCategoryId(),
				considerationAssetCategory.getCategoryId()
			},
			new String[] {assetTagName});
		_addContentObjectEntry(
			new long[] {
				awarenessAssetCategory.getCategoryId(),
				championAssetCategory.getCategoryId(),
				decisionMakerAssetCategory.getCategoryId()
			},
			new String[] {assetTagName});
		_addContentObjectEntry(
			new long[] {
				awarenessAssetCategory.getCategoryId(),
				considerationAssetCategory.getCategoryId()
			},
			new String[] {assetTagName});
		_addContentObjectEntry(
			new long[] {
				championAssetCategory.getCategoryId(),
				decisionMakerAssetCategory.getCategoryId()
			},
			new String[] {assetTagName});
		_addContentObjectEntry(
			new long[] {
				considerationAssetCategory.getCategoryId(),
				decisionMakerAssetCategory.getCategoryId()
			},
			new String[] {assetTagName});

		_assertContentCoverage(
			_toContentCoverage(
				6,
				new ContentCoverageEntry[] {
					_toContentCoverageEntry(1, -1, -1),
					_toContentCoverageEntry(
						1, -1, championAssetCategory.getCategoryId()),
					_toContentCoverageEntry(
						1, -1, decisionMakerAssetCategory.getCategoryId()),
					_toContentCoverageEntry(
						1, awarenessAssetCategory.getCategoryId(), -1),
					_toContentCoverageEntry(
						1, awarenessAssetCategory.getCategoryId(),
						decisionMakerAssetCategory.getCategoryId()),
					_toContentCoverageEntry(
						1, considerationAssetCategory.getCategoryId(), -1),
					_toContentCoverageEntry(
						1, considerationAssetCategory.getCategoryId(),
						championAssetCategory.getCategoryId()),
					_toContentCoverageEntry(
						1, considerationAssetCategory.getCategoryId(),
						decisionMakerAssetCategory.getCategoryId()),
					_toContentCoverageEntry(
						2, awarenessAssetCategory.getCategoryId(),
						championAssetCategory.getCategoryId())
				},
				new AssetCategory[] {
					awarenessAssetCategory, considerationAssetCategory
				},
				new AssetCategory[] {
					championAssetCategory, decisionMakerAssetCategory
				}),
			projectObjectEntry);
	}

	private void _testGetProjectContentCoverageWithInvalidProjectId()
		throws Exception {

		assertHttpResponseStatusCode(
			404,
			contentCoverageResource.getProjectContentCoverageHttpResponse(
				RandomTestUtil.randomLong()));
	}

	private void _testGetProjectContentCoverageWithoutFunnelStagesAndPersonas()
		throws Exception {

		String assetTagName = "L_CMP_TASK_" + RandomTestUtil.randomString(10);

		ObjectEntry objectEntry = _addProjectObjectEntry(
			new long[0], assetTagName);

		AssetCategory decisionMakerAssetCategory = _getAssetCategory(
			"L_CMP_PERSONAS_DECISION_MAKER");

		_addContentObjectEntry(
			new long[] {decisionMakerAssetCategory.getCategoryId()},
			new String[] {assetTagName});

		_assertContentCoverage(
			_toContentCoverage(
				1, new ContentCoverageEntry[0], new AssetCategory[0],
				new AssetCategory[0]),
			objectEntry);
	}

	private void _testGetProjectContentCoverageWithStatuses() throws Exception {
		AssetCategory awarenessAssetCategory = _getAssetCategory(
			"L_CMP_FUNNEL_STAGE_AWARENESS");
		AssetCategory championAssetCategory = _getAssetCategory(
			"L_CMP_PERSONAS_CHAMPION");

		String assetTagName = "L_CMP_TASK_" + RandomTestUtil.randomString(10);

		ObjectEntry objectEntry = _addProjectObjectEntry(
			new long[] {
				awarenessAssetCategory.getCategoryId(),
				championAssetCategory.getCategoryId()
			},
			assetTagName);

		long[] assetCategoryIds = {
			awarenessAssetCategory.getCategoryId(),
			championAssetCategory.getCategoryId()
		};

		for (int status :
				new int[] {
					WorkflowConstants.STATUS_APPROVED,
					WorkflowConstants.STATUS_DRAFT,
					WorkflowConstants.STATUS_EXPIRED,
					WorkflowConstants.STATUS_PENDING,
					WorkflowConstants.STATUS_SCHEDULED
				}) {

			_addContentObjectEntry(
				assetCategoryIds, new String[] {assetTagName}, status);
		}

		for (int status :
				new int[] {
					WorkflowConstants.STATUS_DENIED,
					WorkflowConstants.STATUS_EMPTY,
					WorkflowConstants.STATUS_IN_TRASH,
					WorkflowConstants.STATUS_INACTIVE,
					WorkflowConstants.STATUS_INCOMPLETE
				}) {

			_addContentObjectEntry(
				assetCategoryIds, new String[] {assetTagName}, status);
		}

		_assertContentCoverage(
			_toContentCoverage(
				5,
				new ContentCoverageEntry[] {
					_toContentCoverageEntry(
						5, awarenessAssetCategory.getCategoryId(),
						championAssetCategory.getCategoryId())
				},
				new AssetCategory[] {awarenessAssetCategory},
				new AssetCategory[] {championAssetCategory}),
			objectEntry);
	}

	private ContentCoverage _toContentCoverage(
		long assetCount, ContentCoverageEntry[] contentCoverageEntries,
		AssetCategory[] funnelStageAssetCategories,
		AssetCategory[] personaAssetCategories) {

		ContentCoverage contentCoverage = new ContentCoverage();

		contentCoverage.setAssetCount(assetCount);
		contentCoverage.setContentCoverageEntries(contentCoverageEntries);
		contentCoverage.setFunnelStages(
			TransformUtil.transform(
				funnelStageAssetCategories,
				assetCategory -> new FunnelStage() {
					{
						description = assetCategory.getDescription(
							LocaleUtil.getDefault());
						externalReferenceCode =
							assetCategory.getExternalReferenceCode();
						id = assetCategory.getCategoryId();
						name = assetCategory.getTitle(LocaleUtil.getDefault());
					}
				},
				FunnelStage.class));
		contentCoverage.setPersonas(
			TransformUtil.transform(
				personaAssetCategories,
				assetCategory -> new Persona() {
					{
						description = assetCategory.getDescription(
							LocaleUtil.getDefault());
						externalReferenceCode =
							assetCategory.getExternalReferenceCode();
						id = assetCategory.getCategoryId();
						name = assetCategory.getTitle(LocaleUtil.getDefault());
					}
				},
				Persona.class));

		return contentCoverage;
	}

	private ContentCoverageEntry _toContentCoverageEntry(
		long assetCount, long funnelStageId, long personaId) {

		ContentCoverageEntry contentCoverageEntry = new ContentCoverageEntry();

		contentCoverageEntry.setAssetCount(assetCount);
		contentCoverageEntry.setFunnelStageId(funnelStageId);
		contentCoverageEntry.setPersonaId(personaId);

		return contentCoverageEntry;
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}