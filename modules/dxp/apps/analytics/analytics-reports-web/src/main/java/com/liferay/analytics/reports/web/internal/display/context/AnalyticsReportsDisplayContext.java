/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.display.context;

import com.liferay.analytics.reports.info.item.ClassNameClassPKInfoItemIdentifier;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import java.util.Collections;
import java.util.Map;

/**
 * @author David Arques
 * @author Sarai Díaz
 */
public class AnalyticsReportsDisplayContext<T> {

	public AnalyticsReportsDisplayContext(
		InfoItemReference infoItemReference, Portal portal,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_infoItemReference = infoItemReference;
		_portal = portal;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public Map<String, Object> getData() throws PortalException {
		if (_data != null) {
			return _data;
		}

		_data = Collections.singletonMap(
			"context",
			Collections.singletonMap(
				"analyticsReportsDataURL",
				String.valueOf(
					_getResourceURL("/analytics_reports/get_data"))));

		return _data;
	}

	private ResourceURL _getResourceURL(String resourceID)
		throws PortalException {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(_renderRequest);

		ResourceURL resourceURL =
			(ResourceURL)PortletURLBuilder.createLiferayPortletURL(
				_portal.getLiferayPortletResponse(_renderResponse),
				liferayPortletRequest.getPlid(),
				liferayPortletRequest.getPortletName(),
				PortletRequest.RESOURCE_PHASE, MimeResponse.Copy.PUBLIC
			).setRedirect(
				ParamUtil.getString(_renderRequest, "redirect")
			).setParameter(
				"className", _infoItemReference.getClassName()
			).buildPortletURL();

		if (_infoItemReference.getInfoItemIdentifier() instanceof
				ClassNameClassPKInfoItemIdentifier) {

			ClassNameClassPKInfoItemIdentifier
				classNameClassPKInfoItemIdentifier =
					(ClassNameClassPKInfoItemIdentifier)
						_infoItemReference.getInfoItemIdentifier();

			resourceURL.setParameter(
				"classPK",
				String.valueOf(
					classNameClassPKInfoItemIdentifier.getClassPK()));
			resourceURL.setParameter(
				"classTypeName",
				classNameClassPKInfoItemIdentifier.getClassName());
		}
		else if (_infoItemReference.getInfoItemIdentifier() instanceof
					ClassPKInfoItemIdentifier) {

			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)
					_infoItemReference.getInfoItemIdentifier();

			resourceURL.setParameter(
				"classPK",
				String.valueOf(classPKInfoItemIdentifier.getClassPK()));
		}

		resourceURL.setResourceID(resourceID);

		return resourceURL;
	}

	private Map<String, Object> _data;
	private final InfoItemReference _infoItemReference;
	private final Portal _portal;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}