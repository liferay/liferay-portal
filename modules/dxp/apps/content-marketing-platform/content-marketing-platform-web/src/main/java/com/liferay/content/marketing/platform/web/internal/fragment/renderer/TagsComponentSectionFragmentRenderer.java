/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.fragment.renderer;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.content.marketing.platform.web.internal.util.ObjectEntryUtil;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = FragmentRenderer.class)
public class TagsComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	protected String getComponentName(HttpServletRequest httpServletRequest) {
		return "Tags";
	}

	@Override
	protected String getLabelKey() {
		return "tags";
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

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"cmsGroupId", themeDisplay.getScopeGroupId()
		).put(
			"hasUpdatePermission",
			() -> {
				ModelResourcePermission<ObjectEntry> modelResourcePermission =
					_objectEntryService.getModelResourcePermission(
						objectEntry.getObjectDefinitionId());

				return modelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), objectEntry,
					ActionKeys.UPDATE);
			}
		).put(
			"objectEntryKeywords",
			ListUtil.toArray(
				_assetTagLocalService.getTags(
					objectEntry.getModelClassName(),
					objectEntry.getObjectEntryId()),
				AssetTag.NAME_ACCESSOR)
		).put(
			"projectGroupId", ObjectEntryUtil.getProjectGroupId(objectEntry)
		).build();
	}

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}