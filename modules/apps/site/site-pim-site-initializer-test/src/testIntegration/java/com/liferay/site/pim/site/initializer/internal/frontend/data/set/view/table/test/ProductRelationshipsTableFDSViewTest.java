/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.view.table.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewRegistry;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaField;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class ProductRelationshipsTableFDSViewTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetFDSTableSchema() {
		List<FDSView> fdsViews = _fdsViewRegistry.getFDSViews(
			"com.liferay.site.pim.site.initializer-product-relationships");

		FDSView fdsView = fdsViews.get(0);

		FDSTableSchema fdsTableSchema = fdsView.getFDSTableSchema(LocaleUtil.US);

		Map<String, FDSTableSchemaField> fdsTableSchemaFieldsMap =
			fdsTableSchema.getFDSTableSchemaFieldsMap();

		Assert.assertEquals(
			Arrays.asList("name", "code", "type", "status"),
			new ArrayList<>(fdsTableSchemaFieldsMap.keySet()));

		FDSTableSchemaField nameFDSTableSchemaField =
			fdsTableSchemaFieldsMap.get("name");

		Assert.assertEquals(
			"nameTableCellRenderer",
			nameFDSTableSchemaField.getContentRenderer());

		FDSTableSchemaField statusFDSTableSchemaField =
			fdsTableSchemaFieldsMap.get("status");

		Assert.assertEquals(
			"status", statusFDSTableSchemaField.getContentRenderer());
	}

	@Inject
	private FDSViewRegistry _fdsViewRegistry;

}
