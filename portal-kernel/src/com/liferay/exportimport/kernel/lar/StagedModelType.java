/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Zsolt Berentey
 */
public class StagedModelType {

	public static final String REFERRER_CLASS_NAME_ALL =
		"referrer-class-name-all";

	public static final String REFERRER_CLASS_NAME_ANY =
		"referrer-class-name-any";

	public static final int REFERRER_CLASS_NAME_ID_ALL = -1;

	public static final int REFERRER_CLASS_NAME_ID_ANY = -2;

	public static StagedModelType parse(String stagedModelTypeString) {
		if (Validator.isNull(stagedModelTypeString)) {
			return null;
		}

		try {
			int index = stagedModelTypeString.indexOf(CharPool.POUND);

			if (index == -1) {
				return new StagedModelType(stagedModelTypeString);
			}

			String className = stagedModelTypeString.substring(0, index);

			if (Validator.isNull(className)) {
				return null;
			}

			String referrerClassName = stagedModelTypeString.substring(
				index + 1);

			return new StagedModelType(className, referrerClassName);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	public StagedModelType(Class<?> clazz) {
		setClassName(clazz.getName());
	}

	public StagedModelType(Class<?> clazz, Class<?> referrerClass) {
		setClassName(clazz.getName());
		setReferrerClassName(referrerClass.getName());
	}

	public StagedModelType(long classNameId) {
		setClassNameId(classNameId);
	}

	public StagedModelType(long classNameId, long referrerClassNameId) {
		setClassNameId(classNameId);
		setReferrerClassNameId(referrerClassNameId);
	}

	public StagedModelType(String className) {
		setClassName(className);
	}

	public StagedModelType(String className, String referrerClassName) {
		setClassName(className);
		setReferrerClassName(referrerClassName);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if ((object == null) || !(object instanceof StagedModelType)) {
			return false;
		}

		StagedModelType stagedModelType = (StagedModelType)object;

		if ((stagedModelType._classNameId != _classNameId) ||
			(stagedModelType._referrerClassNameId != _referrerClassNameId)) {

			return false;
		}

		return true;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassNameId() {
		return _classNameId;
	}

	public String getClassSimpleName() {
		return _classSimpleName;
	}

	public String getReferrerClassName() {
		return _referrerClassName;
	}

	public long getReferrerClassNameId() {
		return _referrerClassNameId;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, _classNameId);

		return HashUtil.hash(hashCode, _referrerClassNameId);
	}

	@Override
	public String toString() {
		if ((_referrerClassNameId <= 0) &&
			(_referrerClassNameId != REFERRER_CLASS_NAME_ID_ALL) &&
			(_referrerClassNameId != REFERRER_CLASS_NAME_ID_ANY)) {

			return _className;
		}

		return StringBundler.concat(
			_className, StringPool.POUND, _referrerClassName);
	}

	protected String getSimpleName(String className) {
		if (Validator.isNull(className)) {
			return StringPool.BLANK;
		}

		int index = className.lastIndexOf(StringPool.PERIOD) + 1;

		if (index <= 0) {
			return className;
		}

		return className.substring(index);
	}

	protected void setClassName(String className) {
		_className = className;

		_classSimpleName = getSimpleName(_className);

		if (Validator.isNotNull(className)) {
			_classNameId = PortalUtil.getClassNameId(className);
		}
		else {
			_classNameId = 0;
		}
	}

	protected void setClassNameId(long classNameId) {
		if (classNameId > 0) {
			_className = PortalUtil.getClassName(classNameId);

			_classSimpleName = getSimpleName(_className);
		}
		else {
			_className = null;
			_classSimpleName = null;
		}

		_classNameId = classNameId;
	}

	protected void setClassSimpleName(String classSimpleName) {
		_classSimpleName = classSimpleName;
	}

	protected void setReferrerClassName(String referrerClassName) {
		_referrerClassName = referrerClassName;

		if (Validator.isNull(referrerClassName)) {
			_referrerClassNameId = 0;
		}
		else if (referrerClassName.equals(REFERRER_CLASS_NAME_ALL)) {
			_referrerClassNameId = REFERRER_CLASS_NAME_ID_ALL;
		}
		else if (referrerClassName.equals(REFERRER_CLASS_NAME_ANY)) {
			_referrerClassNameId = REFERRER_CLASS_NAME_ID_ANY;
		}
		else {
			_referrerClassNameId = PortalUtil.getClassNameId(referrerClassName);
		}
	}

	protected void setReferrerClassNameId(long referrerClassNameId) {
		_referrerClassNameId = referrerClassNameId;

		if (referrerClassNameId == 0) {
			_referrerClassName = null;
		}
		else if (referrerClassNameId == REFERRER_CLASS_NAME_ID_ALL) {
			_referrerClassName = REFERRER_CLASS_NAME_ALL;
		}
		else if (referrerClassNameId == REFERRER_CLASS_NAME_ID_ANY) {
			_referrerClassName = REFERRER_CLASS_NAME_ANY;
		}
		else if (referrerClassNameId > 0) {
			_referrerClassName = PortalUtil.getClassName(referrerClassNameId);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagedModelType.class);

	private String _className;
	private long _classNameId;
	private String _classSimpleName;
	private String _referrerClassName;
	private long _referrerClassNameId;

}