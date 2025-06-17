/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetTagService;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.MasterPage;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.Scope;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.FileEntryUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.GroupUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.PageSpecificationUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.site.resource.v1_0.MasterPageResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/master-page.properties",
	scope = ServiceScope.PROTOTYPE, service = MasterPageResource.class
)
public class MasterPageResourceImpl extends BaseMasterPageResourceImpl {

	@Override
	public void deleteSiteSiteByExternalReferenceCodeMasterPage(
			String siteExternalReferenceCode,
			String masterPageExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			masterPageExternalReferenceCode,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));
	}

	@Override
	public MasterPage getSiteSiteByExternalReferenceCodeMasterPage(
			String siteExternalReferenceCode,
			String masterPageExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					masterPageExternalReferenceCode,
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				layoutPageTemplateEntry.getType())) {

			throw new UnsupportedOperationException();
		}

		return _masterPageDTOConverter.toDTO(layoutPageTemplateEntry);
	}

	@Override
	public Page<MasterPage> getSiteSiteByExternalReferenceCodeMasterPagesPage(
			String siteExternalReferenceCode, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		long groupId = GroupUtil.getGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		if (Validator.isNull(search)) {
			return Page.of(
				transform(
					_layoutPageTemplateEntryService.
						getLayoutPageTemplateEntries(
							groupId,
							LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
							pagination.getStartPosition(),
							pagination.getEndPosition(), null),
					layoutPageTemplateEntry -> _masterPageDTOConverter.toDTO(
						layoutPageTemplateEntry)),
				pagination,
				_layoutPageTemplateEntryService.
					getLayoutPageTemplateEntriesCount(
						groupId,
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));
		}

		return Page.of(
			transform(
				_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
					groupId, 0, 0, search,
					LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
					pagination.getStartPosition(), pagination.getEndPosition(),
					null),
				layoutPageTemplateEntry -> _masterPageDTOConverter.toDTO(
					layoutPageTemplateEntry)),
			pagination,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				groupId, 0, 0, search,
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));
	}

	@Override
	public MasterPage postSiteSiteByExternalReferenceCodeMasterPage(
			String siteExternalReferenceCode, MasterPage masterPage)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		return _addMasterPage(
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode),
			masterPage);
	}

	@Override
	public ContentPageSpecification
			postSiteSiteByExternalReferenceCodeMasterPagePageSpecification(
				String siteExternalReferenceCode,
				String pageTemplateExternalReferenceCode,
				ContentPageSpecification contentPageSpecification)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode,
					GroupUtil.getGroupId(
						false, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				layoutPageTemplateEntry.getType())) {

			throw new UnsupportedOperationException();
		}

		return (ContentPageSpecification)_pageSpecificationDTOConverter.toDTO(
			LayoutUtil.addDraftToLayout(
				contentPageSpecification,
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				ServiceContextUtil.createServiceContext(
					layoutPageTemplateEntry.getGroupId(),
					contextHttpServletRequest, contextUser.getUserId())));
	}

	@Override
	public MasterPage putSiteSiteByExternalReferenceCodeMasterPage(
			String siteExternalReferenceCode,
			String masterPageExternalReferenceCode, MasterPage masterPage)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		long groupId = GroupUtil.getGroupId(
			false, contextCompany.getCompanyId(), siteExternalReferenceCode);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					masterPageExternalReferenceCode, groupId);

		if (layoutPageTemplateEntry == null) {
			return _addMasterPage(groupId, masterPage);
		}

		long previewFileEntryId = FileEntryUtil.getPreviewFileEntryId(
			groupId, masterPage.getThumbnail());

		if (previewFileEntryId !=
				layoutPageTemplateEntry.getPreviewFileEntryId()) {

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					previewFileEntryId);
		}

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		layout = LayoutUtil.updateContentLayout(
			layout, layout.getNameMap(), layout.getTitleMap(),
			layout.getDescriptionMap(), layout.getRobotsMap(),
			layout.getFriendlyURLMap(), masterPage.getPageSpecifications(),
			_getServiceContext(groupId, masterPage));

		if (!layoutPageTemplateEntry.isApproved() && layout.isPublished()) {
			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.updateStatus(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					WorkflowConstants.STATUS_APPROVED);
		}

		if (!Objects.equals(
				GetterUtil.getBoolean(masterPage.getMarkedAsDefault()),
				layoutPageTemplateEntry.isDefaultTemplate())) {

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					GetterUtil.getBoolean(masterPage.getMarkedAsDefault()));
		}

		ServiceContext serviceContext = _getServiceContext(groupId, masterPage);

		serviceContext.setAssetCategoryIds(
			_getAssetCategoryIds(
				groupId,
				masterPage.getTaxonomyCategoryItemExternalReferences()));
		serviceContext.setAssetTagNames(
			_getAssetTagNames(
				groupId, masterPage.getKeywordItemExternalReferences()));

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			return _masterPageDTOConverter.toDTO(
				_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					masterPage.getName()));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Override
	protected void preparePatch(
		MasterPage masterPage, MasterPage existingMasterPage) {

		if (masterPage.getKeywordItemExternalReferences() != null) {
			existingMasterPage.setKeywordItemExternalReferences(
				masterPage::getKeywordItemExternalReferences);
		}

		if (masterPage.getPageSpecifications() != null) {
			existingMasterPage.setPageSpecifications(
				masterPage::getPageSpecifications);
		}

		if (masterPage.getTaxonomyCategoryItemExternalReferences() != null) {
			existingMasterPage.setTaxonomyCategoryItemExternalReferences(
				masterPage::getTaxonomyCategoryItemExternalReferences);
		}

		if (masterPage.getThumbnail() != null) {
			existingMasterPage.setThumbnail(masterPage::getThumbnail);
		}
	}

	private MasterPage _addMasterPage(long groupId, MasterPage masterPage)
		throws Exception {

		boolean defaultTemplate = false;

		if (GetterUtil.getBoolean(masterPage.getMarkedAsDefault())) {
			defaultTemplate = true;
		}

		ServiceContext serviceContext = _getServiceContext(groupId, masterPage);

		serviceContext.setAssetCategoryIds(
			_getAssetCategoryIds(
				groupId,
				masterPage.getTaxonomyCategoryItemExternalReferences()));
		serviceContext.setAssetTagNames(
			_getAssetTagNames(
				groupId, masterPage.getKeywordItemExternalReferences()));

		return _masterPageDTOConverter.toDTO(
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				masterPage.getExternalReferenceCode(), groupId,
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				masterPage.getKey(), 0, 0, masterPage.getName(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				FileEntryUtil.getPreviewFileEntryId(
					groupId, masterPage.getThumbnail()),
				defaultTemplate, 0,
				_getLayoutPlid(groupId, masterPage, serviceContext), 0,
				PageSpecificationUtil.getPublishedStatus(
					masterPage.getPageSpecifications()),
				serviceContext));
	}

	private long[] _getAssetCategoryIds(
			long groupId, ItemExternalReference[] itemExternalReferences)
		throws Exception {

		if (ArrayUtil.isEmpty(itemExternalReferences)) {
			return new long[0];
		}

		Group group = _groupService.getGroup(groupId);

		return unsafeTransformToLongArray(
			ListUtil.fromArray(itemExternalReferences),
			itemExternalReference -> {
				long scopeGroupId = groupId;

				Scope scope = itemExternalReference.getScope();

				if (scope != null) {
					scopeGroupId = GroupUtil.getGroupId(
						true, true, group.getCompanyId(),
						scope.getExternalReferenceCode());
				}

				AssetCategory assetCategory =
					_assetCategoryService.fetchCategoryByExternalReferenceCode(
						itemExternalReference.getExternalReferenceCode(),
						scopeGroupId);

				if (assetCategory == null) {
					throw new UnsupportedOperationException();
				}

				return assetCategory.getCategoryId();
			});
	}

	private String[] _getAssetTagNames(
			long groupId, ItemExternalReference[] itemExternalReferences)
		throws Exception {

		if (ArrayUtil.isEmpty(itemExternalReferences)) {
			return new String[0];
		}

		Group group = _groupService.getGroup(groupId);

		return transform(
			itemExternalReferences,
			itemExternalReference -> {
				long scopeGroupId = groupId;

				Scope scope = itemExternalReference.getScope();

				if (scope != null) {
					scopeGroupId = GroupUtil.getGroupId(
						true, true, group.getCompanyId(),
						scope.getExternalReferenceCode());
				}

				AssetTag assetTag =
					_assetTagService.fetchAssetTagByExternalReferenceCode(
						itemExternalReference.getExternalReferenceCode(),
						scopeGroupId);

				if (assetTag == null) {
					throw new UnsupportedOperationException();
				}

				return assetTag.getName();
			},
			String.class);
	}

	private long _getLayoutPlid(
			long groupId, MasterPage masterPage, ServiceContext serviceContext)
		throws Exception {

		Map<Locale, String> nameMap = Collections.singletonMap(
			_portal.getSiteDefaultLocale(groupId), masterPage.getName());

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);
		serviceContext.setAttribute(
			"layout.page.template.entry.type",
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

		Layout layout = LayoutUtil.addContentLayout(
			groupId, masterPage.getPageSpecifications(), true, nameMap, nameMap,
			nameMap, null, LayoutConstants.TYPE_CONTENT, null, true, true,
			Collections.emptyMap(), WorkflowConstants.STATUS_APPROVED,
			serviceContext);

		if (layout == null) {
			return 0;
		}

		return layout.getPlid();
	}

	private ServiceContext _getServiceContext(
		long groupId, MasterPage masterPage) {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, contextHttpServletRequest, null
		).build();

		serviceContext.setCreateDate(masterPage.getDateCreated());
		serviceContext.setModifiedDate(masterPage.getDateModified());
		serviceContext.setUserId(contextUser.getUserId());
		serviceContext.setUuid(masterPage.getUuid());

		return serviceContext;
	}

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private AssetTagService _assetTagService;

	@Reference
	private GroupService _groupService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.MasterPageDTOConverter)"
	)
	private DTOConverter<LayoutPageTemplateEntry, MasterPage>
		_masterPageDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationDTOConverter)"
	)
	private DTOConverter<Layout, PageSpecification>
		_pageSpecificationDTOConverter;

	@Reference
	private Portal _portal;

}