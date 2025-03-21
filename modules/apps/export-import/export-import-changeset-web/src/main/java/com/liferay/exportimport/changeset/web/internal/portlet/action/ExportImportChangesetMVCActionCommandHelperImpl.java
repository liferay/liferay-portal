/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.changeset.web.internal.portlet.action;

import com.liferay.exportimport.changeset.Changeset;
import com.liferay.exportimport.changeset.ChangesetManager;
import com.liferay.exportimport.changeset.portlet.action.ExportImportChangesetMVCActionCommandHelper;
import com.liferay.portal.kernel.util.Constants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(service = ExportImportChangesetMVCActionCommandHelper.class)
public class ExportImportChangesetMVCActionCommandHelperImpl
	extends ExportImportChangesetMVCActionCommand
	implements ExportImportChangesetMVCActionCommandHelper {

	@Override
	public void publish(
			ActionRequest actionRequest, ActionResponse actionResponse,
			Changeset changeset)
		throws Exception {

		_changesetManager.addChangeset(changeset);

		processExportAndPublishAction(
			actionRequest, actionResponse, Constants.PUBLISH,
			changeset.getUuid());
	}

	@Reference
	private ChangesetManager _changesetManager;

}