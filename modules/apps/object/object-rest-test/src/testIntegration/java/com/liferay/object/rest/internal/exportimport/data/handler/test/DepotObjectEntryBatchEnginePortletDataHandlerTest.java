/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseDepotBatchEnginePortletDataHandlerTestCase;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.internal.exportimport.data.handler.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-43996"))
@RunWith(Arquillian.class)
public class DepotObjectEntryBatchEnginePortletDataHandlerTest
	extends BaseDepotBatchEnginePortletDataHandlerTestCase<ObjectEntry> {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		_objectDefinition = ObjectEntryTestUtil.publishObjectDefinition(
			null, ObjectDefinitionTestUtil.getRandomName(),
			Collections.singletonList(
				new ObjectDefinitionSettingBuilder(
				).name(
					ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
				).value(
					StringPool.TRUE
				).build()),
			ObjectDefinitionConstants.SCOPE_DEPOT, TestPropsValues.getUserId());

		super.setUp();
	}

	@Override
	protected ObjectEntry addEmptyEntry(String externalReferenceCode)
		throws Exception {

		Group group = getGroup();

		return ObjectEntryTestUtil.addEmptyObjectEntry(
			_objectDefinition, externalReferenceCode, group.getGroupId(),
			TestPropsValues.getUserId());
	}

	@Override
	protected ObjectEntry addEntry(String externalReferenceCode)
		throws Exception {

		Group group = getGroup();

		return ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, externalReferenceCode, group.getGroupId(),
			TestPropsValues.getUserId());
	}

	@Override
	protected void addStagedModels() throws Exception {
	}

	@Override
	protected void deleteEntry(ObjectEntry objectEntry) throws Exception {
		_objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	@Override
	protected ObjectEntry fetchEntry(String externalReferenceCode, long groupId)
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryTestUtil.fetchObjectEntry(
			_objectDefinition, externalReferenceCode, groupId);

		if ((objectEntry == null) || objectEntry.isInTrash()) {
			return null;
		}

		return objectEntry;
	}

	@Override
	protected String getEntityClassName() {
		return ObjectEntryTestUtil.getEntityClassName(_objectDefinition);
	}

	@Override
	protected long getPrimaryKey(ObjectEntry objectEntry) {
		return objectEntry.getObjectEntryId();
	}

	@Override
	protected int getStatus(ObjectEntry objectEntry) {
		return objectEntry.getStatus();
	}

	@Override
	protected long getUserId(ObjectEntry objectEntry) {
		return objectEntry.getUserId();
	}

	@Override
	protected boolean supportsComments() {
		return true;
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
	protected void updateEntry(ObjectEntry objectEntry) throws Exception {
		ObjectEntryTestUtil.updateObjectEntry(objectEntry);
	}

	@Override
	protected void updateModifiedDate(
			ObjectEntry objectEntry, Date modifiedDate)
		throws Exception {

		ObjectEntryTestUtil.updateModifiedDate(objectEntry, modifiedDate);
	}

	@Override
	protected void updateUserId(ObjectEntry objectEntry, long userId)
		throws Exception {

		ObjectEntryTestUtil.updateUserId(objectEntry, userId);
	}

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}