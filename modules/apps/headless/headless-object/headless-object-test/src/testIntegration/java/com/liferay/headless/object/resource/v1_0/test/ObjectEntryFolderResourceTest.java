/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.object.client.dto.v1_0.ObjectEntryFolder;
import com.liferay.headless.object.client.pagination.Page;
import com.liferay.headless.object.client.pagination.Pagination;
import com.liferay.headless.object.client.problem.Problem;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class ObjectEntryFolderResourceTest
	extends BaseObjectEntryFolderResourceTestCase {

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_testDepotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null,
			new ServiceContext() {
				{
					setCompanyId(testGroup.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});

		_testDepotEntryGroup = _groupLocalService.getGroup(
			_testDepotEntry.getGroupId());
	}

	@Override
	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String scopeKey =
			testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey();

		ObjectEntryFolder objectEntryFolder1 = randomObjectEntryFolder();

		objectEntryFolder1 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, objectEntryFolder1);

		EntityField titleEntityField = new StringEntityField(
			"title", locale -> Field.getSortableFieldName(Field.TITLE));

		for (EntityField entityField : entityFields) {
			Page<ObjectEntryFolder> page =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null,
						StringBundler.concat(
							getFilterString(
								entityField, "between", objectEntryFolder1),
							" and ",
							getFilterString(
								titleEntityField, "contains",
								objectEntryFolder1)),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectEntryFolder1),
				(List<ObjectEntryFolder>)page.getItems());
		}
	}

	@Override
	@Test
	public void testPatchObjectEntryFolder() throws Exception {
		super.testPatchObjectEntryFolder();

		// Change parent object entry folder to default object entry folder

		ObjectEntryFolder postParentObjectEntryFolder =
			testPatchObjectEntryFolder_addObjectEntryFolder();

		ObjectEntryFolder postObjectEntryFolder1 =
			testPatchObjectEntryFolder_addObjectEntryFolder();

		postObjectEntryFolder1.setParentObjectEntryFolderId(
			postParentObjectEntryFolder.getId());

		objectEntryFolderResource.patchObjectEntryFolder(
			postObjectEntryFolder1.getId(), postObjectEntryFolder1);

		postObjectEntryFolder1.setParentObjectEntryFolderId(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		ObjectEntryFolder patchObjectEntryFolder1 =
			objectEntryFolderResource.patchObjectEntryFolder(
				postObjectEntryFolder1.getId(), postObjectEntryFolder1);

		Assert.assertEquals(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			GetterUtil.getLong(
				patchObjectEntryFolder1.getParentObjectEntryFolderId()));

		// Change parent object entry folder to existing object entry folder

		ObjectEntryFolder postObjectEntryFolder2 =
			testPatchObjectEntryFolder_addObjectEntryFolder();

		postObjectEntryFolder2.setParentObjectEntryFolderId(
			postParentObjectEntryFolder.getId());

		ObjectEntryFolder patchObjectEntryFolder2 =
			objectEntryFolderResource.patchObjectEntryFolder(
				postObjectEntryFolder2.getId(), postObjectEntryFolder2);

		Assert.assertEquals(
			postParentObjectEntryFolder.getId(),
			patchObjectEntryFolder2.getParentObjectEntryFolderId());

		// Preserve existing parent object entry folder ID

		ObjectEntryFolder postObjectEntryFolder3 =
			testPatchObjectEntryFolder_addObjectEntryFolder();

		postObjectEntryFolder3.setParentObjectEntryFolderId(
			postParentObjectEntryFolder.getId());

		objectEntryFolderResource.patchObjectEntryFolder(
			postObjectEntryFolder3.getId(), postObjectEntryFolder3);

		postObjectEntryFolder3.setParentObjectEntryFolderId((Long)null);

		ObjectEntryFolder patchObjectEntryFolder3 =
			objectEntryFolderResource.patchObjectEntryFolder(
				postObjectEntryFolder3.getId(), postObjectEntryFolder3);

		Assert.assertEquals(
			postParentObjectEntryFolder.getId(),
			patchObjectEntryFolder3.getParentObjectEntryFolderId());
	}

	@Override
	@Test
	public void testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		super.testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCode();

		_testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithGroupKey();
	}

	@Override
	@Test
	public void testPostScopeScopeKeyObjectEntryFolder() throws Exception {
		super.testPostScopeScopeKeyObjectEntryFolder();

		_testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderByExternalReferenceCode();
		_testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderByObjectEntryFolderId();
		_testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderDataMismatch();
		_testPostScopeScopeKeyObjectEntryFolderWithNonexistentParentObjectEntryFolderByExternalReferenceCode();
		_testPostScopeScopeKeyObjectEntryFolderWithNonexistentParentObjectEntryFolderByObjectEntryFolderId();
	}

	@Override
	@Test
	public void testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		super.testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode();

		_testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderByExternalReferenceCode();
		_testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderByObjectEntryFolderId();
		_testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderDataMismatch();
		_testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithNonexistentParentObjectEntryFolderByExternalReferenceCode();
		_testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithNonexistentParentObjectEntryFolderByObjectEntryFolderId();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "label", "title"};
	}

	@Override
	protected ObjectEntryFolder randomObjectEntryFolder() throws Exception {
		return new ObjectEntryFolder() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = RandomTestUtil.randomString();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				label = StringUtil.toLowerCase(RandomTestUtil.randomString());
				numberOfObjectEntries = RandomTestUtil.randomInt();
				numberOfObjectEntryFolders = RandomTestUtil.randomInt();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected ObjectEntryFolder
			testDeleteObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected String
			testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolder.getScopeKey();
	}

	@Override
	protected ObjectEntryFolder testGetObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected String
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolder.getScopeKey();
	}

	@Override
	protected ObjectEntryFolder
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				String scopeKey, ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			scopeKey, objectEntryFolder);
	}

	@Override
	protected String testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey() {
		return String.valueOf(_testDepotEntry.getGroupId());
	}

	@Override
	protected String
			testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolder.getScopeKey();
	}

	@Override
	protected ObjectEntryFolder
			testGraphQLObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testPatchObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()), objectEntryFolder);
	}

	@Override
	protected ObjectEntryFolder
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_createObjectEntryFolder()
		throws Exception {

		ObjectEntryFolder objectEntryFolder = randomObjectEntryFolder();

		objectEntryFolder.setScopeKey(
			String.valueOf(_testDepotEntry.getGroupId()));

		return objectEntryFolder;
	}

	@Override
	protected String
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolder.getScopeKey();
	}

	private void _testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithGroupKey()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomPatchObjectEntryFolder =
			randomPatchObjectEntryFolder();

		ObjectEntryFolder patchObjectEntryFolder =
			objectEntryFolderResource.
				patchScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					_testDepotEntryGroup.getGroupKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomPatchObjectEntryFolder);

		ObjectEntryFolder expectedPatchObjectEntryFolder =
			postObjectEntryFolder.clone();

		BeanTestUtil.copyProperties(
			randomPatchObjectEntryFolder, expectedPatchObjectEntryFolder);

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					_testDepotEntryGroup.getGroupKey(),
					patchObjectEntryFolder.getExternalReferenceCode());

		assertEquals(expectedPatchObjectEntryFolder, getObjectEntryFolder);
		assertValid(getObjectEntryFolder);
	}

	private void _testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			parentObjectEntryFolder.getExternalReferenceCode());

		ObjectEntryFolder postObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, postObjectEntryFolder);
		assertValid(postObjectEntryFolder);

		Assert.assertEquals(
			postObjectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode(),
			parentObjectEntryFolder.getExternalReferenceCode());
		Assert.assertEquals(
			postObjectEntryFolder.getParentObjectEntryFolderId(),
			parentObjectEntryFolder.getId());
	}

	private void _testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderByObjectEntryFolderId()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			parentObjectEntryFolder.getId());

		ObjectEntryFolder postObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, postObjectEntryFolder);
		assertValid(postObjectEntryFolder);

		Assert.assertEquals(
			postObjectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode(),
			parentObjectEntryFolder.getExternalReferenceCode());
		Assert.assertEquals(
			postObjectEntryFolder.getParentObjectEntryFolderId(),
			parentObjectEntryFolder.getId());
	}

	private void _testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderDataMismatch()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			parentObjectEntryFolder.getExternalReferenceCode());

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			RandomTestUtil.randomLong());

		try {
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _testPostScopeScopeKeyObjectEntryFolderWithNonexistentParentObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			RandomTestUtil.randomString());

		try {
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _testPostScopeScopeKeyObjectEntryFolderWithNonexistentParentObjectEntryFolderByObjectEntryFolderId()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			RandomTestUtil.randomLong());

		try {
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			parentObjectEntryFolder.getExternalReferenceCode());

		ObjectEntryFolder putObjectEntryFolder =
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					postObjectEntryFolder.getScopeKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, putObjectEntryFolder);
		Assert.assertEquals(
			parentObjectEntryFolder.getExternalReferenceCode(),
			putObjectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode());
		assertValid(putObjectEntryFolder);
	}

	private void _testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderByObjectEntryFolderId()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			parentObjectEntryFolder.getId());

		ObjectEntryFolder putObjectEntryFolder =
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					postObjectEntryFolder.getScopeKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, putObjectEntryFolder);
		Assert.assertEquals(
			parentObjectEntryFolder.getExternalReferenceCode(),
			putObjectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode());
		assertValid(putObjectEntryFolder);
	}

	private void _testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderDataMismatch()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			parentObjectEntryFolder.getExternalReferenceCode());

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			RandomTestUtil.randomLong());

		try {
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					postObjectEntryFolder.getScopeKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomObjectEntryFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithNonexistentParentObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		String parentObjectEntryFolderExternalReferenceCode =
			RandomTestUtil.randomString();

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			parentObjectEntryFolderExternalReferenceCode);

		ObjectEntryFolder putObjectEntryFolder =
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					postObjectEntryFolder.getScopeKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, putObjectEntryFolder);
		Assert.assertEquals(
			parentObjectEntryFolderExternalReferenceCode,
			putObjectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode());
		assertValid(putObjectEntryFolder);

		ObjectEntryFolder parentObjectEntryFolder =
			objectEntryFolderResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					putObjectEntryFolder.getScopeKey(),
					parentObjectEntryFolderExternalReferenceCode);

		Assert.assertEquals(
			parentObjectEntryFolderExternalReferenceCode,
			parentObjectEntryFolder.getExternalReferenceCode());
		assertValid(parentObjectEntryFolder);
	}

	private void _testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithNonexistentParentObjectEntryFolderByObjectEntryFolderId()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			RandomTestUtil.randomLong());

		try {
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					postObjectEntryFolder.getScopeKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomObjectEntryFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private DepotEntry _testDepotEntry;

	private Group _testDepotEntryGroup;

}