/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseDepotBatchEnginePortletDataHandlerTestCase;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.internal.exportimport.data.handler.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
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
	extends BaseDepotBatchEnginePortletDataHandlerTestCase {

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
	protected String addEmptyEntry(long groupId, long userId) throws Exception {
		ObjectEntry objectEntry = ObjectEntryTestUtil.addEmptyObjectEntry(
			_objectDefinition, groupId, userId);

		return objectEntry.getExternalReferenceCode();
	}

	@Override
	protected String addEntry(long groupId, Date modifiedDate, long userId)
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, groupId, modifiedDate, userId);

		return objectEntry.getExternalReferenceCode();
	}

	@Override
	protected void addStagedModels() throws Exception {
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_objectEntryLocalService.deleteObjectEntry(
			_fetchObjectEntry(externalReferenceCode, groupId));
	}

	@Override
	protected Object fetchEntry(String externalReferenceCode, long groupId)
		throws Exception {

		ObjectEntry objectEntry = _fetchObjectEntry(
			externalReferenceCode, groupId);

		if ((objectEntry == null) || objectEntry.isInTrash()) {
			return null;
		}

		return objectEntry;
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		ObjectEntry objectEntry = _fetchObjectEntry(
			externalReferenceCode, groupId);

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
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		ObjectEntry objectEntry = _fetchObjectEntry(
			externalReferenceCode, groupId);

		return objectEntry.getObjectEntryId();
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		ObjectEntry objectEntry = _fetchObjectEntry(
			externalReferenceCode, groupId);

		return objectEntry.getStatus();
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
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		ObjectEntryTestUtil.updateObjectEntry(
			_fetchObjectEntry(externalReferenceCode, groupId));
	}

	private ObjectEntry _fetchObjectEntry(
			String externalReferenceCode, long groupId)
		throws Exception {

		return ObjectEntryTestUtil.fetchObjectEntry(
			_objectDefinition, externalReferenceCode, groupId);
	}

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}