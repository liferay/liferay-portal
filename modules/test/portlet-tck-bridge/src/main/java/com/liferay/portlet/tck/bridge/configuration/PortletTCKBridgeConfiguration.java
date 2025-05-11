/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.tck.bridge.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Shuyang Zhou
 * @author Vernon Singleton
 * @author Kyle Stiemann
 */
@ExtendedObjectClassDefinition(category = "infrastructure")
@Meta.OCD(
	id = "com.liferay.portlet.tck.bridge.configuration.PortletTCKBridgeConfiguration",
	localization = "content/Language",
	name = "portlet-tck-bridge-configuration-name"
)
public interface PortletTCKBridgeConfiguration {

	@Meta.AD(deflt = "")
	public String configFile();

	@Meta.AD(
		deflt = "action_tr0_cookie|action_tr1_cookie|action_tr2_cookie|action_tr3_cookie|event_tr0_cookie|event_tr1_cookie|event_tr2_cookie|header_tr0_cookie|header_tr1_cookie|header_tr2_cookie|header_tr3_cookie|render_tr0_cookie|render_tr1_cookie|render_tr2_cookie|render_tr3_cookie|resource_tr0_cookie|resource_tr1_cookie|resource_tr2_cookie|tr4_cookie",
		required = false
	)
	public String[] cookieNames();

}