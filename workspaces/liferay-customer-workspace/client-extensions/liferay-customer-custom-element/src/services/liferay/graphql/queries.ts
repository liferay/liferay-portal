/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {gql} from '@apollo/client';

export const addAccountFlag = gql`
	mutation addAccountFlag($accountFlag: InputC_AccountFlag!) {
		createAccountFlag(input: $accountFlag)
			@rest(
				method: "POST"
				type: "C_AccountFlag"
				path: "/c/accountflags"
			) {
			accountKey
			name
			finished
		}
	}
`;

export const addAdminDXPCloud = gql`
	mutation addAdminDXPCloud($AdminDXPCloud: InputC_AdminDXPCloud!) {
		createAdminDXPCloud(input: $AdminDXPCloud)
			@rest(
				method: "POST"
				type: "C_AdminDXPCloud"
				path: "/c/admindxpclouds/"
			) {
			emailAddress
			firstName
			githubUsername
			lastName
			dxpCloudEnvironmentId
		}
	}
`;

export const addAnalyticsCloudWorkspace = gql`
	mutation addAnalyticsCloudWorkspace(
		$analyticsCloudWorkspace: InputC_AnalyticsCloudWorkspace!
	) {
		createAnalyticsCloudWorkspace(input: $analyticsCloudWorkspace)
			@rest(
				method: "POST"
				type: "C_AnalyticsCloudWorkspace"
				path: "/c/analyticscloudworkspaces/"
			) {
			accountKey
			allowedEmailDomains
			dataCenterLocation
			id
			ownerEmailAddress
			timeZone
			workspaceFriendlyUrl
			workspaceName
		}
	}
`;

export const addBusinessEvent = gql`
	mutation addBusinessEvent($businessEvent: InputC_BusinessEvent!) {
		createBusinessEvent(input: $businessEvent)
			@rest(
				method: "POST"
				type: "C_BusinessEvent"
				path: "/c/businessevents/"
			) {
			associatedTickets
			currentLiferayVersion
			description
			eventType
			name
			newLiferayVersione
			targetGoLiveDateTime
			timeZone
		}
	}
`;

export const addDXPCloudEnvironment = gql`
	mutation addDXPCloudEnvironment(
		$DXPCloudEnvironment: InputC_DXPCloudEnvironment!
	) {
		createDXPCloudEnvironment(input: $DXPCloudEnvironment)
			@rest(
				method: "POST"
				type: "C_DXPCloudEnvironment"
				path: "/c/dxpcloudenvironments/"
			) {
			accountKey
			dataCenterRegion
			disasterDataCenterRegion
			id
			projectId
		}
	}
`;

export const addHighPriorityContact = gql`
	mutation addHighPriorityContact(
		$HighPriorityContacts: InputC_HighPriorityContacts!
	) {
		createHighPriorityContacts(input: $HighPriorityContacts)
			@rest(
				method: "POST"
				type: "C_HighPriorityContactsPage"
				path: "/c/highprioritycontactses/"
			) {
			contactsCategory
			r_userToHighPriorityContacts_userId
		}
	}
`;

export const addIncidentReportAnalyticsCloud = gql`
	mutation addIncidentReportAnalyticsCloud(
		$IncidentReportContactAnalyticsCloud: InputC_IncidentReportContactAnalyticsCloud!
	) {
		createIncidentReportContactAnalyticsCloud(
			input: $IncidentReportContactAnalyticsCloud
		)
			@rest(
				method: "POST"
				type: "C_IncidentReportContactAnalyticsCloud"
				path: "/c/incidentreportcontactanalyticsclouds/"
			) {
			emailAddress
			analyticsCloudWorkspaceId
		}
	}
`;

export const addTeamMembersInvitation = gql`
	mutation addTeamMembersInvitation(
		$TeamMembersInvitation: [InputC_TeamMembersInvitation]!
	) {
		createTeamMembersInvitation(input: $TeamMembersInvitation)
			@rest(
				method: "POST"
				type: "C_TeamMembersInvitation"
				path: "/c/teammembersinvitations/batch"
			) {
			email
			role
		}
	}
`;

export const assignUserAccountWithAccount = gql`
	mutation assignUserAccountWithAccount(
		$emailAddress: String!
		$accountKey: String!
	) {
		createAccountUserAccountByExternalReferenceCodeByEmailAddress(
			externalReferenceCode: $accountKey
			emailAddress: $emailAddress
		) {
			id
		}
	}
`;

export const assignUserAccountWithAccountAndAccountRole = gql`
	mutation assignUserAccountWithAccountAndAccountRole(
		$emailAddress: String!
		$accountKey: String!
		$accountRoleId: Long!
	) {
		createAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
			accountRoleId: $accountRoleId
			emailAddress: $emailAddress
			externalReferenceCode: $accountKey
		)
	}
`;

