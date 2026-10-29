/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.layout.util.LayoutServiceContextHelperUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentEntryLinkUtil {

	public static String getProcessedHTML(
			FragmentEntryLink fragmentEntryLink,
			FragmentEntryProcessorRegistry fragmentEntryProcessorRegistry,
			User user)
		throws Exception {

		try (AutoCloseable autoCloseable =
				LayoutServiceContextHelperUtil.getServiceContextAutoCloseable(
					LayoutLocalServiceUtil.getLayout(
						fragmentEntryLink.getPlid()),
					user)) {

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			HttpServletRequest httpServletRequest = serviceContext.getRequest();
			HttpServletResponse httpServletResponse =
				serviceContext.getResponse();
			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			if ((httpServletRequest == null) && (themeDisplay != null)) {
				httpServletRequest = themeDisplay.getRequest();
			}

			if ((httpServletResponse == null) && (themeDisplay != null)) {
				httpServletResponse = themeDisplay.getResponse();
			}

			if ((httpServletRequest == null) || (httpServletResponse == null)) {
				return fragmentEntryLink.getHtml();
			}

			JSONObject editableValuesJSONObject =
				JSONFactoryUtil.safeCreateJSONObject(
					fragmentEntryLink.getEditableValues());

			try {
				return fragmentEntryProcessorRegistry.
					processFragmentEntryLinkHTML(
						JSONUtil.put(
							FragmentEntryProcessorConstants.
								KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
							editableValuesJSONObject.getJSONObject(
								FragmentEntryProcessorConstants.
									KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR)),
						fragmentEntryLink,
						new DefaultFragmentEntryProcessorContext(
							fragmentEntryLink.getCompanyId(),
							httpServletRequest, httpServletResponse,
							LocaleUtil.getMostRelevantLocale(),
							FragmentEntryLinkConstants.EDIT,
							fragmentEntryLink.getGroupId()));
			}
			catch (Throwable throwable) {
				if (_log.isDebugEnabled()) {
					_log.debug(throwable);
				}

				return fragmentEntryLink.getHtml();
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryLinkUtil.class);

}