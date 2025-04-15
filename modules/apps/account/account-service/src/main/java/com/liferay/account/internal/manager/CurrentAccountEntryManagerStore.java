/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.manager;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountWebKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;

import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = CurrentAccountEntryManagerStore.class)
public class CurrentAccountEntryManagerStore {

	public AccountEntry getAccountEntryFromHttpSession(long groupId) {
		HttpSession httpSession = PortalSessionThreadLocal.getHttpSession();

		if (httpSession == null) {
			return null;
		}

		long currentAccountEntryId = GetterUtil.getLong(
			httpSession.getAttribute(_getKey(groupId)));

		return _accountEntryLocalService.fetchAccountEntry(
			currentAccountEntryId);
	}

	public AccountEntry getAccountEntryFromPortalPreferences(
		long groupId, long userId) {

		com.liferay.portal.kernel.model.PortalPreferences
			modelPortalPreferences =
				_portalPreferencesLocalService.fetchPortalPreferences(
					userId, PortletKeys.PREFS_OWNER_TYPE_USER);

		if (modelPortalPreferences == null) {
			return null;
		}

		PortalPreferences portalPreferences =
			_portalPreferenceValueLocalService.getPortalPreferences(
				modelPortalPreferences, false);

		long accountEntryId = GetterUtil.getLong(
			portalPreferences.getValue(
				AccountEntry.class.getName(), _getKey(groupId)));

		if (accountEntryId > 0) {
			return _accountEntryLocalService.fetchAccountEntry(accountEntryId);
		}

		return null;
	}

	public AccountEntry getCurrentAccountEntry(long groupId, long userId)
		throws PortalException {

		AccountEntry accountEntry = getAccountEntryFromHttpSession(groupId);

		if (accountEntry == null) {
			accountEntry = getAccountEntryFromPortalPreferences(
				groupId, userId);
		}

		return accountEntry;
	}

	public void saveInHttpSession(long accountEntryId, long groupId) {
		HttpSession httpSession = PortalSessionThreadLocal.getHttpSession();

		if (httpSession == null) {
			return;
		}

		httpSession.setAttribute(_getKey(groupId), accountEntryId);
	}

	public void saveInPortalPreferences(
		long accountEntryId, long groupId, long userId) {

		PortalPreferences portalPreferences = _getPortalPreferences(userId);

		String key = _getKey(groupId);

		long currentAccountEntryId = GetterUtil.getLong(
			portalPreferences.getValue(
				AccountEntry.class.getName(), key,
				String.valueOf(AccountConstants.ACCOUNT_ENTRY_ID_GUEST)));

		if (currentAccountEntryId == accountEntryId) {
			return;
		}

		portalPreferences.setValue(
			AccountEntry.class.getName(), key, String.valueOf(accountEntryId));

		_portalPreferencesLocalService.updatePreferences(
			userId, PortletKeys.PREFS_OWNER_TYPE_USER, portalPreferences);
	}

	public void setCurrentAccountEntry(
		long accountEntryId, long groupId, long userId) {

		saveInHttpSession(accountEntryId, groupId);
		saveInPortalPreferences(accountEntryId, groupId, userId);
	}

	private String _getKey(long groupId) {
		return AccountWebKeys.CURRENT_ACCOUNT_ENTRY_ID + groupId;
	}

	private PortalPreferences _getPortalPreferences(long userId) {

		// LPS-156201

		try {
			return _portletPreferencesFactory.getPortalPreferences(
				userId, true);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CurrentAccountEntryManagerStore.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	@Reference
	private PortalPreferenceValueLocalService
		_portalPreferenceValueLocalService;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

}