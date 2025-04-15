/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.exportimport.portlet.preferences.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.search.test.util.exportimport.BaseExportImportPortletPreferencesProcessorTestCase;
import com.liferay.portal.search.web.internal.site.facet.constants.SiteFacetPortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Gustavo Lima
 */
@RunWith(Arquillian.class)
public class SiteFacetSearchExportImportPortletPreferencesProcessorTest
	extends BaseExportImportPortletPreferencesProcessorTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected ExportImportPortletPreferencesProcessor
		getExportImportPortletPreferencesProcessor() {

		return _exportImportPortletPreferencesProcessor;
	}

	@Inject(filter = "jakarta.portlet.name=" + SiteFacetPortletKeys.SITE_FACET)
	private ExportImportPortletPreferencesProcessor
		_exportImportPortletPreferencesProcessor;

}