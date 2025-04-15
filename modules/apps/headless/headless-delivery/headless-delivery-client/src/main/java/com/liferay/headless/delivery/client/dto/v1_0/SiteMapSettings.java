/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.SiteMapSettingsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class SiteMapSettings implements Cloneable, Serializable {

	public static SiteMapSettings toDTO(String json) {
		return SiteMapSettingsSerDes.toDTO(json);
	}

	public ChangeFrequency getChangeFrequency() {
		return changeFrequency;
	}

	public String getChangeFrequencyAsString() {
		if (changeFrequency == null) {
			return null;
		}

		return changeFrequency.toString();
	}

	public void setChangeFrequency(ChangeFrequency changeFrequency) {
		this.changeFrequency = changeFrequency;
	}

	public void setChangeFrequency(
		UnsafeSupplier<ChangeFrequency, Exception>
			changeFrequencyUnsafeSupplier) {

		try {
			changeFrequency = changeFrequencyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ChangeFrequency changeFrequency;

	public Boolean getInclude() {
		return include;
	}

	public void setInclude(Boolean include) {
		this.include = include;
	}

	public void setInclude(
		UnsafeSupplier<Boolean, Exception> includeUnsafeSupplier) {

		try {
			include = includeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean include;

	public Boolean getIncludeChildSitePages() {
		return includeChildSitePages;
	}

	public void setIncludeChildSitePages(Boolean includeChildSitePages) {
		this.includeChildSitePages = includeChildSitePages;
	}

	public void setIncludeChildSitePages(
		UnsafeSupplier<Boolean, Exception>
			includeChildSitePagesUnsafeSupplier) {

		try {
			includeChildSitePages = includeChildSitePagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean includeChildSitePages;

	public Double getPagePriority() {
		return pagePriority;
	}

	public void setPagePriority(Double pagePriority) {
		this.pagePriority = pagePriority;
	}

	public void setPagePriority(
		UnsafeSupplier<Double, Exception> pagePriorityUnsafeSupplier) {

		try {
			pagePriority = pagePriorityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double pagePriority;

	@Override
	public SiteMapSettings clone() throws CloneNotSupportedException {
		return (SiteMapSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SiteMapSettings)) {
			return false;
		}

		SiteMapSettings siteMapSettings = (SiteMapSettings)object;

		return Objects.equals(toString(), siteMapSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SiteMapSettingsSerDes.toJSON(this);
	}

	public static enum ChangeFrequency {

		ALWAYS("Always"), HOURLY("Hourly"), DAILY("Daily"), WEEKLY("Weekly"),
		MONTHLY("Monthly"), YEARLY("Yearly"), NEVER("Never");

		public static ChangeFrequency create(String value) {
			for (ChangeFrequency changeFrequency : values()) {
				if (Objects.equals(changeFrequency.getValue(), value) ||
					Objects.equals(changeFrequency.name(), value)) {

					return changeFrequency;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private ChangeFrequency(String value) {
			_value = value;
		}

		private final String _value;

	}

}