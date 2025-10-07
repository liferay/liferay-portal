/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model;

import com.liferay.portal.kernel.model.ModelHintsCallback;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProvider;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.xml.SAXReaderFactory;

import org.dom4j.io.SAXReader;

/**
 * @author Raymond Augé
 */
public class DefaultModelHintsImpl extends BaseModelHintsImpl {

	public DefaultModelHintsImpl() {
		super(true);
	}

	@Override
	public ModelHintsCallback getModelHintsCallback() {
		return _modelHintsCallback;
	}

	@Override
	public String[] getModelHintsConfigs() {
		return _MODEL_HINTS_CONFIGS;
	}

	@Override
	public SAXReader getSAXReader() {
		SecureXMLFactoryProvider secureXMLFactoryProvider =
			SecureXMLFactoryProviderUtil.getSecureXMLFactoryProvider();

		return SAXReaderFactory.getSAXReader(
			secureXMLFactoryProvider.newXMLReader(),
			PropsValues.XML_VALIDATION_ENABLED, false);
	}

	public class RuntimeModelHintsCallback implements ModelHintsCallback {

		@Override
		public void execute(ClassLoader classLoader, String name) {
			if (classLoader != BaseModelHintsImpl.class.getClassLoader()) {
				ClassNameLocalServiceUtil.getClassName(name);
			}
		}

	}

	private static final String[] _MODEL_HINTS_CONFIGS = StringUtil.split(
		PropsUtil.get(PropsKeys.MODEL_HINTS_CONFIGS));

	private final ModelHintsCallback _modelHintsCallback =
		new RuntimeModelHintsCallback();

}