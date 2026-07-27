/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.portal.instance.lifecycle.InitialRequestPortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.concurrent.FutureTask;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class ContactsDemo
	extends InitialRequestPortalInstanceLifecycleListener {

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Override
	protected void doPortalInstanceRegistered(long companyId) {
		if (Validator.isBlank(FaroPropsValues.FARO_DEMO_CREATOR_METHOD) ||
			StringUtil.equals(
				FaroPropsValues.FARO_DEMO_CREATOR_METHOD, "none")) {

			if (_log.isDebugEnabled()) {
				_log.debug("Skip demo data creation");
			}

			return;
		}

		new Thread(
			new FutureTask<>(
				new CompanyInheritableThreadLocalCallable<>(
					() -> {
						try {
							FaroProject faroProject =
								_faroProjectLocalService.createFaroProject(0);

							faroProject.setWeDeployKey(
								FaroPropsValues.FARO_DEFAULT_WE_DEPLOY_KEY);
						}
						catch (Exception exception) {
							_log.error(exception);
						}

						try {
							_naniteDemoCreatorService.createDemo();

							if (_log.isInfoEnabled()) {
								_log.info("Completed demo data creation");
							}
						}
						catch (Exception exception) {
							_log.error(exception);
						}

						return null;
					})),
			"Faro Contacts Demo"
		).start();
	}

	private static final Log _log = LogFactoryUtil.getLog(ContactsDemo.class);

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private NaniteDemoCreatorService _naniteDemoCreatorService;

	@Reference(target = "(jakarta.portlet.name=faro_portlet)")
	private Portlet _portlet;

}