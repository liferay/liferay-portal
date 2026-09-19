/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.transaction;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.Arrays;

/**
 * @author Shuyang Zhou
 */
public class TransactionConfig {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TransactionConfig)) {
			return false;
		}

		TransactionConfig transactionConfig = (TransactionConfig)object;

		if ((_isolation == transactionConfig._isolation) &&
			Arrays.equals(
				_noRollbackForClassNames,
				transactionConfig._noRollbackForClassNames) &&
			Arrays.equals(
				_noRollbackForClasses,
				transactionConfig._noRollbackForClasses) &&
			(_propagation == transactionConfig._propagation) &&
			(_readOnly == transactionConfig._readOnly) &&
			Arrays.equals(
				_rollbackForClassNames,
				transactionConfig._rollbackForClassNames) &&
			Arrays.equals(
				_rollbackForClasses, transactionConfig._rollbackForClasses) &&
			(_timeout == transactionConfig._timeout)) {

			return true;
		}

		return false;
	}

	public Isolation getIsolation() {
		return _isolation;
	}

	public String[] getNoRollbackForClassNames() {
		return _noRollbackForClassNames;
	}

	public Class<?>[] getNoRollbackForClasses() {
		return _noRollbackForClasses;
	}

	public Propagation getPropagation() {
		return _propagation;
	}

	public String[] getRollbackForClassNames() {
		return _rollbackForClassNames;
	}

	public Class<?>[] getRollbackForClasses() {
		return _rollbackForClasses;
	}

	public int getTimeout() {
		return _timeout;
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, _isolation);

		if (_noRollbackForClassNames == null) {
			hash = HashUtil.hash(hash, 0);
		}
		else {
			for (String noRollbackForClassName : _noRollbackForClassNames) {
				hash = HashUtil.hash(hash, noRollbackForClassName);
			}
		}

		if (_noRollbackForClasses == null) {
			hash = HashUtil.hash(hash, 0);
		}
		else {
			for (Class<?> noRollbackForClass : _noRollbackForClasses) {
				hash = HashUtil.hash(hash, noRollbackForClass);
			}
		}

		hash = HashUtil.hash(hash, _propagation);

		hash = HashUtil.hash(hash, _readOnly);

		if (_rollbackForClassNames == null) {
			hash = HashUtil.hash(hash, 0);
		}
		else {
			for (String rollbackForClassName : _rollbackForClassNames) {
				hash = HashUtil.hash(hash, rollbackForClassName);
			}
		}

		if (_rollbackForClasses == null) {
			hash = HashUtil.hash(hash, 0);
		}
		else {
			for (Class<?> rollbackForClass : _rollbackForClasses) {
				hash = HashUtil.hash(hash, rollbackForClass);
			}
		}

		return HashUtil.hash(hash, _timeout);
	}

	public boolean isReadOnly() {
		return _readOnly;
	}

	public boolean isStrictReadOnly() {
		return _strictReadOnly;
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{isolation=", _isolation, ", noRollbackForClassNames=",
			Arrays.toString(_noRollbackForClassNames),
			", noRollbackForClasses=", Arrays.toString(_noRollbackForClasses),
			", propagation=", _propagation, ", readOnly=", _readOnly,
			", rollbackForClassNames=", Arrays.toString(_rollbackForClassNames),
			", rollbackForClasses=", Arrays.toString(_rollbackForClasses),
			", timeout=", _timeout, StringPool.CLOSE_CURLY_BRACE);
	}

	public static class Builder {

		public TransactionConfig build() {
			return new TransactionConfig(this);
		}

		public Builder setIsolation(Isolation isolation) {
			_isolation = isolation;

			return this;
		}

		public Builder setNoRollbackForClassNames(
			String... noRollbackForClassNames) {

			_noRollbackForClassNames = noRollbackForClassNames;

			return this;
		}

		public Builder setNoRollbackForClasses(
			Class<?>... noRollbackForClasses) {

			_noRollbackForClasses = noRollbackForClasses;

			return this;
		}

		public Builder setPropagation(Propagation propagation) {
			_propagation = propagation;

			return this;
		}

		public Builder setReadOnly(boolean readOnly) {
			_readOnly = readOnly;

			return this;
		}

		public Builder setRollbackForClassNames(
			String... rollbackForClassNames) {

			_rollbackForClassNames = rollbackForClassNames;

			return this;
		}

		public Builder setRollbackForClasses(Class<?>... rollbackForClasses) {
			_rollbackForClasses = rollbackForClasses;

			return this;
		}

		public Builder setStrictReadOnly(boolean strictReadOnly) {
			if (strictReadOnly) {
				_readOnly = true;
			}

			_strictReadOnly = strictReadOnly;

			return this;
		}

		public Builder setTimeout(int timeout) {
			_timeout = timeout;

			return this;
		}

		private static final Class<?>[] _EMPTY_CLASS_ARRAY = new Class<?>[0];

		private Isolation _isolation = Isolation.DEFAULT;
		private String[] _noRollbackForClassNames = StringPool.EMPTY_ARRAY;
		private Class<?>[] _noRollbackForClasses = _EMPTY_CLASS_ARRAY;
		private Propagation _propagation = Propagation.REQUIRED;
		private boolean _readOnly;
		private String[] _rollbackForClassNames = StringPool.EMPTY_ARRAY;
		private Class<?>[] _rollbackForClasses = _EMPTY_CLASS_ARRAY;
		private boolean _strictReadOnly;
		private int _timeout = TransactionDefinition.TIMEOUT_DEFAULT;

	}

	public static class Factory {

		public static TransactionConfig create(
			Isolation isolation, Propagation propagation, boolean readOnly,
			int timeout, Class<?>[] rollbackForClasses,
			String[] rollbackForClassNames, Class<?>[] noRollbackForClasses,
			String[] noRollbackForClassNames) {

			Builder builder = new Builder();

			builder.setIsolation(isolation);
			builder.setPropagation(propagation);
			builder.setReadOnly(readOnly);
			builder.setTimeout(timeout);
			builder.setRollbackForClasses(rollbackForClasses);
			builder.setRollbackForClassNames(rollbackForClassNames);
			builder.setNoRollbackForClasses(noRollbackForClasses);
			builder.setNoRollbackForClassNames(noRollbackForClassNames);

			return builder.build();
		}

		public static TransactionConfig create(
			Propagation propagation, Class<?>[] rollbackForClasses,
			Class<?>... noRollbackForClasses) {

			return create(
				Isolation.PORTAL, propagation, false, -1, rollbackForClasses,
				new String[0], noRollbackForClasses, new String[0]);
		}

	}

	private TransactionConfig(Builder builder) {
		_isolation = builder._isolation;
		_noRollbackForClassNames = builder._noRollbackForClassNames;
		_noRollbackForClasses = builder._noRollbackForClasses;
		_propagation = builder._propagation;
		_readOnly = builder._readOnly;
		_rollbackForClassNames = builder._rollbackForClassNames;
		_rollbackForClasses = builder._rollbackForClasses;
		_strictReadOnly = builder._strictReadOnly;
		_timeout = builder._timeout;
	}

	private final Isolation _isolation;
	private final String[] _noRollbackForClassNames;
	private final Class<?>[] _noRollbackForClasses;
	private final Propagation _propagation;
	private final boolean _readOnly;
	private final String[] _rollbackForClassNames;
	private final Class<?>[] _rollbackForClasses;
	private final boolean _strictReadOnly;
	private final int _timeout;

}