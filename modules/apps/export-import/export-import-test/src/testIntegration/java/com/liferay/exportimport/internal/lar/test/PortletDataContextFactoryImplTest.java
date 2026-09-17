/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabor Komaromi
 */
@RunWith(Arquillian.class)
public class PortletDataContextFactoryImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testCreatePreparePortletDataContext() throws Exception {
		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		PortletDataContext portletDataContext =
			_testCreatePreparePortletDataContext(null);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			_group.getCompanyId(), JournalPortletKeys.JOURNAL);

		PortletDataHandler portletDataHandler =
			portlet.getPortletDataHandlerInstance();

		portletDataHandler.prepareManifestSummary(portletDataContext);

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		Assert.assertEquals(
			1,
			manifestSummary.getModelAdditionCount(
				new StagedModelType(JournalArticle.class)));

		_testCreatePreparePortletDataContext(ExportImportDateUtil.RANGE_ALL);
		_testCreatePreparePortletDataContext(
			ExportImportDateUtil.RANGE_DATE_RANGE);
		_testCreatePreparePortletDataContext(
			ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE);
		_testCreatePreparePortletDataContext(ExportImportDateUtil.RANGE_LAST);
	}

	private PortletDataContext _testCreatePreparePortletDataContext(
			String range)
		throws Exception {

		Date endDate = new Date();

		Date startDate = new Date(endDate.getTime() - Time.HOUR);

		PortletDataContext portletDataContext =
			PortletDataContextFactoryUtil.createPreparePortletDataContext(
				_group.getCompanyId(), _group.getGroupId(), range, startDate,
				endDate);

		Assert.assertEquals(endDate, portletDataContext.getEndDate());
		Assert.assertEquals(
			range,
			MapUtil.getString(
				portletDataContext.getParameterMap(),
				ExportImportDateUtil.RANGE, null));
		Assert.assertEquals(startDate, portletDataContext.getStartDate());

		return portletDataContext;
	}

	@DeleteAfterTestRun
	private Group _group;

}