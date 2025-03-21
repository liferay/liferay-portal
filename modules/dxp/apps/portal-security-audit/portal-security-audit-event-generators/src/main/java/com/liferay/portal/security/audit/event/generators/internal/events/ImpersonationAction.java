/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.internal.events;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class ImpersonationAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {
			doRun(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	protected void doRun(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long plid = ParamUtil.getLong(httpServletRequest, "p_l_id");

		if (plid <= 0) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();
		User realUser = themeDisplay.getRealUser();

		HttpSession httpSession = httpServletRequest.getSession();

		Boolean impersonatingUser = (Boolean)httpSession.getAttribute(
			_IMPERSONATING_USER);

		if (Validator.isNotNull(themeDisplay.getDoAsUserId()) &&
			(user.getUserId() != realUser.getUserId())) {

			if (impersonatingUser == null) {
				httpSession.setAttribute(_IMPERSONATING_USER, Boolean.TRUE);

				JSONObject additionalInfoJSONObject =
					_jsonFactory.createJSONObject();

				additionalInfoJSONObject.put(
					"userEmailAddress", user.getEmailAddress()
				).put(
					"userId", user.getUserId()
				).put(
					"userName", user.getFullName()
				);

				AuditMessage auditMessage = new AuditMessage(
					EventTypes.IMPERSONATE, themeDisplay.getCompanyId(),
					realUser.getUserId(), realUser.getFullName(),
					User.class.getName(), String.valueOf(user.getUserId()),
					null, additionalInfoJSONObject);

				_auditRouter.route(auditMessage);
			}
		}
		else if (impersonatingUser != null) {
			httpSession.removeAttribute(_IMPERSONATING_USER);
		}
	}

	private static final String _IMPERSONATING_USER =
		ImpersonationAction.class + ".IMPERSONATING_USER";

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private JSONFactory _jsonFactory;

}