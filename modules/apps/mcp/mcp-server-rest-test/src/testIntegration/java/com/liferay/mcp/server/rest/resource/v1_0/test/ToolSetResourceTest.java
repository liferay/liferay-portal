/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.client.dto.v1_0.ToolSet;
import com.liferay.mcp.server.rest.client.pagination.Page;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@FeatureFlag("LPD-63311")
@RunWith(Arquillian.class)
public class ToolSetResourceTest extends BaseToolSetResourceTestCase {

	@Override
	@Test
	public void testGetToolSetsPage() throws Exception {
		_assertToolSet(
			toolSet ->
				Objects.equals(toolSet.getName(), "mcp-server-v1.0") &&
				Validator.isNotNull(toolSet.getDescription()));

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		String restContextPath = objectDefinition.getRESTContextPath();

		_assertToolSet(
			toolSet -> Objects.equals(
				"c-" + restContextPath.substring(3), toolSet.getName()));

		_assertToolSet(
			toolSet -> Objects.equals(
				toolSet.getName(), "headless-commerce-admin-pricing-v1.0"));
		_assertToolSet(
			toolSet -> Objects.equals(
				toolSet.getName(), "headless-commerce-admin-pricing-v2.0"));
	}

	private void _assertToolSet(Predicate<ToolSet> predicate) throws Exception {
		Page<ToolSet> toolSetsPage = toolSetResource.getToolSetsPage();

		Assert.assertTrue(
			ListUtil.exists(
				new ArrayList<>(toolSetsPage.getItems()), predicate));
	}

}