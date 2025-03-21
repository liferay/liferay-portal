/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.field.customizer;

import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.field.Field;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizer;
import com.liferay.segments.item.selector.SegmentsEntryItemSelectorCriterion;
import com.liferay.segments.item.selector.SegmentsEntryItemSelectorReturnType;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryLocalService;

import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"segments.field.customizer.entity.name=Segment",
		"segments.field.customizer.key=" + SegmentsEntrySegmentsFieldCustomizer.KEY,
		"segments.field.customizer.priority:Integer=50"
	},
	service = SegmentsFieldCustomizer.class
)
public class SegmentsEntrySegmentsFieldCustomizer
	extends BaseSegmentsFieldCustomizer {

	public static final String KEY = "segment";

	@Override
	public ClassedModel getClassedModel(String fieldValue) {
		return _getSegmentsEntry(fieldValue);
	}

	@Override
	public String getClassName() {
		return SegmentsEntry.class.getName();
	}

	@Override
	public List<String> getFieldNames() {
		return _fieldNames;
	}

	@Override
	public String getFieldValueName(String fieldValue, Locale locale) {
		SegmentsEntry segmentsEntry = _getSegmentsEntry(fieldValue);

		if (segmentsEntry == null) {
			return null;
		}

		return segmentsEntry.getName(locale);
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Field.SelectEntity getSelectEntity(PortletRequest portletRequest) {
		try {
			SegmentsEntryItemSelectorCriterion
				segmentsEntryItemSelectorCriterion =
					new SegmentsEntryItemSelectorCriterion();

			segmentsEntryItemSelectorCriterion.
				setDesiredItemSelectorReturnTypes(
					Collections.singletonList(
						new SegmentsEntryItemSelectorReturnType()));

			long segmentsEntryId = ParamUtil.getLong(
				portletRequest, "segmentsEntryId");

			segmentsEntryItemSelectorCriterion.setExcludedSegmentsEntryIds(
				new long[] {segmentsEntryId});

			segmentsEntryItemSelectorCriterion.setExcludedSources(
				new String[] {
					StringUtil.toLowerCase(
						SegmentsEntryConstants.SOURCE_REFERRED)
				});

			return new Field.SelectEntity(
				"selectEntity",
				getSelectEntityTitle(
					_portal.getLocale(portletRequest),
					SegmentsEntry.class.getName()),
				String.valueOf(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							portletRequest),
						"selectEntity", segmentsEntryItemSelectorCriterion)),
				false);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get select entity", exception);
			}

			return null;
		}
	}

	private SegmentsEntry _getSegmentsEntry(String fieldValue) {
		long segmentsEntryId = GetterUtil.getLong(fieldValue);

		if (segmentsEntryId == 0) {
			return null;
		}

		return _segmentsEntryLocalService.fetchSegmentsEntry(segmentsEntryId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsEntrySegmentsFieldCustomizer.class);

	private static final List<String> _fieldNames = ListUtil.fromArray(
		"segmentsEntryIds");

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

}