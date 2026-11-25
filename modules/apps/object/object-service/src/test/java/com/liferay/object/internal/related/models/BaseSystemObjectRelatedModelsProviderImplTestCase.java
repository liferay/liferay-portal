/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;

import java.util.Collections;

import org.junit.Before;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public abstract class BaseSystemObjectRelatedModelsProviderImplTestCase {

	@Before
	public void setUp() throws Exception {
		_setUpObjectDefinitionLocalService();
		_setUpObjectFieldLocalService();
		_setUpObjectRelationshipLocalService();
		_setUpSystemObjectDefinitionManager();
	}

	protected MockedStatic<PersistedModelLocalServiceRegistryUtil>
		mockPersistedModelLocalServiceRegistryUtil(
			PersistedModelLocalService persistedModelLocalService) {

		MockedStatic<PersistedModelLocalServiceRegistryUtil>
			persistedModelLocalServiceRegistryUtilMockedStatic =
				Mockito.mockStatic(
					PersistedModelLocalServiceRegistryUtil.class);

		persistedModelLocalServiceRegistryUtilMockedStatic.when(
			() ->
				PersistedModelLocalServiceRegistryUtil.
					getPersistedModelLocalService(Mockito.any())
		).thenReturn(
			persistedModelLocalService
		);

		return persistedModelLocalServiceRegistryUtilMockedStatic;
	}

	protected static final long OBJECT_RELATIONSHIP_ID =
		RandomTestUtil.randomLong();

	protected final ObjectDefinition objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	protected final ObjectDefinitionLocalService objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	protected final ObjectFieldLocalService objectFieldLocalService =
		Mockito.mock(ObjectFieldLocalService.class);
	protected final ObjectRelationship objectRelationship = Mockito.mock(
		ObjectRelationship.class);
	protected final ObjectRelationshipLocalService
		objectRelationshipLocalService = Mockito.mock(
			ObjectRelationshipLocalService.class);
	protected final SystemObjectDefinitionManager
		systemObjectDefinitionManager = Mockito.mock(
			SystemObjectDefinitionManager.class);

	private void _setUpObjectDefinitionLocalService() throws Exception {
		Mockito.when(
			objectDefinition.getPKObjectFieldDBColumnName()
		).thenReturn(
			"objectEntryId"
		);

		Mockito.when(
			objectDefinitionLocalService.getObjectDefinition(Mockito.anyLong())
		).thenReturn(
			objectDefinition
		);
	}

	private void _setUpObjectFieldLocalService() throws Exception {
		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getDBColumnName()
		).thenReturn(
			"r_relationship_c_id"
		);

		Mockito.when(
			objectField.getDBColumnNames()
		).thenReturn(
			new String[] {"r_relationship_c_id"}
		);

		Mockito.when(
			objectField.getDBType()
		).thenReturn(
			ObjectFieldConstants.DB_TYPE_LONG
		);

		Mockito.when(
			objectField.hasInsertValues()
		).thenReturn(
			true
		);

		Mockito.when(
			objectFieldLocalService.getObjectField(Mockito.anyLong())
		).thenReturn(
			objectField
		);

		Mockito.when(
			objectFieldLocalService.getObjectFields(
				Mockito.anyLong(), Mockito.any())
		).thenReturn(
			Collections.singletonList(objectField)
		);
	}

	private void _setUpObjectRelationshipLocalService() throws Exception {
		Mockito.when(
			objectRelationshipLocalService.getObjectRelationship(
				OBJECT_RELATIONSHIP_ID)
		).thenReturn(
			objectRelationship
		);
	}

	private void _setUpSystemObjectDefinitionManager() {
		Mockito.doReturn(
			ObjectEntryTable.INSTANCE.objectEntryId
		).when(
			systemObjectDefinitionManager
		).getPrimaryKeyColumn();

		Mockito.when(
			systemObjectDefinitionManager.getTable()
		).thenReturn(
			ObjectEntryTable.INSTANCE
		);
	}

}