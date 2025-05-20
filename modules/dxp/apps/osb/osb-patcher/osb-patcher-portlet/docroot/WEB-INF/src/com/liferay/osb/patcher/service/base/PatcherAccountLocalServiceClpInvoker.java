/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.base;

import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;

import java.util.Arrays;

/**
 * @author Calvin Keum
 * @generated
 */
public class PatcherAccountLocalServiceClpInvoker {
	public Object invokeMethod(String name, String[] parameterTypes,
		Object[] arguments) throws Throwable {
		if (_methodName0.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes0, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.addPatcherAccount((com.liferay.osb.patcher.model.PatcherAccount)arguments[0]);
		}

		if (_methodName1.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes1, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.createPatcherAccount(((Long)arguments[0]).longValue());
		}

		if (_methodName2.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes2, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.deletePatcherAccount(((Long)arguments[0]).longValue());
		}

		if (_methodName3.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes3, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.deletePatcherAccount((com.liferay.osb.patcher.model.PatcherAccount)arguments[0]);
		}

		if (_methodName4.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes4, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.dynamicQuery();
		}

		if (_methodName5.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes5, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0]);
		}

		if (_methodName6.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes6, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue());
		}

		if (_methodName7.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes7, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue(),
				(com.liferay.portal.kernel.util.OrderByComparator)arguments[3]);
		}

		if (_methodName8.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes8, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.dynamicQueryCount((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0]);
		}

		if (_methodName9.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes9, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.dynamicQueryCount((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				(com.liferay.portal.kernel.dao.orm.Projection)arguments[1]);
		}

		if (_methodName10.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes10, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.fetchPatcherAccount(((Long)arguments[0]).longValue());
		}

		if (_methodName11.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes11, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.getPatcherAccount(((Long)arguments[0]).longValue());
		}

		if (_methodName12.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes12, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.getPersistedModel((java.io.Serializable)arguments[0]);
		}

		if (_methodName13.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes13, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.getPatcherAccounts(((Integer)arguments[0]).intValue(),
				((Integer)arguments[1]).intValue());
		}

		if (_methodName14.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes14, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.getPatcherAccountsCount();
		}

		if (_methodName15.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes15, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.updatePatcherAccount((com.liferay.osb.patcher.model.PatcherAccount)arguments[0]);
		}

		if (_methodName16.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes16, parameterTypes)) {
			PatcherAccountLocalServiceUtil.addPatcherBuildPatcherAccount(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());

			return null;
		}

		if (_methodName17.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes17, parameterTypes)) {
			PatcherAccountLocalServiceUtil.addPatcherBuildPatcherAccount(((Long)arguments[0]).longValue(),
				(com.liferay.osb.patcher.model.PatcherAccount)arguments[1]);

			return null;
		}

		if (_methodName18.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes18, parameterTypes)) {
			PatcherAccountLocalServiceUtil.addPatcherBuildPatcherAccounts(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName19.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes19, parameterTypes)) {
			PatcherAccountLocalServiceUtil.addPatcherBuildPatcherAccounts(((Long)arguments[0]).longValue(),
				(java.util.List<com.liferay.osb.patcher.model.PatcherAccount>)arguments[1]);

			return null;
		}

		if (_methodName20.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes20, parameterTypes)) {
			PatcherAccountLocalServiceUtil.clearPatcherBuildPatcherAccounts(((Long)arguments[0]).longValue());

			return null;
		}

		if (_methodName21.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes21, parameterTypes)) {
			PatcherAccountLocalServiceUtil.deletePatcherBuildPatcherAccount(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());

			return null;
		}

		if (_methodName22.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes22, parameterTypes)) {
			PatcherAccountLocalServiceUtil.deletePatcherBuildPatcherAccount(((Long)arguments[0]).longValue(),
				(com.liferay.osb.patcher.model.PatcherAccount)arguments[1]);

			return null;
		}

		if (_methodName23.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes23, parameterTypes)) {
			PatcherAccountLocalServiceUtil.deletePatcherBuildPatcherAccounts(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName24.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes24, parameterTypes)) {
			PatcherAccountLocalServiceUtil.deletePatcherBuildPatcherAccounts(((Long)arguments[0]).longValue(),
				(java.util.List<com.liferay.osb.patcher.model.PatcherAccount>)arguments[1]);

			return null;
		}

		if (_methodName25.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes25, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.getPatcherBuildPatcherAccounts(((Long)arguments[0]).longValue());
		}

		if (_methodName26.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes26, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.getPatcherBuildPatcherAccounts(((Long)arguments[0]).longValue(),
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue());
		}

		if (_methodName27.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes27, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.getPatcherBuildPatcherAccounts(((Long)arguments[0]).longValue(),
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue(),
				(com.liferay.portal.kernel.util.OrderByComparator)arguments[3]);
		}

		if (_methodName28.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes28, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.getPatcherBuildPatcherAccountsCount(((Long)arguments[0]).longValue());
		}

		if (_methodName29.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes29, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.hasPatcherBuildPatcherAccount(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());
		}

		if (_methodName30.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes30, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.hasPatcherBuildPatcherAccounts(((Long)arguments[0]).longValue());
		}

		if (_methodName31.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes31, parameterTypes)) {
			PatcherAccountLocalServiceUtil.setPatcherBuildPatcherAccounts(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName90.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes90, parameterTypes)) {
			return PatcherAccountLocalServiceUtil.getBeanIdentifier();
		}

		if (_methodName91.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes91, parameterTypes)) {
			PatcherAccountLocalServiceUtil.setBeanIdentifier((java.lang.String)arguments[0]);

			return null;
		}

		throw new UnsupportedOperationException();
	}

	private static final String _methodName0 = "addPatcherAccount";
	private static final String[] _methodParameterTypes0 = new String[] {
			"com.liferay.osb.patcher.model.PatcherAccount"
		};
	private static final String _methodName1 = "createPatcherAccount";
	private static final String[] _methodParameterTypes1 = new String[] { "long" };
	private static final String _methodName2 = "deletePatcherAccount";
	private static final String[] _methodParameterTypes2 = new String[] { "long" };
	private static final String _methodName3 = "deletePatcherAccount";
	private static final String[] _methodParameterTypes3 = new String[] {
			"com.liferay.osb.patcher.model.PatcherAccount"
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
	private static final String _methodName10 = "fetchPatcherAccount";
	private static final String[] _methodParameterTypes10 = new String[] { "long" };
	private static final String _methodName11 = "getPatcherAccount";
	private static final String[] _methodParameterTypes11 = new String[] { "long" };
	private static final String _methodName12 = "getPersistedModel";
	private static final String[] _methodParameterTypes12 = new String[] {
			"java.io.Serializable"
		};
	private static final String _methodName13 = "getPatcherAccounts";
	private static final String[] _methodParameterTypes13 = new String[] {
			"int", "int"
		};
	private static final String _methodName14 = "getPatcherAccountsCount";
	private static final String[] _methodParameterTypes14 = new String[] {  };
	private static final String _methodName15 = "updatePatcherAccount";
	private static final String[] _methodParameterTypes15 = new String[] {
			"com.liferay.osb.patcher.model.PatcherAccount"
		};
	private static final String _methodName16 = "addPatcherBuildPatcherAccount";
	private static final String[] _methodParameterTypes16 = new String[] {
			"long", "long"
		};
	private static final String _methodName17 = "addPatcherBuildPatcherAccount";
	private static final String[] _methodParameterTypes17 = new String[] {
			"long", "com.liferay.osb.patcher.model.PatcherAccount"
		};
	private static final String _methodName18 = "addPatcherBuildPatcherAccounts";
	private static final String[] _methodParameterTypes18 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName19 = "addPatcherBuildPatcherAccounts";
	private static final String[] _methodParameterTypes19 = new String[] {
			"long", "java.util.List"
		};
	private static final String _methodName20 = "clearPatcherBuildPatcherAccounts";
	private static final String[] _methodParameterTypes20 = new String[] { "long" };
	private static final String _methodName21 = "deletePatcherBuildPatcherAccount";
	private static final String[] _methodParameterTypes21 = new String[] {
			"long", "long"
		};
	private static final String _methodName22 = "deletePatcherBuildPatcherAccount";
	private static final String[] _methodParameterTypes22 = new String[] {
			"long", "com.liferay.osb.patcher.model.PatcherAccount"
		};
	private static final String _methodName23 = "deletePatcherBuildPatcherAccounts";
	private static final String[] _methodParameterTypes23 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName24 = "deletePatcherBuildPatcherAccounts";
	private static final String[] _methodParameterTypes24 = new String[] {
			"long", "java.util.List"
		};
	private static final String _methodName25 = "getPatcherBuildPatcherAccounts";
	private static final String[] _methodParameterTypes25 = new String[] { "long" };
	private static final String _methodName26 = "getPatcherBuildPatcherAccounts";
	private static final String[] _methodParameterTypes26 = new String[] {
			"long", "int", "int"
		};
	private static final String _methodName27 = "getPatcherBuildPatcherAccounts";
	private static final String[] _methodParameterTypes27 = new String[] {
			"long", "int", "int",
			"com.liferay.portal.kernel.util.OrderByComparator"
		};
	private static final String _methodName28 = "getPatcherBuildPatcherAccountsCount";
	private static final String[] _methodParameterTypes28 = new String[] { "long" };
	private static final String _methodName29 = "hasPatcherBuildPatcherAccount";
	private static final String[] _methodParameterTypes29 = new String[] {
			"long", "long"
		};
	private static final String _methodName30 = "hasPatcherBuildPatcherAccounts";
	private static final String[] _methodParameterTypes30 = new String[] { "long" };
	private static final String _methodName31 = "setPatcherBuildPatcherAccounts";
	private static final String[] _methodParameterTypes31 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName90 = "getBeanIdentifier";
	private static final String[] _methodParameterTypes90 = new String[] {  };
	private static final String _methodName91 = "setBeanIdentifier";
	private static final String[] _methodParameterTypes91 = new String[] {
			"java.lang.String"
		};
}