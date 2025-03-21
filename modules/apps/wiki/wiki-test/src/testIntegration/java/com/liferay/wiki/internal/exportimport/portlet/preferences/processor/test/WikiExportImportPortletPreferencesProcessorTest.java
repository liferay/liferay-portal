/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.exportimport.portlet.preferences.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.controller.PortletExportController;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.test.util.RatingsTestUtil;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.test.util.WikiTestUtil;

import jakarta.portlet.PortletPreferences;

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
public class WikiExportImportPortletPreferencesProcessorTest {

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
			TestPropsValues.getUserId(), _layout, WikiPortletKeys.WIKI,
			"column-1", new HashMap<String, String[]>());

		_portletDataContextExport =
			ExportImportTestUtil.getExportPortletDataContext(
				_group.getGroupId());

		_portletDataContextExport.setPlid(_layout.getPlid());
		_portletDataContextExport.setPortletId(WikiPortletKeys.WIKI);

		_portletDataContextImport =
			ExportImportTestUtil.getImportPortletDataContext(
				_group.getGroupId());

		_portletDataContextImport.setPlid(_layout.getPlid());
		_portletDataContextImport.setPortletId(WikiPortletKeys.WIKI);

		_portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				_layout, WikiPortletKeys.WIKI);

		_portletPreferences.setValue("selectionStyle", "manual");
	}

	@Test
	public void testExportWikiPageWithComments() throws Exception {
		WikiNode wikiNode = WikiTestUtil.addNode(_group.getGroupId());

		WikiPage wikiPage = WikiTestUtil.addPage(
			wikiNode.getGroupId(), wikiNode.getNodeId(), true);

		_portletPreferences.setValue(
			"wikiNodeId", String.valueOf(wikiPage.getNodeId()));

		_portletPreferences.setValue(
			"wikiPageId", String.valueOf(wikiPage.getPageId()));

		_portletPreferences.store();

		User user = TestPropsValues.getUser();

		long commentPrimaryKey = CommentManagerUtil.addComment(
			user.getUserId(), _group.getGroupId(), WikiPage.class.getName(),
			wikiPage.getResourcePrimKey(), RandomTestUtil.randomString(),
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext()));

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		_portletExportController.exportPortlet(
			_portletDataContextExport, _layout.getPlid(), rootElement, false,
			false, true, true, false);

		Map<String, String[]> parameterMap =
			_portletDataContextExport.getParameterMap();

		Assert.assertEquals(
			parameterMap.get(PortletDataHandlerKeys.COMMENTS)[0],
			Boolean.TRUE.toString());

		Set<String> primaryKeys = _portletDataContextExport.getPrimaryKeys();

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(), StringPool.POUND,
					WikiPage.class.getName(), StringPool.POUND,
					wikiPage.getResourcePrimKey())));

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(),
					"#com.liferay.message.boards.model.MBMessage#",
					commentPrimaryKey)));
	}

	@Test
	public void testExportWikiPageWithRatings() throws Exception {
		WikiNode wikiNode = WikiTestUtil.addNode(_group.getGroupId());

		WikiPage wikiPage = WikiTestUtil.addPage(
			wikiNode.getGroupId(), wikiNode.getNodeId(), true);

		_portletPreferences.setValue(
			"wikiNodeId", String.valueOf(wikiPage.getNodeId()));

		_portletPreferences.setValue(
			"wikiPageId", String.valueOf(wikiPage.getPageId()));

		_portletPreferences.store();

		RatingsEntry ratingsEntry = RatingsTestUtil.addEntry(
			WikiPage.class.getName(), wikiPage.getResourcePrimKey());

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		_portletExportController.exportPortlet(
			_portletDataContextExport, _layout.getPlid(), rootElement, false,
			false, true, true, false);

		Map<String, String[]> parameterMap =
			_portletDataContextExport.getParameterMap();

		Assert.assertEquals(
			parameterMap.get(PortletDataHandlerKeys.COMMENTS)[0],
			Boolean.TRUE.toString());

		Set<String> primaryKeys = _portletDataContextExport.getPrimaryKeys();

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(), StringPool.POUND,
					WikiPage.class.getName(), StringPool.POUND,
					wikiPage.getResourcePrimKey())));

		Assert.assertTrue(
			primaryKeys.toString(),
			primaryKeys.contains(
				StringBundler.concat(
					String.class.getName(), StringPool.POUND,
					RatingsEntry.class.getName(), StringPool.POUND,
					ratingsEntry.getEntryId())));
	}

	@Test
	public void testProcessExportPortletPreferencesWiki() throws Exception {
		WikiNode wikiNode = WikiTestUtil.addNode(_group.getGroupId());

		WikiPage wikiPage = WikiTestUtil.addPage(
			wikiNode.getGroupId(), wikiNode.getNodeId(), true);

		_portletPreferences.setValue(
			"wikiNodeId", String.valueOf(wikiPage.getNodeId()));

		_portletPreferences.setValue(
			"wikiPageId", String.valueOf(wikiPage.getPageId()));

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
			"wikiPageId", "");

		Assert.assertEquals(
			wikiPage.getPageId(), GetterUtil.getLong(importedWikiPageId));
	}

	@Inject(filter = "jakarta.portlet.name=" + WikiPortletKeys.WIKI)
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