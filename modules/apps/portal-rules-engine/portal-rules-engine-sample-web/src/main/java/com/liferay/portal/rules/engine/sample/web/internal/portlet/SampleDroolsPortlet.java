/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.rules.engine.sample.web.internal.portlet;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.rules.engine.RulesEngine;
import com.liferay.portal.rules.engine.sample.web.internal.constants.SampleDroolsPortletKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Sample Drools",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + SampleDroolsPortletKeys.SAMPLE_DROOLS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SampleDroolsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		RulesEngine rulesEngine = _rulesEngineSnapshot.get();

		if (rulesEngine != null) {
			renderRequest.setAttribute(
				RulesEngine.class.getName(), rulesEngine);
		}

		super.render(renderRequest, renderResponse);
	}

	private static final Snapshot<RulesEngine> _rulesEngineSnapshot =
		new Snapshot<>(
			SampleDroolsPortlet.class, RulesEngine.class,
			"(rules.engine.default.language=DRL)", true);

}