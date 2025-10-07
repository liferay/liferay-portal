/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.resource.v1_0.DisplayPageTemplateFolderResource;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
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

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_displayPageTemplateFolderResource.setContextAcceptLanguage(
			new AcceptLanguage() {

				@Override
				public List<Locale> getLocales() {
					return Arrays.asList(LocaleUtil.getDefault());
				}

				@Override
				public String getPreferredLanguageId() {
					return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
				}

				@Override
				public Locale getPreferredLocale() {
					return LocaleUtil.getDefault();
				}

			});
		_displayPageTemplateFolderResource.setContextUser(
			TestPropsValues.getUser());
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteSiteDisplayPageTemplateFolder() throws Exception {
		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testGetSiteDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplateFolder());

		displayPageTemplateFolderResource.deleteSiteDisplayPageTemplateFolder(
			testGroup.getExternalReferenceCode(),
			postDisplayPageTemplateFolder.getExternalReferenceCode());

		Assert.assertNull(
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					postDisplayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId()));

		DisplayPageTemplateFolder liveGroupDisplayPageTemplateFolder =
			testGetSiteDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplateFolder());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateFolderResource.
					deleteSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						liveGroupDisplayPageTemplateFolder.
							getExternalReferenceCode()));
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplateFolder() throws Exception {
		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testGetSiteDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplateFolder());

		DisplayPageTemplateFolder getDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.getSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				postDisplayPageTemplateFolder.getExternalReferenceCode());

		assertEquals(
			postDisplayPageTemplateFolder, getDisplayPageTemplateFolder);
		assertValid(getDisplayPageTemplateFolder);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					getDisplayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId());

		List<LayoutPageTemplateCollection> parentLayoutPageTemplateCollections =
			new ArrayList<>();

		_assertParentDisplayPageTemplateFolder(
			_layoutPageTemplateCollectionService.
				moveLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					_getParentLayoutPageTemplateCollectionId(
						5, parentLayoutPageTemplateCollections)),
			parentLayoutPageTemplateCollections);

		_enableLocalStaging();

		assertEquals(
			postDisplayPageTemplateFolder,
			displayPageTemplateFolderResource.getSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				postDisplayPageTemplateFolder.getExternalReferenceCode()));
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
	public void testGetSiteDisplayPageTemplateFoldersPage() throws Exception {
		super.testGetSiteDisplayPageTemplateFoldersPage();

		DisplayPageTemplateFolder displayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		displayPageTemplateFolder.setDateModified(
			new Date(System.currentTimeMillis() - Time.DAY));

		displayPageTemplateFolderResource.postSiteDisplayPageTemplateFolder(
			testGroup.getExternalReferenceCode(), displayPageTemplateFolder);

		Page<DisplayPageTemplateFolder> page =
			displayPageTemplateFolderResource.
				getSiteDisplayPageTemplateFoldersPage(
					testGroup.getExternalReferenceCode(), null, null,
					"dateModified le " +
						new Date(
							System.currentTimeMillis() - Time.DAY
						).toInstant(),
					null, null);

		Assert.assertEquals(1, page.getTotalCount());
	}

	@Override
	@Test
	public void testPatchSiteDisplayPageTemplateFolder() throws Exception {
		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		DisplayPageTemplateFolder displayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		Assert.assertNull(
			displayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode());

		_testPatchSiteDisplayPageTemplateFolder(
			displayPageTemplateFolder.getExternalReferenceCode(),
			parentDisplayPageTemplateFolder.getExternalReferenceCode());

		_testPatchSiteDisplayPageTemplateFolder(
			displayPageTemplateFolder.getExternalReferenceCode(), null);
		_testPatchSiteDisplayPageTemplateFolder(
			displayPageTemplateFolder.getExternalReferenceCode(),
			StringPool.BLANK);

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				displayPageTemplateFolderResource.
					patchSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						RandomTestUtil.randomString(),
						randomDisplayPageTemplateFolder()));

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateFolderResource.
					patchSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						displayPageTemplateFolder.getExternalReferenceCode(),
						displayPageTemplateFolder));
	}

	@Override
	@Test
	public void testPostSiteDisplayPageTemplateFolder() throws Exception {
		super.testPostSiteDisplayPageTemplateFolder();

		_testPostSiteDisplayPageTemplateFolderWithExistingParentExternalReferenceCode();

		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		_testPostSiteDisplayPageTemplateFolderWithInvalidKey(
			postDisplayPageTemplateFolder.getKey(),
			StringBundler.concat(
				"Duplicate display page template folder for group ",
				testGroup.getGroupId(), " with key ",
				postDisplayPageTemplateFolder.getKey()));

		String key =
			RandomTestUtil.randomString() + StringPool.AMPERSAND +
				RandomTestUtil.randomString();

		_testPostSiteDisplayPageTemplateFolderWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key,
				" must contain only alphanumeric characters, dashes, and ",
				"underscores"));

		key = RandomTestUtil.randomString(80);

		_testPostSiteDisplayPageTemplateFolderWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key, " must have fewer than 75 characters"));

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateFolderResource.
					postSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						randomDisplayPageTemplateFolder()));
	}

	@Override
	@Test
	public void testPutSiteDisplayPageTemplateFolder() throws Exception {
		DisplayPageTemplateFolder displayPageTemplateFolder =
			_testPutSiteDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder(), null);

		_assertNoParentDisplayPageTemplateFolder(displayPageTemplateFolder);

		_testPutSiteDisplayPageTemplateFolderWithParentDisplayPageTemplateFolder();

		DisplayPageTemplateFolder liveGroupDisplayPageTemplateFolder =
			_testPutSiteDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder(),
				displayPageTemplateFolder.getExternalReferenceCode());

		_assertParentDisplayPageTemplateFolder(
			liveGroupDisplayPageTemplateFolder, displayPageTemplateFolder);

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateFolderResource.
					putSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						liveGroupDisplayPageTemplateFolder.
							getExternalReferenceCode(),
						liveGroupDisplayPageTemplateFolder));
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

		displayPageTemplateFolder.setDateCreated(
			new Date(System.currentTimeMillis()));
		displayPageTemplateFolder.setDateModified(
			new Date(System.currentTimeMillis()));
		displayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				(String)null);

		return displayPageTemplateFolder;
	}

	@Override
	protected String
			testBatchEngineDeleteImportTask_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Override
	protected DisplayPageTemplateFolder
			testGetSiteDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				String siteExternalReferenceCode,
				DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		if (LazyReferencingThreadLocal.isEnabled()) {
			return _toDisplayPageTemplateFolder(
				_displayPageTemplateFolderResource.
					postSiteDisplayPageTemplateFolder(
						siteExternalReferenceCode,
						_toDisplayPageTemplateFolder(
							displayPageTemplateFolder)));
		}

		return displayPageTemplateFolderResource.
			postSiteDisplayPageTemplateFolder(
				siteExternalReferenceCode, displayPageTemplateFolder);
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSiteDisplayPageTemplateFoldersPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected DisplayPageTemplateFolder
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		return testGetSiteDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
			testGroup.getExternalReferenceCode(), displayPageTemplateFolder);
	}

	private static
		com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateFolder
			_toDisplayPageTemplateFolder(
				DisplayPageTemplateFolder displayPageTemplateFolder) {

		if (displayPageTemplateFolder == null) {
			return null;
		}

		return com.liferay.headless.admin.site.dto.v1_0.
			DisplayPageTemplateFolder.toDTO(
				displayPageTemplateFolder.toString());
	}

	private static DisplayPageTemplateFolder _toDisplayPageTemplateFolder(
		com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateFolder
			displayPageTemplateFolder) {

		if (displayPageTemplateFolder == null) {
			return null;
		}

		return DisplayPageTemplateFolder.toDTO(
			displayPageTemplateFolder.toString());
	}

	private void _assertNoParentDisplayPageTemplateFolder(
			DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		_assertParentDisplayPageTemplateFolder(
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					displayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId()),
			Collections.emptyList());
	}

	private void _assertParentDisplayPageTemplateFolder(
			DisplayPageTemplateFolder displayPageTemplateFolder,
			DisplayPageTemplateFolder parentDisplayPageTemplateFolder)
		throws Exception {

		_assertParentDisplayPageTemplateFolder(
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					displayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId()),
			ListUtil.fromArray(
				_layoutPageTemplateCollectionService.
					getLayoutPageTemplateCollection(
						parentDisplayPageTemplateFolder.
							getExternalReferenceCode(),
						testGroup.getGroupId())));
	}

	private void _assertParentDisplayPageTemplateFolder(
			LayoutPageTemplateCollection layoutPageTemplateCollection,
			List<LayoutPageTemplateCollection>
				parentLayoutPageTemplateCollections)
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			displayPageTemplateFolderResource.getSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				layoutPageTemplateCollection.getExternalReferenceCode());

		if (parentLayoutPageTemplateCollections.isEmpty()) {
			Assert.assertNull(
				displayPageTemplateFolder.getParentDisplayPageTemplateFolder());
			Assert.assertNull(
				displayPageTemplateFolder.
					getParentDisplayPageTemplateFolderExternalReferenceCode());

			return;
		}

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			displayPageTemplateFolder.getParentDisplayPageTemplateFolder();

		Assert.assertEquals(
			parentDisplayPageTemplateFolder.getExternalReferenceCode(),
			displayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode());

		for (LayoutPageTemplateCollection parentLayoutPageTemplateCollection :
				parentLayoutPageTemplateCollections) {

			assertEquals(
				displayPageTemplateFolderResource.
					getSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						parentLayoutPageTemplateCollection.
							getExternalReferenceCode()),
				parentDisplayPageTemplateFolder);

			parentDisplayPageTemplateFolder =
				parentDisplayPageTemplateFolder.
					getParentDisplayPageTemplateFolder();
		}

		Assert.assertNull(parentDisplayPageTemplateFolder);
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

	private DisplayPageTemplateFolder _getParentDisplayPageTemplateFolder(
			int count)
		throws Exception {

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		for (int i = 0; i < count; i++) {
			DisplayPageTemplateFolder displayPageTemplateFolder =
				randomDisplayPageTemplateFolder();

			displayPageTemplateFolder.setParentDisplayPageTemplateFolder(
				parentDisplayPageTemplateFolder);
			displayPageTemplateFolder.
				setParentDisplayPageTemplateFolderExternalReferenceCode(
					parentDisplayPageTemplateFolder.getExternalReferenceCode());

			parentDisplayPageTemplateFolder = displayPageTemplateFolder;
		}

		return parentDisplayPageTemplateFolder;
	}

	private long _getParentLayoutPageTemplateCollectionId(
			int count,
			List<LayoutPageTemplateCollection>
				parentLayoutPageTemplateCollections)
		throws Exception {

		long parentLayoutPageTemplateCollectionId =
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT;

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId());

		for (int i = 0; i < count; i++) {
			LayoutPageTemplateCollection parentLayoutPageTemplateCollection =
				_layoutPageTemplateCollectionService.
					addLayoutPageTemplateCollection(
						null, testGroup.getGroupId(),
						parentLayoutPageTemplateCollectionId, null,
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
						serviceContext);

			parentLayoutPageTemplateCollectionId =
				parentLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId();

			parentLayoutPageTemplateCollections.add(
				parentLayoutPageTemplateCollection);
		}

		Collections.reverse(parentLayoutPageTemplateCollections);

		return parentLayoutPageTemplateCollectionId;
	}

	private void _testPatchSiteDisplayPageTemplateFolder(
			String displayPageTemplateFolderExternalReferenceCode,
			String parentDisplayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		DisplayPageTemplateFolder getDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.getSiteDisplayPageTemplateFolder(
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
				patchSiteDisplayPageTemplateFolder(
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

			_assertNoParentDisplayPageTemplateFolder(
				patchDisplayPageTemplateFolder);
		}
		else {
			_assertParentDisplayPageTemplateFolder(
				_layoutPageTemplateCollectionService.
					getLayoutPageTemplateCollection(
						patchDisplayPageTemplateFolder.
							getExternalReferenceCode(),
						testGroup.getGroupId()),
				ListUtil.fromArray(
					_layoutPageTemplateCollectionService.
						getLayoutPageTemplateCollection(
							parentDisplayPageTemplateFolderExternalReferenceCode,
							testGroup.getGroupId())));
		}
	}

	private void _testPostSiteDisplayPageTemplateFolderWithExistingParentExternalReferenceCode()
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		displayPageTemplateFolder.setKey(StringPool.BLANK);

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				displayPageTemplateFolder);

		Assert.assertNotNull(
			Validator.isNotNull(parentDisplayPageTemplateFolder.getKey()));

		DisplayPageTemplateFolder randomDisplayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		randomDisplayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolder.getExternalReferenceCode());

		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
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
		_assertParentDisplayPageTemplateFolder(
			postDisplayPageTemplateFolder, parentDisplayPageTemplateFolder);
	}

	private void _testPostSiteDisplayPageTemplateFolderWithInvalidKey(
			String key, String title)
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		displayPageTemplateFolder.setKey(key);

		_assertProblemException(
			"CONFLICT", title,
			() ->
				displayPageTemplateFolderResource.
					postSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						displayPageTemplateFolder));
	}

	private DisplayPageTemplateFolder _testPutSiteDisplayPageTemplateFolder(
			DisplayPageTemplateFolder displayPageTemplateFolder,
			String parentDisplayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		displayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolderExternalReferenceCode);

		if (Validator.isNull(
				parentDisplayPageTemplateFolderExternalReferenceCode)) {

			displayPageTemplateFolder.setParentDisplayPageTemplateFolder(
				() -> null);
		}

		DisplayPageTemplateFolder putDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.putSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				displayPageTemplateFolder.getExternalReferenceCode(),
				displayPageTemplateFolder);

		assertEquals(displayPageTemplateFolder, putDisplayPageTemplateFolder);
		assertValid(putDisplayPageTemplateFolder);

		return putDisplayPageTemplateFolder;
	}

	private void _testPutSiteDisplayPageTemplateFolderWithParentDisplayPageTemplateFolder()
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> _testPutSiteDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder(),
				RandomTestUtil.randomString()));

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		DisplayPageTemplateFolder displayPageTemplateFolder =
			_testPutSiteDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder(),
				parentDisplayPageTemplateFolder.getExternalReferenceCode());

		_assertParentDisplayPageTemplateFolder(
			displayPageTemplateFolder, parentDisplayPageTemplateFolder);

		DisplayPageTemplateFolder putDisplayPageTemplateFolder =
			_testPutSiteDisplayPageTemplateFolder(
				displayPageTemplateFolder, StringPool.BLANK);

		_assertNoParentDisplayPageTemplateFolder(putDisplayPageTemplateFolder);

		parentDisplayPageTemplateFolder = _getParentDisplayPageTemplateFolder(
			5);

		putDisplayPageTemplateFolder.setParentDisplayPageTemplateFolder(
			parentDisplayPageTemplateFolder);
		putDisplayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolder.getExternalReferenceCode());

		try {
			_displayPageTemplateFolderResource.putSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				putDisplayPageTemplateFolder.getExternalReferenceCode(),
				_toDisplayPageTemplateFolder(putDisplayPageTemplateFolder));

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(unsupportedOperationException);
			}
		}

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			_displayPageTemplateFolderResource.putSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				putDisplayPageTemplateFolder.getExternalReferenceCode(),
				_toDisplayPageTemplateFolder(putDisplayPageTemplateFolder));

			List<LayoutPageTemplateCollection>
				parentLayoutPageTemplateCollections = new ArrayList<>();

			while (parentDisplayPageTemplateFolder != null) {
				parentLayoutPageTemplateCollections.add(
					_layoutPageTemplateCollectionService.
						getLayoutPageTemplateCollection(
							parentDisplayPageTemplateFolder.
								getExternalReferenceCode(),
							testGroup.getGroupId()));

				parentDisplayPageTemplateFolder =
					parentDisplayPageTemplateFolder.
						getParentDisplayPageTemplateFolder();
			}

			_assertParentDisplayPageTemplateFolder(
				_layoutPageTemplateCollectionService.
					getLayoutPageTemplateCollection(
						putDisplayPageTemplateFolder.getExternalReferenceCode(),
						testGroup.getGroupId()),
				parentLayoutPageTemplateCollections);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DisplayPageTemplateFolderResourceTest.class);

	@Inject
	private DisplayPageTemplateFolderResource
		_displayPageTemplateFolderResource;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Inject
	private StagingLocalService _stagingLocalService;

}