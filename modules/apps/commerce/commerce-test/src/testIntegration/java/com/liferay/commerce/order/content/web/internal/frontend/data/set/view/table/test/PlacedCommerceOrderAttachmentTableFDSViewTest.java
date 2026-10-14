/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.view.table.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceFragmentRendererKeys;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewRegistry;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaField;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tancredi Covioli
 */
@RunWith(Arquillian.class)
public class PlacedCommerceOrderAttachmentTableFDSViewTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		List<FDSView> fdsViews = _fdsViewRegistry.getFDSViews(
			CommerceFragmentRendererKeys.ORDER_ATTACHMENTS_DATA_SET +
				"-placedOrderAttachments");

		FDSView fdsView = fdsViews.get(0);

		FDSTableSchema fdsTableSchema = fdsView.getFDSTableSchema(
			LocaleUtil.US);

		_fdsTableSchemaFieldsMap = fdsTableSchema.getFDSTableSchemaFieldsMap();
	}

	@Test
	public void testGetFDSTableSchemaField() throws Exception {
		_testGetFDSTableSchemaField(
			"dateTime", "modified-date", "dateModified", true);
		_testGetFDSTableSchemaField(
			"dsRequestStatusDataRenderer", "signature-status",
			"dsRequestStatus", false);
		_testGetFDSTableSchemaField(null, "extension", "extension", false);
		_testGetFDSTableSchemaField(null, "priority", "priority", true);
		_testGetFDSTableSchemaField(null, "title", "title", true);
		_testGetFDSTableSchemaField(null, "type", "typeLabel", false);
	}

	private void _testGetFDSTableSchemaField(
		String expectedContentRenderer, String expectedLabel, String fieldName,
		boolean sortable) {

		FDSTableSchemaField fdsTableSchemaField = _fdsTableSchemaFieldsMap.get(
			fieldName);

		Assert.assertEquals(
			expectedContentRenderer, fdsTableSchemaField.getContentRenderer());
		Assert.assertEquals(expectedLabel, fdsTableSchemaField.getLabel());
		Assert.assertEquals(sortable, fdsTableSchemaField.isSortable());
	}

	private Map<String, FDSTableSchemaField> _fdsTableSchemaFieldsMap;

	@Inject
	private FDSViewRegistry _fdsViewRegistry;

}