/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.groovy.script.uses.factory.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectActionTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.security.script.management.groovy.script.uses.factory.GroovyScriptUsesFactory;
import com.liferay.portal.security.script.management.test.rule.ScriptManagementConfigurationTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Fábio Alves
 */
@RunWith(Arquillian.class)
public class ObjectActionGroovyScriptUsesFactoryTest {

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

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectActionTestUtil.addObjectAction(
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, _objectDefinition,
			UnicodePropertiesBuilder.put(
				"script", "return true;"
			).build());

		Assert.assertTrue(_groovyScriptUsesFactory.hasUses());
	}

	@Inject(
		filter = "component.name=com.liferay.object.web.internal.groovy.script.uses.factory.ObjectActionGroovyScriptUsesFactory"
	)
	private GroovyScriptUsesFactory _groovyScriptUsesFactory;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

}