/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeJavaServiceReferenceAnnotationCheck
	extends BaseUpgradeCheck {

	@Override
	protected String afterFormat(
		String fileName, String absolutePath, String content,
		String newContent) {

		return StringUtil.replace(
			newContent,
			"import com.liferay.portal.spring.extender.service." +
				"ServiceReference;",
			"import org.osgi.service.component.annotations.Reference;");
	}

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		for (String annotationBlock :
				JavaSourceUtil.getAnnotationsBlocks(content)) {

			annotationBlock = annotationBlock.trim();

			if (!annotationBlock.startsWith("@ServiceReference")) {
				continue;
			}

			content = StringUtil.replace(
				content, annotationBlock, "@Reference");
		}

		return content;
	}

}