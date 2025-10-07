/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.converter;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.dto.v1_0.AssetLibraryBrief;
import com.liferay.headless.admin.user.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.dto.v1_0.OrganizationBrief;
import com.liferay.headless.admin.user.dto.v1_0.Phone;
import com.liferay.headless.admin.user.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.dto.v1_0.SiteBrief;
import com.liferay.headless.admin.user.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.dto.v1_0.UserAccountContactInformation;
import com.liferay.headless.admin.user.dto.v1_0.UserGroupBrief;
import com.liferay.headless.admin.user.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.EmailAddressUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PermissionUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PhoneUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.RoleBriefUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderListTypeUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.TaxonomyCategoryBriefUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.WebUrlUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.permission.UserBagFactoryUtil;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Admin.User",
		"dto.class.name=com.liferay.portal.kernel.model.User",
		"service.ranking:Integer=" + Integer.MAX_VALUE, "version=v1.0"
	},
	service = DTOConverter.class
)
public class UserResourceDTOConverter
	implements DTOConverter<User, UserAccount> {

	@Override
	public String getContentType() {
		return Account.class.getSimpleName();
	}

	@Override
	public User getObject(String externalReferenceCode) throws Exception {
		User user = _userLocalService.fetchUserByExternalReferenceCode(
			externalReferenceCode, CompanyThreadLocal.getCompanyId());

		if (user == null) {
			user = _userLocalService.getUser(
				GetterUtil.getLong(externalReferenceCode));
		}

		return user;
	}

	@Override
	public UserAccount toDTO(DTOConverterContext dtoConverterContext, User user)
		throws Exception {

		if (user == null) {
			return null;
		}

		Contact contact = user.fetchContact();

		return new UserAccount() {
			{
				setAccountBriefs(
					() -> TransformUtil.transformToArray(
						_accountEntryUserRelService.
							getAccountEntryUserRelsByAccountUserId(
								user.getUserId()),
						accountEntryUserRel -> _toAccountBrief(
							accountEntryUserRel, dtoConverterContext, user),
						AccountBrief.class));
				setActions(dtoConverterContext::getActions);
				setAdditionalName(user::getMiddleName);
				setAlternateName(user::getScreenName);
				setAssetLibraryBriefs(
					() -> NestedFieldsSupplier.supply(
						"assetLibraryBriefs",
						fieldName -> TransformUtil.transformToArray(
							ListUtil.filter(
								user.getAllGroups(),
								group ->
									group.getType() ==
										GroupConstants.TYPE_DEPOT),
							group -> _toAssetLibraryBrief(
								group, dtoConverterContext),
							AssetLibraryBrief.class)));
				setBirthDate(
					() -> {
						if (contact == null) {
							return null;
						}

						return contact.getBirthday();
					});
				setCreator(
					() -> {
						if (contact == null) {
							return null;
						}

						return NestedFieldsSupplier.supply(
							"creator",
							fieldName -> CreatorUtil.toCreator(
								_portal,
								_userLocalService.fetchUser(
									contact.getUserId())));
					});
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						User.class.getName(), user.getUserId(),
						user.getCompanyId(), dtoConverterContext.getLocale()));
				setDashboardURL(
					() -> {
						Group group = user.getGroup();

						if (group == null) {
							return null;
						}

						return group.getDisplayURL(
							_getThemeDisplay(group), true);
					});
				setDateCreated(user::getCreateDate);
				setDateModified(user::getModifiedDate);
				setEmailAddress(user::getEmailAddress);
				setExternalReferenceCode(user::getExternalReferenceCode);
				setFamilyName(user::getLastName);
				setGender(
					() -> {
						if (!PrefsPropsUtil.getBoolean(
								user.getCompanyId(),
								PropsKeys.
									FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_CONTACT_MALE) ||
							(contact == null)) {

							return null;
						}

						if (contact.isMale()) {
							return Gender.MALE;
						}

						return Gender.FEMALE;
					});
				setGivenName(user::getFirstName);
				setHasLoginDate(
					() -> {
						boolean hasLoginDate = false;

						if (user.getLastLoginDate() != null) {
							hasLoginDate = true;
						}

						return hasLoginDate;
					});
				setHonorificPrefix(
					() -> {
						if (contact == null) {
							return null;
						}

						return ServiceBuilderListTypeUtil.
							getServiceBuilderListTypeMessage(
								contact.getPrefixListTypeId(),
								dtoConverterContext.getLocale());
					});
				setHonorificSuffix(
					() -> {
						if (contact == null) {
							return null;
						}

						return ServiceBuilderListTypeUtil.
							getServiceBuilderListTypeMessage(
								contact.getSuffixListTypeId(),
								dtoConverterContext.getLocale());
					});
				setId(user::getUserId);
				setImage(
					() -> {
						if (user.getPortraitId() == 0) {
							return null;
						}

						ThemeDisplay themeDisplay = new ThemeDisplay() {
							{
								setPathImage(_portal.getPathImage());
							}
						};

						return user.getPortraitURL(themeDisplay);
					});
				setImageId(user::getPortraitId);
				setJobTitle(user::getJobTitle);
				setKeywords(
					() -> ListUtil.toArray(
						_assetTagLocalService.getTags(
							User.class.getName(), user.getUserId()),
						AssetTag.NAME_ACCESSOR));
				setLanguageDisplayName(
					() -> {
						if (Validator.isNull(user.getLanguageId())) {
							return null;
						}

						Locale locale = LocaleUtil.fromLanguageId(
							user.getLanguageId());

						return locale.getDisplayName(
							dtoConverterContext.getLocale());
					});
				setLanguageId(user::getLanguageId);
				setLastLoginDate(user::getLastLoginDate);
				setName(user::getFullName);
				setOrganizationBriefs(
					() -> TransformUtil.transformToArray(
						user.getOrganizations(),
						organization -> _toOrganizationBrief(
							dtoConverterContext, organization, user),
						OrganizationBrief.class));
				setPermissions(
					() -> NestedFieldsSupplier.supply(
						"permissions",
						nestedFieldNames -> PermissionUtil.toPermissions(
							user.getCompanyId(), user.getGroupId(),
							user.getUserId(), User.class.getName(),
							_permissionService, _resourceActionLocalService)));
				setProfileURL(
					() -> {
						Group group = user.getGroup();

						if (group == null) {
							return null;
						}

						return group.getDisplayURL(_getThemeDisplay(group));
					});
				setRoleBriefs(
					() -> {
						UserBag userBag = UserBagFactoryUtil.create(user);

						return _toRoleBriefs(
							dtoConverterContext, userBag.getRoles());
					});
				setSiteBriefs(
					() -> TransformUtil.transformToArray(
						_groupLocalService.getUserSitesGroups(user.getUserId()),
						group -> _toSiteBrief(dtoConverterContext, group, user),
						SiteBrief.class));
				setStatus(
					() -> {
						if (user.getStatus() ==
								WorkflowConstants.STATUS_APPROVED) {

							return Status.ACTIVE;
						}

						if (user.getStatus() ==
								WorkflowConstants.STATUS_INACTIVE) {

							return Status.INACTIVE;
						}

						return null;
					});
				setTaxonomyCategoryBriefs(
					() -> NestedFieldsSupplier.supply(
						"taxonomyCategoryBriefs",
						nestedFieldNames -> TransformUtil.transformToArray(
							_assetCategoryService.getCategories(
								User.class.getName(), user.getUserId()),
							assetCategory ->
								TaxonomyCategoryBriefUtil.
									toTaxonomyCategoryBrief(
										assetCategory, dtoConverterContext),
							TaxonomyCategoryBrief.class)));
				setUserAccountContactInformation(
					() -> new UserAccountContactInformation() {
						{
							setEmailAddresses(
								() -> TransformUtil.transformToArray(
									user.getEmailAddresses(),
									EmailAddressUtil::toEmailAddress,
									EmailAddress.class));
							setFacebook(
								() -> {
									if (contact == null) {
										return null;
									}

									return contact.getFacebookSn();
								});
							setJabber(
								() -> {
									if (contact == null) {
										return null;
									}

									return contact.getJabberSn();
								});
							setPostalAddresses(
								() -> TransformUtil.transformToArray(
									user.getAddresses(),
									address -> _postalAddressDTOConverter.toDTO(
										new DefaultDTOConverterContext(
											dtoConverterContext.
												isAcceptAllLanguages(),
											null, _dtoConverterRegistry,
											address.getAddressId(),
											dtoConverterContext.getLocale(),
											dtoConverterContext.getUriInfo(),
											dtoConverterContext.getUser())),
									PostalAddress.class));
							setSkype(
								() -> {
									if (contact == null) {
										return null;
									}

									return contact.getSkypeSn();
								});
							setSms(
								() -> {
									if (contact == null) {
										return null;
									}

									return contact.getSmsSn();
								});
							setTelephones(
								() -> TransformUtil.transformToArray(
									user.getPhones(), PhoneUtil::toPhone,
									Phone.class));
							setTwitter(
								() -> {
									if (contact == null) {
										return null;
									}

									return contact.getTwitterSn();
								});
							setWebUrls(
								() -> TransformUtil.transformToArray(
									user.getWebsites(), WebUrlUtil::toWebUrl,
									WebUrl.class));
						}
					});
				setUserGroupBriefs(
					() -> TransformUtil.transformToArray(
						_userGroupLocalService.getUserUserGroups(
							user.getUserId()),
						userGroup -> _toUserGroupBrief(userGroup),
						UserGroupBrief.class));
			}
		};
	}

	private ThemeDisplay _getThemeDisplay(Group group) {
		return new ThemeDisplay() {
			{
				setPortalURL(StringPool.BLANK);

				if (group != null) {
					setSiteGroupId(group.getGroupId());
				}
			}
		};
	}

	private AccountBrief _toAccountBrief(
			AccountEntryUserRel accountEntryUserRel,
			DTOConverterContext dtoConverterContext, User user)
		throws Exception {

		if (accountEntryUserRel.getAccountEntryId() ==
				AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT) {

			return null;
		}

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			accountEntryUserRel.getAccountEntryId());

		return new AccountBrief() {
			{
				setExternalReferenceCode(
					accountEntry::getExternalReferenceCode);
				setId(accountEntry::getAccountEntryId);
				setName(accountEntry::getName);
				setRoleBriefs(
					() -> TransformUtil.transformToArray(
						_accountRoleLocalService.getAccountRoles(
							accountEntry.getAccountEntryId(), user.getUserId()),
						accountRole -> _toRoleBrief(
							accountRole, dtoConverterContext),
						RoleBrief.class));
				setType(accountEntry::getType);
			}
		};
	}

	private AssetLibraryBrief _toAssetLibraryBrief(
			Group group, DTOConverterContext dtoConverterContext)
		throws PortalException {

		return new AssetLibraryBrief() {
			{
				setExternalReferenceCode(group::getExternalReferenceCode);
				setGroupId(group::getGroupId);
				setName(() -> group.getName(dtoConverterContext.getLocale()));
			}
		};
	}

	private OrganizationBrief _toOrganizationBrief(
		DTOConverterContext dtoConverterContext, Organization organization,
		User user) {

		return new OrganizationBrief() {
			{
				setExternalReferenceCode(
					organization::getExternalReferenceCode);
				setId(organization::getOrganizationId);
				setName(organization::getName);
				setRoleBriefs(
					() -> _toRoleBriefs(
						dtoConverterContext,
						_roleLocalService.getUserGroupRoles(
							user.getUserId(), organization.getGroupId())));
			}
		};
	}

	private RoleBrief _toRoleBrief(
			AccountRole accountRole, DTOConverterContext dtoConverterContext)
		throws Exception {

		Role role = accountRole.getRole();

		return new RoleBrief() {
			{
				setExternalReferenceCode(accountRole::getExternalReferenceCode);
				setId(accountRole::getAccountRoleId);
				setName(accountRole::getRoleName);
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						role.getTitleMap()));
			}
		};
	}

	private RoleBrief[] _toRoleBriefs(
		DTOConverterContext dtoConverterContext, Collection<Role> roles) {

		return TransformUtil.transformToArray(
			roles,
			role -> {
				if (!_roleModelResourcePermission.contains(
						PermissionThreadLocal.getPermissionChecker(), role,
						ActionKeys.VIEW)) {

					return null;
				}

				return RoleBriefUtil.toRoleBrief(dtoConverterContext, role);
			},
			RoleBrief.class);
	}

	private SiteBrief _toSiteBrief(
		DTOConverterContext dtoConverterContext, Group group, User user) {

		return new SiteBrief() {
			{
				setDescriptiveName(
					() -> group.getDescriptiveName(
						dtoConverterContext.getLocale()));
				setDescriptiveName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						group.getDescriptiveNameMap()));
				setExternalReferenceCode(group::getExternalReferenceCode);
				setId(group::getGroupId);
				setName(() -> group.getName(dtoConverterContext.getLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						group.getNameMap()));
				setRoleBriefs(
					() -> _toRoleBriefs(
						dtoConverterContext,
						_roleLocalService.getUserGroupRoles(
							user.getUserId(), group.getGroupId())));
			}
		};
	}

	private UserGroupBrief _toUserGroupBrief(UserGroup userGroup) {
		return new UserGroupBrief() {
			{
				setDescription(userGroup::getDescription);
				setExternalReferenceCode(userGroup::getExternalReferenceCode);
				setId(userGroup::getGroupId);
				setName(userGroup::getName);
			}
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelService;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private Portal _portal;

	@Reference(target = DTOConverterConstants.POSTAL_ADDRESS_DTO_CONVERTER)
	private DTOConverter<Address, PostalAddress> _postalAddressDTOConverter;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Role)"
	)
	private ModelResourcePermission<Role> _roleModelResourcePermission;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}