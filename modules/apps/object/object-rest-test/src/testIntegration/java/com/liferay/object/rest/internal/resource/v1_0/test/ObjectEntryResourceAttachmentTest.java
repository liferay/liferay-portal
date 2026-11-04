/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
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
import com.liferay.object.configuration.ObjectConfiguration;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.Folder;
import com.liferay.object.rest.dto.v1_0.Link;
import com.liferay.object.rest.test.util.ObjectFieldTestUtil;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.scope.Scope;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;
import com.liferay.portlet.documentlibrary.constants.DLConstants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

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
public class ObjectEntryResourceAttachmentTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

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

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

	@Inject
	private PortletFileRepository _portletFileRepository;

}