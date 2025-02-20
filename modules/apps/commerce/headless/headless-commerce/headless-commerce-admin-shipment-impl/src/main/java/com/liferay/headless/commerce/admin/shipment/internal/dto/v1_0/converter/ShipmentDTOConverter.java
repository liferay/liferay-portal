/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.shipment.internal.dto.v1_0.converter;

import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.headless.commerce.admin.shipment.dto.v1_0.Shipment;
import com.liferay.headless.commerce.admin.shipment.dto.v1_0.Status;
import com.liferay.headless.commerce.admin.shipment.internal.dto.v1_0.util.CustomFieldsUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.model.CommerceShipment",
	service = DTOConverter.class
)
public class ShipmentDTOConverter
	implements DTOConverter<CommerceShipment, Shipment> {

	@Override
	public String getContentType() {
		return Shipment.class.getSimpleName();
	}

	@Override
	public Shipment toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceShipment commerceShipment =
			_commerceShipmentLocalService.getCommerceShipment(
				(Long)dtoConverterContext.getId());

		return new Shipment() {
			{
				setAccountId(commerceShipment::getCommerceAccountId);
				setActions(dtoConverterContext::getActions);
				setCarrier(commerceShipment::getCarrier);
				setCreateDate(commerceShipment::getCreateDate);
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CommerceShipment.class.getName(),
						commerceShipment.getCommerceShipmentId(),
						commerceShipment.getCompanyId(),
						dtoConverterContext.getLocale()));
				setExpectedDate(commerceShipment::getExpectedDate);
				setExternalReferenceCode(
					commerceShipment::getExternalReferenceCode);
				setId(commerceShipment::getCommerceShipmentId);
				setModifiedDate(commerceShipment::getModifiedDate);
				setShippingAddressId(commerceShipment::getCommerceAddressId);
				setShippingDate(commerceShipment::getShippingDate);
				setShippingMethodId(
					commerceShipment::getCommerceShippingMethodId);
				setShippingOptionName(commerceShipment::getShippingOptionName);
				setStatus(
					() -> new Status() {
						{
							setCode(commerceShipment::getStatus);
							setLabel(
								() ->
									CommerceShipmentConstants.
										getShipmentStatusLabel(
											commerceShipment.getStatus()));
							setLabel_i18n(
								() -> _language.get(
									LanguageResources.getResourceBundle(
										dtoConverterContext.getLocale()),
									CommerceShipmentConstants.
										getShipmentStatusLabel(
											commerceShipment.getStatus())));
						}
					});
				setTrackingNumber(commerceShipment::getTrackingNumber);
				setTrackingURL(commerceShipment::getTrackingURL);
				setUserName(commerceShipment::getUserName);
			}
		};
	}

	@Reference
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	@Reference
	private Language _language;

}