/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.internal.resource.v1_0;

import com.liferay.commerce.exception.NoSuchAvailabilityEstimateException;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.service.CommerceAvailabilityEstimateService;
import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.AvailabilityEstimate;
import com.liferay.headless.commerce.admin.site.setting.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.commerce.admin.site.setting.internal.mapper.v1_0.util.DTOMapperUtil;
import com.liferay.headless.commerce.admin.site.setting.internal.odata.entity.v1_0.AvailabilityEstimateEntityModel;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.AvailabilityEstimateResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/availability-estimate.properties",
	scope = ServiceScope.PROTOTYPE, service = AvailabilityEstimateResource.class
)
public class AvailabilityEstimateResourceImpl
	extends BaseAvailabilityEstimateResourceImpl {

	@Override
	public Response deleteAvailabilityEstimate(Long id) throws Exception {
		_commerceAvailabilityEstimateService.deleteCommerceAvailabilityEstimate(
			id);

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public void deleteAvailabilityEstimateByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			_commerceAvailabilityEstimateService.
				fetchCommerceAvailabilityEstimateByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceAvailabilityEstimate == null) {
			throw new NoSuchAvailabilityEstimateException(
				"Unable to find availability estimate with external " +
					"reference code " + externalReferenceCode);
		}

		_commerceAvailabilityEstimateService.deleteCommerceAvailabilityEstimate(
			commerceAvailabilityEstimate.getCommerceAvailabilityEstimateId());
	}

	@Override
	public Page<AvailabilityEstimate>
			getCommerceAdminSiteSettingGroupAvailabilityEstimatePage(
				Long groupId, Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_commerceAvailabilityEstimateService.
					getCommerceAvailabilityEstimates(
						contextCompany.getCompanyId(),
						pagination.getStartPosition(),
						pagination.getEndPosition(), null),
				this::_toAvailabilityEstimate),
			pagination,
			_commerceAvailabilityEstimateService.
				getCommerceAvailabilityEstimatesCount(
					contextCompany.getCompanyId()));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public AvailabilityEstimate
			patchAvailabilityEstimateByExternalReferenceCode(
				String externalReferenceCode,
				AvailabilityEstimate availabilityEstimate)
		throws Exception {

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			_commerceAvailabilityEstimateService.
				fetchCommerceAvailabilityEstimateByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceAvailabilityEstimate == null) {
			throw new NoSuchAvailabilityEstimateException(
				"Unable to find availability estimate with external " +
					"reference code " + externalReferenceCode);
		}

		return _toAvailabilityEstimate(
			_updateCommerceAvailabilityEstimate(
				availabilityEstimate, commerceAvailabilityEstimate));
	}

	@Override
	public AvailabilityEstimate
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				Long groupId, AvailabilityEstimate availabilityEstimate)
		throws Exception {

		return _toAvailabilityEstimate(
			_commerceAvailabilityEstimateService.
				addCommerceAvailabilityEstimate(
					availabilityEstimate.getExternalReferenceCode(),
					LanguageUtils.getLocalizedMap(
						availabilityEstimate.getTitle()),
					GetterUtil.getDouble(availabilityEstimate.getPriority()),
					_serviceContextHelper.getServiceContext(contextUser)));
	}

	@Override
	protected AvailabilityEstimate doGetAvailabilityEstimate(Long id)
		throws Exception {

		return _toAvailabilityEstimate(
			_commerceAvailabilityEstimateService.
				getCommerceAvailabilityEstimate(id));
	}

	@Override
	protected AvailabilityEstimate
			doGetAvailabilityEstimateByExternalReferenceCode(
				String externalReferenceCode)
		throws Exception {

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			_commerceAvailabilityEstimateService.
				fetchCommerceAvailabilityEstimateByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceAvailabilityEstimate == null) {
			throw new NoSuchAvailabilityEstimateException(
				"Unable to find availability estimate with external " +
					"reference code " + externalReferenceCode);
		}

		return _toAvailabilityEstimate(commerceAvailabilityEstimate);
	}

	@Override
	protected Page<AvailabilityEstimate> doGetAvailabilityEstimatesPage(
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceAvailabilityEstimate.class.getName(), null, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toAvailabilityEstimate(
				_commerceAvailabilityEstimateService.
					getCommerceAvailabilityEstimate(
						GetterUtil.getLong(
							document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	protected AvailabilityEstimate doPutAvailabilityEstimate(
			Long id, AvailabilityEstimate availabilityEstimate)
		throws Exception {

		return _toAvailabilityEstimate(
			_updateCommerceAvailabilityEstimate(
				availabilityEstimate,
				_commerceAvailabilityEstimateService.
					getCommerceAvailabilityEstimate(id)));
	}

	@Override
	protected AvailabilityEstimate
			doPutAvailabilityEstimateByExternalReferenceCode(
				String externalReferenceCode,
				AvailabilityEstimate availabilityEstimate)
		throws Exception {

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			_commerceAvailabilityEstimateService.
				fetchCommerceAvailabilityEstimateByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceAvailabilityEstimate == null) {
			return _toAvailabilityEstimate(
				_commerceAvailabilityEstimateService.
					addCommerceAvailabilityEstimate(
						externalReferenceCode,
						LanguageUtils.getLocalizedMap(
							availabilityEstimate.getTitle()),
						GetterUtil.getDouble(
							availabilityEstimate.getPriority()),
						_serviceContextHelper.getServiceContext(contextUser)));
		}

		return _toAvailabilityEstimate(
			_updateCommerceAvailabilityEstimate(
				availabilityEstimate, commerceAvailabilityEstimate));
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) {
		return 0L;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return CommerceAvailabilityEstimate.class.getName();
	}

	private AvailabilityEstimate _toAvailabilityEstimate(
		CommerceAvailabilityEstimate commerceAvailabilityEstimate) {

		AvailabilityEstimate availabilityEstimate = DTOMapperUtil.modelToDTO(
			commerceAvailabilityEstimate);

		availabilityEstimate.setCreator(
			() -> NestedFieldsSupplier.supply(
				"creator",
				fieldName -> CreatorUtil.toCreator(
					_portal,
					_userLocalService.fetchUser(
						commerceAvailabilityEstimate.getUserId()))));

		return availabilityEstimate;
	}

	private CommerceAvailabilityEstimate _updateCommerceAvailabilityEstimate(
			AvailabilityEstimate availabilityEstimate,
			CommerceAvailabilityEstimate commerceAvailabilityEstimate)
		throws Exception {

		Map<Locale, String> titleMap =
			commerceAvailabilityEstimate.getTitleMap();

		if (availabilityEstimate.getTitle() != null) {
			titleMap = LanguageUtils.getLocalizedMap(
				availabilityEstimate.getTitle());
		}

		return _commerceAvailabilityEstimateService.
			updateCommerceAvailabilityEstimate(
				GetterUtil.get(
					availabilityEstimate.getExternalReferenceCode(),
					commerceAvailabilityEstimate.getExternalReferenceCode()),
				commerceAvailabilityEstimate.
					getCommerceAvailabilityEstimateId(),
				titleMap,
				GetterUtil.get(
					availabilityEstimate.getPriority(),
					commerceAvailabilityEstimate.getPriority()),
				_serviceContextHelper.getServiceContext(contextUser));
	}

	private static final EntityModel _entityModel =
		new AvailabilityEstimateEntityModel();

	@Reference
	private CommerceAvailabilityEstimateService
		_commerceAvailabilityEstimateService;

	@Reference
	private Portal _portal;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference
	private UserLocalService _userLocalService;

}