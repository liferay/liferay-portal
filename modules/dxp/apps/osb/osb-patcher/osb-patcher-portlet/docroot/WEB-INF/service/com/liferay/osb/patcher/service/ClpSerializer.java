/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.osb.patcher.model.PatcherAccountClp;
import com.liferay.osb.patcher.model.PatcherBuildClp;
import com.liferay.osb.patcher.model.PatcherBuildRelClp;
import com.liferay.osb.patcher.model.PatcherFixClp;
import com.liferay.osb.patcher.model.PatcherFixComponentClp;
import com.liferay.osb.patcher.model.PatcherFixPackClp;
import com.liferay.osb.patcher.model.PatcherFixRelClp;
import com.liferay.osb.patcher.model.PatcherProductVersionClp;
import com.liferay.osb.patcher.model.PatcherProjectVersionClp;
import com.liferay.osb.patcher.model.PatcherTicketHintClp;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ClassLoaderObjectInputStream;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.BaseModel;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Calvin Keum
 */
public class ClpSerializer {

	public static String getServletContextName() {
		if (Validator.isNotNull(_servletContextName)) {
			return _servletContextName;
		}

		synchronized (ClpSerializer.class) {
			if (Validator.isNotNull(_servletContextName)) {
				return _servletContextName;
			}

			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Class<?> portletPropsClass = classLoader.loadClass(
					"com.liferay.util.portlet.PortletProps");

				Method getMethod = portletPropsClass.getMethod(
					"get", new Class<?>[] {String.class});

				String portletPropsServletContextName =
					(String)getMethod.invoke(
						null, "osb-patcher-portlet-deployment-context");

				if (Validator.isNotNull(portletPropsServletContextName)) {
					_servletContextName = portletPropsServletContextName;
				}
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Unable to locate deployment context from portlet properties");
				}
			}

