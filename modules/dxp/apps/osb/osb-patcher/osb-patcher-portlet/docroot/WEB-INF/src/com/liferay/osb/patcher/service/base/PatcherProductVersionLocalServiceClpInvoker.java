/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.service.base;

import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;

import java.util.Arrays;

/**
 * @author Calvin Keum
 * @generated
 */
public class PatcherProductVersionLocalServiceClpInvoker {
	public Object invokeMethod(String name, String[] parameterTypes,
		Object[] arguments) throws Throwable {
		if (_methodName0.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes0, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.addPatcherProductVersion((com.liferay.osb.patcher.model.PatcherProductVersion)arguments[0]);
		}

		if (_methodName1.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes1, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.createPatcherProductVersion(((Long)arguments[0]).longValue());
		}

		if (_methodName2.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes2, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.deletePatcherProductVersion(((Long)arguments[0]).longValue());
		}

		if (_methodName3.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes3, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.deletePatcherProductVersion((com.liferay.osb.patcher.model.PatcherProductVersion)arguments[0]);
		}

		if (_methodName4.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes4, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.dynamicQuery();
		}

		if (_methodName5.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes5, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0]);
		}

		if (_methodName6.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes6, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue());
		}

		if (_methodName7.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes7, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue(),
				(com.liferay.portal.kernel.util.OrderByComparator)arguments[3]);
		}

		if (_methodName8.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes8, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.dynamicQueryCount((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0]);
		}

		if (_methodName9.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes9, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.dynamicQueryCount((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				(com.liferay.portal.kernel.dao.orm.Projection)arguments[1]);
		}

		if (_methodName10.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes10, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(((Long)arguments[0]).longValue());
		}

		if (_methodName11.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes11, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.getPatcherProductVersion(((Long)arguments[0]).longValue());
		}

		if (_methodName12.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes12, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.getPersistedModel((java.io.Serializable)arguments[0]);
		}

		if (_methodName13.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes13, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.getPatcherProductVersions(((Integer)arguments[0]).intValue(),
				((Integer)arguments[1]).intValue());
		}

		if (_methodName14.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes14, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.getPatcherProductVersionsCount();
		}

		if (_methodName15.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes15, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.updatePatcherProductVersion((com.liferay.osb.patcher.model.PatcherProductVersion)arguments[0]);
		}

		if (_methodName74.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes74, parameterTypes)) {
			return PatcherProductVersionLocalServiceUtil.getBeanIdentifier();
		}

		if (_methodName75.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes75, parameterTypes)) {
			PatcherProductVersionLocalServiceUtil.setBeanIdentifier((java.lang.String)arguments[0]);

			return null;
		}

		throw new UnsupportedOperationException();
	}

	private static final String _methodName0 = "addPatcherProductVersion";
	private static final String[] _methodParameterTypes0 = new String[] {
			"com.liferay.osb.patcher.model.PatcherProductVersion"
		};
	private static final String _methodName1 = "createPatcherProductVersion";
	private static final String[] _methodParameterTypes1 = new String[] { "long" };
	private static final String _methodName2 = "deletePatcherProductVersion";
	private static final String[] _methodParameterTypes2 = new String[] { "long" };
	private static final String _methodName3 = "deletePatcherProductVersion";
	private static final String[] _methodParameterTypes3 = new String[] {
			"com.liferay.osb.patcher.model.PatcherProductVersion"
		};
	private static final String _methodName4 = "dynamicQuery";
	private static final String[] _methodParameterTypes4 = new String[] {  };
	private static final String _methodName5 = "dynamicQuery";
	private static final String[] _methodParameterTypes5 = new String[] {
			"com.liferay.portal.kernel.dao.orm.DynamicQuery"
		};
	private static final String _methodName6 = "dynamicQuery";
	private static final String[] _methodParameterTypes6 = new String[] {
			"com.liferay.portal.kernel.dao.orm.DynamicQuery", "int", "int"
		};
	private static final String _methodName7 = "dynamicQuery";
	private static final String[] _methodParameterTypes7 = new String[] {
			"com.liferay.portal.kernel.dao.orm.DynamicQuery", "int", "int",
			"com.liferay.portal.kernel.util.OrderByComparator"
		};
	private static final String _methodName8 = "dynamicQueryCount";
	private static final String[] _methodParameterTypes8 = new String[] {
			"com.liferay.portal.kernel.dao.orm.DynamicQuery"
		};
	private static final String _methodName9 = "dynamicQueryCount";
	private static final String[] _methodParameterTypes9 = new String[] {
			"com.liferay.portal.kernel.dao.orm.DynamicQuery",
			"com.liferay.portal.kernel.dao.orm.Projection"
		};
	private static final String _methodName10 = "fetchPatcherProductVersion";
	private static final String[] _methodParameterTypes10 = new String[] { "long" };
	private static final String _methodName11 = "getPatcherProductVersion";
	private static final String[] _methodParameterTypes11 = new String[] { "long" };
	private static final String _methodName12 = "getPersistedModel";
	private static final String[] _methodParameterTypes12 = new String[] {
			"java.io.Serializable"
		};
	private static final String _methodName13 = "getPatcherProductVersions";
	private static final String[] _methodParameterTypes13 = new String[] {
			"int", "int"
		};
	private static final String _methodName14 = "getPatcherProductVersionsCount";
	private static final String[] _methodParameterTypes14 = new String[] {  };
	private static final String _methodName15 = "updatePatcherProductVersion";
	private static final String[] _methodParameterTypes15 = new String[] {
			"com.liferay.osb.patcher.model.PatcherProductVersion"
		};
	private static final String _methodName74 = "getBeanIdentifier";
	private static final String[] _methodParameterTypes74 = new String[] {  };
	private static final String _methodName75 = "setBeanIdentifier";
	private static final String[] _methodParameterTypes75 = new String[] {
			"java.lang.String"
		};
}