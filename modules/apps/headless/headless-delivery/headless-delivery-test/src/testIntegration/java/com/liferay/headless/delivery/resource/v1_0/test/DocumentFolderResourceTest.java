/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.headless.delivery.client.dto.v1_0.Creator;
import com.liferay.headless.delivery.client.dto.v1_0.DocumentFolder;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class DocumentFolderResourceTest
	extends BaseDocumentFolderResourceTestCase {

	@Override
	@Test
	public void testDeleteDocumentFolderMyRating() throws Exception {
		super.testDeleteDocumentFolderMyRating();

		DocumentFolder documentFolder =
			testDeleteDocumentFolderMyRating_addDocumentFolder();

		assertHttpResponseStatusCode(
			204,
			documentFolderResource.deleteDocumentFolderMyRatingHttpResponse(
				documentFolder.getId()));
		assertHttpResponseStatusCode(
			404,
			documentFolderResource.deleteDocumentFolderMyRatingHttpResponse(
				documentFolder.getId()));

		DocumentFolder irrelevantDocumentFolder =
			randomIrrelevantDocumentFolder();

		assertHttpResponseStatusCode(
			404,
			documentFolderResource.deleteDocumentFolderMyRatingHttpResponse(
				irrelevantDocumentFolder.getId()));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteDocumentFolderMyRating() throws Exception {
		super.testGraphQLDeleteDocumentFolderMyRating();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetDocumentFolderDocumentFoldersPage()
		throws Exception {

		super.testGraphQLGetDocumentFolderDocumentFoldersPage();
	}

	@Override
	@Test
	public void testPostDocumentFolderDocumentFolder() throws Exception {
		super.testPostDocumentFolderDocumentFolder();

		_testPostDocumentFolderDocumentFolderWithPermission();
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
	protected DocumentFolder
			testDeleteDocumentFolderMyRating_addDocumentFolder()
		throws Exception {

		DocumentFolder documentFolder =
			super.testDeleteDocumentFolderMyRating_addDocumentFolder();

		documentFolderResource.putDocumentFolderMyRating(
			documentFolder.getId(), randomRating());

		return documentFolder;
	}

	@Override
	protected DocumentFolder
			testGetAssetLibraryDocumentFoldersRatedByMePage_addDocumentFolder(
				Long assetLibraryId, DocumentFolder documentFolder)
		throws Exception {

		DocumentFolder addedDocumentFolder =
			super.
				testGetAssetLibraryDocumentFoldersRatedByMePage_addDocumentFolder(
					assetLibraryId, documentFolder);

		_addDocumentFolderRatingsEntry(addedDocumentFolder);

		return addedDocumentFolder;
	}

	@Override
	protected DocumentFolder testGetDocumentFolder_addDocumentFolder()
		throws Exception {

		DocumentFolder postDocumentFolder =
			documentFolderResource.postSiteDocumentFolder(
				testGroup.getGroupId(), randomDocumentFolder());

		Assert.assertEquals(
			Integer.valueOf(0), postDocumentFolder.getNumberOfDocuments());

		DLAppTestUtil.addFileEntryWithWorkflow(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			testGroup.getGroupId(), postDocumentFolder.getId(),
			StringPool.BLANK, RandomTestUtil.randomString(10), true,
			new ServiceContext());

		DocumentFolder getDocumentFolder =
			documentFolderResource.getDocumentFolder(
				postDocumentFolder.getId());

		Assert.assertEquals(
			Integer.valueOf(1), getDocumentFolder.getNumberOfDocuments());

		return postDocumentFolder;
	}

	@Override
	protected Long
			testGetDocumentFolderDocumentFoldersPage_getParentDocumentFolderId()
		throws Exception {

		DocumentFolder documentFolder =
			documentFolderResource.postSiteDocumentFolder(
				testGroup.getGroupId(), randomDocumentFolder());

		return documentFolder.getId();
	}

	@Override
	protected DocumentFolder
			testGetSiteDocumentFoldersRatedByMePage_addDocumentFolder(
				Long siteId, DocumentFolder documentFolder)
		throws Exception {

		DocumentFolder addedDocumentFolder =
			super.testGetSiteDocumentFoldersRatedByMePage_addDocumentFolder(
				siteId, documentFolder);

		_addDocumentFolderRatingsEntry(addedDocumentFolder);

		return addedDocumentFolder;
	}

	@Override
	protected DocumentFolder
			testGraphQLAssetLibraryDocumentFolder_addDocumentFolder(
				Long assetLibraryId, DocumentFolder documentFolder)
		throws Exception {

		DocumentFolder addedDocumentFolder =
			super.
				testGetAssetLibraryDocumentFoldersRatedByMePage_addDocumentFolder(
					assetLibraryId, documentFolder);

		_addDocumentFolderRatingsEntry(addedDocumentFolder);

		return addedDocumentFolder;
	}

	@Override
	protected DocumentFolder testGraphQLSiteDocumentFolder_addDocumentFolder(
			Long siteId, DocumentFolder documentFolder)
		throws Exception {

		DocumentFolder addedDocumentFolder =
			super.testGetSiteDocumentFoldersRatedByMePage_addDocumentFolder(
				siteId, documentFolder);

		_addDocumentFolderRatingsEntry(addedDocumentFolder);

		return addedDocumentFolder;
	}

	private void _addDocumentFolderRatingsEntry(DocumentFolder documentFolder)
		throws Exception {

		Creator creator = documentFolder.getCreator();

		_ratingsEntryLocalService.updateEntry(
			creator.getId(), DLFolder.class.getName(), documentFolder.getId(),
			1.0,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));
	}

	private void _testPostDocumentFolderDocumentFolderWithPermission()
		throws Exception {

		DocumentFolder documentFolder = randomDocumentFolder();

		Role userRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		documentFolder.setPermissions(
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

		documentFolder.setViewableBy(DocumentFolder.ViewableBy.OWNER);

		DocumentFolder postDocumentFolder =
			testPostDocumentFolderDocumentFolder_addDocumentFolder(
				documentFolder);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				TestPropsValues.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(postDocumentFolder.getId()),
				userRole.getRoleId());

		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.UPDATE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));
	}

	@Inject
	private RatingsEntryLocalService _ratingsEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}