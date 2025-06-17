/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.render.DDMFormRendererUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class DDMFormInstanceCTDisplayRenderer
	extends BaseCTDisplayRenderer<DDMFormInstance> {

	@Override
	public String[] getAvailableLanguageIds(DDMFormInstance ddmFormInstance) {
		return ddmFormInstance.getAvailableLanguageIds();
	}

	@Override
	public String getDefaultLanguageId(DDMFormInstance ddmFormInstance) {
		return ddmFormInstance.getDefaultLanguageId();
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			DDMFormInstance ddmFormInstance)
		throws PortalException {

		Group group = _groupLocalService.getGroup(ddmFormInstance.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group,
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/admin/edit_form_instance.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"ddmStructureId", ddmFormInstance.getStructureId()
		).setParameter(
			"formInstanceId", ddmFormInstance.getFormInstanceId()
		).setParameter(
			"groupId", ddmFormInstance.getGroupId()
		).buildString();
	}

	@Override
	public Class<DDMFormInstance> getModelClass() {
		return DDMFormInstance.class;
	}

	@Override
	public String getTitle(Locale locale, DDMFormInstance ddmFormInstance) {
		return ddmFormInstance.getName(locale);
	}

	@Override
	public String renderPreview(DisplayContext<DDMFormInstance> displayContext)
		throws Exception {

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		ddmFormFieldRenderingContext.setHttpServletRequest(httpServletRequest);

		ddmFormFieldRenderingContext.setHttpServletResponse(
			displayContext.getHttpServletResponse());
		ddmFormFieldRenderingContext.setLocale(displayContext.getLocale());

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		ddmFormFieldRenderingContext.setPortletNamespace(
			portletResponse.getNamespace());

		ddmFormFieldRenderingContext.setReturnFullContext(true);
		ddmFormFieldRenderingContext.setShowEmptyFieldLabel(true);
		ddmFormFieldRenderingContext.setViewMode(true);

		DDMFormInstance ddmFormInstance = displayContext.getModel();

		return DDMFormRendererUtil.render(
			ddmFormInstance.getDDMForm(), ddmFormFieldRenderingContext);
	}

	@Override
	public boolean showPreviewDiff() {
		return true;
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<DDMFormInstance> displayBuilder) {

		DDMFormInstance ddmFormInstance = displayBuilder.getModel();

		displayBuilder.display(
			"name", ddmFormInstance.getName(displayBuilder.getLocale())
		).display(
			"description",
			ddmFormInstance.getDescription(displayBuilder.getLocale())
		).display(
			"version", ddmFormInstance.getVersion()
		).display(
			"modified-date", ddmFormInstance.getModifiedDate()
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}