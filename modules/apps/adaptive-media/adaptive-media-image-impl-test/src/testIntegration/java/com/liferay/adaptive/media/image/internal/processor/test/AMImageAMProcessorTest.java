/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.processor.test;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.scaler.AMImageScaledImage;
import com.liferay.adaptive.media.image.scaler.AMImageScaler;
import com.liferay.adaptive.media.image.scaler.AMImageScalerRegistry;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Saurasish Basak
 */
@RunWith(Arquillian.class)
public class AMImageAMProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_amImageConfigurationEntry =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				_group.getCompanyId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				HashMapBuilder.put(
					"max-height", String.valueOf(RandomTestUtil.randomInt())
				).put(
					"max-width", String.valueOf(RandomTestUtil.randomInt())
				).build());
	}

	@After
	public void tearDown() throws Exception {
		_amImageConfigurationHelper.forceDeleteAMImageConfigurationEntry(
			_group.getCompanyId(), _amImageConfigurationEntry.getUUID());
	}

	@Sync
	@Test
	public void testProcess() throws Exception {
		_testProcessWhenFileVersionIsDeleted();
		_testProcessWhenFileVersionIsDeletedWhileScaling();
	}

	private FileVersion _addPreviousFileVersion() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.IMAGE_JPEG,
			_getImageBytes(), null, null, null, serviceContext);

		FileVersion fileVersion = fileEntry.getFileVersion();

		_dlAppLocalService.updateFileEntry(
			TestPropsValues.getUserId(), fileEntry.getFileEntryId(),
			fileEntry.getFileName(), ContentTypes.IMAGE_JPEG,
			fileEntry.getTitle(), null, StringPool.BLANK, StringPool.BLANK,
			DLVersionNumberIncrease.MAJOR, _getImageBytes(), null, null, null,
			serviceContext);

		_amImageEntryLocalService.deleteAMImageEntryFileVersion(fileVersion);

		return fileVersion;
	}

	private byte[] _getImageBytes() throws Exception {
		return FileUtil.getBytes(
			AMImageAMProcessorTest.class, "dependencies/image.jpg");
	}

	private ServiceRegistration<AMImageScaler> _registerAMImageScaler(
		AMImageScaler amImageScaler) {

		Bundle bundle = FrameworkUtil.getBundle(AMImageAMProcessorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			AMImageScaler.class, amImageScaler,
			HashMapDictionaryBuilder.<String, Object>put(
				"mimeTypes", ContentTypes.IMAGE_JPEG
			).put(
				"service.ranking", Integer.MAX_VALUE
			).build());
	}

	private void _testProcessWhenFileVersionIsDeleted() throws Exception {
		FileVersion fileVersion = _addPreviousFileVersion();

		_dlFileVersionLocalService.deleteDLFileVersion(
			fileVersion.getFileVersionId());

		AtomicInteger count = new AtomicInteger();

		AMImageScaler amImageScaler = _amImageScalerRegistry.getAMImageScaler(
			ContentTypes.IMAGE_JPEG);

		ServiceRegistration<AMImageScaler> serviceRegistration =
			_registerAMImageScaler(
				(scaledFileVersion, amImageConfigurationEntry) -> {
					count.incrementAndGet();

					return amImageScaler.scaleImage(
						scaledFileVersion, amImageConfigurationEntry);
				});

		try {
			_amProcessor.process(fileVersion);
		}
		finally {
			serviceRegistration.unregister();
		}

		Assert.assertEquals(0, count.get());
		Assert.assertNull(
			_amImageEntryLocalService.fetchAMImageEntry(
				_amImageConfigurationEntry.getUUID(),
				fileVersion.getFileVersionId()));
	}

	private void _testProcessWhenFileVersionIsDeletedWhileScaling()
		throws Exception {

		FileVersion fileVersion = _addPreviousFileVersion();

		AMImageScaler amImageScaler = _amImageScalerRegistry.getAMImageScaler(
			ContentTypes.IMAGE_JPEG);

		ServiceRegistration<AMImageScaler> serviceRegistration =
			_registerAMImageScaler(
				(scaledFileVersion, amImageConfigurationEntry) -> {
					AMImageScaledImage amImageScaledImage =
						amImageScaler.scaleImage(
							scaledFileVersion, amImageConfigurationEntry);

					_dlFileVersionLocalService.deleteDLFileVersion(
						_dlFileVersionLocalService.fetchDLFileVersion(
							scaledFileVersion.getFileVersionId()));

					return amImageScaledImage;
				});

		try {
			_amProcessor.process(
				fileVersion, _amImageConfigurationEntry.getUUID());
		}
		finally {
			serviceRegistration.unregister();
		}

		Assert.assertNull(
			_amImageEntryLocalService.fetchAMImageEntry(
				_amImageConfigurationEntry.getUUID(),
				fileVersion.getFileVersionId()));
	}

	private AMImageConfigurationEntry _amImageConfigurationEntry;

	@Inject
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Inject
	private AMImageEntryLocalService _amImageEntryLocalService;

	@Inject
	private AMImageScalerRegistry _amImageScalerRegistry;

	@Inject(
		filter = "model.class.name=com.liferay.portal.kernel.repository.model.FileVersion"
	)
	private AMProcessor<FileVersion> _amProcessor;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@DeleteAfterTestRun
	private Group _group;

}