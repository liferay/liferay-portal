/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.internal.exportimport.data.handler.test.util.ObjectEntryTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
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
public class ObjectEntryCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

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

		User targetCompanyAdminUser = getTargetCompanyAdminUser();

		_targetObjectDefinition = ObjectEntryTestUtil.publishObjectDefinition(
			_objectDefinition.getExternalReferenceCode(),
			_objectDefinition.getShortName(), Collections.emptyList(),
			ObjectDefinitionConstants.SCOPE_COMPANY,
			targetCompanyAdminUser.getUserId());

		super.setUp();
	}

	@Override
	protected void addEmptyEntry(String externalReferenceCode)
		throws Exception {

		Group group = getGroup();

		ObjectEntryTestUtil.addEmptyObjectEntry(
			_objectDefinition, externalReferenceCode, group.getGroupId(),
			TestPropsValues.getUserId());
	}

	@Override
	protected void addEntry(String externalReferenceCode) throws Exception {
		Group group = getGroup();

		ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, externalReferenceCode, group.getGroupId(),
			TestPropsValues.getUserId());
	}

	@Override
	protected void addStagedModels() throws Exception {
	}

	@Override
	protected void deleteEntry(String externalReferenceCode) throws Exception {
		Group group = getGroup();

		ObjectEntryTestUtil.deleteObjectEntry(
			_objectDefinition, externalReferenceCode, group.getGroupId());
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		return ObjectEntryTestUtil.getCreatorUserId(
			_objectDefinition, externalReferenceCode, groupId);
	}

	@Override
	protected String getEntityClassName() {
		return ObjectEntryTestUtil.getEntityClassName(_objectDefinition);
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		return ObjectEntryTestUtil.getObjectEntryId(
			_objectDefinition, externalReferenceCode, groupId);
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		return ObjectEntryTestUtil.getStatus(
			_objectDefinition, externalReferenceCode, groupId);
	}

	@Override
	protected String getTargetModelClassName() {
		return _targetObjectDefinition.getClassName();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		return ObjectEntryTestUtil.hasObjectEntry(
			_objectDefinition, externalReferenceCode, groupId);
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
	protected void updateCreatorUserId(
			String externalReferenceCode, long userId)
		throws Exception {

		Group group = getGroup();

		ObjectEntryTestUtil.updateCreatorUserId(
			_objectDefinition, externalReferenceCode, group.getGroupId(),
			userId);
	}

	@Override
	protected void updateEntry(String externalReferenceCode) throws Exception {
		Group group = getGroup();

		ObjectEntryTestUtil.updateObjectEntry(
			_objectDefinition, externalReferenceCode, group.getGroupId());
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, Date modifiedDate)
		throws Exception {

		Group group = getGroup();

		ObjectEntryTestUtil.updateModifiedDate(
			_objectDefinition, externalReferenceCode, group.getGroupId(),
			modifiedDate);
	}

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@DeleteAfterTestRun
	private ObjectDefinition _targetObjectDefinition;

}