/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.model.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsEntryLocalServiceUtil;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.segments.service.SegmentsExperimentLocalServiceUtil;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Eduardo García
 */
public class SegmentsExperienceImpl extends SegmentsExperienceBaseImpl {

	@Override
	public long getSegmentsEntryId() {
		if (hasDefaultSegmentsEntry()) {
			return SegmentsEntryConstants.ID_DEFAULT;
		}

		SegmentsEntry segmentsEntry = _getSegmentsEntry();

		if (segmentsEntry == null) {
			return SegmentsEntryConstants.ID_MISSING;
		}

		return segmentsEntry.getSegmentsEntryId();
	}

	@Override
	public String getSegmentsEntryName(Locale locale) {
		if (hasDefaultSegmentsEntry()) {
			return SegmentsEntryConstants.getDefaultSegmentsEntryName(locale);
		}

		SegmentsEntry segmentsEntry = _getSegmentsEntry();

		if (segmentsEntry == null) {
			return StringPool.BLANK;
		}

		return segmentsEntry.getName(locale);
	}

	@Override
	public UnicodeProperties getTypeSettingsUnicodeProperties() {
		if (_typeSettingsUnicodeProperties == null) {
			_typeSettingsUnicodeProperties = UnicodePropertiesBuilder.create(
				true
			).load(
				super.getTypeSettings()
			).build();
		}

		return _typeSettingsUnicodeProperties;
	}

	public boolean hasDefaultSegmentsEntry() {
		return Validator.isNull(getSegmentsEntryERC());
	}

	@Override
	public boolean hasSegmentsExperiment() {
		SegmentsExperience segmentsExperience =
			SegmentsExperienceLocalServiceUtil.fetchSegmentsExperience(
				getSegmentsExperienceId());

		SegmentsExperiment segmentsExperiment =
			SegmentsExperimentLocalServiceUtil.fetchSegmentsExperiment(
				getGroupId(), segmentsExperience.getSegmentsExperienceKey(),
				_getPublishedLayoutPlid());

		if ((segmentsExperiment == null) ||
			!ArrayUtil.contains(
				SegmentsExperimentConstants.Status.getLockedStatusValues(),
				segmentsExperiment.getStatus())) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isDefault() {
		return Objects.equals(
			getSegmentsExperienceKey(),
			SegmentsExperienceConstants.KEY_DEFAULT);
	}

	@Override
	public void setTypeSettingsUnicodeProperties(
		UnicodeProperties typeSettingsUnicodeProperties) {

		_typeSettingsUnicodeProperties = typeSettingsUnicodeProperties;

		super.setTypeSettings(_typeSettingsUnicodeProperties.toString());
	}

	private long _getPublishedLayoutPlid() {
		Layout layout = LayoutLocalServiceUtil.fetchLayout(getPlid());

		if ((layout != null) && layout.isDraftLayout()) {
			return layout.getClassPK();
		}

		return getPlid();
	}

	private SegmentsEntry _getSegmentsEntry() {
		Long groupId = ScopeUtil.getItemGroupId(
			getCompanyId(), getSegmentsEntryScopeERC(), getGroupId());

		if (groupId == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to resolve group ID for segments experience ",
						getSegmentsExperienceId(),
						" with segments entry scope external reference code ",
						getSegmentsEntryScopeERC()));
			}

			return null;
		}

		SegmentsEntry segmentsEntry =
			SegmentsEntryLocalServiceUtil.
				fetchSegmentsEntryByExternalReferenceCode(
					getSegmentsEntryERC(), groupId);

		if (segmentsEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to get segments entry with external reference ",
						"code ", getSegmentsEntryERC(), " and group ID ",
						groupId));
			}

			return null;
		}

		return segmentsEntry;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperienceImpl.class);

	private UnicodeProperties _typeSettingsUnicodeProperties;

}