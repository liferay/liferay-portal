/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramSettingService;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Diagram;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting"
	},
	service = DTOConverter.class
)
public class DiagramDTOConverter
	implements DTOConverter<CSDiagramSetting, Diagram> {

	@Override
	public String getContentType() {
		return Diagram.class.getSimpleName();
	}

	@Override
	public Diagram toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CSDiagramSetting csDiagramSetting =
			_csDiagramSettingService.getCSDiagramSetting(
				(Long)dtoConverterContext.getId());

		CPDefinition cpDefinition = csDiagramSetting.getCPDefinition();

		CProduct cProduct = cpDefinition.getCProduct();

		return new Diagram() {
			{
				setColor(csDiagramSetting::getColor);
				setId(csDiagramSetting::getCSDiagramSettingId);
				setImageURL(
					() -> {
						CPAttachmentFileEntry cpAttachmentFileEntry =
							csDiagramSetting.getCPAttachmentFileEntry();

						FileEntry fileEntry =
							cpAttachmentFileEntry.fetchFileEntry();

						if (fileEntry == null) {
							return null;
						}

						return DLURLHelperUtil.getDownloadURL(
							fileEntry, fileEntry.getFileVersion(), null, null);
					});
				setProductExternalReferenceCode(
					cProduct::getExternalReferenceCode);
				setProductId(cProduct::getCProductId);
				setRadius(csDiagramSetting::getRadius);
				setType(csDiagramSetting::getType);
			}
		};
	}

	@Reference
	private CSDiagramSettingService _csDiagramSettingService;

}