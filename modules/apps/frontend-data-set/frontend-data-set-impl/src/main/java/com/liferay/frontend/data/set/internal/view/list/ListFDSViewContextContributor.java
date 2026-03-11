/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.view.list;

import com.liferay.frontend.data.set.constants.FDSConstants;
import com.liferay.frontend.data.set.view.FDSSchemaLabelField;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewContextContributor;
import com.liferay.frontend.data.set.view.list.BaseListFDSView;
import com.liferay.frontend.data.set.view.list.FDSListSchema;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.List;
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

		if (fdsView instanceof BaseListFDSView baseListFDSView) {
			return _serialize(baseListFDSView, locale);
		}

		return Collections.emptyMap();
	}

	private Map<String, Object> _serialize(
		BaseListFDSView baseListFDSView, Locale locale) {

		return HashMapBuilder.<String, Object>put(
			"schema",
			JSONUtil.put(
				"description", baseListFDSView.getDescription()
			).put(
				"image", baseListFDSView.getImage()
			).put(
				"labels",
				() -> {
					FDSListSchema fdsListSchema =
						baseListFDSView.getFDSListSchema(locale);

					if (fdsListSchema == null) {
						return null;
					}

					List<FDSSchemaLabelField> fdsSchemaLabelFieldList =
						fdsListSchema.getFDSSchemaLabelFieldsList();

					if (ListUtil.isEmpty(fdsSchemaLabelFieldList)) {
						return null;
					}

					JSONArray jsonArray = JSONUtil.putAll();

					TransformUtil.transform(
						fdsSchemaLabelFieldList,
						fdsSchemaLabelField -> jsonArray.put(
							fdsSchemaLabelField.toJSONObject()));

					return jsonArray;
				}
			).put(
				"sticker", baseListFDSView.getSticker()
			).put(
				"symbol", baseListFDSView.getSymbol()
			).put(
				"title",
				() -> {
					String title = baseListFDSView.getTitle();

					if (title.contains(StringPool.PERIOD)) {
						return StringUtil.split(title, StringPool.PERIOD);
					}

					return title;
				}
			)
		).build();
	}

}