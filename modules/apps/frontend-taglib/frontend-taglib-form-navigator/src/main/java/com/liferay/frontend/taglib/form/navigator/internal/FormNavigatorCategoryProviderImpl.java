/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.form.navigator.internal;

import com.liferay.frontend.taglib.form.navigator.FormNavigatorCategory;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorCategoryProvider;
import com.liferay.frontend.taglib.form.navigator.internal.servlet.taglib.ui.WrapperFormNavigatorCategory;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Sergio González
 */
@Component(service = FormNavigatorCategoryProvider.class)
public class FormNavigatorCategoryProviderImpl
	implements FormNavigatorCategoryProvider {

	@Override
	public List<FormNavigatorCategory> getFormNavigatorCategories(
		String formNavigatorId) {

		List<FormNavigatorCategory> formNavigatorCategories =
			_serviceTrackerMap.getService(formNavigatorId);

		if (formNavigatorCategories != null) {
			return formNavigatorCategories;
		}

		return Collections.emptyList();
	}

	@Override
	public String[] getKeys(String formNavigatorId) {
		List<FormNavigatorCategory> formNavigatorCategories =
			getFormNavigatorCategories(formNavigatorId);

		if (ListUtil.isEmpty(formNavigatorCategories)) {
			return new String[] {""};
		}

		List<String> keys = new ArrayList<>();

		for (FormNavigatorCategory formNavigatorCategory :
				formNavigatorCategories) {

			String key = formNavigatorCategory.getKey();

			if (Validator.isNull(key)) {
				continue;
			}

			keys.add(key);
		}

		return keys.toArray(new String[0]);
	}

	@Override
	public String[] getLabels(String formNavigatorId, Locale locale) {
		List<FormNavigatorCategory> formNavigatorCategories =
			getFormNavigatorCategories(formNavigatorId);

		if (ListUtil.isEmpty(formNavigatorCategories)) {
			return new String[] {""};
		}

		List<String> labels = new ArrayList<>();

		for (FormNavigatorCategory formNavigatorCategory :
				formNavigatorCategories) {

			String label = formNavigatorCategory.getLabel(locale);

			if (Validator.isNull(label)) {
				continue;
			}

			labels.add(label);
		}

		return labels.toArray(new String[0]);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, FormNavigatorCategory.class, null,
			ServiceReferenceMapperFactory.createFromFunction(
				bundleContext, FormNavigatorCategory::getFormNavigatorId),
			new PropertyServiceReferenceComparator<>(
				"form.navigator.category.order"));

		_serviceTracker = ServiceTrackerFactory.openWrapperServiceRegistrator(
			bundleContext,
			com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorCategory.
				class,
			FormNavigatorCategory.class, WrapperFormNavigatorCategory::new,
			"form.navigator.category.order");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();

		_serviceTrackerMap.close();
	}

	private ServiceTracker
		<com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorCategory, ?>
			_serviceTracker;
	private ServiceTrackerMap<String, List<FormNavigatorCategory>>
		_serviceTrackerMap;

}