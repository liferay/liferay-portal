/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettings;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.internal.util.FileEntryUtil;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.upload.UniqueFileNameProvider;

import java.net.URL;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Stefano Motta
 */
public class SkuVirtualSettingsUtil {

	public static CPDefinitionVirtualSetting addOrUpdateSkuVirtualSettings(
			CPInstance cpInstance, SkuVirtualSettings skuVirtualSettings,
			CPDefinitionVirtualSettingService cpDefinitionVirtualSettingService,
			CPDVirtualSettingFileEntryService cpdVirtualSettingFileEntryService,
			DLAppService dlAppService, GroupService groupService,
			JournalArticleService journalArticleService,
			RepositoryLocalService repositoryLocalService,
			UniqueFileNameProvider uniqueFileNameProvider,
			ServiceContext serviceContext)
		throws Exception {

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpDefinitionVirtualSettingService.fetchCPDefinitionVirtualSetting(
				CPInstance.class.getName(), cpInstance.getCPInstanceId());

		if (cpDefinitionVirtualSetting == null) {
			return _addSkuVirtualSettings(
				cpInstance, skuVirtualSettings,
				cpDefinitionVirtualSettingService,
				cpdVirtualSettingFileEntryService, dlAppService, groupService,
				journalArticleService, repositoryLocalService,
				uniqueFileNameProvider, serviceContext);
		}

		return _updateSkuVirtualSettings(
			cpInstance, cpDefinitionVirtualSetting,
			cpdVirtualSettingFileEntryService, skuVirtualSettings,
			cpDefinitionVirtualSettingService, dlAppService, groupService,
			journalArticleService, repositoryLocalService,
			uniqueFileNameProvider, serviceContext);
	}

	private static CPDefinitionVirtualSetting _addSkuVirtualSettings(
			CPInstance cpInstance, SkuVirtualSettings skuVirtualSettings,
			CPDefinitionVirtualSettingService cpDefinitionVirtualSettingService,
			CPDVirtualSettingFileEntryService cpdVirtualSettingFileEntryService,
			DLAppService dlAppService, GroupService groupService,
			JournalArticleService journalArticleService,
			RepositoryLocalService repositoryLocalService,
			UniqueFileNameProvider uniqueFileNameProvider,
			ServiceContext serviceContext)
		throws Exception {

		if (!GetterUtil.getBoolean(skuVirtualSettings.getOverride())) {
			return null;
		}

		String attachmentURL = _validateURL(skuVirtualSettings.getUrl());

		long attachmentFileEntryId = FileEntryUtil.getFileEntryId(
			skuVirtualSettings.getAttachment(), attachmentURL,
			cpInstance.getGroupId(), cpdVirtualSettingFileEntryService,
			dlAppService, repositoryLocalService, uniqueFileNameProvider,
			serviceContext);

		String sampleAttachmentURL = null;
		long sampleFileEntryId = 0;

		boolean useSample = GetterUtil.getBoolean(
			skuVirtualSettings.getUseSample());

		if (useSample) {
			sampleAttachmentURL = _validateURL(
				skuVirtualSettings.getSampleURL());

			sampleFileEntryId = FileEntryUtil.getFileEntryId(
				skuVirtualSettings.getSampleAttachment(), sampleAttachmentURL,
				cpInstance.getGroupId(), cpdVirtualSettingFileEntryService,
				dlAppService, repositoryLocalService, uniqueFileNameProvider,
				serviceContext);
		}

		Map<Locale, String> termsOfUseContentMap = null;
		long termsOfUseJournalArticleId = 0;

		boolean termsOfUseRequired = GetterUtil.getBoolean(
			skuVirtualSettings.getTermsOfUseRequired());

		if (termsOfUseRequired) {
			termsOfUseContentMap = LanguageUtils.getLocalizedMap(
				skuVirtualSettings.getTermsOfUseContent());
			termsOfUseJournalArticleId = _getTermsOfUseJournalArticleId(
				cpInstance.getCompanyId(), cpInstance.getGroupId(),
				groupService, journalArticleService, skuVirtualSettings);
		}

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpDefinitionVirtualSettingService.addCPDefinitionVirtualSetting(
				CPInstance.class.getName(), cpInstance.getCPInstanceId(),
				attachmentFileEntryId, attachmentURL,
				_getActivationStatus(
					GetterUtil.getInteger(
						skuVirtualSettings.getActivationStatus(),
						CommerceOrderConstants.ORDER_STATUS_COMPLETED)),
				TimeUnit.DAYS.toMillis(
					GetterUtil.getLong(skuVirtualSettings.getDuration())),
				GetterUtil.getInteger(skuVirtualSettings.getMaxUsages()),
				useSample, sampleFileEntryId, sampleAttachmentURL,
				termsOfUseRequired, termsOfUseContentMap,
				termsOfUseJournalArticleId, true, serviceContext);

		if (skuVirtualSettings.getSkuVirtualSettingsFileEntries() == null) {
			return cpDefinitionVirtualSetting;
		}

		for (SkuVirtualSettingsFileEntry productVirtualSettingsFileEntry :
				skuVirtualSettings.getSkuVirtualSettingsFileEntries()) {

			cpdVirtualSettingFileEntryService.addCPDefinitionVirtualSetting(
				cpDefinitionVirtualSetting.getGroupId(),
				CPInstance.class.getName(), cpInstance.getCPInstanceId(),
				cpDefinitionVirtualSetting.getCPDefinitionVirtualSettingId(),
				FileEntryUtil.getFileEntryId(
					productVirtualSettingsFileEntry.getAttachment(),
					productVirtualSettingsFileEntry.getUrl(),
					cpInstance.getGroupId(), cpdVirtualSettingFileEntryService,
					dlAppService, repositoryLocalService,
					uniqueFileNameProvider, serviceContext),
				productVirtualSettingsFileEntry.getUrl(),
				productVirtualSettingsFileEntry.getVersion());
		}

		return cpDefinitionVirtualSetting;
	}

