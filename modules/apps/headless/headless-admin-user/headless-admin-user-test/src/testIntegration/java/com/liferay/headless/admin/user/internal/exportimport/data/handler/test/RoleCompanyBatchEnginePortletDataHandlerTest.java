/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@RunWith(Arquillian.class)
public class RoleCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testExportImportData() throws Exception {
		super.testExportImportData();

		_testExportImportDataWithObjectDefinitionPermissions();
	}

	@Override
	protected void addEmptyEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		_roleLocalService.getOrAddEmptyRole(
			externalReferenceCode, group.getCompanyId(),
			TestPropsValues.getUserId(), null, 0, RandomTestUtil.randomString(),
			RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_roleLocalService.addRole(
			externalReferenceCode, TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			null, RoleConstants.TYPE_REGULAR, null,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_roleLocalService.deleteRole(_getRole(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		Role role = _getRole(externalReferenceCode, groupId);

		return role.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return com.liferay.headless.admin.user.dto.v1_0.Role.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		Role role = _getRole(externalReferenceCode, groupId);

		return role.getRoleId();
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		Role role = _getRole(externalReferenceCode, groupId);

		return role.getStatus();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		Role role = _roleLocalService.fetchRoleByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());

		if (role == null) {
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

		Role role = _getRole(externalReferenceCode, groupId);

		role.setUserId(userId);

		_roleLocalService.updateRole(role);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Role role = _getRole(externalReferenceCode, groupId);

		_roleLocalService.updateRole(
			role.getExternalReferenceCode(), role.getRoleId(), role.getName(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			role.getDescriptionMap(), role.getSubtype(),
			ServiceContextTestUtil.getServiceContext(
				role.getCompanyId(), groupId, role.getUserId()));
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		Role role = _getRole(externalReferenceCode, groupId);

		role.setModifiedDate(modifiedDate);

		_roleLocalService.updateRole(role);
	}

	private void _addResourcePermission(String name, Role role, String actionId)
		throws Exception {

		_resourcePermissionLocalService.addResourcePermission(
			role.getCompanyId(), name, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(role.getCompanyId()), role.getRoleId(), actionId);
	}

	private Role _getRole(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _roleLocalService.getRoleByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());
	}

	private boolean _hasResourcePermission(
			String name, Role role, String actionId)
		throws Exception {

		return _resourcePermissionLocalService.hasResourcePermission(
			role.getCompanyId(), name, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(role.getCompanyId()), role.getRoleId(), actionId);
	}

	private void _testExportImportDataWithObjectDefinitionPermissions()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();
		Group group = getGroup();

		addEntry(externalReferenceCode, group.getGroupId());

		_objectDefinition1 = ObjectDefinitionTestUtil.publishObjectDefinition();
		Role role = _getRole(externalReferenceCode, group.getGroupId());

		_addResourcePermission(
			_objectDefinition1.getClassName(), role, ActionKeys.VIEW);
		_addResourcePermission(
			_objectDefinition1.getPortletId(), role,
			ActionKeys.ACCESS_IN_CONTROL_PANEL);
		_addResourcePermission(
			_objectDefinition1.getResourceName(), role,
			ObjectActionKeys.ADD_OBJECT_ENTRY);

		_objectDefinition2 = ObjectDefinitionTestUtil.publishObjectDefinition();

		_addResourcePermission(
			_objectDefinition2.getResourceName(), role,
			ObjectActionKeys.ADD_OBJECT_ENTRY);

		User targetCompanyAdminUser = getTargetCompanyAdminUser();

		ObjectDefinition targetObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				ObjectDefinitionConstants.SCOPE_COMPANY,
				targetCompanyAdminUser.getUserId());

		targetObjectDefinition =
			_objectDefinitionLocalService.updateExternalReferenceCode(
				targetObjectDefinition.getObjectDefinitionId(),
				_objectDefinition1.getExternalReferenceCode());

		exportImport(
			null,
			HashMapBuilder.put(
				PortletDataHandlerKeys.PORTLET_DATA,
				new String[] {Boolean.TRUE.toString()}
			).put(
				PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
					getPortletId(),
				new String[] {Boolean.TRUE.toString()}
			).build(),
			null);

		Group targetGroup = getTargetGroup();

		Assert.assertTrue(
			ListUtil.exists(
				_exportImportReportEntryLocalService.
					getExportImportReportEntries(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				exportImportReportEntry -> {
					String errorMessage =
						exportImportReportEntry.getErrorMessage();

					if (Validator.isNotNull(errorMessage) &&
						errorMessage.contains(
							StringBundler.concat(
								ObjectConstants.RESOURCE_NAME, StringPool.POUND,
								_objectDefinition2.getExternalReferenceCode(),
								StringPool.POUND,
								ObjectActionKeys.ADD_OBJECT_ENTRY)) &&
						(exportImportReportEntry.getCompanyId() ==
							targetGroup.getCompanyId()) &&
						(exportImportReportEntry.getType() ==
							ExportImportReportEntryConstants.TYPE_WARNING) &&
						Objects.equals(
							exportImportReportEntry.
								getClassExternalReferenceCode(),
							externalReferenceCode)) {

						return true;
					}

					return false;
				}));

		Role targetRole = _getRole(
			externalReferenceCode, targetGroup.getGroupId());

		Assert.assertFalse(
			_hasResourcePermission(
				_objectDefinition1.getClassName(), targetRole,
				ActionKeys.VIEW));
		Assert.assertTrue(
			_hasResourcePermission(
				targetObjectDefinition.getClassName(), targetRole,
				ActionKeys.VIEW));
		Assert.assertFalse(
			_hasResourcePermission(
				_objectDefinition1.getPortletId(), targetRole,
				ActionKeys.ACCESS_IN_CONTROL_PANEL));
		Assert.assertTrue(
			_hasResourcePermission(
				targetObjectDefinition.getPortletId(), targetRole,
				ActionKeys.ACCESS_IN_CONTROL_PANEL));
		Assert.assertFalse(
			_hasResourcePermission(
				_objectDefinition1.getResourceName(), targetRole,
				ObjectActionKeys.ADD_OBJECT_ENTRY));
		Assert.assertTrue(
			_hasResourcePermission(
				targetObjectDefinition.getResourceName(), targetRole,
				ObjectActionKeys.ADD_OBJECT_ENTRY));
	}

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}