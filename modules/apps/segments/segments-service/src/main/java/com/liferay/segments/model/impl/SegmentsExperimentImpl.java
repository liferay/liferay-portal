/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.model.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsEntryLocalServiceUtil;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.segments.service.SegmentsExperimentRelLocalServiceUtil;

import java.util.List;
import java.util.Locale;

/**
 * The extended model implementation for the SegmentsExperiment service.
 * Represents a row in the &quot;SegmentsExperiment&quot; database table, with
 * each column mapped to a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class.
 * Whenever methods are added, rerun ServiceBuilder to copy their definitions
 * into the <code>com.liferay.segments.model.SegmentsExperiment</code>
 * interface.
 * </p>
 *
 * @author Eduardo García
 */
public class SegmentsExperimentImpl extends SegmentsExperimentBaseImpl {

	@Override
	public double getConfidenceLevel() {
		UnicodeProperties typeSettingsUnicodeProperties =
			getTypeSettingsProperties();

		return GetterUtil.getDouble(
			typeSettingsUnicodeProperties.getProperty("confidenceLevel"));
	}

	@Override
	public String getGoal() {
		UnicodeProperties typeSettingsUnicodeProperties =
			getTypeSettingsProperties();

		return GetterUtil.getString(
			typeSettingsUnicodeProperties.getProperty("goal"));
	}

	@Override
	public String getGoalTarget() {
		UnicodeProperties typeSettingsUnicodeProperties =
			getTypeSettingsProperties();

		return GetterUtil.getString(
			typeSettingsUnicodeProperties.getProperty("goalTarget"));
	}

	@Override
	public String getSegmentsEntryName(Locale locale) throws PortalException {
		SegmentsExperience segmentsExperience =
			SegmentsExperienceLocalServiceUtil.getSegmentsExperience(
				getSegmentsExperienceId());

		if (segmentsExperience.hasDefaultSegmentsEntry()) {
			return SegmentsEntryConstants.getDefaultSegmentsEntryName(locale);
		}

		Long groupId = ScopeUtil.getItemGroupId(
			segmentsExperience.getCompanyId(),
			segmentsExperience.getSegmentsEntryScopeERC(),
			segmentsExperience.getGroupId());

		if (groupId == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to resolve group ID for segments experience ",
						segmentsExperience.getSegmentsExperienceId(),
						" with segments entry scope external reference code ",
						segmentsExperience.getSegmentsEntryScopeERC()));
			}

			return StringPool.BLANK;
		}

		SegmentsEntry segmentsEntry =
			SegmentsEntryLocalServiceUtil.
				fetchSegmentsEntryByExternalReferenceCode(
					segmentsExperience.getSegmentsEntryERC(), groupId);

		if (segmentsEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to get segments entry with external reference ",
						"code ", segmentsExperience.getSegmentsEntryERC(),
						" and group ID ", groupId));
			}

			return StringPool.BLANK;
		}

		return segmentsEntry.getName(locale);
	}

	@Override
	public String getSegmentsExperienceKey() {
		SegmentsExperience segmentsExperience =
			SegmentsExperienceLocalServiceUtil.fetchSegmentsExperience(
				getSegmentsExperienceId());

		if (segmentsExperience == null) {
			return null;
		}

		return segmentsExperience.getSegmentsExperienceKey();
	}

	@Override
	public List<SegmentsExperimentRel> getSegmentsExperimentRels() {
		return SegmentsExperimentRelLocalServiceUtil.getSegmentsExperimentRels(
			getSegmentsExperimentId());
	}

	@Override
	public String getType() {
		UnicodeProperties typeSettingsUnicodeProperties =
			getTypeSettingsProperties();

		return GetterUtil.getString(
			typeSettingsUnicodeProperties.getProperty("type"));
	}

	@Override
	public UnicodeProperties getTypeSettingsProperties() {
		if (_typeSettingsUnicodeProperties == null) {
			_typeSettingsUnicodeProperties = UnicodePropertiesBuilder.create(
				true
			).load(
				super.getTypeSettings()
			).build();
		}

		return _typeSettingsUnicodeProperties;
	}

	@Override
	public long getWinnerSegmentsExperienceId() {
		UnicodeProperties typeSettingsUnicodeProperties =
			getTypeSettingsProperties();

		return GetterUtil.getLong(
			typeSettingsUnicodeProperties.getProperty(
				"winnerSegmentsExperienceId"),
			-1);
	}

	@Override
	public String getWinnerSegmentsExperienceKey() {
		long winnerSegmentsExperienceId = getWinnerSegmentsExperienceId();

		if (winnerSegmentsExperienceId < 0) {
			return StringPool.BLANK;
		}

		SegmentsExperience winnerSegmentsExperience =
			SegmentsExperienceLocalServiceUtil.fetchSegmentsExperience(
				winnerSegmentsExperienceId);

		return winnerSegmentsExperience.getSegmentsExperienceKey();
	}

	@Override
	public void setTypeSettings(String typeSettings) {
		super.setTypeSettings(typeSettings);

		_typeSettingsUnicodeProperties = null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperimentImpl.class);

	private UnicodeProperties _typeSettingsUnicodeProperties;

}