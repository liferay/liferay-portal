/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectFieldSetting;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.resource.v1_0.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
)
@RunWith(Arquillian.class)
public class ObjectFieldResourceTest extends BaseObjectFieldResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_objectDefinition = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			true);
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

	@Override
	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage()
		throws Exception {

		String objectDefinitionExternalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExternalReferenceCode();
		String irrelevantObjectDefinitionExternalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getIrrelevantExternalReferenceCode();

		Page<ObjectField> page =
			objectFieldResource.
				getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
					objectDefinitionExternalReferenceCode, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(9, page.getTotalCount());

		if (irrelevantObjectDefinitionExternalReferenceCode != null) {
			ObjectField irrelevantObjectField =
				testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
					irrelevantObjectDefinitionExternalReferenceCode,
					randomIrrelevantObjectField());

			page =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						irrelevantObjectDefinitionExternalReferenceCode, null,
						null, Pagination.of(1, 10), null);

			Assert.assertEquals(1, page.getTotalCount());

			assertEquals(
				Arrays.asList(irrelevantObjectField),
				(List<ObjectField>)page.getItems());
			assertValid(page);
		}

		ObjectField objectField1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				objectDefinitionExternalReferenceCode, randomObjectField());
		ObjectField objectField2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				objectDefinitionExternalReferenceCode, randomObjectField());

		page =
			objectFieldResource.
				getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
					objectDefinitionExternalReferenceCode, null, null,
					Pagination.of(1, 20), null);

		Assert.assertEquals(11, page.getTotalCount());

		assertContains(objectField1, (List<ObjectField>)page.getItems());
		assertContains(objectField2, (List<ObjectField>)page.getItems());
		assertValid(page);

		objectFieldResource.deleteObjectField(objectField1.getId());
		objectFieldResource.deleteObjectField(objectField2.getId());
	}

	@Override
	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithPagination()
		throws Exception {

		String objectDefinitionExternalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExternalReferenceCode();

		Page<ObjectField> totalPage =
			objectFieldResource.
				getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
					objectDefinitionExternalReferenceCode, null, null, null,
					null);

		int totalCount = GetterUtil.getInteger(totalPage.getTotalCount());

		ObjectField objectField1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				objectDefinitionExternalReferenceCode, randomObjectField());

		ObjectField objectField2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				objectDefinitionExternalReferenceCode, randomObjectField());

		ObjectField objectField3 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				objectDefinitionExternalReferenceCode, randomObjectField());

		Page<ObjectField> page1 =
			objectFieldResource.
				getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
					objectDefinitionExternalReferenceCode, null, null,
					Pagination.of(1, totalCount + 2), null);

		List<ObjectField> objectFields1 = (List<ObjectField>)page1.getItems();

		Assert.assertEquals(
			objectFields1.toString(), totalCount + 2, objectFields1.size());

		Page<ObjectField> page2 =
			objectFieldResource.
				getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
					objectDefinitionExternalReferenceCode, null, null,
					Pagination.of(2, totalCount + 2), null);

		Assert.assertEquals(totalCount + 3, page2.getTotalCount());

		List<ObjectField> objectFields2 = (List<ObjectField>)page2.getItems();

		Assert.assertEquals(objectFields2.toString(), 1, objectFields2.size());

		Page<ObjectField> page3 =
			objectFieldResource.
				getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
					objectDefinitionExternalReferenceCode, null, null,
					Pagination.of(1, totalCount + 3), null);

		assertContains(objectField1, (List<ObjectField>)page3.getItems());
		assertContains(objectField2, (List<ObjectField>)page3.getItems());
		assertContains(objectField3, (List<ObjectField>)page3.getItems());
	}

	@FeatureFlag(enable = false, value = "LPD-17564")
	@Override
	@Test
	public void testGetObjectDefinitionObjectFieldsPage() throws Exception {

		// TODO Fix REST builder

		Long objectDefinitionId =
			testGetObjectDefinitionObjectFieldsPage_getObjectDefinitionId();
		Long irrelevantObjectDefinitionId =
			testGetObjectDefinitionObjectFieldsPage_getIrrelevantObjectDefinitionId();

		Page<ObjectField> page =
			objectFieldResource.getObjectDefinitionObjectFieldsPage(
				objectDefinitionId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantObjectDefinitionId != null) {
			ObjectField irrelevantObjectField =
				testGetObjectDefinitionObjectFieldsPage_addObjectField(
					irrelevantObjectDefinitionId,
					randomIrrelevantObjectField());

			page = objectFieldResource.getObjectDefinitionObjectFieldsPage(
				irrelevantObjectDefinitionId, null, null, Pagination.of(1, 2),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectField, (List<ObjectField>)page.getItems());

			assertValid(page);
		}

		ObjectField objectField1 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		ObjectField objectField2 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		page = objectFieldResource.getObjectDefinitionObjectFieldsPage(
			objectDefinitionId, null, null, Pagination.of(1, 20), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(objectField1, (List<ObjectField>)page.getItems());
		assertContains(objectField2, (List<ObjectField>)page.getItems());
		assertValid(page);

		objectFieldResource.deleteObjectField(objectField1.getId());

		objectFieldResource.deleteObjectField(objectField2.getId());

		page = objectFieldResource.getObjectDefinitionObjectFieldsPage(
			objectDefinitionId, null, null, null, null);

		Collection<ObjectField> items = page.getItems();

		Assert.assertEquals(items.size(), page.getTotalCount());
	}

	@Override
	@Test
	public void testGetObjectDefinitionObjectFieldsPageWithPagination()
		throws Exception {

		// TODO Fix REST builder

		Long objectDefinitionId =
			testGetObjectDefinitionObjectFieldsPage_getObjectDefinitionId();

		Page<ObjectField> totalPage =
			objectFieldResource.getObjectDefinitionObjectFieldsPage(
				objectDefinitionId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(totalPage.getTotalCount());

		ObjectField objectField1 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		ObjectField objectField2 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		ObjectField objectField3 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		Page<ObjectField> page1 =
			objectFieldResource.getObjectDefinitionObjectFieldsPage(
				objectDefinitionId, null, null,
				Pagination.of(1, totalCount + 2), null);

		List<ObjectField> objectFields1 = (List<ObjectField>)page1.getItems();

		Assert.assertEquals(
			objectFields1.toString(), totalCount + 2, objectFields1.size());

		Page<ObjectField> page2 =
			objectFieldResource.getObjectDefinitionObjectFieldsPage(
				objectDefinitionId, null, null,
				Pagination.of(2, totalCount + 2), null);

		Assert.assertEquals(totalCount + 3, page2.getTotalCount());

		List<ObjectField> objectFields2 = (List<ObjectField>)page2.getItems();

		Assert.assertEquals(objectFields2.toString(), 1, objectFields2.size());

		Page<ObjectField> page3 =
			objectFieldResource.getObjectDefinitionObjectFieldsPage(
				objectDefinitionId, null, null,
				Pagination.of(1, totalCount + 3), null);

		assertContains(objectField1, (List<ObjectField>)page3.getItems());
		assertContains(objectField2, (List<ObjectField>)page3.getItems());
		assertContains(objectField3, (List<ObjectField>)page3.getItems());
	}

	@Override
	@Test
	public void testGetObjectField() throws Exception {
		super.testGetObjectField();

		// Unique

		ObjectField objectField = _addUniqueObjectField();

		Assert.assertTrue(objectField.getUnique());

		Page<ObjectField> page1 =
			objectFieldResource.getObjectDefinitionObjectFieldsPage(
				_objectDefinition.getObjectDefinitionId(), null,
				"unique eq true", Pagination.of(1, 2), null);

		assertEquals(
			Collections.singletonList(objectField),
			(List<ObjectField>)page1.getItems());

		Page<ObjectField> page2 =
			objectFieldResource.getObjectDefinitionObjectFieldsPage(
				_objectDefinition.getObjectDefinitionId(), null,
				"unique eq false", Pagination.of(1, 2), null);

		Assert.assertFalse(
			ListUtil.exists(
				(List<ObjectField>)page2.getItems(), ObjectField::getUnique));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectDefinitionObjectFieldsPage() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectFieldNotFound() {
	}

	@Override
	@Test
	public void testPatchObjectField() throws Exception {
		super.testPatchObjectField();

		_testPatchObjectField(_addObjectField(), true);
		_testPatchObjectField(_addUniqueObjectField(), true);
		_testPatchObjectField(_addUniqueObjectField(), false);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"label", "state"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"label"};
	}

	@Override
	protected ObjectField randomObjectField() throws Exception {
		ObjectField objectField = super.randomObjectField();

		objectField.setBusinessType(ObjectField.BusinessType.create("Text"));
		objectField.setDBType(ObjectField.DBType.create("String"));
		objectField.setDefaultValue(StringPool.BLANK);
		objectField.setIndexedAsKeyword(false);
		objectField.setLabel(
			Collections.singletonMap(
				LocaleUtil.US.toString(), "a" + objectField.getName()));
		objectField.setName("a" + objectField.getName());
		objectField.setReadOnly(ObjectField.ReadOnly.FALSE);
		objectField.setRequired(
			!objectField.getLocalized() && objectField.getRequired());
		objectField.setState(false);

		return objectField;
	}

	@Override
	protected ObjectField testDeleteObjectField_addObjectField()
		throws Exception {

		return _addObjectField();
	}

	@Override
	protected ObjectField
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				String objectDefinitionExternalReferenceCode,
				ObjectField objectField)
		throws Exception {

		return objectFieldResource.
			postObjectDefinitionByExternalReferenceCodeObjectField(
				objectDefinitionExternalReferenceCode, objectField);
	}

	@Override
	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExternalReferenceCode()
		throws Exception {

		return _objectDefinition.getExternalReferenceCode();
	}

	@Override
	protected Long
		testGetObjectDefinitionObjectFieldsPage_getObjectDefinitionId() {

		return _objectDefinition.getObjectDefinitionId();
	}

	@Override
	protected ObjectField testGetObjectField_addObjectField() throws Exception {
		return _addObjectField();
	}

	@Override
	protected ObjectField
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageObjectDefinitionObjectField_addObjectField(
				String objectDefinitionExternalReferenceCode,
				ObjectField objectField)
		throws Exception {

		return objectFieldResource.
			postObjectDefinitionByExternalReferenceCodeObjectField(
				objectDefinitionExternalReferenceCode, objectField);
	}

	@Override
	protected ObjectField testGraphQLObjectField_addObjectField()
		throws Exception {

		return _addObjectField();
	}

	@Override
	protected Long
		testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectField_getObjectDefinitionId() {

		return _objectDefinition.getObjectDefinitionId();
	}

	@Override
	protected Long
		testGraphQLPostObjectDefinitionObjectField_getObjectDefinitionId() {

		return _objectDefinition.getObjectDefinitionId();
	}

	@Override
	protected ObjectField testPatchObjectField_addObjectField()
		throws Exception {

		return _addObjectField();
	}

	@Override
	protected ObjectField
			testPostObjectDefinitionByExternalReferenceCodeObjectField_addObjectField(
				ObjectField objectField)
		throws Exception {

		return objectFieldResource.
			postObjectDefinitionByExternalReferenceCodeObjectField(
				_objectDefinition.getExternalReferenceCode(), objectField);
	}

	@Override
	protected ObjectField testPutObjectField_addObjectField() throws Exception {
		return _addObjectField();
	}

	private ObjectField _addObjectField() throws Exception {
		_objectField = objectFieldResource.postObjectDefinitionObjectField(
			_objectDefinition.getObjectDefinitionId(), randomObjectField());

		return _objectField;
	}

	private ObjectField _addUniqueObjectField() throws Exception {
		ObjectField objectField = randomObjectField();

		_setUnique(objectField, true);

		return objectFieldResource.postObjectDefinitionObjectField(
			_objectDefinition.getObjectDefinitionId(), objectField);
	}

	private void _setUnique(ObjectField objectField, boolean unique) {
		objectField.setObjectFieldSettings(
			new ObjectFieldSetting[] {
				new ObjectFieldSetting() {
					{
						name = ObjectFieldSettingConstants.NAME_UNIQUE_VALUES;
						value = String.valueOf(unique);
					}
				}
			});
	}

	private void _testPatchObjectField(ObjectField objectField, boolean unique)
		throws Exception {

		ObjectField randomObjectField = randomObjectField();

		_setUnique(randomObjectField, unique);

		ObjectField patchObjectField = objectFieldResource.patchObjectField(
			objectField.getId(), randomObjectField);

		assertEquals(randomObjectField, patchObjectField);

		Assert.assertEquals(unique, patchObjectField.getUnique());
	}

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectField _objectField;

}