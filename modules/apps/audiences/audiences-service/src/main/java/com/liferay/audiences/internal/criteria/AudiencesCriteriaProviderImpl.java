/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.internal.criteria;

import com.liferay.audiences.constants.AudiencesCriteriaKeys;
import com.liferay.audiences.constants.AudiencesCriteriaTypeKeys;
import com.liferay.audiences.criteria.AudiencesCriteria;
import com.liferay.audiences.criteria.AudiencesCriteriaBuilder;
import com.liferay.audiences.criteria.AudiencesCriteriaProvider;
import com.liferay.audiences.criteria.AudiencesCriteriaType;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.AudiencesCustomAttributesCET;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.service.SegmentsEntryLocalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = AudiencesCriteriaProvider.class)
public class AudiencesCriteriaProviderImpl
	implements AudiencesCriteriaProvider {

	@Override
	public List<AudiencesCriteriaType> getAudiencesCriteriaTypes(
		long companyId, Locale locale) {

		List<AudiencesCriteriaType> audiencesCriteriaTypes = new ArrayList<>();

		audiencesCriteriaTypes.add(
			_getBrowserAttributesAudiencesCriteriaType(locale));
		audiencesCriteriaTypes.add(
			_getGeneralAttributesAudiencesCriteriaType(locale));

		AudiencesCriteriaType customAudiencesCriteriaType =
			_getCustomAudiencesCriteriaType(companyId, locale);

		if (customAudiencesCriteriaType != null) {
			audiencesCriteriaTypes.add(customAudiencesCriteriaType);
		}

		return audiencesCriteriaTypes;
	}

	private AudiencesCriteriaType _getBrowserAttributesAudiencesCriteriaType(
		Locale locale) {

		return new AudiencesCriteriaType(
			Arrays.asList(
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.BROWSER_NAME
				).setLabel(
					_language.get(locale, "browser-name")
				).setType(
					AudiencesCriteria.Type.STRING
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.BROWSER_VERSION
				).setLabel(
					_language.get(locale, "browser-version")
				).setType(
					AudiencesCriteria.Type.STRING
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"braces"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.COOKIES
				).setLabel(
					_language.get(locale, "cookies")
				).setType(
					AudiencesCriteria.Type.SET
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.DEVICE_TYPE
				).setLabel(
					_language.get(locale, "device-type")
				).setType(
					AudiencesCriteria.Type.STRING
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.GEOLOCATION
				).setLabel(
					_language.get(locale, "geolocation")
				).setType(
					AudiencesCriteria.Type.STRING
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.HOSTNAME
				).setLabel(
					_language.get(locale, "hostname")
				).setType(
					AudiencesCriteria.Type.STRING
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.SELECT
				).setKey(
					AudiencesCriteriaKeys.LANGUAGE
				).setLabel(
					_language.get(locale, "language")
				).setOptions(
					_getLanguageOptions(locale)
				).setType(
					AudiencesCriteria.Type.STRING
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"date"
				).setInputType(
					AudiencesCriteria.InputType.DATE
				).setKey(
					AudiencesCriteriaKeys.LOCAL_DATE
				).setLabel(
					_language.get(locale, "local-date")
				).setType(
					AudiencesCriteria.Type.STRING
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"time"
				).setInputType(
					AudiencesCriteria.InputType.SELECT
				).setKey(
					AudiencesCriteriaKeys.LOCAL_HOUR
				).setLabel(
					_language.get(locale, "local-hour")
				).setOptions(
					_getLocalHourOptions()
				).setType(
					AudiencesCriteria.Type.NUMBER
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.PATHNAME
				).setLabel(
					_language.get(locale, "pathname")
				).setType(
					AudiencesCriteria.Type.STRING
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.REFERRER
				).setLabel(
					_language.get(locale, "referrer-url")
				).setType(
					AudiencesCriteria.Type.STRING
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"braces"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.REQUEST_PARAMETERS
				).setLabel(
					_language.get(locale, "request-parameters")
				).setType(
					AudiencesCriteria.Type.SET
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.TIMEZONE
				).setLabel(
					_language.get(locale, "time-zone")
				).setType(
					AudiencesCriteria.Type.STRING
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.URL
				).setLabel(
					_language.get(locale, "url")
				).setType(
					AudiencesCriteria.Type.STRING
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.TEXT
				).setKey(
					AudiencesCriteriaKeys.USER_AGENT
				).setLabel(
					_language.get(locale, "user-agent")
				).setType(
					AudiencesCriteria.Type.STRING
				).build()),
			AudiencesCriteriaTypeKeys.BROWSER_ATTRIBUTES,
			_language.get(
				locale, AudiencesCriteriaTypeKeys.BROWSER_ATTRIBUTES));
	}

	private AudiencesCriteriaType _getCustomAudiencesCriteriaType(
		long companyId, Locale locale) {

		try {
			List<AudiencesCriteria> audiencesCriterias = new ArrayList<>();

			List<AudiencesCriteria.Option> segmentsOptions =
				TransformUtil.transform(
					_segmentsEntryLocalService.getSegmentsEntriesBySource(
						companyId,
						SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
					segmentsEntry -> new AudiencesCriteria.Option(
						segmentsEntry.getName(locale),
						segmentsEntry.getExternalReferenceCode()));

			if (!segmentsOptions.isEmpty()) {
				audiencesCriterias.add(
					AudiencesCriteriaBuilder.setIcon(
						"users"
					).setInputType(
						AudiencesCriteria.InputType.SELECT
					).setKey(
						AudiencesCriteriaKeys.SEGMENTS
					).setLabel(
						_language.get(locale, "segments")
					).setOptions(
						segmentsOptions
					).setType(
						AudiencesCriteria.Type.STRING
					).build());
			}

			List<CET> cets = _cetManager.getCETs(
				companyId, null,
				ClientExtensionEntryConstants.TYPE_AUDIENCES_CUSTOM_ATTRIBUTES,
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null);

			for (CET cet : cets) {
				AudiencesCustomAttributesCET audiencesCustomAttributesCET =
					(AudiencesCustomAttributesCET)cet;

				String[] names = StringUtil.split(
					audiencesCustomAttributesCET.getNames(), CharPool.NEW_LINE);
				String[] symbols = StringUtil.split(
					audiencesCustomAttributesCET.getSymbols(),
					CharPool.NEW_LINE);
				String[] types = StringUtil.split(
					audiencesCustomAttributesCET.getTypes(), CharPool.NEW_LINE);

				for (int i = 0; i < symbols.length; i++) {
					AudiencesCriteria.Type type = AudiencesCriteria.Type.parse(
						types[i]);

					audiencesCriterias.add(
						AudiencesCriteriaBuilder.setIcon(
							"cog"
						).setInputType(
							_getInputType(type)
						).setKey(
							StringBundler.concat(
								"custom:",
								audiencesCustomAttributesCET.getURL(),
								StringPool.POUND, symbols[i])
						).setLabel(
							names[i]
						).setType(
							type
						).build());
				}
			}

			if (audiencesCriterias.isEmpty()) {
				return null;
			}

			return new AudiencesCriteriaType(
				audiencesCriterias, AudiencesCriteriaTypeKeys.CUSTOM,
				_language.get(locale, AudiencesCriteriaTypeKeys.CUSTOM));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	private AudiencesCriteriaType _getGeneralAttributesAudiencesCriteriaType(
		Locale locale) {

		return new AudiencesCriteriaType(
			Arrays.asList(
				AudiencesCriteriaBuilder.setIcon(
					"check"
				).setInputType(
					AudiencesCriteria.InputType.BOOLEAN
				).setKey(
					AudiencesCriteriaKeys.USER_AUTHENTICATION
				).setLabel(
					_language.get(locale, "user-authentication")
				).setType(
					AudiencesCriteria.Type.BOOLEAN
				).build(),
				AudiencesCriteriaBuilder.setIcon(
					"text"
				).setInputType(
					AudiencesCriteria.InputType.SELECT
				).setKey(
					AudiencesCriteriaKeys.USER_LANGUAGE
				).setLabel(
					_language.get(locale, "user-language")
				).setOptions(
					_getLanguageOptions(locale)
				).setType(
					AudiencesCriteria.Type.STRING
				).build()),
			AudiencesCriteriaTypeKeys.GENERAL,
			_language.get(locale, AudiencesCriteriaTypeKeys.GENERAL));
	}

	private AudiencesCriteria.InputType _getInputType(
		AudiencesCriteria.Type type) {

		if (type == AudiencesCriteria.Type.BOOLEAN) {
			return AudiencesCriteria.InputType.BOOLEAN;
		}

		return AudiencesCriteria.InputType.TEXT;
	}

	private List<AudiencesCriteria.Option> _getLanguageOptions(Locale locale) {
		return TransformUtil.transform(
			_language.getAvailableLocales(),
			availableLocale -> new AudiencesCriteria.Option(
				availableLocale.getDisplayName(locale),
				_language.getBCP47LanguageId(availableLocale)));
	}

	private List<AudiencesCriteria.Option> _getLocalHourOptions() {
		List<AudiencesCriteria.Option> options = new ArrayList<>(24);

		for (int hour = 0; hour < 24; hour++) {
			options.add(
				new AudiencesCriteria.Option(
					String.format("%02d:00", hour), String.valueOf(hour)));
		}

		return options;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AudiencesCriteriaProviderImpl.class);

	@Reference
	private CETManager _cetManager;

	@Reference
	private Language _language;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

}