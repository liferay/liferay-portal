/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.constants.BulkSelectionActionStatusConstants;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class MoveBulkSelectionActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMSTestUtil.getOrAddGroup(MoveBulkSelectionActionTest.class);

		_objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BULK_ACTION_TASK", TestPropsValues.getCompanyId());
	}

	@Test
	public void testExecuteRecordsTaskResultOnFailure() throws Exception {
		ObjectEntry objectEntry = _addBulkActionTaskObjectEntry();

		_moveBulkSelectionAction.execute(
			TestPropsValues.getUser(),
			new TestBulkSelection(RandomTestUtil.randomString()),
			HashMapBuilder.<String, Serializable>put(
				"bulkActionTaskId", objectEntry.getObjectEntryId()
			).build());

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(
			BulkSelectionActionStatusConstants.COMPLETED,
			values.get("executionStatus"));
		Assert.assertEquals(
			1, GetterUtil.getInteger(values.get("numberOfFailedItems")));
		Assert.assertEquals(
			0, GetterUtil.getInteger(values.get("numberOfSuccessfulItems")));
		Assert.assertEquals(
			"IllegalArgumentException", values.get("taskResult"));
	}

	@Test
	public void testExecuteRecordsTaskResultOnStructureScopeFailure()
		throws Exception {

		ObjectDefinition objectDefinition = _publishDepotObjectDefinition();

		DepotEntry sourceDepotEntry = _addDepotEntry();

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
			String.valueOf(sourceDepotEntry.getGroupId()));

		ObjectEntryFolder sourceObjectEntryFolder = _addObjectEntryFolder(
			sourceDepotEntry.getGroupId());

		_objectEntryLocalService.addObjectEntry(
			sourceDepotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			sourceObjectEntryFolder.getObjectEntryFolderId(), null,
			HashMapBuilder.<String, Serializable>put(
				"able", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectEntry objectEntry = _addBulkActionTaskObjectEntry();

		DepotEntry targetDepotEntry = _addDepotEntry();

		ObjectEntryFolder targetObjectEntryFolder = _addObjectEntryFolder(
			targetDepotEntry.getGroupId());

		_moveBulkSelectionAction.execute(
			TestPropsValues.getUser(),
			new TestBulkSelection(sourceObjectEntryFolder),
			HashMapBuilder.<String, Serializable>put(
				"bulkActionTaskId", objectEntry.getObjectEntryId()
			).put(
				"objectEntryFolderId",
				targetObjectEntryFolder.getObjectEntryFolderId()
			).build());

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(
			1, GetterUtil.getInteger(values.get("numberOfFailedItems")));
		Assert.assertEquals(
			"structureNotInDestinationSpace", values.get("taskResult"));

		sourceObjectEntryFolder =
			_objectEntryFolderLocalService.getObjectEntryFolder(
				sourceObjectEntryFolder.getObjectEntryFolderId());

		Assert.assertNotEquals(
			targetObjectEntryFolder.getObjectEntryFolderId(),
			sourceObjectEntryFolder.getParentObjectEntryFolderId());
	}

	private ObjectEntry _addBulkActionTaskObjectEntry() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(TestPropsValues.getCompanyId());
		serviceContext.setUserId(TestPropsValues.getUserId());

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			LocaleUtil.toLanguageId(LocaleUtil.US),
			HashMapBuilder.<String, Serializable>put(
				"actionName", "MoveObjectBulkSelectionAction"
			).put(
				"executionStatus", BulkSelectionActionStatusConstants.INITIAL
			).put(
				"numberOfItems", 1
			).put(
				"type", "MoveObjectBulkSelectionAction"
			).build(),
			serviceContext);
	}

	private DepotEntry _addDepotEntry() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(TestPropsValues.getCompanyId());
		serviceContext.setUserId(TestPropsValues.getUserId());

		return _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE, serviceContext);
	}

	private ObjectEntryFolder _addObjectEntryFolder(long groupId)
		throws Exception {

		return ObjectEntryFolderTestUtil.addObjectEntryFolder(
			groupId, TestPropsValues.getUserId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);
	}

	private ObjectDefinition _publishDepotObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, true, false, true,
				false, true, false, false, false, false,
				FriendlyURLResolverConstants.URL_SEPARATOR_Y_OBJECT_ENTRY,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test" + RandomTestUtil.randomString(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_DEPOT,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"able"
					).build()),
				Collections.emptyList(), new ServiceContext());

		return ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.bulk.selection.MoveBulkSelectionAction"
	)
	private BulkSelectionAction<Object> _moveBulkSelectionAction;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private static class TestBulkSelection implements BulkSelection<Object> {

		@Override
		public <E extends PortalException> void forEach(
				UnsafeConsumer<Object, E> unsafeConsumer)
			throws PortalException {

			unsafeConsumer.accept(_object);
		}

		@Override
		public Class<? extends BulkSelectionFactory>
			getBulkSelectionFactoryClass() {

			return null;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return Collections.emptyMap();
		}

		@Override
		public long getSize() {
			return 1;
		}

		@Override
		public Serializable serialize() {
			return null;
		}

		@Override
		public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
			return null;
		}

		private TestBulkSelection(Object object) {
			_object = object;
		}

		private final Object _object;

	}

}