/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.dto.v1_0.UserAccount;
import com.liferay.headless.asset.library.resource.v1_0.UserAccountResource;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Roberto Díaz
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user-account.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = UserAccountResource.class
)
public class UserAccountResourceImpl extends BaseUserAccountResourceImpl {

	@Override
	public void
			deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String userExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		User user = _userService.getUserByExternalReferenceCode(
			userExternalReferenceCode, contextCompany.getCompanyId());

		deleteAssetLibraryUserAccount(group.getGroupId(), user.getUserId());
	}

	@Override
	public void deleteAssetLibraryUserAccount(Long assetLibraryId, Long userId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		_updateUser(assetLibraryId, userId, false);
	}

	@Override
	public UserAccount
			getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String userExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		User user = _userService.getUserByExternalReferenceCode(
			userExternalReferenceCode, contextCompany.getCompanyId());

		return getAssetLibraryUserAccount(group.getGroupId(), user.getUserId());
	}

	@Override
	public Page<UserAccount>
			getAssetLibraryByExternalReferenceCodeUserAccountsPage(
				String externalReferenceCode, String keywords, String search,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group group = _getGroup(externalReferenceCode);

		return getAssetLibraryUserAccountsPage(
			group.getGroupId(), keywords, search, pagination, sorts);
	}

	@Override
	public UserAccount getAssetLibraryUserAccount(
			Long assetLibraryId, Long userId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		if (!_groupService.hasUserGroup(userId, assetLibraryId)) {
			throw new NoSuchUserException(
				"No user exists with user group ID " + userId);
		}

		return _toUserAccount(_userService.getUserById(userId));
	}

	@NestedField(parentClass = AssetLibrary.class, value = "userAccounts")
	@Override
	public Page<UserAccount> getAssetLibraryUserAccountsPage(
			@NestedFieldId("siteId") Long assetLibraryId, String keywords,
			String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _getUserAccountsPage(
			assetLibraryId, keywords, pagination, sorts);
	}

	@Override
	public UserAccount
			putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String userExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		User user = _userService.getUserByExternalReferenceCode(
			userExternalReferenceCode, contextCompany.getCompanyId());

		return putAssetLibraryUserAccount(group.getGroupId(), user.getUserId());
	}

	@Override
	public UserAccount putAssetLibraryUserAccount(
			Long assetLibraryId, Long userId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toUserAccount(_updateUser(assetLibraryId, userId, true));
	}

	private Group _getGroup(String externalReferenceCode) throws Exception {
		Group group = _groupService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (group == null) {
			throw new NoSuchGroupException(
				"No group exists with external reference code " +
					externalReferenceCode);
		}

		return group;
	}

	private Page<UserAccount> _getUserAccountsPage(
			Long groupId, String keywords, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ASSIGN_MEMBERS, groupId,
					"putAssetLibraryUserAccount", _groupModelResourcePermission)
			).put(
				"delete",
				addAction(
					ActionKeys.ASSIGN_MEMBERS, groupId,
					"deleteAssetLibraryUserAccount",
					_groupModelResourcePermission)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, 0L, "getAssetLibraryUserAccountsPage",
					_groupModelResourcePermission)
			).build(),
			booleanQuery -> booleanQuery.add(
				new TermQueryImpl("groupIds", String.valueOf(groupId)),
				BooleanClauseOccur.MUST),
			null, User.class.getName(), keywords, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toUserAccount(
				_userService.getUserById(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private long[] _getUserGroupIds(
		User user, long assetLibraryId, boolean add) {

		Set<Long> groupIds = new HashSet<>();

		for (long groupId : user.getGroupIds()) {
			groupIds.add(groupId);
		}

		if (add) {
			groupIds.add(assetLibraryId);
		}
		else {
			groupIds.remove(assetLibraryId);
		}

		return ArrayUtil.toLongArray(groupIds);
	}

	private UserAccount _toUserAccount(User user) throws Exception {
		return _userAccountDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.ASSIGN_MEMBERS, user.getGroupId(),
						"deleteAssetLibraryUserAccount",
						_groupModelResourcePermission)
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, user.getUserId(),
						"getAssetLibraryUserAccount",
						_userModelResourcePermission)
				).build(),
				_dtoConverterRegistry, contextHttpServletRequest,
				user.getUserId(), contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser));
	}

	private User _updateUser(Long assetLibraryId, Long userId, boolean add)
		throws Exception {

		User user = _userService.getUserById(userId);

		Contact contact = user.getContact();

		Calendar birthdayCal = CalendarFactoryUtil.getCalendar();

		birthdayCal.setTime(user.getBirthday());

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			User.class.getName(), contextHttpServletRequest);

		return _userService.updateUser(
			user.getUserId(), user.getPassword(), null, null,
			user.isPasswordReset(), null, null, user.getScreenName(),
			user.getEmailAddress(), user.getLanguageId(), user.getTimeZoneId(),
			user.getGreeting(), user.getComments(), user.getFirstName(),
			user.getMiddleName(), user.getLastName(),
			contact.getPrefixListTypeId(), contact.getSuffixListTypeId(),
			user.isMale(), birthdayCal.get(Calendar.MONTH),
			birthdayCal.get(Calendar.DATE), birthdayCal.get(Calendar.YEAR),
			contact.getSmsSn(), contact.getFacebookSn(), contact.getJabberSn(),
			contact.getSkypeSn(), contact.getTwitterSn(), user.getJobTitle(),
			_getUserGroupIds(user, assetLibraryId, add),
			user.getOrganizationIds(), null, null, user.getUserGroupIds(),
			serviceContext);
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Group)"
	)
	private ModelResourcePermission<Group> _groupModelResourcePermission;

	@Reference
	private GroupService _groupService;

	@Reference(
		target = "(component.name=com.liferay.headless.asset.library.internal.dto.v1_0.converter.UserAccountDTOConverter)"
	)
	private DTOConverter<User, UserAccount> _userAccountDTOConverter;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.User)"
	)
	private ModelResourcePermission<User> _userModelResourcePermission;

	@Reference
	private UserService _userService;

}