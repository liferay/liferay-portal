/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.oas;

import com.liferay.talend.BaseTestCase;

import java.util.Set;

import org.hamcrest.Matchers;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Igor Beslic
 */
public class OASExplorerTest extends BaseTestCase {

	@Test
	public void testGetEntitySchemaNames() {
		OASExplorer oasExplorer = new OASExplorer();

		Set<String> entitySchemaNames = oasExplorer.getEntitySchemaNames(
			readObject("openapi.json"));

		Assert.assertThat(entitySchemaNames, Matchers.hasSize(17));

		Assert.assertThat(
			entitySchemaNames,
			Matchers.hasItems(
				"Attachment", "Category", "Option", "OptionCategory",
				"ProductOption", "ProductShippingConfiguration",
				"ProductSubscriptionConfiguration", "SchemaBuilderBreaker",
				"Sku", "Specification"));
	}

}