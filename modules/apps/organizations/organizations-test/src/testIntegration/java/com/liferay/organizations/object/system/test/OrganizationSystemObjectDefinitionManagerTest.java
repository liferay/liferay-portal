/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.object.system.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.BaseSystemObjectDefinitionManagerTestCase;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

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
public class OrganizationSystemObjectDefinitionManagerTest
	extends BaseSystemObjectDefinitionManagerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testAddBaseModel() throws Exception {

		// With permissions

		User user1 = TestPropsValues.getUser();

		setUser(user1);

		String comment = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		Map<String, Object> values = HashMapBuilder.<String, Object>put(
			"comment", comment
		).put(
			"name", name
		).build();

		int organizationsCount =
			_organizationLocalService.getOrganizationsCount();

		long organizationId1 = systemObjectDefinitionManager.addBaseModel(
			true, user1, values);

		_assertCount(organizationsCount + 1);

		Organization organization1 = _organizationLocalService.getOrganization(
			organizationId1);

		Assert.assertEquals(comment, organization1.getComments());
		Assert.assertEquals(name, organization1.getName());

		comment = RandomTestUtil.randomString();
		name = RandomTestUtil.randomString();

		long organizationId2 = systemObjectDefinitionManager.addBaseModel(
			true, user1,
			HashMapBuilder.<String, Object>put(
				"comment", comment
			).put(
				"name", name
			).build());

		_assertCount(organizationsCount + 2);

		Organization organization2 = _organizationLocalService.getOrganization(
			organizationId2);

		Assert.assertEquals(comment, organization2.getComments());
		Assert.assertEquals(name, organization2.getName());

		_organizationLocalService.deleteOrganization(organizationId1);
		_organizationLocalService.deleteOrganization(organizationId2);

		// Without permissions

		User user2 = UserTestUtil.addUser();

		setUser(user2);

		AssertUtils.assertFailure(
			PortalException.class,
			StringBundler.concat(
				"User ", user2.getUserId(), " must have ", PortletKeys.PORTAL,
				", ADD_ORGANIZATION permission for null "),
			() -> systemObjectDefinitionManager.addBaseModel(
				true, user2, values));
	}

	@Test
	public void testDeleteBaseModel() throws Exception {
		int organizationsCount =
			_organizationLocalService.getOrganizationsCount();

		long organizationId = systemObjectDefinitionManager.addBaseModel(
			false, TestPropsValues.getUser(),
			HashMapBuilder.<String, Object>put(
				"comment", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).build());

		_assertCount(organizationsCount + 1);

		systemObjectDefinitionManager.deleteBaseModel(
			_organizationLocalService.getOrganization(organizationId));

		AssertUtils.assertFailure(
			NoSuchOrganizationException.class,
			"No Organization exists with the primary key " + organizationId,
			() -> _organizationLocalService.getOrganization(organizationId));

		_assertCount(organizationsCount);
	}

	@Test
	public void testGetObjectFields() throws Exception {
		List<ObjectField> objectFields =
			systemObjectDefinitionManager.getObjectFields();

		Assert.assertNotNull(objectFields);
		Assert.assertEquals(objectFields.toString(), 3, objectFields.size());

		ListIterator<ObjectField> iterator = objectFields.listIterator();

		Assert.assertTrue(iterator.hasNext());

		_assertEquals(
			new LongIntegerObjectFieldBuilder(
			).labelMap(
				_getLabelMap("parentOrganizationId")
			).name(
				"parentOrganizationId"
			).system(
				true
			).build(),
			iterator.next());

		Assert.assertTrue(iterator.hasNext());

		_assertEquals(
			new TextObjectFieldBuilder(
			).labelMap(
				_getLabelMap("comments")
			).name(
				"comment"
			).system(
				true
			).build(),
			iterator.next());

		Assert.assertTrue(iterator.hasNext());

		_assertEquals(
			new TextObjectFieldBuilder(
			).labelMap(
				_getLabelMap("name")
			).name(
				"name"
			).required(
				true
			).system(
				true
			).build(),
			iterator.next());
	}

	@Override
	@Test
	@TestInfo("LPD-62555")
	public void testGetOrAddEmptyBaseModel() throws Exception {
		super.testGetOrAddEmptyBaseModel();
	}

	@Test
	public void testGetters() throws Exception {
		Assert.assertEquals(
			"L_ORGANIZATION",
			systemObjectDefinitionManager.getExternalReferenceCode());
		Assert.assertEquals(
			_getLabelMap("organization"),
			systemObjectDefinitionManager.getLabelMap());
		Assert.assertEquals(
			_getLabelMap("organizations"),
			systemObjectDefinitionManager.getPluralLabelMap());
		Assert.assertEquals(
			ObjectDefinitionConstants.SCOPE_COMPANY,
			systemObjectDefinitionManager.getScope());
		Assert.assertEquals(
			"name", systemObjectDefinitionManager.getTitleObjectFieldName());
		Assert.assertEquals(3, systemObjectDefinitionManager.getVersion());
	}

	@Override
	protected void assertGetOrAddEmptyBaseModelWithoutPermissions(
		BaseModel<?> baseModel, User user) {

		Organization organization = (Organization)baseModel;

		AssertUtils.assertFailure(
			PortalException.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ", PortletKeys.PORTAL,
				", ADD_ORGANIZATION permission for null "),
			() -> systemObjectDefinitionManager.getOrAddEmptyBaseModel(
				RandomTestUtil.randomString(), user));

		AssertUtils.assertFailure(
			PortalException.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have VIEW permission for ",
				Organization.class.getName(), " ",
				organization.getOrganizationId()),
			() -> systemObjectDefinitionManager.getOrAddEmptyBaseModel(
				organization.getExternalReferenceCode(), user));
	}

	@Override
	protected void assertGetOrAddEmptyBaseModelWithPermissions(
		BaseModel<?> baseModel) {

		Organization organization = (Organization)baseModel;

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, organization.getStatus());
	}

	@Override
	protected String getSystemObjectDefinitionName() {
		return "Organization";
	}

	private void _assertCount(int count) throws Exception {
		Assert.assertEquals(
			count, _organizationLocalService.getOrganizationsCount());
	}

	private void _assertEquals(
		ObjectField expectedObjectField, ObjectField actualObjectField) {

		Assert.assertEquals(
			expectedObjectField.getDBColumnName(),
			actualObjectField.getDBColumnName());
		Assert.assertEquals(
			expectedObjectField.getDBTableName(),
			actualObjectField.getDBTableName());
		Assert.assertEquals(
			expectedObjectField.getDBType(), actualObjectField.getDBType());
		Assert.assertEquals(
			expectedObjectField.isIndexed(), actualObjectField.isIndexed());
		Assert.assertEquals(
			expectedObjectField.isIndexedAsKeyword(),
			actualObjectField.isIndexedAsKeyword());
		Assert.assertEquals(
			expectedObjectField.getIndexedLanguageId(),
			actualObjectField.getIndexedLanguageId());
		Assert.assertEquals(
			expectedObjectField.getLabelMap(), actualObjectField.getLabelMap());
		Assert.assertEquals(
			expectedObjectField.getName(), actualObjectField.getName());
		Assert.assertEquals(
			expectedObjectField.isRequired(), actualObjectField.isRequired());
		Assert.assertEquals(
			expectedObjectField.isState(), actualObjectField.isState());
	}

	private Map<Locale, String> _getLabelMap(String labelKey) {
		Map<Locale, String> labelMap = new HashMap<>();

		for (Locale locale : _language.getAvailableLocales()) {
			labelMap.put(locale, _language.get(locale, labelKey));
		}

		return labelMap;
	}

	@Inject
	private Language _language;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

}