/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.field.customizer;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.field.Field;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizer;
import com.liferay.site.teams.item.selector.SiteTeamsItemSelectorCriterion;

import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"segments.field.customizer.entity.name=User",
		"segments.field.customizer.key=" + TeamSegmentsFieldCustomizer.KEY,
		"segments.field.customizer.priority:Integer=50"
	},
	service = SegmentsFieldCustomizer.class
)
public class TeamSegmentsFieldCustomizer extends BaseSegmentsFieldCustomizer {

	public static final String KEY = "team";

	@Override
	public ClassedModel getClassedModel(String fieldValue) {
		return _getTeam(fieldValue);
	}

	@Override
	public String getClassName() {
		return Team.class.getName();
	}

	@Override
	public List<String> getFieldNames() {
		return _fieldNames;
	}

	@Override
	public String getFieldValueName(String fieldValue, Locale locale) {
		Team team = _getTeam(fieldValue);

		if (team == null) {
			return fieldValue;
		}

		return team.getName();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Field.SelectEntity getSelectEntity(PortletRequest portletRequest) {
		try {
			SiteTeamsItemSelectorCriterion siteTeamsItemSelectorCriterion =
				new SiteTeamsItemSelectorCriterion();

			siteTeamsItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
				new UUIDItemSelectorReturnType());

			return new Field.SelectEntity(
				"selectEntity",
				getSelectEntityTitle(
					_portal.getLocale(portletRequest), Team.class.getName()),
				String.valueOf(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							portletRequest),
						"selectEntity", siteTeamsItemSelectorCriterion)),
				false);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get select entity", exception);
			}

			return null;
		}
	}

	private Team _getTeam(String fieldValue) {
		long teamId = GetterUtil.getLong(fieldValue);

		if (teamId == 0) {
			return null;
		}

		return _teamLocalService.fetchTeam(teamId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TeamSegmentsFieldCustomizer.class);

	private static final List<String> _fieldNames = ListUtil.fromArray(
		"teamIds");

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

	@Reference
	private TeamLocalService _teamLocalService;

}