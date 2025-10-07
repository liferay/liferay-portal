/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.document.library.thumbnails.internal.osgi.commands.test;

import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalServiceUtil;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.DLProcessorHelper;
import com.liferay.document.library.kernel.processor.ImageProcessor;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.preview.processor.BasePreviewableDLProcessor;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.image.ImageBag;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ImageConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.event.FileVersionPreviewEventListener;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Future;

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
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class ThumbnailsOSGiCommandsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_disableAMThumbnails();
		_disableDocumentLibraryAM();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_enableAMThumbnails();
		_enableDocumentLibraryAM();
	}

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_company = CompanyTestUtil.addCompany();

		_user = UserTestUtil.addCompanyAdminUser(_company);

		_group = GroupTestUtil.addGroup(
			_company.getCompanyId(), _user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_addConfiguration(100, 100);
		_addConfiguration(300, 300);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, _user.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		_amImageConfigurationHelper.forceDeleteAMImageConfigurationEntry(
			_company.getCompanyId(), _THUMBNAIL_CONFIGURATION + 100);

		_amImageConfigurationHelper.forceDeleteAMImageConfigurationEntry(
			_company.getCompanyId(), _THUMBNAIL_CONFIGURATION + 300);

		FileVersion latestFileVersion = _pngFileEntry.getFileVersion();

		AMImageEntryLocalServiceUtil.deleteAMImageEntryFileVersion(
			latestFileVersion);

		GroupLocalServiceUtil.deleteGroup(_group);

		CompanyLocalServiceUtil.deleteCompany(_company);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testCleanUpDeletesImageThumbnails() throws Exception {
		_cleanUp();

		int count = _getThumbnailCount();

		_addPNGFileEntry();

		Assert.assertEquals(count + 1, _getThumbnailCount());

		_cleanUp();

		Assert.assertEquals(count, _getThumbnailCount());
	}

	@Test
	public void testCleanUpDeletesOnlyImageThumbnails() throws Exception {
		_cleanUp();

		int count = _getThumbnailCount();

		_addPDFFileEntry();
		_addPNGFileEntry();

		Assert.assertEquals(count + 2, _getThumbnailCount());

		_cleanUp();

		Assert.assertEquals(count + 1, _getThumbnailCount());
	}

	@Test
	public void testMigrateDoesNotRemoveThumbnails() throws Exception {
		int count = _getThumbnailCount();

		_addPDFFileEntry();
		_addPNGFileEntry();

		Assert.assertEquals(count + 2, _getThumbnailCount());

		_migrate();

		Assert.assertEquals(count + 2, _getThumbnailCount());
	}

	@Ignore
	@Test
	public void testMigrateOnlyProcessesImages() throws Exception {
		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_HEIGHT", 100, false);
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_WIDTH", 100, false)) {

			FileEntry pdfFileEntry = _addPDFFileEntry();
			FileEntry pngFileEntry = _addPNGFileEntry();

			_migrate();

			Assert.assertEquals(0, _getAdaptiveMediaCount(pdfFileEntry));
			Assert.assertEquals(2, _getAdaptiveMediaCount(pngFileEntry));
		}
	}

	@Test(expected = InvocationTargetException.class)
	public void testMigrateThrowsExceptionWhenNoValidConfiguration()
		throws Exception {

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"DL_FILE_ENTRY_THUMBNAIL_MAX_HEIGHT", 999, false);
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"DL_FILE_ENTRY_THUMBNAIL_MAX_WIDTH", 999, false)) {

			_addPNGFileEntry();

			_migrate();
		}
	}

	private static void _disableAMThumbnails() throws Exception {
		Class<?> clazz = _dlProcessor.getClass();

		ComponentDescriptionDTO componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(clazz), clazz.getName());

		if (componentDescriptionDTO == null) {
			return;
		}

		Promise<Void> promise = _serviceComponentRuntime.disableComponent(
			componentDescriptionDTO);

		promise.getValue();

		Bundle bundle = FrameworkUtil.getBundle(
			ThumbnailsOSGiCommandsTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		DLProcessor imagePreviewableDLProcessor =
			new ImagePreviewableDLProcessor();

		_serviceRegistration = bundleContext.registerService(
			new String[] {
				DLProcessor.class.getName(), ImageProcessor.class.getName()
			},
			imagePreviewableDLProcessor,
			MapUtil.singletonDictionary(
				"type", DLProcessorConstants.IMAGE_PROCESSOR));

		ReflectionTestUtil.setFieldValue(
			imagePreviewableDLProcessor, "dlProcessorHelper",
			_dlProcessorHelper);
		ReflectionTestUtil.setFieldValue(
			imagePreviewableDLProcessor, "messageBus", _messageBus);
		ReflectionTestUtil.setFieldValue(
			imagePreviewableDLProcessor, "store", _store);
	}

	private static void _disableDocumentLibraryAM() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			ThumbnailsOSGiCommandsTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		for (Bundle curBundle : bundleContext.getBundles()) {
			if (_BUNDLE_SYMBOLIC_NAME.equals(curBundle.getSymbolicName())) {
				if (curBundle.getState() == Bundle.ACTIVE) {
					curBundle.stop();
				}

				break;
			}
		}
	}

	private static void _enableAMThumbnails() throws Exception {
		_serviceRegistration.unregister();

		Class<?> clazz = _dlProcessor.getClass();

		ComponentDescriptionDTO componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(clazz), clazz.getName());

		if (componentDescriptionDTO == null) {
			return;
		}

		Promise<Void> promise = _serviceComponentRuntime.enableComponent(
			componentDescriptionDTO);

		promise.getValue();
	}

	private static void _enableDocumentLibraryAM() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			ThumbnailsOSGiCommandsTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		for (Bundle curBundle : bundleContext.getBundles()) {
			if (_BUNDLE_SYMBOLIC_NAME.equals(curBundle.getSymbolicName())) {
				if (curBundle.getState() != Bundle.ACTIVE) {
					curBundle.start();
				}

				break;
			}
		}
	}

	private void _addConfiguration(int width, int height) throws Exception {
		Map<String, String> properties = HashMapBuilder.put(
			"max-height", String.valueOf(height)
		).put(
			"max-width", String.valueOf(width)
		).build();

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			_company.getCompanyId(), _THUMBNAIL_CONFIGURATION + width,
			StringPool.BLANK, _THUMBNAIL_CONFIGURATION + width, properties);
	}

	private FileEntry _addPDFFileEntry() throws Exception {
		return DLAppLocalServiceUtil.addFileEntry(
			null, _user.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF, _read("dependencies/sample.pdf"),
			null, null, null, _serviceContext);
	}

	private FileEntry _addPNGFileEntry() throws Exception {
		_pngFileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, _user.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".png", ContentTypes.IMAGE_PNG,
			_read("dependencies/sample.png"), null, null, null,
			_serviceContext);

		return _pngFileEntry;
	}

	private void _cleanUp() throws Exception {
		_run("cleanUp");
	}

	private long _getAdaptiveMediaCount(FileEntry fileEntry) throws Exception {
		List<AdaptiveMedia<AMProcessor<FileVersion>>> adaptiveMedias =
			_amImageFinder.getAdaptiveMedias(
				amImageQueryBuilder -> amImageQueryBuilder.forFileEntry(
					fileEntry
				).done());

		return adaptiveMedias.size();
	}

	private int _getThumbnailCount() throws Exception {
		String[] fileNames = DLStoreUtil.getFileNames(
			_company.getCompanyId(), BasePreviewableDLProcessor.REPOSITORY_ID,
			BasePreviewableDLProcessor.THUMBNAIL_PATH);

		return fileNames.length;
	}

	private void _migrate() throws Exception {
		_run("migrate");
	}

	private byte[] _read(String fileName) throws Exception {
		return FileUtil.getBytes(ThumbnailsOSGiCommandsTest.class, fileName);
	}

	private void _run(String functionName) throws Exception {
		Class<?> clazz = _thumbnailsOSGiCommands.getClass();

		Method method = clazz.getMethod(functionName, String[].class);

		method.invoke(
			_thumbnailsOSGiCommands,
			(Object)new String[] {String.valueOf(_company.getCompanyId())});
	}

	private static final String _BUNDLE_SYMBOLIC_NAME =
		"com.liferay.adaptive.media.document.library";

	private static final String _THUMBNAIL_CONFIGURATION = "thumbnail";

	@Inject
	private static AMImageConfigurationHelper _amImageConfigurationHelper;

	@Inject
	private static AMImageFinder _amImageFinder;

	@Inject(filter = "type=" + DLProcessorConstants.IMAGE_PROCESSOR)
	private static DLProcessor _dlProcessor;

	@Inject
	private static DLProcessorHelper _dlProcessorHelper;

	@Inject
	private static MessageBus _messageBus;

	@Inject
	private static ServiceComponentRuntime _serviceComponentRuntime;

	private static ServiceRegistration<?> _serviceRegistration;

	@Inject(filter = "default=true")
	private static Store _store;

	@Inject(
		filter = "osgi.command.scope=thumbnails", type = Inject.NoType.class
	)
	private static Object _thumbnailsOSGiCommands;

	private Company _company;
	private Group _group;
	private FileEntry _pngFileEntry;
	private ServiceContext _serviceContext;
	private User _user;

	private static class ImagePreviewableDLProcessor
		extends BasePreviewableDLProcessor implements ImageProcessor {

		@Override
		public void cleanUp(FileEntry fileEntry) {
			deleteFiles(fileEntry, null);
		}

		@Override
		public void cleanUp(FileVersion fileVersion) {
			String type = getThumbnailType(fileVersion);

			deleteFiles(fileVersion, type);
		}

		@Override
		public void generateImages(
				FileVersion sourceFileVersion,
				FileVersion destinationFileVersion)
			throws Exception {

			_generateImages(sourceFileVersion, destinationFileVersion);
		}

		@Override
		public Set<String> getImageMimeTypes() {
			return _imageMimeTypes;
		}

		@Override
		public InputStream getPreviewAsStream(FileVersion fileVersion)
			throws Exception {

			if (_previewGenerationRequired(fileVersion)) {
				String type = getPreviewType(fileVersion);

				return doGetPreviewAsStream(fileVersion, type);
			}

			return fileVersion.getContentStream(false);
		}

		@Override
		public long getPreviewFileSize(FileVersion fileVersion)
			throws Exception {

			if (_previewGenerationRequired(fileVersion)) {
				String type = getPreviewType(fileVersion);

				return doGetPreviewFileSize(fileVersion, type);
			}

			return fileVersion.getSize();
		}

		@Override
		public String getPreviewType(FileVersion fileVersion) {
			return _getType(fileVersion);
		}

		@Override
		public InputStream getThumbnailAsStream(
				FileVersion fileVersion, int index)
			throws Exception {

			return doGetThumbnailAsStream(fileVersion, index);
		}

		@Override
		public long getThumbnailFileSize(FileVersion fileVersion, int index)
			throws Exception {

			return doGetThumbnailFileSize(fileVersion, index);
		}

		@Override
		public String getThumbnailType(FileVersion fileVersion) {
			return _getType(fileVersion);
		}

		@Override
		public String getType() {
			return DLProcessorConstants.IMAGE_PROCESSOR;
		}

		@Override
		public boolean hasImages(FileVersion fileVersion) {
			if ((!PropsValues.DL_FILE_ENTRY_PREVIEW_ENABLED &&
				 !PropsValues.DL_FILE_ENTRY_THUMBNAIL_ENABLED) ||
				(fileVersion.getSize() == 0)) {

				return false;
			}

			boolean hasImages = false;

			try {
				if (_hasPreview(fileVersion) && hasThumbnails(fileVersion)) {
					hasImages = true;
				}

				if (!hasImages && isSupported(fileVersion)) {
					_queueGeneration(null, fileVersion);
				}
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			return hasImages;
		}

		@Override
		public boolean isImageSupported(FileVersion fileVersion) {
			return isSupported(fileVersion);
		}

		@Override
		public boolean isImageSupported(String mimeType) {
			return isSupported(mimeType);
		}

		@Override
		public boolean isSupported(String mimeType) {
			return _imageMimeTypes.contains(mimeType);
		}

		@Override
		public void storeThumbnail(
				long companyId, long groupId, long fileEntryId,
				long fileVersionId, long custom1ImageId, long custom2ImageId,
				InputStream inputStream, String type)
			throws Exception {

			_storeThumbnail(
				companyId, groupId, fileEntryId, fileVersionId, custom1ImageId,
				custom2ImageId, inputStream, type);
		}

		@Override
		public void trigger(
			FileVersion sourceFileVersion, FileVersion destinationFileVersion) {

			super.trigger(sourceFileVersion, destinationFileVersion);

			_queueGeneration(sourceFileVersion, destinationFileVersion);
		}

		@Override
		protected void doExportGeneratedFiles(
				PortletDataContext portletDataContext, FileEntry fileEntry,
				Element fileEntryElement)
			throws Exception {

			exportThumbnails(
				portletDataContext, fileEntry, fileEntryElement, "image");

			exportPreview(portletDataContext, fileEntry, fileEntryElement);
		}

		@Override
		protected void doImportGeneratedFiles(
				PortletDataContext portletDataContext, FileEntry fileEntry,
				FileEntry importedFileEntry, Element fileEntryElement)
			throws Exception {

			importThumbnails(
				portletDataContext, fileEntry, importedFileEntry,
				fileEntryElement, "image");

			FileVersion importedFileVersion =
				importedFileEntry.getFileVersion();

			if (!_previewGenerationRequired(importedFileVersion)) {
				return;
			}

			importPreview(
				portletDataContext, fileEntry, importedFileEntry,
				fileEntryElement, "image", getPreviewType(importedFileVersion));
		}

		protected void exportPreview(
				PortletDataContext portletDataContext, FileEntry fileEntry,
				Element fileEntryElement)
			throws Exception {

			FileVersion fileVersion = fileEntry.getFileVersion();

			if (!isSupported(fileVersion) ||
				!_previewGenerationRequired(fileVersion) ||
				!_hasPreview(fileVersion)) {

				return;
			}

			exportPreview(
				portletDataContext, fileEntry, fileEntryElement, "image",
				getPreviewType(fileVersion));
		}

		@Override
		protected List<Long> getFileVersionIds() {
			return _fileVersionIds;
		}

		private void _generateImages(
				FileVersion sourceFileVersion,
				FileVersion destinationFileVersion)
			throws Exception {

			try {
				if (sourceFileVersion != null) {
					copy(sourceFileVersion, destinationFileVersion);

					return;
				}

				if (!PropsValues.DL_FILE_ENTRY_THUMBNAIL_ENABLED &&
					!PropsValues.DL_FILE_ENTRY_PREVIEW_ENABLED) {

					return;
				}

				try (InputStream inputStream =
						destinationFileVersion.getContentStream(false)) {

					byte[] bytes = FileUtil.getBytes(inputStream);

					ImageBag imageBag = ImageToolUtil.read(bytes);

					RenderedImage renderedImage = imageBag.getRenderedImage();

					if (renderedImage == null) {
						_onFailure(destinationFileVersion);

						return;
					}

					ColorModel colorModel = renderedImage.getColorModel();

					if (colorModel.getNumColorComponents() == 4) {
						Future<RenderedImage> future =
							ImageToolUtil.convertCMYKtoRGB(
								bytes, imageBag.getType());

						if (future == null) {
							_onFailure(destinationFileVersion);

							return;
						}

						String processIdentity = String.valueOf(
							destinationFileVersion.getFileVersionId());

						futures.put(processIdentity, future);

						RenderedImage convertedRenderedImage = future.get();

						if (convertedRenderedImage != null) {
							renderedImage = convertedRenderedImage;
						}
					}

					if (!_hasPreview(destinationFileVersion)) {
						_storePreviewImage(
							destinationFileVersion, renderedImage);
					}

					if (!hasThumbnails(destinationFileVersion)) {
						storeThumbnailImages(
							destinationFileVersion, renderedImage);
					}

					FileVersionPreviewEventListener
						fileVersionPreviewEventListener =
							_fileVersionPreviewEventListenerSnapshot.get();

					if (fileVersionPreviewEventListener != null) {
						fileVersionPreviewEventListener.onSuccess(
							destinationFileVersion);
					}
				}
			}
			catch (NoSuchFileEntryException noSuchFileEntryException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFileEntryException);
				}

				_onFailure(destinationFileVersion);
			}
			finally {
				_fileVersionIds.remove(
					destinationFileVersion.getFileVersionId());
			}
		}

		private String _getType(FileVersion fileVersion) {
			String type = "png";

			if (fileVersion == null) {
				return type;
			}

			String mimeType = fileVersion.getMimeType();

			if (mimeType.equals(ContentTypes.IMAGE_BMP)) {
				type = ImageConstants.TYPE_BMP;
			}
			else if (mimeType.equals(ContentTypes.IMAGE_GIF)) {
				type = ImageConstants.TYPE_GIF;
			}
			else if (mimeType.equals(ContentTypes.IMAGE_JPEG)) {
				type = ImageConstants.TYPE_JPEG;
			}
			else if (mimeType.equals(ContentTypes.IMAGE_PNG)) {
				type = ImageConstants.TYPE_PNG;
			}
			else if (!_previewGenerationRequired(fileVersion)) {
				type = fileVersion.getExtension();
			}

			return type;
		}

		private boolean _hasPreview(FileVersion fileVersion)
			throws PortalException {

			if (PropsValues.DL_FILE_ENTRY_PREVIEW_ENABLED &&
				_previewGenerationRequired(fileVersion)) {

				String type = getPreviewType(fileVersion);

				if (!store.hasFile(
						fileVersion.getCompanyId(), REPOSITORY_ID,
						getPreviewFilePath(fileVersion, type),
						Store.VERSION_DEFAULT)) {

					return false;
				}
			}

			return true;
		}

		private void _onFailure(FileVersion fileVersion) {
			FileVersionPreviewEventListener fileVersionPreviewEventListener =
				_fileVersionPreviewEventListenerSnapshot.get();

			if (fileVersionPreviewEventListener != null) {
				fileVersionPreviewEventListener.onFailure(fileVersion);
			}
		}

		private boolean _previewGenerationRequired(FileVersion fileVersion) {
			String mimeType = fileVersion.getMimeType();

			if (mimeType.contains("tiff") || mimeType.contains("tif")) {
				return true;
			}

			return false;
		}

		private void _queueGeneration(
			FileVersion sourceFileVersion, FileVersion destinationFileVersion) {

			if (_fileVersionIds.contains(
					destinationFileVersion.getFileVersionId()) ||
				!isSupported(destinationFileVersion)) {

				return;
			}

			_fileVersionIds.add(destinationFileVersion.getFileVersionId());

			sendGenerationMessage(
				DestinationNames.DOCUMENT_LIBRARY_IMAGE_PROCESSOR,
				sourceFileVersion, destinationFileVersion);
		}

		private void _storePreviewImage(
				FileVersion fileVersion, RenderedImage renderedImage)
			throws Exception {

			String type = getPreviewType(fileVersion);

			File file = null;

			try {
				file = FileUtil.createTempFile(type);

				try (FileOutputStream fileOutputStream = new FileOutputStream(
						file)) {

					ImageToolUtil.write(renderedImage, type, fileOutputStream);
				}

				addFileToStore(
					fileVersion.getCompanyId(), PREVIEW_PATH,
					getPreviewFilePath(fileVersion, type), file);
			}
			finally {
				FileUtil.delete(file);
			}
		}

		private void _storeThumbnail(
				long companyId, long groupId, long fileEntryId,
				long fileVersionId, long custom1ImageId, long custom2ImageId,
				InputStream inputStream, String type)
			throws Exception {

			StringBundler sb = new StringBundler(5);

			sb.append(
				getPathSegment(groupId, fileEntryId, fileVersionId, false));

			if (custom1ImageId != 0) {
				sb.append(StringPool.DASH);
				sb.append(1);
			}
			else if (custom2ImageId != 0) {
				sb.append(StringPool.DASH);
				sb.append(2);
			}

			if (Validator.isNotNull(type)) {
				sb.append(StringPool.PERIOD);
				sb.append(type);
			}

			String filePath = sb.toString();

			File file = null;

			try {
				file = FileUtil.createTempFile(inputStream);

				addFileToStore(companyId, THUMBNAIL_PATH, filePath, file);
			}
			finally {
				FileUtil.delete(file);
			}
		}

		private static final Log _log = LogFactoryUtil.getLog(
			ImagePreviewableDLProcessor.class);

		private static final Snapshot<FileVersionPreviewEventListener>
			_fileVersionPreviewEventListenerSnapshot = new Snapshot<>(
				ImagePreviewableDLProcessor.class,
				FileVersionPreviewEventListener.class);

		private final List<Long> _fileVersionIds = new Vector<>();
		private final Set<String> _imageMimeTypes = SetUtil.fromArray(
			PropsValues.DL_FILE_ENTRY_PREVIEW_IMAGE_MIME_TYPES);

	}

}