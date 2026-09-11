/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.model.listener;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ClassNameLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = ModelListener.class)
public class CommerceChannelRelModelListener
	extends BaseCPDefinitionSitemapModelListener<CommerceChannelRel> {

	@Override
	public void onAfterCreate(CommerceChannelRel commerceChannelRel) {
		if (commerceChannelRel.getClassNameId() ==
				_classNameLocalService.getClassNameId(
					CPDefinition.class.getName())) {

			addSiteSitemapRegenerationEntry(commerceChannelRel.getCompanyId());
		}
	}

	@Override
	public void onAfterRemove(CommerceChannelRel commerceChannelRel) {
		if (commerceChannelRel.getClassNameId() ==
				_classNameLocalService.getClassNameId(
					CPDefinition.class.getName())) {

			addSiteSitemapRegenerationEntry(commerceChannelRel.getCompanyId());
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

}