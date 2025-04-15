/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.criteria.contributor;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.criteria.mapper.SegmentsCriteriaJSONObjectMapper;
import com.liferay.segments.field.Field;
import com.liferay.segments.internal.odata.entity.EntityModelFieldMapper;
import com.liferay.segments.internal.odata.entity.OrganizationEntityModel;
import com.liferay.segments.odata.retriever.ODataRetriever;

import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"segments.criteria.contributor.key=" + UserOrganizationSegmentsCriteriaContributor.KEY,
		"segments.criteria.contributor.model.class.name=com.liferay.portal.kernel.model.User",
		"segments.criteria.contributor.priority:Integer=20"
	},
	service = SegmentsCriteriaContributor.class
)
public class UserOrganizationSegmentsCriteriaContributor
	implements SegmentsCriteriaContributor {

	public static final String KEY = "user-organization";

	@Override
	public void contribute(
		Criteria criteria, String filterString,
		Criteria.Conjunction conjunction) {

		criteria.addCriterion(getKey(), getType(), filterString, conjunction);

		String newFilterString = null;

		try {
			Matcher matcher = _pattern.matcher(filterString);

			while (matcher.find()) {
				String date = matcher.group("date");

				filterString = StringUtil.replace(
					filterString, matcher.group(),
					StringBundler.concat(
						"dateModified ge ", date, "T00:00:00.000Z and ",
						"dateModified le ", date, "T23:59:59.999Z"));
			}

			List<Organization> organizations = _oDataRetriever.getResults(
				CompanyThreadLocal.getCompanyId(), filterString,
				LocaleUtil.getDefault(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			if (!organizations.isEmpty()) {
				StringBundler sb = new StringBundler(
					(2 * organizations.size()) + 1);

				sb.append("organizationIds in ('");

				for (Organization organization : organizations) {
					sb.append(organization.getOrganizationId());
					sb.append("', '");
				}

				sb.setStringAt("')", sb.index() - 1);

				newFilterString = sb.toString();
			}
		}
		catch (PortalException portalException) {
			_log.error(
				StringBundler.concat(
					"Unable to evaluate criteria ", criteria, " with filter ",
					filterString, " and conjunction ", conjunction.getValue()),
				portalException);
		}

		if (newFilterString == null) {
			newFilterString = "(userId eq '0')";
		}

		criteria.addFilter(getType(), newFilterString, conjunction);
	}

	@Override
	public JSONObject getCriteriaJSONObject(Criteria criteria)
		throws Exception {

		return _segmentsCriteriaJSONObjectMapper.toJSONObject(criteria, this);
	}

	@Override
	public EntityModel getEntityModel() {
		return _entityModel;
	}

	@Override
	public String getEntityName() {
		return OrganizationEntityModel.NAME;
	}

	@Override
	public List<Field> getFields(PortletRequest portletRequest) {
		return _entityModelFieldMapper.getFields(_entityModel, portletRequest);
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Criteria.Type getType() {
		return Criteria.Type.MODEL;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserOrganizationSegmentsCriteriaContributor.class);

	private static final Pattern _pattern = Pattern.compile(
		"dateModified eq (?<date>\\d{4}-\\d{2}-\\d{2})T.*Z");

	@Reference(
		target = "(entity.model.name=" + OrganizationEntityModel.NAME + ")"
	)
	private EntityModel _entityModel;

	@Reference
	private EntityModelFieldMapper _entityModelFieldMapper;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Organization)"
	)
	private ODataRetriever<Organization> _oDataRetriever;

	@Reference(target = "(segments.criteria.mapper.key=odata)")
	private SegmentsCriteriaJSONObjectMapper _segmentsCriteriaJSONObjectMapper;

}