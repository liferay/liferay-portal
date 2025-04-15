/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.taglib.internal.struts;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.NamespaceServletRequest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "path=/portal/comment/discussion/get_comments",
	service = StrutsAction.class
)
public class GetCommentsStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String namespace = ParamUtil.getString(httpServletRequest, "namespace");

		httpServletRequest.setAttribute("aui:form:portletNamespace", namespace);

		HttpServletRequest namespacedHttpServletRequest =
			new NamespaceServletRequest(
				httpServletRequest, StringPool.BLANK, namespace);

		String className = ParamUtil.getString(
			namespacedHttpServletRequest, "className");

		namespacedHttpServletRequest.setAttribute(
			"liferay-comment:discussion:className", className);

		long classPK = ParamUtil.getLong(
			namespacedHttpServletRequest, "classPK");

		namespacedHttpServletRequest.setAttribute(
			"liferay-comment:discussion:classPK", String.valueOf(classPK));

		boolean hideControls = ParamUtil.getBoolean(
			namespacedHttpServletRequest, "hideControls");

		namespacedHttpServletRequest.setAttribute(
			"liferay-comment:discussion:hideControls",
			String.valueOf(hideControls));

		int index = ParamUtil.getInteger(namespacedHttpServletRequest, "index");

		namespacedHttpServletRequest.setAttribute(
			"liferay-comment:discussion:index", String.valueOf(index));

		String portletId = ParamUtil.getString(
			namespacedHttpServletRequest, "p_p_id");

		namespacedHttpServletRequest.setAttribute(
			WebKeys.PORTLET_ID, portletId);

		String randomNamespace = ParamUtil.getString(
			namespacedHttpServletRequest, "randomNamespace");

		namespacedHttpServletRequest.setAttribute(
			"liferay-comment:discussion:randomNamespace", randomNamespace);

		boolean ratingsEnabled = ParamUtil.getBoolean(
			namespacedHttpServletRequest, "ratingsEnabled");

		namespacedHttpServletRequest.setAttribute(
			"liferay-comment:discussion:ratingsEnabled",
			String.valueOf(ratingsEnabled));

		int rootIndexPage = ParamUtil.getInteger(
			namespacedHttpServletRequest, "rootIndexPage");

		namespacedHttpServletRequest.setAttribute(
			"liferay-comment:discussion:rootIndexPage",
			String.valueOf(rootIndexPage));

		long userId = ParamUtil.getLong(namespacedHttpServletRequest, "userId");

		namespacedHttpServletRequest.setAttribute(
			"liferay-comment:discussion:userId", String.valueOf(userId));

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/discussion/page_resources.jsp");

		requestDispatcher.include(
			namespacedHttpServletRequest, httpServletResponse);

		return null;
	}

	@Reference(target = "(osgi.web.symbolicname=com.liferay.comment.taglib)")
	private ServletContext _servletContext;

}