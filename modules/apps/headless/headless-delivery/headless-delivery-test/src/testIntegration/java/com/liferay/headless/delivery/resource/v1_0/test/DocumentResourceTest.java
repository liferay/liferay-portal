/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.headless.delivery.client.dto.v1_0.Creator;
import com.liferay.headless.delivery.client.dto.v1_0.Document;
import com.liferay.headless.delivery.client.dto.v1_0.DocumentType;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.DocumentResource;
import com.liferay.headless.delivery.client.serdes.v1_0.DocumentSerDes;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import java.io.File;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class DocumentResourceTest extends BaseDocumentResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testDeleteDocumentMyRating() throws Exception {
		super.testDeleteDocumentMyRating();

		Document document = testDeleteDocumentMyRating_addDocument();

		assertHttpResponseStatusCode(
			204,
			documentResource.deleteDocumentMyRatingHttpResponse(
				document.getId()));
		assertHttpResponseStatusCode(
			404,
			documentResource.deleteDocumentMyRatingHttpResponse(
				document.getId()));

		Document irrelevantDocument = randomIrrelevantDocument();

		assertHttpResponseStatusCode(
			404,
			documentResource.deleteDocumentMyRatingHttpResponse(
				irrelevantDocument.getId()));
	}

	@Override
	@Test
	public void testGetDocument() throws Exception {
		super.testGetDocument();

		Document document1 = documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());

		Assert.assertTrue(Validator.isNotNull(document1.getContentUrl()));
		Assert.assertTrue(Validator.isNotNull(document1.getDateExpired()));
		Assert.assertTrue(Validator.isNotNull(document1.getDatePublished()));
		Assert.assertTrue(Validator.isNotNull(document1.getFriendlyUrlPath()));

		Document document2 = documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(),
			HashMapBuilder.put(
				"file", () -> FileUtil.createTempFile(new byte[0])
			).build());

		Assert.assertTrue(Validator.isNull(document2.getContentUrl()));
		Assert.assertTrue(Validator.isNotNull(document2.getDateExpired()));
		Assert.assertTrue(Validator.isNotNull(document2.getDatePublished()));
		Assert.assertTrue(Validator.isNotNull(document2.getFriendlyUrlPath()));

		Role guestRole = _roleLocalService.getRole(
			testCompany.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.removeResourcePermission(
			testCompany.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(document1.getId()), guestRole.getRoleId(),
			ActionKeys.DOWNLOAD);

		DocumentResource.Builder builder = DocumentResource.builder();

		String password = StringUtil.randomString();

		User user = UserTestUtil.addUser(
			testCompany.getCompanyId(), testCompany.getUserId(), password,
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		DocumentResource regularUserDocumentResource = builder.authentication(
			user.getLogin(), password
		).build();

		document1 = regularUserDocumentResource.getDocument(document1.getId());

		Assert.assertTrue(Validator.isNull(document1.getContentUrl()));
		Assert.assertTrue(Validator.isNotNull(document1.getDateExpired()));
		Assert.assertTrue(Validator.isNotNull(document1.getDatePublished()));
		Assert.assertTrue(Validator.isNotNull(document1.getFriendlyUrlPath()));
	}

	@Override
	@Test
	public void testGetDocumentRenderedContentByDisplayPageDisplayPageKey()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteDocumentMyRating() throws Exception {
		super.testGraphQLDeleteDocumentMyRating();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetDocumentFolderDocumentsPage() throws Exception {
		super.testGraphQLGetDocumentFolderDocumentsPage();
	}

	@Override
	@Test
	public void testGraphQLGetSiteDocumentsPage() throws Exception {
		Document document1 = testGraphQLDocument_addDocument();
		Document document2 = testGraphQLDocument_addDocument();

		JSONObject documentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"documents",
					HashMapBuilder.<String, Object>put(
						"flatten", true
					).put(
						"page", 1
					).put(
						"pageSize", 2
					).put(
						"siteKey", "\"" + testGroup.getGroupId() + "\""
					).build(),
					new GraphQLField("items", getGraphQLFields()),
					new GraphQLField("page"), new GraphQLField("totalCount"))),
			"JSONObject/data", "JSONObject/documents");

		Assert.assertEquals(2, documentsJSONObject.get("totalCount"));

		assertEqualsIgnoringOrder(
			Arrays.asList(document1, document2),
			Arrays.asList(
				DocumentSerDes.toDTOs(documentsJSONObject.getString("items"))));
	}

	@Override
	@Test
	public void testPatchDocument() throws Exception {
		super.testPatchDocument();

		_testPatchDocumentWithDates();
		_testPatchDocumentWithFriendlyUrlPath();
		_testPatchDocumentWithoutFriendlyUrlPath();
	}

	@Override
	@Test
	public void testPostDocumentFolderDocument() throws Exception {
		super.testPostDocumentFolderDocument();

		_testPostDocumentFolderDocumentWithDLFileEntryType();
		_testPostDocumentFolderDocumentWithExternalVideoShortcutDLFileEntryType();
		_testPostDocumentFolderDocumentWithPermission();
	}

	@Override
	@Test
	public void testPostSiteDocument() throws Exception {
		super.testPostSiteDocument();

		_testPostSiteDocumentWithFriendlyUrlPath();
		_testPostSiteDocumentWithNoMultipartFiles();
	}

	@Override
	@Test
	public void testPutDocument() throws Exception {
		super.testPutDocument();

		_testPutSiteDocumentWithFriendlyUrlPath();
		_testPutSiteDocumentWithNoMultipartFiles();
	}

	@Override
	@Test
	public void testPutSiteDocumentByExternalReferenceCode() throws Exception {
		super.testPutSiteDocumentByExternalReferenceCode();

		DLFolder dlFolder1 = _dlFolderLocalService.addFolder(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			testGroup.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		Document randomDocument = randomDocument();

		randomDocument.setDocumentFolderId(dlFolder1.getFolderId());

		Document putDocument =
			documentResource.putSiteDocumentByExternalReferenceCode(
				randomDocument.getSiteId(),
				randomDocument.getExternalReferenceCode(), randomDocument,
				getMultipartFiles());

		Assert.assertEquals(
			(Long)dlFolder1.getFolderId(), putDocument.getDocumentFolderId());

		DLFolder dlFolder2 = _dlFolderLocalService.addFolder(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			testGroup.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		putDocument.setDocumentFolderExternalReferenceCode(
			dlFolder2.getExternalReferenceCode());

		putDocument = documentResource.putSiteDocumentByExternalReferenceCode(
			putDocument.getSiteId(), putDocument.getExternalReferenceCode(),
			putDocument, getMultipartFiles());

		Assert.assertEquals(
			(Long)dlFolder2.getFolderId(), putDocument.getDocumentFolderId());
	}

	@Override
	protected void assertValid(
			Document document, Map<String, File> multipartFiles)
		throws Exception {

		Assert.assertEquals(
			new String(FileUtil.getBytes(multipartFiles.get("file"))),
			_read("http://localhost:8080" + document.getContentUrl()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"description", "fileName", "friendlyUrlPath", "title"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"creatorId", "fileExtension", "sizeInBytes"};
	}

	@Override
	protected Map<String, File> getMultipartFiles() {
		return HashMapBuilder.<String, File>put(
			"file",
			() -> FileUtil.createTempFile(TestDataConstants.TEST_BYTE_ARRAY)
		).build();
	}

	@Override
	protected Document randomDocument() throws Exception {
		Document document = super.randomDocument();

		document.setDateExpired(
			new Date(System.currentTimeMillis() + Time.YEAR));
		document.setDocumentFolderId(0L);
		document.setViewableBy(Document.ViewableBy.ANYONE);

		return document;
	}

	@Override
	protected Long
			testDeleteAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected Document testDeleteDocumentMyRating_addDocument()
		throws Exception {

		Document document = super.testDeleteDocumentMyRating_addDocument();

		documentResource.putDocumentMyRating(document.getId(), randomRating());

		return document;
	}

	@Override
	protected Long
			testGetAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected Document testGetAssetLibraryDocumentsRatedByMePage_addDocument(
			Long assetLibraryId, Document document)
		throws Exception {

		Document addedDocument =
			super.testGetAssetLibraryDocumentsRatedByMePage_addDocument(
				assetLibraryId, document);

		_addDocumentRatingsEntry(addedDocument);

		return addedDocument;
	}

	@Override
	protected Long testGetDocumentFolderDocumentsPage_getDocumentFolderId()
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGuestPermissions(true);

		Folder folder = DLAppLocalServiceUtil.addFolder(
			null, UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			testGroup.getGroupId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		return folder.getFolderId();
	}

	@Override
	protected Document testGetSiteDocumentsRatedByMePage_addDocument(
			Long siteId, Document document)
		throws Exception {

		Document addedDocument =
			super.testGetSiteDocumentsRatedByMePage_addDocument(
				siteId, document);

		_addDocumentRatingsEntry(addedDocument);

		return addedDocument;
	}

	@Override
	protected Document testGraphQLAssetLibraryDocument_addDocument(
			Long assetLibraryId, Document document)
		throws Exception {

		Document addedDocument =
			super.testGetAssetLibraryDocumentsRatedByMePage_addDocument(
				assetLibraryId, document);

		_addDocumentRatingsEntry(addedDocument);

		return addedDocument;
	}

	@Override
	protected Document testGraphQLDocument_addDocument() throws Exception {
		return testPostDocumentFolderDocument_addDocument(
			randomDocument(), getMultipartFiles());
	}

	@Override
	protected Document
			testGraphQLGetAssetLibraryDocumentByExternalReferenceCode_addDocument()
		throws Exception {

		return testGetAssetLibraryDocumentByExternalReferenceCode_addDocument();
	}

	@Override
	protected Long
			testGraphQLGetAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected Document testGraphQLSiteDocument_addDocument(
			Long siteId, Document document)
		throws Exception {

		Document addedDocument =
			super.testGetSiteDocumentsRatedByMePage_addDocument(
				siteId, document);

		_addDocumentRatingsEntry(addedDocument);

		return addedDocument;
	}

	@Override
	protected Long
			testPutAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	private void _addDocumentRatingsEntry(Document document) throws Exception {
		Creator creator = document.getCreator();

		_ratingsEntryLocalService.updateEntry(
			creator.getId(), DLFileEntry.class.getName(), document.getId(), 1.0,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));
	}

	private DLFileEntryType _addFileEntryType(Group group) throws Exception {
		DDMStructure ddmStructure = _ddmStructureLocalService.addStructure(
			null, group.getCreatorUserId(), group.getGroupId(),
			DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
			PortalUtil.getClassNameId(DLFileEntryMetadata.class),
			StringPool.BLANK,
			HashMapBuilder.put(
				LocaleUtil.getDefault(),
				DLFileEntryMetadata.class.getSimpleName()
			).build(),
			new HashMap<>(), StringPool.BLANK, StorageType.DEFAULT.toString(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		return _dlFileEntryTypeLocalService.addFileEntryType(
			null, group.getCreatorUserId(), group.getGroupId(),
			ddmStructure.getStructureId(), null,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			new HashMap<>(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _assertDocumentType(
		DLFileEntryType dlFileEntryType, Document postDocument) {

		DocumentType documentType = postDocument.getDocumentType();

		Assert.assertNotNull(documentType);
		Assert.assertEquals(
			dlFileEntryType.getName(LocaleUtil.getDefault()),
			documentType.getName());
	}

	private Document _postDocumentFolderDocument(
			Document document, Group group, Map<String, File> multipartFiles)
		throws Exception {

		DLFolder dlFolder = _dlFolderLocalService.addFolder(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			group.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		return documentResource.postDocumentFolderDocument(
			dlFolder.getFolderId(), document, multipartFiles);
	}

	private Document _randomDocument(
			DLFileEntryType dlFileEntryType, Group group)
		throws Exception {

		Document randomDocument = randomDocument();

		randomDocument.setDocumentType(
			new DocumentType() {
				{
					name = dlFileEntryType.getName(LocaleUtil.getDefault());
				}
			});
		randomDocument.setSiteId(group.getGroupId());

		return randomDocument;
	}

	private String _read(String url) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.httpMethod(HttpInvoker.HttpMethod.GET);
		httpInvoker.path(url);
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	private void _testPatchDocumentWithDates() throws Exception {
		Document postDocument = testPatchDocument_addDocument();

		Date dateExpired = postDocument.getDateExpired();
		Date datePublished = postDocument.getDatePublished();

		Document document = new Document();

		document.setDescription(RandomTestUtil.randomString(10));

		Document patchDocument = documentResource.patchDocument(
			postDocument.getId(), document, new HashMap<>());

		Document getDocument = documentResource.getDocument(
			patchDocument.getId());

		Assert.assertEquals(dateExpired, getDocument.getDateExpired());
		Assert.assertEquals(datePublished, getDocument.getDatePublished());
	}

	private void _testPatchDocumentWithFriendlyUrlPath() throws Exception {
		Document postDocument = testPatchDocument_addDocument();

		Document document = new Document();

		String friendlyUrlPath = StringUtil.toLowerCase(
			StringUtil.randomString());

		document.setFriendlyUrlPath(friendlyUrlPath);

		Document patchDocument = documentResource.patchDocument(
			postDocument.getId(), document, new HashMap<>());

		Document expectedPatchDocument = postDocument.clone();

		expectedPatchDocument.setFriendlyUrlPath(friendlyUrlPath);

		Document getDocument = documentResource.getDocument(
			patchDocument.getId());

		assertEquals(expectedPatchDocument, getDocument);
		assertValid(getDocument);
	}

	private void _testPatchDocumentWithoutFriendlyUrlPath() throws Exception {
		Document postDocument = testPatchDocument_addDocument();

		Document document = new Document();

		String description = StringUtil.toLowerCase(StringUtil.randomString());

		document.setDescription(description);

		Document patchDocument = documentResource.patchDocument(
			postDocument.getId(), document, new HashMap<>());

		Document expectedPatchDocument = postDocument.clone();

		expectedPatchDocument.setDescription(description);

		Document getDocument = documentResource.getDocument(
			patchDocument.getId());

		assertEquals(expectedPatchDocument, getDocument);
		assertValid(getDocument);
	}

	private void _testPostDocumentFolderDocumentWithDLFileEntryType()
		throws Exception {

		DLFileEntryType dlFileEntryType = _addFileEntryType(testGroup);

		Document document = _randomDocument(dlFileEntryType, testGroup);

		Map<String, File> multipartFiles = getMultipartFiles();

		Document postDocument = _postDocumentFolderDocument(
			document, testGroup, multipartFiles);

		_assertDocumentType(dlFileEntryType, postDocument);

		Group childGroup = GroupTestUtil.addGroup(testGroup.getGroupId());

		document = _randomDocument(dlFileEntryType, childGroup);

		postDocument = _postDocumentFolderDocument(
			document, childGroup, multipartFiles);

		DocumentType documentType = postDocument.getDocumentType();

		Assert.assertEquals(
			dlFileEntryType.getName(LocaleUtil.getDefault()),
			documentType.getName());

		GroupTestUtil.deleteGroup(childGroup);
	}

	private void _testPostDocumentFolderDocumentWithExternalVideoShortcutDLFileEntryType()
		throws Exception {

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getFileEntryType(
				testCompany.getGroupId(), "DL_VIDEO_EXTERNAL_SHORTCUT");

		Document postDocument = _postDocumentFolderDocument(
			_randomDocument(dlFileEntryType, testGroup), testGroup,
			new HashMap<>());

		Assert.assertEquals(
			ContentTypes.APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML,
			postDocument.getEncodingFormat());

		_assertDocumentType(dlFileEntryType, postDocument);
	}

	private void _testPostDocumentFolderDocumentWithPermission()
		throws Exception {

		Document document = _randomDocument(
			_addFileEntryType(testGroup), testGroup);

		Role userRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		document.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(
							new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});
						setRoleExternalReferenceCode(
							userRole.getExternalReferenceCode());
						setRoleName(userRole.getName());
						setRoleType(userRole.getTypeLabel());
					}
				}
			});

		document.setViewableBy(Document.ViewableBy.OWNER);

		Document postDocument = _postDocumentFolderDocument(
			document, testGroup, getMultipartFiles());

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				TestPropsValues.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(postDocument.getId()), userRole.getRoleId());

		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.UPDATE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));
	}

	private void _testPostSiteDocumentWithFriendlyUrlPath() throws Exception {
		Document randomDocument = randomDocument();

		String friendlyUrlPath = StringUtil.toLowerCase(
			StringUtil.randomString());

		randomDocument.setFriendlyUrlPath(friendlyUrlPath);

		Document postDocument = testPostSiteDocument_addDocument(
			randomDocument, Collections.emptyMap());

		Assert.assertEquals(friendlyUrlPath, postDocument.getFriendlyUrlPath());
	}

	private void _testPostSiteDocumentWithNoMultipartFiles() throws Exception {
		Document randomDocument = randomDocument();

		Document postDocument = testPostSiteDocument_addDocument(
			randomDocument, new HashMap<>());

		assertEquals(randomDocument, postDocument);
		assertValid(postDocument);

		Assert.assertEquals(StringPool.BLANK, postDocument.getContentUrl());
		Assert.assertEquals(
			0, GetterUtil.getLong(postDocument.getSizeInBytes()));
	}

	private void _testPutSiteDocumentWithFriendlyUrlPath() throws Exception {
		Document randomDocument = randomDocument();

		Document postDocument = testPostSiteDocument_addDocument(
			randomDocument, Collections.emptyMap());

		String friendlyUrlPath = StringUtil.toLowerCase(
			StringUtil.randomString());

		postDocument.setFriendlyUrlPath(friendlyUrlPath);

		Document putDocument = documentResource.putDocument(
			postDocument.getId(), postDocument, Collections.emptyMap());

		Assert.assertEquals(friendlyUrlPath, putDocument.getFriendlyUrlPath());
	}

	private void _testPutSiteDocumentWithNoMultipartFiles() throws Exception {
		Document postDocument = testPutDocument_addDocument();

		Document randomDocument = randomDocument();

		Document putDocument = documentResource.putDocument(
			postDocument.getId(), randomDocument, new HashMap<>());

		assertEquals(randomDocument, putDocument);
		assertValid(putDocument);

		Assert.assertEquals(
			"1.1",
			HttpComponentsUtil.getParameter(
				putDocument.getContentUrl(), "version", false));

		Assert.assertEquals(
			postDocument.getSizeInBytes(), putDocument.getSizeInBytes());
	}

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private RatingsEntryLocalService _ratingsEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}