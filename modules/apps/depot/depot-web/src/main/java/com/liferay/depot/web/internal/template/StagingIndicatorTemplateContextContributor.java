/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.template;

import com.liferay.depot.web.internal.util.StagingIndicatorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Evan Thibodeau
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_THEME,
	service = TemplateContextContributor.class
)
public class StagingIndicatorTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		try {
			if (StagingIndicatorUtil.isShowStagingIndicator(
					httpServletRequest)) {

				contextObjects.put(
					"bodyCssClass",
					GetterUtil.getString(contextObjects.get("bodyCssClass")) +
						" has-staging-indicator");
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagingIndicatorTemplateContextContributor.class);

}