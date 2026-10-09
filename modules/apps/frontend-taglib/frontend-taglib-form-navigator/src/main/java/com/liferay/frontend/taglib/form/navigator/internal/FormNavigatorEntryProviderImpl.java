/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.form.navigator.internal;

import com.liferay.frontend.taglib.form.navigator.FormNavigatorCategoryProvider;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntryProvider;
import com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorContextConstants;
import com.liferay.frontend.taglib.form.navigator.context.FormNavigatorContextProvider;
import com.liferay.frontend.taglib.form.navigator.internal.configuration.FormNavigatorEntryConfigurationRetriever;
import com.liferay.frontend.taglib.form.navigator.internal.servlet.taglib.ui.WrapperFormNavigatorEntry;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
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
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Sergio González
 */
@Component(service = FormNavigatorEntryProvider.class)
public class FormNavigatorEntryProviderImpl
	implements FormNavigatorEntryProvider {

	public <T> List<FormNavigatorEntry<T>> getFormNavigatorEntries(
		String formNavigatorId, String categoryKey, T formModelBean) {

		String context = _getContext(formNavigatorId, formModelBean);

		List<String> formNavigatorEntryKeys =
			_formNavigatorEntryConfigurationRetriever.getFormNavigatorEntryKeys(
				formNavigatorId, categoryKey, context);

		return _getFormNavigatorEntries(
			formNavigatorId, formNavigatorEntryKeys);
	}

	@Override
	public <T> List<FormNavigatorEntry<T>> getFormNavigatorEntries(
		String formNavigatorId, String categoryKey, User user,
		T formModelBean) {

		List<FormNavigatorEntry<T>> formNavigatorEntries =
			_getFormNavigatorEntries(
				formNavigatorId, categoryKey, formModelBean);

		if (ListUtil.isEmpty(formNavigatorEntries)) {
			return Collections.emptyList();
		}

		return _filterVisibleFormNavigatorEntries(
			formNavigatorEntries, user, formModelBean);
	}

	@Override
	public <T> List<FormNavigatorEntry<T>> getFormNavigatorEntries(
		String formNavigatorId, User user, T formModelBean) {

		List<FormNavigatorEntry<T>> formNavigatorEntries = new ArrayList<>();

		String[] categoryKeys = _formNavigatorCategoryProvider.getKeys(
			formNavigatorId);

		for (String categoryKey : categoryKeys) {
			List<FormNavigatorEntry<T>> curFormNavigatorEntries =
				_getFormNavigatorEntries(
					formNavigatorId, categoryKey, formModelBean);

			if (curFormNavigatorEntries == null) {
				continue;
			}

			formNavigatorEntries.addAll(curFormNavigatorEntries);
		}

		return _filterVisibleFormNavigatorEntries(
			formNavigatorEntries, user, formModelBean);
	}

	@Override
	public <T> String[] getKeys(
		String formNavigatorId, String categoryKey, User user,
		T formModelBean) {

		List<String> keys = new ArrayList<>();

		List<FormNavigatorEntry<T>> formNavigatorEntries =
			getFormNavigatorEntries(
				formNavigatorId, categoryKey, user, formModelBean);

		for (FormNavigatorEntry<T> formNavigatorEntry : formNavigatorEntries) {
			String key = formNavigatorEntry.getKey();

			if (Validator.isNull(key)) {
				continue;
			}

			keys.add(key);
		}

		return keys.toArray(new String[0]);
	}

	@Override
	public <T> String[] getLabels(
		String formNavigatorId, String categoryKey, User user, T formModelBean,
		Locale locale) {

		List<String> labels = new ArrayList<>();

		List<FormNavigatorEntry<T>> formNavigatorEntries =
			getFormNavigatorEntries(
				formNavigatorId, categoryKey, user, formModelBean);

		for (FormNavigatorEntry<T> formNavigatorEntry : formNavigatorEntries) {
			String label = formNavigatorEntry.getLabel(locale);

			if (Validator.isNull(label)) {
				continue;
			}

			labels.add(label);
		}

		return labels.toArray(new String[0]);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_formNavigatorContextProviderMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext,
				(Class<FormNavigatorContextProvider<?>>)
					(Class<?>)FormNavigatorContextProvider.class,
				FormNavigatorContextConstants.FORM_NAVIGATOR_ID);
		_formNavigatorEntries = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, FormNavigatorEntry.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(formNavigatorEntry, emitter) -> emitter.emit(
					_getKey(
						formNavigatorEntry.getFormNavigatorId(),
						formNavigatorEntry.getCategoryKey()))),
			new PropertyServiceReferenceComparator<>(
				"form.navigator.entry.order"));
		_serviceTracker = ServiceTrackerFactory.openWrapperServiceRegistrator(
			bundleContext,
			(Class
				<com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorEntry
					<?>>)
						(Class<?>)
							com.liferay.portal.kernel.servlet.taglib.ui.
								FormNavigatorEntry.class,
			FormNavigatorEntry.class, WrapperFormNavigatorEntry::new,
			"form.navigator.entry.order");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();

		_formNavigatorContextProviderMap.close();
	}

	private <T> List<FormNavigatorEntry<T>> _filterVisibleFormNavigatorEntries(
		List<FormNavigatorEntry<T>> formNavigatorEntries, User user,
		T formModelBean) {

		return TransformUtil.transform(
			formNavigatorEntries,
			formNavigatorEntry -> {
				if (formNavigatorEntry.isVisible(user, formModelBean)) {
					return formNavigatorEntry;
				}

				return null;
			});
	}

	private <T> List<FormNavigatorEntry<T>>
		_getConfigurationFormNavigatorEntries(
			String formNavigatorId, String categoryKey, T formModelBean) {

		return getFormNavigatorEntries(
			formNavigatorId, categoryKey, formModelBean);
	}

	private <T> String _getContext(String formNavigatorId, T formModelBean) {
		FormNavigatorContextProvider<T> formNavigatorContextProvider =
			(FormNavigatorContextProvider<T>)
				_formNavigatorContextProviderMap.getService(formNavigatorId);

		if (formNavigatorContextProvider != null) {
			return formNavigatorContextProvider.getContext(formModelBean);
		}

		if (formModelBean == null) {
			return FormNavigatorContextConstants.CONTEXT_ADD;
		}

		return FormNavigatorContextConstants.CONTEXT_UPDATE;
	}

	private <T> List<FormNavigatorEntry<T>> _getFormNavigatorEntries(
		String formNavigatorId, List<String> formNavigatorEntryKeys) {

		if (formNavigatorEntryKeys == null) {
			return null;
		}

		return TransformUtil.transform(
			formNavigatorEntryKeys,
			formNavigatorEntryKey -> _getFormNavigatorEntry(
				formNavigatorEntryKey, formNavigatorId));
	}

	private <T> List<FormNavigatorEntry<T>> _getFormNavigatorEntries(
		String formNavigatorId, String categoryKey, T formModelBean) {

		List<FormNavigatorEntry<T>> formNavigatorEntries =
			_getConfigurationFormNavigatorEntries(
				formNavigatorId, categoryKey, formModelBean);

		if (formNavigatorEntries != null) {
			return formNavigatorEntries;
		}

		return (List)_formNavigatorEntries.getService(
			_getKey(formNavigatorId, categoryKey));
	}

	private <T> FormNavigatorEntry<T> _getFormNavigatorEntry(
		String key, String formNavigatorId) {

		FormNavigatorEntry<T> formNavigatorEntry =
			(FormNavigatorEntry<T>)_formNavigatorEntries.getService(
				_getKey(key, formNavigatorId));

		if ((formNavigatorEntry == null) && _log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"There is no form navigator entry for the form ",
					formNavigatorId, " with key", key));
		}

		return formNavigatorEntry;
	}

	private String _getKey(String formNavigatorId, String categoryKey) {
		return formNavigatorId + StringPool.PERIOD + categoryKey;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FormNavigatorEntryProviderImpl.class);

	@Reference
	private FormNavigatorCategoryProvider _formNavigatorCategoryProvider;

	private ServiceTrackerMap<String, FormNavigatorContextProvider<?>>
		_formNavigatorContextProviderMap;
	private ServiceTrackerMap<String, List<FormNavigatorEntry>>
		_formNavigatorEntries;

	@Reference
	private FormNavigatorEntryConfigurationRetriever
		_formNavigatorEntryConfigurationRetriever;

	private ServiceTracker
		<com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorEntry<?>, ?>
			_serviceTracker;

}