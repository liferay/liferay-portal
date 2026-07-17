/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.resource.v1_0;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.headless.admin.fragment.dto.v1_0.ResourceFolder;
import com.liferay.headless.admin.fragment.internal.odata.entity.v1_0.ResourceFolderEntityModel;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.FragmentSetUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.ResourceFolderUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.fragment.internal.util.EnabledUtil;
import com.liferay.headless.admin.fragment.resource.v1_0.ResourceFolderResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/resource-folder.properties",
	scope = ServiceScope.PROTOTYPE, service = ResourceFolderResource.class
)
public class ResourceFolderResourceImpl extends BaseResourceFolderResourceImpl {

	@Override
	public void deleteSiteResourceFolder(
			String siteExternalReferenceCode,
			String resourceFolderExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		Folder folder = _dlAppService.getFolderByExternalReferenceCode(
			resourceFolderExternalReferenceCode,
			GroupUtil.getStagingAwareGroupId(
				true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		ResourceFolderUtil.checkResourceFolder(
			_dlFolderLocalService.getDLFolder(folder.getFolderId()));

		_dlAppService.deleteFolder(folder.getFolderId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<ResourceFolder> getSiteFragmentSetResourceFoldersPage(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode, Pagination pagination)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.
				getFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode, groupId);

		long resourcesFolderId = fragmentCollection.getResourcesFolderId(false);

		if (resourcesFolderId <= 0) {
			return Page.of(Collections.emptyList());
		}

		return _getResourceFoldersPage(groupId, pagination, resourcesFolderId);
	}

	@Override
	public ResourceFolder getSiteResourceFolder(
			String siteExternalReferenceCode,
			String resourceFolderExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		Folder folder = _dlAppService.getFolderByExternalReferenceCode(
			resourceFolderExternalReferenceCode,
			GroupUtil.getGroupId(
				true, true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		DLFolder dlFolder = _dlFolderLocalService.getDLFolder(
			folder.getFolderId());

		ResourceFolderUtil.checkResourceFolder(dlFolder);

		return _toResourceFolder(dlFolder);
	}

	@Override
	public Page<ResourceFolder> getSiteResourceFolderResourceFoldersPage(
			String siteExternalReferenceCode,
			String resourceFolderExternalReferenceCode, Pagination pagination)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		Folder folder = _dlAppService.getFolderByExternalReferenceCode(
			resourceFolderExternalReferenceCode, groupId);

		ResourceFolderUtil.checkResourceFolder(
			_dlFolderLocalService.getDLFolder(folder.getFolderId()));

		return _getResourceFoldersPage(
			groupId, pagination, folder.getFolderId());
	}

	@Override
	public Page<ResourceFolder> getSiteResourceFoldersPage(
			String siteExternalReferenceCode, Filter filter,
			Pagination pagination)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		long[] resourcesFolderIds = transformToLongArray(
			_fragmentCollectionLocalService.getFragmentCollections(groupId),
			fragmentCollection -> {
				long resourcesFolderId =
					fragmentCollection.getResourcesFolderId(false);

				if (resourcesFolderId <= 0) {
					return null;
				}

				return resourcesFolderId;
			});

		return _getResourceFoldersPage(
			filter, groupId, pagination, resourcesFolderIds);
	}

	@Override
	public ResourceFolder postSiteFragmentSetResourceFolder(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode,
			ResourceFolder resourceFolder)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		return _toResourceFolder(
			_addDLFolder(
				_fragmentCollectionService.
					getFragmentCollectionByExternalReferenceCode(
						fragmentSetExternalReferenceCode, groupId),
				groupId, resourceFolder));
	}

	@Override
	public ResourceFolder postSiteResourceFolder(
			String siteExternalReferenceCode, ResourceFolder resourceFolder)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		return _toResourceFolder(
			_addDLFolder(
				_getOrAddFragmentCollection(groupId, resourceFolder), groupId,
				resourceFolder));
	}

	@Override
	public ResourceFolder putSiteResourceFolder(
			String siteExternalReferenceCode,
			String resourceFolderExternalReferenceCode,
			ResourceFolder resourceFolder)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		DLFolder dlFolder =
			_dlFolderLocalService.fetchDLFolderByExternalReferenceCode(
				resourceFolderExternalReferenceCode, groupId);

