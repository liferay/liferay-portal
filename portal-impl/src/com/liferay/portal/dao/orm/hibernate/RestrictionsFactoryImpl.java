/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactory;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collection;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class RestrictionsFactoryImpl implements RestrictionsFactory {

	@Override
	public Criterion allEq(Map<String, Criterion> propertyNameValues) {
		return new CriterionImpl();
	}

	@Override
	public Criterion and(Criterion lhs, Criterion rhs) {
		return new CriterionImpl();
	}

	@Override
	public Criterion between(String propertyName, Object lo, Object hi) {
		return new CriterionImpl();
	}

	@Override
	public Conjunction conjunction() {
		return new ConjunctionImpl();
	}

	@Override
	public Disjunction disjunction() {
		return new DisjunctionImpl();
	}

	@Override
	public Criterion eq(String propertyName, Object value) {
		return new CriterionImpl();
	}

	@Override
	public Criterion eqProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl();
	}

	@Override
	public Criterion ge(String propertyName, Object value) {
		return new CriterionImpl();
	}

	@Override
	public Criterion geProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl();
	}

	@Override
	public Criterion gt(String propertyName, Object value) {
		return new CriterionImpl();
	}

	@Override
	public Criterion gtProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl();
	}

	@Override
	public Criterion ilike(String propertyName, Object value) {
		return new CriterionImpl();
	}

	@Override
	public Criterion in(String propertyName, Collection<?> values) {
		return new CriterionImpl();
	}

	@Override
	public Criterion in(String propertyName, Object[] values) {
		return in(propertyName, ListUtil.fromArray(values));
	}

	@Override
	public Criterion isEmpty(String propertyName) {
		return new CriterionImpl();
	}

	@Override
	public Criterion isNotEmpty(String propertyName) {
		return new CriterionImpl();
	}

	@Override
	public Criterion isNotNull(String propertyName) {
		return new CriterionImpl();
	}

	@Override
	public Criterion isNull(String propertyName) {
		return new CriterionImpl();
	}

	@Override
	public Criterion le(String propertyName, Object value) {
		return new CriterionImpl();
	}

	@Override
	public Criterion leProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl();
	}

	@Override
	public Criterion like(String propertyName, Object value) {
		return new CriterionImpl();
	}

	@Override
	public Criterion lt(String propertyName, Object value) {
		return new CriterionImpl();
	}

	@Override
	public Criterion ltProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl();
	}

	@Override
	public Criterion ne(String propertyName, Object value) {
		return new CriterionImpl();
	}

	@Override
	public Criterion neProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl();
	}

	@Override
	public Criterion not(Criterion expression) {
		return new CriterionImpl();
	}

	@Override
	public Criterion or(Criterion lhs, Criterion rhs) {
		return new CriterionImpl();
	}

	@Override
	public Criterion sizeEq(String propertyName, int size) {
		return new CriterionImpl();
	}

	@Override
	public Criterion sizeGe(String propertyName, int size) {
		return new CriterionImpl();
	}

	@Override
	public Criterion sizeGt(String propertyName, int size) {
		return new CriterionImpl();
	}

	@Override
	public Criterion sizeLe(String propertyName, int size) {
		return new CriterionImpl();
	}

	@Override
	public Criterion sizeLt(String propertyName, int size) {
		return new CriterionImpl();
	}

	@Override
	public Criterion sizeNe(String propertyName, int size) {
		return new CriterionImpl();
	}

	@Override
	public Criterion sqlRestriction(String sql) {
		return new CriterionImpl();
	}

	@Override
	public Criterion sqlRestriction(String sql, Object value, Type type) {
		return new CriterionImpl();
	}

	@Override
	public Criterion sqlRestriction(String sql, Object[] values, Type[] types) {
		return new CriterionImpl();
	}

}