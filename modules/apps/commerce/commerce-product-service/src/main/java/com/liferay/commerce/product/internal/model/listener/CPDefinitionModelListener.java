/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.model.listener;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Component;

/**
 * @author Cheryl Tang
 */
@Component(service = ModelListener.class)
public class CPDefinitionModelListener
	extends BaseCPDefinitionSitemapModelListener<CPDefinition> {

	@Override
	public void onAfterCreate(CPDefinition cpDefinition) {
		addSiteSitemapRegenerationEntry(cpDefinition.getCompanyId());
	}

	@Override
	public void onAfterRemove(CPDefinition cpDefinition) {
		addSiteSitemapRegenerationEntry(cpDefinition.getCompanyId());
	}

	@Override
	public void onAfterUpdate(
		CPDefinition originalCPDefinition, CPDefinition cpDefinition) {

		addSiteSitemapRegenerationEntry(cpDefinition.getCompanyId());
	}

}