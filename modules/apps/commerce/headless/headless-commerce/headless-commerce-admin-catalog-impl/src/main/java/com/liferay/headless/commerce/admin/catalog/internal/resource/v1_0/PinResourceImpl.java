/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryService;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramPinService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Pin;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.MappedProductUtil;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.PinUtil;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.PinResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/pin.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = PinResource.class
)
@CTAware
public class PinResourceImpl extends BasePinResourceImpl {

	@Override
	public void deletePin(Long pinId) throws Exception {
		CSDiagramPin csDiagramPin = _csDiagramPinService.getCSDiagramPin(pinId);

		CSDiagramEntry csDiagramEntry =
			_csDiagramEntryService.fetchCSDiagramEntry(
				csDiagramPin.getCPDefinitionId(), csDiagramPin.getSequence());

		if ((csDiagramEntry != null) &&
			!ListUtil.exists(
				_csDiagramPinService.getCSDiagramPins(
					csDiagramPin.getCPDefinitionId(), -1, -1),
				curCSDiagramPin ->
					(csDiagramPin.getCSDiagramPinId() !=
						curCSDiagramPin.getCSDiagramPinId()) &&
					Objects.equals(
						csDiagramPin.getSequence(),
						curCSDiagramPin.getSequence()))) {

			_csDiagramEntryService.deleteCSDiagramEntry(csDiagramEntry);
		}

		_csDiagramPinService.deleteCSDiagramPin(csDiagramPin);
	}

	@Override
	public Page<Pin> getProductByExternalReferenceCodePinsPage(
			String externalReferenceCode, String search, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId(),
					false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code: " +
					externalReferenceCode);
		}

		return Page.of(
			_toPins(
				_csDiagramPinService.getCSDiagramPins(
					cpDefinition.getCPDefinitionId(),
					pagination.getStartPosition(),
					pagination.getEndPosition())),
			pagination,
			_csDiagramPinService.getCSDiagramPinsCount(
				cpDefinition.getCPDefinitionId()));
	}

	@NestedField(parentClass = Product.class, value = "pins")
	@Override
	public Page<Pin> getProductIdPinsPage(
			Long productId, String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(
				productId, false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		return Page.of(
			_toPins(
				_csDiagramPinService.getCSDiagramPins(
					cpDefinition.getCPDefinitionId(),
					pagination.getStartPosition(),
					pagination.getEndPosition())),
			pagination,
			_csDiagramPinService.getCSDiagramPinsCount(
				cpDefinition.getCPDefinitionId()));
	}

	@Override
	public Pin patchPin(Long pinId, Pin pin) throws Exception {
		CSDiagramPin csDiagramPin = _csDiagramPinService.getCSDiagramPin(pinId);

		PinUtil.updateCSDiagramPin(csDiagramPin, _csDiagramPinService, pin);

		CPDefinition cpDefinition = csDiagramPin.getCPDefinition();

		_addOrUpdateMappedProduct(
			csDiagramPin.getCPDefinitionId(), cpDefinition.getGroupId(), pin);

		return _toPin(pinId);
	}

	@Override
	public Pin postProductByExternalReferenceCodePin(
			String externalReferenceCode, Pin pin)
		throws Exception {

		CPDefinition cpDefinition =
			ProductUtil.fetchCPDefinitionByCProductExternalReferenceCode(
				_cpDefinitionService, externalReferenceCode,
				contextCompany.getCompanyId());

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return _addPin(
			cpDefinition.getCPDefinitionId(), cpDefinition.getGroupId(), pin);
	}

	@Override
	public Pin postProductIdPin(Long productId, Pin pin) throws Exception {
		CPDefinition cpDefinition = ProductUtil.fetchCPDefinitionByCProductId(
			_cpDefinitionService, productId);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		return _addPin(
			cpDefinition.getCPDefinitionId(), cpDefinition.getGroupId(), pin);
	}

	private void _addOrUpdateMappedProduct(
			long cpDefinitionId, long groupId, Pin pin)
		throws Exception {

		MappedProduct mappedProduct = pin.getMappedProduct();

		if (mappedProduct != null) {
			long skuId = GetterUtil.getLong(mappedProduct.getSkuId());

			CPInstance cpInstance =
				_cpInstanceService.fetchCPInstanceByExternalReferenceCode(
					mappedProduct.getSkuExternalReferenceCode(),
					contextCompany.getCompanyId());

			if (cpInstance != null) {
				skuId = cpInstance.getCPInstanceId();
			}

			long productId = GetterUtil.getLong(mappedProduct.getProductId());

			CPDefinition cpDefinition =
				ProductUtil.fetchCPDefinitionByCProductExternalReferenceCode(
					_cpDefinitionService,
					mappedProduct.getProductExternalReferenceCode(),
					contextCompany.getCompanyId());

			if (cpDefinition != null) {
				productId = cpDefinition.getCProductId();
			}

			ServiceContext serviceContext =
				_serviceContextHelper.getServiceContext(groupId);

			serviceContext.setExpandoBridgeAttributes(
				MappedProductUtil.getExpandoBridgeAttributes(
					contextCompany.getCompanyId(),
					contextAcceptLanguage.getPreferredLocale(), mappedProduct));

			CSDiagramEntry csDiagramEntry =
				_csDiagramEntryService.fetchCSDiagramEntry(
					cpDefinitionId, GetterUtil.getString(pin.getSequence()));

			if (csDiagramEntry == null) {
				_csDiagramEntryService.addCSDiagramEntry(
					cpDefinitionId, skuId, productId,
					MappedProductUtil.isDiagram(csDiagramEntry, mappedProduct),
					GetterUtil.getInteger(mappedProduct.getQuantity()),
					GetterUtil.getString(mappedProduct.getSequence()),
					GetterUtil.getString(mappedProduct.getSku()),
					serviceContext);
			}
			else {
				_csDiagramEntryService.updateCSDiagramEntry(
					csDiagramEntry.getCSDiagramEntryId(), skuId, productId,
					MappedProductUtil.isDiagram(csDiagramEntry, mappedProduct),
					GetterUtil.getInteger(mappedProduct.getQuantity()),
					GetterUtil.getString(mappedProduct.getSequence()),
					GetterUtil.getString(mappedProduct.getSku()),
					serviceContext);
			}
		}
	}

	private Pin _addPin(long cpDefinitionId, long groupId, Pin pin)
		throws Exception {

		CSDiagramPin csDiagramPin = PinUtil.addCSDiagramPin(
			cpDefinitionId, _csDiagramPinService, pin);

		_addOrUpdateMappedProduct(cpDefinitionId, groupId, pin);

		return _toPin(csDiagramPin.getCSDiagramPinId());
	}

	private Pin _toPin(long csDiagramPinId) throws Exception {
		return _pinDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				csDiagramPinId, contextAcceptLanguage.getPreferredLocale()));
	}

	private List<Pin> _toPins(List<CSDiagramPin> csDiagramPins) {
		return transform(
			csDiagramPins,
			csDiagramPin -> _toPin(csDiagramPin.getCSDiagramPinId()));
	}

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference
	private CSDiagramEntryService _csDiagramEntryService;

	@Reference
	private CSDiagramPinService _csDiagramPinService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.PinDTOConverter)"
	)
	private DTOConverter<CSDiagramEntry, Pin> _pinDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}