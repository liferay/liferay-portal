/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.service.impl;

import com.liferay.cookies.model.CookiesConsentPreference;
import com.liferay.cookies.service.base.CookiesConsentPreferenceLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Christopher Kian
 */
@Component(
	property = "model.class.name=com.liferay.cookies.model.CookiesConsentPreference",
	service = AopService.class
)
public class CookiesConsentPreferenceLocalServiceImpl
	extends CookiesConsentPreferenceLocalServiceBaseImpl {

	public CookiesConsentPreference addCookiesConsentPreference(
			long userId, String domain, Date expirationDate, String name,
			String value)
		throws PortalException {

		CookiesConsentPreference cookiesConsentPreference =
			cookiesConsentPreferencePersistence.create(
				counterLocalService.increment(
					CookiesConsentPreference.class.getName()));

		User user = _userLocalService.getUser(userId);

		cookiesConsentPreference.setUserId(userId);
		cookiesConsentPreference.setUserName(user.getFullName());

		cookiesConsentPreference.setDomain(domain);
		cookiesConsentPreference.setExpirationDate(expirationDate);
		cookiesConsentPreference.setName(name);
		cookiesConsentPreference.setValue(value);

		return cookiesConsentPreferencePersistence.update(
			cookiesConsentPreference);
	}

	public void deleteCookiesConsentPreference(
			long userId, String domain, String name)
		throws PortalException {

		cookiesConsentPreferencePersistence.removeByU_D_N(userId, domain, name);
	}

	public void deleteCookiesConsentPreferences(long userId) {
		cookiesConsentPreferencePersistence.removeByUserId(userId);
	}

	public void deleteCookiesConsentPreferences(long userId, String domain) {
		cookiesConsentPreferencePersistence.removeByU_D(userId, domain);
	}

	public CookiesConsentPreference getCookiesConsentPreference(
			long userId, String domain, String name)
		throws PortalException {

		return cookiesConsentPreferencePersistence.findByU_D_N(
			userId, domain, name);
	}

	public List<CookiesConsentPreference> getCookiesConsentPreferences(
		long userId, String domain) {

		return cookiesConsentPreferencePersistence.findByU_D(userId, domain);
	}

	public CookiesConsentPreference updateCookiesConsentPreference(
		CookiesConsentPreference cookiesConsentPreference) {

		return cookiesConsentPreferencePersistence.updateImpl(
			cookiesConsentPreference);
	}

	@Reference
	private UserLocalService _userLocalService;

}