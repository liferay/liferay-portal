/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.spi.ast.DefaultASTNodeListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public class SystemObject1toMObjectRelatedModelsProviderImplTest
	extends BaseSystemObjectRelatedModelsProviderImplTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testFetchRelatedModel() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			objectRelationship.getCompanyId()
		).thenReturn(
			companyId
		);

		PersistedModelLocalService persistedModelLocalService = Mockito.mock(
			PersistedModelLocalService.class);

		Mockito.when(
			persistedModelLocalService.dslQuery(Mockito.any())
		).thenReturn(
			Collections.emptyList()
		);

		long groupId = RandomTestUtil.randomLong();

		try (MockedStatic<PersistedModelLocalServiceRegistryUtil>
				persistedModelLocalServiceRegistryUtilMockedStatic =
					mockPersistedModelLocalServiceRegistryUtil(
						persistedModelLocalService)) {

			SystemObject1toMObjectRelatedModelsProviderImpl<User>
				systemObject1toMObjectRelatedModelsProviderImpl =
					new SystemObject1toMObjectRelatedModelsProviderImpl<>(
						Mockito.mock(InlineSQLHelper.class), objectDefinition,
						objectDefinitionLocalService,
						Mockito.mock(ObjectEntryLocalService.class),
						objectFieldLocalService, objectRelationshipLocalService,
						systemObjectDefinitionManager,
						Mockito.mock(
							SystemObjectDefinitionManagerRegistry.class));

			Assert.assertNull(
				systemObject1toMObjectRelatedModelsProviderImpl.
					fetchRelatedModel(
						groupId, OBJECT_RELATIONSHIP_ID,
						RandomTestUtil.randomLong()));
		}

		ArgumentCaptor<DSLQuery> dslQueryArgumentCaptor =
			ArgumentCaptor.forClass(DSLQuery.class);

		Mockito.verify(
			persistedModelLocalService
		).dslQuery(
			dslQueryArgumentCaptor.capture()
		);

		DSLQuery dslQuery = dslQueryArgumentCaptor.getValue();

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		dslQuery.toSQL(
			sql -> {
			},
			defaultASTNodeListener);

		List<Object> scalarValues = defaultASTNodeListener.getScalarValues();

		Assert.assertTrue(scalarValues.contains(companyId));
		Assert.assertFalse(scalarValues.contains(groupId));
	}

}