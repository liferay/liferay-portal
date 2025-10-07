/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.site.internal.odata.entity.v1_0.DisplayPageTemplateFolderEntityModel;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.DisplayPageTemplateFolderUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.GroupUtil;
import com.liferay.headless.admin.site.resource.v1_0.DisplayPageTemplateFolderResource;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 * @author Bárbara Cabrera
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/display-page-template-folder.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE,
	service = DisplayPageTemplateFolderResource.class
)
public class DisplayPageTemplateFolderResourceImpl
	extends BaseDisplayPageTemplateFolderResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate
		<DisplayPageTemplateFolder> {

	@Override
	public void deleteSiteDisplayPageTemplateFolder(
			String siteExternalReferenceCode,
			String displayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		_layoutPageTemplateCollectionService.deleteLayoutPageTemplateCollection(
			displayPageTemplateFolderExternalReferenceCode,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportDescriptor() {

			@Override
			public String getItemClassName() {
				return LayoutPageTemplateCollection.class.getName();
			}

			@Override
			public String getLabel() {
				return "display-page-template-folders";
			}

			@Override
			public String getPortletId() {
				return LayoutAdminPortletKeys.GROUP_PAGES;
			}

			@Override
			public Scope getScope() {
				return Scope.SITE;
			}

			@Override
			public boolean isActive(PortletDataContext portletDataContext) {
				return FeatureFlagManagerUtil.isEnabled("LPD-35443");
			}

		};
	}

	@Override
	protected DisplayPageTemplateFolder doGetSiteDisplayPageTemplateFolder(
			String siteExternalReferenceCode,
			String displayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		return _toDisplayPageTemplateFolder(
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					displayPageTemplateFolderExternalReferenceCode,
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode)));
	}

	@Override
	protected Page<DisplayPageTemplateFolder>
			doGetSiteDisplayPageTemplateFoldersPage(
				String siteExternalReferenceCode, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		long groupId = GroupUtil.getGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			filter, LayoutPageTemplateCollection.class.getName(), search,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					Field.TYPE,
					String.valueOf(
						LayoutPageTemplateCollectionTypeConstants.
							DISPLAY_PAGE));
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> _toDisplayPageTemplateFolder(
				_layoutPageTemplateCollectionService.
					fetchLayoutPageTemplateCollection(
						GetterUtil.getLong(
							document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	protected DisplayPageTemplateFolder doPostSiteDisplayPageTemplateFolder(
			String siteExternalReferenceCode,
			DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		return _addDisplayPageTemplateFolder(
			displayPageTemplateFolder,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));
	}

	@Override
	protected DisplayPageTemplateFolder doPutSiteDisplayPageTemplateFolder(
			String siteExternalReferenceCode,
			String displayPageTemplateFolderExternalReferenceCode,
			DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		long groupId = GroupUtil.getGroupId(
			false, contextCompany.getCompanyId(), siteExternalReferenceCode);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					displayPageTemplateFolderExternalReferenceCode, groupId);

		if (layoutPageTemplateCollection == null) {
			return _addDisplayPageTemplateFolder(
				displayPageTemplateFolder, groupId);
		}

		long parentLayoutPageTemplateCollectionId =
			DisplayPageTemplateFolderUtil.
				getParentLayoutPageTemplateCollectionId(
					displayPageTemplateFolder, groupId,
					contextHttpServletRequest);

		if (!Objects.equals(
				layoutPageTemplateCollection.
					getParentLayoutPageTemplateCollectionId(),
				parentLayoutPageTemplateCollectionId)) {

			layoutPageTemplateCollection =
				_layoutPageTemplateCollectionService.
					moveLayoutPageTemplateCollection(
						layoutPageTemplateCollection.
							getLayoutPageTemplateCollectionId(),
						parentLayoutPageTemplateCollectionId);
		}

		return _toDisplayPageTemplateFolder(
			_layoutPageTemplateCollectionService.
				updateLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					displayPageTemplateFolder.getName(),
					displayPageTemplateFolder.getDescription()));
	}

	@Override
	protected Long getPermissionCheckerResourceId(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					externalReferenceCode,
					getPermissionCheckerGroupId(groupExternalReferenceCode));

		return layoutPageTemplateCollection.getPrimaryKey();
	}

	@Override
	protected String getPermissionCheckerResourceName(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		return LayoutPageTemplateCollection.class.getName();
	}

	private DisplayPageTemplateFolder _addDisplayPageTemplateFolder(
			DisplayPageTemplateFolder displayPageTemplateFolder, long groupId)
		throws Exception {

		return _toDisplayPageTemplateFolder(
			DisplayPageTemplateFolderUtil.addLayoutPageTemplateCollection(
				displayPageTemplateFolder, groupId, contextHttpServletRequest));
	}

	private DisplayPageTemplateFolder _toDisplayPageTemplateFolder(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws Exception {

		return _displayPageTemplateFolderDTOConverter.toDTO(
			layoutPageTemplateCollection);
	}

	private static final EntityModel _entityModel =
		new DisplayPageTemplateFolderEntityModel();

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.DisplayPageTemplateFolderDTOConverter)"
	)
	private DTOConverter
		<LayoutPageTemplateCollection, DisplayPageTemplateFolder>
			_displayPageTemplateFolderDTOConverter;

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

}