export const createAccountUserRoles = gql`
	mutation createAccountUserRoles(
		$accountRoleId: Long!
		$emailAddress: String!
		$externalReferenceCode: String!
	) {
		createAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
			accountRoleId: $accountRoleId
			emailAddress: $emailAddress
			externalReferenceCode: $externalReferenceCode
		)
	}
`;

export const createAndAssignUserAccountWithAccountAndAccountRole = gql`
	mutation createAndAssignUserAccountWithAccountAndAccountRole(
		$emailAddress: String!
		$userAccount: InputUserAccount!
		$accountKey: String!
		$accountRoleId: Long!
	) {
		createAccountUserAccountByExternalReferenceCode(
			userAccount: $userAccount
			externalReferenceCode: $accountKey
		) {
			id
		}
		createAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
			accountRoleId: $accountRoleId
			emailAddress: $emailAddress
			externalReferenceCode: $accountKey
		)
	}
`;

export const deleteAccountUserAccount = gql`
	mutation deleteAccountUserAccountByExternalReferenceCodeByEmailAddress(
		$emailAddress: String!
		$accountKey: String!
	) {
		deleteAccountUserAccountByExternalReferenceCodeByEmailAddress(
			emailAddress: $emailAddress
			externalReferenceCode: $accountKey
		)
	}
`;

export const deleteAccountUserRoles = gql`
	mutation deleteAccountUserRoles(
		$accountRoleId: Long!
		$emailAddress: String!
		$accountKey: String!
	) {
		deleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
			accountRoleId: $accountRoleId
			emailAddress: $emailAddress
			externalReferenceCode: $accountKey
		)
	}
`;

export const deleteHighPriorityContacts = gql`
	mutation deleteHighPriorityContacts($highPriorityContactsId: Long!) {
		deleteHighPriorityContacts(
			highPriorityContactsId: $highPriorityContactsId
		)
			@rest(
				type: "Boolean"
				path: "/c/highprioritycontactses/{args.highPriorityContactsId}"
				method: "DELETE"
			) {
			NoResponse
		}
	}
`;

export const getAccountAccountRolesByExternalReferenceCode = gql`
	query getAccountAccountRolesByExternalReferenceCode(
		$externalReferenceCode: String
	) {
		accountAccountRolesByExternalReferenceCode(
			externalReferenceCode: $externalReferenceCode
		) {
			items {
				id
				displayName
				roleId
			}
		}
	}
`;

export const getAccountByExternalReferenceCode = gql`
	query getAccountByExternalReferenceCode($externalReferenceCode: String!) {
		accountByExternalReferenceCode(
			externalReferenceCode: $externalReferenceCode
		) {
			id
			name
		}
	}
`;

export const getAccountFlags = gql`
	query getAccountFlags($filter: String) {
		c {
			accountFlags(filter: $filter) {
				items {
					accountKey
					name
					finished
				}
			}
		}
	}
`;

export const getAccountRoles = gql`
	query getAccountRoles($accountId: Long!) {
		accountAccountRoles(accountId: $accountId) {
			items {
				id
				name
			}
		}
	}
`;

export const getAccounts = gql`
	query getAccounts($pageSize: Int = 20) {
		accounts(pageSize: $pageSize) {
			items {
				externalReferenceCode
				name
			}
		}
	}
`;

export const getAccountSubscriptionGroups = gql`
	query getAccountSubscriptionGroups(
		$aggregation: [String]
		$filter: String
		$page: Int = 1
		$pageSize: Int = 20
		$search: String
		$sort: String
	) {
		c {
			accountSubscriptionGroups(
				aggregation: $aggregation
				filter: $filter
				page: $page
				pageSize: $pageSize
				search: $search
				sort: $sort
			) {
				items {
					accountSubscriptionGroupId
					accountKey
					activationProductName
					activationStatus
					externalReferenceCode
					hasActivation
					name
					tabOrder
					menuOrder
				}
			}
		}
	}
`;

export const getAccountSubscriptions = gql`
	query getAccountSubscriptions($filter: String) {
		c {
			accountSubscriptions(filter: $filter) {
				items {
					accountKey
					accountSubscriptionGroupERC
					accountSubscriptionId
					c_accountSubscriptionId
					endDate
					externalReferenceCode
					instanceSize
					name
					quantity
					startDate
				}
				totalCount
			}
		}
	}
`;

export const getAccountUserAccountsByExternalReferenceCode = gql`
	query getAccountUserAccountsByExternalReferenceCode(
		$externalReferenceCode: String!
		$pageSize: Int = 20
		$filter: String
	) {
		accountUserAccountsByExternalReferenceCode(
			externalReferenceCode: $externalReferenceCode
			pageSize: $pageSize
			filter: $filter
		) {
			items {
				dateCreated
				id
				emailAddress
				lastLoginDate
				name
				accountBriefs {
					name
					externalReferenceCode
					roleBriefs {
						id
						name
					}
				}
			}
		}
	}
`;

