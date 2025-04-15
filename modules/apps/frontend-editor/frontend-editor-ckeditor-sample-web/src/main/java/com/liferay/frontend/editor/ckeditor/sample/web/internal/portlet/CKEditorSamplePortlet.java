/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor.sample.web.internal.portlet;

import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.frontend.editor.ckeditor.sample.web.internal.constants.CKEditorSamplePortletKeys;
import com.liferay.frontend.editor.ckeditor.sample.web.internal.constants.CKEditorSampleWebKeys;
import com.liferay.frontend.editor.ckeditor.sample.web.internal.display.context.CKEditorSampleDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julien Castelain
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-ckeditor-sample",
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=CKEditor Sample",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + CKEditorSamplePortletKeys.CKEDITOR_SAMPLE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CKEditorSamplePortlet extends MVCPortlet {

	@Override
	public void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			CKEditorSampleWebKeys.CKEDITOR_SAMPLE_DISPLAY_CONTEXT,
			new CKEditorSampleDisplayContext(_cetManager, renderRequest));

		super.doDispatch(renderRequest, renderResponse);
	}

	@Reference
	private CETManager _cetManager;

}