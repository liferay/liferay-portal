/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.json.transformer.CompanyJSONTransformer;
import com.liferay.portal.json.transformer.FileJSONTransformer;
import com.liferay.portal.json.transformer.JSONArrayJSONTransformer;
import com.liferay.portal.json.transformer.JSONObjectJSONTransformer;
import com.liferay.portal.json.transformer.JSONSerializableJSONTransformer;
import com.liferay.portal.json.transformer.RepositoryModelJSONTransformer;
import com.liferay.portal.json.transformer.UserJSONTransformer;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.json.JSONTransformer;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletDisplayModel;
import com.liferay.portal.kernel.repository.model.RepositoryModel;

import jakarta.portlet.PortletURL;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import jodd.introspector.CachingIntrospector;
import jodd.introspector.ClassIntrospector;

import jodd.json.JsonSerializer;
import jodd.json.TypeJsonSerializerMap;
import jodd.json.meta.JsonAnnotationManager;

/**
 * @author Igor Spasic
 */
public class JSONInit {

	public static synchronized void init() {
		try {
			if (_initalized) {
				return;
			}

			_registerDefaultTransformers();

			_initalized = true;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static void _registerDefaultTransformers() throws Exception {
		ClassIntrospector.Implementation.set(
			new CachingIntrospector(true, true, true, new String[] {"_"}));

		JsonAnnotationManager jsonAnnotationManager =
			JsonAnnotationManager.get();

		jsonAnnotationManager.setJsonAnnotation(JSON.class);

		JsonSerializer.Defaults.excludedTypes = new Class<?>[] {
			ExpandoBridge.class, InputStream.class, LiferayPortletRequest.class,
			LiferayPortletResponse.class, OutputStream.class,
			PortletDisplayModel.class, PortletURL.class
		};

		JsonSerializer.Defaults.excludedTypeNames = new String[] {"javax.*"};

		TypeJsonSerializerMap typeJsonSerializerMap =
			TypeJsonSerializerMap.get();

		Class<?>[][] classesArray = new Class<?>[][] {
			new Class<?>[] {Company.class, CompanyJSONTransformer.class},
			new Class<?>[] {File.class, FileJSONTransformer.class},
			new Class<?>[] {JSONArray.class, JSONArrayJSONTransformer.class},
			new Class<?>[] {JSONObject.class, JSONObjectJSONTransformer.class},
			new Class<?>[] {
				JSONSerializable.class, JSONSerializableJSONTransformer.class
			},
			new Class<?>[] {
				RepositoryModel.class, RepositoryModelJSONTransformer.class
			},
			new Class<?>[] {User.class, UserJSONTransformer.class}
		};

		for (Class<?>[] classes : classesArray) {
			typeJsonSerializerMap.register(
				classes[0],
				new JoddJsonTransformer(
					(JSONTransformer)classes[1].newInstance()));
		}
	}

	private static boolean _initalized;

}