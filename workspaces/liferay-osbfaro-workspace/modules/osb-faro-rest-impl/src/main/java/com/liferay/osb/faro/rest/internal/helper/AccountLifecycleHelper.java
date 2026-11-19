/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.helper;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.exception.NoSuchEntryException;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.internal.jaxrs.util.ProblemUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Caio Pinheiro
 */
@Component(service = AccountLifecycleHelper.class)
public class AccountLifecycleHelper {

	public AccountLifecycle getAccountLifecycle(
		String accountLifecycleId, String channelId, FaroProject faroProject) {

		try {
			AccountLifecycle accountLifecycle =
				_contactsEngineClient.getAccountLifecycle(
					faroProject, accountLifecycleId);

			Results<AccountLifecycle> results =
				_contactsEngineClient.getAccountLifecycles(
					faroProject, GetterUtil.getLong(channelId), null,
					accountLifecycle.getExternalReferenceCode(), null, 1, 1,
					null);

			for (AccountLifecycle channelAccountLifecycle :
					results.getItems()) {

				if (Objects.equals(
						channelAccountLifecycle.getId(),
						accountLifecycle.getId())) {

					return accountLifecycle;
				}
			}

			throw ProblemUtil.getNotFoundException(
				_getNotFoundExceptionTitle(accountLifecycleId));
		}
		catch (FaroEngineClientException faroEngineClientException) {
			if ((faroEngineClientException instanceof NoSuchEntryException) ||
				(faroEngineClientException.getStatus() ==
					HttpServletResponse.SC_BAD_REQUEST)) {

				throw ProblemUtil.getNotFoundException(
					_getNotFoundExceptionTitle(accountLifecycleId));
			}

			throw faroEngineClientException;
		}
	}

	private String _getNotFoundExceptionTitle(String accountLifecycleId) {
		return "No account lifecycle was found with ID " + accountLifecycleId;
	}

	@Reference
	private ContactsEngineClient _contactsEngineClient;

}