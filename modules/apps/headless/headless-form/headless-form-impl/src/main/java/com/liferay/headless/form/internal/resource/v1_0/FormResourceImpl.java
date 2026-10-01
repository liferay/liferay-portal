/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.internal.resource.v1_0;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.headless.form.dto.v1_0.Form;
import com.liferay.headless.form.dto.v1_0.FormContext;
import com.liferay.headless.form.dto.v1_0.FormDocument;
import com.liferay.headless.form.dto.v1_0.util.FormDocumentUtil;
import com.liferay.headless.form.dto.v1_0.util.FormUtil;
import com.liferay.headless.form.internal.dto.v1_0.util.FormContextUtil;
import com.liferay.headless.form.resource.v1_0.FormResource;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;
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
	properties = "OSGI-INF/liferay/rest/v1_0/form.properties",
	scope = ServiceScope.PROTOTYPE, service = FormResource.class
)
@Deprecated
public class FormResourceImpl extends BaseFormResourceImpl {

	@Override
	public Form getForm(Long formId) throws Exception {
		return FormUtil.toForm(
			contextAcceptLanguage.isAcceptAllLanguages(),
			_ddmFormInstanceService.getFormInstance(formId), _portal,
			contextAcceptLanguage.getPreferredLocale(), _userLocalService);
	}

	@Override
	public Page<Form> getSiteFormsPage(Long siteId, Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_ddmFormInstanceService.getFormInstances(
					contextCompany.getCompanyId(), siteId,
					pagination.getStartPosition(), pagination.getEndPosition()),
				ddmFormInstance -> FormUtil.toForm(
					contextAcceptLanguage.isAcceptAllLanguages(),
					ddmFormInstance, _portal,
					contextAcceptLanguage.getPreferredLocale(),
					_userLocalService)),
			pagination,
			_ddmFormInstanceService.getFormInstancesCount(
				contextCompany.getCompanyId(), siteId));
	}

	@Override
	public FormContext postFormEvaluateContext(
			Long formId, FormContext formContext)
		throws Exception {

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceService.getFormInstance(formId);

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setGroupId(ddmFormInstance.getGroupId());
		ddmFormRenderingContext.setHttpServletRequest(
			contextHttpServletRequest);

		return FormContextUtil.evaluateContext(
			ddmFormInstance, ddmFormRenderingContext,
			_ddmFormTemplateContextFactory, formContext.getFormFieldValues(),
			contextAcceptLanguage.getPreferredLocale());
	}

	@Override
	public FormDocument postFormFormDocument(
			Long formId, MultipartBody multipartBody)
		throws Exception {

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceService.getFormInstance(formId);

		FormDocument formDocument = multipartBody.getValueAsInstance(
			"formDocument", FormDocument.class);

		Long folderId = formDocument.getFolderId();

		if (folderId == null) {
			folderId = 0L;
		}

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		return FormDocumentUtil.toFormDocument(
			_dlurlHelper,
			_dlAppService.addFileEntry(
				null, ddmFormInstance.getGroupId(), folderId,
				binaryFile.getFileName(), binaryFile.getContentType(),
				formDocument.getTitle(), formDocument.getTitle(),
				formDocument.getDescription(), null,
				binaryFile.getInputStream(), binaryFile.getSize(), null, null,
				null, new ServiceContext()));
	}

	@Reference
	private DDMFormInstanceService _ddmFormInstanceService;

	@Reference
	private DDMFormTemplateContextFactory _ddmFormTemplateContextFactory;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlurlHelper;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}