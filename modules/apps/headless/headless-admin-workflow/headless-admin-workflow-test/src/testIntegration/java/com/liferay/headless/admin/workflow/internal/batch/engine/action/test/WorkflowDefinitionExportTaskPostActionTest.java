/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.batch.engine.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.action.ExportTaskPostAction;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowDefinition;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
public class WorkflowDefinitionExportTaskPostActionTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-49856"))
	@Test
	@TestInfo("LPD-112332")
	public void testRun() throws Exception {
		BatchEngineExportTask batchEngineExportTask =
			_batchEngineExportTaskLocalService.createBatchEngineExportTask(
				RandomTestUtil.randomLong());

		batchEngineExportTask.setCompanyId(TestPropsValues.getCompanyId());

		WorkflowDefinition workflowDefinition = new WorkflowDefinition();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);
		User user = UserTestUtil.addUser();

		workflowDefinition.setContent(
			StringUtil.replace(
				StringUtil.read(
					getClass(),
					"dependencies/role-and-user-recipients-and-assignments-" +
						"workflow-definition.json"),
				new String[] {
					"[$ROLE_ID$]", "[$USER_ID$]", "[$WORKFLOW_DEFINITION_NAME$]"
				},
				new String[] {
					String.valueOf(role.getRoleId()),
					String.valueOf(user.getUserId()),
					RandomTestUtil.randomString()
				}));

		ExportImportThreadLocal.setPortletExportInProcess(true);

		try {
			_exportTaskPostAction.run(
				batchEngineExportTask, null, workflowDefinition);
		}
		finally {
			ExportImportThreadLocal.setPortletExportInProcess(false);
		}

		String content = workflowDefinition.getContent();

		Assert.assertEquals(
			3, StringUtil.count(content, role.getExternalReferenceCode()));
		Assert.assertEquals(
			2, StringUtil.count(content, user.getExternalReferenceCode()));

		_roleLocalService.deleteRole(role);
		_userLocalService.deleteUser(user);
	}

	@Inject
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	@Inject(
		filter = "component.name=com.liferay.headless.admin.workflow.internal.batch.engine.action.WorkflowDefinitionExportTaskPostAction"
	)
	private ExportTaskPostAction _exportTaskPostAction;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}