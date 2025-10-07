/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class OrganizationVerticalCard extends BaseVerticalCard {

	public OrganizationVerticalCard(
		List<DropdownItem> actionDropdownItems, Organization organization,
		RenderRequest renderRequest, RowChecker rowChecker, PortletURL rowURL) {

		super(organization, renderRequest, rowChecker);

		_actionDropdownItems = actionDropdownItems;
		_organization = organization;
		_rowURL = rowURL;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return _actionDropdownItems;
	}

	@Override
	public String getHref() {
		if (_rowURL == null) {
			return null;
		}

		return _rowURL.toString();
	}

	@Override
	public String getIcon() {
		return "organizations";
	}

	@Override
	public String getImageSrc() {
		return _organization.getLogoURL();
	}

	@Override
	public List<LabelItem> getLabels() {
		if (FeatureFlagManagerUtil.isEnabled("LPD-35914")) {
			return LabelItemListBuilder.add(
				labelItem -> labelItem.setStatus(_organization.getStatus())
			).build();
		}

		return Collections.emptyList();
	}

	@Override
	public Map<String, String> getLabelStylesMap() {
		if (FeatureFlagManagerUtil.isEnabled("LPD-35914")) {
			return HashMapBuilder.put(
				"labelStylesMap",
				WorkflowConstants.getStatusStyle(_organization.getStatus())
			).build();
		}

		return Collections.emptyMap();
	}

	@Override
	public String getStickerCssClass() {
		return StringPool.BLANK;
	}

	@Override
	public String getStickerIcon() {
		return null;
	}

	@Override
	public String getStickerImageSrc() {
		return null;
	}

	@Override
	public String getSubtitle() {
		return LanguageUtil.get(
			themeDisplay.getLocale(), _organization.getType());
	}

	@Override
	public String getTitle() {
		return _organization.getName();
	}

	private final List<DropdownItem> _actionDropdownItems;
	private final Organization _organization;
	private final PortletURL _rowURL;

}