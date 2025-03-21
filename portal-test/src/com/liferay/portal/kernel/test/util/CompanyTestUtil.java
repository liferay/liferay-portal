/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.util.PortalInstances;

import jakarta.portlet.PortletPreferences;

import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Manuel de la Peña
 */
public class CompanyTestUtil {

	public static Company addCompany() throws Exception {
		return addCompany(RandomTestUtil.randomString());
	}

	public static Company addCompany(boolean initialize) throws Exception {
		if (!initialize) {
			return addCompany(RandomTestUtil.randomString());
		}

		try {
			return TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					Company company = addCompany(RandomTestUtil.randomString());

					PortalInstances.initCompany(company);

					return company;
				});
		}
		catch (Exception exception) {
			throw exception;
		}
		catch (Throwable throwable) {
			throw new Exception(throwable);
		}
	}

	public static Company addCompany(String name) throws Exception {
		String virtualHostname = name + "." + RandomTestUtil.randomString(3);

		return CompanyLocalServiceUtil.addCompany(
			null, name, virtualHostname, virtualHostname, 0, true, true, null,
			null, null, null, null, null);
	}

	public static void resetCompanyLocales(
			long companyId, Collection<Locale> locales, Locale defaultLocale)
		throws Exception {

		String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

		String languageIds = StringUtil.merge(
			LocaleUtil.toLanguageIds(locales));

		resetCompanyLocales(companyId, languageIds, defaultLanguageId);
	}

	public static void resetCompanyLocales(
			long companyId, String languageIds, String defaultLanguageId)
		throws Exception {

		// Reset company default locale and timezone

		User user = UserLocalServiceUtil.loadGetGuestUser(companyId);

		user.setLanguageId(defaultLanguageId);

		TimeZone timeZone = TimeZoneUtil.getDefault();

		user.setTimeZoneId(timeZone.getID());

		UserLocalServiceUtil.updateUser(user);

		// Reset thread locals

		CompanyThreadLocal.setCompanyId(companyId);

		LocaleThreadLocal.setDefaultLocale(
			LocaleUtil.fromLanguageId(defaultLanguageId, false));

		// Reset company supported locales

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			companyId);

		portletPreferences.setValue(PropsKeys.LOCALES, languageIds);

		portletPreferences.store();

		// Reset company locales cache

		LanguageUtil.resetAvailableLocales(companyId);
	}

	private static final TransactionConfig _transactionConfig;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.REQUIRED);
		builder.setRollbackForClasses(Exception.class);

		_transactionConfig = builder.build();
	}

}