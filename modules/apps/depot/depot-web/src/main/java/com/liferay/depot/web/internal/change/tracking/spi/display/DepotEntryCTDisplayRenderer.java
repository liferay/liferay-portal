/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brooke Dalton
 */
@Component(service = CTDisplayRenderer.class)
public class DepotEntryCTDisplayRenderer
	extends BaseCTDisplayRenderer<DepotEntry> {

	@Override
	public String[] getAvailableLanguageIds(DepotEntry depotEntry) {
		try {
			Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

			return group.getAvailableLanguageIds();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	@Override
	public String getDefaultLanguageId(DepotEntry depotEntry) {
		try {
			Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

			return group.getDefaultLanguageId();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, null, DepotPortletKeys.DEPOT_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/depot/edit_depot_entry"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"depotEntryId", depotEntry.getDepotEntryId()
		).buildString();
	}

	@Override
	public Class<DepotEntry> getModelClass() {
		return DepotEntry.class;
	}

	@Override
	public String getTitle(Locale locale, DepotEntry depotEntry)
		throws PortalException {

		Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

		return group.getName(locale);
	}

	@Override
	public String getTypeName(Locale locale) {
		return _language.get(locale, "asset-library");
	}

	@Override
	protected void buildDisplay(DisplayBuilder<DepotEntry> displayBuilder)
		throws PortalException {

		DepotEntry depotEntry = displayBuilder.getModel();

		displayBuilder.display(
			"name",
			() -> {
				Group group = depotEntry.getGroup();

				return group.getName(displayBuilder.getLocale());
			}
		).display(
			"description",
			() -> {
				Group group = depotEntry.getGroup();

				return group.getDescription(displayBuilder.getLocale());
			}
		).display(
			"user",
			() -> {
				User user = _userLocalService.getUser(depotEntry.getUserId());

				return user.getFullName();
			}
		).display(
			"create-date", depotEntry.getCreateDate()
		).display(
			"modified-date",
			() -> {
				Date date = depotEntry.getModifiedDate();

				if (date != null) {
					return date;
				}

				return null;
			}
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DepotEntryCTDisplayRenderer.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}