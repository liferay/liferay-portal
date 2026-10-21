/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser;

import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.JavaMethodSignature;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Get;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Info;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.OpenAPIYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Operation;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daniel Raposo
 */
public class ResourceOpenAPIParserTest {

	@Test
	public void testGetFeatureFlag() {
		Assert.assertEquals("LPD-1", _getFeatureFlag("LPD-1", null));
		Assert.assertEquals("LPD-2", _getFeatureFlag(null, "LPD-2"));
		Assert.assertNull(_getFeatureFlag(null, null));

		try {
			_getFeatureFlag("LPD-1", "LPD-2");

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Operation \"getTestEntity\" declares feature flag \"LPD-2\" " +
					"but its API already declares feature flag \"LPD-1\"",
				illegalArgumentException.getMessage());
		}
	}

	private String _getFeatureFlag(
		String infoFeatureFlag, String operationFeatureFlag) {

		Operation operation = new Get();

		OpenAPIYAML openAPIYAML = new OpenAPIYAML();

		Info info = new Info();

		info.setFeatureFlag(infoFeatureFlag);

		openAPIYAML.setInfo(info);

		operation.setFeatureFlag(operationFeatureFlag);

		operation.setOperationId("getTestEntity");

		return ResourceOpenAPIParser.getFeatureFlag(
			new JavaMethodSignature(
				null, null, operation, null, null, Collections.emptyList(),
				null, null, null),
			openAPIYAML);
	}

}