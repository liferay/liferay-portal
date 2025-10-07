/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.client.model.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.asah.connector.internal.client.model.Experiment;
import com.liferay.segments.asah.connector.internal.client.model.ExperimentStatus;
import com.liferay.segments.asah.connector.internal.client.model.ExperimentType;
import com.liferay.segments.asah.connector.internal.client.model.Goal;
import com.liferay.segments.asah.connector.internal.client.model.GoalMetric;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.exception.SegmentsExperimentGoalException;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;

/**
 * @author Sarai Díaz
 * @author David Arques
 */
public class ExperimentUtil {

	public static Experiment toExperiment(
			CompanyLocalService companyLocalService, String dataSourceId,
			GroupLocalService groupLocalService, Layout layout, Locale locale,
			Portal portal, SegmentsEntryLocalService segmentsEntryLocalService,
			SegmentsExperienceLocalService segmentsExperienceLocalService,
			SegmentsExperiment segmentsExperiment)
		throws PortalException {

		return toExperiment(
			_getChannelId(groupLocalService, layout), dataSourceId,
			SegmentsEntryConstants.getDefaultSegmentsEntryName(locale), layout,
			locale,
			_getLayoutFullURL(
				portal, companyLocalService, groupLocalService, layout),
			segmentsEntryLocalService, segmentsExperienceLocalService,
			segmentsExperiment);
	}

	public static Experiment toExperiment(
			CompanyLocalService companyLocalService, String dataSourceId,
			GroupLocalService groupLocalService,
			LayoutLocalService layoutLocalService, Locale locale, Portal portal,
			SegmentsEntryLocalService segmentsEntryLocalService,
			SegmentsExperienceLocalService segmentsExperienceLocalService,
			SegmentsExperiment segmentsExperiment)
		throws PortalException {

		return toExperiment(
			companyLocalService, dataSourceId, groupLocalService,
			layoutLocalService.getLayout(segmentsExperiment.getPlid()), locale,
			portal, segmentsEntryLocalService, segmentsExperienceLocalService,
			segmentsExperiment);
	}

	protected static Experiment toExperiment(
			String channelId, String dataSourceId,
			String defaultSegmentsEntryName, Layout layout, Locale locale,
			String pageURL, SegmentsEntryLocalService segmentsEntryLocalService,
			SegmentsExperienceLocalService segmentsExperienceLocalService,
			SegmentsExperiment segmentsExperiment)
		throws PortalException {

		Experiment experiment = new Experiment();

		experiment.setChannelId(channelId);
		experiment.setConfidenceLevel(
			segmentsExperiment.getConfidenceLevel() * 100);
		experiment.setCreateDate(segmentsExperiment.getCreateDate());
		experiment.setDataSourceId(dataSourceId);
		experiment.setDescription(segmentsExperiment.getDescription());
		experiment.setDXPGroupId(layout.getGroupId());
		experiment.setDXPLayoutId(layout.getUuid());

		List<SegmentsExperimentRel> segmentsExperimentRels =
			segmentsExperiment.getSegmentsExperimentRels();

		if (ListUtil.isNotEmpty(segmentsExperimentRels)) {
			experiment.setDXPVariants(
				DXPVariantUtil.toDXPVariantList(
					locale, segmentsExperienceLocalService,
					segmentsExperimentRels));
		}

		experiment.setExperimentStatus(
			_toExperimentStatus(segmentsExperiment.getStatus()));
		experiment.setExperimentType(
			ExperimentType.parse(segmentsExperiment.getType()));
		experiment.setGoal(_toExperimentGoal(segmentsExperiment));
		experiment.setId(segmentsExperiment.getSegmentsExperimentKey());
		experiment.setModifiedDate(segmentsExperiment.getModifiedDate());
		experiment.setName(segmentsExperiment.getName());
		experiment.setPageRelativePath(layout.getFriendlyURL());
		experiment.setPageTitle(layout.getTitle(locale));
		experiment.setPageURL(pageURL);
		experiment.setPublishable(true);

		if ((segmentsExperiment.getStatus() ==
				SegmentsExperimentConstants.STATUS_COMPLETED) ||
			((segmentsExperiment.getStatus() ==
				SegmentsExperimentConstants.STATUS_TERMINATED) &&
			 (segmentsExperiment.getWinnerSegmentsExperienceId() > 0))) {

			experiment.setPublishedDXPVariantId(
				_getSegmentsExperienceKey(
					segmentsExperienceLocalService.getSegmentsExperience(
						segmentsExperiment.getWinnerSegmentsExperienceId())));
		}

		SegmentsExperience segmentsExperience =
			segmentsExperienceLocalService.getSegmentsExperience(
				segmentsExperiment.getSegmentsExperienceId());

		experiment.setDXPExperienceId(
			_getSegmentsExperienceKey(segmentsExperience));
		experiment.setDXPExperienceName(segmentsExperience.getName(locale));

		SegmentsEntry segmentsEntry =
			segmentsEntryLocalService.fetchSegmentsEntry(
				segmentsExperience.getSegmentsEntryId());

		if (segmentsEntry == null) {
			experiment.setDXPSegmentId(SegmentsEntryConstants.KEY_DEFAULT);
			experiment.setDXPSegmentName(defaultSegmentsEntryName);
		}
		else {
			experiment.setDXPSegmentId(segmentsEntry.getSegmentsEntryKey());
			experiment.setDXPSegmentName(segmentsEntry.getName(locale));
		}

		return experiment;
	}

