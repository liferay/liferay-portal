/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util.internal.portlet;

import com.liferay.exportimport.test.util.constants.DummyFolderPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Akos Thurzo
 */
@Component(
	property = {
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"jakarta.portlet.name=" + DummyFolderPortletKeys.DUMMY_FOLDER,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class DummyFolderPortlet extends MVCPortlet {
}