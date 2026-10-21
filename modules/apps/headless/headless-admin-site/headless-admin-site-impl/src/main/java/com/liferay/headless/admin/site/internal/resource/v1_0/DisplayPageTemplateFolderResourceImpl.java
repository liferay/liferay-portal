/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.odata.entity.v1_0.DisplayPageTemplateFolderEntityModel;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.DisplayPageTemplateFolderActionUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.DisplayPageTemplateFolderUtil;
import com.liferay.headless.admin.site.internal.util.EnabledUtil;
import com.liferay.headless.admin.site.resource.v1_0.DisplayPageTemplateFolderResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 * @author Bárbara Cabrera
 * @author Javier Moral
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/display-page-template-folder.properties",
	property = {
		"crud.entity.class.name=com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateFolder",
		"crud.item.delegate=true",
		"export.import.vulcan.batch.engine.task.item.delegate=true"
	},
	scope = ServiceScope.PROTOTYPE,
	service = DisplayPageTemplateFolderResource.class
)
public class DisplayPageTemplateFolderResourceImpl
	extends BaseDisplayPageTemplateFolderResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate
		<DisplayPageTemplateFolder>,
			   VulcanCRUDItemDelegate<DisplayPageTemplateFolder> {

	@Override
	public void deleteDesignLibraryDisplayPageTemplateFolder(
			String designLibraryExternalReferenceCode,
			String displayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		_layoutPageTemplateCollectionService.deleteLayoutPageTemplateCollection(
			displayPageTemplateFolderExternalReferenceCode,
			_getDesignLibraryGroupId(designLibraryExternalReferenceCode));
	}

	@Override
	public void deleteSiteDisplayPageTemplateFolder(
			String siteExternalReferenceCode,
			String displayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		_layoutPageTemplateCollectionService.deleteLayoutPageTemplateCollection(
			displayPageTemplateFolderExternalReferenceCode,
			GroupUtil.getStagingAwareGroupId(
				contextCompany.getCompanyId(), siteExternalReferenceCode));
	}

	@Override
	public DisplayPageTemplateFolder getDesignLibraryDisplayPageTemplateFolder(
			String designLibraryExternalReferenceCode,
			String displayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		return _toDesignLibraryDisplayPageTemplateFolder(
			designLibraryExternalReferenceCode,
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					displayPageTemplateFolderExternalReferenceCode,
					_getDesignLibraryGroupId(
						designLibraryExternalReferenceCode)));
	}

	@Override
	public Page<Permission>
			getDesignLibraryDisplayPageTemplateFolderPermissionsPage(
				String designLibraryExternalReferenceCode,
				String displayPageTemplateFolderExternalReferenceCode,
				String roleNames)
		throws Exception {

		long groupId = _getDesignLibraryGroupId(
			designLibraryExternalReferenceCode);
		Long resourceId = getPermissionCheckerResourceId(
			designLibraryExternalReferenceCode,
			displayPageTemplateFolderExternalReferenceCode);
		String resourceName = getPermissionCheckerResourceName(
			designLibraryExternalReferenceCode,
			displayPageTemplateFolderExternalReferenceCode);

		_permissionService.checkPermission(groupId, resourceName, resourceId);

		return _toDesignLibraryPermissionPage(
			groupId, resourceId, resourceName, roleNames);
	}

	@Override
	public Page<DisplayPageTemplateFolder>
			getDesignLibraryDisplayPageTemplateFoldersPage(
				String designLibraryExternalReferenceCode, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		long groupId = _getDesignLibraryGroupId(
			designLibraryExternalReferenceCode);

		if (!_depotEntryModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				_depotEntryLocalService.getGroupDepotEntry(groupId),
				ActionKeys.VIEW)) {

			return Page.of(Collections.emptyList());
		}

		return _getDisplayPageTemplateFoldersPage(
			aggregation, filter, groupId, pagination, search, sorts,
			layoutPageTemplateCollection ->
				_toDesignLibraryDisplayPageTemplateFolder(
					designLibraryExternalReferenceCode,
					layoutPageTemplateCollection));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<LayoutPageTemplateCollection>
		getExportImportDescriptor() {

		return new ExportImportDescriptor<>() {

			@Override
			public Function<LayoutPageTemplateCollection, Boolean>
				getApplicableModelFunction() {

				return layoutPageTemplateCollection ->
					layoutPageTemplateCollection.getType() ==
						LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE;
			}

			@Override
			public String getKey() {
				return LayoutPageTemplateCollection.class.getName() + "-" +
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE;
			}

			@Override
			public String getLabelLanguageKey() {
				return "display-page-template-folders";
			}

			@Override
			public Class<LayoutPageTemplateCollection> getModelClass() {
				return LayoutPageTemplateCollection.class;
			}

			@Override
			public String getPortletId() {
				return LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES;
			}

			@Override
			public Scope getScope() {
				return Scope.SITE;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_DESIGN;
			}

			@Override
			public boolean isStagingSupported() {
				return true;
			}

		};
	}

	@Override
	public DisplayPageTemplateFolder getItem(Long id) throws Exception {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(id);

		if ((layoutPageTemplateCollection == null) ||
			(layoutPageTemplateCollection.getType() !=
				LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE)) {

			throw new NotFoundException(
				"No display page template folder exists with ID " + id);
		}

		Group group = _groupLocalService.getGroup(
			layoutPageTemplateCollection.getGroupId());

		if (group.isDepot()) {
			EnabledUtil.checkDesignLibrariesEnabled(contextCompany);

			return _toDesignLibraryDisplayPageTemplateFolder(
				group.getExternalReferenceCode(), layoutPageTemplateCollection);
		}

		return _toDisplayPageTemplateFolder(layoutPageTemplateCollection);
	}

	@Override
	public Page<Permission>
			putDesignLibraryDisplayPageTemplateFolderPermissionsPage(
				String designLibraryExternalReferenceCode,
				String displayPageTemplateFolderExternalReferenceCode,
				Permission[] permissions)
		throws Exception {

		super.putSiteDisplayPageTemplateFolderPermissionsPage(
			designLibraryExternalReferenceCode,
			displayPageTemplateFolderExternalReferenceCode, permissions);

		long groupId = _getDesignLibraryGroupId(
			designLibraryExternalReferenceCode);
		Long resourceId = getPermissionCheckerResourceId(
			designLibraryExternalReferenceCode,
			displayPageTemplateFolderExternalReferenceCode);
		String resourceName = getPermissionCheckerResourceName(
			designLibraryExternalReferenceCode,
			displayPageTemplateFolderExternalReferenceCode);

		return _toDesignLibraryPermissionPage(
			groupId, resourceId, resourceName, null);
	}

	@Override
	protected DisplayPageTemplateFolder doGetSiteDisplayPageTemplateFolder(
			String siteExternalReferenceCode,
			String displayPageTemplateFolderExternalReferenceCode)
		throws Exception {

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

		return _getDisplayPageTemplateFoldersPage(
			aggregation, filter,
			GroupUtil.getGroupId(
				true, contextCompany.getCompanyId(), siteExternalReferenceCode),
			pagination, search, sorts, this::_toDisplayPageTemplateFolder);
	}

	@Override
	protected DisplayPageTemplateFolder doPostSiteDisplayPageTemplateFolder(
			String siteExternalReferenceCode,
			DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		return _addDisplayPageTemplateFolder(
			displayPageTemplateFolder,
			GroupUtil.getStagingAwareGroupId(
				contextCompany.getCompanyId(), siteExternalReferenceCode));
	}

	@Override
	protected DisplayPageTemplateFolder doPutSiteDisplayPageTemplateFolder(
			String siteExternalReferenceCode,
			String displayPageTemplateFolderExternalReferenceCode,
			DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		long groupId = GroupUtil.getStagingAwareGroupId(
			contextCompany.getCompanyId(), siteExternalReferenceCode);

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

	private long _getDesignLibraryGroupId(
			String designLibraryExternalReferenceCode)
		throws Exception {

		return GroupUtil.getDepotGroupId(
			contextCompany.getCompanyId(), designLibraryExternalReferenceCode,
			DepotConstants.TYPE_DESIGN_LIBRARY);
	}

	private Page<DisplayPageTemplateFolder> _getDisplayPageTemplateFoldersPage(
			Aggregation aggregation, Filter filter, long groupId,
			Pagination pagination, String search, Sort[] sorts,
			UnsafeFunction
				<LayoutPageTemplateCollection, DisplayPageTemplateFolder,
				 Exception> unsafeFunction)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			filter, LayoutPageTemplateCollection.class.getName(), search,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setAttribute(
					Field.TYPE,
					String.valueOf(
						LayoutPageTemplateCollectionTypeConstants.
							DISPLAY_PAGE));
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> unsafeFunction.apply(
				_layoutPageTemplateCollectionService.
					fetchLayoutPageTemplateCollection(
						GetterUtil.getLong(
							document.get(Field.ENTRY_CLASS_PK)))));
	}

	private DisplayPageTemplateFolder _toDesignLibraryDisplayPageTemplateFolder(
			String designLibraryExternalReferenceCode,
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws Exception {

		return _displayPageTemplateFolderDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage,
				DisplayPageTemplateFolderActionUtil.getDesignLibraryActions(
					contextScopeChecker, designLibraryExternalReferenceCode,
					layoutPageTemplateCollection,
					_layoutPageTemplateCollectionModelResourcePermission,
					contextUriInfo),
				Collections.emptyMap(), _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				contextUriInfo, contextUser),
			layoutPageTemplateCollection);
	}

	private Page<Permission> _toDesignLibraryPermissionPage(
			long groupId, Long resourceId, String resourceName,
			String roleNames)
		throws Exception {

		return toPermissionPage(
			HashMapBuilder.put(
				"get",
				addAction(
					ActionKeys.PERMISSIONS, resourceId,
					"getDesignLibraryDisplayPageTemplateFolderPermissionsPage",
					null, resourceName, groupId)
			).put(
				"replace",
				addAction(
					ActionKeys.PERMISSIONS, resourceId,
					"putDesignLibraryDisplayPageTemplateFolderPermissionsPage",
					null, resourceName, groupId)
			).build(),
			resourceId, resourceName, roleNames);
	}

	private DisplayPageTemplateFolder _toDisplayPageTemplateFolder(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws Exception {

		return _displayPageTemplateFolderDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				contextUriInfo, contextUser),
			layoutPageTemplateCollection);
	}

	private static final EntityModel _entityModel =
		new DisplayPageTemplateFolderEntityModel();

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference(target = "(model.class.name=com.liferay.depot.model.DepotEntry)")
	private ModelResourcePermission<DepotEntry>
		_depotEntryModelResourcePermission;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.DisplayPageTemplateFolderDTOConverter)"
	)
	private DTOConverter
		<LayoutPageTemplateCollection, DisplayPageTemplateFolder>
			_displayPageTemplateFolderDTOConverter;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateCollection)"
	)
	private ModelResourcePermission<LayoutPageTemplateCollection>
		_layoutPageTemplateCollectionModelResourcePermission;

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private PermissionService _permissionService;

}