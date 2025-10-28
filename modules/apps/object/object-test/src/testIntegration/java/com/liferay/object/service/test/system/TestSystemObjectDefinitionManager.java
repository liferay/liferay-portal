/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test.system;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectField;
import com.liferay.object.system.BaseSystemObjectDefinitionManager;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.List;
import java.util.Map;

/**
 * @author Feliphe Marinho
 */
public class TestSystemObjectDefinitionManager
	extends BaseSystemObjectDefinitionManager {

	public TestSystemObjectDefinitionManager(
		Class<?> modelClass, String name, String restContextPath) {

		_modelClass = modelClass;
		_name = name;
		_restContextPath = restContextPath;
	}

	@Override
	public long addBaseModel(
			boolean checkPermissions, User user, Map<String, Object> values)
		throws Exception {

		return 0;
	}

	@Override
	public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
		throws PortalException {

		return null;
	}

	@Override
	public BaseModel<?> fetchBaseModelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return null;
	}

	@Override
	public BaseModel<?> getBaseModelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return null;
	}

	@Override
	public String getBaseModelExternalReferenceCode(long primaryKey)
		throws PortalException {

		return null;
	}

	@Override
	public String getExternalReferenceCode() {
		return null;
	}

	@Override
	public JaxRsApplicationDescriptor getJaxRsApplicationDescriptor() {
		List<String> restContextPaths = StringUtil.split(
			_restContextPath, CharPool.SLASH);

		return new JaxRsApplicationDescriptor(
			"", restContextPaths.get(0),
			StringUtil.merge(
				restContextPaths.subList(2, restContextPaths.size()),
				StringPool.SLASH),
			restContextPaths.get(1));
	}

	@Override
	public Map<String, String> getLabelKeys() {
		return HashMapBuilder.put(
			"label", "test"
		).put(
			"pluralLabel", "tests"
		).build();
	}

	@Override
	public Class<?> getModelClass() {
		return _modelClass;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public List<ObjectField> getObjectFields() {
		return null;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return ObjectEntryTable.INSTANCE.objectEntryId;
	}

	@Override
	public String getScope() {
		return ObjectDefinitionConstants.SCOPE_COMPANY;
	}

	@Override
	public Table getTable() {
		return ObjectEntryTable.INSTANCE;
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public void updateBaseModel(
			long primaryKey, User user, Map<String, Object> values)
		throws Exception {
	}

	@Override
	public long upsertBaseModel(
		String externalReferenceCode, long companyId, User user,
		Map<String, Object> values) {

		return 0;
	}

	private final Class<?> _modelClass;
	private final String _name;
	private final String _restContextPath;

}