/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.converter;

import com.liferay.headless.admin.user.dto.v1_0.Segment;
import com.liferay.headless.admin.user.internal.constants.SegmentsSourceConstants;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.segments.model.SegmentsEntry"
	},
	service = DTOConverter.class
)
public class SegmentDTOConverter
	implements DTOConverter<SegmentsEntry, Segment> {

	@Override
	public String getContentType() {
		return SegmentsEntry.class.getSimpleName();
	}

	@Override
	public Segment toDTO(SegmentsEntry segmentsEntry) {
		return new Segment() {
			{
				setActive(segmentsEntry::isActive);
				setCriteria(
					() -> {
						String criteria = segmentsEntry.getCriteria();

						if (!criteria.isEmpty()) {
							return segmentsEntry.getCriteria();
						}

						return null;
					});
				setCriteriaValue(
					() -> {
						String criteria = segmentsEntry.getCriteria();

						if (criteria.isEmpty()) {
							return null;
						}

						try {
							return _toMap(
								_jsonFactory.createJSONObject(
									segmentsEntry.getCriteria()));
						}
						catch (JSONException jsonException) {
							if (_log.isWarnEnabled()) {
								_log.warn(jsonException);
							}
						}

						return null;
					});
				setDateCreated(segmentsEntry::getCreateDate);
				setDateModified(segmentsEntry::getModifiedDate);
				setId(segmentsEntry::getSegmentsEntryId);
				setName(
					() -> segmentsEntry.getName(
						segmentsEntry.getDefaultLanguageId()));
				setSiteId(segmentsEntry::getGroupId);
				setSource(
					() -> {
						if (StringUtil.equals(
								segmentsEntry.getSource(),
								SegmentsEntryConstants.
									SOURCE_ASAH_FARO_BACKEND)) {

							return SegmentsSourceConstants.
								SOURCE_ASAH_FARO_BACKEND;
						}
						else if (StringUtil.equals(
									segmentsEntry.getSource(),
									SegmentsEntryConstants.SOURCE_REFERRED)) {

							return SegmentsSourceConstants.SOURCE_REFERRED;
						}

						return SegmentsSourceConstants.SOURCE_DEFAULT;
					});
			}
		};
	}

	private Map<String, Object> _toMap(JSONObject jsonObject) {
		Map<String, Object> map = new HashMap<>();

		for (String key : jsonObject.keySet()) {
			if (jsonObject.getJSONObject(key) != null) {
				map.put(key, _toMap(jsonObject.getJSONObject(key)));
			}
			else {
				map.put(key, jsonObject.get(key));
			}
		}

		return map;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentDTOConverter.class);

	@Reference
	private JSONFactory _jsonFactory;

}