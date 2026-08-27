/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.buffer;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.util.ClassUtil;

import java.lang.reflect.Method;

import java.util.Objects;

/**
 * @author André de Oliveira
 * @author Michael C. Han
 */
public class IndexerRequest {

	public IndexerRequest(
		Method method, ClassedModel classedModel, Indexer<?> indexer) {

		_method = method;
		_classedModel = classedModel;

		_indexer = new NoAutoCommitIndexer<>(indexer);

		_modelPrimaryKey = (Long)classedModel.getPrimaryKeyObj();
	}

	public IndexerRequest(
		Method method, Indexer<?> indexer, String modelClassName,
		Long modelPrimaryKey) {

		_method = method;
		_indexer = new NoAutoCommitIndexer<>(indexer);
		_modelClassName = modelClassName;
		_modelPrimaryKey = modelPrimaryKey;

		_classedModel = null;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof IndexerRequest)) {
			return false;
		}

		IndexerRequest indexerRequest = (IndexerRequest)object;

		if (Objects.equals(_indexer, indexerRequest._indexer) &&
			(Objects.equals(_method, indexerRequest._method) ||
			 (Objects.equals(
				 _method.getName(), indexerRequest._method.getName()) &&
			  Objects.equals(
				  _modelPrimaryKey, indexerRequest._modelPrimaryKey))) &&
			Objects.equals(
				_getModelTypeKey(), indexerRequest._getModelTypeKey())) {

			return true;
		}

		return false;
	}

	public void execute() throws Exception {
		Class<?>[] parameterTypes = _method.getParameterTypes();

		if (parameterTypes.length == 1) {
			_method.invoke(_indexer, _classedModel);
		}
		else {
			_method.invoke(_indexer, _getModelClassName(), _modelPrimaryKey);
		}
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, _method.getName());

		hashCode = HashUtil.hash(hashCode, _getModelTypeKey());

		return HashUtil.hash(hashCode, _modelPrimaryKey);
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{classModel=", _classedModel, ", indexer=",
			ClassUtil.getClassName(_indexer), ", method=", _method,
			", modelPrimaryKey=", _modelPrimaryKey, "}");
	}

	private String _getModelClassName() {
		if (_modelClassName == null) {
			_modelClassName = _classedModel.getModelClassName();
		}

		return _modelClassName;
	}

	private Object _getModelTypeKey() {
		if (_classedModel == null) {
			return _modelClassName;
		}

		return _classedModel.getClass();
	}

	private final ClassedModel _classedModel;
	private final Indexer<?> _indexer;
	private final Method _method;
	private String _modelClassName;
	private final Long _modelPrimaryKey;

}