/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public class SystemObject1to1ObjectRelatedModelsProviderImplTest
	extends BaseSystemObjectRelatedModelsProviderImplTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetRelatedModels() throws Exception {
		AssertUtils.assertFailure(
			PortalException.class,
			"More than one model is related through the one to one object " +
				"relationship " + OBJECT_RELATIONSHIP_ID,
			() -> _getRelatedModels(
				List.of(Mockito.mock(User.class), Mockito.mock(User.class)),
				0));

		User user = Mockito.mock(User.class);

		Assert.assertEquals(
			Collections.singletonList(user),
			_getRelatedModels(Collections.singletonList(user), 0));
		Assert.assertEquals(
			Collections.emptyList(),
			_getRelatedModels(Collections.singletonList(user), 1));
	}

	private List<User> _getRelatedModels(List<User> relatedModels, int start)
		throws Exception {

		PersistedModelLocalService persistedModelLocalService = Mockito.mock(
			PersistedModelLocalService.class);

		Mockito.when(
			persistedModelLocalService.dslQuery(Mockito.any())
		).thenReturn(
			relatedModels
		);

		try (MockedStatic<PersistedModelLocalServiceRegistryUtil>
				persistedModelLocalServiceRegistryUtilMockedStatic =
					mockPersistedModelLocalServiceRegistryUtil(
						persistedModelLocalService)) {

			SystemObject1to1ObjectRelatedModelsProviderImpl<User>
				systemObject1to1ObjectRelatedModelsProviderImpl =
					new SystemObject1to1ObjectRelatedModelsProviderImpl<>(
						Mockito.mock(InlineSQLHelper.class), objectDefinition,
						objectDefinitionLocalService,
						Mockito.mock(ObjectEntryLocalService.class),
						objectFieldLocalService, objectRelationshipLocalService,
						systemObjectDefinitionManager,
						Mockito.mock(
							SystemObjectDefinitionManagerRegistry.class));

			return systemObject1to1ObjectRelatedModelsProviderImpl.
				getRelatedModels(
					0, OBJECT_RELATIONSHIP_ID, null, false,
					RandomTestUtil.randomLong(), null, start, start + 1, null);
		}
	}

}