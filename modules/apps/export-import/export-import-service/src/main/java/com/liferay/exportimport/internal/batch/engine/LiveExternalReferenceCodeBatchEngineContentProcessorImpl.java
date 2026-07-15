/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.batch.engine;

import com.liferay.batch.engine.BatchEngineContentProcessor;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(
	property = "field.name=scope.externalReferenceCode",
	service = BatchEngineContentProcessor.class
)
public class LiveExternalReferenceCodeBatchEngineContentProcessorImpl
	implements BatchEngineContentProcessor {

	@Override
	public String process(String content) {
		if (!ExportImportThreadLocal.isStagingInProcess()) {
			return content;
		}

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			content, CompanyThreadLocal.getCompanyId());

		if (group == null) {
			return content;
		}

		Group liveGroup = group.getLiveGroup();

		if (liveGroup == null) {
			return content;
		}

		return liveGroup.getExternalReferenceCode();
	}

	@Reference
	private GroupLocalService _groupLocalService;

}