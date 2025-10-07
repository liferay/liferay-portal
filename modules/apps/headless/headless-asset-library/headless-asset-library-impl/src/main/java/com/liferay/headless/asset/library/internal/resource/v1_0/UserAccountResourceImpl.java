/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.dto.v1_0.UserAccount;
import com.liferay.headless.asset.library.resource.v1_0.UserAccountResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserTable;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
			deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String userAccountExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		User user = _userService.getUserByExternalReferenceCode(
			userAccountExternalReferenceCode, contextCompany.getCompanyId());

		deleteAssetLibraryUserAccount(group.getGroupId(), user.getUserId());
	}

	@Override
	public void deleteAssetLibraryUserAccount(
			Long assetLibraryId, Long userAccountId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		_updateUser(assetLibraryId, userAccountId, false);
	}

	@Override
	public UserAccount
			getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String userAccountExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group group = _getGroup(assetLibraryExternalReferenceCode);

		_checkAssetLibraryAdminOrAssetLibraryMember(group.getGroupId());

		User user = _userLocalService.getUserByExternalReferenceCode(
			userAccountExternalReferenceCode, contextCompany.getCompanyId());

		_checkAssetLibraryMember(group.getGroupId(), user.getUserId());

		return _toUserAccount(group.getGroupId(), user);
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

		_checkAssetLibraryAdminOrAssetLibraryMember(group.getGroupId());

		return _getUserAccountsPage(
			group.getGroupId(), keywords, pagination, sorts);
	}

	@Override
	public UserAccount getAssetLibraryUserAccount(
			Long assetLibraryId, Long userAccountId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		_checkAssetLibraryAdminOrAssetLibraryMember(assetLibraryId);

		User user = _userLocalService.getUserById(userAccountId);

		_checkAssetLibraryMember(assetLibraryId, user.getUserId());

		return _toUserAccount(assetLibraryId, user);
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

		_checkAssetLibraryAdminOrAssetLibraryMember(assetLibraryId);

		return _getUserAccountsPage(
			assetLibraryId, keywords, pagination, sorts);
	}

	@Override
	public UserAccount
			putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String userAccountExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		User user = _userService.getUserByExternalReferenceCode(
			userAccountExternalReferenceCode, contextCompany.getCompanyId());

		return putAssetLibraryUserAccount(group.getGroupId(), user.getUserId());
	}

	@Override
	public UserAccount putAssetLibraryUserAccount(
			Long assetLibraryId, Long userAccountId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toUserAccount(
			assetLibraryId, _updateUser(assetLibraryId, userAccountId, true));
	}

	private void _checkAssetLibraryAdminOrAssetLibraryMember(long groupId)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isGroupAdmin(groupId)) {
			return;
		}

		if (!_userService.hasGroupUser(groupId, contextUser.getUserId())) {
			throw new PrincipalException.MustHavePermission(
				contextUser.getUserId(), ActionKeys.VIEW);
		}
	}

	private void _checkAssetLibraryMember(long groupId, long userId)
		throws Exception {

		if (!_userLocalService.hasGroupUser(groupId, userId)) {
			throw new NoSuchUserException(
				StringBundler.concat(
					"User ", userId, " is not associated to group ", groupId));
		}
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
		Long groupId, String keywords, Pagination pagination, Sort[] sorts) {

		return Page.of(
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
					ActionKeys.VIEW_MEMBERS, groupId,
					"getAssetLibraryUserAccountsPage",
					_groupModelResourcePermission)
			).build(),
			transform(
				_userLocalService.searchBySocial(
					contextCompany.getCompanyId(), new long[] {groupId}, null,
					keywords, pagination.getStartPosition(),
					pagination.getEndPosition(), _toOrderByComparator(sorts)),
				user -> _toUserAccount(groupId, user)),
			pagination,
			_userLocalService.searchCountBySocial(
				contextCompany.getCompanyId(), new long[] {groupId}, null,
				keywords));
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

	private OrderByComparator<User> _toOrderByComparator(Sort[] sorts) {
		if (ArrayUtil.isEmpty(sorts)) {
			return null;
		}

		List<Object> objects = new ArrayList<>();

		for (Sort sort : sorts) {
			if (Objects.equals(sort.getFieldName(), "externalReferenceCode")) {
				objects.add(sort.getFieldName());
				objects.add(!sort.isReverse());
			}
			else if (Objects.equals(sort.getFieldName(), "id")) {
				objects.add("userId");
				objects.add(!sort.isReverse());
			}
			else if (Objects.equals(sort.getFieldName(), "name")) {
				objects.add("firstName");
				objects.add(!sort.isReverse());
				objects.add("middleName");
				objects.add(!sort.isReverse());
				objects.add("lastName");
				objects.add(!sort.isReverse());
			}
		}

		return OrderByComparatorFactoryUtil.create(
			UserTable.INSTANCE.getTableName(), objects.toArray());
	}

	private UserAccount _toUserAccount(long assetLibraryId, User user)
		throws Exception {

		DefaultDTOConverterContext defaultDTOConverterContext =
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
				contextUriInfo, contextUser);

		defaultDTOConverterContext.setAttribute(
			"assetLibraryId", assetLibraryId);

		return _userAccountDTOConverter.toDTO(defaultDTOConverterContext);
	}

	private User _updateUser(
			Long assetLibraryId, Long userAccountId, boolean add)
		throws Exception {

		User user = _userService.getUserById(userAccountId);

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

	@Reference
	private UserLocalService _userLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.User)"
	)
	private ModelResourcePermission<User> _userModelResourcePermission;

	@Reference
	private UserService _userService;

}