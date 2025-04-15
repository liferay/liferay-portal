/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import java.util.concurrent.FutureTask;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(service = {})
public class ContactsDemo {

	@Activate
	protected void activate() {
		if (Validator.isBlank(FaroPropsValues.FARO_DEMO_CREATOR_METHOD) ||
			StringUtil.equals(
				FaroPropsValues.FARO_DEMO_CREATOR_METHOD, "none")) {

			if (_log.isDebugEnabled()) {
				_log.debug("Skip demo data creation");
			}

			return;
		}

		_futureTask = new FutureTask<>(
			() -> {
				long startTime = System.currentTimeMillis();

				while ((System.currentTimeMillis() - startTime) <
							(Time.MINUTE * 5)) {

					try {
						FaroProject faroProject =
							_faroProjectLocalService.createFaroProject(0);

						faroProject.setWeDeployKey(
							FaroPropsValues.FARO_DEFAULT_WE_DEPLOY_KEY);

						break;
					}
					catch (Exception exception) {
						_log.error(exception);

						Thread.sleep(Time.SECOND * 30);
					}
				}

				if (StringUtil.equals(
						FaroPropsValues.FARO_DEMO_CREATOR_METHOD, "nanite")) {

					_naniteDemoCreatorService.createDemo();
				}
				else {
					_snapshotDemoCreatorService.createDemo();
				}

				if (_log.isInfoEnabled()) {
					_log.info("Completed demo data creation");
				}

				return null;
			});

		Thread thread = new Thread(
			_futureTask, "Contacts Demo Creation Thread");

		thread.setDaemon(true);

		thread.start();
	}

	@Deactivate
	protected void deactivate() {
		if (_futureTask != null) {
			_futureTask.cancel(true);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(ContactsDemo.class);

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	private FutureTask<Void> _futureTask;

	@Reference
	private NaniteDemoCreatorService _naniteDemoCreatorService;

	@Reference(target = "(jakarta.portlet.name=faro_portlet)")
	private Portlet _portlet;

	@Reference
	private SnapshotDemoCreatorService _snapshotDemoCreatorService;

}