/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.security.audit.storage.model.AuditEventAccountEntryRelTable;
import com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelImpl;
import com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelModelImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from AuditEventAccountEntryRel.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelImpl",
		"table.name=Audit_EventAccountEntryRel"
	},
	service = ArgumentsResolver.class
)
public class AuditEventAccountEntryRelModelArgumentsResolver
	implements ArgumentsResolver {

	@Override
	public Object[] getArguments(
		FinderPath finderPath, BaseModel<?> baseModel, boolean checkColumn,
		boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		if ((columnNames == null) || (columnNames.length == 0)) {
			if (baseModel.isNew()) {
				return new Object[0];
			}

			return null;
		}

		AuditEventAccountEntryRelModelImpl auditEventAccountEntryRelModelImpl =
			(AuditEventAccountEntryRelModelImpl)baseModel;

		long columnBitmask =
			auditEventAccountEntryRelModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				auditEventAccountEntryRelModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					auditEventAccountEntryRelModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				auditEventAccountEntryRelModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return AuditEventAccountEntryRelImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return AuditEventAccountEntryRelTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		AuditEventAccountEntryRelModelImpl auditEventAccountEntryRelModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					auditEventAccountEntryRelModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				value = auditEventAccountEntryRelModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:-821093335