/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.service.impl;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.override.exception.PLOEntryImportException;
import com.liferay.portal.language.override.exception.PLOEntryKeyException;
import com.liferay.portal.language.override.exception.PLOEntryLanguageIdException;
import com.liferay.portal.language.override.exception.PLOEntryValueException;
import com.liferay.portal.language.override.internal.PLOEntryModelListener;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.model.PLOEntryTable;
import com.liferay.portal.language.override.service.base.PLOEntryLocalServiceBaseImpl;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Drew Brokke
 * @author Thiago Buarque
 */
@Component(
	property = "model.class.name=com.liferay.portal.language.override.model.PLOEntry",
	service = AopService.class
)
public class PLOEntryLocalServiceImpl extends PLOEntryLocalServiceBaseImpl {

	@Override
	public PLOEntry addOrUpdatePLOEntry(
			String externalReferenceCode, long companyId, long userId,
			String key, String languageId, String value)
		throws PortalException {

		languageId = _normalizeLanguageId(languageId);

		_validate(key, languageId, value);

		return _addOrUpdatePLOEntry(
			externalReferenceCode, companyId, userId, key, languageId, value);
	}

	@Override
	public void deletePLOEntries(long companyId, String key) {
		ploEntryPersistence.removeByC_K(companyId, key);
	}

	@Override
	public PLOEntry deletePLOEntry(
		long companyId, String key, String languageId) {

		PLOEntry ploEntry = fetchPLOEntry(companyId, key, languageId);

		if (ploEntry == null) {
			return null;
		}

		return deletePLOEntry(ploEntry);
	}

