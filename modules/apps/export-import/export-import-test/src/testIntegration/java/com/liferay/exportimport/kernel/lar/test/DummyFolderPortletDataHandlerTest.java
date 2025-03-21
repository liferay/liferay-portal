/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.test.util.constants.DummyFolderPortletKeys;
import com.liferay.exportimport.test.util.exportimport.data.handler.DummyFolderPortletDataHandler;
import com.liferay.exportimport.test.util.lar.BasePortletDataHandlerTestCase;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.PortletPreferencesImpl;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class DummyFolderPortletDataHandlerTest
	extends BasePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	@Test
	public void testExportImportData() throws Exception {
		DummyFolderPortletDataHandler dummyFolderPortletDataHandler =
			(DummyFolderPortletDataHandler)portletDataHandler;

		try {
			dummyFolderPortletDataHandler.setEnabled(true);

			super.testExportImportData();

			PortletPreferences portletPreferences =
				new PortletPreferencesImpl();

			initContext();

			portletDataContext.setEndDate(getEndDate());

			dummyFolderPortletDataHandler.setEnabled(false);

			Assert.assertNull(
				portletDataHandler.exportData(
					portletDataContext, portletId, portletPreferences));

			Assert.assertNull(
				portletDataHandler.importData(
					portletDataContext, portletId, portletPreferences, null));
		}
		finally {
			dummyFolderPortletDataHandler.setEnabled(true);
		}
	}

	@Override
	protected void addStagedModels() throws Exception {
	}

	@Override
	protected DataLevel getDataLevel() {
		return DataLevel.SITE;
	}

	@Override
	protected String getPortletId() {
		return DummyFolderPortletKeys.DUMMY_FOLDER;
	}

	@Override
	protected boolean isDataPortalLevel() {
		return false;
	}

	@Override
	protected boolean isDataPortletInstanceLevel() {
		return false;
	}

	@Override
	protected boolean isDataSiteLevel() {
		return true;
	}

	@Override
	protected boolean isDisplayPortlet() {
		return false;
	}

}