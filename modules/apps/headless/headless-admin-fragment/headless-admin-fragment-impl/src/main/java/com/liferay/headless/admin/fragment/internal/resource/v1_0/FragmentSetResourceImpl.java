/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.resource.v1_0;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.internal.odata.entity.v1_0.FragmentSetEntityModel;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.FragmentCollectionActionUtil;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.fragment.internal.util.EnabledUtil;
import com.liferay.headless.admin.fragment.resource.v1_0.FragmentSetResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/fragment-set.properties",
	property = {
		"crud.entity.class.name=com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet",
		"crud.item.delegate=true"
	},
	scope = ServiceScope.PROTOTYPE, service = FragmentSetResource.class
)
public class FragmentSetResourceImpl
	extends BaseFragmentSetResourceImpl
	implements VulcanCRUDItemDelegate<FragmentSet> {

	@Override
	public void deleteDesignLibraryFragmentSet(
			String designLibraryExternalReferenceCode,
			String fragmentSetExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkDesignLibrariesEnabled(contextCompany);

		_fragmentCollectionService.deleteFragmentCollection(
			fragmentSetExternalReferenceCode,
			GroupUtil.getDepotGroupId(
				contextCompany.getCompanyId(),
				designLibraryExternalReferenceCode,
				DepotConstants.TYPE_DESIGN_LIBRARY));
	}

	@Override
	public void deleteSiteFragmentSet(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		_fragmentCollectionService.deleteFragmentCollection(
			fragmentSetExternalReferenceCode,
			GroupUtil.getStagingAwareGroupId(
				true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));
	}

	@Override
	public FragmentSet getDesignLibraryFragmentSet(
			String designLibraryExternalReferenceCode,
			String fragmentSetExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkDesignLibrariesEnabled(contextCompany);

		long groupId = GroupUtil.getDepotGroupId(
			contextCompany.getCompanyId(), designLibraryExternalReferenceCode,
			DepotConstants.TYPE_DESIGN_LIBRARY);

		return _getFragmentSet(
			fragmentSetExternalReferenceCode, groupId,
			_getDesignLibraryActionsUnsafeFunction(
				designLibraryExternalReferenceCode, groupId));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public FragmentSet getItem(Long id) throws Exception {
		FragmentCollection fragmentCollection =
			_fragmentCollectionService.getFragmentCollection(id);

		Group group = _groupLocalService.getGroup(
			fragmentCollection.getGroupId());

		if (group.isDepot()) {
			EnabledUtil.checkDesignLibrariesEnabled(contextCompany);

			return _toFragmentSet(
				fragmentCollection,
				_getDesignLibraryActionsUnsafeFunction(
					group.getExternalReferenceCode(), group.getGroupId()));
		}

		EnabledUtil.checkEnabled(contextCompany);

		return _toFragmentSet(
			fragmentCollection,
			_getSiteActionsUnsafeFunction(
				group.getGroupId(), group.getExternalReferenceCode()));
	}

	@Override
	public FragmentSet getSiteFragmentSet(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		return _getFragmentSet(
			fragmentSetExternalReferenceCode, groupId,
			_getSiteActionsUnsafeFunction(groupId, siteExternalReferenceCode));
	}

	@Override
	public Page<FragmentSet> getSiteFragmentSetsPage(
			String siteExternalReferenceCode, Filter filter,
			Pagination pagination)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		UnsafeFunction
			<FragmentCollection, Map<String, Map<String, String>>, Exception>
				unsafeFunction = _getSiteActionsUnsafeFunction(
					groupId, siteExternalReferenceCode);

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			filter, FragmentCollection.class.getName(), null, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			null,
			document -> {
				FragmentCollection fragmentCollection =
					_fragmentCollectionService.fetchFragmentCollection(
						GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));

				if (fragmentCollection == null) {
					return null;
				}

				return _toFragmentSet(fragmentCollection, unsafeFunction);
			});
	}

	@Override
	public FragmentSet postSiteFragmentSet(
			String siteExternalReferenceCode, FragmentSet fragmentSet)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		return _toFragmentSet(
			_fragmentCollectionService.addFragmentCollection(
				fragmentSet.getExternalReferenceCode(), groupId,
				fragmentSet.getKey(), fragmentSet.getName(),
				fragmentSet.getDescription(),
				GetterUtil.getBoolean(fragmentSet.getMarketplace()),
				ServiceContextUtil.getServiceContext(
					contextCompany.getCompanyId(), fragmentSet.getDateCreated(),
					groupId, contextHttpServletRequest,
					fragmentSet.getDateModified(), contextUser.getUserId())),
			_getSiteActionsUnsafeFunction(groupId, siteExternalReferenceCode));
	}

	@Override
	public FragmentSet putSiteFragmentSet(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode, FragmentSet fragmentSet)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode, groupId);

		if (fragmentCollection == null) {
			return _toFragmentSet(
				_fragmentCollectionService.addFragmentCollection(
					fragmentSetExternalReferenceCode, groupId,
					fragmentSet.getKey(), fragmentSet.getName(),
					fragmentSet.getDescription(),
					GetterUtil.getBoolean(fragmentSet.getMarketplace()),
					ServiceContextUtil.getServiceContext(
						contextCompany.getCompanyId(),
						fragmentSet.getDateCreated(), groupId,
						contextHttpServletRequest,
						fragmentSet.getDateModified(),
						contextUser.getUserId())),
				_getSiteActionsUnsafeFunction(
					groupId, siteExternalReferenceCode));
		}

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextUtil.getServiceContext(
				contextCompany.getCompanyId(), null, groupId,
				contextHttpServletRequest, fragmentSet.getDateModified(),
				contextUser.getUserId()));

		try {
			return _toFragmentSet(
				_fragmentCollectionService.updateFragmentCollection(
					fragmentCollection.getFragmentCollectionId(),
					fragmentSet.getName(), fragmentSet.getDescription()),
				_getSiteActionsUnsafeFunction(
					groupId, siteExternalReferenceCode));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private UnsafeFunction
		<FragmentCollection, Map<String, Map<String, String>>, Exception>
			_getDesignLibraryActionsUnsafeFunction(
				String designLibraryExternalReferenceCode, long groupId) {

		boolean manageFragmentEntries = _hasManageFragmentEntriesPermission(
			groupId);

		return fragmentCollection ->
			FragmentCollectionActionUtil.getDesignLibraryActions(
				contextScopeChecker, designLibraryExternalReferenceCode,
				fragmentCollection, manageFragmentEntries, contextUriInfo);
	}

	private FragmentSet _getFragmentSet(
			String externalReferenceCode, long groupId,
			UnsafeFunction
				<FragmentCollection, Map<String, Map<String, String>>,
				 Exception> unsafeFunction)
		throws Exception {

		return _toFragmentSet(
			_fragmentCollectionService.
				getFragmentCollectionByExternalReferenceCode(
					externalReferenceCode, groupId),
			unsafeFunction);
	}

	private UnsafeFunction
		<FragmentCollection, Map<String, Map<String, String>>, Exception>
			_getSiteActionsUnsafeFunction(
				long groupId, String siteExternalReferenceCode) {

		boolean manageFragmentEntries = _hasManageFragmentEntriesPermission(
			groupId);

		return fragmentCollection ->
			FragmentCollectionActionUtil.getSiteActions(
				contextScopeChecker, fragmentCollection, manageFragmentEntries,
				siteExternalReferenceCode, contextUriInfo);
	}

	private boolean _hasManageFragmentEntriesPermission(long groupId) {
		return _portletResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);
	}

	private FragmentSet _toFragmentSet(
			FragmentCollection fragmentCollection,
			UnsafeFunction
				<FragmentCollection, Map<String, Map<String, String>>,
				 Exception> unsafeFunction)
		throws Exception {

		return _fragmentSetDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				false, unsafeFunction.apply(fragmentCollection), null,
				_dtoConverterRegistry, contextHttpServletRequest,
				fragmentCollection.getFragmentCollectionId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			fragmentCollection);
	}

	private static final EntityModel _entityModel =
		new FragmentSetEntityModel();

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.fragment.internal.dto.v1_0.converter.FragmentSetDTOConverter)"
	)
	private DTOConverter<FragmentCollection, FragmentSet>
		_fragmentSetDTOConverter;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(resource.name=" + FragmentConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}