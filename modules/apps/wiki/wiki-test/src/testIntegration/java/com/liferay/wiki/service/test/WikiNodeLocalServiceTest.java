/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProgressTracker;
import com.liferay.portal.kernel.util.ProgressTrackerThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.exception.DuplicateWikiNodeExternalReferenceCodeException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.test.util.WikiTestUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class WikiNodeLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddDefaultNode() throws Exception {
		_group = GroupTestUtil.addGroup();

		String initialNodeName = RandomTestUtil.randomString();

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), WikiConstants.SERVICE_NAME,
						MapUtil.<String, Object>singletonDictionary(
							"initialNodeName", initialNodeName))) {

			_node = WikiNodeLocalServiceUtil.addDefaultNode(
				TestPropsValues.getUserId(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		}

		Assert.assertEquals(initialNodeName, _node.getName());
	}

	@Test(expected = DuplicateWikiNodeExternalReferenceCodeException.class)
	public void testAddNodeWithExistingExternalReferenceCode()
		throws Exception {

		User user = TestPropsValues.getUser();

		WikiNode wikiNode = WikiNodeLocalServiceUtil.addNode(
			user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		WikiNodeLocalServiceUtil.addNode(
			wikiNode.getExternalReferenceCode(), user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testAddNodeWithExternalReferenceCode() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();
		User user = TestPropsValues.getUser();

		WikiNode wikiNode = WikiNodeLocalServiceUtil.addNode(
			externalReferenceCode, user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			externalReferenceCode, wikiNode.getExternalReferenceCode());
	}

	@Test
	public void testAddNodeWithoutExternalReferenceCode()
		throws PortalException {

		User user = TestPropsValues.getUser();

		WikiNode wikiNode1 = WikiNodeLocalServiceUtil.addNode(
			user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		String externalReferenceCode = wikiNode1.getExternalReferenceCode();

		Assert.assertEquals(externalReferenceCode, wikiNode1.getUuid());

		WikiNode wikiNode2 =
			WikiNodeLocalServiceUtil.getWikiNodeByExternalReferenceCode(
				externalReferenceCode, TestPropsValues.getGroupId());

		Assert.assertEquals(wikiNode1, wikiNode2);
	}

	@Test
	public void testImportPages() throws Exception {
		_node = WikiTestUtil.addNode(TestPropsValues.getGroupId());

		ProgressTracker progressTracker = new ProgressTracker(
			StringUtil.randomString());

		ProgressTrackerThreadLocal.setProgressTracker(progressTracker);

		byte[] bytes = FileUtil.getBytes(
			getClass(),
			"/com/liferay/wiki/service/test/dependencies" +
				"/liferay_media_wiki.xml");

		InputStream inputStream = new ByteArrayInputStream(bytes);

		WikiNodeLocalServiceUtil.importPages(
			TestPropsValues.getUserId(), _node.getNodeId(),
			new InputStream[] {inputStream, null, null},
			Collections.<String, String[]>emptyMap());

		WikiPage importedPage = WikiPageLocalServiceUtil.fetchPage(
			_node.getNodeId(), "Liferay");

		Assert.assertNotNull(importedPage);
	}

	@Test
	public void testImportPagesFrontPage() throws Exception {
		_group = GroupTestUtil.addGroup();

		_node = WikiTestUtil.addNode(_group.getGroupId());

		ProgressTracker progressTracker = new ProgressTracker(
			StringUtil.randomString());

		ProgressTrackerThreadLocal.setProgressTracker(progressTracker);

		String frontPageName = RandomTestUtil.randomString();

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), WikiConstants.SERVICE_NAME,
						MapUtil.<String, Object>singletonDictionary(
							"frontPageName", frontPageName))) {

			WikiNodeLocalServiceUtil.importPages(
				TestPropsValues.getUserId(), _node.getNodeId(),
				new InputStream[] {
					new ByteArrayInputStream(
						FileUtil.getBytes(
							getClass(),
							"/com/liferay/wiki/service/test/dependencies" +
								"/liferay_media_wiki.xml")),
					null, null
				},
				Collections.singletonMap(
					WikiWebKeys.OPTIONS_FRONT_PAGE, new String[] {"Liferay"}));
		}

		Assert.assertNotNull(
			WikiPageLocalServiceUtil.fetchPage(
				_node.getNodeId(), frontPageName));
	}

	@Test
	public void testMediaLinks() throws Exception {
		_node = WikiTestUtil.addNode(TestPropsValues.getGroupId());

		ProgressTracker progressTracker = new ProgressTracker(
			StringUtil.randomString());

		ProgressTrackerThreadLocal.setProgressTracker(progressTracker);

		byte[] pagesBytes = FileUtil.getBytes(
			getClass(),
			"/com/liferay/wiki/service/test/dependencies/media_link_test.xml");

		InputStream pagesInputStream = new ByteArrayInputStream(pagesBytes);

		byte[] filesBytes = FileUtil.getBytes(
			getClass(),
			"/com/liferay/wiki/service/test/dependencies/media_link_test.zip");

		InputStream filesInputStream = new ByteArrayInputStream(filesBytes);

		WikiNodeLocalServiceUtil.importPages(
			TestPropsValues.getUserId(), _node.getNodeId(),
			new InputStream[] {pagesInputStream, null, filesInputStream},
			Collections.<String, String[]>emptyMap());

		WikiPage importedPage = WikiPageLocalServiceUtil.fetchPage(
			_node.getNodeId(), "Media link migration test");

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = CompanyLocalServiceUtil.getCompany(
			_node.getCompanyId());

		themeDisplay.setPortalURL(company.getPortalURL(_node.getGroupId()));

		WikiPage sharedImagesPage = WikiPageLocalServiceUtil.fetchPage(
			_node.getNodeId(), "SharedImages");

		long sharedImagesPageAttachmentsFolderId =
			sharedImagesPage.getAttachmentsFolderId();

		FileEntry attachmentFileEntry =
			PortletFileRepositoryUtil.getPortletFileEntry(
				_node.getGroupId(), sharedImagesPageAttachmentsFolderId,
				"media_link_test.docx");

		String attachmentFileEntryURL =
			PortletFileRepositoryUtil.getPortletFileEntryURL(
				themeDisplay, attachmentFileEntry, StringPool.BLANK);

		String linkTag = "[[" + attachmentFileEntryURL + "|Download link]]";

		Assert.assertEquals(
			"<<TableOfContents>>\n\n" + linkTag, importedPage.getContent());
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private WikiNode _node;

}