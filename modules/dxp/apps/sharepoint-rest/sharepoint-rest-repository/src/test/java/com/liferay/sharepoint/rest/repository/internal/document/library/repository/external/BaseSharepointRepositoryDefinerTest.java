/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.document.library.repository.external;

import com.liferay.document.library.repository.external.MinorVersionCapability;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.capabilities.PortalCapabilityLocator;
import com.liferay.portal.kernel.repository.registry.CapabilityRegistry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.sharepoint.rest.repository.internal.configuration.SharepointRepositoryConfiguration;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/**
 * @author Akhash Ramprakash
 */
public class BaseSharepointRepositoryDefinerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_baseSharepointRepositoryDefiner.portalCapabilityLocator = Mockito.mock(
			PortalCapabilityLocator.class);
		_baseSharepointRepositoryDefiner.repositoryLocalService =
			_repositoryLocalService;

		ReflectionTestUtil.setFieldValue(
			_baseSharepointRepositoryDefiner, "_portalCache", _portalCache);
		ReflectionTestUtil.setFieldValue(
			_baseSharepointRepositoryDefiner,
			"_sharepointRepositoryConfiguration",
			Mockito.mock(SharepointRepositoryConfiguration.class));

		Mockito.when(
			_repositoryLocalService.getRepository(Mockito.anyLong())
		).thenReturn(
			Mockito.mock(Repository.class)
		);
	}

	@Test
	public void testIsMinorVersionEnabled() throws Exception {
		_testIsMinorVersionEnabledWhenNotCached();
		_testIsMinorVersionEnabledWhenPrincipalExceptionIsThrown();
	}

	private MinorVersionCapability _getMinorVersionCapability(
		long repositoryId) {

		CapabilityRegistry<DocumentRepository> capabilityRegistry =
			Mockito.mock(CapabilityRegistry.class);

		DocumentRepository documentRepository = Mockito.mock(
			DocumentRepository.class);

		Mockito.when(
			documentRepository.getRepositoryId()
		).thenReturn(
			repositoryId
		);

		Mockito.when(
			capabilityRegistry.getTarget()
		).thenReturn(
			documentRepository
		);

		_baseSharepointRepositoryDefiner.registerCapabilities(
			capabilityRegistry);

		ArgumentCaptor<MinorVersionCapability> argumentCaptor =
			ArgumentCaptor.forClass(MinorVersionCapability.class);

		Mockito.verify(
			capabilityRegistry
		).addExportedCapability(
			Mockito.eq(MinorVersionCapability.class), argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private void _testIsMinorVersionEnabledWhenNotCached() throws Exception {
		long repositoryId = RandomTestUtil.randomLong();

		MinorVersionCapability minorVersionCapability =
			_getMinorVersionCapability(repositoryId);

		try (MockedConstruction<SharepointExtRepository>
				sharepointExtRepositoryMockedConstruction =
					Mockito.mockConstruction(
						SharepointExtRepository.class,
						(sharepointExtRepository, context) -> Mockito.when(
							sharepointExtRepository.isMinorVersionEnabled()
						).thenReturn(
							true
						))) {

			Assert.assertTrue(minorVersionCapability.isMinorVersionEnabled());

			Mockito.verify(
				_portalCache
			).put(
				repositoryId, true, 300
			);

			Mockito.when(
				_portalCache.get(repositoryId)
			).thenReturn(
				false
			);

			Assert.assertFalse(minorVersionCapability.isMinorVersionEnabled());

			List<SharepointExtRepository> sharepointExtRepositories =
				sharepointExtRepositoryMockedConstruction.constructed();

			Assert.assertEquals(
				sharepointExtRepositories.toString(), 1,
				sharepointExtRepositories.size());
		}
	}

	private void _testIsMinorVersionEnabledWhenPrincipalExceptionIsThrown()
		throws Exception {

		long repositoryId = RandomTestUtil.randomLong();

		MinorVersionCapability minorVersionCapability =
			_getMinorVersionCapability(repositoryId);

		try (MockedConstruction<SharepointExtRepository>
				sharepointExtRepositoryMockedConstruction =
					Mockito.mockConstruction(
						SharepointExtRepository.class,
						(sharepointExtRepository, context) -> Mockito.when(
							sharepointExtRepository.isMinorVersionEnabled()
						).thenThrow(
							new PrincipalException(
								RandomTestUtil.randomString())
						))) {

			Assert.assertThrows(
				PrincipalException.class,
				minorVersionCapability::isMinorVersionEnabled);

			Mockito.verify(
				_portalCache, Mockito.never()
			).put(
				Mockito.eq(repositoryId), Mockito.anyBoolean(), Mockito.anyInt()
			);
		}
	}

	private final BaseSharepointRepositoryDefiner
		_baseSharepointRepositoryDefiner =
			new ScopedSharepointRepositoryDefiner();
	private final PortalCache<Long, Boolean> _portalCache = Mockito.mock(
		PortalCache.class);
	private final RepositoryLocalService _repositoryLocalService = Mockito.mock(
		RepositoryLocalService.class);

}