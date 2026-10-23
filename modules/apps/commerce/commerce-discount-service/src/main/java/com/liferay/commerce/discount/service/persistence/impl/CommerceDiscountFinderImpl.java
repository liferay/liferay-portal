/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.impl;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.impl.CommerceDiscountImpl;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountFinder;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(service = CommerceDiscountFinder.class)
public class CommerceDiscountFinderImpl
	extends CommerceDiscountFinderBaseImpl implements CommerceDiscountFinder {

	public static final String COUNT_BY_COMMERCE_PRICING_CLASS_ID =
		CommerceDiscountFinder.class.getName() +
			".countByCommercePricingClassId";

	public static final String COUNT_BY_VALID_DISCOUNT =
		CommerceDiscountFinder.class.getName() + ".countByValidDiscount";

	public static final String FIND_BY_COMMERCE_PRICING_CLASS_ID =
		CommerceDiscountFinder.class.getName() +
			".findByCommercePricingClassId";

	public static final String FIND_PL_DISCOUNT_PRODUCT =
		CommerceDiscountFinder.class.getName() +
			".findPriceListDiscountProduct";

	@Override
	public int countByCommercePricingClassId(
		long commercePricingClassId, String title, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_COMMERCE_PRICING_CLASS_ID);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, CommerceDiscount.class.getName(),
					"CommerceDiscount.commerceDiscountId");
			}

			String[] keywords = _customSQL.keywords(title, true);

			if (Validator.isNotNull(title)) {
				sql = _customSQL.replaceKeywords(
					sql, "(LOWER(CommerceDiscount.title)", StringPool.LIKE,
					true, keywords);
				sql = _customSQL.replaceAndOperator(sql, false);
			}
			else {
				sql = StringUtil.removeSubstring(
					sql,
					" AND (LOWER(CommerceDiscount.title) LIKE ? " +
						"[$AND_OR_NULL_CHECK$])");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(
				_portal.getClassNameId(CommercePricingClass.class.getName()));
			queryPos.add(commercePricingClassId);

			if (Validator.isNotNull(title)) {
				queryPos.add(keywords, 2);
			}

			Iterator<Long> iterator = sqlQuery.iterate();

			if (iterator.hasNext()) {
				Long count = iterator.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public int countByValidCommerceDiscount(
		long commerceAccountId, long[] commerceAccountGroupIds,
		long commerceChannelId, long commerceDiscountId) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_VALID_DISCOUNT);

			if ((commerceAccountGroupIds != null) &&
				(commerceAccountGroupIds.length > 0)) {

				sql = replaceQueryClassPKs(
					sql, "[$ACCOUNT_GROUP_IDS$]", commerceAccountGroupIds);
			}
			else {
				sql = replaceQueryClassPKs(
					sql, "[$ACCOUNT_GROUP_IDS$]", new long[] {0});
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(
				_portal.getClassNameId(CommerceDiscount.class.getName()));
			queryPos.add(commerceDiscountId);
			queryPos.add(commerceAccountId);
			queryPos.add(commerceChannelId);
			queryPos.add(commerceAccountId);
			queryPos.add(commerceChannelId);
			queryPos.add(commerceChannelId);

			Iterator<Long> iterator = sqlQuery.iterate();

			if (iterator.hasNext()) {
				Long count = iterator.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<CommerceDiscount> findByCommercePricingClassId(
		long commercePricingClassId, String title, int start, int end,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String[] keywords = _customSQL.keywords(title, true);

			String sql = _customSQL.get(
				getClass(), FIND_BY_COMMERCE_PRICING_CLASS_ID);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, CommerceDiscount.class.getName(),
					"CommerceDiscount.commerceDiscountId");
			}

			if (Validator.isNotNull(title)) {
				sql = _customSQL.replaceKeywords(
					sql, "(LOWER(CommerceDiscount.title)", StringPool.LIKE,
					true, keywords);
				sql = _customSQL.replaceAndOperator(sql, false);
			}
			else {
				sql = StringUtil.removeSubstring(
					sql,
					" AND (LOWER(CommerceDiscount.title) LIKE ? " +
						"[$AND_OR_NULL_CHECK$])");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				CommerceDiscountImpl.TABLE_NAME, CommerceDiscountImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(
				_portal.getClassNameId(CommercePricingClass.class.getName()));
			queryPos.add(commercePricingClassId);

			if (Validator.isNotNull(title)) {
				queryPos.add(keywords, 2);
			}

			return (List<CommerceDiscount>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<CommerceDiscount> findPriceListDiscountProduct(
		long[] commerceDiscountIds, long cpDefinitionId,
		long[] assetCategoryIds, long[] commercePricingClassIds) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_PL_DISCOUNT_PRODUCT);

			if ((commerceDiscountIds != null) &&
				(commerceDiscountIds.length > 0)) {

				sql = replaceQueryClassPKs(
					sql, "[$DISCOUNT_IDS$]", commerceDiscountIds);
			}
			else {
				sql = replaceQueryClassPKs(
					sql, "[$DISCOUNT_IDS$]", new long[] {0});
			}

			if ((assetCategoryIds != null) && (assetCategoryIds.length > 0)) {
				sql = replaceQueryClassPKs(
					sql, "[$CLASS_PK_CATEGORIES$]", assetCategoryIds);
			}
			else {
				sql = replaceQueryClassPKs(
					sql, "[$CLASS_PK_CATEGORIES$]", new long[] {0});
			}

			if ((commercePricingClassIds != null) &&
				(commercePricingClassIds.length > 0)) {

				sql = replaceQueryClassPKs(
					sql, "[$CLASS_PK_PRICING_CLASSES$]",
					commercePricingClassIds);
			}
			else {
				sql = replaceQueryClassPKs(
					sql, "[$CLASS_PK_PRICING_CLASSES$]", new long[] {0});
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				CommerceDiscountImpl.TABLE_NAME, CommerceDiscountImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(cpDefinitionId);
			queryPos.add(_portal.getClassNameId(CPDefinition.class.getName()));
			queryPos.add(_portal.getClassNameId(AssetCategory.class.getName()));
			queryPos.add(
				_portal.getClassNameId(CommercePricingClass.class.getName()));

			return (List<CommerceDiscount>)QueryUtil.list(
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