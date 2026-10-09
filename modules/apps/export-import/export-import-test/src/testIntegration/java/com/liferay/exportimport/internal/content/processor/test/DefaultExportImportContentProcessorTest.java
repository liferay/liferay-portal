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
import com.liferay.exportimport.internal.content.processor.test.util.ExportImportContentProcessorTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.exception.ExportImportContentValidationException;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.test.util.TestReaderWriter;
import com.liferay.exportimport.test.util.TestUserIdStrategy;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.repository.capabilities.ThumbnailCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
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
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PortalImpl;

import java.io.File;
import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
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

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			_fileEntry, "dl_references.txt", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

		_exportImportContentProcessor.validateContentReferences(
			_stagingGroup.getGroupId(), content);

		List<String> urls = ExportImportContentProcessorTestUtil.getURLs(
			content);

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
			if (!entry.contains(DLFileEntryConstants.getClassName())) {
				continue;
			}

			Assert.assertTrue(
				content,
				content.contains(
					"[$dl-reference=" + entry + "$,$include-uuid=false$]") ||
				content.contains(
					"[$dl-reference=" + entry + "$,$include-uuid=true$]"));

			count++;
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

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			_fileEntry, "dl_references_file_friendly_urls.txt", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

		_exportImportContentProcessor.validateContentReferences(
			_stagingGroup.getGroupId(), content);

		List<String> urls = ExportImportContentProcessorTestUtil.getURLs(
			content);

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
			if (!entry.contains(DLFileEntryConstants.getClassName())) {
				continue;
			}

			Assert.assertTrue(
				content,
				content.contains(
					"[$dl-reference=" + entry +
						"$,$include-friendly-url=true$]"));

			count++;
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

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			_fileEntry, "invalid_dl_references.txt", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

		List<String> urls = ExportImportContentProcessorTestUtil.getURLs(
			content);

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

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			_fileEntry, "layout_references.txt", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

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

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			_fileEntry, "layout_references.txt", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

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
			ExportImportContentProcessorTestUtil.getContent("layout_links.txt"),
			_stagingPrivateLayout, _stagingPublicLayout);

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

		String content = ExportImportContentProcessorTestUtil.getContent(
			"url_links.txt");

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
			ExportImportContentProcessorTestUtil.getContent(
				"layout_links_user_group.txt"),
			privateLayout, publicLayout);

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

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			fileEntry, "dl_references.txt", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

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

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			_fileEntry, "layout_references.txt", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

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
			ExportImportContentProcessorTestUtil.getContent("layout_links.txt"),
			_stagingPrivateLayout, _stagingPublicLayout);

		String liveContent = _replaceLinksToLayoutsParameters(
			ExportImportContentProcessorTestUtil.getContent("layout_links.txt"),
			_livePrivateLayout, _livePublicLayout);

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

		String content = ExportImportContentProcessorTestUtil.getContent(
			"layout_links_ids_replacement.txt");

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

		String contentInFile = ExportImportContentProcessorTestUtil.getContent(
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

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			_fileEntry, "invalid_layout_references.txt", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

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

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			_fileEntry, "journal-content.xml", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

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

		for (Locale locale :
				new Locale[] {
					_defaultLocale,
					ExportImportContentProcessorTestUtil.getNondefaultLocale()
				}) {

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

	private void _exportImportLayouts(boolean privateLayout) throws Exception {
		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			_stagingGroup.getGroupId(), privateLayout);

		User user = TestPropsValues.getUser();

		Map<String, Serializable> publishLayoutLocalSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildPublishLayoutLocalSettingsMap(
					user, _stagingGroup.getGroupId(), _liveGroup.getGroupId(),
					privateLayout, ExportImportHelperUtil.getLayoutIds(layouts),
					HashMapBuilder.put(
						PortletDataHandlerKeys.PORTLET_DATA,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_DATA + "_" +
							LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS,
						new String[] {Boolean.TRUE.toString()}
					).build());

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_LAYOUT_LOCAL,
					publishLayoutLocalSettingsMap);

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			File larFile = ExportImportLocalServiceUtil.exportLayoutsAsFile(
				exportImportConfiguration);

			ExportImportLocalServiceUtil.importLayouts(
				exportImportConfiguration, larFile);
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}
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

	private void _testImportDLReferences(boolean deleteFileEntryBeforeImport)
		throws Exception {

		Element referrerStagedModelElement =
			_portletDataContextExport.getExportDataElement(
				_referrerStagedModel);

		String referrerStagedModelPath = ExportImportPathUtil.getModelPath(
			_referrerStagedModel);

		referrerStagedModelElement.addAttribute(
			"path", referrerStagedModelPath);

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			_fileEntry, "dl_references.txt", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

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

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			_fileEntry, "dl_references_file_friendly_urls.txt", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

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
		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup,
			new ObjectValuePair<>(
				_externalPrivateLayout, _externalPublicLayout),
			_fileEntry, "layout_references.txt", _stagingGroup,
			new ObjectValuePair<>(_stagingPrivateLayout, _stagingPublicLayout),
			_liveGroup,
			new ObjectValuePair<>(_livePrivateLayout, _livePublicLayout));

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

	private static final char[] _LAYOUT_REFERENCE_STOP_CHARS = {
		CharPool.APOSTROPHE, CharPool.CLOSE_BRACKET, CharPool.CLOSE_CURLY_BRACE,
		CharPool.CLOSE_PARENTHESIS, CharPool.GREATER_THAN, CharPool.LESS_THAN,
		CharPool.PIPE, CharPool.POUND, CharPool.QUESTION, CharPool.QUOTE,
		CharPool.SPACE
	};

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
	private String _oldLayoutFriendlyURLPrivateUserServletMapping;
	private PortletDataContext _portletDataContextExport;
	private PortletDataContext _portletDataContextImport;
	private StagedModel _referrerStagedModel;

	@DeleteAfterTestRun
	private Group _stagingGroup;

	private Layout _stagingPrivateLayout;
	private Layout _stagingPublicLayout;

}