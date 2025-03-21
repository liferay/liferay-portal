/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.item.selector.web.internal;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.organizations.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.OrganizationVerticalCard;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Organization;

import jakarta.portlet.RenderRequest;

import java.util.Locale;

/**
 * @author Eudaldo Aloso
 */
public class OrganizationItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public OrganizationItemDescriptor(Organization organization) {
		_organization = organization;
	}

	@Override
	public String getIcon() {
		return "organizations";
	}

	@Override
	public String getImageURL() {
		return _organization.getLogoURL();
	}

	@Override
	public String getPayload() {
		return JSONUtil.put(
			"name", _organization.getName()
		).put(
			"organizationId", String.valueOf(_organization.getOrganizationId())
		).put(
			"type", _organization.getType()
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return _organization.getName();
	}

	@Override
	public VerticalCard getVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker) {

		return new OrganizationVerticalCard(
			_organization, renderRequest, rowChecker);
	}

	private final Organization _organization;

}