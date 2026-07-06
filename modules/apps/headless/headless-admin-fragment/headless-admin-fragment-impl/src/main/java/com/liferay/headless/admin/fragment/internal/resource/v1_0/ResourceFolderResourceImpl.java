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
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.dto.v1_0.ResourceFolder;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.FragmentSetUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.fragment.internal.util.EnabledUtil;
import com.liferay.headless.admin.fragment.resource.v1_0.ResourceFolderResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

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
				_getOrAddFragmentCollection(
					resourceFolder.getFragmentSet(), groupId),
				groupId, resourceFolder));
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

	private FragmentCollection _getFragmentCollection(DLFolder dlFolder) {
		DLFolder parentDLFolder = _dlFolderLocalService.fetchDLFolder(
			dlFolder.getParentFolderId());

		while ((parentDLFolder != null) && !parentDLFolder.isMountPoint()) {
			dlFolder = parentDLFolder;

			parentDLFolder = _dlFolderLocalService.fetchDLFolder(
				dlFolder.getParentFolderId());
		}

		return _fragmentCollectionLocalService.fetchFragmentCollection(
			dlFolder.getGroupId(), dlFolder.getName());
	}

	private FragmentCollection _getOrAddFragmentCollection(
			FragmentSet fragmentSet, long groupId)
		throws Exception {

		if ((fragmentSet == null) ||
			Validator.isNull(fragmentSet.getExternalReferenceCode())) {

			throw new IllegalArgumentException(
				_language.get(
					contextAcceptLanguage.getPreferredLocale(),
					"a-fragment-set-external-reference-code-is-required-to-" +
						"create-a-new-resource-folder"));
		}

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					fragmentSet.getExternalReferenceCode(), groupId);

		if (fragmentCollection != null) {
			return fragmentCollection;
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			throw new IllegalArgumentException(
				_language.format(
					contextAcceptLanguage.getPreferredLocale(),
					"no-fragment-set-was-found-with-external-reference-code-x",
					fragmentSet.getExternalReferenceCode()));
		}

		return FragmentSetUtil.addFragmentCollection(
			fragmentSet,
			ServiceContextUtil.getServiceContext(
				contextCompany.getCompanyId(), fragmentSet.getDateCreated(),
				groupId, contextHttpServletRequest,
				fragmentSet.getDateModified(), contextUser.getUserId()));
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
				_getOrAddFragmentCollection(
					parentResourceFolder.getFragmentSet(), groupId),
				groupId, parentResourceFolder);
		}

		if ((parentDLFolder != null) &&
			(_getFragmentCollection(parentDLFolder) == null)) {

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