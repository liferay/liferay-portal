/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.account.constants.AccountConstants;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettings;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Status;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "dto.class.name=com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettings",
	service = DTOConverter.class
)
public class SkuVirtualSettingsDTOConverter
	implements DTOConverter<CPInstance, SkuVirtualSettings> {

	@Override
	public String getContentType() {
		return SkuVirtualSettings.class.getSimpleName();
	}

	@Override
	public SkuVirtualSettings toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPInstance cpInstance = _cpInstanceService.getCPInstance(
			(Long)dtoConverterContext.getId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		if (!VirtualCPTypeConstants.NAME.equals(
				cpDefinition.getProductTypeName())) {

			return null;
		}

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.fetchCPDefinitionVirtualSetting(
				CPInstance.class.getName(), cpInstance.getCPInstanceId());

		if (cpDefinitionVirtualSetting == null) {
			return null;
		}

		List<CPDVirtualSettingFileEntry> cpdVirtualSettingFileEntries =
			cpDefinitionVirtualSetting.getCPDVirtualSettingFileEntries();

		return new SkuVirtualSettings() {
			{
				setActivationStatus(
					cpDefinitionVirtualSetting::getActivationStatus);
				setActivationStatusInfo(
					() -> {
						String orderStatusLabel =
							CommerceOrderConstants.getOrderStatusLabel(
								cpDefinitionVirtualSetting.
									getActivationStatus());

						return new Status() {
							{
								setCode(
									cpDefinitionVirtualSetting::
										getActivationStatus);
								setLabel(() -> orderStatusLabel);
								setLabel_i18n(
									() -> _language.get(
										dtoConverterContext.getLocale(),
										orderStatusLabel));
							}
						};
					});
				setDuration(
					() -> TimeUnit.MILLISECONDS.toDays(
						cpDefinitionVirtualSetting.getDuration()));
				setId(
					cpDefinitionVirtualSetting::
						getCPDefinitionVirtualSettingId);
				setMaxUsages(cpDefinitionVirtualSetting::getMaxUsages);
				setOverride(cpDefinitionVirtualSetting::isOverride);
				setSampleSrc(
					() -> {
						FileEntry fileEntry =
							cpDefinitionVirtualSetting.getSampleFileEntry();

						if (fileEntry == null) {
							return null;
						}

						return _commerceMediaResolver.
							getDownloadVirtualProductSampleURL(
								CPInstance.class.getName(),
								cpInstance.getCPInstanceId(),
								AccountConstants.ACCOUNT_ENTRY_ID_ADMIN,
								fileEntry.getFileEntryId());
					});
				setSampleURL(cpDefinitionVirtualSetting::getSampleURL);
				setSkuVirtualSettingsFileEntries(
					() -> _toSkuVirtualSettingsFileEntries(
						cpdVirtualSettingFileEntries, cpInstance));
				setSrc(
					() -> {
						if (cpdVirtualSettingFileEntries.isEmpty()) {
							return null;
						}

						CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
							cpdVirtualSettingFileEntries.get(0);

						long fileEntryId =
							cpdVirtualSettingFileEntry.getFileEntryId();

						if (fileEntryId == 0) {
							return null;
						}

						return _commerceMediaResolver.
							getDownloadVirtualProductURL(
								CPInstance.class.getName(),
								cpInstance.getCPDefinitionId(),
								AccountConstants.ACCOUNT_ENTRY_ID_ADMIN,
								fileEntryId);
					});
				setTermsOfUseContent(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinitionVirtualSetting.getTermsOfUseContentMap()));
				setTermsOfUseJournalArticleExternalReferenceCode(
					() -> {
						JournalArticle journalArticle =
							cpDefinitionVirtualSetting.
								getTermsOfUseJournalArticle();

						if (journalArticle == null) {
							return null;
						}

						return journalArticle.getExternalReferenceCode();
					});
				setTermsOfUseJournalArticleGroupExternalReferenceCode(
					() -> {
						JournalArticle journalArticle =
							cpDefinitionVirtualSetting.
								getTermsOfUseJournalArticle();

						if (journalArticle == null) {
							return null;
						}

						Group group = _groupLocalService.getGroup(
							journalArticle.getGroupId());

						String groupExternalReferenceCode =
							group.getExternalReferenceCode();

						if (Validator.isNull(groupExternalReferenceCode)) {
							return null;
						}

						return groupExternalReferenceCode;
					});
				setTermsOfUseJournalArticleId(
					() -> {
						JournalArticle journalArticle =
							cpDefinitionVirtualSetting.
								getTermsOfUseJournalArticle();

						if (journalArticle == null) {
							return null;
						}

						return journalArticle.getResourcePrimKey();
					});
				setTermsOfUseRequired(
					cpDefinitionVirtualSetting::isTermsOfUseRequired);
				setUrl(
					() -> {
						if (cpdVirtualSettingFileEntries.isEmpty()) {
							return null;
						}

						CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
							cpdVirtualSettingFileEntries.get(0);

						if (Validator.isNull(
								cpdVirtualSettingFileEntry.getUrl())) {

							return null;
						}

						return cpdVirtualSettingFileEntry.getUrl();
					});
				setUseSample(cpDefinitionVirtualSetting::isUseSample);
			}
		};
	}

	private SkuVirtualSettingsFileEntry[] _toSkuVirtualSettingsFileEntries(
		List<CPDVirtualSettingFileEntry> cpdVirtualSettingFileEntries,
		CPInstance cpInstance) {

		return TransformUtil.transformToArray(
			cpdVirtualSettingFileEntries,
			cpdVirtualSettingFileEntry -> new SkuVirtualSettingsFileEntry() {
				{
					setSrc(
						() -> {
							long fileEntryId =
								cpdVirtualSettingFileEntry.getFileEntryId();

							if (fileEntryId == 0) {
								return null;
							}

							return _commerceMediaResolver.
								getDownloadVirtualProductURL(
									CPInstance.class.getName(),
									cpInstance.getCPInstanceId(),
									AccountConstants.ACCOUNT_ENTRY_ID_ADMIN,
									fileEntryId);
						});
					setUrl(
						() -> {
							if (Validator.isNull(
									cpdVirtualSettingFileEntry.getUrl())) {

								return null;
							}

							return cpdVirtualSettingFileEntry.getUrl();
						});
					setVersion(
						() -> {
							if (Validator.isNull(
									cpdVirtualSettingFileEntry.getVersion())) {

								return null;
							}

							return cpdVirtualSettingFileEntry.getVersion();
						});
				}
			},
			SkuVirtualSettingsFileEntry.class);
	}

	@Reference
	private CommerceMediaResolver _commerceMediaResolver;

	@Reference
	private CPDefinitionVirtualSettingService
		_cpDefinitionVirtualSettingService;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

}