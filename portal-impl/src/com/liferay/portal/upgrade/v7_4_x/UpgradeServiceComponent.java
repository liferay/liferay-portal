/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.IOException;

import java.util.Properties;

import org.osgi.framework.Bundle;

/**
 * @author Jorge Díaz
 */
public class UpgradeServiceComponent extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		processConcurrently(
			SQLTransformer.transform(
				StringBundler.concat(
					"select buildNamespace, buildNumber, data_ from ",
					"ServiceComponent where buildNamespace like 'com.",
					"liferay%' and buildNumber = (select max(buildNumber) ",
					"from ServiceComponent TEMP_TABLE where ServiceComponent.",
					"buildNamespace = TEMP_TABLE.buildNamespace)")),
			"update ServiceComponent set data_ = ? where buildNamespace = ? " +
				"and buildNumber = ?",
			resultSet -> new Object[] {
				resultSet.getString("buildNamespace"),
				resultSet.getLong("buildNumber"), resultSet.getString("data_")
			},
			(values, preparedStatement) -> {
				String buildNamespace = (String)values[0];

				Bundle bundle = BundleUtil.getBundle(
					SystemBundleUtil.getBundleContext(), buildNamespace);

				if (bundle == null) {
					return;
				}

				Properties properties = PropertiesUtil.load(
					bundle.getResource("service.properties"));

				String buildNumberServiceProperties = properties.getProperty(
					"build.number");

				Long buildNumber = (Long)values[1];

				if (!StringUtil.equals(
						buildNumberServiceProperties, buildNumber.toString())) {

					return;
				}

				String bundleData = _generateXML(bundle);
				String data = (String)values[2];

				if (!data.equals(bundleData)) {
					preparedStatement.setString(1, bundleData);
					preparedStatement.setString(2, buildNamespace);
					preparedStatement.setLong(3, buildNumber);

					preparedStatement.addBatch();
				}
			},
			null);
	}

	private String _generateXML(Bundle bundle) throws IOException {
		Document document = SAXReaderUtil.createDocument(StringPool.UTF8);

		Element dataElement = document.addElement("data");

		Element tablesSQLElement = dataElement.addElement("tables-sql");

		tablesSQLElement.addCDATA(DBResourceUtil.getModuleTablesSQL(bundle));

		Element sequencesSQLElement = dataElement.addElement("sequences-sql");

		sequencesSQLElement.addCDATA(
			DBResourceUtil.getModuleSequencesSQL(bundle));

		Element indexesSQLElement = dataElement.addElement("indexes-sql");

		indexesSQLElement.addCDATA(DBResourceUtil.getModuleIndexesSQL(bundle));

		return document.formattedString();
	}

}