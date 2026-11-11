/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.fragment.renderer;

import com.liferay.content.marketing.platform.web.internal.util.ObjectEntryUtil;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Larissa Ribeiro
 */
@Component(service = FragmentRenderer.class)
public class DueDateAlertComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	protected String getComponentName(HttpServletRequest httpServletRequest) {
		return "DueDateAlert";
	}

	@Override
	protected String getLabelKey() {
		return "due-date-alert";
	}

	@Override
	protected String getModuleName() {
		return "content-marketing-platform-web";
	}

	@Override
	protected Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryUtil.getObjectEntry(
			httpServletRequest);

		if ((objectEntry == null) ||
			!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-107678")) {

			return Collections.emptyMap();
		}

		ObjectEntry parentCMPTaskObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				MapUtil.getLong(
					objectEntry.getValues(),
					"r_cmpTaskToCMPTasks_c_cmpTaskId"));

		if (parentCMPTaskObjectEntry == null) {
			return Collections.emptyMap();
		}

		String dueDate = MapUtil.getString(
			parentCMPTaskObjectEntry.getValues(), "dueDate");

		if (Validator.isNull(dueDate)) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"parentDueDate",
			DateUtil.getDate(
				DateUtil.parseDate(
					ObjectFieldUtil.getDateTimePattern(dueDate), dueDate,
					LocaleUtil.US),
				"yyyy-MM-dd", LocaleUtil.US)
		).build();
	}

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}