export const getAnalyticsCloudPageInfo = gql`
	query getAnalyticsCloudPageInfo($accountSubscriptionsFilter: String) {
		c {
			accountSubscriptions(filter: $accountSubscriptionsFilter) {
				items {
					accountKey
					externalReferenceCode
					hasDisasterDataCenterRegion
					name
				}
			}
			analyticsCloudDataCenterLocations {
				items {
					analyticsCloudDataCenterLocationId
					name
					value
				}
			}
		}
	}
`;

export const getAnalyticsCloudWorkspace = gql`
	query getAnalyticsCloudWorkspace($filter: String) {
		c {
			analyticsCloudWorkspaces(filter: $filter) {
				items {
					analyticsCloudWorkspaceId
					workspaceGroupId
				}
			}
		}
	}
`;

export const getBannedEmailDomains = gql`
	query getBannedEmailDomains(
		$aggregation: [String]
		$filter: String
		$page: Int = 1
		$pageSize: Int = 20
		$search: String
		$sort: String
	) {
		c {
			bannedEmailDomains(
				aggregation: $aggregation
				filter: $filter
				page: $page
				pageSize: $pageSize
				search: $search
				sort: $sort
			) {
				items {
					bannedEmailDomainId
					domain
				}
			}
		}
	}
`;

export const getBusinessEvent = gql`
	query getBusinessEvent($businessEventId: Long!) {
		businessEvent(businessEventId: $businessEventId)
			@rest(
				method: "GET"
				type: "C_BusinessEvent"
				path: "/c/businessevents/{args.businessEventId}"
			) {
			id
			r_accountEntryToBusinessEvents_accountEntryId
		}
	}
`;

export const getCommerceOrderItems = gql`
	query getCommerceOrderItems(
		$filter: String
		$page: Int = 1
		$pageSize: Int = 20
	) {
		orderItems(filter: $filter, page: $page, pageSize: $pageSize) {
			items {
				externalReferenceCode
				quantity
				customFields {
					name
					customValue {
						data
					}
				}
				options
			}
			totalCount
		}
	}
`;

export const getDXPCloudEnvironment = gql`
	query getDXPCloudEnvironment($filter: String) {
		c {
			dXPCloudEnvironments(filter: $filter) {
				items {
					dxpCloudEnvironmentId
					projectId
				}
			}
		}
	}
`;

export const getDXPCloudPageInfo = gql`
	query getDXPCloudPageInfo($accountSubscriptionsFilter: String) {
		c {
			accountSubscriptions(filter: $accountSubscriptionsFilter) {
				items {
					accountKey
					externalReferenceCode
					hasDisasterDataCenterRegion
					name
				}
				totalCount
			}

			dXPCDataCenterRegions(sort: "name:asc") {
				items {
					dxpcDataCenterRegionId
					name
					value
				}
				totalCount
			}
		}
	}
`;

export const getKoroneikiAccounts = gql`
	query getKoroneikiAccounts(
		$filter: String
		$pageSize: Int = 20
		$page: Int = 1
	) {
		c {
			koroneikiAccounts(
				filter: $filter
				pageSize: $pageSize
				page: $page
			) {
				items {
					accountKey
					acWorkspaceGroupId
					allowSelfProvisioning
					code
					dxpVersion
					externalReferenceCode
					liferayContactEmailAddress
					liferayContactName
					liferayContactRole
					maxRequestors
					partner
					region
					name
					salesforceAccountKey
					salesforceProjectKey
					slaCurrent
					slaCurrentEndDate
					slaCurrentStartDate
					slaExpired
					slaExpiredEndDate
					slaExpiredStartDate
					slaFuture
					slaFutureEndDate
					slaFutureStartDate
				}
				totalCount
			}
		}
	}
`;

export const getLiferayExperienceCloudEnvironments = gql`
	query getLiferayExperienceCloudEnvironments($filter: String) {
		c {
			liferayExperienceCloudEnvironments(filter: $filter) {
				items {
					liferayExperienceCloudEnvironmentId
					projectId
				}
			}
		}
	}
`;

export const getListTypeDefinitions = gql`
	query getListTypeDefinitions($filter: String) {
		listTypeDefinitions(filter: $filter) {
			items {
				listTypeEntries {
					key
					name
				}
			}
		}
	}
`;

