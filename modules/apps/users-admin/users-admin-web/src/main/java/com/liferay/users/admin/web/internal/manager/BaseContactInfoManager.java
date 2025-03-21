/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.manager;

import jakarta.portlet.ActionRequest;

import java.util.List;

/**
 * @author Drew Brokke
 */
public abstract class BaseContactInfoManager<T>
	implements ContactInfoManager<T> {

	@Override
	public void delete(long primaryKey) throws Exception {
		T contactInfo = get(primaryKey);

		doDelete(primaryKey);

		if (isPrimary(contactInfo)) {
			List<T> contactInfos = getAll();

			if (contactInfos.isEmpty()) {
				return;
			}

			makePrimary(contactInfos.get(0));
		}
	}

	@Override
	public void edit(ActionRequest actionRequest) throws Exception {
		T contactInfo = construct(actionRequest);

		if (getPrimaryKey(contactInfo) > 0L) {
			doUpdate(contactInfo);
		}
		else {
			contactInfo = doAdd(contactInfo);
		}

		List<T> contactInfos = getAll();

		if (contactInfos.isEmpty()) {
			return;
		}

		if (!_hasPrimary(contactInfos)) {
			long size = contactInfos.size();

			for (T tempContactInfo : contactInfos) {
				if ((size == 1) ||
					(getPrimaryKey(tempContactInfo) != getPrimaryKey(
						contactInfo))) {

					setPrimary(tempContactInfo, true);

					doUpdate(tempContactInfo);
				}
			}
		}
	}

	@Override
	public void makePrimary(long primaryKey) throws Exception {
		makePrimary(get(primaryKey));
	}

	protected abstract T construct(ActionRequest actionRequest)
		throws Exception;

	protected abstract T doAdd(T contactInfo) throws Exception;

	protected abstract void doDelete(long primaryKey) throws Exception;

	protected abstract void doUpdate(T contactInfo) throws Exception;

	protected abstract T get(long primaryKey) throws Exception;

	protected abstract List<T> getAll() throws Exception;

	protected abstract long getPrimaryKey(T contactInfo);

	protected abstract boolean isPrimary(T contactInfo);

	protected void makePrimary(T contactInfo) throws Exception {
		setPrimary(contactInfo, true);

		doUpdate(contactInfo);
	}

	protected abstract void setPrimary(T contactInfo, boolean primary);

	private boolean _hasPrimary(List<T> contactInfos) {
		for (T contacInfo : contactInfos) {
			if (isPrimary(contacInfo)) {
				return true;
			}
		}

		return false;
	}

}