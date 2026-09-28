/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.field.filter.parser;

import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.object.field.frontend.data.set.filter.OneToManySelectionFDSFilter;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectViewFilterColumn;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Carolina Barbosa
 */
public class OneToManyObjectFieldFilterStrategyTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetFDSFilter() throws Exception {
		Mockito.when(
			_objectDefinition.getRESTContextPath()
		).thenReturn(
			_REST_CONTEXT_PATH
		);

		Mockito.when(
			_objectDefinition.getTitleObjectFieldId()
		).thenReturn(
			_TITLE_OBJECT_FIELD_ID
		);

		Mockito.when(
			_objectDefinitionLocalService.getObjectDefinition(
				_objectRelationship.getObjectDefinitionId1())
		).thenReturn(
			_objectDefinition
		);

		Mockito.when(
			_relationshipObjectField.getLabel(LocaleUtil.US)
		).thenReturn(
			_RELATIONSHIP_OBJECT_FIELD_LABEL
		);

		Mockito.when(
			_relationshipObjectField.getName()
		).thenReturn(
			_RELATIONSHIP_OBJECT_FIELD_NAME
		);

		Mockito.when(
			_titleObjectField.getLabel(LocaleUtil.US)
		).thenReturn(
			_TITLE_OBJECT_FIELD_LABEL
		);

		Mockito.when(
			_titleObjectField.getName()
		).thenReturn(
			_TITLE_OBJECT_FIELD_NAME
		);

		Mockito.when(
			_objectFieldLocalService.getObjectField(_TITLE_OBJECT_FIELD_ID)
		).thenReturn(
			_titleObjectField
		);

		Mockito.when(
			_objectRelationshipLocalService.
				fetchObjectRelationshipByObjectFieldId2(
					_relationshipObjectField.getObjectFieldId())
		).thenReturn(
			_objectRelationship
		);

		OneToManyObjectFieldFilterStrategy oneToManyObjectFieldFilterStrategy =
			new OneToManyObjectFieldFilterStrategy(
				0, LocaleUtil.US, _objectDefinition,
				_objectDefinitionLocalService, null, _relationshipObjectField,
				_objectFieldLocalService, _objectRelationshipLocalService,
				_objectViewFilterColumn, null);

		OneToManySelectionFDSFilter oneToManySelectionFDSFilter =
			(OneToManySelectionFDSFilter)
				oneToManyObjectFieldFilterStrategy.getFDSFilter();

