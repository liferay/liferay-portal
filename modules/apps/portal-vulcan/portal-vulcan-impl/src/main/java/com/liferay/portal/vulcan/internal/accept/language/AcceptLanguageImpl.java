/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.accept.language;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.HttpHeaders;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * @author Cristina González
 */
public class AcceptLanguageImpl implements AcceptLanguage {

	public AcceptLanguageImpl(
		HttpServletRequest httpServletRequest, Language language,
		Portal portal) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_portal = portal;
	}

	@Override
	public List<Locale> getLocales() {
		String acceptLanguage = _httpServletRequest.getHeader(
			HttpHeaders.ACCEPT_LANGUAGE);

		if (Validator.isNull(acceptLanguage)) {
			return Collections.emptyList();
		}

		if (acceptLanguage.equals("zh-Hans-CN")) {
			acceptLanguage = "zh-CN";
		}
		else if (acceptLanguage.equals("zh-Hant-TW")) {
			acceptLanguage = "zh-TW";
		}

		try {
			Company company = _portal.getCompany(_httpServletRequest);

			Set<Locale> companyAvailableLocales =
				_language.getCompanyAvailableLocales(company.getCompanyId());

			List<Locale> locales = Locale.filter(
				Locale.LanguageRange.parse(acceptLanguage),
				companyAvailableLocales);

			locales = TransformUtil.transform(
				locales,
				locale -> {
					for (Locale availableLocale : companyAvailableLocales) {
						if (LocaleUtil.equals(locale, availableLocale)) {
							return availableLocale;
						}
					}

					return null;
				});

			if (ListUtil.isEmpty(locales) &&
				!Objects.equals(
					_httpServletRequest.getMethod(), HttpMethod.GET)) {

				throw new NotAcceptableException(
					"No locales match the accepted languages: " +
						acceptLanguage);
			}

			return locales;
		}
		catch (PortalException portalException) {
			throw new InternalServerErrorException(
				"Unable to get locales: " + portalException.getMessage(),
				portalException);
		}
	}

	@Override
	public String getPreferredLanguageId() {
		return LocaleUtil.toLanguageId(getPreferredLocale());
	}

	@Override
	public Locale getPreferredLocale() {
		List<Locale> locales = getLocales();

		if (ListUtil.isNotEmpty(locales)) {
			return locales.get(0);
		}

		try {
			User user = _portal.initUser(_httpServletRequest);

			return user.getLocale();
		}
		catch (NoSuchUserException noSuchUserException) {
			throw new NotFoundException(
				"Unable to get preferred locale from nonexistent user",
				noSuchUserException);
		}
		catch (Exception exception) {
			throw new InternalServerErrorException(
				"Unable to get preferred locale: " + exception.getMessage(),
				exception);
		}
	}

	@Override
	public boolean isAcceptAllLanguages() {
		String acceptAllLanguages = _httpServletRequest.getHeader(
			"X-Liferay-Accept-All-Languages");

		if (acceptAllLanguages != null) {
			return GetterUtil.getBoolean(acceptAllLanguages);
		}

		return GetterUtil.getBoolean(
			_httpServletRequest.getHeader("X-Accept-All-Languages"));
	}

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;

}