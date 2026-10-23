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
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class PIMConnectorTableFDSViewTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		List<FDSView> fdsViews = _fdsViewRegistry.getFDSViews(
			"com.liferay.site.pim.site.initializer-connectors");

		FDSView fdsView = fdsViews.get(0);

		FDSTableSchema fdsTableSchema = fdsView.getFDSTableSchema(
			LocaleUtil.US);

		_fdsTableSchemaFieldsMap = fdsTableSchema.getFDSTableSchemaFieldsMap();
	}

	@Test
	public void testGetFDSTableSchema() {
		Assert.assertEquals(
			Arrays.asList(
				"name", "key", "dateModified", "active", "mappingStatus",
				"lastExecutionStatus", "lastExecutionDate"),
			new ArrayList<>(_fdsTableSchemaFieldsMap.keySet()));

		_testGetFDSTableSchema("dateTime", "dateModified", "modified", true);
		_testGetFDSTableSchema(
			"dateTime", "lastExecutionDate", "last-execution-date", false);
		_testGetFDSTableSchema(
			"lastExecutionStatusTableCellRenderer", "lastExecutionStatus",
			"last-execution-status", false);
		_testGetFDSTableSchema(
			"mappingStatusTableCellRenderer", "mappingStatus", "mapping-status",
			false);
		_testGetFDSTableSchema("nameTableCellRenderer", "name", "name", false);
		_testGetFDSTableSchema(
			"statusTableCellRenderer", "active", "status", false);
		_testGetFDSTableSchema(null, "key", "connector", false);
	}

	private void _testGetFDSTableSchema(
		String contentRenderer, String fieldName, String label,
		boolean sortable) {

		FDSTableSchemaField fdsTableSchemaField = _fdsTableSchemaFieldsMap.get(
			fieldName);

		Assert.assertEquals(
			contentRenderer, fdsTableSchemaField.getContentRenderer());
		Assert.assertEquals(label, fdsTableSchemaField.getLabel());
		Assert.assertEquals(sortable, fdsTableSchemaField.isSortable());
	}

	private Map<String, FDSTableSchemaField> _fdsTableSchemaFieldsMap;

	@Inject
	private FDSViewRegistry _fdsViewRegistry;

}