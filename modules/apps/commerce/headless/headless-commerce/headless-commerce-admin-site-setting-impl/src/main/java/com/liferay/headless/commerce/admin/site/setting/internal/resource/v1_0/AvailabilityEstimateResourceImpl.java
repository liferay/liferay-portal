/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.internal.resource.v1_0;

import com.liferay.commerce.exception.NoSuchAvailabilityEstimateException;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.service.CommerceAvailabilityEstimateService;
import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.AvailabilityEstimate;
import com.liferay.headless.commerce.admin.site.setting.internal.mapper.v1_0.util.DTOMapperUtil;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.AvailabilityEstimateResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;

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
	public AvailabilityEstimate getAvailabilityEstimate(Long id)
		throws Exception {

		return DTOMapperUtil.modelToDTO(
			_commerceAvailabilityEstimateService.
				getCommerceAvailabilityEstimate(id));
	}

	@Override
	public AvailabilityEstimate getAvailabilityEstimateByExternalReferenceCode(
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

		return DTOMapperUtil.modelToDTO(commerceAvailabilityEstimate);
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
				DTOMapperUtil::modelToDTO),
			pagination,
			_commerceAvailabilityEstimateService.
				getCommerceAvailabilityEstimatesCount(
					contextCompany.getCompanyId()));
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

		return DTOMapperUtil.modelToDTO(
			_updateCommerceAvailabilityEstimate(
				availabilityEstimate, commerceAvailabilityEstimate));
	}

	@Override
	public AvailabilityEstimate
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				Long groupId, AvailabilityEstimate availabilityEstimate)
		throws Exception {

		return DTOMapperUtil.modelToDTO(
			_commerceAvailabilityEstimateService.
				addCommerceAvailabilityEstimate(
					availabilityEstimate.getExternalReferenceCode(),
					LanguageUtils.getLocalizedMap(
						availabilityEstimate.getTitle()),
					GetterUtil.getDouble(availabilityEstimate.getPriority()),
					_serviceContextHelper.getServiceContext(contextUser)));
	}

	@Override
	public AvailabilityEstimate putAvailabilityEstimate(
			Long id, AvailabilityEstimate availabilityEstimate)
		throws Exception {

		return DTOMapperUtil.modelToDTO(
			_updateCommerceAvailabilityEstimate(
				availabilityEstimate,
				_commerceAvailabilityEstimateService.
					getCommerceAvailabilityEstimate(id)));
	}

	@Override
	public AvailabilityEstimate putAvailabilityEstimateByExternalReferenceCode(
			String externalReferenceCode,
			AvailabilityEstimate availabilityEstimate)
		throws Exception {

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			_commerceAvailabilityEstimateService.
				fetchCommerceAvailabilityEstimateByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceAvailabilityEstimate == null) {
			return DTOMapperUtil.modelToDTO(
				_commerceAvailabilityEstimateService.
					addCommerceAvailabilityEstimate(
						externalReferenceCode,
						LanguageUtils.getLocalizedMap(
							availabilityEstimate.getTitle()),
						GetterUtil.getDouble(
							availabilityEstimate.getPriority()),
						_serviceContextHelper.getServiceContext(contextUser)));
		}

		return DTOMapperUtil.modelToDTO(
			_updateCommerceAvailabilityEstimate(
				availabilityEstimate, commerceAvailabilityEstimate));
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

	@Reference
	private CommerceAvailabilityEstimateService
		_commerceAvailabilityEstimateService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}