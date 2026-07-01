/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.site.generator.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Mylena Monte
 */
public class ViewGenerationsDisplayContext {

	public ViewGenerationsDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public String getAPIURL() {
		return "/o/content-site-generator/generations";
	}

	public List<DropdownItem> getBulkActionDropdownItems() throws Exception {
		return List.of(
			new FDSActionDropdownItem(
				getAPIURL() + "/{id}", "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "delete",
				"delete", "async"));
	}

	public CreationMenu getCreationMenu() throws Exception {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(_getViewIdeateStepRenderURL());
				dropdownItem.setIcon("stars");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new-generation"));
			}
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_httpServletRequest, "create-a-new-generation-to-get-started")
		).put(
			"title", LanguageUtil.get(_httpServletRequest, "no-generations-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return List.of(
			new FDSActionDropdownItem(
				_getViewRefineStepRenderURL(), "view", "view",
				LanguageUtil.get(_httpServletRequest, "view"), "get", null,
				null),
			new FDSActionDropdownItem(
				getAPIURL() + "/{id}", "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "delete",
				"delete", "async"));
	}

	public long getGenerationId() {
		long generationId = ParamUtil.getLong(
			_httpServletRequest, "generationId");

		if (generationId > 0) {
			return generationId;
		}

		return ParamUtil.getLong(
			PortalUtil.getOriginalServletRequest(_httpServletRequest),
			"generationId");
	}

	public Map<String, Object> getWizardProps() {
		return HashMapBuilder.<String, Object>put(
			"apiURL", getAPIURL()
		).put(
			"generationId",
			() -> {
				long generationId = getGenerationId();

				if (generationId > 0) {
					return generationId;
				}

				return null;
			}
		).put(
			"generationsURL", _getViewGenerationsRenderURL()
		).build();
	}

	private String _getViewGenerationsRenderURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/view.jsp"
		).buildString();
	}

	private String _getViewIdeateStepRenderURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/view_wizard.jsp"
		).buildString();
	}

	private String _getViewRefineStepRenderURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/view_wizard.jsp"
		).setParameter(
			"generationId", "{id}"
		).buildString();
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}