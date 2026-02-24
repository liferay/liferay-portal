/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.view.list;

import com.liferay.frontend.data.set.view.list.FDSListSchemaBuilder;
import com.liferay.frontend.data.set.view.list.FDSListSchemaBuilderFactory;

import org.osgi.service.component.annotations.Component;

/**
 * @author Mylena Monte
 */
@Component(service = FDSListSchemaBuilderFactory.class)
public class FDSListSchemaBuilderFactoryImpl
	implements FDSListSchemaBuilderFactory {

	@Override
	public FDSListSchemaBuilder create() {
		return new FDSListSchemaBuilderImpl();
	}

}