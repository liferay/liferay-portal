/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.test.util;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.sample.model.CTSChild;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

/**
 * @author David Truong
 */
public class CTSampleTestUtil {

	public static long addCTSChild() throws Exception {
		return addCTSChild(0, 0, null, 1);
	}

	public static long addCTSChild(int count) throws Exception {
		return addCTSChild(0, 0, null, count);
	}

	public static long addCTSChild(
			long ctsGrandParentId, long parentCTSChildId, String ctsParentName)
		throws Exception {

		return addCTSChild(
			ctsGrandParentId, parentCTSChildId, ctsParentName, 1);
	}

	public static long addCTSChild(
			long ctsGrandParentId, long parentCTSChildId, String ctsParentName,
			int count)
		throws Exception {

		long ctsChildId = 0;

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"insert into CTSChild (mvccVersion, ctCollectionId, ",
					"ctsChildId, companyId, ctsGrandParentId, ",
					"parentCTSChildId, ctsParentName, name) values (?, ?, ?, ",
					"?, ?, ?, ?, ?)"));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				StringBundler.concat(
					"insert into CTEntry (mvccVersion, uuid_, ",
					"externalReferenceCode, ctCollectionId, ctEntryId, ",
					"companyId, userId, createDate, modifiedDate, ",
					"modelClassNameId, modelClassPK, modelMvccVersion, ",
					"changeType) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
					"?)"))) {

			long ctCollectionId = CTCollectionThreadLocal.getCTCollectionId();

			for (int i = 0; i < count; i++) {
				ctsChildId = CounterLocalServiceUtil.increment(
					CTSChild.class.getName());

				preparedStatement1.setLong(1, 1);
				preparedStatement1.setLong(2, ctCollectionId);
				preparedStatement1.setLong(3, ctsChildId);
				preparedStatement1.setLong(4, TestPropsValues.getCompanyId());
				preparedStatement1.setLong(5, ctsGrandParentId);
				preparedStatement1.setLong(6, parentCTSChildId);
				preparedStatement1.setString(7, ctsParentName);
				preparedStatement1.setString(8, String.valueOf(ctsChildId));

				preparedStatement1.addBatch();

				if (ctCollectionId > 0) {
					_addCTEntry(
						ctCollectionId, CTConstants.CT_CHANGE_TYPE_ADDITION,
						ClassNameLocalServiceUtil.getClassNameId(
							CTSChild.class),
						ctsChildId, preparedStatement2);
				}
			}

			preparedStatement1.executeBatch();

			if (ctCollectionId > 0) {
				preparedStatement2.executeBatch();
			}
		}

		return ctsChildId;
	}

	public static void reset() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL("truncate table CTSChild");
		db.runSQL("truncate table CTSGrandParent");
		db.runSQL("truncate table CTSParent");
	}

	private static void _addCTEntry(
			long ctCollectionId, int changeType, long modelClassNameId,
			long modelClassPK, PreparedStatement preparedStatement)
		throws Exception {

		preparedStatement.setLong(1, 1);

		String uuid = PortalUUIDUtil.generate();

		preparedStatement.setString(2, uuid);
		preparedStatement.setString(3, uuid);

		preparedStatement.setLong(4, ctCollectionId);
		preparedStatement.setLong(
			5, CounterLocalServiceUtil.increment(CTEntry.class.getName()));
		preparedStatement.setLong(6, TestPropsValues.getCompanyId());
		preparedStatement.setLong(7, TestPropsValues.getUserId());

		Date date = new Date(System.currentTimeMillis());

		preparedStatement.setDate(8, date);
		preparedStatement.setDate(9, date);

		preparedStatement.setLong(10, modelClassNameId);
		preparedStatement.setLong(11, modelClassPK);
		preparedStatement.setLong(12, 1);
		preparedStatement.setInt(13, changeType);

		preparedStatement.addBatch();
	}

}