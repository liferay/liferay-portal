/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecificationVersion;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.PageSpecificationVersionResource;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.provider.LayoutContentVersionDataProvider;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-10622")
@RunWith(Arquillian.class)
public class PageSpecificationVersionResourceTest
	extends BasePageSpecificationVersionResourceTestCase {

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

		_irrelevantGroupLayout = LayoutTestUtil.addTypeContentLayout(
			irrelevantGroup);
		_testGroupLayout = LayoutTestUtil.addTypeContentLayout(testGroup);
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	@TestInfo("LPD-90029")
	public void testDeleteSiteSitePagePageSpecificationVersion()
		throws Exception {

		super.testDeleteSiteSitePagePageSpecificationVersion();
	}

	@Override
	@Test
	public void testGetSiteSitePagePageSpecificationVersion() throws Exception {
		super.testGetSiteSitePagePageSpecificationVersion();

		_testGetSiteSitePagePageSpecificationVersionActions();
		_testGetSiteSitePagePageSpecificationVersionMismatchedSitePage();
		_testGetSiteSitePagePageSpecificationVersionPageSpecificationNestedField();
	}

	@Override
	@Test
	public void testGetSiteSitePagePageSpecificationVersionsPage()
		throws Exception {

		super.testGetSiteSitePagePageSpecificationVersionsPage();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"externalReferenceCode", "name"};
	}

	@Override
	protected PageSpecificationVersion
			testDeleteSiteSitePagePageSpecificationVersion_addPageSpecificationVersion()
		throws Exception {

		return _addPageSpecificationVersion(WorkflowConstants.STATUS_DRAFT);
	}

	@Override
	protected String
			testDeleteSiteSitePagePageSpecificationVersion_getSitePageExternalReferenceCode()
		throws Exception {

		return _testGroupLayout.getExternalReferenceCode();
	}

	@Override
	protected PageSpecificationVersion
			testGetSiteSitePagePageSpecificationVersion_addPageSpecificationVersion()
		throws Exception {

		return _addPageSpecificationVersion();
	}

	@Override
	protected String
			testGetSiteSitePagePageSpecificationVersion_getSitePageExternalReferenceCode()
		throws Exception {

		return _testGroupLayout.getExternalReferenceCode();
	}

	@Override
	protected PageSpecificationVersion
			testGetSiteSitePagePageSpecificationVersionsPage_addPageSpecificationVersion(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode,
				PageSpecificationVersion pageSpecificationVersion)
		throws Exception {

		return _addPageSpecificationVersion(
			siteExternalReferenceCode, sitePageExternalReferenceCode,
			pageSpecificationVersion);
	}

	@Override
	protected String
			testGetSiteSitePagePageSpecificationVersionsPage_getIrrelevantSitePageExternalReferenceCode()
		throws Exception {

		return _irrelevantGroupLayout.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSitePagePageSpecificationVersionsPage_getSitePageExternalReferenceCode()
		throws Exception {

		return _testGroupLayout.getExternalReferenceCode();
	}

	private PageSpecificationVersion _addPageSpecificationVersion()
		throws Exception {

		return _addPageSpecificationVersion(WorkflowConstants.STATUS_APPROVED);
	}

	private PageSpecificationVersion _addPageSpecificationVersion(
			Group group, Layout layout,
			PageSpecificationVersion pageSpecificationVersion)
		throws Exception {

		return _addPageSpecificationVersion(
			pageSpecificationVersion.getExternalReferenceCode(), group, layout,
			pageSpecificationVersion.getName(),
			WorkflowConstants.STATUS_APPROVED);
	}

	private PageSpecificationVersion _addPageSpecificationVersion(int status)
		throws Exception {

		return _addPageSpecificationVersion(
			RandomTestUtil.randomString(), testGroup, _testGroupLayout,
			RandomTestUtil.randomString(), status);
	}

	private PageSpecificationVersion _addPageSpecificationVersion(
			String externalReferenceCode, Group group, Layout layout,
			String name, int status)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionLocalService.addLayoutContentVersion(
				externalReferenceCode, TestPropsValues.getUserId(),
				_layoutContentVersionDataProvider.getLayoutContentVersionData(
					draftLayout,
					ServiceContextTestUtil.getServiceContext(
						group.getGroupId())),
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(), name
				).build(),
				draftLayout.getPlid(), status);

		if (Validator.isNotNull(externalReferenceCode)) {
			Assert.assertEquals(
				externalReferenceCode,
				layoutContentVersion.getExternalReferenceCode());
		}

		return pageSpecificationVersionResource.
			getSiteSitePagePageSpecificationVersion(
				group.getExternalReferenceCode(),
				layout.getExternalReferenceCode(),
				layoutContentVersion.getExternalReferenceCode());
	}

	private PageSpecificationVersion _addPageSpecificationVersion(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			PageSpecificationVersion pageSpecificationVersion)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, testCompany.getCompanyId());

		return _addPageSpecificationVersion(
			group,
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePageExternalReferenceCode, group.getGroupId()),
			pageSpecificationVersion);
	}

	private void _assertActionHref(
		PageSpecificationVersion pageSpecificationVersion, String... keys) {

		Map<String, Map<String, String>> actions =
			pageSpecificationVersion.getActions();

		String content = StringBundler.concat(
			"/sites/", testGroup.getExternalReferenceCode(), "/site-pages/",
			_testGroupLayout.getExternalReferenceCode(),
			"/page-specification-versions/",
			pageSpecificationVersion.getExternalReferenceCode());

		for (String key : keys) {
			Map<String, String> action = actions.get(key);

			String href = action.get("href");

			Assert.assertTrue(key, href.contains(content));
		}
	}

	private PageSpecificationVersionResource
			_getPageSpecificationVersionResource()
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return PageSpecificationVersionResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "pageSpecification"
		).build();
	}

	private void _testGetSiteSitePagePageSpecificationVersionActions()
		throws Exception {

		PageSpecificationVersion firstPageSpecificationVersion =
			_addPageSpecificationVersion();

		firstPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					_testGroupLayout.getExternalReferenceCode(),
					firstPageSpecificationVersion.getExternalReferenceCode());

		Map<String, Map<String, String>> firstActions =
			firstPageSpecificationVersion.getActions();

		Assert.assertNull(firstActions.get("delete"));

		_assertActionHref(firstPageSpecificationVersion, "get");

		PageSpecificationVersion secondPageSpecificationVersion =
			_addPageSpecificationVersion();

		firstPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					_testGroupLayout.getExternalReferenceCode(),
					firstPageSpecificationVersion.getExternalReferenceCode());

		_assertActionHref(firstPageSpecificationVersion, "delete", "get");

		secondPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					_testGroupLayout.getExternalReferenceCode(),
					secondPageSpecificationVersion.getExternalReferenceCode());

		Map<String, Map<String, String>> secondActions =
			secondPageSpecificationVersion.getActions();

		Assert.assertNull(secondActions.get("delete"));

		_assertActionHref(secondPageSpecificationVersion, "get");
	}

	private void _testGetSiteSitePagePageSpecificationVersionMismatchedSitePage()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		PageSpecificationVersion pageSpecificationVersion =
			testGetSiteSitePagePageSpecificationVersion_addPageSpecificationVersion();

		try {
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					layout.getExternalReferenceCode(),
					pageSpecificationVersion.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"The page specification version must belong to the site page",
				problem.getTitle());
		}
	}

	private void _testGetSiteSitePagePageSpecificationVersionPageSpecificationNestedField()
		throws Exception {

		PageSpecificationVersion pageSpecificationVersion =
			testGetSiteSitePagePageSpecificationVersion_addPageSpecificationVersion();

		PageSpecificationVersion getPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					_testGroupLayout.getExternalReferenceCode(),
					pageSpecificationVersion.getExternalReferenceCode());

		Assert.assertNull(getPageSpecificationVersion.getPageSpecification());

		PageSpecificationVersionResource pageSpecificationVersionResource =
			_getPageSpecificationVersionResource();

		getPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					_testGroupLayout.getExternalReferenceCode(),
					pageSpecificationVersion.getExternalReferenceCode());

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionLocalService.
				getLayoutContentVersionByExternalReferenceCode(
					pageSpecificationVersion.getExternalReferenceCode(),
					testGroup.getGroupId());

		Assert.assertEquals(
			PageSpecification.toDTO(layoutContentVersion.getData()),
			getPageSpecificationVersion.getPageSpecification());
	}

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _irrelevantGroupLayout;

	@Inject
	private LayoutContentVersionDataProvider _layoutContentVersionDataProvider;

	@Inject
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private Layout _testGroupLayout;

}