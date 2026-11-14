/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.external;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.repository.external.model.ExtRepositoryFileEntryAdapter;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.RepositoryEntryLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Caio Farias
 */
public class ExtRepositoryAdapterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_dlRepositoryGuestPassword = GetterUtil.getString(
			PropsUtil.get(PropsKeys.DL_REPOSITORY_GUEST_PASSWORD));
		_secretResolverSnapshot = ReflectionTestUtil.getAndSetFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			new Snapshot<SecretResolver>(
				SecretResolverUtil.class, SecretResolver.class) {

				@Override
				public SecretResolver get() {
					return _secretResolver;
				}

			});
	}

	@After
	public void tearDown() {
		PropsUtil.set(
			PropsKeys.DL_REPOSITORY_GUEST_PASSWORD, _dlRepositoryGuestPassword);
		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			_secretResolverSnapshot);
	}

	@Test
	public void testGetFileEntries() throws Exception {
		ExtRepository extRepository = Mockito.mock(ExtRepository.class);

		ExtRepositoryFileEntry extRepositoryFileEntry1 =
			_getExtRepositoryFileEntry();
		ExtRepositoryFileEntry extRepositoryFileEntry2 =
			_getExtRepositoryFileEntry();

		String rootFolderKey = RandomTestUtil.randomString();

		Mockito.doReturn(
			Arrays.asList(extRepositoryFileEntry1, extRepositoryFileEntry2)
		).when(
			extRepository
		).getExtRepositoryObjects(
			ExtRepositoryObjectType.FILE, rootFolderKey
		);

		Mockito.when(
			extRepository.getExtRepositoryFileVersions(extRepositoryFileEntry1)
		).thenThrow(
			new PortalException()
		);

		ExtRepositoryFileVersion extRepositoryFileVersion = Mockito.mock(
			ExtRepositoryFileVersion.class);

		Mockito.when(
			extRepositoryFileVersion.getExtRepositoryModelKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			extRepository.getExtRepositoryFileVersions(extRepositoryFileEntry2)
		).thenReturn(
			Collections.singletonList(extRepositoryFileVersion)
		);

		Mockito.when(
			extRepository.getRootFolderKey()
		).thenReturn(
			rootFolderKey
		);

		ExtRepositoryAdapter extRepositoryAdapter = new ExtRepositoryAdapter(
			extRepository);

		extRepositoryAdapter.setDLAppHelperLocalService(
			Mockito.mock(DLAppHelperLocalService.class));

		RepositoryEntryLocalService repositoryEntryLocalService = Mockito.mock(
			RepositoryEntryLocalService.class);

		Mockito.when(
			repositoryEntryLocalService.getRepositoryEntry(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyString())
		).thenReturn(
			Mockito.mock(RepositoryEntry.class)
		);

		extRepositoryAdapter.setRepositoryEntryLocalService(
			repositoryEntryLocalService);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ExtRepositoryAdapter.class.getName(), LoggerTestUtil.WARN)) {

			List<FileEntry> fileEntries = extRepositoryAdapter.getFileEntries(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			Assert.assertEquals(fileEntries.toString(), 1, fileEntries.size());

			ExtRepositoryFileEntryAdapter extRepositoryFileEntryAdapter =
				(ExtRepositoryFileEntryAdapter)fileEntries.get(0);

			Assert.assertSame(
				extRepositoryFileEntry2,
				extRepositoryFileEntryAdapter.getExtRepositoryModel());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to get external repository object " +
					extRepositoryFileEntry1.getExtRepositoryModelKey(),
				logEntry.getMessage());
		}
	}

	@Test
	public void testGetPassword() {
		String keyReferenceString = KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				KeyReference.Type.SECRET));
		String password = RandomTestUtil.randomString();

		_secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			_secretResolver.resolve(CompanyConstants.SYSTEM, keyReferenceString)
		).thenReturn(
			password
		);

		_testGetPassword(password, keyReferenceString);
		_testGetPassword(password, password);
	}

	private ExtRepositoryFileEntry _getExtRepositoryFileEntry() {
		ExtRepositoryFileEntry extRepositoryFileEntry = Mockito.mock(
			ExtRepositoryFileEntry.class);

		Mockito.when(
			extRepositoryFileEntry.getExtRepositoryModelKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return extRepositoryFileEntry;
	}

	private void _testGetPassword(String expectedPassword, String password) {
		PropsUtil.set(PropsKeys.DL_REPOSITORY_GUEST_PASSWORD, password);

		ExtRepositoryAdapter extRepositoryAdapter = new ExtRepositoryAdapter(
			Mockito.mock(ExtRepository.class));

		Assert.assertEquals(
			expectedPassword,
			ReflectionTestUtil.invoke(
				extRepositoryAdapter, "_getPassword", new Class<?>[0]));
	}

	private String _dlRepositoryGuestPassword;
	private SecretResolver _secretResolver;
	private Snapshot<SecretResolver> _secretResolverSnapshot;

}