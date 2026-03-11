/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.view.cards;

import com.liferay.frontend.data.set.constants.FDSConstants;
import com.liferay.frontend.data.set.view.FDSSchemaLabelField;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewContextContributor;
import com.liferay.frontend.data.set.view.cards.BaseCardsFDSView;
import com.liferay.frontend.data.set.view.cards.FDSCardSchema;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Bruno Basto
 */
@Component(
	property = "frontend.data.set.view.name=" + FDSConstants.CARDS,
	service = FDSViewContextContributor.class
)
public class CardsFDSViewContextContributor
	implements FDSViewContextContributor {

	@Override
	public Map<String, Object> getFDSViewContext(
		FDSView fdsView, Locale locale) {

		if (fdsView instanceof BaseCardsFDSView) {
			return _serialize((BaseCardsFDSView)fdsView, locale);
		}

		return Collections.emptyMap();
	}

	private Map<String, Object> _serialize(
		BaseCardsFDSView baseCardsFDSView, Locale locale) {

		return HashMapBuilder.<String, Object>put(
			"schema",
			JSONUtil.put(
				"description", baseCardsFDSView.getDescription()
			).put(
				"image", baseCardsFDSView.getImage()
			).put(
				"labels",
				() -> {
					FDSCardSchema fdsCardSchema =
						baseCardsFDSView.getFDSCardSchema(locale);

					List<FDSSchemaLabelField> fdsSchemaLabelFieldList =
						fdsCardSchema.getFDSSchemaLabelFieldsList();

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
				"link", baseCardsFDSView.getLink()
			).put(
				"sticker", baseCardsFDSView.getSticker()
			).put(
				"symbol", baseCardsFDSView.getSymbol()
			).put(
				"title", baseCardsFDSView.getTitle()
			)
		).build();
	}

}