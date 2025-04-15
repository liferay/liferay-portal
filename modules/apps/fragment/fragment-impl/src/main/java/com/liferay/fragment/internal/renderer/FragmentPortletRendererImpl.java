/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer;

import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentPortletRenderer;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.constants.PortletPreferencesFactoryConstants;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.portletext.RuntimeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pavel Savinov
 */
@Component(service = FragmentPortletRenderer.class)
public class FragmentPortletRendererImpl implements FragmentPortletRenderer {

	@Override
	public String renderPortlet(
			FragmentEntryLink fragmentEntryLink,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String portletName,
			String instanceId, String defaultPreferences)
		throws PortalException {

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		boolean inheritedFromMaster = false;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if ((themeDisplay != null) &&
			(fragmentEntryLink.getPlid() != themeDisplay.getPlid())) {

			inheritedFromMaster = true;
		}

		try {
			RuntimeTag.doTag(
				portletName, instanceId, StringPool.BLANK,
				PortletPreferencesFactoryConstants.
					SETTINGS_SCOPE_PORTLET_INSTANCE,
				defaultPreferences, inheritedFromMaster, null,
				httpServletRequest,
				new PipingServletResponse(
					httpServletResponse, unsyncStringWriter));
		}
		catch (Exception exception) {
			throw new FragmentEntryContentException(exception);
		}

		return unsyncStringWriter.toString();
	}

}