/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.base;

import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;

import java.util.Arrays;

/**
 * @author Calvin Keum
 * @generated
 */
public class PatcherFixLocalServiceClpInvoker {
	public Object invokeMethod(String name, String[] parameterTypes,
		Object[] arguments) throws Throwable {
		if (_methodName0.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes0, parameterTypes)) {
			return PatcherFixLocalServiceUtil.addPatcherFix((com.liferay.osb.patcher.model.PatcherFix)arguments[0]);
		}

		if (_methodName1.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes1, parameterTypes)) {
			return PatcherFixLocalServiceUtil.createPatcherFix(((Long)arguments[0]).longValue());
		}

		if (_methodName2.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes2, parameterTypes)) {
			return PatcherFixLocalServiceUtil.deletePatcherFix(((Long)arguments[0]).longValue());
		}

		if (_methodName3.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes3, parameterTypes)) {
			return PatcherFixLocalServiceUtil.deletePatcherFix((com.liferay.osb.patcher.model.PatcherFix)arguments[0]);
		}

		if (_methodName4.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes4, parameterTypes)) {
			return PatcherFixLocalServiceUtil.dynamicQuery();
		}

		if (_methodName5.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes5, parameterTypes)) {
			return PatcherFixLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0]);
		}

		if (_methodName6.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes6, parameterTypes)) {
			return PatcherFixLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue());
		}

		if (_methodName7.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes7, parameterTypes)) {
			return PatcherFixLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue(),
				(com.liferay.portal.kernel.util.OrderByComparator)arguments[3]);
		}

		if (_methodName8.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes8, parameterTypes)) {
			return PatcherFixLocalServiceUtil.dynamicQueryCount((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0]);
		}

		if (_methodName9.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes9, parameterTypes)) {
			return PatcherFixLocalServiceUtil.dynamicQueryCount((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				(com.liferay.portal.kernel.dao.orm.Projection)arguments[1]);
		}

		if (_methodName10.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes10, parameterTypes)) {
			return PatcherFixLocalServiceUtil.fetchPatcherFix(((Long)arguments[0]).longValue());
		}

		if (_methodName11.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes11, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPatcherFix(((Long)arguments[0]).longValue());
		}

		if (_methodName12.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes12, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPersistedModel((java.io.Serializable)arguments[0]);
		}

		if (_methodName13.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes13, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPatcherFixs(((Integer)arguments[0]).intValue(),
				((Integer)arguments[1]).intValue());
		}

		if (_methodName14.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes14, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPatcherFixsCount();
		}

		if (_methodName15.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes15, parameterTypes)) {
			return PatcherFixLocalServiceUtil.updatePatcherFix((com.liferay.osb.patcher.model.PatcherFix)arguments[0]);
		}

		if (_methodName16.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes16, parameterTypes)) {
			PatcherFixLocalServiceUtil.addPatcherBuildPatcherFix(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());

			return null;
		}

		if (_methodName17.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes17, parameterTypes)) {
			PatcherFixLocalServiceUtil.addPatcherBuildPatcherFix(((Long)arguments[0]).longValue(),
				(com.liferay.osb.patcher.model.PatcherFix)arguments[1]);

			return null;
		}

		if (_methodName18.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes18, parameterTypes)) {
			PatcherFixLocalServiceUtil.addPatcherBuildPatcherFixs(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName19.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes19, parameterTypes)) {
			PatcherFixLocalServiceUtil.addPatcherBuildPatcherFixs(((Long)arguments[0]).longValue(),
				(java.util.List<com.liferay.osb.patcher.model.PatcherFix>)arguments[1]);

			return null;
		}

		if (_methodName20.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes20, parameterTypes)) {
			PatcherFixLocalServiceUtil.clearPatcherBuildPatcherFixs(((Long)arguments[0]).longValue());

			return null;
		}

		if (_methodName21.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes21, parameterTypes)) {
			PatcherFixLocalServiceUtil.deletePatcherBuildPatcherFix(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());

			return null;
		}

		if (_methodName22.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes22, parameterTypes)) {
			PatcherFixLocalServiceUtil.deletePatcherBuildPatcherFix(((Long)arguments[0]).longValue(),
				(com.liferay.osb.patcher.model.PatcherFix)arguments[1]);

			return null;
		}

		if (_methodName23.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes23, parameterTypes)) {
			PatcherFixLocalServiceUtil.deletePatcherBuildPatcherFixs(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName24.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes24, parameterTypes)) {
			PatcherFixLocalServiceUtil.deletePatcherBuildPatcherFixs(((Long)arguments[0]).longValue(),
				(java.util.List<com.liferay.osb.patcher.model.PatcherFix>)arguments[1]);

			return null;
		}

		if (_methodName25.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes25, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(((Long)arguments[0]).longValue());
		}

		if (_methodName26.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes26, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(((Long)arguments[0]).longValue(),
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue());
		}

		if (_methodName27.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes27, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(((Long)arguments[0]).longValue(),
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue(),
				(com.liferay.portal.kernel.util.OrderByComparator)arguments[3]);
		}

		if (_methodName28.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes28, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixsCount(((Long)arguments[0]).longValue());
		}

		if (_methodName29.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes29, parameterTypes)) {
			return PatcherFixLocalServiceUtil.hasPatcherBuildPatcherFix(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());
		}

		if (_methodName30.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes30, parameterTypes)) {
			return PatcherFixLocalServiceUtil.hasPatcherBuildPatcherFixs(((Long)arguments[0]).longValue());
		}

		if (_methodName31.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes31, parameterTypes)) {
			PatcherFixLocalServiceUtil.setPatcherBuildPatcherFixs(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName32.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes32, parameterTypes)) {
			PatcherFixLocalServiceUtil.addPatcherFixPackPatcherFix(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());

			return null;
		}

		if (_methodName33.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes33, parameterTypes)) {
			PatcherFixLocalServiceUtil.addPatcherFixPackPatcherFix(((Long)arguments[0]).longValue(),
				(com.liferay.osb.patcher.model.PatcherFix)arguments[1]);

			return null;
		}

		if (_methodName34.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes34, parameterTypes)) {
			PatcherFixLocalServiceUtil.addPatcherFixPackPatcherFixs(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName35.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes35, parameterTypes)) {
			PatcherFixLocalServiceUtil.addPatcherFixPackPatcherFixs(((Long)arguments[0]).longValue(),
				(java.util.List<com.liferay.osb.patcher.model.PatcherFix>)arguments[1]);

			return null;
		}

		if (_methodName36.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes36, parameterTypes)) {
			PatcherFixLocalServiceUtil.clearPatcherFixPackPatcherFixs(((Long)arguments[0]).longValue());

			return null;
		}

		if (_methodName37.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes37, parameterTypes)) {
			PatcherFixLocalServiceUtil.deletePatcherFixPackPatcherFix(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());

			return null;
		}

		if (_methodName38.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes38, parameterTypes)) {
			PatcherFixLocalServiceUtil.deletePatcherFixPackPatcherFix(((Long)arguments[0]).longValue(),
				(com.liferay.osb.patcher.model.PatcherFix)arguments[1]);

			return null;
		}

		if (_methodName39.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes39, parameterTypes)) {
			PatcherFixLocalServiceUtil.deletePatcherFixPackPatcherFixs(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName40.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes40, parameterTypes)) {
			PatcherFixLocalServiceUtil.deletePatcherFixPackPatcherFixs(((Long)arguments[0]).longValue(),
				(java.util.List<com.liferay.osb.patcher.model.PatcherFix>)arguments[1]);

			return null;
		}

		if (_methodName41.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes41, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixs(((Long)arguments[0]).longValue());
		}

		if (_methodName42.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes42, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixs(((Long)arguments[0]).longValue(),
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue());
		}

		if (_methodName43.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes43, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixs(((Long)arguments[0]).longValue(),
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue(),
				(com.liferay.portal.kernel.util.OrderByComparator)arguments[3]);
		}

		if (_methodName44.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes44, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixsCount(((Long)arguments[0]).longValue());
		}

		if (_methodName45.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes45, parameterTypes)) {
			return PatcherFixLocalServiceUtil.hasPatcherFixPackPatcherFix(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());
		}

		if (_methodName46.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes46, parameterTypes)) {
			return PatcherFixLocalServiceUtil.hasPatcherFixPackPatcherFixs(((Long)arguments[0]).longValue());
		}

		if (_methodName47.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes47, parameterTypes)) {
			PatcherFixLocalServiceUtil.setPatcherFixPackPatcherFixs(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName106.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes106, parameterTypes)) {
			return PatcherFixLocalServiceUtil.getBeanIdentifier();
		}

		if (_methodName107.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes107, parameterTypes)) {
			PatcherFixLocalServiceUtil.setBeanIdentifier((java.lang.String)arguments[0]);

			return null;
		}

		throw new UnsupportedOperationException();
	}

	private static final String _methodName0 = "addPatcherFix";
	private static final String[] _methodParameterTypes0 = new String[] {
			"com.liferay.osb.patcher.model.PatcherFix"
		};
	private static final String _methodName1 = "createPatcherFix";
	private static final String[] _methodParameterTypes1 = new String[] { "long" };
	private static final String _methodName2 = "deletePatcherFix";
	private static final String[] _methodParameterTypes2 = new String[] { "long" };
	private static final String _methodName3 = "deletePatcherFix";
	private static final String[] _methodParameterTypes3 = new String[] {
			"com.liferay.osb.patcher.model.PatcherFix"
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
	private static final String _methodName10 = "fetchPatcherFix";
	private static final String[] _methodParameterTypes10 = new String[] { "long" };
	private static final String _methodName11 = "getPatcherFix";
	private static final String[] _methodParameterTypes11 = new String[] { "long" };
	private static final String _methodName12 = "getPersistedModel";
	private static final String[] _methodParameterTypes12 = new String[] {
			"java.io.Serializable"
		};
	private static final String _methodName13 = "getPatcherFixs";
	private static final String[] _methodParameterTypes13 = new String[] {
			"int", "int"
		};
	private static final String _methodName14 = "getPatcherFixsCount";
	private static final String[] _methodParameterTypes14 = new String[] {  };
	private static final String _methodName15 = "updatePatcherFix";
	private static final String[] _methodParameterTypes15 = new String[] {
			"com.liferay.osb.patcher.model.PatcherFix"
		};
	private static final String _methodName16 = "addPatcherBuildPatcherFix";
	private static final String[] _methodParameterTypes16 = new String[] {
			"long", "long"
		};
	private static final String _methodName17 = "addPatcherBuildPatcherFix";
	private static final String[] _methodParameterTypes17 = new String[] {
			"long", "com.liferay.osb.patcher.model.PatcherFix"
		};
	private static final String _methodName18 = "addPatcherBuildPatcherFixs";
	private static final String[] _methodParameterTypes18 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName19 = "addPatcherBuildPatcherFixs";
	private static final String[] _methodParameterTypes19 = new String[] {
			"long", "java.util.List"
		};
	private static final String _methodName20 = "clearPatcherBuildPatcherFixs";
	private static final String[] _methodParameterTypes20 = new String[] { "long" };
	private static final String _methodName21 = "deletePatcherBuildPatcherFix";
	private static final String[] _methodParameterTypes21 = new String[] {
			"long", "long"
		};
	private static final String _methodName22 = "deletePatcherBuildPatcherFix";
	private static final String[] _methodParameterTypes22 = new String[] {
			"long", "com.liferay.osb.patcher.model.PatcherFix"
		};
	private static final String _methodName23 = "deletePatcherBuildPatcherFixs";
	private static final String[] _methodParameterTypes23 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName24 = "deletePatcherBuildPatcherFixs";
	private static final String[] _methodParameterTypes24 = new String[] {
			"long", "java.util.List"
		};
	private static final String _methodName25 = "getPatcherBuildPatcherFixs";
	private static final String[] _methodParameterTypes25 = new String[] { "long" };
	private static final String _methodName26 = "getPatcherBuildPatcherFixs";
	private static final String[] _methodParameterTypes26 = new String[] {
			"long", "int", "int"
		};
	private static final String _methodName27 = "getPatcherBuildPatcherFixs";
	private static final String[] _methodParameterTypes27 = new String[] {
			"long", "int", "int",
			"com.liferay.portal.kernel.util.OrderByComparator"
		};
	private static final String _methodName28 = "getPatcherBuildPatcherFixsCount";
	private static final String[] _methodParameterTypes28 = new String[] { "long" };
	private static final String _methodName29 = "hasPatcherBuildPatcherFix";
	private static final String[] _methodParameterTypes29 = new String[] {
			"long", "long"
		};
	private static final String _methodName30 = "hasPatcherBuildPatcherFixs";
	private static final String[] _methodParameterTypes30 = new String[] { "long" };
	private static final String _methodName31 = "setPatcherBuildPatcherFixs";
	private static final String[] _methodParameterTypes31 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName32 = "addPatcherFixPackPatcherFix";
	private static final String[] _methodParameterTypes32 = new String[] {
			"long", "long"
		};
	private static final String _methodName33 = "addPatcherFixPackPatcherFix";
	private static final String[] _methodParameterTypes33 = new String[] {
			"long", "com.liferay.osb.patcher.model.PatcherFix"
		};
	private static final String _methodName34 = "addPatcherFixPackPatcherFixs";
	private static final String[] _methodParameterTypes34 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName35 = "addPatcherFixPackPatcherFixs";
	private static final String[] _methodParameterTypes35 = new String[] {
			"long", "java.util.List"
		};
	private static final String _methodName36 = "clearPatcherFixPackPatcherFixs";
	private static final String[] _methodParameterTypes36 = new String[] { "long" };
	private static final String _methodName37 = "deletePatcherFixPackPatcherFix";
	private static final String[] _methodParameterTypes37 = new String[] {
			"long", "long"
		};
	private static final String _methodName38 = "deletePatcherFixPackPatcherFix";
	private static final String[] _methodParameterTypes38 = new String[] {
			"long", "com.liferay.osb.patcher.model.PatcherFix"
		};
	private static final String _methodName39 = "deletePatcherFixPackPatcherFixs";
	private static final String[] _methodParameterTypes39 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName40 = "deletePatcherFixPackPatcherFixs";
	private static final String[] _methodParameterTypes40 = new String[] {
			"long", "java.util.List"
		};
	private static final String _methodName41 = "getPatcherFixPackPatcherFixs";
	private static final String[] _methodParameterTypes41 = new String[] { "long" };
	private static final String _methodName42 = "getPatcherFixPackPatcherFixs";
	private static final String[] _methodParameterTypes42 = new String[] {
			"long", "int", "int"
		};
	private static final String _methodName43 = "getPatcherFixPackPatcherFixs";
	private static final String[] _methodParameterTypes43 = new String[] {
			"long", "int", "int",
			"com.liferay.portal.kernel.util.OrderByComparator"
		};
	private static final String _methodName44 = "getPatcherFixPackPatcherFixsCount";
	private static final String[] _methodParameterTypes44 = new String[] { "long" };
	private static final String _methodName45 = "hasPatcherFixPackPatcherFix";
	private static final String[] _methodParameterTypes45 = new String[] {
			"long", "long"
		};
	private static final String _methodName46 = "hasPatcherFixPackPatcherFixs";
	private static final String[] _methodParameterTypes46 = new String[] { "long" };
	private static final String _methodName47 = "setPatcherFixPackPatcherFixs";
	private static final String[] _methodParameterTypes47 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName106 = "getBeanIdentifier";
	private static final String[] _methodParameterTypes106 = new String[] {  };
	private static final String _methodName107 = "setBeanIdentifier";
	private static final String[] _methodParameterTypes107 = new String[] {
			"java.lang.String"
		};
}