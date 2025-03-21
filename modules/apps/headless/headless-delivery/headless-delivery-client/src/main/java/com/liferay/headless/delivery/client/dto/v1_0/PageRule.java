/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.PageRuleSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PageRule implements Cloneable, Serializable {

	public static PageRule toDTO(String json) {
		return PageRuleSerDes.toDTO(json);
	}

	public ConditionType getConditionType() {
		return conditionType;
	}

	public String getConditionTypeAsString() {
		if (conditionType == null) {
			return null;
		}

		return conditionType.toString();
	}

	public void setConditionType(ConditionType conditionType) {
		this.conditionType = conditionType;
	}

	public void setConditionType(
		UnsafeSupplier<ConditionType, Exception> conditionTypeUnsafeSupplier) {

		try {
			conditionType = conditionTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ConditionType conditionType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public PageRuleAction[] getPageRuleActions() {
		return pageRuleActions;
	}

	public void setPageRuleActions(PageRuleAction[] pageRuleActions) {
		this.pageRuleActions = pageRuleActions;
	}

	public void setPageRuleActions(
		UnsafeSupplier<PageRuleAction[], Exception>
			pageRuleActionsUnsafeSupplier) {

		try {
			pageRuleActions = pageRuleActionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PageRuleAction[] pageRuleActions;

	public PageRuleCondition[] getPageRuleConditions() {
		return pageRuleConditions;
	}

	public void setPageRuleConditions(PageRuleCondition[] pageRuleConditions) {
		this.pageRuleConditions = pageRuleConditions;
	}

	public void setPageRuleConditions(
		UnsafeSupplier<PageRuleCondition[], Exception>
			pageRuleConditionsUnsafeSupplier) {

		try {
			pageRuleConditions = pageRuleConditionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PageRuleCondition[] pageRuleConditions;

	@Override
	public PageRule clone() throws CloneNotSupportedException {
		return (PageRule)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageRule)) {
			return false;
		}

		PageRule pageRule = (PageRule)object;

		return Objects.equals(toString(), pageRule.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageRuleSerDes.toJSON(this);
	}

	public static enum ConditionType {

		ALL("All"), ANY("Any");

		public static ConditionType create(String value) {
			for (ConditionType conditionType : values()) {
				if (Objects.equals(conditionType.getValue(), value) ||
					Objects.equals(conditionType.name(), value)) {

					return conditionType;
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

		private ConditionType(String value) {
			_value = value;
		}

		private final String _value;

	}

}