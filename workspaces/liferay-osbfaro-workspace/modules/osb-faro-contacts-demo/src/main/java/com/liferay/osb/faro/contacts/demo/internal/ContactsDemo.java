/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class ContactsDemo extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (Validator.isBlank(FaroPropsValues.FARO_DEMO_CREATOR_METHOD) ||
			StringUtil.equals(
				FaroPropsValues.FARO_DEMO_CREATOR_METHOD, "none")) {

			if (_log.isDebugEnabled()) {
				_log.debug("Skip demo data creation");
			}

			return;
		}

		try {
			FaroProject faroProject =
				_faroProjectLocalService.createFaroProject(0);

			faroProject.setWeDeployKey(
				FaroPropsValues.FARO_DEFAULT_WE_DEPLOY_KEY);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		_naniteDemoCreatorService.createDemo();

		if (_log.isInfoEnabled()) {
			_log.info("Completed demo data creation");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(ContactsDemo.class);

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private NaniteDemoCreatorService _naniteDemoCreatorService;

	@Reference(target = "(jakarta.portlet.name=faro_portlet)")
	private Portlet _portlet;

}