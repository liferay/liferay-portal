/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.criteria.contributor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.field.Field;

import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.Locale;

/**
 * Provides an interface for extending the segment's {@link Criteria} by adding
 * more filters.
 *
 * @author Eduardo García
 */
public interface SegmentsCriteriaContributor {

	public static void contribute(
		Criteria criteria, String filterString,
		Criteria.Conjunction conjunction, String key, Criteria.Type type) {

		criteria.addCriterion(key, type, filterString, conjunction);
		criteria.addFilter(type, filterString, conjunction);
	}

	/**
	 * Contributes the criterion to a segment's criteria.
	 *
	 * @param criteria the segment's criteria
	 * @param filterString the criterion's filter as a string
	 * @param conjunction the criterion's conjunction
	 */
	public default void contribute(
		Criteria criteria, String filterString,
		Criteria.Conjunction conjunction) {

		SegmentsCriteriaContributor.contribute(
			criteria, filterString, conjunction, getKey(), getType());
	}

	/**
	 * Returns a criteria as a JSONObject.
	 *
	 * @param  criteria the segment's criteria
	 * @return the JSONObject from the segment's criteria
	 * @review
	 */
	public JSONObject getCriteriaJSONObject(Criteria criteria) throws Exception;

	/**
	 * Returns the contributed criterion from the criteria.
	 *
	 * @param  criteria the segment's criteria
	 * @return the contributed criterion
	 */
	public default Criteria.Criterion getCriterion(Criteria criteria) {
		return criteria.getCriterion(getKey());
	}

	/**
	 * Returns the entity model associated with the contributor.
	 *
	 * @return the entity model associated with the contributor
	 */
	public EntityModel getEntityModel();

	/**
	 * Returns the name of the entity model associated with the contributor.
	 *
	 * @return the name of the entity model associated with the contributor
	 */
	public String getEntityName();

	/**
	 * Returns the list of fields that are supported by this contributor.
	 *
	 * @param  portletRequest the portlet request
	 * @return the list of fields that are supported by this contributor
	 */
	public List<Field> getFields(PortletRequest portletRequest);

	/**
	 * Returns the contributor's unique key.
	 *
	 * @return the contributor's unique key
	 */
	public String getKey();

	/**
	 * Returns the label displayed in the user interface based on the locale.
	 *
	 * @param  locale the locale to apply for the label
	 * @return the label displayed in the user interface
	 */
	public default String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "contributor." + getKey());
	}

	/**
	 * Returns the contributor's type.
	 *
	 * @return the contributor's type
	 * @see    Criteria.Type
	 */
	public Criteria.Type getType();

}