/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.resource.v1_0;

import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.headless.admin.fragment.dto.v1_0.ResourceFolder;
import com.liferay.headless.admin.fragment.internal.odata.entity.v1_0.ResourceFolderEntityModel;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.FragmentSetUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.fragment.internal.util.EnabledUtil;
import com.liferay.headless.admin.fragment.resource.v1_0.ResourceFolderResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

		_checkResourceFolder(
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

		_checkResourceFolder(dlFolder);

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

		_checkResourceFolder(
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

		List<Long> resourcesFolderIds = new ArrayList<>();

		for (FragmentCollection fragmentCollection :
				_fragmentCollectionLocalService.getFragmentCollections(
					groupId)) {

			long resourcesFolderId = fragmentCollection.getResourcesFolderId(
				false);

			if (resourcesFolderId > 0) {
				resourcesFolderIds.add(resourcesFolderId);
			}
		}

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

		_checkResourceFolder(dlFolder);

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

		DLFolder parentDLFolder = _getParentDLFolder(
			fragmentCollection, groupId, resourceFolder);

		Folder folder = _dlAppService.addFolder(
			resourceFolder.getExternalReferenceCode(),
			parentDLFolder.getRepositoryId(), parentDLFolder.getFolderId(),
			resourceFolder.getName(), StringPool.BLANK,
			ServiceContextUtil.getServiceContext(
				contextCompany.getCompanyId(), resourceFolder.getDateCreated(),
				groupId, contextHttpServletRequest,
				resourceFolder.getDateModified(), contextUser.getUserId()));

		return _dlFolderLocalService.getDLFolder(folder.getFolderId());
	}

	private void _checkResourceFolder(DLFolder dlFolder) throws Exception {
		if (FragmentSetUtil.getFragmentCollection(dlFolder) == null) {
			throw new NoSuchFolderException(
				"No resource folder exists with external reference code " +
					dlFolder.getExternalReferenceCode());
		}
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

	private DLFolder _getParentDLFolder(
			FragmentCollection fragmentCollection, long groupId,
			ResourceFolder resourceFolder)
		throws Exception {

		ResourceFolder parentResourceFolder =
			resourceFolder.getParentResourceFolder();
		String parentResourceFolderExternalReferenceCode =
			resourceFolder.getParentResourceFolderExternalReferenceCode();

		if (Validator.isNull(parentResourceFolderExternalReferenceCode)) {
			if (!LazyReferencingThreadLocal.isEnabled() ||
				(parentResourceFolder == null) ||
				Validator.isNull(
					parentResourceFolder.getExternalReferenceCode())) {

				return _dlFolderLocalService.getDLFolder(
					fragmentCollection.getResourcesFolderId(true));
			}

			parentResourceFolderExternalReferenceCode =
				parentResourceFolder.getExternalReferenceCode();
		}

		DLFolder parentDLFolder =
			_dlFolderLocalService.fetchDLFolderByExternalReferenceCode(
				parentResourceFolderExternalReferenceCode, groupId);

		if ((parentDLFolder == null) && (parentResourceFolder != null) &&
			LazyReferencingThreadLocal.isEnabled()) {

			if (!Objects.equals(
					parentResourceFolder.getExternalReferenceCode(),
					parentResourceFolderExternalReferenceCode)) {

				throw new IllegalArgumentException(
					_language.get(
						contextAcceptLanguage.getPreferredLocale(),
						"the-parent-resource-folder-external-reference-codes-" +
							"do-not-match"));
			}

			parentDLFolder = _addDLFolder(
				_getOrAddFragmentCollection(groupId, parentResourceFolder),
				groupId, parentResourceFolder);
		}

		if ((parentDLFolder != null) &&
			(FragmentSetUtil.getFragmentCollection(parentDLFolder) == null)) {

			parentDLFolder = null;
		}

		if (parentDLFolder == null) {
			throw new IllegalArgumentException(
				_language.format(
					contextAcceptLanguage.getPreferredLocale(),
					"no-resource-folder-was-found-with-external-reference-" +
						"code-x",
					parentResourceFolderExternalReferenceCode));
		}

		return parentDLFolder;
	}

	private Page<ResourceFolder> _getResourceFoldersPage(
			Filter filter, long groupId, Pagination pagination,
			List<Long> resourcesFolderIds)
		throws Exception {

		if (resourcesFolderIds.isEmpty()) {
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

	@Reference
	private Language _language;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.fragment.internal.dto.v1_0.converter.ResourceFolderDTOConverter)"
	)
	private DTOConverter<DLFolder, ResourceFolder> _resourceFolderDTOConverter;

}