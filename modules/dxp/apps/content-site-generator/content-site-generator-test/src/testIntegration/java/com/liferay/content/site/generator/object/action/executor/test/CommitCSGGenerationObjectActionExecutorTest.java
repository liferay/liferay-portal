/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.site.generator.object.action.executor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Sousa
 */
@FeatureFlag("LPD-85515")
@RunWith(Arquillian.class)
public class CommitCSGGenerationObjectActionExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_generationObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CSG_GENERATION", TestPropsValues.getCompanyId());
		_generationItemObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CSG_GENERATION_ITEM", TestPropsValues.getCompanyId());

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		for (String externalReferenceCode :
				_listTypeDefinitionExternalReferenceCodes) {

			ListTypeDefinition listTypeDefinition =
				_listTypeDefinitionLocalService.
					fetchListTypeDefinitionByExternalReferenceCode(
						externalReferenceCode, TestPropsValues.getCompanyId());

			if (listTypeDefinition == null) {
				continue;
			}

			_listTypeDefinitionLocalService.deleteListTypeDefinition(
				listTypeDefinition);
		}

		GroupTestUtil.deleteGroup(_group);
	}

	@Test
	public void testExecute() throws Exception {
		ObjectEntry generationObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0L, TestPropsValues.getUserId(),
				_generationObjectDefinition.getObjectDefinitionId(), 0L, null,
				HashMapBuilder.<String, Serializable>put(
					"generationStatus", "ready"
				).put(
					"prompt", RandomTestUtil.randomString()
				).put(
					"title", RandomTestUtil.randomString()
				).build(),
				_serviceContext);

		_addGenerationItemObjectEntry(generationObjectEntry, 1);
		_addGenerationItemObjectEntry(generationObjectEntry, 2);

		_objectActionEngine.executeObjectAction(
			"commit", ObjectActionTriggerConstants.KEY_STANDALONE,
			_generationObjectDefinition.getObjectDefinitionId(),
			JSONUtil.put(
				"classPK", generationObjectEntry.getObjectEntryId()
			).put(
				"objectEntry",
				HashMapBuilder.putAll(
					generationObjectEntry.getModelAttributes()
				).put(
					"values", generationObjectEntry.getValues()
				).build()
			),
			TestPropsValues.getUserId());

		IdempotentRetryAssert.retryAssert(
			60, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				Map<String, Serializable> values =
					_objectEntryLocalService.getValues(
						generationObjectEntry.getObjectEntryId());

				Assert.assertNotNull(values.get("commitDate"));
				Assert.assertTrue(
					Validator.isBlank(
						MapUtil.getString(values, "failureReason")));
				Assert.assertEquals(
					"committed", values.get("generationStatus"));

				return null;
			});

		Assert.assertNotNull(
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					_listTypeDefinitionExternalReferenceCodes.get(0),
					TestPropsValues.getCompanyId()));
		Assert.assertNotNull(
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					_listTypeDefinitionExternalReferenceCodes.get(1),
					TestPropsValues.getCompanyId()));
	}

	private void _addGenerationItemObjectEntry(
			ObjectEntry generationObjectEntry, int loadOrder)
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_listTypeDefinitionExternalReferenceCodes.add(externalReferenceCode);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".json",
			ContentTypes.APPLICATION_JSON,
			JSONUtil.put(
				"configuration",
				JSONUtil.put(
					"className",
					"com.liferay.headless.admin.list.type.dto.v1_0." +
						"ListTypeDefinition"
				).put(
					"multiCompany", true
				).put(
					"parameters",
					JSONUtil.put(
						"containsHeaders", "true"
					).put(
						"createStrategy", "UPSERT"
					).put(
						"importStrategy", "ON_ERROR_FAIL"
					).put(
						"updateStrategy", "UPDATE"
					)
				).put(
					"taskItemDelegateName", "DEFAULT"
				)
			).put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", externalReferenceCode
					).put(
						"listTypeEntries", JSONFactoryUtil.createJSONArray()
					).put(
						"name", RandomTestUtil.randomString()
					).put(
						"system", true
					))
			).toString(
			).getBytes(
				StandardCharsets.UTF_8
			),
			null, null, null, _serviceContext);

		_objectEntryLocalService.addObjectEntry(
			0L, TestPropsValues.getUserId(),
			_generationItemObjectDefinition.getObjectDefinitionId(), 0L, null,
			HashMapBuilder.<String, Serializable>put(
				"batchFile", fileEntry.getFileEntryId()
			).put(
				"fileName", fileEntry.getFileName()
			).put(
				"itemCount", 1
			).put(
				"loadOrder", loadOrder
			).put(
				"r_items_l_csgGenerationId",
				generationObjectEntry.getObjectEntryId()
			).build(),
			_serviceContext);
	}

	private static ObjectDefinition _generationItemObjectDefinition;
	private static ObjectDefinition _generationObjectDefinition;
	private static Group _group;
	private static final List<String>
		_listTypeDefinitionExternalReferenceCodes = new ArrayList<>();

	@Inject
	private static ListTypeDefinitionLocalService
		_listTypeDefinitionLocalService;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	private static ServiceContext _serviceContext;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private ObjectActionEngine _objectActionEngine;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}