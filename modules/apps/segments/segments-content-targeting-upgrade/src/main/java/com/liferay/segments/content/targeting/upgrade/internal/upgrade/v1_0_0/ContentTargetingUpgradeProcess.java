/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0;

import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.BrowseRuleConverter;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.CustomFieldRuleConverter;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.LanguageRuleConverter;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.LastLoginDateRuleConverter;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.OSRuleConverter;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.OrganizationMemberRuleConverter;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.PreviousVisitedSiteRuleConverter;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.RegularRoleRuleConverter;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.RuleConverter;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.SiteMemberRuleConverter;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.UserGroupMemberRuleConverter;
import com.liferay.segments.content.targeting.upgrade.internal.upgrade.v1_0_0.util.UserLoggedRuleConverter;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.service.SegmentsEntryLocalService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Eduardo García
 */
public class ContentTargetingUpgradeProcess extends UpgradeProcess {

	public ContentTargetingUpgradeProcess(
		ExpandoColumnLocalService expandoColumnLocalService,
		ExpandoTableLocalService expandoTableLocalService,
		JSONFactory jsonFactory,
		SegmentsEntryLocalService segmentsEntryLocalService,
		SegmentsCriteriaContributor userOrganizationSegmentsCriteriaContributor,
		SegmentsCriteriaContributor userSegmentsCriteriaContributor) {

		_segmentsEntryLocalService = segmentsEntryLocalService;

		_ruleConverters.put(
			BrowseRuleConverter.RULE_CONVERTER_KEY, new BrowseRuleConverter());
		_ruleConverters.put(
			CustomFieldRuleConverter.RULE_CONVERTER_KEY,
			new CustomFieldRuleConverter(
				expandoColumnLocalService, expandoTableLocalService,
				jsonFactory, userSegmentsCriteriaContributor));
		_ruleConverters.put(
			LanguageRuleConverter.RULE_CONVERTER_KEY,
			new LanguageRuleConverter());
		_ruleConverters.put(
			LastLoginDateRuleConverter.RULE_CONVERTER_KEY,
			new LastLoginDateRuleConverter(jsonFactory));
		_ruleConverters.put(
			OrganizationMemberRuleConverter.RULE_CONVERTER_KEY,
			new OrganizationMemberRuleConverter(
				userOrganizationSegmentsCriteriaContributor));
		_ruleConverters.put(
			OSRuleConverter.RULE_CONVERTER_KEY, new OSRuleConverter());
		_ruleConverters.put(
			PreviousVisitedSiteRuleConverter.RULE_CONVERTER_KEY,
			new PreviousVisitedSiteRuleConverter(jsonFactory));
		_ruleConverters.put(
			RegularRoleRuleConverter.RULE_CONVERTER_KEY,
			new RegularRoleRuleConverter(userSegmentsCriteriaContributor));
		_ruleConverters.put(
			SiteMemberRuleConverter.RULE_CONVERTER_KEY,
			new SiteMemberRuleConverter(userSegmentsCriteriaContributor));
		_ruleConverters.put(
			UserGroupMemberRuleConverter.RULE_CONVERTER_KEY,
			new UserGroupMemberRuleConverter(userSegmentsCriteriaContributor));
		_ruleConverters.put(
			UserLoggedRuleConverter.RULE_CONVERTER_KEY,
			new UserLoggedRuleConverter());
	}

	@Override
	public void doUpgrade() throws Exception {
		if (!hasTable("CT_UserSegment")) {
			return;
		}

		_upgradeContentTargetingUserSegments();
		_deleteContentTargetingData();
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropTables(
				"CT_AU_AnonymousUser", "CT_Analytics_AnalyticsEvent",
				"CT_Analytics_AnalyticsReferrer", "CT_AnonymousUserUserSegment",
				"CT_CCR_CampaignContent", "CT_CTA_CTAction",
				"CT_CTA_CTActionTotal", "CT_Campaign",
				"CT_Campaigns_UserSegments", "CT_ChannelInstance",
				"CT_ReportInstance", "CT_RuleInstance",
				"CT_ScorePoints_ScorePoint", "CT_Tactic",
				"CT_Tactics_UserSegments", "CT_TrackingActionInstance",
				"CT_USCR_UserSegmentContent", "CT_UserSegment",
				"CT_Visited_ContentVisited", "CT_Visited_PageVisited")
		};
	}

	private void _deleteContentTargetingData() throws Exception {
		runSQL(
			"delete from ClassName_ where value like '" + _CT_PACKAGE_NAME +
				"%'");

		runSQL(
			"delete from Release_ where servletContextName like '" +
				_CT_PACKAGE_NAME + "%'");

		runSQL(
			"delete from ResourceAction where name like '" + _CT_PACKAGE_NAME +
				"%'");

		runSQL(
			"delete from ResourcePermission where name like '" +
				_CT_PACKAGE_NAME + "%'");

		runSQL("delete from ServiceComponent where buildNamespace like 'CT%'");
	}

	private String _getCriteria(long userSegmentId) throws Exception {
		Criteria criteria = new Criteria();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select companyId, ruleKey, typeSettings from " +
					"CT_RuleInstance where userSegmentId = ?")) {

			preparedStatement.setLong(1, userSegmentId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String ruleKey = resultSet.getString("ruleKey");

					RuleConverter ruleConverter = _ruleConverters.get(ruleKey);

					if (ruleConverter == null) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to perform automated update of rule " +
									ruleKey);
						}

						continue;
					}

					long companyId = resultSet.getLong("companyId");
					String typeSettings = resultSet.getString("typeSettings");

					ruleConverter.convert(companyId, criteria, typeSettings);
				}
			}
		}

		return CriteriaSerializer.serialize(criteria);
	}

	private void _upgradeContentTargetingUserSegments() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from CT_UserSegment");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			ServiceContext serviceContext = new ServiceContext();

			while (resultSet.next()) {
				long userSegmentId = resultSet.getLong("userSegmentId");

				if (_log.isInfoEnabled()) {
					_log.info(
						"Upgrading Content Targeting User Segment " +
							userSegmentId);
				}

				String name = resultSet.getString("name");

				Map<Locale, String> nameMap =
					LocalizationUtil.getLocalizationMap(name);

				Map<Locale, String> descriptionMap =
					LocalizationUtil.getLocalizationMap(
						resultSet.getString("description"));

				serviceContext.setScopeGroupId(resultSet.getLong("groupId"));
				serviceContext.setUserId(
					PortalUtil.getValidUserId(
						resultSet.getLong("companyId"),
						resultSet.getLong("userId")));

				Locale defaultLocale = LocaleUtil.fromLanguageId(
					LocalizationUtil.getDefaultLanguageId(name));

				Locale currentDefaultLocale =
					LocaleThreadLocal.getSiteDefaultLocale();

				try {
					LocaleThreadLocal.setSiteDefaultLocale(defaultLocale);

					_segmentsEntryLocalService.addSegmentsEntry(
						null, "ct_" + userSegmentId, nameMap, descriptionMap,
						true, _getCriteria(userSegmentId),
						SegmentsEntryConstants.SOURCE_DEFAULT, serviceContext);
				}
				finally {
					LocaleThreadLocal.setSiteDefaultLocale(
						currentDefaultLocale);
				}
			}
		}
	}

	private static final String _CT_PACKAGE_NAME =
		"com.liferay.content.targeting";

	private static final Log _log = LogFactoryUtil.getLog(
		ContentTargetingUpgradeProcess.class);

	private final Map<String, RuleConverter> _ruleConverters = new HashMap<>();
	private final SegmentsEntryLocalService _segmentsEntryLocalService;

}