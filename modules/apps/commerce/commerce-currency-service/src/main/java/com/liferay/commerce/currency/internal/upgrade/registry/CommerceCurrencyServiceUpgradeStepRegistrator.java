/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.internal.upgrade.registry;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = UpgradeStepRegistrator.class)
public class CommerceCurrencyServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		if (_log.isInfoEnabled()) {
			_log.info("Commerce currency upgrade step registrator started");
		}

		registry.register(
			"1.0.0", "1.1.0",
			UpgradeProcessFactory.dropColumns("CommerceCurrency", "groupId"));

		registry.register(
			"1.1.0", "1.2.0",
			UpgradeProcessFactory.addColumns(
				"CommerceCurrency", "VARCHAR(75)", "symbol"));

		registry.register(
			"1.2.0", "1.3.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"CommerceCurrency"};
				}

			});

		registry.register(
			"1.3.0", "1.4.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"CommerceCurrency"};
				}

			});

		if (_log.isInfoEnabled()) {
			_log.info("Commerce currency upgrade step registrator finished");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCurrencyServiceUpgradeStepRegistrator.class);

}