/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
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
import java.util.Random;
import java.util.function.Function;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 * @author Magdalena Jedraszak
 * @author Daniel Raposo
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

	@Before
	@Override
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryResourceAttachmentTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistrations = Arrays.asList(
			bundleContext.registerService(
				ModelListener.class, _testDLFileEntryModelListener, null),
			bundleContext.registerService(
				ModelListener.class, _testObjectEntryModelListener, null));

		super.setUp();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);

		super.tearDown();
	}

	@Test
	public void testGetObjectEntryWithAttachmentObjectField() throws Exception {
		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		FileEntry fileEntry = addTempFileEntry(
			DLTestUtil.randomTextFileBytes(), objectDefinition1,
			RandomTestUtil.randomString());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				fileEntry.getFileEntryId()
			).toString(),
			objectDefinition1.getRESTContextPath(), Http.Method.POST);

		Group testGroup = groupLocalService.getGroup(testGroupId);

		JSONObject scopeJSONObject = JSONUtil.put(
			"externalReferenceCode", testGroup.getExternalReferenceCode()
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
			content, objectDefinition1, RandomTestUtil.randomString());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				fileEntry.getFileEntryId()
			).toString(),
			StringBundler.concat(
				objectDefinition1.getRESTContextPath(), "?nestedFields=",
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
			objectDefinition1.getRESTContextPath() +
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
				objectDefinition1.getRESTContextPath(),
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
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				dlFileEntry.getFileEntryId()
			).toString(),
			objectDefinition1.getRESTContextPath(), Http.Method.POST);

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
			objectDefinition1.getRESTContextPath() +
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
				objectDefinition1.getRESTContextPath(),
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
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
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
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
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

		FileEntry pdfFileEntry = _addFileEntry(
			FileUtil.getBytes(getClass(), "dependencies/document.pdf"),
			ContentTypes.APPLICATION_PDF, "pdf");

		DLFileEntry pdfDLFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			pdfFileEntry.getFileEntryId());

		ObjectDefinition objectDefinition2 = getObjectDefinition2();

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				pdfDLFileEntry.getFileEntryId()
			).toString(),
			objectDefinition2.getRESTContextPath(), Http.Method.POST);

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
				objectDefinition2.getRESTContextPath(),
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
				ContentTypes.IMAGE_JPEG, "jpg"),
			"Square", "Large (Bigger Than 1024)");
		_testGetObjectEntryWithAttachmentObjectFieldImageMetadata(
			_addFileEntry(
				FileUtil.getBytes(
					getClass(), "dependencies/sample_medium_tall_600x800.jpg"),
				ContentTypes.IMAGE_JPEG, "jpg"),
			"Tall", "Medium (Up to 1024x768)");
		_testGetObjectEntryWithAttachmentObjectFieldImageMetadata(
			_addFileEntry(
				FileUtil.getBytes(
					getClass(), "dependencies/sample_small_wide_400x300.jpg"),
				ContentTypes.IMAGE_JPEG, "jpg"),
			"Wide", "Small (Up to 400x300)");
		_testGetObjectEntryWithAttachmentObjectFieldImageMetadata(
			_addFileEntry(
				RandomTestUtil.randomBytes(), ContentTypes.TEXT_PLAIN, "txt"),
			null, null);
	}

	@Test
	public void testPatchCustomObjectEntryWithAttachmentObjectFieldScope()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		ObjectDefinition objectDefinition = getObjectDefinition1();

		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			objectDefinition.getResourceName(), role);

		String userName = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);
		String userPassword = RandomTestUtil.randomString();

		User user = addUser(userName, userPassword);

		roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		HTTPTestUtil.customize(
		).withCredentials(
			userName + "@liferay.com", userPassword
		).apply(
			() -> {

				// Default scope

				JSONObject objectEntryJSONObject =
					HTTPTestUtil.invokeToJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
						).toString(),
						objectDefinition.getRESTContextPath(),
						Http.Method.POST);

				String endpoint = StringBundler.concat(
					objectDefinition.getRESTContextPath(), StringPool.SLASH,
					objectEntryJSONObject.get("id"));

				com.liferay.object.rest.dto.v1_0.FileEntry fileEntry1 =
					_toFileEntryWithFileBase64(
						Base64.encode(DLTestUtil.randomTextFileBytes()),
						RandomTestUtil.randomString() + ".txt", null, null);

				fileEntry1.setExternalReferenceCode(
					RandomTestUtil.randomString(
						UniqueStringRandomizerBumper.INSTANCE));

				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
						JSONFactoryUtil.createJSONObject(fileEntry1.toString())
					).toString(),
					endpoint, Http.Method.PATCH);

				Company company = companyLocalService.getCompany(
					objectDefinition.getCompanyId());

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
					_toFileEntryWithFileBase64(
						Base64.encode(DLTestUtil.randomTextFileBytes()),
						RandomTestUtil.randomString() + ".txt", null, null);

				fileEntry2.setExternalReferenceCode(
					RandomTestUtil.randomString(
						UniqueStringRandomizerBumper.INSTANCE));
				fileEntry2.setScope(scope);

				JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
						JSONFactoryUtil.createJSONObject(fileEntry2.toString())
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
					JSONFactoryUtil.createJSONObject(scope.toString()));

				userLocalService.deleteGroupUser(
					group.getGroupId(), user.getUserId());

				// User who cannot add documents to the site

				com.liferay.object.rest.dto.v1_0.FileEntry fileEntry3 =
					_toFileEntryWithFileBase64(
						Base64.encode(DLTestUtil.randomTextFileBytes()),
						RandomTestUtil.randomString() + ".txt", null, null);

				fileEntry3.setExternalReferenceCode(
					RandomTestUtil.randomString(
						UniqueStringRandomizerBumper.INSTANCE));
				fileEntry3.setScope(scope);

				jsonObject = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
						JSONFactoryUtil.createJSONObject(fileEntry3.toString())
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

		_testPatchObjectEntryWithAttachmentObjectFieldId(String::valueOf);
		_testPatchObjectEntryWithAttachmentObjectFieldId(
			fileEntryId -> JSONUtil.put("id", fileEntryId));
	}

	@Test
	@TestInfo("LPD-79894")
	public void testPatchObjectEntryWithKeywordsAndExistingAttachmentObjectField()
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition1();

		JSONObject objectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_createFileEntryJSONObject(
					null, OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ObjectMapperUtil.class.getName(), LoggerTestUtil.WARN)) {

			JSONObject patchedJSONObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"keywords", JSONUtil.putAll("tag1")
				).toString(),
				objectDefinition.getRESTContextPath() + StringPool.SLASH +
					objectEntryJSONObject.getString("id"),
				Http.Method.PATCH);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());

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

			Assert.assertEquals(1, keywordsJSONArray.length());
			Assert.assertEquals("tag1", keywordsJSONArray.get(0));
		}
	}

	@Test
	public void testPatchPutCustomObjectEntryWithAttachmentObjectField()
		throws Exception {

		_testPatchPutCustomObjectEntryWithAttachmentObjectField(
			Http.Method.PATCH, getObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAttachmentObjectField(
			Http.Method.PATCH, getObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAttachmentObjectField(
			Http.Method.PATCH, getSiteScopedObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAttachmentObjectField(
			Http.Method.PATCH, getSiteScopedObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAttachmentObjectField(
			Http.Method.PUT, getObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAttachmentObjectField(
			Http.Method.PUT, getObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAttachmentObjectField(
			Http.Method.PUT, getSiteScopedObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAttachmentObjectField(
			Http.Method.PUT, getSiteScopedObjectDefinition1(), true);
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

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		JSONObject objectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				JSONFactoryUtil.createJSONObject(
					String.valueOf(
						_toFileEntryWithFileBase64(
							Base64.encode(DLTestUtil.randomTextFileBytes()),
							RandomTestUtil.randomString() + ".txt", null,
							null)))
			).toString(),
			StringBundler.concat(
				objectDefinition1.getRESTContextPath(), "?nestedFields=",
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

		String randomString = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);

		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"A" + randomString,
				Collections.singletonList(
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
						objectDefinition2.getRESTContextPath(),
						Http.Method.POST);

					Assert.assertEquals(
						"NOT_FOUND", jsonObject.getString("status"));

					jsonObject = HTTPTestUtil.invokeToJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
							JSONUtil.put("id", RandomTestUtil.randomLong())
						).toString(),
						objectDefinition2.getRESTContextPath(),
						Http.Method.POST);

					Assert.assertEquals(
						"BAD_REQUEST", jsonObject.getString("status"));

					jsonObject = HTTPTestUtil.invokeToJSONObject(
						JSONUtil.put(
							OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
							JSONUtil.put("id", fileEntry.getId())
						).toString(),
						objectDefinition2.getRESTContextPath(),
						Http.Method.POST);

					Assert.assertEquals(
						"FORBIDDEN", jsonObject.getString("status"));
				}
			);
		}
		finally {
			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition2);
		}
	}

	@Test
	public void testPostCustomObjectEntryWithEmptyStringAttachmentObjectField()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ObjectMapperUtil.class.getName(), LoggerTestUtil.WARN)) {

			ObjectDefinition objectDefinition = getObjectDefinition1();

			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					StringPool.BLANK
				).toString(),
				objectDefinition.getRESTContextPath(), Http.Method.POST);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());

			Assert.assertNull(
				jsonObject.get(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE));
		}
	}

	@Test
	public void testPostCustomObjectEntryWithLargeAttachmentObjectField()
		throws Exception {

		String randomString = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);

		String objectFieldName = "x" + randomString;

		ObjectField objectField = ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
			ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
			objectFieldName, objectFieldName,
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
					"100"
				).build()),
			false);

		ObjectDefinition objectDefinition = getObjectDefinition1();

		objectField.setObjectDefinitionId(
			objectDefinition.getObjectDefinitionId());

		ObjectFieldTestUtil.addCustomObjectField(
			TestPropsValues.getUserId(), objectField);

		byte[] content = new byte[60_000_000];

		Random random = new Random();

		random.nextBytes(content);

		String fileBase64 = Base64.encode(content);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				objectFieldName,
				JSONUtil.put(
					"fileBase64", fileBase64
				).put(
					"name", RandomTestUtil.randomString() + ".txt"
				)
			).toString(),
			StringBundler.concat(
				objectDefinition.getRESTContextPath(), "?nestedFields=",
				objectFieldName, ".fileBase64"),
			Http.Method.POST);

		_assertAttachmentJSONObject(
			_dlFileEntryLocalService.getDLFileEntry(
				_testDLFileEntryModelListener.getLastFileEntryId()),
			fileBase64, jsonObject.getJSONObject(objectFieldName),
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
			_publishObjectDefinitionWithLocalizedAttachmentObjectField();

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry =
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null);

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

		JSONObject fileEntryJSONObject =
			localizedAttachmentI18nJSONObject.getJSONObject("en_US");

		Assert.assertEquals(
			fileEntry.getFileBase64(),
			fileEntryJSONObject.getString("fileBase64"));

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testPutCustomObjectEntryWithLocalizedAttachmentObjectField()
		throws Exception {

		// File with a nonexistent external reference code

		ObjectDefinition objectDefinition =
			_publishObjectDefinitionWithLocalizedAttachmentObjectField();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry1 =
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null);

		fileEntry1.setExternalReferenceCode(
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE));

		Scope scope = Scope.of(group, LocaleUtil.getDefault());

		fileEntry1.setScope(scope);

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry2 =
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null);

		fileEntry2.setExternalReferenceCode(
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE));
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

		JSONObject localizedAttachmentI18nJSONObject = jsonObject.getJSONObject(
			"localizedAttachment_i18n");

		_assertAttachmentJSONObject(
			null, fileEntry1.getFileBase64(),
			localizedAttachmentI18nJSONObject.getJSONObject("en_US"),
			JSONFactoryUtil.createJSONObject(scope.toString()));
		_assertAttachmentJSONObject(
			null, fileEntry2.getFileBase64(),
			localizedAttachmentI18nJSONObject.getJSONObject("pt_BR"),
			JSONFactoryUtil.createJSONObject(scope.toString()));

		// File with an existing external reference code

		com.liferay.portal.kernel.repository.model.Folder existingFolder =
			_dlAppLocalService.addFolder(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		DLFileEntry existingDLFileEntry = _addDLFileEntry(
			new byte[0], existingFolder.getFolderId());

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry3 =
			_toFileEntryWithFileBase64(
				null, existingDLFileEntry.getFileName(), null, testGroupId);

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

		localizedAttachmentI18nJSONObject = jsonObject.getJSONObject(
			"localizedAttachment_i18n");

		JSONObject fileEntryJSONObject =
			localizedAttachmentI18nJSONObject.getJSONObject("en_US");

		Assert.assertEquals(
			fileEntry3.getExternalReferenceCode(),
			fileEntryJSONObject.getString("externalReferenceCode"));

		fileEntryJSONObject = localizedAttachmentI18nJSONObject.getJSONObject(
			"pt_BR");

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
			ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			new ByteArrayInputStream(content), 0, null, null, null,
			ServiceContextTestUtil.getServiceContext());

		return _dlFileEntryLocalService.getDLFileEntry(
			fileEntry.getFileEntryId());
	}

	private FileEntry _addFileEntry(
			byte[] content, String contentType, String extension)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringBundler.concat(
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE),
				StringPool.PERIOD, extension),
			contentType,
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), content, null, null, null,
			ServiceContextTestUtil.getServiceContext());
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

	private void _assertJSONObjectWithAttachmentObjectField(
			JSONObject expectedJSONObject, JSONObject jsonObject,
			String objectFieldName)
		throws Exception {

		JSONAssert.assertEquals(
			expectedJSONObject.toString(), jsonObject.toString(),
			JSONCompareMode.LENIENT);

		JSONObject expectedAttachmentJSONObject =
			expectedJSONObject.getJSONObject(objectFieldName);

		if (expectedAttachmentJSONObject != null) {
			JSONAssert.assertEquals(
				expectedAttachmentJSONObject.toString(),
				String.valueOf(jsonObject.get(objectFieldName)),
				JSONCompareMode.STRICT);
		}
	}

	private void _assertPatchPutCustomObjectEntryWithAttachmentObjectField(
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
				_createFileEntryJSONObject(
					fileEntry.getFolder(), objectFieldName)
			).toString(),
			getEndpoint(objectDefinition, testGroupId), Http.Method.POST);

		String endpoint =
			objectDefinition.getRESTContextPath() + StringPool.SLASH +
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

		_assertJSONObjectWithAttachmentObjectField(
			expectedJSONObjectUnsafeFunction.apply(fileEntry), jsonObject,
			objectFieldName);
	}

	private void _assertPatchPutCustomObjectEntryWithInvalidFileURL(
			String expectedTitle, String fileURL, Http.Method httpMethod,
			ObjectDefinition objectDefinition, boolean useExternalReferenceCode)
		throws Exception {

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", expectedTitle
			),
			_toFileEntryWithFileURL(
				RandomTestUtil.randomString() + ".txt", fileURL, null),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);
	}

	private void _assertPostCustomObjectEntryWithAttachmentObjectField(
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

		_assertJSONObjectWithAttachmentObjectField(
			expectedJSONObjectUnsafeFunction.apply(fileEntry), jsonObject,
			objectFieldName);
	}

	private void _assertPostCustomObjectEntryWithInvalidFileURL(
			String expectedTitle, String fileURL,
			ObjectDefinition objectDefinition)
		throws Exception {

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", expectedTitle
			),
			_toFileEntryWithFileURL(
				RandomTestUtil.randomString() + ".txt", fileURL,
				group.getGroupId()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);
	}

	private JSONObject _createFileEntryJSONObject(
			Folder folder, String objectFieldName)
		throws Exception {

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry =
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null);

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
						siteId = testGroupId;
					}
				});
		}

		return JSONFactoryUtil.createJSONObject(fileEntry.toString());
	}

	private DLFolder _getDLFolder(
			ObjectDefinition objectDefinition, boolean showFilesInLibrary)
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

		if (showFilesInLibrary) {
			return _dlFolderLocalService.fetchFolder(
				groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				objectDefinition.getShortName());
		}

		Repository repository = _portletFileRepository.getPortletRepository(
			groupId, objectDefinition.getPortletId());

		return _dlFolderLocalService.getFolder(
			repository.getGroupId(), repository.getDlFolderId(),
			String.valueOf(TestPropsValues.getUserId()));
	}

	private JSONObject _getFileEntryJSONObject(
			DLFolder dlFolder,
			com.liferay.object.rest.dto.v1_0.FileEntry fileEntry,
			ObjectDefinition objectDefinition, String objectFieldName)
		throws Exception {

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			_testDLFileEntryModelListener.getLastFileEntryId());

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			_testObjectEntryModelListener.getLastObjectEntryId());

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), objectFieldName);

		JSONObject jsonObject = JSONUtil.put(
			"alternativeText", dlFileVersion.getDescription()
		).put(
			"extension", dlFileEntry.getExtension()
		).put(
			"externalReferenceCode", dlFileEntry.getExternalReferenceCode()
		).put(
			"id", Long.valueOf(dlFileEntry.getFileEntryId())
		).put(
			"link",
			() -> {
				Link link = new Link();

				long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
				long repositoryId = 0;

				if (dlFolder == null) {
					Folder folder = fileEntry.getFolder();

					if (folder == null) {
						Scope scope = fileEntry.getScope();

						if ((scope != null) &&
							(scope.getExternalReferenceCode() != null)) {

							Group scopeGroup =
								groupLocalService.
									fetchGroupByExternalReferenceCode(
										scope.getExternalReferenceCode(),
										objectDefinition.getCompanyId());

							if (scopeGroup != null) {
								repositoryId = scopeGroup.getGroupId();
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

				Date modifiedDate = dlFileVersion.getModifiedDate();

				String groupExternalReferenceCode = StringPool.BLANK;

				if (!StringUtil.equals(
						objectDefinition.getScope(),
						ObjectDefinitionConstants.SCOPE_COMPANY)) {

					Group objectEntryGroup = groupLocalService.fetchGroup(
						objectEntry.getGroupId());

					groupExternalReferenceCode =
						objectEntryGroup.getExternalReferenceCode();
				}

				link.setHref(
					StringBundler.concat(
						"/documents/", repositoryId, StringPool.SLASH, folderId,
						StringPool.SLASH,
						URLCodec.encodeURL(fileEntry.getName()),
						StringPool.SLASH, dlFileEntry.getUuid(), "?version=",
						dlFileVersion.getVersion(), "&t=",
						modifiedDate.getTime(),
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

				Group dlFileEntryGroup = groupLocalService.getGroup(
					dlFileEntry.getGroupId());

				Scope.Type type =
					(dlFileEntryGroup.getType() == GroupConstants.TYPE_DEPOT) ?
						Scope.Type.ASSET_LIBRARY : Scope.Type.SITE;

				Scope scope = Scope.ofReference(
					dlFileEntryGroup.getExternalReferenceCode(), type);

				return JSONFactoryUtil.createJSONObject(scope.toString());
			}
		).put(
			"size",
			LanguageUtil.formatStorageSize(
				dlFileEntry.getSize(), LocaleUtil.getDefault())
		);

		String fileSource = ObjectFieldSettingUtil.getValue(
			ObjectFieldSettingConstants.NAME_FILE_SOURCE, objectField);

		if ((StringUtil.equals(
				fileSource,
				ObjectFieldSettingConstants.
					VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT) ||
			 StringUtil.equals(
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

	private ObjectDefinition
			_publishObjectDefinitionWithLocalizedAttachmentObjectField()
		throws Exception {

		String randomString = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);

		String objectDefinitionName = "A" + randomString;

		return ObjectDefinitionTestUtil.publishObjectDefinition(
			objectDefinitionName,
			Collections.singletonList(
				new AttachmentObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap("localizedAttachment")
				).localized(
					true
				).name(
					"localizedAttachment"
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
			ObjectDefinitionConstants.SCOPE_COMPANY);
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

		ObjectDefinition objectDefinition = getObjectDefinition1();

		JSONObject objectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				fileEntry.getFileEntryId()
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		RawMetadataProcessorUtil.saveMetadata(fileEntry.getFileVersion());

		objectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntryJSONObject.getString("id"), "?nestedFields=",
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				".metadata"),
			Http.Method.GET);

		JSONObject attachmentJSONObject = objectEntryJSONObject.getJSONObject(
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);

		JSONObject metadataJSONObject = attachmentJSONObject.getJSONObject(
			"metadata");

		Assert.assertEquals(
			expectedAspectRatio, metadataJSONObject.opt("aspectRatio"));
		Assert.assertEquals(
			expectedResolution, metadataJSONObject.opt("resolution"));
	}

	private void _testPatchObjectEntryWithAttachmentObjectFieldId(
			Function<Long, Object> function)
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition1();

		JSONObject objectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_createFileEntryJSONObject(
					null, OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ObjectMapperUtil.class.getName(), LoggerTestUtil.WARN)) {

			JSONObject attachmentJSONObject =
				objectEntryJSONObject.getJSONObject(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

			JSONObject patchedJSONObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					function.apply(attachmentJSONObject.getLong("id"))
				).toString(),
				objectDefinition.getRESTContextPath() + StringPool.SLASH +
					objectEntryJSONObject.getString("id"),
				Http.Method.PATCH);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());

			JSONObject patchedAttachmentJSONObject =
				patchedJSONObject.getJSONObject(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

			Assert.assertEquals(
				attachmentJSONObject.getLong("id"),
				patchedAttachmentJSONObject.getLong("id"));
		}
	}

	private void _testPatchPutCustomObjectEntryWithAttachmentObjectField(
			Http.Method httpMethod, ObjectDefinition objectDefinition,
			boolean useExternalReferenceCode)
		throws Exception {

		// CMS basic document source, file from Base64

		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null,
				depotEntry.getGroupId()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE,
			useExternalReferenceCode);

		// Documents and media source, file from Base64

		DLFolder dlFolder1 = DLTestUtil.addDLFolder(testGroupId);

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					dlFolder1, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				dlFolder1.getExternalReferenceCode(), dlFolder1.getGroupId()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		DLFolder dlFolder2 = DLTestUtil.addDLFolder(group.getGroupId());

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					dlFolder2, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				dlFolder2.getExternalReferenceCode(), dlFolder2.getGroupId()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);
		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null,
				group.getGroupId()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);
		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, testGroupId),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		// Documents and media source, file from Base64 with external reference
		// code

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put("status", "NOT_FOUND"),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), testGroupId),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		com.liferay.portal.kernel.repository.model.Folder existingFolder =
			_dlAppLocalService.addFolder(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		byte[] fileContent = DLTestUtil.randomTextFileBytes();

		DLFileEntry existingDLFileEntry = _addDLFileEntry(
			fileContent, existingFolder.getFolderId());

		com.liferay.object.rest.dto.v1_0.FileEntry testFileEntry =
			_toFileEntryWithFileBase64(
				Base64.encode(fileContent), existingDLFileEntry.getFileName(),
				null, testGroupId);

		String externalReferenceCode1 =
			existingDLFileEntry.getExternalReferenceCode();

		testFileEntry.setExternalReferenceCode(externalReferenceCode1);

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				JSONObject jsonObject = _getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					jsonObject.put(
						"externalReferenceCode", externalReferenceCode1));
			},
			testFileEntry, httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		testFileEntry = _toFileEntryWithFileBase64(
			null, RandomTestUtil.randomString() + ".txt", null, null);

		String externalReferenceCode2 = RandomTestUtil.randomString();

		testFileEntry.setExternalReferenceCode(externalReferenceCode2);

		Group scopeGroup = GroupTestUtil.addGroup();

		testFileEntry.setScope(Scope.of(scopeGroup, LocaleUtil.getDefault()));

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				JSONObject jsonObject = _getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					jsonObject.put(
						"externalReferenceCode", externalReferenceCode2));
			},
			testFileEntry, httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		FileEntry serviceBuilderFileEntry = _dlAppLocalService.getFileEntry(
			_testDLFileEntryModelListener.getLastFileEntryId());

		Assert.assertEquals(
			externalReferenceCode2,
			serviceBuilderFileEntry.getExternalReferenceCode());
		Assert.assertEquals(
			scopeGroup.getGroupId(), serviceBuilderFileEntry.getGroupId());

		InputStream inputStream = serviceBuilderFileEntry.getContentStream();

		Assert.assertEquals(-1, inputStream.read());

		// Documents and media source, file from Base64 with nested fields

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
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
					));
			},
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				dlFolder1.getExternalReferenceCode(), dlFolder1.getGroupId()),
			httpMethod, "fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
			useExternalReferenceCode);

		// Documents and media source, file from Base64 without add document
		// permission

		String userName = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);
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

				_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
					fileEntry -> JSONUtil.put("status", "FORBIDDEN"),
					_toFileEntryWithFileBase64(
						Base64.encode(DLTestUtil.randomTextFileBytes()),
						RandomTestUtil.randomString() + ".txt",
						dlFolder.getExternalReferenceCode(),
						dlFolder.getGroupId()),
					httpMethod, null, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					useExternalReferenceCode);

				resourcePermissionLocalService.setResourcePermissions(
					TestPropsValues.getCompanyId(),
					DLFolderConstants.getClassName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(dlFolder.getFolderId()), role.getRoleId(),
					new String[] {ActionKeys.ADD_DOCUMENT});

				_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
					fileEntry -> JSONUtil.put(
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
						_getFileEntryJSONObject(
							dlFolder, fileEntry, objectDefinition,
							OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
					_toFileEntryWithFileBase64(
						Base64.encode(DLTestUtil.randomTextFileBytes()),
						RandomTestUtil.randomString() + ".txt",
						dlFolder.getExternalReferenceCode(),
						dlFolder.getGroupId()),
					httpMethod, null, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					useExternalReferenceCode);
			}
		);

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
		FileEntry textFileEntry = _addFileEntry(
			DLTestUtil.randomTextFileBytes(),
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN);

		String previewFileURL = StringBundler.concat(
			"http://", company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false),
			_dlURLHelper.getPreviewURL(
				textFileEntry, textFileEntry.getFileVersion(), null,
				StringPool.BLANK, false, true));

		String hostNotAllowedMessage = StringBundler.concat(
			"Unable to download file from ", previewFileURL, " because host ",
			company.getVirtualHostname(), " is not allowed");

		// Documents and media source, file from local URL with host allowed
		// and local network access disabled

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[] {company.getVirtualHostname()}, false)) {

			_assertPatchPutCustomObjectEntryWithInvalidFileURL(
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

			_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
				fileEntry -> JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					_getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
				_toFileEntryWithFileURL(
					RandomTestUtil.randomString() + ".txt", previewFileURL,
					null),
				httpMethod, null, objectDefinition,
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				useExternalReferenceCode);
		}

		// Documents and media source, file from local URL with local network
		// access disabled

		_assertPatchPutCustomObjectEntryWithInvalidFileURL(
			hostNotAllowedMessage, previewFileURL, httpMethod, objectDefinition,
			useExternalReferenceCode);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[0], true)) {

			// Documents and media source, file from local URL with local
			// network access enabled

			_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
				fileEntry -> JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					_getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
				_toFileEntryWithFileURL(
					RandomTestUtil.randomString() + ".txt", previewFileURL,
					null),
				httpMethod, null, objectDefinition,
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				useExternalReferenceCode);

			_testUnreachableFileURLs(
				company,
				(expectedTitle, fileURL) ->
					_assertPatchPutCustomObjectEntryWithInvalidFileURL(
						expectedTitle, fileURL, httpMethod, objectDefinition,
						useExternalReferenceCode));
		}

		// Documents and media source, file from URL with host not allowed

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[] {"www.able.com"}, true)) {

			_assertPatchPutCustomObjectEntryWithInvalidFileURL(
				hostNotAllowedMessage, previewFileURL, httpMethod,
				objectDefinition, useExternalReferenceCode);
		}

		// Documents and media source, file from URL with unsupported protocol

		String jarFileURL = StringBundler.concat(
			"jar:file:///", RandomTestUtil.randomString(),
			".jar!/META-INF/MANIFEST.MF");

		_assertPatchPutCustomObjectEntryWithInvalidFileURL(
			"Unable to download file from " + jarFileURL +
				" because protocol jar is not supported",
			jarFileURL, httpMethod, objectDefinition, useExternalReferenceCode);

		String unsupportedProtocolFileURL = StringBundler.concat(
			"file://", company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false));

		_assertPatchPutCustomObjectEntryWithInvalidFileURL(
			"Unable to download file from " + unsupportedProtocolFileURL +
				" because protocol file is not supported",
			unsupportedProtocolFileURL, httpMethod, objectDefinition,
			useExternalReferenceCode);

		// User computer to CMS basic document source, file from Base64

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE,
			useExternalReferenceCode);

		// User computer to documents and media source, file from Base64

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				StringBundler.concat(
					"File ", fileEntry.getName(),
					" exceeds the maximum permitted size of ",
					MAX_FILE_SIZE_VALUE, " MB")
			),
			_toFileEntryWithFileBase64(
				Base64.encode(
					DLTestUtil.randomTextFileBytes(
						(MAX_FILE_SIZE_VALUE * 1024 * 1024) + 1)),
				RandomTestUtil.randomString() + ".txt", null, null),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "File name is null"
			),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()), null, null,
				null),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Invalid file extension for " + fileEntry.getName()
			),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".err", null, null),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Unable to decode Base64 file"
			),
			_toFileEntryWithFileBase64(
				new String(
					ArrayUtil.append(
						DLTestUtil.randomTextFileBytes(), (byte)'$')),
				RandomTestUtil.randomString() + ".txt", null, null),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), RandomTestUtil.randomLong()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
			useExternalReferenceCode);
		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), RandomTestUtil.randomLong()),
			httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
			useExternalReferenceCode);

		// User computer to documents and media source, file from Base64 with
		// existing name

		testFileEntry = _toFileEntryWithFileBase64(
			Base64.encode(DLTestUtil.randomTextFileBytes()),
			RandomTestUtil.randomString() + ".txt", null, null);

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			testFileEntry, httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
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

		testFileEntry = _toFileEntryWithFileBase64(
			Base64.encode(DLTestUtil.randomTextFileBytes()),
			RandomTestUtil.randomString() + ".txt", null, null);

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			testFileEntry, httpMethod, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
			useExternalReferenceCode);
		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
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

		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				JSONObject jsonObject = _getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
					jsonObject.put("fileBase64", fileEntry.getFileBase64()));
			},
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			httpMethod, "fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
			useExternalReferenceCode);
		_assertPatchPutCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				JSONObject jsonObject = _getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
					jsonObject.put("fileBase64", fileEntry.getFileBase64()));
			},
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			httpMethod, "fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
			useExternalReferenceCode);
	}

	private void _testPostCustomObjectEntryWithAttachmentObjectField(
			ObjectDefinition objectDefinition)
		throws Exception {

		// CMS basic document source, file from Base64

		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null,
				depotEntry.getGroupId()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE);

		// Documents and media source, file from Base64

		DLFolder dlFolder1 = DLTestUtil.addDLFolder(testGroupId);

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					dlFolder1, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				dlFolder1.getExternalReferenceCode(), dlFolder1.getGroupId()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		DLFolder dlFolder2 = DLTestUtil.addDLFolder(group.getGroupId());

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					dlFolder2, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				dlFolder2.getExternalReferenceCode(), dlFolder2.getGroupId()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null,
				group.getGroupId()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				_getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, testGroupId),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		// Documents and media source, file from Base64 with external reference
		// code

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put("status", "NOT_FOUND"),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), testGroupId),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		com.liferay.portal.kernel.repository.model.Folder existingFolder =
			_dlAppLocalService.addFolder(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		byte[] fileContent = DLTestUtil.randomTextFileBytes();

		DLFileEntry existingDLFileEntry = _addDLFileEntry(
			fileContent, existingFolder.getFolderId());

		com.liferay.object.rest.dto.v1_0.FileEntry testFileEntry =
			_toFileEntryWithFileBase64(
				Base64.encode(fileContent), existingDLFileEntry.getFileName(),
				null, testGroupId);

		String externalReferenceCode1 =
			existingDLFileEntry.getExternalReferenceCode();

		testFileEntry.setExternalReferenceCode(externalReferenceCode1);

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				JSONObject jsonObject = _getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					jsonObject.put(
						"externalReferenceCode", externalReferenceCode1));
			},
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		testFileEntry = _toFileEntryWithFileBase64(
			null, RandomTestUtil.randomString() + ".txt", null, null);

		String externalReferenceCode2 = RandomTestUtil.randomString();

		testFileEntry.setExternalReferenceCode(externalReferenceCode2);

		Group scopeGroup = GroupTestUtil.addGroup();

		testFileEntry.setScope(Scope.of(scopeGroup, LocaleUtil.getDefault()));

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				JSONObject jsonObject = _getFileEntryJSONObject(
					null, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					jsonObject.put(
						"externalReferenceCode", externalReferenceCode2));
			},
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		FileEntry serviceBuilderFileEntry = _dlAppLocalService.getFileEntry(
			_testDLFileEntryModelListener.getLastFileEntryId());

		Assert.assertEquals(
			externalReferenceCode2,
			serviceBuilderFileEntry.getExternalReferenceCode());
		Assert.assertEquals(
			scopeGroup.getGroupId(), serviceBuilderFileEntry.getGroupId());

		InputStream inputStream = serviceBuilderFileEntry.getContentStream();

		Assert.assertEquals(-1, inputStream.read());

		// Documents and media source, file from Base64 with nested fields

		_assertPostCustomObjectEntryWithAttachmentObjectField(
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
					));
			},
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				dlFolder1.getExternalReferenceCode(), dlFolder1.getGroupId()),
			"fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		// Documents and media source, file from Base64 without add document
		// permission

		String userName = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);
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

				_assertPostCustomObjectEntryWithAttachmentObjectField(
					fileEntry -> JSONUtil.put("status", "FORBIDDEN"),
					_toFileEntryWithFileBase64(
						Base64.encode(DLTestUtil.randomTextFileBytes()),
						RandomTestUtil.randomString() + ".txt",
						dlFolder.getExternalReferenceCode(),
						dlFolder.getGroupId()),
					null, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

				resourcePermissionLocalService.setResourcePermissions(
					TestPropsValues.getCompanyId(),
					DLFolderConstants.getClassName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(dlFolder.getFolderId()), role.getRoleId(),
					new String[] {ActionKeys.ADD_DOCUMENT});

				_assertPostCustomObjectEntryWithAttachmentObjectField(
					fileEntry -> JSONUtil.put(
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
						_getFileEntryJSONObject(
							dlFolder, fileEntry, objectDefinition,
							OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
					_toFileEntryWithFileBase64(
						Base64.encode(DLTestUtil.randomTextFileBytes()),
						RandomTestUtil.randomString() + ".txt",
						dlFolder.getExternalReferenceCode(),
						dlFolder.getGroupId()),
					null, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);
			}
		);

		// Documents and media source, file from generated thumbnail

		DLFolder dlFolder3 = DLTestUtil.addDLFolder(testGroupId);

		FileEntry imageFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), testGroupId,
			dlFolder3.getFolderId(), RandomTestUtil.randomString() + ".jpg",
			ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null, ServiceContextTestUtil.getServiceContext());

		String thumbnailSrc = _dlURLHelper.getThumbnailSrc(
			imageFileEntry, null);

		Assert.assertNotNull(thumbnailSrc);

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				JSONObject jsonObject = _getFileEntryJSONObject(
					dlFolder3, fileEntry, objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					jsonObject.put("thumbnailURL", thumbnailSrc));
			},
			new com.liferay.object.rest.dto.v1_0.FileEntry() {
				{
					id = imageFileEntry.getFileEntryId();
					mimeType = imageFileEntry.getMimeType();
					name = imageFileEntry.getFileName();
				}
			},
			"thumbnailURL", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
		FileEntry textFileEntry = _addFileEntry(
			DLTestUtil.randomTextFileBytes(),
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN);

		String previewFileURL = StringBundler.concat(
			"http://", company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false),
			_dlURLHelper.getPreviewURL(
				textFileEntry, textFileEntry.getFileVersion(), null,
				StringPool.BLANK, false, true));

		String hostNotAllowedMessage = StringBundler.concat(
			"Unable to download file from ", previewFileURL, " because host ",
			company.getVirtualHostname(), " is not allowed");

		// Documents and media source, file from local URL with host allowed
		// and local network access disabled

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[] {company.getVirtualHostname()}, false)) {

			_assertPostCustomObjectEntryWithInvalidFileURL(
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

			_assertPostCustomObjectEntryWithAttachmentObjectField(
				fileEntry -> JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					_getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
				_toFileEntryWithFileURL(
					RandomTestUtil.randomString() + ".txt", previewFileURL,
					group.getGroupId()),
				null, objectDefinition,
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);
		}

		// Documents and media source, file from local URL with local network
		// access disabled

		_assertPostCustomObjectEntryWithInvalidFileURL(
			hostNotAllowedMessage, previewFileURL, objectDefinition);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[0], true)) {

			// Documents and media source, file from local URL with local
			// network access enabled

			FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
				testGroupId, TestPropsValues.getUserId(),
				RandomTestUtil.randomString(),
				TempFileEntryUtil.getTempFileName(
					RandomTestUtil.randomString() + ".txt"),
				FileUtil.createTempFile(DLTestUtil.randomTextFileBytes()),
				ContentTypes.TEXT_PLAIN);

			_assertPostCustomObjectEntryWithAttachmentObjectField(
				fileEntry -> JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					_getFileEntryJSONObject(
						null, fileEntry, objectDefinition,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
				_toFileEntryWithFileURL(
					tempFileEntry.getTitle(), previewFileURL,
					group.getGroupId()),
				null, objectDefinition,
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE);

			_testUnreachableFileURLs(
				company,
				(expectedTitle, fileURL) ->
					_assertPostCustomObjectEntryWithInvalidFileURL(
						expectedTitle, fileURL, objectDefinition));
		}

		// Documents and media source, file from URL with host not allowed

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper = _swapObjectConfiguration(
					new String[] {"www.able.com"}, true)) {

			_assertPostCustomObjectEntryWithInvalidFileURL(
				hostNotAllowedMessage, previewFileURL, objectDefinition);
		}

		// Documents and media source, file from URL with unsupported protocol

		String jarFileURL = StringBundler.concat(
			"jar:file:///", RandomTestUtil.randomString(),
			".jar!/META-INF/MANIFEST.MF");

		_assertPostCustomObjectEntryWithInvalidFileURL(
			"Unable to download file from " + jarFileURL +
				" because protocol jar is not supported",
			jarFileURL, objectDefinition);

		String unsupportedProtocolFileURL = StringBundler.concat(
			"file://", company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false));

		_assertPostCustomObjectEntryWithInvalidFileURL(
			"Unable to download file from " + unsupportedProtocolFileURL +
				" because protocol file is not supported",
			unsupportedProtocolFileURL, objectDefinition);

		// User computer to CMS basic document source, file from Base64

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE);

		// User computer to documents and media source, file from Base64

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				StringBundler.concat(
					"File ", fileEntry.getName(),
					" exceeds the maximum permitted size of ",
					MAX_FILE_SIZE_VALUE, " MB")
			),
			_toFileEntryWithFileBase64(
				Base64.encode(
					DLTestUtil.randomTextFileBytes(
						(MAX_FILE_SIZE_VALUE * 1024 * 1024) + 1)),
				RandomTestUtil.randomString() + ".txt", null, null),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "File content is empty"
			),
			_toFileEntryWithFileBase64(
				Base64.encode(new byte[0]),
				RandomTestUtil.randomString() + ".txt", null, null),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "File name is null"
			),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()), null, null,
				null),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Invalid file extension for " + fileEntry.getName()
			),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".err", null, null),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Unable to decode Base64 file"
			),
			_toFileEntryWithFileBase64(
				new String(
					ArrayUtil.append(
						DLTestUtil.randomTextFileBytes(), (byte)'$')),
				RandomTestUtil.randomString() + ".txt", null, null),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), RandomTestUtil.randomLong()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt",
				RandomTestUtil.randomString(), RandomTestUtil.randomLong()),
			null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

		// User computer to documents and media source, file from Base64 with
		// existing name

		testFileEntry = _toFileEntryWithFileBase64(
			Base64.encode(DLTestUtil.randomTextFileBytes()),
			RandomTestUtil.randomString() + ".txt", null, null);

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1)),
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
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

		testFileEntry = _toFileEntryWithFileBase64(
			Base64.encode(DLTestUtil.randomTextFileBytes()),
			RandomTestUtil.randomString() + ".txt", null, null);

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> JSONUtil.put(
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				_getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2)),
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
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

		testFileEntry = _toFileEntryWithFileBase64(
			Base64.encode(fileContent), existingDLFileEntry.getFileName(), null,
			testGroupId);

		String externalReferenceCode3 =
			existingDLFileEntry.getExternalReferenceCode();

		testFileEntry.setExternalReferenceCode(externalReferenceCode3);

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				JSONObject jsonObject = _getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
					jsonObject.put(
						"externalReferenceCode", externalReferenceCode3));
			},
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

		testFileEntry = _toFileEntryWithFileBase64(
			null, RandomTestUtil.randomString() + ".txt", null, null);

		String externalReferenceCode4 = RandomTestUtil.randomString();

		testFileEntry.setExternalReferenceCode(externalReferenceCode4);

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				JSONObject jsonObject = _getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
					jsonObject.put(
						"externalReferenceCode", externalReferenceCode4));
			},
			testFileEntry, null, objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

		serviceBuilderFileEntry = _dlAppLocalService.getFileEntry(
			_testDLFileEntryModelListener.getLastFileEntryId());

		Assert.assertEquals(
			externalReferenceCode4,
			serviceBuilderFileEntry.getExternalReferenceCode());

		inputStream = serviceBuilderFileEntry.getContentStream();

		Assert.assertEquals(-1, inputStream.read());

		// User computer to documents and media source, file from Base64 with
		// nested fields

		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				JSONObject jsonObject = _getFileEntryJSONObject(
					_getDLFolder(objectDefinition, false), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
					jsonObject.put("fileBase64", fileEntry.getFileBase64()));
			},
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			"fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1);
		_assertPostCustomObjectEntryWithAttachmentObjectField(
			fileEntry -> {
				JSONObject jsonObject = _getFileEntryJSONObject(
					_getDLFolder(objectDefinition, true), fileEntry,
					objectDefinition,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);

				return JSONUtil.put(
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
					jsonObject.put("fileBase64", fileEntry.getFileBase64()));
			},
			_toFileEntryWithFileBase64(
				Base64.encode(DLTestUtil.randomTextFileBytes()),
				RandomTestUtil.randomString() + ".txt", null, null),
			"fileBase64,folder", objectDefinition,
			OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2);
	}

	private void _testUnreachableFileURLs(
			Company company,
			UnsafeBiConsumer<String, String, Exception> unsafeBiConsumer)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.object.rest.internal.manager.v1_0." +
					"DefaultObjectEntryManagerImpl",
				LoggerTestUtil.ERROR)) {

			// Documents and media source, file from URL and host down

			String hostDownFileURL = StringBundler.concat(
				"http://", company.getVirtualHostname(), ":",
				PortalUtil.getPortalServerPort(false) + 1);

			unsafeBiConsumer.accept(
				"Unable to download file from " + hostDownFileURL,
				hostDownFileURL);

			// Documents and media source, file from URL malformed

			String malformedFileURL = StringBundler.concat(
				"http//", company.getVirtualHostname(), ":",
				PortalUtil.getPortalServerPort(false), StringPool.SLASH,
				RandomTestUtil.randomString());

			unsafeBiConsumer.accept(
				"Unable to download file from " + malformedFileURL,
				malformedFileURL);
		}

		// Documents and media source, file from URL not found

		String notFoundPath = StringPool.SLASH + RandomTestUtil.randomString();

		String notFoundFileURL = StringBundler.concat(
			"http://", company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false), notFoundPath);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			unsafeBiConsumer.accept(
				"Unable to download file from " + notFoundFileURL +
					" because of unexpected HTTP response code 404",
				notFoundFileURL);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(LoggerTestUtil.WARN, logEntry.getPriority());

			String message = logEntry.getMessage();

			Assert.assertTrue(message.contains("code=\"404\""));
			Assert.assertTrue(message.contains("uri=" + notFoundPath));
		}
	}

	private com.liferay.object.rest.dto.v1_0.FileEntry
		_toFileEntryWithFileBase64(
			String fileBase64, String fileName,
			String folderExternalReferenceCode, Long folderSiteId) {

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry =
			new com.liferay.object.rest.dto.v1_0.FileEntry();

		fileEntry.setFileBase64(fileBase64);
		fileEntry.setMimeType(ContentTypes.TEXT_PLAIN);
		fileEntry.setName(fileName);

		if ((folderExternalReferenceCode != null) || (folderSiteId != null)) {
			Folder folder = new Folder();

			folder.setExternalReferenceCode(folderExternalReferenceCode);
			folder.setSiteId(folderSiteId);

			fileEntry.setFolder(folder);
		}

		return fileEntry;
	}

	private com.liferay.object.rest.dto.v1_0.FileEntry _toFileEntryWithFileURL(
		String fileName, String fileURL, Long folderSiteId) {

		com.liferay.object.rest.dto.v1_0.FileEntry fileEntry =
			new com.liferay.object.rest.dto.v1_0.FileEntry();

		fileEntry.setFileURL(fileURL);
		fileEntry.setMimeType(ContentTypes.TEXT_PLAIN);
		fileEntry.setName(fileName);

		if (folderSiteId != null) {
			Folder folder = new Folder();

			folder.setSiteId(folderSiteId);

			fileEntry.setFolder(folder);
		}

		return fileEntry;
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

	private List<ServiceRegistration<?>> _serviceRegistrations;
	private final TestDLFileEntryModelListener _testDLFileEntryModelListener =
		new TestDLFileEntryModelListener();
	private final TestObjectEntryModelListener _testObjectEntryModelListener =
		new TestObjectEntryModelListener();

	private static class TestDLFileEntryModelListener
		extends BaseModelListener<DLFileEntry> {

		public long getLastFileEntryId() {
			return _lastFileEntryId;
		}

		@Override
		public void onAfterCreate(DLFileEntry dlFileEntry)
			throws ModelListenerException {

			_lastFileEntryId = dlFileEntry.getFileEntryId();
		}

		private long _lastFileEntryId;

	}

	private static class TestObjectEntryModelListener
		extends BaseModelListener<ObjectEntry> {

		public long getLastObjectEntryId() {
			return _lastObjectEntryId;
		}

		@Override
		public void onAfterCreate(ObjectEntry objectEntry)
			throws ModelListenerException {

			_lastObjectEntryId = objectEntry.getObjectEntryId();
		}

		private long _lastObjectEntryId;

	}

}