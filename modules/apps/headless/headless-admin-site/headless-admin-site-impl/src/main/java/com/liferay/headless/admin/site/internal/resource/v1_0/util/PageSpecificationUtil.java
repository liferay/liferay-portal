/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.BasicFragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.EmbeddedPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.FormFragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FragmentInstance;
import com.liferay.headless.admin.site.dto.v1_0.LinkToPagePageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.LinkToURLPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.dto.v1_0.PageSetPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.WidgetInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageSpecification;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.constants.SegmentsExperienceConstants;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Rubén Pulido
 */
public class PageSpecificationUtil {

	public static PageSpecification getPageSpecification(
		PageSpecification[] pageSpecifications) {

		if (ArrayUtil.isEmpty(pageSpecifications)) {
			return null;
		}

		if (pageSpecifications.length != 1) {
			throw new IllegalArgumentException(
				"Exactly one page specification is required");
		}

		PageSpecification pageSpecification = pageSpecifications[0];

		if ((!(pageSpecification instanceof EmbeddedPageSpecification) &&
			 !(pageSpecification instanceof LinkToPagePageSpecification) &&
			 !(pageSpecification instanceof LinkToURLPageSpecification) &&
			 !(pageSpecification instanceof PageSetPageSpecification) &&
			 !(pageSpecification instanceof WidgetPageSpecification)) ||
			!Objects.equals(
				pageSpecification.getStatus(),
				PageSpecification.Status.APPROVED)) {

			throw new IllegalArgumentException(
				"The page specification is invalid or has not been approved");
		}

		return pageSpecification;
	}

	public static int getPublishedStatus(
		PageSpecification[] pageSpecifications) {

		if (pageSpecifications == null) {
			return WorkflowConstants.STATUS_DRAFT;
		}

		if (pageSpecifications.length != 2) {
			throw new IllegalArgumentException(
				"Exactly two page specifications are required");
		}

		PageSpecification[] sortedContentPageSpecifications =
			getSortedContentPageSpecifications(pageSpecifications);

		ContentPageSpecification publishedContentPageSpecification =
			(ContentPageSpecification)sortedContentPageSpecifications[1];

		if (Objects.equals(
				publishedContentPageSpecification.getStatus(),
				PageSpecification.Status.APPROVED)) {

			return WorkflowConstants.STATUS_APPROVED;
		}

		return WorkflowConstants.STATUS_DRAFT;
	}

	public static PageSpecification[] getSortedContentPageSpecifications(
		PageSpecification[] pageSpecifications) {

		if (pageSpecifications == null) {
			return null;
		}

		if (pageSpecifications.length != 2) {
			throw new IllegalArgumentException(
				"Exactly two page specifications are required");
		}

		ContentPageSpecification draftContentPageSpecification;
		ContentPageSpecification publishedContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[0];

		if (Validator.isNull(
				publishedContentPageSpecification.
					getDraftContentPageSpecificationExternalReferenceCode())) {

			draftContentPageSpecification = publishedContentPageSpecification;
			publishedContentPageSpecification =
				(ContentPageSpecification)pageSpecifications[1];
		}
		else {
			draftContentPageSpecification =
				(ContentPageSpecification)pageSpecifications[1];
		}

		if (Validator.isNull(
				publishedContentPageSpecification.
					getDraftContentPageSpecificationExternalReferenceCode()) ||
			!Objects.equals(
				draftContentPageSpecification.getExternalReferenceCode(),
				publishedContentPageSpecification.
					getDraftContentPageSpecificationExternalReferenceCode())) {

			throw new IllegalArgumentException(
				"The draft page specification's external reference code does " +
					"not match the published page specification's draft " +
						"external reference code");
		}

		return new PageSpecification[] {
			draftContentPageSpecification, publishedContentPageSpecification
		};
	}

	public static WidgetPageSpecification getWidgetPageSpecification(
		PageSpecification[] pageSpecifications) {

		return (WidgetPageSpecification)getPageSpecification(
			pageSpecifications);
	}

