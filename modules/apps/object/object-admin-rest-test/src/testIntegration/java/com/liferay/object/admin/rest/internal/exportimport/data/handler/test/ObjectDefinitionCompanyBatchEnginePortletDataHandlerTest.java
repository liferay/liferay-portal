/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;
import java.util.Date;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEmptyEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		_objectDefinitionLocalService.getOrAddEmptyObjectDefinition(
			externalReferenceCode, group.getCompanyId(),
			TestPropsValues.getUserId(), 0, true,
			ObjectDefinitionConstants.SCOPE_COMPANY, false);
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_objectDefinitionLocalService.addCustomObjectDefinition(
			externalReferenceCode, TestPropsValues.getUserId(), 0, null, null,
			true, true, false, false, true, false, false, false, false, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectDefinitionTestUtil.getRandomName(), null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			true, ObjectDefinitionConstants.SCOPE_COMPANY,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(), Collections.emptyList(),
			Collections.emptyList(),
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_objectDefinitionLocalService.deleteObjectDefinition(
			_getObjectDefinition(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		ObjectDefinition objectDefinition = _getObjectDefinition(
			externalReferenceCode, groupId);

		return objectDefinition.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition.class.
			getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		ObjectDefinition objectDefinition = _getObjectDefinition(
			externalReferenceCode, groupId);

		return objectDefinition.getObjectDefinitionId();
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		ObjectDefinition objectDefinition = _getObjectDefinition(
			externalReferenceCode, groupId);

		return objectDefinition.getStatus();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, group.getCompanyId());

		if (objectDefinition == null) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean supportsComments() {
		return false;
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
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		ObjectDefinition objectDefinition = _getObjectDefinition(
			externalReferenceCode, groupId);

		objectDefinition.setUserId(userId);

		_objectDefinitionLocalService.updateObjectDefinition(objectDefinition);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		ObjectDefinition objectDefinition = _getObjectDefinition(
			externalReferenceCode, groupId);

		String name = objectDefinition.getShortName();

		if (!objectDefinition.isApproved()) {
			name = ObjectDefinitionTestUtil.getRandomName();
		}

		objectDefinition =
			_objectDefinitionLocalService.updateCustomObjectDefinition(
				objectDefinition.getExternalReferenceCode(),
				objectDefinition.getObjectDefinitionId(),
				objectDefinition.getAccountEntryRestrictedObjectFieldId(),
				objectDefinition.getDescriptionObjectFieldId(),
				objectDefinition.getObjectFolderId(),
				objectDefinition.getTitleObjectFieldId(),
				objectDefinition.isAccountEntryRestricted(),
				objectDefinition.isActive(), objectDefinition.getClassName(),
				objectDefinition.getDescriptionMap(),
				objectDefinition.isEnableCategorization(),
				objectDefinition.isEnableComments(),
				objectDefinition.isEnableFormContainer(),
				objectDefinition.isEnableFriendlyURLCustomization(),
				objectDefinition.isEnableIndexSearch(),
				objectDefinition.isEnableObjectEntryDraft(),
				objectDefinition.isEnableObjectEntryHistory(),
				objectDefinition.isEnableObjectEntrySchedule(),
				objectDefinition.isEnableObjectEntrySubscription(),
				objectDefinition.isEnableObjectEntryVersioning(),
				objectDefinition.getFriendlyURLSeparator(),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				name, objectDefinition.getPanelAppOrder(),
				objectDefinition.getPanelCategoryKey(),
				objectDefinition.isPortlet(),
				objectDefinition.getPluralLabelMap(),
				objectDefinition.getScope(), objectDefinition.getStatus(),
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(),
				ServiceContextTestUtil.getServiceContext(
					groupId, objectDefinition.getUserId()));

		if (objectDefinition.isApproved()) {
			return;
		}

		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				objectDefinition.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"x" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				false
			).build());

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		ObjectDefinition objectDefinition = _getObjectDefinition(
			externalReferenceCode, groupId);

		objectDefinition.setModifiedDate(modifiedDate);

		_objectDefinitionLocalService.updateObjectDefinition(objectDefinition);
	}

	private ObjectDefinition _getObjectDefinition(
			String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _objectDefinitionLocalService.
			getObjectDefinitionByExternalReferenceCode(
				externalReferenceCode, group.getCompanyId());
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}