export const getMyUserAccount = gql`
	query getMyUserAccount {
		myUserAccount {
			accountBriefs {
				externalReferenceCode
				id
				name
				roleBriefs {
					id
					name
				}
			}
			externalReferenceCode
			id
			image
			name
			roleBriefs {
				id
				name
			}
			organizationBriefs {
				id
				name
			}
		}
	}
`;

export const getNotificationTemplateByExternalRefenceCode = gql`
	query getNotificationTemplateByExternalRefenceCode(
		$externalReferenceCode: String!
	) {
		notificationTemplateByExternalReferenceCode(
			externalReferenceCode: $externalReferenceCode
		) {
			body
			dateCreated
			id
			name
			recipients
			subject
			type
		}
	}
`;

export const getOrganizations = gql`
	query getOrganizations($filter: String) {
		organizations(filter: $filter) {
			items {
				name
				id
				accounts {
					totalCount
					items {
						id
						name
						externalReferenceCode
					}
				}
			}
		}
	}
`;

export const getStructuredContentFolders = gql`
	query getStructuredContentFolders($siteKey: String!, $filter: String) {
		structuredContentFolders(siteKey: $siteKey, filter: $filter) {
			items {
				id
				name
				structuredContents {
					items {
						friendlyUrlPath
						id
						key
					}
				}
			}
		}
	}
`;

export const getUserAccount = gql`
	query getUserAccount($id: Long!) {
		userAccount(userAccountId: $id) {
			accountBriefs {
				externalReferenceCode
				id
				name
				roleBriefs {
					id
					name
				}
			}
			emailAddress
			externalReferenceCode
			id
			image
			name
			roleBriefs {
				id
				name
			}
			organizationBriefs {
				id
				name
			}
		}
	}
`;

export const getUserAccountByEmail = gql`
	query GetUserAccounts($filter: String) {
		userAccounts(filter: $filter) {
			items {
				alternateName
				emailAddress
				familyName
				givenName
				id
				name
			}
		}
	}
`;

export const notificationQueueEntry = gql`
	mutation createNotificationQueueEntry(
		$notificationQueueEntry: InputNotificationQueueEntry!
	) {
		createNotificationQueueEntry(
			notificationQueueEntry: $notificationQueueEntry
		) {
			body
			id
			recipients
			subject
		}
	}
`;

export const patchOrderItemByExternalReferenceCode = gql`
	mutation patchOrderItemByExternalReferenceCode(
		$externalReferenceCode: String
		$orderItem: InputOrderItem
	) {
		patchOrderItemByExternalReferenceCode(
			externalReferenceCode: $externalReferenceCode
			orderItem: $orderItem
		)
	}
`;

export const patchUserAccount = gql`
	mutation patchUserAccount(
		$userAccountId: Long!
		$userAccount: InputUserAccount!
	) {
		patchUserAccount(
			userAccountId: $userAccountId
			userAccount: $userAccount
		) {
			alternateName
			familyName
			givenName
			id
		}
	}
`;

export const updateAccountSubscriptionGroups = gql`
	mutation putAccountSubscriptionGroups(
		$id: Long!
		$accountSubscriptionGroup: InputC_AccountSubscriptionGroup!
	) {
		updateAccountSubscriptionGroup(
			accountSubscriptionGroupId: $id
			input: $accountSubscriptionGroup
		)
			@rest(
				method: "PUT"
				type: "C_AccountSubscriptionGroup"
				path: "/c/accountsubscriptiongroups/{args.accountSubscriptionGroupId}"
			) {
			accountSubscriptionGroupId
			accountKey
			activationStatus
			externalReferenceCode
			name
		}
	}
`;

export const updateBusinessEvent = gql`
	mutation updateBusinessEvent(
		$businessEvent: InputC_BusinessEvent!
		$businessEventId: Long!
	) {
		updateBusinessEvent(
			businessEventId: $businessEventId
			input: $businessEvent
		)
			@rest(
				method: "PUT"
				type: "C_BusinessEvent"
				path: "/c/businessevents/{args.businessEventId}"
			) {
			actualGoLiveDateTime
			associatedTickets
			currentLiferayVersion
			description
			eventType
			lastComment
			name
			newLiferayVersion
			targetGoLiveDateTime
			timeZone
		}
	}
`;

export const updateDXPCloudEnvironment = gql`
	mutation updateDXPCloudProjectId(
		$dxpCloudEnvironmentId: Long!
		$DXPCloudEnvironment: InputC_DXPCloudEnvironment!
	) {
		updateDXPCloudEnvironment(
			dxpCloudEnvironmentId: $dxpCloudEnvironmentId
			input: $DXPCloudEnvironment
		)
			@rest(
				method: "PUT"
				type: "C_DXPCloudEnvironment"
				path: "/c/dxpcloudenvironments/{args.dxpCloudEnvironmentId}"
			) {
			dxpCloudEnvironmentId
		}
	}
`;
