/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.client.dto.v1_0.StructuredContentFolder;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.problem.Problem;
import com.liferay.headless.delivery.client.resource.v1_0.StructuredContentFolderResource;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.util.PropsValues;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class StructuredContentFolderResourceTest
	extends BaseStructuredContentFolderResourceTestCase {

	@Override
	@Test
	public void testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode()
		throws Exception {

		super.
			testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode();

		String externalReferenceCode = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		try {
			structuredContentFolderResource.
				deleteAssetLibraryStructuredContentFolderByExternalReferenceCode(
					testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					externalReferenceCode);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}

		StructuredContentFolder parentStructuredContentFolder1 =
			testPostAssetLibraryStructuredContentFolder_addStructuredContentFolder(
				_randomStructuredContentFolder());

		StructuredContentFolder postStructuredContentFolder1 =
			structuredContentFolderResource.
				postStructuredContentFolderStructuredContentFolder(
					parentStructuredContentFolder1.getId(),
					_randomStructuredContentFolder());

		assertHttpResponseStatusCode(
			204,
			structuredContentFolderResource.
				deleteAssetLibraryStructuredContentFolderByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					postStructuredContentFolder1.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			structuredContentFolderResource.
				getAssetLibraryStructuredContentFolderByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					postStructuredContentFolder1.getExternalReferenceCode()));

		StructuredContentFolder parentStructuredContentFolder2 =
			testPostAssetLibraryStructuredContentFolder_addStructuredContentFolder(
				_randomStructuredContentFolder());

		StructuredContentFolder postStructuredContentFolder2 =
			structuredContentFolderResource.
				postStructuredContentFolderStructuredContentFolder(
					parentStructuredContentFolder2.getId(),
					_randomStructuredContentFolder());

		assertHttpResponseStatusCode(
			204,
			structuredContentFolderResource.
				deleteAssetLibraryStructuredContentFolderByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					parentStructuredContentFolder2.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			structuredContentFolderResource.
				getAssetLibraryStructuredContentFolderByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					parentStructuredContentFolder2.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			structuredContentFolderResource.
				getAssetLibraryStructuredContentFolderByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					postStructuredContentFolder2.getExternalReferenceCode()));

		StructuredContentFolder randomStructuredContentFolder =
			_randomStructuredContentFolder();

		randomStructuredContentFolder.setExternalReferenceCode("");

		StructuredContentFolder postStructuredContentFolder3 =
			structuredContentFolderResource.
				postAssetLibraryStructuredContentFolder(
					testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					randomStructuredContentFolder);

		JournalFolder journalFolder = JournalFolderLocalServiceUtil.getFolder(
			postStructuredContentFolder3.getId());

		assertHttpResponseStatusCode(
			204,
			structuredContentFolderResource.
				deleteAssetLibraryStructuredContentFolderByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					journalFolder.getUuid()));

		assertHttpResponseStatusCode(
			404,
			structuredContentFolderResource.
				getAssetLibraryStructuredContentFolderByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					postStructuredContentFolder3.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testDeleteSiteStructuredContentFolderByExternalReferenceCode()
		throws Exception {

		super.testDeleteSiteStructuredContentFolderByExternalReferenceCode();

		StructuredContentFolder randomStructuredContentFolder =
			_randomStructuredContentFolder();

		randomStructuredContentFolder.setExternalReferenceCode("");

		StructuredContentFolder postStructuredContentFolder =
			structuredContentFolderResource.postSiteStructuredContentFolder(
				testGroup.getGroupId(), randomStructuredContentFolder);

		JournalFolder journalFolder = JournalFolderLocalServiceUtil.getFolder(
			postStructuredContentFolder.getId());

		assertHttpResponseStatusCode(
			204,
			structuredContentFolderResource.
				deleteSiteStructuredContentFolderByExternalReferenceCodeHttpResponse(
					postStructuredContentFolder.getSiteId(),
					journalFolder.getUuid()));
		assertHttpResponseStatusCode(
			404,
			structuredContentFolderResource.
				getSiteStructuredContentFolderByExternalReferenceCodeHttpResponse(
					postStructuredContentFolder.getSiteId(),
					journalFolder.getUuid()));
	}

	@Override
	@Test
	public void testGetAssetLibraryStructuredContentFolderByExternalReferenceCode()
		throws Exception {

		super.
			testGetAssetLibraryStructuredContentFolderByExternalReferenceCode();

		StructuredContentFolder parentStructuredContentFolder1 =
			testPostAssetLibraryStructuredContentFolder_addStructuredContentFolder(
				_randomStructuredContentFolder());

		StructuredContentFolder postStructuredContentFolder1 =
			structuredContentFolderResource.
				postStructuredContentFolderStructuredContentFolder(
					parentStructuredContentFolder1.getId(),
					_randomStructuredContentFolder());

		StructuredContentFolder getStructuredContentFolder1 =
			structuredContentFolderResource.
				getAssetLibraryStructuredContentFolderByExternalReferenceCode(
					testGetAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					postStructuredContentFolder1.getExternalReferenceCode());

		assertEquals(postStructuredContentFolder1, getStructuredContentFolder1);
		assertValid(getStructuredContentFolder1);

		StructuredContentFolder getParentStructuredContentFolder =
			structuredContentFolderResource.
				getAssetLibraryStructuredContentFolderByExternalReferenceCode(
					testGetAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					parentStructuredContentFolder1.getExternalReferenceCode());

		assertEquals(
			parentStructuredContentFolder1, getParentStructuredContentFolder);
		assertValid(getParentStructuredContentFolder);

		StructuredContentFolder parentStructuredContentFolder2 =
			testPostAssetLibraryStructuredContentFolder_addStructuredContentFolder(
				_randomStructuredContentFolder());

		StructuredContentFolder randomStructuredContentFolder =
			_randomStructuredContentFolder();

		randomStructuredContentFolder.setExternalReferenceCode("");

		StructuredContentFolder postStructuredContentFolder2 =
			structuredContentFolderResource.
				postStructuredContentFolderStructuredContentFolder(
					parentStructuredContentFolder2.getId(),
					randomStructuredContentFolder);

		JournalFolder journalFolder = JournalFolderLocalServiceUtil.getFolder(
			postStructuredContentFolder2.getId());

		StructuredContentFolder getStructuredContentFolder2 =
			structuredContentFolderResource.
				getAssetLibraryStructuredContentFolderByExternalReferenceCode(
					testGetAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					journalFolder.getUuid());

		assertEquals(postStructuredContentFolder2, getStructuredContentFolder2);
		assertValid(getStructuredContentFolder2);

		String externalReferenceCode = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		try {
			structuredContentFolderResource.
				getAssetLibraryStructuredContentFolderByExternalReferenceCode(
					testGetAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId(),
					externalReferenceCode);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}

		long assetLibraryId = RandomTestUtil.randomLong();

		try {
			structuredContentFolderResource.
				getAssetLibraryStructuredContentFolderByExternalReferenceCode(
					assetLibraryId,
					postStructuredContentFolder1.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	@Test
	public void testGetSiteStructuredContentFolderByExternalReferenceCode()
		throws Exception {

		super.testGetSiteStructuredContentFolderByExternalReferenceCode();

		StructuredContentFolder randomStructuredContentFolder =
			_randomStructuredContentFolder();

		randomStructuredContentFolder.setExternalReferenceCode("");

		StructuredContentFolder postStructuredContentFolder =
			structuredContentFolderResource.postSiteStructuredContentFolder(
				testGroup.getGroupId(), randomStructuredContentFolder);

		JournalFolder journalFolder = JournalFolderLocalServiceUtil.getFolder(
			postStructuredContentFolder.getId());

		StructuredContentFolder getStructuredContentFolder =
			structuredContentFolderResource.
				getSiteStructuredContentFolderByExternalReferenceCode(
					postStructuredContentFolder.getSiteId(),
					journalFolder.getUuid());

		assertEquals(postStructuredContentFolder, getStructuredContentFolder);
		assertValid(getStructuredContentFolder);
	}

	@Override
	@Test
	public void testGetStructuredContentFolderStructuredContentFoldersPage()
		throws Exception {

		super.testGetStructuredContentFolderStructuredContentFoldersPage();

		StructuredContentFolder postStructuredContentFolder =
			structuredContentFolderResource.
				postAssetLibraryStructuredContentFolder(
					testDepotEntry.getDepotEntryId(),
					_randomStructuredContentFolder());

		StructuredContentFolderResource.Builder builder =
			StructuredContentFolderResource.builder();

		structuredContentFolderResource = builder.authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "profileURL"
		).build();

		Page<StructuredContentFolder> page =
			structuredContentFolderResource.
				getAssetLibraryStructuredContentFoldersPage(
					testDepotEntry.getDepotEntryId(), null, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		Assert.assertEquals(
			postStructuredContentFolder.getId(),
			page.fetchFirstItem(
			).getId());

		Assert.assertNotNull(
			page.fetchFirstItem(
			).getCreator(
			).getProfileURL());

		assertValid(page);
	}

	@Override
	@Test
	public void testPostAssetLibraryStructuredContentFolder() throws Exception {
		super.testPostAssetLibraryStructuredContentFolder();

		_assertStructuredContentFolder(
			structuredContentFolder ->
				testPostAssetLibraryStructuredContentFolder_addStructuredContentFolder(
					structuredContentFolder));

		StructuredContentFolder postStructuredContentFolder =
			testPostAssetLibraryStructuredContentFolder_addStructuredContentFolder(
				_randomStructuredContentFolder());

		StructuredContentFolder randomStructuredContentFolder =
			_randomStructuredContentFolder();

		randomStructuredContentFolder.setExternalReferenceCode(
			postStructuredContentFolder.getExternalReferenceCode());

		try {
			structuredContentFolderResource.
				postAssetLibraryStructuredContentFolder(
					testDepotEntry.getDepotEntryId(),
					randomStructuredContentFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				_language.get(
					LocaleUtil.getDefault(),
					"this-external-reference-code-is-already-in-use"),
				problem.getTitle());
		}
	}

	@Override
	@Test
	public void testPostSiteStructuredContentFolder() throws Exception {
		super.testPostSiteStructuredContentFolder();

		StructuredContentFolder randomStructuredContentFolder =
			_randomStructuredContentFolder();

		randomStructuredContentFolder.setExternalReferenceCode("");

		StructuredContentFolder postStructuredContentFolder =
			testPostSiteStructuredContentFolder_addStructuredContentFolder(
				randomStructuredContentFolder);

		JournalFolder journalFolder = JournalFolderLocalServiceUtil.getFolder(
			postStructuredContentFolder.getId());

		Assert.assertEquals(
			journalFolder.getUuid(),
			postStructuredContentFolder.getExternalReferenceCode());

		assertValid(postStructuredContentFolder);
	}

	@Override
	@Test
	public void testPostStructuredContentFolderStructuredContentFolder()
		throws Exception {

		super.testPostStructuredContentFolderStructuredContentFolder();

		StructuredContentFolder parentStructuredContentFolder =
			testPostAssetLibraryStructuredContentFolder_addStructuredContentFolder(
				_randomStructuredContentFolder());

		_assertStructuredContentFolder(
			structuredContentFolder ->
				structuredContentFolderResource.
					postStructuredContentFolderStructuredContentFolder(
						parentStructuredContentFolder.getId(),
						structuredContentFolder));
	}

	@Override
	@Test
	public void testPutAssetLibraryStructuredContentFolderByExternalReferenceCode()
		throws Exception {

		super.
			testPutAssetLibraryStructuredContentFolderByExternalReferenceCode();

		StructuredContentFolder parentStructuredContentFolder1 =
			testPostAssetLibraryStructuredContentFolder_addStructuredContentFolder(
				_randomStructuredContentFolder());

		StructuredContentFolder postStructuredContentFolder1 =
			structuredContentFolderResource.
				postStructuredContentFolderStructuredContentFolder(
					parentStructuredContentFolder1.getId(),
					_randomStructuredContentFolder());

		StructuredContentFolder randomStructuredContentFolder1 =
			new StructuredContentFolder() {
				{
					name = postStructuredContentFolder1.getName();
				}
			};

		StructuredContentFolder putStructuredContentFolder1 =
			structuredContentFolderResource.
				putAssetLibraryStructuredContentFolderByExternalReferenceCode(
					testDepotEntry.getDepotEntryId(),
					StringUtil.toLowerCase(RandomTestUtil.randomString()),
					randomStructuredContentFolder1);

		Assert.assertEquals(
			postStructuredContentFolder1.getName(),
			putStructuredContentFolder1.getName());
		Assert.assertNotEquals(
			postStructuredContentFolder1.getExternalReferenceCode(),
			putStructuredContentFolder1.getExternalReferenceCode());

		assertValid(putStructuredContentFolder1);

		StructuredContentFolder randomStructuredContentFolder2 =
			_randomStructuredContentFolder();

		StructuredContentFolder structuredContentFolder =
			testPostAssetLibraryStructuredContentFolder_addStructuredContentFolder(
				randomStructuredContentFolder2);

		randomStructuredContentFolder2.setExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		StructuredContentFolder putStructuredContentFolder2 =
			structuredContentFolderResource.
				putAssetLibraryStructuredContentFolderByExternalReferenceCode(
					testDepotEntry.getDepotEntryId(),
					structuredContentFolder.getExternalReferenceCode(),
					randomStructuredContentFolder2);

		assertEquals(putStructuredContentFolder2, structuredContentFolder);
		assertValid(putStructuredContentFolder2);

		StructuredContentFolder parentStructuredContentFolder2 =
			testPostAssetLibraryStructuredContentFolder_addStructuredContentFolder(
				_randomStructuredContentFolder());

		StructuredContentFolder postStructuredContentFolder2 =
			structuredContentFolderResource.
				postStructuredContentFolderStructuredContentFolder(
					parentStructuredContentFolder2.getId(),
					_randomStructuredContentFolder());

		StructuredContentFolder randomStructuredContentFolder3 =
			new StructuredContentFolder() {
				{
					name = StringUtil.toLowerCase(
						RandomTestUtil.randomString());
				}
			};

		StructuredContentFolder putStructuredContentFolder3 =
			structuredContentFolderResource.
				putAssetLibraryStructuredContentFolderByExternalReferenceCode(
					testDepotEntry.getDepotEntryId(),
					postStructuredContentFolder2.getExternalReferenceCode(),
					randomStructuredContentFolder3);

		Assert.assertEquals(
			randomStructuredContentFolder3.getName(),
			putStructuredContentFolder3.getName());
		Assert.assertEquals(
			postStructuredContentFolder2.getExternalReferenceCode(),
			putStructuredContentFolder3.getExternalReferenceCode());

		assertValid(putStructuredContentFolder3);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "name"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"creatorId"};
	}

	@Override
	protected Long
			testDeleteAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected Long
			testGetAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected Long
			testGetStructuredContentFolderStructuredContentFoldersPage_getIrrelevantParentStructuredContentFolderId()
		throws Exception {

		JournalFolder journalFolder = JournalTestUtil.addFolder(
			irrelevantGroup.getGroupId(), RandomTestUtil.randomString());

		return journalFolder.getFolderId();
	}

	@Override
	protected Long
			testGetStructuredContentFolderStructuredContentFoldersPage_getParentStructuredContentFolderId()
		throws Exception {

		JournalFolder journalFolder = JournalTestUtil.addFolder(
			testGroup.getGroupId(), RandomTestUtil.randomString());

		return journalFolder.getFolderId();
	}

	@Override
	protected StructuredContentFolder
			testGraphQLGetAssetLibraryStructuredContentFolderByExternalReferenceCode_addStructuredContentFolder()
		throws Exception {

		return testGetAssetLibraryStructuredContentFolderByExternalReferenceCode_addStructuredContentFolder();
	}

	@Override
	protected Long
			testGraphQLGetAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected StructuredContentFolder
			testPutAssetLibraryStructuredContentFolderByExternalReferenceCode_createStructuredContentFolder()
		throws Exception {

		return _randomStructuredContentFolder();
	}

	@Override
	protected Long
			testPutAssetLibraryStructuredContentFolderByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected StructuredContentFolder
			testPutSiteStructuredContentFolderByExternalReferenceCode_createStructuredContentFolder()
		throws Exception {

		return _randomStructuredContentFolder();
	}

	private void _assertStructuredContentFolder(
			UnsafeFunction
				<StructuredContentFolder, StructuredContentFolder, Exception>
					postStructuredContentFolderUnsafeFunction)
		throws Exception {

		StructuredContentFolder randomStructuredContentFolder =
			_randomStructuredContentFolder();

		randomStructuredContentFolder.setExternalReferenceCode("");

		StructuredContentFolder postStructuredContentFolder =
			postStructuredContentFolderUnsafeFunction.apply(
				randomStructuredContentFolder);

		JournalFolder journalFolder = JournalFolderLocalServiceUtil.getFolder(
			postStructuredContentFolder.getId());

		Assert.assertEquals(
			postStructuredContentFolder.getExternalReferenceCode(),
			journalFolder.getUuid());

		assertValid(postStructuredContentFolder);
	}

	private StructuredContentFolder _randomStructuredContentFolder()
		throws Exception {

		return new StructuredContentFolder() {
			{
				assetLibraryKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				numberOfStructuredContentFolders = RandomTestUtil.randomInt();
				numberOfStructuredContents = RandomTestUtil.randomInt();
				parentStructuredContentFolderId = 0L;
				siteId = testGroup.getGroupId();
				subscribed = RandomTestUtil.randomBoolean();
			}
		};
	}

	@Inject
	private Language _language;

}