/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(service = CTDisplayRenderer.class)
public class KaleoDefinitionCTDisplayRenderer
	extends BaseCTDisplayRenderer<KaleoDefinition> {

	@Override
	public String getEditURL(
		HttpServletRequest httpServletRequest,
		KaleoDefinition kaleoDefinition) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, themeDisplay.getScopeGroup(),
				WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/definition/edit_workflow_definition.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"name", kaleoDefinition.getName()
		).setParameter(
			"version", kaleoDefinition.getVersion()
		).build(
		).toString();
	}

	@Override
	public Class<KaleoDefinition> getModelClass() {
		return KaleoDefinition.class;
	}

	@Override
	public String getTitle(Locale locale, KaleoDefinition kaleoDefinition) {
		return kaleoDefinition.getTitle(locale);
	}

	@Override
	public String renderPreview(DisplayContext<KaleoDefinition> displayContext)
		throws WorkflowException {

		KaleoDefinition kaleoDefinition = displayContext.getModel();

		return StringBundler.concat(
			"<pre>",
			HtmlUtil.escapeAttribute(kaleoDefinition.getContentAsXML()),
			"</pre>");
	}

	@Override
	public boolean showPreviewDiff() {
		return true;
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<KaleoDefinition> displayBuilder) {

		KaleoDefinition kaleoDefinition = displayBuilder.getModel();

		displayBuilder.display(
			"name", kaleoDefinition.getName()
		).display(
			"title", kaleoDefinition.getTitle(displayBuilder.getLocale())
		).display(
			"description", kaleoDefinition.getDescription()
		).display(
			"created-by",
			() -> {
				String userName = kaleoDefinition.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", kaleoDefinition.getCreateDate()
		).display(
			"last-modified", kaleoDefinition.getModifiedDate()
		).display(
			"version", kaleoDefinition.getVersion()
		).display(
			"active", kaleoDefinition.isActive()
		);
	}

	@Reference
	private Portal _portal;

}