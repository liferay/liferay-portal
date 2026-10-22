/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.batch.engine;

import com.liferay.batch.engine.BatchEngineContentProcessor;
import com.liferay.headless.admin.site.internal.batch.engine.util.LayoutSetPrototypeMergeUtil;
import com.liferay.portal.kernel.model.Layout;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"field.name=fragmentInstance.draftFragmentInstanceExternalReferenceCode",
		"field.name=fragmentInstance.fragmentInstanceExternalReferenceCode",
		"field.name=itemExternalReference.externalReferenceCode",
		"field.name=itemReference.externalReferenceCode",
		"field.name=pageElementDefinition.draftWidgetInstanceExternalReferenceCode",
		"field.name=pageElementDefinition.widgetInstanceExternalReferenceCode",
		"field.name=pageExperiences.externalReferenceCode",
		"field.name=pageExperiences.pageSpecificationExternalReferenceCode",
		"field.name=pageSettings.linkToPageExternalReferenceCode",
		"field.name=pageSpecifications.draftContentPageSpecificationExternalReferenceCode",
		"field.name=pageSpecifications.externalReferenceCode",
		"field.name=sitePageItemExternalReference.externalReferenceCode"
	},
	service = BatchEngineContentProcessor.class
)
public class
	LayoutSetPrototypeMergeExternalReferenceCodeBatchEngineContentProcessor
		implements BatchEngineContentProcessor {

	@Override
	public String process(
		String content, Map<String, Object> fieldNameValueMap) {

		Object className = fieldNameValueMap.get("className");

		if ((className != null) &&
			!Objects.equals(className, Layout.class.getName())) {

			return content;
		}

		return LayoutSetPrototypeMergeUtil.getLayoutExternalReferenceCode(
			content);
	}

}