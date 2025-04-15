/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.servlet;

import com.liferay.portal.kernel.util.ServerDetector;

import jakarta.servlet.jsp.JspFactory;

/**
 * @author Shuyang Zhou
 */
public class JspFactorySwapper {

	public static void swap() {
		if (!ServerDetector.isTomcat()) {
			return;
		}

		JspFactory jspFactory = JspFactory.getDefaultFactory();

		if (jspFactory instanceof JspFactoryWrapper) {
			return;
		}

		synchronized (JspFactorySwapper.class) {
			if (_jspFactoryWrapper == null) {
				_jspFactoryWrapper = new JspFactoryWrapper(jspFactory);
			}

			JspFactory.setDefaultFactory(_jspFactoryWrapper);
		}
	}

	private static JspFactoryWrapper _jspFactoryWrapper;

}