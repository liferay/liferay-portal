/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.clamd.scanner.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Shuyang Zhou
 */
@ExtendedObjectClassDefinition(
	category = "antivirus", liferayLearnMessageKey = "general",
	liferayLearnMessageResource = "antivirus-clamd-scanner"
)
@Meta.OCD(
	id = "com.liferay.antivirus.clamd.scanner.internal.configuration.ClamdAntivirusScannerConfiguration",
	localization = "content/Language",
	name = "antivirus-clamd-scanner-configuration-name"
)
public interface ClamdAntivirusScannerConfiguration {

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(deflt = "", name = "hostname")
	public String hostname();

	@Meta.AD(deflt = "3310", name = "port", required = false)
	public int port();

	@Meta.AD(
		deflt = "120000", name = "timeout[socket-connection-so]",
		required = false
	)
	public int timeout();

}