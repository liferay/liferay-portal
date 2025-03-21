/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "The settings of a widget page.", value = "WidgetPageSettings"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WidgetPageSettings")
public class WidgetPageSettings extends PageSettings implements Serializable {

	public static WidgetPageSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(WidgetPageSettings.class, json);
	}

	public static WidgetPageSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(WidgetPageSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the widget page is customizable."
	)
	public Boolean getCustomizable() {
		if (_customizableSupplier != null) {
			customizable = _customizableSupplier.get();

			_customizableSupplier = null;
		}

		return customizable;
	}

	public void setCustomizable(Boolean customizable) {
		this.customizable = customizable;

		_customizableSupplier = null;
	}

	@JsonIgnore
	public void setCustomizable(
		UnsafeSupplier<Boolean, Exception> customizableUnsafeSupplier) {

		_customizableSupplier = () -> {
			try {
				return customizableUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A flag that indicates whether the widget page is customizable."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean customizable;

	@JsonIgnore
	private Supplier<Boolean> _customizableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The IDs of the customizable sections."
	)
	public String[] getCustomizableSectionIds() {
		if (_customizableSectionIdsSupplier != null) {
			customizableSectionIds = _customizableSectionIdsSupplier.get();

			_customizableSectionIdsSupplier = null;
		}

		return customizableSectionIds;
	}

	public void setCustomizableSectionIds(String[] customizableSectionIds) {
		this.customizableSectionIds = customizableSectionIds;

		_customizableSectionIdsSupplier = null;
	}

	@JsonIgnore
	public void setCustomizableSectionIds(
		UnsafeSupplier<String[], Exception>
			customizableSectionIdsUnsafeSupplier) {

		_customizableSectionIdsSupplier = () -> {
			try {
				return customizableSectionIdsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The IDs of the customizable sections.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] customizableSectionIds;

	@JsonIgnore
	private Supplier<String[]> _customizableSectionIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether this widget page will inherit changes made to the associated widget page template."
	)
	public Boolean getInheritChanges() {
		if (_inheritChangesSupplier != null) {
			inheritChanges = _inheritChangesSupplier.get();

			_inheritChangesSupplier = null;
		}

		return inheritChanges;
	}

	public void setInheritChanges(Boolean inheritChanges) {
		this.inheritChanges = inheritChanges;

		_inheritChangesSupplier = null;
	}

	@JsonIgnore
	public void setInheritChanges(
		UnsafeSupplier<Boolean, Exception> inheritChangesUnsafeSupplier) {

		_inheritChangesSupplier = () -> {
			try {
				return inheritChangesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Whether this widget page will inherit changes made to the associated widget page template."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean inheritChanges;

	@JsonIgnore
	private Supplier<Boolean> _inheritChangesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the layout template."
	)
	public String getLayoutTemplateId() {
		if (_layoutTemplateIdSupplier != null) {
			layoutTemplateId = _layoutTemplateIdSupplier.get();

			_layoutTemplateIdSupplier = null;
		}

		return layoutTemplateId;
	}

	public void setLayoutTemplateId(String layoutTemplateId) {
		this.layoutTemplateId = layoutTemplateId;

		_layoutTemplateIdSupplier = null;
	}

	@JsonIgnore
	public void setLayoutTemplateId(
		UnsafeSupplier<String, Exception> layoutTemplateIdUnsafeSupplier) {

		_layoutTemplateIdSupplier = () -> {
			try {
				return layoutTemplateIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The ID of the layout template.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String layoutTemplateId;

	@JsonIgnore
	private Supplier<String> _layoutTemplateIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ItemExternalReference getWidgetPageTemplateReference() {
		if (_widgetPageTemplateReferenceSupplier != null) {
			widgetPageTemplateReference =
				_widgetPageTemplateReferenceSupplier.get();

			_widgetPageTemplateReferenceSupplier = null;
		}

		return widgetPageTemplateReference;
	}

	public void setWidgetPageTemplateReference(
		ItemExternalReference widgetPageTemplateReference) {

		this.widgetPageTemplateReference = widgetPageTemplateReference;

		_widgetPageTemplateReferenceSupplier = null;
	}

	@JsonIgnore
	public void setWidgetPageTemplateReference(
		UnsafeSupplier<ItemExternalReference, Exception>
			widgetPageTemplateReferenceUnsafeSupplier) {

		_widgetPageTemplateReferenceSupplier = () -> {
			try {
				return widgetPageTemplateReferenceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ItemExternalReference widgetPageTemplateReference;

	@JsonIgnore
	private Supplier<ItemExternalReference>
		_widgetPageTemplateReferenceSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetPageSettings)) {
			return false;
		}

		WidgetPageSettings widgetPageSettings = (WidgetPageSettings)object;

		return Objects.equals(toString(), widgetPageSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean customizable = getCustomizable();

		if (customizable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customizable\": ");

			sb.append(customizable);
		}

		String[] customizableSectionIds = getCustomizableSectionIds();

		if (customizableSectionIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customizableSectionIds\": ");

			sb.append("[");

			for (int i = 0; i < customizableSectionIds.length; i++) {
				sb.append("\"");

				sb.append(_escape(customizableSectionIds[i]));

				sb.append("\"");

				if ((i + 1) < customizableSectionIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean inheritChanges = getInheritChanges();

		if (inheritChanges != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inheritChanges\": ");

			sb.append(inheritChanges);
		}

		String layoutTemplateId = getLayoutTemplateId();

		if (layoutTemplateId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"layoutTemplateId\": ");

			sb.append("\"");

			sb.append(_escape(layoutTemplateId));

			sb.append("\"");
		}

		ItemExternalReference widgetPageTemplateReference =
			getWidgetPageTemplateReference();

		if (widgetPageTemplateReference != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetPageTemplateReference\": ");

			sb.append(String.valueOf(widgetPageTemplateReference));
		}

		CustomMetaTag[] customMetaTags = getCustomMetaTags();

		if (customMetaTags != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customMetaTags\": ");

			sb.append("[");

			for (int i = 0; i < customMetaTags.length; i++) {
				sb.append(String.valueOf(customMetaTags[i]));

				if ((i + 1) < customMetaTags.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean hiddenFromNavigation = getHiddenFromNavigation();

		if (hiddenFromNavigation != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hiddenFromNavigation\": ");

			sb.append(hiddenFromNavigation);
		}

		NavigationMenuSettings navigationMenuSettings =
			getNavigationMenuSettings();

		if (navigationMenuSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuSettings\": ");

			sb.append(String.valueOf(navigationMenuSettings));
		}

		OpenGraphSettings openGraphSettings = getOpenGraphSettings();

		if (openGraphSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"openGraphSettings\": ");

			sb.append(String.valueOf(openGraphSettings));
		}

		SEOSettings seoSettings = getSeoSettings();

		if (seoSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seoSettings\": ");

			sb.append(String.valueOf(seoSettings));
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(type);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.WidgetPageSettings",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}