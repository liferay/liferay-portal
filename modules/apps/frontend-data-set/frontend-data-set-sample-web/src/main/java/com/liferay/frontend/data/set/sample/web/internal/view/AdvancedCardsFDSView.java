/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.view;

import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.cards.BaseCardsFDSView;
import com.liferay.frontend.data.set.view.cards.FDSCardSchema;
import com.liferay.frontend.data.set.view.cards.FDSCardSchemaBuilder;
import com.liferay.frontend.data.set.view.cards.FDSCardSchemaBuilderFactory;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier de Arcos
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.ADVANCED,
	service = FDSView.class
)
public class AdvancedCardsFDSView extends BaseCardsFDSView {

	@Override
	public String getDescription() {
		return "description";
	}

	@Override
	public FDSCardSchema getFDSCardSchema(Locale locale) {
		FDSCardSchemaBuilder fdsCardSchemaBuilder =
			_fdsCardSchemaBuilderFactory.create();

		return fdsCardSchemaBuilder.add(
			"status.label",
			HashMapBuilder.put(
				"approved", "success"
			).put(
				"expired", "danger"
			).build(),
			"status.label_i18n"
		).build();
	}

	@Override
	public String getTitle() {
		return "title";
	}

	@Reference
	private FDSCardSchemaBuilderFactory _fdsCardSchemaBuilderFactory;

}