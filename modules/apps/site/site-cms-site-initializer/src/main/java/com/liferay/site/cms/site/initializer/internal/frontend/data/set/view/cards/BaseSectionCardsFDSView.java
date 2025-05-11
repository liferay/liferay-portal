/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.view.cards;

import com.liferay.frontend.data.set.view.cards.BaseCardsFDSView;
import com.liferay.frontend.data.set.view.cards.FDSCardSchema;
import com.liferay.frontend.data.set.view.cards.FDSCardSchemaBuilder;
import com.liferay.frontend.data.set.view.cards.FDSCardSchemaBuilderFactory;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Locale;

/**
 * @author Mikel Lorza
 */
public abstract class BaseSectionCardsFDSView extends BaseCardsFDSView {

	@Override
	public FDSCardSchema getFDSCardSchema(Locale locale) {
		FDSCardSchemaBuilderFactory fdsCardSchemaBuilderFactory =
			_fdsCardSchemaBuilderFactorySnapshot.get();

		if (fdsCardSchemaBuilderFactory == null) {
			return null;
		}

		FDSCardSchemaBuilder fdsCardSchemaBuilder =
			fdsCardSchemaBuilderFactory.create();

		return fdsCardSchemaBuilder.add(
			"embedded.status.label",
			HashMapBuilder.put(
				"approved", "success"
			).put(
				"denied", "danger"
			).put(
				"draft", "secondary"
			).put(
				"expired", "danger"
			).put(
				"in-trash", "info"
			).put(
				"inactive", "secondary"
			).put(
				"incomplete", "warning"
			).put(
				"pending", "info"
			).put(
				"scheduled", "info"
			).build(),
			"embedded.status.label_i18n"
		).build();
	}

	private static final Snapshot<FDSCardSchemaBuilderFactory>
		_fdsCardSchemaBuilderFactorySnapshot = new Snapshot<>(
			BaseSectionCardsFDSView.class, FDSCardSchemaBuilderFactory.class);

}