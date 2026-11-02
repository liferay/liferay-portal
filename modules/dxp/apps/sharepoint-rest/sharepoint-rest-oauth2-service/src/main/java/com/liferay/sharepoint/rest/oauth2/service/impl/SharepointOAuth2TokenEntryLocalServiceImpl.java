/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.oauth2.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry;
import com.liferay.sharepoint.rest.oauth2.service.base.SharepointOAuth2TokenEntryLocalServiceBaseImpl;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry",
	service = AopService.class
)
public class SharepointOAuth2TokenEntryLocalServiceImpl
	extends SharepointOAuth2TokenEntryLocalServiceBaseImpl {

	@Override
	public SharepointOAuth2TokenEntry addSharepointOAuth2TokenEntry(
			long userId, String configurationPid, String accessToken,
			String refreshToken, Date expirationDate)
		throws PortalException {

		SharepointOAuth2TokenEntry sharepointOAuth2TokenEntry =
			sharepointOAuth2TokenEntryPersistence.fetchByU_C(
				userId, configurationPid);

		if (sharepointOAuth2TokenEntry != null) {
			sharepointOAuth2TokenEntry.setAccessToken(accessToken);
			sharepointOAuth2TokenEntry.setExpirationDate(expirationDate);
			sharepointOAuth2TokenEntry.setRefreshToken(refreshToken);

			return sharepointOAuth2TokenEntryPersistence.update(
				sharepointOAuth2TokenEntry);
		}

		long sharepointOAuth2TokenEntryId = counterLocalService.increment();

		sharepointOAuth2TokenEntry =
			sharepointOAuth2TokenEntryPersistence.create(
				sharepointOAuth2TokenEntryId);

		sharepointOAuth2TokenEntry.setUserId(userId);

		User user = _userLocalService.getUser(userId);

		sharepointOAuth2TokenEntry.setUserName(user.getFullName());

		sharepointOAuth2TokenEntry.setCreateDate(new Date());
		sharepointOAuth2TokenEntry.setAccessToken(accessToken);
		sharepointOAuth2TokenEntry.setConfigurationPid(configurationPid);
		sharepointOAuth2TokenEntry.setExpirationDate(expirationDate);
		sharepointOAuth2TokenEntry.setRefreshToken(refreshToken);

		return sharepointOAuth2TokenEntryPersistence.update(
			sharepointOAuth2TokenEntry);
	}

	@Override
	public void deleteSharepointOAuth2TokenEntry(
			long userId, String configurationPid)
		throws PortalException {

		sharepointOAuth2TokenEntryPersistence.removeByU_C(
			userId, configurationPid);
	}

	@Override
	public void deleteUserSharepointOAuth2TokenEntries(long userId) {
		sharepointOAuth2TokenEntryPersistence.removeByUserId(userId);
	}

	@Override
	public SharepointOAuth2TokenEntry fetchSharepointOAuth2TokenEntry(
		long userId, String configurationPid) {

		return sharepointOAuth2TokenEntryPersistence.fetchByU_C(
			userId, configurationPid);
	}

	@Override
	public int getUserSharepointOAuth2TokenEntriesCount(long userId) {
		return sharepointOAuth2TokenEntryPersistence.countByUserId(userId);
	}

	@Reference
	private UserLocalService _userLocalService;

}