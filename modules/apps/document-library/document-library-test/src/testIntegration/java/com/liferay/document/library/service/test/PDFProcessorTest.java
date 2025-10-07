/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.PDFProcessorUtil;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Dictionary;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class PDFProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		_originalDLFileEntryPreviewForkProcessEnabled =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED",
				false);
	}

	@AfterClass
	public static void tearDownClass() {
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED",
			_originalDLFileEntryPreviewForkProcessEnabled);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@After
	public void tearDown() throws Exception {
		if (_dlProcessorServiceRegistration != null) {
			_dlProcessorServiceRegistration.unregister();
		}

		if (_messageListenerServiceRegistration != null) {
			_messageListenerServiceRegistration.unregister();
		}
	}

	@Test
	public void testShouldCleanUpProcessorsOnCancelCheckOut() throws Exception {
		AtomicBoolean cleanUp = registerCleanUpDLProcessor();

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		_dlAppService.checkOutFileEntry(
			fileEntry.getFileEntryId(), _serviceContext);

		_dlAppService.cancelCheckOut(fileEntry.getFileEntryId());

		Assert.assertTrue(cleanUp.get());
	}

	@Test
	public void testShouldCleanUpProcessorsOnDelete() throws Exception {
		AtomicBoolean cleanUp = registerCleanUpDLProcessor();

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		_dlAppService.deleteFileEntry(fileEntry.getFileEntryId());

		Assert.assertTrue(cleanUp.get());
	}

	@Test
	public void testShouldCleanUpProcessorsOnUpdate() throws Exception {
		AtomicBoolean cleanUp = registerCleanUpDLProcessor();

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		_dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), StringUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF, StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), DLVersionNumberIncrease.MAJOR,
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"),
			fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
			fileEntry.getReviewDate(), _serviceContext);

		Assert.assertTrue(cleanUp.get());
	}

	@Ignore
	@Test
	public void testShouldCleanUpProcessorsOnUpdateAndCheckIn()
		throws Exception {

		AtomicBoolean cleanUp = registerCleanUpDLProcessor();

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		byte[] bytes = FileUtil.getBytes(getClass(), "dependencies/test.pdf");

		InputStream inputStream = new ByteArrayInputStream(bytes);

		_dlAppService.updateFileEntryAndCheckIn(
			fileEntry.getFileEntryId(), StringUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF, StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), DLVersionNumberIncrease.MAJOR,
			inputStream, bytes.length, fileEntry.getDisplayDate(),
			fileEntry.getExpirationDate(), fileEntry.getReviewDate(),
			_serviceContext);

		Assert.assertTrue(cleanUp.get());
	}

	@Test
	public void testShouldCopyPreviousPreviewOnCheckIn() throws Exception {
		AtomicInteger count = registerPDFProcessorMessageListener(
			EventType.COPY_PREVIOUS);

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		_dlAppService.checkInFileEntry(
			fileEntry.getFileEntryId(), DLVersionNumberIncrease.MAJOR,
			StringUtil.randomString(), _serviceContext);

		Assert.assertEquals(1, count.get());
	}

	@Test
	public void testShouldCopyPreviousPreviewOnCheckOut() throws Exception {
		AtomicInteger count = registerPDFProcessorMessageListener(
			EventType.COPY_PREVIOUS);

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		_dlAppService.checkOutFileEntry(
			fileEntry.getFileEntryId(), _serviceContext);

		Assert.assertEquals(1, count.get());
	}

	@Test
	public void testShouldCopyPreviousPreviewOnRevert() throws Exception {
		AtomicInteger count = registerPDFProcessorMessageListener(
			EventType.COPY_PREVIOUS);

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		String version = fileEntry.getVersion();

		fileEntry = _dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), StringUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF, StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), DLVersionNumberIncrease.MAJOR,
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"),
			fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
			fileEntry.getReviewDate(), _serviceContext);

		Assert.assertNotEquals(version, fileEntry.getVersion());

		_dlAppService.revertFileEntry(
			fileEntry.getFileEntryId(), version, _serviceContext);

		Assert.assertEquals(1, count.get());
	}

	@Test
	public void testShouldCopyPreviousPreviewOnUpdateAndCheckInWithNoContent()
		throws Exception {

		AtomicInteger count = registerPDFProcessorMessageListener(
			EventType.COPY_PREVIOUS);

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		_dlAppService.updateFileEntryAndCheckIn(
			fileEntry.getFileEntryId(), StringUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF, StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), DLVersionNumberIncrease.MAJOR, null, 0,
			fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
			fileEntry.getReviewDate(), _serviceContext);

		Assert.assertEquals(2, count.get());
	}

	@Test
	public void testShouldCopyPreviousPreviewOnUpdateWithNoContent()
		throws Exception {

		AtomicInteger count = registerPDFProcessorMessageListener(
			EventType.COPY_PREVIOUS);

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		_dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), StringUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF, StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), DLVersionNumberIncrease.MAJOR,
			new byte[0], fileEntry.getDisplayDate(),
			fileEntry.getExpirationDate(), fileEntry.getReviewDate(),
			_serviceContext);

		Assert.assertEquals(1, count.get());
	}

	@Test
	public void testShouldCreateNewPreviewOnAdd() throws Exception {
		AtomicInteger count = registerPDFProcessorMessageListener(
			EventType.GENERATE_NEW);

		_dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		Assert.assertEquals(1, count.get());
	}

	@Test
	public void testShouldCreateNewPreviewOnAddWithCorrectNumberOfPages()
		throws Exception {

		FileEntry fileEntry1 = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test_2.pdf"), null,
			null, null, _serviceContext);

		Assert.assertEquals(
			2,
			PDFProcessorUtil.getPreviewFileCount(fileEntry1.getFileVersion()));

		FileEntry fileEntry2 = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		Assert.assertEquals(
			1,
			PDFProcessorUtil.getPreviewFileCount(fileEntry2.getFileVersion()));
	}

	@Test
	public void testShouldCreateNewPreviewOnAddWithCorrectNumberOfPagesConfiguration()
		throws Exception {

		_withDLFileEntrySystemConfiguration(
			10,
			() -> {
				FileEntry fileEntry = _dlAppService.addFileEntry(
					null, _serviceContext.getScopeGroupId(),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					StringUtil.randomString() + ".pdf",
					ContentTypes.APPLICATION_PDF, StringUtil.randomString(),
					StringUtil.randomString(), StringUtil.randomString(),
					StringUtil.randomString(),
					FileUtil.getBytes(getClass(), "dependencies/test_2.pdf"),
					null, null, null, _serviceContext);

				Assert.assertEquals(
					2,
					PDFProcessorUtil.getPreviewFileCount(
						fileEntry.getFileVersion()));
			});
	}

	@Test
	public void testShouldCreateNewPreviewOnAddWithCorrectNumberOfPagesLimitedByConfiguration()
		throws Exception {

		_withDLFileEntrySystemConfiguration(
			1,
			() -> {
				FileEntry fileEntry = _dlAppService.addFileEntry(
					null, _serviceContext.getScopeGroupId(),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					StringUtil.randomString() + ".pdf",
					ContentTypes.APPLICATION_PDF, StringUtil.randomString(),
					StringUtil.randomString(), StringUtil.randomString(),
					StringUtil.randomString(),
					FileUtil.getBytes(getClass(), "dependencies/test_2.pdf"),
					null, null, null, _serviceContext);

				Assert.assertEquals(
					1,
					PDFProcessorUtil.getPreviewFileCount(
						fileEntry.getFileVersion()));
			});
	}

	@Test
	public void testShouldCreateNewPreviewOnCancelCheckOut() throws Exception {
		AtomicInteger count = registerPDFProcessorMessageListener(
			EventType.GENERATE_NEW);

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		_dlAppService.checkOutFileEntry(
			fileEntry.getFileEntryId(), _serviceContext);

		_dlAppService.cancelCheckOut(fileEntry.getFileEntryId());

		Assert.assertEquals(2, count.get());
	}

	@Ignore
	@Test
	public void testShouldCreateNewPreviewOnUpdateAndCheckInWithContent()
		throws Exception {

		AtomicInteger count = registerPDFProcessorMessageListener(
			EventType.GENERATE_NEW);

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		byte[] bytes = FileUtil.getBytes(getClass(), "dependencies/test.pdf");

		InputStream inputStream = new ByteArrayInputStream(bytes);

		_dlAppService.updateFileEntryAndCheckIn(
			fileEntry.getFileEntryId(), StringUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF, StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), DLVersionNumberIncrease.MAJOR,
			inputStream, bytes.length, fileEntry.getDisplayDate(),
			fileEntry.getExpirationDate(), fileEntry.getReviewDate(),
			_serviceContext);

		Assert.assertEquals(2, count.get());
	}

	@Test
	public void testShouldCreateNewPreviewOnUpdateWithContent()
		throws Exception {

		AtomicInteger count = registerPDFProcessorMessageListener(
			EventType.GENERATE_NEW);

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"), null, null,
			null, _serviceContext);

		_dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), StringUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF, StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), DLVersionNumberIncrease.MAJOR,
			FileUtil.getBytes(getClass(), "dependencies/test.pdf"),
			fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
			fileEntry.getReviewDate(), _serviceContext);

		Assert.assertEquals(2, count.get());
	}

	protected AtomicBoolean registerCleanUpDLProcessor() {
		final AtomicBoolean cleanUp = new AtomicBoolean(false);

		DLProcessor cleanUpDLProcessor = new DLProcessor() {

			@Override
			public void cleanUp(FileEntry fileEntry) {
				cleanUp.set(true);
			}

			@Override
			public void cleanUp(FileVersion fileVersion) {
				cleanUp.set(true);
			}

			@Override
			public void copy(
				FileVersion sourceFileVersion,
				FileVersion destinationFileVersion) {
			}

			@Override
			public void exportGeneratedFiles(
					PortletDataContext portletDataContext, FileEntry fileEntry,
					Element fileEntryElement)
				throws Exception {
			}

			@Override
			public String getType() {
				return DLProcessorConstants.PDF_PROCESSOR;
			}

			@Override
			public void importGeneratedFiles(
					PortletDataContext portletDataContext, FileEntry fileEntry,
					FileEntry importedFileEntry, Element fileEntryElement)
				throws Exception {
			}

			@Override
			public boolean isSupported(FileVersion fileVersion) {
				return true;
			}

			@Override
			public boolean isSupported(String mimeType) {
				return true;
			}

			@Override
			public void trigger(
				FileVersion sourceFileVersion,
				FileVersion destinationFileVersion) {
			}

		};

		Bundle bundle = FrameworkUtil.getBundle(PDFProcessorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_dlProcessorServiceRegistration = bundleContext.registerService(
			DLProcessor.class, cleanUpDLProcessor,
			HashMapDictionaryBuilder.<String, Object>put(
				"service.ranking", 1000
			).put(
				"type", DLProcessorConstants.PDF_PROCESSOR
			).build());

		return cleanUp;
	}

	protected AtomicInteger registerPDFProcessorMessageListener(
		EventType eventType) {

		AtomicInteger count = new AtomicInteger();

		Bundle bundle = FrameworkUtil.getBundle(PDFProcessorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_messageListenerServiceRegistration = bundleContext.registerService(
			MessageListener.class,
			message -> {
				Object[] payload = (Object[])message.getPayload();

				if (eventType.isMatch(payload[0])) {
					count.incrementAndGet();
				}
			},
			MapUtil.singletonDictionary(
				"destination.name",
				DestinationNames.DOCUMENT_LIBRARY_PDF_PROCESSOR));

		return count;
	}

	protected enum EventType {

		COPY_PREVIOUS {

			@Override
			public boolean isMatch(Object object) {
				return object != null;
			}

		},
		GENERATE_NEW {

			@Override
			public boolean isMatch(Object object) {
				if (object == null) {
					return true;
				}

				return false;
			}

		};

		public abstract boolean isMatch(Object object);

	}

	private void _withDLFileEntrySystemConfiguration(
			int maxNumberOfPages, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				"maxNumberOfPages", maxNumberOfPages
			).build();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.document.library.configuration." +
						"DLFileEntryConfiguration",
					dictionary)) {

			unsafeRunnable.run();
		}
	}

	private static boolean _originalDLFileEntryPreviewForkProcessEnabled;

	@Inject
	private DLAppService _dlAppService;

	private ServiceRegistration<DLProcessor> _dlProcessorServiceRegistration;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MessageBus _messageBus;

	private ServiceRegistration<MessageListener>
		_messageListenerServiceRegistration;
	private ServiceContext _serviceContext;

}