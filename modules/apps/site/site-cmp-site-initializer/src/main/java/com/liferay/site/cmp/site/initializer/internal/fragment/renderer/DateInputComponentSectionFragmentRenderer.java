/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.input.template.parser.FragmentEntryInputTemplateNodeContextHelper;
import com.liferay.fragment.input.template.parser.InputTemplateNode;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Franca
 */
@Component(service = FragmentRenderer.class)
public class DateInputComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "INPUTS";
	}

	@Override
	public int getType() {
		return FragmentConstants.TYPE_INPUT;
	}

	@Override
	public String getTypeOptions() {
		return JSONUtil.put(
			"fieldTypes", JSONUtil.putAll("date")
		).toString();
	}

	@Override
	protected String getComponentName(HttpServletRequest httpServletRequest) {
		return "DateInput";
	}

	@Override
	protected String getLabelKey() {
		return "date";
	}

	@Override
	protected String getModuleName() {
		return "site-cmp-site-initializer";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		InputTemplateNode inputTemplateNode =
			_fragmentEntryInputTemplateNodeContextHelper.toInputTemplateNode(
				fragmentRendererContext.getAttributes(),
				language.get(fragmentRendererContext.getLocale(), "date"),
				fragmentRendererContext.getFragmentEntryLink(),
				httpServletRequest, fragmentRendererContext.getInfoForm(),
				fragmentRendererContext.getLocale());

		JSONObject jsonObject = inputTemplateNode.toJSONObject();

		return HashMapBuilder.<String, Object>put(
			"editMode", fragmentRendererContext.isEditMode()
		).putAll(
			jsonObject.toMap()
		).build();
	}

	@Reference
	private FragmentEntryInputTemplateNodeContextHelper
		_fragmentEntryInputTemplateNodeContextHelper;

}