	private static int _getActivationStatus(int activationStatus) {
		if (ArrayUtil.contains(
				VirtualCPTypeConstants.ACTIVATION_STATUSES, activationStatus)) {

			return activationStatus;
		}

		return CommerceOrderConstants.ORDER_STATUS_COMPLETED;
	}

	private static long _getTermsOfUseJournalArticleId(
			long companyId, long groupId, GroupService groupService,
			JournalArticleService journalArticleService,
			SkuVirtualSettings skuVirtualSettings)
		throws Exception {

		String externalReferenceCode =
			skuVirtualSettings.
				getTermsOfUseJournalArticleExternalReferenceCode();

		if (Validator.isNull(externalReferenceCode)) {
			return GetterUtil.getLong(
				skuVirtualSettings.getTermsOfUseJournalArticleId());
		}

		String groupExternalReferenceCode =
			skuVirtualSettings.
				getTermsOfUseJournalArticleGroupExternalReferenceCode();

		long journalArticleGroupId = groupId;

		if (Validator.isNotNull(groupExternalReferenceCode)) {
			Group group = groupService.getGroupByExternalReferenceCode(
				groupExternalReferenceCode, companyId);

			journalArticleGroupId = group.getGroupId();
		}

		JournalArticle journalArticle =
			journalArticleService.fetchLatestArticleByExternalReferenceCode(
				journalArticleGroupId, externalReferenceCode);

		if (journalArticle == null) {
			return 0;
		}

		return journalArticle.getResourcePrimKey();
	}

