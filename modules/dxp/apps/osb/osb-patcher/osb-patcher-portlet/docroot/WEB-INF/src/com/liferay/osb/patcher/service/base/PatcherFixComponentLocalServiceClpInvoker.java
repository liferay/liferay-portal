/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.base;

import com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil;

import java.util.Arrays;

/**
 * @author Calvin Keum
 * @generated
 */
public class PatcherFixComponentLocalServiceClpInvoker {
	public Object invokeMethod(String name, String[] parameterTypes,
		Object[] arguments) throws Throwable {
		if (_methodName0.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes0, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.addPatcherFixComponent((com.liferay.osb.patcher.model.PatcherFixComponent)arguments[0]);
		}

		if (_methodName1.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes1, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.createPatcherFixComponent(((Long)arguments[0]).longValue());
		}

		if (_methodName2.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes2, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.deletePatcherFixComponent(((Long)arguments[0]).longValue());
		}

		if (_methodName3.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes3, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.deletePatcherFixComponent((com.liferay.osb.patcher.model.PatcherFixComponent)arguments[0]);
		}

		if (_methodName4.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes4, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.dynamicQuery();
		}

		if (_methodName5.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes5, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0]);
		}

		if (_methodName6.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes6, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue());
		}

		if (_methodName7.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes7, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue(),
				(com.liferay.portal.kernel.util.OrderByComparator)arguments[3]);
		}

		if (_methodName8.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes8, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.dynamicQueryCount((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0]);
		}

		if (_methodName9.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes9, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.dynamicQueryCount((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				(com.liferay.portal.kernel.dao.orm.Projection)arguments[1]);
		}

		if (_methodName10.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes10, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(((Long)arguments[0]).longValue());
		}

		if (_methodName11.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes11, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.getPatcherFixComponent(((Long)arguments[0]).longValue());
		}

		if (_methodName12.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes12, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.getPersistedModel((java.io.Serializable)arguments[0]);
		}

		if (_methodName13.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes13, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.getPatcherFixComponents(((Integer)arguments[0]).intValue(),
				((Integer)arguments[1]).intValue());
		}

		if (_methodName14.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes14, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.getPatcherFixComponentsCount();
		}

		if (_methodName15.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes15, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.updatePatcherFixComponent((com.liferay.osb.patcher.model.PatcherFixComponent)arguments[0]);
		}

		if (_methodName74.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes74, parameterTypes)) {
			return PatcherFixComponentLocalServiceUtil.getBeanIdentifier();
		}

		if (_methodName75.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes75, parameterTypes)) {
			PatcherFixComponentLocalServiceUtil.setBeanIdentifier((java.lang.String)arguments[0]);

			return null;
		}

		throw new UnsupportedOperationException();
	}

	private static final String _methodName0 = "addPatcherFixComponent";
	private static final String[] _methodParameterTypes0 = new String[] {
			"com.liferay.osb.patcher.model.PatcherFixComponent"
		};
	private static final String _methodName1 = "createPatcherFixComponent";
	private static final String[] _methodParameterTypes1 = new String[] { "long" };
	private static final String _methodName2 = "deletePatcherFixComponent";
	private static final String[] _methodParameterTypes2 = new String[] { "long" };
	private static final String _methodName3 = "deletePatcherFixComponent";
	private static final String[] _methodParameterTypes3 = new String[] {
			"com.liferay.osb.patcher.model.PatcherFixComponent"
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
	private static final String _methodName10 = "fetchPatcherFixComponent";
	private static final String[] _methodParameterTypes10 = new String[] { "long" };
	private static final String _methodName11 = "getPatcherFixComponent";
	private static final String[] _methodParameterTypes11 = new String[] { "long" };
	private static final String _methodName12 = "getPersistedModel";
	private static final String[] _methodParameterTypes12 = new String[] {
			"java.io.Serializable"
		};
	private static final String _methodName13 = "getPatcherFixComponents";
	private static final String[] _methodParameterTypes13 = new String[] {
			"int", "int"
		};
	private static final String _methodName14 = "getPatcherFixComponentsCount";
	private static final String[] _methodParameterTypes14 = new String[] {  };
	private static final String _methodName15 = "updatePatcherFixComponent";
	private static final String[] _methodParameterTypes15 = new String[] {
			"com.liferay.osb.patcher.model.PatcherFixComponent"
		};
	private static final String _methodName74 = "getBeanIdentifier";
	private static final String[] _methodParameterTypes74 = new String[] {  };
	private static final String _methodName75 = "setBeanIdentifier";
	private static final String[] _methodParameterTypes75 = new String[] {
			"java.lang.String"
		};
}