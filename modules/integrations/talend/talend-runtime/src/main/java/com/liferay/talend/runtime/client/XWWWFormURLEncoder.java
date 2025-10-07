/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.client;

import javax.ws.rs.core.Form;

/**
 * @author Igor Beslic
 */
public class XWWWFormURLEncoder {

	public String encode(String... args) {
		if (args == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < args.length;) {
			sb.append(args[i]);
			sb.append("=");
			sb.append(args[i + 1]);

			i = i + 2;

			if (i < args.length) {
				sb.append("&");
			}
		}

		return sb.toString();
	}

	public Form toForm(String... args) {
		Form form = new Form();

		for (int i = 0; i < args.length;) {
			form.param(args[i], args[i + 1]);

			i = i + 2;
		}

		return form;
	}

}