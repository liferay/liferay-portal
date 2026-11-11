/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.google.docs.internal.verify;

import com.liferay.document.library.google.docs.internal.helper.GoogleDocsDLFileEntryTypeUtil;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.verify.VerifyProcess;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = VerifyProcess.class)
public class DLGoogleDocsVerifyProcess extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		_checkGoogleDocsDLFileEntryType();
	}

	private void _checkGoogleDocsDLFileEntryType() throws Exception {
		_companyLocalService.forEachCompany(
			company ->
				GoogleDocsDLFileEntryTypeUtil.addGoogleDocsDLFileEntryType(
					company, _ddmStructureLocalService,
					_defaultDDMStructureHelper,
					_classNameLocalService.getClassNameId(
						DLFileEntryMetadata.class),
					_dlFileEntryTypeLocalService, false, _userLocalService));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DefaultDDMStructureHelper _defaultDDMStructureHelper;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private UserLocalService _userLocalService;

}