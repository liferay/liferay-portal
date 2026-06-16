/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.client.dto.v1_0;

import com.liferay.seo.studio.rest.client.function.UnsafeSupplier;
import com.liferay.seo.studio.rest.client.serdes.v1_0.CrawlHitSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Brooke Dalton
 * @generated
 */
@Generated("")
public class CrawlHit implements Cloneable, Serializable {

	public static CrawlHit toDTO(String json) {
		return CrawlHitSerDes.toDTO(json);
	}

	public String getCanonicalUrl() {
		return canonicalUrl;
	}

	public void setCanonicalUrl(String canonicalUrl) {
		this.canonicalUrl = canonicalUrl;
	}

	public void setCanonicalUrl(
		UnsafeSupplier<String, Exception> canonicalUrlUnsafeSupplier) {

		try {
			canonicalUrl = canonicalUrlUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String canonicalUrl;

	public String[] getLinks() {
		return links;
	}

	public void setLinks(String[] links) {
		this.links = links;
	}

	public void setLinks(
		UnsafeSupplier<String[], Exception> linksUnsafeSupplier) {

		try {
			links = linksUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] links;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String title;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUrl(UnsafeSupplier<String, Exception> urlUnsafeSupplier) {
		try {
			url = urlUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String url;

	@Override
	public CrawlHit clone() throws CloneNotSupportedException {
		return (CrawlHit)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CrawlHit)) {
			return false;
		}

		CrawlHit crawlHit = (CrawlHit)object;

		return Objects.equals(toString(), crawlHit.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CrawlHitSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-517948860