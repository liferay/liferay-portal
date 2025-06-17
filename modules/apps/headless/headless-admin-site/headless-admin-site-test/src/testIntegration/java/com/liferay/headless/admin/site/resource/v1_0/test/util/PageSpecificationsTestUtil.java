/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.constants.SegmentsExperienceConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.junit.Assert;

/**
 * @author Lourdes Fernández Besada
 */
public class PageSpecificationsTestUtil {

	public static void assertContentPageSpecification(
			PageSpecification pageSpecification, long plid)
		throws Exception {

		Layout layout = LayoutLocalServiceUtil.getLayout(plid);

		Assert.assertEquals(
			layout.getExternalReferenceCode(),
			pageSpecification.getExternalReferenceCode());

		if (layout.isDraftLayout()) {
			if (layout.isApproved()) {
				Assert.assertEquals(
					PageSpecification.Status.APPROVED,
					pageSpecification.getStatus());
			}
			else {
				Assert.assertEquals(
					PageSpecification.Status.DRAFT,
					pageSpecification.getStatus());
			}
		}
		else if (_isPublished(layout.fetchDraftLayout())) {
			Assert.assertEquals(
				PageSpecification.Status.APPROVED,
				pageSpecification.getStatus());
		}
		else {
			Assert.assertEquals(
				PageSpecification.Status.DRAFT, pageSpecification.getStatus());
		}

		Assert.assertEquals(
			PageSpecification.Type.CONTENT_PAGE_SPECIFICATION,
			pageSpecification.getType());
	}