		if (dlFolder == null) {
			resourceFolder.setExternalReferenceCode(
				() -> resourceFolderExternalReferenceCode);

			return _toResourceFolder(
				_addDLFolder(
					_getOrAddFragmentCollection(groupId, resourceFolder),
					groupId, resourceFolder));
		}

		ResourceFolderUtil.checkResourceFolder(dlFolder);

		Folder folder = _dlAppService.updateFolder(
			dlFolder.getFolderId(), resourceFolder.getName(),
			dlFolder.getDescription(),
			ServiceContextUtil.getServiceContext(
				contextCompany.getCompanyId(), resourceFolder.getDateCreated(),
				groupId, contextHttpServletRequest,
				resourceFolder.getDateModified(), contextUser.getUserId()));

		return _toResourceFolder(
			_dlFolderLocalService.getDLFolder(folder.getFolderId()));
	}

	private DLFolder _addDLFolder(
			FragmentCollection fragmentCollection, long groupId,
			ResourceFolder resourceFolder)
		throws Exception {

		return ResourceFolderUtil.addDLFolder(
			contextCompany.getCompanyId(), fragmentCollection, groupId,
			contextHttpServletRequest,
			contextAcceptLanguage.getPreferredLocale(), resourceFolder,
			contextUser.getUserId());
	}

	private FragmentCollection _getOrAddFragmentCollection(
			long groupId, ResourceFolder resourceFolder)
		throws Exception {

		return FragmentSetUtil.getOrAddFragmentCollection(
			contextCompany.getCompanyId(), resourceFolder.getFragmentSet(),
			resourceFolder.getFragmentSetExternalReferenceCode(), groupId,
			contextHttpServletRequest,
			"a-fragment-set-external-reference-code-is-required-to-create-a-" +
				"new-resource-folder",
			contextAcceptLanguage.getPreferredLocale(),
			contextUser.getUserId());
	}

	private Page<ResourceFolder> _getResourceFoldersPage(
			Filter filter, long groupId, Pagination pagination,
			long[] resourcesFolderIds)
		throws Exception {

		if (ArrayUtil.isEmpty(resourcesFolderIds)) {
			return Page.of(Collections.emptyList());
		}

		return _getResourceFoldersPage(
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				BooleanFilter treePathBooleanFilter = new BooleanFilter();

				for (long resourcesFolderId : resourcesFolderIds) {
					booleanFilter.add(
						new TermFilter(
							Field.ENTRY_CLASS_PK,
							String.valueOf(resourcesFolderId)),
						BooleanClauseOccur.MUST_NOT);
					treePathBooleanFilter.add(
						new TermFilter(
							Field.TREE_PATH, String.valueOf(resourcesFolderId)),
						BooleanClauseOccur.SHOULD);
				}

				booleanFilter.add(
					treePathBooleanFilter, BooleanClauseOccur.MUST);
			},
			filter, groupId, pagination);
	}

	private Page<ResourceFolder> _getResourceFoldersPage(
			long groupId, Pagination pagination, long parentFolderId)
		throws Exception {

		return _getResourceFoldersPage(
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						Field.FOLDER_ID, String.valueOf(parentFolderId)),
					BooleanClauseOccur.MUST);
			},
			null, groupId, pagination);
	}

	private Page<ResourceFolder> _getResourceFoldersPage(
			UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
			Filter filter, long groupId, Pagination pagination)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(), booleanQueryUnsafeConsumer, filter,
			DLFolder.class.getName(), null, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute("showHidden", Boolean.TRUE);
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			null,
			document -> _toResourceFolder(
				_dlFolderLocalService.getDLFolder(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private ResourceFolder _toResourceFolder(DLFolder dlFolder)
		throws Exception {

		return _resourceFolderDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				false, null, _dtoConverterRegistry, contextHttpServletRequest,
				dlFolder.getFolderId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			dlFolder);
	}

	private static final EntityModel _entityModel =
		new ResourceFolderEntityModel();

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.fragment.internal.dto.v1_0.converter.ResourceFolderDTOConverter)"
	)
	private DTOConverter<DLFolder, ResourceFolder> _resourceFolderDTOConverter;

}