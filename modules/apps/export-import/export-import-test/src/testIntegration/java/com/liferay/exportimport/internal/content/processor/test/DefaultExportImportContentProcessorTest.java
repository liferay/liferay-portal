/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.content.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppHelperLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.content.processor.ExportImportContentProcessorRegistryUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.exception.ExportImportContentValidationException;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.test.util.TestReaderWriter;
import com.liferay.exportimport.test.util.TestUserIdStrategy;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.capabilities.ThumbnailCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PortalImpl;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michael Bowerman
 * @author Gergely Mathe
 */
@RunWith(Arquillian.class)
public class DefaultExportImportContentProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_defaultLocale = LocaleUtil.getDefault();
		_nondefaultLocale = _getNondefaultLocale();

		UserTestUtil.setUser(TestPropsValues.getUser());

		_externalGroup = GroupTestUtil.addGroup();

		_liveGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHosts(_liveGroup);

		GroupTestUtil.enableLocalStaging(
			_liveGroup, TestPropsValues.getUserId());

		_stagingGroup = _liveGroup.getStagingGroup();

		GroupTestUtil.addLayoutSetVirtualHosts(_stagingGroup);

		_fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		ThumbnailCapability thumbnailCapability =
			_fileEntry.getRepositoryCapability(ThumbnailCapability.class);

		_fileEntry = thumbnailCapability.setLargeImageId(
			_fileEntry, _fileEntry.getFileEntryId());

		TestReaderWriter testReaderWriter = new TestReaderWriter();

		_portletDataContextExport =
			PortletDataContextFactoryUtil.createExportPortletDataContext(
				_stagingGroup.getCompanyId(), _stagingGroup.getGroupId(),
				new HashMap<>(),
				new Date(System.currentTimeMillis() - Time.HOUR), new Date(),
				testReaderWriter);

		Document document = SAXReaderUtil.createDocument();

		Element manifestRootElement = document.addElement("root");

		manifestRootElement.addElement("header");

		testReaderWriter.addEntry("/manifest.xml", document.asXML());

		Element rootElement = SAXReaderUtil.createElement("root");

		_portletDataContextExport.setExportDataRootElement(rootElement);

		_stagingPrivateLayout = _addMultiLocaleLayout(_stagingGroup, true);

		_stagingPublicLayout = _addMultiLocaleLayout(_stagingGroup, false);

		_portletDataContextExport.setPlid(_stagingPublicLayout.getPlid());

		_portletDataContextImport =
			PortletDataContextFactoryUtil.createImportPortletDataContext(
				_liveGroup.getCompanyId(), _liveGroup.getGroupId(),
				new HashMap<>(), new TestUserIdStrategy(), testReaderWriter);

		_portletDataContextImport.setImportDataRootElement(rootElement);

		Element missingReferencesElement = rootElement.addElement(
			"missing-references");

		_portletDataContextExport.setMissingReferencesElement(
			missingReferencesElement);

		_portletDataContextImport.setMissingReferencesElement(
			missingReferencesElement);

		_livePrivateLayout = _addMultiLocaleLayout(_liveGroup, true);
		_livePublicLayout = _addMultiLocaleLayout(_liveGroup, false);

		_externalPrivateLayout = _addMultiLocaleLayout(_externalGroup, true);
		_externalPublicLayout = _addMultiLocaleLayout(_externalGroup, false);

		Map<Long, Long> layoutPlids =
			(Map<Long, Long>)_portletDataContextImport.getNewPrimaryKeysMap(
				Layout.class);

		layoutPlids.put(
			_stagingPrivateLayout.getPlid(), _livePrivateLayout.getPlid());
		layoutPlids.put(
			_stagingPublicLayout.getPlid(), _livePublicLayout.getPlid());

		_portletDataContextImport.setPlid(_livePublicLayout.getPlid());
		_portletDataContextImport.setSourceGroupId(_stagingGroup.getGroupId());

		rootElement.addElement("entry");

		_referrerStagedModel = JournalTestUtil.addArticle(
			_stagingGroup.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		_exportImportContentProcessor =
			ExportImportContentProcessorRegistryUtil.
				getExportImportContentProcessor(String.class.getName());
	}

	@Test
	public void testExportDLReferences() throws Exception {
		_portletDataContextExport.setZipWriter(new TestReaderWriter());

		String content = _replaceParameters(
			_getContent("dl_references.txt"), _fileEntry);

		_exportImportContentProcessor.validateContentReferences(
			_stagingGroup.getGroupId(), content);

		List<String> urls = _getURLs(content);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		for (String url : urls) {
			Assert.assertFalse(content, content.contains(url));
		}

		TestReaderWriter testReaderWriter =
			(TestReaderWriter)_portletDataContextExport.getZipWriter();

		_assertContainsReference(
			testReaderWriter.getEntries(), DLFileEntryConstants.getClassName(),
			_fileEntry.getFileEntryId());

		_assertContainsBinary(
			testReaderWriter.getBinaryEntries(),
			DLFileEntryConstants.getClassName(), _fileEntry.getFileEntryId());

		int count = 0;

		for (String entry : testReaderWriter.getEntries()) {
			if (entry.contains(DLFileEntryConstants.getClassName())) {
				Assert.assertTrue(
					content,
					content.contains(
						"[$dl-reference=" + entry +
							"$,$include-uuid=false$]") ||
					content.contains(
						"[$dl-reference=" + entry + "$,$include-uuid=true$]"));

				count++;
			}
		}

		Assert.assertTrue(
			"There should be at least one file entry reference", count > 0);
	}

	@Test
	public void testExportDLReferencesFriendlyURL() throws Exception {
		_portletDataContextExport.setZipWriter(new TestReaderWriter());

		_fileEntry = DLAppLocalServiceUtil.updateFileEntry(
			TestPropsValues.getUserId(), _fileEntry.getFileEntryId(),
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			_fileEntry.getTitle(), _fileEntry.getTitle(), StringPool.BLANK,
			StringPool.BLANK, DLVersionNumberIncrease.AUTOMATIC,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		String content = _replaceParameters(
			_getContent("dl_references_file_friendly_urls.txt"), _fileEntry);

		_exportImportContentProcessor.validateContentReferences(
			_stagingGroup.getGroupId(), content);

		List<String> urls = _getURLs(content);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		for (String url : urls) {
			Assert.assertFalse(
				url + " must not be in :" + content, content.contains(url));
		}

		TestReaderWriter testReaderWriter =
			(TestReaderWriter)_portletDataContextExport.getZipWriter();

		_assertContainsReference(
			testReaderWriter.getEntries(), DLFileEntryConstants.getClassName(),
			_fileEntry.getFileEntryId());

		_assertContainsBinary(
			testReaderWriter.getBinaryEntries(),
			DLFileEntryConstants.getClassName(), _fileEntry.getFileEntryId());

		int count = 0;

		for (String entry : testReaderWriter.getEntries()) {
			if (entry.contains(DLFileEntryConstants.getClassName())) {
				Assert.assertTrue(
					content,
					content.contains(
						"[$dl-reference=" + entry +
							"$,$include-friendly-url=true$]"));

				count++;
			}
		}

		Assert.assertTrue(
			"There should be at least one file entry reference", count > 0);
	}

	@Test
	public void testExportDLReferencesInvalidReference() throws Exception {
		_portletDataContextExport.setZipWriter(new TestReaderWriter());

		_fileEntry = DLAppLocalServiceUtil.updateFileEntry(
			TestPropsValues.getUserId(), _fileEntry.getFileEntryId(),
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			_fileEntry.getTitle(), _fileEntry.getTitle(), StringPool.BLANK,
			StringPool.BLANK, DLVersionNumberIncrease.AUTOMATIC,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		String content = _replaceParameters(
			_getContent("invalid_dl_references.txt"), _fileEntry);

		List<String> urls = _getURLs(content);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		for (String url : urls) {
			Assert.assertTrue(
				url + " must be unchanged in: " + content,
				content.contains(url));
		}
	}

	@Test
	public void testExportLayoutReferencesWithContext() throws Exception {
		PortalImpl portalImpl = new PortalImpl() {

			@Override
			public String getPathContext() {
				return "/de";
			}

		};

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(portalImpl);

		Portal originalPortal = ReflectionTestUtil.getAndSetFieldValue(
			_layoutReferencesExportImportContentProcessor, "_portal",
			portalImpl);

		_oldLayoutFriendlyURLPrivateUserServletMapping =
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING;

		ReflectionTestUtil.setFieldValue(
			PropsValues.class,
			"LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING", "/en");

		ReflectionTestUtil.setFieldValue(
			_layoutReferencesExportImportContentProcessor,
			"_PRIVATE_USER_SERVLET_MAPPING", "/en/");

		String content = _replaceParameters(
			_getContent("layout_references.txt"), _fileEntry);

		_exportImportContentProcessor.validateContentReferences(
			_stagingGroup.getGroupId(), content);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		Assert.assertFalse(
			content,
			content.contains(VirtualLayoutConstants.CANONICAL_URL_SEPARATOR));
		Assert.assertFalse(
			content,
			content.contains(GroupConstants.CONTROL_PANEL_FRIENDLY_URL));
		Assert.assertFalse(
			content,
			content.contains(PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL));
		Assert.assertFalse(
			content,
			content.contains(
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING));
		Assert.assertFalse(
			content,
			content.contains(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING));
		Assert.assertTrue(
			content,
			content.contains(
				"@data_handler_group_friendly_url@@" + _liveGroup.getUuid() +
					"@"));
		Assert.assertFalse(content, content.contains(_stagingGroup.getUuid()));
		Assert.assertFalse(
			content, content.contains(_stagingGroup.getFriendlyURL()));
		Assert.assertTrue(
			content, content.contains("@data_handler_path_context@/en@"));
		Assert.assertFalse(
			content, content.contains("@data_handler_path_context@/de@"));

		ReflectionTestUtil.setFieldValue(
			PropsValues.class,
			"LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING",
			_oldLayoutFriendlyURLPrivateUserServletMapping);

		ReflectionTestUtil.setFieldValue(
			_layoutReferencesExportImportContentProcessor,
			"_PRIVATE_USER_SERVLET_MAPPING",
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING +
				StringPool.SLASH);

		portalUtil.setPortal(new PortalImpl());

		ReflectionTestUtil.setFieldValue(
			_layoutReferencesExportImportContentProcessor, "_portal",
			originalPortal);
	}

	@Test
	public void testExportLayoutReferencesWithoutContext() throws Exception {
		_oldLayoutFriendlyURLPrivateUserServletMapping =
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING;

		ReflectionTestUtil.setFieldValue(
			PropsValues.class,
			"LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING", "/en");

		ReflectionTestUtil.setFieldValue(
			_layoutReferencesExportImportContentProcessor,
			"_PRIVATE_USER_SERVLET_MAPPING", "/en/");

		String content = _replaceParameters(
			_getContent("layout_references.txt"), _fileEntry);

		_exportImportContentProcessor.validateContentReferences(
			_stagingGroup.getGroupId(), content);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		Assert.assertFalse(
			content,
			content.contains(VirtualLayoutConstants.CANONICAL_URL_SEPARATOR));
		Assert.assertFalse(
			content,
			content.contains(GroupConstants.CONTROL_PANEL_FRIENDLY_URL));
		Assert.assertFalse(
			content,
			content.contains(PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL));
		Assert.assertFalse(
			content,
			content.contains(
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING));
		Assert.assertFalse(
			content,
			content.contains(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING));
		Assert.assertTrue(
			content,
			content.contains(
				"@data_handler_group_friendly_url@@" + _liveGroup.getUuid() +
					"@"));
		Assert.assertFalse(content, content.contains(_stagingGroup.getUuid()));
		Assert.assertFalse(
			content, content.contains(_stagingGroup.getFriendlyURL()));
		Assert.assertFalse(content, content.contains("/en/en"));

		ReflectionTestUtil.setFieldValue(
			PropsValues.class,
			"LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING",
			_oldLayoutFriendlyURLPrivateUserServletMapping);

		ReflectionTestUtil.setFieldValue(
			_layoutReferencesExportImportContentProcessor,
			"_PRIVATE_USER_SERVLET_MAPPING",
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING +
				StringPool.SLASH);
	}

	@Test
	public void testExportLinksToLayouts() throws Exception {
		String content = _replaceLinksToLayoutsParameters(
			_getContent("layout_links.txt"), _stagingPrivateLayout,
			_stagingPublicLayout);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		_assertLinksToLayouts(content, _stagingPrivateLayout, 0);
		_assertLinksToLayouts(
			content, _stagingPrivateLayout, _stagingPrivateLayout.getGroupId());
		_assertLinksToLayouts(content, _stagingPublicLayout, 0);
		_assertLinksToLayouts(
			content, _stagingPublicLayout, _stagingPublicLayout.getGroupId());
	}

	@Test
	public void testExportLinksToURLsWithStopCharacters() throws Exception {
		String path = RandomTestUtil.randomString();

		String content = _getContent("url_links.txt");

		content = content.replaceAll("PATH", path);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		_assertContainsPathWithStopCharacters(content, path);
	}

	@Test
	public void testExportLinksToUserLayouts() throws Exception {
		User user = TestPropsValues.getUser();

		Group group = user.getGroup();

		Layout privateLayout = LayoutTestUtil.addTypePortletLayout(group, true);
		Layout publicLayout = LayoutTestUtil.addTypePortletLayout(group, false);

		PortletDataContext portletDataContextExport =
			PortletDataContextFactoryUtil.createExportPortletDataContext(
				group.getCompanyId(), group.getGroupId(), new HashMap<>(),
				new Date(System.currentTimeMillis() - Time.HOUR), new Date(),
				new TestReaderWriter());

		Element rootElement = SAXReaderUtil.createElement("root");

		portletDataContextExport.setExportDataRootElement(rootElement);
		portletDataContextExport.setMissingReferencesElement(
			rootElement.addElement("missing-references"));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		String content = _replaceLinksToLayoutsParameters(
			_getContent("layout_links_user_group.txt"), privateLayout,
			publicLayout);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			portletDataContextExport, journalArticle, content, true, true);

		_assertLinksToLayouts(content, privateLayout, 0);
		_assertLinksToLayouts(
			content, privateLayout, privateLayout.getGroupId());
		_assertLinksToLayouts(content, publicLayout, 0);
		_assertLinksToLayouts(content, publicLayout, publicLayout.getGroupId());
	}

	@Test
	public void testExportUUIDDLReference() throws Exception {
		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"00000000-0000-0000-0000-000000000000.txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		ThumbnailCapability thumbnailCapability =
			fileEntry.getRepositoryCapability(ThumbnailCapability.class);

		fileEntry = thumbnailCapability.setLargeImageId(
			fileEntry, fileEntry.getFileEntryId());

		_portletDataContextExport.setZipWriter(new TestReaderWriter());

		String content = _replaceParameters(
			_getContent("dl_references.txt"), fileEntry);

		_exportImportContentProcessor.validateContentReferences(
			_stagingGroup.getGroupId(), content);
	}

	@Test
	public void testImportDLReferences1() throws Exception {
		_testImportDLReferences(false);
	}

	@Test
	public void testImportDLReferences2() throws Exception {
		_testImportDLReferences(true);
	}

	@Test
	public void testImportDLReferencesFileEntryDeleted() throws Exception {
		DLAppHelperLocalServiceUtil.deleteFileEntry(_fileEntry);

		_testImportDLReferences(false);
	}

	@Test
	public void testImportDLReferencesFileEntryInTrash1() throws Exception {
		DLAppHelperLocalServiceUtil.moveFileEntryToTrash(
			TestPropsValues.getUserId(), _fileEntry);

		_testImportDLReferences(false);
	}

	@Test
	public void testImportDLReferencesFileEntryInTrash2() throws Exception {
		DLAppHelperLocalServiceUtil.moveFileEntryToTrash(
			TestPropsValues.getUserId(), _fileEntry);

		_testImportDLReferences(true);
	}

	@Test
	public void testImportDLReferencesFriendlyURLDeletingBefore()
		throws Exception {

		_testImportDLReferencesFriendlyURL(true);
	}

	@Test
	public void testImportDLReferencesFriendlyURLWithoutDeletingBefore()
		throws Exception {

		_testImportDLReferencesFriendlyURL(false);
	}

	@Test
	public void testImportLayoutReferences() throws Exception {
		_testImportLayoutReferences();
	}

	@Test
	public void testImportLayoutReferencesOnExternalGroupWithDifferentUUID()
		throws Exception {

		String content = _replaceParameters(
			_getContent("layout_references.txt"), _fileEntry);

		_exportImportContentProcessor.validateContentReferences(
			_stagingGroup.getGroupId(), content);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			false);

		String uuidString = String.valueOf(UUID.randomUUID());

		content = StringUtil.replace(
			content, _externalGroup.getUuid(), uuidString);

		content = _exportImportContentProcessor.replaceImportContentReferences(
			_portletDataContextImport, _referrerStagedModel, content);

		Assert.assertTrue(
			StringBundler.concat(
				"The imported content should contain the friendly URL of the ",
				"external group (\"", _externalGroup.getFriendlyURL(),
				"\"), but it does not:\n", content),
			content.contains(_externalGroup.getFriendlyURL()));

		Assert.assertFalse(
			"The imported content should not contain any @ variables, but it " +
				"does:\n" + content,
			content.contains(StringPool.AT));

		Assert.assertFalse(
			content, content.contains("data_handler_group_friendly_url"));
		Assert.assertFalse(
			content, content.contains("data_handler_path_context"));
		Assert.assertFalse(
			content,
			content.contains("data_handler_private_group_servlet_mapping"));
		Assert.assertFalse(
			content,
			content.contains("data_handler_private_user_servlet_mapping"));
		Assert.assertFalse(
			content, content.contains("data_handler_public_servlet_mapping"));
		Assert.assertFalse(
			content, content.contains("data_handler_site_admin_url"));
	}

	@Test
	public void testImportLayoutReferencesOnSameGroup() throws Exception {
		_portletDataContextImport.setGroupId(_stagingGroup.getGroupId());
		_portletDataContextImport.setScopeGroupId(_stagingGroup.getGroupId());

		_testImportLayoutReferences();
	}

	@Test
	public void testImportLinksToLayouts() throws Exception {
		String content = _replaceLinksToLayoutsParameters(
			_getContent("layout_links.txt"), _stagingPrivateLayout,
			_stagingPublicLayout);

		String liveContent = _replaceLinksToLayoutsParameters(
			_getContent("layout_links.txt"), _livePrivateLayout,
			_livePublicLayout);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		String importedContent =
			_exportImportContentProcessor.replaceImportContentReferences(
				_portletDataContextImport, _referrerStagedModel, content);

		Assert.assertEquals(liveContent, importedContent);
	}

	@Test
	public void testImportLinksToLayoutsIdsReplacement() throws Exception {
		LayoutTestUtil.addTypePortletLayout(_liveGroup, true);
		LayoutTestUtil.addTypePortletLayout(_liveGroup, false);

		_exportImportLayouts(true);
		_exportImportLayouts(false);

		Layout importedPrivateLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				_stagingPrivateLayout.getUuid(), _liveGroup.getGroupId(), true);
		Layout importedPublicLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				_stagingPublicLayout.getUuid(), _liveGroup.getGroupId(), false);

		Map<Long, Long> layoutPlids =
			(Map<Long, Long>)_portletDataContextImport.getNewPrimaryKeysMap(
				Layout.class);

		layoutPlids.put(
			_stagingPrivateLayout.getPlid(), importedPrivateLayout.getPlid());
		layoutPlids.put(
			_stagingPublicLayout.getPlid(), importedPublicLayout.getPlid());

		String content = _getContent("layout_links_ids_replacement.txt");

		String expectedContent = _replaceLinksToLayoutsParameters(
			content, importedPrivateLayout, importedPublicLayout);

		content = _replaceLinksToLayoutsParameters(
			content, _stagingPrivateLayout, _stagingPublicLayout);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		String importedContent =
			_exportImportContentProcessor.replaceImportContentReferences(
				_portletDataContextImport, _referrerStagedModel, content);

		Assert.assertEquals(expectedContent, importedContent);
	}

	@Test
	public void testImportLinksToLayoutsInLayoutSetPrototype()
		throws Exception {

		LayoutTestUtil.addTypePortletLayout(_liveGroup, true);

		_exportImportLayouts(true);

		Layout importedPrivateLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				_stagingPrivateLayout.getUuid(), _liveGroup.getGroupId(), true);

		Map<Long, Layout> layouts =
			(Map<Long, Layout>)_portletDataContextImport.getNewPrimaryKeysMap(
				Layout.class + ".layout");

		layouts.put(3L, importedPrivateLayout);

		String contentInFile = _getContent(
			"layout_links_in_layoutset_prototype.txt");

		String content = _replaceLinksToLayoutsParametersInLayoutSetPrototype(
			contentInFile);

		String importedContent =
			_exportImportContentProcessor.replaceImportContentReferences(
				_portletDataContextImport, _referrerStagedModel, content);

		Assert.assertTrue(
			"Template ID should have been replaced in the imported content",
			!importedContent.contains("template"));
	}

	@Test
	public void testInvalidLayoutReferencesCauseNoSuchLayoutException()
		throws Exception {

		PortalImpl portalImpl = new PortalImpl() {

			@Override
			public String getPathContext() {
				return "/de";
			}

		};

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(portalImpl);

		String content = _replaceParameters(
			_getContent("invalid_layout_references.txt"), _fileEntry);

		String[] layoutReferences = StringUtil.split(
			content, StringPool.NEW_LINE);

		for (String layoutReference : layoutReferences) {
			if (!layoutReference.contains(PortalUtil.getPathContext())) {
				continue;
			}

			boolean noSuchLayoutExceptionThrown = false;

			try {
				_exportImportContentProcessor.validateContentReferences(
					_stagingGroup.getGroupId(), layoutReference);
			}
			catch (ExportImportContentValidationException
						exportImportContentValidationException) {

				Throwable throwable =
					exportImportContentValidationException.getCause();

				if ((throwable instanceof NoSuchLayoutException) ||
					(exportImportContentValidationException.getType() ==
						ExportImportContentValidationException.
							LAYOUT_GROUP_NOT_FOUND)) {

					noSuchLayoutExceptionThrown = true;
				}
			}

			Assert.assertTrue(
				layoutReference + " was not flagged as invalid",
				noSuchLayoutExceptionThrown);
		}

		portalUtil.setPortal(new PortalImpl());
	}

	@Test
	public void testReplaceExportContentReferencesWithFileEntryInTrash()
		throws Exception {

		String content = _replaceParameters(
			_getContent("journal-content.xml"), _fileEntry);

		FileEntry deletedFileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		content = StringUtil.replace(
			content,
			new String[] {
				"[$DELETED_GROUP_ID$]", "[$DELETED_TITLE$]", "[$DELETED_UUID$]"
			},
			new String[] {
				String.valueOf(deletedFileEntry.getGroupId()),
				deletedFileEntry.getTitle(), deletedFileEntry.getUuid()
			});

		_referrerStagedModel = JournalTestUtil.addArticle(
			_stagingGroup.getGroupId(), RandomTestUtil.randomString(), content);

		DLAppHelperLocalServiceUtil.moveFileEntryToTrash(
			TestPropsValues.getUserId(), deletedFileEntry);

		Element referrerStagedModelElement =
			_portletDataContextExport.getExportDataElement(
				_referrerStagedModel);

		String referrerStagedModelPath = ExportImportPathUtil.getModelPath(
			_referrerStagedModel);

		referrerStagedModelElement.addAttribute(
			"path", referrerStagedModelPath);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		_portletDataContextImport.setScopeGroupId(_fileEntry.getGroupId());

		_exportImportContentProcessor.replaceImportContentReferences(
			_portletDataContextImport, _referrerStagedModel, content);
	}

	private Layout _addMultiLocaleLayout(Group group, boolean privateLayout)
		throws Exception {

		Map<Locale, String> nameMap = new HashMap<>();
		Map<Locale, String> friendlyURLMap = new HashMap<>();

		for (Locale locale : new Locale[] {_defaultLocale, _nondefaultLocale}) {
			String name = RandomTestUtil.randomString(
				LayoutFriendlyURLRandomizerBumper.INSTANCE,
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE);

			String friendlyURL =
				StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name);

			nameMap.put(locale, name);

			friendlyURLMap.put(locale, friendlyURL);
		}

		return LayoutTestUtil.addTypePortletLayout(
			group.getGroupId(), privateLayout, nameMap, friendlyURLMap);
	}

	private void _assertContainsBinary(
		List<String> entries, String className, long classPK) {

		Pattern pattern = Pattern.compile(
			String.format("/%s/%d/\\d+\\.\\d+$", className, classPK));

		Assert.assertTrue(
			String.format(
				"%s does not contain a binary entry for %s with primary key %s",
				entries.toString(), className, classPK),
			ListUtil.exists(entries, pattern.asPredicate()));
	}

	private void _assertContainsPathWithStopCharacters(
		String content, String path) {

		for (char stopChar : _LAYOUT_REFERENCE_STOP_CHARS) {
			StringBundler sb = new StringBundler(4);

			sb.append(path);
			sb.append(StringPool.SLASH);
			sb.append(stopChar);
			sb.append(StringPool.SLASH);

			Assert.assertTrue(
				String.format(
					"%s does not contain the path %s", content, sb.toString()),
				content.contains(sb.toString()));
		}
	}

	private void _assertContainsReference(
		List<String> entries, String className, long classPK) {

		String expected = String.format("/%s/%d.xml", className, classPK);

		Assert.assertTrue(
			String.format(
				"%s does not contain an entry for %s with primary key %s",
				entries.toString(), className, classPK),
			ListUtil.exists(entries, entry -> entry.endsWith(expected)));
	}

	private void _assertLinksToLayouts(
		String content, Layout layout, long groupId) {

		StringBundler sb = new StringBundler(9);

		sb.append(StringPool.OPEN_BRACKET);
		sb.append(layout.getLayoutId());
		sb.append(CharPool.AT);

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (layout.isPrivateLayout()) {
			if (group == null) {
				sb.append("private");
			}
			else if (group.isUser()) {
				sb.append("private-user");
			}
			else {
				sb.append("private-group");
			}
		}
		else {
			sb.append("public");
		}

		sb.append(CharPool.AT);
		sb.append(layout.getPlid());

		if (group != null) {
			sb.append(CharPool.AT);
			sb.append(String.valueOf(groupId));
		}

		sb.append(StringPool.CLOSE_BRACKET);

		Assert.assertTrue(content, content.contains(sb.toString()));
	}

	private String _duplicateLinesWithParamNames(
		String content, String[] findParams, String[] addParams) {

		if (StringUtil.indexOfAny(content, findParams) <= -1) {
			return content;
		}

		List<String> urls = ListUtil.fromArray(StringUtil.splitLines(content));

		List<String> outURLs = new ArrayList<>();

		for (String url : urls) {
			outURLs.add(url);

			if (StringUtil.indexOfAny(url, findParams) > -1) {
				outURLs.add(StringUtil.replace(url, findParams, addParams));
			}
		}

		return StringUtil.merge(outURLs, StringPool.NEW_LINE);
	}

	private void _exportImportLayouts(boolean privateLayout) throws Exception {
		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			_stagingGroup.getGroupId(), privateLayout);

		User user = TestPropsValues.getUser();

		Map<String, Serializable> publishLayoutLocalSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildPublishLayoutLocalSettingsMap(
					user, _stagingGroup.getGroupId(), _liveGroup.getGroupId(),
					privateLayout, ExportImportHelperUtil.getLayoutIds(layouts),
					new HashMap<>());

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_LAYOUT_LOCAL,
					publishLayoutLocalSettingsMap);

		File larFile = ExportImportLocalServiceUtil.exportLayoutsAsFile(
			exportImportConfiguration);

		ExportImportLocalServiceUtil.importLayouts(
			exportImportConfiguration, larFile);
	}

	private String _extractValidContent(String content) {
		List<String> lines = ListUtil.fromArray(StringUtil.splitLines(content));
		List<String> validLines = new ArrayList<>();

		for (String line : lines) {
			if (Validator.isNotNull(line) && !line.endsWith(StringPool.COLON)) {
				validLines.add(line);
			}
		}

		return StringUtil.merge(validLines, StringPool.NEW_LINE);
	}

	private String _getContent(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		Scanner scanner = new Scanner(inputStream);

		scanner.useDelimiter("\\Z");

		return scanner.next();
	}

	private Locale _getNondefaultLocale() throws Exception {
		for (Locale locale : _locales) {
			if (!locale.equals(_defaultLocale)) {
				return locale;
			}
		}

		throw new Exception("Could not find a non-default locale");
	}

	private List<String> _getURLs(String content) {
		List<String> urls = new ArrayList<>();

		Matcher matcher = _pattern.matcher(StringPool.BLANK);

		String[] lines = StringUtil.split(content, StringPool.NEW_LINE);

		for (String line : lines) {
			matcher.reset(line);

			if (matcher.find()) {
				urls.add(line);
			}
		}

		return urls;
	}

	private String _replaceExternalGroupFriendlyURLs(String content) {
		return _duplicateLinesWithParamNames(
			content, _GROUP_FRIENDLY_URL_VARIABLES,
			_EXTERNAL_GROUP_FRIENDLY_URL_VARIABLES);
	}

	private String _replaceLinksToLayoutsParameters(
		String content, Layout privateLayout, Layout publicLayout) {

		return StringUtil.replace(
			content,
			new String[] {
				"[$GROUP_ID_PRIVATE$]", "[$GROUP_ID_PUBLIC$]",
				"[$LAYOUT_ID_PRIVATE$]", "[$LAYOUT_ID_PUBLIC$]"
			},
			new String[] {
				String.valueOf(privateLayout.getGroupId()),
				String.valueOf(publicLayout.getGroupId()),
				String.valueOf(privateLayout.getLayoutId()),
				String.valueOf(publicLayout.getLayoutId())
			});
	}

	private String _replaceLinksToLayoutsParametersInLayoutSetPrototype(
		String content) {

		String portalURL = TestPropsValues.PORTAL_URL;

		String portalURLPlaceholderToReplace = "[$PORTAL_URL$]";

		String templateIdPlaceholderToReplace = "[$ID$]";

		return StringUtil.replace(
			content,
			new String[] {
				portalURLPlaceholderToReplace, templateIdPlaceholderToReplace
			},
			new String[] {
				portalURL, String.valueOf(_stagingGroup.getGroupId())
			});
	}

	private String _replaceMultiLocaleLayoutFriendlyURLs(String content) {
		return _duplicateLinesWithParamNames(
			content, _MULTI_LOCALE_LAYOUT_VARIABLES,
			_NONDEFAULT_MULTI_LOCALE_LAYOUT_VARIABLES);
	}

	private String _replaceParameters(String content, FileEntry fileEntry) {
		Company company = CompanyLocalServiceUtil.fetchCompany(
			fileEntry.getCompanyId());

		content = _replaceExternalGroupFriendlyURLs(content);
		content = _replaceMultiLocaleLayoutFriendlyURLs(content);

		Map<Locale, String> livePublicLayoutFriendlyURLMap =
			_livePublicLayout.getFriendlyURLMap();
		Map<Locale, String> stagingPrivateLayoutFriendlyURLMap =
			_stagingPrivateLayout.getFriendlyURLMap();

		LayoutSet stagingPrivateLayoutSet = _stagingGroup.getPrivateLayoutSet();

		NavigableMap<String, String> stagingPrivateVirtualHostnames =
			stagingPrivateLayoutSet.getVirtualHostnames();

		Map<Locale, String> stagingPublicLayoutFriendlyURLMap =
			_stagingPublicLayout.getFriendlyURLMap();

		LayoutSet stagingPublicLayoutSet = _stagingGroup.getPublicLayoutSet();

		NavigableMap<String, String> stagingPublicVirtualHostnames =
			stagingPublicLayoutSet.getVirtualHostnames();

		content = StringUtil.replace(
			content,
			new String[] {
				"[$BLOG_ENTRY_DISPLAY_SERVLET_MAPPING$]",
				"[$CANONICAL_URL_SEPARATOR$]", "[$CONTROL_PANEL_FRIENDLY_URL$]",
				"[$CONTROL_PANEL_LAYOUT_FRIENDLY_URL$]",
				"[$DL_ENTRY_DISPLAY_SERVLET_MAPPING$]",
				"[$EXTERNAL_GROUP_FRIENDLY_URL$]",
				"[$EXTERNAL_PRIVATE_LAYOUT_FRIENDLY_URL$]",
				"[$EXTERNAL_PUBLIC_LAYOUT_FRIENDLY_URL$]",
				"[$FILE_ENTRY_FRIENDLY_URL$]", "[$FILE_NAME$]",
				"[$FRIENDLY_URL_SEPARATOR$]", "[$GROUP_FRIENDLY_URL$]",
				"[$GROUP_ID$]", "[$GROUP_NAME$]",
				"[$GROUP_PRIVATE_PAGES_VIRTUAL_HOST$]",
				"[$GROUP_PUBLIC_PAGES_VIRTUAL_HOST$]", "[$IMAGE_ID$]",
				"[$LIVE_GROUP_FRIENDLY_URL$]", "[$LIVE_GROUP_ID$]",
				"[$LIVE_PUBLIC_LAYOUT_FRIENDLY_URL$]",
				"[$NON_DEFAULT_LIVE_PUBLIC_LAYOUT_FRIENDLY_URL$]",
				"[$NON_DEFAULT_PRIVATE_LAYOUT_FRIENDLY_URL$]",
				"[$NON_DEFAULT_PUBLIC_LAYOUT_FRIENDLY_URL$]",
				"[$NONREPLACEABLE_PRIVATE_LAYOUT_FRIENDLY_URL$]",
				"[$NONREPLACEABLE_PUBLIC_LAYOUT_FRIENDLY_URL$]",
				"[$PATH_CONTEXT$]", "[$PATH_FRIENDLY_URL_PRIVATE_GROUP$]",
				"[$PATH_FRIENDLY_URL_PRIVATE_USER$]",
				"[$PATH_FRIENDLY_URL_PUBLIC$]",
				"[$PRIVATE_LAYOUT_FRIENDLY_URL$]",
				"[$PUBLIC_LAYOUT_FRIENDLY_URL$]", "[$TITLE$]", "[$UUID$]",
				"[$WEB_CONTENT_DISPLAY_SERVLET_MAPPING$]", "[$WEB_ID$]"
			},
			new String[] {
				FriendlyURLResolverConstants.URL_SEPARATOR_X_BLOGS_ENTRY,
				VirtualLayoutConstants.CANONICAL_URL_SEPARATOR,
				GroupConstants.CONTROL_PANEL_FRIENDLY_URL,
				PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL,
				FriendlyURLResolverConstants.URL_SEPARATOR_X_FILE_ENTRY,
				_externalGroup.getFriendlyURL(),
				_externalPrivateLayout.getFriendlyURL(),
				_externalPublicLayout.getFriendlyURL(),
				FriendlyURLNormalizerUtil.normalizeWithPeriodsAndSlashes(
					fileEntry.getTitle()),
				fileEntry.getFileName(), Portal.FRIENDLY_URL_SEPARATOR,
				_stagingGroup.getFriendlyURL(),
				String.valueOf(fileEntry.getGroupId()),
				StringUtil.removeFirst(
					_stagingGroup.getFriendlyURL(), StringPool.SLASH),
				stagingPrivateVirtualHostnames.firstKey(),
				stagingPublicVirtualHostnames.firstKey(),
				String.valueOf(fileEntry.getFileEntryId()),
				_liveGroup.getFriendlyURL(),
				String.valueOf(_liveGroup.getGroupId()),
				_livePublicLayout.getFriendlyURL(),
				livePublicLayoutFriendlyURLMap.get(_nondefaultLocale),
				stagingPrivateLayoutFriendlyURLMap.get(_nondefaultLocale),
				stagingPublicLayoutFriendlyURLMap.get(_nondefaultLocale),
				_stagingPrivateLayout.getFriendlyURL(),
				_stagingPublicLayout.getFriendlyURL(),
				PortalUtil.getPathContext(),
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING,
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING,
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_stagingPrivateLayout.getFriendlyURL(),
				_stagingPublicLayout.getFriendlyURL(), fileEntry.getTitle(),
				fileEntry.getUuid(),
				FriendlyURLResolverConstants.URL_SEPARATOR_X_JOURNAL_ARTICLE,
				company.getWebId()
			});

		if (!content.contains("[$TIMESTAMP")) {
			return _extractValidContent(content);
		}

		return _replaceTimestampParameters(content);
	}

	private String _replaceTimestampParameters(String content) {
		List<String> urls = ListUtil.fromArray(StringUtil.splitLines(content));

		String timestampParameter = "t=123456789";

		String parameters1 = timestampParameter + "&width=100&height=100";
		String parameters2 = "width=100&" + timestampParameter + "&height=100";
		String parameters3 = "width=100&height=100&" + timestampParameter;
		String parameters4 = StringBundler.concat(
			timestampParameter, "?", timestampParameter,
			"&width=100&height=100");

		List<String> outURLs = new ArrayList<>();

		for (String url : urls) {
			if (Validator.isNotNull(url) && !url.contains("[$TIMESTAMP") &&
				!url.endsWith(StringPool.COLON)) {

				outURLs.add(url);

				continue;
			}

			outURLs.add(
				StringUtil.replace(
					url, new String[] {"[$TIMESTAMP$]", "[$TIMESTAMP_ONLY$]"},
					new String[] {"&" + parameters1, "?" + parameters1}));
			outURLs.add(
				StringUtil.replace(
					url, new String[] {"[$TIMESTAMP$]", "[$TIMESTAMP_ONLY$]"},
					new String[] {"&" + parameters2, "?" + parameters2}));
			outURLs.add(
				StringUtil.replace(
					url, new String[] {"[$TIMESTAMP$]", "[$TIMESTAMP_ONLY$]"},
					new String[] {"&" + parameters3, "?" + parameters3}));
			outURLs.add(
				StringUtil.replace(
					url, new String[] {"[$TIMESTAMP$]", "[$TIMESTAMP_ONLY$]"},
					new String[] {StringPool.BLANK, "?" + parameters4}));
		}

		return StringUtil.merge(outURLs, StringPool.NEW_LINE);
	}

	private void _testImportDLReferences(boolean deleteFileEntryBeforeImport)
		throws Exception {

		Element referrerStagedModelElement =
			_portletDataContextExport.getExportDataElement(
				_referrerStagedModel);

		String referrerStagedModelPath = ExportImportPathUtil.getModelPath(
			_referrerStagedModel);

		referrerStagedModelElement.addAttribute(
			"path", referrerStagedModelPath);

		String content = _replaceParameters(
			_getContent("dl_references.txt"), _fileEntry);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		_portletDataContextImport.setScopeGroupId(_fileEntry.getGroupId());

		if (deleteFileEntryBeforeImport) {
			DLAppLocalServiceUtil.deleteFileEntry(_fileEntry.getFileEntryId());
		}

		content = _exportImportContentProcessor.replaceImportContentReferences(
			_portletDataContextImport, _referrerStagedModel, content);

		Assert.assertFalse(content, content.contains("[$dl-reference="));
	}

	private void _testImportDLReferencesFriendlyURL(
			boolean deleteFileEntryBeforeImport)
		throws Exception {

		_fileEntry = DLAppLocalServiceUtil.updateFileEntry(
			TestPropsValues.getUserId(), _fileEntry.getFileEntryId(),
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			_fileEntry.getTitle(), _fileEntry.getTitle(), StringPool.BLANK,
			StringPool.BLANK, DLVersionNumberIncrease.AUTOMATIC,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		Element referrerStagedModelElement =
			_portletDataContextExport.getExportDataElement(
				_referrerStagedModel);

		String referrerStagedModelPath = ExportImportPathUtil.getModelPath(
			_referrerStagedModel);

		referrerStagedModelElement.addAttribute(
			"path", referrerStagedModelPath);

		String content = _replaceParameters(
			_getContent("dl_references_file_friendly_urls.txt"), _fileEntry);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			true);

		_portletDataContextImport.setScopeGroupId(_fileEntry.getGroupId());

		if (deleteFileEntryBeforeImport) {
			DLAppLocalServiceUtil.deleteFileEntry(_fileEntry.getFileEntryId());
		}

		content = _exportImportContentProcessor.replaceImportContentReferences(
			_portletDataContextImport, _referrerStagedModel, content);

		Assert.assertFalse(content, content.contains("[$dl-reference="));
	}

	private void _testImportLayoutReferences() throws Exception {
		String content = _replaceParameters(
			_getContent("layout_references.txt"), _fileEntry);

		_exportImportContentProcessor.validateContentReferences(
			_stagingGroup.getGroupId(), content);

		content = _exportImportContentProcessor.replaceExportContentReferences(
			_portletDataContextExport, _referrerStagedModel, content, true,
			false);
		content = _exportImportContentProcessor.replaceImportContentReferences(
			_portletDataContextImport, _referrerStagedModel, content);

		Assert.assertFalse(
			content, content.contains("data_handler_group_friendly_url"));
		Assert.assertFalse(
			content, content.contains("data_handler_path_context"));
		Assert.assertFalse(
			content,
			content.contains("data_handler_private_group_servlet_mapping"));
		Assert.assertFalse(
			content,
			content.contains("data_handler_private_user_servlet_mapping"));
		Assert.assertFalse(
			content, content.contains("data_handler_public_servlet_mapping"));
		Assert.assertFalse(
			content, content.contains("data_handler_site_admin_url"));
	}

	private static final String[] _EXTERNAL_GROUP_FRIENDLY_URL_VARIABLES = {
		"[$EXTERNAL_GROUP_FRIENDLY_URL$]",
		"[$EXTERNAL_PRIVATE_LAYOUT_FRIENDLY_URL$]",
		"[$EXTERNAL_PUBLIC_LAYOUT_FRIENDLY_URL$]"
	};

	private static final String[] _GROUP_FRIENDLY_URL_VARIABLES = {
		"[$GROUP_FRIENDLY_URL$]", "[$PRIVATE_LAYOUT_FRIENDLY_URL$]",
		"[$PUBLIC_LAYOUT_FRIENDLY_URL$]"
	};

	private static final char[] _LAYOUT_REFERENCE_STOP_CHARS = {
		CharPool.APOSTROPHE, CharPool.CLOSE_BRACKET, CharPool.CLOSE_CURLY_BRACE,
		CharPool.CLOSE_PARENTHESIS, CharPool.GREATER_THAN, CharPool.LESS_THAN,
		CharPool.PIPE, CharPool.POUND, CharPool.QUESTION, CharPool.QUOTE,
		CharPool.SPACE
	};

	private static final String[] _MULTI_LOCALE_LAYOUT_VARIABLES = {
		"[$LIVE_PUBLIC_LAYOUT_FRIENDLY_URL$]",
		"[$PRIVATE_LAYOUT_FRIENDLY_URL$]", "[$PUBLIC_LAYOUT_FRIENDLY_URL$]"
	};

	private static final String[] _NONDEFAULT_MULTI_LOCALE_LAYOUT_VARIABLES = {
		"[$NON_DEFAULT_LIVE_PUBLIC_LAYOUT_FRIENDLY_URL$]",
		"[$NON_DEFAULT_PRIVATE_LAYOUT_FRIENDLY_URL$]",
		"[$NON_DEFAULT_PUBLIC_LAYOUT_FRIENDLY_URL$]"
	};

	private static final Locale[] _locales = {
		LocaleUtil.US, LocaleUtil.GERMANY, LocaleUtil.SPAIN
	};
	private static String _oldLayoutFriendlyURLPrivateUserServletMapping;
	private static final Pattern _pattern = Pattern.compile(
		"href=|url\\(|\\{|\\[");

	private Locale _defaultLocale;
	private ExportImportContentProcessor<String> _exportImportContentProcessor;

	@DeleteAfterTestRun
	private Group _externalGroup;

	private Layout _externalPrivateLayout;
	private Layout _externalPublicLayout;
	private FileEntry _fileEntry;

	@Inject(filter = "content.processor.type=LayoutReferences")
	private ExportImportContentProcessor<String>
		_layoutReferencesExportImportContentProcessor;

	@DeleteAfterTestRun
	private Group _liveGroup;

	private Layout _livePrivateLayout;
	private Layout _livePublicLayout;
	private Locale _nondefaultLocale;
	private PortletDataContext _portletDataContextExport;
	private PortletDataContext _portletDataContextImport;
	private StagedModel _referrerStagedModel;

	@DeleteAfterTestRun
	private Group _stagingGroup;

	private Layout _stagingPrivateLayout;
	private Layout _stagingPublicLayout;

}