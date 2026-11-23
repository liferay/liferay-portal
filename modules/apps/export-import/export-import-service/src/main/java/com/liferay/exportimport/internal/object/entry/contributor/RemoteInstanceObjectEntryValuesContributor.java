/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.object.entry.contributor;

import com.liferay.exportimport.internal.constants.RemoteInstanceConstants;
import com.liferay.object.entry.ObjectEntryContext;
import com.liferay.object.entry.contributor.ObjectEntryValuesContributor;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.KeyReferenceUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = ObjectEntryValuesContributor.class)
public class RemoteInstanceObjectEntryValuesContributor
	implements ObjectDefinitionScoped, ObjectEntryValuesContributor {

	@Override
	public void contribute(ObjectEntryContext objectEntryContext) {
		Map<String, Serializable> values = objectEntryContext.getValues();

		String clientSecret = MapUtil.getString(values, "clientSecret");

		if (Validator.isNull(clientSecret) ||
			StringUtil.startsWith(
				clientSecret, RemoteInstanceConstants.CLIENT_SECRET_PREFIX) ||
			KeyReferenceUtil.isKeyReference(clientSecret)) {

			return;
		}

		try {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					objectEntryContext.getObjectDefinitionId());

			Company company = _companyLocalService.getCompany(
				objectDefinition.getCompanyId());

			String encryptedClientSecret = _encryptor.encryptAuthenticated(
				company.getKeyObj(), clientSecret);

			values.put(
				"clientSecret",
				RemoteInstanceConstants.CLIENT_SECRET_PREFIX +
					encryptedClientSecret);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public List<String> getAllowedObjectDefinitionNames() {
		return Collections.singletonList("RemoteInstance");
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private Encryptor _encryptor;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}