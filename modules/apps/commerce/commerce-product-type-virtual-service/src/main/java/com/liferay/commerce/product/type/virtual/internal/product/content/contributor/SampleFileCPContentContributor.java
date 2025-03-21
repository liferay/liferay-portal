/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.internal.product.content.contributor;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.commerce.product.type.virtual.util.VirtualCPTypeHelper;
import com.liferay.commerce.product.util.CPContentContributor;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "commerce.product.content.contributor.name=" + SampleFileCPContentContributor.NAME,
	service = CPContentContributor.class
)
public class SampleFileCPContentContributor implements CPContentContributor {

	public static final String NAME = "sampleFile";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JSONObject getValue(
			CPInstance cpInstance, HttpServletRequest httpServletRequest)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		if (cpInstance == null) {
			return jsonObject;
		}

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingLocalService.
				fetchCPDefinitionVirtualSetting(
					CPInstance.class.getName(), cpInstance.getCPInstanceId());

		if ((cpDefinitionVirtualSetting == null) ||
			!cpDefinitionVirtualSetting.isOverride()) {

			cpDefinitionVirtualSetting =
				_cpDefinitionVirtualSettingLocalService.
					fetchCPDefinitionVirtualSetting(
						CPDefinition.class.getName(),
						cpInstance.getCPDefinitionId());
		}

		if (cpDefinitionVirtualSetting == null) {
			jsonObject.put(NAME, StringPool.BLANK);

			return jsonObject;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String sampleURL = _virtualCPTypeHelper.getSampleURL(
			cpInstance.getCPDefinitionId(), cpInstance.getCPInstanceId(),
			themeDisplay);

		if (Validator.isNull(sampleURL)) {
			jsonObject.put(NAME, StringPool.BLANK);

			return jsonObject;
		}

		String sampleFile = _getSampleFileHtml(sampleURL, httpServletRequest);

		jsonObject.put(NAME, sampleFile);

		return jsonObject;
	}

	private String _getSampleFileHtml(
		String sampleURL, HttpServletRequest httpServletRequest) {

		return StringBundler.concat(
			"<a class=\"btn btn-primary\" href=\"", sampleURL, StringPool.QUOTE,
			StringPool.GREATER_THAN,
			_language.get(httpServletRequest, "download-sample-file"), "</a>");
	}

	@Reference
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private VirtualCPTypeHelper _virtualCPTypeHelper;

}