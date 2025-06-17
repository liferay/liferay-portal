/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.item.provider;

import com.liferay.commerce.product.content.util.CPMedia;
import com.liferay.commerce.product.content.web.internal.info.CPAttachmentFileEntryInfoItemFields;
import com.liferay.commerce.product.content.web.internal.util.CPMediaImpl;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.expando.info.item.provider.ExpandoInfoItemFieldSetProvider;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = InfoItemFieldValuesProvider.class)
public class CPAttachmentFileEntryInfoItemFieldValuesProvider
	implements InfoItemFieldValuesProvider<CPAttachmentFileEntry> {

	@Override
	public InfoItemFieldValues getInfoItemFieldValues(
		CPAttachmentFileEntry cpAttachmentFileEntry) {

		return InfoItemFieldValues.builder(
		).infoFieldValues(
			_getCPAttachmentFileEntryInfoFieldValues(cpAttachmentFileEntry)
		).infoFieldValues(
			_expandoInfoItemFieldSetProvider.getInfoFieldValues(
				CPAttachmentFileEntry.class.getName(), cpAttachmentFileEntry)
		).infoFieldValues(
			_templateInfoItemFieldSetProvider.getInfoFieldValues(
				CPAttachmentFileEntry.class.getName(), cpAttachmentFileEntry)
		).infoFieldValues(
			_infoItemFieldReaderFieldSetProvider.getInfoFieldValues(
				CPAttachmentFileEntry.class.getName(), cpAttachmentFileEntry)
		).infoItemReference(
			new InfoItemReference(
				CPAttachmentFileEntry.class.getName(),
				cpAttachmentFileEntry.getClassPK())
		).build();
	}

	private List<InfoFieldValue<Object>>
		_getCPAttachmentFileEntryInfoFieldValues(
			CPAttachmentFileEntry cpAttachmentFileEntry) {

		List<InfoFieldValue<Object>> cpAttachmentFileEntryInfoFieldValues =
			new ArrayList<>();

		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.approvedInfoField,
				cpAttachmentFileEntry.isApproved()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.CDNEnabledInfoField,
				cpAttachmentFileEntry.isCDNEnabled()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.companyIdInfoField,
				cpAttachmentFileEntry.getCompanyId()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.
					cpAttachmentFileEntryIdInfoField,
				cpAttachmentFileEntry.getCPAttachmentFileEntryId()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.cpDefinitionIdInfoField,
				cpAttachmentFileEntry.getClassPK()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.createDateInfoField,
				cpAttachmentFileEntry.getCreateDate()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.defaultLanguageIdInfoField,
				cpAttachmentFileEntry.getDefaultLanguageId()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.displayDateInfoField,
				cpAttachmentFileEntry.getDisplayDate()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.draftInfoField,
				cpAttachmentFileEntry.isDraft()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.expirationDateInfoField,
				cpAttachmentFileEntry.getExpirationDate()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.expiredInfoField,
				cpAttachmentFileEntry.isExpired()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.fileEntryIdInfoField,
				cpAttachmentFileEntry.getFileEntryId()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.groupIdInfoField,
				cpAttachmentFileEntry.getGroupId()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.inactiveInfoField,
				cpAttachmentFileEntry.isInactive()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.incompleteInfoField,
				cpAttachmentFileEntry.isIncomplete()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.lastPublishDateInfoField,
				cpAttachmentFileEntry.getLastPublishDate()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.modifiedDateInfoField,
				cpAttachmentFileEntry.getModifiedDate()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.optionsInfoField,
				cpAttachmentFileEntry.getJson()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.priorityInfoField,
				cpAttachmentFileEntry.getPriority()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.scheduledInfoField,
				cpAttachmentFileEntry.isScheduled()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.stagedModelTypeInfoField,
				cpAttachmentFileEntry.getStagedModelType()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.statusInfoField,
				cpAttachmentFileEntry.getStatus()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.statusByUserIdInfoField,
				cpAttachmentFileEntry.getStatusByUserId()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.statusByUserNameInfoField,
				cpAttachmentFileEntry.getStatusByUserName()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.statusByUserUuidInfoField,
				cpAttachmentFileEntry.getStatusByUserUuid()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.statusDateInfoField,
				cpAttachmentFileEntry.getStatusDate()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.titleInfoField,
				InfoLocalizedValue.<String>builder(
				).defaultLocale(
					LocaleUtil.fromLanguageId(
						cpAttachmentFileEntry.getDefaultLanguageId())
				).values(
					cpAttachmentFileEntry.getTitleMap()
				).build()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.userIdInfoField,
				cpAttachmentFileEntry.getUserId()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.userNameInfoField,
				cpAttachmentFileEntry.getUserName()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.userUuidInfoField,
				cpAttachmentFileEntry.getUserUuid()));
		cpAttachmentFileEntryInfoFieldValues.add(
			new InfoFieldValue<>(
				CPAttachmentFileEntryInfoItemFields.uuidInfoField,
				cpAttachmentFileEntry.getUuid()));

		CPMedia cpMedia = null;

		try {
			ThemeDisplay themeDisplay = _getThemeDisplay();

			if (themeDisplay != null) {
				cpMedia = new CPMediaImpl(
					CommerceUtil.getCommerceAccountId(
						CommerceContextThreadLocal.get()),
					cpAttachmentFileEntry, themeDisplay);
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		if (cpMedia != null) {
			cpAttachmentFileEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					CPAttachmentFileEntryInfoItemFields.downloadURLInfoField,
					cpMedia.getDownloadURL()));
			cpAttachmentFileEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					CPAttachmentFileEntryInfoItemFields.mimeTypeInfoField,
					cpMedia.mimeType()));
			cpAttachmentFileEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					CPAttachmentFileEntryInfoItemFields.sizeInfoField,
					cpMedia.getSize()));
			cpAttachmentFileEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					CPAttachmentFileEntryInfoItemFields.thumbnailURLInfoField,
					cpMedia.getThumbnailURL()));
			cpAttachmentFileEntryInfoFieldValues.add(
				new InfoFieldValue<>(
					CPAttachmentFileEntryInfoItemFields.URLInfoField,
					cpMedia.getURL()));
		}

		return cpAttachmentFileEntryInfoFieldValues;
	}

	private ThemeDisplay _getThemeDisplay() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			return serviceContext.getThemeDisplay();
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPAttachmentFileEntryInfoItemFieldValuesProvider.class);

	@Reference
	private ExpandoInfoItemFieldSetProvider _expandoInfoItemFieldSetProvider;

	@Reference
	private InfoItemFieldReaderFieldSetProvider
		_infoItemFieldReaderFieldSetProvider;

	@Reference
	private TemplateInfoItemFieldSetProvider _templateInfoItemFieldSetProvider;

}