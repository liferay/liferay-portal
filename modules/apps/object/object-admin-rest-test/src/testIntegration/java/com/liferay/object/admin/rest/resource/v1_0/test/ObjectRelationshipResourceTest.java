/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.problem.Problem;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectRelationshipResource;
import com.liferay.object.admin.rest.resource.v1_0.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 * @author Murilo Stodolni
 */
@RunWith(Arquillian.class)
public class ObjectRelationshipResourceTest
	extends BaseObjectRelationshipResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_objectDefinition1 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();
		_objectDefinition2 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_objectDefinition1 != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition1.getObjectDefinitionId());
		}

		if (_objectDefinition2 != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition2.getObjectDefinitionId());
		}
	}

	@Ignore
	@Override
	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithFilterDateTimeEquals() {
	}

	@Override
	@Test
	public void testGetObjectDefinitionObjectRelationshipsPage()
		throws Exception {

		super.testGetObjectDefinitionObjectRelationshipsPage();

		ObjectRelationship randomObjectRelationship =
			randomObjectRelationship();

		String objectRelationshipLabel1 = RandomTestUtil.randomString();
		String objectRelationshipLabel2 = RandomTestUtil.randomString();

		randomObjectRelationship.setLabel(
			HashMapBuilder.put(
				LocaleUtil.BRAZIL.toLanguageTag(), objectRelationshipLabel1
			).put(
				LocaleUtil.US.toLanguageTag(), objectRelationshipLabel2
			).build());

		ObjectRelationship objectRelationship =
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				testGetObjectDefinitionObjectRelationshipsPage_getObjectDefinitionId(),
				randomObjectRelationship);

		_testGetObjectDefinitionObjectRelationshipsPage(
			objectRelationship, LocaleUtil.BRAZIL, objectRelationshipLabel1);
		_testGetObjectDefinitionObjectRelationshipsPage(
			objectRelationship, LocaleUtil.BRAZIL,
			objectRelationship.getName());
		_testGetObjectDefinitionObjectRelationshipsPage(
			objectRelationship, LocaleUtil.US, objectRelationshipLabel2);
		_testGetObjectDefinitionObjectRelationshipsPage(
			objectRelationship, LocaleUtil.US, objectRelationship.getName());
	}

	@Ignore
	@Override
	@Test
	public void testGetObjectDefinitionObjectRelationshipsPageWithFilterDateTimeEquals() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectRelationship() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectRelationshipNotFound() {
	}

	@Override
	@Test
	public void testPostObjectDefinitionObjectRelationship() throws Exception {
		super.testPostObjectDefinitionObjectRelationship();

		ObjectRelationship randomObjectRelationship =
			randomObjectRelationship();

		randomObjectRelationship.setObjectDefinitionExternalReferenceCode2(
			RandomTestUtil.randomString());
		randomObjectRelationship.setObjectDefinitionId2(0L);
		randomObjectRelationship.setObjectDefinitionModifiable2(() -> null);
		randomObjectRelationship.setObjectDefinitionScope2(
			ObjectDefinitionConstants.SCOPE_SITE);
		randomObjectRelationship.setObjectDefinitionSystem2(() -> null);

		ObjectRelationship postObjectRelationship =
			testPostObjectDefinitionObjectRelationship_addObjectRelationship(
				randomObjectRelationship);

		Assert.assertNotNull(
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					randomObjectRelationship.
						getObjectDefinitionExternalReferenceCode2(),
					TestPropsValues.getCompanyId()));

		Assert.assertTrue(
			postObjectRelationship.getObjectDefinitionModifiable2());
		Assert.assertEquals(
			randomObjectRelationship.getObjectDefinitionScope2(),
			postObjectRelationship.getObjectDefinitionScope2());
		Assert.assertFalse(postObjectRelationship.getObjectDefinitionSystem2());

		ObjectRelationship systemObjectRelationship =
			randomObjectRelationship();

		String externalReferenceCode =
			ObjectDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_PREFIX_SYSTEM_OBJECT_DEFINITION +
					RandomTestUtil.randomString();

		systemObjectRelationship.setObjectDefinitionExternalReferenceCode2(
			externalReferenceCode);

		systemObjectRelationship.setObjectDefinitionId2(0L);
		systemObjectRelationship.setObjectDefinitionModifiable2(() -> null);
		systemObjectRelationship.setObjectDefinitionScope2(
			RandomTestUtil.randomString());
		systemObjectRelationship.setObjectDefinitionSystem2(() -> null);

		ObjectRelationship postSystemObjectRelationship =
			testPostObjectDefinitionObjectRelationship_addObjectRelationship(
				systemObjectRelationship);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId());

		Assert.assertTrue(objectDefinition.isSystem());

		Assert.assertTrue(
			postSystemObjectRelationship.getObjectDefinitionSystem2());
	}

	@Override
	@Test
	public void testPutObjectRelationshipByExternalReferenceCode()
		throws Exception {

		// TODO Modify REST builder because of LPS-201121

		ObjectRelationship postObjectRelationship =
			testPutObjectRelationshipByExternalReferenceCode_addObjectRelationship();

		ObjectRelationship randomObjectRelationship =
			randomObjectRelationship();

		ObjectRelationship putObjectRelationship =
			objectRelationshipResource.
				putObjectRelationshipByExternalReferenceCode(
					postObjectRelationship.getExternalReferenceCode(),
					randomObjectRelationship);

		assertEquals(randomObjectRelationship, putObjectRelationship);
		assertValid(putObjectRelationship);

		ObjectRelationship getObjectRelationship =
			objectRelationshipResource.getObjectRelationship(
				putObjectRelationship.getId());

		assertEquals(randomObjectRelationship, getObjectRelationship);
		assertValid(getObjectRelationship);

		ObjectRelationship newObjectRelationship =
			testPutObjectRelationshipByExternalReferenceCode_createObjectRelationship();

		putObjectRelationship =
			objectRelationshipResource.
				putObjectRelationshipByExternalReferenceCode(
					newObjectRelationship.getExternalReferenceCode(),
					newObjectRelationship);

		assertEquals(newObjectRelationship, putObjectRelationship);
		assertValid(putObjectRelationship);

		getObjectRelationship =
			objectRelationshipResource.getObjectRelationship(
				putObjectRelationship.getId());

		assertEquals(newObjectRelationship, getObjectRelationship);

		Assert.assertEquals(
			newObjectRelationship.getExternalReferenceCode(),
			putObjectRelationship.getExternalReferenceCode());

		randomObjectRelationship = randomObjectRelationship();

		randomObjectRelationship.setObjectDefinitionId1((Long)null);

		putObjectRelationship =
			objectRelationshipResource.
				putObjectRelationshipByExternalReferenceCode(
					postObjectRelationship.getExternalReferenceCode(),
					randomObjectRelationship);

		Assert.assertEquals(
			Long.valueOf(_objectDefinition1.getObjectDefinitionId()),
			putObjectRelationship.getObjectDefinitionId1());

		randomObjectRelationship.setObjectDefinitionExternalReferenceCode1(
			(String)null);

		try {
			objectRelationshipResource.
				putObjectRelationshipByExternalReferenceCode(
					postObjectRelationship.getExternalReferenceCode(),
					randomObjectRelationship);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"dateCreated", "dateModified", "description", "label", "userId"
		};
	}

	@Override
	protected ObjectRelationship randomObjectRelationship() throws Exception {
		ObjectRelationship objectRelationship =
			super.randomObjectRelationship();

		objectRelationship.setEdge(false);
		objectRelationship.setName("a" + RandomTestUtil.randomString());
		objectRelationship.setObjectDefinitionExternalReferenceCode1(
			_objectDefinition1.getExternalReferenceCode());
		objectRelationship.setObjectDefinitionExternalReferenceCode2(
			_objectDefinition2.getExternalReferenceCode());
		objectRelationship.setObjectDefinitionId1(
			_objectDefinition1.getObjectDefinitionId());
		objectRelationship.setObjectDefinitionId2(
			_objectDefinition2.getObjectDefinitionId());
		objectRelationship.setObjectDefinitionModifiable2(true);
		objectRelationship.setObjectDefinitionName2(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		objectRelationship.setObjectDefinitionSystem2(false);
		objectRelationship.setParameterObjectFieldId(0L);
		objectRelationship.setParameterObjectFieldName(StringPool.BLANK);
		objectRelationship.setReverse(false);
		objectRelationship.setType(ObjectRelationship.Type.ONE_TO_MANY);

		return objectRelationship;
	}

	@Override
	protected ObjectRelationship
			testDeleteObjectRelationship_addObjectRelationship()
		throws Exception {

		return testPostObjectDefinitionObjectRelationship_addObjectRelationship(
			randomObjectRelationship());
	}

	@Override
	protected ObjectRelationship
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				String externalReferenceCode,
				ObjectRelationship objectRelationship)
		throws Exception {

		return testPostObjectDefinitionObjectRelationship_addObjectRelationship(
			randomObjectRelationship());
	}

	@Override
	protected String
		testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExternalReferenceCode() {

		return _objectDefinition1.getExternalReferenceCode();
	}

	@Override
	protected Long
		testGetObjectDefinitionObjectRelationshipsPage_getObjectDefinitionId() {

		return _objectDefinition1.getObjectDefinitionId();
	}

	@Override
	protected ObjectRelationship
			testGetObjectRelationship_addObjectRelationship()
		throws Exception {

		return testPostObjectDefinitionObjectRelationship_addObjectRelationship(
			randomObjectRelationship());
	}

	@Override
	protected ObjectRelationship
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageObjectDefinitionObjectRelationship_addObjectRelationship(
				String externalReferenceCode,
				ObjectRelationship objectRelationship)
		throws Exception {

		return testPostObjectDefinitionObjectRelationship_addObjectRelationship(
			randomObjectRelationship());
	}

	@Override
	protected ObjectRelationship
			testGraphQLObjectRelationship_addObjectRelationship()
		throws Exception {

		return testPostObjectDefinitionObjectRelationship_addObjectRelationship(
			randomObjectRelationship());
	}

	@Override
	protected Long
		testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectRelationship_getObjectDefinitionId() {

		return _objectDefinition1.getObjectDefinitionId();
	}

	@Override
	protected Long
		testGraphQLPostObjectDefinitionObjectRelationship_getObjectDefinitionId() {

		return _objectDefinition1.getObjectDefinitionId();
	}

	@Override
	protected ObjectRelationship
			testPostObjectDefinitionByExternalReferenceCodeObjectRelationship_addObjectRelationship(
				ObjectRelationship objectRelationship)
		throws Exception {

		return objectRelationshipResource.
			postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExternalReferenceCode(),
				objectRelationship);
	}

	@Override
	protected ObjectRelationship
			testPutObjectRelationship_addObjectRelationship()
		throws Exception {

		return testPostObjectDefinitionObjectRelationship_addObjectRelationship(
			randomObjectRelationship());
	}

	@Override
	protected ObjectRelationship
			testPutObjectRelationshipByExternalReferenceCode_addObjectRelationship()
		throws Exception {

		return testPostObjectDefinitionObjectRelationship_addObjectRelationship(
			randomObjectRelationship());
	}

	private void _testGetObjectDefinitionObjectRelationshipsPage(
			ObjectRelationship expectedObjectRelationship, Locale locale,
			String search)
		throws Exception {

		User user = testVulcanCRUDItemDelegate_getUser();

		ObjectRelationshipResource objectRelationshipResource =
			ObjectRelationshipResource.builder(
			).authentication(
				user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				locale
			).build();

		Page<ObjectRelationship> page =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					testGetObjectDefinitionObjectRelationshipsPage_getObjectDefinitionId(),
					search, null, null, null);

		assertEquals(
			Collections.singletonList(expectedObjectRelationship),
			(List<ObjectRelationship>)page.getItems());
	}

	private ObjectDefinition _objectDefinition1;
	private ObjectDefinition _objectDefinition2;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}