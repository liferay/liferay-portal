/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.factory;

import com.liferay.client.extension.type.CET;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.portlet.PortletRequest;

import java.util.Date;
import java.util.Properties;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Iván Zaera Avellón
 */
@ProviderType
public interface CETImplFactory<T extends CET> {

	public T create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties);

	public UnicodeProperties getUnicodeProperties(
		PortletRequest portletRequest);

	public boolean isURLCETPropertyName(String name);

	public void validate(T newCET, T oldCET) throws PortalException;

}