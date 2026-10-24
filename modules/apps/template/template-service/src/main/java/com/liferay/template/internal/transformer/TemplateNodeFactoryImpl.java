/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.internal.transformer;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.templateparser.TemplateNode;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.template.info.field.transformer.BaseTemplateNodeTransformer;
import com.liferay.template.info.field.transformer.TemplateNodeTransformer;
import com.liferay.template.transformer.TemplateNodeFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = TemplateNodeFactory.class)
public class TemplateNodeFactoryImpl implements TemplateNodeFactory {

	@Override
	public TemplateNode createTemplateNode(
		InfoFieldValue<Object> infoFieldValue, ThemeDisplay themeDisplay) {

		InfoField<?> infoField = infoFieldValue.getInfoField();

		InfoFieldType infoFieldType = infoField.getInfoFieldType();

		Class<?> infoFieldTypeClass = infoFieldType.getClass();

		TemplateNodeTransformer templateNodeTransformer =
			_getTemplateNodeTransformer(infoFieldTypeClass.getName());

		return templateNodeTransformer.transform(infoFieldValue, themeDisplay);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, TemplateNodeTransformer.class,
			"info.field.type.class.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private TemplateNodeTransformer _getTemplateNodeTransformer(
		String className) {

		TemplateNodeTransformer templateNodeTransformer =
			_serviceTrackerMap.getService(className);

		if (templateNodeTransformer != null) {
			return templateNodeTransformer;
		}

		return _defaultTemplateNodeTransformer;
	}

	private static final TemplateNodeTransformer
		_defaultTemplateNodeTransformer = new BaseTemplateNodeTransformer() {

			@Override
			public TemplateNode transform(
				InfoFieldValue<Object> infoFieldValue,
				ThemeDisplay themeDisplay) {

				return getDefaultTemplateNode(infoFieldValue, themeDisplay);
			}

		};

	private ServiceTrackerMap<String, TemplateNodeTransformer>
		_serviceTrackerMap;

}