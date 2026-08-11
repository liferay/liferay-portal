/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
@GraphQLName("AssetStatistics")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AssetStatistics")
public class AssetStatistics implements Serializable {

	public static AssetStatistics toDTO(String json) {
		return ObjectMapperUtil.readValue(AssetStatistics.class, json);
	}

	public static AssetStatistics unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AssetStatistics.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getApprovedCount() {
		if (_approvedCountSupplier != null) {
			approvedCount = _approvedCountSupplier.get();

			_approvedCountSupplier = null;
		}

		return approvedCount;
	}

	public void setApprovedCount(Long approvedCount) {
		this.approvedCount = approvedCount;

		_approvedCountSupplier = null;
	}

	@JsonIgnore
	public void setApprovedCount(
		UnsafeSupplier<Long, Exception> approvedCountUnsafeSupplier) {

		_approvedCountSupplier = () -> {
			try {
				return approvedCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long approvedCount;

	@JsonIgnore
	private Supplier<Long> _approvedCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getBrokenLinksCount() {
		if (_brokenLinksCountSupplier != null) {
			brokenLinksCount = _brokenLinksCountSupplier.get();

			_brokenLinksCountSupplier = null;
		}

		return brokenLinksCount;
	}

	public void setBrokenLinksCount(Long brokenLinksCount) {
		this.brokenLinksCount = brokenLinksCount;

		_brokenLinksCountSupplier = null;
	}

	@JsonIgnore
	public void setBrokenLinksCount(
		UnsafeSupplier<Long, Exception> brokenLinksCountUnsafeSupplier) {

		_brokenLinksCountSupplier = () -> {
			try {
				return brokenLinksCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long brokenLinksCount;

	@JsonIgnore
	private Supplier<Long> _brokenLinksCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getExpiredCount() {
		if (_expiredCountSupplier != null) {
			expiredCount = _expiredCountSupplier.get();

			_expiredCountSupplier = null;
		}

		return expiredCount;
	}

	public void setExpiredCount(Long expiredCount) {
		this.expiredCount = expiredCount;

		_expiredCountSupplier = null;
	}

	@JsonIgnore
	public void setExpiredCount(
		UnsafeSupplier<Long, Exception> expiredCountUnsafeSupplier) {

		_expiredCountSupplier = () -> {
			try {
				return expiredCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long expiredCount;

	@JsonIgnore
	private Supplier<Long> _expiredCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getExpiringSoonCount() {
		if (_expiringSoonCountSupplier != null) {
			expiringSoonCount = _expiringSoonCountSupplier.get();

			_expiringSoonCountSupplier = null;
		}

		return expiringSoonCount;
	}

	public void setExpiringSoonCount(Long expiringSoonCount) {
		this.expiringSoonCount = expiringSoonCount;

		_expiringSoonCountSupplier = null;
	}

	@JsonIgnore
	public void setExpiringSoonCount(
		UnsafeSupplier<Long, Exception> expiringSoonCountUnsafeSupplier) {

		_expiringSoonCountSupplier = () -> {
			try {
				return expiringSoonCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long expiringSoonCount;

	@JsonIgnore
	private Supplier<Long> _expiringSoonCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getInDraftCount() {
		if (_inDraftCountSupplier != null) {
			inDraftCount = _inDraftCountSupplier.get();

			_inDraftCountSupplier = null;
		}

		return inDraftCount;
	}

	public void setInDraftCount(Long inDraftCount) {
		this.inDraftCount = inDraftCount;

		_inDraftCountSupplier = null;
	}

	@JsonIgnore
	public void setInDraftCount(
		UnsafeSupplier<Long, Exception> inDraftCountUnsafeSupplier) {

		_inDraftCountSupplier = () -> {
			try {
				return inDraftCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long inDraftCount;

	@JsonIgnore
	private Supplier<Long> _inDraftCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getPendingCount() {
		if (_pendingCountSupplier != null) {
			pendingCount = _pendingCountSupplier.get();

			_pendingCountSupplier = null;
		}

		return pendingCount;
	}

	public void setPendingCount(Long pendingCount) {
		this.pendingCount = pendingCount;

		_pendingCountSupplier = null;
	}

	@JsonIgnore
	public void setPendingCount(
		UnsafeSupplier<Long, Exception> pendingCountUnsafeSupplier) {

		_pendingCountSupplier = () -> {
			try {
				return pendingCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long pendingCount;

	@JsonIgnore
	private Supplier<Long> _pendingCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getReviewDateOverdueCount() {
		if (_reviewDateOverdueCountSupplier != null) {
			reviewDateOverdueCount = _reviewDateOverdueCountSupplier.get();

			_reviewDateOverdueCountSupplier = null;
		}

		return reviewDateOverdueCount;
	}

	public void setReviewDateOverdueCount(Long reviewDateOverdueCount) {
		this.reviewDateOverdueCount = reviewDateOverdueCount;

		_reviewDateOverdueCountSupplier = null;
	}

	@JsonIgnore
	public void setReviewDateOverdueCount(
		UnsafeSupplier<Long, Exception> reviewDateOverdueCountUnsafeSupplier) {

		_reviewDateOverdueCountSupplier = () -> {
			try {
				return reviewDateOverdueCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long reviewDateOverdueCount;

	@JsonIgnore
	private Supplier<Long> _reviewDateOverdueCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getScheduledCount() {
		if (_scheduledCountSupplier != null) {
			scheduledCount = _scheduledCountSupplier.get();

			_scheduledCountSupplier = null;
		}

		return scheduledCount;
	}

	public void setScheduledCount(Long scheduledCount) {
		this.scheduledCount = scheduledCount;

		_scheduledCountSupplier = null;
	}

	@JsonIgnore
	public void setScheduledCount(
		UnsafeSupplier<Long, Exception> scheduledCountUnsafeSupplier) {

		_scheduledCountSupplier = () -> {
			try {
				return scheduledCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long scheduledCount;

	@JsonIgnore
	private Supplier<Long> _scheduledCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getTotalCount() {
		if (_totalCountSupplier != null) {
			totalCount = _totalCountSupplier.get();

			_totalCountSupplier = null;
		}

		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;

		_totalCountSupplier = null;
	}

	@JsonIgnore
	public void setTotalCount(
		UnsafeSupplier<Long, Exception> totalCountUnsafeSupplier) {

		_totalCountSupplier = () -> {
			try {
				return totalCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long totalCount;

	@JsonIgnore
	private Supplier<Long> _totalCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getUpcomingReviewCount() {
		if (_upcomingReviewCountSupplier != null) {
			upcomingReviewCount = _upcomingReviewCountSupplier.get();

			_upcomingReviewCountSupplier = null;
		}

		return upcomingReviewCount;
	}

	public void setUpcomingReviewCount(Long upcomingReviewCount) {
		this.upcomingReviewCount = upcomingReviewCount;

		_upcomingReviewCountSupplier = null;
	}

	@JsonIgnore
	public void setUpcomingReviewCount(
		UnsafeSupplier<Long, Exception> upcomingReviewCountUnsafeSupplier) {

		_upcomingReviewCountSupplier = () -> {
			try {
				return upcomingReviewCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long upcomingReviewCount;

	@JsonIgnore
	private Supplier<Long> _upcomingReviewCountSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetStatistics)) {
			return false;
		}

		AssetStatistics assetStatistics = (AssetStatistics)object;

		return Objects.equals(toString(), assetStatistics.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long approvedCount = getApprovedCount();

		if (approvedCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"approvedCount\": ");

			sb.append(approvedCount);
		}

		Long brokenLinksCount = getBrokenLinksCount();

		if (brokenLinksCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"brokenLinksCount\": ");

			sb.append(brokenLinksCount);
		}

		Long expiredCount = getExpiredCount();

		if (expiredCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expiredCount\": ");

			sb.append(expiredCount);
		}

		Long expiringSoonCount = getExpiringSoonCount();

		if (expiringSoonCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expiringSoonCount\": ");

			sb.append(expiringSoonCount);
		}

		Long inDraftCount = getInDraftCount();

		if (inDraftCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inDraftCount\": ");

			sb.append(inDraftCount);
		}

		Long pendingCount = getPendingCount();

		if (pendingCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pendingCount\": ");

			sb.append(pendingCount);
		}

		Long reviewDateOverdueCount = getReviewDateOverdueCount();

		if (reviewDateOverdueCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reviewDateOverdueCount\": ");

			sb.append(reviewDateOverdueCount);
		}

		Long scheduledCount = getScheduledCount();

		if (scheduledCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduledCount\": ");

			sb.append(scheduledCount);
		}

		Long totalCount = getTotalCount();

		if (totalCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalCount\": ");

			sb.append(totalCount);
		}

		Long upcomingReviewCount = getUpcomingReviewCount();

		if (upcomingReviewCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"upcomingReviewCount\": ");

			sb.append(upcomingReviewCount);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.cms.dto.v1_0.AssetStatistics",
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
// LIFERAY-REST-BUILDER-HASH:-178751699