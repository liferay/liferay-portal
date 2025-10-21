/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.view.count.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.LockMode;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.view.count.model.ViewCountEntry;
import com.liferay.view.count.model.impl.ViewCountEntryImpl;
import com.liferay.view.count.service.persistence.ViewCountEntryFinder;
import com.liferay.view.count.service.persistence.ViewCountEntryPK;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = ViewCountEntryFinder.class)
public class ViewCountEntryFinderImpl
	extends ViewCountEntryFinderBaseImpl implements ViewCountEntryFinder {

	@Override
	public void incrementViewCount(
		long companyId, long classNameId, long classPK, int increment) {

		Session session = null;

		try {
			session = openSession();

			ViewCountEntryPK viewCountEntryPK = new ViewCountEntryPK(
				companyId, classNameId, classPK);

			ViewCountEntry viewCountEntry = (ViewCountEntry)session.get(
				ViewCountEntryImpl.class, viewCountEntryPK,
				LockMode.PESSIMISTIC_WRITE);

			if (viewCountEntry == null) {
				viewCountEntry = new ViewCountEntryImpl();

				viewCountEntry.setPrimaryKey(viewCountEntryPK);

				viewCountEntry.setViewCount(increment);

				session.save(viewCountEntry);

				session.flush();
			}
			else {
				viewCountEntry.setViewCount(
					viewCountEntry.getViewCount() + increment);

				session.saveOrUpdate(viewCountEntry);
			}

			_entityCache.putResult(
				ViewCountEntryImpl.class, viewCountEntry, false, true);
		}
		finally {
			closeSession(session);
		}
	}

	@Reference
	private EntityCache _entityCache;

}