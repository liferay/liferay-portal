/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.jaxrs.exception.mapper;

import com.liferay.knowledge.base.exception.KBArticleUrlTitleException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code KBArticleUrlTitleException} to a {@code 400} error.
 *
 * @author Víctor Galán
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Delivery)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Delivery.KnowledgeBaseArticleFriendlyURLExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class KnowledgeBaseArticleFriendlyURLExceptionMapper
	extends BaseExceptionMapper<KBArticleUrlTitleException> {

	@Override
	protected Problem getProblem(
		KBArticleUrlTitleException kbArticleUrlTitleException) {

		int statusCode = 400;

		if (kbArticleUrlTitleException instanceof
				KBArticleUrlTitleException.MustNotBeDuplicate) {

			statusCode = 409;
		}

		return new Problem(
			Response.Status.fromStatusCode(statusCode),
			StringUtil.replace(
				kbArticleUrlTitleException.getMessage(), "URL title",
				"Friendly URL"));
	}

}