	private static String _getChannelId(
			GroupLocalService groupLocalService, Layout layout)
		throws PortalException {

		Group group = groupLocalService.getGroup(layout.getGroupId());

		return group.getTypeSettingsProperty("analyticsChannelId");
	}

	private static String _getLayoutFullURL(
			Portal portal, CompanyLocalService companyLocalService,
			GroupLocalService groupLocalService, Layout layout)
		throws PortalException {

		StringBundler sb = new StringBundler(4);

		Group group = groupLocalService.getGroup(layout.getGroupId());

		if (group.isLayout()) {
			long parentGroupId = group.getParentGroupId();

			if (parentGroupId > 0) {
				group = groupLocalService.getGroup(parentGroupId);
			}
		}

		String virtualHostname = null;

		LayoutSet layoutSet = layout.getLayoutSet();

		NavigableMap<String, String> virtualHostnames =
			layoutSet.getVirtualHostnames();

		if (!virtualHostnames.isEmpty()) {
			virtualHostname = virtualHostnames.firstKey();
		}
		else {
			Company company = companyLocalService.getCompany(
				layout.getCompanyId());

			virtualHostname = company.getVirtualHostname();
		}

		boolean secure = StringUtil.equalsIgnoreCase(
			Http.HTTPS, PropsValues.WEB_SERVER_PROTOCOL);

		sb.append(
			portal.getPortalURL(
				virtualHostname, portal.getPortalServerPort(secure), secure));

		if (layout.isPrivateLayout()) {
			sb.append(portal.getPathFriendlyURLPrivateGroup());
		}
		else {
			sb.append(portal.getPathFriendlyURLPublic());
		}

		sb.append(group.getFriendlyURL());
		sb.append(layout.getFriendlyURL());

		return sb.toString();
	}

	private static String _getSegmentsExperienceKey(
		SegmentsExperience segmentsExperience) {

		UnicodeProperties typeSettingsUnicodeProperties =
			segmentsExperience.getTypeSettingsUnicodeProperties();

		String segmentsExperimentSegmentsExperienceKey =
			typeSettingsUnicodeProperties.get(
				"segmentsExperimentSegmentsExperienceKey");

		if (Validator.isNotNull(segmentsExperimentSegmentsExperienceKey)) {
			return segmentsExperimentSegmentsExperienceKey;
		}

		return segmentsExperience.getSegmentsExperienceKey();
	}

	private static Goal _toExperimentGoal(SegmentsExperiment segmentsExperiment)
		throws SegmentsExperimentGoalException {

		SegmentsExperimentConstants.Goal goal =
			SegmentsExperimentConstants.Goal.parse(
				segmentsExperiment.getGoal());

		if (goal != null) {
			String goalName = goal.name();

			for (GoalMetric goalMetric : GoalMetric.values()) {
				if (goalName.equals(goalMetric.name())) {
					return new Goal(
						GoalMetric.valueOf(goalName),
						segmentsExperiment.getGoalTarget());
				}
			}
		}

		throw new SegmentsExperimentGoalException();
	}

	private static ExperimentStatus _toExperimentStatus(int status) {
		if (status == SegmentsExperimentConstants.STATUS_COMPLETED) {
			return ExperimentStatus.COMPLETED;
		}
		else if (status ==
					SegmentsExperimentConstants.STATUS_FINISHED_NO_WINNER) {

			return ExperimentStatus.FINISHED_NO_WINNER;
		}
		else if (status == SegmentsExperimentConstants.STATUS_FINISHED_WINNER) {
			return ExperimentStatus.FINISHED_WINNER;
		}
		else if (status == SegmentsExperimentConstants.STATUS_PAUSED) {
			return ExperimentStatus.PAUSED;
		}
		else if (status == SegmentsExperimentConstants.STATUS_RUNNING) {
			return ExperimentStatus.RUNNING;
		}
		else if (status == SegmentsExperimentConstants.STATUS_SCHEDULED) {
			return ExperimentStatus.SCHEDULED;
		}
		else if (status == SegmentsExperimentConstants.STATUS_TERMINATED) {
			return ExperimentStatus.TERMINATED;
		}

		return ExperimentStatus.DRAFT;
	}

}