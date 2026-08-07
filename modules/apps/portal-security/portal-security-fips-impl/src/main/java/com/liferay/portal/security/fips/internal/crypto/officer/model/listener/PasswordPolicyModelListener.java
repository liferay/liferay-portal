/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.crypto.officer.model.listener;

import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.RequiredPasswordPolicyException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.fips.constants.FIPSConstants;
import com.liferay.portal.util.PortalInstances;

import org.osgi.service.component.annotations.Component;

/**
 * @author Manuele Castro
 */
@Component(service = ModelListener.class)
public class PasswordPolicyModelListener
	extends BaseModelListener<PasswordPolicy> {

	@Override
	public void onBeforeRemove(PasswordPolicy passwordPolicy)
		throws ModelListenerException {

		if (PortalInstances.isCurrentCompanyInDeletionProcess() ||
			!PropsValues.FIPS_ENABLED ||
			!StringUtil.equals(
				FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER,
				passwordPolicy.getName())) {

			return;
		}

		throw new ModelListenerException(
			new RequiredPasswordPolicyException(
				"Password Policy \"" +
					FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER +
						"\" is required in FIPS mode"));
	}

}