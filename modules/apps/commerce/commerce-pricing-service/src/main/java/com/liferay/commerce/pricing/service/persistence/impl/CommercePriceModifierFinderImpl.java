/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.persistence.impl;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.commerce.pricing.constants.CommercePriceModifierConstants;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.impl.CommercePriceModifierImpl;
import com.liferay.commerce.pricing.service.persistence.CommercePriceModifierFinder;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(service = CommercePriceModifierFinder.class)
public class CommercePriceModifierFinderImpl
	extends CommercePriceModifierFinderBaseImpl
	implements CommercePriceModifierFinder {

	public static final String FIND_BY_C_C_C_P =
		CommercePriceModifierFinder.class.getName() + ".findByC_C_C_P";

	@Override
	public List<CommercePriceModifier> findByC_C_C_P(
		long commercePriceListId, long cpDefinitionId,
		long[] assetCategoriesIds, long[] commercePricingClassesIds) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_C_C_C_P);

			if ((assetCategoriesIds != null) &&
				(assetCategoriesIds.length > 0)) {

				sql = replaceQueryClassPKs(
					sql, "[$CLASS_PK_CATEGORIES$]", assetCategoriesIds);
			}
			else {
				sql = replaceQueryClassPKs(
					sql, "[$CLASS_PK_CATEGORIES$]", new long[] {0});
			}

			if ((commercePricingClassesIds != null) &&
				(commercePricingClassesIds.length > 0)) {

				sql = replaceQueryClassPKs(
					sql, "[$CLASS_PK_PRICING_CLASSES$]",
					commercePricingClassesIds);
			}
			else {
				sql = replaceQueryClassPKs(
					sql, "[$CLASS_PK_PRICING_CLASSES$]", new long[] {0});
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				CommercePriceModifierImpl.TABLE_NAME,
				CommercePriceModifierImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(commercePriceListId);
			queryPos.add(cpDefinitionId);
			queryPos.add(_portal.getClassNameId(CPDefinition.class.getName()));
			queryPos.add(_portal.getClassNameId(AssetCategory.class.getName()));
			queryPos.add(
				_portal.getClassNameId(CommercePricingClass.class.getName()));
			queryPos.add(CommercePriceModifierConstants.TARGET_CATALOG);

			return (List<CommercePriceModifier>)QueryUtil.list(
				sqlQuery, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected String replaceQueryClassPKs(
		String sql, String queryPlaceholder, long[] classPKs) {

		StringBundler sb = new StringBundler(classPKs.length);

		for (int i = 0; i < classPKs.length; i++) {
			sb.append(classPKs[i]);

			if (i == (classPKs.length - 1)) {
				continue;
			}

			sb.append(", ");
		}

		return StringUtil.replace(sql, queryPlaceholder, sb.toString());
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private Portal _portal;

}