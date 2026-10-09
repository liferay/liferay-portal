/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal;

import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.kaleo.definition.parser.WorkflowModelParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Nathaly Gomes
 */
public class DefaultWorkflowEngineImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDeployWorkflowDefinition() throws Exception {
		DefaultWorkflowEngineImpl defaultWorkflowEngineImpl =
			new DefaultWorkflowEngineImpl();

		PortletResourcePermission portletResourcePermission = Mockito.mock(
			PortletResourcePermission.class);

		ReflectionTestUtil.setFieldValue(
			defaultWorkflowEngineImpl, "_portletResourcePermission",
			portletResourcePermission);

		WorkflowModelParser workflowModelParser = Mockito.mock(
			WorkflowModelParser.class);

		ReflectionTestUtil.setFieldValue(
			defaultWorkflowEngineImpl, "_workflowModelParser",
			workflowModelParser);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			Mockito.mock(PermissionChecker.class));

		Mockito.doThrow(
			new PrincipalException.MustHavePermission(
				0, ActionKeys.ADD_DEFINITION)
		).when(
			portletResourcePermission
		).check(
			Mockito.any(PermissionChecker.class), Mockito.anyLong(),
			Mockito.eq(ActionKeys.ADD_DEFINITION)
		);

		Assert.assertThrows(
			WorkflowException.class,
			() -> defaultWorkflowEngineImpl.deployWorkflowDefinition(
				null, null, null, null, false,
				new ByteArrayInputStream(new byte[0]), new ServiceContext()));

		Mockito.verify(
			portletResourcePermission
		).check(
			Mockito.any(PermissionChecker.class), Mockito.anyLong(),
			Mockito.eq(ActionKeys.ADD_DEFINITION)
		);

		Mockito.verify(
			workflowModelParser, Mockito.never()
		).parse(
			Mockito.anyLong(), Mockito.any(InputStream.class)
		);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);
	}

}