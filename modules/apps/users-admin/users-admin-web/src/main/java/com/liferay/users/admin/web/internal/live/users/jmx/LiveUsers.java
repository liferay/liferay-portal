/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.live.users.jmx;

import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.util.concurrent.atomic.AtomicLong;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	enabled = false,
	property = {
		"jmx.objectname=com.liferay.users.admin:classification=live_users,name=LiveUsers",
		"jmx.objectname.cache.key=LiveUsers"
	},
	service = DynamicMBean.class
)
public class LiveUsers extends StandardMBean implements LiveUsersMBean {

	public LiveUsers() throws NotCompliantMBeanException {
		super(LiveUsersMBean.class);
	}

	@Override
	public long getCompanyLiveUsersCount(String webId) {
		return com.liferay.portal.liveusers.LiveUsers.getSessionUsersCount(
			PortalInstancePool.getCompanyId(webId));
	}

	@Override
	public long getServerLiveUsersCount() {
		AtomicLong atomicLong = new AtomicLong();

		_companyLocalService.forEachCompanyId(
			companyId -> atomicLong.addAndGet(
				com.liferay.portal.liveusers.LiveUsers.getSessionUsersCount(
					companyId)));

		return atomicLong.get();
	}

	@Reference
	private CompanyLocalService _companyLocalService;

}