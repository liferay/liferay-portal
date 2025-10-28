/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.trash.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.test.util.BaseTrashHandlerTestCase;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
)
@RunWith(Arquillian.class)
public class ObjectEntryTrashHandlerTest extends BaseTrashHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(),
					"a" + RandomTestUtil.randomString())),
			ObjectDefinitionConstants.SCOPE_SITE);
	}

	@Test
	public void testAddDeletionSystemEventForCompanyScopeObjectDefinition()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(),
						"a" + RandomTestUtil.randomString())),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0L, objectDefinition.getObjectDefinitionId(),
			Collections.emptyMap());

		_testAddDeletionSystemEvent(0L, objectEntry);
	}

	@Test
	public void testAddDeletionSystemEventForSiteScopeObjectDefinition()
		throws Exception {

		baseModel = addBaseModelWithWorkflow(
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		_testAddDeletionSystemEvent(group.getGroupId(), (ObjectEntry)baseModel);
	}

	@Override
	@Test
	public void testTrashAndRestoreWithApprovedStatus() throws Exception {

		// TODO LPD-59712

	}

	@Override
	@Test
	public void testTrashAndRestoreWithApprovedStatusRestoreStatus()
		throws Exception {

		// TODO LPD-59712

	}

	@Override
	protected BaseModel<?> addBaseModelWithWorkflow(
			BaseModel<?> parentBaseModel, ServiceContext serviceContext)
		throws Exception {

		Group group = (Group)parentBaseModel;

		return ObjectEntryTestUtil.addObjectEntry(
			group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
			Collections.emptyMap());
	}

	@Override
	protected BaseModel<?> getBaseModel(long primaryKey) throws Exception {
		return ObjectEntryLocalServiceUtil.getObjectEntry(primaryKey);
	}

	@Override
	protected Class<?> getBaseModelClass() {
		return ObjectEntry.class;
	}

	@Override
	protected String getBaseModelClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	protected int getNotInTrashBaseModelsCount(BaseModel<?> parentBaseModel)
		throws Exception {

		return ObjectEntryLocalServiceUtil.getObjectEntriesCount(
			(Long)parentBaseModel.getPrimaryKeyObj());
	}

	@Override
	protected TrashHandler getTrashHandler(String className) {
		return TrashHandlerRegistryUtil.getTrashHandler(
			_objectDefinition.getClassName());
	}

	@Override
	protected String getUniqueTitle(BaseModel<?> baseModel) {
		ObjectEntry objectEntry = (ObjectEntry)baseModel;

		return String.valueOf(objectEntry.getObjectEntryId());
	}

	@Override
	protected boolean isInTrashContainer(TrashedModel trashedModel) {
		return _trashHelper.isInTrashContainer(trashedModel);
	}

	@Override
	protected void moveBaseModelToTrash(long primaryKey) throws Exception {
		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			primaryKey);

		_objectEntryLocalService.moveObjectEntryToTrash(
			TestPropsValues.getUserId(), objectEntry,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _testAddDeletionSystemEvent(
			long groupId, ObjectEntry objectEntry)
		throws Exception {

		long primaryKey = objectEntry.getPrimaryKey();

		moveBaseModelToTrash(primaryKey);

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				groupId,
				_portal.getClassNameId(objectEntry.getModelClassName()),
				primaryKey);

		Assert.assertEquals(systemEvents.toString(), 1, systemEvents.size());

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			objectEntry.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());
		Assert.assertEquals(
			SystemEventConstants.TYPE_DELETE, systemEvent.getType());
	}

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	@Inject
	private TrashHelper _trashHelper;

}