/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutReference;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.model.PortletPreferenceValueTable;
import com.liferay.portal.kernel.model.PortletPreferencesTable;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.persistence.LayoutFinder;
import com.liferay.portal.kernel.service.persistence.LayoutUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.model.impl.PortletPreferenceValueImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutFinderImpl
	extends LayoutFinderBaseImpl implements LayoutFinder {

	@Override
	public List<Layout> findByNullFriendlyURL() {
		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				DSLQueryFactoryUtil.select(
				).from(
					LayoutTable.INSTANCE
				).where(
					LayoutTable.INSTANCE.friendlyURL.eq(
						""
					).or(
						LayoutTable.INSTANCE.friendlyURL.isNull()
					)
				));

			sqlQuery.addEntity("Layout", LayoutImpl.class);

			return sqlQuery.list(true);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<Layout> findByScopeGroup(long groupId) {
		return _findByScopeGroup(
			LayoutTable.INSTANCE.groupId.eq(
				groupId
			).and(
				InlineSQLHelperUtil.getPermissionWherePredicate(
					Layout.class, LayoutTable.INSTANCE.plid, groupId)
			));
	}

	@Override
	public List<Layout> findByScopeGroup(long groupId, boolean privateLayout) {
		return _findByScopeGroup(
			LayoutTable.INSTANCE.groupId.eq(
				groupId
			).and(
				LayoutTable.INSTANCE.privateLayout.eq(privateLayout)
			));
	}

	@Override
	public List<LayoutReference> findByC_P_P(
		long companyId, String portletId, String preferencesKey,
		String preferencesValue) {

		Session session = null;

		try {
			session = openSession();

			Expression<String> valueExpression =
				PortletPreferenceValueTable.INSTANCE.smallValue;

			if (preferencesValue.length() >
					PortletPreferenceValueImpl.SMALL_VALUE_MAX_LENGTH) {

				valueExpression = DSLFunctionFactoryUtil.castClobText(
					PortletPreferenceValueTable.INSTANCE.largeValue);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				DSLQueryFactoryUtil.selectDistinct(
					LayoutTable.INSTANCE.plid.as("layoutPlid"),
					PortletPreferencesTable.INSTANCE.portletId.as(
						"preferencesPortletId")
				).from(
					LayoutTable.INSTANCE
				).innerJoinON(
					PortletPreferencesTable.INSTANCE,
					PortletPreferencesTable.INSTANCE.plid.eq(
						LayoutTable.INSTANCE.plid)
				).innerJoinON(
					PortletPreferenceValueTable.INSTANCE,
					PortletPreferenceValueTable.INSTANCE.portletPreferencesId.
						eq(
							PortletPreferencesTable.INSTANCE.
								portletPreferencesId)
				).where(
					LayoutTable.INSTANCE.companyId.eq(
						companyId
					).and(
						PortletPreferencesTable.INSTANCE.portletId.eq(
							portletId
						).or(
							PortletPreferencesTable.INSTANCE.portletId.like(
								portletId.concat("_INSTANCE_%"))
						).withParentheses()
					).and(
						PortletPreferenceValueTable.INSTANCE.name.eq(
							preferencesKey)
					).and(
						valueExpression.eq(preferencesValue)
					)
				));

			sqlQuery.addScalar("layoutPlid", Type.LONG);
			sqlQuery.addScalar("preferencesPortletId", Type.STRING);

			List<LayoutReference> layoutReferences = new ArrayList<>();

			Iterator<Object[]> iterator = sqlQuery.iterate();

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				Long layoutPlid = (Long)array[0];
				String preferencesPortletId = (String)array[1];

				Layout layout = LayoutUtil.findByPrimaryKey(
					layoutPlid.longValue());

				layoutReferences.add(
					new LayoutReference(layout, preferencesPortletId));
			}

			return layoutReferences;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private List<Layout> _findByScopeGroup(Predicate wherePredicate) {
		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				DSLQueryFactoryUtil.select(
					LayoutTable.INSTANCE
				).from(
					LayoutTable.INSTANCE
				).innerJoinON(
					GroupTable.INSTANCE,
					GroupTable.INSTANCE.companyId.eq(
						LayoutTable.INSTANCE.companyId
					).and(
						GroupTable.INSTANCE.classNameId.eq(
							PortalUtil.getClassNameId(Layout.class))
					).and(
						GroupTable.INSTANCE.classPK.eq(
							LayoutTable.INSTANCE.plid)
					)
				).where(
					wherePredicate
				));

			sqlQuery.addEntity("Layout", LayoutImpl.class);

			return sqlQuery.list(true);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

}