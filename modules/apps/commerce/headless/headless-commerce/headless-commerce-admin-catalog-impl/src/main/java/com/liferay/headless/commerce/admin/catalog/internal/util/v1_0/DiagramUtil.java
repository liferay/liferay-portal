/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelService;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.commerce.shop.by.diagram.constants.CSDiagramSettingsConstants;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramSettingService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.AttachmentBase64;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Diagram;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.upload.UniqueFileNameProvider;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class DiagramUtil {

	public static CSDiagramSetting addCSDiagramSetting(
			long companyId,
			CPAttachmentFileEntryService cpAttachmentFileEntryService,
			long cpDefinitionId,
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
			CPOptionService cpOptionService,
			CSDiagramSettingService csDiagramSettingService, Diagram diagram,
			long groupId, Locale locale,
			ServiceContextHelper serviceContextHelper,
			UniqueFileNameProvider uniqueFileNameProvider)
		throws Exception {

		diagram = _addOrUpdateDiagramImage(
			ClassNameLocalServiceUtil.getClassNameId(
				CPDefinition.class.getName()),
			cpDefinitionId, companyId, cpAttachmentFileEntryService,
			cpDefinitionOptionRelService, cpDefinitionOptionValueRelService,
			cpOptionService, diagram, groupId, locale, serviceContextHelper,
			uniqueFileNameProvider);

		return csDiagramSettingService.addCSDiagramSetting(
			cpDefinitionId, GetterUtil.getLong(diagram.getImageId()),
			GetterUtil.getString(diagram.getColor()),
			GetterUtil.getDouble(diagram.getRadius()),
			GetterUtil.getString(diagram.getType(), "diagram.type.default"));
	}

	public static CSDiagramSetting addOrUpdateCSDiagramSetting(
			long companyId,
			CPAttachmentFileEntryService cpAttachmentFileEntryService,
			long cpDefinitionId,
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
			CPOptionService cpOptionService,
			CSDiagramSettingService csDiagramSettingService, Diagram diagram,
			long groupId, Locale locale,
			ServiceContextHelper serviceContextHelper,
			UniqueFileNameProvider uniqueFileNameProvider)
		throws Exception {

		CSDiagramSetting csDiagramSetting =
			csDiagramSettingService.fetchCSDiagramSettingByCPDefinitionId(
				cpDefinitionId);

		if (csDiagramSetting == null) {
			return addCSDiagramSetting(
				companyId, cpAttachmentFileEntryService, cpDefinitionId,
				cpDefinitionOptionRelService, cpDefinitionOptionValueRelService,
				cpOptionService, csDiagramSettingService, diagram, groupId,
				locale, serviceContextHelper, uniqueFileNameProvider);
		}

		return updateCSDiagramSetting(
			companyId, cpAttachmentFileEntryService,
			cpDefinitionOptionRelService, cpDefinitionOptionValueRelService,
			cpOptionService, csDiagramSetting, csDiagramSettingService, diagram,
			groupId, locale, serviceContextHelper, uniqueFileNameProvider);
	}

	public static CSDiagramSetting updateCSDiagramSetting(
			long companyId,
			CPAttachmentFileEntryService cpAttachmentFileEntryService,
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
			CPOptionService cpOptionService, CSDiagramSetting csDiagramSetting,
			CSDiagramSettingService csDiagramSettingService, Diagram diagram,
			long groupId, Locale locale,
			ServiceContextHelper serviceContextHelper,
			UniqueFileNameProvider uniqueFileNameProvider)
		throws Exception {

		diagram = _addOrUpdateDiagramImage(
			ClassNameLocalServiceUtil.getClassNameId(
				CPDefinition.class.getName()),
			csDiagramSetting.getCPDefinitionId(), companyId,
			cpAttachmentFileEntryService, cpDefinitionOptionRelService,
			cpDefinitionOptionValueRelService, cpOptionService, diagram,
			groupId, locale, serviceContextHelper, uniqueFileNameProvider);

		return csDiagramSettingService.updateCSDiagramSetting(
			csDiagramSetting.getCSDiagramSettingId(),
			GetterUtil.get(
				diagram.getImageId(),
				csDiagramSetting.getCPAttachmentFileEntryId()),
			GetterUtil.getString(
				diagram.getColor(), csDiagramSetting.getColor()),
			GetterUtil.getDouble(
				diagram.getRadius(), csDiagramSetting.getRadius()),
			GetterUtil.getString(
				diagram.getType(), csDiagramSetting.getType()));
	}

	private static Diagram _addOrUpdateDiagramImage(
			long classNameId, long classPK, long companyId,
			CPAttachmentFileEntryService cpAttachmentFileEntryService,
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
			CPOptionService cpOptionService, Diagram diagram, long groupId,
			Locale locale, ServiceContextHelper serviceContextHelper,
			UniqueFileNameProvider uniqueFileNameProvider)
		throws Exception {

		if (diagram.getAttachmentBase64() == null) {
			String imageExternalReferenceCode =
				diagram.getImageExternalReferenceCode();

			if (Validator.isNull(imageExternalReferenceCode)) {
				CPAttachmentFileEntry cpAttachmentFileEntry =
					cpAttachmentFileEntryService.fetchCPAttachmentFileEntry(
						GetterUtil.getLong(diagram.getImageId()));

				if ((cpAttachmentFileEntry != null) &&
					(cpAttachmentFileEntry.getCompanyId() != companyId)) {

					diagram.setImageId(() -> 0L);
				}

				return diagram;
			}

			CPAttachmentFileEntry cpAttachmentFileEntry =
				cpAttachmentFileEntryService.
					fetchCPAttachmentFileEntryByExternalReferenceCode(
						imageExternalReferenceCode, companyId);

			if (cpAttachmentFileEntry == null) {
				diagram.setImageId(() -> 0L);
			}
			else {
				diagram.setImageId(
					cpAttachmentFileEntry::getCPAttachmentFileEntryId);
			}

			return diagram;
		}

		diagram.setImageId(
			() -> {
				CPAttachmentFileEntry cpAttachmentFileEntry =
					AttachmentUtil.addOrUpdateCPAttachmentFileEntry(
						cpAttachmentFileEntryService,
						cpDefinitionOptionRelService,
						cpDefinitionOptionValueRelService, cpOptionService,
						uniqueFileNameProvider, diagram.getAttachmentBase64(),
						classNameId, classPK,
						CSDiagramSettingsConstants.TYPE_DIAGRAM,
						_getDiagramServiceContext(
							companyId, diagram, groupId, locale,
							serviceContextHelper));

				return cpAttachmentFileEntry.getCPAttachmentFileEntryId();
			});

		return diagram;
	}

	private static ServiceContext _getDiagramServiceContext(
			long companyId, Diagram diagram, long groupId, Locale locale,
			ServiceContextHelper serviceContextHelper)
		throws Exception {

		ServiceContext serviceContext = serviceContextHelper.getServiceContext(
			groupId);

		AttachmentBase64 attachmentBase64 = diagram.getAttachmentBase64();

		if (attachmentBase64 == null) {
			return serviceContext;
		}

		Map<String, Serializable> expandoBridgeAttributes =
			CustomFieldsUtil.toMap(
				CPAttachmentFileEntry.class.getName(), companyId,
				attachmentBase64.getCustomFields(), locale);

		if (expandoBridgeAttributes == null) {
			expandoBridgeAttributes = new HashMap<>();
		}

		serviceContext.setExpandoBridgeAttributes(expandoBridgeAttributes);

		return serviceContext;
	}

}