/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.headless.admin.user.dto.v1_0.SharedAsset;
import com.liferay.headless.admin.user.internal.odata.entity.v1_0.SharedAssetEntityModel;
import com.liferay.headless.admin.user.resource.v1_0.SharedAssetResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.service.SharingEntryService;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.HashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/shared-asset.properties",
	scope = ServiceScope.PROTOTYPE, service = SharedAssetResource.class
)
public class SharedAssetResourceImpl extends BaseSharedAssetResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<SharedAsset> getMyUserAccountSharedAssetsSharedByMePage(
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			new HashMap<>(), _getSharedByMeBooleanQueryUnsafeConsumer(), filter,
			SharingEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames("sharingEntryId"),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toSharedAsset(
				_sharingEntryService.getSharingEntry(
					GetterUtil.getLong(document.get("sharingEntryId")))));
	}

	@Override
	public Page<SharedAsset> getMyUserAccountSharedAssetsSharedWithMePage(
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			new HashMap<>(), _getSharedWithMeBooleanQueryUnsafeConsumer(),
			filter, SharingEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames("sharingEntryId"),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toSharedAsset(
				_sharingEntryService.getSharingEntry(
					GetterUtil.getLong(document.get("sharingEntryId")))));
	}

	private UnsafeConsumer<BooleanQuery, Exception>
		_getSharedByMeBooleanQueryUnsafeConsumer() {

		return booleanQuery -> {
			BooleanFilter booleanFilter = booleanQuery.getPreBooleanFilter();

			booleanFilter.add(
				new TermFilter(
					"userId", String.valueOf(contextUser.getUserId())),
				BooleanClauseOccur.MUST);
		};
	}

	private UnsafeConsumer<BooleanQuery, Exception>
		_getSharedWithMeBooleanQueryUnsafeConsumer() {

		return booleanQuery -> {
			BooleanFilter booleanFilter = booleanQuery.getPreBooleanFilter();

			TermsFilter toUserGroupIdTermsFilter = new TermsFilter(
				"toUserGroupId");

			toUserGroupIdTermsFilter.addValues(
				transformToArray(
					_userGroupLocalService.getUserUserGroups(
						contextUser.getUserId()),
					userGroup -> String.valueOf(userGroup.getUserGroupId()),
					String.class));

			BooleanFilter userBooleanFilter = new BooleanFilter();

			userBooleanFilter.add(
				toUserGroupIdTermsFilter, BooleanClauseOccur.SHOULD);

			TermsFilter toUserIdTermsFilter = new TermsFilter("toUserId");

			toUserIdTermsFilter.addValue(
				String.valueOf(contextUser.getUserId()));

			userBooleanFilter.add(
				toUserIdTermsFilter, BooleanClauseOccur.SHOULD);

			booleanFilter.add(userBooleanFilter, BooleanClauseOccur.MUST);
		};
	}

	private SharedAsset _toSharedAsset(SharingEntry sharingEntry)
		throws Exception {

		return _sharedAssetDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), new HashMap<>(),
				_dtoConverterRegistry, sharingEntry.getSharingEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			sharingEntry);
	}

	private static final EntityModel _entityModel =
		new SharedAssetEntityModel();

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.user.internal.dto.v1_0.converter.SharedAssetDTOConverter)"
	)
	private DTOConverter<SharingEntry, SharedAsset> _sharedAssetDTOConverter;

	@Reference
	private SharingEntryService _sharingEntryService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}