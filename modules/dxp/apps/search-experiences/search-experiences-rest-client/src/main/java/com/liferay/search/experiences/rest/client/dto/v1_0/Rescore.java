/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.RescoreSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class Rescore implements Cloneable, Serializable {

	public static Rescore toDTO(String json) {
		return RescoreSerDes.toDTO(json);
	}

	public Object getQuery() {
		return query;
	}

	public void setQuery(Object query) {
		this.query = query;
	}

	public void setQuery(
		UnsafeSupplier<Object, Exception> queryUnsafeSupplier) {

		try {
			query = queryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object query;

	public Object getQueryWeight() {
		return queryWeight;
	}

	public void setQueryWeight(Object queryWeight) {
		this.queryWeight = queryWeight;
	}

	public void setQueryWeight(
		UnsafeSupplier<Object, Exception> queryWeightUnsafeSupplier) {

		try {
			queryWeight = queryWeightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object queryWeight;

	public Object getRescoreQueryWeight() {
		return rescoreQueryWeight;
	}

	public void setRescoreQueryWeight(Object rescoreQueryWeight) {
		this.rescoreQueryWeight = rescoreQueryWeight;
	}

	public void setRescoreQueryWeight(
		UnsafeSupplier<Object, Exception> rescoreQueryWeightUnsafeSupplier) {

		try {
			rescoreQueryWeight = rescoreQueryWeightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object rescoreQueryWeight;

	public String getScoreMode() {
		return scoreMode;
	}

	public void setScoreMode(String scoreMode) {
		this.scoreMode = scoreMode;
	}

	public void setScoreMode(
		UnsafeSupplier<String, Exception> scoreModeUnsafeSupplier) {

		try {
			scoreMode = scoreModeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String scoreMode;

	public Object getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Object windowSize) {
		this.windowSize = windowSize;
	}

	public void setWindowSize(
		UnsafeSupplier<Object, Exception> windowSizeUnsafeSupplier) {

		try {
			windowSize = windowSizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object windowSize;

	@Override
	public Rescore clone() throws CloneNotSupportedException {
		return (Rescore)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Rescore)) {
			return false;
		}

		Rescore rescore = (Rescore)object;

		return Objects.equals(toString(), rescore.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return RescoreSerDes.toJSON(this);
	}

}