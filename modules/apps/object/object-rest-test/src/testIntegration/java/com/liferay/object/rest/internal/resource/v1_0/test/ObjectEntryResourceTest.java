/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalServiceUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.processor.RawMetadataProcessorUtil;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.ParentTaxonomyCategory;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.ParentTaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.delivery.dto.v1_0.Comment;
import com.liferay.headless.delivery.dto.v1_0.Creator;
import com.liferay.map.geocoder.GeocoderResult;
import com.liferay.map.test.util.GeocoderTestUtil;
import com.liferay.object.configuration.ObjectConfiguration;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.field.builder.AssigneeObjectFieldBuilder;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.LocationObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.Folder;
import com.liferay.object.rest.dto.v1_0.Link;
import com.liferay.object.rest.dto.v1_0.SystemProperties;
import com.liferay.object.rest.dto.v1_0.Version;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.test.util.ObjectFieldTestUtil;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.rest.test.util.UserAccountTestUtil;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.WorkflowableComment;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.scope.Scope;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;
import com.liferay.portlet.documentlibrary.constants.DLConstants;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import jakarta.ws.rs.core.HttpHeaders;

import java.io.ByteArrayInputStream;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
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
	public void testDeleteScopeScopeKeyByExternalReferenceCode()
		throws Exception {

		// Site scope external reference code

		Group group = groupLocalService.fetchGroup(testGroupId);

		_siteScopedObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			OBJECT_FIELD_VALUE_1);

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getEndpoint(
					getSiteScopedObjectDefinition1(),
					group.getExternalReferenceCode()),
				"/by-external-reference-code/",
				_siteScopedObjectEntry1.getExternalReferenceCode()),
			Http.Method.DELETE);

		assertNotFound(
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getSiteScopedObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH,
					_siteScopedObjectEntry1.getObjectEntryId()),
				Http.Method.GET));

		// Site scope group ID

		_siteScopedObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			OBJECT_FIELD_VALUE_1);

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getEndpoint(
					getSiteScopedObjectDefinition1(), group.getGroupId()),
				"/by-external-reference-code/",
				_siteScopedObjectEntry1.getExternalReferenceCode()),
			Http.Method.DELETE);

		assertNotFound(
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getSiteScopedObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH,
					_siteScopedObjectEntry1.getObjectEntryId()),
				Http.Method.GET));

		// Site scope group key

		_siteScopedObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			OBJECT_FIELD_VALUE_1);

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getEndpoint(
					getSiteScopedObjectDefinition1(), group.getGroupKey()),
				"/by-external-reference-code/",
				_siteScopedObjectEntry1.getExternalReferenceCode()),
			Http.Method.DELETE);

		assertNotFound(
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getSiteScopedObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH,
					_siteScopedObjectEntry1.getObjectEntryId()),
				Http.Method.GET));
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
	public void testGetObjectEntriesPageWithObjectActions() throws Exception {
		JSONObject actionsJSONObject1 = _getActionsJSONObject(
			getObjectDefinition4());

		Assert.assertFalse(actionsJSONObject1.isNull("create"));
		Assert.assertFalse(actionsJSONObject1.isNull("createBatch"));
		Assert.assertFalse(actionsJSONObject1.isNull("deleteBatch"));
		Assert.assertFalse(actionsJSONObject1.isNull("get"));
		Assert.assertFalse(actionsJSONObject1.isNull("updateBatch"));

		HTTPTestUtil.customize(
		).withGuest(
		).apply(
			() -> {
				JSONObject actionsJSONObject2 = _getActionsJSONObject(
					getObjectDefinition4());

				Assert.assertTrue(
					actionsJSONObject2.keySet(
					).isEmpty());
			}
		);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			getObjectDefinition4().getResourceName(), role);
		addResourcePermission(
			ActionKeys.VIEW, getObjectDefinition4().getClassName(), role);

		String userName = RandomTestUtil.randomString();

		User user = addUser(userName, "test1");

		roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		HTTPTestUtil.customize(
		).withCredentials(
			userName + "@liferay.com", "test1"
		).apply(
			() -> {
				JSONObject actionsJSONObject3 = _getActionsJSONObject(
					getObjectDefinition4());

				Assert.assertFalse(actionsJSONObject3.isNull("create"));
				Assert.assertFalse(actionsJSONObject3.isNull("createBatch"));
				Assert.assertTrue(actionsJSONObject3.isNull("deleteBatch"));
				Assert.assertFalse(actionsJSONObject3.isNull("get"));
				Assert.assertTrue(actionsJSONObject3.isNull("updateBatch"));
			}
		);

		addResourcePermission(
			ActionKeys.DELETE, getObjectDefinition4().getClassName(), role);
		addResourcePermission(
			ActionKeys.UPDATE, getObjectDefinition4().getClassName(), role);

		HTTPTestUtil.customize(
		).withCredentials(
			userName + "@liferay.com", "test1"
		).apply(
			() -> {
				JSONObject actionsJSONObject4 = _getActionsJSONObject(
					getObjectDefinition4());

				Assert.assertFalse(actionsJSONObject4.isNull("create"));
				Assert.assertFalse(actionsJSONObject4.isNull("createBatch"));
				Assert.assertFalse(actionsJSONObject4.isNull("deleteBatch"));
				Assert.assertFalse(actionsJSONObject4.isNull("get"));
				Assert.assertFalse(actionsJSONObject4.isNull("updateBatch"));
			}
		);
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
	@TestInfo("LPD-62553")
	public void testGetObjectEntryActionsWithCompanySharingDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						group.getCompanyId(),
						"com.liferay.sharing.internal.configuration." +
							"SharingCompanyConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			_testGetObjectEntryActions(false);
		}
	}

	@Test
	@TestInfo("LPD-62553")
	public void testGetObjectEntryActionsWithGroupSharingDisabled()
		throws Exception {

		UnicodeProperties originalUnicodeProperties =
			group.getTypeSettingsProperties();

		groupLocalService.updateGroup(
			group.getGroupId(),
			UnicodePropertiesBuilder.create(
				originalUnicodeProperties, true
			).put(
				"sharingEnabled", false
			).buildString());

		try {
			_testGetObjectEntryActions(false);
		}
		finally {
			groupLocalService.updateGroup(
				group.getGroupId(), originalUnicodeProperties.toString());
		}
	}

	@Test
	@TestInfo("LPD-62553")
	public void testGetObjectEntryActionsWithSharingEnabled() throws Exception {
		_testGetObjectEntryActions(true);
	}

	@Test
	@TestInfo("LPD-62553")
	public void testGetObjectEntryActionsWithSystemSharingDisabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.sharing.internal.configuration." +
						"SharingSystemConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", false
					).build())) {

			_testGetObjectEntryActions(false);
		}
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
	public void testGetObjectEntryPermissionsPage() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			getObjectDefinition1().getResourceName(), role);

		User user = addUser("test1", "test1");

		roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		HTTPTestUtil.customize(
		).withCredentials(
			"test1@liferay.com", "test1"
		).apply(
			() -> {
				String externalReferenceCode = RandomTestUtil.randomString();

				JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", externalReferenceCode
					).toString(),
					getEndpoint(getObjectDefinition1(), testGroupId),
					Http.Method.POST);

				jsonObject = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.putAll(
						getPermissionsJSONObject(new String[0], role.getName())
					).toString(),
					StringBundler.concat(
						getEndpoint(getObjectDefinition1(), testGroupId), "/",
						jsonObject.getString("id"), "/permissions"),
					Http.Method.PUT);

				Assert.assertNotEquals(
					jsonObject.getString("status"), "FORBIDDEN",
					jsonObject.getString("status"));
			}
		);
	}

	@Test
	@TestInfo("LPD-83639")
	public void testGetObjectEntryShareAction() throws Exception {

		// With asset library administrator role

		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(depotEntry.getGroupId());

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
			depotEntry, serviceContext, depotEntry.getUserId());

		ObjectEntry objectEntry1 = _addObjectEntry(
			depotEntry, objectDefinition, objectEntryFolder, serviceContext,
			depotEntry.getUserId());

		String password = RandomTestUtil.randomString();

		User user = addUser(RandomTestUtil.randomString(), password);

		Role role = roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);

		UserGroupRole userGroupRole =
			userGroupRoleLocalService.addUserGroupRole(
				user.getUserId(), depotEntry.getGroupId(), role.getRoleId());

		JSONObject jsonObject = _getActionsJSONObject(
			objectDefinition, objectEntry1, user, password);

		Assert.assertTrue(jsonObject.has("share"));

		userGroupRoleLocalService.deleteUserGroupRole(userGroupRole);

		// With asset library content reviewer role

		role = roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);

		userGroupRole = userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), depotEntry.getGroupId(), role.getRoleId());

		jsonObject = _getActionsJSONObject(
			objectDefinition, objectEntry1, user, password);

		Assert.assertFalse(jsonObject.has("share"));

		// With asset library content reviewer role and shared object entry

		SharingEntry sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, user.getUserId(),
			classNameLocalService.getClassNameId(
				objectEntry1.getModelClassName()),
			objectEntry1.getObjectEntryId(), depotEntry.getGroupId(), true,
			List.of(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				depotEntry.getGroupId(), TestPropsValues.getUserId()));

		jsonObject = _getActionsJSONObject(
			objectDefinition, objectEntry1, user, password);

		_sharingEntryLocalService.deleteSharingEntry(
			sharingEntry.getSharingEntryId());

		Assert.assertTrue(jsonObject.has("share"));

		userGroupRoleLocalService.deleteUserGroupRole(userGroupRole);

		// With CMS administrator role

		role = roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		jsonObject = _getActionsJSONObject(
			objectDefinition, objectEntry1, user, password);

		Assert.assertTrue(jsonObject.has("share"));

		roleLocalService.deleteUserRole(user.getUserId(), role.getRoleId());

		// With user as creator

		ObjectEntry objectEntry2 = _addObjectEntry(
			depotEntry, objectDefinition, objectEntryFolder,
			ServiceContextTestUtil.getServiceContext(
				depotEntry.getGroupId(), user.getUserId()),
			user.getUserId());

		jsonObject = _getActionsJSONObject(
			objectDefinition, objectEntry2, user, password);

		Assert.assertTrue(jsonObject.has("share"));

		// Without role

		jsonObject = _getActionsJSONObject(
			objectDefinition, objectEntry1, user, password);

		Assert.assertFalse(jsonObject.has("share"));
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
	public void testGetObjectEntryWithAttachmentObjectField() throws Exception {
		FileEntry fileEntry = addTempFileEntry(
			DLTestUtil.randomTextFileBytes(), getObjectDefinition1(),
			RandomTestUtil.randomString());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value1"
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				fileEntry.getFileEntryId()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONObject scopeJSONObject = JSONUtil.put(
			"externalReferenceCode",
			() -> {
				Group group = groupLocalService.getGroup(testGroupId);

				return group.getExternalReferenceCode();
			}
		).put(
			"type", Scope.Type.SITE.getValue()
		);

		_assertAttachmentJSONObject(
			null, null,
			jsonObject.getJSONObject(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1),
			scopeJSONObject);

		byte[] content = DLTestUtil.randomTextFileBytes();

		fileEntry = addTempFileEntry(
			content, getObjectDefinition1(), RandomTestUtil.randomString());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value2"
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				fileEntry.getFileEntryId()
			).toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), "?nestedFields=",
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				".fileBase64"),
			Http.Method.POST);

		_assertAttachmentJSONObject(
			null, Base64.encode(content),
			jsonObject.getJSONObject(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1),
			scopeJSONObject);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() +
				"/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.GET);

		_assertAttachmentJSONObject(
			null, null,
			jsonObject.getJSONObject(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1),
			scopeJSONObject);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/",
				jsonObject.getString("externalReferenceCode"), "?nestedFields=",
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				".fileBase64"),
			Http.Method.GET);

		_assertAttachmentJSONObject(
			null, Base64.encode(content),
			jsonObject.getJSONObject(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1),
			scopeJSONObject);

		content = DLTestUtil.randomTextFileBytes();

		DLFileEntry dlFileEntry = _addDLFileEntry(
			content, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value1"
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				dlFileEntry.getFileEntryId()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		scopeJSONObject = JSONUtil.put(
			"externalReferenceCode", group.getExternalReferenceCode()
		).put(
			"type", Scope.Type.SITE.getValue()
		);

		_assertAttachmentJSONObject(
			dlFileEntry, null,
			jsonObject.getJSONObject(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE),
			scopeJSONObject);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() +
				"/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.GET);

		_assertAttachmentJSONObject(
			dlFileEntry, null,
			jsonObject.getJSONObject(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE),
			scopeJSONObject);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/",
				jsonObject.getString("externalReferenceCode"), "?nestedFields=",
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				".fileBase64"),
			Http.Method.GET);

		_assertAttachmentJSONObject(
			null, Base64.encode(content),
			jsonObject.getJSONObject(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE),
			scopeJSONObject);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value1"
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				dlFileEntry.getFileEntryId()
			).toString(),
			getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
			Http.Method.POST);

		_assertAttachmentJSONObject(
			dlFileEntry, null,
			jsonObject.getJSONObject(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE),
			scopeJSONObject);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value1"
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				dlFileEntry.getFileEntryId()
			).toString(),
			getEndpoint(getSiteScopedObjectDefinition1(), group.getGroupId()),
			Http.Method.POST);

		_assertAttachmentJSONObject(
			dlFileEntry, null,
			jsonObject.getJSONObject(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE),
			null);

		FileEntry pdfFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/document.pdf"), null,
			null, null, ServiceContextTestUtil.getServiceContext());

		DLFileEntry pdfDLFileEntry = _dlFileEntryLocalService.getFileEntry(
			pdfFileEntry.getFileEntryId());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value1"
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				pdfDLFileEntry.getFileEntryId()
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		_assertAttachmentJSONObject(
			pdfDLFileEntry, null,
			jsonObject.getJSONObject(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE),
			scopeJSONObject);

		Assert.assertNull(
			JSONUtil.getValueAsJSONObject(
				jsonObject,
				"JSONObject/" +
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				"JSONObject/metadata"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition2().getRESTContextPath(),
				"/by-external-reference-code/",
				jsonObject.getString("externalReferenceCode"), "?nestedFields=",
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				".metadata"),
			Http.Method.GET);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"numberOfPages", 2
			).toString(),
			JSONUtil.getValueAsString(
				jsonObject,
				"JSONObject/" +
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				"JSONObject/metadata"),
			JSONCompareMode.LENIENT);
	}

	@Test
	@TestInfo("LPD-85194")
	public void testGetObjectEntryWithAttachmentObjectFieldImageMetadata()
		throws Exception {

		_testGetObjectEntryWithAttachmentObjectFieldImageMetadata(
			_addFileEntry(
				FileUtil.getBytes(
					getClass(),
					"dependencies/sample_large_square_1500x1500.jpg"),
				RandomTestUtil.randomString() + ".jpg",
				ContentTypes.IMAGE_JPEG),
			"Square", "Large (Bigger Than 1024)");
		_testGetObjectEntryWithAttachmentObjectFieldImageMetadata(
			_addFileEntry(
				FileUtil.getBytes(
					getClass(), "dependencies/sample_medium_tall_600x800.jpg"),
				RandomTestUtil.randomString() + ".jpg",
				ContentTypes.IMAGE_JPEG),
			"Tall", "Medium (Up to 1024x768)");
		_testGetObjectEntryWithAttachmentObjectFieldImageMetadata(
			_addFileEntry(
				FileUtil.getBytes(
					getClass(), "dependencies/sample_small_wide_400x300.jpg"),
				RandomTestUtil.randomString() + ".jpg",
				ContentTypes.IMAGE_JPEG),
			"Wide", "Small (Up to 400x300)");
		_testGetObjectEntryWithAttachmentObjectFieldImageMetadata(
			_addFileEntry(
				RandomTestUtil.randomBytes(),
				RandomTestUtil.randomString() + ".txt",
				ContentTypes.TEXT_PLAIN),
			null, null);
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
	public void testGetObjectEntryWithKeywords() throws Exception {
		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value"
			).put(
				"keywords", JSONUtil.putAll("tag1", "tag2")
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition2().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.GET);

		JSONArray keywordsJSONArray = jsonObject.getJSONArray("keywords");

		Assert.assertEquals("tag1", keywordsJSONArray.get(0));
		Assert.assertEquals("tag2", keywordsJSONArray.get(1));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"keywords", JSONUtil.putAll("tag1", "tag2", "tag3")
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition2().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.GET);

		keywordsJSONArray = jsonObject.getJSONArray("keywords");

		Assert.assertEquals("tag1", keywordsJSONArray.get(0));
		Assert.assertEquals("tag2", keywordsJSONArray.get(1));
		Assert.assertEquals("tag3", keywordsJSONArray.get(2));

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
			getObjectDefinition2().getClassName(), jsonObject.getLong("id"));

		IdempotentRetryAssert.retryAssert(
			30, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS,
			() -> {
				Map<String, Integer> assetCounts = new HashMap<>();

				for (AssetTag assetTag :
						AssetTagLocalServiceUtil.getAssetEntryAssetTags(
							assetEntry.getEntryId())) {

					assetCounts.put(
						assetTag.getName(), assetTag.getAssetCount());
				}

				Assert.assertEquals(
					HashMapBuilder.put(
						"tag1", 2
					).put(
						"tag2", 2
					).put(
						"tag3", 1
					).build(),
					assetCounts);

				return null;
			});
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
	public void testGetObjectEntryWithObjectActions() throws Exception {
		_testGetObjectEntryWithObjectActions(
			_addObjectAction(getObjectDefinition1()), getObjectDefinition1(),
			(actionJSONObject, jsonObject, objectAction) -> Assert.assertEquals(
				StringBundler.concat(
					"http://localhost:", PortalUtil.getPortalServerPort(false),
					"/o", getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/",
					jsonObject.getString("externalReferenceCode"),
					"/object-actions/", objectAction.getName()),
				actionJSONObject.getString("href")));

		_testGetObjectEntryWithObjectActions(
			_addObjectAction(getSiteScopedObjectDefinition1()),
			getSiteScopedObjectDefinition1(),
			(actionJSONObject, jsonObject, objectAction) -> Assert.assertEquals(
				StringBundler.concat(
					"http://localhost:", PortalUtil.getPortalServerPort(false),
					"/o", getSiteScopedObjectDefinition1().getRESTContextPath(),
					"/scopes/", testGroupId, "/by-external-reference-code/",
					jsonObject.getString("externalReferenceCode"),
					"/object-actions/", objectAction.getName()),
				actionJSONObject.getString("href")));
	}

	@Test
	@TestInfo("LPD-66174")
	public void testGetObjectEntryWithSystemObjectCustomFieldsInOneToManyRelationship()
		throws Exception {

		String expandoFieldName = RandomTestUtil.randomString();
		String expandoFieldValue = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			HashMapBuilder.<String, Serializable>put(
				() -> {
					ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
						ExpandoTestUtil.addTable(
							PortalUtil.getClassNameId(User.class),
							ExpandoTableConstants.DEFAULT_TABLE_NAME),
						expandoFieldName, ExpandoColumnConstants.STRING);

					return expandoColumn.getName();
				},
				expandoFieldValue
			).build());

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[0], serviceContext);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			userSystemObjectDefinition, getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_userERC"),
				user.getExternalReferenceCode()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				jsonObject1.getString("id"), "?nestedFields=",
				objectRelationship1.getName()),
			Http.Method.GET);

		JSONArray customFieldsJSONArray = JSONUtil.getValueAsJSONArray(
			jsonObject2, "JSONObject/" + objectRelationship1.getName(),
			"JSONArray/customFields");

		Assert.assertEquals(1, customFieldsJSONArray.length());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"customValue", JSONUtil.put("data", expandoFieldValue)
			).put(
				"dataType", "Text"
			).put(
				"name", expandoFieldName
			).toString(),
			String.valueOf(customFieldsJSONArray.getJSONObject(0)),
			JSONCompareMode.STRICT);
	}

	@Test
	public void testGetObjectEntryWithTaxonomyCategories() throws Exception {

		// Company scope

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary companyAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), company.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory companyTaxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				company.getGroupId(), companyAssetVocabulary.getVocabularyId());

		TaxonomyCategory companyTaxonomyCategory2 =
			_postTaxonomyCategoryTaxonomyCategory(
				company.getGroupId(), companyTaxonomyCategory1.getId(),
				companyTaxonomyCategory1.getTaxonomyVocabularyId());

		_testGetObjectEntryWithTaxonomyCategories(
			0, getObjectDefinition1(), companyTaxonomyCategory1,
			companyTaxonomyCategory2);

		// Depot scope

		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		AssetVocabulary depotAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), depotEntry.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory depotTaxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				depotEntry.getGroupId(),
				depotAssetVocabulary.getVocabularyId());

		TaxonomyCategory depotTaxonomyCategory2 =
			_postTaxonomyCategoryTaxonomyCategory(
				depotEntry.getGroupId(), depotTaxonomyCategory1.getId(),
				depotTaxonomyCategory1.getTaxonomyVocabularyId());

		ObjectDefinition depotScopedObjectDefinition =
			addDepotScopedObjectDefinition();

		_testGetObjectEntryWithTaxonomyCategories(
			depotEntry.getGroupId(), depotScopedObjectDefinition,
			depotTaxonomyCategory1, depotTaxonomyCategory2);

		// Site scope

		TaxonomyCategory siteTaxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				testGroupId, assetVocabulary.getVocabularyId());

		TaxonomyCategory siteTaxonomyCategory2 =
			_postTaxonomyCategoryTaxonomyCategory(
				testGroupId, siteTaxonomyCategory1.getId(),
				siteTaxonomyCategory1.getTaxonomyVocabularyId());

		_testGetObjectEntryWithTaxonomyCategories(
			testGroupId, getSiteScopedObjectDefinition1(),
			siteTaxonomyCategory1, siteTaxonomyCategory2);
	}

	@Test
	public void testGetObjectEntryWithTaxonomyCategoriesAndEmbeddedTaxonomyCategory()
		throws Exception {

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), company.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory taxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());
		TaxonomyCategory taxonomyCategory2 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value"
			).put(
				"taxonomyCategoryIds",
				JSONUtil.putAll(
					taxonomyCategory1.getId(), taxonomyCategory2.getId())
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				jsonObject.getString("id"),
				"?nestedFields=embeddedTaxonomyCategory"),
			Http.Method.GET);

		_assertTaxonomyCategories(
			jsonObject.getLong("scopeId"),
			jsonObject.getJSONArray("taxonomyCategoryBriefs"), true,
			taxonomyCategory1, taxonomyCategory2);
	}

	@Test
	public void testGetObjectRelationshipERCFieldNameInOneToManyRelationship()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, getObjectDefinition2().getRESTContextPath(), Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			itemJSONObject.getString(objectRelationship1.getName() + "ERC"),
			objectEntry1.getExternalReferenceCode());
	}

	@Test
	public void testGetObjectRelationshipERCFieldNameInOneToManyRelationshipFromRelatedObjectEntry()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), "?nestedFields=",
				objectRelationship1.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		JSONArray relationshipJSONArray = itemJSONObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(1, relationshipJSONArray.length());

		JSONObject relatedObjectEntryJSONObject =
			relationshipJSONArray.getJSONObject(0);

		Assert.assertEquals(
			relatedObjectEntryJSONObject.getString(
				objectRelationship1.getName() + "ERC"),
			objectEntry1.getExternalReferenceCode());
	}

	@Test
	public void testGetScopeScopeKeyByExternalReferenceCode() throws Exception {

		// Site scope external reference code

		Group group = groupLocalService.fetchGroup(testGroupId);

		String objectFieldValue = RandomTestUtil.randomString();

		_siteScopedObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			objectFieldValue);

		Assert.assertEquals(
			objectFieldValue,
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					null,
					StringBundler.concat(
						getEndpoint(
							getSiteScopedObjectDefinition1(),
							group.getExternalReferenceCode()),
						"/by-external-reference-code/",
						_siteScopedObjectEntry1.getExternalReferenceCode()),
					Http.Method.GET),
				"Object/" + OBJECT_FIELD_NAME_1));

		// Site scope group ID

		Assert.assertEquals(
			objectFieldValue,
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					null,
					StringBundler.concat(
						getEndpoint(
							getSiteScopedObjectDefinition1(),
							group.getGroupId()),
						"/by-external-reference-code/",
						_siteScopedObjectEntry1.getExternalReferenceCode()),
					Http.Method.GET),
				"Object/" + OBJECT_FIELD_NAME_1));

		// Site scope group key

		Assert.assertEquals(
			objectFieldValue,
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					null,
					StringBundler.concat(
						getEndpoint(
							getSiteScopedObjectDefinition1(),
							group.getGroupKey()),
						"/by-external-reference-code/",
						_siteScopedObjectEntry1.getExternalReferenceCode()),
					Http.Method.GET),
				"Object/" + OBJECT_FIELD_NAME_1));
	}

	@Test
	public void testGetScopeScopeKeyObjectEntriesPage() throws Exception {

		// Depot scope group ID

		ObjectDefinition depotScopedObjectDefinition =
			addDepotScopedObjectDefinition();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			assertNotFound(
				HTTPTestUtil.invokeToJSONObject(
					null,
					getEndpoint(
						depotScopedObjectDefinition,
						RandomTestUtil.randomLong()),
					Http.Method.GET));
		}

		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		ObjectEntry depotScopedObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			depotEntry.getGroupId(), depotScopedObjectDefinition,
			Collections.emptyMap());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(depotScopedObjectDefinition, depotEntry.getGroupId()),
			Http.Method.GET);

		assertItem(
			0, jsonObject, "id", depotScopedObjectEntry.getObjectEntryId());

		// Site scope external reference code

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			assertNotFound(
				HTTPTestUtil.invokeToJSONObject(
					null,
					getEndpoint(
						getSiteScopedObjectDefinition1(),
						RandomTestUtil.randomLong()),
					Http.Method.GET));
		}

		_siteScopedObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getSiteScopedObjectDefinition1(), OBJECT_FIELD_NAME_1,
			OBJECT_FIELD_VALUE_1);

		Group group = groupLocalService.fetchGroup(testGroupId);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(
				getSiteScopedObjectDefinition1(),
				group.getExternalReferenceCode()),
			Http.Method.GET);

		assertItem(0, jsonObject, OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		// Site scope group ID

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(getSiteScopedObjectDefinition1(), group.getGroupId()),
			Http.Method.GET);

		assertItem(0, jsonObject, OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		// Site scope group key

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(getSiteScopedObjectDefinition1(), group.getGroupKey()),
			Http.Method.GET);

		assertItem(0, jsonObject, OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
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
	@TestInfo("LPD-53919")
	public void testPatchCustomObjectEntryUnlinkNestedCustomObjectEntries()
		throws Exception {

		// Many to many

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, objectDefinition1, getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		ObjectDefinition siteScopedObjectDefinition1 =
			getSiteScopedObjectDefinition1();

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				siteScopedObjectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		// Many to one

		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, objectDefinition1, getObjectDefinition2());
		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				siteScopedObjectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2());

		// One to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, objectDefinition1, getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				siteScopedObjectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PATCH, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	@TestInfo("LPD-53919")
	public void testPatchCustomObjectEntryUnlinkNestedCustomObjectEntriesByExternalReferenceCode()
		throws Exception {

		// Many to many

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, objectDefinition1, getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		ObjectDefinition siteScopedObjectDefinition1 =
			getSiteScopedObjectDefinition1();

		String externalReferenceCodeEndpoint = getEndpoint(
			siteScopedObjectDefinition1, testGroupId);

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				externalReferenceCodeEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		// Many to one

		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, objectDefinition1, getObjectDefinition2());
		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				externalReferenceCodeEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2());

		// One to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, objectDefinition1, getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				externalReferenceCodeEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PATCH, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testPatchCustomObjectEntryWithAttachmentObjectFieldScope()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			getObjectDefinition1().getResourceName(), role);

		String password = RandomTestUtil.randomString();
		String userName = RandomTestUtil.randomString();

		User user = addUser(userName, password);

		roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		HTTPTestUtil.customize(
		).withCredentials(
			userName + "@liferay.com", password
		).apply(
			() -> {

				// Default scope

				JSONObject objectEntryJSONObject =
					HTTPTestUtil.invokeToJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
						).toString(),
						getObjectDefinition1().getRESTContextPath(),
						Http.Method.POST);

				String endpoint = StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH, objectEntryJSONObject.get("id"));

				com.liferay.object.rest.dto.v1_0.FileEntry fileEntry1 =
					_toFileEntry(
						Base64::encode, DLTestUtil.randomTextFileBytes(),
						RandomTestUtil.randomString() + ".txt", null, null,
						ContentTypes.TEXT_PLAIN);

				fileEntry1.setExternalReferenceCode(
					RandomTestUtil.randomString());

				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
						jsonFactory.createJSONObject(fileEntry1.toString())
					).toString(),
					endpoint, Http.Method.PATCH);

				Company company = companyLocalService.getCompany(
					getObjectDefinition1().getCompanyId());

				DLFileEntry dlFileEntry =
					_dlFileEntryLocalService.
						fetchDLFileEntryByExternalReferenceCode(
							fileEntry1.getExternalReferenceCode(),
							company.getGroupId());

				Assert.assertEquals(user.getUserId(), dlFileEntry.getUserId());

				// Site member

				userLocalService.addGroupUser(
					group.getGroupId(), user.getUserId());

				Scope scope = Scope.of(group, LocaleUtil.getDefault());

				com.liferay.object.rest.dto.v1_0.FileEntry fileEntry2 =
					_toFileEntry(
						Base64::encode, DLTestUtil.randomTextFileBytes(),
						RandomTestUtil.randomString() + ".txt", null, null,
						ContentTypes.TEXT_PLAIN);

				fileEntry2.setExternalReferenceCode(
					RandomTestUtil.randomString());
				fileEntry2.setScope(scope);

				JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
						jsonFactory.createJSONObject(fileEntry2.toString())
					).toString(),
					endpoint, Http.Method.PATCH);

				dlFileEntry =
					_dlFileEntryLocalService.
						fetchDLFileEntryByExternalReferenceCode(
							fileEntry2.getExternalReferenceCode(),
							group.getGroupId());

				Assert.assertEquals(user.getUserId(), dlFileEntry.getUserId());

				_assertAttachmentJSONObject(
					dlFileEntry, null,
					jsonObject.getJSONObject(
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1),
					jsonFactory.createJSONObject(scope.toString()));

				userLocalService.deleteGroupUser(
					group.getGroupId(), user.getUserId());

				// User who cannot add documents to the site

				com.liferay.object.rest.dto.v1_0.FileEntry fileEntry3 =
					_toFileEntry(
						Base64::encode, DLTestUtil.randomTextFileBytes(),
						RandomTestUtil.randomString() + ".txt", null, null,
						ContentTypes.TEXT_PLAIN);

				fileEntry3.setExternalReferenceCode(
					RandomTestUtil.randomString());
				fileEntry3.setScope(scope);

				jsonObject = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
						jsonFactory.createJSONObject(fileEntry3.toString())
					).toString(),
					endpoint, Http.Method.PATCH);

				Assert.assertEquals(
					"FORBIDDEN", jsonObject.getString("status"));

				Assert.assertNull(
					_dlFileEntryLocalService.
						fetchDLFileEntryByExternalReferenceCode(
							fileEntry3.getExternalReferenceCode(),
							group.getGroupId()));
			}
		);
	}

	@Test
	public void testPatchObjectEntryWithAttachmentObjectFieldId()
		throws Exception {

		_testPatchObjectEntryWithAttachmentObjectField(String::valueOf);
		_testPatchObjectEntryWithAttachmentObjectField(
			fileEntryId -> JSONUtil.put("id", fileEntryId));
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
	public void testPatchObjectEntryWithKeywords() throws Exception {
		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value"
			).put(
				"keywords", JSONUtil.putAll("tag1", "tag2")
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"keywords", JSONUtil.putAll("tag1", "tag2", "tag3")
			).toString(),
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.PATCH);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.GET);

		JSONArray keywordsJSONArray = jsonObject.getJSONArray("keywords");

		Assert.assertEquals("tag1", keywordsJSONArray.get(0));
		Assert.assertEquals("tag2", keywordsJSONArray.get(1));
		Assert.assertEquals("tag3", keywordsJSONArray.get(2));
	}

	@Test
	@TestInfo("LPD-79894")
	public void testPatchObjectEntryWithKeywordsAndExistingAttachmentObjectField()
		throws Exception {

		JSONObject objectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_toFileEntryJSONObject(
					DLTestUtil.randomTextFileBytes(),
					RandomTestUtil.randomString() + ".txt", null,
					ContentTypes.TEXT_PLAIN,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)
			).toString(),
			getEndpoint(getObjectDefinition1(), testGroupId), Http.Method.POST);

		Assert.assertNotNull(
			objectEntryJSONObject.get(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ObjectMapperUtil.class.getName(), LoggerTestUtil.WARN)) {

			JSONObject patchedJSONObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"keywords", JSONUtil.putAll("tag1")
				).toString(),
				getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
					objectEntryJSONObject.getString("id"),
				Http.Method.PATCH);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());

			Assert.assertNotNull(
				patchedJSONObject.get(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE));

			JSONObject attachmentJSONObject =
				objectEntryJSONObject.getJSONObject(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);
			JSONObject patchedAttachmentJSONObject =
				patchedJSONObject.getJSONObject(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

			Assert.assertEquals(
				attachmentJSONObject.getLong("id"),
				patchedAttachmentJSONObject.getLong("id"));

			JSONArray keywordsJSONArray = patchedJSONObject.getJSONArray(
				"keywords");

			Assert.assertEquals("tag1", keywordsJSONArray.get(0));
			Assert.assertEquals(1, keywordsJSONArray.length());
		}
	}

	@Test
	public void testPatchObjectEntryWithTaxonomyCategories() throws Exception {
		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), company.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory taxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());
		TaxonomyCategory taxonomyCategory2 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value"
			).put(
				"taxonomyCategoryIds",
				JSONUtil.putAll(
					taxonomyCategory1.getId(), taxonomyCategory2.getId())
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		TaxonomyCategory taxonomyCategory3 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"taxonomyCategoryIds",
				JSONUtil.putAll(
					taxonomyCategory1.getId(), taxonomyCategory2.getId(),
					taxonomyCategory3.getId())
			).toString(),
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.PATCH);

		_assertTaxonomyCategories(
			jsonObject.getLong("scopeId"),
			jsonObject.getJSONArray("taxonomyCategoryBriefs"), false,
			taxonomyCategory1, taxonomyCategory2, taxonomyCategory3);
	}

	@Test
	@TestInfo("LPD-110103")
	public void testPatchPostPutCustomObjectEntryWithPermissions()
		throws Exception {

		// Invalid permissions

		JSONArray invalidPermissionsJSONArray = JSONUtil.putAll(
			getPermissionsJSONObject(
				new String[] {ActionKeys.DELETE},
				RandomTestUtil.randomString()));

		assertNotFound(
			_postCustomObjectEntryWithPermissions(
				true, invalidPermissionsJSONArray));
		assertNotFound(
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true,
				_postCustomObjectEntryWithPermissions(true, null),
				invalidPermissionsJSONArray));
		assertNotFound(
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PUT, true,
				_postCustomObjectEntryWithPermissions(true, null),
				invalidPermissionsJSONArray));
		assertNotFound(
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true,
				_postCustomObjectEntryWithPermissions(true, null),
				invalidPermissionsJSONArray));
		assertNotFound(
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PUT, true,
				_postCustomObjectEntryWithPermissions(true, null),
				invalidPermissionsJSONArray));

		// No permissions in the body request

		JSONObject objectEntryJSONObject =
			_postCustomObjectEntryWithPermissions(true, null);

		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getOwnerPermissionsJSONObject(getObjectDefinition1())),
			objectEntryJSONObject);
		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getOwnerPermissionsJSONObject(getObjectDefinition1())),
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject, null));
		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getOwnerPermissionsJSONObject(getObjectDefinition1())),
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject, null));
		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getOwnerPermissionsJSONObject(getObjectDefinition1())),
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject, null));
		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getOwnerPermissionsJSONObject(getObjectDefinition1())),
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject, null));

		// Permissions with another object entry ID

		JSONObject otherObjectEntryJSONObject =
			_postCustomObjectEntryWithPermissions(true, null);

		String objectEntryJSON = JSONUtil.put(
			OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
		).put(
			"id", otherObjectEntryJSONObject.getLong("id")
		).put(
			"permissions",
			JSONUtil.putAll(
				getPermissionsJSONObject(new String[0], RoleConstants.OWNER))
		).toString();

		_assertCustomObjectEntryWithPermissions(
			jsonFactory.createJSONArray(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSON,
				getObjectDefinition1().getRESTContextPath() +
					"?nestedFields=permissions",
				Http.Method.POST));

		String byExternalReferenceCodeEndpoint = StringBundler.concat(
			getObjectDefinition1().getRESTContextPath(),
			"/by-external-reference-code/",
			objectEntryJSONObject.getString("externalReferenceCode"),
			"?nestedFields=permissions");

		_assertCustomObjectEntryWithPermissions(
			jsonFactory.createJSONArray(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSON, byExternalReferenceCodeEndpoint,
				Http.Method.PATCH));
		_assertCustomObjectEntryWithPermissions(
			jsonFactory.createJSONArray(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSON, byExternalReferenceCodeEndpoint,
				Http.Method.PUT));

		String endpoint = StringBundler.concat(
			getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
			objectEntryJSONObject.getLong("id"), "?nestedFields=permissions");

		_assertCustomObjectEntryWithPermissions(
			jsonFactory.createJSONArray(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSON, endpoint, Http.Method.PATCH));
		_assertCustomObjectEntryWithPermissions(
			jsonFactory.createJSONArray(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSON, endpoint, Http.Method.PUT));

		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getOwnerPermissionsJSONObject(getObjectDefinition1())),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH, otherObjectEntryJSONObject.getLong("id"),
					"?nestedFields=permissions"),
				Http.Method.GET));

		// Permissions with different roles

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			getObjectDefinition1().getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role1.getRoleId(),
			ActionKeys.DELETE);

		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		objectEntryJSONObject = _postCustomObjectEntryWithPermissions(
			true,
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.PERMISSIONS}, role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					role2.getName())));

		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.PERMISSIONS},
					role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					role2.getName())),
			objectEntryJSONObject);

		Role role3 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.UPDATE},
					role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role3.getName())),
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject,
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.UPDATE}, role1.getName()),
					getPermissionsJSONObject(
						new String[] {ActionKeys.DELETE}, role3.getName()))));
		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.UPDATE},
					role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.VIEW},
					role3.getName())),
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject,
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.UPDATE}, role1.getName()),
					getPermissionsJSONObject(
						new String[] {ActionKeys.DELETE, ActionKeys.VIEW},
						role3.getName()))));
		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.VIEW}, role3.getName())),
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject,
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.VIEW}, role3.getName()))));
		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.UPDATE}, role3.getName())),
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject,
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.UPDATE}, role3.getName()))));

		// Permissions with empty list

		JSONArray companyPermissionsJSONArray = JSONUtil.putAll(
			getPermissionsJSONObject(
				new String[] {ActionKeys.DELETE}, role1.getName()));

		_assertCustomObjectEntryWithPermissions(
			companyPermissionsJSONArray,
			_postCustomObjectEntryWithPermissions(
				true, jsonFactory.createJSONArray()));
		_assertCustomObjectEntryWithPermissions(
			companyPermissionsJSONArray,
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject,
				jsonFactory.createJSONArray()));
		_assertCustomObjectEntryWithPermissions(
			companyPermissionsJSONArray,
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject,
				jsonFactory.createJSONArray()));
		_assertCustomObjectEntryWithPermissions(
			companyPermissionsJSONArray,
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject,
				jsonFactory.createJSONArray()));
		_assertCustomObjectEntryWithPermissions(
			companyPermissionsJSONArray,
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject,
				jsonFactory.createJSONArray()));

		// Permissions without nested fields

		objectEntryJSONObject = _postCustomObjectEntryWithPermissions(
			false, null);

		Assert.assertNull(objectEntryJSONObject.get("permissions"));

		objectEntryJSONObject =
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PATCH, false, objectEntryJSONObject, null);

		Assert.assertNull(objectEntryJSONObject.get("permissions"));

		objectEntryJSONObject =
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PUT, false, objectEntryJSONObject, null);

		Assert.assertNull(objectEntryJSONObject.get("permissions"));

		objectEntryJSONObject = _patchPutCustomObjectEntryWithPermissions(
			Http.Method.PATCH, false, objectEntryJSONObject, null);

		Assert.assertNull(objectEntryJSONObject.get("permissions"));

		objectEntryJSONObject = _patchPutCustomObjectEntryWithPermissions(
			Http.Method.PUT, false, objectEntryJSONObject, null);

		Assert.assertNull(objectEntryJSONObject.get("permissions"));
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
	public void testPatchPutCustomObjectEntryByExternalReferenceCodeWithAttachmentObjectField()
		throws Exception {

		_testPatchPutCustomObjectEntryWithAttachmentField(
			Http.Method.PATCH, getObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			Http.Method.PUT, getObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			Http.Method.PATCH, getSiteScopedObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAttachmentField(
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
	public void testPatchPutCustomObjectEntryWithAttachmentObjectField()
		throws Exception {

		_testPatchPutCustomObjectEntryWithAttachmentField(
			Http.Method.PATCH, getObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			Http.Method.PUT, getObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			Http.Method.PATCH, getSiteScopedObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAttachmentField(
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
	@TestInfo("LPD-53245")
	public void testPatchScopeScopeKeyByExternalReferenceCode()
		throws Exception {

		Group group = groupLocalService.fetchGroup(testGroupId);

		// Scope key: external reference code

		String externalReferenceCodeEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), group.getExternalReferenceCode());

		assertPatchCustomObjectEntry(
			jsonObject ->
				externalReferenceCodeEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			group.getExternalReferenceCode());

		// Scope key: group ID

		String groupIdEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), testGroupId);

		assertPatchCustomObjectEntry(
			jsonObject ->
				groupIdEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			testGroupId);

		// Scope key: group key

		String groupKeyEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), group.getGroupKey());

		assertPatchCustomObjectEntry(
			jsonObject ->
				groupKeyEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			group.getGroupKey());
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
	public void testPostCustomObjectEntryWithAttachmentObjectField()
		throws Exception {

		_testPostCustomObjectEntryWithAttachmentObjectField(
			getObjectDefinition1());
		_testPostCustomObjectEntryWithAttachmentObjectField(
			getSiteScopedObjectDefinition1());
	}

	@Test
	@TestInfo("LPD-64453")
	public void testPostCustomObjectEntryWithAttachmentObjectFieldInDifferentCompany()
		throws Exception {

		JSONObject objectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				JSONFactoryUtil.createJSONObject(
					String.valueOf(
						_toFileEntry(
							Base64::encode, DLTestUtil.randomTextFileBytes(),
							RandomTestUtil.randomString() + ".txt", null, null,
							ContentTypes.TEXT_PLAIN)))
			).toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), "?nestedFields=",
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE, ".folder"),
			Http.Method.POST);

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry =
			com.liferay.object.rest.dto.v1_0.FileEntry.toDTO(
				JSONUtil.getValueAsString(
					objectEntryJSONObject,
					"JSONObject/" +
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE));

		Company company = getCompanyByWebId();

		User adminUser = UserTestUtil.getAdminUser(company.getCompanyId());

		String objectDefinitionName = ObjectDefinitionTestUtil.getRandomName();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				objectDefinitionName,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
						ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_ACCEPTED_FILE_EXTENSIONS
							).value(
								"txt"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_FILE_SOURCE
							).value(
								ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
							).value(
								String.valueOf(MAX_FILE_SIZE_VALUE)
							).build()),
						false),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
						ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_ACCEPTED_FILE_EXTENSIONS
							).value(
								"txt"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_FILE_SOURCE
							).value(
								ObjectFieldSettingConstants.
									VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
							).value(
								String.valueOf(MAX_FILE_SIZE_VALUE)
							).build()),
						false),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
						ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_ACCEPTED_FILE_EXTENSIONS
							).value(
								"txt"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_FILE_SOURCE
							).value(
								ObjectFieldSettingConstants.
									VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
							).value(
								String.valueOf(MAX_FILE_SIZE_VALUE)
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_SHOW_FILES_IN_LIBRARY
							).value(
								Boolean.TRUE.toString()
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_STORAGE_DL_FOLDER_PATH
							).value(
								StringPool.SLASH + objectDefinitionName
							).build()),
						false)),
				ObjectDefinitionConstants.SCOPE_COMPANY, adminUser.getUserId());

		try {
			HTTPTestUtil.customize(
			).withBaseURL(
				"http://www.able.com:" + PortalUtil.getPortalServerPort(false)
			).withCredentials(
				"test@www.able.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).apply(
				() -> {
					JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
							JSONFactoryUtil.createJSONObject(
								fileEntry.toString())
						).toString(),
						objectDefinition.getRESTContextPath(),
						Http.Method.POST);

					Assert.assertEquals(
						"NOT_FOUND", jsonObject.getString("status"));
				}
			);

			HTTPTestUtil.customize(
			).withBaseURL(
				"http://www.able.com:" + PortalUtil.getPortalServerPort(false)
			).withCredentials(
				"test@www.able.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).apply(
				() -> {
					JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
							JSONUtil.put("id", RandomTestUtil.randomLong())
						).toString(),
						objectDefinition.getRESTContextPath(),
						Http.Method.POST);

					Assert.assertEquals(
						"BAD_REQUEST", jsonObject.getString("status"));
				}
			);

			HTTPTestUtil.customize(
			).withBaseURL(
				"http://www.able.com:" + PortalUtil.getPortalServerPort(false)
			).withCredentials(
				"test@www.able.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).apply(
				() -> {
					JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
							JSONUtil.put("id", fileEntry.getId())
						).toString(),
						objectDefinition.getRESTContextPath(),
						Http.Method.POST);

					Assert.assertEquals(
						"FORBIDDEN", jsonObject.getString("status"));
				}
			);
		}
		finally {
			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
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

	@Test
	public void testPostCustomObjectEntryWithEmptyNestedCustomObjectEntriesInOneToManyRelationship()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(), JSONFactoryUtil.createJSONArray());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		JSONArray jsonArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(0, jsonArray.length());
	}

	@Test
	public void testPostCustomObjectEntryWithEmptyStringAttachmentObjectField()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ObjectMapperUtil.class.getName(), LoggerTestUtil.WARN)) {

			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					StringPool.BLANK
				).toString(),
				getEndpoint(getObjectDefinition1(), testGroupId),
				Http.Method.POST);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());

			Assert.assertNull(
				jsonObject.get(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE));
		}
	}

	@Test
	public void testPostCustomObjectEntryWithInvalidNestedCustomObjectEntries()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			objectRelationship1 =
				ObjectRelationshipTestUtil.addObjectRelationship(
					getObjectDefinition1(), getObjectDefinition2(),
					TestPropsValues.getUserId(),
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInManyToManyRelationship(
				getObjectDefinition1().getRESTContextPath(),
				objectRelationship1);

			objectRelationship1 =
				ObjectRelationshipTestUtil.addObjectRelationship(
					getObjectDefinition1(), getObjectDefinition2(),
					TestPropsValues.getUserId(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInManyToOneRelationship(
				getObjectDefinition2().getRESTContextPath(),
				objectRelationship1);

			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInOneToManyRelationship(
				getObjectDefinition1().getRESTContextPath(),
				objectRelationship1);
		}
	}

	@Test
	public void testPostCustomObjectEntryWithLargeAttachmentObjectField()
		throws Exception {

		String attachmentFieldName = "x" + RandomTestUtil.randomString();

		ObjectField objectField = ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
			ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
			attachmentFieldName, attachmentFieldName,
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS
				).value(
					"txt"
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_FILE_SOURCE
				).value(
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
				).value(
					String.valueOf(100)
				).build()),
			false);

		objectField.setObjectDefinitionId(
			getObjectDefinition1().getObjectDefinitionId());

		ObjectFieldTestUtil.addCustomObjectField(
			TestPropsValues.getUserId(), objectField);

		byte[] data = new byte[60_000_000];

		Random random = new Random();

		random.nextBytes(data);

		String base64 = Base64.encode(data);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				attachmentFieldName,
				JSONUtil.put(
					"fileBase64", base64
				).put(
					"name", StringUtil.randomString() + ".txt"
				)
			).toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), "?nestedFields=",
				attachmentFieldName, ".fileBase64"),
			Http.Method.POST);

		_assertAttachmentJSONObject(
			_dlFileEntryLocalService.getDLFileEntry(
				testDLFileEntryModelListener.getLastFileEntryId()),
			base64, jsonObject.getJSONObject(attachmentFieldName),
			JSONUtil.put(
				"externalReferenceCode", "L_GLOBAL"
			).put(
				"type", Scope.Type.SITE.getValue()
			));
	}

	@Test
	@TestInfo("LPD-80355")
	public void testPostCustomObjectEntryWithLocalizedAttachmentObjectFieldAndNotLocalizedValue()
		throws Exception {

		ObjectDefinition objectDefinition =
			_createObjectDefinitionWithLocalizedAttachmentField(
				"localizedAttachment", ObjectDefinitionTestUtil.getRandomName(),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		byte[] fileContent = DLTestUtil.randomTextFileBytes();

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry = _toFileEntry(
			Base64::encode, fileContent, RandomTestUtil.randomString() + ".txt",
			null, null, ContentTypes.TEXT_PLAIN);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"localizedAttachment",
				JSONFactoryUtil.createJSONObject(fileEntry.toString())
			).toString(),
			objectDefinition.getRESTContextPath() +
				"?nestedFields=localizedAttachment.fileBase64",
			Http.Method.POST);

		JSONObject localizedAttachmentI18nJSONObject = jsonObject.getJSONObject(
			"localizedAttachment_i18n");

		Assert.assertNotNull(localizedAttachmentI18nJSONObject);

		JSONObject defaultLanguageJSONObject =
			localizedAttachmentI18nJSONObject.getJSONObject("en_US");

		Assert.assertNotNull(defaultLanguageJSONObject);
		Assert.assertEquals(
			Base64.encode(fileContent),
			defaultLanguageJSONObject.getString("fileBase64"));

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
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
	public void testPostCustomObjectEntryWithManyToOneRelationshipPriorities()
		throws Exception {

		// Custom object

		JSONObject customObjectJSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).toString(),
			getObjectDefinition1().getRESTContextPath() +
				"?fields=id,externalReferenceCode," + OBJECT_FIELD_NAME_1,
			Http.Method.POST);

		JSONObject customObjectJSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).toString(),
			getObjectDefinition1().getRESTContextPath() +
				"?fields=id,externalReferenceCode," + OBJECT_FIELD_NAME_1,
			Http.Method.POST);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testPostCustomObjectEntryWithManyToOneRelationshipPriorities(
			customObjectJSONObject1, customObjectJSONObject2,
			JSONFactoryUtil::createJSONObject, objectRelationship1);

		// System object

		JSONObject systemObjectJSONObject1 =
			UserAccountTestUtil.addUserAccountJSONObject(
				systemObjectDefinitionManager,
				HashMapBuilder.<String, Serializable>put(
					OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_1)
				).build());

		JSONObject systemObjectJSONObject2 =
			UserAccountTestUtil.addUserAccountJSONObject(
				systemObjectDefinitionManager,
				HashMapBuilder.<String, Serializable>put(
					OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
				).build());

		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			userSystemObjectDefinition, getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testPostCustomObjectEntryWithManyToOneRelationshipPriorities(
			systemObjectJSONObject1, systemObjectJSONObject2,
			() -> JSONFactoryUtil.createJSONObject(
				UserAccountTestUtil.randomUserAccount(
				).toString()),
			objectRelationship2);
	}

	@Test
	@TestInfo("LPD-55622")
	public void testPostCustomObjectEntryWithNestedCustomObjectEntriesInManyToManyRelationship()
		throws Exception {

		// Company scope

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
				}));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		JSONArray nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		String objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntryId, "?nestedFields=", objectRelationship1.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		// Company-Site scope: with 'scopeKey'

		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getSiteScopedObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship2.getName(),
			_withScopeKey(
				createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1, ERC_VALUE_2},
					OBJECT_FIELD_NAME_2,
					new String[] {
						NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
					}),
				"Guest"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship2.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntryId, "?nestedFields=", objectRelationship2.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship2.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		// Company-Site scope: with empty 'scopeKey'

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship2.getName(),
			_withScopeKey(
				createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1, ERC_VALUE_2},
					OBJECT_FIELD_NAME_2,
					new String[] {
						NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
					}),
				StringPool.BLANK));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Group ID 0 is not valid for scope \"site\""
			).toString(),
			jsonObject.toString(), JSONCompareMode.LENIENT);

		// Company-Site scope: with nonexistent 'scopeKey'

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship2.getName(),
			_withScopeKey(
				createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1, ERC_VALUE_2},
					OBJECT_FIELD_NAME_2,
					new String[] {
						NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
					}),
				RandomTestUtil.randomString()));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Group ID 0 is not valid for scope \"site\""
			).toString(),
			jsonObject.toString(), JSONCompareMode.LENIENT);

		// Company-Site scope: without 'scopeKey'

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship2.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
				}));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				StringBundler.concat(
					"No scope key was provided for the \"",
					getSiteScopedObjectDefinition2().getName(), "\" entry.")
			).toString(),
			jsonObject.toString(), JSONCompareMode.LENIENT);

		// Site scope

		objectRelationship3 = ObjectRelationshipTestUtil.addObjectRelationship(
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship3.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
				}));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
			Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship3.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getSiteScopedObjectDefinition1().getRESTContextPath(),
				StringPool.SLASH, objectEntryId, "?nestedFields=",
				objectRelationship3.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship3.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);
	}

	@Test
	public void testPostCustomObjectEntryWithNestedCustomObjectEntriesInManyToOneRelationship()
		throws Exception {

		// Company scope

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString()));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.replaceLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id",
						""))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);

		String objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition2().getRESTContextPath(), StringPool.SLASH,
				objectEntryId, "?nestedFields=",
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.removeLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id"))),
			Http.Method.GET);

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.removeLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id"))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);

		// Site scope

		objectRelationship7 = ObjectRelationshipTestUtil.addObjectRelationship(
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship7.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString()));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getEndpoint(getSiteScopedObjectDefinition2(), testGroupId),
			Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship7.getName(), "_",
					StringUtil.replaceLast(
						getSiteScopedObjectDefinition1().getPKObjectFieldName(),
						"Id", ""))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);

		objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getSiteScopedObjectDefinition2().getRESTContextPath(),
				StringPool.SLASH, objectEntryId, "?nestedFields=",
				StringBundler.concat(
					"r_", objectRelationship7.getName(), "_",
					StringUtil.removeLast(
						getSiteScopedObjectDefinition1().getPKObjectFieldName(),
						"Id"))),
			Http.Method.GET);

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship7.getName(), "_",
					StringUtil.removeLast(
						getSiteScopedObjectDefinition1().getPKObjectFieldName(),
						"Id"))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);
	}

	@Test
	public void testPostCustomObjectEntryWithNestedCustomObjectEntriesInOneToManyRelationship()
		throws Exception {

		// Company scope

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
				}));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		JSONArray nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		String objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntryId, "?nestedFields=", objectRelationship1.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		// Site scope

		objectRelationship7 = ObjectRelationshipTestUtil.addObjectRelationship(
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectEntryJSONObject = JSONUtil.put(
			objectRelationship7.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
				}));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
			Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship7.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);

		objectEntryId = jsonObject.getString("id");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getSiteScopedObjectDefinition1().getRESTContextPath(),
				StringPool.SLASH, objectEntryId, "?nestedFields=",
				objectRelationship7.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship7.getName());

		Assert.assertEquals(2, nestedObjectEntriesJSONArray.length());

		_assertEquals(nestedObjectEntriesJSONArray);
	}

	@Test
	public void testPostCustomObjectEntryWithNonexistentRelatedCustomObjectEntryId()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectField objectField = objectFieldLocalService.fetchObjectField(
			objectRelationship1.getObjectFieldId2());

		JSONObject objectEntryJSONObject = JSONUtil.put(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
		).put(
			StringBundler.concat(
				"r_", objectRelationship1.getName(), "_",
				getObjectDefinition1().getPKObjectFieldName()),
			StringPool.BLANK
		);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		long randomObjectEntryId = RandomTestUtil.randomLong();

		objectEntryJSONObject = JSONUtil.put(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
		).put(
			StringBundler.concat(
				"r_", objectRelationship1.getName(), "_",
				getObjectDefinition1().getPKObjectFieldName()),
			randomObjectEntryId
		);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "NOT_FOUND"
			).put(
				"title",
				String.format(
					"The value for %s does not exist.", objectField.getName())
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSONObject.toString(),
				getObjectDefinition2().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				userSystemObjectDefinition, getObjectDefinition2(),
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectField = objectFieldLocalService.fetchObjectField(
			objectRelationship.getObjectFieldId2());

		objectEntryJSONObject = JSONUtil.put(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
		).put(
			StringBundler.concat(
				"r_", objectRelationship.getName(), "_",
				userSystemObjectDefinition.getPKObjectFieldName()),
			randomObjectEntryId
		);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "NOT_FOUND"
			).put(
				"title",
				String.format(
					"The value for %s does not exist.", objectField.getName())
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSONObject.toString(),
				getObjectDefinition2().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
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

	@Test
	public void testPostObjectEntryWithKeywordsAndTaxonomyCategoryIdsWhenCategorizationDisabled()
		throws Exception {

		// TODO LPS-173383 Add test coverage for invalid state transitions

		getObjectDefinition1().setEnableCategorization(false);

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition1());

		try {
			TaxonomyCategory taxonomyCategory =
				postTaxonomyVocabularyTaxonomyCategory();

			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"keywords", JSONUtil.putAll("tag")
				).put(
					"taxonomyCategoryIds",
					JSONUtil.putAll(taxonomyCategory.getId())
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

			Assert.assertFalse(jsonObject.has("keywords"));
			Assert.assertFalse(jsonObject.has("taxonomyCategoryBriefs"));

			AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
				getObjectDefinition1().getClassName(), jsonObject.getInt("id"));

			List<AssetTag> assetEntryAssetTags =
				AssetTagLocalServiceUtil.getAssetEntryAssetTags(
					assetEntry.getEntryId());

			Assert.assertEquals(
				assetEntryAssetTags.toString(), 0, assetEntryAssetTags.size());

			List<AssetCategory> assetCategories =
				AssetCategoryLocalServiceUtil.getCategories(
					getObjectDefinition1().getClassName(),
					jsonObject.getInt("id"));

			Assert.assertEquals(
				assetCategories.toString(), 0, assetCategories.size());
		}
		finally {
			getObjectDefinition1().setEnableCategorization(true);

			objectDefinition1 =
				objectDefinitionLocalService.updateObjectDefinition(
					getObjectDefinition1());
		}
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
	public void testPostObjectEntryWithTaxonomyCategories() throws Exception {
		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), company.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		long classNameId = classNameLocalService.getClassNameId(
			getObjectDefinition1().getClassName());

		assetVocabulary.setSettings(
			StringBundler.concat(
				"multiValued=false\nrequiredClassNameIds=", classNameId,
				":0\nselectedClassNameIds=", classNameId, ":0"));

		assetVocabulary = assetVocabularyLocalService.updateAssetVocabulary(
			assetVocabulary);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				String.format(
					"Please select at least one category for <em>%s</em>.",
					assetVocabulary.getName())
			).put(
				"type", "AssetCategoryException"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		TaxonomyCategory taxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());
		TaxonomyCategory taxonomyCategory2 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				String.format(
					"You cannot select more than one category for <em>%s</em>.",
					assetVocabulary.getName())
			).put(
				"type", "AssetCategoryException"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.putAll(
						taxonomyCategory1.getId(), taxonomyCategory2.getId())
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"taxonomyCategoryIds",
				JSONUtil.putAll(taxonomyCategory1.getId())
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertNotNull(
			objectEntryLocalService.fetchObjectEntry(jsonObject.getLong("id")));

		assetVocabularyLocalService.deleteVocabulary(assetVocabulary);

		_testPostObjectEntryWithCrossSiteTaxonomyCategories();
	}

	@Test
	public void testPostRelatedObjectEntryInDifferentCompany()
		throws Exception {

		ObjectRelationship objectRelationship1 = _addObjectRelationship(
			TestPropsValues.getCompanyId());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinitionLocalService.getObjectDefinition(
				objectRelationship1.getObjectDefinitionId1()),
			OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString());

		Company company = getCompanyByWebId();

		ObjectRelationship objectRelationship2 = _addObjectRelationship(
			company.getCompanyId());

		HTTPTestUtil.customize(
		).withBaseURL(
			"http://www.able.com:" + PortalUtil.getPortalServerPort(false)
		).withCredentials(
			"test@www.able.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).apply(
			() -> {
				ObjectDefinition objectDefinition =
					objectDefinitionLocalService.getObjectDefinition(
						objectRelationship2.getObjectDefinitionId2());

				ObjectField objectField =
					objectFieldLocalService.getObjectField(
						objectRelationship2.getObjectFieldId2());

				Assert.assertEquals(
					400,
					HTTPTestUtil.invokeToHttpCode(
						JSONUtil.put(
							objectField.getName(),
							objectEntry.getObjectEntryId()
						).toString(),
						objectDefinition.getRESTContextPath(),
						Http.Method.POST));
			}
		);
	}

	@Test
	public void testPostScopeScopeKey() throws Exception {
		Group group1 = groupLocalService.fetchGroup(testGroupId);
		Group group2 = GroupTestUtil.addGroup();

		_testPostScopeScopeKey(
			group1.getExternalReferenceCode(),
			group2.getExternalReferenceCode());
		_testPostScopeScopeKey(group1.getGroupId(), group2.getGroupId());
		_testPostScopeScopeKey(group1.getGroupKey(), group2.getGroupKey());
	}

	@Test
	public void testPostSiteScopedCustomObjectEntryInUserGroup()
		throws Exception {

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(
				getSiteScopedObjectDefinition1(), userGroup.getGroupId()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).toString(),
			getEndpoint(
				getSiteScopedObjectDefinition1(), userGroup.getGroupId()),
			Http.Method.POST);

		Assert.assertEquals(
			OBJECT_FIELD_VALUE_1, jsonObject.getInt(OBJECT_FIELD_NAME_1));
	}

	@Test
	public void testPutByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCodeWithSlash()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a/b"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "c"
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "c/d"
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		// Slash on the left side

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						getObjectDefinition1().getRESTContextPath(),
						"/by-external-reference-code/a/b/",
						objectRelationship1.getName(), "/c"),
					Http.Method.PUT));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/a%252Fb/",
					objectRelationship1.getName(), "/c"),
				Http.Method.PUT));

		// Slash on the right side

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						getObjectDefinition1().getRESTContextPath(),
						"/by-external-reference-code/a/",
						objectRelationship1.getName(), "/c/d"),
					Http.Method.PUT));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/a/",
					objectRelationship1.getName(), "/c%252Fd"),
				Http.Method.PUT));

		// Slash on the both sides

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						getObjectDefinition1().getRESTContextPath(),
						"/by-external-reference-code/a/b/",
						objectRelationship1.getName(), "/c/d"),
					Http.Method.PUT));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/a%252Fb/",
					objectRelationship1.getName(), "/c%252Fd"),
				Http.Method.PUT));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						getObjectDefinition1().getRESTContextPath(),
						"/by-external-reference-code/a%252Fb/",
						objectRelationship1.getName(), "/c/d"),
					Http.Method.PUT));
			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						getObjectDefinition1().getRESTContextPath(),
						"/by-external-reference-code/a/b/",
						objectRelationship1.getName(), "/c%252Fd"),
					Http.Method.PUT));
		}
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
	public void testPutByExternalReferenceCodeManyToManyRelationship()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/",
				objectEntry1.getExternalReferenceCode(), StringPool.SLASH,
				objectRelationship1.getName(), StringPool.SLASH,
				objectEntry2.getExternalReferenceCode()),
			Http.Method.PUT);

		Assert.assertEquals(
			objectEntry2.getExternalReferenceCode(),
			jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals(
			OBJECT_FIELD_VALUE_2, jsonObject.getInt(OBJECT_FIELD_NAME_2));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition2().getRESTContextPath(),
				"/by-external-reference-code/",
				objectEntry2.getExternalReferenceCode(), StringPool.SLASH,
				objectRelationship1.getName(), StringPool.SLASH,
				objectEntry1.getExternalReferenceCode()),
			Http.Method.PUT);

		Assert.assertEquals(
			objectEntry1.getExternalReferenceCode(),
			jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals(
			OBJECT_FIELD_VALUE_1, jsonObject.getInt(OBJECT_FIELD_NAME_1));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition2().getRESTContextPath(),
				"/by-external-reference-code/",
				objectEntry2.getExternalReferenceCode(), StringPool.SLASH,
				objectRelationship1.getName(), StringPool.SLASH,
				RandomTestUtil.randomString()),
			Http.Method.PUT);

		Assert.assertNull(jsonObject.get("title"));
	}

	@Test
	public void testPutByExternalReferenceCodeManyToManyRelationshipWithSelf()
		throws Exception {

		String objectFieldValue1 = RandomTestUtil.randomString();
		String objectFieldValue2 = RandomTestUtil.randomString();
		String objectFieldValue3 = RandomTestUtil.randomString();

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, objectFieldValue1
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue2
					).put(
						"externalReferenceCode", ERC_VALUE_2
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue3
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/", ERC_VALUE_1),
			Http.Method.PUT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue2
					).put(
						"externalReferenceCode", ERC_VALUE_2
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue3
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).put(
				"totalCount", 2
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH, jsonObject.get("id"), StringPool.SLASH,
					objectRelationship1.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_2
					).put(
						"externalReferenceCode", ERC_VALUE_2
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, _NEW_OBJECT_FIELD_VALUE_3
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/", ERC_VALUE_1),
			Http.Method.PUT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_2
					).put(
						"externalReferenceCode", ERC_VALUE_2
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, _NEW_OBJECT_FIELD_VALUE_3
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH, jsonObject.get("id"), "?nestedFields=",
					objectRelationship1.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testPutByExternalReferenceCodeMultipleManyToManyRelationships()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition3(), getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).put(
					objectRelationship1.getName(),
					createObjectEntriesJSONArray(
						new String[] {RandomTestUtil.randomString()},
						OBJECT_FIELD_NAME_2,
						new String[] {RandomTestUtil.randomString()})
				).put(
					objectRelationship2.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_3},
						RandomTestUtil.randomString(),
						new String[] {RandomTestUtil.randomString()})
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
				).put(
					objectRelationship1.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
						new String[] {NEW_OBJECT_FIELD_VALUE_2})
				).toString(),
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_1),
				Http.Method.PUT));

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put("externalReferenceCode", ERC_VALUE_2))
			).put(
				objectRelationship2.getName(),
				JSONUtil.putAll(
					JSONUtil.put("externalReferenceCode", ERC_VALUE_3))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_1,
					"?nestedFields=", objectRelationship1.getName(), ",",
					objectRelationship2.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPutByExternalReferenceCodeMultipleOneToManyRelationships()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition2(), getObjectDefinition3(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", ERC_VALUE_2
			).put(
				objectRelationship1.getName(),
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				)
			).put(
				objectRelationship2.getName(),
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).put(
						"externalReferenceCode", ERC_VALUE_3
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).put(
						"externalReferenceCode", RandomTestUtil.randomString()
					))
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
				).toString(),
				StringBundler.concat(
					getObjectDefinition2().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_2),
				Http.Method.PUT));

		String objectEntryId = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/", ERC_VALUE_1),
			Http.Method.GET
		).get(
			"id"
		).toString();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put("externalReferenceCode", ERC_VALUE_2))
			).put(
				"lastPage", 1
			).put(
				"page", 1
			).put(
				"pageSize", 20
			).put(
				"totalCount", 1
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH, objectEntryId, StringPool.SLASH,
					objectRelationship1.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
				).put(
					objectRelationship1.getName(),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
					).put(
						"externalReferenceCode", ERC_VALUE_1
					)
				).put(
					objectRelationship2.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_3}, OBJECT_FIELD_NAME_3,
						new String[] {_NEW_OBJECT_FIELD_VALUE_3})
				).toString(),
				StringBundler.concat(
					getObjectDefinition2().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_2),
				Http.Method.PUT));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
					).put(
						"externalReferenceCode", ERC_VALUE_2
					))
			).put(
				"lastPage", 1
			).put(
				"page", 1
			).put(
				"pageSize", 20
			).put(
				"totalCount", 1
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH, objectEntryId, StringPool.SLASH,
					objectRelationship1.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, _NEW_OBJECT_FIELD_VALUE_3
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).put(
				"lastPage", 1
			).put(
				"page", 1
			).put(
				"pageSize", 20
			).put(
				"totalCount", 1
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition2().getRESTContextPath(),
					StringPool.SLASH, jsonObject.get("id"), StringPool.SLASH,
					objectRelationship2.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
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
	public void testPutByExternalReferenceCodeWithNonexistentValueOneToManyRelationship()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
				).put(
					objectRelationship1.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
						new String[] {NEW_OBJECT_FIELD_VALUE_2})
				).toString(),
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_1),
				Http.Method.PUT));

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put("externalReferenceCode", ERC_VALUE_2))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_1,
					"?nestedFields=", objectRelationship1.getName()),
				Http.Method.GET
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

	@Test
	public void testPutCustomObjectEntryPermissionsPage() throws Exception {

		// Invalid permissions

		JSONObject objectEntryJSONObject =
			_postCustomObjectEntryWithPermissions(true, null);

		JSONObject invalidPermissionJSONObject =
			_putCustomObjectEntryPermissionsPage(
				objectEntryJSONObject.getLong("id"),
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.DELETE},
						RandomTestUtil.randomString())));

		Assert.assertEquals(
			"NOT_FOUND", invalidPermissionJSONObject.getString("status"));

		// Permissions with different roles

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			getObjectDefinition1().getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role1.getRoleId(),
			ActionKeys.DELETE);

		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		objectEntryJSONObject = _postCustomObjectEntryWithPermissions(
			true,
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.PERMISSIONS}, role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					role2.getName())));

		Role role3 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.UPDATE},
					role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role3.getName())),
			_putCustomObjectEntryPermissionsPage(
				objectEntryJSONObject.getLong("id"),
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.UPDATE}, role1.getName()),
					getPermissionsJSONObject(
						new String[] {ActionKeys.DELETE}, role3.getName()))));
		_assertCustomObjectEntryWithPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.VIEW}, role3.getName())),
			_putCustomObjectEntryPermissionsPage(
				objectEntryJSONObject.getLong("id"),
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.VIEW}, role3.getName()))));

		// Permissions with empty list

		JSONArray companyPermissionsJSONArray = JSONUtil.putAll(
			getPermissionsJSONObject(
				new String[] {ActionKeys.DELETE}, role1.getName()));

		_assertCustomObjectEntryWithPermissions(
			companyPermissionsJSONArray,
			_putCustomObjectEntryPermissionsPage(
				objectEntryJSONObject.getLong("id"),
				jsonFactory.createJSONArray()));
	}

	@Test
	@TestInfo("LPD-53919")
	public void testPutCustomObjectEntryUnlinkNestedCustomObjectEntries()
		throws Exception {

		// Many to many

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, objectDefinition1, getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		ObjectDefinition siteScopedObjectDefinition1 =
			getSiteScopedObjectDefinition1();

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				siteScopedObjectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		// Many to one

		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, objectDefinition1, getObjectDefinition2());
		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				siteScopedObjectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2());

		// One to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, objectDefinition1, getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				siteScopedObjectDefinition1.getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			Http.Method.PUT, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	@TestInfo("LPD-53919")
	public void testPutCustomObjectEntryUnlinkNestedCustomObjectEntriesByExternalReferenceCode()
		throws Exception {

		// Many to many

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, objectDefinition1, getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		ObjectDefinition siteScopedObjectDefinition1 =
			getSiteScopedObjectDefinition1();

		String externalReferenceCodeEndpoint = getEndpoint(
			siteScopedObjectDefinition1, testGroupId);

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				externalReferenceCodeEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		// Many to one

		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, objectDefinition1, getObjectDefinition2());
		_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
			jsonObject ->
				externalReferenceCodeEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2());

		// One to many

		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				objectDefinition1.getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, objectDefinition1, getObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
			jsonObject ->
				externalReferenceCodeEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			Http.Method.PUT, siteScopedObjectDefinition1,
			getSiteScopedObjectDefinition2(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testPutCustomObjectEntryWithLocalizedAttachmentObjectField()
		throws Exception {

		// File with a nonexistent external reference code

		ObjectDefinition objectDefinition =
			_createObjectDefinitionWithLocalizedAttachmentField(
				"localizedAttachment", ObjectDefinitionTestUtil.getRandomName(),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			String.valueOf(jsonFactory.createJSONObject()),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		byte[] fileContent1 = DLTestUtil.randomTextFileBytes();

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry1 = _toFileEntry(
			Base64::encode, fileContent1,
			RandomTestUtil.randomString() + ".txt", null, null,
			ContentTypes.TEXT_PLAIN);

		fileEntry1.setExternalReferenceCode(RandomTestUtil.randomString());

		Scope scope = Scope.of(group, LocaleUtil.getDefault());

		fileEntry1.setScope(scope);

		byte[] fileContent2 = DLTestUtil.randomTextFileBytes();

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry2 = _toFileEntry(
			Base64::encode, fileContent2,
			RandomTestUtil.randomString() + ".txt", null, null,
			ContentTypes.TEXT_PLAIN);

		fileEntry2.setExternalReferenceCode(RandomTestUtil.randomString());
		fileEntry2.setScope(scope);

		String endpoint = StringBundler.concat(
			objectDefinition.getRESTContextPath(), StringPool.SLASH,
			jsonObject.get("id"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"localizedAttachment_i18n",
				JSONUtil.put(
					"en_US",
					JSONFactoryUtil.createJSONObject(fileEntry1.toString())
				).put(
					"pt_BR",
					JSONFactoryUtil.createJSONObject(fileEntry2.toString())
				)
			).toString(),
			endpoint + "?nestedFields=localizedAttachment.fileBase64",
			Http.Method.PUT);

		JSONObject localizedAttachmentI18nValuesJSONObject =
			jsonObject.getJSONObject("localizedAttachment_i18n");

		_assertAttachmentJSONObject(
			null, Base64.encode(fileContent1),
			localizedAttachmentI18nValuesJSONObject.getJSONObject("en_US"),
			jsonFactory.createJSONObject(scope.toString()));
		_assertAttachmentJSONObject(
			null, Base64.encode(fileContent2),
			localizedAttachmentI18nValuesJSONObject.getJSONObject("pt_BR"),
			jsonFactory.createJSONObject(scope.toString()));

		// File with an existing external reference code

		com.liferay.portal.kernel.repository.model.Folder existingFolder =
			_dlAppLocalService.addFolder(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		DLFileEntry existingDLFileEntry = _addDLFileEntry(
			new byte[0], existingFolder.getFolderId());

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry3 = _toFileEntry(
			content -> null, new byte[0], existingDLFileEntry.getFileName(),
			null, testGroupId, existingDLFileEntry.getMimeType());

		fileEntry3.setExternalReferenceCode(
			existingDLFileEntry.getExternalReferenceCode());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"localizedAttachment_i18n",
				JSONUtil.put(
					"en_US",
					JSONFactoryUtil.createJSONObject(fileEntry3.toString())
				).put(
					"pt_BR",
					JSONFactoryUtil.createJSONObject(fileEntry3.toString())
				)
			).toString(),
			endpoint, Http.Method.PUT);

		localizedAttachmentI18nValuesJSONObject = jsonObject.getJSONObject(
			"localizedAttachment_i18n");

		JSONObject fileEntryJSONObject =
			localizedAttachmentI18nValuesJSONObject.getJSONObject("en_US");

		Assert.assertEquals(
			fileEntry3.getExternalReferenceCode(),
			fileEntryJSONObject.getString("externalReferenceCode"));

		fileEntryJSONObject =
			localizedAttachmentI18nValuesJSONObject.getJSONObject("pt_BR");

		Assert.assertEquals(
			fileEntry3.getExternalReferenceCode(),
			fileEntryJSONObject.getString("externalReferenceCode"));

		// Invalid format

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"The value is invalid for object field \"" +
					"localizedAttachment_i18n\""
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"localizedAttachment_i18n", RandomTestUtil.randomLong()
				).toString(),
				endpoint, Http.Method.PUT
			).toString(),
			JSONCompareMode.STRICT);

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testPutCustomObjectEntryWithNestedCustomObjectEntriesByExternalReferenceCode()
		throws Exception {

		// Many to many

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testPutCustomObjectEntryWithNestedCustomObjectEntriesByExternalReferenceCode(
			false);

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// Many to one

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition2(), getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testPutCustomObjectEntryWithNestedCustomObjectEntriesByExternalReferenceCode(
			true);

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);

		// One to many

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testPutCustomObjectEntryWithNestedCustomObjectEntriesByExternalReferenceCode(
			false);
	}

	@Test
	public void testPutCustomObjectEntryWithNestedCustomObjectEntriesInManyToManyRelationship()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				}));

		HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONObject newObjectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1}, OBJECT_FIELD_NAME_2,
				new String[] {NEW_OBJECT_FIELD_VALUE_1}));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			newObjectEntryJSONObject.toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntry1.getPrimaryKey()),
			Http.Method.PUT);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		JSONArray nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(1, nestedObjectEntriesJSONArray.length());

		_assertObjectEntryField(
			(JSONObject)nestedObjectEntriesJSONArray.get(0),
			OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntry1.getPrimaryKey(), "?nestedFields=",
				objectRelationship1.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(1, nestedObjectEntriesJSONArray.length());

		_assertObjectEntryField(
			(JSONObject)nestedObjectEntriesJSONArray.get(0),
			OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1);
	}

	@Test
	public void testPutCustomObjectEntryWithNestedCustomObjectEntriesInManyToOneRelationship()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString()));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		String objectEntryId = jsonObject.getString("id");

		JSONObject newObjectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString()));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			newObjectEntryJSONObject.toString(),
			StringBundler.concat(
				getObjectDefinition2().getRESTContextPath(), StringPool.SLASH,
				objectEntryId),
			Http.Method.PUT);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.replaceLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id",
						""))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition2().getRESTContextPath(), StringPool.SLASH,
				objectEntryId, "?nestedFields=",
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.removeLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id"))),
			Http.Method.GET);

		_assertObjectEntryField(
			jsonObject.getJSONObject(
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_",
					StringUtil.removeLast(
						getObjectDefinition1().getPKObjectFieldName(), "Id"))),
			OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1);
	}

	@Test
	public void testPutCustomObjectEntryWithNestedCustomObjectEntriesInOneToManyRelationship()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
				new String[] {
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				}));

		HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONObject newObjectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1}, OBJECT_FIELD_NAME_2,
				new String[] {NEW_OBJECT_FIELD_VALUE_1}));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			newObjectEntryJSONObject.toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntry1.getPrimaryKey()),
			Http.Method.PUT);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		JSONArray nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(1, nestedObjectEntriesJSONArray.length());

		_assertObjectEntryField(
			(JSONObject)nestedObjectEntriesJSONArray.get(0),
			OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntry1.getPrimaryKey(), "?nestedFields=",
				objectRelationship1.getName()),
			Http.Method.GET);

		nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(1, nestedObjectEntriesJSONArray.length());

		_assertObjectEntryField(
			(JSONObject)nestedObjectEntriesJSONArray.get(0),
			OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1);
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPutScopeScopeKeyByExternalReferenceCode() throws Exception {
		Group group = groupLocalService.fetchGroup(testGroupId);

		// Scope key: external reference code

		String externalReferenceCodeEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), group.getExternalReferenceCode());

		assertPutCustomObjectEntry(
			jsonObject ->
				externalReferenceCodeEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			group.getExternalReferenceCode());

		// Scope key: group ID

		String groupIdEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), testGroupId);

		assertPutCustomObjectEntry(
			jsonObject ->
				groupIdEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			testGroupId);

		// Scope key: group key

		String groupKeyEndpoint = getEndpoint(
			getSiteScopedObjectDefinition1(), group.getGroupKey());

		assertPutCustomObjectEntry(
			jsonObject ->
				groupKeyEndpoint + "/by-external-reference-code/" +
					jsonObject.getString("externalReferenceCode"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			group.getGroupKey());
	}

	@Test
	public void testPutSiteScopedCustomObjectEntryWithNestedCustomObjectEntries()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject postJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				objectRelationship1.getName(),
				createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1, ERC_VALUE_2},
					OBJECT_FIELD_NAME_1,
					new String[] {
						NEW_OBJECT_FIELD_VALUE_1, NEW_OBJECT_FIELD_VALUE_2
					})
			).toString(),
			getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
			Http.Method.POST);

		JSONObject putJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship1.getName(),
				createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_3}, OBJECT_FIELD_NAME_2,
					new String[] {NEW_OBJECT_FIELD_VALUE_2})
			).toString(),
			getSiteScopedObjectDefinition1().getRESTContextPath() + "/" +
				postJSONObject.getLong("id"),
			Http.Method.PUT);

		Assert.assertEquals(
			ERC_VALUE_3,
			putJSONObject.getJSONArray(
				objectRelationship1.getName()
			).getJSONObject(
				0
			).getString(
				"externalReferenceCode"
			));
	}

	private DLFileEntry _addDLFileEntry(byte[] content, long folderId)
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(), folderId,
			TempFileEntryUtil.getTempFileName(
				RandomTestUtil.randomString() + ".txt"),
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			new ByteArrayInputStream(content), 0, null, null, null,
			ServiceContextTestUtil.getServiceContext());

		return _dlFileEntryLocalService.getFileEntry(
			fileEntry.getFileEntryId());
	}

	private FileEntry _addFileEntry(
			byte[] content, String fileName, String contentType)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName, contentType,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), content, null,
			null, null, ServiceContextTestUtil.getServiceContext());
	}

	private ObjectAction _addObjectAction(ObjectDefinition objectDefinition)
		throws Exception {

		return _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"secret", "standalone"
			).put(
				"url", "https://standalone.com"
			).build(),
			false);
	}

	private ObjectEntry _addObjectEntry(
			DepotEntry depotEntry, ObjectDefinition objectDefinition,
			ObjectEntryFolder objectEntryFolder, ServiceContext serviceContext,
			long userId)
		throws Exception {

		return objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), userId,
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), "en_US",
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			serviceContext);
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			DepotEntry depotEntry, ServiceContext serviceContext, long userId)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					depotEntry.getGroupId(), depotEntry.getCompanyId());

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			null, depotEntry.getGroupId(), userId,
			objectEntryFolder.getObjectEntryFolderId(),
			RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			RandomTestUtil.randomString(), serviceContext);
	}

	private ObjectRelationship _addObjectRelationship(long companyId)
		throws Exception {

		User user = UserTestUtil.getAdminUser(companyId);

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						OBJECT_FIELD_NAME_TEXT
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY, user.getUserId());
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						OBJECT_FIELD_NAME_TEXT
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY, user.getUserId());

		return ObjectRelationshipTestUtil.addObjectRelationship(
			objectDefinition1, objectDefinition2, user.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	private void _assertAttachmentJSONObject(
			DLFileEntry dlFileEntry, String fileBase64, JSONObject jsonObject,
			JSONObject scopeJSONObject)
		throws Exception {

		if (dlFileEntry != null) {
			DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

			Assert.assertEquals(
				dlFileVersion.getDescription(),
				jsonObject.getString("alternativeText"));

			Assert.assertEquals(
				dlFileEntry.getExternalReferenceCode(),
				jsonObject.getString("externalReferenceCode"));
			Assert.assertEquals(
				dlFileEntry.getFileEntryId(), jsonObject.getLong("id"));
			Assert.assertEquals(
				dlFileEntry.getMimeType(), jsonObject.getString("mimeType"));
			Assert.assertEquals(
				dlFileEntry.getFileName(), jsonObject.getString("name"));
		}
		else {
			Assert.assertNotNull(jsonObject.get("externalReferenceCode"));
		}

		Assert.assertEquals(fileBase64, jsonObject.get("fileBase64"));

		if (scopeJSONObject == null) {
			Assert.assertFalse(jsonObject.has("scope"));
		}
		else {
			JSONAssert.assertEquals(
				scopeJSONObject.toString(), jsonObject.getString("scope"),
				JSONCompareMode.LENIENT);
		}
	}

	private void _assertCustomObjectEntryWithPermissions(
			JSONArray expectedPermissionsJSONArray, JSONObject jsonObject)
		throws Exception {

		JSONArray actualPermissionsJSONArray = jsonObject.getJSONArray(
			"permissions");

		if (actualPermissionsJSONArray == null) {
			actualPermissionsJSONArray = jsonObject.getJSONArray("items");
		}

		JSONAssert.assertEquals(
			String.valueOf(expectedPermissionsJSONArray),
			String.valueOf(actualPermissionsJSONArray),
			JSONCompareMode.LENIENT);
	}

	private void _assertEquals(JSONArray nestedObjectEntriesJSONArray)
		throws Exception {

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1
				).put(
					"externalReferenceCode", ERC_VALUE_1
				),
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
				).put(
					"externalReferenceCode", ERC_VALUE_2
				)
			).toString(),
			nestedObjectEntriesJSONArray.toString(), JSONCompareMode.LENIENT);
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

	private void _assertJSONObjectWithAttachmentField(
			JSONObject jsonObject1, JSONObject jsonObject2,
			String objectFieldName)
		throws Exception {

		JSONAssert.assertEquals(
			jsonObject1.toString(), jsonObject2.toString(),
			JSONCompareMode.LENIENT);

		Object object = jsonObject1.get(objectFieldName);

		if (object != null) {
			JSONAssert.assertEquals(
				String.valueOf(object),
				String.valueOf(jsonObject2.get(objectFieldName)),
				JSONCompareMode.STRICT);
		}
	}

	private void _assertObjectEntryField(
		JSONObject objectEntryJSONObject, String objectFieldName,
		String objectFieldValue) {

		int objectEntryId = objectEntryJSONObject.getInt("id");

		ObjectEntry objectEntry = objectEntryLocalService.fetchObjectEntry(
			objectEntryId);

		Assert.assertEquals(
			"_assertObjectEntryField",
			MapUtil.getString(objectEntry.getValues(), objectFieldName),
			objectFieldValue);
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

	private void _assertTaxonomyCategories(
			long groupId, JSONArray jsonArray1,
			boolean withEmbeddedTaxonomyCategory,
			TaxonomyCategory... taxonomyCategories)
		throws Exception {

		JSONArray jsonArray2 = jsonFactory.createJSONArray();

		for (TaxonomyCategory taxonomyCategory : taxonomyCategories) {
			jsonArray2.put(
				_toTaxonomyCategoryJSONObject(
					groupId, taxonomyCategory, withEmbeddedTaxonomyCategory));
		}

		JSONAssert.assertEquals(
			jsonArray2.toString(), jsonArray1.toString(),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	private ObjectDefinition
			_createObjectDefinitionWithLocalizedAttachmentField(
				String attachmentFieldName, String objectDefinitionName,
				String scope)
		throws Exception {

		return ObjectDefinitionTestUtil.publishObjectDefinition(
			objectDefinitionName,
			Collections.singletonList(
				new AttachmentObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(attachmentFieldName)
				).localized(
					true
				).name(
					attachmentFieldName
				).objectFieldSettings(
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS
						).value(
							"txt"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE
						).value(
							ObjectFieldSettingConstants.
								VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
						).value(
							"100"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_SHOW_FILES_IN_LIBRARY
						).value(
							StringPool.TRUE
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_STORAGE_DL_FOLDER_PATH
						).value(
							StringPool.SLASH + objectDefinitionName
						).build())
				).build()),
			scope, TestPropsValues.getUserId());
	}

	private void _enableComments(ObjectDefinition objectDefinition) {
		objectDefinition.setEnableComments(true);

		objectDefinitionLocalService.updateObjectDefinition(objectDefinition);
	}

	private Map<String, String> _getActionValue(String href, String method) {
		return HashMapBuilder.put(
			"href", href
		).put(
			"method", method
		).build();
	}

	private JSONObject _getActionsJSONObject(ObjectDefinition objectDefinition)
		throws Exception {

		JSONObject responseJSONObject = HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition.getRESTContextPath(), Http.Method.GET);

		if ((responseJSONObject == null) ||
			!responseJSONObject.has("actions")) {

			return JSONFactoryUtil.createJSONObject();
		}

		return responseJSONObject.getJSONObject("actions");
	}

	private JSONObject _getActionsJSONObject(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			User user, String password)
		throws Exception {

		AtomicReference<JSONObject> atomicReference = new AtomicReference<>();

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				JSONObject responseJSONObject = HTTPTestUtil.invokeToJSONObject(
					null,
					objectDefinition.getRESTContextPath() + StringPool.SLASH +
						objectEntry.getObjectEntryId(),
					Http.Method.GET);

				atomicReference.set(
					responseJSONObject.getJSONObject("actions"));
			}
		);

		return atomicReference.get();
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

	private DLFolder _getDLFolder(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		Repository repository = _portletFileRepository.getPortletRepository(
			groupId, objectDefinition.getPortletId());

		return _dlFolderLocalService.getFolder(
			repository.getGroupId(), repository.getDlFolderId(),
			String.valueOf(TestPropsValues.getUserId()));
	}

	private DLFolder _getDLFolder(
			ObjectDefinition objectDefinition, boolean showInDocsAndMedia)
		throws Exception {

		long groupId = 0;

		ObjectScopeProvider objectScopeProvider =
			objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware()) {
			groupId = testGroupId;
		}
		else {
			Company company = companyLocalService.getCompany(
				objectDefinition.getCompanyId());

			groupId = company.getGroupId();
		}

		if (showInDocsAndMedia) {
			return _dlFolderLocalService.fetchFolder(
				groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				objectDefinition.getShortName());
		}

		return _getDLFolder(groupId, objectDefinition);
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

	private Map<String, Map<String, String>> _getExpectedActions(
		String externalReferenceCode, ObjectDefinition objectDefinition,
		String objectEntryFolderId, String objectEntryId,
		boolean sharingEnabled) {

		String href = StringBundler.concat(
			"http://localhost:", PortalUtil.getPortalServerPort(false), "/o",
			objectDefinition.getRESTContextPath(), StringPool.SLASH,
			objectEntryId);
		String scopedEndpoint = StringBundler.concat(
			"http://localhost:", PortalUtil.getPortalServerPort(false), "/o",
			objectDefinition.getRESTContextPath(), "/scopes/",
			group.getGroupId());

		return HashMapBuilder.<String, Map<String, String>>put(
			"copy",
			_getActionValue(
				StringBundler.concat(
					href, "/by-object-entry-folder-id/{objectEntryFolderId}",
					"/copy"),
				"POST")
		).put(
			"copy-replace",
			_getActionValue(
				StringBundler.concat(
					href, "/by-object-entry-folder-id/{objectEntryFolderId}",
					"/copy-replace"),
				"POST")
		).put(
			"delete", _getActionValue(href, "DELETE")
		).put(
			"duplicate",
			_getActionValue(
				StringBundler.concat(
					href, "/by-object-entry-folder-id/", objectEntryFolderId,
					"/copy"),
				"POST")
		).put(
			"expire",
			_getActionValue(
				StringBundler.concat(
					scopedEndpoint, "/by-external-reference-code/",
					externalReferenceCode, "/expire"),
				"POST")
		).put(
			"get", _getActionValue(href, "GET")
		).put(
			"get-by-scope", _getActionValue(scopedEndpoint, "GET")
		).put(
			"move",
			_getActionValue(
				StringBundler.concat(
					href, "/by-object-entry-folder-id/{objectEntryFolderId}",
					"/move"),
				"POST")
		).put(
			"move-replace",
			_getActionValue(
				StringBundler.concat(
					href, "/by-object-entry-folder-id/{objectEntryFolderId}",
					"/move-replace"),
				"POST")
		).put(
			"permissions", _getActionValue(href + "/permissions", "GET")
		).put(
			"replace", _getActionValue(href, "PUT")
		).put(
			"share",
			() -> {
				if (sharingEnabled) {
					return _getActionValue(href, "GET");
				}

				return null;
			}
		).put(
			"update", _getActionValue(href, "PATCH")
		).put(
			"versions",
			_getActionValue(
				StringBundler.concat(
					scopedEndpoint, "/by-external-reference-code/",
					externalReferenceCode, "/versions"),
				"GET")
		).build();
	}

	private JSONObject _getFileEntryJSONObject(
			DLFolder dlFolder,
			com.liferay.object.rest.dto.v1_0.FileEntry fileEntry,
			ObjectDefinition objectDefinition, String objectFieldName)
		throws Exception {

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			testDLFileEntryModelListener.getLastFileEntryId());

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			testObjectEntryModelListener.getLastObjectEntryId());

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), objectFieldName);

		JSONObject jsonObject = JSONUtil.put(
			"alternativeText", dlFileVersion.getDescription()
		).put(
			"extension", dlFileEntry::getExtension
		).put(
			"externalReferenceCode", dlFileEntry::getExternalReferenceCode
		).put(
			"id", testDLFileEntryModelListener.getLastFileEntryId()
		).put(
			"link",
			() -> {
				Link link = new Link();

				FileEntry serviceBuilderFileEntry =
					_dlAppLocalService.getFileEntry(
						testDLFileEntryModelListener.getLastFileEntryId());

				FileVersion fileVersion =
					serviceBuilderFileEntry.getFileVersion();

				long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
				long repositoryId = 0;

				if (dlFolder == null) {
					Folder folder = fileEntry.getFolder();

					if (folder == null) {
						Scope scope = fileEntry.getScope();

						if ((scope != null) &&
							(scope.getExternalReferenceCode() != null)) {

							Group group =
								groupLocalService.
									fetchGroupByExternalReferenceCode(
										scope.getExternalReferenceCode(),
										objectDefinition.getCompanyId());

							if (group != null) {
								repositoryId = group.getGroupId();
							}
						}

						if (repositoryId == 0) {
							if (StringUtil.equals(
									objectDefinition.getScope(),
									ObjectDefinitionConstants.SCOPE_SITE)) {

								repositoryId = testGroupId;
							}
							else {
								Company company =
									companyLocalService.getCompany(
										objectDefinition.getCompanyId());

								repositoryId = company.getGroupId();
							}
						}
					}
					else {
						repositoryId = folder.getSiteId();
					}
				}
				else {
					folderId = dlFolder.getFolderId();
					repositoryId = dlFolder.getRepositoryId();
				}

				Date modifiedDate = fileVersion.getModifiedDate();

				String groupExternalReferenceCode = StringPool.BLANK;

				if (!StringUtil.equals(
						objectDefinition.getScope(),
						ObjectDefinitionConstants.SCOPE_COMPANY)) {

					Group group = groupLocalService.fetchGroup(
						objectEntry.getGroupId());

					groupExternalReferenceCode =
						group.getExternalReferenceCode();
				}

				link.setHref(
					StringBundler.concat(
						"/documents/", repositoryId, "/", folderId, "/",
						URLCodec.encodeURL(fileEntry.getName()), "/",
						serviceBuilderFileEntry.getUuid(), "?version=",
						fileVersion.getVersion(), "&t=", modifiedDate.getTime(),
						"&download=true&groupExternalReferenceCode=",
						groupExternalReferenceCode,
						"&objectDefinitionExternalReferenceCode=",
						objectDefinition.getExternalReferenceCode(),
						"&objectEntryExternalReferenceCode=",
						objectEntry.getExternalReferenceCode(),
						"&objectFieldExternalReferenceCode=",
						objectField.getExternalReferenceCode()));

				link.setLabel(fileEntry.getName());

				return JSONFactoryUtil.createJSONObject(link.toString());
			}
		).put(
			"mimeType", fileEntry.getMimeType()
		).put(
			"name", fileEntry.getName()
		).put(
			"scope",
			() -> {
				if (!StringUtil.equals(
						objectDefinition.getScope(),
						ObjectDefinitionConstants.SCOPE_COMPANY) &&
					(dlFileEntry.getGroupId() == objectEntry.getGroupId())) {

					return null;
				}

				Group group = groupLocalService.getGroup(
					dlFileEntry.getGroupId());

				Scope.Type type =
					(group.getType() == GroupConstants.TYPE_DEPOT) ?
						Scope.Type.ASSET_LIBRARY : Scope.Type.SITE;

				Scope scope = Scope.ofReference(
					group.getExternalReferenceCode(), type);

				return JSONFactoryUtil.createJSONObject(scope.toString());
			}
		).put(
			"size",
			LanguageUtil.formatStorageSize(
				dlFileEntry.getSize(), LocaleUtil.getDefault())
		);

		String fileSource = ObjectFieldSettingUtil.getValue(
			ObjectFieldSettingConstants.NAME_FILE_SOURCE, objectField);

		if ((Objects.equals(
				fileSource,
				ObjectFieldSettingConstants.
					VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT) ||
			 Objects.equals(
				 fileSource,
				 ObjectFieldSettingConstants.
					 VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA)) &&
			!GetterUtil.getBoolean(
				ObjectFieldSettingUtil.getValue(
					ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_LIBRARY,
					objectField))) {

			jsonObject.put(
				"fileURL",
				"http://localhost:" + PortalUtil.getPortalServerPort(false));
		}

		return jsonObject;
	}

	private JSONObject _getFileEntryJSONObject(
			DLFolder dlFolder,
			com.liferay.object.rest.dto.v1_0.FileEntry fileEntry,
			ObjectDefinition objectDefinition, String objectFieldName,
			String thumbnailSrc)
		throws Exception {

		JSONObject jsonObject = _getFileEntryJSONObject(
			dlFolder, fileEntry, objectDefinition, objectFieldName);

		return jsonObject.put("thumbnailURL", thumbnailSrc);
	}

	private JSONObject _getObjectEntryFileMetadataJSONObject(
			FileEntry fileEntry)
		throws Exception {

		JSONObject objectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				fileEntry.getFileEntryId()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		RawMetadataProcessorUtil.saveMetadata(fileEntry.getFileVersion());

		JSONObject responseJSONObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), "/",
				objectEntryJSONObject.getString("id"), "?nestedFields=",
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				".metadata"),
			Http.Method.GET);

		JSONObject jsonObject = responseJSONObject.getJSONObject(
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);

		return jsonObject.getJSONObject("metadata");
	}

	private int _getTempFilesCount() {
		File tempFile = FileUtil.createTempFile();

		File parentFile = tempFile.getParentFile();

		return ArrayUtil.getLength(parentFile.listFiles());
	}

	private JSONObject
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method httpMethod, boolean nestedFields,
				JSONObject objectEntryJSONObject,
				JSONArray permissionsJSONArray)
		throws Exception {

		String endpoint =
			getObjectDefinition1().getRESTContextPath() +
				"/by-external-reference-code/" +
					objectEntryJSONObject.getString("externalReferenceCode");

		if (nestedFields) {
			endpoint = endpoint + "?nestedFields=permissions";
		}

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"permissions", permissionsJSONArray
			).toString(),
			endpoint, httpMethod);
	}

	private JSONObject _patchPutCustomObjectEntryWithPermissions(
			Http.Method httpMethod, boolean nestedFields,
			JSONObject objectEntryJSONObject, JSONArray permissionsJSONArray)
		throws Exception {

		String endpoint =
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				objectEntryJSONObject.getLong("id");

		if (nestedFields) {
			endpoint = endpoint + "?nestedFields=permissions";
		}

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"permissions", permissionsJSONArray
			).toString(),
			endpoint, httpMethod);
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

	private JSONObject _postCustomObjectEntryWithPermissions(
			boolean nestedFields, JSONArray permissionsJSONArray)
		throws Exception {

		String endpoint = getObjectDefinition1().getRESTContextPath();

		if (nestedFields) {
			endpoint = endpoint + "?nestedFields=permissions";
		}

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"permissions", permissionsJSONArray
			).toString(),
			endpoint, Http.Method.POST);
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

	private TaxonomyCategory _postTaxonomyCategoryTaxonomyCategory(
			long groupId, String taxonomyCategoryId, long taxonomyVocabularyId)
		throws Exception {

		return taxonomyCategoryResource.postTaxonomyCategoryTaxonomyCategory(
			taxonomyCategoryId,
			randomTaxonomyCategory(groupId, taxonomyVocabularyId));
	}

	private JSONObject _putCustomObjectEntryPermissionsPage(
			long id, JSONArray permissionsJSONArray)
		throws Exception {

		return HTTPTestUtil.invokeToJSONObject(
			permissionsJSONArray.toString(),
			StringBundler.concat(
				getEndpoint(getObjectDefinition1(), testGroupId),
				StringPool.SLASH, id, "/permissions"),
			Http.Method.PUT);
	}

	private CompanyConfigurationTemporarySwapper _swapObjectConfiguration(
			String[] attachmentFileURLHostsAllowed,
			boolean attachmentFileURLLocalNetworkAccessEnabled)
		throws Exception {

		return new CompanyConfigurationTemporarySwapper(
			TestPropsValues.getCompanyId(), ObjectConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"attachmentFileURLHostsAllowed", attachmentFileURLHostsAllowed
			).put(
				"attachmentFileURLLocalNetworkAccessEnabled",
				attachmentFileURLLocalNetworkAccessEnabled
			).build());
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

	private void _testGetObjectEntryActions(boolean sharingEnabled)
		throws Exception {

		JSONObject jsonObject = postObjectEntry(
			group.getGroupId(), getSiteScopedObjectDefinition1());

		jsonObject = getObjectEntryJSONObject(
			getSiteScopedObjectDefinition1(), jsonObject.getString("id"));

		JSONObject actionsJSONObject = jsonObject.getJSONObject("actions");

		Assert.assertEquals(
			_getExpectedActions(
				jsonObject.getString("externalReferenceCode"),
				getSiteScopedObjectDefinition1(),
				jsonObject.getString("objectEntryFolderId"),
				jsonObject.getString("id"), sharingEnabled),
			actionsJSONObject.toMap());
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

	private void _testGetObjectEntryWithAttachmentObjectFieldImageMetadata(
			FileEntry fileEntry, String expectedAspectRatio,
			String expectedResolution)
		throws Exception {

		JSONObject metadataJSONObject = _getObjectEntryFileMetadataJSONObject(
			fileEntry);

		if (expectedAspectRatio != null) {
			Assert.assertEquals(
				expectedAspectRatio,
				metadataJSONObject.getString("aspectRatio"));
		}
		else {
			Assert.assertNull(metadataJSONObject.opt("aspectRatio"));
		}

		if (expectedResolution != null) {
			Assert.assertEquals(
				expectedResolution, metadataJSONObject.getString("resolution"));
		}
		else {
			Assert.assertNull(metadataJSONObject.opt("resolution"));
		}
	}

	private void _testGetObjectEntryWithObjectActions(
			ObjectAction objectAction, ObjectDefinition objectDefinition,
			UnsafeTriConsumer<JSONObject, JSONObject, ObjectAction, Exception>
				unsafeTriConsumer)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value"
			).toString(),
			getEndpoint(objectDefinition, testGroupId), Http.Method.POST);

		JSONObject actionsJSONObject = jsonObject.getJSONObject("actions");

		JSONObject actionJSONObject = actionsJSONObject.getJSONObject(
			objectAction.getName());

		Assert.assertEquals("PUT", actionJSONObject.getString("method"));

		unsafeTriConsumer.accept(actionJSONObject, jsonObject, objectAction);
	}

	private void _testGetObjectEntryWithTaxonomyCategories(
			long groupId, ObjectDefinition objectDefinition,
			TaxonomyCategory... taxonomyCategories)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value"
			).put(
				"taxonomyCategoryIds",
				JSONUtil.toJSONArray(
					taxonomyCategories, TaxonomyCategory::getId)
			).toString(),
			getEndpoint(objectDefinition, groupId), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			objectDefinition.getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.GET);

		_assertTaxonomyCategories(
			jsonObject.getLong("scopeId"),
			jsonObject.getJSONArray("taxonomyCategoryBriefs"), false,
			taxonomyCategories);
	}

	private void _testPatchObjectEntryWithAttachmentObjectField(
			Function<Long, Object> function)
		throws Exception {

		JSONObject objectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_toFileEntryJSONObject(
					DLTestUtil.randomTextFileBytes(),
					RandomTestUtil.randomString() + ".txt", null,
					ContentTypes.TEXT_PLAIN,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)
			).toString(),
			getEndpoint(getObjectDefinition1(), testGroupId), Http.Method.POST);

		JSONObject attachmentJSONObject = objectEntryJSONObject.getJSONObject(
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ObjectMapperUtil.class.getName(), LoggerTestUtil.WARN)) {

			JSONObject patchedJSONObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					function.apply(attachmentJSONObject.getLong("id"))
				).toString(),
				getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
					objectEntryJSONObject.getString("id"),
				Http.Method.PATCH);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());

			JSONObject patchedAttachmentJSONObject =
				patchedJSONObject.getJSONObject(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

			Assert.assertEquals(
				attachmentJSONObject.getLong("id"),
				patchedAttachmentJSONObject.getLong("id"));
		}
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

	private void
			_testPatchPutCustomObjectEntryUnlinkManyToOneNestedCustomObjectEntries(
				Function<JSONObject, String> endpointFunction,
				Http.Method method, ObjectDefinition objectDefinition1,
				ObjectDefinition objectDefinition2)
		throws Exception {

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition2, objectDefinition1,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition2, objectDefinition1,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship3 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition2, objectDefinition1,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship4 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition2, objectDefinition1,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship5 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition2, objectDefinition1,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship6 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition2, objectDefinition1,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		try {
			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					objectRelationship1.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).put(
					objectRelationship2.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).put(
					objectRelationship3.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).put(
					objectRelationship4.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).put(
					objectRelationship5.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).put(
					objectRelationship6.getName(),
					JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).toString())
				).toString(),
				getEndpoint(objectDefinition1, testGroupId), Http.Method.POST);

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					objectRelationship1.getName(),
					JSONFactoryUtil.createJSONObject()
				).put(
					objectRelationship2.getName(), org.json.JSONObject.NULL
				).put(
					StringBundler.concat(
						"r_", objectRelationship3.getName(), "_",
						objectDefinition2.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", objectRelationship4.getName(), "_",
						objectDefinition2.getPKObjectFieldName()),
					org.json.JSONObject.NULL
				).put(
					StringBundler.concat(
						"r_", objectRelationship5.getName(), "_",
						StringUtil.replaceLast(
							objectDefinition2.getPKObjectFieldName(), "Id",
							"ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", objectRelationship6.getName(), "_",
						StringUtil.replaceLast(
							objectDefinition2.getPKObjectFieldName(), "Id",
							"ERC")),
					org.json.JSONObject.NULL
				).toString(),
				endpointFunction.apply(jsonObject), method);

			Assert.assertEquals(
				0,
				jsonObject.getJSONObject(
					"status"
				).get(
					"code"
				));
			Assert.assertNull(
				jsonObject.getJSONObject(objectRelationship1.getName()));
			Assert.assertNull(
				jsonObject.getJSONObject(objectRelationship2.getName()));
			Assert.assertNull(
				jsonObject.getJSONObject(objectRelationship3.getName()));
			Assert.assertNull(
				jsonObject.getJSONObject(objectRelationship4.getName()));
			Assert.assertNull(
				jsonObject.getJSONObject(objectRelationship5.getName()));
			Assert.assertNull(
				jsonObject.getJSONObject(objectRelationship6.getName()));

			JSONAssert.assertEquals(
				JSONUtil.put(
					StringBundler.concat(
						"r_", objectRelationship1.getName(), "_",
						objectDefinition2.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", objectRelationship1.getName(), "_",
						StringUtil.replaceLast(
							objectDefinition2.getPKObjectFieldName(), "Id",
							"ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", objectRelationship2.getName(), "_",
						objectDefinition2.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", objectRelationship2.getName(), "_",
						StringUtil.replaceLast(
							objectDefinition2.getPKObjectFieldName(), "Id",
							"ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", objectRelationship3.getName(), "_",
						objectDefinition2.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", objectRelationship3.getName(), "_",
						StringUtil.replaceLast(
							objectDefinition2.getPKObjectFieldName(), "Id",
							"ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", objectRelationship4.getName(), "_",
						objectDefinition2.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", objectRelationship4.getName(), "_",
						StringUtil.replaceLast(
							objectDefinition2.getPKObjectFieldName(), "Id",
							"ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", objectRelationship5.getName(), "_",
						objectDefinition2.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", objectRelationship5.getName(), "_",
						StringUtil.replaceLast(
							objectDefinition2.getPKObjectFieldName(), "Id",
							"ERC")),
					""
				).put(
					StringBundler.concat(
						"r_", objectRelationship6.getName(), "_",
						objectDefinition2.getPKObjectFieldName()),
					0
				).put(
					StringBundler.concat(
						"r_", objectRelationship6.getName(), "_",
						StringUtil.replaceLast(
							objectDefinition2.getPKObjectFieldName(), "Id",
							"ERC")),
					""
				).toString(),
				jsonObject.toString(), JSONCompareMode.LENIENT);
		}
		finally {
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship1);
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship2);
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship3);
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship4);
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship5);
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship6);
		}
	}

	private void
			_testPatchPutCustomObjectEntryUnlinkXToManyNestedCustomObjectEntries(
				Function<JSONObject, String> endpointFunction,
				Http.Method method, ObjectDefinition objectDefinition1,
				ObjectDefinition objectDefinition2, String type)
		throws Exception {

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, objectDefinition2,
				TestPropsValues.getUserId(), type);
		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, objectDefinition2,
				TestPropsValues.getUserId(), type);

		try {
			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					objectRelationship1.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_1, ERC_VALUE_2},
						OBJECT_FIELD_NAME_2,
						new String[] {
							RandomTestUtil.randomString(),
							RandomTestUtil.randomString()
						})
				).put(
					objectRelationship2.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_1, ERC_VALUE_2},
						OBJECT_FIELD_NAME_2,
						new String[] {
							RandomTestUtil.randomString(),
							RandomTestUtil.randomString()
						})
				).toString(),
				getEndpoint(objectDefinition1, testGroupId), Http.Method.POST);

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					objectRelationship1.getName(),
					JSONFactoryUtil.createJSONArray()
				).put(
					objectRelationship2.getName(), org.json.JSONObject.NULL
				).toString(),
				endpointFunction.apply(jsonObject), method);

			Assert.assertEquals(
				0,
				jsonObject.getJSONObject(
					"status"
				).get(
					"code"
				));

			JSONArray nestedObjectEntriesJSONArray1 = jsonObject.getJSONArray(
				objectRelationship1.getName());

			Assert.assertEquals(0, nestedObjectEntriesJSONArray1.length());

			JSONArray nestedObjectEntriesJSONArray2 = jsonObject.getJSONArray(
				objectRelationship2.getName());

			Assert.assertEquals(0, nestedObjectEntriesJSONArray2.length());
		}
		finally {
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship1);
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship2);
		}
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

	private void _testPatchPutCustomObjectEntryWithAttachmentField(
			Http.Method httpMethod, ObjectDefinition objectDefinition,
			boolean useExternalReferenceCode)
		throws Exception {

		// CMS basic document source, file from Base64

		FileEntry customFileEntry1 = TempFileEntryUtil.addTempFileEntry(
			testGroupId, TestPropsValues.getUserId(), StringUtil.randomString(),
			TempFileEntryUtil.getTempFileName(
				StringUtil.randomString() + ".txt"),
			FileUtil.createTempFile(DLTestUtil.randomTextFileBytes()),
			ContentTypes.TEXT_PLAIN);
		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null,
				depotEntry.getGroupId(), customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE,
			useExternalReferenceCode);

		// Documents and media source, file from Base64

		DLFolder dlFolder1 = DLTestUtil.addDLFolder(testGroupId);

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					dlFolder1, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				dlFolder1.getExternalReferenceCode(), dlFolder1.getGroupId(),
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		DLFolder dlFolder2 = DLTestUtil.addDLFolder(group.getGroupId());

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					dlFolder2, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				dlFolder2.getExternalReferenceCode(), dlFolder2.getGroupId(),
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null,
				group.getGroupId(), customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, testGroupId,
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		// Documents and media source, file from Base64 with external reference
		// code

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put("status", "NOT_FOUND"),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), testGroupId,
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		byte[] fileContent = DLTestUtil.randomTextFileBytes();

		com.liferay.portal.kernel.repository.model.Folder existingFolder =
			_dlAppLocalService.addFolder(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		DLFileEntry existingDLFileEntry = _addDLFileEntry(
			fileContent, existingFolder.getFolderId());

		com.liferay.object.rest.dto.v1_0.FileEntry testFileEntry = _toFileEntry(
			Base64::encode, fileContent, existingDLFileEntry.getFileName(),
			null, testGroupId, customFileEntry1.getMimeType());

		String externalReferenceCode1 =
			existingDLFileEntry.getExternalReferenceCode();

		testFileEntry.setExternalReferenceCode(externalReferenceCode1);

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				() -> {
					JSONObject jsonObject = _getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

					jsonObject.put(
						"externalReferenceCode", externalReferenceCode1);

					return jsonObject;
				}),
			testFileEntry, httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		testFileEntry = _toFileEntry(
			content -> null, DLTestUtil.randomTextFileBytes(),
			RandomTestUtil.randomString() + ".txt", null, null,
			customFileEntry1.getMimeType());

		String externalReferenceCode2 = RandomTestUtil.randomString();

		testFileEntry.setExternalReferenceCode(externalReferenceCode2);

		Group scopeGroup = GroupTestUtil.addGroup();

		testFileEntry.setScope(Scope.of(scopeGroup, LocaleUtil.getDefault()));

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				() -> {
					JSONObject jsonObject = _getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

					jsonObject.put(
						"externalReferenceCode", externalReferenceCode2);

					return jsonObject;
				}),
			testFileEntry, httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		FileEntry serviceBuilderFileEntry = _dlAppLocalService.getFileEntry(
			testDLFileEntryModelListener.getLastFileEntryId());

		Assert.assertEquals(
			externalReferenceCode2,
			serviceBuilderFileEntry.getExternalReferenceCode());
		Assert.assertEquals(
			scopeGroup.getGroupId(), serviceBuilderFileEntry.getGroupId());

		InputStream inputStream = serviceBuilderFileEntry.getContentStream();

		Assert.assertEquals(-1, inputStream.read());

		// Documents and media source, file from Base64 with nested fields

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> {
				Folder folder = new Folder() {
					{
						externalReferenceCode =
							dlFolder1.getExternalReferenceCode();
						siteId = dlFolder1.getGroupId();
					}
				};

				JSONObject jsonObject = _getFileEntryJSONObject(
					dlFolder1, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					jsonObject.put(
						"fileBase64", fileEntry.getFileBase64()
					).put(
						"folder",
						JSONFactoryUtil.createJSONObject(folder.toString())
					).put(
						"mimeType", customFileEntry1.getMimeType()
					));
			},
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				dlFolder1.getExternalReferenceCode(), dlFolder1.getGroupId(),
				customFileEntry1.getMimeType()),
			httpMethod, "fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		// Documents and media source, file from Base64 without add document
		// permission

		String userName = RandomTestUtil.randomString();
		String userPassword = RandomTestUtil.randomString();

		HTTPTestUtil.customize(
		).withCredentials(
			userName + "@liferay.com", userPassword
		).apply(
			() -> {
				DLFolder dlFolder = DLTestUtil.addDLFolder(testGroupId);

				Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

				User user = addUser(userName, userPassword);

				roleLocalService.addUserRole(
					user.getUserId(), role.getRoleId());

				addResourcePermission(
					ObjectActionKeys.ADD_OBJECT_ENTRY,
					objectDefinition.getResourceName(), role);

				resourcePermissionLocalService.setResourcePermissions(
					TestPropsValues.getCompanyId(), DLConstants.RESOURCE_NAME,
					ResourceConstants.SCOPE_GROUP, String.valueOf(testGroupId),
					role.getRoleId(), new String[] {ActionKeys.ADD_DOCUMENT});

				_testPatchPutCustomObjectEntryWithAttachmentField(
					fileEntry -> JSONUtil.put("status", "FORBIDDEN"),
					_toFileEntry(
						Base64::encode, DLTestUtil.randomTextFileBytes(),
						RandomTestUtil.randomString() + ".txt",
						dlFolder.getExternalReferenceCode(),
						dlFolder.getGroupId(), customFileEntry1.getMimeType()),
					httpMethod, null, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					useExternalReferenceCode);

				resourcePermissionLocalService.setResourcePermissions(
					TestPropsValues.getCompanyId(),
					DLFolderConstants.getClassName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(dlFolder.getFolderId()), role.getRoleId(),
					new String[] {ActionKeys.ADD_DOCUMENT});

				_testPatchPutCustomObjectEntryWithAttachmentField(
					fileEntry -> JSONUtil.put(
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
						_getFileEntryJSONObject(
							dlFolder, fileEntry, objectDefinition,
							OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
					_toFileEntry(
						Base64::encode, DLTestUtil.randomTextFileBytes(),
						RandomTestUtil.randomString() + ".txt",
						dlFolder.getExternalReferenceCode(),
						dlFolder.getGroupId(), customFileEntry1.getMimeType()),
					httpMethod, null, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					useExternalReferenceCode);
			}
		);

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		String previewFileURL = StringBundler.concat(
			"http://", company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false),
			_dlURLHelper.getPreviewURL(
				customFileEntry1, customFileEntry1.getFileVersion(), null, "",
				false, true));

		String hostNotAllowedMessage = StringBundler.concat(
			"Unable to download file from ", previewFileURL, " because host ",
			company.getVirtualHostname(), " is not allowed");

		// Documents and media source, file from local URL with host allowed
		// and local network access disabled

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[] {company.getVirtualHostname()}, false)) {

			_testPatchPutCustomObjectEntryWithInvalidFileURL(
				hostNotAllowedMessage, previewFileURL, httpMethod,
				objectDefinition, useExternalReferenceCode);
		}

		// Documents and media source, file from local URL with host allowed
		// and local network access enabled

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[] {
						StringUtil.toUpperCase(company.getVirtualHostname())
					},
					true)) {

			_testPatchPutCustomObjectEntryWithAttachmentField(
				fileEntry -> JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					_getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
				_toFileEntry(
					RandomTestUtil.randomString() + ".txt", previewFileURL,
					null, null, customFileEntry1.getMimeType()),
				httpMethod, null, objectDefinition,
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				useExternalReferenceCode);
		}

		// Documents and media source, file from local URL with local network
		// access disabled

		_testPatchPutCustomObjectEntryWithInvalidFileURL(
			hostNotAllowedMessage, previewFileURL, httpMethod, objectDefinition,
			useExternalReferenceCode);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[0], true)) {

			// Documents and media source, file from local URL with local
			// network access enabled

			_testPatchPutCustomObjectEntryWithAttachmentField(
				fileEntry -> JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					_getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
				_toFileEntry(
					RandomTestUtil.randomString() + ".txt", previewFileURL,
					null, null, customFileEntry1.getMimeType()),
				httpMethod, null, objectDefinition,
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				useExternalReferenceCode);

			// Documents and media source, file from URL and host down

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.object.rest.internal.manager.v1_0." +
						"DefaultObjectEntryManagerImpl",
					LoggerTestUtil.ERROR)) {

				String url = StringBundler.concat(
					"http://", company.getVirtualHostname(), ":",
					PortalUtil.getPortalServerPort(false) + 1);

				_testPatchPutCustomObjectEntryWithInvalidFileURL(
					"Unable to download file from " + url, url, httpMethod,
					objectDefinition, useExternalReferenceCode);
			}

			// Documents and media source, file from URL malformed

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.object.rest.internal.manager.v1_0." +
						"DefaultObjectEntryManagerImpl",
					LoggerTestUtil.ERROR)) {

				String url = StringBundler.concat(
					"http//", company.getVirtualHostname(), ":",
					PortalUtil.getPortalServerPort(false), "/",
					RandomTestUtil.randomString());

				_testPatchPutCustomObjectEntryWithInvalidFileURL(
					"Unable to download file from " + url, url, httpMethod,
					objectDefinition, useExternalReferenceCode);
			}

			// Documents and media source, file from URL not found

			String notFoundPath = "/" + RandomTestUtil.randomString();

			String httpCode404URL = StringBundler.concat(
				"http://", company.getVirtualHostname(), ":",
				PortalUtil.getPortalServerPort(false), notFoundPath);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"portal_web.docroot.errors.code_jsp",
					LoggerTestUtil.WARN)) {

				_testPatchPutCustomObjectEntryWithInvalidFileURL(
					"Unable to download file from " + httpCode404URL +
						" because of unexpected HTTP response code 404",
					httpCode404URL, httpMethod, objectDefinition,
					useExternalReferenceCode);

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					LoggerTestUtil.WARN, logEntry.getPriority());

				String message = logEntry.getMessage();

				Assert.assertTrue(message, message.contains("code=\"404\""));
				Assert.assertTrue(
					message, message.contains("uri=" + notFoundPath));
			}
		}

		// Documents and media source, file from URL with host not allowed

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[] {"www.able.com"}, true)) {

			_testPatchPutCustomObjectEntryWithInvalidFileURL(
				hostNotAllowedMessage, previewFileURL, httpMethod,
				objectDefinition, useExternalReferenceCode);
		}

		// Documents and media source, file from URL with unsupported protocol

		String jarFileURL = StringBundler.concat(
			"jar:file:///", RandomTestUtil.randomString(),
			".jar!/META-INF/MANIFEST.MF");

		_testPatchPutCustomObjectEntryWithInvalidFileURL(
			"Unable to download file from " + jarFileURL +
				" because protocol jar is not supported",
			jarFileURL, httpMethod, objectDefinition, useExternalReferenceCode);

		String unsupportedProtocolFileURL = StringBundler.concat(
			"file://", company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false));

		_testPatchPutCustomObjectEntryWithInvalidFileURL(
			"Unable to download file from " + unsupportedProtocolFileURL +
				" because protocol file is not supported",
			unsupportedProtocolFileURL, httpMethod, objectDefinition,
			useExternalReferenceCode);

		// User computer to CMS basic document source, file from Base64

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE,
			useExternalReferenceCode);

		// User computer to documents and media source, file from Base64

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				StringBundler.concat(
					"File ", fileEntry.getName(),
					" exceeds the maximum permitted size of ",
					MAX_FILE_SIZE_VALUE, " MB")
			),
			_toFileEntry(
				Base64::encode,
				DLTestUtil.randomTextFileBytes(
					(MAX_FILE_SIZE_VALUE * 1024 * 1024) + 1),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "File name is null"
			),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(), null, null,
				null, customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Invalid file extension for " + fileEntry.getName()
			),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".err", null, null,
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Unable to decode Base64 file"
			),
			_toFileEntry(
				String::new,
				ArrayUtil.append(DLTestUtil.randomTextFileBytes(), (byte)'$'),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
			useExternalReferenceCode);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				customFileEntry1.getMimeType()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
			useExternalReferenceCode);

		// User computer to documents and media source, file from Base64 with
		// existing name

		testFileEntry = _toFileEntry(
			Base64::encode, DLTestUtil.randomTextFileBytes(),
			RandomTestUtil.randomString() + ".txt", null, null,
			customFileEntry1.getMimeType());

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			testFileEntry, httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> {
				fileEntry.setName(
					StringUtil.replace(
						fileEntry.getName(), ".txt", " (1).txt"));

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
					_getFileEntryJSONObject(
						_getDLFolder(objectDefinition, false), fileEntry,
						objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1));
			},
			testFileEntry, httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);

		testFileEntry = _toFileEntry(
			Base64::encode, DLTestUtil.randomTextFileBytes(),
			RandomTestUtil.randomString() + ".txt", null, null,
			customFileEntry1.getMimeType());

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			testFileEntry, httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
			useExternalReferenceCode);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> {
				fileEntry.setName(
					StringUtil.replace(
						fileEntry.getName(), ".txt", " (1).txt"));

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
					_getFileEntryJSONObject(
						_getDLFolder(objectDefinition, true), fileEntry,
						objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2));
			},
			testFileEntry, httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
			useExternalReferenceCode);

		// User computer to documents and media source, file from Base64 with
		// nested fields

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				() -> {
					JSONObject jsonObject = _getFileEntryJSONObject(
						_getDLFolder(objectDefinition, false), fileEntry,
						objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);

					return jsonObject.put(
						"fileBase64", fileEntry.getFileBase64());
				}),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			httpMethod, "fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				() -> {
					JSONObject jsonObject = _getFileEntryJSONObject(
						_getDLFolder(objectDefinition, true), fileEntry,
						objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

					return jsonObject.put(
						"fileBase64", fileEntry.getFileBase64());
				}),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			httpMethod, "fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
			useExternalReferenceCode);
	}

	private void _testPatchPutCustomObjectEntryWithAttachmentField(
			UnsafeFunction
				<com.liferay.object.rest.dto.v1_0.FileEntry, JSONObject,
				 Exception> expectedJSONObjectUnsafeFunction,
			com.liferay.object.rest.dto.v1_0.FileEntry fileEntry,
			Http.Method httpMethod, String nestedFields,
			ObjectDefinition objectDefinition, String objectFieldName,
			boolean useExternalReferenceCode)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				objectFieldName,
				_toFileEntryJSONObject(
					DLTestUtil.randomTextFileBytes(),
					RandomTestUtil.randomString() + ".txt",
					fileEntry.getFolder(), ContentTypes.TEXT_PLAIN,
					objectFieldName)
			).toString(),
			getEndpoint(objectDefinition, testGroupId), Http.Method.POST);

		String endpoint =
			objectDefinition.getRESTContextPath() + "/" +
				jsonObject.getLong("id");

		if (useExternalReferenceCode) {
			endpoint =
				getEndpoint(objectDefinition, testGroupId) +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode");
		}

		if (nestedFields != null) {
			endpoint = StringBundler.concat(
				endpoint, "?nestedFields=",
				StringUtil.merge(
					TransformUtil.transform(
						StringUtil.split(nestedFields),
						nestedField -> objectFieldName + "." + nestedField,
						String.class)));
		}

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				objectFieldName,
				JSONFactoryUtil.createJSONObject(fileEntry.toString())
			).put(
				"externalReferenceCode",
				jsonObject.getString("externalReferenceCode")
			).put(
				"id", jsonObject.getLong("id")
			).toString(),
			endpoint, httpMethod);

		_assertJSONObjectWithAttachmentField(
			expectedJSONObjectUnsafeFunction.apply(fileEntry), jsonObject,
			objectFieldName);
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

	private void _testPatchPutCustomObjectEntryWithInvalidFileURL(
			String expectedTitle, String fileURL, Http.Method httpMethod,
			ObjectDefinition objectDefinition, boolean useExternalReferenceCode)
		throws Exception {

		_testPatchPutCustomObjectEntryWithAttachmentField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", expectedTitle
			),
			_toFileEntry(
				RandomTestUtil.randomString() + ".txt", fileURL, null, null,
				ContentTypes.TEXT_PLAIN),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);
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

	private void _testPostCustomObjectEntryWithAttachmentObjectField(
			ObjectDefinition objectDefinition)
		throws Exception {

		// CMS basic document source, file from Base64

		FileEntry customFileEntry1 = TempFileEntryUtil.addTempFileEntry(
			testGroupId, TestPropsValues.getUserId(), StringUtil.randomString(),
			TempFileEntryUtil.getTempFileName(
				StringUtil.randomString() + ".txt"),
			FileUtil.createTempFile(DLTestUtil.randomTextFileBytes()),
			ContentTypes.TEXT_PLAIN);
		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null,
				depotEntry.getGroupId(), customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE);

		// Documents and media source, file from Base64

		DLFolder dlFolder1 = DLTestUtil.addDLFolder(testGroupId);

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					dlFolder1, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				dlFolder1.getExternalReferenceCode(), dlFolder1.getGroupId(),
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		DLFolder dlFolder2 = DLTestUtil.addDLFolder(group.getGroupId());

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					dlFolder2, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				dlFolder2.getExternalReferenceCode(), dlFolder2.getGroupId(),
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null,
				group.getGroupId(), customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, testGroupId,
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		// Documents and media source, file from Base64 with external reference
		// code

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put("status", "NOT_FOUND"),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), testGroupId,
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		byte[] fileContent = DLTestUtil.randomTextFileBytes();

		com.liferay.portal.kernel.repository.model.Folder existingFolder =
			_dlAppLocalService.addFolder(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		DLFileEntry existingDLFileEntry = _addDLFileEntry(
			fileContent, existingFolder.getFolderId());

		com.liferay.object.rest.dto.v1_0.FileEntry testFileEntry = _toFileEntry(
			Base64::encode, fileContent, existingDLFileEntry.getFileName(),
			null, testGroupId, customFileEntry1.getMimeType());

		String externalReferenceCode1 =
			existingDLFileEntry.getExternalReferenceCode();

		testFileEntry.setExternalReferenceCode(externalReferenceCode1);

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				() -> {
					JSONObject jsonObject = _getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

					jsonObject.put(
						"externalReferenceCode", externalReferenceCode1);

					return jsonObject;
				}),
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		testFileEntry = _toFileEntry(
			content -> null, DLTestUtil.randomTextFileBytes(),
			RandomTestUtil.randomString() + ".txt", null, null,
			customFileEntry1.getMimeType());

		String externalReferenceCode2 = RandomTestUtil.randomString();

		testFileEntry.setExternalReferenceCode(externalReferenceCode2);

		Group scopeGroup = GroupTestUtil.addGroup();

		testFileEntry.setScope(Scope.of(scopeGroup, LocaleUtil.getDefault()));

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				() -> {
					JSONObject jsonObject = _getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

					jsonObject.put(
						"externalReferenceCode", externalReferenceCode2);

					return jsonObject;
				}),
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		FileEntry serviceBuilderFileEntry = _dlAppLocalService.getFileEntry(
			testDLFileEntryModelListener.getLastFileEntryId());

		Assert.assertEquals(
			externalReferenceCode2,
			serviceBuilderFileEntry.getExternalReferenceCode());
		Assert.assertEquals(
			scopeGroup.getGroupId(), serviceBuilderFileEntry.getGroupId());

		InputStream inputStream = serviceBuilderFileEntry.getContentStream();

		Assert.assertEquals(-1, inputStream.read());

		// Documents and media source, file from Base64 with nested fields

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				Folder folder = new Folder() {
					{
						externalReferenceCode =
							dlFolder1.getExternalReferenceCode();
						siteId = dlFolder1.getGroupId();
					}
				};

				JSONObject jsonObject = _getFileEntryJSONObject(
					dlFolder1, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					jsonObject.put(
						"fileBase64", fileEntry.getFileBase64()
					).put(
						"folder",
						JSONFactoryUtil.createJSONObject(folder.toString())
					).put(
						"mimeType", fileEntry.getMimeType()
					));
			},
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				dlFolder1.getExternalReferenceCode(), dlFolder1.getGroupId(),
				customFileEntry1.getMimeType()),
			"fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		// Documents and media source, file from Base64 without add document
		// permission

		String userName = RandomTestUtil.randomString();
		String userPassword = RandomTestUtil.randomString();

		HTTPTestUtil.customize(
		).withCredentials(
			userName + "@liferay.com", userPassword
		).apply(
			() -> {
				DLFolder dlFolder = DLTestUtil.addDLFolder(testGroupId);

				Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

				User user = addUser(userName, userPassword);

				roleLocalService.addUserRole(
					user.getUserId(), role.getRoleId());

				addResourcePermission(
					ObjectActionKeys.ADD_OBJECT_ENTRY,
					objectDefinition.getResourceName(), role);

				_testPostCustomObjectEntryWithAttachmentObjectField(
					fileEntry -> JSONUtil.put("status", "FORBIDDEN"),
					_toFileEntry(
						Base64::encode, DLTestUtil.randomTextFileBytes(),
						RandomTestUtil.randomString() + ".txt",
						dlFolder.getExternalReferenceCode(),
						dlFolder.getGroupId(), customFileEntry1.getMimeType()),
					null, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

				resourcePermissionLocalService.setResourcePermissions(
					TestPropsValues.getCompanyId(),
					DLFolderConstants.getClassName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(dlFolder.getFolderId()), role.getRoleId(),
					new String[] {ActionKeys.ADD_DOCUMENT});

				_testPostCustomObjectEntryWithAttachmentObjectField(
					fileEntry -> JSONUtil.put(
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
						_getFileEntryJSONObject(
							dlFolder, fileEntry, objectDefinition,
							OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
					_toFileEntry(
						Base64::encode, DLTestUtil.randomTextFileBytes(),
						RandomTestUtil.randomString() + ".txt",
						dlFolder.getExternalReferenceCode(),
						dlFolder.getGroupId(), customFileEntry1.getMimeType()),
					null, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);
			}
		);

		// Documents and media source, file from generated thumbnail

		DLFolder dlFolder3 = DLTestUtil.addDLFolder(testGroupId);

		FileEntry customFileEntry2 = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), testGroupId,
			dlFolder3.getFolderId(), StringUtil.randomString() + ".jpg",
			ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null, ServiceContextTestUtil.getServiceContext());

		String thumbnailSrc = _dlURLHelper.getThumbnailSrc(
			customFileEntry2, null);

		Assert.assertNotNull(thumbnailSrc);

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					dlFolder3, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					thumbnailSrc)),
			_toFileEntry(
				customFileEntry2.getFileName(),
				customFileEntry2.getFileEntryId(),
				customFileEntry2.getMimeType()),
			"thumbnailURL", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		String previewFileURL = StringBundler.concat(
			"http://", company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false),
			_dlURLHelper.getPreviewURL(
				customFileEntry1, customFileEntry1.getFileVersion(), null, "",
				false, true));

		String hostNotAllowedMessage = StringBundler.concat(
			"Unable to download file from ", previewFileURL, " because host ",
			company.getVirtualHostname(), " is not allowed");

		// Documents and media source, file from local URL with host allowed
		// and local network access disabled

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[] {company.getVirtualHostname()}, false)) {

			_testPostCustomObjectEntryWithInvalidFileURL(
				hostNotAllowedMessage, previewFileURL, objectDefinition);
		}

		// Documents and media source, file from local URL with host allowed
		// and local network access enabled

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[] {
						StringUtil.toUpperCase(company.getVirtualHostname())
					},
					true)) {

			_testPostCustomObjectEntryWithAttachmentObjectField(
				fileEntry -> JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					_getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
				_toFileEntry(
					RandomTestUtil.randomString() + ".txt", previewFileURL,
					null, group.getGroupId(), customFileEntry1.getMimeType()),
				null, objectDefinition,
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);
		}

		// Documents and media source, file from local URL with local network
		// access disabled

		_testPostCustomObjectEntryWithInvalidFileURL(
			hostNotAllowedMessage, previewFileURL, objectDefinition);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[0], true)) {

			// Documents and media source, file from local URL with local
			// network access enabled

			_testPostCustomObjectEntryWithAttachmentObjectField(
				fileEntry -> JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					_getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
				_toFileEntry(
					customFileEntry1.getTitle(), previewFileURL, null,
					group.getGroupId(), customFileEntry1.getMimeType()),
				null, objectDefinition,
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

			// Documents and media source, file from URL and host down

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.object.rest.internal.manager.v1_0." +
						"DefaultObjectEntryManagerImpl",
					LoggerTestUtil.ERROR)) {

				String hostDownFileURL = StringBundler.concat(
					"http://", company.getVirtualHostname(), ":",
					PortalUtil.getPortalServerPort(false) + 1);

				_testPostCustomObjectEntryWithInvalidFileURL(
					"Unable to download file from " + hostDownFileURL,
					hostDownFileURL, objectDefinition);
			}

			// Documents and media source, file from URL malformed

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.object.rest.internal.manager.v1_0." +
						"DefaultObjectEntryManagerImpl",
					LoggerTestUtil.ERROR)) {

				String malformedFileURL = StringBundler.concat(
					"http//", company.getVirtualHostname(), ":",
					PortalUtil.getPortalServerPort(false), "/",
					RandomTestUtil.randomString());

				_testPostCustomObjectEntryWithInvalidFileURL(
					"Unable to download file from " + malformedFileURL,
					malformedFileURL, objectDefinition);
			}

			// Documents and media source, file from URL not found

			String notFoundPath = "/" + RandomTestUtil.randomString();

			String resourceNotFoundFileURL = StringBundler.concat(
				"http://", company.getVirtualHostname(), ":",
				PortalUtil.getPortalServerPort(false), notFoundPath);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"portal_web.docroot.errors.code_jsp",
					LoggerTestUtil.WARN)) {

				_testPostCustomObjectEntryWithInvalidFileURL(
					"Unable to download file from " + resourceNotFoundFileURL +
						" because of unexpected HTTP response code 404",
					resourceNotFoundFileURL, objectDefinition);

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					LoggerTestUtil.WARN, logEntry.getPriority());

				String message = logEntry.getMessage();

				Assert.assertTrue(message, message.contains("code=\"404\""));
				Assert.assertTrue(
					message, message.contains("uri=" + notFoundPath));
			}
		}

		// Documents and media source, file from URL with host not allowed

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[] {"www.able.com"}, true)) {

			_testPostCustomObjectEntryWithInvalidFileURL(
				hostNotAllowedMessage, previewFileURL, objectDefinition);
		}

		// Documents and media source, file from URL with unsupported protocol

		String jarFileURL = StringBundler.concat(
			"jar:file:///", RandomTestUtil.randomString(),
			".jar!/META-INF/MANIFEST.MF");

		_testPostCustomObjectEntryWithInvalidFileURL(
			"Unable to download file from " + jarFileURL +
				" because protocol jar is not supported",
			jarFileURL, objectDefinition);

		String unsupportedProtocolURL = StringBundler.concat(
			"file://", company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false));

		_testPostCustomObjectEntryWithInvalidFileURL(
			"Unable to download file from " + unsupportedProtocolURL +
				" because protocol file is not supported",
			unsupportedProtocolURL, objectDefinition);

		// User computer to CMS basic document source, file from Base64

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE);

		// User computer to documents and media source, file from Base64

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				StringBundler.concat(
					"File ", fileEntry.getName(),
					" exceeds the maximum permitted size of ",
					MAX_FILE_SIZE_VALUE, " MB")
			),
			_toFileEntry(
				Base64::encode,
				DLTestUtil.randomTextFileBytes(
					(MAX_FILE_SIZE_VALUE * 1024 * 1024) + 1),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "File content is empty"
			),
			_toFileEntry(
				Base64::encode, new byte[0],
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "File name is null"
			),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(), null, null,
				null, customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Invalid file extension for " + fileEntry.getName()
			),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".err", null, null,
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Unable to decode Base64 file"
			),
			_toFileEntry(
				String::new,
				ArrayUtil.append(DLTestUtil.randomTextFileBytes(), (byte)'$'),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				customFileEntry1.getMimeType()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

		// User computer to documents and media source, file from Base64 with
		// existing name

		testFileEntry = _toFileEntry(
			Base64::encode, DLTestUtil.randomTextFileBytes(),
			RandomTestUtil.randomString() + ".txt", null, null,
			customFileEntry1.getMimeType());

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				fileEntry.setName(
					StringUtil.replace(
						fileEntry.getName(), ".txt", " (1).txt"));

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
					_getFileEntryJSONObject(
						_getDLFolder(objectDefinition, false), fileEntry,
						objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1));
			},
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);

		testFileEntry = _toFileEntry(
			Base64::encode, DLTestUtil.randomTextFileBytes(),
			RandomTestUtil.randomString() + ".txt", null, null,
			customFileEntry1.getMimeType());

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				fileEntry.setName(
					StringUtil.replace(
						fileEntry.getName(), ".txt", " (1).txt"));

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
					_getFileEntryJSONObject(
						_getDLFolder(objectDefinition, true), fileEntry,
						objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2));
			},
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

		// User computer to documents and media source, file from Base64 with
		// external reference code

		fileContent = DLTestUtil.randomTextFileBytes();

		existingDLFileEntry = _addDLFileEntry(
			fileContent, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		testFileEntry = _toFileEntry(
			Base64::encode, fileContent, existingDLFileEntry.getFileName(),
			null, testGroupId, customFileEntry1.getMimeType());

		String externalReferenceCode3 =
			existingDLFileEntry.getExternalReferenceCode();

		testFileEntry.setExternalReferenceCode(externalReferenceCode3);

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				() -> {
					JSONObject jsonObject = _getFileEntryJSONObject(
						_getDLFolder(objectDefinition, true), fileEntry,
						objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

					jsonObject.put(
						"externalReferenceCode", externalReferenceCode3);

					return jsonObject;
				}),
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

		testFileEntry = _toFileEntry(
			content -> null, DLTestUtil.randomTextFileBytes(),
			RandomTestUtil.randomString() + ".txt", null, null,
			customFileEntry1.getMimeType());

		String externalReferenceCode4 = RandomTestUtil.randomString();

		testFileEntry.setExternalReferenceCode(externalReferenceCode4);

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				() -> {
					JSONObject jsonObject = _getFileEntryJSONObject(
						_getDLFolder(objectDefinition, true), fileEntry,
						objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

					jsonObject.put(
						"externalReferenceCode", externalReferenceCode4);

					return jsonObject;
				}),
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

		serviceBuilderFileEntry = _dlAppLocalService.getFileEntry(
			testDLFileEntryModelListener.getLastFileEntryId());

		Assert.assertEquals(
			externalReferenceCode4,
			serviceBuilderFileEntry.getExternalReferenceCode());

		inputStream = serviceBuilderFileEntry.getContentStream();

		Assert.assertEquals(-1, inputStream.read());

		// User computer to documents and media source, file from Base64 with
		// nested fields

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				() -> {
					JSONObject jsonObject = _getFileEntryJSONObject(
						_getDLFolder(objectDefinition, false), fileEntry,
						objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);

					return jsonObject.put(
						"fileBase64", fileEntry.getFileBase64());
				}),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			"fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);
		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				() -> {
					JSONObject jsonObject = _getFileEntryJSONObject(
						_getDLFolder(objectDefinition, true), fileEntry,
						objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

					return jsonObject.put(
						"fileBase64", fileEntry.getFileBase64());
				}),
			_toFileEntry(
				Base64::encode, DLTestUtil.randomTextFileBytes(),
				RandomTestUtil.randomString() + ".txt", null, null,
				customFileEntry1.getMimeType()),
			"fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
	}

	private void _testPostCustomObjectEntryWithAttachmentObjectField(
			UnsafeFunction
				<com.liferay.object.rest.dto.v1_0.FileEntry, JSONObject,
				 Exception> expectedJSONObjectUnsafeFunction,
			com.liferay.object.rest.dto.v1_0.FileEntry fileEntry,
			String nestedFields, ObjectDefinition objectDefinition,
			String objectFieldName)
		throws Exception {

		String endpoint = getEndpoint(objectDefinition, testGroupId);

		if (nestedFields != null) {
			endpoint = StringBundler.concat(
				endpoint, "?nestedFields=",
				StringUtil.merge(
					TransformUtil.transform(
						StringUtil.split(nestedFields),
						nestedField -> objectFieldName + "." + nestedField,
						String.class)));
		}

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				objectFieldName,
				JSONFactoryUtil.createJSONObject(fileEntry.toString())
			).toString(),
			endpoint, Http.Method.POST);

		_assertJSONObjectWithAttachmentField(
			expectedJSONObjectUnsafeFunction.apply(fileEntry), jsonObject,
			objectFieldName);
	}

	private void _testPostCustomObjectEntryWithInvalidFileURL(
			String expectedTitle, String fileURL,
			ObjectDefinition objectDefinition)
		throws Exception {

		_testPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", expectedTitle
			),
			_toFileEntry(
				RandomTestUtil.randomString() + ".txt", fileURL, null,
				group.getGroupId(), ContentTypes.TEXT_PLAIN),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);
	}

	private void
			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInManyToManyRelationship(
				String objectDefinitionRESTContextPath,
				ObjectRelationship objectRelationship)
		throws Exception {

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
				).put(
					"externalReferenceCode", ERC_VALUE_2
				).toString()));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(), objectDefinitionRESTContextPath,
			Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject.get("status"));
	}

	private void
			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInManyToOneRelationship(
				String objectDefinitionRESTContextPath,
				ObjectRelationship objectRelationship)
		throws Exception {

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship.getName(),
			createObjectEntriesJSONArray(
				new String[] {ERC_VALUE_1, ERC_VALUE_2}, OBJECT_FIELD_NAME_1,
				new String[] {
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				}));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(), objectDefinitionRESTContextPath,
			Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject.get("status"));
	}

	private void
			_testPostCustomObjectEntryWithInvalidNestedCustomObjectEntriesInOneToManyRelationship(
				String objectDefinitionRESTContextPath,
				ObjectRelationship objectRelationship)
		throws Exception {

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship.getName(),
			JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
				).put(
					"externalReferenceCode", ERC_VALUE_2
				).toString()));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(), objectDefinitionRESTContextPath,
			Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject.get("status"));
	}

	private void _testPostCustomObjectEntryWithManyToOneRelationshipPriorities(
			JSONObject jsonObject1, JSONObject jsonObject2,
			UnsafeSupplier<JSONObject, Exception> jsonObjectUnsafeSupplier,
			ObjectRelationship objectRelationship)
		throws Exception {

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		String objectFieldName = objectField.getName();

		String objectRelationshipERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectField);

		// priority(ERC) > priority(invalid id)

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)
			).put(
				objectFieldName, Long.valueOf(jsonObject1.getLong("id"))
			).put(
				objectRelationshipERCObjectFieldName,
				jsonObject1.getString("externalReferenceCode")
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
				).put(
					objectFieldName, RandomTestUtil.randomLong()
				).put(
					objectRelationshipERCObjectFieldName,
					jsonObject1.getString("externalReferenceCode")
				).toString(),
				getObjectDefinition2().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);

		//  priority(id) > priority(ERC)

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)
			).put(
				objectFieldName, Long.valueOf(jsonObject1.getLong("id"))
			).put(
				objectRelationshipERCObjectFieldName,
				jsonObject1.getString("externalReferenceCode")
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
				).put(
					objectFieldName, jsonObject1.getLong("id")
				).put(
					objectRelationshipERCObjectFieldName,
					jsonObject2.getString("externalReferenceCode")
				).toString(),
				getObjectDefinition2().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);

		// priority(relationshipName) > priority(id) > priority(ERC)

		String objectRelationshipName = objectRelationship.getName();

		JSONObject actualJSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
			).put(
				objectFieldName, jsonObject2.getLong("id")
			).put(
				objectRelationshipERCObjectFieldName,
				jsonObject2.getString("externalReferenceCode")
			).put(
				objectRelationshipName, jsonObject1
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)
			).put(
				objectFieldName, Long.valueOf(jsonObject1.getLong("id"))
			).put(
				objectRelationshipERCObjectFieldName,
				jsonObject1.getString("externalReferenceCode")
			).toString(),
			actualJSONObject1.toString(), JSONCompareMode.LENIENT);

		Assert.assertEquals(
			jsonObject1.getString("externalReferenceCode"),
			JSONUtil.getValueAsString(
				actualJSONObject1, "JSONObject/" + objectRelationshipName,
				"Object/externalReferenceCode"));

		JSONObject actualJSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
			).put(
				objectFieldName, jsonObject1.getLong("id")
			).put(
				objectRelationshipERCObjectFieldName,
				jsonObject1.getString("externalReferenceCode")
			).put(
				objectRelationshipName, jsonObjectUnsafeSupplier.get()
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		Assert.assertNotEquals(
			jsonObject1.getLong("id"),
			actualJSONObject2.getLong(objectFieldName));

		Assert.assertNotEquals(
			jsonObject1.getString("externalReferenceCode"),
			actualJSONObject2.getString(objectRelationshipERCObjectFieldName));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			// invalid ERC

			JSONAssert.assertEquals(
				JSONUtil.put(
					"status", "NOT_FOUND"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
					).put(
						objectRelationshipERCObjectFieldName,
						RandomTestUtil.randomString()
					).toString(),
					getObjectDefinition2().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// priority(invalid id) > priority(invalid ERC)

			JSONAssert.assertEquals(
				JSONUtil.put(
					"status", "NOT_FOUND"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
					).put(
						objectFieldName, RandomTestUtil.randomLong()
					).put(
						objectRelationshipERCObjectFieldName,
						RandomTestUtil.randomString()
					).toString(),
					getObjectDefinition2().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);
		}
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

	private void _testPostObjectEntryWithCrossSiteTaxonomyCategories()
		throws Exception {

		// Can add a category from a connected depot entry

		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), testGroupId);

		AssetVocabulary depotAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), depotEntry.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory depotTaxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				depotEntry.getGroupId(),
				depotAssetVocabulary.getVocabularyId());

		Assert.assertNotNull(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(depotTaxonomyCategory.getId())
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST
			).get(
				"id"
			));

		_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			depotEntryGroupRel.getDepotEntryGroupRelId());

		// Can add a category from a parent site

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory taxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				group.getGroupId(), assetVocabulary.getVocabularyId());

		Group childGroup = GroupTestUtil.addGroup(group.getGroupId());

		Assert.assertNotNull(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(taxonomyCategory.getId())
				).toString(),
				getEndpoint(
					getSiteScopedObjectDefinition1(), childGroup.getGroupId()),
				Http.Method.POST
			).get(
				"id"
			));

		groupLocalService.deleteGroup(childGroup);

		// Can add a category from an all spaces vocabulary

		AssetVocabulary allSpacesAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		AssetVocabularyGroupRelLocalServiceUtil.addAssetVocabularyGroupRel(
			GroupConstants.ANY_PARENT_GROUP_ID,
			allSpacesAssetVocabulary.getVocabularyId(),
			DepotConstants.TYPE_SPACE);

		TaxonomyCategory allSpacesTaxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				allSpacesAssetVocabulary.getGroupId(),
				allSpacesAssetVocabulary.getVocabularyId());

		Assert.assertNotNull(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(allSpacesTaxonomyCategory.getId())
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST
			).get(
				"id"
			));

		assetVocabularyLocalService.deleteVocabulary(allSpacesAssetVocabulary);

		// Can add a category from a space associated vocabulary

		AssetVocabulary sharedAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		AssetVocabularyGroupRelLocalServiceUtil.addAssetVocabularyGroupRel(
			testGroupId, sharedAssetVocabulary.getVocabularyId(),
			DepotConstants.TYPE_SPACE);

		TaxonomyCategory sharedTaxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				sharedAssetVocabulary.getGroupId(),
				sharedAssetVocabulary.getVocabularyId());

		Assert.assertNotNull(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryBriefs",
					JSONUtil.putAll(
						JSONUtil.put(
							"parentTaxonomyVocabulary",
							JSONUtil.put(
								"externalReferenceCode",
								sharedAssetVocabulary.
									getExternalReferenceCode())
						).put(
							"scope",
							JSONUtil.put(
								"externalReferenceCode",
								group.getExternalReferenceCode()
							).put(
								"type", "Site"
							)
						).put(
							"taxonomyCategoryExternalReferenceCode",
							sharedTaxonomyCategory.getExternalReferenceCode()
						))
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST
			).get(
				"id"
			));

		assetVocabularyLocalService.deleteVocabulary(sharedAssetVocabulary);

		// Can add a category from a system vocabulary

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		assetVocabularySettingsHelper.setSystem(true);

		AssetVocabulary systemAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), null,
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
				).build(),
				null, assetVocabularySettingsHelper.toString(),
				AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
				new ServiceContext());

		AssetVocabularyGroupRelLocalServiceUtil.addAssetVocabularyGroupRel(
			GroupConstants.ANY_PARENT_GROUP_ID,
			systemAssetVocabulary.getVocabularyId(), DepotConstants.TYPE_SPACE);

		TaxonomyCategory systemTaxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				systemAssetVocabulary.getGroupId(),
				systemAssetVocabulary.getVocabularyId());

		Assert.assertNotNull(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryBriefs",
					JSONUtil.putAll(
						JSONUtil.put(
							"scope",
							JSONUtil.put(
								"externalReferenceCode",
								group.getExternalReferenceCode()
							).put(
								"type", "Site"
							)
						).put(
							"taxonomyCategoryExternalReferenceCode",
							systemTaxonomyCategory.getExternalReferenceCode()
						))
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST
			).get(
				"id"
			));

		boolean deleteInProcess = GroupThreadLocal.isDeleteInProcess();

		try {
			GroupThreadLocal.setDeleteInProcess(true);

			assetVocabularyLocalService.deleteVocabulary(systemAssetVocabulary);
		}
		finally {
			GroupThreadLocal.setDeleteInProcess(deleteInProcess);
		}

		// Cannot add a category from an unassociated vocabulary

		AssetVocabulary foreignAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory foreignTaxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				foreignAssetVocabulary.getGroupId(),
				foreignAssetVocabulary.getVocabularyId());

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryBriefs",
					JSONUtil.putAll(
						JSONUtil.put(
							"scope",
							JSONUtil.put(
								"externalReferenceCode",
								group.getExternalReferenceCode()
							).put(
								"type", "Site"
							)
						).put(
							"taxonomyCategoryExternalReferenceCode",
							foreignTaxonomyCategory.getExternalReferenceCode()
						))
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST));

		assetVocabularyLocalService.deleteVocabulary(foreignAssetVocabulary);

		// Cannot add a category to a company-scoped entry

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(taxonomyCategory.getId())
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));

		// Cannot add a category from a foreign site

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(taxonomyCategory.getId())
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST));

		// Cannot add a category via UPSERT

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(taxonomyCategory.getId())
				).toString(),
				StringBundler.concat(
					getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
					"/by-external-reference-code/",
					RandomTestUtil.randomString()),
				Http.Method.PUT));

		// No empty category created

		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertNull(
			AssetCategoryLocalServiceUtil.
				fetchAssetCategoryByExternalReferenceCode(
					externalReferenceCode, group.getGroupId()));

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryBriefs",
					JSONUtil.put(
						JSONUtil.put(
							"scope",
							JSONUtil.put(
								"externalReferenceCode",
								group.getExternalReferenceCode()
							).put(
								"type", "Site"
							)
						).put(
							"taxonomyCategoryExternalReferenceCode",
							externalReferenceCode
						))
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST));

		Assert.assertNull(
			AssetCategoryLocalServiceUtil.
				fetchAssetCategoryByExternalReferenceCode(
					externalReferenceCode, group.getGroupId()));

		assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
		assetVocabularyLocalService.deleteVocabulary(depotAssetVocabulary);
	}

	private void _testPostScopeScopeKey(Object scopeKey1, Object scopeKey2)
		throws Exception {

		String objectFieldValue = RandomTestUtil.randomString();

		Assert.assertEquals(
			objectFieldValue,
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue
					).toString(),
					getEndpoint(getSiteScopedObjectDefinition1(), scopeKey1),
					Http.Method.POST),
				"Object/" + OBJECT_FIELD_NAME_1));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, getEndpoint(getSiteScopedObjectDefinition1(), scopeKey2),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());
	}

	private void
			_testPutCustomObjectEntryWithNestedCustomObjectEntriesByExternalReferenceCode(
				boolean manyToOne)
		throws Exception {

		JSONObject objectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			() -> {
				if (manyToOne) {
					return JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
						).put(
							"externalReferenceCode", ERC_VALUE_1
						).toString());
				}

				return createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1, ERC_VALUE_2},
					OBJECT_FIELD_NAME_2,
					new String[] {
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString()
					});
			});

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONObject newObjectEntryJSONObject = JSONUtil.put(
			objectRelationship1.getName(),
			() -> {
				if (manyToOne) {
					return JSONFactoryUtil.createJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1
						).put(
							"externalReferenceCode", ERC_VALUE_1
						).toString());
				}

				return createObjectEntriesJSONArray(
					new String[] {ERC_VALUE_1}, OBJECT_FIELD_NAME_2,
					new String[] {NEW_OBJECT_FIELD_VALUE_1});
			});

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			newObjectEntryJSONObject.toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/",
				jsonObject.getString("externalReferenceCode")),
			Http.Method.PUT);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		if (manyToOne) {
			_assertObjectEntryField(
				jsonObject.getJSONObject(
					StringBundler.concat(
						"r_", objectRelationship1.getName(), "_",
						StringUtil.replaceLast(
							getObjectDefinition2().getPKObjectFieldName(), "Id",
							""))),
				OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1);
		}
		else {
			JSONArray nestedObjectEntriesJSONArray = jsonObject.getJSONArray(
				objectRelationship1.getName());

			Assert.assertEquals(1, nestedObjectEntriesJSONArray.length());

			_assertObjectEntryField(
				(JSONObject)nestedObjectEntriesJSONArray.get(0),
				OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_1);
		}
	}

	private String _toDateString(Date date) {
		Instant instant = date.toInstant();

		return String.valueOf(instant.truncatedTo(ChronoUnit.MINUTES));
	}

	private JSONObject _toEmbeddedTaxonomyCategoryJSONObject(
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		TaxonomyCategory embeddedTaxonomyCategory = taxonomyCategory.clone();

		embeddedTaxonomyCategory.setActions(
			HashMapBuilder.<String, Map<String, String>>put(
				"add-category",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/taxonomy-categories/",
						taxonomyCategory.getId(), "/taxonomy-categories")
				).put(
					"method", "POST"
				).build()
			).put(
				"delete",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/taxonomy-categories/",
						taxonomyCategory.getId())
				).put(
					"method", "DELETE"
				).build()
			).put(
				"get",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/taxonomy-categories/",
						taxonomyCategory.getId())
				).put(
					"method", "GET"
				).build()
			).put(
				"replace",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/taxonomy-categories/",
						taxonomyCategory.getId())
				).put(
					"method", "PUT"
				).build()
			).put(
				"update",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/taxonomy-categories/",
						taxonomyCategory.getId())
				).put(
					"method", "PATCH"
				).build()
			).build());

		return JSONFactoryUtil.createJSONObject(
			embeddedTaxonomyCategory.toString());
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

	private com.liferay.object.rest.dto.v1_0.FileEntry _toFileEntry(
		Function<byte[], String> encodeFunction, byte[] fileContent,
		String fileName, String folderExternalReferenceCode, Long folderSiteId,
		String mimeType) {

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry =
			new com.liferay.object.rest.dto.v1_0.FileEntry();

		fileEntry.setFileBase64(encodeFunction.apply(fileContent));
		fileEntry.setName(fileName);
		fileEntry.setMimeType(mimeType);

		if ((folderExternalReferenceCode != null) || (folderSiteId != null)) {
			Folder folder = new Folder();

			folder.setExternalReferenceCode(folderExternalReferenceCode);
			folder.setSiteId(folderSiteId);

			fileEntry.setFolder(folder);
		}

		return fileEntry;
	}

	private com.liferay.object.rest.dto.v1_0.FileEntry _toFileEntry(
		String fileName, Long id, String mimeType) {

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry =
			new com.liferay.object.rest.dto.v1_0.FileEntry();

		fileEntry.setName(fileName);
		fileEntry.setId(id);
		fileEntry.setMimeType(mimeType);

		return fileEntry;
	}

	private com.liferay.object.rest.dto.v1_0.FileEntry _toFileEntry(
		String fileName, String fileURL, String folderExternalReferenceCode,
		Long folderSiteId, String mimeType) {

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry =
			new com.liferay.object.rest.dto.v1_0.FileEntry();

		fileEntry.setFileURL(fileURL);
		fileEntry.setName(fileName);
		fileEntry.setMimeType(mimeType);

		if ((folderExternalReferenceCode != null) || (folderSiteId != null)) {
			Folder folder = new Folder();

			folder.setExternalReferenceCode(folderExternalReferenceCode);
			folder.setSiteId(folderSiteId);

			fileEntry.setFolder(folder);
		}

		return fileEntry;
	}

	private JSONObject _toFileEntryJSONObject(
			byte[] fileContent, String fileName, Folder folder, String mimeType,
			String objectFieldName)
		throws Exception {

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry =
			new com.liferay.object.rest.dto.v1_0.FileEntry();

		fileEntry.setFileBase64(Base64.encode(fileContent));
		fileEntry.setMimeType(mimeType);

		if (StringUtil.equals(
				objectFieldName,
				OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE)) {

			fileEntry.setFolder(folder);
		}
		else if (StringUtil.equals(
					objectFieldName,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)) {

			fileEntry.setFolder(
				new Folder() {
					{
						setSiteId(testGroupId);
					}
				});
		}

		fileEntry.setName(fileName);

		return JSONFactoryUtil.createJSONObject(fileEntry.toString());
	}

	private JSONObject _toTaxonomyCategoryJSONObject(
			long groupId, TaxonomyCategory taxonomyCategory,
			boolean withEmbeddedTaxonomyCategory)
		throws Exception {

		JSONObject jsonObject = JSONUtil.put(
			"parentTaxonomyCategory",
			() -> {
				ParentTaxonomyCategory parentTaxonomyCategory =
					taxonomyCategory.getParentTaxonomyCategory();

				if (parentTaxonomyCategory == null) {
					return null;
				}

				return JSONUtil.put(
					"externalReferenceCode",
					parentTaxonomyCategory.getExternalReferenceCode());
			}
		).put(
			"parentTaxonomyVocabulary",
			() -> {
				ParentTaxonomyVocabulary parentTaxonomyVocabulary =
					taxonomyCategory.getParentTaxonomyVocabulary();

				return JSONUtil.put(
					"externalReferenceCode",
					parentTaxonomyVocabulary.getExternalReferenceCode());
			}
		).put(
			"scope",
			() -> {
				if (groupId == taxonomyCategory.getSiteId()) {
					return null;
				}

				Scope scope = Scope.of(
					taxonomyCategory.getSiteId(), LocaleUtil.getDefault());

				return JSONUtil.put(
					"externalReferenceCode", scope.getExternalReferenceCode()
				).put(
					"type", scope.getTypeAsString()
				);
			}
		).put(
			"taxonomyCategoryExternalReferenceCode",
			taxonomyCategory.getExternalReferenceCode()
		).put(
			"taxonomyCategoryId", Long.valueOf(taxonomyCategory.getId())
		).put(
			"taxonomyCategoryName", taxonomyCategory.getName()
		);

		if (!withEmbeddedTaxonomyCategory) {
			return jsonObject;
		}

		return jsonObject.put(
			"embeddedTaxonomyCategory",
			_toEmbeddedTaxonomyCategoryJSONObject(taxonomyCategory));
	}

	private JSONArray _withScopeKey(JSONArray jsonArray, String scopeKey) {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			jsonObject.put("scopeKey", scopeKey);
		}

		return jsonArray;
	}

	private static final String _NEW_OBJECT_FIELD_VALUE_3 =
		RandomTestUtil.randomString();

	@Inject
	private CommentManager _commentManager;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	private ObjectEntry _siteScopedObjectEntry1;

}