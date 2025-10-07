/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Raymond Augé
 */
@ExtendedObjectClassDefinition(generateUI = false)
@Meta.OCD(
	factory = true,
	id = "com.liferay.portal.k8s.agent.configuration.PortalK8sAgentConfiguration"
)
public interface PortalK8sAgentConfiguration {

	@Meta.AD(type = Meta.Type.String)
	public String apiServerHost();

	@Meta.AD(type = Meta.Type.String)
	public int apiServerPort();

	@Meta.AD(deflt = "true", required = false, type = Meta.Type.String)
	public boolean apiServerSSL();

	@Meta.AD(type = Meta.Type.String)
	public String caCertData();

	@Meta.AD(deflt = "300", required = false, type = Meta.Type.Long)
	public long debounceDelayMillis();

	@Meta.AD(deflt = "1000", required = false, type = Meta.Type.Long)
	public long deferSecondaryNodeMillis();

	@Meta.AD(
		deflt = "lxc.liferay.com/metadataType=ext-provision", required = false,
		type = Meta.Type.String
	)
	public String labelSelector();

	@Meta.AD(type = Meta.Type.String)
	public String namespace();

	@Meta.AD(type = Meta.Type.String)
	public String saToken();

}