			if (Validator.isNull(_servletContextName)) {
				try {
					String propsUtilServletContextName = PropsUtil.get(
						"osb-patcher-portlet-deployment-context");

					if (Validator.isNotNull(propsUtilServletContextName)) {
						_servletContextName = propsUtilServletContextName;
					}
				}
				catch (Throwable t) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"Unable to locate deployment context from portal properties");
					}
				}
			}

			if (Validator.isNull(_servletContextName)) {
				_servletContextName = "osb-patcher-portlet";
			}

			return _servletContextName;
		}
	}

	public static Object translateInput(BaseModel<?> oldModel) {
		Class<?> oldModelClass = oldModel.getClass();

		String oldModelClassName = oldModelClass.getName();

		if (oldModelClassName.equals(PatcherAccountClp.class.getName())) {
			return translateInputPatcherAccount(oldModel);
		}

		if (oldModelClassName.equals(PatcherBuildClp.class.getName())) {
			return translateInputPatcherBuild(oldModel);
		}

		if (oldModelClassName.equals(PatcherBuildRelClp.class.getName())) {
			return translateInputPatcherBuildRel(oldModel);
		}

		if (oldModelClassName.equals(PatcherFixClp.class.getName())) {
			return translateInputPatcherFix(oldModel);
		}

		if (oldModelClassName.equals(PatcherFixComponentClp.class.getName())) {
			return translateInputPatcherFixComponent(oldModel);
		}

		if (oldModelClassName.equals(PatcherFixPackClp.class.getName())) {
			return translateInputPatcherFixPack(oldModel);
		}

		if (oldModelClassName.equals(PatcherFixRelClp.class.getName())) {
			return translateInputPatcherFixRel(oldModel);
		}

		if (oldModelClassName.equals(
				PatcherProductVersionClp.class.getName())) {

			return translateInputPatcherProductVersion(oldModel);
		}

		if (oldModelClassName.equals(
				PatcherProjectVersionClp.class.getName())) {

			return translateInputPatcherProjectVersion(oldModel);
		}

		if (oldModelClassName.equals(PatcherTicketHintClp.class.getName())) {
			return translateInputPatcherTicketHint(oldModel);
		}

		return oldModel;
	}

	public static Object translateInput(List<Object> oldList) {
		List<Object> newList = new ArrayList<>(oldList.size());

		for (Object curObj : oldList) {
			newList.add(translateInput(curObj));
		}

		return newList;
	}

	public static Object translateInput(Object obj) {
		if (obj instanceof BaseModel<?>) {
			return translateInput((BaseModel<?>)obj);
		}
		else if (obj instanceof List<?>) {
			return translateInput((List<Object>)obj);
		}

		return obj;
	}

	public static Object translateInputPatcherAccount(BaseModel<?> oldModel) {
		PatcherAccountClp oldClpModel = (PatcherAccountClp)oldModel;

		BaseModel<?> newModel = oldClpModel.getPatcherAccountRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInputPatcherBuild(BaseModel<?> oldModel) {
		PatcherBuildClp oldClpModel = (PatcherBuildClp)oldModel;

		BaseModel<?> newModel = oldClpModel.getPatcherBuildRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInputPatcherBuildRel(BaseModel<?> oldModel) {
		PatcherBuildRelClp oldClpModel = (PatcherBuildRelClp)oldModel;

		BaseModel<?> newModel = oldClpModel.getPatcherBuildRelRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInputPatcherFix(BaseModel<?> oldModel) {
		PatcherFixClp oldClpModel = (PatcherFixClp)oldModel;

		BaseModel<?> newModel = oldClpModel.getPatcherFixRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInputPatcherFixComponent(
		BaseModel<?> oldModel) {

		PatcherFixComponentClp oldClpModel = (PatcherFixComponentClp)oldModel;

		BaseModel<?> newModel = oldClpModel.getPatcherFixComponentRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInputPatcherFixPack(BaseModel<?> oldModel) {
		PatcherFixPackClp oldClpModel = (PatcherFixPackClp)oldModel;

		BaseModel<?> newModel = oldClpModel.getPatcherFixPackRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInputPatcherFixRel(BaseModel<?> oldModel) {
		PatcherFixRelClp oldClpModel = (PatcherFixRelClp)oldModel;

		BaseModel<?> newModel = oldClpModel.getPatcherFixRelRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInputPatcherProductVersion(
		BaseModel<?> oldModel) {

		PatcherProductVersionClp oldClpModel =
			(PatcherProductVersionClp)oldModel;

		BaseModel<?> newModel =
			oldClpModel.getPatcherProductVersionRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInputPatcherProjectVersion(
		BaseModel<?> oldModel) {

		PatcherProjectVersionClp oldClpModel =
			(PatcherProjectVersionClp)oldModel;

		BaseModel<?> newModel =
			oldClpModel.getPatcherProjectVersionRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInputPatcherTicketHint(
		BaseModel<?> oldModel) {

		PatcherTicketHintClp oldClpModel = (PatcherTicketHintClp)oldModel;

		BaseModel<?> newModel = oldClpModel.getPatcherTicketHintRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateOutput(BaseModel<?> oldModel) {
		Class<?> oldModelClass = oldModel.getClass();

		String oldModelClassName = oldModelClass.getName();

		if (oldModelClassName.equals(
				"com.liferay.osb.patcher.model.impl.PatcherAccountImpl")) {

			return translateOutputPatcherAccount(oldModel);
		}
		else if (oldModelClassName.endsWith("Clp")) {
			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Method getClpSerializerClassMethod = oldModelClass.getMethod(
					"getClpSerializerClass");

				Class<?> oldClpSerializerClass =
					(Class<?>)getClpSerializerClassMethod.invoke(oldModel);

				Class<?> newClpSerializerClass = classLoader.loadClass(
					oldClpSerializerClass.getName());

				Method translateOutputMethod = newClpSerializerClass.getMethod(
					"translateOutput", BaseModel.class);

				Class<?> oldModelModelClass = oldModel.getModelClass();

				Method getRemoteModelMethod = oldModelClass.getMethod(
					"get" + oldModelModelClass.getSimpleName() + "RemoteModel");

				Object oldRemoteModel = getRemoteModelMethod.invoke(oldModel);

				BaseModel<?> newModel =
					(BaseModel<?>)translateOutputMethod.invoke(
						null, oldRemoteModel);

				return newModel;
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to translate " + oldModelClassName, t);
				}
			}
		}

		if (oldModelClassName.equals(
				"com.liferay.osb.patcher.model.impl.PatcherBuildImpl")) {

			return translateOutputPatcherBuild(oldModel);
		}
		else if (oldModelClassName.endsWith("Clp")) {
			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Method getClpSerializerClassMethod = oldModelClass.getMethod(
					"getClpSerializerClass");

				Class<?> oldClpSerializerClass =
					(Class<?>)getClpSerializerClassMethod.invoke(oldModel);

				Class<?> newClpSerializerClass = classLoader.loadClass(
					oldClpSerializerClass.getName());

				Method translateOutputMethod = newClpSerializerClass.getMethod(
					"translateOutput", BaseModel.class);

				Class<?> oldModelModelClass = oldModel.getModelClass();

				Method getRemoteModelMethod = oldModelClass.getMethod(
					"get" + oldModelModelClass.getSimpleName() + "RemoteModel");

				Object oldRemoteModel = getRemoteModelMethod.invoke(oldModel);

				BaseModel<?> newModel =
					(BaseModel<?>)translateOutputMethod.invoke(
						null, oldRemoteModel);

				return newModel;
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to translate " + oldModelClassName, t);
				}
			}
		}

		if (oldModelClassName.equals(
				"com.liferay.osb.patcher.model.impl.PatcherBuildRelImpl")) {

			return translateOutputPatcherBuildRel(oldModel);
		}
		else if (oldModelClassName.endsWith("Clp")) {
			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Method getClpSerializerClassMethod = oldModelClass.getMethod(
					"getClpSerializerClass");

				Class<?> oldClpSerializerClass =
					(Class<?>)getClpSerializerClassMethod.invoke(oldModel);

				Class<?> newClpSerializerClass = classLoader.loadClass(
					oldClpSerializerClass.getName());

				Method translateOutputMethod = newClpSerializerClass.getMethod(
					"translateOutput", BaseModel.class);

				Class<?> oldModelModelClass = oldModel.getModelClass();

				Method getRemoteModelMethod = oldModelClass.getMethod(
					"get" + oldModelModelClass.getSimpleName() + "RemoteModel");

				Object oldRemoteModel = getRemoteModelMethod.invoke(oldModel);

				BaseModel<?> newModel =
					(BaseModel<?>)translateOutputMethod.invoke(
						null, oldRemoteModel);

				return newModel;
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to translate " + oldModelClassName, t);
				}
			}
		}

		if (oldModelClassName.equals(
				"com.liferay.osb.patcher.model.impl.PatcherFixImpl")) {

			return translateOutputPatcherFix(oldModel);
		}
		else if (oldModelClassName.endsWith("Clp")) {
			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Method getClpSerializerClassMethod = oldModelClass.getMethod(
					"getClpSerializerClass");

				Class<?> oldClpSerializerClass =
					(Class<?>)getClpSerializerClassMethod.invoke(oldModel);

				Class<?> newClpSerializerClass = classLoader.loadClass(
					oldClpSerializerClass.getName());

				Method translateOutputMethod = newClpSerializerClass.getMethod(
					"translateOutput", BaseModel.class);

				Class<?> oldModelModelClass = oldModel.getModelClass();

				Method getRemoteModelMethod = oldModelClass.getMethod(
					"get" + oldModelModelClass.getSimpleName() + "RemoteModel");

				Object oldRemoteModel = getRemoteModelMethod.invoke(oldModel);

				BaseModel<?> newModel =
					(BaseModel<?>)translateOutputMethod.invoke(
						null, oldRemoteModel);

				return newModel;
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to translate " + oldModelClassName, t);
				}
			}
		}

		if (oldModelClassName.equals(
				"com.liferay.osb.patcher.model.impl.PatcherFixComponentImpl")) {

			return translateOutputPatcherFixComponent(oldModel);
		}
		else if (oldModelClassName.endsWith("Clp")) {
			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Method getClpSerializerClassMethod = oldModelClass.getMethod(
					"getClpSerializerClass");

				Class<?> oldClpSerializerClass =
					(Class<?>)getClpSerializerClassMethod.invoke(oldModel);

				Class<?> newClpSerializerClass = classLoader.loadClass(
					oldClpSerializerClass.getName());

				Method translateOutputMethod = newClpSerializerClass.getMethod(
					"translateOutput", BaseModel.class);

				Class<?> oldModelModelClass = oldModel.getModelClass();

				Method getRemoteModelMethod = oldModelClass.getMethod(
					"get" + oldModelModelClass.getSimpleName() + "RemoteModel");

				Object oldRemoteModel = getRemoteModelMethod.invoke(oldModel);

				BaseModel<?> newModel =
					(BaseModel<?>)translateOutputMethod.invoke(
						null, oldRemoteModel);

				return newModel;
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to translate " + oldModelClassName, t);
				}
			}
		}

		if (oldModelClassName.equals(
				"com.liferay.osb.patcher.model.impl.PatcherFixPackImpl")) {

			return translateOutputPatcherFixPack(oldModel);
		}
		else if (oldModelClassName.endsWith("Clp")) {
			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Method getClpSerializerClassMethod = oldModelClass.getMethod(
					"getClpSerializerClass");

				Class<?> oldClpSerializerClass =
					(Class<?>)getClpSerializerClassMethod.invoke(oldModel);

				Class<?> newClpSerializerClass = classLoader.loadClass(
					oldClpSerializerClass.getName());

				Method translateOutputMethod = newClpSerializerClass.getMethod(
					"translateOutput", BaseModel.class);

				Class<?> oldModelModelClass = oldModel.getModelClass();

				Method getRemoteModelMethod = oldModelClass.getMethod(
					"get" + oldModelModelClass.getSimpleName() + "RemoteModel");

				Object oldRemoteModel = getRemoteModelMethod.invoke(oldModel);

				BaseModel<?> newModel =
					(BaseModel<?>)translateOutputMethod.invoke(
						null, oldRemoteModel);

				return newModel;
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to translate " + oldModelClassName, t);
				}
			}
		}

		if (oldModelClassName.equals(
				"com.liferay.osb.patcher.model.impl.PatcherFixRelImpl")) {

			return translateOutputPatcherFixRel(oldModel);
		}
		else if (oldModelClassName.endsWith("Clp")) {
			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Method getClpSerializerClassMethod = oldModelClass.getMethod(
					"getClpSerializerClass");

				Class<?> oldClpSerializerClass =
					(Class<?>)getClpSerializerClassMethod.invoke(oldModel);

				Class<?> newClpSerializerClass = classLoader.loadClass(
					oldClpSerializerClass.getName());

				Method translateOutputMethod = newClpSerializerClass.getMethod(
					"translateOutput", BaseModel.class);

				Class<?> oldModelModelClass = oldModel.getModelClass();

				Method getRemoteModelMethod = oldModelClass.getMethod(
					"get" + oldModelModelClass.getSimpleName() + "RemoteModel");

				Object oldRemoteModel = getRemoteModelMethod.invoke(oldModel);

				BaseModel<?> newModel =
					(BaseModel<?>)translateOutputMethod.invoke(
						null, oldRemoteModel);

				return newModel;
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to translate " + oldModelClassName, t);
				}
			}
		}

		if (oldModelClassName.equals(
				"com.liferay.osb.patcher.model.impl.PatcherProductVersionImpl")) {

			return translateOutputPatcherProductVersion(oldModel);
		}
		else if (oldModelClassName.endsWith("Clp")) {
			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Method getClpSerializerClassMethod = oldModelClass.getMethod(
					"getClpSerializerClass");

				Class<?> oldClpSerializerClass =
					(Class<?>)getClpSerializerClassMethod.invoke(oldModel);

				Class<?> newClpSerializerClass = classLoader.loadClass(
					oldClpSerializerClass.getName());

				Method translateOutputMethod = newClpSerializerClass.getMethod(
					"translateOutput", BaseModel.class);

				Class<?> oldModelModelClass = oldModel.getModelClass();

				Method getRemoteModelMethod = oldModelClass.getMethod(
					"get" + oldModelModelClass.getSimpleName() + "RemoteModel");

				Object oldRemoteModel = getRemoteModelMethod.invoke(oldModel);

				BaseModel<?> newModel =
					(BaseModel<?>)translateOutputMethod.invoke(
						null, oldRemoteModel);

				return newModel;
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to translate " + oldModelClassName, t);
				}
			}
		}

		if (oldModelClassName.equals(
				"com.liferay.osb.patcher.model.impl.PatcherProjectVersionImpl")) {

			return translateOutputPatcherProjectVersion(oldModel);
		}
		else if (oldModelClassName.endsWith("Clp")) {
			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Method getClpSerializerClassMethod = oldModelClass.getMethod(
					"getClpSerializerClass");

				Class<?> oldClpSerializerClass =
					(Class<?>)getClpSerializerClassMethod.invoke(oldModel);

				Class<?> newClpSerializerClass = classLoader.loadClass(
					oldClpSerializerClass.getName());

				Method translateOutputMethod = newClpSerializerClass.getMethod(
					"translateOutput", BaseModel.class);

				Class<?> oldModelModelClass = oldModel.getModelClass();

				Method getRemoteModelMethod = oldModelClass.getMethod(
					"get" + oldModelModelClass.getSimpleName() + "RemoteModel");

				Object oldRemoteModel = getRemoteModelMethod.invoke(oldModel);

				BaseModel<?> newModel =
					(BaseModel<?>)translateOutputMethod.invoke(
						null, oldRemoteModel);

				return newModel;
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to translate " + oldModelClassName, t);
				}
			}
		}

		if (oldModelClassName.equals(
				"com.liferay.osb.patcher.model.impl.PatcherTicketHintImpl")) {

			return translateOutputPatcherTicketHint(oldModel);
		}
		else if (oldModelClassName.endsWith("Clp")) {
			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Method getClpSerializerClassMethod = oldModelClass.getMethod(
					"getClpSerializerClass");

				Class<?> oldClpSerializerClass =
					(Class<?>)getClpSerializerClassMethod.invoke(oldModel);

				Class<?> newClpSerializerClass = classLoader.loadClass(
					oldClpSerializerClass.getName());

				Method translateOutputMethod = newClpSerializerClass.getMethod(
					"translateOutput", BaseModel.class);

				Class<?> oldModelModelClass = oldModel.getModelClass();

				Method getRemoteModelMethod = oldModelClass.getMethod(
					"get" + oldModelModelClass.getSimpleName() + "RemoteModel");

				Object oldRemoteModel = getRemoteModelMethod.invoke(oldModel);

				BaseModel<?> newModel =
					(BaseModel<?>)translateOutputMethod.invoke(
						null, oldRemoteModel);

				return newModel;
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info("Unable to translate " + oldModelClassName, t);
				}
			}
		}

		return oldModel;
	}

	public static Object translateOutput(List<Object> oldList) {
		List<Object> newList = new ArrayList<>(oldList.size());

		for (Object curObj : oldList) {
			newList.add(translateOutput(curObj));
		}

		return newList;
	}

	public static Object translateOutput(Object obj) {
		if (obj instanceof BaseModel<?>) {
			return translateOutput((BaseModel<?>)obj);
		}
		else if (obj instanceof List<?>) {
			return translateOutput((List<Object>)obj);
		}

		return obj;
	}

	public static Object translateOutputPatcherAccount(BaseModel<?> oldModel) {
		PatcherAccountClp newModel = new PatcherAccountClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setPatcherAccountRemoteModel(oldModel);

		return newModel;
	}

	public static Object translateOutputPatcherBuild(BaseModel<?> oldModel) {
		PatcherBuildClp newModel = new PatcherBuildClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setPatcherBuildRemoteModel(oldModel);

		return newModel;
	}

	public static Object translateOutputPatcherBuildRel(BaseModel<?> oldModel) {
		PatcherBuildRelClp newModel = new PatcherBuildRelClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setPatcherBuildRelRemoteModel(oldModel);

		return newModel;
	}

	public static Object translateOutputPatcherFix(BaseModel<?> oldModel) {
		PatcherFixClp newModel = new PatcherFixClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setPatcherFixRemoteModel(oldModel);

		return newModel;
	}

	public static Object translateOutputPatcherFixComponent(
		BaseModel<?> oldModel) {

		PatcherFixComponentClp newModel = new PatcherFixComponentClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setPatcherFixComponentRemoteModel(oldModel);

		return newModel;
	}

	public static Object translateOutputPatcherFixPack(BaseModel<?> oldModel) {
		PatcherFixPackClp newModel = new PatcherFixPackClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setPatcherFixPackRemoteModel(oldModel);

		return newModel;
	}

	public static Object translateOutputPatcherFixRel(BaseModel<?> oldModel) {
		PatcherFixRelClp newModel = new PatcherFixRelClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setPatcherFixRelRemoteModel(oldModel);

		return newModel;
	}

	public static Object translateOutputPatcherProductVersion(
		BaseModel<?> oldModel) {

		PatcherProductVersionClp newModel = new PatcherProductVersionClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setPatcherProductVersionRemoteModel(oldModel);

		return newModel;
	}

	public static Object translateOutputPatcherProjectVersion(
		BaseModel<?> oldModel) {

		PatcherProjectVersionClp newModel = new PatcherProjectVersionClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setPatcherProjectVersionRemoteModel(oldModel);

		return newModel;
	}

	public static Object translateOutputPatcherTicketHint(
		BaseModel<?> oldModel) {

		PatcherTicketHintClp newModel = new PatcherTicketHintClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setPatcherTicketHintRemoteModel(oldModel);

		return newModel;
	}

	public static Throwable translateThrowable(Throwable throwable) {
		if (_useReflectionToTranslateThrowable) {
			try {
				UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
					new UnsyncByteArrayOutputStream();

				ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					unsyncByteArrayOutputStream);

				objectOutputStream.writeObject(throwable);

				objectOutputStream.flush();
				objectOutputStream.close();

				UnsyncByteArrayInputStream unsyncByteArrayInputStream =
					new UnsyncByteArrayInputStream(
						unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
						unsyncByteArrayOutputStream.size());

				Thread currentThread = Thread.currentThread();

				ClassLoader contextClassLoader =
					currentThread.getContextClassLoader();

				ObjectInputStream objectInputStream =
					new ClassLoaderObjectInputStream(
						unsyncByteArrayInputStream, contextClassLoader);

				throwable = (Throwable)objectInputStream.readObject();

				objectInputStream.close();

				return throwable;
			}
			catch (ClassNotFoundException cnfe) {
				if (_log.isInfoEnabled()) {
					_log.info("Do not use reflection to translate throwable");
				}

				_useReflectionToTranslateThrowable = false;
			}
			catch (SecurityException se) {
				if (_log.isInfoEnabled()) {
					_log.info("Do not use reflection to translate throwable");
				}

				_useReflectionToTranslateThrowable = false;
			}
			catch (Throwable throwable2) {
				_log.error(throwable2, throwable2);

				return throwable2;
			}
		}

		Class<?> clazz = throwable.getClass();

		String className = clazz.getName();

		if (className.equals(
				"com.liferay.osb.patcher.NoSuchPatcherAccountException")) {

			return new com.liferay.osb.patcher.NoSuchPatcherAccountException(
				throwable.getMessage(), throwable.getCause());
		}

		if (className.equals(
				"com.liferay.osb.patcher.NoSuchPatcherBuildException")) {

			return new com.liferay.osb.patcher.NoSuchPatcherBuildException(
				throwable.getMessage(), throwable.getCause());
		}

		if (className.equals(
				"com.liferay.osb.patcher.NoSuchPatcherBuildRelException")) {

			return new com.liferay.osb.patcher.NoSuchPatcherBuildRelException(
				throwable.getMessage(), throwable.getCause());
		}

		if (className.equals(
				"com.liferay.osb.patcher.NoSuchPatcherFixException")) {

			return new com.liferay.osb.patcher.NoSuchPatcherFixException(
				throwable.getMessage(), throwable.getCause());
		}

		if (className.equals(
				"com.liferay.osb.patcher.NoSuchPatcherFixComponentException")) {

			return new com.liferay.osb.patcher.
				NoSuchPatcherFixComponentException(
					throwable.getMessage(), throwable.getCause());
		}

		if (className.equals(
				"com.liferay.osb.patcher.NoSuchPatcherFixPackException")) {

			return new com.liferay.osb.patcher.NoSuchPatcherFixPackException(
				throwable.getMessage(), throwable.getCause());
		}

		if (className.equals(
				"com.liferay.osb.patcher.NoSuchPatcherFixRelException")) {

			return new com.liferay.osb.patcher.NoSuchPatcherFixRelException(
				throwable.getMessage(), throwable.getCause());
		}

		if (className.equals(
				"com.liferay.osb.patcher.NoSuchPatcherProductVersionException")) {

			return new com.liferay.osb.patcher.
				NoSuchPatcherProductVersionException(
					throwable.getMessage(), throwable.getCause());
		}

		if (className.equals(
				"com.liferay.osb.patcher.NoSuchPatcherProjectVersionException")) {

			return new com.liferay.osb.patcher.
				NoSuchPatcherProjectVersionException(
					throwable.getMessage(), throwable.getCause());
		}

		if (className.equals(
				"com.liferay.osb.patcher.NoSuchPatcherTicketHintException")) {

			return new com.liferay.osb.patcher.NoSuchPatcherTicketHintException(
				throwable.getMessage(), throwable.getCause());
		}

		return throwable;
	}

	private static final Log _log = LogFactoryUtil.getLog(ClpSerializer.class);

	private static String _servletContextName;
	private static boolean _useReflectionToTranslateThrowable = true;

}