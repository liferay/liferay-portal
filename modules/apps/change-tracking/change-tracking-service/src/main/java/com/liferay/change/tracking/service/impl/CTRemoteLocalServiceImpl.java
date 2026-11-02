/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.model.CTRemote;
import com.liferay.change.tracking.service.base.CTRemoteLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.model.CTRemote",
	service = AopService.class
)
public class CTRemoteLocalServiceImpl extends CTRemoteLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CTRemote addCTRemote(
			long userId, String name, String description, String url,
			String clientId, String clientSecret)
		throws PortalException {

		long ctRemoteId = counterLocalService.increment(
			CTRemote.class.getName());

		CTRemote ctRemote = ctRemotePersistence.create(ctRemoteId);

		User user = _userLocalService.getUser(userId);

		ctRemote.setCompanyId(user.getCompanyId());

		ctRemote.setUserId(userId);
		ctRemote.setName(name);
		ctRemote.setDescription(description);
		ctRemote.setUrl(url);
		ctRemote.setClientId(clientId);
		ctRemote.setClientSecret(clientSecret);

		ctRemote = ctRemotePersistence.update(ctRemote);

		_resourceLocalService.addResources(
			ctRemote.getCompanyId(), 0, ctRemote.getUserId(),
			CTRemote.class.getName(), ctRemote.getCtRemoteId(), false, false,
			false);

		return ctRemote;
	}

	@Override
	public CTRemote fetchCTRemote(long ctRemoteId) {
		return ctRemotePersistence.fetchByPrimaryKey(ctRemoteId);
	}

	@Override
	public List<CTRemote> getCTRemotes(long companyId) {
		return ctRemotePersistence.findByCompanyId(companyId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CTRemote updateCTRemote(
			long ctRemoteId, String name, String description, String url,
			String clientId, String clientSecret)
		throws PortalException {

		CTRemote ctRemote = ctRemotePersistence.findByPrimaryKey(ctRemoteId);

		ctRemote.setName(name);
		ctRemote.setDescription(description);
		ctRemote.setUrl(url);
		ctRemote.setClientId(clientId);
		ctRemote.setClientSecret(clientSecret);

		return ctRemotePersistence.update(ctRemote);
	}

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}