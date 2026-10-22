/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.batch.engine;

import com.liferay.batch.engine.BatchEngineContentProcessor;
import com.liferay.headless.admin.site.internal.batch.engine.util.LayoutSetPrototypeMergeUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"field.name=externalReferenceCode",
		"field.name=parentSitePageExternalReferenceCode",
		"model.class.name=com.liferay.headless.admin.site.dto.v1_0.SitePage"
	},
	service = BatchEngineContentProcessor.class
)
public class
	LayoutSetPrototypeMergeSitePageExternalReferenceCodeBatchEngineContentProcessor
		implements BatchEngineContentProcessor {

	@Override
	public String process(
		String content, Map<String, Object> fieldNameValueMap) {

		return LayoutSetPrototypeMergeUtil.getLayoutExternalReferenceCode(
			content);
	}

}