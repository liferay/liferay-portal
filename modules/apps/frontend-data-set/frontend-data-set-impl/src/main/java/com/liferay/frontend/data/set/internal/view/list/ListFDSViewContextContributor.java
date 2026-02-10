/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.view.list;

import com.liferay.frontend.data.set.constants.FDSConstants;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewContextContributor;
import com.liferay.frontend.data.set.view.list.BaseListFDSView;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marco Leo
 */
@Component(
	property = "frontend.data.set.view.name=" + FDSConstants.LIST,
	service = FDSViewContextContributor.class
)
public class ListFDSViewContextContributor
	implements FDSViewContextContributor {

	@Override
	public Map<String, Object> getFDSViewContext(
		FDSView fdsView, Locale locale) {

		if (fdsView instanceof BaseListFDSView) {
			return _serialize((BaseListFDSView)fdsView);
		}

		return Collections.emptyMap();
	}

	private Map<String, Object> _serialize(BaseListFDSView baseListFDSView) {
		return HashMapBuilder.<String, Object>put(
			"schema",
			HashMapBuilder.<String, Object>put(
				"description", baseListFDSView.getDescription()
			).put(
				"image", baseListFDSView.getImage()
			).put(
				"sticker", baseListFDSView.getSticker()
			).put(
				"symbol", baseListFDSView.getSymbol()
			).put(
				"tags", baseListFDSView.getTags()
			).put(
				"title",
				() -> {
					String title = baseListFDSView.getTitle();

					if (title.contains(StringPool.PERIOD)) {
						return StringUtil.split(title, StringPool.PERIOD);
					}

					return title;
				}
			).build()
		).build();
	}

}