/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.web.internal.portlet;

import com.liferay.comment.web.internal.constants.CommentPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.icon=/icons/comment.png",
		"jakarta.portlet.display-name=Comments",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.name=" + CommentPortletKeys.COMMENT,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest",
		"jakarta.portlet.security-role-ref=power-user",
		"jakarta.portlet.security-role-ref=user", "jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CommentPortlet extends MVCPortlet {
}