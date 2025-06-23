/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 * @author Bárbara Cabrera
 */
@FeatureFlag("LPD-35443")
@RunWith(Arquillian.class)
public class DisplayPageTemplateFolderResourceTest
	extends BaseDisplayPageTemplateFolderResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testDeleteSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder()
		throws Exception {

		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplateFolder());

		displayPageTemplateFolderResource.
			deleteSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				postDisplayPageTemplateFolder.getExternalReferenceCode());

		Assert.assertNull(
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					postDisplayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId()));

		DisplayPageTemplateFolder liveGroupDisplayPageTemplateFolder =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplateFolder());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateFolderResource.
					deleteSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						liveGroupDisplayPageTemplateFolder.
							getExternalReferenceCode()));
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplateFolderPermissionsPage()
		throws Exception {

		super.testGetSiteDisplayPageTemplateFolderPermissionsPage();
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder()
		throws Exception {

		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplateFolder());

		DisplayPageTemplateFolder getDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
					testGroup.getExternalReferenceCode(),
					postDisplayPageTemplateFolder.getExternalReferenceCode());

		assertEquals(
			postDisplayPageTemplateFolder, getDisplayPageTemplateFolder);
		assertValid(getDisplayPageTemplateFolder);

		_enableLocalStaging();

		assertEquals(
			postDisplayPageTemplateFolder,
			displayPageTemplateFolderResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
					testGroup.getExternalReferenceCode(),
					postDisplayPageTemplateFolder.getExternalReferenceCode()));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder()
		throws Exception {

		super.
			testGraphQLGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder();
	}

	@Override
	@Test
	public void testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder()
		throws Exception {

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		DisplayPageTemplateFolder displayPageTemplateFolder =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		Assert.assertNull(
			displayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode());

		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
			displayPageTemplateFolder.getExternalReferenceCode(),
			parentDisplayPageTemplateFolder.getExternalReferenceCode());

		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
			displayPageTemplateFolder.getExternalReferenceCode(), null);
		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
			displayPageTemplateFolder.getExternalReferenceCode(),
			StringPool.BLANK);

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				displayPageTemplateFolderResource.
					patchSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						RandomTestUtil.randomString(),
						randomDisplayPageTemplateFolder()));

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateFolderResource.
					patchSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						displayPageTemplateFolder.getExternalReferenceCode(),
						displayPageTemplateFolder));
	}

	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder()
		throws Exception {

		super.
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder();

		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderWithExistingParentExternalReferenceCode();

		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderWithInvalidKey(
			postDisplayPageTemplateFolder.getKey(),
			StringBundler.concat(
				"Duplicate display page template folder for group ",
				testGroup.getGroupId(), " with key ",
				postDisplayPageTemplateFolder.getKey()));

		String key =
			RandomTestUtil.randomString() + StringPool.AMPERSAND +
				RandomTestUtil.randomString();

		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key,
				" must contain only alphanumeric characters, dashes, and ",
				"underscores"));

		key = RandomTestUtil.randomString(80);

		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key, " must have fewer than 75 characters"));

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateFolderResource.
					postSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						randomDisplayPageTemplateFolder()));
	}

	@Ignore
	@Override
	@Test
	public void testPutSiteDisplayPageTemplateFolderPermissionsPage()
		throws Exception {

		super.testPutSiteDisplayPageTemplateFolderPermissionsPage();
	}

	@Override
	@Test
	public void testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder()
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder(),
				RandomTestUtil.randomString());

		Assert.assertNull(
			displayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode());

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		displayPageTemplateFolder =
			_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder(),
				parentDisplayPageTemplateFolder.getExternalReferenceCode());

		Assert.assertEquals(
			parentDisplayPageTemplateFolder.getExternalReferenceCode(),
			displayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode());

		displayPageTemplateFolder =
			_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				displayPageTemplateFolder, StringPool.BLANK);

		Assert.assertNull(
			displayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode());

		DisplayPageTemplateFolder liveGroupDisplayPageTemplateFolder =
			_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder(),
				RandomTestUtil.randomString());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateFolderResource.
					putSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						liveGroupDisplayPageTemplateFolder.
							getExternalReferenceCode(),
						parentDisplayPageTemplateFolder));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "externalReferenceCode", "name"};
	}

	@Override
	protected DisplayPageTemplateFolder randomDisplayPageTemplateFolder()
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			super.randomDisplayPageTemplateFolder();

		displayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				(String)null);

		return displayPageTemplateFolder;
	}

	@Ignore
	@Override
	@Test
	protected DisplayPageTemplateFolder
			testGetSiteDisplayPageTemplateFolderPermissionsPage_addDisplayPageTemplateFolder()
		throws Exception {

		return super.
			testGetSiteDisplayPageTemplateFolderPermissionsPage_addDisplayPageTemplateFolder();
	}

	@Override
	protected DisplayPageTemplateFolder
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				String siteExternalReferenceCode,
				DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		return displayPageTemplateFolderResource.
			postSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				siteExternalReferenceCode, displayPageTemplateFolder);
	}

	@Override
	protected DisplayPageTemplateFolder
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		return testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
			testGroup.getExternalReferenceCode(), displayPageTemplateFolder);
	}

	@Ignore
	@Override
	@Test
	protected DisplayPageTemplateFolder
			testPutSiteDisplayPageTemplateFolderPermissionsPage_addDisplayPageTemplateFolder()
		throws Exception {

		return super.
			testPutSiteDisplayPageTemplateFolderPermissionsPage_addDisplayPageTemplateFolder();
	}

	private void _assertProblemException(
			String status, String title,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
			Assert.assertEquals(title, problem.getTitle());
		}
	}

	private void _enableLocalStaging() throws Exception {
		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), testGroup, true, false,
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));
	}

	private void
			_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				String displayPageTemplateFolderExternalReferenceCode,
				String parentDisplayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		DisplayPageTemplateFolder getDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
					testGroup.getExternalReferenceCode(),
					displayPageTemplateFolderExternalReferenceCode);

		DisplayPageTemplateFolder randomDisplayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		randomDisplayPageTemplateFolder.setExternalReferenceCode(
			displayPageTemplateFolderExternalReferenceCode);
		randomDisplayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolderExternalReferenceCode);

		DisplayPageTemplateFolder patchDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.
				patchSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
					testGroup.getExternalReferenceCode(),
					displayPageTemplateFolderExternalReferenceCode,
					randomDisplayPageTemplateFolder);

		assertEquals(
			randomDisplayPageTemplateFolder, patchDisplayPageTemplateFolder);
		assertValid(patchDisplayPageTemplateFolder);

		if (parentDisplayPageTemplateFolderExternalReferenceCode == null) {
			parentDisplayPageTemplateFolderExternalReferenceCode =
				getDisplayPageTemplateFolder.
					getParentDisplayPageTemplateFolderExternalReferenceCode();
		}

		if (Validator.isBlank(
				parentDisplayPageTemplateFolderExternalReferenceCode)) {

			Assert.assertNull(
				patchDisplayPageTemplateFolder.
					getParentDisplayPageTemplateFolderExternalReferenceCode());
		}
		else {
			Assert.assertEquals(
				parentDisplayPageTemplateFolderExternalReferenceCode,
				patchDisplayPageTemplateFolder.
					getParentDisplayPageTemplateFolderExternalReferenceCode());
		}
	}

	private void _testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderWithExistingParentExternalReferenceCode()
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		displayPageTemplateFolder.setKey(StringPool.BLANK);

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				displayPageTemplateFolder);

		Assert.assertNotNull(
			Validator.isNotNull(parentDisplayPageTemplateFolder.getKey()));

		DisplayPageTemplateFolder randomDisplayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		randomDisplayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolder.getExternalReferenceCode());

		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder);

		assertEquals(
			randomDisplayPageTemplateFolder, postDisplayPageTemplateFolder);
		Assert.assertEquals(
			randomDisplayPageTemplateFolder.getKey(),
			postDisplayPageTemplateFolder.getKey());
		Assert.assertEquals(
			randomDisplayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode(),
			postDisplayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode());
		assertValid(postDisplayPageTemplateFolder);
		Assert.assertNotNull(
			postDisplayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode());
	}

	private void
			_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderWithInvalidKey(
				String key, String title)
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		displayPageTemplateFolder.setKey(key);

		_assertProblemException(
			"CONFLICT", title,
			() ->
				displayPageTemplateFolderResource.
					postSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						displayPageTemplateFolder));
	}

	private DisplayPageTemplateFolder
			_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				DisplayPageTemplateFolder displayPageTemplateFolder,
				String parentDisplayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		displayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolderExternalReferenceCode);

		DisplayPageTemplateFolder putDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
					testGroup.getExternalReferenceCode(),
					displayPageTemplateFolder.getExternalReferenceCode(),
					displayPageTemplateFolder);

		assertEquals(displayPageTemplateFolder, putDisplayPageTemplateFolder);
		assertValid(putDisplayPageTemplateFolder);

		return putDisplayPageTemplateFolder;
	}

	@Inject
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Inject
	private StagingLocalService _stagingLocalService;

}