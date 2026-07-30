/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.cmp.client.dto.v1_0.ContentCoverage;
import com.liferay.headless.cmp.client.dto.v1_0.ContentCoverageEntry;
import com.liferay.headless.cmp.client.dto.v1_0.FunnelStage;
import com.liferay.headless.cmp.client.dto.v1_0.Persona;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	private AssetCategory _addAssetCategory(
			String assetVocabularyExternalReferenceCode)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				getAssetVocabularyByExternalReferenceCode(
					assetVocabularyExternalReferenceCode, _group.getGroupId());

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assetCategories.add(assetCategory);

		return assetCategory;
	}

	private ObjectEntry _addCMPProjectObjectEntry(long[] assetCategoryIds)
		throws Exception {

		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		_partialUpdateObjectEntry(assetCategoryIds, cmpProjectObjectEntry);

		return cmpProjectObjectEntry;
	}

	private ObjectEntry _addCMSBasicWebContentObjectEntry(
			long[] assetCategoryIds, ObjectEntry cmpTaskObjectEntry)
		throws Exception {

		ObjectEntry cmsBasicWebContentObjectEntry =
			CMPTestUtil.addCMSBasicWebContentObjectEntry(
				_depotEntry, RandomTestUtil.randomString());

		if (cmpTaskObjectEntry != null) {
			CMPTestUtil.addCMPTaskLinkObjectEntry(
				cmpTaskObjectEntry, cmsBasicWebContentObjectEntry);
		}

		_partialUpdateObjectEntry(
			assetCategoryIds, cmsBasicWebContentObjectEntry);

		return cmsBasicWebContentObjectEntry;
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
			long[] assetCategoryIds, ObjectEntry objectEntry)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(objectEntry.getGroupId());

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		_objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), new HashMap<>(),
			serviceContext);
	}

	private void _testGetProjectContentCoverageWithFunnelStages()
		throws Exception {

		AssetCategory cmpFunnelStageAwarenessAssetCategory = _getAssetCategory(
			"L_CMP_FUNNEL_STAGE_AWARENESS");

		ObjectEntry cmpProjectObjectEntry = _addCMPProjectObjectEntry(
			new long[] {cmpFunnelStageAwarenessAssetCategory.getCategoryId()});

		ObjectEntry cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		_addCMSBasicWebContentObjectEntry(new long[0], cmpTaskObjectEntry);
		_addCMSBasicWebContentObjectEntry(
			new long[] {cmpFunnelStageAwarenessAssetCategory.getCategoryId()},
			cmpTaskObjectEntry);

		ObjectEntry unrelatedCMPTaskObjectEntry =
			CMPTestUtil.addCMPTaskObjectEntry();

		_addCMSBasicWebContentObjectEntry(
			new long[] {cmpFunnelStageAwarenessAssetCategory.getCategoryId()},
			unrelatedCMPTaskObjectEntry);

		_assertContentCoverage(
			_toContentCoverage(
				2,
				new ContentCoverageEntry[] {
					_toContentCoverageEntry(1, -1, -1),
					_toContentCoverageEntry(
						1, cmpFunnelStageAwarenessAssetCategory.getCategoryId(),
						-1)
				},
				new AssetCategory[] {cmpFunnelStageAwarenessAssetCategory},
				new AssetCategory[0]),
			cmpProjectObjectEntry);
	}

	private void _testGetProjectContentCoverageWithFunnelStagesAndPersonas()
		throws Exception {

		AssetCategory cmpFunnelStageAssetCategory = _addAssetCategory(
			"L_CMP_FUNNEL_STAGE");
		AssetCategory cmpPersonasStageAssetCategory = _addAssetCategory(
			"L_CMP_PERSONAS");

		long[] customAssetCategoryIds = {
			cmpFunnelStageAssetCategory.getCategoryId(),
			cmpPersonasStageAssetCategory.getCategoryId()
		};

		ObjectEntry customCMPProjectObjectEntry = _addCMPProjectObjectEntry(
			customAssetCategoryIds);

		_addCMSBasicWebContentObjectEntry(
			customAssetCategoryIds,
			CMPTestUtil.addCMPTaskObjectEntry(customCMPProjectObjectEntry));

		_assertContentCoverage(
			_toContentCoverage(
				1,
				new ContentCoverageEntry[] {
					_toContentCoverageEntry(
						1, cmpFunnelStageAssetCategory.getCategoryId(),
						cmpPersonasStageAssetCategory.getCategoryId())
				},
				new AssetCategory[] {cmpFunnelStageAssetCategory},
				new AssetCategory[] {cmpPersonasStageAssetCategory}),
			customCMPProjectObjectEntry);

		AssetCategory cmpFunnelStageAwarenessAssetCategory = _getAssetCategory(
			"L_CMP_FUNNEL_STAGE_AWARENESS");
		AssetCategory cmpFunnelStageConsiderationAssetCategory =
			_getAssetCategory("L_CMP_FUNNEL_STAGE_CONSIDERATION");
		AssetCategory cmpPersonasChampionAssetCategory = _getAssetCategory(
			"L_CMP_PERSONAS_CHAMPION");
		AssetCategory cmpPersonasDecisionMakerAssetCategory = _getAssetCategory(
			"L_CMP_PERSONAS_DECISION_MAKER");

		ObjectEntry cmpProjectObjectEntry = _addCMPProjectObjectEntry(
			new long[] {
				cmpFunnelStageAwarenessAssetCategory.getCategoryId(),
				cmpFunnelStageConsiderationAssetCategory.getCategoryId(),
				cmpPersonasChampionAssetCategory.getCategoryId(),
				cmpPersonasDecisionMakerAssetCategory.getCategoryId()
			});

		ObjectEntry cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		_addCMSBasicWebContentObjectEntry(new long[0], cmpTaskObjectEntry);
		_addCMSBasicWebContentObjectEntry(
			new long[] {
				cmpFunnelStageAwarenessAssetCategory.getCategoryId(),
				cmpFunnelStageConsiderationAssetCategory.getCategoryId(),
				cmpPersonasChampionAssetCategory.getCategoryId()
			},
			cmpTaskObjectEntry);
		_addCMSBasicWebContentObjectEntry(
			new long[] {
				cmpFunnelStageAwarenessAssetCategory.getCategoryId(),
				cmpPersonasChampionAssetCategory.getCategoryId(),
				cmpPersonasDecisionMakerAssetCategory.getCategoryId()
			},
			cmpTaskObjectEntry);
		_addCMSBasicWebContentObjectEntry(
			new long[] {
				cmpFunnelStageAwarenessAssetCategory.getCategoryId(),
				cmpFunnelStageConsiderationAssetCategory.getCategoryId()
			},
			cmpTaskObjectEntry);
		_addCMSBasicWebContentObjectEntry(
			new long[] {
				cmpPersonasChampionAssetCategory.getCategoryId(),
				cmpPersonasDecisionMakerAssetCategory.getCategoryId()
			},
			cmpTaskObjectEntry);
		_addCMSBasicWebContentObjectEntry(
			new long[] {
				cmpFunnelStageConsiderationAssetCategory.getCategoryId(),
				cmpPersonasDecisionMakerAssetCategory.getCategoryId()
			},
			cmpTaskObjectEntry);

		_assertContentCoverage(
			_toContentCoverage(
				6,
				new ContentCoverageEntry[] {
					_toContentCoverageEntry(1, -1, -1),
					_toContentCoverageEntry(
						1, -1,
						cmpPersonasChampionAssetCategory.getCategoryId()),
					_toContentCoverageEntry(
						1, -1,
						cmpPersonasDecisionMakerAssetCategory.getCategoryId()),
					_toContentCoverageEntry(
						1, cmpFunnelStageAwarenessAssetCategory.getCategoryId(),
						-1),
					_toContentCoverageEntry(
						1, cmpFunnelStageAwarenessAssetCategory.getCategoryId(),
						cmpPersonasDecisionMakerAssetCategory.getCategoryId()),
					_toContentCoverageEntry(
						1,
						cmpFunnelStageConsiderationAssetCategory.
							getCategoryId(),
						-1),
					_toContentCoverageEntry(
						1,
						cmpFunnelStageConsiderationAssetCategory.
							getCategoryId(),
						cmpPersonasChampionAssetCategory.getCategoryId()),
					_toContentCoverageEntry(
						1,
						cmpFunnelStageConsiderationAssetCategory.
							getCategoryId(),
						cmpPersonasDecisionMakerAssetCategory.getCategoryId()),
					_toContentCoverageEntry(
						2, cmpFunnelStageAwarenessAssetCategory.getCategoryId(),
						cmpPersonasChampionAssetCategory.getCategoryId())
				},
				new AssetCategory[] {
					cmpFunnelStageAwarenessAssetCategory,
					cmpFunnelStageConsiderationAssetCategory
				},
				new AssetCategory[] {
					cmpPersonasChampionAssetCategory,
					cmpPersonasDecisionMakerAssetCategory
				}),
			cmpProjectObjectEntry);
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

		ObjectEntry cmpProjectObjectEntry = _addCMPProjectObjectEntry(
			new long[0]);

		ObjectEntry cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		AssetCategory cmpPersonasDecisionMakerAssetCategory = _getAssetCategory(
			"L_CMP_PERSONAS_DECISION_MAKER");

		_addCMSBasicWebContentObjectEntry(
			new long[] {cmpPersonasDecisionMakerAssetCategory.getCategoryId()},
			cmpTaskObjectEntry);

		_assertContentCoverage(
			_toContentCoverage(
				1, new ContentCoverageEntry[0], new AssetCategory[0],
				new AssetCategory[0]),
			cmpProjectObjectEntry);
	}

	private void _testGetProjectContentCoverageWithStatuses() throws Exception {
		AssetCategory cmpFunnelStageAwarenessAssetCategory = _getAssetCategory(
			"L_CMP_FUNNEL_STAGE_AWARENESS");
		AssetCategory cmpPersonasChampionAssetCategory = _getAssetCategory(
			"L_CMP_PERSONAS_CHAMPION");

		ObjectEntry cmpProjectObjectEntry = _addCMPProjectObjectEntry(
			new long[] {
				cmpFunnelStageAwarenessAssetCategory.getCategoryId(),
				cmpPersonasChampionAssetCategory.getCategoryId()
			});

		ObjectEntry cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		long[] assetCategoryIds = {
			cmpFunnelStageAwarenessAssetCategory.getCategoryId(),
			cmpPersonasChampionAssetCategory.getCategoryId()
		};

		for (int status :
				new int[] {
					WorkflowConstants.STATUS_APPROVED,
					WorkflowConstants.STATUS_DRAFT,
					WorkflowConstants.STATUS_EXPIRED,
					WorkflowConstants.STATUS_PENDING,
					WorkflowConstants.STATUS_SCHEDULED
				}) {

			ObjectEntry cmsBasicWebContentObjectEntry =
				_addCMSBasicWebContentObjectEntry(
					assetCategoryIds, cmpTaskObjectEntry);

			_objectEntryLocalService.updateStatus(
				TestPropsValues.getUserId(),
				cmsBasicWebContentObjectEntry.getObjectEntryId(), status,
				ServiceContextTestUtil.getServiceContext(
					_depotEntry.getGroupId()));
		}

		for (int status :
				new int[] {
					WorkflowConstants.STATUS_DENIED,
					WorkflowConstants.STATUS_EMPTY,
					WorkflowConstants.STATUS_IN_TRASH,
					WorkflowConstants.STATUS_INACTIVE,
					WorkflowConstants.STATUS_INCOMPLETE
				}) {

			ObjectEntry cmsBasicWebContentObjectEntry =
				_addCMSBasicWebContentObjectEntry(
					assetCategoryIds, cmpTaskObjectEntry);

			_objectEntryLocalService.updateStatus(
				TestPropsValues.getUserId(),
				cmsBasicWebContentObjectEntry.getObjectEntryId(), status,
				ServiceContextTestUtil.getServiceContext(
					_depotEntry.getGroupId()));
		}

		_assertContentCoverage(
			_toContentCoverage(
				5,
				new ContentCoverageEntry[] {
					_toContentCoverageEntry(
						5, cmpFunnelStageAwarenessAssetCategory.getCategoryId(),
						cmpPersonasChampionAssetCategory.getCategoryId())
				},
				new AssetCategory[] {cmpFunnelStageAwarenessAssetCategory},
				new AssetCategory[] {cmpPersonasChampionAssetCategory}),
			cmpProjectObjectEntry);
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

	@DeleteAfterTestRun
	private List<AssetCategory> _assetCategories = new ArrayList<>();

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}