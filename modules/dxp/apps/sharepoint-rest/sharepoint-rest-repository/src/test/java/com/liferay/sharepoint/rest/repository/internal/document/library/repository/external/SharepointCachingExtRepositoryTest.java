/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.document.library.repository.external;

import com.liferay.document.library.repository.external.ExtRepository;
import com.liferay.document.library.repository.external.ExtRepositoryFileEntry;
import com.liferay.document.library.repository.external.ExtRepositoryFolder;
import com.liferay.document.library.repository.external.ExtRepositoryObject;
import com.liferay.document.library.repository.external.ExtRepositoryObjectType;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Akhash Ramprakash
 */
public class SharepointCachingExtRepositoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		CentralizedThreadLocal.clearShortLivedCentralizedThreadLocals();
	}

	@Test
	public void testGetExtRepositoryObjects() throws Exception {
		ExtRepository extRepository = Mockito.mock(ExtRepository.class);

		String extRepositoryFolderKey = "/" + RandomTestUtil.randomString();

		List<ExtRepositoryFileEntry> extRepositoryFileEntries =
			Collections.singletonList(
				Mockito.mock(ExtRepositoryFileEntry.class));

		Mockito.when(
			extRepository.getExtRepositoryObjects(
				ExtRepositoryObjectType.FILE, extRepositoryFolderKey)
		).thenReturn(
			extRepositoryFileEntries
		);

		List<ExtRepositoryFolder> extRepositoryFolders =
			Collections.singletonList(Mockito.mock(ExtRepositoryFolder.class));

		Mockito.when(
			extRepository.getExtRepositoryObjects(
				ExtRepositoryObjectType.FOLDER, extRepositoryFolderKey)
		).thenReturn(
			extRepositoryFolders
		);

		SharepointCachingExtRepository sharepointCachingExtRepository =
			new SharepointCachingExtRepository(extRepository);

		for (int i = 0; i < 2; i++) {
			Assert.assertSame(
				extRepositoryFileEntries,
				sharepointCachingExtRepository.getExtRepositoryObjects(
					ExtRepositoryObjectType.FILE, extRepositoryFolderKey));
			Assert.assertSame(
				extRepositoryFolders,
				sharepointCachingExtRepository.getExtRepositoryObjects(
					ExtRepositoryObjectType.FOLDER, extRepositoryFolderKey));
			Assert.assertEquals(
				Arrays.asList(
					extRepositoryFileEntries.get(0),
					extRepositoryFolders.get(0)),
				sharepointCachingExtRepository.getExtRepositoryObjects(
					ExtRepositoryObjectType.OBJECT, extRepositoryFolderKey));
		}

		Mockito.verify(
			extRepository
		).getExtRepositoryObjects(
			ExtRepositoryObjectType.FILE, extRepositoryFolderKey
		);

		Mockito.verify(
			extRepository
		).getExtRepositoryObjects(
			ExtRepositoryObjectType.FOLDER, extRepositoryFolderKey
		);

		Mockito.verify(
			extRepository, Mockito.never()
		).getExtRepositoryObjects(
			ExtRepositoryObjectType.OBJECT, extRepositoryFolderKey
		);
	}

	@Test
	public void testGetExtRepositoryParentFolder() throws Exception {
		_testGetExtRepositoryParentFolderWhenParentIsCached();
		_testGetExtRepositoryParentFolderWhenParentIsRequestedTwice();
		_testGetExtRepositoryParentFolderWhenParentIsRoot();
	}

	private ExtRepositoryObject _getExtRepositoryObject(
		String extRepositoryModelKey) {

		ExtRepositoryObject extRepositoryObject = Mockito.mock(
			ExtRepositoryObject.class);

		Mockito.when(
			extRepositoryObject.getExtRepositoryModelKey()
		).thenReturn(
			extRepositoryModelKey
		);

		return extRepositoryObject;
	}

	private void _testGetExtRepositoryParentFolderWhenParentIsCached()
		throws Exception {

		ExtRepository extRepository = Mockito.mock(ExtRepository.class);

		ExtRepositoryFolder extRepositoryFolder = Mockito.mock(
			ExtRepositoryFolder.class);
		String extRepositoryFolderKey = "/" + RandomTestUtil.randomString();

		Mockito.when(
			extRepository.getExtRepositoryObject(
				ExtRepositoryObjectType.FOLDER, extRepositoryFolderKey)
		).thenReturn(
			extRepositoryFolder
		);

		SharepointCachingExtRepository sharepointCachingExtRepository =
			new SharepointCachingExtRepository(extRepository);

		sharepointCachingExtRepository.getExtRepositoryObject(
			ExtRepositoryObjectType.FOLDER, extRepositoryFolderKey);

		Assert.assertSame(
			extRepositoryFolder,
			sharepointCachingExtRepository.getExtRepositoryParentFolder(
				_getExtRepositoryObject(
					extRepositoryFolderKey + "/" +
						RandomTestUtil.randomString())));
	}

	private void _testGetExtRepositoryParentFolderWhenParentIsRequestedTwice()
		throws Exception {

		ExtRepository extRepository = Mockito.mock(ExtRepository.class);

		ExtRepositoryFolder extRepositoryFolder = Mockito.mock(
			ExtRepositoryFolder.class);

		Mockito.when(
			extRepository.getExtRepositoryParentFolder(
				Mockito.any(ExtRepositoryObject.class))
		).thenReturn(
			extRepositoryFolder
		);

		SharepointCachingExtRepository sharepointCachingExtRepository =
			new SharepointCachingExtRepository(extRepository);

		String extRepositoryFolderKey = "/" + RandomTestUtil.randomString();

		for (int i = 0; i < 2; i++) {
			Assert.assertSame(
				extRepositoryFolder,
				sharepointCachingExtRepository.getExtRepositoryParentFolder(
					_getExtRepositoryObject(
						extRepositoryFolderKey + "/" +
							RandomTestUtil.randomString())));
		}

		Mockito.verify(
			extRepository
		).getExtRepositoryParentFolder(
			Mockito.any(ExtRepositoryObject.class)
		);
	}

	private void _testGetExtRepositoryParentFolderWhenParentIsRoot()
		throws Exception {

		ExtRepository extRepository = Mockito.mock(ExtRepository.class);

		ExtRepositoryFolder extRepositoryFolder = Mockito.mock(
			ExtRepositoryFolder.class);

		Mockito.when(
			extRepository.getExtRepositoryParentFolder(
				Mockito.any(ExtRepositoryObject.class))
		).thenReturn(
			extRepositoryFolder
		);

		String rootFolderKey = "/" + RandomTestUtil.randomString();

		Mockito.when(
			extRepository.getRootFolderKey()
		).thenReturn(
			rootFolderKey
		);

		Mockito.when(
			extRepository.getExtRepositoryObject(
				ExtRepositoryObjectType.FOLDER, rootFolderKey)
		).thenReturn(
			Mockito.mock(ExtRepositoryFolder.class)
		);

		SharepointCachingExtRepository sharepointCachingExtRepository =
			new SharepointCachingExtRepository(extRepository);

		sharepointCachingExtRepository.getExtRepositoryObject(
			ExtRepositoryObjectType.FOLDER, rootFolderKey);

		Assert.assertSame(
			extRepositoryFolder,
			sharepointCachingExtRepository.getExtRepositoryParentFolder(
				_getExtRepositoryObject(
					rootFolderKey + "/" + RandomTestUtil.randomString())));
	}

}