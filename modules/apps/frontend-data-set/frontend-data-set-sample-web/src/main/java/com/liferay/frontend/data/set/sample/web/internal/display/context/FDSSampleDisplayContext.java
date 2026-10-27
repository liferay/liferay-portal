/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.display.context;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.FDSVisualizationModeCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.frontend.data.set.sample.web.internal.display.context.helper.FDSRequestHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Javier Gamarra
 * @author Javier de Arcos
 */
public class FDSSampleDisplayContext {

	public FDSSampleDisplayContext(
		CETManager cetManager, HttpServletRequest httpServletRequest,
		RenderResponse renderResponse) {

		_cetManager = cetManager;
		_renderResponse = renderResponse;

		_fdsRequestHelper = new FDSRequestHelper(httpServletRequest);
	}

	public String getAPIURL() {
		return "/o/c/fdssamples?sort=title:asc";
	}

	public CreationMenu getCreationMenu() throws Exception {
		return new CreationMenu();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"filtered",
			JSONUtil.put(
				"search",
				JSONUtil.put(
					"description",
					LanguageUtil.get(
						_fdsRequestHelper.getRequest(), "custom-description")
				).put(
					"image", "/states/empty_state.svg"
				).put(
					"imageReducedMotion",
					"/states/empty_state_reduced_motion.svg"
				).put(
					"title",
					LanguageUtil.get(
						_fdsRequestHelper.getRequest(), "custom-title")
				))
		).build();
	}

	public Map<String, Object> getFDSVisualizationModeCETAdditionalProps()
		throws Exception {

		FDSVisualizationModeCET fdsVisualizationModeCET =
			_getFDSVisualizationModeCET();

		if (fdsVisualizationModeCET == null) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"fdsVisualizationModeCET",
			HashMapBuilder.put(
				"externalReferenceCode",
				fdsVisualizationModeCET.getExternalReferenceCode()
			).put(
				"icon", fdsVisualizationModeCET.getIcon()
			).put(
				"label",
				fdsVisualizationModeCET.getName(_fdsRequestHelper.getLocale())
			).put(
				"url", fdsVisualizationModeCET.getURL()
			).build()
		).build();
	}

	private FDSVisualizationModeCET _getFDSVisualizationModeCET()
		throws Exception {

		List<FDSVisualizationModeCET> fdsVisualizationModeCETs =
			(List)_cetManager.getCETs(
				CompanyThreadLocal.getCompanyId(), null,
				ClientExtensionEntryConstants.TYPE_FDS_VISUALIZATION_MODE,
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null);

		for (FDSVisualizationModeCET fdsVisualizationModeCET :
				fdsVisualizationModeCETs) {

			if (!fdsVisualizationModeCET.isReadOnly()) {
				return fdsVisualizationModeCET;
			}
		}

		for (FDSVisualizationModeCET fdsVisualizationModeCET :
				fdsVisualizationModeCETs) {

			if (Objects.equals(
					fdsVisualizationModeCET.getExternalReferenceCode(),
					"LXC:liferay-sample-fds-visualization-mode")) {

				return fdsVisualizationModeCET;
			}
		}

		return null;
	}

	private final CETManager _cetManager;
	private final FDSRequestHelper _fdsRequestHelper;
	private final RenderResponse _renderResponse;

}