	public static PageSpecification[] toContentPageSpecifications(
		ContentPageSpecification contentPageSpecification,
		String sitePageExternalReferenceCode) {

		ContentPageSpecification draftContentPageSpecification = null;
		ContentPageSpecification publishedContentPageSpecification = null;

		if (Objects.equals(
				contentPageSpecification.getExternalReferenceCode(),
				sitePageExternalReferenceCode)) {

			String draftContentPageSpecificationExternalReferenceCode =
				contentPageSpecification.
					getDraftContentPageSpecificationExternalReferenceCode();

			if (Validator.isNull(
					draftContentPageSpecificationExternalReferenceCode)) {

				draftContentPageSpecificationExternalReferenceCode =
					sitePageExternalReferenceCode +
						LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT;
			}

			publishedContentPageSpecification = contentPageSpecification;

			draftContentPageSpecification = _toDraftContentPageSpecification(
				draftContentPageSpecificationExternalReferenceCode,
				publishedContentPageSpecification);

			_processPublishedContentPageSpecification(
				draftContentPageSpecificationExternalReferenceCode,
				publishedContentPageSpecification);
		}
		else {
			draftContentPageSpecification = contentPageSpecification;

			publishedContentPageSpecification =
				_toPublishedContentPageSpecification(
					draftContentPageSpecification,
					sitePageExternalReferenceCode);

			draftContentPageSpecification.setStatus(
				() -> PageSpecification.Status.APPROVED);
		}

		return new PageSpecification[] {
			publishedContentPageSpecification, draftContentPageSpecification
		};
	}

	private static void _processDraftFragmentInstance(
		FragmentInstance fragmentInstance) {

		if (fragmentInstance == null) {
			return;
		}

		fragmentInstance.setUuid(() -> null);

		String draftFragmentInstanceExternalReferenceCode =
			fragmentInstance.getDraftFragmentInstanceExternalReferenceCode();

		if (Validator.isNotNull(draftFragmentInstanceExternalReferenceCode)) {
			fragmentInstance.setDraftFragmentInstanceExternalReferenceCode(
				() -> null);
			fragmentInstance.setFragmentInstanceExternalReferenceCode(
				() -> draftFragmentInstanceExternalReferenceCode);

			return;
		}

		String fragmentInstanceExternalReferenceCode =
			fragmentInstance.getFragmentInstanceExternalReferenceCode();

		if (Validator.isNotNull(fragmentInstanceExternalReferenceCode)) {
			fragmentInstance.setFragmentInstanceExternalReferenceCode(
				() ->
					fragmentInstanceExternalReferenceCode +
						LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT);
		}
	}

	private static void _processDraftWidgetInstancePageElementDefinition(
		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition) {

		String draftWidgetInstanceExternalReferenceCode =
			widgetInstancePageElementDefinition.
				getDraftWidgetInstanceExternalReferenceCode();

		if (Validator.isNotNull(draftWidgetInstanceExternalReferenceCode)) {
			widgetInstancePageElementDefinition.
				setDraftWidgetInstanceExternalReferenceCode(() -> null);
			widgetInstancePageElementDefinition.
				setWidgetInstanceExternalReferenceCode(
					() -> draftWidgetInstanceExternalReferenceCode);

			return;
		}

		String widgetInstanceExternalReferenceCode =
			widgetInstancePageElementDefinition.
				getWidgetInstanceExternalReferenceCode();

		if (Validator.isNotNull(widgetInstanceExternalReferenceCode)) {
			widgetInstancePageElementDefinition.
				setWidgetInstanceExternalReferenceCode(
					() ->
						widgetInstanceExternalReferenceCode +
							LayoutConstants.
								EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT);
		}
	}

	private static void _processPageElements(
		Consumer<FragmentInstance> fragmentInstanceConsumer,
		PageElement[] pageElements,
		Consumer<WidgetInstancePageElementDefinition>
			widgetInstancePageElementDefinitionConsumer) {

		if (pageElements == null) {
			return;
		}

		for (PageElement pageElement : pageElements) {
			PageElementDefinition pageElementDefinition =
				pageElement.getPageElementDefinition();

			if (pageElementDefinition instanceof
					BasicFragmentInstancePageElementDefinition
						basicFragmentInstancePageElementDefinition) {

				fragmentInstanceConsumer.accept(
					basicFragmentInstancePageElementDefinition.
						getFragmentInstance());
			}
			else if (pageElementDefinition instanceof
						FormFragmentInstancePageElementDefinition
							formFragmentInstancePageElementDefinition) {

				fragmentInstanceConsumer.accept(
					formFragmentInstancePageElementDefinition.
						getFragmentInstance());
			}
			else if (pageElementDefinition instanceof
						WidgetInstancePageElementDefinition
							widgetInstancePageElementDefinition) {

				widgetInstancePageElementDefinitionConsumer.accept(
					widgetInstancePageElementDefinition);
			}

			_processPageElements(
				fragmentInstanceConsumer, pageElement.getPageElements(),
				widgetInstancePageElementDefinitionConsumer);
		}
	}

