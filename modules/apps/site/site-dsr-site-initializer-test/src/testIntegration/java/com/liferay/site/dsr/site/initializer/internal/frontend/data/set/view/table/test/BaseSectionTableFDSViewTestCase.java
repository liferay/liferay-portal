/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.frontend.data.set.view.table.test;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewRegistry;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaField;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;

/**
 * @author Stefano Motta
 */
public abstract class BaseSectionTableFDSViewTestCase {

	@Before
	public void setUp() throws Exception {
		List<FDSView> fdsViews = _fdsViewRegistry.getFDSViews(getFDSName());

		FDSView fdsView = fdsViews.get(0);

		FDSTableSchema fdsTableSchema = fdsView.getFDSTableSchema(
			LocaleUtil.US);

		fdsTableSchemaFieldsMap = fdsTableSchema.getFDSTableSchemaFieldsMap();
	}

	protected void assertFDSTableSchemaField(
		String expectedActionId, String expectedContentRenderer,
		String expectedLabel, String fieldName) {

		FDSTableSchemaField fdsTableSchemaField = fdsTableSchemaFieldsMap.get(
			fieldName);

		Assert.assertEquals(
			expectedActionId, fdsTableSchemaField.getActionId());
		Assert.assertEquals(
			expectedContentRenderer, fdsTableSchemaField.getContentRenderer());
		Assert.assertEquals(expectedLabel, fdsTableSchemaField.getLabel());
	}

	protected abstract String getFDSName();

	protected Map<String, FDSTableSchemaField> fdsTableSchemaFieldsMap;

	@Inject
	private FDSViewRegistry _fdsViewRegistry;

}