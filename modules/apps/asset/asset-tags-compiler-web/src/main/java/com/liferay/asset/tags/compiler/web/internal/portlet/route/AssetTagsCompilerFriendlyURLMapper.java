/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.compiler.web.internal.portlet.route;

import com.liferay.asset.tags.compiler.web.internal.configuration.AssetTagsCompilerWebConfigurationValues;
import com.liferay.asset.tags.compiler.web.internal.constants.AssetTagsCompilerPortletKeys;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.BaseFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
@Component(
	property = "jakarta.portlet.name=" + AssetTagsCompilerPortletKeys.ASSET_TAGS_COMPILER,
	service = FriendlyURLMapper.class
)
public class AssetTagsCompilerFriendlyURLMapper extends BaseFriendlyURLMapper {

	@Override
	public String buildPath(LiferayPortletURL liferayPortletURL) {
		return null;
	}

	@Override
	public String getMapping() {
		return _MAPPING;
	}

	@Override
	public boolean isCheckMappingWithPrefix() {
		return _CHECK_MAPPING_WITH_PREFIX;
	}

	@Override
	public void populateParams(
		String friendlyURLPath, Map<String, String[]> parameterMap,
		Map<String, Object> requestContext) {

		if (AssetTagsCompilerWebConfigurationValues.ENABLED) {
			addParameter(parameterMap, "p_p_id", getPortletId());
			addParameter(parameterMap, "p_p_lifecycle", "0");
			addParameter(parameterMap, "p_p_state", WindowState.NORMAL);
			addParameter(parameterMap, "p_p_mode", PortletMode.VIEW);

			int x = friendlyURLPath.indexOf(CharPool.SLASH, 1);
			int y = friendlyURLPath.length();

			String[] entries = StringUtil.split(
				friendlyURLPath.substring(x + 1, y), CharPool.SLASH);

			if (entries.length > 0) {
				addParameter(parameterMap, "tags", entries);
			}
			else {
				addParameter(parameterMap, "tags", StringPool.BLANK);
			}
		}
	}

	private static final boolean _CHECK_MAPPING_WITH_PREFIX = false;

	private static final String _MAPPING = "tags";

}