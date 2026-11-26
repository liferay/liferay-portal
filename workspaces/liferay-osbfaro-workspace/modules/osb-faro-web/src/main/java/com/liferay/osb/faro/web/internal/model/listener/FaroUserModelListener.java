/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.listener;

import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.web.internal.helper.WorkspaceSiteHelper;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ModelListener.class)
public class FaroUserModelListener extends BaseModelListener<FaroUser> {

	@Override
	public void onAfterCreate(FaroUser faroUser) throws ModelListenerException {
		_updateUserGroupRoles(faroUser);
	}

	@Override
	public void onAfterUpdate(FaroUser originalFaroUser, FaroUser faroUser)
		throws ModelListenerException {

		_updateUserGroupRoles(faroUser);
	}

	private void _updateUserGroupRoles(FaroUser faroUser) {
		if (!FeatureFlagManagerUtil.isEnabled(
				faroUser.getCompanyId(), "LPD-114293")) {

			return;
		}

		try {
			_workspaceSiteHelper.updateUserGroupRoles(faroUser);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private WorkspaceSiteHelper _workspaceSiteHelper;

}