/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.test.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.PropsValues;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDate;
import java.time.ZoneOffset;

import java.util.List;

/**
 * @author Jorge García Jiménez
 */
public class FIPSTestUtil {

	public static List<JSONObject> getAuditLogJSONObjects() throws Exception {
		return TransformUtil.unsafeTransform(
			Files.readAllLines(getAuditLogPath()),
			JSONFactoryUtil::createJSONObject);
	}

	public static Path getAuditLogPath() {
		LocalDate localDate = LocalDate.now(ZoneOffset.UTC);

		return Paths.get(
			PropsValues.LIFERAY_HOME, "logs",
			StringBundler.concat("fips-audit.", localDate, ".ndjson"));
	}

}