/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.frontend.data.set.view.table.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewRegistry;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaField;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.builder.AggregationObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectViewColumn;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.service.persistence.ObjectViewColumnPersistence;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

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
import org.junit.runner.RunWith;

/**
 * @author Nathaly Gomes
 */
@RunWith(Arquillian.class)
public class ObjectEntriesTableFDSViewTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		_user.setTimeZoneId("Europe/Madrid");

		_user = _userLocalService.updateUser(_user);

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(_user.getUserId());
	}

	@After
	public void tearDown() {
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testGetFDSTableSchema() throws Exception {
		_objectDefinition1 = ObjectDefinitionTestUtil.publishObjectDefinition();
		_objectDefinition2 = ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinition1,
				_objectDefinition2);

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new AggregationObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				_objectDefinition1.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_FUNCTION
					).value(
						ObjectFieldSettingConstants.VALUE_COUNT
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.
							NAME_OBJECT_RELATIONSHIP_NAME
					).value(
						objectRelationship.getName()
					).build())
			).userId(
				_user.getUserId()
			).build());

		Map<String, FDSTableSchemaField> fdsTableSchemaFieldsMap =
			_getFDSTableSchemaFieldsMap(_objectDefinition1);

		Assert.assertFalse(
			fdsTableSchemaFieldsMap.containsKey("externalReferenceCode"));

		_assertFDSTableSchemaField(
			"Author", true, fdsTableSchemaFieldsMap, "creator.name");
		_assertFDSTableSchemaField("ID", true, fdsTableSchemaFieldsMap, "id");
		_assertFDSTableSchemaField(
			"Status", true, fdsTableSchemaFieldsMap, "status");
		_assertFDSTableSchemaField(
			objectField.getLabel(LocaleUtil.US), false, fdsTableSchemaFieldsMap,
			objectField.getName());

		_objectViewLocalService.addObjectView(
			null, _user.getUserId(), _objectDefinition1.getObjectDefinitionId(),
			true,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			Arrays.asList(
				_createObjectViewColumn("Create Date", "createDate"),
				_createObjectViewColumn("Modified Date", "modifiedDate")),
			Collections.emptyList(), Collections.emptyList());

		fdsTableSchemaFieldsMap = _getFDSTableSchemaFieldsMap(
			_objectDefinition1);

		_assertFDSTableSchemaField(
			"Create Date", true, fdsTableSchemaFieldsMap, "dateCreated");
		_assertFDSTableSchemaField(
			"Modified Date", true, fdsTableSchemaFieldsMap, "dateModified");
		_assertTimeZone(
			fdsTableSchemaFieldsMap, _user.getTimeZoneId(), "dateCreated");
		_assertTimeZone(
			fdsTableSchemaFieldsMap, _user.getTimeZoneId(), "dateModified");
	}

	private void _assertFDSTableSchemaField(
		String expectedLabel, boolean expectedSortable,
		Map<String, FDSTableSchemaField> fdsTableSchemaFieldsMap,
		String fieldName) {

		FDSTableSchemaField fdsTableSchemaField = fdsTableSchemaFieldsMap.get(
			fieldName);

		Assert.assertNotNull(fdsTableSchemaField);
		Assert.assertEquals(expectedLabel, fdsTableSchemaField.getLabel());
		Assert.assertEquals(expectedSortable, fdsTableSchemaField.isSortable());
	}

	private void _assertTimeZone(
		Map<String, FDSTableSchemaField> fdsTableSchemaFieldsMap,
		String expectedTimeZoneId, String fieldName) {

		FDSTableSchemaField fdsTableSchemaField = fdsTableSchemaFieldsMap.get(
			fieldName);

		Assert.assertNotNull(fdsTableSchemaField);

		JSONObject jsonObject = fdsTableSchemaField.toJSONObject();

		jsonObject = jsonObject.getJSONObject("format");

		Assert.assertEquals(
			expectedTimeZoneId, jsonObject.getString("timeZone"));
	}

	private ObjectViewColumn _createObjectViewColumn(
		String objectFieldLabel, String objectFieldName) {

		ObjectViewColumn objectViewColumn = _objectViewColumnPersistence.create(
			0);

		objectViewColumn.setLabelMap(
			LocalizedMapUtil.getLocalizedMap(objectFieldLabel));
		objectViewColumn.setObjectFieldName(objectFieldName);
		objectViewColumn.setPriority(0);

		return objectViewColumn;
	}

	private Map<String, FDSTableSchemaField> _getFDSTableSchemaFieldsMap(
		ObjectDefinition objectDefinition) {

		List<FDSView> fdsViews = _fdsViewRegistry.getFDSViews(
			objectDefinition.getPortletId());

		FDSView fdsView = fdsViews.get(0);

		FDSTableSchema fdsTableSchema = fdsView.getFDSTableSchema(
			LocaleUtil.US);

		return fdsTableSchema.getFDSTableSchemaFieldsMap();
	}

	@Inject
	private FDSViewRegistry _fdsViewRegistry;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ObjectViewColumnPersistence _objectViewColumnPersistence;

	@Inject
	private ObjectViewLocalService _objectViewLocalService;

	private String _originalName;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}