/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.controller.PortletExportController;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.test.util.RatingsTestUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class MBExportImportPortletPreferencesProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group.getGroupId());

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), _layout, MBPortletKeys.MESSAGE_BOARDS,
			"column-1", new HashMap<String, String[]>());

		_portletDataContextExport =
			ExportImportTestUtil.getExportPortletDataContext(
				_group.getGroupId());

		_portletDataContextExport.setPlid(_layout.getPlid());
		_portletDataContextExport.setPortletId(MBPortletKeys.MESSAGE_BOARDS);

		_portletDataContextImport =
			ExportImportTestUtil.getImportPortletDataContext(
				_group.getGroupId());

		_portletDataContextImport.setPlid(_layout.getPlid());
		_portletDataContextImport.setPortletId(MBPortletKeys.MESSAGE_BOARDS);

		_portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				_layout, MBPortletKeys.MESSAGE_BOARDS);

		_portletPreferences.setValue("selectionStyle", "manual");
	}

	@Test
	public void testExportMBMessageWithRatings() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		MBMessage mbMessage = MBMessageLocalServiceUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			_group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			0, 0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			MBMessageConstants.DEFAULT_FORMAT, Collections.emptyList(), false,
			0.0, false, serviceContext);

		_portletPreferences.setValue(
			"mbMessageId", String.valueOf(mbMessage.getMessageId()));

		_portletPreferences.store();

		RatingsEntry ratingsEntry = RatingsTestUtil.addEntry(
			MBMessage.class.getName(), mbMessage.getMessageId());

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		_portletExportController.exportPortlet(
			_portletDataContextExport, _layout.getPlid(), rootElement, false,
			false, true, true, false);

		Map<String, String[]> parameterMap =
			_portletDataContextExport.getParameterMap();

		Assert.assertEquals(
			parameterMap.get(PortletDataHandlerKeys.RATINGS)[0],
			Boolean.TRUE.toString());

		Set<String> primaryKeys = _portletDataContextExport.getPrimaryKeys();

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(), StringPool.POUND,
					MBMessage.class.getName(), StringPool.POUND,
					mbMessage.getMessageId())));

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(), StringPool.POUND,
					RatingsEntry.class.getName(), StringPool.POUND,
					ratingsEntry.getEntryId())));
	}

	@Test
	public void testProcessExportPortletPreferencesMBMessage()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		MBMessage mbMessage = MBMessageLocalServiceUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			_group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			0, 0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			MBMessageConstants.DEFAULT_FORMAT, Collections.emptyList(), false,
			0.0, false, serviceContext);

		_portletPreferences.setValue(
			"mbMessageId", String.valueOf(mbMessage.getMessageId()));

		_portletPreferences.store();

		PortletPreferences exportedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processExportPortletPreferences(
					_portletDataContextExport, _portletPreferences);

		PortletPreferences importedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processImportPortletPreferences(
					_portletDataContextImport, exportedPortletPreferences);

		String importedWikiPageId = importedPortletPreferences.getValue(
			"mbMessageId", "");

		Assert.assertEquals(
			mbMessage.getMessageId(), GetterUtil.getLong(importedWikiPageId));
	}

	@Inject(filter = "jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS)
	private ExportImportPortletPreferencesProcessor
		_exportImportPortletPreferencesProcessor;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private PortletDataContext _portletDataContextExport;
	private PortletDataContext _portletDataContextImport;

	@Inject
	private PortletExportController _portletExportController;

	private PortletPreferences _portletPreferences;

}