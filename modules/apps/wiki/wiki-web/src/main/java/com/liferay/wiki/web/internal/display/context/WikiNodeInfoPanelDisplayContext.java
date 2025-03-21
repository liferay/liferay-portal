/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.display.context;

import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.web.internal.display.context.helper.WikiNodeInfoPanelRequestHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Roberto Díaz
 */
public class WikiNodeInfoPanelDisplayContext {

	public WikiNodeInfoPanelDisplayContext(
		HttpServletRequest httpServletRequest) {

		_wikiNodeInfoPanelRequestHelper = new WikiNodeInfoPanelRequestHelper(
			httpServletRequest);
	}

	public WikiNode getFirstNode() {
		List<WikiNode> nodes = _wikiNodeInfoPanelRequestHelper.getNodes();

		if (nodes.isEmpty()) {
			return null;
		}

		return nodes.get(0);
	}

	public int getNodesCount() {
		return WikiNodeLocalServiceUtil.getNodesCount(
			_wikiNodeInfoPanelRequestHelper.getScopeGroupId());
	}

	public int getSelectedNodesCount() {
		List<?> items = _getSelectedNodes();

		return items.size();
	}

	public boolean isMultipleNodeSelection() {
		List<?> items = _getSelectedNodes();

		if (items.size() > 1) {
			return true;
		}

		return false;
	}

	public boolean isSingleNodeSelection() {
		List<WikiNode> nodes = _wikiNodeInfoPanelRequestHelper.getNodes();

		if (nodes.size() == 1) {
			return true;
		}

		return false;
	}

	private List<?> _getSelectedNodes() {
		return _wikiNodeInfoPanelRequestHelper.getNodes();
	}

	private final WikiNodeInfoPanelRequestHelper
		_wikiNodeInfoPanelRequestHelper;

}