/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.model.impl;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLocalServiceUtil;
import com.liferay.commerce.product.service.CPInstanceLocalServiceUtil;
import com.liferay.commerce.product.service.CProductLocalServiceUtil;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.service.CommerceWishListLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Andrea Di Giorgi
 */
public class CommerceWishListItemImpl extends CommerceWishListItemBaseImpl {

	@Override
	public CPInstance fetchCPInstance() throws PortalException {
		return CPInstanceLocalServiceUtil.fetchCProductInstance(
			getCProductId(), getCPInstanceUuid());
	}

	@Override
	public CPDefinition getCPDefinition() throws PortalException {
		CProduct cProduct = getCProduct();

		return CPDefinitionLocalServiceUtil.getCPDefinition(
			cProduct.getPublishedCPDefinitionId());
	}

	@Override
	public CProduct getCProduct() throws PortalException {
		return CProductLocalServiceUtil.getCProduct(getCProductId());
	}

	@Override
	public CommerceWishList getCommerceWishList() throws PortalException {
		return CommerceWishListLocalServiceUtil.getCommerceWishList(
			getCommerceWishListId());
	}

	@Override
	public boolean isIgnoreSKUCombinations() throws PortalException {
		CPDefinition cpDefinition = getCPDefinition();
		CPInstance cpInstance = fetchCPInstance();

		if (cpDefinition.isIgnoreSKUCombinations() || (cpInstance != null)) {
			return true;
		}

		return false;
	}

}