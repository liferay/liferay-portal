/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.fragment.renderer;

import com.liferay.content.marketing.platform.web.internal.util.ObjectEntryUtil;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = FragmentRenderer.class)
public class HistoryComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "history";
	}

	@Override
	protected String getComponentName(HttpServletRequest httpServletRequest) {
		ObjectEntry objectEntry = ObjectEntryUtil.getObjectEntry(
			httpServletRequest);

		if (objectEntry == null) {
			return null;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (StringUtil.equals(
				objectDefinition.getExternalReferenceCode(), "L_CMP_PROJECT")) {

			return "ProjectHistory";
		}

		return "TaskHistory";
	}

	@Override
	protected String getLabelKey() {
		return "history[record]";
	}

	@Override
	protected String getModuleName() {
		return "content-marketing-platform-web";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		ObjectEntry objectEntry = ObjectEntryUtil.getObjectEntry(
			httpServletRequest);

		if (objectEntry == null) {
			return null;
		}

		return HashMapBuilder.<String, Object>put(
			"apiURL",
			() -> {
				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.fetchObjectDefinition(
						objectEntry.getObjectDefinitionId());

				return StringBundler.concat(
					"/o", objectDefinition.getRESTContextPath(),
					StringPool.SLASH, objectEntry.getObjectEntryId(),
					"?fields=auditEvents&nestedFields=auditEvents");
			}
		).build();
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}