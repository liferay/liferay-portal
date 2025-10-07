/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutCloneFactory {

	public static LayoutClone getInstance() {
		if (_layoutClone == null) {
			if (Validator.isNotNull(PropsValues.LAYOUT_CLONE_IMPL)) {
				if (_log.isDebugEnabled()) {
					_log.debug("Instantiate " + PropsValues.LAYOUT_CLONE_IMPL);
				}

				ClassLoader classLoader =
					PortalClassLoaderUtil.getClassLoader();

				try {
					Class<?> clazz = classLoader.loadClass(
						PropsValues.LAYOUT_CLONE_IMPL);

					_layoutClone = (LayoutClone)clazz.newInstance();
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				Class<?> clazz = _layoutClone.getClass();

				_log.debug("Return " + clazz.getName());
			}
		}

		return _layoutClone;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutCloneFactory.class);

	private static LayoutClone _layoutClone;

}