	public static void assertPageSpecifications(
		ContentPageSpecification expectedDraftContentPageSpecification,
		ContentPageSpecification expectedPublishedContentPageSpecification,
		PageSpecification[] pageSpecifications, Layout layout,
		PageSpecification.Status status) {

		ContentPageSpecification publishedContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[0];

		String expectedPublishedContentPageSpecificationExternalReferenceCode =
			expectedPublishedContentPageSpecification.
				getExternalReferenceCode();

		if (expectedPublishedContentPageSpecificationExternalReferenceCode ==
				null) {

			Assert.assertNotNull(layout.getExternalReferenceCode());
			Assert.assertNotNull(
				publishedContentPageSpecification.getExternalReferenceCode());
		}
		else {
			Assert.assertEquals(
				expectedPublishedContentPageSpecificationExternalReferenceCode,
				layout.getExternalReferenceCode());
			Assert.assertEquals(
				expectedPublishedContentPageSpecificationExternalReferenceCode,
				publishedContentPageSpecification.getExternalReferenceCode());
		}

		Assert.assertEquals(
			expectedPublishedContentPageSpecification.getStatus(),
			publishedContentPageSpecification.getStatus());

		ContentPageSpecification draftContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[1];

		Assert.assertNull(
			draftContentPageSpecification.
				getDraftContentPageSpecificationExternalReferenceCode());
		Assert.assertEquals(
			draftContentPageSpecification.getExternalReferenceCode(),
			publishedContentPageSpecification.
				getDraftContentPageSpecificationExternalReferenceCode());

		if (expectedDraftContentPageSpecification.getExternalReferenceCode() ==
				null) {

			Assert.assertNotNull(
				draftContentPageSpecification.getExternalReferenceCode());
		}
		else {
			Assert.assertEquals(
				expectedDraftContentPageSpecification.
					getExternalReferenceCode(),
				draftContentPageSpecification.getExternalReferenceCode());
		}

		Assert.assertEquals(
			expectedDraftContentPageSpecification.getStatus(),
			draftContentPageSpecification.getStatus());

		Assert.assertEquals(
			status, publishedContentPageSpecification.getStatus());

		PageExperiencesTestUtil.assertPageExperiences(
			expectedPublishedContentPageSpecification.getPageExperiences(),
			layout, publishedContentPageSpecification.getPageExperiences());

		Assert.assertEquals(
			expectedPublishedContentPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode(),
			publishedContentPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode());

		Layout draftLayout = layout.fetchDraftLayout();

		if (Objects.equals(PageSpecification.Status.APPROVED, status)) {
			Assert.assertTrue(_isPublished(draftLayout));
		}
		else {
			Assert.assertFalse(_isPublished(draftLayout));
		}

		Assert.assertEquals(
			draftContentPageSpecification.getExternalReferenceCode(),
			draftLayout.getExternalReferenceCode());

		PageExperiencesTestUtil.assertPageExperiences(
			expectedDraftContentPageSpecification.getPageExperiences(),
			draftLayout, draftContentPageSpecification.getPageExperiences());

		Assert.assertEquals(
			expectedDraftContentPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode(),
			draftContentPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode());

		if (Objects.equals(
				PageSpecification.Status.APPROVED,
				expectedDraftContentPageSpecification.getStatus())) {

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, draftLayout.getStatus());
		}
		else {
			Assert.assertEquals(
				WorkflowConstants.STATUS_DRAFT, draftLayout.getStatus());
		}
	}

	public static void assertPageSpecifications(
			Layout layout, PageSpecification[] pageSpecifications)
		throws Exception {

		Assert.assertTrue(ArrayUtil.isNotEmpty(pageSpecifications));

		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent()) {
			Assert.assertEquals(
				Arrays.toString(pageSpecifications), 1,
				pageSpecifications.length);

			PageSpecification pageSpecification = pageSpecifications[0];

			Assert.assertEquals(
				layout.getExternalReferenceCode(),
				pageSpecification.getExternalReferenceCode());
			Assert.assertEquals(
				PageSpecification.Status.APPROVED,
				pageSpecification.getStatus());
			Assert.assertEquals(
				PageSpecification.Type.WIDGET_PAGE_SPECIFICATION,
				pageSpecification.getType());

			return;
		}

		Assert.assertEquals(
			Arrays.toString(pageSpecifications), 2, pageSpecifications.length);

		assertContentPageSpecification(pageSpecifications[0], layout.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		assertContentPageSpecification(
			pageSpecifications[1], draftLayout.getPlid());
	}

	public static ContentPageSpecification getContentPageSpecification(
		String curDraftContentPageSpecificationExternalReferenceCode,
		PageSpecification.Status curStatus) {

		ContentPageSpecification contentPageSpecification =
			new ContentPageSpecification() {
				{
					setDraftContentPageSpecificationExternalReferenceCode(
						() ->
							curDraftContentPageSpecificationExternalReferenceCode);
					setExternalReferenceCode(RandomTestUtil::randomString);
					setStatus(() -> curStatus);
					setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
				}
			};

		contentPageSpecification.setPageExperiences(
			() -> {
				PageExperience pageExperience = new PageExperience();

				pageExperience.setExternalReferenceCode(
					RandomTestUtil::randomString);
				pageExperience.setKey(SegmentsExperienceConstants.KEY_DEFAULT);
				pageExperience.setName_i18n(
					Collections.singletonMap(
						"en-US", RandomTestUtil.randomString()));
				pageExperience.setPageElements(new PageElement[0]);
				pageExperience.setPageSpecificationExternalReferenceCode(
					contentPageSpecification.getExternalReferenceCode());

				return new PageExperience[] {pageExperience};
			});

		return contentPageSpecification;
	}

	public static void testPostSiteSiteByExternalReferenceCodePageSpecification(
			Layout layout, PageSpecification[] pageSpecifications,
			ServiceContext serviceContext,
			UnsafeFunction
				<ContentPageSpecification, ContentPageSpecification, Exception>
					unsafeFunction)
		throws Exception {

		assertPageSpecifications(layout, pageSpecifications);

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertFalse(_isPublished(draftLayout));

		assertPageSpecifications(layout, pageSpecifications);

		ContentPageSpecification publishedContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[0];

		Assert.assertEquals(
			publishedContentPageSpecification.getStatus(),
			PageSpecification.Status.DRAFT);

		publishedContentPageSpecification.setExternalReferenceCode(
			layout.getExternalReferenceCode());

		_assertProblemException(
			() -> unsafeFunction.apply(publishedContentPageSpecification));

		Assert.assertEquals(
			draftLayout.getStatus(), WorkflowConstants.STATUS_APPROVED);

		ContentPageSpecification draftContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[1];

		draftContentPageSpecification.setExternalReferenceCode(
			draftLayout.getExternalReferenceCode());
		draftContentPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		assertContentPageSpecification(
			unsafeFunction.apply(draftContentPageSpecification),
			draftLayout.getPlid());

		draftLayout = LayoutLocalServiceUtil.getLayout(draftLayout.getPlid());

		Assert.assertEquals(
			draftLayout.getStatus(), WorkflowConstants.STATUS_DRAFT);

		_assertProblemException(
			() -> unsafeFunction.apply(draftContentPageSpecification));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		draftLayout = layout.fetchDraftLayout();

		Assert.assertEquals(
			draftLayout.getStatus(), WorkflowConstants.STATUS_APPROVED);

		publishedContentPageSpecification.setExternalReferenceCode(
			draftLayout.getExternalReferenceCode());

		publishedContentPageSpecification.setStatus(
			PageSpecification.Status.APPROVED);

		_assertProblemException(
			() -> unsafeFunction.apply(publishedContentPageSpecification));

		publishedContentPageSpecification.setExternalReferenceCode(
			layout.getExternalReferenceCode());

		_assertProblemException(
			() -> unsafeFunction.apply(publishedContentPageSpecification));

		draftContentPageSpecification.setExternalReferenceCode(
			draftLayout.getExternalReferenceCode());
		draftContentPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		draftLayout = LayoutLocalServiceUtil.getLayout(draftLayout.getPlid());

		Assert.assertEquals(
			draftLayout.getStatus(), WorkflowConstants.STATUS_APPROVED);

		assertContentPageSpecification(
			unsafeFunction.apply(draftContentPageSpecification),
			draftLayout.getPlid());

		_assertProblemException(
			() -> unsafeFunction.apply(draftContentPageSpecification));

		draftLayout = LayoutLocalServiceUtil.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		draftContentPageSpecification.setExternalReferenceCode((String)null);
		draftContentPageSpecification.setStatus((PageSpecification.Status)null);

		assertContentPageSpecification(
			unsafeFunction.apply(draftContentPageSpecification),
			draftLayout.getPlid());

		draftLayout = LayoutLocalServiceUtil.getLayout(draftLayout.getPlid());

		Assert.assertEquals(
			draftLayout.getStatus(), WorkflowConstants.STATUS_DRAFT);

		_assertProblemException(
			() -> unsafeFunction.apply(draftContentPageSpecification));
	}

	private static void _assertProblemException(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();
			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private static boolean _isPublished(Layout draftLayout) {
		return GetterUtil.getBoolean(
			draftLayout.getTypeSettingsProperty(
				LayoutTypeSettingsConstants.KEY_PUBLISHED));
	}

}