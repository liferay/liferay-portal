/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.service.impl.test;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.counter.AMImageCounter;
import com.liferay.adaptive.media.image.exception.DuplicateAMImageEntryException;
import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.IOException;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;

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
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class AMImageEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_amImageConfigurationEntries = deleteAllAMImageConfigurationEntries();

		Bundle bundle = FrameworkUtil.getBundle(
			AMImageEntryLocalServiceTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() throws Exception {
		deleteAllAMImageConfigurationEntries();

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				_amImageConfigurationEntries) {

			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry.getName(),
				amImageConfigurationEntry.getDescription(),
				amImageConfigurationEntry.getUUID(),
				amImageConfigurationEntry.getProperties());
		}
	}

	@Test
	public void testAddAMImageEntry() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry =
			_addAMImageConfigurationEntry("uuid", 100, 200);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		byte[] bytes = _getImageBytes();

		FileEntry fileEntry = _addFileEntry(bytes, serviceContext);

		FileVersion fileVersion = fileEntry.getFileVersion();

		AMImageEntry amImageEntry =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry, fileVersion, 100, 300,
				new UnsyncByteArrayInputStream(bytes), 12345);

		Assert.assertEquals(
			TestPropsValues.getCompanyId(), amImageEntry.getCompanyId());
		Assert.assertEquals(_group.getGroupId(), amImageEntry.getGroupId());
		Assert.assertEquals(
			fileVersion.getFileVersionId(), amImageEntry.getFileVersionId());
		Assert.assertEquals(
			ContentTypes.IMAGE_JPEG, amImageEntry.getMimeType());
		Assert.assertEquals(100, amImageEntry.getHeight());
		Assert.assertEquals(300, amImageEntry.getWidth());
		Assert.assertEquals(12345, amImageEntry.getSize());
		Assert.assertEquals(
			amImageConfigurationEntry.getUUID(),
			amImageEntry.getConfigurationUuid());
	}

	@Test(expected = DuplicateAMImageEntryException.class)
	public void testAddDuplicateAMImageEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		byte[] bytes = _getImageBytes();

		FileEntry fileEntry = _addFileEntry(bytes, serviceContext);

		AMImageConfigurationEntry amImageConfigurationEntry =
			_addAMImageConfigurationEntry("uuid", 100, 200);

		AMImageEntryLocalServiceUtil.addAMImageEntry(
			amImageConfigurationEntry, fileEntry.getFileVersion(), 100, 300,
			new UnsyncByteArrayInputStream(bytes), 12345);

		AMImageEntryLocalServiceUtil.addAMImageEntry(
			amImageConfigurationEntry, fileEntry.getFileVersion(), 100, 300,
			new UnsyncByteArrayInputStream(bytes), 12345);
	}

	@Test
	public void testDeleteAMImageEntries() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_addAMImageConfigurationEntry("uuid1", 100, 200);

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_addAMImageConfigurationEntry("uuid2", 300, 400);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		byte[] bytes = _getImageBytes();

		FileEntry fileEntry1 = _addFileEntry(bytes, serviceContext);

		AMImageEntry amImageEntryConfiguration1FileVersion1 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry1, fileEntry1.getFileVersion(), 100,
				300, new UnsyncByteArrayInputStream(bytes), 12345);
		AMImageEntry amImageEntryConfiguration2FileVersion1 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry2, fileEntry1.getFileVersion(), 300,
				500, new UnsyncByteArrayInputStream(bytes), 123456);

		FileEntry fileEntry2 = _addFileEntry(bytes, serviceContext);

		AMImageEntry amImageEntryConfiguration1FileVersion2 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry1, fileEntry2.getFileVersion(), 100,
				300, new UnsyncByteArrayInputStream(bytes), 12345);
		AMImageEntry amImageEntryConfiguration2FileVersion2 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry2, fileEntry2.getFileVersion(), 300,
				500, new UnsyncByteArrayInputStream(bytes), 123456);

		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion1.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion1.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion2.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion2.getAmImageEntryId()));

		AMImageEntryLocalServiceUtil.deleteAMImageEntries(
			TestPropsValues.getCompanyId(), amImageConfigurationEntry1);

		Assert.assertNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion1.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion1.getAmImageEntryId()));
		Assert.assertNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion2.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion2.getAmImageEntryId()));
	}

	@Test
	public void testDeleteAMImageEntryFileVersion1() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_addAMImageConfigurationEntry("uuid1", 100, 200);

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_addAMImageConfigurationEntry("uuid2", 300, 400);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		byte[] bytes = _getImageBytes();

		FileEntry fileEntry1 = _addFileEntry(bytes, serviceContext);

		AMImageEntry amImageEntryConfiguration1FileVersion1 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry1, fileEntry1.getFileVersion(), 100,
				300, new UnsyncByteArrayInputStream(bytes), 12345);
		AMImageEntry amImageEntryConfiguration2FileVersion1 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry2, fileEntry1.getFileVersion(), 300,
				500, new UnsyncByteArrayInputStream(bytes), 123456);

		FileEntry fileEntry2 = _addFileEntry(bytes, serviceContext);

		AMImageEntry amImageEntryConfiguration1FileVersion2 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry1, fileEntry2.getFileVersion(), 100,
				300, new UnsyncByteArrayInputStream(bytes), 12345);
		AMImageEntry amImageEntryConfiguration2FileVersion2 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry2, fileEntry2.getFileVersion(), 300,
				500, new UnsyncByteArrayInputStream(bytes), 123456);

		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion1.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion1.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion2.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion2.getAmImageEntryId()));

		AMImageEntryLocalServiceUtil.deleteAMImageEntryFileVersion(
			fileEntry1.getFileVersion());

		Assert.assertNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion1.getAmImageEntryId()));
		Assert.assertNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion1.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion2.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion2.getAmImageEntryId()));

		AMImageEntryLocalServiceUtil.deleteAMImageEntryFileVersion(
			fileEntry2.getFileVersion());

		Assert.assertNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion1.getAmImageEntryId()));
		Assert.assertNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion1.getAmImageEntryId()));
		Assert.assertNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion2.getAmImageEntryId()));
		Assert.assertNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion2.getAmImageEntryId()));
	}

	@Test
	public void testDeleteAMImageEntryFileVersion2() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_addAMImageConfigurationEntry("uuid1", 100, 200);

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_addAMImageConfigurationEntry("uuid2", 300, 400);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		byte[] bytes = _getImageBytes();

		FileEntry fileEntry1 = _addFileEntry(bytes, serviceContext);

		AMImageEntry amImageEntryConfiguration1FileVersion1 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry1, fileEntry1.getFileVersion(), 100,
				300, new UnsyncByteArrayInputStream(bytes), 12345);
		AMImageEntry amImageEntryConfiguration2FileVersion1 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry2, fileEntry1.getFileVersion(), 300,
				500, new UnsyncByteArrayInputStream(bytes), 123456);

		FileEntry fileEntry2 = _addFileEntry(bytes, serviceContext);

		AMImageEntry amImageEntryConfiguration1FileVersion2 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry1, fileEntry2.getFileVersion(), 100,
				300, new UnsyncByteArrayInputStream(bytes), 12345);
		AMImageEntry amImageEntryConfiguration2FileVersion2 =
			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry2, fileEntry2.getFileVersion(), 300,
				500, new UnsyncByteArrayInputStream(bytes), 123456);

		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion1.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion1.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion2.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion2.getAmImageEntryId()));

		FileVersion fileVersion = fileEntry1.getFileVersion();

		AMImageEntryLocalServiceUtil.deleteAMImageEntryFileVersion(
			amImageConfigurationEntry1.getUUID(),
			fileVersion.getFileVersionId());

		Assert.assertNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion1.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion1.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration1FileVersion2.getAmImageEntryId()));
		Assert.assertNotNull(
			AMImageEntryLocalServiceUtil.fetchAMImageEntry(
				amImageEntryConfiguration2FileVersion2.getAmImageEntryId()));
	}

	@Test
	public void testGetAMImageEntriesCount() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_addAMImageConfigurationEntry("uuid1", 100, 200);

		AMImageConfigurationEntry amImageConfigurationEntry2 =
			_addAMImageConfigurationEntry("uuid2", 300, 400);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		byte[] bytes = _getImageBytes();

		FileEntry fileEntry1 = _addFileEntry(bytes, serviceContext);

		AMImageEntryLocalServiceUtil.addAMImageEntry(
			amImageConfigurationEntry1, fileEntry1.getFileVersion(), 100, 300,
			new UnsyncByteArrayInputStream(bytes), 12345);
		AMImageEntryLocalServiceUtil.addAMImageEntry(
			amImageConfigurationEntry2, fileEntry1.getFileVersion(), 300, 500,
			new UnsyncByteArrayInputStream(bytes), 123456);

		FileEntry fileEntry2 = _addFileEntry(bytes, serviceContext);

		AMImageEntryLocalServiceUtil.addAMImageEntry(
			amImageConfigurationEntry1, fileEntry2.getFileVersion(), 100, 300,
			new UnsyncByteArrayInputStream(bytes), 12345);

		Assert.assertEquals(
			2,
			AMImageEntryLocalServiceUtil.getAMImageEntriesCount(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry1.getUUID()));
		Assert.assertEquals(
			1,
			AMImageEntryLocalServiceUtil.getAMImageEntriesCount(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry2.getUUID()));
	}

	@Test
	public void testGetExpectedAMImageEntriesCount() throws Exception {
		ServiceRegistration<AMImageCounter> amImageCounterServiceRegistration =
			null;

		try {
			PrincipalThreadLocal.setName(TestPropsValues.getUserId());

			int counter = RandomTestUtil.randomInt(100, 300);

			amImageCounterServiceRegistration = _registerAMImageCounter(
				"test", counter);

			int expectedAMImageEntriesCount =
				AMImageEntryLocalServiceUtil.getExpectedAMImageEntriesCount(
					TestPropsValues.getCompanyId());

			Assert.assertTrue(
				String.valueOf(expectedAMImageEntriesCount),
				expectedAMImageEntriesCount >= counter);
		}
		finally {
			_unregisterAMImageCounter(amImageCounterServiceRegistration);
		}
	}

	@Test
	public void testGetExpectedAMImageEntriesCountSumsAllAMImageCounters()
		throws Exception {

		ServiceRegistration<AMImageCounter> amImageCounterServiceRegistration1 =
			null;
		ServiceRegistration<AMImageCounter> amImageCounterServiceRegistration2 =
			null;

		try {
			PrincipalThreadLocal.setName(TestPropsValues.getUserId());

			int counter1 = RandomTestUtil.randomInt(100, 300);

			amImageCounterServiceRegistration1 = _registerAMImageCounter(
				"test1", counter1);

			int counter2 = RandomTestUtil.randomInt(100, 300);

			amImageCounterServiceRegistration2 = _registerAMImageCounter(
				"test2", counter2);

			int expectedAMImageEntriesCount =
				AMImageEntryLocalServiceUtil.getExpectedAMImageEntriesCount(
					TestPropsValues.getCompanyId());

			Assert.assertTrue(
				String.valueOf(expectedAMImageEntriesCount),
				expectedAMImageEntriesCount >= (counter1 + counter2));
		}
		finally {
			_unregisterAMImageCounter(amImageCounterServiceRegistration1);
			_unregisterAMImageCounter(amImageCounterServiceRegistration2);
		}
	}

	@Test
	public void testGetPercentage() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry =
			_addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "uuid1", 100, 200);

		ServiceRegistration<AMImageCounter> amImageCounterServiceRegistration =
			null;

		try {
			PrincipalThreadLocal.setName(TestPropsValues.getUserId());

			amImageCounterServiceRegistration = _registerAMImageCounter(
				"test", 4);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(_group.getGroupId());

			byte[] bytes = _getImageBytes();

			FileEntry fileEntry = _addFileEntry(
				TestPropsValues.getUserId(), _group.getGroupId(), bytes,
				serviceContext);

			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry, fileEntry.getFileVersion(), 100, 200,
				new UnsyncByteArrayInputStream(bytes), 12345);

			int percentage = AMImageEntryLocalServiceUtil.getPercentage(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry.getUUID(),
				AMImageEntryLocalServiceUtil.getExpectedAMImageEntriesCount(
					TestPropsValues.getCompanyId()));

			Assert.assertTrue(String.valueOf(percentage), percentage <= 20);
		}
		finally {
			_unregisterAMImageCounter(amImageCounterServiceRegistration);
		}
	}

	@Test
	public void testGetPercentageMax100() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry =
			_addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "uuid1", 100, 200);

		ServiceRegistration<AMImageCounter> amImageCounterServiceRegistration =
			null;

		try {
			amImageCounterServiceRegistration = _registerAMImageCounter(
				"test",
				-AMImageEntryLocalServiceUtil.getExpectedAMImageEntriesCount(
					TestPropsValues.getCompanyId()));

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(_group.getGroupId());

			byte[] bytes = _getImageBytes();

			FileEntry fileEntry1 = _addFileEntry(
				TestPropsValues.getUserId(), _group.getGroupId(), bytes,
				serviceContext);

			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry, fileEntry1.getFileVersion(), 100,
				200, new UnsyncByteArrayInputStream(bytes), 12345);

			FileEntry fileEntry2 = _addFileEntry(
				TestPropsValues.getUserId(), _group.getGroupId(), bytes,
				serviceContext);

			AMImageEntryLocalServiceUtil.addAMImageEntry(
				amImageConfigurationEntry, fileEntry2.getFileVersion(), 100,
				200, new UnsyncByteArrayInputStream(bytes), 12345);

			Assert.assertEquals(
				100,
				AMImageEntryLocalServiceUtil.getPercentage(
					TestPropsValues.getCompanyId(),
					amImageConfigurationEntry.getUUID(),
					AMImageEntryLocalServiceUtil.getExpectedAMImageEntriesCount(
						TestPropsValues.getCompanyId())));
		}
		finally {
			_unregisterAMImageCounter(amImageCounterServiceRegistration);
		}
	}

	@Test
	public void testGetPercentageWhenNoImages() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry1 =
			_addAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "uuid1", 100, 200);

		Assert.assertEquals(
			0,
			AMImageEntryLocalServiceUtil.getPercentage(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry1.getUUID(),
				AMImageEntryLocalServiceUtil.getExpectedAMImageEntriesCount(
					TestPropsValues.getCompanyId())));
	}

	@Test
	public void testHasAMImageEntryContentReturnsCachedResultWhenContentMissing()
		throws Exception {

		AMImageConfigurationEntry amImageConfigurationEntry =
			_addAMImageConfigurationEntry("uuid", 100, 200);

		byte[] bytes = _getImageBytes();

		FileEntry fileEntry = _addFileEntry(
			bytes,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		FileVersion fileVersion = fileEntry.getFileVersion();

		AMImageEntryLocalServiceUtil.addAMImageEntry(
			amImageConfigurationEntry, fileVersion, 100, 300,
			new UnsyncByteArrayInputStream(bytes), 12345);

		Assert.assertTrue(
			AMImageEntryLocalServiceUtil.hasAMImageEntryContent(
				amImageConfigurationEntry.getUUID(), fileVersion));

		DLStoreUtil.deleteDirectory(
			fileEntry.getCompanyId(), CompanyConstants.SYSTEM, "adaptive");

		Assert.assertTrue(
			AMImageEntryLocalServiceUtil.hasAMImageEntryContent(
				amImageConfigurationEntry.getUUID(), fileVersion));
	}

	@Test
	public void testHasAMImageEntryContentWhenContentPresent()
		throws Exception {

		AMImageConfigurationEntry amImageConfigurationEntry =
			_addAMImageConfigurationEntry("uuid", 100, 200);

		byte[] bytes = _getImageBytes();

		FileEntry fileEntry = _addFileEntry(
			bytes,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		FileVersion fileVersion = fileEntry.getFileVersion();

		AMImageEntryLocalServiceUtil.addAMImageEntry(
			amImageConfigurationEntry, fileVersion, 100, 300,
			new UnsyncByteArrayInputStream(bytes), 12345);

		Assert.assertTrue(
			AMImageEntryLocalServiceUtil.hasAMImageEntryContent(
				amImageConfigurationEntry.getUUID(), fileVersion));
	}

	@Test
	public void testHasAMImageEntryContentWhenNoContentPresent()
		throws Exception {

		AMImageConfigurationEntry amImageConfigurationEntry =
			_addAMImageConfigurationEntry("uuid", 100, 200);

		byte[] bytes = _getImageBytes();

		FileEntry fileEntry = _addFileEntry(
			bytes,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		FileVersion fileVersion = fileEntry.getFileVersion();

		AMImageEntryLocalServiceUtil.addAMImageEntry(
			amImageConfigurationEntry, fileVersion, 100, 300,
			new UnsyncByteArrayInputStream(bytes), 12345);

		DLStoreUtil.deleteDirectory(
			fileEntry.getCompanyId(), CompanyConstants.SYSTEM, "adaptive");

		Assert.assertFalse(
			AMImageEntryLocalServiceUtil.hasAMImageEntryContent(
				amImageConfigurationEntry.getUUID(), fileVersion));
	}

	protected Collection<AMImageConfigurationEntry>
			deleteAllAMImageConfigurationEntries()
		throws IOException, PortalException {

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry -> true);

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			_amImageConfigurationHelper.forceDeleteAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry.getUUID());
		}

		return amImageConfigurationEntries;
	}

	private AMImageConfigurationEntry _addAMImageConfigurationEntry(
			long companyId, String uuid, int maxHeight, int maxWidth)
		throws Exception {

		return _amImageConfigurationHelper.addAMImageConfigurationEntry(
			companyId, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), uuid,
			HashMapBuilder.put(
				"max-height", String.valueOf(maxHeight)
			).put(
				"max-width", String.valueOf(maxWidth)
			).build());
	}

	private AMImageConfigurationEntry _addAMImageConfigurationEntry(
			String uuid, int maxHeight, int maxWidth)
		throws Exception {

		return _addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), uuid, maxHeight, maxWidth);
	}

	private FileEntry _addFileEntry(byte[] bytes, ServiceContext serviceContext)
		throws PortalException {

		return _addFileEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), bytes,
			serviceContext);
	}

	private FileEntry _addFileEntry(
			long userId, long groupId, byte[] bytes,
			ServiceContext serviceContext)
		throws PortalException {

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.addFileEntry(
			null, userId, groupId, groupId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.IMAGE_JPEG,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT,
			Collections.emptyMap(), null, new UnsyncByteArrayInputStream(bytes),
			bytes.length, null, null, null, serviceContext);

		return new LiferayFileEntry(dlFileEntry);
	}

	private byte[] _getImageBytes() throws Exception {
		return FileUtil.getBytes(
			AMImageEntryLocalServiceTest.class,
			"/com/liferay/adaptive/media/image/image.jpg");
	}

	private ServiceRegistration<AMImageCounter> _registerAMImageCounter(
		String key, int counter) {

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("adaptive.media.key", key);

		return _bundleContext.registerService(
			AMImageCounter.class, new TestAMImageCounter(counter), properties);
	}

	private void _unregisterAMImageCounter(
		ServiceRegistration<AMImageCounter> serviceRegistration) {

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private Collection<AMImageConfigurationEntry> _amImageConfigurationEntries;

	@Inject
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	private BundleContext _bundleContext;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private class TestAMImageCounter implements AMImageCounter {

		public TestAMImageCounter(int count) {
			_count = count;
		}

		@Override
		public int countExpectedAMImageEntries(long companyId) {
			return _count;
		}

		private final int _count;

	}

}