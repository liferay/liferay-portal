/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.GetterUtil;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zsolt Balogh
 */
public class BaseModelUtil {

	public static BaseModel<?> fetchBaseModel(
			Class<?> clazz, String modelClassName, long classPK)
		throws Exception {

		ClassLoader classLoader = clazz.getClassLoader();

		int pos = modelClassName.indexOf(".model.");

		String simpleClassName = modelClassName.substring(pos + 7);

		Class<?> localServiceUtilClass = classLoader.loadClass(
			StringBundler.concat(
				modelClassName.substring(0, pos), ".service.", simpleClassName,
				"LocalServiceUtil"));

		String methodName = "fetch" + simpleClassName;

		Method fetchBaseModelMethod = localServiceUtilClass.getMethod(
			methodName, new Class<?>[] {long.class});

		return (BaseModel<?>)fetchBaseModelMethod.invoke(
			localServiceUtilClass, classPK);
	}

	public static String fetchBaseModelRequestKey(BaseModel<?> baseModel) {
		try {
			return getBaseModelRequestKey(baseModel);
		}
		catch (Exception exception) {
			return null;
		}
	}

	public static List<Long> fetchBaseModelsPrimaryIds(
			List<BaseModel> baseModels)
		throws Exception {

		List<Long> baseModelIds = new ArrayList<>();

		for (BaseModel<?> baseModel : baseModels) {
			baseModelIds.add(GetterUtil.getLong(baseModel.getPrimaryKeyObj()));
		}

		return baseModelIds;
	}

	public static Integer fetchBaseModelStatus(BaseModel<?> baseModel) {
		try {
			return getBaseModelStatus(baseModel);
		}
		catch (Exception exception) {
			return null;
		}
	}

	public static Long fetchBaseModelStatusByUserId(BaseModel<?> baseModel) {
		try {
			return getBaseModelStatusByUserId(baseModel);
		}
		catch (Exception exception) {
			return null;
		}
	}

	public static Long fetchBaseModelUserId(BaseModel<?> baseModel) {
		try {
			return getBaseModelUserId(baseModel);
		}
		catch (Exception exception) {
			return null;
		}
	}

	public static String getBaseModelRequestKey(BaseModel<?> baseModel)
		throws Exception {

		Class<?> clazz = baseModel.getClass();

		Method getRequestKeyMethod = clazz.getMethod(
			"getRequestKey", new Class<?>[0]);

		return (String)getRequestKeyMethod.invoke(baseModel);
	}

	public static int getBaseModelStatus(BaseModel<?> baseModel)
		throws Exception {

		Class<?> baseModelClass = baseModel.getClass();

		Method getStatusMethod = baseModelClass.getMethod(
			"getStatus", new Class<?>[0]);

		return (Integer)getStatusMethod.invoke(baseModel);
	}

	public static long getBaseModelStatusByUserId(BaseModel<?> baseModel)
		throws Exception {

		Class<?> baseModelClass = baseModel.getClass();

		Method getStatusByUserIdMethod = baseModelClass.getMethod(
			"getStatusByUserId", new Class<?>[0]);

		return (Long)getStatusByUserIdMethod.invoke(baseModel);
	}

	public static long getBaseModelUserId(BaseModel<?> baseModel)
		throws Exception {

		Class<?> baseModelClass = baseModel.getClass();

		Method getUserIdMethod = baseModelClass.getMethod(
			"getUserId", new Class<?>[0]);

		return (Long)getUserIdMethod.invoke(baseModel);
	}

	public static void setBaseModelStatus(BaseModel<?> baseModel, int status)
		throws Exception {

		Class<?> clazz = baseModel.getClass();

		Method setStatusMethod = clazz.getMethod(
			"setStatus", new Class<?>[] {int.class});

		setStatusMethod.invoke(baseModel, status);
	}

}