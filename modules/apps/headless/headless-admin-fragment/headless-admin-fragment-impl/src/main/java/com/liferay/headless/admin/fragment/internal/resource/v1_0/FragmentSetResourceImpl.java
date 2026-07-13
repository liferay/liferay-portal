/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.resource.v1_0;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.internal.odata.entity.v1_0.FragmentSetEntityModel;
import com.liferay.headless.admin.fragment.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.fragment.internal.util.EnabledUtil;
import com.liferay.headless.admin.fragment.resource.v1_0.FragmentSetResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.Filter;
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
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public FragmentSet getItem(Long id) throws Exception {
		EnabledUtil.checkDesignLibrariesEnabled(contextCompany);

		return _toFragmentSet(
			_fragmentCollectionService.getFragmentCollection(id));
	}

	@Override
	public FragmentSet getSiteFragmentSet(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		return _toFragmentSet(
			_fragmentCollectionService.
				getFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode,
					GroupUtil.getGroupId(
						true, true, contextCompany.getCompanyId(),
						siteExternalReferenceCode)));
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

				return _toFragmentSet(fragmentCollection);
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
					fragmentSet.getDateModified(), contextUser.getUserId())));
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
						contextUser.getUserId())));
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
					fragmentSet.getName(), fragmentSet.getDescription()));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private FragmentSet _toFragmentSet(FragmentCollection fragmentCollection)
		throws Exception {

		return _fragmentSetDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				false, null, _dtoConverterRegistry, contextHttpServletRequest,
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

}