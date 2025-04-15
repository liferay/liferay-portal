/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.portlet;

import com.liferay.dynamic.data.mapping.configuration.DDMWebConfiguration;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.constants.DDMWebKeys;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException;
import com.liferay.dynamic.data.mapping.exception.RequiredStructureException;
import com.liferay.dynamic.data.mapping.exception.RequiredTemplateException;
import com.liferay.dynamic.data.mapping.exception.StructureDefinitionException;
import com.liferay.dynamic.data.mapping.exception.StructureDuplicateElementException;
import com.liferay.dynamic.data.mapping.exception.StructureNameException;
import com.liferay.dynamic.data.mapping.exception.TemplateNameException;
import com.liferay.dynamic.data.mapping.exception.TemplateScriptException;
import com.liferay.dynamic.data.mapping.exception.TemplateSmallImageContentException;
import com.liferay.dynamic.data.mapping.exception.TemplateSmallImageNameException;
import com.liferay.dynamic.data.mapping.exception.TemplateSmallImageSizeException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.dynamic.data.mapping.util.DDMTemplateHelper;
import com.liferay.dynamic.data.mapping.validator.DDMFormLayoutValidationException;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustNotDuplicateFieldName;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetOptionsForField;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidCharactersForFieldName;
import com.liferay.dynamic.data.mapping.web.internal.display.context.DDMDisplayContext;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.PortletPreferencesException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.taglib.DynamicIncludeUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	configurationPid = "com.liferay.dynamic.data.mapping.configuration.DDMWebConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.autopropagated-parameters=backURL",
		"com.liferay.portlet.autopropagated-parameters=navigationStartsOn",
		"com.liferay.portlet.autopropagated-parameters=refererPortletName",
		"com.liferay.portlet.autopropagated-parameters=refererWebDAVToken",
		"com.liferay.portlet.autopropagated-parameters=showAncestorScopes",
		"com.liferay.portlet.autopropagated-parameters=showBackURL",
		"com.liferay.portlet.autopropagated-parameters=showCacheableInput",
		"com.liferay.portlet.autopropagated-parameters=showHeader",
		"com.liferay.portlet.autopropagated-parameters=showManageTemplates",
		"com.liferay.portlet.css-class-wrapper=portlet-dynamic-data-mapping",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-javascript=/js/legacy/custom_fields.js",
		"com.liferay.portlet.header-portlet-javascript=/js/legacy/main.js",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Dynamic Data Mapping Web",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.always-send-redirect=true",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class DDMPortlet extends MVCPortlet {

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		try {
			super.processAction(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchStructureException ||
				exception instanceof NoSuchTemplateException ||
				exception instanceof PortletPreferencesException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				include("/error.jsp", actionRequest, actionResponse);
			}
			else if (exception instanceof DDMFormLayoutValidationException ||
					 exception instanceof DDMFormValidationException ||
					 exception instanceof LocaleException ||
					 exception instanceof MustNotDuplicateFieldName ||
					 exception instanceof MustSetOptionsForField ||
					 exception instanceof MustSetValidCharactersForFieldName ||
					 exception instanceof RequiredStructureException ||
					 exception instanceof RequiredTemplateException ||
					 exception instanceof StructureDefinitionException ||
					 exception instanceof StructureDuplicateElementException ||
					 exception instanceof StructureNameException ||
					 exception instanceof TemplateNameException ||
					 exception instanceof TemplateNameException ||
					 exception instanceof TemplateScriptException ||
					 exception instanceof TemplateSmallImageContentException ||
					 exception instanceof TemplateSmallImageNameException ||
					 exception instanceof TemplateSmallImageSizeException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);

				if (exception instanceof RequiredStructureException ||
					exception instanceof RequiredTemplateException) {

					String redirect = portal.escapeRedirect(
						ParamUtil.getString(actionRequest, "redirect"));

					if (Validator.isNotNull(redirect)) {
						actionResponse.sendRedirect(redirect);
					}
				}
			}
			else {
				throw exception;
			}
		}
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			_setDDMDisplayContextRequestAttribute(
				renderRequest, renderResponse);

			_setDDMTemplateRequestAttribute(renderRequest);

			_setDDMStructureRequestAttribute(renderRequest);
		}
		catch (NoSuchStructureException | NoSuchTemplateException exception) {

			// Let this slide because the user can manually input a structure
			// or template key for a new model that does not yet exist

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
		catch (Exception exception) {
			if (exception instanceof PortletPreferencesException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				include("/error.jsp", renderRequest, renderResponse);
			}
			else {
				throw new PortletException(exception);
			}
		}

		DynamicIncludeUtil.include(
			portal.getHttpServletRequest(renderRequest),
			portal.getHttpServletResponse(renderResponse),
			DDMPortlet.class.getName() + "#formRendered", true);

		super.render(renderRequest, renderResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		ddmWebConfiguration = ConfigurableUtil.createConfigurable(
			DDMWebConfiguration.class, properties);
	}

	@Reference
	protected volatile DDMStructureLinkLocalService
		ddmStructureLinkLocalService;

	@Reference
	protected volatile DDMStructureLocalService ddmStructureLocalService;

	@Reference
	protected volatile DDMStructureService ddmStructureService;

	@Reference
	protected DDMTemplateHelper ddmTemplateHelper;

	@Reference
	protected volatile DDMTemplateLocalService ddmTemplateLocalService;

	@Reference
	protected volatile DDMTemplateService ddmTemplateService;

	protected volatile DDMWebConfiguration ddmWebConfiguration;

	@Reference
	protected Portal portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.dynamic.data.mapping.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	protected Release release;

	private void _setDDMDisplayContextRequestAttribute(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortalException {

		DDMDisplayContext ddmDisplayContext = new DDMDisplayContext(
			renderRequest, renderResponse, ddmStructureLinkLocalService,
			ddmStructureService, ddmTemplateHelper, ddmTemplateService,
			ddmWebConfiguration);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, ddmDisplayContext);
	}

	private void _setDDMStructureRequestAttribute(RenderRequest renderRequest)
		throws PortalException {

		long classNameId = ParamUtil.getLong(renderRequest, "classNameId");
		long classPK = ParamUtil.getLong(renderRequest, "classPK");

		if ((classNameId > 0) && (classPK > 0)) {
			DDMStructure structure = null;

			long structureClassNameId = portal.getClassNameId(
				DDMStructure.class);

			if ((structureClassNameId == classNameId) && (classPK > 0)) {
				structure = ddmStructureLocalService.getStructure(classPK);
			}

			renderRequest.setAttribute(
				DDMWebKeys.DYNAMIC_DATA_MAPPING_STRUCTURE, structure);
		}
	}

	private void _setDDMTemplateRequestAttribute(RenderRequest renderRequest)
		throws PortalException {

		long templateId = ParamUtil.getLong(renderRequest, "templateId");

		if (templateId > 0) {
			DDMTemplate template = ddmTemplateLocalService.getDDMTemplate(
				templateId);

			renderRequest.setAttribute(
				DDMWebKeys.DYNAMIC_DATA_MAPPING_TEMPLATE, template);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(DDMPortlet.class);

}