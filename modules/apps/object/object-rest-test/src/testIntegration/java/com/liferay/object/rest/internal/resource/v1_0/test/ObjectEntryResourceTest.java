/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.dto.v1_0.Comment;
import com.liferay.headless.delivery.dto.v1_0.Creator;
import com.liferay.map.geocoder.GeocoderResult;
import com.liferay.map.test.util.GeocoderTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.field.builder.AssigneeObjectFieldBuilder;
import com.liferay.object.field.builder.LocationObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.SystemProperties;
import com.liferay.object.rest.dto.v1_0.Version;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.WorkflowableComment;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.ws.rs.core.HttpHeaders;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.lang.reflect.Method;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceTest extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-43996")
	@Test
	public void testDeleteObjectEntryWithComments() throws Exception {

		// Company scope

		_testDeleteObjectEntryWithComments(0, getObjectDefinition1());

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testDeleteObjectEntryWithComments(
				testGroupId, getSiteScopedObjectDefinition1());
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@Test
	public void testGetCreatorExternalReferenceCode() throws Exception {
		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, null, true, false,
				true, false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
						ObjectFieldConstants.DB_TYPE_BOOLEAN, true, false, null,
						"Test Field", "testField", true)),
				Collections.emptyList(), new ServiceContext());

		objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		User user = UserTestUtil.addUser(group.getGroupId());

		ObjectEntry serviceBuilderObjectEntry =
			objectEntryLocalService.addObjectEntry(
				0, user.getUserId(), objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"testField", true
				).build(),
				ServiceContextTestUtil.getServiceContext());

		long objectEntryId = serviceBuilderObjectEntry.getObjectEntryId();

		ObjectEntryResource objectEntryResource = getObjectEntryResource(
			objectDefinition, TestPropsValues.getUser());

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			objectEntryResource.getObjectEntry(objectEntryId);

		Creator creator = objectEntry.getCreator();

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			creator.getExternalReferenceCode());

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testGetObjectEntriesPageWithComments() throws Exception {

		// Company scope

		_testGetObjectEntriesPageWithComments(0, getObjectDefinition1());

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testGetObjectEntriesPageWithComments(
				testGroupId, getSiteScopedObjectDefinition1());
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@Test
	public void testGetObjectEntriesPageWithDeletedObjectField()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						OBJECT_FIELD_NAME_1, OBJECT_FIELD_NAME_1)));

		ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, "able", RandomTestUtil.randomString());

		HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition.getRESTContextPath(), Http.Method.GET);

		objectFieldLocalService.deleteObjectField(
			objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), OBJECT_FIELD_NAME_1));

		ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, "able", RandomTestUtil.randomString());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition.getRESTContextPath(), Http.Method.GET);

		Assert.assertEquals(
			jsonObject.toString(), 2, jsonObject.getInt("totalCount"));

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testGetObjectEntriesPageWithLocalizedObjectField()
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition4().getRESTContextPath() + "/?search=textEng",
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition4().getRESTContextPath() + "/?search=textEng",
			HashMapBuilder.put(
				"Accept-Language", "es-ES"
			).build(),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition4().getRESTContextPath() + "/?search=textEsp",
			HashMapBuilder.put(
				"Accept-Language", "es-ES"
			).build(),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());
	}

	@Test
	public void testGetObjectEntriesVersionsPage() throws Exception {
		getObjectDefinition1().setEnableObjectEntryVersioning(true);

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition1());

		getObjectDefinition2().setEnableObjectEntryVersioning(true);

		objectDefinition2 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition2());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectEntryResource objectEntryResource1 = getObjectEntryResource(
			getObjectDefinition1(), TestPropsValues.getUser());

		Page<com.liferay.object.rest.dto.v1_0.ObjectEntry> objectEntryPage =
			objectEntryResource1.getByExternalReferenceCodeVersionsPage(
				objectEntry.getExternalReferenceCode(),
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null);

		Assert.assertEquals(2, objectEntryPage.getTotalCount());

		objectEntryPage =
			objectEntryResource1.getByExternalReferenceCodeVersionsPage(
				objectEntry.getExternalReferenceCode(),
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				new Sort[] {new Sort("version", Sort.INT_TYPE, true)});

		List<com.liferay.object.rest.dto.v1_0.ObjectEntry> objectEntries =
			new ArrayList<>(objectEntryPage.getItems());

		_assertObjectEntryVersionNumber(objectEntries.get(0), 2);
		_assertObjectEntryVersionNumber(objectEntries.get(1), 1);

		ObjectEntryResource objectEntryResource2 = getObjectEntryResource(
			getObjectDefinition2(), TestPropsValues.getUser());

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			StringBundler.concat(
				"No ObjectEntry exists with the key {externalReferenceCode=",
				objectEntry.getExternalReferenceCode(), ", groupId=",
				objectEntry.getGroupId(), ", companyId=",
				objectEntry.getCompanyId(), ", objectDefinitionId=",
				getObjectDefinition2().getObjectDefinitionId(), "}"),
			() -> objectEntryResource2.getByExternalReferenceCodeVersionsPage(
				objectEntry.getExternalReferenceCode(),
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null));
	}

	@Test
	public void testGetObjectEntriesWithAssigneeObjectField() throws Exception {
		ObjectFieldUtil.addCustomObjectField(
			new AssigneeObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				"assignee"
			).objectDefinitionId(
				getObjectDefinition1().getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		User user1 = UserTestUtil.addUser();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode",
					() -> {
						Role role = roleLocalService.getRole(
							TestPropsValues.getCompanyId(),
							AccountRoleConstants.
								REQUIRED_ROLE_NAME_ACCOUNT_MANAGER);

						Organization organization =
							OrganizationTestUtil.addOrganization();

						_organizationLocalService.addUserOrganization(
							user1.getUserId(),
							organization.getOrganizationId());

						Group group = groupLocalService.getOrganizationGroup(
							TestPropsValues.getCompanyId(),
							organization.getOrganizationId());

						userGroupRoleLocalService.addUserGroupRole(
							user1.getUserId(), group.getGroupId(),
							role.getRoleId());

						return role.getExternalReferenceCode();
					}
				).put(
					"type", "Role"
				).build()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode", user1.getExternalReferenceCode()
				).put(
					"type", "User"
				).build()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		User user2 = UserTestUtil.addUser();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode",
					() -> {
						Role role = RoleTestUtil.addRole(
							"Regular Role", RoleConstants.TYPE_REGULAR);

						userLocalService.addRoleUser(role.getRoleId(), user2);

						return role.getExternalReferenceCode();
					}
				).put(
					"type", "Role"
				).build()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode", user2.getExternalReferenceCode()
				).put(
					"type", "User"
				).build()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"?assigneeUserExternalReferenceCode=",
				user1.getExternalReferenceCode(), "&sort=",
				URLCodec.encodeURL("id:asc")),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(2, itemsJSONArray.length());

		Assert.assertEquals(
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER,
			JSONUtil.getValue(
				itemsJSONArray.getJSONObject(0), "JSONObject/assignee",
				"Object/name"));
		Assert.assertEquals(
			user1.getFullName(),
			JSONUtil.getValue(
				itemsJSONArray.getJSONObject(1), "JSONObject/assignee",
				"Object/name"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"?assigneeUserExternalReferenceCode=",
				user2.getExternalReferenceCode(), "&sort=",
				URLCodec.encodeURL("id:asc")),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(2, itemsJSONArray.length());

		Assert.assertEquals(
			"Regular Role",
			JSONUtil.getValue(
				itemsJSONArray.getJSONObject(0), "JSONObject/assignee",
				"Object/name"));
		Assert.assertEquals(
			user2.getFullName(),
			JSONUtil.getValue(
				itemsJSONArray.getJSONObject(1), "JSONObject/assignee",
				"Object/name"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() +
				"?assigneeUserExternalReferenceCode=" +
					RandomTestUtil.randomString(),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());
	}

	@Test
	public void testGetObjectEntriesWithPagination() throws Exception {
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);

		_assertPagination(7, getObjectDefinition1());

		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);

		_assertPagination(9, getObjectDefinition1());

		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);

		_assertPagination(11, getObjectDefinition1());
	}

	@Test
	public void testGetObjectEntryByExternalReferenceCodeWithSlash()
		throws Exception {

		ObjectField textObjectField = objectFieldLocalService.getObjectField(
			getObjectDefinition1().getObjectDefinitionId(),
			OBJECT_FIELD_NAME_1);

		getObjectDefinition1().setTitleObjectFieldId(
			textObjectField.getObjectFieldId());

		objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition1());

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a/",
				Http.Method.GET));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a/"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%252F",
				Http.Method.GET));
		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%252F/",
				Http.Method.GET));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "/a"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/%252Fa",
				Http.Method.GET));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a/b/c"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		try (LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					getObjectDefinition1().getRESTContextPath() +
						"/by-external-reference-code/a/b/c",
					Http.Method.GET));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%252Fb%252Fc",
				Http.Method.GET));
		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%252Fb%252Fc/",
				Http.Method.GET));
		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%2Fb%2Fc",
				Http.Method.GET));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a//b"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%252F%252Fb",
				Http.Method.GET));
	}

	@Test
	public void testGetObjectEntryTranslation() throws Exception {
		_testGetObjectEntryTranslation(
			StringBundler.concat(
				getObjectDefinition4().getRESTContextPath(), StringPool.SLASH,
				getObjectEntry5().getObjectEntryId(),
				"/translations?sourceLanguageId=en_US",
				"&targetLanguageIds=es_ES"),
			null);
	}

	@Test
	public void testGetObjectEntryTranslationLanguage() throws Exception {
		_testGetObjectEntryTranslation(
			StringBundler.concat(
				getObjectDefinition4().getRESTContextPath(), StringPool.SLASH,
				getObjectEntry5().getObjectEntryId(),
				"/translations/en_US?targetLanguageId=es_ES"),
			HashMapBuilder.put(
				HttpHeaders.ACCEPT, "application/xliff+xml"
			).build());
	}

	@Test
	public void testGetObjectEntryWithFriendlyURL() throws Exception {
		getObjectDefinition1().setEnableFriendlyURLCustomization(true);

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition1());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomInt()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.GET);

		Assert.assertEquals(
			jsonObject.getString("externalReferenceCode"),
			jsonObject.getString("friendlyUrlPath"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"friendlyUrlPath", StringPool.SLASH
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.GET);

		Assert.assertEquals(
			jsonObject.getString("externalReferenceCode"),
			jsonObject.getString("friendlyUrlPath"));

		_assertGetObjectEntryWithFriendlyURL("hello-world", "/hello-world");
		_assertGetObjectEntryWithFriendlyURL("parent/child", "parent//child");
		_assertGetObjectEntryWithFriendlyURL("test-url", "Test URL");
	}

	@Test
	public void testGetObjectEntryWithLocalizedObjectField() throws Exception {

		// "Accept-Language" header

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT, "longTextEsp"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT, "<p>richTextEsp</p>"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT + "RawText", "richTextEsp"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_TEXT, "textEsp"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				getObjectDefinition4().getRESTContextPath() + StringPool.SLASH +
					getObjectEntry5().getObjectEntryId(),
				HashMapBuilder.put(
					"Accept-Language", "es-ES"
				).build(),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		// Empty "Accept-Language" header

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT, "longTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT, "<p>richTextEng</p>"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT + "RawText", "richTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_TEXT, "textEng"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				getObjectDefinition4().getRESTContextPath() + StringPool.SLASH +
					getObjectEntry5().getObjectEntryId(),
				HashMapBuilder.put(
					"Accept-Language", ""
				).build(),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		// Nonexistent "Accept-Language" header

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT, "longTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT, "<p>richTextEng</p>"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT + "RawText", "richTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_TEXT, "textEng"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				getObjectDefinition4().getRESTContextPath() + StringPool.SLASH +
					getObjectEntry5().getObjectEntryId(),
				HashMapBuilder.put(
					"Accept-Language", "de-DE"
				).build(),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		// Without "Accept-Language" header

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT, "longTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT, "<p>richTextEng</p>"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT + "RawText", "richTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_TEXT, "textEng"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				getObjectDefinition4().getRESTContextPath() + StringPool.SLASH +
					getObjectEntry5().getObjectEntryId(),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPatchByExternalReferenceCodeCustomObjectEntry()
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		assertPatchCustomObjectEntry(
			jsonObject ->
				objectDefinition1.getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			objectDefinition1, getObjectDefinition2(), testGroupId);
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPatchCustomObjectEntry() throws Exception {
		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		assertPatchCustomObjectEntry(
			jsonObject ->
				objectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			objectDefinition1, getObjectDefinition2(), testGroupId);

		ObjectDefinition siteScopedObjectDefinition1 =
			getSiteScopedObjectDefinition1();

		assertPatchCustomObjectEntry(
			jsonObject ->
				siteScopedObjectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			siteScopedObjectDefinition1, getSiteScopedObjectDefinition2(),
			testGroupId);
	}

	@Test
	public void testPatchObjectEntryWithFriendlyURLCustomizationEnabled()
		throws Exception {

		// With friendly URL

		getObjectDefinition1().setEnableFriendlyURLCustomization(true);

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition1());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"friendlyUrlPath_i18n",
				HashMapBuilder.put(
					"en_US", "Test URL"
				).build()
			).toString(),
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.PATCH);

		Assert.assertEquals(
			"test-url", jsonObject.getString("friendlyUrlPath"));

		// Without friendly URL

		String friendlyURL = StringUtil.toLowerCase(
			RandomTestUtil.randomString());
		String objectFieldValue = RandomTestUtil.randomString();

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, objectFieldValue
			).put(
				"friendlyUrlPath_i18n",
				HashMapBuilder.put(
					"en_US", friendlyURL
				).build()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, objectFieldValue
			).toString(),
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.PATCH);

		Assert.assertEquals(
			friendlyURL, jsonObject.getString("friendlyUrlPath"));
	}

	@Test
	public void testPatchPutCustomObjectEntryByExternalReferenceCodeWithAssigneeObjectField()
		throws Exception {

		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PATCH, getObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PUT, getObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PATCH, getSiteScopedObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PUT, getSiteScopedObjectDefinition1(), true);
	}

	@Test
	public void testPatchPutCustomObjectEntryExternalReferenceCode()
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", ERC_VALUE_1
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		_testPatchPutCustomObjectEntryExternalReferenceCode(
			Http.Method.PATCH, jsonObject.getLong("id"));
		_testPatchPutCustomObjectEntryExternalReferenceCode(
			Http.Method.PUT, jsonObject.getLong("id"));
	}

	@Test
	public void testPatchPutCustomObjectEntryWithAssigneeObjectField()
		throws Exception {

		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PATCH, getObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PUT, getObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PATCH, getSiteScopedObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PUT, getSiteScopedObjectDefinition1(), false);
	}

	@Test
	public void testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode()
		throws Exception {

		_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
			Http.Method.PATCH, getObjectDefinition1(),
			getSiteScopedObjectDefinition1());
		_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
			Http.Method.PUT, getObjectDefinition2(),
			getSiteScopedObjectDefinition2());
	}

	@Test
	public void testPatchPutCustomObjectEntryWithScheduleDates()
		throws Exception {

		ObjectDefinition objectDefinition = publishLocalizedObjectDefinition(
			OBJECT_FIELD_NAME_1);

		Date displayDate = new Date();
		Date expirationDate = new Date(System.currentTimeMillis() + Time.HOUR);
		Date reviewDate = new Date();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"displayDate", displayDate
			).put(
				"expirationDate", expirationDate
			).put(
				"reviewDate", reviewDate
			).build());

		String endpoint =
			objectDefinition.getRESTContextPath() +
				"/by-external-reference-code/" +
					objectEntry.getExternalReferenceCode();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).toString(),
			endpoint, Http.Method.PATCH);

		Assert.assertEquals(
			_toDateString(displayDate), jsonObject.get("displayDate"));
		Assert.assertEquals(
			_toDateString(expirationDate), jsonObject.get("expirationDate"));
		Assert.assertEquals(
			_toDateString(reviewDate), jsonObject.get("reviewDate"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).toString(),
			endpoint, Http.Method.PUT);

		Assert.assertNull(jsonObject.get("displayDate"));
		Assert.assertNull(jsonObject.get("expirationDate"));
		Assert.assertNull(jsonObject.get("reviewDate"));
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPatchPutObjectEntryWithComments() throws Exception {

		// Company scope

		_enableComments(getObjectDefinition1());

		_testPatchPutObjectEntryWithComments(
			0, Http.Method.PATCH, getObjectDefinition1());
		_testPatchPutObjectEntryWithComments(
			0, Http.Method.PUT, getObjectDefinition1());

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_enableComments(getSiteScopedObjectDefinition1());

			_testPatchPutObjectEntryWithComments(
				testGroupId, Http.Method.PATCH,
				getSiteScopedObjectDefinition1());
			_testPatchPutObjectEntryWithComments(
				testGroupId, Http.Method.PUT, getSiteScopedObjectDefinition1());
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPatchPutObjectEntryWithMissingParentCommentReference()
		throws Exception {

		_enableComments(getObjectDefinition1());

		_testPatchPutObjectEntryWithMissingParentCommentReference(
			Http.Method.PATCH);
		_testPatchPutObjectEntryWithMissingParentCommentReference(
			Http.Method.PUT);
	}

	@Test
	public void testPostByExternalReferenceCodeExpire() throws Exception {

		// Company scope

		ObjectEntry companyObjectEntry = objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			getObjectDefinition1().getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, companyObjectEntry.getStatus());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getEndpoint(getObjectDefinition1(), 0),
				"/by-external-reference-code/",
				companyObjectEntry.getExternalReferenceCode(), "/expire"),
			Http.Method.POST);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		// Site scope

		Group group = groupLocalService.fetchGroup(testGroupId);

		ObjectEntry siteObjectEntry = objectEntryLocalService.addObjectEntry(
			group.getGroupId(), TestPropsValues.getUserId(),
			getSiteScopedObjectDefinition1().getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, siteObjectEntry.getStatus());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getEndpoint(
					getSiteScopedObjectDefinition1(), group.getGroupId()),
				"/by-external-reference-code/",
				siteObjectEntry.getExternalReferenceCode(), "/expire"),
			Http.Method.POST);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));
	}

	@Test
	public void testPostCustomObjectEntryWithAssigneeObjectField()
		throws Exception {

		_testPostCustomObjectEntryWithAssigneeObjectField(
			getObjectDefinition1());
		_testPostCustomObjectEntryWithAssigneeObjectField(
			getSiteScopedObjectDefinition1());
	}

	@Test
	public void testPostCustomObjectEntryWithAutoIncrementObjectField()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT,
						ObjectFieldConstants.DB_TYPE_STRING, "autoIncrement",
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_INITIAL_VALUE
							).value(
								"10"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_SUFFIX
							).value(
								"-private"
							).build()))),
				false);

		String endpoint = getEndpoint(objectDefinition, testGroupId);

		HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint, Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"autoIncrement", "100-private"
			).toString(),
			endpoint, Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"autoIncrement", "90-private"
			).toString(),
			endpoint, Http.Method.POST);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, "?sort=", URLCodec.encodeURL("id:asc")),
			Http.Method.GET);

		assertItem(0, jsonObject, "autoIncrement", "10-private");
		assertItem(1, jsonObject, "autoIncrement", "100-private");
		assertItem(2, jsonObject, "autoIncrement", "90-private");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, "?sort=", URLCodec.encodeURL("autoIncrement:asc")),
			Http.Method.GET);

		assertItem(0, jsonObject, "autoIncrement", "10-private");
		assertItem(1, jsonObject, "autoIncrement", "90-private");
		assertItem(2, jsonObject, "autoIncrement", "100-private");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, "?sort=", URLCodec.encodeURL("autoIncrement:desc")),
			Http.Method.GET);

		assertItem(0, jsonObject, "autoIncrement", "100-private");
		assertItem(1, jsonObject, "autoIncrement", "90-private");
		assertItem(2, jsonObject, "autoIncrement", "10-private");

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testPostCustomObjectEntryWithDateTimeObjectField()
		throws Exception {

		User user = GuestOrUserUtil.getGuestOrUser();

		String timeZoneId = user.getTimeZoneId();

		try {
			String testTimeZoneId = "Europe/Madrid";

			user.setTimeZoneId(testTimeZoneId);

			user = userLocalService.updateUser(user);

			// Format: "EEE MMM dd HH:mm:ss zzz yyyy"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"Thu Jul 27 11:00:00 CET 2000"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"Thu Jul 27 11:00:00 CET 2000"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "dd-MMM-yyyy hh:mm:ss.SSS a"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"27-Jul-2000 11:00:00.000 AM"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"27-Jul-2000 11:00:00.000 AM"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd HH:mm"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27 11:00"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27 11:00"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd HH:mm:ss.S"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27 11:00:00.000"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27 11:00:00.000"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd HH:mm:ss.SSS"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27 11:00:00.000"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27 11:00:00.000"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd'T'HH:mm:ss'Z'"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T11:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27T11:00:00Z"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T11:00:00Z"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd'T'HH:mm:ss.SSS"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27T11:00:00.000"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27T11:00:00.000"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T11:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27T11:00:00.000Z"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27T11:00:00.000Z"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ" and negative offset

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T15:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27T11:00:00.000-0400"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27T11:00:00.000-0400"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ" and positive offset

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T07:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27T11:00:00.000+0400"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27T11:00:00.000+0400"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);
		}
		finally {
			user.setTimeZoneId(timeZoneId);

			userLocalService.updateUser(user);
		}
	}

	@Test
	public void testPostCustomObjectEntryWithDuplicateExternalReferenceCode()
		throws Exception {

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST));

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST));
	}

	@FeatureFlag("LPD-11388")
	@Test
	public void testPostCustomObjectEntryWithLocalizedLocationObjectField()
		throws Exception {

		GeocoderResult geocoderResult = new GeocoderResult(
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
			RandomTestUtil.randomDouble());

		try (SafeCloseable safeCloseable =
				GeocoderTestUtil.swapWithSafeCloseable(
					geocoderResult,
					objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_LOCATION))) {

			ObjectDefinition objectDefinition =
				ObjectDefinitionTestUtil.publishObjectDefinition(
					Collections.singletonList(
						new LocationObjectFieldBuilder(
						).labelMap(
							RandomTestUtil.randomLocaleStringMap()
						).localized(
							true
						).name(
							"location"
						).build()));

			String address = RandomTestUtil.randomString();
			double latitude = RandomTestUtil.randomDouble();
			double longitude = RandomTestUtil.randomDouble();

			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"location_i18n",
					JSONUtil.put(
						"en_US", JSONUtil.put("address", address)
					).put(
						"pt_BR",
						JSONUtil.put(
							"coordinates",
							JSONUtil.put(
								"latitude", latitude
							).put(
								"longitude", longitude
							))
					)
				).toString(),
				objectDefinition.getRESTContextPath(), Http.Method.POST);

			JSONObject i18nJSONObject = jsonObject.getJSONObject(
				"location_i18n");

			assertLocationJSONObject(
				address, geocoderResult.getLatitude(),
				geocoderResult.getLongitude(),
				i18nJSONObject.getJSONObject("en_US"));
			assertLocationJSONObject(
				geocoderResult.getAddress(), latitude, longitude,
				i18nJSONObject.getJSONObject("pt_BR"));
		}
	}

	@FeatureFlag("LPD-11388")
	@Test
	public void testPostCustomObjectEntryWithLocationObjectField()
		throws Exception {

		GeocoderResult geocoderResult = new GeocoderResult(
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
			RandomTestUtil.randomDouble());

		try (SafeCloseable safeCloseable =
				GeocoderTestUtil.swapWithSafeCloseable(
					geocoderResult,
					objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_LOCATION))) {

			String defaultValue = RandomTestUtil.randomString();

			ObjectDefinition objectDefinition =
				ObjectDefinitionTestUtil.publishObjectDefinition(
					Collections.singletonList(
						new LocationObjectFieldBuilder(
						).labelMap(
							RandomTestUtil.randomLocaleStringMap()
						).name(
							"location"
						).objectFieldSettings(
							Arrays.asList(
								new ObjectFieldSettingBuilder(
								).name(
									ObjectFieldSettingConstants.
										NAME_DEFAULT_VALUE
								).value(
									JSONUtil.put(
										"address", defaultValue
									).toString()
								).build(),
								new ObjectFieldSettingBuilder(
								).name(
									ObjectFieldSettingConstants.
										NAME_DEFAULT_VALUE_TYPE
								).value(
									ObjectFieldSettingConstants.
										VALUE_INPUT_AS_VALUE
								).build())
						).build()));

			String address = RandomTestUtil.randomString();

			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"location", JSONUtil.put("address", address)
				).toString(),
				objectDefinition.getRESTContextPath(), Http.Method.POST);

			assertLocationJSONObject(
				address, geocoderResult.getLatitude(),
				geocoderResult.getLongitude(),
				jsonObject.getJSONObject("location"));

			objectValidationRuleLocalService.addObjectValidationRule(
				StringUtil.randomString(), TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(), true,
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap("Field must not be empty"),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				"not(isEmpty(location))", false, Collections.emptyList());

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				String.valueOf(jsonFactory.createJSONObject()),
				objectDefinition.getRESTContextPath(), Http.Method.POST);

			assertLocationJSONObject(
				defaultValue, geocoderResult.getLatitude(),
				geocoderResult.getLongitude(),
				jsonObject.getJSONObject("location"));
		}
	}

	@Test
	public void testPostCustomObjectEntryWithStatus() throws Exception {

		// With code inside status

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"status", JSONUtil.put("code", 0)
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));

		// Without code inside status

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"status", JSONUtil.put("label", "approved")
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));

		// Without status

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPostObjectEntriesWithComments() throws Exception {

		// Company scope

		_testPostObjectEntriesWithComments(0, getObjectDefinition1());

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testPostObjectEntriesWithComments(
				testGroupId, getSiteScopedObjectDefinition1());
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPostObjectEntriesWithMissingParentCommentReference()
		throws Exception {

		_enableComments(getObjectDefinition1());

		String parentExternalReferenceCode = RandomTestUtil.randomString();

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			new com.liferay.object.rest.dto.v1_0.ObjectEntry() {
				{
					systemProperties = new SystemProperties() {
						{
							comments = new Comment[] {
								new Comment() {
									{
										externalReferenceCode =
											RandomTestUtil.randomString();
										parentCommentExternalReferenceCode =
											parentExternalReferenceCode;
										text = RandomTestUtil.randomString();
									}
								}
							};
						}
					};
				}
			};

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Assert.assertNull(
			_commentManager.fetchComment(
				company.getGroupId(), parentExternalReferenceCode));

		ObjectEntryResource objectEntryResource = getObjectEntryResource(
			getObjectDefinition1(), TestPropsValues.getUser());

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			objectEntryResource.postObjectEntry(objectEntry);
		}

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_commentManager.getComment(
				company.getGroupId(), parentExternalReferenceCode);

		Assert.assertTrue(serviceBuilderComment instanceof WorkflowableComment);

		WorkflowableComment workflowableComment =
			(WorkflowableComment)serviceBuilderComment;

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, workflowableComment.getStatus());

		_commentManager.deleteComment(serviceBuilderComment.getCommentId());
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPostObjectEntryWithObjectFieldComments() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"comments", "comments", false)),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		_enableComments(objectDefinition);

		String value = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"comments", value
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(value, jsonObject.getString("comments"));

		objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPutByExternalReferenceCodeCustomObjectEntry()
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		assertPutCustomObjectEntry(
			jsonObject ->
				objectDefinition1.getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			objectDefinition1, getObjectDefinition2(), testGroupId);
	}

	@Test
	@TestInfo("LPD-103450")
	public void testPutByExternalReferenceCodeRestoreNotInTrash()
		throws Exception {

		ObjectEntry objectEntry = objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			getObjectDefinition1().getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertFalse(objectEntry.isInTrash());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"Unable to restore this item because it is not in the " +
					"Recycle Bin."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getEndpoint(getObjectDefinition1(), 0),
					"/by-external-reference-code/",
					objectEntry.getExternalReferenceCode(), "/restore"),
				Http.Method.PUT
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPutCustomObjectEntry() throws Exception {
		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		assertPutCustomObjectEntry(
			jsonObject ->
				objectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			objectDefinition1, getObjectDefinition2(), testGroupId);

		ObjectDefinition siteScopedObjectDefinition1 =
			getSiteScopedObjectDefinition1();

		assertPutCustomObjectEntry(
			jsonObject ->
				siteScopedObjectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			siteScopedObjectDefinition1, getSiteScopedObjectDefinition2(),
			testGroupId);
	}

	private void _assertGetObjectEntryWithFriendlyURL(
			String expectedFriendlyUrlPath, String friendlyUrlPath)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"friendlyUrlPath", friendlyUrlPath
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.GET);

		Assert.assertEquals(
			expectedFriendlyUrlPath, jsonObject.getString("friendlyUrlPath"));
	}

	private void _assertObjectEntryVersionNumber(
		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry,
		int versionNumber) {

		SystemProperties systemProperties = objectEntry.getSystemProperties();

		Version version = systemProperties.getVersion();

		Assert.assertEquals(versionNumber, (int)version.getNumber());
	}

	private void _assertPagination(
			int expectedSize, ObjectDefinition objectDefinition)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition.getRESTContextPath() + "?page=1&pageSize=5",
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(5, itemsJSONArray.length());

		Assert.assertEquals(1, jsonObject.getLong("page"));
		Assert.assertEquals(5, jsonObject.getLong("pageSize"));
		Assert.assertEquals(expectedSize, jsonObject.getLong("totalCount"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition.getRESTContextPath() + "?pageSize=-1",
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(expectedSize, itemsJSONArray.length());

		Assert.assertEquals(expectedSize, jsonObject.getLong("totalCount"));
	}

	private void _enableComments(ObjectDefinition objectDefinition) {
		objectDefinition.setEnableComments(true);

		objectDefinitionLocalService.updateObjectDefinition(objectDefinition);
	}

	private JSONArray _getCommentsJSONArray(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(objectDefinition, groupId) +
				"?nestedFields=systemProperties.comments",
			Http.Method.GET);

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		jsonObject = jsonArray.getJSONObject(0);

		JSONObject systemPropertiesJSONObject = jsonObject.getJSONObject(
			"systemProperties");

		return systemPropertiesJSONObject.getJSONArray("comments");
	}

	private String _getDeletePatchPutEndpoint(
		long groupId, ObjectDefinition objectDefinition,
		JSONObject objectEntryJSONObject) {

		String endpoint = getEndpoint(objectDefinition, groupId);

		ObjectScopeProvider objectScopeProvider =
			objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware() && (groupId != 0)) {
			return StringBundler.concat(
				endpoint, "/by-external-reference-code/",
				objectEntryJSONObject.getString("externalReferenceCode"));
		}

		return StringBundler.concat(
			endpoint, StringPool.SLASH, objectEntryJSONObject.getLong("id"));
	}

	private int _getTempFilesCount() {
		File tempFile = FileUtil.createTempFile();

		File parentFile = tempFile.getParentFile();

		return ArrayUtil.getLength(parentFile.listFiles());
	}

	private JSONArray _patchPutObjectEntryWithComments(
			JSONArray commentsJSONArray, long groupId, Http.Method httpMethod,
			ObjectDefinition objectDefinition, JSONObject objectEntryJSONObject)
		throws Exception {

		String endpoint = _getDeletePatchPutEndpoint(
			groupId, objectDefinition, objectEntryJSONObject);

		endpoint = endpoint + "?nestedFields=systemProperties.comments";

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"systemProperties", JSONUtil.put("comments", commentsJSONArray)
			).toString(),
			endpoint, httpMethod);

		JSONObject systemPropertiesJSONObject = jsonObject.getJSONObject(
			"systemProperties");

		return systemPropertiesJSONObject.getJSONArray("comments");
	}

	private JSONObject _postCustomObjectEntryWithAssigneeObjectField(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		ObjectField objectField = objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), "assignee");

		if (objectField == null) {
			ObjectFieldUtil.addCustomObjectField(
				new AssigneeObjectFieldBuilder(
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).name(
					"assignee"
				).objectDefinitionId(
					objectDefinition.getObjectDefinitionId()
				).userId(
					TestPropsValues.getUserId()
				).build());
		}

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode", user.getExternalReferenceCode()
				).put(
					"type", "User"
				).build()
			).toString(),
			getEndpoint(objectDefinition, testGroupId), Http.Method.POST);
	}

	private JSONArray _postObjectEntryWithComments(
			JSONArray commentsJSONArray, long groupId, boolean nestedFields,
			ObjectDefinition objectDefinition)
		throws Exception {

		JSONArray jsonArray = null;

		String endpoint = getEndpoint(objectDefinition, groupId);

		if (nestedFields) {
			endpoint = endpoint + "?nestedFields=systemProperties.comments";
		}

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"systemProperties", JSONUtil.put("comments", commentsJSONArray)
			).toString(),
			endpoint, Http.Method.POST);

		JSONObject systemPropertiesJSONObject = jsonObject.getJSONObject(
			"systemProperties");

		if (systemPropertiesJSONObject != null) {
			jsonArray = systemPropertiesJSONObject.getJSONArray("comments");
		}

		if (jsonArray == null) {
			return JSONFactoryUtil.createJSONArray();
		}

		return jsonArray;
	}

	private void _testDeleteObjectEntryWithComments(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		_enableComments(objectDefinition);

		String externalReferenceCode = RandomTestUtil.randomString();

		JSONArray commentsJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"parentCommentExternalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			));

		String endpoint = getEndpoint(objectDefinition, groupId);

		JSONObject objectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"systemProperties", JSONUtil.put("comments", commentsJSONArray)
			).toString(),
			endpoint + "?nestedFields=systemProperties.comments",
			Http.Method.POST);

		long objectEntryId = objectEntryJSONObject.getLong("id");

		List<com.liferay.portal.kernel.comment.Comment> serviceBuilderComment =
			_commentManager.getComments(
				objectDefinition.getClassName(), objectEntryId,
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertFalse(serviceBuilderComment.isEmpty());

		endpoint = _getDeletePatchPutEndpoint(
			groupId, objectDefinition, objectEntryJSONObject);

		HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.DELETE);

		serviceBuilderComment = _commentManager.getComments(
			objectDefinition.getClassName(), objectEntryId,
			WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertTrue(serviceBuilderComment.isEmpty());
	}

	private void _testGetObjectEntriesPageWithComments(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		_enableComments(objectDefinition);

		String externalReferenceCode = RandomTestUtil.randomString();

		JSONArray commentsJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"parentCommentExternalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"text", RandomTestUtil.randomString()
			));

		_postObjectEntryWithComments(
			commentsJSONArray, groupId, false, objectDefinition);

		JSONAssert.assertEquals(
			_toExpectedCommentsJSONArray(
				commentsJSONArray
			).toString(),
			_getCommentsJSONArray(
				groupId, objectDefinition
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private void _testGetObjectEntryTranslation(
			String endpoint, Map<String, String> headers)
		throws Exception {

		int tempFilesCount = _getTempFilesCount();

		try (InputStream inputStream = HTTPTestUtil.invokeToInputStream(
				null, endpoint, headers, Http.Method.GET)) {

			Assert.assertNotNull(inputStream);

			try (ZipInputStream zipInputStream = new ZipInputStream(
					inputStream)) {

				zipInputStream.getNextEntry();

				Assert.assertNotNull(StringUtil.read(zipInputStream));
			}
		}

		Assert.assertEquals(tempFilesCount, _getTempFilesCount());
	}

	private void _testPatchPutCustomObjectEntryExternalReferenceCode(
			Http.Method httpMethod, long objectEntryId)
		throws Exception {

		_testPatchPutCustomObjectEntryExternalReferenceCode(
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntryId),
			ERC_VALUE_2, httpMethod);
		_testPatchPutCustomObjectEntryExternalReferenceCode(
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/", ERC_VALUE_2),
			ERC_VALUE_3, httpMethod);
	}

	private void _testPatchPutCustomObjectEntryExternalReferenceCode(
			String endpoint, String externalReferenceCode,
			Http.Method httpMethod)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).toString(),
			endpoint, httpMethod);

		Assert.assertEquals(
			externalReferenceCode,
			jsonObject.getString("externalReferenceCode"));
	}

	private void _testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method httpMethod, ObjectDefinition objectDefinition,
			boolean useExternalReferenceCode)
		throws Exception {

		JSONObject jsonObject = _postCustomObjectEntryWithAssigneeObjectField(
			objectDefinition, UserTestUtil.addUser());

		String endpoint =
			objectDefinition.getRESTContextPath() + "/" +
				jsonObject.getLong("id");

		if (useExternalReferenceCode) {
			endpoint =
				getEndpoint(objectDefinition, testGroupId) +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode");
		}

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode", role.getExternalReferenceCode()
				).put(
					"type", "Role"
				).build()
			).put(
				"externalReferenceCode",
				jsonObject.getString("externalReferenceCode")
			).put(
				"id", jsonObject.getLong("id")
			).toString(),
			endpoint, httpMethod);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"externalReferenceCode", role.getExternalReferenceCode()
			).put(
				"name", role.getName()
			).put(
				"type", "Role"
			).toString(),
			String.valueOf(jsonObject.get("assignee")),
			JSONCompareMode.LENIENT);
	}

	private void
			_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
				Http.Method httpMethod, ObjectDefinition objectDefinition,
				ObjectDefinition siteScopedObjectDefinition)
		throws Exception {

		_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
			objectDefinition.getRESTContextPath(),
			objectDefinition.getRESTContextPath() +
				"/by-external-reference-code/",
			httpMethod);

		String endpoint = getEndpoint(siteScopedObjectDefinition, testGroupId);

		_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
			endpoint, endpoint + "/by-external-reference-code/", httpMethod);
	}

	private void
			_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
				String endpoint1, String endpoint2, Http.Method httpMethod)
		throws Exception {

		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", externalReferenceCode1
				).toString(),
				endpoint1, Http.Method.POST));
		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", externalReferenceCode2
				).toString(),
				endpoint1, Http.Method.POST));
		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					"externalReferenceCode", externalReferenceCode2
				).toString(),
				endpoint2 + externalReferenceCode1, httpMethod));
	}

	private void _testPatchPutObjectEntryWithComments(
			long groupId, Http.Method httpMethod,
			ObjectDefinition objectDefinition)
		throws Exception {

		JSONObject objectEntryJSONObject = postObjectEntry(
			groupId, objectDefinition);

		String externalReferenceCode = RandomTestUtil.randomString();

		JSONArray commentsJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"parentCommentExternalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			));

		JSONAssert.assertEquals(
			_toExpectedCommentsJSONArray(
				commentsJSONArray
			).toString(),
			_patchPutObjectEntryWithComments(
				commentsJSONArray, groupId, httpMethod, objectDefinition,
				objectEntryJSONObject
			).toString(),
			JSONCompareMode.LENIENT);

		commentsJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"parentCommentExternalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"text", RandomTestUtil.randomString()
			));

		JSONAssert.assertEquals(
			_toExpectedCommentsJSONArray(
				commentsJSONArray
			).toString(),
			_patchPutObjectEntryWithComments(
				commentsJSONArray, groupId, httpMethod, objectDefinition,
				objectEntryJSONObject
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private void _testPatchPutObjectEntryWithMissingParentCommentReference(
			Http.Method httpMethod)
		throws Exception {

		ObjectEntry serviceBuilderObjectEntry =
			objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				getObjectDefinition1().getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		String parentExternalReferenceCode = RandomTestUtil.randomString();

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			new com.liferay.object.rest.dto.v1_0.ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
					).build();
					systemProperties = new SystemProperties() {
						{
							comments = new Comment[] {
								new Comment() {
									{
										externalReferenceCode =
											RandomTestUtil.randomString();
										parentCommentExternalReferenceCode =
											parentExternalReferenceCode;
										text = RandomTestUtil.randomString();
									}
								}
							};
						}
					};
				}
			};

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Assert.assertNull(
			_commentManager.fetchComment(
				company.getGroupId(), parentExternalReferenceCode));

		ObjectEntryResource objectEntryResource = getObjectEntryResource(
			getObjectDefinition1(), TestPropsValues.getUser());

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			if (httpMethod == Http.Method.PATCH) {
				objectEntryResource.patchObjectEntry(
					serviceBuilderObjectEntry.getObjectEntryId(), objectEntry);
			}
			else if (httpMethod == Http.Method.PUT) {
				objectEntryResource.putObjectEntry(
					serviceBuilderObjectEntry.getObjectEntryId(), objectEntry);
			}
		}

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_commentManager.getComment(
				company.getGroupId(), parentExternalReferenceCode);

		Assert.assertTrue(serviceBuilderComment instanceof WorkflowableComment);

		WorkflowableComment workflowableComment =
			(WorkflowableComment)serviceBuilderComment;

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, workflowableComment.getStatus());

		_commentManager.deleteComment(serviceBuilderComment.getCommentId());
	}

	private void _testPostCustomObjectEntryWithAssigneeObjectField(
			ObjectDefinition objectDefinition)
		throws Exception {

		User user = UserTestUtil.addUser();

		user.setPortraitId(RandomTestUtil.nextLong());

		user = userLocalService.updateUser(user);

		JSONObject jsonObject = _postCustomObjectEntryWithAssigneeObjectField(
			objectDefinition, user);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"externalReferenceCode", user.getExternalReferenceCode()
			).put(
				"name", user.getFullName()
			).put(
				"portrait",
				user.getPortraitURL(
					new ThemeDisplay() {
						{
							setPathImage(_portal.getPathImage());
						}
					})
			).put(
				"type", "User"
			).toString(),
			String.valueOf(jsonObject.get("assignee")),
			JSONCompareMode.LENIENT);
	}

	private void _testPostObjectEntriesWithComments(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		JSONArray commentsJSONArray = JSONUtil.put(
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"text", RandomTestUtil.randomString()
			));

		Assert.assertEquals(
			0,
			_postObjectEntryWithComments(
				commentsJSONArray, groupId, true, objectDefinition
			).length());

		_enableComments(objectDefinition);

		JSONAssert.assertEquals(
			_toExpectedCommentsJSONArray(
				commentsJSONArray
			).toString(),
			_postObjectEntryWithComments(
				commentsJSONArray, groupId, true, objectDefinition
			).toString(),
			JSONCompareMode.LENIENT);

		String externalReferenceCode = RandomTestUtil.randomString();

		commentsJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"parentCommentExternalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			));

		JSONAssert.assertEquals(
			_toExpectedCommentsJSONArray(
				commentsJSONArray
			).toString(),
			_postObjectEntryWithComments(
				commentsJSONArray, groupId, true, objectDefinition
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private String _toDateString(Date date) {
		Instant instant = date.toInstant();

		return String.valueOf(instant.truncatedTo(ChronoUnit.MINUTES));
	}

	private JSONArray _toExpectedCommentsJSONArray(JSONArray commentsJSONArray)
		throws Exception {

		JSONArray expectedCommentsJSONArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < commentsJSONArray.length(); i++) {
			JSONObject commentJSONObject = commentsJSONArray.getJSONObject(i);

			expectedCommentsJSONArray.put(
				JSONFactoryUtil.createJSONObject(
					commentJSONObject.toString()
				).put(
					"text",
					StringBundler.concat(
						"<p>", commentJSONObject.getString("text"), "</p>")
				));
		}

		return expectedCommentsJSONArray;
	}

	@Inject
	private CommentManager _commentManager;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private Portal _portal;

}