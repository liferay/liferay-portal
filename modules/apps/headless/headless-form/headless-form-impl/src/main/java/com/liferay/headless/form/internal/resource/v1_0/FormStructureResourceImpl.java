/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.internal.resource.v1_0;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.headless.form.dto.v1_0.FormStructure;
import com.liferay.headless.form.dto.v1_0.util.StructureUtil;
import com.liferay.headless.form.resource.v1_0.FormStructureResource;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 * @author Victor Oliveira
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/form-structure.properties",
	scope = ServiceScope.PROTOTYPE, service = FormStructureResource.class
)
@Deprecated
public class FormStructureResourceImpl extends BaseFormStructureResourceImpl {

	@Override
	public FormStructure getFormStructure(Long formStructureId)
		throws Exception {

		return StructureUtil.toFormStructure(
			contextAcceptLanguage.isAcceptAllLanguages(),
			_ddmStructureService.getStructure(formStructureId),
			contextAcceptLanguage.getPreferredLocale(), _portal,
			_userLocalService);
	}

	@Override
	public Page<FormStructure> getSiteFormStructuresPage(
			Long siteId, Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_ddmStructureService.getStructures(
					contextCompany.getCompanyId(), new long[] {siteId},
					_getClassNameId(), pagination.getStartPosition(),
					pagination.getEndPosition(), null),
				ddmStructure -> StructureUtil.toFormStructure(
					contextAcceptLanguage.isAcceptAllLanguages(), ddmStructure,
					contextAcceptLanguage.getPreferredLocale(), _portal,
					_userLocalService)),
			pagination,
			_ddmStructureService.getStructuresCount(
				contextCompany.getCompanyId(), new long[] {siteId},
				_getClassNameId()));
	}

	private long _getClassNameId() {
		return _portal.getClassNameId(DDMFormInstance.class.getName());
	}

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}