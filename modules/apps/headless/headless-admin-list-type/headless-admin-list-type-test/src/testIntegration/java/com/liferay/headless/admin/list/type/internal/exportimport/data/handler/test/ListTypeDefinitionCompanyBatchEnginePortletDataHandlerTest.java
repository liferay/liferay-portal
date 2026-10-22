/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.Date;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class ListTypeDefinitionCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEmptyEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		_listTypeDefinitionLocalService.getOrAddEmptyListTypeDefinition(
			externalReferenceCode, group.getCompanyId(),
			TestPropsValues.getUserId(), false);
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_listTypeDefinitionLocalService.addListTypeDefinition(
			externalReferenceCode, TestPropsValues.getUserId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			false, Collections.emptyList(),
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_listTypeDefinitionLocalService.deleteListTypeDefinition(
			_getListTypeDefinition(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		ListTypeDefinition listTypeDefinition = _getListTypeDefinition(
			externalReferenceCode, groupId);

		return listTypeDefinition.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition.
			class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		ListTypeDefinition listTypeDefinition = _getListTypeDefinition(
			externalReferenceCode, groupId);

		return listTypeDefinition.getListTypeDefinitionId();
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		ListTypeDefinition listTypeDefinition = _getListTypeDefinition(
			externalReferenceCode, groupId);

		return listTypeDefinition.getStatus();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					externalReferenceCode, group.getCompanyId());

		if (listTypeDefinition == null) {
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

		ListTypeDefinition listTypeDefinition = _getListTypeDefinition(
			externalReferenceCode, groupId);

		listTypeDefinition.setUserId(userId);

		_listTypeDefinitionLocalService.updateListTypeDefinition(
			listTypeDefinition);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		ListTypeDefinition listTypeDefinition = _getListTypeDefinition(
			externalReferenceCode, groupId);

		_listTypeDefinitionLocalService.updateListTypeDefinition(
			listTypeDefinition.getExternalReferenceCode(),
			listTypeDefinition.getListTypeDefinitionId(),
			listTypeDefinition.getUserId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyList(),
			ServiceContextTestUtil.getServiceContext(
				listTypeDefinition.getCompanyId(), groupId,
				listTypeDefinition.getUserId()));
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		ListTypeDefinition listTypeDefinition = _getListTypeDefinition(
			externalReferenceCode, groupId);

		listTypeDefinition.setModifiedDate(modifiedDate);

		_listTypeDefinitionLocalService.updateListTypeDefinition(
			listTypeDefinition);
	}

	private ListTypeDefinition _getListTypeDefinition(
			String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _listTypeDefinitionLocalService.
			getListTypeDefinitionByExternalReferenceCode(
				externalReferenceCode, group.getCompanyId());
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

}