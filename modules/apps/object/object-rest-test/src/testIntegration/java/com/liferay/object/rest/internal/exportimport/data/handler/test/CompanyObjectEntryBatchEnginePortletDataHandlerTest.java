/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.internal.exportimport.data.handler.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
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
public class CompanyObjectEntryBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase<ObjectEntry> {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		_objectDefinition = ObjectEntryTestUtil.publishObjectDefinition(
			null, ObjectDefinitionTestUtil.getRandomName(),
			Collections.emptyList(), ObjectDefinitionConstants.SCOPE_COMPANY,
			TestPropsValues.getUserId());

		super.setUp();

		_targetObjectDefinition = ObjectEntryTestUtil.publishObjectDefinition(
			_objectDefinition.getExternalReferenceCode(),
			_objectDefinition.getShortName(), Collections.emptyList(),
			ObjectDefinitionConstants.SCOPE_COMPANY, targetUser.getUserId());
	}

	@Override
	protected void addEmptyEntry(String externalReferenceCode)
		throws Exception {

		ObjectEntryTestUtil.addEmptyObjectEntry(
			_objectDefinition, externalReferenceCode, group.getGroupId(),
			TestPropsValues.getUserId());
	}

	@Override
	protected void addEntry(String externalReferenceCode) throws Exception {
		ObjectEntryTestUtil.addObjectEntry(
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
	protected long getCreatorUserId(ObjectEntry objectEntry) {
		return objectEntry.getUserId();
	}

	@Override
	protected ExportImportVulcanBatchEngineTaskItemDelegate<?>
			getExportImportVulcanBatchEngineTaskItemDelegate()
		throws Exception {

		return getExportImportVulcanBatchEngineTaskItemDelegate(
			ObjectEntryResource.class,
			ObjectEntryTestUtil.getFilterString(_objectDefinition));
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
	protected String getTargetModelClassName() {
		return _targetObjectDefinition.getClassName();
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

	@DeleteAfterTestRun
	private ObjectDefinition _targetObjectDefinition;

}