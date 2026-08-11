/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

/**
 * @author Alan Huang
 */
public class JDKPreviewFeaturesCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.endsWith(".properties") && !fileName.endsWith(".sh") &&
			!fileName.endsWith(".xml") && !fileName.endsWith(".yaml") &&
			!fileName.endsWith(".yml")) {

			return content;
		}

		int x = -1;

		while (true) {
			x = content.indexOf("--enable-preview", x + 1);

			if (x == -1) {
				return content;
			}

			addMessage(
				fileName, "Do not use \"--enable-preview\"",
				getLineNumber(content, x));
		}
	}

}