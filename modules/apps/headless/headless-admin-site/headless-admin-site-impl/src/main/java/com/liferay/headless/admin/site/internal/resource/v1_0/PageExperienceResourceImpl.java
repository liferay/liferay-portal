/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.SegmentsExperienceUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.site.resource.v1_0.PageExperienceResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.segments.constants.SegmentsActionKeys;
import com.liferay.segments.constants.SegmentsConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

import jakarta.ws.rs.NotFoundException;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/page-experience.properties",
	scope = ServiceScope.PROTOTYPE, service = PageExperienceResource.class
)
public class PageExperienceResourceImpl extends BasePageExperienceResourceImpl {

	@Override
	@Tags({@Tag(description = "[DEV]", name = "PageExperience")})
	public void deleteSitePageExperience(
			String siteExternalReferenceCode,
			String pageExperienceExternalReferenceCode)
		throws Exception {

		long groupId = GroupUtil.getStagingAwareGroupId(
			contextCompany.getCompanyId(), siteExternalReferenceCode);

		SegmentsExperience segmentsExperience =
			_segmentsExperienceService.
				getSegmentsExperienceByExternalReferenceCode(
					pageExperienceExternalReferenceCode, groupId);

		if (segmentsExperience.isDefault()) {
			throw new IllegalArgumentException(
				"The default page experience cannot be deleted or modified");
		}

		Layout layout = _layoutLocalService.fetchLayout(
			segmentsExperience.getPlid());

		if (!layout.isDraftLayout()) {
			throw new IllegalArgumentException(
				"Page experiences can only be modified on a draft page " +
					"specification");
		}

		_segmentsExperienceService.deleteSegmentsExperience(
			pageExperienceExternalReferenceCode, groupId);
	}

	@Override
	public PageExperience getSitePageExperience(
			String siteExternalReferenceCode,
			String pageExperienceExternalReferenceCode)
		throws Exception {

		return _toPageExperience(
			_segmentsExperienceService.
				getSegmentsExperienceByExternalReferenceCode(
					pageExperienceExternalReferenceCode,
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode)));
	}

	@Override
	public Page<PageExperience> getSitePageSpecificationPageExperiencesPage(
			String siteExternalReferenceCode,
			String pageSpecificationExternalReferenceCode)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			pageSpecificationExternalReferenceCode,
			GroupUtil.getGroupId(
				true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		if (layout == null) {
			return Page.of(Collections.emptyList());
		}

		return Page.of(
			transform(
				_segmentsExperienceService.getSegmentsExperiences(
					layout.getGroupId(), layout.getPlid(), true,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
				this::_toPageExperience));
	}

	@Override
	public PageExperience postSitePageSpecificationPageExperience(
			String siteExternalReferenceCode,
			String pageSpecificationExternalReferenceCode,
			PageExperience pageExperience)
		throws Exception {

		long groupId = GroupUtil.getStagingAwareGroupId(
			contextCompany.getCompanyId(), siteExternalReferenceCode);

		Layout layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			pageExperience.getPageSpecificationExternalReferenceCode(),
			groupId);

		if (!layout.isDraftLayout()) {
			throw new IllegalArgumentException(
				"Page experiences can only be modified on a draft page " +
					"specification");
		}

		return _addPageExperience(layout, groupId, pageExperience);
	}

	@Override
	public PageExperience putSitePageExperience(
			String siteExternalReferenceCode,
			String pageExperienceExternalReferenceCode,
			PageExperience pageExperience)
		throws Exception {

		long groupId = GroupUtil.getStagingAwareGroupId(
			contextCompany.getCompanyId(), siteExternalReferenceCode);

		Layout layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			pageExperience.getPageSpecificationExternalReferenceCode(),
			groupId);

		if (!layout.isDraftLayout()) {
			throw new IllegalArgumentException(
				"Page experiences can only be modified on a draft page " +
					"specification");
		}

		SegmentsExperience segmentsExperience =
			_segmentsExperienceService.
				fetchSegmentsExperienceByExternalReferenceCode(
					pageExperienceExternalReferenceCode, groupId);

		if (segmentsExperience == null) {
			return _addPageExperience(layout, groupId, pageExperience);
		}

		if (segmentsExperience.isDefault()) {
			throw new IllegalArgumentException(
				"The default page experience cannot be deleted or modified");
		}

		if (layout.getPlid() != segmentsExperience.getPlid()) {
			throw new IllegalArgumentException(
				"The page experience does not belong to this page " +
					"specification");
		}

		_segmentsExperienceResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), segmentsExperience,
			ActionKeys.UPDATE);

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout, contextUser)) {

			return _toPageExperience(
				SegmentsExperienceUtil.updateSegmentsExperience(
					_fragmentEntryProcessorRegistry, _infoItemServiceRegistry,
					layout, pageExperience, pageExperience.getPriority(),
					segmentsExperience,
					ServiceContextUtil.createServiceContext(
						groupId, contextHttpServletRequest,
						contextUser.getUserId())));
		}
	}

	@Override
	protected void preparePatch(
		PageExperience pageExperience, PageExperience existingPageExperience) {

		if (pageExperience.getPageElements() != null) {
			existingPageExperience.setPageElements(
				pageExperience::getPageElements);
		}
	}

	private PageExperience _addPageExperience(
			Layout layout, long groupId, PageExperience pageExperience)
		throws Exception {

		if (!_layoutPermission.containsLayoutRestrictedUpdatePermission(
				PermissionThreadLocal.getPermissionChecker(), layout)) {

			_portletResourcePermission.check(
				PermissionThreadLocal.getPermissionChecker(), groupId,
				SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);
		}

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout, contextUser)) {

			SegmentsExperienceUtil.validateSegmentsExperienceLayout(layout);

			return _toPageExperience(
				SegmentsExperienceUtil.addSegmentsExperience(
					_fragmentEntryProcessorRegistry, _infoItemServiceRegistry,
					layout, pageExperience, pageExperience.getPriority(),
					ServiceContextUtil.createServiceContext(
						groupId, contextHttpServletRequest,
						contextUser.getUserId(), pageExperience.getUuid())));
		}
	}

	private PageExperience _toPageExperience(
			SegmentsExperience segmentsExperience)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					segmentsExperience.getGroupId(),
					segmentsExperience.getPlid());

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					segmentsExperience.getSegmentsExperienceId());

		if (layoutPageTemplateStructureRel == null) {
			throw new NotFoundException(
				StringBundler.concat(
					"No page experience exists with the external reference ",
					"code \"", segmentsExperience.getExternalReferenceCode(),
					"\""));
		}

		return _pageExperienceDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage,
				HashMapBuilder.<String, Object>put(
					"companyId", segmentsExperience.getCompanyId()
				).put(
					"scopeGroupId", segmentsExperience.getGroupId()
				).build(),
				_dtoConverterRegistry, contextHttpServletRequest,
				segmentsExperience.getSegmentsExperienceId(), contextUriInfo,
				contextUser),
			layoutPageTemplateStructureRel);
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageExperienceDTOConverter)"
	)
	private DTOConverter<LayoutPageTemplateStructureRel, PageExperience>
		_pageExperienceDTOConverter;

	@Reference(
		target = "(resource.name=" + SegmentsConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.segments.model.SegmentsExperience)"
	)
	private ModelResourcePermission<SegmentsExperience>
		_segmentsExperienceResourcePermission;

	@Reference
	private SegmentsExperienceService _segmentsExperienceService;

}