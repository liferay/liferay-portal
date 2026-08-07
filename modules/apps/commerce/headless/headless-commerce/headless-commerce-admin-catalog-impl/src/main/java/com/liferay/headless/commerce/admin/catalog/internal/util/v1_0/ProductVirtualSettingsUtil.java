/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductVirtualSettings;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductVirtualSettingsFileEntry;
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
public class ProductVirtualSettingsUtil {

	public static CPDefinitionVirtualSetting addOrUpdateProductVirtualSettings(
			CPDefinition cpDefinition,
			ProductVirtualSettings productVirtualSettings,
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
				CPDefinition.class.getName(), cpDefinition.getCPDefinitionId());

		if (cpDefinitionVirtualSetting == null) {
			return _addProductVirtualSettings(
				cpDefinition, productVirtualSettings,
				cpDefinitionVirtualSettingService,
				cpdVirtualSettingFileEntryService, dlAppService, groupService,
				journalArticleService, repositoryLocalService,
				uniqueFileNameProvider, serviceContext);
		}

		return _updateProductVirtualSettings(
			cpDefinition, cpDefinitionVirtualSetting, productVirtualSettings,
			cpDefinitionVirtualSettingService,
			cpdVirtualSettingFileEntryService, dlAppService, groupService,
			journalArticleService, repositoryLocalService,
			uniqueFileNameProvider, serviceContext);
	}

	private static CPDefinitionVirtualSetting _addProductVirtualSettings(
			CPDefinition cpDefinition,
			ProductVirtualSettings productVirtualSettings,
			CPDefinitionVirtualSettingService cpDefinitionVirtualSettingService,
			CPDVirtualSettingFileEntryService cpdVirtualSettingFileEntryService,
			DLAppService dlAppService, GroupService groupService,
			JournalArticleService journalArticleService,
			RepositoryLocalService repositoryLocalService,
			UniqueFileNameProvider uniqueFileNameProvider,
			ServiceContext serviceContext)
		throws Exception {

		String attachmentURL = _validateURL(productVirtualSettings.getUrl());

		long attachmentFileEntryId = FileEntryUtil.getFileEntryId(
			productVirtualSettings.getAttachment(), attachmentURL,
			cpDefinition.getGroupId(), cpdVirtualSettingFileEntryService,
			dlAppService, repositoryLocalService, uniqueFileNameProvider,
			serviceContext);

		String sampleAttachmentURL = null;
		long sampleFileEntryId = 0;

		boolean useSample = GetterUtil.getBoolean(
			productVirtualSettings.getUseSample());

		if (useSample) {
			sampleAttachmentURL = _validateURL(
				productVirtualSettings.getSampleURL());

			sampleFileEntryId = FileEntryUtil.getFileEntryId(
				productVirtualSettings.getSampleAttachment(),
				sampleAttachmentURL, cpDefinition.getGroupId(),
				cpdVirtualSettingFileEntryService, dlAppService,
				repositoryLocalService, uniqueFileNameProvider, serviceContext);
		}

		Map<Locale, String> termsOfUseContentMap = null;
		long termsOfUseJournalArticleId = 0;

		boolean termsOfUseRequired = GetterUtil.getBoolean(
			productVirtualSettings.getTermsOfUseRequired());

		if (termsOfUseRequired) {
			termsOfUseContentMap = LanguageUtils.getLocalizedMap(
				productVirtualSettings.getTermsOfUseContent());
			termsOfUseJournalArticleId = _getTermsOfUseJournalArticleId(
				cpDefinition.getCompanyId(), cpDefinition.getGroupId(),
				groupService, journalArticleService, productVirtualSettings);
		}

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpDefinitionVirtualSettingService.addCPDefinitionVirtualSetting(
				CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
				attachmentFileEntryId, attachmentURL,
				_getActivationStatus(
					GetterUtil.getInteger(
						productVirtualSettings.getActivationStatus(),
						CommerceOrderConstants.ORDER_STATUS_COMPLETED)),
				TimeUnit.DAYS.toMillis(
					GetterUtil.getLong(productVirtualSettings.getDuration())),
				GetterUtil.getInteger(productVirtualSettings.getMaxUsages()),
				useSample, sampleFileEntryId, sampleAttachmentURL,
				termsOfUseRequired, termsOfUseContentMap,
				termsOfUseJournalArticleId, false, serviceContext);

		if (productVirtualSettings.getProductVirtualSettingsFileEntries() ==
				null) {

			return cpDefinitionVirtualSetting;
		}

		for (ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry :
				productVirtualSettings.getProductVirtualSettingsFileEntries()) {

			cpdVirtualSettingFileEntryService.addCPDefinitionVirtualSetting(
				cpDefinitionVirtualSetting.getGroupId(),
				CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
				cpDefinitionVirtualSetting.getCPDefinitionVirtualSettingId(),
				FileEntryUtil.getFileEntryId(
					productVirtualSettingsFileEntry.getAttachment(),
					productVirtualSettingsFileEntry.getUrl(),
					cpDefinition.getGroupId(),
					cpdVirtualSettingFileEntryService, dlAppService,
					repositoryLocalService, uniqueFileNameProvider,
					serviceContext),
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
			ProductVirtualSettings productVirtualSettings)
		throws Exception {

		String externalReferenceCode =
			productVirtualSettings.
				getTermsOfUseJournalArticleExternalReferenceCode();

		if (Validator.isNull(externalReferenceCode)) {
			return GetterUtil.getLong(
				productVirtualSettings.getTermsOfUseJournalArticleId());
		}

		String groupExternalReferenceCode =
			productVirtualSettings.
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

	private static CPDefinitionVirtualSetting _updateProductVirtualSettings(
			CPDefinition cpDefinition,
			CPDefinitionVirtualSetting cpDefinitionVirtualSetting,
			ProductVirtualSettings productVirtualSettings,
			CPDefinitionVirtualSettingService cpDefinitionVirtualSettingService,
			CPDVirtualSettingFileEntryService cpdVirtualSettingFileEntryService,
			DLAppService dlAppService, GroupService groupService,
			JournalArticleService journalArticleService,
			RepositoryLocalService repositoryLocalService,
			UniqueFileNameProvider uniqueFileNameProvider,
			ServiceContext serviceContext)
		throws Exception {

		long attachmentFileEntryId = 0;
		String attachmentURL = _validateURL(productVirtualSettings.getUrl());

		if (Validator.isNull(attachmentURL)) {
			List<CPDVirtualSettingFileEntry> cpdVirtualSettingFileEntries =
				cpDefinitionVirtualSetting.getCPDVirtualSettingFileEntries();

			CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
				cpdVirtualSettingFileEntries.get(0);

			if (Validator.isNull(productVirtualSettings.getAttachment())) {
				attachmentURL = cpdVirtualSettingFileEntry.getUrl();
			}
			else {
				attachmentFileEntryId = FileEntryUtil.getFileEntryId(
					productVirtualSettings.getAttachment(), attachmentURL,
					cpDefinition.getGroupId(),
					cpdVirtualSettingFileEntryService, dlAppService,
					repositoryLocalService, uniqueFileNameProvider,
					serviceContext);
			}

			if (attachmentFileEntryId == 0) {
				attachmentFileEntryId =
					cpdVirtualSettingFileEntry.getFileEntryId();
			}
		}

		Long duration = productVirtualSettings.getDuration();

		if (duration != null) {
			duration = TimeUnit.DAYS.toMillis(duration);
		}

		String sampleAttachmentURL = null;
		long sampleFileEntryId = 0;

		boolean useSample = GetterUtil.getBoolean(
			productVirtualSettings.getUseSample(),
			cpDefinitionVirtualSetting.isUseSample());

		if (useSample) {
			sampleAttachmentURL = _validateURL(
				productVirtualSettings.getSampleURL());

			if (Validator.isNull(sampleAttachmentURL)) {
				if (Validator.isNull(
						productVirtualSettings.getSampleAttachment())) {

					sampleAttachmentURL =
						cpDefinitionVirtualSetting.getSampleURL();
				}
				else {
					sampleFileEntryId = FileEntryUtil.getFileEntryId(
						productVirtualSettings.getSampleAttachment(),
						sampleAttachmentURL, cpDefinition.getGroupId(),
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
			productVirtualSettings.getTermsOfUseRequired(),
			cpDefinitionVirtualSetting.isTermsOfUseRequired());

		if (termsOfUseRequired) {
			termsOfUseContentMap = LanguageUtils.getLocalizedMap(
				productVirtualSettings.getTermsOfUseContent());
			termsOfUseJournalArticleId = _getTermsOfUseJournalArticleId(
				cpDefinitionVirtualSetting.getCompanyId(),
				cpDefinitionVirtualSetting.getGroupId(), groupService,
				journalArticleService, productVirtualSettings);

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
						productVirtualSettings.getActivationStatus(),
						cpDefinitionVirtualSetting.getActivationStatus())),
				GetterUtil.getLong(
					duration, cpDefinitionVirtualSetting.getDuration()),
				GetterUtil.getInteger(
					productVirtualSettings.getMaxUsages(),
					cpDefinitionVirtualSetting.getMaxUsages()),
				useSample, sampleFileEntryId, sampleAttachmentURL,
				termsOfUseRequired, termsOfUseContentMap,
				termsOfUseJournalArticleId, serviceContext);

		if (productVirtualSettings.getProductVirtualSettingsFileEntries() ==
				null) {

			return cpDefinitionVirtualSetting;
		}

		for (CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry :
				cpDefinitionVirtualSetting.getCPDVirtualSettingFileEntries()) {

			cpdVirtualSettingFileEntryService.deleteCPDVirtualSettingFileEntry(
				CPDefinition.class.getName(),
				cpDefinitionVirtualSetting.getClassPK(),
				cpdVirtualSettingFileEntry.
					getCPDefinitionVirtualSettingFileEntryId());
		}

		for (ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry :
				productVirtualSettings.getProductVirtualSettingsFileEntries()) {

			cpdVirtualSettingFileEntryService.addCPDefinitionVirtualSetting(
				cpDefinitionVirtualSetting.getGroupId(),
				CPDefinition.class.getName(),
				cpDefinitionVirtualSetting.getClassPK(),
				cpDefinitionVirtualSetting.getCPDefinitionVirtualSettingId(),
				FileEntryUtil.getFileEntryId(
					productVirtualSettingsFileEntry.getAttachment(),
					productVirtualSettingsFileEntry.getUrl(),
					cpDefinition.getGroupId(),
					cpdVirtualSettingFileEntryService, dlAppService,
					repositoryLocalService, uniqueFileNameProvider,
					serviceContext),
				productVirtualSettingsFileEntry.getUrl(),
				productVirtualSettingsFileEntry.getVersion());
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