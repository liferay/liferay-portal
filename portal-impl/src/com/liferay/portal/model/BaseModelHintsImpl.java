/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHints;
import com.liferay.portal.kernel.model.ModelHintsCallback;
import com.liferay.portal.kernel.model.ModelHintsConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Brian Wing Shun Chan
 * @author Tomas Polesovsky
 * @author Raymond Augé
 */
public abstract class BaseModelHintsImpl implements ModelHints {

	public BaseModelHintsImpl() {
		this(false);
	}

	public BaseModelHintsImpl(boolean productionMode) {
		_productionMode = productionMode;
	}

	public void afterPropertiesSet() {
		_hintCollections = new ConcurrentHashMap<>();
		_defaultHints = new ConcurrentHashMap<>();
		_fieldDataBagsMap = new ConcurrentHashMap<>();
		_models = new ConcurrentSkipListSet<>();

		try {
			Class<?> clazz = getClass();

			ClassLoader classLoader = clazz.getClassLoader();

			for (String config : getModelHintsConfigs()) {
				if (config.startsWith("classpath*:")) {
					String name = config.substring("classpath*:".length());

					Enumeration<URL> enumeration = classLoader.getResources(
						name);

					if (_log.isDebugEnabled() &&
						!enumeration.hasMoreElements()) {

						_log.debug("No resources found for " + name);
					}

					while (enumeration.hasMoreElements()) {
						URL url = enumeration.nextElement();

						if (_log.isDebugEnabled()) {
							_log.debug(
								StringBundler.concat(
									"Loading ", name, " from ", url));
						}

						try (InputStream inputStream = url.openStream()) {
							read(classLoader, url.toString(), inputStream);
						}
					}
				}
				else {
					InputStream inputStream = classLoader.getResourceAsStream(
						config);

					if (inputStream == null) {
						File file = new File(config);

						if (!file.exists()) {
							continue;
						}

						inputStream = new FileInputStream(file);
					}

					try (InputStream curInputStream = inputStream) {
						read(classLoader, config, curInputStream);
					}
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public String buildCustomValidatorName(String validatorName) {
		return StringBundler.concat(
			validatorName, StringPool.UNDERLINE, StringUtil.randomId());
	}

	@Override
	public Map<String, String> getDefaultHints(String model) {
		return _defaultHints.get(model);
	}

	@Override
	public Object getFieldsElement(String model, String field) {
		Map<String, FieldDataBag> fieldDataBags = _fieldDataBagsMap.get(model);

		if (fieldDataBags == null) {
			return null;
		}

		FieldDataBag fieldDataBag = fieldDataBags.get(field);

		if (fieldDataBag == null) {
			return null;
		}

		return fieldDataBag._element;
	}

	@Override
	public Map<String, String> getHints(String model, String field) {
		Map<String, FieldDataBag> fieldDataBags = _fieldDataBagsMap.get(model);

		if (fieldDataBags == null) {
			return null;
		}

		FieldDataBag fieldDataBag = fieldDataBags.get(field);

		if (fieldDataBag == null) {
			return null;
		}

		return fieldDataBag._hints;
	}

	@Override
	public int getMaxLength(String model, String field) {
		Map<String, String> hints = getHints(model, field);

		if (hints == null) {
			return Integer.MAX_VALUE;
		}

		int maxLength = GetterUtil.getInteger(
			ModelHintsConstants.TEXT_MAX_LENGTH);

		return GetterUtil.getInteger(hints.get("max-length"), maxLength);
	}

	public abstract ModelHintsCallback getModelHintsCallback();

	public abstract String[] getModelHintsConfigs();

	@Override
	public List<String> getModels() {
		return ListUtil.fromCollection(_models);
	}

	public abstract SAXReader getSAXReader();

	@Override
	public Tuple getSanitizeTuple(String model, String field) {
		Map<String, FieldDataBag> fieldDataBags = _fieldDataBagsMap.get(model);

		if (fieldDataBags == null) {
			return null;
		}

		FieldDataBag fieldDataBag = fieldDataBags.get(field);

		if (fieldDataBag == null) {
			return null;
		}

		return fieldDataBag._sanitize;
	}

	@Override
	public List<Tuple> getSanitizeTuples(String model) {
		Map<String, FieldDataBag> fieldDataBags = _fieldDataBagsMap.get(model);

		if (fieldDataBags == null) {
			return Collections.emptyList();
		}

		List<Tuple> sanitizeTuples = new ArrayList<>();

		for (FieldDataBag fieldDataBag : fieldDataBags.values()) {
			Tuple tuple = fieldDataBag._sanitize;

			if (tuple != null) {
				sanitizeTuples.add(tuple);
			}
		}

		return sanitizeTuples;
	}

	@Override
	public String getType(String model, String field) {
		Map<String, FieldDataBag> fieldDataBags = _fieldDataBagsMap.get(model);

		if (fieldDataBags == null) {
			return null;
		}

		FieldDataBag fieldDataBag = fieldDataBags.get(field);

		if (fieldDataBag == null) {
			return null;
		}

		return fieldDataBag._type;
	}

	@Override
	public List<Tuple> getValidators(String model, String field) {
		Map<String, FieldDataBag> fieldDataBags = _fieldDataBagsMap.get(model);

		if (fieldDataBags == null) {
			return null;
		}

		FieldDataBag fieldDataBag = fieldDataBags.get(field);

		if (fieldDataBag == null) {
			return null;
		}

		return fieldDataBag._validators;
	}

	@Override
	public String getValue(
		String model, String field, String name, String defaultValue) {

		Map<String, String> hints = getHints(model, field);

		if (hints == null) {
			return defaultValue;
		}

		return GetterUtil.getString(hints.get(name), defaultValue);
	}

	@Override
	public boolean hasField(String model, String field) {
		Map<String, FieldDataBag> fieldDataBags = _fieldDataBagsMap.get(model);

		if (fieldDataBags == null) {
			return false;
		}

		FieldDataBag fieldDataBag = fieldDataBags.get(field);

		if (fieldDataBag == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isCustomValidator(String validatorName) {
		return validatorName.equals("custom");
	}

	@Override
	public boolean isLocalized(String model, String field) {
		Map<String, FieldDataBag> fieldDataBags = _fieldDataBagsMap.get(model);

		if (fieldDataBags == null) {
			return false;
		}

		FieldDataBag fieldDataBag = fieldDataBags.get(field);

		if (fieldDataBag == null) {
			return false;
		}

		return fieldDataBag._localized;
	}

	@Override
	public void read(ClassLoader classLoader, InputStream inputStream)
		throws Exception {

		read(classLoader, null, inputStream);
	}

	@Override
	public void read(ClassLoader classLoader, String source) throws Exception {
		read(classLoader, source, classLoader.getResourceAsStream(source));
	}

	public void read(
			ClassLoader classLoader, String source, InputStream inputStream)
		throws Exception {

		if (inputStream == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot load " + source);
			}

			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Loading " + source);
		}

		SAXReader saxReader = getSAXReader();

		Document document = saxReader.read(inputStream);

		Element rootElement = document.getRootElement();

		List<Element> rootElements = rootElement.elements("hint-collection");

		for (Element hintCollectionElement : rootElements) {
			String name = hintCollectionElement.attributeValue("name");

			Map<String, String> hints = _hintCollections.get(name);

			if (hints == null) {
				hints = new HashMap<>();

				_hintCollections.put(name, hints);
			}

			List<Element> hintElements = hintCollectionElement.elements("hint");

			for (Element hintElement : hintElements) {
				String hintName = hintElement.attributeValue("name");
				String hintValue = hintElement.getText();

				hints.put(hintName, hintValue);
			}
		}

		rootElements = rootElement.elements("model");

		for (Element modelElement : rootElements) {
			String name = modelElement.attributeValue("name");

			ModelHintsCallback modelHintsCallback = getModelHintsCallback();

			modelHintsCallback.execute(classLoader, name);

			Map<String, String> defaultHints = new HashMap<>();

			if (!_productionMode) {
				_defaultHints.put(name, defaultHints);

				Element defaultHintsElement = modelElement.element(
					"default-hints");

				if (defaultHintsElement != null) {
					List<Element> hintElements = defaultHintsElement.elements(
						"hint");

					for (Element hintElement : hintElements) {
						String hintName = hintElement.attributeValue("name");
						String hintValue = hintElement.getText();

						defaultHints.put(hintName, hintValue);
					}
				}
			}

			Map<String, FieldDataBag> fieldDataBags = _fieldDataBagsMap.get(
				name);

			if (fieldDataBags == null) {
				fieldDataBags = new HashMap<>();

				_fieldDataBagsMap.put(name, fieldDataBags);
			}

			_models.add(name);

			List<Element> modelElements = modelElement.elements("field");

			for (Element fieldElement : modelElements) {
				String fieldName = fieldElement.attributeValue("name");

				FieldDataBag fieldDataBag = new FieldDataBag();

				fieldDataBags.put(fieldName, fieldDataBag);

				String fieldType = fieldElement.attributeValue("type");
				boolean fieldLocalized = GetterUtil.getBoolean(
					fieldElement.attributeValue("localized"));

				Map<String, String> fieldHints = HashMapBuilder.putAll(
					defaultHints
				).build();

				List<Element> fieldElements = fieldElement.elements(
					"hint-collection");

				for (Element hintCollectionElement : fieldElements) {
					Map<String, String> hints = _hintCollections.get(
						hintCollectionElement.attributeValue("name"));

					fieldHints.putAll(hints);
				}

				fieldElements = fieldElement.elements("hint");

				for (Element hintElement : fieldElements) {
					String hintName = hintElement.attributeValue("name");
					String hintValue = hintElement.getText();

					fieldHints.put(hintName, hintValue);
				}

				Tuple fieldSanitize = null;

				Element sanitizeElement = fieldElement.element("sanitize");

				if (sanitizeElement != null) {
					String contentType = sanitizeElement.attributeValue(
						"content-type");
					String modes = sanitizeElement.attributeValue("modes");

					fieldSanitize = new Tuple(fieldName, contentType, modes);
				}

				Map<String, Tuple> fieldValidators = new TreeMap<>();

				fieldElements = fieldElement.elements("validator");

				for (Element validatorElement : fieldElements) {
					String validatorName = validatorElement.attributeValue(
						"name");

					if (Validator.isNull(validatorName)) {
						continue;
					}

					String validatorErrorMessage = GetterUtil.getString(
						validatorElement.attributeValue("error-message"));
					String validatorValue = GetterUtil.getString(
						validatorElement.getText());

					boolean customValidator = isCustomValidator(validatorName);

					if (customValidator) {
						validatorName = buildCustomValidatorName(validatorName);
					}

					Tuple fieldValidator = new Tuple(
						fieldName, validatorName, validatorErrorMessage,
						validatorValue, customValidator);

					fieldValidators.put(validatorName, fieldValidator);
				}

				if (_productionMode) {
					fieldElement = null;
				}

				fieldDataBag._element = fieldElement;
				fieldDataBag._localized = fieldLocalized;
				fieldDataBag._type = fieldType;

				if (fieldHints.isEmpty()) {
					fieldHints = Collections.emptyMap();
				}
				else if (fieldHints.size() == 1) {
					Set<Map.Entry<String, String>> set = fieldHints.entrySet();

					Iterator<Map.Entry<String, String>> iterator =
						set.iterator();

					Map.Entry<String, String> entry = iterator.next();

					fieldHints = Collections.singletonMap(
						entry.getKey(), entry.getValue());
				}

				fieldDataBag._hints = fieldHints;

				if (fieldSanitize != null) {
					fieldDataBag._sanitize = fieldSanitize;
				}

				if (!fieldValidators.isEmpty()) {
					fieldDataBag._validators = ListUtil.fromMapValues(
						fieldValidators);
				}
			}
		}
	}

	@Override
	public String trimString(String model, String field, String value) {
		if (value == null) {
			return value;
		}

		int maxLength = getMaxLength(model, field);

		if (value.length() > maxLength) {
			return value.substring(0, maxLength);
		}

		return value;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseModelHintsImpl.class);

	private Map<String, Map<String, String>> _defaultHints;
	private Map<String, Map<String, FieldDataBag>> _fieldDataBagsMap;
	private Map<String, Map<String, String>> _hintCollections;
	private Set<String> _models;
	private final boolean _productionMode;

	private static class FieldDataBag {

		private Element _element;
		private Map<String, String> _hints;
		private boolean _localized;
		private Tuple _sanitize;
		private String _type;
		private List<Tuple> _validators;

	}

}