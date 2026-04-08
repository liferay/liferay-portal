/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramSettingService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Diagram;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.DiagramUtil;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.DiagramResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.upload.UniqueFileNameProvider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/diagram.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = DiagramResource.class
)
@CTAware
public class DiagramResourceImpl extends BaseDiagramResourceImpl {

	@Override
	public Diagram getProductByExternalReferenceCodeDiagram(
			String externalReferenceCode)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId(),
					false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		CSDiagramSetting csDiagramSetting =
			_csDiagramSettingService.getCSDiagramSettingByCPDefinitionId(
				cpDefinition.getCPDefinitionId());

		return _toDiagram(csDiagramSetting.getCSDiagramSettingId());
	}

	@NestedField(parentClass = Product.class, value = "diagram")
	@Override
	public Diagram getProductIdDiagram(Long productId) throws Exception {
		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(
				productId, false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		CSDiagramSetting csDiagramSetting =
			_csDiagramSettingService.getCSDiagramSettingByCPDefinitionId(
				cpDefinition.getCPDefinitionId());

		return _toDiagram(csDiagramSetting.getCSDiagramSettingId());
	}

	@Override
	public Diagram patchDiagram(Long diagramId, Diagram diagram)
		throws Exception {

		CSDiagramSetting csDiagramSetting =
			_csDiagramSettingService.getCSDiagramSetting(diagramId);

		CPDefinition cpDefinition = ProductUtil.fetchCPDefinitionByCProductId(
			_cpDefinitionService,
			csDiagramSetting.getCPDefinition(
			).getCProductId());

		DiagramUtil.updateCSDiagramSetting(
			contextCompany.getCompanyId(), _cpAttachmentFileEntryService,
			_cpDefinitionOptionRelService, _cpDefinitionOptionValueRelService,
			_cpOptionService, csDiagramSetting, _csDiagramSettingService,
			diagram, cpDefinition.getGroupId(),
			contextAcceptLanguage.getPreferredLocale(), _serviceContextHelper,
			_uniqueFileNameProvider);

		return _toDiagram(diagramId);
	}

	@Override
	public Diagram postProductByExternalReferenceCodeDiagram(
			String externalReferenceCode, Diagram diagram)
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

		CSDiagramSetting csDiagramSetting = DiagramUtil.addCSDiagramSetting(
			contextCompany.getCompanyId(), _cpAttachmentFileEntryService,
			cpDefinition.getCPDefinitionId(), _cpDefinitionOptionRelService,
			_cpDefinitionOptionValueRelService, _cpOptionService,
			_csDiagramSettingService, diagram, cpDefinition.getGroupId(),
			contextAcceptLanguage.getPreferredLocale(), _serviceContextHelper,
			_uniqueFileNameProvider);

		return _toDiagram(csDiagramSetting.getCSDiagramSettingId());
	}

	@Override
	public Diagram postProductIdDiagram(Long productId, Diagram diagram)
		throws Exception {

		CPDefinition cpDefinition = ProductUtil.fetchCPDefinitionByCProductId(
			_cpDefinitionService, productId);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		CSDiagramSetting csDiagramSetting = DiagramUtil.addCSDiagramSetting(
			contextCompany.getCompanyId(), _cpAttachmentFileEntryService,
			cpDefinition.getCPDefinitionId(), _cpDefinitionOptionRelService,
			_cpDefinitionOptionValueRelService, _cpOptionService,
			_csDiagramSettingService, diagram, cpDefinition.getGroupId(),
			contextAcceptLanguage.getPreferredLocale(), _serviceContextHelper,
			_uniqueFileNameProvider);

		return _toDiagram(csDiagramSetting.getCSDiagramSettingId());
	}

	private Diagram _toDiagram(long csDiagramSettingId) throws Exception {
		return _diagramDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				csDiagramSettingId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Reference
	private CPAttachmentFileEntryService _cpAttachmentFileEntryService;

	@Reference
	private CPDefinitionOptionRelService _cpDefinitionOptionRelService;

	@Reference
	private CPDefinitionOptionValueRelService
		_cpDefinitionOptionValueRelService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CPOptionService _cpOptionService;

	@Reference
	private CSDiagramSettingService _csDiagramSettingService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.DiagramDTOConverter)"
	)
	private DTOConverter<CSDiagramSetting, Diagram> _diagramDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

}