/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.constants.SynonymsPortletKeys;
import com.liferay.portal.search.tuning.synonyms.web.internal.display.context.EditSynonymSetsDisplayBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Filipe Oshiro
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SynonymsPortletKeys.SYNONYMS,
		"mvc.command.name=/synonyms/edit_synonym_sets"
	},
	service = MVCRenderCommand.class
)
public class EditSynonymSetsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		EditSynonymSetsDisplayBuilder editSynonymSetsDisplayBuilder =
			new EditSynonymSetsDisplayBuilder(
				_portal.getHttpServletRequest(renderRequest), _portal,
				renderRequest, renderResponse, _synonymSetIndexNameBuilder,
				_synonymSetIndexReader);

		renderRequest.setAttribute(
			SynonymsPortletKeys.EDIT_SYNONYM_SET_DISPLAY_CONTEXT,
			editSynonymSetsDisplayBuilder.build());

		return "/edit_synonym_sets.jsp";
	}

	@Activate
	protected void activate() {
		_synonymSetIndexReader = new SynonymSetIndexReader(
			_searchEngineAdapter);
	}

	@Reference
	private Portal _portal;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

	private SynonymSetIndexReader _synonymSetIndexReader;

}