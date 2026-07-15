/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.ParentTaxonomyCategory;
import com.liferay.headless.admin.site.dto.v1_0.ParentTaxonomyVocabulary;
import com.liferay.headless.admin.site.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.site.internal.util.LogUtil;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.scope.Scope;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class ServiceContextUtil {

	public static ServiceContext createServiceContext(
			long companyId, Date createDate, long groupId,
			HttpServletRequest httpServletRequest, String[] keywords,
			Date modifiedDate, TaxonomyCategoryBrief[] taxonomyCategoryBriefs,
			long userId, String uuid)
		throws Exception {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, httpServletRequest, null
		).build();

		serviceContext.setAssetCategoryIds(
			_getAssetCategoryIds(groupId, taxonomyCategoryBriefs));
		serviceContext.setAssetTagNames(keywords);
		serviceContext.setCompanyId(companyId);
		serviceContext.setCreateDate(createDate);
		serviceContext.setModifiedDate(modifiedDate);
		serviceContext.setUserId(userId);
		serviceContext.setUuid(uuid);

		return serviceContext;
	}

	public static ServiceContext createServiceContext(
		long groupId, HttpServletRequest httpServletRequest, long userId) {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, httpServletRequest, null
		).build();

		serviceContext.setUserId(userId);

		return serviceContext;
	}

	public static ServiceContext createServiceContext(
		long groupId, HttpServletRequest httpServletRequest, long userId,
		String uuid) {

		ServiceContext serviceContext = createServiceContext(
			groupId, httpServletRequest, userId);

		serviceContext.setUuid(uuid);

		return serviceContext;
	}

	public static void setContentPageSpecificationsAttributes(
			ContentPageSpecification draftContentPageSpecification,
			long groupId,
			ContentPageSpecification publishedContentPageSpecification,
			ServiceContext serviceContext)
		throws Exception {

		PageExperience defaultPageExperience =
			PageExperienceUtil.getDefaultPageExperience(
				publishedContentPageSpecification.getPageExperiences());

		serviceContext.setAttribute(
			"defaultSegmentsExperienceExternalReferenceCode",
			defaultPageExperience.getExternalReferenceCode());
		serviceContext.setAttribute(
			"defaultSegmentsExperienceUuid", defaultPageExperience.getUuid());

		defaultPageExperience = PageExperienceUtil.getDefaultPageExperience(
			draftContentPageSpecification.getPageExperiences());

		serviceContext.setAttribute(
			"draftLayoutDefaultSegmentsExperienceExternalReferenceCode",
			defaultPageExperience.getExternalReferenceCode());
		serviceContext.setAttribute(
			"draftLayoutDefaultSegmentsExperienceUuid",
			defaultPageExperience.getUuid());

		serviceContext.setAttribute(
			"draftLayoutExternalReferenceCode",
			draftContentPageSpecification.getExternalReferenceCode());

		setLayoutSetPrototypeLayoutERC(
			groupId, draftContentPageSpecification, serviceContext,
			draftContentPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode());
		setLayoutSetPrototypeLayoutERC(
			groupId, publishedContentPageSpecification, serviceContext,
			publishedContentPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode());

		if (Objects.equals(
				publishedContentPageSpecification.getStatus(),
				PageSpecification.Status.APPROVED)) {

			serviceContext.setAttribute("published", Boolean.TRUE.toString());
		}
		else {
			serviceContext.setAttribute("published", Boolean.FALSE.toString());
		}
	}

	public static void setLayoutSetPrototypeLayoutERC(
			long groupId, PageSpecification pageSpecification,
			ServiceContext serviceContext,
			String siteTemplatePageSpecificationExternalReferenceCode)
		throws Exception {

		if (Validator.isNull(
				siteTemplatePageSpecificationExternalReferenceCode)) {

			if (MergeLayoutPrototypesThreadLocal.isInProgress()) {
				siteTemplatePageSpecificationExternalReferenceCode =
					pageSpecification.getExternalReferenceCode();
			}
			else {
				return;
			}
		}

		boolean privateLayout = Boolean.FALSE;

		int layoutPageTemplateEntryType = GetterUtil.getInteger(
			serviceContext.getAttribute("layout.page.template.entry.type"), -1);

		if (Objects.equals(
				LayoutPageTemplateEntryTypeConstants.BASIC,
				layoutPageTemplateEntryType) ||
			Objects.equals(
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				layoutPageTemplateEntryType) ||
			Objects.equals(
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
				layoutPageTemplateEntryType)) {

			privateLayout = Boolean.TRUE;
		}

		boolean draftLayout = Boolean.FALSE;

		if (Objects.equals(
				PageSpecification.Type.CONTENT_PAGE_SPECIFICATION,
				pageSpecification.getType())) {

			ContentPageSpecification contentPageSpecification =
				(ContentPageSpecification)pageSpecification;

			if (Validator.isNull(
					contentPageSpecification.
						getDraftContentPageSpecificationExternalReferenceCode())) {

				draftLayout = Boolean.TRUE;
			}
		}

		LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			groupId, privateLayout);

		if (!layoutSet.isLayoutSetPrototypeLinkActive()) {
			return;
		}

		LayoutSetPrototype layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSet.getLayoutSetPrototypeUuid(),
					layoutSet.getCompanyId());

		Layout layoutSetPrototypeLayout =
			LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
				siteTemplatePageSpecificationExternalReferenceCode,
				layoutSetPrototype.getGroupId());

		if (layoutSetPrototypeLayout == null) {
			LogUtil.logOptionalReference(
				Layout.class,
				siteTemplatePageSpecificationExternalReferenceCode,
				layoutSetPrototype.getGroupId());
		}

		serviceContext.setAttribute(
			draftLayout ? "draftLayoutLayoutSetPrototypeLayoutERC" :
				"layoutSetPrototypeLayoutERC",
			siteTemplatePageSpecificationExternalReferenceCode);
	}

	private static long[] _getAssetCategoryIds(
			long groupId, TaxonomyCategoryBrief[] taxonomyCategoryBriefs)
		throws Exception {

		if (ArrayUtil.isEmpty(taxonomyCategoryBriefs)) {
			return new long[0];
		}

		Group group = GroupServiceUtil.getGroup(groupId);

		return TransformUtil.unsafeTransformToLongArray(
			ListUtil.fromArray(taxonomyCategoryBriefs),
			taxonomyCategoryBrief -> {
				long scopeGroupId = groupId;

				Scope scope = taxonomyCategoryBrief.getScope();

				if (scope != null) {
					scopeGroupId = GroupUtil.getGroupId(
						true, true, group.getCompanyId(),
						scope.getExternalReferenceCode());
				}

				String parentTaxonomyCategoryExternalReferenceCode = null;

				ParentTaxonomyVocabulary parentTaxonomyVocabulary =
					taxonomyCategoryBrief.getParentTaxonomyVocabulary();

				ParentTaxonomyCategory parentTaxonomyCategory =
					taxonomyCategoryBrief.getParentTaxonomyCategory();

				if (parentTaxonomyCategory != null) {
					parentTaxonomyCategoryExternalReferenceCode =
						parentTaxonomyCategory.getExternalReferenceCode();
				}

				AssetCategory assetCategory =
					AssetCategoryServiceUtil.getOrAddEmptyCategoryWithAncestors(
						taxonomyCategoryBrief.
							getTaxonomyCategoryExternalReferenceCode(),
						scopeGroupId,
						parentTaxonomyCategoryExternalReferenceCode,
						parentTaxonomyVocabulary.getExternalReferenceCode());

				return assetCategory.getCategoryId();
			});
	}

}