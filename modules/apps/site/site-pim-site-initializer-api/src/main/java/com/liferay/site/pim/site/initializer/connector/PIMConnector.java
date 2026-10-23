/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.connector;

import com.liferay.object.model.ObjectEntry;

import java.util.List;
import java.util.Locale;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
@ProviderType
public interface PIMConnector {

	public String execute(ObjectEntry objectEntry) throws Exception;

	public String getKey();

	public String getName(Locale locale);

	public List<PIMConnectorChannelField> getPIMConnectorChannelFields();

	public String getSettingsObjectDefinitionExternalReferenceCode();

	public boolean isActive(long companyId);

}