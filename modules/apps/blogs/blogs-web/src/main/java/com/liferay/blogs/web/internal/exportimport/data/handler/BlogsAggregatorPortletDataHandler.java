/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.exportimport.data.handler;

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.DefaultConfigurationPortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Julio Camarero
 */
@Component(
	property = "jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_AGGREGATOR,
	service = PortletDataHandler.class
)
public class BlogsAggregatorPortletDataHandler
	extends DefaultConfigurationPortletDataHandler {

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Activate
	protected void activate() {
		setDataLevel(DataLevel.PORTLET_INSTANCE);
		setPublishToLiveByDefault(true);
	}

}