/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.faces.test.bridge.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Dante Wang
 */
@ExtendedObjectClassDefinition(category = "infrastructure", generateUI = false)
@Meta.OCD(
	id = "com.liferay.faces.test.bridge.configuration.FacesTestBridgeConfiguration",
	name = "faces-test-bridge-configuration-name"
)
public interface FacesTestBridgeConfiguration {

	@Meta.AD(deflt = "csfcfc", required = false)
	public String[] cookieNames();

}