	private static CPDefinitionVirtualSetting _updateSkuVirtualSettings(
			CPInstance cpInstance,
			CPDefinitionVirtualSetting cpDefinitionVirtualSetting,
			CPDVirtualSettingFileEntryService cpdVirtualSettingFileEntryService,
			SkuVirtualSettings skuVirtualSettings,
			CPDefinitionVirtualSettingService cpDefinitionVirtualSettingService,
			DLAppService dlAppService, GroupService groupService,
			JournalArticleService journalArticleService,
			RepositoryLocalService repositoryLocalService,
			UniqueFileNameProvider uniqueFileNameProvider,
			ServiceContext serviceContext)
		throws Exception {

		if (!GetterUtil.getBoolean(
				skuVirtualSettings.getOverride(),
				cpDefinitionVirtualSetting.isOverride())) {

			return cpDefinitionVirtualSettingService.
				deleteCPDefinitionVirtualSetting(
					CPInstance.class.getName(), cpInstance.getCPInstanceId());
		}

		long attachmentFileEntryId = 0;
		String attachmentURL = _validateURL(skuVirtualSettings.getUrl());

		if (Validator.isNull(attachmentURL)) {
			List<CPDVirtualSettingFileEntry> cpdVirtualSettingFileEntries =
				cpDefinitionVirtualSetting.getCPDVirtualSettingFileEntries();

			CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
				cpdVirtualSettingFileEntries.get(0);

			if (Validator.isNull(skuVirtualSettings.getAttachment())) {
				attachmentURL = cpdVirtualSettingFileEntry.getUrl();
			}
			else {
				attachmentFileEntryId = FileEntryUtil.getFileEntryId(
					skuVirtualSettings.getAttachment(), attachmentURL,
					cpInstance.getGroupId(), cpdVirtualSettingFileEntryService,
					dlAppService, repositoryLocalService,
					uniqueFileNameProvider, serviceContext);
			}

			if (attachmentFileEntryId == 0) {
				attachmentFileEntryId =
					cpdVirtualSettingFileEntry.getFileEntryId();
			}
		}

		Long duration = skuVirtualSettings.getDuration();

		if (duration != null) {
			duration = TimeUnit.DAYS.toMillis(duration);
		}

		String sampleAttachmentURL = null;
		long sampleFileEntryId = 0;

		boolean useSample = GetterUtil.getBoolean(
			skuVirtualSettings.getUseSample(),
			cpDefinitionVirtualSetting.isUseSample());

		if (useSample) {
			sampleAttachmentURL = _validateURL(
				skuVirtualSettings.getSampleURL());

			if (Validator.isNull(sampleAttachmentURL)) {
				if (Validator.isNull(
						skuVirtualSettings.getSampleAttachment())) {

					sampleAttachmentURL =
						cpDefinitionVirtualSetting.getSampleURL();
				}
				else {
					sampleFileEntryId = FileEntryUtil.getFileEntryId(
						skuVirtualSettings.getSampleAttachment(),
						sampleAttachmentURL, cpInstance.getGroupId(),
						cpdVirtualSettingFileEntryService, dlAppService,
						repositoryLocalService, uniqueFileNameProvider,
						serviceContext);
				}

				if (sampleFileEntryId == 0) {
					sampleFileEntryId =
						cpDefinitionVirtualSetting.getSampleFileEntryId();
				}
			}
		}

		Map<Locale, String> termsOfUseContentMap = null;
		long termsOfUseJournalArticleId = 0;

		boolean termsOfUseRequired = GetterUtil.get(
			skuVirtualSettings.getTermsOfUseRequired(),
			cpDefinitionVirtualSetting.isTermsOfUseRequired());

		if (termsOfUseRequired) {
			termsOfUseContentMap = LanguageUtils.getLocalizedMap(
				skuVirtualSettings.getTermsOfUseContent());
			termsOfUseJournalArticleId = _getTermsOfUseJournalArticleId(
				cpDefinitionVirtualSetting.getCompanyId(),
				cpDefinitionVirtualSetting.getGroupId(), groupService,
				journalArticleService, skuVirtualSettings);

			if ((termsOfUseContentMap == null) &&
				(termsOfUseJournalArticleId == 0)) {

				JournalArticle termsOfUseJournalArticle =
					cpDefinitionVirtualSetting.getTermsOfUseJournalArticle();

				if (termsOfUseJournalArticle != null) {
					termsOfUseJournalArticleId =
						termsOfUseJournalArticle.getResourcePrimKey();
				}
				else {
					termsOfUseContentMap =
						cpDefinitionVirtualSetting.getTermsOfUseContentMap();
				}
			}
		}

		cpDefinitionVirtualSetting =
			cpDefinitionVirtualSettingService.updateCPDefinitionVirtualSetting(
				cpDefinitionVirtualSetting.getCPDefinitionVirtualSettingId(),
				attachmentFileEntryId, attachmentURL,
				_getActivationStatus(
					GetterUtil.getInteger(
						skuVirtualSettings.getActivationStatus(),
						cpDefinitionVirtualSetting.getActivationStatus())),
				GetterUtil.getLong(
					duration, cpDefinitionVirtualSetting.getDuration()),
				GetterUtil.getInteger(
					skuVirtualSettings.getMaxUsages(),
					cpDefinitionVirtualSetting.getMaxUsages()),
				useSample, sampleFileEntryId, sampleAttachmentURL,
				termsOfUseRequired, termsOfUseContentMap,
				termsOfUseJournalArticleId, true, serviceContext);

		if (skuVirtualSettings.getSkuVirtualSettingsFileEntries() == null) {
			return cpDefinitionVirtualSetting;
		}

		for (CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry :
				cpDefinitionVirtualSetting.getCPDVirtualSettingFileEntries()) {

			cpdVirtualSettingFileEntryService.deleteCPDVirtualSettingFileEntry(
				CPInstance.class.getName(),
				cpDefinitionVirtualSetting.getClassPK(),
				cpdVirtualSettingFileEntry.
					getCPDefinitionVirtualSettingFileEntryId());
		}

		for (SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry :
				skuVirtualSettings.getSkuVirtualSettingsFileEntries()) {

			cpdVirtualSettingFileEntryService.addCPDefinitionVirtualSetting(
				cpDefinitionVirtualSetting.getGroupId(),
				CPInstance.class.getName(),
				cpDefinitionVirtualSetting.getClassPK(),
				cpDefinitionVirtualSetting.getCPDefinitionVirtualSettingId(),
				FileEntryUtil.getFileEntryId(
					skuVirtualSettingsFileEntry.getAttachment(),
					skuVirtualSettingsFileEntry.getUrl(),
					cpInstance.getGroupId(), cpdVirtualSettingFileEntryService,
					dlAppService, repositoryLocalService,
					uniqueFileNameProvider, serviceContext),
				skuVirtualSettingsFileEntry.getUrl(),
				skuVirtualSettingsFileEntry.getVersion());
		}

		return cpDefinitionVirtualSetting;
	}

	private static String _validateURL(String value) throws Exception {
		if (Validator.isNull(value)) {
			return null;
		}

		URL url = new URL(value);

		return url.toString();
	}

}