/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.view.cards;

import com.liferay.frontend.data.set.view.cards.FDSCardSchemaBuilder;
import com.liferay.frontend.data.set.view.cards.FDSCardSchemaBuilderFactory;

import org.osgi.service.component.annotations.Component;

/**
 * @author Mikel Lorza
 */
@Component(service = FDSCardSchemaBuilderFactory.class)
public class FDSCardSchemaBuilderFactoryImpl
	implements FDSCardSchemaBuilderFactory {

	@Override
	public FDSCardSchemaBuilder create() {
		return new FDSCardSchemaBuilderImpl();
	}

}