	@Override
	public PLOEntry deletePLOEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return deletePLOEntry(
			getPLOEntryByExternalReferenceCode(
				externalReferenceCode, companyId));
	}

	@Override
	public PLOEntry fetchPLOEntry(
		long companyId, String key, String languageId) {

		return ploEntryPersistence.fetchByC_K_L(companyId, key, languageId);
	}

	@Override
	public List<PLOEntry> getPLOEntries(long companyId) {
		return ploEntryPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<PLOEntry> getPLOEntries(
		long companyId, int start, int end,
		OrderByComparator<PLOEntry> orderByComparator) {

		return getPLOEntries(companyId, null, start, end, orderByComparator);
	}

	@Override
	public List<PLOEntry> getPLOEntries(long companyId, String languageId) {
		return ploEntryPersistence.findByC_L(companyId, languageId);
	}

	@Override
	public List<PLOEntry> getPLOEntries(
		long companyId, String keywords, int start, int end,
		OrderByComparator<PLOEntry> orderByComparator) {

		return ploEntryPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				PLOEntryTable.INSTANCE
			).from(
				PLOEntryTable.INSTANCE
			).where(
				_getPredicate(companyId, keywords)
			).orderBy(
				orderByStep -> {
					if (orderByComparator == null) {
						return orderByStep.orderBy(
							PLOEntryTable.INSTANCE.key.ascending());
					}

					return orderByStep.orderBy(
						PLOEntryTable.INSTANCE, orderByComparator);
				}
			).limit(
				start, end
			));
	}

	@Override
	public int getPLOEntriesCount(long companyId) {
		return ploEntryPersistence.countByCompanyId(companyId);
	}

	@Override
	public int getPLOEntriesCount(long companyId, String keywords) {
		return ploEntryPersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				PLOEntryTable.INSTANCE
			).where(
				_getPredicate(companyId, keywords)
			));
	}

	@Override
	public void importPLOEntries(
			long companyId, long userId, String languageId,
			Properties properties)
		throws PortalException {

		languageId = _normalizeLanguageId(languageId);

		PLOEntryImportException.InvalidTranslations invalidTranslations = null;

		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			try {
				_validate(
					(String)entry.getKey(), languageId,
					(String)entry.getValue());
			}
			catch (Exception exception) {
				if (invalidTranslations == null) {
					invalidTranslations =
						new PLOEntryImportException.InvalidTranslations();
				}

				invalidTranslations.addSuppressed(exception);
			}
		}

		if (invalidTranslations != null) {
			throw invalidTranslations;
		}

		try (SafeCloseable safeCloseable =
				ClusterInvokeThreadLocal.setEnabledWithSafeCloseable(false)) {

			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				_addOrUpdatePLOEntry(
					null, companyId, userId, (String)entry.getKey(), languageId,
					(String)entry.getValue());
			}
		}

		PLOEntryModelListener.clearCache(_clusterExecutor);
	}

	@Override
	public void setPLOEntries(
			long companyId, long userId, String key,
			Map<Locale, String> localizationMap)
		throws PortalException {

		for (Map.Entry<Locale, String> entry : localizationMap.entrySet()) {
			String languageId = _language.getLanguageId(entry.getKey());
			String value = StringUtil.trim(entry.getValue());

			if ((value == null) || value.equals(StringPool.BLANK)) {
				deletePLOEntry(companyId, key, languageId);
			}
			else {
				addOrUpdatePLOEntry(
					null, companyId, userId, key, languageId, value);
			}
		}
	}

	private PLOEntry _addOrUpdatePLOEntry(
			String externalReferenceCode, long companyId, long userId,
			String key, String languageId, String value)
		throws PortalException {

		PLOEntry ploEntry = null;

		if (Validator.isNotNull(externalReferenceCode)) {
			ploEntry = ploEntryPersistence.fetchByERC_C(
				externalReferenceCode, companyId);
		}

		PLOEntry keyLanguageIdPLOEntry = fetchPLOEntry(
			companyId, key, languageId);

		if (ploEntry != null) {
			if ((keyLanguageIdPLOEntry != null) &&
				(keyLanguageIdPLOEntry.getPloEntryId() !=
					ploEntry.getPloEntryId())) {

				throw new PLOEntryKeyException.MustNotBeDuplicate(
					key, languageId);
			}

			if (Objects.equals(ploEntry.getKey(), key) &&
				Objects.equals(ploEntry.getLanguageId(), languageId) &&
				Objects.equals(ploEntry.getValue(), value)) {

				return ploEntry;
			}

			ploEntry.setKey(key);
			ploEntry.setLanguageId(languageId);
			ploEntry.setValue(value);

			return updatePLOEntry(ploEntry);
		}

		if (keyLanguageIdPLOEntry != null) {
			if (Validator.isNotNull(externalReferenceCode)) {
				keyLanguageIdPLOEntry.setExternalReferenceCode(
					externalReferenceCode);
			}
			else if (Objects.equals(keyLanguageIdPLOEntry.getValue(), value)) {
				return keyLanguageIdPLOEntry;
			}

			keyLanguageIdPLOEntry.setValue(value);

			return updatePLOEntry(keyLanguageIdPLOEntry);
		}

		ploEntry = createPLOEntry(counterLocalService.increment());

		ploEntry.setExternalReferenceCode(externalReferenceCode);
		ploEntry.setCompanyId(companyId);
		ploEntry.setUserId(userId);
		ploEntry.setKey(key);
		ploEntry.setLanguageId(languageId);
		ploEntry.setValue(value);

		return addPLOEntry(ploEntry);
	}

	private Predicate _getPredicate(long companyId, String keywords) {
		return PLOEntryTable.INSTANCE.companyId.eq(
			companyId
		).and(
			() -> {
				if (Validator.isNull(keywords)) {
					return null;
				}

				String[] keywordsArray = _customSQL.keywords(
					keywords, true, WildcardMode.SURROUND);

				return Predicate.withParentheses(
					Predicate.or(
						_customSQL.getKeywordsPredicate(
							DSLFunctionFactoryUtil.lower(
								PLOEntryTable.INSTANCE.key),
							keywordsArray),
						_customSQL.getKeywordsPredicate(
							DSLFunctionFactoryUtil.lower(
								DSLFunctionFactoryUtil.castClobText(
									PLOEntryTable.INSTANCE.value)),
							keywordsArray)));
			}
		);
	}

	private String _normalizeLanguageId(String languageId) {
		languageId = StringUtil.replace(
			languageId, CharPool.DASH, CharPool.UNDERLINE);

		String[] parts = languageId.split(StringPool.UNDERLINE);

		if (parts.length < 2) {
			Locale locale = _language.getLocale(languageId);

			if (locale == null) {
				return languageId;
			}

			return locale.toString();
		}

		languageId =
			StringUtil.lowerCase(parts[0]) + StringPool.UNDERLINE +
				StringUtil.upperCase(parts[1]);

		if (parts.length == 3) {
			return languageId + StringPool.UNDERLINE + parts[2];
		}

		return languageId;
	}

	private void _validate(String key, String languageId, String value)
		throws PortalException {

		if (Validator.isBlank(key)) {
			throw new PLOEntryKeyException.MustNotBeNull();
		}

		int keyMaxLength = ModelHintsUtil.getMaxLength(
			PLOEntry.class.getName(), "key");

		if (key.length() > keyMaxLength) {
			throw new PLOEntryKeyException.MustBeShorter(keyMaxLength);
		}

		if (!ArrayUtil.contains(PropsValues.LOCALES, languageId)) {
			throw new PLOEntryLanguageIdException.MustBeAvailable(
				PropsValues.LOCALES, languageId);
		}

		if (Validator.isBlank(value)) {
			throw new PLOEntryValueException.MustNotBeNull();
		}
	}

	@Reference
	private ClusterExecutor _clusterExecutor;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private Language _language;

}