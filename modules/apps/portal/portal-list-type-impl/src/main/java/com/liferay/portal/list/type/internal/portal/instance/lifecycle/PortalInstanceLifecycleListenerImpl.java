/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.list.type.internal.portal.instance.lifecycle;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Ortiz
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class PortalInstanceLifecycleListenerImpl
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstancePreunregistered(Company company)
		throws Exception {

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		_listTypeLocalService.deleteListTypes(company.getCompanyId());
	}

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		try {
			JSONArray listTypesJSONArray = _jsonFactory.createJSONArray(
				StringUtil.read(
					getClassLoader(),
					"com/liferay/portal/list/type/impl/dependencies" +
						"/portal-list-types.json",
					false));

			for (int i = 0; i < listTypesJSONArray.length(); i++) {
				JSONObject listTypeJSONObject =
					listTypesJSONArray.getJSONObject(i);

				try {
					_listTypeLocalService.addListType(
						company.getCompanyId(),
						listTypeJSONObject.getString("name"),
						listTypeJSONObject.getString("type"));
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstanceLifecycleListenerImpl.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

}