		Assert.assertEquals(
			"/o" + _REST_CONTEXT_PATH, oneToManySelectionFDSFilter.getAPIURL());
		Assert.assertEquals(
			_RELATIONSHIP_OBJECT_FIELD_NAME,
			oneToManySelectionFDSFilter.getId());
		Assert.assertEquals(
			_TITLE_OBJECT_FIELD_NAME,
			oneToManySelectionFDSFilter.getItemLabel());
		Assert.assertEquals(
			_RELATIONSHIP_OBJECT_FIELD_LABEL + StringPool.SPACE +
				_TITLE_OBJECT_FIELD_LABEL,
			oneToManySelectionFDSFilter.getLabel());
	}

	@Test
	public void testGetSelectionFDSFilterItems() throws Exception {
		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			"com.liferay.portal.kernel.model.User"
		);

		long objectDefinitionId = RandomTestUtil.randomLong();

		Mockito.when(
			_objectDefinition.getObjectDefinitionId()
		).thenReturn(
			objectDefinitionId
		);

		Mockito.when(
			_objectDefinition.isUnmodifiableSystemObject()
		).thenReturn(
			true
		);

		long primaryKey1 = RandomTestUtil.randomLong();
		long primaryKey2 = RandomTestUtil.randomLong();
		long primaryKey3 = RandomTestUtil.randomLong();

		try (MockedStatic<PersistedModelLocalServiceRegistryUtil>
				persistedModelLocalServiceRegistryUtilMockedStatic =
					Mockito.mockStatic(
						PersistedModelLocalServiceRegistryUtil.class)) {

			String primaryKeyColumnName = RandomTestUtil.randomString();

			Mockito.when(
				_user.getModelAttributes()
			).thenReturn(
				Collections.singletonMap(primaryKeyColumnName, primaryKey1)
			);

			Mockito.when(
				_persistedModelLocalService.fetchPersistedModel(primaryKey2)
			).thenReturn(
				_user
			);

			Mockito.when(
				_persistedModelLocalService.fetchPersistedModel(primaryKey3)
			).thenReturn(
				null
			);

			persistedModelLocalServiceRegistryUtilMockedStatic.when(
				() ->
					PersistedModelLocalServiceRegistryUtil.
						getPersistedModelLocalService(
							_objectDefinition.getClassName())
			).thenReturn(
				_persistedModelLocalService
			);

			Mockito.when(
				_primaryKeyColumn.getName()
			).thenReturn(
				primaryKeyColumnName
			);

			Mockito.doReturn(
				_primaryKeyColumn
			).when(
				_systemObjectDefinitionManager
			).getPrimaryKeyColumn();

			Mockito.when(
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						_objectDefinition.getName())
			).thenReturn(
				_systemObjectDefinitionManager
			);

			String titleValue = RandomTestUtil.randomString();

			Mockito.when(
				_objectEntryLocalService.getTitleValue(
					objectDefinitionId, primaryKey1)
			).thenReturn(
				titleValue
			);

			JSONArray jsonArray = JSONUtil.putAll(primaryKey2, primaryKey3);

			OneToManyObjectFieldFilterStrategy
				oneToManyObjectFieldFilterStrategy = Mockito.spy(
					new OneToManyObjectFieldFilterStrategy(
						0, LocaleUtil.US, _objectDefinition,
						_objectDefinitionLocalService, _objectEntryLocalService,
						_relationshipObjectField, _objectFieldLocalService,
						_objectRelationshipLocalService,
						_objectViewFilterColumn,
						_systemObjectDefinitionManagerRegistry));

			Mockito.doReturn(
				jsonArray
			).when(
				oneToManyObjectFieldFilterStrategy
			).getJSONArray();

			List<SelectionFDSFilterItem> selectionFDSFilterItems =
				oneToManyObjectFieldFilterStrategy.getSelectionFDSFilterItems();

			Assert.assertEquals(
				selectionFDSFilterItems.toString(), 1,
				selectionFDSFilterItems.size());

			SelectionFDSFilterItem selectionFDSFilterItem =
				selectionFDSFilterItems.get(0);

			Assert.assertEquals(titleValue, selectionFDSFilterItem.getLabel());
			Assert.assertEquals(primaryKey2, selectionFDSFilterItem.getValue());

			Mockito.verify(
				_objectEntryLocalService
			).getTitleValue(
				objectDefinitionId, primaryKey1
			);

			Mockito.verify(
				_objectEntryLocalService, Mockito.never()
			).getTitleValue(
				objectDefinitionId, primaryKey3
			);
		}
	}

	private static final String _RELATIONSHIP_OBJECT_FIELD_LABEL =
		RandomTestUtil.randomString();

	private static final String _RELATIONSHIP_OBJECT_FIELD_NAME =
		RandomTestUtil.randomString();

	private static final String _REST_CONTEXT_PATH =
		RandomTestUtil.randomString();

	private static final long _TITLE_OBJECT_FIELD_ID =
		RandomTestUtil.randomLong();

	private static final String _TITLE_OBJECT_FIELD_LABEL =
		RandomTestUtil.randomString();

	private static final String _TITLE_OBJECT_FIELD_NAME =
		RandomTestUtil.randomString();

	@Mock
	private ObjectDefinition _objectDefinition;

	@Mock
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Mock
	private ObjectEntryLocalService _objectEntryLocalService;

	@Mock
	private ObjectFieldLocalService _objectFieldLocalService;

	@Mock
	private ObjectRelationship _objectRelationship;

	@Mock
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Mock
	private ObjectViewFilterColumn _objectViewFilterColumn;

	@Mock
	private PersistedModelLocalService _persistedModelLocalService;

	@Mock
	private Column<?, Long> _primaryKeyColumn;

	@Mock
	private ObjectField _relationshipObjectField;

	@Mock
	private SystemObjectDefinitionManager _systemObjectDefinitionManager;

	@Mock
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Mock
	private ObjectField _titleObjectField;

	@Mock
	private User _user;

}