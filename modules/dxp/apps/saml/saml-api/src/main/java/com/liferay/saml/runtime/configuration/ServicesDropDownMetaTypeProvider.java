/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.runtime.configuration;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.saml.runtime.credential.KeyStoreManager;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import java.util.Locale;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Carlos Sierra Andrés
 */
public class ServicesDropDownMetaTypeProvider
	implements Closeable, MetaTypeProvider {

	public ServicesDropDownMetaTypeProvider(
		BundleContext bundleContext, String className, String metatypePID,
		String attributeID) {

		this(
			bundleContext, className, metatypePID, null, null, attributeID,
			null, null);
	}

	public ServicesDropDownMetaTypeProvider(
		BundleContext bundleContext, String className, String metatypePID,
		String metatypeName, String metatypeDescription, String attributeID,
		String attributeName, String attributeDescription) {

		this(
			bundleContext, className, metatypePID, metatypeName,
			metatypeDescription, attributeID, attributeName,
			attributeDescription, s -> s.getProperty("component.name"),
			s -> "(component.name=" + s.getProperty("component.name") + ")");
	}

	public ServicesDropDownMetaTypeProvider(
		BundleContext bundleContext, String className, String metatypePID,
		String metatypeName, String metatypeDescription, String attributeID,
		String attributeName, String attributeDescription,
		Function<ServiceReference<?>, Object> labelFunction,
		Function<ServiceReference<?>, String> valuesFunction) {

		_metatypePID = metatypePID;
		_metatypeName = metatypeName;
		_metatypeDescription = metatypeDescription;
		_attributeID = attributeID;
		_attributeName = attributeName;
		_attributeDescription = attributeDescription;
		_labelFunction = labelFunction;
		_valuesFunction = valuesFunction;

		try {
			Filter filter = bundleContext.createFilter(
				"(&(component.name=*)(objectClass=" + className + "))");

			_serviceTracker = new ServiceTracker<>(bundleContext, filter, null);

			_serviceTracker.open();
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new IllegalArgumentException(
				className + " is an invalid class name",
				invalidSyntaxException);
		}
	}

	@Override
	public void close() throws IOException {
		if (_serviceTracker != null) {
			_serviceTracker.close();
		}
	}

	@Override
	public String[] getLocales() {
		return TransformUtil.transformToArray(
			LanguageUtil.getAvailableLocales(), Locale::toLanguageTag,
			String.class);
	}

	public String getMetatypePID() {
		return _metatypePID;
	}

	@Override
	public ObjectClassDefinition getObjectClassDefinition(
		String id, String locale) {

		return new ObjectClassDefinition() {

			@Override
			public AttributeDefinition[] getAttributeDefinitions(int filter) {
				if ((filter == OPTIONAL) || (filter == ALL)) {
					return new AttributeDefinition[] {
						new AttributeDefinition() {

							@Override
							public int getCardinality() {
								return 0;
							}

							@Override
							public String[] getDefaultValue() {
								return new String[0];
							}

							@Override
							public String getDescription() {
								return _attributeDescription;
							}

							@Override
							public String getID() {
								return _attributeID;
							}

							@Override
							public String getName() {
								return _attributeName;
							}

							@Override
							public String[] getOptionLabels() {
								return (String[])TransformUtil.transform(
									_getServiceReferences(),
									serviceReference -> _labelFunction.apply(
										serviceReference),
									String.class);
							}

							@Override
							public String[] getOptionValues() {
								return TransformUtil.transform(
									_getServiceReferences(),
									serviceReference -> _valuesFunction.apply(
										serviceReference),
									String.class);
							}

							@Override
							public int getType() {
								return STRING;
							}

							@Override
							public String validate(String value) {
								return null;
							}

						}
					};
				}

				return new AttributeDefinition[0];
			}

			@Override
			public String getDescription() {
				return _metatypeDescription;
			}

			@Override
			public String getID() {
				return _metatypePID;
			}

			@Override
			public InputStream getIcon(int size) throws IOException {
				return null;
			}

			@Override
			public String getName() {
				return _metatypeName;
			}

		};
	}

	private ServiceReference<KeyStoreManager>[] _getServiceReferences() {
		ServiceReference<KeyStoreManager>[] serviceReferences =
			_serviceTracker.getServiceReferences();

		if (serviceReferences == null) {
			return null;
		}

		return serviceReferences;
	}

	private String _attributeDescription;
	private String _attributeID;
	private String _attributeName;
	private Function<ServiceReference<?>, Object> _labelFunction;
	private String _metatypeDescription;
	private String _metatypeName;
	private String _metatypePID;
	private final ServiceTracker<KeyStoreManager, KeyStoreManager>
		_serviceTracker;
	private Function<ServiceReference<?>, String> _valuesFunction;

}