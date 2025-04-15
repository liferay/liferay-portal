/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.portlet.configuration.icon;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.trash.constants.TrashPortletKeys;
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
public class DeleteTrashPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getIconCssClass() {
		return "trash";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "delete");
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
				"deleteEntries"
			).setRedirect(
				trashDisplayContext.getViewContentRedirectURL()
			).setParameter(
				"className", trashDisplayContext.getClassName()
			).setParameter(
				"classPK", trashDisplayContext.getClassPK()
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
		return 200.0;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		if (!CTCollectionThreadLocal.isProductionMode()) {
			return false;
		}

		TrashDisplayContext trashDisplayContext = new TrashDisplayContext(
			_portal.getHttpServletRequest(portletRequest), null, null);

		TrashHandler trashHandler = trashDisplayContext.getTrashHandler();

		if ((trashHandler == null) || trashHandler.isContainerModel()) {
			return false;
		}

		try {
			if (!trashHandler.isDeletable(trashDisplayContext.getClassPK())) {
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
		DeleteTrashPortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}