/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecificationVersionPageExperience;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.provider.LayoutContentVersionDataProvider;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.content.service.LayoutContentVersionPreviewLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Locale;
import java.util.Map;

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
public class PageSpecificationVersionPageExperienceResourceTest
	extends BasePageSpecificationVersionPageExperienceResourceTestCase {

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

		_layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		Layout draftLayout = _layout.fetchDraftLayout();

		_layoutContentVersion =
			_layoutContentVersionLocalService.addLayoutContentVersion(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_layoutContentVersionDataProvider.getLayoutContentVersionData(
					draftLayout,
					ServiceContextTestUtil.getServiceContext(
						testGroup.getGroupId())),
				RandomTestUtil.randomLocaleStringMap(), draftLayout.getPlid(),
				WorkflowConstants.STATUS_APPROVED);
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Ignore
	@Override
	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		super.testEscapeRegexInStringFields();
	}

	@Override
	@Test
	@TestInfo("LPD-103339")
	public void testGetSiteSitePagePageSpecificationVersionPageSpecificationVersionPageExperiencesPage()
		throws Exception {

		super.
			testGetSiteSitePagePageSpecificationVersionPageSpecificationVersionPageExperiencesPage();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"availablePreviewLanguageIds", "externalReferenceCode", "name_i18n",
			"priority"
		};
	}

	@Override
	protected PageSpecificationVersionPageExperience
			randomPageSpecificationVersionPageExperience()
		throws Exception {

		PageSpecificationVersionPageExperience
			pageSpecificationVersionPageExperience =
				super.randomPageSpecificationVersionPageExperience();

		pageSpecificationVersionPageExperience.setName_i18n(
			RandomTestUtil.randomLanguageIdStringMap());

		return pageSpecificationVersionPageExperience;
	}

	@Override
	protected PageSpecificationVersionPageExperience
			testGetSiteSitePagePageSpecificationVersionPageSpecificationVersionPageExperiencesPage_addPageSpecificationVersionPageExperience(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode,
				String pageSpecificationVersionExternalReferenceCode,
				PageSpecificationVersionPageExperience
					pageSpecificationVersionPageExperience)
		throws Exception {

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionLocalService.
				getLayoutContentVersionByExternalReferenceCode(
					pageSpecificationVersionExternalReferenceCode,
					testGroup.getGroupId());

		PageExperience pageExperience = new PageExperience();

		String segmentsExperienceERC = RandomTestUtil.randomString();

		pageExperience.setExternalReferenceCode(segmentsExperienceERC);

		pageExperience.setName_i18n(RandomTestUtil.randomLanguageIdStringMap());
		pageExperience.setPriority(RandomTestUtil.randomInt());

		ContentPageSpecification contentPageSpecification =
			ContentPageSpecification.toDTO(layoutContentVersion.getData());

		contentPageSpecification.setPageExperiences(
			ArrayUtil.append(
				contentPageSpecification.getPageExperiences(), pageExperience));

		layoutContentVersion.setData(contentPageSpecification.toString());

		layoutContentVersion =
			_layoutContentVersionLocalService.updateLayoutContentVersion(
				layoutContentVersion);

		long layoutContentVersionId =
			layoutContentVersion.getLayoutContentVersionId();

		for (Locale locale :
				LanguageUtil.getAvailableLocales(testGroup.getGroupId())) {

			_layoutContentVersionPreviewLocalService.
				addLayoutContentVersionPreview(
					TestPropsValues.getUserId(), layoutContentVersionId,
					RandomTestUtil.randomString(),
					LocaleUtil.toLanguageId(locale), segmentsExperienceERC);
		}

		Map<String, List<String>> segmentsExperienceERCsLanguageIds =
			_layoutContentVersionPreviewLocalService.
				getSegmentsExperienceERCsLanguageIds(layoutContentVersionId);

		List<String> availablePreviewLanguageIds =
			segmentsExperienceERCsLanguageIds.get(segmentsExperienceERC);

		pageSpecificationVersionPageExperience.setAvailablePreviewLanguageIds(
			availablePreviewLanguageIds.toArray(new String[0]));

		pageSpecificationVersionPageExperience.setExternalReferenceCode(
			segmentsExperienceERC);
		pageSpecificationVersionPageExperience.setName_i18n(
			pageExperience.getName_i18n());
		pageSpecificationVersionPageExperience.setPriority(
			pageExperience.getPriority());

		return pageSpecificationVersionPageExperience;
	}

	@Override
	protected String
			testGetSiteSitePagePageSpecificationVersionPageSpecificationVersionPageExperiencesPage_getPageSpecificationVersionExternalReferenceCode()
		throws Exception {

		return _layoutContentVersion.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSitePagePageSpecificationVersionPageSpecificationVersionPageExperiencesPage_getSitePageExternalReferenceCode()
		throws Exception {

		return _layout.getExternalReferenceCode();
	}

	private Layout _layout;
	private LayoutContentVersion _layoutContentVersion;

	@Inject
	private LayoutContentVersionDataProvider _layoutContentVersionDataProvider;

	@Inject
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Inject
	private LayoutContentVersionPreviewLocalService
		_layoutContentVersionPreviewLocalService;

}