/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.catalog;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;

import java.util.List;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public interface CPCatalogEntry {

	public long getCPDefinitionId();

	public List<CPDefinitionOptionRel> getCPDefinitionOptionRels();

	public List<CPSku> getCPSkus();

	public long getCProductId();

	public double getDepth();

	public String getDescription();

	public long getGroupId();

	public double getHeight();

	public String getMetaDescription(String languageId);

	public String getMetaKeywords(String languageId);

	public String getMetaTitle(String languageId);

	public String getName();

	public String getProductTypeName();

	public String getShortDescription();

	public String getUrl();

	public boolean isIgnoreSKUCombinations();

}