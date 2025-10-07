/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.osgi.commands;

import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.store.StoreAreaProcessor;
import com.liferay.document.library.service.DLStorageQuotaLocalService;
import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.time.Duration;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"osgi.command.function=cleanUp", "osgi.command.function=update",
		"osgi.command.scope=documentLibrary"
	},
	service = OSGiCommands.class
)
public class DocumentLibraryOSGiCommands implements OSGiCommands {

	public void cleanUp(long companyId) {
		StoreAreaProcessor storeAreaProcessor = _serviceTracker.getService();

		if (storeAreaProcessor == null) {
			System.out.println(
				"Do nothing because the selected store " +
					PropsValues.DL_STORE_IMPL +
						" does not support store areas.");

			return;
		}

		storeAreaProcessor.cleanUpDeletedStoreArea(
			companyId, Integer.MAX_VALUE,
			name -> !_isDLFileVersionReferenced(companyId, name),
			StringPool.BLANK, Duration.ofSeconds(1));
		storeAreaProcessor.cleanUpNewStoreArea(
			companyId, Integer.MAX_VALUE,
			name -> !_isDLFileVersionReferenced(companyId, name),
			StringPool.BLANK, Duration.ofSeconds(1));
	}

	public void update(String... companyIds) {
		for (String companyId : companyIds) {
			try {
				_dlStorageQuotaLocalService.updateStorageSize(
					GetterUtil.getLong(companyId));

				System.out.printf(
					"Successfully updated document library storage quota for " +
						"company %s%n",
					companyId);
			}
			catch (Exception exception) {
				_log.error(exception);

				System.out.printf(
					"Unable to update document library storage quota for " +
						"company %s. See server log for more details.%n",
					companyId);
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext)
		throws InvalidSyntaxException {

		_serviceTracker = new ServiceTracker<>(
			bundleContext,
			bundleContext.createFilter(
				StringBundler.concat(
					"(&(objectClass=", StoreAreaProcessor.class.getName(),
					")(store.type=", PropsValues.DL_STORE_IMPL, "))")),
			null);

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private boolean _isDLFileVersionReferenced(Long companyId, String name) {
		int index = name.lastIndexOf(StringPool.TILDE);

		if (index == -1) {
			return true;
		}

		int count = _dlFileVersionLocalService.getFileVersionsCount(
			companyId, name.substring(index + 1));

		if (count > 0) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DocumentLibraryOSGiCommands.class);

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private DLStorageQuotaLocalService _dlStorageQuotaLocalService;

	private ServiceTracker<StoreAreaProcessor, StoreAreaProcessor>
		_serviceTracker;

}