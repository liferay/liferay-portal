/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.portlet.configuration.icon;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashPortletKeys;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.web.internal.display.context.TrashDisplayContext;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TrashPortletKeys.TRASH,
		"path=/view_content.jsp"
	},
	service = PortletConfigurationIcon.class
)
public class RestoreRootTrashPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getIconCssClass() {
		return "restore";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "restore");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			TrashDisplayContext trashDisplayContext = new TrashDisplayContext(
				_portal.getHttpServletRequest(portletRequest),
				_portal.getLiferayPortletRequest(portletRequest),
				_portal.getLiferayPortletResponse(portletResponse));

			return PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					portletRequest, TrashPortletKeys.TRASH,
					PortletRequest.ACTION_PHASE)
			).setActionName(
				"restoreEntries"
			).setRedirect(
				trashDisplayContext.getViewContentRedirectURL()
			).setParameter(
				"trashEntryId", trashDisplayContext.getTrashEntryId()
			).buildString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public double getWeight() {
		return 100.0;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		TrashDisplayContext trashDisplayContext = new TrashDisplayContext(
			_portal.getHttpServletRequest(portletRequest), null, null);

		TrashEntry trashEntry = trashDisplayContext.getTrashEntry();

		if (trashEntry == null) {
			return false;
		}

		TrashHandler trashHandler = trashDisplayContext.getTrashHandler();

		if (trashHandler == null) {
			return false;
		}

		TrashedModel trashedModel = trashHandler.getTrashedModel(
			trashEntry.getClassPK());

		try {
			if (!trashHandler.isRestorable(trashEntry.getClassPK()) ||
				_trashHelper.isInTrashContainer(trashedModel)) {

				return false;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RestoreRootTrashPortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

}