/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.engine;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.internal.util.PIMLinkUtil;
import com.liferay.site.pim.site.initializer.link.PIMLinkRelatedEntry;
import com.liferay.site.pim.site.initializer.link.PIMLinkType;
import com.liferay.site.pim.site.initializer.link.PIMLinkTypeRegistry;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Balazs Breier
 */
public class PIMLinkEngineImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			objectDefinition.getPKObjectFieldName()
		).thenReturn(
			"id"
		);

		Mockito.when(
			_objectDefinitionLocalService.getObjectDefinitionByClassName(
				_COMPANY_ID, _CLASS_NAME)
		).thenReturn(
			objectDefinition
		);

		ReflectionTestUtil.setFieldValue(
			_pimLinkEngineImpl, "_filterFactory", _filterFactory);
		ReflectionTestUtil.setFieldValue(
			_pimLinkEngineImpl, "_objectDefinitionLocalService",
			_objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimLinkEngineImpl, "_objectEntryLocalService",
			_objectEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimLinkEngineImpl, "_pimLinkTypeRegistry", _pimLinkTypeRegistry);

		_pimLinkUtilMockedStatic.when(
			() -> PIMLinkUtil.getPIMLinkObjectDefinition(_COMPANY_ID)
		).thenReturn(
			objectDefinition
		);

		PIMLinkType pimLinkType = Mockito.mock(PIMLinkType.class);

		Mockito.when(
			pimLinkType.isClustered()
		).thenReturn(
			true
		);

		Mockito.when(
			_pimLinkTypeRegistry.getPIMLinkType(_TYPE)
		).thenReturn(
			pimLinkType
		);
	}

	@After
	public void tearDown() {
		_pimLinkUtilMockedStatic.close();
	}

	@Test
	public void testAddPIMLinks() throws Exception {
		ObjectEntry objectEntry1 = _mockObjectEntry("CLUSTER-1", "PRODUCT-1");
		ObjectEntry objectEntry2 = _mockObjectEntry("CLUSTER-2", "PRODUCT-2");

		_mockValuesList(
			"clusterKey eq 'CLUSTER-1' and primary eq true and type eq '" +
				_TYPE + "'",
			Collections.singletonList(
				_getValues("CLUSTER-1", 1, true, "PRODUCT-1")));
		_mockValuesList(
			"clusterKey eq 'CLUSTER-2' and type eq '" + _TYPE + "'",
			Collections.singletonList(
				_getValues("CLUSTER-2", 2, true, "PRODUCT-2")));

		_pimLinkEngineImpl.addPIMLinks(
			objectEntry1, Collections.singletonList(objectEntry2), _TYPE);

		Mockito.verify(
			_objectEntryLocalService
		).partialUpdateObjectEntry(
			Mockito.anyLong(), Mockito.eq(2L), Mockito.eq(0L),
			Mockito.eq(
				HashMapBuilder.<String, Serializable>put(
					"clusterKey", "CLUSTER-1"
				).put(
					"primary", false
				).build()),
			Mockito.any(ServiceContext.class)
		);

		ObjectEntry objectEntry3 = _mockObjectEntry("CLUSTER-3", "PRODUCT-3");
		ObjectEntry objectEntry4 = _mockObjectEntry("CLUSTER-4", "PRODUCT-4");
		ObjectEntry objectEntry5 = _mockObjectEntry("CLUSTER-5", "PRODUCT-5");

		_pimLinkUtilMockedStatic.when(
			() -> PIMLinkUtil.getValuesList(
				_COMPANY_ID, _filterFactory,
				"clusterKey eq 'CLUSTER-3' and primary eq true and type eq '" +
					_TYPE + "'",
				_GROUP_ID)
		).thenReturn(
			Collections.emptyList(),
			Collections.singletonList(
				_getValues("CLUSTER-3", 4, true, "PRODUCT-4"))
		);

		_mockValuesList(
			"clusterKey eq 'CLUSTER-4' and type eq '" + _TYPE + "'",
			Collections.singletonList(
				_getValues("CLUSTER-4", 4, true, "PRODUCT-4")));
		_mockValuesList(
			"clusterKey eq 'CLUSTER-5' and type eq '" + _TYPE + "'",
			Collections.singletonList(
				_getValues("CLUSTER-5", 5, true, "PRODUCT-5")));

		_pimLinkEngineImpl.addPIMLinks(
			objectEntry3, Arrays.asList(objectEntry4, objectEntry5), _TYPE);

		Mockito.verify(
			_objectEntryLocalService
		).partialUpdateObjectEntry(
			Mockito.anyLong(), Mockito.eq(4L), Mockito.eq(0L),
			Mockito.eq(
				HashMapBuilder.<String, Serializable>put(
					"clusterKey", "CLUSTER-3"
				).build()),
			Mockito.any(ServiceContext.class)
		);

		Mockito.verify(
			_objectEntryLocalService
		).partialUpdateObjectEntry(
			Mockito.anyLong(), Mockito.eq(5L), Mockito.eq(0L),
			Mockito.eq(
				HashMapBuilder.<String, Serializable>put(
					"clusterKey", "CLUSTER-3"
				).put(
					"primary", false
				).build()),
			Mockito.any(ServiceContext.class)
		);
	}

	@Test
	public void testGetPIMLinkRelatedEntries() throws Exception {
		ObjectEntry objectEntry1 = _mockObjectEntry("CLUSTER-1", "PRODUCT-1");
		ObjectEntry objectEntry2 = _mockObjectEntry("CLUSTER-1", "PRODUCT-2");
		ObjectEntry objectEntry3 = _mockObjectEntry("CLUSTER-1", "PRODUCT-3");

		_mockValuesList(
			"clusterKey in ('CLUSTER-1')",
			Arrays.asList(
				_getValues("CLUSTER-1", 3, false, "PRODUCT-3"),
				_getValues("CLUSTER-1", 1, false, "PRODUCT-1"),
				_getValues("CLUSTER-1", 2, true, "PRODUCT-2")));
		_mockValuesList(
			StringBundler.concat(
				"sourceClassExternalReferenceCode eq 'PRODUCT-1' and ",
				"sourceClassName eq '", _CLASS_NAME, "'"),
			Collections.singletonList(
				_getValues("CLUSTER-1", 1, false, "PRODUCT-1")));

		List<PIMLinkRelatedEntry> pimLinkRelatedEntries =
			_pimLinkEngineImpl.getPIMLinkRelatedEntries(null, objectEntry1);

		Assert.assertEquals(
			pimLinkRelatedEntries.toString(), 3, pimLinkRelatedEntries.size());

		PIMLinkRelatedEntry pimLinkRelatedEntry = pimLinkRelatedEntries.get(0);

		Assert.assertEquals(objectEntry1, pimLinkRelatedEntry.getObjectEntry());
		Assert.assertFalse(pimLinkRelatedEntry.isPrimary());

		pimLinkRelatedEntry = pimLinkRelatedEntries.get(1);

		Assert.assertEquals(objectEntry2, pimLinkRelatedEntry.getObjectEntry());
		Assert.assertTrue(pimLinkRelatedEntry.isPrimary());

		pimLinkRelatedEntry = pimLinkRelatedEntries.get(2);

		Assert.assertEquals(objectEntry3, pimLinkRelatedEntry.getObjectEntry());
		Assert.assertFalse(pimLinkRelatedEntry.isPrimary());

		ObjectEntry objectEntry4 = _mockObjectEntry("CLUSTER-2", "PRODUCT-4");

		_mockValuesList(
			"clusterKey in ('CLUSTER-2')",
			Collections.singletonList(
				_getValues("CLUSTER-2", 4, false, "PRODUCT-4")));
		_mockValuesList(
			StringBundler.concat(
				"sourceClassExternalReferenceCode eq 'PRODUCT-4' and ",
				"sourceClassName eq '", _CLASS_NAME, "'"),
			Collections.singletonList(
				_getValues("CLUSTER-2", 4, false, "PRODUCT-4")));

		pimLinkRelatedEntries = _pimLinkEngineImpl.getPIMLinkRelatedEntries(
			null, objectEntry4);

		Assert.assertTrue(pimLinkRelatedEntries.isEmpty());
	}

	@Test
	public void testSetPrimaryPIMLink() throws Exception {
		ObjectEntry objectEntry1 = _mockObjectEntry(null, "PRODUCT-1");

		Assert.assertThrows(
			UnsupportedOperationException.class,
			() -> _pimLinkEngineImpl.setPrimaryPIMLink(objectEntry1, _TYPE));

		ObjectEntry objectEntry2 = _mockObjectEntry("CLUSTER-1", "PRODUCT-2");

		_mockValuesList(
			"clusterKey eq 'CLUSTER-1' and type eq '" + _TYPE + "'",
			Arrays.asList(
				_getValues("CLUSTER-1", 2, false, "PRODUCT-2"),
				_getValues("CLUSTER-1", 3, true, "PRODUCT-3"),
				_getValues("CLUSTER-1", 4, false, "PRODUCT-4")));

		_pimLinkEngineImpl.setPrimaryPIMLink(objectEntry2, _TYPE);

		Mockito.verify(
			_objectEntryLocalService
		).partialUpdateObjectEntry(
			Mockito.anyLong(), Mockito.eq(2L), Mockito.eq(0L),
			Mockito.eq(
				HashMapBuilder.<String, Serializable>put(
					"primary", true
				).build()),
			Mockito.any(ServiceContext.class)
		);

		Mockito.verify(
			_objectEntryLocalService
		).partialUpdateObjectEntry(
			Mockito.anyLong(), Mockito.eq(3L), Mockito.eq(0L),
			Mockito.eq(
				HashMapBuilder.<String, Serializable>put(
					"primary", false
				).build()),
			Mockito.any(ServiceContext.class)
		);

		Mockito.verify(
			_objectEntryLocalService, Mockito.never()
		).partialUpdateObjectEntry(
			Mockito.anyLong(), Mockito.eq(4L), Mockito.anyLong(),
			Mockito.anyMap(), Mockito.any(ServiceContext.class)
		);
	}

	private Map<String, Serializable> _getValues(
		String clusterKey, long id, boolean primary,
		String sourceClassExternalReferenceCode) {

		return HashMapBuilder.<String, Serializable>put(
			"clusterKey", clusterKey
		).put(
			"id", id
		).put(
			"primary", primary
		).put(
			"sourceClassExternalReferenceCode", sourceClassExternalReferenceCode
		).put(
			"sourceClassName", _CLASS_NAME
		).put(
			"type", _TYPE
		).build();
	}

	private ObjectEntry _mockObjectEntry(
			String clusterKey, String externalReferenceCode)
		throws Exception {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			objectEntry.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			objectEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			objectEntry.getModelClassName()
		).thenReturn(
			_CLASS_NAME
		);

		Mockito.when(
			objectEntry.getTitleValue()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(
				externalReferenceCode, _GROUP_ID, _OBJECT_DEFINITION_ID)
		).thenReturn(
			objectEntry
		);

		_pimLinkUtilMockedStatic.when(
			() -> PIMLinkUtil.getClusterKey(_filterFactory, objectEntry, _TYPE)
		).thenReturn(
			clusterKey
		);

		return objectEntry;
	}

	private void _mockValuesList(
		String filterString, List<Map<String, Serializable>> valuesList) {

		_pimLinkUtilMockedStatic.when(
			() -> PIMLinkUtil.getValuesList(
				_COMPANY_ID, _filterFactory, filterString, _GROUP_ID)
		).thenReturn(
			valuesList
		);
	}

	private static final String _CLASS_NAME = RandomTestUtil.randomString();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final String _TYPE = "variant";

	private final FilterFactory<Predicate> _filterFactory = Mockito.mock(
		FilterFactory.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final PIMLinkEngineImpl _pimLinkEngineImpl =
		new PIMLinkEngineImpl();
	private final PIMLinkTypeRegistry _pimLinkTypeRegistry = Mockito.mock(
		PIMLinkTypeRegistry.class);
	private final MockedStatic<PIMLinkUtil> _pimLinkUtilMockedStatic =
		Mockito.mockStatic(PIMLinkUtil.class);

}