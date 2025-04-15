/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.manager;

import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.service.OrgLaborLocalService;
import com.liferay.portal.kernel.service.OrgLaborService;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;

import java.util.List;

/**
 * @author Samuel Trong Tran
 */
public class OrgLaborContactInfoManager
	extends BaseContactInfoManager<OrgLabor> {

	public OrgLaborContactInfoManager(
		long classPK, OrgLaborLocalService orgLaborLocalService,
		OrgLaborService orgLaborService) {

		_classPK = classPK;
		_orgLaborLocalService = orgLaborLocalService;
		_orgLaborService = orgLaborService;
	}

	@Override
	protected OrgLabor construct(ActionRequest actionRequest) throws Exception {
		long orgLaborId = ParamUtil.getLong(actionRequest, "primaryKey");

		long listTypeId = ParamUtil.getLong(
			actionRequest, "orgLaborListTypeId");
		int sunOpen = ParamUtil.getInteger(actionRequest, "sunOpen", -1);
		int sunClose = ParamUtil.getInteger(actionRequest, "sunClose", -1);
		int monOpen = ParamUtil.getInteger(actionRequest, "monOpen", -1);
		int monClose = ParamUtil.getInteger(actionRequest, "monClose", -1);
		int tueOpen = ParamUtil.getInteger(actionRequest, "tueOpen", -1);
		int tueClose = ParamUtil.getInteger(actionRequest, "tueClose", -1);
		int wedOpen = ParamUtil.getInteger(actionRequest, "wedOpen", -1);
		int wedClose = ParamUtil.getInteger(actionRequest, "wedClose", -1);
		int thuOpen = ParamUtil.getInteger(actionRequest, "thuOpen", -1);
		int thuClose = ParamUtil.getInteger(actionRequest, "thuClose", -1);
		int friOpen = ParamUtil.getInteger(actionRequest, "friOpen", -1);
		int friClose = ParamUtil.getInteger(actionRequest, "friClose", -1);
		int satOpen = ParamUtil.getInteger(actionRequest, "satOpen", -1);
		int satClose = ParamUtil.getInteger(actionRequest, "satClose", -1);

		OrgLabor orgLabor = _orgLaborLocalService.createOrgLabor(orgLaborId);

		orgLabor.setListTypeId(listTypeId);
		orgLabor.setSunOpen(sunOpen);
		orgLabor.setSunClose(sunClose);
		orgLabor.setMonOpen(monOpen);
		orgLabor.setMonClose(monClose);
		orgLabor.setTueOpen(tueOpen);
		orgLabor.setTueClose(tueClose);
		orgLabor.setWedOpen(wedOpen);
		orgLabor.setWedClose(wedClose);
		orgLabor.setThuOpen(thuOpen);
		orgLabor.setThuClose(thuClose);
		orgLabor.setFriOpen(friOpen);
		orgLabor.setFriClose(friClose);
		orgLabor.setSatOpen(satOpen);
		orgLabor.setSatClose(satClose);

		return orgLabor;
	}

	@Override
	protected OrgLabor doAdd(OrgLabor orgLabor) throws Exception {
		return _orgLaborService.addOrgLabor(
			_classPK, orgLabor.getListTypeId(), orgLabor.getSunOpen(),
			orgLabor.getSunClose(), orgLabor.getMonOpen(),
			orgLabor.getMonClose(), orgLabor.getTueOpen(),
			orgLabor.getTueClose(), orgLabor.getWedOpen(),
			orgLabor.getWedClose(), orgLabor.getThuOpen(),
			orgLabor.getThuClose(), orgLabor.getFriOpen(),
			orgLabor.getFriClose(), orgLabor.getSatOpen(),
			orgLabor.getSatClose());
	}

	@Override
	protected void doDelete(long orgLaborId) throws Exception {
		_orgLaborService.deleteOrgLabor(orgLaborId);
	}

	@Override
	protected void doUpdate(OrgLabor orgLabor) throws Exception {
		_orgLaborService.updateOrgLabor(
			orgLabor.getOrgLaborId(), orgLabor.getListTypeId(),
			orgLabor.getSunOpen(), orgLabor.getSunClose(),
			orgLabor.getMonOpen(), orgLabor.getMonClose(),
			orgLabor.getTueOpen(), orgLabor.getTueClose(),
			orgLabor.getWedOpen(), orgLabor.getWedClose(),
			orgLabor.getThuOpen(), orgLabor.getThuClose(),
			orgLabor.getFriOpen(), orgLabor.getFriClose(),
			orgLabor.getSatOpen(), orgLabor.getSatClose());
	}

	@Override
	protected OrgLabor get(long orgLaborId) throws Exception {
		return _orgLaborService.getOrgLabor(orgLaborId);
	}

	@Override
	protected List<OrgLabor> getAll() throws Exception {
		return _orgLaborService.getOrgLabors(_classPK);
	}

	@Override
	protected long getPrimaryKey(OrgLabor orgLabor) {
		return orgLabor.getOrgLaborId();
	}

	@Override
	protected boolean isPrimary(OrgLabor orgLabor) {
		return false;
	}

	@Override
	protected void setPrimary(OrgLabor orgLabor, boolean primary) {
	}

	private final long _classPK;
	private final OrgLaborLocalService _orgLaborLocalService;
	private final OrgLaborService _orgLaborService;

}