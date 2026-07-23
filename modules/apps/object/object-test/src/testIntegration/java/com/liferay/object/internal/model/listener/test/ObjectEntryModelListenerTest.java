/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Tree;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalDateTimeUtil;

import java.io.Serializable;

import java.time.LocalDate;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
public class ObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectDefinitionTree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build());

		ObjectDefinition objectDefinitionA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_A");

		objectDefinitionA.setEnableObjectEntryVersioning(true);

		objectDefinitionA =
			_objectDefinitionLocalService.updateObjectDefinition(
				objectDefinitionA);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setModifiedDate(_getDate(-2));

		_objectEntryA = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinitionA.getObjectDefinitionId(), serviceContext,
			Collections.emptyMap());

		ObjectDefinition objectDefinitionAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AA");

		_objectRelationshipObjectFieldName =
			_getObjectRelationshipObjectFieldName(objectDefinitionAA);

		serviceContext.setModifiedDate(_getDate(-1));

		_objectEntryAA = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinitionAA.getObjectDefinitionId(), serviceContext,
			HashMapBuilder.<String, Serializable>put(
				_objectRelationshipObjectFieldName,
				_objectEntryA.getObjectEntryId()
			).build());
	}

	@After
	public void tearDown() throws Exception {
		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA"}, _objectEntryLocalService,
			_objectRelationshipLocalService);
	}

	@Test
	public void testOnAfterCreate() throws Exception {
		_assertModifiedDate(_getDate(-1));

		ObjectDefinition objectDefinitionAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAA");

		ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinitionAAA.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_getObjectRelationshipObjectFieldName(objectDefinitionAAA),
				_objectEntryAA.getObjectEntryId()
			).build());

		_assertModifiedDate(new Date());
	}

	@Test
	public void testOnAfterRemove() throws Exception {
		_objectEntryLocalService.deleteObjectEntry(_objectEntryAA);

		_assertModifiedDate(new Date());
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), _objectEntryAA.getObjectEntryId(),
			_objectEntryAA.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				_objectRelationshipObjectFieldName,
				_objectEntryA.getObjectEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertModifiedDate(new Date());
	}

	private void _assertModifiedDate(Date expectedModifiedDate)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			_objectEntryA.getObjectEntryId());

		Assert.assertEquals(
			_toLocalDate(expectedModifiedDate),
			_toLocalDate(objectEntry.getModifiedDate()));

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionLocalService.getObjectEntryVersion(
				objectEntry.getObjectEntryId(), objectEntry.getVersion());

		Assert.assertEquals(
			_toLocalDate(expectedModifiedDate),
			_toLocalDate(objectEntryVersion.getModifiedDate()));
	}

	private Date _getDate(int dayOfYear) {
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.DAY_OF_YEAR, dayOfYear);

		return calendar.getTime();
	}

	private String _getObjectRelationshipObjectFieldName(
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectRelationship objectRelationship =
			TreeTestUtil.getEdgeObjectRelationship(
				objectDefinition, _objectRelationshipLocalService,
				_objectDefinitionTree);

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		return objectField.getName();
	}

	private LocalDate _toLocalDate(Date date) {
		return LocalDateTimeUtil.toLocalDateTime(
			date
		).toLocalDate();
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private Tree _objectDefinitionTree;
	private ObjectEntry _objectEntryA;
	private ObjectEntry _objectEntryAA;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private String _objectRelationshipObjectFieldName;

}