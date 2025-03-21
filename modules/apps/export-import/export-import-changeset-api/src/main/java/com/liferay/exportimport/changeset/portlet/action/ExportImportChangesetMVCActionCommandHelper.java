/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.changeset.portlet.action;

import com.liferay.exportimport.changeset.Changeset;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Máté Thurzó
 */
@ProviderType
public interface ExportImportChangesetMVCActionCommandHelper {

	public void publish(
			ActionRequest actionRequest, ActionResponse actionResponse,
			Changeset changeset)
		throws Exception;

}