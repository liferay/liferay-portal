/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.capabilities.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.util.comparator.FileVersionVersionComparator;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.document.library.versioning.VersionPurger;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;

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
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class LiferayVersioningCapabilityTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDoesNotLimitTheNumberOfVersionsPerFileEntry()
		throws Exception {

		_withMaximumNumberOfVersionsConfigured(
			0,
			() -> {
				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId());

				FileEntry fileEntry = _addRandomFileEntry(serviceContext);

				for (int i = 0; i < 10; i++) {
					_generateNewVersion(fileEntry, serviceContext);
				}

				Assert.assertEquals(
					11,
					fileEntry.getFileVersionsCount(
						WorkflowConstants.STATUS_ANY));
			});
	}

	@Test
	public void testGetFileEntryVersionsByRange() throws Exception {
		int numberOfVersions = 2;

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		List<FileVersion> expectedDLFileVersions = new ArrayList<>();

		FileEntry fileEntry = DLAppTestUtil.addFileEntryWithWorkflow(
			TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt",
			RandomTestUtil.randomString(), true, serviceContext);

		for (int i = 0; i < numberOfVersions; i++) {
			fileEntry = _generateNewVersion(fileEntry, serviceContext);

			expectedDLFileVersions.add(fileEntry.getFileVersion());
		}

		List<FileVersion> actualDLFileVersions = fileEntry.getFileVersions(
			WorkflowConstants.STATUS_ANY, 0, numberOfVersions);

		Assert.assertEquals(
			actualDLFileVersions.toString(), numberOfVersions,
			actualDLFileVersions.size());

		Collections.sort(
			expectedDLFileVersions, new FileVersionVersionComparator());

		Assert.assertEquals(expectedDLFileVersions, actualDLFileVersions);
	}

	@Test
	public void testGetFileEntryVersionsByRangeAndStatus() throws Exception {
		int numberOfExtraApprovedVersions = 2;

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		List<FileVersion> expectedDLFileVersions = new ArrayList<>();

		FileEntry fileEntry = DLAppTestUtil.addFileEntryWithWorkflow(
			TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt",
			RandomTestUtil.randomString(), true, serviceContext);

		for (int i = 0; i < numberOfExtraApprovedVersions; i++) {
			fileEntry = DLAppServiceUtil.updateFileEntry(
				fileEntry.getFileEntryId(), RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK,
				DLVersionNumberIncrease.MINOR, null, 0,
				fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
				fileEntry.getReviewDate(), serviceContext);

			expectedDLFileVersions.add(fileEntry.getFileVersion());
		}

		fileEntry = DLAppServiceUtil.updateFileEntry(
			fileEntry.getFileEntryId(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, DLVersionNumberIncrease.MINOR, null, 0,
			fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
			fileEntry.getReviewDate(), serviceContext);

		fileEntry = _updateStatus(
			fileEntry, WorkflowConstants.STATUS_DRAFT, serviceContext);

		List<FileVersion> actualDLFileVersions = fileEntry.getFileVersions(
			WorkflowConstants.STATUS_APPROVED, 0,
			numberOfExtraApprovedVersions);

		Assert.assertEquals(
			actualDLFileVersions.toString(), numberOfExtraApprovedVersions,
			actualDLFileVersions.size());

		Collections.sort(
			expectedDLFileVersions, new FileVersionVersionComparator());

		Assert.assertEquals(expectedDLFileVersions, actualDLFileVersions);
	}

	@Test
	public void testLimitsTheNumberOfVersionsPerFileEntry() throws Exception {
		_withMaximumNumberOfVersionsConfigured(
			2,
			() -> {
				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId());

				FileEntry fileEntry = _addRandomFileEntry(serviceContext);

				for (int i = 0; i < 10; i++) {
					_generateNewVersion(fileEntry, serviceContext);
				}

				Assert.assertEquals(
					2,
					fileEntry.getFileVersionsCount(
						WorkflowConstants.STATUS_ANY));

				List<FileVersion> fileVersions = fileEntry.getFileVersions(
					WorkflowConstants.STATUS_ANY);

				FileVersion fileVersion1 = fileVersions.get(0);

				Assert.assertEquals("1.10", fileVersion1.getVersion());

				FileVersion fileVersion2 = fileVersions.get(1);

				Assert.assertEquals("1.9", fileVersion2.getVersion());
			});
	}

	@Test
	public void testLimitsTheNumberOfVersionsPerFileEntryToOne()
		throws Exception {

		_withMaximumNumberOfVersionsConfigured(
			1,
			() -> {
				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId());

				FileEntry fileEntry = _addRandomFileEntry(serviceContext);

				for (int i = 0; i < 10; i++) {
					_generateNewVersion(fileEntry, serviceContext);
				}

				Assert.assertEquals(
					1,
					fileEntry.getFileVersionsCount(
						WorkflowConstants.STATUS_ANY));

				List<FileVersion> fileVersions = fileEntry.getFileVersions(
					WorkflowConstants.STATUS_ANY);

				FileVersion fileVersion1 = fileVersions.get(0);

				Assert.assertEquals("1.10", fileVersion1.getVersion());
			});
	}

	@Test
	@TestInfo("LPD-107151")
	public void testLimitsTheNumberOfVersionsPerFileEntryWithoutDeletePermission()
		throws Exception {

		_withMaximumNumberOfVersionsConfigured(
			2,
			() -> {
				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId());

				FileEntry fileEntry = _addRandomFileEntry(serviceContext);

				RoleTestUtil.addResourcePermission(
					RoleConstants.SITE_MEMBER, DLFileEntry.class.getName(),
					ResourceConstants.SCOPE_GROUP,
					String.valueOf(_group.getGroupId()), ActionKeys.UPDATE);

				_user = UserTestUtil.addUser(_group.getGroupId());

				UserTestUtil.setUser(_user);

				for (int i = 0; i < 3; i++) {
					_generateNewVersion(fileEntry, serviceContext);
				}

				Assert.assertEquals(
					2,
					fileEntry.getFileVersionsCount(
						WorkflowConstants.STATUS_ANY));
			});
	}

	@Test
	public void testNotifiesAboutEachFileVersionDeletion() throws Exception {
		_withMaximumNumberOfVersionsConfigured(
			2,
			() -> {
				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId());

				FileEntry fileEntry = _addRandomFileEntry(serviceContext);

				List<FileVersion> deletedFileVersions = new ArrayList<>();

				Bundle bundle = FrameworkUtil.getBundle(
					LiferayVersioningCapabilityTest.class);

				BundleContext bundleContext = bundle.getBundleContext();

				ServiceRegistration<VersionPurger.VersionPurgedListener>
					capabilityServiceRegistration =
						bundleContext.registerService(
							VersionPurger.VersionPurgedListener.class,
							deletedFileVersions::add, null);

				try {
					for (int i = 0; i < 10; i++) {
						_generateNewVersion(fileEntry, serviceContext);
					}

					Assert.assertEquals(
						deletedFileVersions.toString(), 9,
						deletedFileVersions.size());
				}
				finally {
					capabilityServiceRegistration.unregister();
				}
			});
	}

	@Test
	public void testRespectsTheLimitWhenCheckedInAndOut() throws Exception {
		int numberOfVersions = 2;

		_withMaximumNumberOfVersionsConfigured(
			numberOfVersions,
			() -> {
				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId());

				FileEntry fileEntry = _addRandomFileEntry(serviceContext);

				for (int i = 0; i < (numberOfVersions + 10); i++) {
					DLAppServiceUtil.checkOutFileEntry(
						fileEntry.getFileEntryId(), serviceContext);

					DLAppServiceUtil.checkInFileEntry(
						fileEntry.getFileEntryId(),
						DLVersionNumberIncrease.MAJOR,
						StringUtil.randomString(), serviceContext);
				}

				List<FileVersion> fileVersions = fileEntry.getFileVersions(
					WorkflowConstants.STATUS_ANY);

				Assert.assertEquals(
					"The number of versions stored are: ", numberOfVersions,
					fileVersions.size());

				FileVersion fileVersion = fileVersions.get(0);

				Assert.assertEquals("13.0", fileVersion.getVersion());

				fileVersion = fileVersions.get(1);

				Assert.assertEquals("12.0", fileVersion.getVersion());
			});
	}

	private FileEntry _addRandomFileEntry(ServiceContext serviceContext)
		throws PortalException {

		return DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			serviceContext);
	}

	private FileEntry _generateNewVersion(
			FileEntry fileEntry, ServiceContext serviceContext)
		throws PortalException {

		return DLAppServiceUtil.updateFileEntry(
			fileEntry.getFileEntryId(), fileEntry.getFileName(),
			fileEntry.getMimeType(), fileEntry.getTitle(), StringPool.BLANK,
			fileEntry.getDescription(), RandomTestUtil.randomString(),
			DLVersionNumberIncrease.MINOR, TestDataConstants.TEST_BYTE_ARRAY,
			fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
			fileEntry.getReviewDate(), serviceContext);
	}

	private FileEntry _updateStatus(
			FileEntry fileEntry, int status, ServiceContext serviceContext)
		throws Exception {

		FileVersion fileVersion = fileEntry.getFileVersion();

		DLFileEntryLocalServiceUtil.updateStatus(
			TestPropsValues.getUserId(), fileVersion.getFileVersionId(), status,
			serviceContext,
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_URL, "http://localhost"
			).build());

		return DLAppLocalServiceUtil.getFileEntry(fileEntry.getFileEntryId());
	}

	private void _withMaximumNumberOfVersionsConfigured(
			int maximumNumberOfVersions,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				"maximumNumberOfVersions", maximumNumberOfVersions
			).build();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.document.library.configuration." +
						"DLConfiguration",
					dictionary)) {

			unsafeRunnable.run();
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

}