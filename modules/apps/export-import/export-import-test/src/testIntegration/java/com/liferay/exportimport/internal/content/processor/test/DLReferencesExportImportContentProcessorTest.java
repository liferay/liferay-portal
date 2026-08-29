/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.content.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppHelperLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.exportimport.configuration.ExportImportServiceConfiguration;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.internal.content.processor.test.util.ExportImportContentProcessorTestUtil;
import com.liferay.exportimport.kernel.exception.ExportImportContentValidationException;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.test.util.TestReaderWriter;
import com.liferay.exportimport.test.util.TestUserIdStrategy;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.capabilities.ThumbnailCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class DLReferencesExportImportContentProcessorTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_externalGroup = GroupTestUtil.addGroup();

		_sourceGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHosts(_sourceGroup);

		_targetGroup = GroupTestUtil.addGroup();

		GroupTestUtil.addLayoutSetVirtualHosts(_targetGroup);

		_fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_sourceGroup.getGroupId(), TestPropsValues.getUserId()));

		ThumbnailCapability thumbnailCapability =
			_fileEntry.getRepositoryCapability(ThumbnailCapability.class);

		_fileEntry = thumbnailCapability.setLargeImageId(
			_fileEntry, _fileEntry.getFileEntryId());

		TestReaderWriter testReaderWriter = new TestReaderWriter();

		_portletDataContextExport =
			PortletDataContextFactoryUtil.createExportPortletDataContext(
				_sourceGroup.getCompanyId(), _sourceGroup.getGroupId(),
				new HashMap<>(),
				new Date(System.currentTimeMillis() - Time.HOUR), new Date(),
				testReaderWriter);

		Document document = SAXReaderUtil.createDocument();

		Element manifestRootElement = document.addElement("root");

		manifestRootElement.addElement("header");

		testReaderWriter.addEntry("/manifest.xml", document.asXML());

		Element rootElement = SAXReaderUtil.createElement("root");

		_portletDataContextImport =
			PortletDataContextFactoryUtil.createImportPortletDataContext(
				_targetGroup.getCompanyId(), _targetGroup.getGroupId(),
				new HashMap<>(), new TestUserIdStrategy(), testReaderWriter);

		_portletDataContextImport.setExportImportProcessId(
			String.valueOf(RandomTestUtil.randomLong()));

		_portletDataContextImport.setImportDataRootElement(rootElement);

		_portletDataContextImport.setSourceGroupId(_sourceGroup.getGroupId());
	}

	@Test
	public void testExportDLReferences() throws Exception {
		_portletDataContextExport.setZipWriter(new TestReaderWriter());

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup, null, _fileEntry, "dl_references.txt", _sourceGroup,
			null, _targetGroup, null);

		_dlReferencesExportImportContentProcessor.validateContentReferences(
			_sourceGroup.getGroupId(), content);

		List<String> urls = ExportImportContentProcessorTestUtil.getURLs(
			content);

		content =
			_dlReferencesExportImportContentProcessor.
				replaceExportContentReferences(
					content, _portletDataContextExport);

		for (String url : urls) {
			Assert.assertFalse(content, content.contains(url));
		}

		TestReaderWriter testReaderWriter =
			(TestReaderWriter)_portletDataContextExport.getZipWriter();

		_assertEmpty(testReaderWriter.getBinaryEntries());
		_assertEmpty(testReaderWriter.getEntries());
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
				_sourceGroup.getGroupId(), TestPropsValues.getUserId()));

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup, null, _fileEntry,
			"dl_references_file_friendly_urls.txt", _sourceGroup, null,
			_targetGroup, null);

		_dlReferencesExportImportContentProcessor.validateContentReferences(
			_sourceGroup.getGroupId(), content);

		List<String> urls = ExportImportContentProcessorTestUtil.getURLs(
			content);

		content =
			_dlReferencesExportImportContentProcessor.
				replaceExportContentReferences(
					content, _portletDataContextExport);

		for (String url : urls) {
			Assert.assertFalse(content, content.contains(url));
		}

		TestReaderWriter testReaderWriter =
			(TestReaderWriter)_portletDataContextExport.getZipWriter();

		_assertEmpty(testReaderWriter.getBinaryEntries());
		_assertEmpty(testReaderWriter.getEntries());
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
				_sourceGroup.getGroupId(), TestPropsValues.getUserId()));

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup, null, _fileEntry, "invalid_dl_references.txt",
			_sourceGroup, null, _targetGroup, null);

		List<String> urls = ExportImportContentProcessorTestUtil.getURLs(
			content);

		content =
			_dlReferencesExportImportContentProcessor.
				replaceExportContentReferences(
					content, _portletDataContextExport);

		for (String url : urls) {
			Assert.assertTrue(
				url + " must be unchanged in: " + content,
				content.contains(url));
		}

		TestReaderWriter testReaderWriter =
			(TestReaderWriter)_portletDataContextExport.getZipWriter();

		_assertEmpty(testReaderWriter.getBinaryEntries());
		_assertEmpty(testReaderWriter.getEntries());
	}

	@Test
	public void testImportDLReferences() throws Exception {
		_testImportDLReferences(false);
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
	@TestInfo("LPS-91233")
	public void testValidateContentReferencesInvalidReferenceValidationDisabled()
		throws Exception {

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup, null, _fileEntry, "invalid_dl_references.txt",
			_sourceGroup, null, _targetGroup, null);

		Assert.assertThrows(
			ExportImportContentValidationException.class,
			() ->
				_dlReferencesExportImportContentProcessor.
					validateContentReferences(
						_sourceGroup.getGroupId(), content));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						ExportImportServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"validateFileEntryReferences", false
						).build())) {

			_dlReferencesExportImportContentProcessor.validateContentReferences(
				_sourceGroup.getGroupId(), content);
		}
	}

	private void _assertEmpty(List<String> list) {
		Assert.assertTrue(list.isEmpty());
	}

	private void _testImportDLReferences(boolean deleteFileEntryBeforeImport)
		throws Exception {

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup, null, _fileEntry, "dl_references.txt", _sourceGroup,
			null, _targetGroup, null);

		content =
			_dlReferencesExportImportContentProcessor.
				replaceExportContentReferences(
					content, _portletDataContextExport);

		_portletDataContextImport.setScopeGroupId(_fileEntry.getGroupId());

		if (deleteFileEntryBeforeImport) {
			DLAppLocalServiceUtil.deleteFileEntry(_fileEntry.getFileEntryId());
		}

		content =
			_dlReferencesExportImportContentProcessor.
				replaceImportContentReferences(
					content, _portletDataContextImport);

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
				_sourceGroup.getGroupId(), TestPropsValues.getUserId()));

		String content = ExportImportContentProcessorTestUtil.replaceParameters(
			_externalGroup, null, _fileEntry,
			"dl_references_file_friendly_urls.txt", _sourceGroup, null,
			_targetGroup, null);

		content =
			_dlReferencesExportImportContentProcessor.
				replaceExportContentReferences(
					content, _portletDataContextExport);

		_portletDataContextImport.setScopeGroupId(_fileEntry.getGroupId());

		if (deleteFileEntryBeforeImport) {
			DLAppLocalServiceUtil.deleteFileEntry(_fileEntry.getFileEntryId());
		}

		content =
			_dlReferencesExportImportContentProcessor.
				replaceImportContentReferences(
					content, _portletDataContextImport);

		Assert.assertFalse(content, content.contains("[$dl-reference="));
	}

	@Inject(filter = "content.processor.type=DLReferences")
	private ExportImportContentProcessor<String>
		_dlReferencesExportImportContentProcessor;

	@DeleteAfterTestRun
	private Group _externalGroup;

	private FileEntry _fileEntry;
	private PortletDataContext _portletDataContextExport;
	private PortletDataContext _portletDataContextImport;

	@DeleteAfterTestRun
	private Group _sourceGroup;

	@DeleteAfterTestRun
	private Group _targetGroup;

}