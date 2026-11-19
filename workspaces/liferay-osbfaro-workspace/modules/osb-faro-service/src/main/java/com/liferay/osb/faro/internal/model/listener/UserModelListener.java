/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.internal.model.listener;

import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sillas Cavalcanti
 */
@Component(service = ModelListener.class)
public class UserModelListener extends BaseModelListener<User> {

	@Override
	public void onAfterRemove(User user) throws ModelListenerException {
		try {
			_faroUserLocalService.deleteFaroUsersByLiveUserId(user.getUserId());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to delete the faro users of user " + user.getUserId(),
				portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserModelListener.class);

	@Reference
	private FaroUserLocalService _faroUserLocalService;

}