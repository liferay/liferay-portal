/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.portlet.test;

import com.liferay.fragment.entry.processor.portlet.constants.FragmentEntryLinkPortletKeys;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Test",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.name=" + FragmentEntryLinkPortletKeys.FRAGMENT_ENTRY_LINK_INSTANCEABLE_TEST_PORTLET,
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class FragmentEntryLinkInstanceableTestPortlet extends MVCPortlet {

	@Activate
	protected void activate() {
		_portletRegistry.registerAlias(
			FragmentEntryLinkPortletKeys.
				FRAGMENT_ENTRY_LINK_INSTANCEABLE_TEST_PORTLET_ALIAS,
			FragmentEntryLinkPortletKeys.
				FRAGMENT_ENTRY_LINK_INSTANCEABLE_TEST_PORTLET);
	}

	@Deactivate
	protected void deactivate() {
		_portletRegistry.unregisterAlias(
			FragmentEntryLinkPortletKeys.
				FRAGMENT_ENTRY_LINK_INSTANCEABLE_TEST_PORTLET_ALIAS);
	}

	@Reference
	private PortletRegistry _portletRegistry;

}