/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
public abstract class BaseContentPageEditorMVCActionCommand
	extends BaseMVCActionCommand implements MVCActionCommand {

	protected abstract void doCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception;

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			layoutLockManager.getLock(actionRequest);

			doCommand(actionRequest, actionResponse);

			sendRedirect(actionRequest, actionResponse);
		}
		catch (LockedLayoutException lockedLayoutException) {
			if (_log.isDebugEnabled()) {
				_log.debug(lockedLayoutException);
			}

			sendRedirect(
				actionRequest, actionResponse,
				layoutLockManager.getLockedLayoutURL(actionRequest));
		}
	}

	@Reference
	protected LayoutLockManager layoutLockManager;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseContentPageEditorMVCActionCommand.class);

}