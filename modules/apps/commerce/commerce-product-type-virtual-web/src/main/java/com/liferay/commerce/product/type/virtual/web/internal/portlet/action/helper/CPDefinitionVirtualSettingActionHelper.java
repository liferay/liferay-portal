/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.portlet.action.helper;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemFileEntryService;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingService;
import com.liferay.commerce.product.type.virtual.web.internal.constants.CPDefinitionVirtualSettingWebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = CPDefinitionVirtualSettingActionHelper.class)
public class CPDefinitionVirtualSettingActionHelper {

	public CommerceVirtualOrderItemFileEntry
			getCommerceVirtualOrderItemFileEntry(RenderRequest renderRequest)
		throws PortalException {

		long commerceVirtualOrderItemFileEntryId = ParamUtil.getLong(
			renderRequest, "commerceVirtualOrderItemFileEntryId");

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			_commerceVirtualOrderItemFileEntryService.
				fetchCommerceVirtualOrderItemFileEntry(
					commerceVirtualOrderItemFileEntryId);

		if (commerceVirtualOrderItemFileEntry != null) {
			renderRequest.setAttribute(
				CPDefinitionVirtualSettingWebKeys.
					CPD_VIRTUAL_SETTING_FILE_ENTRY,
				commerceVirtualOrderItemFileEntryId);
		}

		return commerceVirtualOrderItemFileEntry;
	}

	public CPDefinitionVirtualSetting getCPDefinitionVirtualSetting(
			RenderRequest renderRequest)
		throws PortalException {

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			(CPDefinitionVirtualSetting)renderRequest.getAttribute(
				CPDefinitionVirtualSettingWebKeys.
					CP_DEFINITION_VIRTUAL_SETTING);

		if (cpDefinitionVirtualSetting != null) {
			return cpDefinitionVirtualSetting;
		}

		long cpDefinitionId = ParamUtil.getLong(
			renderRequest, "cpDefinitionId");
		long cpInstanceId = ParamUtil.getLong(renderRequest, "cpInstanceId");

		if (cpInstanceId > 0) {
			cpDefinitionVirtualSetting =
				_cpDefinitionVirtualSettingService.
					fetchCPDefinitionVirtualSetting(
						CPInstance.class.getName(), cpInstanceId);
		}
		else if (cpDefinitionId > 0) {
			cpDefinitionVirtualSetting =
				_cpDefinitionVirtualSettingService.
					fetchCPDefinitionVirtualSetting(
						CPDefinition.class.getName(), cpDefinitionId);
		}

		if (cpDefinitionVirtualSetting != null) {
			renderRequest.setAttribute(
				CPDefinitionVirtualSettingWebKeys.CP_DEFINITION_VIRTUAL_SETTING,
				cpDefinitionVirtualSetting);
		}

		return cpDefinitionVirtualSetting;
	}

	public CPDVirtualSettingFileEntry getCPDVirtualSettingFileEntry(
			RenderRequest renderRequest)
		throws PortalException {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			(CPDVirtualSettingFileEntry)renderRequest.getAttribute(
				CPDefinitionVirtualSettingWebKeys.
					CPD_VIRTUAL_SETTING_FILE_ENTRY);

		if (cpdVirtualSettingFileEntry != null) {
			return cpdVirtualSettingFileEntry;
		}

		long cpdVirtualSettingFileEntryId = ParamUtil.getLong(
			renderRequest, "cpdVirtualSettingFileEntryId");

		cpdVirtualSettingFileEntry =
			_cpdefinitionVirtualSettingFileEntryService.
				fetchCPDVirtualSettingFileEntry(cpdVirtualSettingFileEntryId);

		if (cpdVirtualSettingFileEntry != null) {
			renderRequest.setAttribute(
				CPDefinitionVirtualSettingWebKeys.
					CPD_VIRTUAL_SETTING_FILE_ENTRY,
				cpdVirtualSettingFileEntry);
		}

		return cpdVirtualSettingFileEntry;
	}

	@Reference
	private CommerceVirtualOrderItemFileEntryService
		_commerceVirtualOrderItemFileEntryService;

	@Reference
	private CPDVirtualSettingFileEntryService
		_cpdefinitionVirtualSettingFileEntryService;

	@Reference
	private CPDefinitionVirtualSettingService
		_cpDefinitionVirtualSettingService;

}