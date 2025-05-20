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

import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;

import java.util.Arrays;

/**
 * @author Calvin Keum
 * @generated
 */
public class PatcherBuildLocalServiceClpInvoker {
	public Object invokeMethod(String name, String[] parameterTypes,
		Object[] arguments) throws Throwable {
		if (_methodName0.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes0, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.addPatcherBuild((com.liferay.osb.patcher.model.PatcherBuild)arguments[0]);
		}

		if (_methodName1.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes1, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.createPatcherBuild(((Long)arguments[0]).longValue());
		}

		if (_methodName2.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes2, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.deletePatcherBuild(((Long)arguments[0]).longValue());
		}

		if (_methodName3.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes3, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.deletePatcherBuild((com.liferay.osb.patcher.model.PatcherBuild)arguments[0]);
		}

		if (_methodName4.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes4, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.dynamicQuery();
		}

		if (_methodName5.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes5, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0]);
		}

		if (_methodName6.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes6, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue());
		}

		if (_methodName7.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes7, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.dynamicQuery((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue(),
				(com.liferay.portal.kernel.util.OrderByComparator)arguments[3]);
		}

		if (_methodName8.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes8, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.dynamicQueryCount((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0]);
		}

		if (_methodName9.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes9, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.dynamicQueryCount((com.liferay.portal.kernel.dao.orm.DynamicQuery)arguments[0],
				(com.liferay.portal.kernel.dao.orm.Projection)arguments[1]);
		}

		if (_methodName10.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes10, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.fetchPatcherBuild(((Long)arguments[0]).longValue());
		}

		if (_methodName11.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes11, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPatcherBuild(((Long)arguments[0]).longValue());
		}

		if (_methodName12.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes12, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPersistedModel((java.io.Serializable)arguments[0]);
		}

		if (_methodName13.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes13, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPatcherBuilds(((Integer)arguments[0]).intValue(),
				((Integer)arguments[1]).intValue());
		}

		if (_methodName14.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes14, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPatcherBuildsCount();
		}

		if (_methodName15.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes15, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.updatePatcherBuild((com.liferay.osb.patcher.model.PatcherBuild)arguments[0]);
		}

		if (_methodName16.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes16, parameterTypes)) {
			PatcherBuildLocalServiceUtil.addPatcherAccountPatcherBuild(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());

			return null;
		}

		if (_methodName17.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes17, parameterTypes)) {
			PatcherBuildLocalServiceUtil.addPatcherAccountPatcherBuild(((Long)arguments[0]).longValue(),
				(com.liferay.osb.patcher.model.PatcherBuild)arguments[1]);

			return null;
		}

		if (_methodName18.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes18, parameterTypes)) {
			PatcherBuildLocalServiceUtil.addPatcherAccountPatcherBuilds(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName19.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes19, parameterTypes)) {
			PatcherBuildLocalServiceUtil.addPatcherAccountPatcherBuilds(((Long)arguments[0]).longValue(),
				(java.util.List<com.liferay.osb.patcher.model.PatcherBuild>)arguments[1]);

			return null;
		}

		if (_methodName20.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes20, parameterTypes)) {
			PatcherBuildLocalServiceUtil.clearPatcherAccountPatcherBuilds(((Long)arguments[0]).longValue());

			return null;
		}

		if (_methodName21.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes21, parameterTypes)) {
			PatcherBuildLocalServiceUtil.deletePatcherAccountPatcherBuild(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());

			return null;
		}

		if (_methodName22.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes22, parameterTypes)) {
			PatcherBuildLocalServiceUtil.deletePatcherAccountPatcherBuild(((Long)arguments[0]).longValue(),
				(com.liferay.osb.patcher.model.PatcherBuild)arguments[1]);

			return null;
		}

		if (_methodName23.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes23, parameterTypes)) {
			PatcherBuildLocalServiceUtil.deletePatcherAccountPatcherBuilds(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName24.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes24, parameterTypes)) {
			PatcherBuildLocalServiceUtil.deletePatcherAccountPatcherBuilds(((Long)arguments[0]).longValue(),
				(java.util.List<com.liferay.osb.patcher.model.PatcherBuild>)arguments[1]);

			return null;
		}

		if (_methodName25.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes25, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPatcherAccountPatcherBuilds(((Long)arguments[0]).longValue());
		}

		if (_methodName26.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes26, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPatcherAccountPatcherBuilds(((Long)arguments[0]).longValue(),
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue());
		}

		if (_methodName27.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes27, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPatcherAccountPatcherBuilds(((Long)arguments[0]).longValue(),
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue(),
				(com.liferay.portal.kernel.util.OrderByComparator)arguments[3]);
		}

		if (_methodName28.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes28, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPatcherAccountPatcherBuildsCount(((Long)arguments[0]).longValue());
		}

		if (_methodName29.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes29, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.hasPatcherAccountPatcherBuild(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());
		}

		if (_methodName30.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes30, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.hasPatcherAccountPatcherBuilds(((Long)arguments[0]).longValue());
		}

		if (_methodName31.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes31, parameterTypes)) {
			PatcherBuildLocalServiceUtil.setPatcherAccountPatcherBuilds(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName32.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes32, parameterTypes)) {
			PatcherBuildLocalServiceUtil.addPatcherFixPatcherBuild(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());

			return null;
		}

		if (_methodName33.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes33, parameterTypes)) {
			PatcherBuildLocalServiceUtil.addPatcherFixPatcherBuild(((Long)arguments[0]).longValue(),
				(com.liferay.osb.patcher.model.PatcherBuild)arguments[1]);

			return null;
		}

		if (_methodName34.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes34, parameterTypes)) {
			PatcherBuildLocalServiceUtil.addPatcherFixPatcherBuilds(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName35.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes35, parameterTypes)) {
			PatcherBuildLocalServiceUtil.addPatcherFixPatcherBuilds(((Long)arguments[0]).longValue(),
				(java.util.List<com.liferay.osb.patcher.model.PatcherBuild>)arguments[1]);

			return null;
		}

		if (_methodName36.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes36, parameterTypes)) {
			PatcherBuildLocalServiceUtil.clearPatcherFixPatcherBuilds(((Long)arguments[0]).longValue());

			return null;
		}

		if (_methodName37.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes37, parameterTypes)) {
			PatcherBuildLocalServiceUtil.deletePatcherFixPatcherBuild(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());

			return null;
		}

		if (_methodName38.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes38, parameterTypes)) {
			PatcherBuildLocalServiceUtil.deletePatcherFixPatcherBuild(((Long)arguments[0]).longValue(),
				(com.liferay.osb.patcher.model.PatcherBuild)arguments[1]);

			return null;
		}

		if (_methodName39.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes39, parameterTypes)) {
			PatcherBuildLocalServiceUtil.deletePatcherFixPatcherBuilds(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName40.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes40, parameterTypes)) {
			PatcherBuildLocalServiceUtil.deletePatcherFixPatcherBuilds(((Long)arguments[0]).longValue(),
				(java.util.List<com.liferay.osb.patcher.model.PatcherBuild>)arguments[1]);

			return null;
		}

		if (_methodName41.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes41, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuilds(((Long)arguments[0]).longValue());
		}

		if (_methodName42.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes42, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuilds(((Long)arguments[0]).longValue(),
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue());
		}

		if (_methodName43.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes43, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuilds(((Long)arguments[0]).longValue(),
				((Integer)arguments[1]).intValue(),
				((Integer)arguments[2]).intValue(),
				(com.liferay.portal.kernel.util.OrderByComparator)arguments[3]);
		}

		if (_methodName44.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes44, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuildsCount(((Long)arguments[0]).longValue());
		}

		if (_methodName45.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes45, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.hasPatcherFixPatcherBuild(((Long)arguments[0]).longValue(),
				((Long)arguments[1]).longValue());
		}

		if (_methodName46.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes46, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.hasPatcherFixPatcherBuilds(((Long)arguments[0]).longValue());
		}

		if (_methodName47.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes47, parameterTypes)) {
			PatcherBuildLocalServiceUtil.setPatcherFixPatcherBuilds(((Long)arguments[0]).longValue(),
				(long[])arguments[1]);

			return null;
		}

		if (_methodName106.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes106, parameterTypes)) {
			return PatcherBuildLocalServiceUtil.getBeanIdentifier();
		}

		if (_methodName107.equals(name) &&
				Arrays.deepEquals(_methodParameterTypes107, parameterTypes)) {
			PatcherBuildLocalServiceUtil.setBeanIdentifier((java.lang.String)arguments[0]);

			return null;
		}

		throw new UnsupportedOperationException();
	}

	private static final String _methodName0 = "addPatcherBuild";
	private static final String[] _methodParameterTypes0 = new String[] {
			"com.liferay.osb.patcher.model.PatcherBuild"
		};
	private static final String _methodName1 = "createPatcherBuild";
	private static final String[] _methodParameterTypes1 = new String[] { "long" };
	private static final String _methodName2 = "deletePatcherBuild";
	private static final String[] _methodParameterTypes2 = new String[] { "long" };
	private static final String _methodName3 = "deletePatcherBuild";
	private static final String[] _methodParameterTypes3 = new String[] {
			"com.liferay.osb.patcher.model.PatcherBuild"
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
	private static final String _methodName10 = "fetchPatcherBuild";
	private static final String[] _methodParameterTypes10 = new String[] { "long" };
	private static final String _methodName11 = "getPatcherBuild";
	private static final String[] _methodParameterTypes11 = new String[] { "long" };
	private static final String _methodName12 = "getPersistedModel";
	private static final String[] _methodParameterTypes12 = new String[] {
			"java.io.Serializable"
		};
	private static final String _methodName13 = "getPatcherBuilds";
	private static final String[] _methodParameterTypes13 = new String[] {
			"int", "int"
		};
	private static final String _methodName14 = "getPatcherBuildsCount";
	private static final String[] _methodParameterTypes14 = new String[] {  };
	private static final String _methodName15 = "updatePatcherBuild";
	private static final String[] _methodParameterTypes15 = new String[] {
			"com.liferay.osb.patcher.model.PatcherBuild"
		};
	private static final String _methodName16 = "addPatcherAccountPatcherBuild";
	private static final String[] _methodParameterTypes16 = new String[] {
			"long", "long"
		};
	private static final String _methodName17 = "addPatcherAccountPatcherBuild";
	private static final String[] _methodParameterTypes17 = new String[] {
			"long", "com.liferay.osb.patcher.model.PatcherBuild"
		};
	private static final String _methodName18 = "addPatcherAccountPatcherBuilds";
	private static final String[] _methodParameterTypes18 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName19 = "addPatcherAccountPatcherBuilds";
	private static final String[] _methodParameterTypes19 = new String[] {
			"long", "java.util.List"
		};
	private static final String _methodName20 = "clearPatcherAccountPatcherBuilds";
	private static final String[] _methodParameterTypes20 = new String[] { "long" };
	private static final String _methodName21 = "deletePatcherAccountPatcherBuild";
	private static final String[] _methodParameterTypes21 = new String[] {
			"long", "long"
		};
	private static final String _methodName22 = "deletePatcherAccountPatcherBuild";
	private static final String[] _methodParameterTypes22 = new String[] {
			"long", "com.liferay.osb.patcher.model.PatcherBuild"
		};
	private static final String _methodName23 = "deletePatcherAccountPatcherBuilds";
	private static final String[] _methodParameterTypes23 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName24 = "deletePatcherAccountPatcherBuilds";
	private static final String[] _methodParameterTypes24 = new String[] {
			"long", "java.util.List"
		};
	private static final String _methodName25 = "getPatcherAccountPatcherBuilds";
	private static final String[] _methodParameterTypes25 = new String[] { "long" };
	private static final String _methodName26 = "getPatcherAccountPatcherBuilds";
	private static final String[] _methodParameterTypes26 = new String[] {
			"long", "int", "int"
		};
	private static final String _methodName27 = "getPatcherAccountPatcherBuilds";
	private static final String[] _methodParameterTypes27 = new String[] {
			"long", "int", "int",
			"com.liferay.portal.kernel.util.OrderByComparator"
		};
	private static final String _methodName28 = "getPatcherAccountPatcherBuildsCount";
	private static final String[] _methodParameterTypes28 = new String[] { "long" };
	private static final String _methodName29 = "hasPatcherAccountPatcherBuild";
	private static final String[] _methodParameterTypes29 = new String[] {
			"long", "long"
		};
	private static final String _methodName30 = "hasPatcherAccountPatcherBuilds";
	private static final String[] _methodParameterTypes30 = new String[] { "long" };
	private static final String _methodName31 = "setPatcherAccountPatcherBuilds";
	private static final String[] _methodParameterTypes31 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName32 = "addPatcherFixPatcherBuild";
	private static final String[] _methodParameterTypes32 = new String[] {
			"long", "long"
		};
	private static final String _methodName33 = "addPatcherFixPatcherBuild";
	private static final String[] _methodParameterTypes33 = new String[] {
			"long", "com.liferay.osb.patcher.model.PatcherBuild"
		};
	private static final String _methodName34 = "addPatcherFixPatcherBuilds";
	private static final String[] _methodParameterTypes34 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName35 = "addPatcherFixPatcherBuilds";
	private static final String[] _methodParameterTypes35 = new String[] {
			"long", "java.util.List"
		};
	private static final String _methodName36 = "clearPatcherFixPatcherBuilds";
	private static final String[] _methodParameterTypes36 = new String[] { "long" };
	private static final String _methodName37 = "deletePatcherFixPatcherBuild";
	private static final String[] _methodParameterTypes37 = new String[] {
			"long", "long"
		};
	private static final String _methodName38 = "deletePatcherFixPatcherBuild";
	private static final String[] _methodParameterTypes38 = new String[] {
			"long", "com.liferay.osb.patcher.model.PatcherBuild"
		};
	private static final String _methodName39 = "deletePatcherFixPatcherBuilds";
	private static final String[] _methodParameterTypes39 = new String[] {
			"long", "long[][]"
		};
	private static final String _methodName40 = "deletePatcherFixPatcherBuilds";
	private static final String[] _methodParameterTypes40 = new String[] {
			"long", "java.util.List"
		};
	private static final String _methodName41 = "getPatcherFixPatcherBuilds";
	private static final String[] _methodParameterTypes41 = new String[] { "long" };
	private static final String _methodName42 = "getPatcherFixPatcherBuilds";
	private static final String[] _methodParameterTypes42 = new String[] {
			"long", "int", "int"
		};
	private static final String _methodName43 = "getPatcherFixPatcherBuilds";
	private static final String[] _methodParameterTypes43 = new String[] {
			"long", "int", "int",
			"com.liferay.portal.kernel.util.OrderByComparator"
		};
	private static final String _methodName44 = "getPatcherFixPatcherBuildsCount";
	private static final String[] _methodParameterTypes44 = new String[] { "long" };
	private static final String _methodName45 = "hasPatcherFixPatcherBuild";
	private static final String[] _methodParameterTypes45 = new String[] {
			"long", "long"
		};
	private static final String _methodName46 = "hasPatcherFixPatcherBuilds";
	private static final String[] _methodParameterTypes46 = new String[] { "long" };
	private static final String _methodName47 = "setPatcherFixPatcherBuilds";
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