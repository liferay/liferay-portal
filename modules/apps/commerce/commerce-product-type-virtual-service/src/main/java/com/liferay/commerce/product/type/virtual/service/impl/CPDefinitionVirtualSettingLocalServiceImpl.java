/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service.impl;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingSampleException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingSampleFileEntryIdException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingSampleURLException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingTermsOfUseArticleResourcePKException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingTermsOfUseContentException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingTermsOfUseException;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryLocalService;
import com.liferay.commerce.product.type.virtual.service.base.CPDefinitionVirtualSettingLocalServiceBaseImpl;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting",
	service = AopService.class
)
public class CPDefinitionVirtualSettingLocalServiceImpl
	extends CPDefinitionVirtualSettingLocalServiceBaseImpl {

	@Override
	public CPDefinitionVirtualSetting addCPDefinitionVirtualSetting(
			String className, long classPK, long fileEntryId, String url,
			int activationStatus, long duration, int maxUsages,
			boolean useSample, long sampleFileEntryId, String sampleURL,
			boolean termsOfUseRequired,
			Map<Locale, String> termsOfUseContentMap,
			long termsOfUseJournalArticleResourcePrimKey, boolean override,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		long groupId = 0;

		if (className.equals(CPDefinition.class.getName())) {
			CPDefinition cpDefinition =
				_cpDefinitionLocalService.getCPDefinition(classPK);

			groupId = cpDefinition.getGroupId();
		}
		else {
			CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
				classPK);

			groupId = cpInstance.getGroupId();
		}

		if (Validator.isNotNull(url)) {
			fileEntryId = 0;
		}
		else {
			url = null;
		}

		if (useSample) {
			if (Validator.isNotNull(sampleURL)) {
				sampleFileEntryId = 0;
			}
			else {
				sampleURL = null;
			}
		}
		else {
			sampleURL = null;
			sampleFileEntryId = 0;
		}

		if (termsOfUseRequired) {
			if (termsOfUseJournalArticleResourcePrimKey > 0) {
				termsOfUseContentMap = Collections.emptyMap();
			}
			else {
				termsOfUseJournalArticleResourcePrimKey = 0;
			}
		}
		else {
			termsOfUseContentMap = Collections.emptyMap();
			termsOfUseJournalArticleResourcePrimKey = 0;
		}

		_validate(
			useSample, sampleFileEntryId, sampleURL, termsOfUseRequired,
			termsOfUseContentMap, termsOfUseJournalArticleResourcePrimKey);

		long cpDefinitionVirtualSettingId = counterLocalService.increment();

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpDefinitionVirtualSettingPersistence.create(
				cpDefinitionVirtualSettingId);

		if (className.equals(CPDefinition.class.getName()) &&
			_cpDefinitionLocalService.isVersionable(classPK)) {

			CPDefinition newCPDefinition =
				_cpDefinitionLocalService.copyCPDefinition(
					cpDefinitionVirtualSetting.getClassPK());

			classPK = newCPDefinition.getCPDefinitionId();
		}
		else if (className.equals(CPInstance.class.getName())) {
			CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
				classPK);

			if (_cpDefinitionLocalService.isVersionable(
					cpInstance.getCPDefinitionId())) {

				CPDefinition newCPDefinition =
					_cpDefinitionLocalService.copyCPDefinition(
						cpInstance.getCPDefinitionId());

				CPInstance newCPInstance =
					_cpInstanceLocalService.getCProductInstance(
						newCPDefinition.getCProductId(),
						cpInstance.getCPInstanceUuid());

				classPK = newCPInstance.getCPInstanceId();
			}
		}

		cpDefinitionVirtualSetting.setGroupId(groupId);
		cpDefinitionVirtualSetting.setCompanyId(user.getCompanyId());
		cpDefinitionVirtualSetting.setUserId(user.getUserId());
		cpDefinitionVirtualSetting.setUserName(user.getFullName());
		cpDefinitionVirtualSetting.setClassName(className);
		cpDefinitionVirtualSetting.setClassPK(classPK);
		cpDefinitionVirtualSetting.setActivationStatus(activationStatus);
		cpDefinitionVirtualSetting.setDuration(duration);
		cpDefinitionVirtualSetting.setMaxUsages(maxUsages);
		cpDefinitionVirtualSetting.setUseSample(useSample);
		cpDefinitionVirtualSetting.setSampleFileEntryId(sampleFileEntryId);
		cpDefinitionVirtualSetting.setSampleURL(sampleURL);
		cpDefinitionVirtualSetting.setTermsOfUseRequired(termsOfUseRequired);
		cpDefinitionVirtualSetting.setTermsOfUseContentMap(
			termsOfUseContentMap);
		cpDefinitionVirtualSetting.setTermsOfUseJournalArticleResourcePrimKey(
			termsOfUseJournalArticleResourcePrimKey);
		cpDefinitionVirtualSetting.setOverride(override);
		cpDefinitionVirtualSetting.setExpandoBridgeAttributes(serviceContext);

		cpDefinitionVirtualSetting =
			cpDefinitionVirtualSettingPersistence.update(
				cpDefinitionVirtualSetting);

		if ((fileEntryId > 0) || Validator.isNotNull(url)) {
			_cpdVirtualSettingFileEntryLocalService.
				addCPDVirtualSettingFileEntry(
					user.getUserId(), groupId,
					cpDefinitionVirtualSetting.
						getCPDefinitionVirtualSettingId(),
					fileEntryId, url, StringPool.BLANK);
		}

		return cpDefinitionVirtualSetting;
	}

	@Override
	public void cloneCPDefinitionVirtualSetting(
		long cpDefinitionId, long newCPDefinitionId) {

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpDefinitionVirtualSettingLocalService.
				fetchCPDefinitionVirtualSetting(
					CPDefinition.class.getName(), cpDefinitionId);

		if (cpDefinitionVirtualSetting != null) {
			CPDefinitionVirtualSetting newCPDefinitionVirtualSetting =
				(CPDefinitionVirtualSetting)cpDefinitionVirtualSetting.clone();

			newCPDefinitionVirtualSetting.setUuid(PortalUUIDUtil.generate());
			newCPDefinitionVirtualSetting.setCPDefinitionVirtualSettingId(
				counterLocalService.increment());
			newCPDefinitionVirtualSetting.setClassPK(newCPDefinitionId);

			cpDefinitionVirtualSettingLocalService.
				addCPDefinitionVirtualSetting(newCPDefinitionVirtualSetting);

			for (CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry :
					_cpdVirtualSettingFileEntryLocalService.
						getCPDVirtualSettingFileEntries(
							cpDefinitionVirtualSetting.
								getCPDefinitionVirtualSettingId())) {

				CPDVirtualSettingFileEntry newCPDVirtualSettingFileEntry =
					(CPDVirtualSettingFileEntry)
						cpdVirtualSettingFileEntry.clone();

				newCPDVirtualSettingFileEntry.setUuid(
					PortalUUIDUtil.generate());
				newCPDVirtualSettingFileEntry.
					setCPDefinitionVirtualSettingFileEntryId(
						counterLocalService.increment());
				newCPDVirtualSettingFileEntry.setCPDefinitionVirtualSettingId(
					newCPDefinitionVirtualSetting.
						getCPDefinitionVirtualSettingId());

				_cpdVirtualSettingFileEntryLocalService.
					addCPDVirtualSettingFileEntry(
						newCPDVirtualSettingFileEntry);
			}
		}
	}

	@Override
	public CPDefinitionVirtualSetting deleteCPDefinitionVirtualSetting(
			String className, long classPK)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpDefinitionVirtualSettingPersistence.fetchByC_C(
				classNameId, classPK);

		if (cpDefinitionVirtualSetting != null) {
			if (className.equals(CPDefinition.class.getName()) &&
				_cpDefinitionLocalService.isVersionable(classPK)) {

				CPDefinition newCPDefinition =
					_cpDefinitionLocalService.copyCPDefinition(classPK);

				cpDefinitionVirtualSetting =
					cpDefinitionVirtualSettingPersistence.findByC_C(
						classNameId, newCPDefinition.getCPDefinitionId());
			}
			else if (className.equals(CPInstance.class.getName())) {
				CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
					classPK);

				if (_cpDefinitionLocalService.isVersionable(
						cpInstance.getCPDefinitionId())) {

					CPDefinition newCPDefinition =
						_cpDefinitionLocalService.copyCPDefinition(
							cpInstance.getCPDefinitionId());

					CPInstance newCPInstance =
						_cpInstanceLocalService.getCProductInstance(
							newCPDefinition.getCProductId(),
							cpInstance.getCPInstanceUuid());

					cpDefinitionVirtualSetting =
						cpDefinitionVirtualSettingPersistence.findByC_C(
							classNameId, newCPInstance.getCPInstanceId());
				}
			}

			_cpdVirtualSettingFileEntryLocalService.
				deleteCPDVirtualSettingFileEntries(
					cpDefinitionVirtualSetting.
						getCPDefinitionVirtualSettingId());

			cpDefinitionVirtualSettingPersistence.remove(
				cpDefinitionVirtualSetting);
		}

		return cpDefinitionVirtualSetting;
	}

	@Override
	public CPDefinitionVirtualSetting fetchCPDefinitionVirtualSetting(
		String className, long classPK) {

		return cpDefinitionVirtualSettingPersistence.fetchByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public CPDefinitionVirtualSetting getCPDefinitionVirtualSetting(
		String className, long classPK) {

		return cpDefinitionVirtualSettingPersistence.fetchByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public CPDefinitionVirtualSetting updateCPDefinitionVirtualSetting(
			long cpDefinitionVirtualSettingId, long fileEntryId, String url,
			int activationStatus, long duration, int maxUsages,
			boolean useSample, long sampleFileEntryId, String sampleURL,
			boolean termsOfUseRequired,
			Map<Locale, String> termsOfUseContentMap,
			long termsOfUseJournalArticleResourcePrimKey, boolean override,
			ServiceContext serviceContext)
		throws PortalException {

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpDefinitionVirtualSettingPersistence.findByPrimaryKey(
				cpDefinitionVirtualSettingId);

		if (useSample) {
			if (Validator.isNotNull(sampleURL)) {
				sampleFileEntryId = 0;
			}
			else {
				sampleURL = null;
			}
		}
		else {
			sampleURL = null;
			sampleFileEntryId = 0;
		}

		if (termsOfUseRequired) {
			if (termsOfUseJournalArticleResourcePrimKey > 0) {
				termsOfUseContentMap = Collections.emptyMap();
			}
			else {
				termsOfUseJournalArticleResourcePrimKey = 0;
			}
		}
		else {
			termsOfUseContentMap = Collections.emptyMap();
			termsOfUseJournalArticleResourcePrimKey = 0;
		}

		_validate(
			useSample, sampleFileEntryId, sampleURL, termsOfUseRequired,
			termsOfUseContentMap, termsOfUseJournalArticleResourcePrimKey);

		long cpDefinitionClassNameId = _classNameLocalService.getClassNameId(
			CPDefinition.class);
		long cpInstanceClassNameId = _classNameLocalService.getClassNameId(
			CPInstance.class);

		if ((cpDefinitionVirtualSetting.getClassNameId() ==
				cpDefinitionClassNameId) &&
			_cpDefinitionLocalService.isVersionable(
				cpDefinitionVirtualSetting.getClassPK())) {

			CPDefinition newCPDefinition =
				_cpDefinitionLocalService.copyCPDefinition(
					cpDefinitionVirtualSetting.getClassPK());

			cpDefinitionVirtualSetting =
				cpDefinitionVirtualSettingPersistence.findByC_C(
					cpDefinitionVirtualSetting.getClassNameId(),
					newCPDefinition.getCPDefinitionId());
		}
		else if (cpDefinitionVirtualSetting.getClassNameId() ==
					cpInstanceClassNameId) {

			CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
				cpDefinitionVirtualSetting.getClassPK());

			if (_cpDefinitionLocalService.isVersionable(
					cpInstance.getCPDefinitionId())) {

				CPDefinition newCPDefinition =
					_cpDefinitionLocalService.copyCPDefinition(
						cpInstance.getCPDefinitionId());

				CPInstance newCPInstance =
					_cpInstanceLocalService.getCProductInstance(
						newCPDefinition.getCProductId(),
						cpInstance.getCPInstanceUuid());

				cpDefinitionVirtualSetting =
					cpDefinitionVirtualSettingPersistence.findByC_C(
						cpDefinitionVirtualSetting.getClassNameId(),
						newCPInstance.getCPInstanceId());
			}
		}

		cpDefinitionVirtualSetting.setActivationStatus(activationStatus);
		cpDefinitionVirtualSetting.setDuration(duration);
		cpDefinitionVirtualSetting.setMaxUsages(maxUsages);
		cpDefinitionVirtualSetting.setUseSample(useSample);
		cpDefinitionVirtualSetting.setSampleFileEntryId(sampleFileEntryId);
		cpDefinitionVirtualSetting.setSampleURL(sampleURL);
		cpDefinitionVirtualSetting.setTermsOfUseRequired(termsOfUseRequired);
		cpDefinitionVirtualSetting.setTermsOfUseContentMap(
			termsOfUseContentMap);
		cpDefinitionVirtualSetting.setTermsOfUseJournalArticleResourcePrimKey(
			termsOfUseJournalArticleResourcePrimKey);
		cpDefinitionVirtualSetting.setOverride(override);
		cpDefinitionVirtualSetting.setExpandoBridgeAttributes(serviceContext);

		cpDefinitionVirtualSetting =
			cpDefinitionVirtualSettingPersistence.update(
				cpDefinitionVirtualSetting);

		if ((fileEntryId > 0) || Validator.isNotNull(url)) {
			_cpdVirtualSettingFileEntryLocalService.
				addCPDVirtualSettingFileEntry(
					cpDefinitionVirtualSetting.getUserId(),
					cpDefinitionVirtualSetting.getGroupId(),
					cpDefinitionVirtualSetting.
						getCPDefinitionVirtualSettingId(),
					fileEntryId, url, StringPool.BLANK);
		}

		return cpDefinitionVirtualSetting;
	}

	@Override
	public CPDefinitionVirtualSetting updateCPDefinitionVirtualSetting(
			long cpDefinitionVirtualSettingId, long fileEntryId, String url,
			int activationStatus, long duration, int maxUsages,
			boolean useSample, long sampleFileEntryId, String sampleURL,
			boolean termsOfUseRequired,
			Map<Locale, String> termsOfUseContentMap,
			long termsOfUseJournalArticleResourcePrimKey,
			ServiceContext serviceContext)
		throws PortalException {

		return cpDefinitionVirtualSettingLocalService.
			updateCPDefinitionVirtualSetting(
				cpDefinitionVirtualSettingId, fileEntryId, url,
				activationStatus, duration, maxUsages, useSample,
				sampleFileEntryId, sampleURL, termsOfUseRequired,
				termsOfUseContentMap, termsOfUseJournalArticleResourcePrimKey,
				false, serviceContext);
	}

	private void _validate(
			boolean useSample, long sampleFileEntryId, String sampleURL,
			boolean termsOfUseRequired,
			Map<Locale, String> termsOfUseContentMap,
			long termsOfUseJournalArticleResourcePrimKey)
		throws PortalException {

		if (useSample) {
			if (sampleFileEntryId > 0) {
				try {
					_dlAppLocalService.getFileEntry(sampleFileEntryId);
				}
				catch (NoSuchFileEntryException noSuchFileEntryException) {
					throw new CPDefinitionVirtualSettingSampleFileEntryIdException(
						noSuchFileEntryException);
				}
			}
			else if (Validator.isNull(sampleURL)) {
				throw new CPDefinitionVirtualSettingSampleException();
			}
			else {
				try {
					new URL(sampleURL);
				}
				catch (MalformedURLException malformedURLException) {
					throw new CPDefinitionVirtualSettingSampleURLException(
						malformedURLException);
				}
			}
		}

		if (termsOfUseRequired) {
			if (termsOfUseJournalArticleResourcePrimKey > 0) {
				JournalArticle journalArticle =
					_journalArticleLocalService.fetchLatestArticle(
						termsOfUseJournalArticleResourcePrimKey);

				if (journalArticle == null) {
					throw new CPDefinitionVirtualSettingTermsOfUseArticleResourcePKException();
				}
			}
			else if ((termsOfUseJournalArticleResourcePrimKey <= 0) &&
					 MapUtil.isEmpty(termsOfUseContentMap)) {

				throw new CPDefinitionVirtualSettingTermsOfUseException();
			}
			else {
				boolean empty = true;

				for (Map.Entry<Locale, String> entry :
						termsOfUseContentMap.entrySet()) {

					if (Validator.isNotNull(entry.getValue())) {
						empty = false;
					}
				}

				if (empty) {
					throw new CPDefinitionVirtualSettingTermsOfUseContentException();
				}
			}
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDVirtualSettingFileEntryLocalService
		_cpdVirtualSettingFileEntryLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}