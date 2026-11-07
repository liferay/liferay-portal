/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.model.listener;

import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.user.associated.data.web.internal.util.AnonymousUserConfigurationUtil;

import java.io.IOException;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = ModelListener.class)
public class UserModelListener extends BaseModelListener<User> {

	@Override
	public void onAfterRemove(User user) throws ModelListenerException {
		try {
			_deleteAnonymousUserConfiguration(
				user.getCompanyId(), user.getUserId());
		}
		catch (InvalidSyntaxException | IOException exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _deleteAnonymousUserConfiguration(long companyId, long userId)
		throws InvalidSyntaxException, IOException {

		Configuration configuration = AnonymousUserConfigurationUtil.get(
			companyId, _configurationAdmin, userId);

		if (configuration == null) {
			return;
		}

		configuration.delete();
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

}