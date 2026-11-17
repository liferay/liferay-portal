/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.document.library.repository.external;

import com.liferay.document.library.repository.authorization.oauth2.TokenStore;
import com.liferay.document.library.repository.external.ExtRepositoryFileVersion;
import com.liferay.document.library.repository.external.ExtRepositoryFileVersionDescriptor;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.sharepoint.rest.repository.internal.configuration.SharepointRepositoryConfiguration;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.external.model.SharepointFileEntry;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.external.model.SharepointFileVersion;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Akhash Ramprakash
 */
public class SharepointExtRepositoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetExtRepositoryFileVersion() throws Exception {
		SharepointExtRepository sharepointExtRepository = Mockito.spy(
			new SharepointExtRepository(
				Collections.emptyMap(),
				Mockito.mock(SharepointRepositoryConfiguration.class),
				Mockito.mock(TokenStore.class)));

		String extRepositoryModelKey = "/" + RandomTestUtil.randomString();
		String version = RandomTestUtil.randomString();

		SharepointFileEntry sharepointFileEntry = new SharepointFileEntry(
			extRepositoryModelKey, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.nextDate(),
			RandomTestUtil.nextDate(), RandomTestUtil.randomLong(),
			extRepositoryModelKey + ":" + RandomTestUtil.randomInt(), version,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), null);

		ExtRepositoryFileVersion latestExtRepositoryFileVersion =
			sharepointFileEntry.asFileVersion();

		ExtRepositoryFileVersionDescriptor extRepositoryFileVersionDescriptor =
			sharepointExtRepository.getExtRepositoryFileVersionDescriptor(
				latestExtRepositoryFileVersion.getExtRepositoryModelKey());

		Assert.assertSame(
			latestExtRepositoryFileVersion,
			sharepointExtRepository.getExtRepositoryFileVersion(
				sharepointFileEntry,
				extRepositoryFileVersionDescriptor.getVersion()));

		Assert.assertSame(
			latestExtRepositoryFileVersion,
			sharepointExtRepository.getExtRepositoryFileVersion(
				sharepointFileEntry, version));

		Mockito.verify(
			sharepointExtRepository, Mockito.never()
		).getExtRepositoryFileVersions(
			sharepointFileEntry
		);

		String olderVersion = RandomTestUtil.randomString();

		ExtRepositoryFileVersion olderExtRepositoryFileVersion =
			new SharepointFileVersion(
				extRepositoryModelKey + ":" + olderVersion, olderVersion,
				RandomTestUtil.randomString(), RandomTestUtil.nextDate(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLong(), sharepointFileEntry);

		Mockito.doReturn(
			Arrays.asList(
				latestExtRepositoryFileVersion, olderExtRepositoryFileVersion)
		).when(
			sharepointExtRepository
		).getExtRepositoryFileVersions(
			sharepointFileEntry
		);

		Assert.assertSame(
			olderExtRepositoryFileVersion,
			sharepointExtRepository.getExtRepositoryFileVersion(
				sharepointFileEntry, olderVersion));
	}

}