/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.util;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Samuel Trong Tran
 */
public class PublicationsPortletURLUtil {

	public static String getDeleteHref(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse,
		String backURL, long ctCollectionId, Language language) {

		return StringBundler.concat(
			"javascript:Liferay.Util.openConfirmModal({message: '",
			language.get(
				httpServletRequest,
				"are-you-sure-you-want-to-delete-this-publication"),
			"', onConfirm: (isConfirmed) => {if (isConfirmed) {",
			"submitForm(document.hrefFm, '",
			getHref(
				renderResponse.createActionURL(), ActionRequest.ACTION_NAME,
				"/change_tracking/delete_ct_collection", "redirect", backURL,
				"ctCollectionId", String.valueOf(ctCollectionId)),
			"');} else {self.focus();}}});");
	}

	public static String getHref(PortletURL portletURL, Object... parameters) {
		if (parameters != null) {
			if ((parameters.length % 2) != 0) {
				throw new IllegalArgumentException(
					"Parameters length is not an even number");
			}

			for (int i = 0; i < parameters.length; i += 2) {
				String parameterName = String.valueOf(parameters[i]);
				String parameterValue = String.valueOf(parameters[i + 1]);

				portletURL.setParameter(parameterName, parameterValue);
			}
		}

		return portletURL.toString();
	}

	public static String getPermissionsHref(
			HttpServletRequest httpServletRequest, CTCollection ctCollection,
			Language language)
		throws Exception {

		return StringBundler.concat(
			"javascript:Liferay.Util.openWindow({dialog: {destroyOnHide: ",
			"true,}, dialogIframe: {bodyCssClass: 'dialog-with-footer'}, ",
			"title:'", language.get(httpServletRequest, "permissions"),
			"', uri:'",
			PermissionsURLTag.doTag(
				StringPool.BLANK, CTCollection.class.getName(),
				HtmlUtil.escape(ctCollection.getName()), null,
				String.valueOf(ctCollection.getCtCollectionId()),
				LiferayWindowState.POP_UP.toString(), null, httpServletRequest),
			"',});");
	}

	public static void setWindowState(
		PortletURL portletURL, WindowState windowState) {

		try {
			portletURL.setWindowState(windowState);
		}
		catch (WindowStateException windowStateException) {
			ReflectionUtil.throwException(windowStateException);
		}
	}

	private PublicationsPortletURLUtil() {
	}

}