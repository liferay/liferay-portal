/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service.impl;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryTable;
import com.liferay.blogs.model.BlogsStatsUser;
import com.liferay.blogs.model.impl.BlogsStatsUserImpl;
import com.liferay.blogs.service.base.BlogsStatsUserLocalServiceBaseImpl;
import com.liferay.blogs.service.persistence.BlogsEntryPersistence;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Alias;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.query.LimitStep;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.petra.sql.dsl.query.sort.OrderByExpression;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Users_OrgsTable;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.ratings.kernel.model.RatingsEntryTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Máté Thurzó
 */
@Component(
	property = "model.class.name=com.liferay.blogs.model.BlogsStatsUser",
	service = AopService.class
)
public class BlogsStatsUserLocalServiceImpl
	extends BlogsStatsUserLocalServiceBaseImpl {

	@Override
	public List<BlogsStatsUser> getGroupStatsUsers(
		long groupId, int start, int end) {

		Predicate predicate = _groupIdAlias.eq(groupId);

		return _getBlogsStatsUsers(
			UnaryOperator.identity(),
			predicate.and(_entryCountExpression.neq(0L)),
			_lastPostDateExpression.descending(), start, end);
	}

	@Override
	public List<BlogsStatsUser> getGroupsStatsUsers(
		long companyId, long groupId, int start, int end) {

		Predicate predicate = _companyIdAlias.eq(companyId);

		return _getBlogsStatsUsers(
			UnaryOperator.identity(), predicate.and(_groupIdAlias.eq(groupId)),
			_entryCountExpression.descending(), start, end);
	}

	@Override
	public List<BlogsStatsUser> getOrganizationStatsUsers(
		long organizationId, int start, int end) {

		return _getBlogsStatsUsers(
			joinStep -> joinStep.innerJoinON(
				Users_OrgsTable.INSTANCE,
				Users_OrgsTable.INSTANCE.userId.eq(
					BlogsEntryTable.INSTANCE.userId
				).and(
					Users_OrgsTable.INSTANCE.organizationId.eq(organizationId)
				)),
			null, _lastPostDateExpression.descending(), start, end);
	}

	@Override
	public BlogsStatsUser getStatsUser(long groupId, long userId)
		throws PortalException {

		Predicate predicate = _groupIdAlias.eq(groupId);

		List<BlogsStatsUser> blogsStatsUsers = _getBlogsStatsUsers(
			UnaryOperator.identity(), predicate.and(_userIdAlias.eq(userId)),
			_groupIdExpression.descending(), 0, 1);

		if (blogsStatsUsers.isEmpty()) {
			return new BlogsStatsUserImpl(0, groupId, null, 0, 0, 0, userId);
		}

		return blogsStatsUsers.get(0);
	}

	private List<BlogsStatsUser> _getBlogsStatsUsers(
		UnaryOperator<JoinStep> unaryOperator, Predicate predicate,
		OrderByExpression orderByExpression, int start, int end) {

		JoinStep joinStep = DSLQueryFactoryUtil.select(
			_entryCountExpression, _groupIdAlias, _lastPostDateExpression,
			DSLFunctionFactoryUtil.countDistinct(
				RatingsEntryTable.INSTANCE.entryId
			).as(
				"ratingsTotalEntries"
			),
			DSLFunctionFactoryUtil.avg(
				RatingsEntryTable.INSTANCE.score
			).as(
				"ratingsAverageScore"
			),
			DSLFunctionFactoryUtil.sum(
				RatingsEntryTable.INSTANCE.score
			).as(
				"ratingsTotalScore"
			),
			_userIdAlias, _companyIdAlias, _groupIdExpression
		).from(
			BlogsEntryTable.INSTANCE
		);

		joinStep = unaryOperator.apply(joinStep);

		Table innerTable = joinStep.leftJoinOn(
			RatingsEntryTable.INSTANCE,
			RatingsEntryTable.INSTANCE.classNameId.eq(
				_classNameLocalService.getClassNameId(
					BlogsEntry.class.getName())
			).and(
				RatingsEntryTable.INSTANCE.classPK.eq(
					BlogsEntryTable.INSTANCE.entryId)
			)
		).groupBy(
			BlogsEntryTable.INSTANCE.groupId, BlogsEntryTable.INSTANCE.userId,
			BlogsEntryTable.INSTANCE.companyId
		).as(
			"innerTable"
		);

		OrderByStep orderByStep = DSLQueryFactoryUtil.select(
		).from(
			innerTable
		).where(
			predicate
		);

		LimitStep limitStep = orderByStep;

		if (orderByExpression != null) {
			limitStep = orderByStep.orderBy(orderByExpression);
		}

		List<Object[]> results = _blogsEntryPersistence.dslQuery(
			limitStep.limit(start, end));

		List<BlogsStatsUser> blogsStatsUsers = new ArrayList<>(results.size());

		for (Object[] columns : results) {
			blogsStatsUsers.add(
				new BlogsStatsUserImpl(
					GetterUtil.getLong(columns[0]),
					GetterUtil.getLong(columns[1]), (Date)columns[2],
					GetterUtil.getLong(columns[3]),
					GetterUtil.getDouble(columns[4]),
					GetterUtil.getDouble(columns[5]),
					GetterUtil.getLong(columns[6])));
		}

		return blogsStatsUsers;
	}

	@Reference
	private BlogsEntryPersistence _blogsEntryPersistence;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	private final Alias<Long> _companyIdAlias =
		BlogsEntryTable.INSTANCE.companyId.as("blogsEntryCompanyId");
	private final Expression<Long> _entryCountExpression =
		DSLFunctionFactoryUtil.countDistinct(
			BlogsEntryTable.INSTANCE.entryId
		).as(
			"entryCount"
		);
	private final Alias<Long> _groupIdAlias =
		BlogsEntryTable.INSTANCE.groupId.as("blogsEntryGroupId");
	private final Expression<Long> _groupIdExpression =
		DSLFunctionFactoryUtil.max(
			BlogsEntryTable.INSTANCE.groupId
		).as(
			"groupId"
		);
	private final Expression<Date> _lastPostDateExpression =
		DSLFunctionFactoryUtil.max(
			BlogsEntryTable.INSTANCE.modifiedDate
		).as(
			"lastPostDate"
		);
	private final Alias<Long> _userIdAlias = BlogsEntryTable.INSTANCE.userId.as(
		"blogsEntryUserId");

}