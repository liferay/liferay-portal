/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayout;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutBox;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutColumn;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutRow;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutTab;
import com.liferay.object.admin.rest.resource.v1_0.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class ObjectLayoutResourceTest extends BaseObjectLayoutResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Able")
			).name(
				"able"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).required(
				true
			).build());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_objectDefinition != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition.getObjectDefinitionId());
		}
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectLayout() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectLayoutNotFound() {
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"label"};
	}

	@Override
	protected ObjectLayout randomObjectLayout() throws Exception {
		ObjectLayout objectLayout = super.randomObjectLayout();

		objectLayout.setDefaultObjectLayout(false);
		objectLayout.setName(
			Collections.singletonMap("en_US", RandomTestUtil.randomString()));
		objectLayout.setObjectDefinitionExternalReferenceCode(
			_objectDefinition.getExternalReferenceCode());
		objectLayout.setObjectDefinitionId(
			_objectDefinition.getObjectDefinitionId());
		objectLayout.setObjectLayoutTabs(
			new ObjectLayoutTab[] {_randomObjectLayoutTab()});

		return objectLayout;
	}

	@Override
	protected ObjectLayout testDeleteObjectLayout_addObjectLayout()
		throws Exception {

		return objectLayoutResource.postObjectDefinitionObjectLayout(
			_objectDefinition.getObjectDefinitionId(), randomObjectLayout());
	}

	@Override
	protected ObjectLayout
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_addObjectLayout(
				String objectDefinitionExternalReferenceCode,
				ObjectLayout objectLayout)
		throws Exception {

		return objectLayoutResource.
			postObjectDefinitionByExternalReferenceCodeObjectLayout(
				objectDefinitionExternalReferenceCode, objectLayout);
	}

	@Override
	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_getExternalReferenceCode()
		throws Exception {

		return _objectDefinition.getExternalReferenceCode();
	}

	@Override
	protected Long
			testGetObjectDefinitionObjectLayoutsPage_getObjectDefinitionId()
		throws Exception {

		return _objectDefinition.getObjectDefinitionId();
	}

	@Override
	protected ObjectLayout testGetObjectLayout_addObjectLayout()
		throws Exception {

		return objectLayoutResource.postObjectDefinitionObjectLayout(
			_objectDefinition.getObjectDefinitionId(), randomObjectLayout());
	}

	@Override
	protected ObjectLayout
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageObjectDefinitionObjectLayout_addObjectLayout(
				String objectDefinitionExternalReferenceCode,
				ObjectLayout objectLayout)
		throws Exception {

		return objectLayoutResource.
			postObjectDefinitionByExternalReferenceCodeObjectLayout(
				objectDefinitionExternalReferenceCode, objectLayout);
	}

	@Override
	protected ObjectLayout testGraphQLObjectLayout_addObjectLayout()
		throws Exception {

		return objectLayoutResource.postObjectDefinitionObjectLayout(
			_objectDefinition.getObjectDefinitionId(), randomObjectLayout());
	}

	@Override
	protected Long
			testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectLayout_getObjectDefinitionId(
				ObjectLayout objectLayout)
		throws Exception {

		return objectLayout.getObjectDefinitionId();
	}

	@Override
	protected Long
			testGraphQLPostObjectDefinitionObjectLayout_getObjectDefinitionId(
				ObjectLayout objectLayout)
		throws Exception {

		return objectLayout.getObjectDefinitionId();
	}

	@Override
	protected ObjectLayout
			testPostObjectDefinitionByExternalReferenceCodeObjectLayout_addObjectLayout(
				ObjectLayout objectLayout)
		throws Exception {

		return objectLayoutResource.
			postObjectDefinitionByExternalReferenceCodeObjectLayout(
				_objectDefinition.getExternalReferenceCode(), objectLayout);
	}

	@Override
	protected ObjectLayout testPutObjectLayout_addObjectLayout()
		throws Exception {

		return objectLayoutResource.postObjectDefinitionObjectLayout(
			_objectDefinition.getObjectDefinitionId(), randomObjectLayout());
	}

	private ObjectLayoutBox _randomObjectLayoutBox() {
		return new ObjectLayoutBox() {
			{
				collapsable = RandomTestUtil.randomBoolean();
				name = Collections.singletonMap(
					"en_US", RandomTestUtil.randomString());
				objectLayoutRows = new ObjectLayoutRow[] {
					_randomObjectLayoutRow()
				};
				priority = RandomTestUtil.randomInt();
				type = Type.REGULAR;
			}
		};
	}

	private ObjectLayoutColumn _randomObjectLayoutColumn() {
		return new ObjectLayoutColumn() {
			{
				objectFieldName = _objectField.getName();
				priority = RandomTestUtil.randomInt();
				size = RandomTestUtil.randomInt(1, 12);
			}
		};
	}

	private ObjectLayoutRow _randomObjectLayoutRow() {
		return new ObjectLayoutRow() {
			{
				objectLayoutColumns = new ObjectLayoutColumn[] {
					_randomObjectLayoutColumn()
				};
				priority = RandomTestUtil.randomInt();
			}
		};
	}

	private ObjectLayoutTab _randomObjectLayoutTab() {
		return new ObjectLayoutTab() {
			{
				name = Collections.singletonMap(
					"en_US", RandomTestUtil.randomString());
				objectLayoutBoxes = new ObjectLayoutBox[] {
					_randomObjectLayoutBox()
				};
				objectRelationshipId = 0L;
				priority = RandomTestUtil.randomInt();
			}
		};
	}

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectField _objectField;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

}