	private static void _processPublishedContentPageSpecification(
		String draftContentPageSpecificationExternalReferenceCode,
		ContentPageSpecification publishedContentPageSpecification) {

		if (Validator.isNull(
				publishedContentPageSpecification.
					getDraftContentPageSpecificationExternalReferenceCode())) {

			publishedContentPageSpecification.
				setDraftContentPageSpecificationExternalReferenceCode(
					() -> draftContentPageSpecificationExternalReferenceCode);
		}

		PageExperience[] pageExperiences =
			publishedContentPageSpecification.getPageExperiences();

		if (ArrayUtil.isEmpty(pageExperiences)) {
			return;
		}

		for (PageExperience pageExperience : pageExperiences) {
			_processPageElements(
				PageSpecificationUtil::_processPublishedFragmentInstance,
				pageExperience.getPageElements(),
				PageSpecificationUtil::
					_processPublishedWidgetInstancePageElementDefinition);
		}
	}

	private static void _processPublishedFragmentInstance(
		FragmentInstance fragmentInstance) {

		if ((fragmentInstance == null) ||
			Validator.isNotNull(
				fragmentInstance.
					getDraftFragmentInstanceExternalReferenceCode())) {

			return;
		}

		String fragmentInstanceExternalReferenceCode =
			fragmentInstance.getFragmentInstanceExternalReferenceCode();

		if (Validator.isNotNull(fragmentInstanceExternalReferenceCode)) {
			fragmentInstance.setDraftFragmentInstanceExternalReferenceCode(
				() ->
					fragmentInstanceExternalReferenceCode +
						LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT);
		}
	}

	private static void _processPublishedFromDraftFragmentInstance(
		FragmentInstance fragmentInstance) {

		if (fragmentInstance == null) {
			return;
		}

		fragmentInstance.setUuid(() -> null);

		String draftFragmentInstanceExternalReferenceCode =
			fragmentInstance.getFragmentInstanceExternalReferenceCode();

		if (Validator.isNull(draftFragmentInstanceExternalReferenceCode)) {
			return;
		}

		String publishedFragmentInstanceExternalReferenceCode =
			_toPublishedExternalReferenceCode(
				draftFragmentInstanceExternalReferenceCode);

		fragmentInstance.setDraftFragmentInstanceExternalReferenceCode(
			() -> draftFragmentInstanceExternalReferenceCode);
		fragmentInstance.setFragmentInstanceExternalReferenceCode(
			() -> publishedFragmentInstanceExternalReferenceCode);
	}

	private static void
		_processPublishedFromDraftWidgetInstancePageElementDefinition(
			WidgetInstancePageElementDefinition
				widgetInstancePageElementDefinition) {

		String draftWidgetInstanceExternalReferenceCode =
			widgetInstancePageElementDefinition.
				getWidgetInstanceExternalReferenceCode();

		if (Validator.isNull(draftWidgetInstanceExternalReferenceCode)) {
			return;
		}

		String publishedWidgetInstanceExternalReferenceCode =
			_toPublishedExternalReferenceCode(
				draftWidgetInstanceExternalReferenceCode);

		widgetInstancePageElementDefinition.
			setDraftWidgetInstanceExternalReferenceCode(
				() -> draftWidgetInstanceExternalReferenceCode);
		widgetInstancePageElementDefinition.
			setWidgetInstanceExternalReferenceCode(
				() -> publishedWidgetInstanceExternalReferenceCode);
	}

	private static void _processPublishedWidgetInstancePageElementDefinition(
		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition) {

		if (Validator.isNotNull(
				widgetInstancePageElementDefinition.
					getDraftWidgetInstanceExternalReferenceCode())) {

			return;
		}

		String widgetInstanceExternalReferenceCode =
			widgetInstancePageElementDefinition.
				getWidgetInstanceExternalReferenceCode();

		if (Validator.isNotNull(widgetInstanceExternalReferenceCode)) {
			widgetInstancePageElementDefinition.
				setDraftWidgetInstanceExternalReferenceCode(
					() ->
						widgetInstanceExternalReferenceCode +
							LayoutConstants.
								EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT);
		}
	}

