/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.internal.resource.v1_0;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.form.dto.v1_0.FormDocument;
import com.liferay.headless.form.dto.v1_0.util.FormDocumentUtil;
import com.liferay.headless.form.resource.v1_0.FormDocumentResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 * @author Victor Oliveira
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/form-document.properties",
	scope = ServiceScope.PROTOTYPE, service = FormDocumentResource.class
)
@Deprecated
public class FormDocumentResourceImpl extends BaseFormDocumentResourceImpl {

	@Override
	public void deleteFormDocument(Long formDocumentId) throws Exception {
		_dlAppService.deleteFileEntry(formDocumentId);
	}

	@Override
	public FormDocument getFormDocument(Long formDocumentId) throws Exception {
		return FormDocumentUtil.toFormDocument(
			_dlurlHelper, _dlAppService.getFileEntry(formDocumentId));
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlurlHelper;

}