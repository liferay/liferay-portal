/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.portlet.route;

import com.liferay.portal.kernel.portlet.DefaultFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.questions.web.internal.constants.QuestionsPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Gamarra
 */
@Component(
	property = {
		"com.liferay.portlet.friendly-url-routes=META-INF/friendly-url-routes/routes.xml",
		"jakarta.portlet.name=" + QuestionsPortletKeys.QUESTIONS
	},
	service = FriendlyURLMapper.class
)
public class QuestionsFriendlyURLMapper extends DefaultFriendlyURLMapper {

	@Override
	public String getMapping() {
		return _MAPPING;
	}

	@Override
	public boolean isCheckMappingWithPrefix() {
		return false;
	}

	private static final String _MAPPING = "questions";

}