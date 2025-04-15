/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.portlet.action;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Michael C. Han
 */
@ProviderType
public interface PortalSettingsFormContributor {

	public String getDeleteMVCActionCommandName();

	public String getParameterNamespace();

	public String getSaveMVCActionCommandName();

	public String getSettingsId();

	public void validateForm(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException;

}