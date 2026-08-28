/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.util;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Christopher Kian
 */
public class AWSARNUtil {

	public static String resolve(
		String arnTemplate, String awsAccountId, String awsRegion,
		long companyId, String identifier) {

		if (Validator.isNull(arnTemplate) || (identifier == null) ||
			identifier.startsWith("alias/") || identifier.startsWith("arn:")) {

			return identifier;
		}

		if (Validator.isNull(awsAccountId)) {
			if (arnTemplate.contains("{accountId}")) {
				throw new IllegalArgumentException(
					"Unable to resolve AWS account ID for ARN template \"" +
						arnTemplate + "\"");
			}
		}
		else {
			arnTemplate = StringUtil.replace(
				arnTemplate, "{accountId}", awsAccountId);
		}

		arnTemplate = StringUtil.replace(
			arnTemplate, "{companyId}", String.valueOf(companyId));
		arnTemplate = StringUtil.replace(
			arnTemplate, "{identifier}", identifier);

		if (Validator.isNull(awsRegion)) {
			if (arnTemplate.contains("{region}")) {
				throw new IllegalArgumentException(
					"Unable to resolve AWS region for ARN template \"" +
						arnTemplate + "\"");
			}
		}
		else {
			arnTemplate = StringUtil.replace(
				arnTemplate, "{region}", awsRegion);
		}

		return arnTemplate;
	}

}