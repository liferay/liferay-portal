/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.internal.item.filter;

import com.liferay.info.filter.InfoFilterProvider;
import com.liferay.info.filter.TagsInfoFilter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pablo Molina
 */
@Component(service = InfoFilterProvider.class)
public class TagsInfoFilterProvider
	implements InfoFilterProvider<TagsInfoFilter> {

	@Override
	public TagsInfoFilter create(Map<String, String[]> values) {
		TagsInfoFilter tagsInfoFilter = new TagsInfoFilter();

		tagsInfoFilter.setTagNames(_getAssetTagNames(values));

		return tagsInfoFilter;
	}

	private String[][] _getAssetTagNames(Map<String, String[]> values) {
		Set<String[]> assetTagIds = new HashSet<>();

		for (Map.Entry<String, String[]> entry : values.entrySet()) {
			if (!StringUtil.startsWith(
					entry.getKey(),
					TagsInfoFilter.FILTER_TYPE_NAME + StringPool.UNDERLINE)) {

				continue;
			}

			assetTagIds.add(
				ArrayUtil.filter(
					GetterUtil.getStringValues(entry.getValue()),
					tagName -> Objects.nonNull(tagName)));
		}

		return assetTagIds.toArray(new String[assetTagIds.size()][]);
	}

}