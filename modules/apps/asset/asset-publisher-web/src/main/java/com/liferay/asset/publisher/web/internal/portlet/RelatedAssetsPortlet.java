/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration;
import com.liferay.asset.publisher.web.internal.util.AssetPublisherCustomizerRegistry;
import com.liferay.asset.util.LinkedAssetEntryIdsUtil;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	configurationPid = "com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.ajaxable=true",
		"com.liferay.portlet.css-class-wrapper=portlet-asset-publisher",
		"com.liferay.portlet.display-category=category.cms",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=1",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Related Assets",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + AssetPublisherPortletKeys.RELATED_ASSETS,
		"jakarta.portlet.preferences=classpath:/META-INF/portlet-preferences/related-assets-default-portlet-preferences.xml",
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.supported-public-render-parameter=assetEntryId",
		"jakarta.portlet.supported-public-render-parameter=categoryId",
		"jakarta.portlet.supported-public-render-parameter=resetCur",
		"jakarta.portlet.supported-public-render-parameter=tag",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class RelatedAssetsPortlet extends AssetPublisherPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		long assetEntryId = ParamUtil.getLong(
			_portal.getHttpServletRequest(renderRequest), "assetEntryId");

		if (assetEntryId > 0) {
			LinkedAssetEntryIdsUtil.addLinkedAssetEntryId(
				renderRequest, assetEntryId);
		}

		super.render(renderRequest, renderResponse);
	}

	@Activate
	@Modified
	@Override
	protected void activate(Map<String, Object> properties) {
		assetPublisherWebConfiguration = ConfigurableUtil.createConfigurable(
			AssetPublisherWebConfiguration.class, properties);

		assetPublisherCustomizerRegistry = new AssetPublisherCustomizerRegistry(
			assetPublisherHelper, assetPublisherWebConfiguration);

		_portletRegistry.registerAlias(
			_ALIAS, AssetPublisherPortletKeys.RELATED_ASSETS);
	}

	@Deactivate
	@Override
	protected void deactivate() {
		_portletRegistry.unregisterAlias(_ALIAS);
	}

	private static final String _ALIAS = "related-assets";

	@Reference
	private Portal _portal;

	@Reference
	private PortletRegistry _portletRegistry;

}