	private static ContentPageSpecification _toDraftContentPageSpecification(
		String draftContentPageSpecificationExternalReferenceCode,
		ContentPageSpecification publishedContentPageSpecification) {

		ContentPageSpecification draftContentPageSpecification =
			ContentPageSpecification.unsafeToDTO(
				publishedContentPageSpecification.toString());

		draftContentPageSpecification.setExternalReferenceCode(
			() -> draftContentPageSpecificationExternalReferenceCode);
		draftContentPageSpecification.setStatus(
			() -> PageSpecification.Status.APPROVED);
		draftContentPageSpecification.
			setDraftContentPageSpecificationExternalReferenceCode(() -> null);

		PageExperience[] pageExperiences =
			draftContentPageSpecification.getPageExperiences();

		if (ArrayUtil.isEmpty(pageExperiences)) {
			return draftContentPageSpecification;
		}

		for (PageExperience pageExperience : pageExperiences) {
			String pageExperienceExternalReferenceCode =
				pageExperience.getExternalReferenceCode();

			pageExperience.setExternalReferenceCode(
				() -> {
					String externalReferenceCode =
						draftContentPageSpecificationExternalReferenceCode +
							LayoutConstants.
								EXTERNAL_REFERENCE_CODE_SUFFIX_DEFAULT;

					if (!Objects.equals(
							pageExperience.getKey(),
							SegmentsExperienceConstants.KEY_DEFAULT)) {

						externalReferenceCode =
							pageExperienceExternalReferenceCode +
								LayoutConstants.
									EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT;
					}

					return externalReferenceCode;
				});

			pageExperience.setPageSpecificationExternalReferenceCode(
				() -> draftContentPageSpecificationExternalReferenceCode);
			pageExperience.setUuid(() -> null);

			_processPageElements(
				PageSpecificationUtil::_processDraftFragmentInstance,
				pageExperience.getPageElements(),
				PageSpecificationUtil::
					_processDraftWidgetInstancePageElementDefinition);
		}

		return draftContentPageSpecification;
	}

	private static ContentPageSpecification
		_toPublishedContentPageSpecification(
			ContentPageSpecification draftContentPageSpecification,
			String publishedContentPageSpecificationExternalReferenceCode) {

		ContentPageSpecification publishedContentPageSpecification =
			ContentPageSpecification.unsafeToDTO(
				draftContentPageSpecification.toString());

		publishedContentPageSpecification.setExternalReferenceCode(
			() -> publishedContentPageSpecificationExternalReferenceCode);
		publishedContentPageSpecification.
			setDraftContentPageSpecificationExternalReferenceCode(
				draftContentPageSpecification::getExternalReferenceCode);

		PageExperience[] pageExperiences =
			publishedContentPageSpecification.getPageExperiences();

		if (ArrayUtil.isEmpty(pageExperiences)) {
			return publishedContentPageSpecification;
		}

		for (PageExperience pageExperience : pageExperiences) {
			String publishedPageExperienceExternalReferenceCode;

			if (Objects.equals(
					pageExperience.getKey(),
					SegmentsExperienceConstants.KEY_DEFAULT)) {

				publishedPageExperienceExternalReferenceCode =
					publishedContentPageSpecificationExternalReferenceCode +
						LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DEFAULT;
			}
			else {
				publishedPageExperienceExternalReferenceCode =
					_toPublishedExternalReferenceCode(
						pageExperience.getExternalReferenceCode());
			}

			String publishedPageExperienceExternalReferenceCodeFinal =
				publishedPageExperienceExternalReferenceCode;

			pageExperience.setExternalReferenceCode(
				() -> publishedPageExperienceExternalReferenceCodeFinal);

			pageExperience.setPageSpecificationExternalReferenceCode(
				() -> publishedContentPageSpecificationExternalReferenceCode);
			pageExperience.setUuid(() -> null);

			_processPageElements(
				PageSpecificationUtil::
					_processPublishedFromDraftFragmentInstance,
				pageExperience.getPageElements(),
				PageSpecificationUtil::
					_processPublishedFromDraftWidgetInstancePageElementDefinition);
		}

		return publishedContentPageSpecification;
	}

	private static String _toPublishedExternalReferenceCode(
		String externalReferenceCode) {

		if (Validator.isNull(externalReferenceCode)) {
			return externalReferenceCode;
		}

		if (externalReferenceCode.endsWith(
				LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT)) {

			int suffixDraftLength =
				LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT.length();

			return externalReferenceCode.substring(
				0, externalReferenceCode.length() - suffixDraftLength);
		}

		return externalReferenceCode +
			LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_PUBLISHED;
	}

}