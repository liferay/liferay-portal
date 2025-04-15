/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.display.context;

import com.liferay.depot.application.DepotApplication;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryServiceUtil;
import com.liferay.depot.web.internal.application.controller.DepotApplicationController;
import com.liferay.depot.web.internal.constants.DepotAdminWebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;

import jakarta.portlet.PortletRequest;

import java.util.Collection;
import java.util.Locale;

/**
 * @author Cristina González
 */
public class DepotAdminDetailsDisplayContext {

	public DepotAdminDetailsDisplayContext(
		DepotApplicationController depotApplicationController,
		PortletRequest portletRequest) {

		_depotApplicationController = depotApplicationController;
		_portletRequest = portletRequest;
	}

	public Collection<DepotApplication> getDepotApplications() {
		return _depotApplicationController.getCustomizableDepotApplications();
	}

	public long getDepotEntryId() {
		DepotEntry depotEntry = (DepotEntry)_portletRequest.getAttribute(
			DepotAdminWebKeys.DEPOT_ENTRY);

		return depotEntry.getDepotEntryId();
	}

	public String getDepotName(Locale locale) throws PortalException {
		Group group = getGroup();

		return group.getName(locale);
	}

	public Group getGroup() throws PortalException {
		if (_group == null) {
			DepotEntry depotEntry = DepotEntryServiceUtil.getDepotEntry(
				getDepotEntryId());

			_group = depotEntry.getGroup();
		}

		return _group;
	}

	public long getGroupId() {
		DepotEntry depotEntry = (DepotEntry)_portletRequest.getAttribute(
			DepotAdminWebKeys.DEPOT_ENTRY);

		return depotEntry.getGroupId();
	}

	public boolean isEnabled(String portletId) throws PortalException {
		return _depotApplicationController.isEnabled(portletId, _getGroupId());
	}

	private long _getGroupId() throws PortalException {
		Group group = getGroup();

		return group.getGroupId();
	}

	private final DepotApplicationController _depotApplicationController;
	private Group _group;
	private final PortletRequest _portletRequest;

}