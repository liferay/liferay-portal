/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.site.model.SiteFriendlyURL;
import com.liferay.site.service.base.SiteFriendlyURLLocalServiceBaseImpl;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "model.class.name=com.liferay.site.model.SiteFriendlyURL",
	service = AopService.class
)
public class SiteFriendlyURLLocalServiceImpl
	extends SiteFriendlyURLLocalServiceBaseImpl {

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SiteFriendlyURL deleteSiteFriendlyURL(
		SiteFriendlyURL siteFriendlyURL) {

		return siteFriendlyURLPersistence.remove(siteFriendlyURL);
	}

	@Override
	public SiteFriendlyURL fetchSiteFriendlyURL(
		long companyId, long groupId, String languageId) {

		return siteFriendlyURLPersistence.fetchByG_C_L(
			groupId, companyId, languageId);
	}

	@Override
	public SiteFriendlyURL fetchSiteFriendlyURLByFriendlyURL(
		long companyId, String friendlyURL) {

		return siteFriendlyURLPersistence.fetchByC_F(companyId, friendlyURL);
	}

	@Override
	public List<SiteFriendlyURL> getSiteFriendlyURLs(
		long companyId, long groupId) {

		return siteFriendlyURLPersistence.findByG_C(groupId, companyId);
	}

}