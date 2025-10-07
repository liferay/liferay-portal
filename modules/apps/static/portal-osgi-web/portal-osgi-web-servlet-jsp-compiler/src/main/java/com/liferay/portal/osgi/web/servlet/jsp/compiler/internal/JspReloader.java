/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.File;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Matthew Tambara
 */
@Component(service = {})
public class JspReloader {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_bundleContext.addBundleListener(_jspReloadBundleListener);
	}

	@Deactivate
	protected void deactivate() {
		_bundleContext.removeBundleListener(_jspReloadBundleListener);
	}

	private String _toString(BundleEvent bundleEvent) {
		StringBundler sb = new StringBundler(6);

		sb.append("{bundle=");
		sb.append(bundleEvent.getBundle());
		sb.append(", origin=");
		sb.append(bundleEvent.getOrigin());
		sb.append(", type=");

		if (BundleEvent.UPDATED == bundleEvent.getType()) {
			sb.append("UPDATED}");
		}
		else {
			sb.append("UNINSTALLED}");
		}

		return sb.toString();
	}

	private static final String _WORK_DIR = StringBundler.concat(
		PropsValues.LIFERAY_HOME, File.separator, "work", File.separator);

	private static final Log _log = LogFactoryUtil.getLog(JspReloader.class);

	private BundleContext _bundleContext;

	private final BundleListener _jspReloadBundleListener =
		new BundleListener() {

			@Override
			public void bundleChanged(BundleEvent bundleEvent) {
				int type = bundleEvent.getType();

				if ((type != BundleEvent.UPDATED) &&
					(type != BundleEvent.UNINSTALLED)) {

					return;
				}

				Bundle bundle = bundleEvent.getBundle();

				File file = new File(
					_WORK_DIR,
					bundle.getSymbolicName() + StringPool.DASH +
						bundle.getVersion());

				if (file.exists()) {
					FileUtil.deltree(file);

					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Removed Jasper work dir ", file, " on event ",
								_toString(bundleEvent)));
					}
				}
			}

		};

}