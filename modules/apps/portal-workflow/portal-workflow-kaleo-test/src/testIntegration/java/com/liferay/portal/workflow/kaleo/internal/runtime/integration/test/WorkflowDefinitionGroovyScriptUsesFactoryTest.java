/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.runtime.integration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.security.script.management.groovy.script.uses.factory.GroovyScriptUsesFactory;
import com.liferay.portal.security.script.management.test.rule.ScriptManagementConfigurationTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Fábio Alves
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class WorkflowDefinitionGroovyScriptUsesFactoryTest
	extends BaseWorkflowManagerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			ScriptManagementConfigurationTestRule.INSTANCE);

	@Test
	public void testHasUses() throws Exception {
		Assert.assertFalse(_groovyScriptUsesFactory.hasUses());

		_deployWorkflowDefinition("message-boards-user-stats-moderation");

		Assert.assertFalse(_groovyScriptUsesFactory.hasUses());

		_deployWorkflowDefinition("Site Member Single Approver");

		Assert.assertTrue(_groovyScriptUsesFactory.hasUses());
	}

	private void _deployWorkflowDefinition(String name) throws Exception {
		_workflowDefinitionManager.deployWorkflowDefinition(
			FileUtil.getBytes(
				getResourceInputStream(
					"single-approver-site-member-workflow-definition.xml")),
			TestPropsValues.getCompanyId(), RandomTestUtil.randomString(), name,
			StringPool.BLANK, TestPropsValues.getUserId());
	}

	@Inject(
		filter = "component.name=com.liferay.portal.workflow.web.internal.groovy.script.uses.factory.WorkflowDefinitionGroovyScriptUsesFactory"
	)
	private GroovyScriptUsesFactory _groovyScriptUsesFactory;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

}