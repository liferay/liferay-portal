/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.talend.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.dispatch.constants.DispatchWebKeys;
import com.liferay.dispatch.metadata.DispatchTriggerMetadataProvider;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.talend.web.internal.display.context.TalendDispatchDisplayContext;
import com.liferay.dispatch.talend.web.internal.executor.TalendDispatchTaskExecutor;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.admin.util.OmniadminUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class DispatchTalendScreenNavigationEntry
	extends DispatchTalendScreenNavigationCategory
	implements ScreenNavigationEntry<DispatchTrigger> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, DispatchTrigger dispatchTrigger) {
		if ((dispatchTrigger == null) ||
			!Objects.equals(
				dispatchTrigger.getDispatchTaskExecutorType(),
				TalendDispatchTaskExecutor.TALEND) ||
			!OmniadminUtil.isOmniadmin(user)) {

			return false;
		}

		return true;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new TalendDispatchDisplayContext(
				_dispatchTriggerMetadataProvider.getDispatchTriggerMetadata(
					_getDispatchTriggerId(httpServletRequest))));

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/view.jsp");
	}

	private long _getDispatchTriggerId(HttpServletRequest httpServletRequest) {
		DispatchTrigger dispatchTrigger =
			(DispatchTrigger)httpServletRequest.getAttribute(
				DispatchWebKeys.DISPATCH_TRIGGER);

		if (dispatchTrigger == null) {
			return 0;
		}

		return dispatchTrigger.getDispatchTriggerId();
	}

	@Reference
	private DispatchTriggerMetadataProvider _dispatchTriggerMetadataProvider;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.dispatch.talend.web)"
	)
	private ServletContext _servletContext;

}